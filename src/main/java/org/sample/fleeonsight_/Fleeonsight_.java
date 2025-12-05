package org.sample.fleeonsight_;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.sample.fleeonsight_.AnimalSystem.Animalstate.MobState;
import org.sample.fleeonsight_.PlayerSystem.PlayerState;

import java.util.WeakHashMap;

import static org.sample.fleeonsight_.ProcessTick.ProcessAnimalAI.*;

public class Fleeonsight_ implements ModInitializer {
    public static final String MOD_ID = "FleeOnSight";
    public static WeakHashMap<MobEntity, MobState> MobStates = new WeakHashMap<>();// Store mob states with weak references
    public static WeakHashMap<PlayerEntity, PlayerState> playerStates = new WeakHashMap<>();// Store player states with weak references

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        System.out.println("FleeOnSight Mod initialized!");
    }

    private void onWorldTick(ServerWorld world) {

        // Process animal AI each tick
        processAnimalAI(world);
    }
}


