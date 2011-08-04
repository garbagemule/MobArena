package com.garbagemule.MobArena.waves;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.util.WaveUtils;

public class BossWave extends AbstractWave// TODO: implement/extend something?
{
    private MACreature boss;
    private LivingEntity bossCreature;
    private List<BossAbility> abilities;
    private Set<Creature> adds;
    private BossHealth bossHealth;
    private int healthAmount;
    private List<Integer> taskList;
    
    // Recurrent
    public BossWave(Arena arena, String name, int wave, int frequency, int priority, Configuration config, String path)
    {
        super(arena, name, wave, frequency, priority);
        load(config, path);
    }
    
    // Single
    public BossWave(Arena arena, String name, int wave, Configuration config, String path)
    {
        super(arena, name, wave);
        load(config, path);
    }
    
    private void load(Configuration config, String path)
    {
        setType(WaveType.BOSS);
        taskList     = new LinkedList<Integer>();
        abilities    = new LinkedList<BossAbility>();
        
        // Get monster and health
        boss         = MACreature.fromString(config.getString(path + "monster"));
        bossHealth   = WaveUtils.getEnumFromString(BossHealth.class, config.getString(path + "health"), BossHealth.MEDIUM);

        // Get abilities
        String abilities = config.getString(path + "abilities");
        if (abilities != null)
        {
            for (String a : abilities.split(","))
            {
                String ability = a.trim().replaceAll("-", "_").toUpperCase();
                System.out.println(ability);
                addAbility(BossAbility.fromString(ability));
            }
        }
    }

    public void spawn(int wave)
    {
        // Spawn the boss and set the arena
        bossCreature = boss.spawn(getWorld(), getArena().getBossSpawnpoint());
        if (bossCreature instanceof Creature)
            ((Creature) bossCreature).setTarget(MAUtils.getClosestPlayer(bossCreature, getArena()));
        getArena().addMonster(bossCreature);
        getArena().setBossWave(this);
        
        // Set the health stuff
        bossCreature.setHealth(200);
        healthAmount = bossHealth.getAmount(getArena().getPlayerCount());
        
        startAbilityTasks();
    }
    
    private void startAbilityTasks()
    {
        final int abilityCount = abilities.size();
        
        int i = 1;
        for (final BossAbility ability : abilities)
        {
            // Schedule the task
            int abilityTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getArena().getPlugin(),
                new Runnable()
                {
                    public void run()
                    {
                        ability.activate(getArena(), bossCreature);
                    }
                }, 50*i, 50*abilityCount);
            
            // Add the task to the task list for cancelling later, and increment counter
            taskList.add(abilityTask);
            i++;
        }
    }
    
    public void cancelAbilityTasks()
    {
        for (Integer i : taskList)
            Bukkit.getServer().getScheduler().cancelTask(i);
    }
    
    public void clear()
    {
        cancelAbilityTasks();
        getArena().setBossWave(null);
        getWorld().createExplosion(bossCreature.getLocation(), 1);
        bossCreature.damage(32768);
    }
    
    public void addAbility(BossAbility ability)
    {
        abilities.add(ability);
    }
    
    public void addAdds(Creature creature)
    {
        adds.add(creature);
    }
    
    public int getHealth()
    {
        return healthAmount;
    }
    
    public LivingEntity getEntity()
    {
        return bossCreature;
    }
    
    public void setHealth(int healthAmount)
    {
        this.healthAmount = healthAmount;
    }
    
    public void subtractHealth(int amount)
    {
        healthAmount -= amount;
    }
}
