package org.sample.fleeonsight.ProcessTick;

import org.sample.fleeonsight.AnimalSystem.AnimalStateMachine.CowStateMachine;
import org.sample.fleeonsight.AnimalSystem.AnimalStateMachine.PigStateMachine;
import org.sample.fleeonsight.AnimalSystem.AnimalStateMachine.SheepStateMachine;

public class ProcessAnimalAISupport {
    public final static SheepStateMachine SheepAI = new SheepStateMachine();
    public final static PigStateMachine PigAI = new PigStateMachine();
    public final static CowStateMachine CowAI = new CowStateMachine();
}
