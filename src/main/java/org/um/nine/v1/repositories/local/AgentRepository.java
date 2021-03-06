package org.um.nine.v1.repositories.local;

import com.google.inject.Inject;
import org.um.nine.v1.agents.baseline.BaselineAgent;
import org.um.nine.v1.agents.reinforcement.ReinforcementAgent;
import org.um.nine.v1.contracts.repositories.IAgentRepository;

public class AgentRepository implements IAgentRepository {


    @Inject
    private BaselineAgent baselineAgent;
    @Inject
    private ReinforcementAgent reinforcementAgent;

    @Inject
    public AgentRepository(BaselineAgent baselineAgent, ReinforcementAgent reinforcementAgent){
        this.baselineAgent = baselineAgent;
        this.reinforcementAgent = reinforcementAgent;
    }


    @Override
    public BaselineAgent baselineAgent() {
        return this.baselineAgent;
    }

    @Override
    public ReinforcementAgent reinforcementAgent() {
        return this.reinforcementAgent;
    }
}
