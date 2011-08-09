package com.garbagemule.MobArena.util;

import java.io.Serializable;

public class MAInventoryItem implements Serializable
{
    private static final long serialVersionUID = 739709220350581510L;
    private int typeId;
    private int amount;
    private short durability;
    
    public MAInventoryItem(int typeId, int amount, short durability)
    {
        this.typeId = typeId;
        this.amount = amount;
        this.durability = durability;
    }

    public int getTypeId()       { return typeId; }
    public int getAmount()       { return amount; }
    public short getDurability() { return durability; }
}
