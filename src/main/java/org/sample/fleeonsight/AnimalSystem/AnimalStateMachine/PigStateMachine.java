package org.sample.fleeonsight.AnimalSystem.AnimalStateMachine;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.sample.fleeonsight.AnimalSystem.Animalstate.MobState;

import static org.sample.fleeonsight.AnimalSystem.Animalstate.State.*;
import static org.sample.fleeonsight.LogicConfig.DEFAULT_DETECTION_RANGE;
import static org.sample.fleeonsight.LogicConfig.FLEE_SPEED;

// State machine for pig
public class PigStateMachine implements AnimalStateMachine {

    @Override
    public void updateStates(LivingEntity pig, PlayerEntity player, MobState mobState, org.sample.fleeonsight.PlayerSystem.PlayerState playerState) {
        double distance = pig.distanceTo(player);
        switch (mobState.currentState){
            case DEFAULT_EMPTY:
                if((player.isHolding(Items.CARROT) || player.isHolding(Items.POTATO) || player.isHolding(Items.BEETROOT) || player.isHolding(Items.CARROT_ON_A_STICK)) && distance <= DEFAULT_DETECTION_RANGE + 0.2){
                    mobState.currentState = FRIENDLY;
                }

                else if(distance <= playerState.detectionRange && FOVcheck(pig, player)){
                    mobState.currentState = FLEEING;
                }
                break;

            case FRIENDLY:
                if(pig.getAttacker() == player && !(player.isHolding(Items.CARROT) || player.isHolding(Items.POTATO) || player.isHolding(Items.BEETROOT) || player.isHolding(Items.CARROT_ON_A_STICK))){
                    mobState.currentState = FLEEING;
                }
                break;

            case FLEEING:
                if((player.isHolding(Items.CARROT) || player.isHolding(Items.POTATO) || player.isHolding(Items.BEETROOT) || player.isHolding(Items.CARROT_ON_A_STICK)) && distance <= DEFAULT_DETECTION_RANGE + 0.2){
                    mobState.currentState = FRIENDLY;
                }
                else if(distance >= org.sample.fleeonsight.LogicConfig.STOP_RANGE){
                    mobState.currentState = DEFAULT_EMPTY;
                    pig.setAttacker(player);
                }
                break;
        }
    }

    @Override
    public void applyFlee_logic(net.minecraft.entity.mob.MobEntity pig, PlayerEntity player) {
        Vec3d fromPlayer = pig.getPos().subtract(player.getPos()).normalize();
        Vec3d fleeDir = fromPlayer.multiply(26.5);
        Vec3d targetPos = pig.getPos().add(fleeDir);
        pig.getNavigation().startMovingTo(
                targetPos.x,
                targetPos.y,
                targetPos.z,
                FLEE_SPEED * 0.70
        );
    }
}
