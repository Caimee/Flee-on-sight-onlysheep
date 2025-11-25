package org.sample.fleeonsightforsheep;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Fleeonsightforsheep implements ModInitializer { public static final String MOD_ID = "Animalflee";
    private static final double DETECTION_RANGE = 8.0;
    private static final double FLEE_SPEED = 0.25;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        System.out.println("Animal Flee Mod initialized!");
    }

    private void onWorldTick(ServerWorld world) {

        List<ServerPlayerEntity> players = world.getPlayers();

        for (PlayerEntity player : players) {
            Box detectionBox = new Box(
                    player.getX() - DETECTION_RANGE,
                    player.getY() - DETECTION_RANGE,
                    player.getZ() - DETECTION_RANGE,
                    player.getX() + DETECTION_RANGE,
                    player.getY() + DETECTION_RANGE,
                    player.getZ() + DETECTION_RANGE
            );

            List<SheepEntity> nearbyanimal = world.getEntitiesByClass(
                    SheepEntity.class,
                    detectionBox,
                    sheep -> sheep.isAlive()
            );

            for (SheepEntity sheep : nearbyanimal) {
                fleeSheepFromPlayer(sheep, player);
            }
        }
    }

    private void fleeSheepFromPlayer(SheepEntity sheep, PlayerEntity player) {
        double sheepyaw = sheep.getHeadYaw();
        Vec3d animalPos = sheep.getPos();
        Vec3d playerPos = player.getPos();
        Vec3d vec = playerPos.subtract(animalPos); // vector from sheep to player
        Vec3d facing = Vec3d.fromPolar(0, (float) sheepyaw);
        boolean sightcheck = (2*(vec.normalize().dotProduct(facing.normalize())) > 1);//shit

        double dx = animalPos.x - playerPos.x;
        double dz = animalPos.z - playerPos.z;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0 && distance < DETECTION_RANGE && sightcheck) {
            sheep.setAttacker(player);

        }
    }


}

