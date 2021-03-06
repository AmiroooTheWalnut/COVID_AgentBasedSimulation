package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace;
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
public class CensusBlockGroup extends Marker implements Serializable {
    static final long serialVersionUID = softwareVersion;
    public long id;
    
    public Country country;
    public State state;
    public County county;
    public CensusTract censusTract;
    
    public transient ArrayList<SafegraphPlace> places;
    
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
    
    
}
