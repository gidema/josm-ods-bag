package org.openstreetmap.josm.plugins.ods.bag;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ServiceConfigurationTest {
    @Test
    void testConfiguration() {
        ServiceCollector collector = new ServiceCollector();
        collector.run();
        assertTrue(collector.issues.isEmpty(), collector.issues.toString());
    }

    private class ServiceCollector {
        Set<String> issues = new HashSet<>();

        void run() {
            try {
                Enumeration<URL> folders = getClass().getClassLoader()
                        .getResources("META-INF/services");
                while (folders.hasMoreElements()) {
                    URL url = folders.nextElement();
                    if (!url.getProtocol().equals("jar"))
                        getServices(url);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void getServices(URL url) {
            try {
                Path path = Paths.get(url.toURI());
                Files.list(path).forEach(file -> getServices(file));
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void getServices(Path file) {
            Class<?> serviceClass;
            String name = file.getFileName().toString();
            try {
                serviceClass = Class.forName(name);
                try {
                    Files.lines(file).forEach(
                            line -> getImplementations(serviceClass, line));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                issues.add(e.toString());
            }
        }

        private void getImplementations(Class<?> serviceClass, String line) {
            String className = line.trim();
            if (className.isEmpty())
                return;
            try {
                Class<?> implementationClass = Class.forName(className);
                if (!serviceClass.isAssignableFrom(implementationClass)) {
                    issues.add(implementationClass.getName()
                            + " doesn't implement " + serviceClass.getName());
                }
            } catch (ClassNotFoundException e) {
                issues.add(e.toString());
            }
        }
    }
}
