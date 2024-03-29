package lspi.basisfunctions;

import lspi.types.BasisFunctions;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.SparseVector;

import java.io.Serializable;

public class ExactBasis implements BasisFunctions, Serializable {

    protected int[] numStates;
    protected int[] offsets;
    protected int numActions;

    /**
     * Constructs ExactBasis instance.
     *
     * <p>
     * numStates contains the number of possible values for each part of the state vector. For
     * example a state that consists of x and y position in a 10x10 grid would have a numStates
     * value of {10, 10}.
     *
     * @param numStates  Number of possible values for each part of the state
     * @param numActions Number of possible actions
     */
    public ExactBasis(int[] numStates, int numActions) {
        this.numStates = new int[numStates.length];
        System.arraycopy(numStates, 0, this.numStates, 0, numStates.length);
        this.offsets = new int[numStates.length];
        this.offsets[0] = 1;
        for (int i = 1; i < offsets.length; i++) {
            offsets[i] = offsets[i - 1] * numStates[i - 1];
        }
        this.numActions = numActions;
    }

    /**
     * Given a world state and an action return the index in the sparse vector that is equal to 1.
     * There will only be one value that is non-zero and its value will be 1.
     *
     * @param state  Environment state
     * @param action Action being performed
     * @return Index in sparse vector equal to 1
     */
    public int getStateActionIndex(Vector state, int action) {
        int base = action * (this.size() / numActions);

        int offset = 0;
        for (int i = 0; i < state.size(); i++) {
            offset += offsets[i] * state.get(i);
        }
        return base + offset;
    }

    @Override
    public Vector evaluate(Vector state, int action) {
        Vector result = new SparseVector(this.size());

        int index = getStateActionIndex(state, action);

        result.set(index, 1);
        return result;
    }

    @Override
    public int size() {
        int totalStates = 1;
        for (int i : numStates) {
            totalStates *= i;
        }
        return totalStates * numActions;
    }
}