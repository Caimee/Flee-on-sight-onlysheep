package org.sample.fleeonsight_;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.sample.fleeonsight_.AnimalSystem.Animalstate.MobState;
import org.sample.fleeonsight_.PlayerSystem.PlayerState;

import java.util.List;

import static org.sample.fleeonsight_.Fleeonsight_.MobStates;
import static org.sample.fleeonsight_.Fleeonsight_.playerStates;

// Utility class for entity-related operations
public class EntityUtils {

    public static List<? extends SheepEntity> getAllLoadedSheep(ServerWorld world) {
        return world.getEntitiesByType(EntityType.SHEEP, e -> true);
    }

    public static List<? extends PigEntity> getAllLoadedPig(ServerWorld world) {
        return world.getEntitiesByType(EntityType.PIG, e -> true);
    }

    public static List<? extends CowEntity> getAllLoadedCow(ServerWorld world) {
        return world.getEntitiesByType(EntityType.COW, e -> true);
    }

    public static PlayerEntity getNearbyPlayer(ServerWorld world, MobEntity mob) {
        return world.getClosestPlayer(mob, 42.6);
    }

    // Retrieve or create the PlayerState for a given player
    public static PlayerState getPlayerState(PlayerEntity player) {
        return playerStates.computeIfAbsent(player, p -> new PlayerState());
    }

    // Retrieve or create the MobState for a given mob
    public static MobState getMobState(MobEntity mob) {
        return MobStates.computeIfAbsent(mob, s -> new MobState());
    }
}
