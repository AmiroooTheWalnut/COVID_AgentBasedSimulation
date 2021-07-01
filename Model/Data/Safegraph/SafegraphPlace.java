package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import java.io.Serializable;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class SafegraphPlace implements Serializable, Comparable<SafegraphPlace> {
    static final long serialVersionUID = softwareVersion;
    
//    public String id;//DROPPED IT IS GOING TO BE DEPRECIATED
    public transient SafegraphPlace parent;
    public String placeKey;
    public float lat;
    public float lon;
    public ArrayList<Brand> brands;
    public Category category;
    // SUB CATEGORY IS DROPPED
    public int naics_code;
    public transient CensusBlockGroup censusBlock;
//    public String location_name;//DROPPED
//    public String street_address;//DROPPED
//    public City city;
//    public State state;//STATE //DROPPED
//    public ZipCode zipcode;//DROPPED
//    public Country country;//DROPPED
    // PHONE NUMBER DROPPED
    public OpenHours[] openHours;//7 days starting from monday
    // category_tags,opened_on	closed_on,tracking_opened_since,tracking_closed_since DROPPED
    
    public float landArea=-1;
    public int buldingLevels=-1;

    @Override
    public int compareTo(SafegraphPlace o) {
        return placeKey.compareTo(o.placeKey);
    }
    
}
