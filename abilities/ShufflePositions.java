import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.*;

@AbilityInfo(
    name = "Shuffle Positions",
    aliases = {"shuffle","shufflepositions"}
)
public class ShufflePositions implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        // Grab the players and add the boss
        List<LivingEntity> entities = new ArrayList<LivingEntity>(arena.getPlayersInArena());
        entities.add(boss.getEntity());
        
        // Grab the locations
        List<Location> locations = new LinkedList<Location>();
        for (LivingEntity e : entities) {
            locations.add(e.getLocation());
        }
        
        // Shuffle the entities list.
        Collections.shuffle(entities);
        
        /* The entities are shuffled, but the locations are not, so if
         * we remove the first element of each list, chances are they
         * will not match, i.e. shuffle achieved! */
        while (!entities.isEmpty() && !locations.isEmpty()) {
            entities.remove(0).teleport(locations.remove(0));
        }
    }
}
