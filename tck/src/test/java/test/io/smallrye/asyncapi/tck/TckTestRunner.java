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

package test.io.smallrye.asyncapi.tck;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.jandex.IndexView;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import io.apicurio.datamodels.Library;
import io.apicurio.datamodels.core.models.Document;
import io.smallrye.asyncapi.api.AsyncApiConfig;
import io.smallrye.asyncapi.api.AsyncApiDocument;
import io.smallrye.asyncapi.api.util.ArchiveUtil;
import io.smallrye.asyncapi.runtime.AsyncApiProcessor;
import io.smallrye.asyncapi.runtime.AsyncApiStaticFile;

/**
 * A Junit 4 test runner used to quickly run the AsyncAPI tck tests directly against the
 * {@link AsyncApiDocument} without spinning up an MP compliant server. This is not
 * a replacement for running the full AsyncAPI TCK using Arquillian. However, it runs
 * much faster and does *most* of what we need for coverage.
 *
 * @author eric.wittmann@gmail.com
 */
@SuppressWarnings("rawtypes")
public class TckTestRunner extends ParentRunner<ProxiedTckTest> {

    private Class<?> testClass;
    private Class<? extends Arquillian> tckTestClass;

    public static Map<Class, Document> ASYNC_API_DOCS = new HashMap<>();

    /**
     * Constructor.
     *
     * @param testClass
     * @throws InitializationError
     */
    public TckTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
        this.tckTestClass = determineTckTestClass(testClass);

        // The Archive (shrinkwrap deployment)
        Archive archive = archive();
        // MPConfig
        AsyncApiConfig config = ArchiveUtil.archiveToConfig(archive);

        try {
            IndexView index = ArchiveUtil.archiveToIndex(config, archive);
            AsyncApiStaticFile staticFile = ArchiveUtil.archiveToStaticFile(archive);

            // Reset and then initialize the AsyncApiDocument for this test.
            AsyncApiDocument.INSTANCE.reset();
            AsyncApiDocument.INSTANCE.config(config);
            AsyncApiDocument.INSTANCE.modelFromStaticFile(AsyncApiProcessor.modelFromStaticFile(staticFile));
            AsyncApiDocument.INSTANCE.modelFromAnnotations(AsyncApiProcessor.modelFromAnnotations(config, index));
            AsyncApiDocument.INSTANCE.modelFromReader(AsyncApiProcessor.modelFromReader(config, getContextClassLoader()));
            AsyncApiDocument.INSTANCE.filter(AsyncApiProcessor.getFilter(config, getContextClassLoader()));
            AsyncApiDocument.INSTANCE.initialize();

            Assert.assertNotNull("Generated OAI document must not be null.", AsyncApiDocument.INSTANCE.get());

            ASYNC_API_DOCS.put(testClass, AsyncApiDocument.INSTANCE.get());

            // Output the /asyncapi content to a file for debugging purposes
            File parent = new File("target", "TckTestRunner");
            if (!parent.exists()) {
                parent.mkdir();
            }
            File file = new File(parent, testClass.getName() + ".json");
            String content = Library.writeDocumentToJSONString(AsyncApiDocument.INSTANCE.get());
            try (FileWriter writer = new FileWriter(file)) {
                IOUtils.write(content, writer);
            }
        } catch (Exception e) {
            throw new InitializationError(e);
        }
    }

    /**
     * Creates and returns the shrinkwrap archive for this test.
     */
    private Archive archive() throws InitializationError {
        try {
            Method[] methods = tckTestClass.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Deployment.class)) {
                    Archive archive = (Archive) method.invoke(null);
                    return archive;
                }
            }
            throw new Exception("No @Deployment archive found for test.");
        } catch (Exception e) {
            throw new InitializationError(e);
        }
    }

    /**
     * Figures out what TCK test is being run.
     *
     * @throws InitializationError
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Arquillian> determineTckTestClass(Class<?> testClass) throws InitializationError {
        ParameterizedType ptype = (ParameterizedType) testClass.getGenericSuperclass();
        Class cc = (Class) ptype.getActualTypeArguments()[0];
        return cc;
    }

    /**
     * @see org.junit.runners.ParentRunner#getChildren()
     */
    @Override
    protected List<ProxiedTckTest> getChildren() {
        List<ProxiedTckTest> children = new ArrayList<>();
        Method[] methods = tckTestClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(org.testng.annotations.Test.class)) {
                try {
                    ProxiedTckTest test = new ProxiedTckTest();
                    Object theTestObj = this.testClass.newInstance();
                    test.setTest(theTestObj);
                    test.setTestMethod(method);
                    test.setDelegate(createDelegate(theTestObj));
                    children.add(test);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        children.sort(new Comparator<ProxiedTckTest>() {
            @Override
            public int compare(ProxiedTckTest o1, ProxiedTckTest o2) {
                return o1.getTestMethod().getName().compareTo(o2.getTestMethod().getName());
            }
        });
        return children;
    }

    /**
     * Creates the delegate test instance. This is done by instantiating the test itself
     * and calling its "getDelegate()" method. If no such method exists then an error
     * is thrown.
     */
    private Arquillian createDelegate(Object testObj) throws Exception {
        Object delegate = testObj.getClass().getMethod("getDelegate").invoke(testObj);
        return (Arquillian) delegate;
    }

    /**
     * @see org.junit.runners.ParentRunner#describeChild(java.lang.Object)
     */
    @Override
    protected Description describeChild(ProxiedTckTest child) {
        return Description.createTestDescription(tckTestClass, child.getTestMethod().getName());
    }

    /**
     * @see org.junit.runners.ParentRunner#runChild(java.lang.Object, org.junit.runner.notification.RunNotifier)
     */
    @Override
    protected void runChild(final ProxiedTckTest child, final RunNotifier notifier) {
        AsyncApiDocument.INSTANCE.set(TckTestRunner.ASYNC_API_DOCS.get(child.getTest().getClass()));

        Description description = describeChild(child);
        if (isIgnored(child)) {
            notifier.fireTestIgnored(description);
        } else {
            Statement statement = new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        Object[] args = (Object[]) child.getTest().getClass().getMethod("getTestArguments")
                                .invoke(child.getTest());
                        child.getTestMethod().invoke(child.getDelegate(), args);
                    } catch (InvocationTargetException e) {
                        Throwable cause = e.getCause();
                        org.testng.annotations.Test testAnno = child.getTestMethod()
                                .getAnnotation(org.testng.annotations.Test.class);
                        Class[] expectedExceptions = testAnno.expectedExceptions();
                        if (expectedExceptions != null && expectedExceptions.length > 0) {
                            Class expectedException = expectedExceptions[0];
                            Assert.assertEquals(expectedException, cause.getClass());
                        } else {
                            throw cause;
                        }
                    }
                }
            };
            runLeaf(statement, description, notifier);
        }

    }

    /**
     * @see org.junit.runners.ParentRunner#isIgnored(java.lang.Object)
     */
    @Override
    protected boolean isIgnored(ProxiedTckTest child) {
        return child.getTestMethod().isAnnotationPresent(Ignore.class);
    }

    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return AccessController
                .doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
    }

}
