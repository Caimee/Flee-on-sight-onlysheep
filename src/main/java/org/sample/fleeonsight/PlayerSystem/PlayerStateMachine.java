package org.sample.fleeonsight.PlayerSystem;

import net.minecraft.entity.player.PlayerEntity;
import org.sample.fleeonsight.LogicConfig;

import static org.sample.fleeonsight.LogicConfig.SNEAK_RANGE;

// State machine for player
public class PlayerStateMachine {

    // logic of sneaking state machine
    public static void updateSneakingState(PlayerEntity player, PlayerState state) {
        //Enter sneaking state
        if (!state.isSneaking && player.isSneaking()) {
            state.isSneaking = true;
        }

        //Exit sneaking state
        if (state.isSneaking && !player.isSneaking()) {
            state.isSneaking = false;
        }
    }

    // execute player state effects
    public static void playerStateExecute(PlayerState state) {
        if (state.isSneaking) {
            state.detectionRange = SNEAK_RANGE;
        }
        if (!state.isSneaking) {
            state.detectionRange = LogicConfig.DEFAULT_DETECTION_RANGE;
        }
    }
}
