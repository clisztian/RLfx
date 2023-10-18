package lspi.examples;


import lspi.agent.PolicySampler;
import lspi.basisfunctions.FakeBasis;
import lspi.basisfunctions.PolynomialBasis;
import lspi.core.Lspi;
import lspi.domains.Chain;
import lspi.domains.Simulator;
import lspi.types.BasisFunctions;
import lspi.types.Policy;
import lspi.types.Sample;
import no.uib.cipr.matrix.Matrices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ChainMain {

    public static final Logger logger = LoggerFactory.getLogger(ChainMain.class);

    /**
     * Runs the chain domain with Lspi with a set of parameters that are known to work. These
     * parameters are from the paper and original Matlab implementation of Lspi.
     *
     * @param args Now arguments are used
     */
    public static void main(String[] args) {
        logger.info("Initializing chain domain with 10 states and 90% success probability");

        Simulator simulator = new Chain(10, .9, 0);
        BasisFunctions fakeBasis = new FakeBasis();
        BasisFunctions polyBasis = new PolynomialBasis(3, simulator.numActions());
        Policy randomPolicy = new Policy(1,
                simulator.numActions(),
                fakeBasis,
                Matrices.random(fakeBasis.size()));
        Policy learnedPolicy = new Policy(0,
                simulator.numActions(),
                polyBasis,
                Matrices.random(polyBasis.size()));

        logger.info("Sampling 10 episodes with 500 steps using random policy");
        List<Sample> samples = PolicySampler.sample(simulator, 10, 500, randomPolicy);

        logger.info("Running Lspi");
        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10, Lspi.PolicyImprover.LSTDQ_MTJ);

        logger.info("Evaluating random and learned policy");
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        logger.info("Random Policy Average Rewards: " + avgRandomRewards);
        logger.info("Learned Policy Average Rewards: " + avgLearnedRewards);

        System.out.println("Random Policy Average Rewards: " + avgRandomRewards);
        System.out.println("Learned Policy Average Rewards: " + avgLearnedRewards);

    }
}
