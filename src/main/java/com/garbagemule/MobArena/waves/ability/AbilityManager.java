package com.garbagemule.MobArena.waves.ability;

import com.garbagemule.MobArena.waves.ability.core.ChainLightning;
import com.garbagemule.MobArena.waves.ability.core.DisorientDistant;
import com.garbagemule.MobArena.waves.ability.core.DisorientNearby;
import com.garbagemule.MobArena.waves.ability.core.DisorientTarget;
import com.garbagemule.MobArena.waves.ability.core.FetchDistant;
import com.garbagemule.MobArena.waves.ability.core.FetchNearby;
import com.garbagemule.MobArena.waves.ability.core.FetchTarget;
import com.garbagemule.MobArena.waves.ability.core.FireAura;
import com.garbagemule.MobArena.waves.ability.core.Flood;
import com.garbagemule.MobArena.waves.ability.core.LightningAura;
import com.garbagemule.MobArena.waves.ability.core.LivingBomb;
import com.garbagemule.MobArena.waves.ability.core.ObsidianBomb;
import com.garbagemule.MobArena.waves.ability.core.PullDistant;
import com.garbagemule.MobArena.waves.ability.core.PullNearby;
import com.garbagemule.MobArena.waves.ability.core.PullTarget;
import com.garbagemule.MobArena.waves.ability.core.RootTarget;
import com.garbagemule.MobArena.waves.ability.core.ShootArrow;
import com.garbagemule.MobArena.waves.ability.core.ShootFireball;
import com.garbagemule.MobArena.waves.ability.core.ShufflePositions;
import com.garbagemule.MobArena.waves.ability.core.ThrowDistant;
import com.garbagemule.MobArena.waves.ability.core.ThrowNearby;
import com.garbagemule.MobArena.waves.ability.core.ThrowTarget;
import com.garbagemule.MobArena.waves.ability.core.WarpToPlayer;
import org.bukkit.Bukkit;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityManager
{
    private static final String ma = "plugins" + File.separator + "MobArena.jar";
    private static final String cb = System.getProperty("java.class.path");
    private static final String classpath = ma + System.getProperty("path.separator") + cb;

    private static Map<String,Class<? extends Ability>> abilities;

    /**
     * Get an instance of an ability by alias
     * @param alias the alias of an ability
     * @return a new Ability object, or null
     */
    public static Ability getAbility(String alias) {
        try {
            Class<? extends Ability> cls = abilities.get(alias.toLowerCase().replaceAll("[-_.]", ""));
            return cls.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Load all the core abilities included in MobArena
     */
    public static void loadCoreAbilities() {
        if (abilities == null) abilities = new HashMap<>();

        register(ChainLightning.class);
        register(DisorientDistant.class);
        register(DisorientNearby.class);
        register(DisorientTarget.class);
        register(FetchDistant.class);
        register(FetchNearby.class);
        register(FetchTarget.class);
        register(FireAura.class);
        register(Flood.class);
        register(LightningAura.class);
        register(LivingBomb.class);
        register(ObsidianBomb.class);
        register(PullDistant.class);
        register(PullNearby.class);
        register(PullTarget.class);
        register(RootTarget.class);
        register(ShootArrow.class);
        register(ShootFireball.class);
        register(ShufflePositions.class);
        register(ThrowDistant.class);
        register(ThrowNearby.class);
        register(ThrowTarget.class);
        register(WarpToPlayer.class);
    }

    /**
     * Load the custom abilities from the specified directory.
     * @param dataDir main plugin data folder
     */
    public static void loadCustomAbilities(File dataDir) {
        if (abilities == null) abilities = new HashMap<>();

        File classDir = new File(dataDir, "abilities");
        if (!classDir.exists()) {
            return;
        }

        // Grab the source directory.
        File javaDir = new File(classDir, "src");

        /* If the source directory exists, we need to verify that the system
         * has a java compiler before attempting anything. If not, we need to
         * skip the compiling step and just go straight to loading in the
         * existing class files. */
        if (javaDir.exists()) {
            if (ToolProvider.getSystemJavaCompiler() != null) {
                compileAbilities(javaDir, classDir);
            } else {
                Bukkit.getLogger().warning("[MobArena] Found plugins/MobArena/abilities/src/ folder, but no Java compiler. The source files will not be compiled!");
            }
        }

        // Load all the custom abilities.
        loadClasses(classDir);
    }

    private static void register(Class<? extends Ability> cls) {
        register(cls, false);
    }

    /**
     * Register an ability by its class object
     * @param cls the ability class
     */
    private static void register(Class<? extends Ability> cls, boolean announce) {
        AbilityInfo info = cls.getAnnotation(AbilityInfo.class);
        if (info == null) return;

        // Map all the aliases
        for (String alias : info.aliases()) {
            abilities.put(alias, cls);
        }

        // Announce custom abilities
        if (announce) Bukkit.getLogger().info("[MobArena] Loaded custom ability '" + info.name() + "'");
    }

    private static void compileAbilities(File javaDir, File classDir) {
        if (!javaDir.exists()) return;

        // Make ready a new list of files to compile.
        List<File> toCompile = getSourceFilesToCompile(javaDir, classDir);

        // No files to compile?
        if (toCompile.isEmpty()) {
            return;
        }

        // Notify the console.
        Bukkit.getLogger().info("[MobArena] Compiling abilities: " + fileListToString(toCompile));

        // Get the compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        // Generate some JavaFileObjects
        try {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(toCompile);

            // Include the MobArena.jar on the classpath, and set the destination folder.
            List<String> options = Arrays.asList("-classpath", classpath, "-d", classDir.getPath());

            // Set up the compilation task.
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);

            // Call the task.
            task.call();

            // And close the file manager.
            fileManager.close();
        }
        catch (Exception e) {
            Bukkit.getLogger().severe("[MobArena] Compilation step failed...");
            e.printStackTrace();
        }
    }

    private static List<File> getSourceFilesToCompile(File javaDir, File classDir) {
        List<File> result = new ArrayList<>();

        if (javaDir == null || !javaDir.exists()) {
            return result;
        }

        // Grab the array of compiled files.
        File[] classFiles = classDir.listFiles();

        // Go through each source file.
        for (File javaFile : javaDir.listFiles()) {
            // Skip if it's not a .java file.
            if (!javaFile.getName().endsWith(".java")) {
                Bukkit.getLogger().info("[MobArena] Found invalid ability file: " + javaFile.getName());
                continue;
            }

            // Find the associated .class file.
            File classFile = findClassFile(javaFile, classFiles);

            // If the .class file is newer, we don't need to compile.
            if (isClassFileNewer(javaFile, classFile)) {
                continue;
            }
            result.add(javaFile);
        }

        return result;
    }

    private static File findClassFile(File javaFile, File[] classFiles) {
        String javaFileName = javaFile.getName();
        String classFileName = javaFileName.substring(0, javaFileName.lastIndexOf(".")) + ".class";

        for (File classFile : classFiles) {
            if (classFile.getName().equals(classFileName)) {
                return classFile;
            }
        }
        return null;
    }

    private static boolean isClassFileNewer(File javaFile, File classFile) {
        if (classFile == null) return false;

        return (classFile.lastModified() > javaFile.lastModified());
    }

    /**
     * (Compiles and) loads all custom abilities in the given directory.
     * @param classDir a directory
     */
    private static void loadClasses(File classDir) {
        // Grab the class loader
        ClassLoader loader = getLoader(classDir);
        if (loader == null) return;

        for (File file : classDir.listFiles()) {
            String filename = file.getName();

            // Only load .class files.
            int dot = filename.lastIndexOf(".class");
            if (dot < 0) continue;

            // Trim off the .class extension
            String name = filename.substring(0, file.getName().lastIndexOf("."));

            try {
                // Load the class
                Class<?> cls = loader.loadClass(name);

                // Verify that it's an Ability, then register it
                if (Ability.class.isAssignableFrom(cls)) {
                    register(cls.asSubclass(Ability.class), true);
                }
            } catch (Exception e) {}
        }
    }

    /**
     * Get a ClassLoader for the given directory.
     * @param dir a directory
     * @return a ClassLoader, or null
     */
    private static ClassLoader getLoader(File dir) {
        try {
            ClassLoader loader = new URLClassLoader(new URL[] { dir.toURI().toURL() }, Ability.class.getClassLoader());
            return loader;
        }
        catch (Exception e) {}

        return null;
    }

    private static String fileListToString(List<File> list) {
        return fileListToString(list, null);
    }

    private static String fileListToString(List<File> list, String exclude) {
        if (list.isEmpty()) return "";

        StringBuffer buffy = new StringBuffer();

        for (File file : list) {
            String name = file.getName();
            int dot = name.lastIndexOf(".");

            if (exclude != null && name.contains(exclude)) {
                continue;
            }

            buffy.append(", " + name.substring(0, dot));
        }

        // Trim off the first ", ".
        return buffy.substring(2);
    }
}
