package org.sample.fleeonsight.ProcessTick;

import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.sample.fleeonsight.AnimalSystem.AnimalGroupManager.AnimalGroupFleeManager;
import org.sample.fleeonsight.AnimalSystem.Animalstate.MobState;
import org.sample.fleeonsight.PlayerSystem.PlayerState;
import org.sample.fleeonsight.PlayerSystem.PlayerStateMachine;

import java.util.List;

import static org.sample.fleeonsight.EntityUtils.*;
import static org.sample.fleeonsight.ProcessTick.ProcessAnimalAISupport.*;

public class ProcessAnimalAI {


    public static void processSheepAI(ServerWorld world, List<? extends SheepEntity> sheepGroup) {
        for (SheepEntity sheep : sheepGroup) {
            PlayerEntity player = getNearbyPlayer(world, sheep);
            if (player == null) {
                continue;
            }
            PlayerState playerState = getPlayerState(player);
            PlayerStateMachine.updateSneakingState(player, playerState);
            PlayerStateMachine.playerStateExecute(playerState);
            MobState sheepState = getMobState(sheep);
            SheepAI.updateFriendlyState(sheep, player, sheepState);
            SheepAI.updateFleeingState(sheep, player, sheepState, playerState);
            AnimalGroupFleeManager.manageSheepGroupFlee(sheep);
            if (!sheepState.isFriendly && sheepState.isFleeing) {
                SheepAI.applyFlee_logic(sheep, player);
            }
        }
    }

    public static void processPigAI(ServerWorld world, List<? extends PigEntity> pigGroup) {
        for (PigEntity pig : pigGroup) {
            PlayerEntity player = getNearbyPlayer(world, pig);
            if (player == null) {
                continue;
            }
            PlayerState playerState = getPlayerState(player);
            PlayerStateMachine.updateSneakingState(player, playerState);
            PlayerStateMachine.playerStateExecute(playerState);
            MobState pigState = getMobState(pig);
            PigAI.updateFriendlyState(pig, player, pigState);
            PigAI.updateFleeingState(pig, player, pigState, playerState);
            AnimalGroupFleeManager.managePigGroupFlee(pig);
            if (!pigState.isFriendly && pigState.isFleeing) {
                PigAI.applyFlee_logic(pig, player);
            }
        }
    }

    public static void processCowAI(ServerWorld world, List<? extends CowEntity> cowGroup) {
        for (CowEntity cow : cowGroup) {
            PlayerEntity player = getNearbyPlayer(world, cow);
            if (player == null) {
                continue;
            }
            PlayerState playerState = getPlayerState(player);
            PlayerStateMachine.updateSneakingState(player, playerState);
            PlayerStateMachine.playerStateExecute(playerState);
            MobState cowState = getMobState(cow);
            CowAI.updateFriendlyState(cow, player, cowState);
            CowAI.updateFleeingState(cow, player, cowState, playerState);
            AnimalGroupFleeManager.manageCowGroupFlee(cow);
            if (!cowState.isFriendly && cowState.isFleeing) {
                CowAI.applyFlee_logic(cow, player);
            }
        }
    }
}
