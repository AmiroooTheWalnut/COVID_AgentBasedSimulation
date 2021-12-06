/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygons;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns;
import COVID_AgentBasedSimulation.Model.Dataset;
import COVID_AgentBasedSimulation.Model.DatasetTemplate;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.RecordTemplate;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import de.fhpotsdam.unfolding.geo.Location;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.objenesis.strategy.StdInstantiatorStrategy;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class AllGISData extends Dataset implements Serializable {

    static final long serialVersionUID = softwareVersion;
    public ArrayList<Country> countries;

    @Override
    public void setDatasetTemplate() {
        datasetTemplate = new DatasetTemplate();
        datasetTemplate.name = "AllGISData";

        DatasetTemplate Country = new DatasetTemplate();
        Country.name = "Country";

        DatasetTemplate State = new DatasetTemplate();
        State.name = "State";

        DatasetTemplate County = new DatasetTemplate();
        County.name = "County";

        DatasetTemplate CensusTract = new DatasetTemplate();
        CensusTract.name = "CensusTract";

        DatasetTemplate CensusBlockGroup = new DatasetTemplate();
        CensusBlockGroup.name = "CensusBlockGroup";

        for (int i = 0; i < CensusBlockGroup.class.getFields().length; i++) {
            RecordTemplate temp = new RecordTemplate();
            temp.name = CensusBlockGroup.class.getFields()[i].getName() + "(" + CensusBlockGroup.class.getFields()[i].getGenericType().getTypeName() + ")";
            CensusBlockGroup.recordTemplates.add(temp);
        }

        CensusTract.innerDatasetTemplates.add(CensusBlockGroup);
        County.innerDatasetTemplates.add(CensusTract);
        State.innerDatasetTemplates.add(County);
        Country.innerDatasetTemplates.add(State);
        datasetTemplate.innerDatasetTemplates.add(Country);
    }

    public CensusBlockGroup findCensusBlockGroup(long id) {
        if (id == 0) {
            return null;
        }
        byte stateID = (byte) getMidDigits(id, 11, 12);
        int countyID = (int) getMidDigits(id, 8, 12);
        long censusTractID = (long) getMidDigits(id, 2, 12);
//        byte censusBlockGroupID=(byte)getMidDigits(id,1,1);
        for (int i = 0; i < countries.size(); i++) {
            try{
            State state = countries.get(i).findState(stateID);
            County county = state.findCounty(countyID);
            CensusTract censusTract = county.findCensusTract(censusTractID);
            CensusBlockGroup censusBlockGroup = censusTract.findCensusBlock(id);
            return censusBlockGroup;
            }catch(Exception ex){
                System.out.println("^^^^^^^^^^");
            }
        }
        return null;
    }

    public long getMidDigits(long number, int start, int end) {
        long output = (long) (Math.floor(number / Math.pow(10, (start - 1))) - Math.floor(number / Math.pow(10, end)) * Math.pow(10, (end - start + 1)));
        return output;
    }

    public void processUSData(String geographyDirectory) {
        System.out.println("READING STATES");
        File statesFile = new File(geographyDirectory + "/US_States.json");
        try ( BufferedReader br = new BufferedReader(new FileReader(statesFile))) {
//            if (countries == null) {
//                countries = new ArrayList();
//            }
            countries = new ArrayList();
            Country us = findAndInsertCountry("USA");
            us.states = new ArrayList();

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
                    state.country = us;
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
        try ( BufferedReader br = new BufferedReader(new FileReader(countiesFile))) {
            String line;
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            while ((line = br.readLine()) != null) {
                if (line.contains("{ \"type\": \"Feature\", \"properties\":")) {
                    JSONObject root = new JSONObject(line);
                    JSONObject properties = root.getJSONObject("properties");
                    String stateIdString = properties.getString("STATEFP");
                    byte stateId = Byte.parseByte(stateIdString);
                    State state = countries.get(countries.size() - 1).findAndInsertState(stateId);

                    String countyIdString = properties.getString("COUNTYFP");
                    String countyIdRevised = stateIdString + countyIdString;
                    int countyId = Integer.parseInt(countyIdRevised);
                    County county = state.findAndInsertCounty(countyId);
                    if (!properties.isNull("NAMELSAD")) {
                        county.name = properties.getString("NAMELSAD");
                    } else {
                        county.name = "NULL";
                    }
                    county.country = countries.get(countries.size() - 1);
                    county.state = state;
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
        try ( BufferedReader br = new BufferedReader(new FileReader(censusBlockGroupFile))) {
            String line;
            int deguggingCounter = 0;
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            while ((line = br.readLine()) != null) {
                if (line.contains("{ \"type\": \"Feature\", \"properties\":")) {
                    JSONObject root = new JSONObject(line);
                    JSONObject properties = root.getJSONObject("properties");
                    String stateIdString = properties.getString("STATEFP");
                    byte stateId = Byte.parseByte(properties.getString("STATEFP"));
                    State state = countries.get(countries.size() - 1).findAndInsertState(stateId);

                    String countyIdString = properties.getString("COUNTYFP");
                    String countyIdRevised = stateIdString + countyIdString;
                    int countyId = Integer.parseInt(countyIdRevised);
                    County county = state.findAndInsertCounty(countyId);

                    String censusTractIdString = properties.getString("TRACTCE");
                    String censusTractIdRevised = stateIdString + countyIdString + censusTractIdString;
                    long censusTractId = Long.parseLong(censusTractIdRevised);
                    CensusTract censusTract = county.findAndInsertCensusTract(censusTractId);
                    censusTract.country = countries.get(countries.size() - 1);
                    censusTract.state = state;
                    censusTract.county = county;
                    long censusBlockLong = Long.parseLong(properties.getString("GEOID"));
                    CensusBlockGroup censusBlock = censusTract.findAndInsertCensusBlock(censusBlockLong);
                    censusBlock.country = countries.get(countries.size() - 1);
                    censusBlock.state = state;
                    censusBlock.county = county;
                    censusBlock.censusTract = censusTract;

                    censusBlock.lon = Float.parseFloat(properties.getString("INTPTLAT"));
                    censusBlock.lat = Float.parseFloat(properties.getString("INTPTLON"));
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

        System.out.println("READING 500 CITIES");
        File citiesFile = new File(geographyDirectory + "/500_Cities__Census_Tract-level_Data__GIS_Friendly_Format___2018_release.csv");
        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(citiesFile, StandardCharsets.UTF_8);
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            for (int i = 0; i < data.getRowCount(); i++) {
                CsvRow row = data.getRow(i);
                String cityName = row.getField("PlaceName");
                String censusTractString = row.getField("TractFIPS");
//                    System.out.println(i);
//                    if(i==11){
//                        System.out.println(i);
//                    }
                long censusTractID = Long.parseLong(censusTractString);
                int countyID = Integer.parseInt(censusTractString.substring(0, censusTractString.length() - 6));
                byte stateID = Byte.parseByte(censusTractString.substring(0, censusTractString.length() - 9));
                State state = countries.get(countries.size() - 1).findState(stateID);
                County county = state.findCounty(countyID);
                City city = county.findAndInsertCity(cityName);
                CensusTract censusTract = county.findCensusTract(censusTractID);
                city.findAndInsertCensusTract(censusTract);
//                    System.out.println(i);

                counter = counter + 1;
                if (counter > counterInterval) {
                    largerCounter = largerCounter + 1;
                    counter = 0;
                    System.out.println("Num rows read: " + largerCounter * counterInterval);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Patterns.class.getName()).log(Level.SEVERE, (String) null, ex);
        }

        System.out.println("READING census block population");
        File cBGPopulationFile = new File(geographyDirectory + "/USCensusBlockPopulation.csv");
        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(cBGPopulationFile, StandardCharsets.UTF_8);
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            for (int i = 0; i < data.getRowCount(); i++) {
                CsvRow row = data.getRow(i);
                String populationNumber = row.getField("Population");
                String cBGString = row.getField("census_block_group");

                int censusBlockGroupID = Integer.parseInt(cBGString.substring(cBGString.length() - 1));
                //int censusTractID = Integer.parseInt(censusTractString.substring(censusTractString.length() - 6));
                long censusTractID = Long.parseLong(cBGString.substring(0, cBGString.length() - 1));
                int countyID = Integer.parseInt(cBGString.substring(0, cBGString.length() - 7));
                byte stateID = Byte.parseByte(cBGString.substring(0, cBGString.length() - 10));
                State state = countries.get(countries.size() - 1).findState(stateID);
//                if(state==null){
//                    System.out.println("!!!");
//                }
                County county = state.findCounty(countyID);
//                if(county==null){
//                    System.out.println("!!!");
//                }
//                City city = county.findAndInsertCity(cityName);
                CensusTract censusTract = county.findCensusTract(censusTractID);
//                if(censusTract==null){
//                    System.out.println("!!!");
//                }
                CensusBlockGroup censusBlockGroup = censusTract.findCensusBlock(Long.parseLong(cBGString));
//                if(censusBlockGroup==null){
//                    System.out.println("!!!");
//                }
                censusBlockGroup.population = Integer.parseInt(populationNumber);
                censusTract.population = censusTract.population + censusBlockGroup.population;
                county.population = county.population + censusBlockGroup.population;
                state.population = state.population + censusBlockGroup.population;
                countries.get(countries.size() - 1).population = countries.get(countries.size() - 1).population + censusBlockGroup.population;

//                city.findAndInsertCensusTract(censusTract);
//                    System.out.println(i);
                counter = counter + 1;
                if (counter > counterInterval) {
                    largerCounter = largerCounter + 1;
                    counter = 0;
                    System.out.println("Num rows read: " + largerCounter * counterInterval);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Patterns.class.getName()).log(Level.SEVERE, (String) null, ex);
        }

        for (int i = 0; i < countries.size(); i++) {
            for (int j = 0; j < countries.get(i).states.size(); j++) {
                for (int k = 0; k < countries.get(i).states.get(j).counties.size(); k++) {
                    if (countries.get(i).states.get(j).counties.get(k).cities != null) {
                        for (int m = 0; m < countries.get(i).states.get(j).counties.get(k).cities.size(); m++) {

                            City targetCity = countries.get(i).states.get(j).counties.get(k).cities.get(m);

                            for (int n = 0; n < countries.get(i).states.get(j).counties.size(); n++) {
                                if (countries.get(i).states.get(j).counties.get(n).cities != null) {
                                    for (int d = 0; d < countries.get(i).states.get(j).counties.get(n).cities.size(); d++) {
                                        if (targetCity.name.equals(countries.get(i).states.get(j).counties.get(n).cities.get(d).name)) {
                                            if (targetCity.censusTracts.get(0).id != countries.get(i).states.get(j).counties.get(n).cities.get(d).censusTracts.get(0).id) {
                                                for (int f = 0; f < countries.get(i).states.get(j).counties.get(n).cities.get(d).censusTracts.size(); f++) {
                                                    targetCity.censusTracts.add(countries.get(i).states.get(j).counties.get(n).cities.get(d).censusTracts.get(f));
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

        for (int i = 0; i < countries.size(); i++) {
            countries.get(i).getLatLonSizeFromChildren();
            for (int j = 0; j < countries.get(i).states.size(); j++) {
                for (int k = 0; k < countries.get(i).states.get(j).counties.size(); k++) {
                    if (countries.get(i).states.get(j).counties.get(k).cities != null) {
                        for (int m = 0; m < countries.get(i).states.get(j).counties.get(k).cities.size(); m++) {
                            countries.get(i).states.get(j).counties.get(k).cities.get(m).calcPopulation();
                        }
                    }
                }
            }
        }
        AllGISData.saveAllGISDataKryo("./datasets/ProcessedGeoData", this);
    }

    public void readCensusBlockGroupPolygon(String geographyDirectory) {
        File censusBlockGroupFile = new File(geographyDirectory + "/US_CensusBlockGroup.json");
        try ( BufferedReader br = new BufferedReader(new FileReader(censusBlockGroupFile))) {
            String line;
            int deguggingCounter = 0;
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            int debugCounter = 0;
            while ((line = br.readLine()) != null) {
                if (line.contains("{ \"type\": \"Feature\", \"properties\":")) {
                    JSONObject root = new JSONObject(line);
                    JSONObject properties = root.getJSONObject("properties");
                    String stateIdString = properties.getString("STATEFP");
                    byte stateId = Byte.parseByte(properties.getString("STATEFP"));
                    State state = countries.get(countries.size() - 1).findAndInsertState(stateId);

                    String countyIdString = properties.getString("COUNTYFP");
                    String countyIdRevised = stateIdString + countyIdString;
                    int countyId = Integer.parseInt(countyIdRevised);
                    County county = state.findAndInsertCounty(countyId);

                    String censusTractIdString = properties.getString("TRACTCE");
                    String censusTractIdRevised = stateIdString + countyIdString + censusTractIdString;
                    long censusTractId = Long.parseLong(censusTractIdRevised);
                    CensusTract censusTract = county.findAndInsertCensusTract(censusTractId);
//                    censusTract.country = countries.get(countries.size() - 1);
//                    censusTract.state = state;
//                    censusTract.county = county;
                    long censusBlockLong = Long.parseLong(properties.getString("GEOID"));
                    CensusBlockGroup censusBlock = censusTract.findAndInsertCensusBlock(censusBlockLong);

                    if (censusBlock.shape == null) {
                        censusBlock.shape = new ArrayList();
                    }
//                    censusBlock.country = countries.get(countries.size() - 1);
//                    censusBlock.state = state;
//                    censusBlock.county = county;
//                    censusBlock.censusTract = censusTract;
                    JSONObject geometry = root.getJSONObject("geometry");
                    JSONArray coordsT = geometry.getJSONArray("coordinates");
                    String geomType = (String) (geometry.get("type"));
                    if (geomType.equals("Polygon")) {
                        JSONArray coords = coordsT.getJSONArray(0);
                        ArrayList<Coordinate> coordsArrayList = new ArrayList();
                        for (int i = 0; i < coords.length(); i++) {
                            coordsArrayList.add(new Coordinate(coords.getJSONArray(i).getDouble(0), coords.getJSONArray(i).getDouble(1)));
                        }
                        Coordinate coordsArray[] = new Coordinate[coordsArrayList.size()];
                        for (int m = 0; m < coordsArrayList.size(); m++) {
                            coordsArray[m] = coordsArrayList.get(m);
                        }
                        GeometryFactory geomFactory = new GeometryFactory();
                        try {
                            LinearRing linearRing = geomFactory.createLinearRing(coordsArray);
                            Polygon poly = geomFactory.createPolygon(linearRing);

                            censusBlock.shape.add(poly);
                        } catch (Exception ex) {

                        }
                    } else if (geomType.equals("MultiPolygon")) {
                        for (int m = 0; m < coordsT.length(); m++) {
                            JSONArray coords = coordsT.getJSONArray(m);
                            ArrayList<Coordinate> coordsArrayList = new ArrayList();

                            for (int i = 0; i < coords.getJSONArray(0).length(); i++) {
                                coordsArrayList.add(new Coordinate(coords.getJSONArray(0).getJSONArray(i).getDouble(0), coords.getJSONArray(0).getJSONArray(i).getDouble(1)));
                            }
                            Coordinate coordsArray[] = new Coordinate[coordsArrayList.size()];
                            for (int h = 0; h < coordsArrayList.size(); h++) {
                                coordsArray[h] = coordsArrayList.get(h);
                            }
                            GeometryFactory geomFactory = new GeometryFactory();
                            try {
                                LinearRing linearRing = geomFactory.createLinearRing(coordsArray);
                                Polygon poly = geomFactory.createPolygon(linearRing);

                                censusBlock.shape.add(poly);
                            } catch (Exception ex) {

                            }

                        }
                    }

//                    JSONArray coords = coordsT.getJSONArray(0);
//                    ArrayList<Coordinate> coordsArrayList = new ArrayList();
//                    for (int i = 0; i < coords.length(); i++) {
//                        if (geomType.equals("Polygon")) {
//                            coordsArrayList.add(new Coordinate(coords.getJSONArray(i).getDouble(0), coords.getJSONArray(i).getDouble(1)));
//                        } else if (geomType.equals("MultiPolygon")) {
//                            System.out.println("MultiPolygon!!!");
//                        }
////                        System.out.println(coords.get(i));
////                        System.out.println(debugCounter);
//
//                    }
                    debugCounter = debugCounter + 1;

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
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
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

    public boolean isInScope(Object scope, CensusBlockGroup cBG) {
        if (scope instanceof Country) {
            Country scopeCountry = ((Country) scope);
            for (int j = 0; j < scopeCountry.states.size(); j++) {
                for (int k = 0; k < scopeCountry.states.get(j).counties.size(); k++) {
                    for (int m = 0; m < scopeCountry.states.get(j).counties.get(k).censusTracts.size(); m++) {
                        for (int v = 0; v < scopeCountry.states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.size(); v++) {
                            if (cBG.id == scopeCountry.states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v).id) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        } else if (scope instanceof State) {
            State scopeState = ((State) scope);
            for (int k = 0; k < scopeState.counties.size(); k++) {
                for (int m = 0; m < scopeState.counties.get(k).censusTracts.size(); m++) {
                    for (int v = 0; v < scopeState.counties.get(k).censusTracts.get(m).censusBlocks.size(); v++) {
                        if (cBG.id == scopeState.counties.get(k).censusTracts.get(m).censusBlocks.get(v).id) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else if (scope instanceof County) {
            County scopeCounty = ((County) scope);
            for (int m = 0; m < scopeCounty.censusTracts.size(); m++) {
                for (int v = 0; v < scopeCounty.censusTracts.get(m).censusBlocks.size(); v++) {
                    if (cBG.id == scopeCounty.censusTracts.get(m).censusBlocks.get(v).id) {
                        return true;
                    }
                }
            }
            return false;
        } else if (scope instanceof CensusTract) {
            CensusTract scopeCensusTract = ((CensusTract) scope);
            for (int v = 0; v < scopeCensusTract.censusBlocks.size(); v++) {
                if (cBG.id == scopeCensusTract.censusBlocks.get(v).id) {
                    return true;
                }
            }
            return false;
        } else if (scope instanceof City) {
            City scopeCity = ((City) scope);
            for (int m = 0; m < scopeCity.censusTracts.size(); m++) {
                for (int v = 0; v < scopeCity.censusTracts.get(m).censusBlocks.size(); v++) {
                    if (cBG.id == scopeCity.censusTracts.get(m).censusBlocks.get(v).id) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            System.out.println("SCOPE UNKNOWN!");
            return false;
        }
    }

    public void loadScopeCBGPolygons(Scope scope) {
        System.out.println("READING CENSUS BLOCK GROUPS");
        String geographyDirectory = "./datasets";
        File censusBlockGroupFile = new File(geographyDirectory + "/US_CensusBlockGroup.json");
        try ( BufferedReader br = new BufferedReader(new FileReader(censusBlockGroupFile))) {
            String line;
            int deguggingCounter = 0;
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            while ((line = br.readLine()) != null) {
                if (line.contains("{ \"type\": \"Feature\", \"properties\":")) {
                    JSONObject root = new JSONObject(line);
                    JSONObject properties = root.getJSONObject("properties");

                    long censusBlockLong = Long.parseLong(properties.getString("GEOID"));
                    CensusBlockGroup foundCBG = scope.findCBG(censusBlockLong);
                    
                    
                    
                    if (foundCBG != null) {
                    JSONObject geometry = root.getJSONObject("geometry");
                    JSONArray coordsT = geometry.getJSONArray("coordinates");
                    String geomType = (String) (geometry.get("type"));
                    MyPolygons polys=new MyPolygons();
                    if (geomType.equals("Polygon")) {
                        JSONArray coords = coordsT.getJSONArray(0);
                        MyPolygon poly=new MyPolygon();
//                        ArrayList<Coordinate> coordsArrayList = new ArrayList();
                        for (int i = 0; i < coords.length(); i++) {
                            poly.points.add(new Location(coords.getJSONArray(i).getDouble(1),coords.getJSONArray(i).getDouble(0)));
//                            coordsArrayList.add(new Coordinate(coords.getJSONArray(i).getDouble(0), coords.getJSONArray(i).getDouble(1)));
                        }
                        polys.polygons.add(poly);
//                        Coordinate coordsArray[] = new Coordinate[coordsArrayList.size()];
//                        for (int m = 0; m < coordsArrayList.size(); m++) {
//                            coordsArray[m] = coordsArrayList.get(m);
//                        }
//                        GeometryFactory geomFactory = new GeometryFactory();
//                        try {
//                            LinearRing linearRing = geomFactory.createLinearRing(coordsArray);
//                            Polygon poly = geomFactory.createPolygon(linearRing);
//
//                            foundCBG.shape.add(poly);
//                        } catch (Exception ex) {
//                            
//                        }
                        scope.cBGPolygons.put(censusBlockLong, polys);
                    } else if (geomType.equals("MultiPolygon")) {
                        for (int m = 0; m < coordsT.length(); m++) {
                            JSONArray coords = coordsT.getJSONArray(m);
                            
                            MyPolygon poly=new MyPolygon();
                            
//                            ArrayList<Coordinate> coordsArrayList = new ArrayList();

                            for (int i = 0; i < coords.getJSONArray(0).length(); i++) {
                                poly.points.add(new Location(coords.getJSONArray(i).getDouble(0),coords.getJSONArray(i).getDouble(1)));
//                                coordsArrayList.add(new Coordinate(coords.getJSONArray(0).getJSONArray(i).getDouble(0), coords.getJSONArray(0).getJSONArray(i).getDouble(1)));
                            }
                            
                            polys.polygons.add(poly);
                            
                            scope.cBGPolygons.put(censusBlockLong, polys);
                            
//                            Coordinate coordsArray[] = new Coordinate[coordsArrayList.size()];
//                            for (int h = 0; h < coordsArrayList.size(); h++) {
//                                coordsArray[h] = coordsArrayList.get(h);
//                            }
//                            GeometryFactory geomFactory = new GeometryFactory();
//                            try {
//                                LinearRing linearRing = geomFactory.createLinearRing(coordsArray);
//                                Polygon poly = geomFactory.createPolygon(linearRing);
//
//                                foundCBG.shape.add(poly);
//                            } catch (Exception ex) {
//
//                            }

                        }
                    }
                    }
                    
                    
//                    if (foundCBG != null) {
//                        foundCBG.polygon = new MyPolygon();
//                        JSONArray coords = root.getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
//                        for (int i = 0; i < coords.length(); i++) {
//                            double lon = coords.getJSONArray(i).getDouble(0);
//                            double lat = coords.getJSONArray(i).getDouble(1);
//                            foundCBG.polygon.points.add(new Location(lat, lon));
//                        }
//                        scope.cBGPolygons.put(foundCBG.id,foundCBG.polygon);
//                    }
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
    }

}
