package com.garbagemule.MobArena.waves;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MAMessages.Msg;
import com.garbagemule.MobArena.util.WaveUtils;

public class BossWave extends AbstractWave
{
    private MACreature boss;
    private LivingEntity bossCreature;
    private List<BossAbility> abilities;
    private Set<Creature> adds;
    private BossHealth bossHealth;
    private int healthAmount, abilityTask, abilityInterval;
    private boolean lowHealthAnnounced = false, abilityAnnounce;
    
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
        abilityTask  = -1;
        abilities    = new LinkedList<BossAbility>();
        
        // Get monster and health
        boss         = MACreature.fromString(config.getString(path + "monster"));
        bossHealth   = WaveUtils.getEnumFromString(BossHealth.class, config.getString(path + "health"), BossHealth.MEDIUM);

        // Get abilities
        abilityInterval  = config.getInt(path + "ability-interval", 3) * 20;
        abilityAnnounce  = config.getBoolean(path + "ability-announce", true);
        String abilities = config.getString(path + "abilities");
        if (abilities != null)
        {
            for (String a : abilities.split(","))
            {
                String ability = a.trim();
                addAbility(BossAbility.fromString(ability));
            }
        }
    }

    public void spawn(int wave)
    {
        // Announce spawning
        MAUtils.tellAll(getArena(), Msg.WAVE_BOSS, ""+wave);
        
        // Spawn the boss and set the arena
        bossCreature = boss.spawn(getArena(), getWorld(), getArena().getBossSpawnpoint());
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
        
        // If there are no abilities, don't start the timer.
        if (abilityCount == 0)
            return;
        
        abilityTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getArena().getPlugin(),
            new Runnable()
            {
                private int counter = 0;
                
                public void run()
                {
                    // Check to see if the boss is still alive. If not, end this boss wave.
                    if (bossCreature.isDead())
                    {
                        clear();
                        return;
                    }
                    
                    // Grab the next ability
                    BossAbility ability = abilities.get(counter % abilityCount);
                    
                    // Announce it
                    if (abilityAnnounce)
                        MAUtils.tellAll(getArena(), Msg.WAVE_BOSS_ABILITY, ability.toString());
                    
                    // Activate!
                    ability.run(getArena(), bossCreature);
                    
                    // Increment counter
                    counter++;
                }
            }, 100, abilityInterval);
    }
    
    public void cancelAbilityTask()
    {
        if (abilityTask != -1)
            Bukkit.getServer().getScheduler().cancelTask(abilityTask);
    }
    
    public void clear()
    {
        cancelAbilityTask();
        getArena().setBossWave(null);
        
        if (bossCreature == null) return;
            
        CraftEntity ce = (CraftEntity) bossCreature;
        CraftWorld cw = (CraftWorld) getWorld();
        WorldServer ws = cw.getHandle();
        Location l = bossCreature.getLocation();
        ws.createExplosion(ce.getHandle(), l.getX(), l.getY() + 1, l.getZ(), 1f, false);
        
        bossCreature.remove();
    }
    
    public void addAbility(BossAbility ability)
    {
        abilities.add(ability);
    }
    
    public void addAdds(Creature creature)
    {
        adds.add(creature);
    }
    
    public LivingEntity getEntity()
    {
        return bossCreature;
    }
    
    public int getHealth()
    {
        return healthAmount;
    }
    
    public void setHealth(int healthAmount)
    {
        this.healthAmount = healthAmount;
    }
    
    public void subtractHealth(int amount)
    {
        healthAmount -= amount;
    }
    
    public boolean isLowHealthAnnounced()
    {
        return lowHealthAnnounced;
    }
    
    public void setLowHealthAnnounced(boolean value)
    {
        lowHealthAnnounced = value;
    }
}
