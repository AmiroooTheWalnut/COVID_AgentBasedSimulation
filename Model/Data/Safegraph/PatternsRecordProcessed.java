package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.POI;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class PatternsRecordProcessed implements Serializable, Comparable<PatternsRecordProcessed>{
    static final long serialVersionUID = softwareVersion;
    
    public transient boolean needToBeRemoved=false;
    
    public transient SafegraphPlace place;
    public String placeKey;
    public long poi_cbg;
    public transient CensusBlockGroup poi_cbg_censusBlock;
//    public SafegraphPlace parent_place;//NEEDS TO KNOW ALL PLACES BEFOREHAND!
//    public ArrayList<Brand> brands;
    public ZonedDateTime date_range_start;
    public ZonedDateTime date_range_end;
//    public byte timeGMTOffsetHour;
    public int raw_visit_counts;
    public int raw_visitor_counts;//Unique visits
    public int[] visits_by_day;//The number of visits to the POI each day (local time) over the covered time period. (30 days)
    public ArrayList<LongIntTuple> visitor_home_cbgs;
    public ArrayList<LongIntTuple> visitor_daytime_cbgs;
    public transient ArrayList<CensusBlockGroupIntegerTuple> visitor_home_cbgs_place;
    public transient ArrayList<CensusBlockGroupIntegerTuple> visitor_daytime_cbgs_place;
    public int distance_from_home;//Median distance from home travelled by visitors (of visitors whose home we have identified) in meters.
    public double median_dwell;//Median minimum dwell time in minutes.
    public ArrayList<DwellTime> bucketed_dwell_times;//Key is range of minutes and value is number of visits that were within that duration.
//    public ArrayList<HashMap<Brand,Integer>> related_same_day_brand;
//    public ArrayList<HashMap<Brand,Integer>> related_same_month_brand;
    public int[] popularity_by_hour;//A mapping of hour of day to the number of visits in each hour over the course of the date range in local time. First element in the array corresponds to the hour of midnight to 1 am. (24 hours)
    public HashMap<Byte,Integer> popularity_by_day;//A mapping of day of week to the number of visits on each day (local time) in the course of the date range. (7 days with day of the week names)
//    public String device_type;//The number of visitors to the POI that are using android vs. ios.
//    public OpenHours[] openHours;//7 days starting from monday//NEED TO BE IN "PLACES"
    
    public transient int sumVisitsByDayOfMonth=0;//COULD BE PREPROCESSED I.E. REMOVING TRANSIENT
    public transient int sumVisitsByDayOfWeek=0;//COULD BE PREPROCESSED I.E. REMOVING TRANSIENT
    public transient int sumVisitsByHourofDay=0;//COULD BE PREPROCESSED I.E. REMOVING TRANSIENT
    public transient int sumDwellTime=0;//COULD BE PREPROCESSED I.E. REMOVING TRANSIENT
    public transient POI pOI;
    

    @Override
    public int compareTo(PatternsRecordProcessed o) {
        return placeKey.compareTo(o.placeKey);
        
    }
}
