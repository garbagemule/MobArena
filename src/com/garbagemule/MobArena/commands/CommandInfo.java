package com.garbagemule.MobArena.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo
{
    /**
     * The actual name of the command. Not really used anywhere.
     */
    public String name();
    
    /**
     * A regex pattern that allows minor oddities and alternatives to the command name.
     */
    public String pattern();
    
    /**
     * The usage message, i.e. how the command should be used.
     */
    public String usage();
    
    /**
     * A description of what the command does.
     */
    public String desc();
    
    /**
     * The permission required to execute this command.
     */
    public String permission();
}