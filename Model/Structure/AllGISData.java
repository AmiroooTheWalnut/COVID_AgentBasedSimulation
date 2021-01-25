/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
public class AllGISData {

    public ArrayList<Country> countries;

    public void processUSData(String geographyFile) {
        File patternFile = new File(geographyFile);
        try (BufferedReader br = new BufferedReader(new FileReader(patternFile))) {
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
                    CensusBlock censusBlock = censusTract.findAndInsertCensusBlock(censusBlockLong);
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
            AllGISData.saveAllGISDataKryo("./datasets/ProcessedGeoData", this);
        } catch (IOException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public float[] getSizeMiddleLatLon(JSONArray input) {
        float minLat = Float.MAX_VALUE;
        float maxLat = Float.MIN_VALUE;
        float minLon = Float.MAX_VALUE;
        float maxLon = Float.MIN_VALUE;
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
        }
        float[] results = new float[3];
        results[0] = (maxLat + minLat) / 2f;
        results[1] = (maxLon + minLon) / 2f;
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
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CensusBlock.class);
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
