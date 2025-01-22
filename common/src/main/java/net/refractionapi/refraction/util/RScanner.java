package net.refractionapi.refraction.util;

import net.refractionapi.refraction.Refraction;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scans all files given in a package <br>
 * Implementation of <a href="https://web.archive.org/web/20090227113602/http://snippets.dzone.com:80/posts/show/4831">Ref</a> <br>
 * --Zeus
 */
public class RScanner {
    private final String packageName;
    private Class<?> withType;

    public RScanner(String packageName) {
        this.packageName = packageName;
    }

    public RScanner() {
        this("net.refractionapi.refraction");
    }

    public RScanner withType(Class<? extends Annotation> type) {
        this.withType = type;
        return this;
    }

    public Set<Class<?>> scan() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = this.packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            Set<File> directories = new HashSet<>();
            Set<Class<?>> classes = new HashSet<>();
            while (resources.hasMoreElements()) {
                directories.add(new File(resources.nextElement().getFile().replaceAll("%[^a-zA-Z]+[/]", ""))); // invalid characters regex, don't know why they appear... --Zeus
            }
            for (File directory : directories) {
                classes.addAll(getClasses(directory, this.packageName));
            }
            return classes.stream() // TODO add more options for scanning
                    .filter((clazz) -> {
                       return this.withType == null || clazz.isAnnotationPresent((Class<? extends Annotation>) this.withType);
                    }).collect(Collectors.toSet());
        } catch (Exception e) {
            Refraction.LOGGER.error("Failed to scan package {}", this.packageName, e);
        }
        return null;
    }

    private static HashSet<Class<?>> getClasses(File directory, String packageName) {
        HashSet<Class<?>> classes = new HashSet<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        assert files != null;
        classes.addAll(Arrays.stream(files)
                .flatMap(file -> {
                    try {
                        if (file.isDirectory() && !file.getPath().contains("mixin")) { // loading mixin classes causes a crash --Zeus
                            assert !file.getName().contains(".");
                            return getClasses(file, "%s.%s".formatted(packageName, file.getName())).stream();
                        } else if (file.getName().endsWith(".class")) {
                            return Stream.of(Class.forName("%s.%s".formatted(packageName, file.getName().substring(0, file.getName().length() - 6))));
                        }
                    } catch (Exception e) {
                        Refraction.LOGGER.error("Failed to load class {}", file.getName(), e);
                    }
                    return Stream.empty();
                }).collect(Collectors.toSet()));
        return classes;
    }
}
