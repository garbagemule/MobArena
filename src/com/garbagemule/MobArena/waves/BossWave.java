package com.garbagemule.MobArena.waves;

import java.util.Set;

import org.bukkit.entity.Creature;

import com.garbagemule.MobArena.waves.Wave.BossAbility;

public class BossWave
{    
    private Creature boss;
    private Set<BossAbility> abilities;
    private Set<Creature> adds;
    private int health;
    
    public BossWave(Creature boss)
    {
        this.boss = boss;
    }
    
    public void addAbility(BossAbility ability)
    {
        abilities.add(ability);
    }
    
    public void addAdds(Creature creature)
    {
        adds.add(creature);
    }
    
    public void setHealth(int health)
    {
        this.health = health;
    }
}
