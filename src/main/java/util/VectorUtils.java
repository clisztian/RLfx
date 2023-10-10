package util;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class VectorUtils {
    public static List<Vec> removeZeroVectors(Iterable<Vec> vlist)
    {
        List<Vec> vstarlist = new ArrayList<Vec>();
        for (Vec v : vlist)
        {
            if (!v.isZero())
            {
                vstarlist.add(v);
            }
        }

        return vstarlist;
    }

    public static ImmutablePair<List<Vec>, List<Double>> normalize(Iterable<Vec> vlist)
    {
        List<Double> norms = new ArrayList<Double>();
        List<Vec> vstarlist = new ArrayList<Vec>();
        for (Vec v : vlist)
        {
            norms.add(v.norm(2));
            vstarlist.add(v.normalize());
        }

        return ImmutablePair.of(vstarlist, norms);
    }


}