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
public class Country extends Scope implements Serializable {
    static final long serialVersionUID = softwareVersion;
    public String name;
    public transient boolean isLatLonCalculated=false;
    public ArrayList<State> states;

    public void getLatLonSizeFromChildren(){
        float minLat = Float.MAX_VALUE;
        float maxLat = -Float.MAX_VALUE;
        float minLon = Float.MAX_VALUE;
        float maxLon = -Float.MAX_VALUE;
        float latCumulative=0;
        float lonCumulative=0;
        for (int i = 0; i < states.size(); i++) {
            if(states.get(i).isLatLonCalculated==false){
                states.get(i).getLatLonSizeFromChildren();
                states.get(i).isLatLonCalculated=true;
            }
            float childLat = states.get(i).lat;
            float childLon = states.get(i).lon;
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
        lat = latCumulative / (float)states.size();
        lon = lonCumulative / (float)states.size();
        size = Math.max(maxLat - minLat, maxLon - minLon);
    }
    
    public boolean isNewStateUnique(String input) {
        if (states == null) {
            states = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).name.equals(input)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isNewStateUnique(byte input) {
        if (states == null) {
            states = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).id==input) {
                    return false;
                }
            }
        }
        return true;
    }

    public State findState(String input) {
        if (states == null) {
            states = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).name.equals(input)) {
                    return states.get(i);
                }
            }
        }
        return null;
    }
    
    public State findState(byte input) {
        if (states == null) {
            states = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).id==input) {
                    return states.get(i);
                }
            }
        }
        return null;
    }

    public State findAndInsertState(String input) {
        if (states == null) {
            states = new ArrayList();
            State temp = new State();
            temp.name = input;
            states.add(temp);
            return states.get(0);
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).name.equals(input)) {
                    return states.get(i);
                }
            }
        }
        State temp = new State();
        temp.name = input;
        states.add(temp);
        return states.get(states.size()-1);
    }
    
    public State findAndInsertState(byte input) {
        if (states == null) {
            states = new ArrayList();
            State temp = new State();
            temp.id = input;
            states.add(temp);
            return states.get(0);
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).id==input) {
                    return states.get(i);
                }
            }
        }
        State temp = new State();
        temp.id = input;
        states.add(temp);
        return states.get(states.size()-1);
    }

}
