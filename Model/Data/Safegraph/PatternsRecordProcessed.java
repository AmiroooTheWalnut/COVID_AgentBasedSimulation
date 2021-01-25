package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlock;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class PatternsRecordProcessed implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public transient SafegraphPlace place;
    public String placeKey;
    public long poi_cbg;
//    public SafegraphPlace parent_place;//NEEDS TO KNOW ALL PLACES BEFOREHAND!
//    public ArrayList<Brand> brands;
    public LocalDateTime date_range_start;
    public LocalDateTime date_range_end;
//    public byte timeGMTOffsetHour;
    public int raw_visit_counts;
    public int raw_visitor_counts;//Unique visits
    public int[] visits_by_day;//The number of visits to the POI each day (local time) over the covered time period. (30 days)
    public HashMap<Long,Integer> visitor_home_cbgs;
    public HashMap<Long,Integer> visitor_daytime_cbgs;
    public int distance_from_home;//Median distance from home travelled by visitors (of visitors whose home we have identified) in meters.
    public double median_dwell;//Median minimum dwell time in minutes.
    public ArrayList<DwellTime> bucketed_dwell_times;//Key is range of minutes and value is number of visits that were within that duration.
//    public ArrayList<HashMap<Brand,Integer>> related_same_day_brand;
//    public ArrayList<HashMap<Brand,Integer>> related_same_month_brand;
    public int[] popularity_by_hour;//A mapping of hour of day to the number of visits in each hour over the course of the date range in local time. First element in the array corresponds to the hour of midnight to 1 am. (24 hours)
    public HashMap<Byte,Integer> popularity_by_day;//A mapping of day of week to the number of visits on each day (local time) in the course of the date range. (7 days with day of the week names)
//    public String device_type;//The number of visitors to the POI that are using android vs. ios.
    public OpenHours[] openHours;//7 days starting from monday
}
