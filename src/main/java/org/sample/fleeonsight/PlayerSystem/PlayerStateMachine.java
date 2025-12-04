package org.sample.fleeonsight.PlayerSystem;

import net.minecraft.entity.player.PlayerEntity;
import org.sample.fleeonsight.LogicConfig;

import static org.sample.fleeonsight.LogicConfig.SNEAK_RANGE;

public class PlayerStateMachine {

    // logic of sneaking state machine
    public static void updateSneakingState(PlayerEntity player, PlayerState state) {
        if (!state.isSneaking && player.isSneaking()) {
            state.isSneaking = true;
        }
        if (state.isSneaking && !player.isSneaking()) {
            state.isSneaking = false;
        }
    }

    public static void playerStateExecute(PlayerEntity player, PlayerState state) {
        if (state.isSneaking) {
            state.detectionRange = SNEAK_RANGE;
        }
        if (!state.isSneaking) {
            state.detectionRange = LogicConfig.DEFAULT_DETECTION_RANGE;
        }
    }
}
