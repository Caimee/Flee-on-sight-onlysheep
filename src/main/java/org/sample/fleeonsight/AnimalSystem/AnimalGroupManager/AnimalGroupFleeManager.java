package org.sample.fleeonsight.AnimalSystem.AnimalGroupManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import org.sample.fleeonsight.LogicConfig;

import java.util.function.Predicate;

import static org.sample.fleeonsight.EntityUtils.getMobState;
import static org.sample.fleeonsight.LogicConfig.delayTicks;

/**
 * Generic manager that spreads the fleeing state among herd animals.
 */
public class AnimalGroupFleeManager {

    /**
     * Generic method to spread fleeing behavior among animals of the same type.
     */
    public static <T extends AnimalEntity> void manageGroupFlee(T animal) {

        EntityType<?> type = animal.getType();
        var state = getMobState(animal);

        // skip if the animal is not fleeing
        if (!state.isFleeing) {
            state.timer = 0;
            return;
        }

        // increase timer up to max of 5
        if (state.timer < delayTicks) {
            state.timer++;
            return;
        }

        var world = animal.getWorld();

        // Get all animals of the same type nearby
        var nearby = world.getEntitiesByType(
                type,
                animal.getBoundingBox().expand(LogicConfig.GROUP_FLEE_RADIUS),
                Predicate.not(a -> a == animal)
        );

        // Set fleeing state to each nearby animal that is not fleeing
        for (Entity other : nearby) {
            var otherState = getMobState((MobEntity) other);
            if (!otherState.isFleeing) {
                otherState.isFleeing = true;
            }
        }
        state.timer = 0;
    }
}

