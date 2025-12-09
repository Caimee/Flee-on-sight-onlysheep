package org.sample.fleeonsight.AnimalSystem.AnimalStateMachine;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.sample.fleeonsight.PlayerSystem.PlayerState;
import org.sample.fleeonsight.AnimalSystem.Animalstate.MobState;

import static org.sample.fleeonsight.AnimalSystem.Animalstate.State.*;
import static org.sample.fleeonsight.LogicConfig.*;

// Interface defining the state machine behavior for animals
public interface AnimalStateMachine {

    default void updateStates(LivingEntity animal, PlayerEntity player, MobState mobState, PlayerState playerState){
        double distance = animal.distanceTo(player);
        switch (mobState.currentState){
            case DEFAULT_EMPTY:
                if(player.isHolding(Items.WHEAT) && distance <= DEFAULT_DETECTION_RANGE + 0.2){
                    mobState.currentState = FRIENDLY;
                }
                else if(distance <= playerState.detectionRange && FOVcheck(animal, player)){
                    mobState.currentState = FLEEING;
                }
                break;

            case FRIENDLY:
                if(animal.getAttacker() == player && !player.isHolding(Items.WHEAT)){
                    mobState.currentState = FLEEING;
                }
                break;

            case FLEEING:
                if(player.isHolding(Items.WHEAT) && distance <= DEFAULT_DETECTION_RANGE + 0.2){
                    mobState.currentState = FRIENDLY;
                }

                else if(distance >= STOP_RANGE){
                    mobState.currentState = DEFAULT_EMPTY;
                    animal.setAttacker(player);
                }
                break;
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
