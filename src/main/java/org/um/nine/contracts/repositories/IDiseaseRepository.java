package org.um.nine.contracts.repositories;

import org.um.nine.domain.Cure;
import org.um.nine.domain.Disease;
import org.um.nine.domain.InfectionRateMarker;
import org.um.nine.domain.Marker;

import java.util.HashMap;
import java.util.List;

public interface IDiseaseRepository {
    void reset();
    List<InfectionRateMarker> getInfectionRate();
    List<Marker> getOutbreakMarker();
    HashMap<String, Cure> getCures();
    List<Disease> getBlackCubes();
    List<Disease> getYellowCubes();
    List<Disease> getBlueCubes();
    List<Disease> getRedCubes();
}
