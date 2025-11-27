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
    private final WeakHashMap<SheepEntity, Boolean> fleeing = new WeakHashMap<>();
    private final WeakHashMap<SheepEntity, Boolean> friendlySheep = new WeakHashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        System.out.println("Animal Flee Mod initialized!");
    }

    private void onWorldTick(ServerWorld world) {
        List<ServerPlayerEntity> players = world.getPlayers();

        var group =  world.getEntitiesByType(EntityType.SHEEP, e -> true);
        for (SheepEntity sheep : group) {
            PlayerEntity player = world.getClosestPlayer(sheep, 32.0);
            if (player == null) {
                continue;
            }
            sheep_state_check(sheep, player);
            if (!friendlySheep.getOrDefault(sheep, false)) {
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
            sheep.setAttacker(player);//stop fleeing and then panic wander
            return;
        }
        // 执行
        if (isfleeing) {
            flee_logic(sheep, player);
        }
    }

    // the logic of flee
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

    public boolean filter(PlayerEntity player) {
        return !player.isHolding(Items.WHEAT);
    }

    //sheep 的 friendly 状态机
    public void sheep_state_check(SheepEntity sheep, PlayerEntity player) {
        boolean is_friendly = friendlySheep.getOrDefault(sheep, false);
        friendlySheep.putIfAbsent(sheep, false);
        if (!is_friendly && anglecheck(sheep, player) && player.isHolding(Items.WHEAT) && (sheep.distanceTo(player) < 8)) {
            is_friendly = true;
            friendlySheep.put(sheep, true);
        }
    }
}


