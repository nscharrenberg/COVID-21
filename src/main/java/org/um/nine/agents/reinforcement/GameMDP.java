package org.um.nine.agents.reinforcement;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.um.nine.agents.reinforcement.utils.AgentBox;

public class GameMDP implements MDP<AgentBox, Integer, DiscreteSpace> {
    private final DiscreteSpace actionSpace;
    private final AgentBox observationSpace = null;
    private final ArrayObservationSpace<AgentBox> arrayObservationSpace;

    public GameMDP() {
        AllPossibleActions apa = new AllPossibleActions();
        this.actionSpace = new DiscreteSpace(apa.size());
        this.arrayObservationSpace = new ArrayObservationSpace<>(new int[]{AgentBox.FEATURE_COUNT});
    }

    @Override
    public ObservationSpace<AgentBox> getObservationSpace() {
        return this.arrayObservationSpace;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return this.actionSpace;
    }

    @Override
    public AgentBox reset() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<AgentBox> step(Integer integer) {
        return null;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public MDP<AgentBox, Integer, DiscreteSpace> newInstance() {
        return null;
    }
}
