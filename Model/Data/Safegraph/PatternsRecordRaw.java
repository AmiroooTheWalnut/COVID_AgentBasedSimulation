package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
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
public class PatternsRecordRaw implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public String placekey;//Unique and persistent ID tied to this POI.
    public String safegraph_place_id;//Unique and persistent ID tied to this POI (to be retired in favor of placekey).
    public String parent_placekey;//If place is a tenant / sub-store inside a larger place (e.g. mall, airport, stadium), this lists the placekey of the parent place, otherwise null.
    public String parent_safegraph_place_id;//If place is encompassed a larger place (e.g. mall, airport, stadium), this lists the safegraph_place_id of the parent place; otherwise null (to be retired in favor of parent_placekey).
    public String location_name;//The name of the place of interest.
    public String street_address;
    public String city;
    public String region;
    public String postal_code;//ZIP CODE for US, postal code for Canada
    public ArrayList<String> safegraph_brand_ids;//Unique and consistent ID that represents this specific brand.
    public ArrayList<String> brands;//If this POI is an instance of a larger brand that we have explicitly identified, this column will contain that brand name. This is an easy way to, for example, unambiguously select all Target stores in the USA. A POI may have multiple brands, as in a new car dealership that sells ford and lincoln cars. 	
    public String date_range_start;//Start time for measurement period in ISO 8601 format of YYYY-MM-DDTHH:mm:SS�hh:mm (local time with offset from GMT).
    public String date_range_end;//End time for measurement period in ISO 8601 format of YYYY-MM-DDTHH:mm:SS�hh:mm (local time with offset from GMT). The end time will be the last day of the month at 12 a.m. local time.
    public int raw_visit_counts;
    public int raw_visitor_counts;//Unique visits
    public int[] visits_by_day;//The number of visits to the POI each day (local time) over the covered time period. (30 days)
    public String poi_cbg;//The census block group the POI is located within. (CENSUS TRACT)
    public HashMap<String,Object> visitor_home_cbgs;//A mapping of census block groups to the number of visitors to the POI whose home is in that census block group.
    public HashMap<String,Object> visitor_daytime_cbgs;//A mapping of census block groups to the number of visitors to the POI whose primary daytime location between 9 am - 5 pm is in that census block group.
    public HashMap<String,Object> visitor_country_of_origin;
    public int distance_from_home;//Median distance from home travelled by visitors (of visitors whose home we have identified) in meters.
    public double median_dwell;//Median minimum dwell time in minutes.
    public HashMap<String,Object> bucketed_dwell_times;//Key is range of minutes and value is number of visits that were within that duration.
    public HashMap<String,Object> related_same_day_brand;//Other brands that the visitors to this POI visited on the same day as the visit to this POI where customer overlap differs by at least 5% from the SafeGraph national average. The mapping has the brand as the key. The value shown for each brand is a percentage representing the median of the following calculation for each day in the month
    public HashMap<String,Object> related_same_month_brand;//Other brands that the visitors to this POI visited in the same month as the visit to this POI where customer overlap differs by at least 5% from the SafeGraph national average. The value shown for each brand is a percentage representing
    public int[] popularity_by_hour;//A mapping of hour of day to the number of visits in each hour over the course of the date range in local time. First element in the array corresponds to the hour of midnight to 1 am. (24 hours)
    public HashMap<String,Object> popularity_by_day;//A mapping of day of week to the number of visits on each day (local time) in the course of the date range. (7 days with day of the week names)
    public HashMap<String,Object> device_type;//The number of visitors to the POI that are using android vs. ios.
}
