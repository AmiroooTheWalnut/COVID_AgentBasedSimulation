/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.Model.Dataset;
import COVID_AgentBasedSimulation.Model.DatasetTemplate;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.RecordTemplate;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 *
 * @author user
 */
public class AllGISData extends Dataset implements Serializable {

    static final long serialVersionUID = softwareVersion;
    public ArrayList<Country> countries;

    
    @Override
    public void setDatasetTemplate(){
        datasetTemplate=new DatasetTemplate();
        datasetTemplate.name="AllGISData";
        
        DatasetTemplate Country=new DatasetTemplate();
        Country.name="Country";
        
        DatasetTemplate State=new DatasetTemplate();
        State.name="State";
        
        DatasetTemplate County=new DatasetTemplate();
        County.name="County";
        
        DatasetTemplate CensusTract=new DatasetTemplate();
        CensusTract.name="CensusTract";
        
        DatasetTemplate CensusBlockGroup=new DatasetTemplate();
        CensusBlockGroup.name="CensusBlockGroup";
        
        for(int i=0;i<CensusBlockGroup.class.getFields().length;i++){
            RecordTemplate temp=new RecordTemplate();
            temp.name=CensusBlockGroup.class.getFields()[i].getName();
            CensusBlockGroup.recordTemplates.add(temp);
        }
        
        CensusTract.innerDatasetTemplates.add(CensusBlockGroup);
        County.innerDatasetTemplates.add(CensusTract);
        State.innerDatasetTemplates.add(County);
        Country.innerDatasetTemplates.add(State);
        datasetTemplate.innerDatasetTemplates.add(Country);
    }
    
    public CensusBlockGroup findCensusBlockGroup(long id) {
        if(id==0){
            return null;
        }
        byte stateID=(byte)getMidDigits(id,11,12);
        int countyID=(int)getMidDigits(id,8,10);
        int censusTractID=(int)getMidDigits(id,2,7);
//        byte censusBlockGroupID=(byte)getMidDigits(id,1,1);
        for (int i = 0; i < countries.size(); i++) {
            State state=countries.get(i).findState(stateID);
            County county=state.findCounty(countyID);
            CensusTract censusTract=county.findCensusTract(censusTractID);
            CensusBlockGroup censusBlockGroup=censusTract.findCensusBlock(id);
            return censusBlockGroup;
//            for (int j = 0; j < countries.get(i).states.size(); j++) {
//                for (int k = 0; k < countries.get(i).states.get(j).counties.size(); k++) {
//                    for (int l = 0; l < countries.get(i).states.get(j).counties.get(k).censusTracts.size(); l++) {
//                        for (int m = 0; m < countries.get(i).states.get(j).counties.get(k).censusTracts.get(l).censusBlocks.size(); m++) {
//                            if (countries.get(i).states.get(j).counties.get(k).censusTracts.get(l).censusBlocks.get(m).id == id) {
//                                return countries.get(i).states.get(j).counties.get(k).censusTracts.get(l).censusBlocks.get(m);
//                            }
//                        }
//                    }
//                }
//            }
        }
        return null;
    }

    public long getMidDigits(long number, int start, int end) {
        long output=(long)(Math.floor(number/Math.pow(10,(start-1)))-Math.floor(number/Math.pow(10,end))*Math.pow(10,(end-start+1)));
        return output;
    }

    public void processUSData(String geographyDirectory) {
        System.out.println("READING STATES");
        File statesFile = new File(geographyDirectory + "/US_States.json");
        try (BufferedReader br = new BufferedReader(new FileReader(statesFile))) {
            Country us = new Country();
            us.name = "USA";
            us.states = new ArrayList();
            if (countries == null) {
                countries = new ArrayList();
            }
            countries.add(us);
            String line;
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            while ((line = br.readLine()) != null) {
                if (line.contains("{ \"type\": \"Feature\", \"properties\":")) {
                    JSONObject root = new JSONObject(line);
                    JSONObject properties = root.getJSONObject("properties");
                    byte stateId = Byte.parseByte(properties.getString("GEOID"));
                    State state = countries.get(countries.size() - 1).findAndInsertState(stateId);
                    if (!properties.isNull("STUSPS")) {
                        state.name = properties.getString("STUSPS");
                    } else {
                        state.name = "NULL";
                    }
                }
                counter = counter + 1;
                if (counter > counterInterval) {
                    largerCounter = largerCounter + 1;
                    counter = 0;
                    System.out.println("Num rows read: " + largerCounter * counterInterval);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("READING COUNTIES");
        File countiesFile = new File(geographyDirectory + "/US_Counties.json");
        try (BufferedReader br = new BufferedReader(new FileReader(countiesFile))) {
            String line;
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            while ((line = br.readLine()) != null) {
                if (line.contains("{ \"type\": \"Feature\", \"properties\":")) {
                    JSONObject root = new JSONObject(line);
                    JSONObject properties = root.getJSONObject("properties");
                    byte stateId = Byte.parseByte(properties.getString("STATEFP"));
                    State state = countries.get(countries.size() - 1).findAndInsertState(stateId);

                    int countyId = Integer.parseInt(properties.getString("COUNTYFP"));
                    County county = state.findAndInsertCounty(countyId);
                    if (!properties.isNull("NAMELSAD")) {
                        county.name = properties.getString("NAMELSAD");
                    } else {
                        county.name = "NULL";
                    }
                }
                counter = counter + 1;
                if (counter > counterInterval) {
                    largerCounter = largerCounter + 1;
                    counter = 0;
                    System.out.println("Num rows read: " + largerCounter * counterInterval);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("READING CENSUS BLOCK GROUPS");
        File censusBlockGroupFile = new File(geographyDirectory + "/US_CensusBlockGroup.json");
        try (BufferedReader br = new BufferedReader(new FileReader(censusBlockGroupFile))) {
            String line;
            int deguggingCounter = 0;
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            while ((line = br.readLine()) != null) {
                if (line.contains("{ \"type\": \"Feature\", \"properties\":")) {
                    JSONObject root = new JSONObject(line);
                    JSONObject properties = root.getJSONObject("properties");
                    byte stateId = Byte.parseByte(properties.getString("STATEFP"));
                    State state = countries.get(countries.size() - 1).findAndInsertState(stateId);

                    int countyId = Integer.parseInt(properties.getString("COUNTYFP"));
                    County county = state.findAndInsertCounty(countyId);

                    int censusTractInt = properties.getInt("TRACTCE");
                    CensusTract censusTract = county.findAndInsertCensusTract(censusTractInt);
                    long censusBlockLong = Long.parseLong(properties.getString("GEOID"));
                    CensusBlockGroup censusBlock = censusTract.findAndInsertCensusBlock(censusBlockLong);
                    censusBlock.country = countries.get(countries.size() - 1);
                    censusBlock.state = state;
                    censusBlock.county = county;
                    censusBlock.censusTract = censusTract;

                    censusBlock.lon = Float.valueOf(properties.getString("INTPTLAT"));
                    censusBlock.lat = Float.valueOf(properties.getString("INTPTLON"));
                    censusBlock.size = 0.008f;
                }
                deguggingCounter = deguggingCounter + 1;
                counter = counter + 1;
                if (counter > counterInterval) {
                    largerCounter = largerCounter + 1;
                    counter = 0;
                    System.out.println("Num rows read: " + largerCounter * counterInterval);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < countries.size(); i++) {
            countries.get(i).getLatLonSizeFromChildren();
        }
        AllGISData.saveAllGISDataKryo("./datasets/ProcessedGeoData", this);
    }

    public void processUSData_Old(String geographyFile) {
        File geoFile = new File(geographyFile);
        try (BufferedReader br = new BufferedReader(new FileReader(geoFile))) {
            Country us = new Country();
            us.name = "USA";
            us.states = new ArrayList();
            if (countries == null) {
                countries = new ArrayList();
            }
            countries.add(us);
            String line;
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            while ((line = br.readLine()) != null) {
                if (line.contains("{ \"type\": \"Feature\", \"properties\":")) {
                    JSONObject root = new JSONObject(line);
                    JSONObject properties = root.getJSONObject("properties");
                    byte stateId = Byte.parseByte(properties.getString("StateFIPS"));
                    State state = countries.get(countries.size() - 1).findAndInsertState(stateId);
                    if (!properties.isNull("State")) {
                        state.name = properties.getString("State");
                    } else {
                        state.name = "NULL";
                    }
                    int countyId = Integer.parseInt(properties.getString("CountyFIPS"));
                    County county = state.findAndInsertCounty(countyId);
                    if (!properties.isNull("County")) {
                        county.name = properties.getString("County");
                    } else {
                        county.name = "NULL";
                    }
                    int censusTractInt = properties.getInt("TractCode");
                    CensusTract censusTract = county.findAndInsertCensusTract(censusTractInt);
                    long censusBlockLong = Long.parseLong(properties.getString("CensusBlockGroup"));
                    CensusBlockGroup censusBlock = censusTract.findAndInsertCensusBlock(censusBlockLong);
                    censusBlock.country = countries.get(countries.size() - 1);
                    censusBlock.state = state;
                    censusBlock.county = county;
                    censusBlock.censusTract = censusTract;
                    float[] results = getSizeMiddleLatLon(root.getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0).getJSONArray(0));
                    censusBlock.lat = results[0];
                    censusBlock.lon = results[1];
                    censusBlock.size = results[2];
                }
                counter = counter + 1;
                if (counter > counterInterval) {
                    largerCounter = largerCounter + 1;
                    counter = 0;
                    System.out.println("Num rows read: " + largerCounter * counterInterval);
                }
            }
            for (int i = 0; i < countries.size(); i++) {
                countries.get(i).getLatLonSizeFromChildren();
            }
            AllGISData.saveAllGISDataKryo("./datasets/ProcessedGeoData", this);
        } catch (IOException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public float[] getSizeMiddleLatLon(JSONArray input) {
        float minLat = Float.MAX_VALUE;
        float maxLat = -Float.MAX_VALUE;
        float minLon = Float.MAX_VALUE;
        float maxLon = -Float.MAX_VALUE;
        float latCumulative = 0;
        float lonCumulative = 0;
        for (int i = 0; i < input.length(); i++) {
            float lat = input.getJSONArray(i).getNumber(0).floatValue();
            float lon = input.getJSONArray(i).getNumber(1).floatValue();
            if (lat > maxLat) {
                maxLat = lat;
            }
            if (lat < minLat) {
                minLat = lat;
            }
            if (lon > maxLon) {
                maxLon = lon;
            }
            if (lon < minLon) {
                minLon = lon;
            }
            latCumulative = latCumulative + lat;
            lonCumulative = lonCumulative + lon;
        }
        float[] results = new float[3];
        results[0] = latCumulative / (float) input.length();
        results[1] = lonCumulative / (float) input.length();
        results[2] = Math.max(maxLat - minLat, maxLon - minLon);
        return results;
    }

    public boolean isNewCountryUnique(String input) {
        if (countries == null) {
            countries = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < countries.size(); i++) {
                if (countries.get(i).name.equals(input)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Country findCountry(String input) {
        if (countries == null) {
            countries = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < countries.size(); i++) {
                if (countries.get(i).name.equals(input)) {
                    return countries.get(i);
                }
            }
        }
        return null;
    }

    public Country findAndInsertCountry(String input) {
        if (countries == null) {
            countries = new ArrayList();
            Country temp = new Country();
            temp.name = input;
            countries.add(temp);
            return countries.get(0);
        } else {
            for (int i = 0; i < countries.size(); i++) {
                if (countries.get(i).name.equals(input)) {
                    return countries.get(i);
                }
            }
        }
        Country temp = new Country();
        temp.name = input;
        countries.add(temp);
        return countries.get(countries.size() - 1);
    }

    public static void saveAllGISDataKryo(String passed_file_path, AllGISData allGISData) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.AllGISData.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CensusTract.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.City.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.Country.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.County.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.State.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.ZipCode.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.register(java.lang.Long.class);
        kryo.register(java.lang.Float.class);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(java.time.ZonedDateTime.class);
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path + ".bin"));
            kryo.writeObject(output, allGISData);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
