package org.sample.fleeonsight.AnimalSystem.AnimalStateMachine;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.sample.fleeonsight.AnimalSystem.Animalstate.MobState;

import static org.sample.fleeonsight.LogicConfig.FLEE_SPEED;

public class PigStateMachine implements AnimalStateMachine {

    @Override
    public void updateFriendlyState(LivingEntity pig, PlayerEntity player, MobState state) {
        if (!state.isFriendly && FOVcheck(pig, player) && (player.isHolding(Items.CARROT_ON_A_STICK) || player.isHolding(Items.POTATO) || player.isHolding(Items.BEETROOT)) && (pig.distanceTo(player) < 8)) {
            state.isFriendly = true;//entry friendly state
        }
        if (state.isFriendly && pig.getAttacker() == player) {
            state.isFriendly = false;//exit friendly state
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
