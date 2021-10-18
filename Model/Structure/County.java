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
public class County extends Scope implements Serializable, Comparable<County>{

    static final long serialVersionUID = softwareVersion;
    public String name;
    public int id;
    public transient boolean isLatLonCalculated = false;
    public ArrayList<City> cities;
    public ArrayList<ZipCode> zipcodes;
    
    public Country country;
    public State state;

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
        if (cities != null) {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).isLatLonCalculated == false) {
                    cities.get(i).getLatLonSizeFromChildren();
                    cities.get(i).isLatLonCalculated = true;
                }
            }
        }

        lat = latCumulative / (float) censusTracts.size();
        lon = lonCumulative / (float) censusTracts.size();
        size = Math.max(maxLat - minLat, maxLon - minLon);
    }

    public boolean isNewCityUnique(String input) {
        if (cities == null) {
            cities = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(input)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isNewZipCodeUnique(int input) {
        if (zipcodes == null) {
            zipcodes = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < zipcodes.size(); i++) {
                if (zipcodes.get(i).code == input) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isNewCensusTractUnique(int input) {
        if (censusTracts == null) {
            censusTracts = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < censusTracts.size(); i++) {
                if (censusTracts.get(i).id == input) {
                    return false;
                }
            }
        }
        return true;
    }

    public City findCity(String input) {
        if (cities == null) {
            cities = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(input)) {
                    return cities.get(i);
                }
            }
        }
        return null;
    }

    public ZipCode findZipCode(int input) {
        if (zipcodes == null) {
            zipcodes = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < zipcodes.size(); i++) {
                if (zipcodes.get(i).code == input) {
                    return zipcodes.get(i);
                }
            }
        }
        return null;
    }

    public CensusTract findCensusTract(long input) {
        if (censusTracts == null) {
            censusTracts = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < censusTracts.size(); i++) {
                if (censusTracts.get(i).id == input) {
                    return censusTracts.get(i);
                }
            }
        }
        return null;
    }

    public City findAndInsertCity(String input) {
        if (cities == null) {
            cities = new ArrayList();
            City temp = new City();
            temp.name = input;
            cities.add(temp);
            return cities.get(0);
        } else {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(input)) {
                    return cities.get(i);
                }
            }
        }
        City temp = new City();
        temp.name = input;
        cities.add(temp);
        return cities.get(cities.size() - 1);
    }

    public ZipCode findAndInsertZipCode(int input) {
        if (zipcodes == null) {
            zipcodes = new ArrayList();
            ZipCode temp = new ZipCode();
            temp.code = input;
            zipcodes.add(temp);
            return zipcodes.get(0);
        } else {
            for (int i = 0; i < zipcodes.size(); i++) {
                if (zipcodes.get(i).code == input) {
                    return zipcodes.get(i);
                }
            }
        }
        ZipCode temp = new ZipCode();
        temp.code = input;
        zipcodes.add(temp);
        return zipcodes.get(zipcodes.size() - 1);
    }

    public CensusTract findAndInsertCensusTract(long input) {
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

    @Override
    public int compareTo(County o) {
        return name.compareTo(o.name);
    }

}
