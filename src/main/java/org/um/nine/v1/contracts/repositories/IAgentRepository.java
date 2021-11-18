package org.um.nine.v1.contracts.repositories;

import org.um.nine.v1.agents.baseline.BaselineAgent;
import org.um.nine.v1.agents.reinforcement.ReinforcementAgent;

public interface IAgentRepository {
    BaselineAgent baselineAgent();
    ReinforcementAgent reinforcementAgent();
}
