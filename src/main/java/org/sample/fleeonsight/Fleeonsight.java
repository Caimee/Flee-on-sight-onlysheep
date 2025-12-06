package org.sample.fleeonsight;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.sample.fleeonsight.AnimalSystem.Animalstate.MobState;
import org.sample.fleeonsight.PlayerSystem.PlayerState;

import java.util.WeakHashMap;

import static org.sample.fleeonsight.ProcessTick.ProcessAnimalAI.*;

//Todo: support mod menu and config file for these parameters

public class Fleeonsight implements ModInitializer {
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


