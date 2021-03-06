package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import COVID_AgentBasedSimulation.Model.Dataset;
import COVID_AgentBasedSimulation.Model.DatasetTemplate;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.RecordTemplate;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Country;
import COVID_AgentBasedSimulation.Model.Structure.County;
import COVID_AgentBasedSimulation.Model.Structure.State;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objenesis.strategy.StdInstantiatorStrategy;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class Safegraph extends Dataset implements Serializable {

    static final long serialVersionUID = softwareVersion;

    public AllPatterns allPatterns;
    public AllSafegraphPlaces allSafegraphPlaces;

    @Override
    public void requestDataset(AllGISData allGISData, String project, String year, String month, boolean isParallel, int numCPU) {
        clearPatternsPlaces();
        loadPatternsPlacesSet(year + "_" + month, allGISData, project, isParallel, numCPU);
        startingDate = findEarliestPatternTime();
        endingDate = findLatestPatternTime();
    }

    @Override
    public void setDatasetTemplate() {
        datasetTemplate = new DatasetTemplate();
        datasetTemplate.name = "Safegraph";

        DatasetTemplate allPatterns = new DatasetTemplate();
        allPatterns.name = "allPatterns";
        DatasetTemplate allSafegraphPlaces = new DatasetTemplate();
        allSafegraphPlaces.name = "allSafegraphPlaces";

        DatasetTemplate monthlyPatternsList = new DatasetTemplate();
        monthlyPatternsList.name = "monthlyPatternsList";

        allPatterns.innerDatasetTemplates.add(monthlyPatternsList);

        DatasetTemplate recordsPattern = new DatasetTemplate();
        recordsPattern.name = "patternRecords";

        for (int i = 0; i < PatternsRecordProcessed.class.getFields().length; i++) {
            RecordTemplate temp = new RecordTemplate();
            temp.name = PatternsRecordProcessed.class.getFields()[i].getName() + "(" + PatternsRecordProcessed.class.getFields()[i].getGenericType().getTypeName() + ")";
            recordsPattern.recordTemplates.add(temp);
        }
        monthlyPatternsList.innerDatasetTemplates.add(recordsPattern);

        DatasetTemplate monthlySafegraphPlacesList = new DatasetTemplate();
        monthlySafegraphPlacesList.name = "monthlySafegraphPlacesList";
        allSafegraphPlaces.innerDatasetTemplates.add(monthlySafegraphPlacesList);

        DatasetTemplate recordsPlace = new DatasetTemplate();
        recordsPlace.name = "placesRecords";

        for (int i = 0; i < SafegraphPlace.class.getFields().length; i++) {
            RecordTemplate temp = new RecordTemplate();
            temp.name = SafegraphPlace.class.getFields()[i].getName() + "(" + SafegraphPlace.class.getFields()[i].getGenericType().getTypeName() + ")";
            recordsPlace.recordTemplates.add(temp);
        }
        monthlySafegraphPlacesList.innerDatasetTemplates.add(recordsPlace);

        datasetTemplate.innerDatasetTemplates.add(allPatterns);
        datasetTemplate.innerDatasetTemplates.add(allSafegraphPlaces);
    }

    public ZonedDateTime findEarliestPatternTime() {
        ZonedDateTime earliestTime = ZonedDateTime.now();
        for (int i = 0; i < allPatterns.monthlyPatternsList.size(); i++) {
            for (int j = 0; j < allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                boolean isBefore = allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).date_range_start.isBefore(earliestTime);
                if (isBefore == true) {
                    earliestTime = allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).date_range_start;
                }
            }
        }
        return earliestTime;
    }

    public ZonedDateTime findLatestPatternTime() {
        ZonedDateTime latestTime = ZonedDateTime.of(LocalDateTime.MIN, ZoneId.ofOffset("UTC", ZoneOffset.UTC));
        for (int i = 0; i < allPatterns.monthlyPatternsList.size(); i++) {
            for (int j = 0; j < allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                boolean isBefore = allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).date_range_start.isBefore(latestTime);
                if (isBefore == true) {
                    latestTime = allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).date_range_start;
                }
            }
        }
        return latestTime;
    }

    public ArrayList<String> checkMonthAvailability() {
        ArrayList<String> availableMonths = new ArrayList();
        String[] patternsDirectories = AllPatterns.detectAllPatterns("./datasets/Safegraph/FullData");
        String[] placesDirectories = AllSafegraphPlaces.detectAllPlaces("./datasets/Safegraph/FullData");
        for (int i = 0; i < patternsDirectories.length; i++) {
            for (int j = 0; j < placesDirectories.length; j++) {
                if (patternsDirectories[i].substring(9).equals(placesDirectories[j].substring(9))) {
                    File processedPatterns = new File("./datasets/Safegraph/FullData/" + patternsDirectories[i] + "/processedData.bin");
                    File processedPlaces = new File("./datasets/Safegraph/FullData/" + placesDirectories[i] + "/processedData.bin");
                    if (processedPatterns.exists() && processedPlaces.exists()) {
                        availableMonths.add(patternsDirectories[i]);
                    }
                }
            }
        }
        return availableMonths;
    }

    public static void savePatternsSerializable(String passed_file_path, Patterns allData) {
        FileOutputStream f_out;
        try {
            f_out = new FileOutputStream(passed_file_path + ".data");
            ObjectOutputStream obj_out;
            try {
                obj_out = new ObjectOutputStream(f_out);
                obj_out.writeObject(allData);
                obj_out.close();
            } catch (IOException ex) {
                Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void savePatternsKryo(String passed_file_path, Patterns patterns) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed.class);
        kryo.register(java.time.LocalDateTime.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.LongIntTuple.class);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(java.time.ZonedDateTime.class);
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path + ".bin"));
            kryo.writeObject(output, patterns);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveSafegraphPlacesKryo(String passed_file_path, SafegraphPlaces safegraphPlaces) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlaces.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Brands.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Brand.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Categories.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Category.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace.class);
        kryo.register(java.lang.String.class);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(java.time.ZonedDateTime.class);
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path + ".bin"));
            kryo.writeObject(output, safegraphPlaces);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loadPatternsSerializable(String passed_file_path, Patterns allData) {

        FileOutputStream f_out;
        try {
            f_out = new FileOutputStream(passed_file_path + ".data");
            ObjectOutputStream obj_out;
            try {
                obj_out = new ObjectOutputStream(f_out);
                obj_out.writeObject(allData);
                obj_out.close();
            } catch (IOException ex) {
                Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Patterns loadPatternsKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed.class);
        kryo.register(java.time.LocalDateTime.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.LongIntTuple.class);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(java.time.ZonedDateTime.class);
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Input input;
        try {
            File temp = new File(passed_file_path);
            if (temp.exists() == true) {
                input = new Input(new FileInputStream(passed_file_path));
                Patterns patterns = kryo.readObject(input, Patterns.class);
                input.close();

                return patterns;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static SafegraphPlaces loadSafegraphPlacesKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlaces.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Brands.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Brand.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Categories.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Category.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace.class);
        kryo.register(java.lang.String.class);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(java.time.ZonedDateTime.class);
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Input input;
        try {
            File temp = new File(passed_file_path);
            if (temp.exists() == true) {
                input = new Input(new FileInputStream(passed_file_path));
                SafegraphPlaces safegraphPlaces = kryo.readObject(input, SafegraphPlaces.class);
                input.close();

                return safegraphPlaces;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void saveAllPatternsSerializable(String passed_file_path) {

        FileOutputStream f_out;
        try {
            f_out = new FileOutputStream(passed_file_path + ".data");
            ObjectOutputStream obj_out;
            try {
                obj_out = new ObjectOutputStream(f_out);
                obj_out.writeObject(allPatterns);
                obj_out.close();
            } catch (IOException ex) {
                Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveAllPatternsKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path + ".bin"));
            kryo.writeObject(output, allPatterns);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clearPatternsPlaces() {
        if (allPatterns != null) {
            allPatterns.monthlyPatternsList.clear();
        } else {
            allPatterns = new AllPatterns();
        }
        if (allSafegraphPlaces != null) {
            allSafegraphPlaces.monthlySafegraphPlacesList.clear();
        } else {
            allSafegraphPlaces = new AllSafegraphPlaces();
        }
    }

    public void loadPatternsPlacesSet(String date, AllGISData allGISData, String project, boolean isParallel, int numCPU) {
        Patterns patterns = loadPatternsKryo("./datasets/Safegraph/"+project+"/patterns_" + date + "/processedData.bin");
        SafegraphPlaces safegraphPlaces = loadSafegraphPlacesKryo("./datasets/Safegraph/"+project+"/core_poi_" + date + "/processedData.bin");

        System.out.println("Data loaded");
        System.out.println("Connecting patterns and places ...");
        connectPatternsAndPlaces(patterns, safegraphPlaces, allGISData, isParallel, numCPU);
        System.out.println("Connection done");
        if (patterns != null) {
            if (allPatterns != null) {
                allPatterns.monthlyPatternsList.add(patterns);
            } else {
                allPatterns = new AllPatterns();
                allPatterns.monthlyPatternsList.add(patterns);
            }
        }
        if (safegraphPlaces != null) {
            if (allSafegraphPlaces != null) {
                allSafegraphPlaces.monthlySafegraphPlacesList.add(safegraphPlaces);
            } else {
                allSafegraphPlaces = new AllSafegraphPlaces();
                allSafegraphPlaces.monthlySafegraphPlacesList.add(safegraphPlaces);
            }
        }
    }

    public void connectPatternsAndPlaces(Patterns patterns, SafegraphPlaces places, AllGISData allGISData, boolean isParallel, int numCPU) {
        for (int i = patterns.patternRecords.size() - 1; i >= 0; i--) {
            if (patterns.patternRecords.get(i).poi_cbg == 0) {
                patterns.patternRecords.remove(i);
//                places.placesRecords.remove(i);
            } else {
                if (patterns.patternRecords.get(i).visitor_daytime_cbgs != null) {
                    patterns.patternRecords.get(i).visitor_daytime_cbgs_place = new ArrayList();
                }
                if (patterns.patternRecords.get(i).visitor_home_cbgs != null) {
                    patterns.patternRecords.get(i).visitor_home_cbgs_place = new ArrayList();
                }
            }
        }
        if (isParallel == true) {
            int numProcessors = numCPU;
            if (numProcessors > Runtime.getRuntime().availableProcessors()) {
                numProcessors = Runtime.getRuntime().availableProcessors();
            }
            ParallelPatternPlaceConnection parallelPatternPlaceConnection[] = new ParallelPatternPlaceConnection[numProcessors];

            for (int i = 0; i < numProcessors - 1; i++) {
                parallelPatternPlaceConnection[i] = new ParallelPatternPlaceConnection(i, null, patterns, places, allGISData, (int) Math.floor(i * ((patterns.patternRecords.size()) / numProcessors)), (int) Math.floor((i + 1) * ((patterns.patternRecords.size()) / numProcessors)));
            }
            parallelPatternPlaceConnection[numProcessors - 1] = new ParallelPatternPlaceConnection(numProcessors - 1, null, patterns, places, allGISData, (int) Math.floor((numProcessors - 1) * ((patterns.patternRecords.size()) / numProcessors)), patterns.patternRecords.size());

            for (int i = 0; i < numProcessors; i++) {
                parallelPatternPlaceConnection[i].myThread.start();
            }
            for (int i = 0; i < numProcessors; i++) {
                try {
                    parallelPatternPlaceConnection[i].myThread.join();
                    System.out.println("thread " + i + "finished for records: " + parallelPatternPlaceConnection[i].myStartIndex + " | " + parallelPatternPlaceConnection[i].myEndIndex);
                } catch (InterruptedException ie) {
                    System.out.println(ie.toString());
                }
            }
        } else {
            int counter = 0;
            int largerCounter = 0;
            int counterInterval = 1000;
            int lastValidIndex = 0;
            for (int i = 0; i < patterns.patternRecords.size(); i++) {
                if (places != null) {
                    for (int j = 0; j < places.placesRecords.size(); j++) {
                        int plusSign = -1;
                        int minusSign = -1;
                        if (i + j + lastValidIndex < places.placesRecords.size()) {
                            plusSign = i + j + lastValidIndex;
                        }
                        if (i - j + lastValidIndex > -1) {
                            minusSign = i - j + lastValidIndex;
                        }
                        if (plusSign > -1) {
                            if (patterns.patternRecords.get(i).placeKey.equals(places.placesRecords.get(plusSign).placeKey)) {

                                CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).poi_cbg);
                                lastValidIndex = lastValidIndex + j;
                                if (temp == null) {
//                                patterns.records.remove(i);
//                                places.records.remove(i);
                                    System.out.println("CENSUS BLOCK GROUP NOT FOUND!");
                                    break;
                                }
                                places.placesRecords.get(plusSign).censusBlock = temp;
                                patterns.patternRecords.get(i).place = places.placesRecords.get(plusSign);
                                break;
                            }
                        }
                        if (minusSign > -1) {
//                    System.out.println("PATTERNS: "+patterns.records.get(i).placeKey);
//                    System.out.println("PLACES: "+places.records.get(minusSign).placeKey);
                            if (patterns.patternRecords.get(i).placeKey.equals(places.placesRecords.get(minusSign).placeKey)) {
                                CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).poi_cbg);
                                lastValidIndex = lastValidIndex - j;
                                if (temp == null) {
//                                patterns.records.remove(i);
//                                places.records.remove(i);
                                    System.out.println("CENSUS BLOCK GROUP NOT FOUND!");
                                    break;
                                }
                                places.placesRecords.get(minusSign).censusBlock = temp;
                                patterns.patternRecords.get(i).place = places.placesRecords.get(minusSign);
                                break;
                            }
                        }
                    }
                }
                CensusBlockGroup tempCensusTract = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).poi_cbg);
                patterns.patternRecords.get(i).poi_cbg_censusBlock = tempCensusTract;
                if (patterns.patternRecords.get(i).visitor_daytime_cbgs != null) {
                    for (int k = 0; k < patterns.patternRecords.get(i).visitor_daytime_cbgs.size(); k++) {
                        CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).visitor_daytime_cbgs.get(k).key);
                        patterns.patternRecords.get(i).visitor_daytime_cbgs_place.add(new CensusBlockGroupIntegerTuple(temp, patterns.patternRecords.get(i).visitor_daytime_cbgs.get(k).value));
                    }
                }
                if (patterns.patternRecords.get(i).visitor_home_cbgs != null) {
                    for (int k = 0; k < patterns.patternRecords.get(i).visitor_home_cbgs.size(); k++) {
                        CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).visitor_home_cbgs.get(k).key);
                        patterns.patternRecords.get(i).visitor_home_cbgs_place.add(new CensusBlockGroupIntegerTuple(temp, patterns.patternRecords.get(i).visitor_home_cbgs.get(k).value));
                    }
                }
                counter = counter + 1;
                if (counter > counterInterval) {
                    largerCounter = largerCounter + 1;
                    counter = 0;
                    System.out.println("Num patterns processed: " + largerCounter * counterInterval);
                }
            }
        }
    }

    public static Patterns getSubPattern(Object restriction, Patterns patterns) {
        Patterns output = new Patterns();
        if (restriction instanceof Country) {
            Country country = ((Country) restriction);
            output.name = patterns.name;
            output.patternRecords = new ArrayList();
            for (int i = 0; i < patterns.patternRecords.size(); i++) {
                if (patterns.patternRecords.get(i).place.censusBlock.country.name.equals(country.name)) {
                    output.patternRecords.add(patterns.patternRecords.get(i));
                }
            }
        }
        if (restriction instanceof State) {
            State state = ((State) restriction);
            output.name = patterns.name;
            output.patternRecords = new ArrayList();
            for (int i = 0; i < patterns.patternRecords.size(); i++) {
                if (patterns.patternRecords.get(i).place.censusBlock.state.name.equals(state.name)) {
                    output.patternRecords.add(patterns.patternRecords.get(i));
                }
            }
        }
        if (restriction instanceof County) {
            County county = ((County) restriction);
            output.name = patterns.name;
            output.patternRecords = new ArrayList();
            for (int i = 0; i < patterns.patternRecords.size(); i++) {
                if (patterns.patternRecords.get(i).place.censusBlock.county.name.equals(county.name)) {
                    output.patternRecords.add(patterns.patternRecords.get(i));
                }
            }
        }
        if (restriction instanceof City) {
            City city = ((City) restriction);
            output.name = patterns.name;
            output.patternRecords = new ArrayList();
            for (int i = 0; i < patterns.patternRecords.size(); i++) {
                for (int j = 0; j < city.censusTracts.size(); j++) {
                    if (patterns.patternRecords.get(i).place == null) {
                        System.out.println("!!!!!!!!");
                    }
                    if (patterns.patternRecords.get(i).place.censusBlock.censusTract.id == city.censusTracts.get(j).id) {
                        output.patternRecords.add(patterns.patternRecords.get(i));
                        break;
                    }
                }
            }
        }
        return output;
    }

    public static SafegraphPlaces getSubPlace(Object restriction, SafegraphPlaces safegraphPlaces) {
        SafegraphPlaces output = new SafegraphPlaces();
        if (restriction instanceof Country) {
            Country country = ((Country) restriction);
            output.name = safegraphPlaces.name;
            output.placesRecords = new ArrayList();
            for (int i = 0; i < safegraphPlaces.placesRecords.size(); i++) {
                if (safegraphPlaces.placesRecords.get(i).censusBlock.country.name.equals(country.name)) {
                    output.placesRecords.add(safegraphPlaces.placesRecords.get(i));
                }
            }
        }
        if (restriction instanceof State) {
            State state = ((State) restriction);
            output.name = safegraphPlaces.name;
            output.placesRecords = new ArrayList();
            for (int i = 0; i < safegraphPlaces.placesRecords.size(); i++) {
                if (safegraphPlaces.placesRecords.get(i).censusBlock.state.name.equals(state.name)) {
                    output.placesRecords.add(safegraphPlaces.placesRecords.get(i));
                }
            }
        }
        if (restriction instanceof County) {
            County county = ((County) restriction);
            output.name = safegraphPlaces.name;
            output.placesRecords = new ArrayList();
            for (int i = 0; i < safegraphPlaces.placesRecords.size(); i++) {
                if (safegraphPlaces.placesRecords.get(i).censusBlock.county.name.equals(county.name)) {
                    output.placesRecords.add(safegraphPlaces.placesRecords.get(i));
                }
            }
        }
        if (restriction instanceof City) {
            City city = ((City) restriction);
            output.name = safegraphPlaces.name;
            output.placesRecords = new ArrayList();
            for (int i = 0; i < safegraphPlaces.placesRecords.size(); i++) {
                for (int j = 0; j < city.censusTracts.size(); j++) {
                    if (safegraphPlaces.placesRecords.get(i).censusBlock != null) {
                        if (safegraphPlaces.placesRecords.get(i).censusBlock.censusTract.id == city.censusTracts.get(j).id) {
                            output.placesRecords.add(safegraphPlaces.placesRecords.get(i));
                            break;
                        }
                    }
                }
            }
        }
        return output;
    }

    public void initAllPatternsAllPlaces() {
        allPatterns = new AllPatterns();
        allPatterns.monthlyPatternsList = new ArrayList();
        allSafegraphPlaces = new AllSafegraphPlaces();
        allSafegraphPlaces.monthlySafegraphPlacesList = new ArrayList();
    }

}
