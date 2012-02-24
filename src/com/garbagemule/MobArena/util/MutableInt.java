package com.garbagemule.MobArena.util;

public class MutableInt
{
    private int value;
    
    public MutableInt(int value) {
        this.value = value;
    }
    
    public MutableInt() {
        this(0);
    }
    
    public void add(int amount) {
        this.value += amount;
    }
    
    public void inc() {
        this.value++;
    }
    
    public int value() {
        return value;
    }
    
    @Override
    public String toString() {
        return "" + value;
    }
}
