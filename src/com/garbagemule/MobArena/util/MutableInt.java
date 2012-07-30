package com.garbagemule.MobArena.util;

public class MutableInt
{
    private int value;
    
    /**
     * Create a new MutableInt with the given value.
     * @param value the initial value of the MutableInt
     */
    public MutableInt(int value) {
        this.value = value;
    }
    
    /**
     * Create a new MutableInt with value 0.
     */
    public MutableInt() {
        this(0);
    }
    
    /**
     * Add the given amount to the internal int value.
     * @param amount the amount to add
     */
    public void add(int amount) {
        this.value += amount;
    }
    
    /**
     * Subtract the given amount from the internal int value.
     * @param amount the amount to subtract
     */
    public void sub(int amount) {
        this.value -= amount;
    }
    
    /**
     * Increment the value and return it.
     * This is essentially the same as calling add(1), followed by value().
     * @return the value after incrementing by one
     */
    public int inc() {
        return ++this.value;
    }
    
    /**
     * Decrement the value and return it.
     * This is essentially the same as calling sub(1), followed by value().
     * @return the value after decrementing by one
     */
    public int dec() {
        return --this.value;
    }
    
    /**
     * The value of the MutableInt.
     * @return the current value
     */
    public int value() {
        return value;
    }
    
    @Override
    public String toString() {
        return "" + value;
    }
}
