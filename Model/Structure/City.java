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
@Getter @Setter
public class City extends Marker implements Serializable, Comparable<City> {
    static final long serialVersionUID = softwareVersion;
    public String name;
    public ArrayList<CensusTract> censusTracts;
    
    public transient ArrayList<VDCell> vDCells;
    public transient ArrayList<CBGVDCell> cBGVDCells;
    
    public transient boolean isLatLonCalculated=false;
    
    public void getLatLonSizeFromChildren(){
        float minLat = Float.MAX_VALUE;
        float maxLat = -Float.MAX_VALUE;
        float minLon = Float.MAX_VALUE;
        float maxLon = -Float.MAX_VALUE;
        float latCumulative=0;
        float lonCumulative=0;
        for (int i = 0; i < censusTracts.size(); i++) {
            if(censusTracts.get(i).isLatLonCalculated==false){
                censusTracts.get(i).getLatLonSizeFromChildren();
                censusTracts.get(i).isLatLonCalculated=true;
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
            latCumulative=latCumulative+childLat;
            lonCumulative=lonCumulative+childLon;
        }
        lat = latCumulative / (float)censusTracts.size();
        lon = lonCumulative / (float)censusTracts.size();
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
                if (censusTracts.get(i).id==input) {
                    return censusTracts.get(i);
                }
            }
        }
        CensusTract temp = new CensusTract();
        temp.id = input;
        censusTracts.add(temp);
        return censusTracts.get(censusTracts.size()-1);
    }
    
    public CensusTract findAndInsertCensusTract(CensusTract input) {
        if (censusTracts == null) {
            censusTracts = new ArrayList();
            censusTracts.add(input);
            return censusTracts.get(0);
        } else {
            for (int i = 0; i < censusTracts.size(); i++) {
                if (censusTracts.get(i).id==input.id) {
                    return censusTracts.get(i);
                }
            }
        }
        censusTracts.add(input);
        return censusTracts.get(censusTracts.size()-1);
    }
    
    public void calcPopulation(){
        population=0;
        for(int i=0;i<censusTracts.size();i++){
            population=population+censusTracts.get(i).population;
        }
    }

    @Override
    public int compareTo(City o) {
        return name.compareTo(o.name);
    }
    
}
