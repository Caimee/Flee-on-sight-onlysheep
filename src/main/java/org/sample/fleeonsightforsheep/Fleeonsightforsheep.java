package org.sample.fleeonsightforsheep;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.WeakHashMap;

/*TODO：
       1.引入sneak机制
       2.引入概率触发机制
 */
public class Fleeonsightforsheep implements ModInitializer {
    public static final String MOD_ID = "Animalflee";
    private static final double DETECTION_RANGE = 6.0;
    private static final double STOP_RANGE = 22.0;
    private static final double FLEE_SPEED = 0.25;
    private static final int ANGLE = 120;// represent whole FOV
    WeakHashMap<SheepEntity, SheepAIState> sheepStates = new WeakHashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        System.out.println("FleeOnSight Mod initialized!");
    }

    private void onWorldTick(ServerWorld world) {
        var group = getAllLoadedSheep(world);
        for (SheepEntity sheep : group) {
            PlayerEntity player = getNearbyplayer(world, sheep);
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

    //sheep的flee状态机
    private void updateFleeingState(SheepEntity sheep, PlayerEntity player, SheepAIState state) {
        double distance = sheep.distanceTo(player);

        // 我去状态机太tm优雅了
        if (!state.isFleeing && distance <= DETECTION_RANGE && FOVcheck(sheep, player)) {
            state.isFleeing = true;
        }
        if (state.isFleeing && distance >= STOP_RANGE) {
            state.isFleeing = false;
            sheep.setAttacker(player);//stop fleeing and then panic wander
        }
    }

    //sheep 的 friendly 状态机
    public void updateFriendlyState(SheepEntity sheep, PlayerEntity player, SheepAIState state) {

        if (!state.isFriendly && FOVcheck(sheep, player) && player.isHolding(Items.WHEAT) && (sheep.distanceTo(player) < 8)) {
            state.isFriendly = true;
        }
    }

    private List<? extends SheepEntity> getAllLoadedSheep(ServerWorld world) {
        return world.getEntitiesByType(EntityType.SHEEP, e -> true);
    }

    private PlayerEntity getNearbyplayer(ServerWorld world, SheepEntity sheep) {
        return world.getClosestPlayer(sheep, 32.0);
    }

    // the logic of flee
    public void applyFlee_logic(SheepEntity sheep, PlayerEntity player) {
        Vec3d sheepPos = sheep.getPos();
        Vec3d playerPos = player.getPos();
        double dx = sheepPos.x - playerPos.x;
        double dz = sheepPos.z - playerPos.z;
        double distance = Math.sqrt(dx * dx + dz * dz);
        // they flee straight !
        double normalizedX = (dx / distance) * FLEE_SPEED;
        double normalizedZ = (dz / distance) * FLEE_SPEED;
        sheep.setVelocity(normalizedX, sheep.getVelocity().y, normalizedZ);
        sheep.velocityModified = true;
        float yaw = (float) (Math.atan2(normalizedZ, normalizedX) * 180.0 / Math.PI) - 90.0F;
        sheep.setYaw(yaw);
        sheep.setBodyYaw(yaw);
        sheep.setHeadYaw(yaw);
    }

    public boolean FOVcheck(SheepEntity sheep, PlayerEntity player) {
        Vec3d vec = player.getPos().subtract(sheep.getPos()).normalize();// vector from sheep to player
        Vec3d facing = Vec3d.fromPolar(0, (float) sheep.getHeadYaw()).normalize();// vector sheep's head
        double dot = facing.dotProduct(vec);
        return dot > Math.cos(Math.toRadians(ANGLE * 0.5));// ANGLE is the whole FOV
    }
}


