package lspi.types;

import no.uib.cipr.matrix.Vector;

public interface BasisFunctions {

    Vector evaluate(Vector state, int action);

    int size();
}