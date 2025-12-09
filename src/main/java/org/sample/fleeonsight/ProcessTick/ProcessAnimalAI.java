package org.sample.fleeonsight.ProcessTick;


import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.sample.fleeonsight.AnimalSystem.AnimalGroupManager.AnimalGroupFleeManager;
import org.sample.fleeonsight.AnimalSystem.AnimalStateMachine.AnimalStateMachine;
import org.sample.fleeonsight.AnimalSystem.Animalstate.MobState;
import org.sample.fleeonsight.PlayerSystem.PlayerState;
import org.sample.fleeonsight.PlayerSystem.PlayerStateMachine;

import java.util.List;

import static org.sample.fleeonsight.AnimalSystem.Animalstate.State.FLEEING;
import static org.sample.fleeonsight.EntityUtils.*;
import static org.sample.fleeonsight.ProcessTick.ProcessAnimalAISupport.*;

// Main class to process animal AI each tick
public class ProcessAnimalAI {

    // Process AI for all relevant animals in the world
    public static void processAnimalAI(ServerWorld world) {
        var sheepGroup = getAllLoadedSheep(world);
        var pigGroup = getAllLoadedPig(world);
        var cowGroup = getAllLoadedCow(world);
        processAI(world, sheepGroup);
        processAI(world, cowGroup);
        processAI(world, pigGroup);
    }

    // Generic method to process AI for a group of animals
    public static void processAI(ServerWorld world, List<? extends AnimalEntity> animalGroup) {

        if (animalGroup == null || animalGroup.isEmpty()) {
            return;
        }

        // Get the appropriate AI handler for the animal type
        AnimalStateMachine aiHandler = getAnimalAI(animalGroup.get(0));

        for (AnimalEntity animal : animalGroup) {

            // Find the nearest player
            PlayerEntity player = getNearbyPlayer(world, animal);
            if (player == null || player.isCreative()) {
                continue;
            }

            // Retrieve states
            PlayerState playerState = getPlayerState(player);
            MobState animalState = getMobState(animal);

            // Update states
            PlayerStateMachine.updateSneakingState(player, playerState);
            PlayerStateMachine.playerStateExecute(playerState);

            // Update animal states based on player state
            aiHandler.updateStates(animal, player, animalState, playerState);

            // Manage group fleeing behavior
            AnimalGroupFleeManager.manageGroupFlee(animal);

            // Execute fleeing logic if applicable
            if (animalState.currentState == FLEEING) {
                aiHandler.applyFlee_logic(animal, player);
            }
        }
    }
}
