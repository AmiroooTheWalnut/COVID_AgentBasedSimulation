package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlock;
import java.io.Serializable;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class SafegraphPlace implements Serializable {
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
    public transient CensusBlock censusBlock;
//    public String location_name;//DROPPED
//    public String street_address;//DROPPED
//    public City city;
//    public State state;//STATE //DROPPED
//    public ZipCode zipcode;//DROPPED
//    public Country country;//DROPPED
    // PHONE NUMBER DROPPED
//    public OpenHours[] openHours;//7 days starting from monday //RELOCATED TO PATTERNS
    // category_tags,opened_on	closed_on,tracking_opened_since,tracking_closed_since DROPPED
    
}
