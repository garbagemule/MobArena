package com.garbagemule.MobArena.util.classparsing;

import java.util.Collection;

public interface ClassIterationStrategy
{
    /**
     * Given a package, e.g. com.garbagemule.MobArena.commands.user, the
     * concrete strategy will return a collection of qualified class names
     * either from a directory or a jar file.
     * @param packagePath path from which to extract classes
     * @return a list of qualified class names or null
     */
    public Collection<String> getClassesFromPackage(String packagePath);
}
