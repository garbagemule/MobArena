package com.garbagemule.MobArena.util.classparsing;

import java.util.*;

public class ClassParser
{
    private ClassIterationStrategy strategy;
    
    public ClassParser(ClassIterationStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Using an iteration strategy provided in the constructor, this method tries
     * to iterate through all classes within a given "package".
     * @param packagePath a package path, e.g. com.garbagemule.MobArena.commands.user
     * @return a list of Class objects
     */
    public Collection<Class<?>> getClasses(String packagePath) {
        Collection<Class<?>> result = new LinkedList<Class<?>>();
        
        for (String classname : strategy.getClassesFromPackage(packagePath)) {
            Class<?> c = makeClass(classname);
            
            if (c != null) {
                result.add(c);
            }
        }
        
        return result;
    }

    /**
     * Get a class using its qualified (full package) name. 
     * @param qualifiedName qualified name of a class
     * @return a Class object, or null if the class could not be made.
     */
    private static Class<?> makeClass(String qualifiedName) {
        try {
            return Class.forName(qualifiedName);
        }
        catch (Exception e) {
            return null;
        }
    }
}
