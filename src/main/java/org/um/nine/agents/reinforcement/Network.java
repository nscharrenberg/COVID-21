package org.um.nine.agents.reinforcement;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.nd4j.linalg.learning.config.RmsProp;

import java.io.File;
import java.io.IOException;

public class Network {
    private static final long SEED_VALUE = 123L;
    public static QLearningConfiguration config() {
        return QLearningConfiguration.builder()
                .seed(SEED_VALUE)
                .maxEpochStep(200)
                .maxStep(15000)
                .expRepMaxSize(150000)
                .batchSize(128)
                .targetDqnUpdateFreq(500)
                .updateStart(10)
                .rewardFactor(.01)
                .gamma(.99)
                .errorClamp(.1f)
                .epsilonNbStep(1000)
                .doubleDQN(true)
                .build();
    }

    public static DQNFactoryStdDense buildFactory() {
        DQNDenseNetworkConfiguration build = DQNDenseNetworkConfiguration.builder()
                .l2(.001)
                .updater(new RmsProp(.000025))
                .numHiddenNodes(300)
                .numLayers(2)
                .build();

        return new DQNFactoryStdDense(build);
    }

    public static MultiLayerNetwork load (String name) {
        try {
            return MultiLayerNetwork.load(new File(name), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
