package org.sample.fleeonsightforsheep;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;


public class SheepFleeAIManager {
    private static final double DETECTION_RANGE = 6.0;
    private static final double STOP_RANGE = 22.0;
    private static final double FLEE_SPEED = 2.1;
    private static final int ANGLE = 120;// represent whole FOV

    //sheep 的 fleeing 状态机
    public static void updateFleeingState(SheepEntity sheep, PlayerEntity player, SheepAIState state) {
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
    public static void updateFriendlyState(SheepEntity sheep, PlayerEntity player, SheepAIState state) {
        if (!state.isFriendly && FOVcheck(sheep, player) && player.isHolding(Items.WHEAT) && (sheep.distanceTo(player) < 8)) {
            state.isFriendly = true;
        }
    }

    public static boolean FOVcheck(SheepEntity sheep, PlayerEntity player) {
        Vec3d vec = player.getPos().subtract(sheep.getPos()).normalize();// vector from sheep to player
        Vec3d facing = Vec3d.fromPolar(0, sheep.getHeadYaw()).normalize();// vector sheep's head
        double dot = facing.dotProduct(vec);
        return dot > Math.cos(Math.toRadians(ANGLE * 0.5));// ANGLE is the whole FOV
    }

    // the logic of flee
    public static void applyFlee_logic(SheepEntity sheep, PlayerEntity player) {
        Vec3d fromPlayer = sheep.getPos().subtract(player.getPos()).normalize();

        // 逃跑方向（反方向）
        Vec3d fleeDir = fromPlayer.multiply(22.0); // 逃跑 12 格距离

        // 逃跑目标点
        Vec3d targetPos = sheep.getPos().add(fleeDir);

        // 使用导航去逃跑
        sheep.getNavigation().startMovingTo(
                targetPos.x,
                targetPos.y,
                targetPos.z,
                FLEE_SPEED
        );
    }
}
