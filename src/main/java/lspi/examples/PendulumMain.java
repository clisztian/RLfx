package lspi.examples;

import lspi.agent.PolicySampler;
import lspi.basisfunctions.FakeBasis;

import lspi.basisfunctions.GaussianRbf;
import lspi.core.Lspi;

import lspi.domains.Pendulum;
import lspi.domains.Simulator;
import lspi.types.BasisFunctions;
import lspi.types.Policy;
import lspi.types.Sample;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PendulumMain {

    public static final Logger logger = LoggerFactory.getLogger(PendulumMain.class);

    /**
     * Main method. Runs the Pendulum domain with known working settings. These settings are taken
     * from the Lspi paper and original Matlab code.
     *
     * @param args Not used
     */
    public static void main(String[] args) {
        logger.info("Initializing pendulum domain");

        Simulator simulator = new Pendulum();
        BasisFunctions fakeBasis = new FakeBasis();
        BasisFunctions rbfBasis = new GaussianRbf(3, 3, 3);
        Vector n = Matrices.random(fakeBasis.size());
        Policy randomPolicy = new Policy(1,
                simulator.numActions(),
                fakeBasis,
                Matrices.random(fakeBasis.size()));
        Policy learnedPolicy = new Policy(0,
                simulator.numActions(),
                rbfBasis,
                Matrices.random(rbfBasis.size()));

        logger.info("Sampling 1000 episodes with 50 steps using random policy");
        List<Sample> samples = PolicySampler.sample(simulator, 1000, 50, randomPolicy);

        logger.info("Running Lspi");
        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 20, Lspi.PolicyImprover.LSTDQ_MTJ);

        logger.info("Evaluating random and learned policy");
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 1000, 50, randomPolicy);
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 1000, 50, learnedPolicy);

        logger.info("Random Policy Average Rewards: " + avgRandomRewards);
        logger.info("Learned Policy Average Rewards: " + avgLearnedRewards);

    }

}
