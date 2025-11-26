package org.sample.fleeonsightforsheep;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

public class Fleeonsightforsheep implements ModInitializer {
    public static final String MOD_ID = "Animalflee";
    private static final double DETECTION_RANGE = 6.0;
    private static final double STOP_RANGE = 19.0;
    private static final double FLEE_SPEED = 0.25;
    private static final int ANGLE = 120;// represent whole FOV
    private final WeakHashMap<SheepEntity, Boolean> fleeing = new WeakHashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        System.out.println("Animal Flee Mod initialized!");
    }

    private void onWorldTick(ServerWorld world) {

        List<ServerPlayerEntity> players = world.getPlayers();

        for (PlayerEntity player : players) {
            List<SheepEntity> group = player.getWorld().getEntitiesByClass(
                    SheepEntity.class, player.getBoundingBox().expand(25.0),
                    LivingEntity::isAlive
            );

            for (SheepEntity sheep : group) {
                fleeSheepFromPlayer(sheep, player);
            }
        }
    }

    //check whether launch flee_logic or not
    private void fleeSheepFromPlayer(SheepEntity sheep, PlayerEntity player) {

        boolean isfleeing = fleeing.getOrDefault(sheep, false);

        Vec3d animalPos = sheep.getPos();
        Vec3d playerPos = player.getPos();

        double dx = animalPos.x - playerPos.x;
        double dz = animalPos.z - playerPos.z;
        double distance = Math.sqrt(dx * dx + dz * dz);

        // 我去状态机太tm优雅了
        if (!isfleeing && distance <= DETECTION_RANGE && anglecheck(sheep, player)) {
            isfleeing = true;// so the third "if" is in the same tick
            fleeing.put(sheep, true);
        }
        if (isfleeing && distance >= STOP_RANGE) {
            isfleeing = false;
            fleeing.put(sheep, false);


        }

        if (isfleeing) {
            flee_logic(sheep, player);
        }
    }

    public void flee_logic(SheepEntity sheep, PlayerEntity player) {
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

    public boolean anglecheck(SheepEntity sheep, PlayerEntity player) {
        Vec3d vec = player.getPos().subtract(sheep.getPos()).normalize();// vector from sheep to player
        Vec3d facing = Vec3d.fromPolar(0, (float) sheep.getHeadYaw()).normalize();// vector sheep's head
        double dot = facing.dotProduct(vec);
        return dot > Math.cos(Math.toRadians(ANGLE * 0.5));// ANGLE is the whole FOV
    }
}
