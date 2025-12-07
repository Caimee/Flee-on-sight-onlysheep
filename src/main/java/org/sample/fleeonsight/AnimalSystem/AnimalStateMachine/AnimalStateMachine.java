package org.sample.fleeonsight.AnimalSystem.AnimalStateMachine;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.sample.fleeonsight.PlayerSystem.PlayerState;
import org.sample.fleeonsight.AnimalSystem.Animalstate.MobState;

import static org.sample.fleeonsight.LogicConfig.*;

// Interface defining the state machine behavior for animals
public interface AnimalStateMachine {

    //logic of fleeing state machine
    default void updateFleeingState(LivingEntity animal, PlayerEntity player, MobState MobState) {

        // Enter fleeing state
        if (!MobState.isFleeing && ((MobState.isPlayerDetected && !MobState.isFriendly)|| MobState.isGroupStartled)) {
            MobState.isFleeing = true;
        }

        // Exit fleeing state
        if(MobState.isFleeing && !MobState.isPlayerDetected){
            MobState.isFleeing = false;
            animal.setAttacker(player);
        }
        if (MobState.isFleeing && MobState.isFriendly) {
            MobState.isFleeing = false;
        }
    }

    default void updatePlayerDetectedState(LivingEntity animal, PlayerEntity player,MobState MobState, PlayerState playerState) {
        double distance = animal.distanceTo(player);

        if (!MobState.isPlayerDetected && distance <= playerState.detectionRange && FOVcheck(animal, player) ) {
            MobState.isPlayerDetected = true;
        }

        if (MobState.isPlayerDetected && distance >= STOP_RANGE) {
            MobState.isPlayerDetected = false;
        }
    }

    default void updateGroupStartledState(MobEntity animal, MobState MobState) {
        if (MobState.isGroupStartled) {
            MobState.isGroupStartled = false;
        }
    }

    //logic of friendly state machine
    default void updateFriendlyState(LivingEntity animal, PlayerEntity player, MobState state) {

        // Enter friendly state
        if (!state.isFriendly  && FOVcheck(animal, player) && player.isHolding(Items.WHEAT) && (animal.distanceTo(player) < DEFAULT_DETECTION_RANGE + 0.2)) {
            state.isFriendly = true;//entry friendly state
        }

        // Exit friendly state
        if (state.isFriendly && animal.getAttacker() == player) {
            state.isFriendly = false;
        }
    }

    default boolean FOVcheck(LivingEntity animal, PlayerEntity player) {
        Vec3d vec = player.getPos().subtract(animal.getPos()).normalize();// vector from animal to player
        Vec3d facing = Vec3d.fromPolar(0, animal.getHeadYaw()).normalize();// vector animal's head

        // calculate and compare the dot product
        double dot = facing.dotProduct(vec);
        return dot > Math.cos(Math.toRadians(ANGLE * 0.5));// ANGLE is the whole FOV !
    }

    //logic of flee
    default void applyFlee_logic(MobEntity animal, PlayerEntity player) {
        Vec3d fromPlayer = animal.getPos().subtract(player.getPos()).normalize();// vector from player to animal
        Vec3d fleeDir = fromPlayer.multiply(26.5);// flee distance
        Vec3d targetPos = animal.getPos().add(fleeDir);// target position
        // initiate movement towards the target position at flee speed
        animal.getNavigation().startMovingTo(
                targetPos.x,
                targetPos.y,
                targetPos.z,
                FLEE_SPEED
        );
    }

}
