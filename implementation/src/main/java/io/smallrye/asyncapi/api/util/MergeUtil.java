/**
 * Copyright 2019 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smallrye.asyncapi.api.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger;

import io.apicurio.datamodels.core.models.Document;
import io.apicurio.datamodels.core.models.Node;
import io.apicurio.datamodels.core.models.common.Parameter;
import io.apicurio.datamodels.core.models.common.SecurityRequirement;
import io.apicurio.datamodels.core.models.common.Server;
import io.apicurio.datamodels.core.models.common.Tag;

/**
 * Used to merge two AsyncAPI data models into a single one. The MP+AsyncAPI 1.0 spec
 * requires that any or all of the various mechanisms for producing an AsyncAPI document
 * can be used. When more than one mechanism is used, each mechanism produces an
 * AsyncAPI document. These multiple documents must then be sensibly merged into
 * a final result.
 *
 * @author eric.wittmann@gmail.com
 */
public class MergeUtil {
    private static final Logger LOG = Logger.getLogger(MergeUtil.class);

    private static final Set<String> EXCLUDED_FIELDS = new HashSet<>();
    static {
        EXCLUDED_FIELDS.add("_parent");
        EXCLUDED_FIELDS.add("_ownerDocument");
    }

    /**
     * Constructor.
     */
    private MergeUtil() {
    }

    /**
     * Merges two documents and returns the result.
     *
     * @param document1 Document instance
     * @param document2 Document instance
     * @return Merged Document instance
     */
    public static final Document merge(Document document1, Document document2) {
        return mergeObjects(document1, document2);
    }

    /**
     * Generic merge of two objects of the same type.
     *
     * @param object1 First object
     * @param object2 Second object
     * @param <T> Type parameter
     * @return Merged object
     */
    @SuppressWarnings({ "rawtypes" })
    public static <T extends Node> T mergeObjects(T object1, T object2) {
        if (object1 == null && object2 != null) {
            return object2;
        }
        if (object1 != null && object2 == null) {
            return object1;
        }
        if (object1 == null && object2 == null) {
            return null;
        }

        // It's uncommon, but in some cases the values could be different types.  In this case, just take the
        // 2nd one (the override).
        if (!object1.getClass().equals(object2.getClass())) {
            return object2;
        }

        // Copy all public fields (may not be bean properties)
        try {
            Field[] fields = object1.getClass().getFields();
            for (Field field : fields) {
                if (Modifier.isPublic(field.getModifiers()) && !EXCLUDED_FIELDS.contains(field.getName())) {
                    Object fieldVal1 = field.get(object1);
                    Object fieldVal2 = field.get(object2);
                    if (fieldVal2 == null) {
                        continue;
                    }
                    if (fieldVal2 instanceof Node) {
                        Node node2 = (Node) fieldVal2;
                        node2._ownerDocument = object1.ownerDocument();
                        node2._parent = object1;
                        Node newValue = mergeObjects((Node) fieldVal1, (Node) fieldVal2);
                        field.set(object1, newValue);
                    } else if (fieldVal2 instanceof Map) {
                        Map values1 = (Map) fieldVal1;
                        Map values2 = (Map) fieldVal2;
                        Map newValues = mergeMaps(values1, values2, object1);
                        field.set(object1, newValues);
                    } else if (fieldVal2 instanceof List) {
                        List values1 = (List) fieldVal1;
                        List values2 = (List) fieldVal2;
                        List newValues = mergeLists(values1, values2, object1);
                        field.set(object1, newValues);
                    } else {
                        field.set(object1, fieldVal2);
                    }
                }
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOG.error("Failed to merge two data model nodes.", e);
            throw new RuntimeException(e);
        }
        return object1;
    }

    /**
     * Merges two Maps. Any values missing from Map1 but present in Map2 will be added. If a value
     * is present in both maps, it will be overridden or merged.
     *
     * @param values1
     * @param values2
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Map mergeMaps(Map values1, Map values2, Node parent) {
        if (values1 == null && values2 == null) {
            return null;
        }
        if (values1 != null && values2 == null) {
            return values1;
        }
        if (values1 == null && values2 != null) {
            return values2;
        }

        for (Object key : values2.keySet()) {
            if (values1.containsKey(key)) {
                Object pval1 = values1.get(key);
                Object pval2 = values2.get(key);
                if (pval1 instanceof Map) {
                    // Do not support maps of maps - not found in the data model
                } else if (pval1 instanceof List) {
                    // Do not support maps of lists - not found in the data model
                } else if (pval1 instanceof Node) {
                    ((Node) pval2)._ownerDocument = parent.ownerDocument();
                    ((Node) pval2)._parent = parent;
                    values1.put(key, mergeObjects((Node) pval1, (Node) pval2));
                } else {
                    values1.put(key, pval2);
                }
            } else {
                Object pval2 = values2.get(key);
                if (pval2 instanceof Node) {
                    ((Node) pval2)._ownerDocument = parent.ownerDocument();
                    ((Node) pval2)._parent = parent;
                }
                values1.put(key, pval2);
            }
        }

        return values1;
    }

    /**
     * Merges two Lists. Any values missing from List1 but present in List2 will be added. Depending on
     * the type of list, further processing and de-duping may be required.
     *
     * @param values1
     * @param values2
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List mergeLists(List values1, List values2, Node parent) {
        if (values1 == null && values2 == null) {
            return null;
        }
        if (values1 != null && values2 == null) {
            return values1;
        }
        if (values1 == null && values2 != null) {
            return values2;
        }

        values2.forEach(value -> {
            if (value instanceof Node) {
                ((Node) value)._ownerDocument = parent.ownerDocument();
                ((Node) value)._parent = parent;
            }
        });

        if (values1.get(0) instanceof String) {
            return mergeStringLists(values1, values2);
        }

        if (values1.get(0) instanceof Tag) {
            return mergeTagLists(values1, values2);
        }

        if (values1.get(0) instanceof Server) {
            return mergeServerLists(values1, values2);
        }

        if (values1.get(0) instanceof SecurityRequirement) {
            return mergeSecurityRequirementLists(values1, values2);
        }

        if (values1.get(0) instanceof Parameter) {
            return mergeParameterLists(values1, values2);
        }

        values1.addAll(values2);
        return values1;
    }

    /**
     * Merge a list of strings. In all cases, string lists are really sets. So this is just
     * combining the two lists and then culling duplicates.
     *
     * @param values1
     * @param values2
     */
    private static List<String> mergeStringLists(List<String> values1, List<String> values2) {
        Set<String> set = new LinkedHashSet<String>();
        set.addAll(values1);
        set.addAll(values2);
        return new ArrayList<String>(set);
    }

    /**
     * Merge two lists of Tags. Tags are a special case because they are named and you cannot
     * have two Tags with the same name. This will append any tags from values2 that don't
     * exist in values1. It will *merge* any tags found in values2 that already exist in
     * values1.
     *
     * @param values1
     * @param values2
     */
    private static List<Tag> mergeTagLists(List<Tag> values1, List<Tag> values2) {
        for (Tag value2 : values2) {
            Tag match = null;
            for (Tag value1 : values1) {
                if (value1.name != null && value1.name.equals(value2.name)) {
                    match = value1;
                    break;
                }
            }
            if (match == null) {
                values1.add(value2);
            } else {
                mergeObjects(match, value2);
            }
        }
        return values1;
    }

    /**
     * Merge two lists of Servers. Servers are a special case because they must be unique
     * by the 'url' property each must have.
     *
     * @param values1
     * @param values2
     */
    private static List<Server> mergeServerLists(List<Server> values1, List<Server> values2) {
        for (Server value2 : values2) {
            Server match = null;
            for (Server value1 : values1) {
                if (value1.url != null && value1.url.equals(value2.url)) {
                    match = value1;
                    break;
                }
            }
            if (match == null) {
                values1.add(value2);
            } else {
                mergeObjects(match, value2);
            }
        }
        return values1;
    }

    /**
     * Merge two lists of Security Requirements. Security Requirement lists are are a
     * special case because
     * values1.
     *
     * @param values1
     * @param values2
     */
    private static List<SecurityRequirement> mergeSecurityRequirementLists(List<SecurityRequirement> values1,
            List<SecurityRequirement> values2) {
        for (SecurityRequirement value2 : values2) {
            if (values1.contains(value2)) {
                continue;
            }
            values1.add(value2);
        }
        return values1;
    }

    /**
     * Merge two lists of Parameters. Parameters are a special case because they must be unique
     * by the name in 'in' each have
     *
     * @param values1
     * @param values2
     */
    private static List<Parameter> mergeParameterLists(List<Parameter> values1, List<Parameter> values2) {
        for (Parameter value2 : values2) {
            Parameter match = null;
            for (Parameter value1 : values1) {
                if (value1.name == null || !value1.name.equals(value2.name)) {
                    continue;
                }

                match = value1;
                break;
            }
            if (match == null) {
                values1.add(value2);
            } else {
                mergeObjects(match, value2);
            }
        }
        return values1;
    }
}
