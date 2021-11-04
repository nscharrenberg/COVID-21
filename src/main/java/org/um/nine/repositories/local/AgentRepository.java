package org.um.nine.repositories.local;

import org.um.nine.agents.baseline.BaselineAgent;
import org.um.nine.agents.reinforcement.ReinforcementAgent;
import org.um.nine.contracts.repositories.IAgentRepository;

public class AgentRepository implements IAgentRepository {
    private final BaselineAgent baselineAgent;
    private final ReinforcementAgent reinforcementAgent;

    public AgentRepository(){
        this.baselineAgent = new BaselineAgent();
        this.reinforcementAgent = new ReinforcementAgent();
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
