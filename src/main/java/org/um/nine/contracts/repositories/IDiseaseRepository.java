package org.um.nine.contracts.repositories;

import org.um.nine.domain.*;

import java.util.HashMap;
import java.util.List;

public interface IDiseaseRepository {
    void reset();
    List<InfectionRateMarker> getInfectionRate();
    List<OutbreakMarker> getOutbreakMarker();
    HashMap<String, Cure> getCures();
    List<Disease> getBlackCubes();
    List<Disease> getYellowCubes();
    List<Disease> getBlueCubes();
    List<Disease> getRedCubes();
}
