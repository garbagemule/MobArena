package com.garbagemule.MobArena.waves;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Creature;

import com.garbagemule.MobArena.waves.Wave.BossAbility;

public class BossWave // TODO: implement/extend something?
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

    public void spawn(int wave, Collection<Location> spawnpoints)
    {
        // Spawn boss and adds
        // Something like this, perhaps? Pseudo-code
        // LivingEntity b = spawnCreature(bossType, random location)
        // boss = (Creature) b;
        // boss.setHealth(health);
        // for (String a : ablts)
        //    abilities.add(BossAbility.fromString(a));
        // for (int i = 0; i < addCount; i++)
        //    adds.add(spawnCreature(addType, bossLocation);
    }
}
