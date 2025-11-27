package org.sample.fleeonsightforsheep;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.WeakHashMap;

/*TODO：
       1.引入sneak机制
       2.引入概率触发机制
 */
public class Fleeonsightforsheep implements ModInitializer {
    public static final String MOD_ID = "FleeOnSight";
    WeakHashMap<SheepEntity, SheepAIState> sheepStates = new WeakHashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        System.out.println("FleeOnSight Mod initialized!");
    }

    private void onWorldTick(ServerWorld world) {
        var group = getAllLoadedSheep(world);
        for (SheepEntity sheep : group) {
            PlayerEntity player = getNearbyPlayer(world, sheep);
            if (player == null) {
                continue;
            }
            SheepAIState state = getState(sheep);
            SheepFleeAIManager.updateFriendlyState(sheep, player, state);
            SheepFleeAIManager.updateFleeingState(sheep, player, state);
            if (!state.isFriendly && state.isFleeing) {
                SheepFleeAIManager.applyFlee_logic(sheep, player);//执行逃跑
            }
        }
    }

    private SheepAIState getState(SheepEntity sheep) {
        return sheepStates.computeIfAbsent(sheep, s -> new SheepAIState());
    }

    private List<? extends SheepEntity> getAllLoadedSheep(ServerWorld world) {
        return world.getEntitiesByType(EntityType.SHEEP, e -> true);
    }

    private PlayerEntity getNearbyPlayer(ServerWorld world, SheepEntity sheep) {
        return world.getClosestPlayer(sheep, 32.0);
    }
}


