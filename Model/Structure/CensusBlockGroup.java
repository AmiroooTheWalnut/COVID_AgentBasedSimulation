package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Polygon;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class CensusBlockGroup extends Marker implements Serializable, Comparable<CensusBlockGroup> {
    static final long serialVersionUID = softwareVersion;
    public long id;
    
    public Country country;
    public State state;
    public County county;
    public CensusTract censusTract;
    
    public transient MyPolygon polygon;
    
    public transient ArrayList<ArrayList<SafegraphPlace>> vDsPlacesShops;
    public transient ArrayList<Double> proportionOfVDsShops;
    
    public transient ArrayList<ArrayList<SafegraphPlace>> vDsPlacesSchools;
    public transient ArrayList<Double> proportionOfVDsSchools;
    
    public transient ArrayList<ArrayList<SafegraphPlace>> vDsPlacesTemples;
    public transient ArrayList<Double> proportionOfVDsTemples;
    
    public transient ArrayList<Polygon> shape;
    
    public transient ArrayList<SafegraphPlace> places;
    
    public transient Object[] cBGVDFromCBGResultFound;
    public transient Object[] cBGVDFromCBGResultClosest;
    
    public boolean isNewPlace(String input) {
        if (places == null) {
            places = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < places.size(); i++) {
                if (places.get(i).placeKey.equals(input)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public SafegraphPlace findPlace(String input) {
        if (places == null) {
            places = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < places.size(); i++) {
                if (places.get(i).placeKey.equals(input)) {
                    return places.get(i);
                }
            }
        }
        return null;
    }
    
    public SafegraphPlace findAndInsertPlace(String input) {
        if (places == null) {
            places = new ArrayList();
            SafegraphPlace temp = new SafegraphPlace();
            temp.placeKey = input;
            places.add(temp);
            return places.get(0);
        } else {
            for (int i = 0; i < places.size(); i++) {
                if (places.get(i).placeKey.equals(input)) {
                    return places.get(i);
                }
            }
        }
        SafegraphPlace temp = new SafegraphPlace();
        temp.placeKey = input;
        places.add(temp);
        return places.get(places.size()-1);
    }

    @Override
    public int compareTo(CensusBlockGroup o) {
        if (this.id == o.id) {
            return 0;
        } else if (this.id > o.id) {
            return 1;
        } else {
            return -1;
        }
    }
    
    
}
