package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.plantarum.core.cut.CounterAgentCut;
import ru.plantarum.core.entity.CounterAgent;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CounterAgentCutService {

    private final CounterAgentService counterAgentService;
    /*
     * method for separate important fields of CounterAgents
     */
    public List<CounterAgentCut> getCutAgents() {
        List<CounterAgent> agents = counterAgentService.findAllActive();
        List<CounterAgentCut> cutAgents = new ArrayList<>();
        for (CounterAgent agent : agents) {
            cutAgents.add(new CounterAgentCut(agent.getIdCounterAgent(), agent.getCounterAgentName(),
                    agent.getCounterAgentPhone(), agent.getCounterAgentProfile()));
        }
        return cutAgents;
    }
}
