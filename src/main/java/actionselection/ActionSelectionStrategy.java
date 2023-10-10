package actionselection;

import models.QModel;
import models.UtilityModel;
import util.IndexValue;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface ActionSelectionStrategy extends Serializable, Cloneable {
    IndexValue selectAction(int stateId, QModel model, Set<Integer> actionsAtState);
    IndexValue selectAction(int stateId, UtilityModel model, Set<Integer> actionsAtState);
    String getPrototype();
    Map<String, String> getAttributes();
}
