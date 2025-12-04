package org.sample.fleeonsight;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.sample.fleeonsight.AnimalSystem.AnimalStateMachine.CowStateMachine;
import org.sample.fleeonsight.AnimalSystem.AnimalStateMachine.PigStateMachine;
import org.sample.fleeonsight.AnimalSystem.AnimalStateMachine.SheepStateMachine;
import org.sample.fleeonsight.AnimalSystem.Animalstate.MobState;
import org.sample.fleeonsight.PlayerSystem.PlayerState;

import java.util.WeakHashMap;

import static org.sample.fleeonsight.EntityUtils.*;
import static org.sample.fleeonsight.ProcessTick.ProcessAnimalAI.*;

public class Fleeonsight implements ModInitializer {
    public static final String MOD_ID = "FleeOnSight";
    public static WeakHashMap<MobEntity, MobState> MobStates = new WeakHashMap<>();
    public static WeakHashMap<PlayerEntity, PlayerState> playerStates = new WeakHashMap<>();
    SheepStateMachine SheepAI = new SheepStateMachine();
    PigStateMachine PigAI = new PigStateMachine();
    CowStateMachine CowAI = new CowStateMachine();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        System.out.println("FleeOnSight Mod initialized!");
    }

    private void onWorldTick(ServerWorld world) {
        var sheepGroup = getAllLoadedSheep(world);
        var pigGroup = getAllLoadedPig(world);
        var cowGroup = getAllLoadedCow(world);
        processSheepAI(world, sheepGroup);
        processCowAI(world, cowGroup);
        processPigAI(world, pigGroup);
    }


}


