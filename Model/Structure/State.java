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
public class State extends Scope implements Serializable, Comparable<State>{
    static final long serialVersionUID = softwareVersion;
    public String name;
    public byte id;
    public transient boolean isLatLonCalculated=false;
    public ArrayList<County> counties;
    
    public Country country;
    
    public void getLatLonSizeFromChildren(){
        float minLat = Float.MAX_VALUE;
        float maxLat = -Float.MAX_VALUE;
        float minLon = Float.MAX_VALUE;
        float maxLon = -Float.MAX_VALUE;
        float latCumulative=0;
        float lonCumulative=0;
        for (int i = 0; i < counties.size(); i++) {
            if(counties.get(i).isLatLonCalculated==false){
                counties.get(i).getLatLonSizeFromChildren();
                counties.get(i).isLatLonCalculated=true;
            }
            float childLat = counties.get(i).lat;
            float childLon = counties.get(i).lon;
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
        lat = latCumulative / (float)counties.size();
        lon = lonCumulative / (float)counties.size();
        size = Math.max(maxLat - minLat, maxLon - minLon);
    }
    
    public boolean isNewCountyUnique(String input) {
        if (counties == null) {
            counties = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).name.equals(input)) {
                    return false;
                }
            }
        }
        return true;
    }

    public County findCounty(String input) {
        if (counties == null) {
            counties = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).name.equals(input)) {
                    return counties.get(i);
                }
            }
        }
        return null;
    }

    public County findAndInsertCounty(String input) {
        if (counties == null) {
            counties = new ArrayList();
            County temp = new County();
            temp.name = input;
            counties.add(temp);
            return counties.get(0);
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (!counties.get(i).name.equals("NULL")) {
                    if (counties.get(i).name.equals(input)) {
                        return counties.get(i);
                    }
                } else {
                    return null;
                }
            }
        }
        County temp = new County();
        temp.name = input;
        counties.add(temp);
        return counties.get(counties.size() - 1);
    }

    public boolean isNewCountyUnique(int input) {
        if (counties == null) {
            counties = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).id == input) {
                    return false;
                }
            }
        }
        return true;
    }

    public County findCounty(int input) {
        if (counties == null) {
            counties = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).id == input) {
                    return counties.get(i);
                }
            }
        }
        return null;
    }

    public County findAndInsertCounty(int input) {
        if (counties == null) {
            counties = new ArrayList();
            County temp = new County();
            temp.id = input;
            counties.add(temp);
            return counties.get(0);
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).id == input) {
                    return counties.get(i);
                }
            }
        }
        County temp = new County();
        temp.id = input;
        counties.add(temp);
        return counties.get(counties.size() - 1);
    }

    @Override
    public int compareTo(State o) {
        return name.compareTo(o.name);
    }

}
