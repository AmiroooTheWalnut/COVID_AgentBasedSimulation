package COVID_AgentBasedSimulation.Model.Structure;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class City extends Scope implements Serializable, Comparable<City> {

    static final long serialVersionUID = softwareVersion;
    public String name;
    
    
    
    public transient boolean isLatLonCalculated = false;
    
    public Object[] getVDTessellationFromCBG(int tessellationIndex, CensusBlockGroup input, boolean noNull) {
        if (tessellations.get(tessellationIndex).cells != null) {
            double dist = Double.MAX_VALUE;
            Object[] closest = new Object[2];
            for (int i = 0; i < tessellations.get(tessellationIndex).cells.size(); i++) {
                for (int j = 0; j < tessellations.get(tessellationIndex).cells.get(i).cBGsIDsInvolved.size(); j++) {
                    if (tessellations.get(tessellationIndex).cells.get(i).cBGsIDsInvolved.get(j) == input.id) {
                        Object[] output = new Object[2];
                        output[0] = tessellations.get(tessellationIndex).cells.get(i);
                        output[1] = tessellations.get(tessellationIndex).cells.get(i).cBGsPercentageInvolved.get(j);
                        return output;
                    }
                }
                if (noNull == true) {
                    if (Math.pow(tessellations.get(tessellationIndex).cells.get(i).lat - input.lat, 2) + Math.pow(tessellations.get(tessellationIndex).cells.get(i).lon - input.lon, 2) < dist) {
                        dist = Math.pow(tessellations.get(tessellationIndex).cells.get(i).lat - input.lat, 2) + Math.pow(tessellations.get(tessellationIndex).cells.get(i).lon - input.lon, 2);
                        closest[0] = tessellations.get(tessellationIndex).cells.get(i);
                        closest[1] = tessellations.get(tessellationIndex).cells.get(i).cBGsPercentageInvolved.get((int) (Math.random() * tessellations.get(tessellationIndex).cells.get(i).cBGsPercentageInvolved.size() - 1));
                    }
                }
            }
            return closest;
        }
        return null;
    }

    public Object[] getVDFromCBG(CensusBlockGroup input, boolean noNull) {
        if (vDCells != null) {
            double dist = Double.MAX_VALUE;
            Object[] closest = new Object[2];
            for (int i = 0; i < vDCells.size(); i++) {
                for (int j = 0; j < vDCells.get(i).cBGsIDsInvolved.size(); j++) {
                    if (vDCells.get(i).cBGsIDsInvolved.get(j) == input.id) {
                        Object[] output = new Object[2];
                        output[0] = vDCells.get(i);
                        output[1] = vDCells.get(i).cBGsPercentageInvolved.get(j);
                        return output;
                    }
                }
                if (noNull == true) {
                    if (Math.pow(vDCells.get(i).lat - input.lat, 2) + Math.pow(vDCells.get(i).lon - input.lon, 2) < dist) {
                        dist = Math.pow(vDCells.get(i).lat - input.lat, 2) + Math.pow(vDCells.get(i).lon - input.lon, 2);
                        closest[0] = vDCells.get(i);
                        closest[1] = vDCells.get(i).cBGsPercentageInvolved.get((int) (Math.random() * vDCells.get(i).cBGsPercentageInvolved.size() - 1));
                    }
                }
            }
            return closest;
        }
        return null;
    }

    public Object[] getCBGVDFromCBG(CensusBlockGroup input, boolean noNull) {
        if (cBGVDCells != null) {
            double dist = Double.MAX_VALUE;
            Object[] closest = new Object[3];
            closest[2] = false;
            for (int i = 0; i < cBGVDCells.size(); i++) {
                for (int j = 0; j < cBGVDCells.get(i).cBGsIDsInvolved.size(); j++) {
                    if (cBGVDCells.get(i).cBGsIDsInvolved.get(j) == input.id) {
                        Object[] output = new Object[3];
                        output[0] = cBGVDCells.get(i);
                        output[1] = cBGVDCells.get(i).cBGsPercentageInvolved.get(j);
                        output[2] = true;
                        return output;
                    }
                }
                if (noNull == true) {
                    if (Math.pow(cBGVDCells.get(i).lat - input.lat, 2) + Math.pow(cBGVDCells.get(i).lon - input.lon, 2) < dist) {
                        dist = Math.pow(cBGVDCells.get(i).lat - input.lat, 2) + Math.pow(cBGVDCells.get(i).lon - input.lon, 2);
                        closest[0] = cBGVDCells.get(i);
                        closest[1] = cBGVDCells.get(i).cBGsPercentageInvolved.get((int) (Math.random() * cBGVDCells.get(i).cBGsPercentageInvolved.size() - 1));
                    }
                }
            }
            return closest;

        }
        return null;
    }

    public void getCBGVDFromCBGForAllCBGs() {
        for (int i = 0; i < censusTracts.size(); i++) {
            for (int j = 0; j < censusTracts.get(i).censusBlocks.size(); j++) {
                Object[] result = getCBGVDFromCBG(censusTracts.get(i).censusBlocks.get(j), true);
                if (((boolean) (result[2])) == true) {
                    censusTracts.get(i).censusBlocks.get(j).cBGVDFromCBGResultFound = result;
                } else {
                    censusTracts.get(i).censusBlocks.get(j).cBGVDFromCBGResultClosest = result;
                }
            }
        }
    }

    public void getLatLonSizeFromChildren() {
        float minLat = Float.MAX_VALUE;
        float maxLat = -Float.MAX_VALUE;
        float minLon = Float.MAX_VALUE;
        float maxLon = -Float.MAX_VALUE;
        float latCumulative = 0;
        float lonCumulative = 0;
        for (int i = 0; i < censusTracts.size(); i++) {
            if (censusTracts.get(i).isLatLonCalculated == false) {
                censusTracts.get(i).getLatLonSizeFromChildren();
                censusTracts.get(i).isLatLonCalculated = true;
            }
            float childLat = censusTracts.get(i).lat;
            float childLon = censusTracts.get(i).lon;
            if (childLat > maxLat) {
                maxLat = childLat;
            }
            if (childLat < minLat) {
                minLat = childLat;
            }
            if (childLon > maxLon) {
                maxLon = childLon;
            }
            if (childLon < minLon) {
                minLon = childLon;
            }
            latCumulative = latCumulative + childLat;
            lonCumulative = lonCumulative + childLon;
        }
        lat = latCumulative / (float) censusTracts.size();
        lon = lonCumulative / (float) censusTracts.size();
        size = Math.max(maxLat - minLat, maxLon - minLon);
    }

    public CensusTract findAndInsertCensusTract(int input) {
        if (censusTracts == null) {
            censusTracts = new ArrayList();
            CensusTract temp = new CensusTract();
            temp.id = input;
            censusTracts.add(temp);
            return censusTracts.get(0);
        } else {
            for (int i = 0; i < censusTracts.size(); i++) {
                if (censusTracts.get(i).id == input) {
                    return censusTracts.get(i);
                }
            }
        }
        CensusTract temp = new CensusTract();
        temp.id = input;
        censusTracts.add(temp);
        return censusTracts.get(censusTracts.size() - 1);
    }

    public CensusTract findAndInsertCensusTract(CensusTract input) {
        if (censusTracts == null) {
            censusTracts = new ArrayList();
            censusTracts.add(input);
            return censusTracts.get(0);
        } else {
            for (int i = 0; i < censusTracts.size(); i++) {
                if (censusTracts.get(i).id == input.id) {
                    return censusTracts.get(i);
                }
            }
        }
        censusTracts.add(input);
        return censusTracts.get(censusTracts.size() - 1);
    }

    public void calcPopulation() {
        population = 0;
        for (int i = 0; i < censusTracts.size(); i++) {
            population = population + censusTracts.get(i).population;
        }
    }

    @Override
    public int compareTo(City o) {
        return name.compareTo(o.name);
    }

    public CensusBlockGroup findCBG(long id) {
        for (int i = 0; i < censusTracts.size(); i++) {
            CensusBlockGroup cBG = censusTracts.get(i).findCensusBlock(id);
            if(cBG!=null){
                return cBG;
            }
        }
        return null;
    }

}
