package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import COVID_AgentBasedSimulation.Model.Dataset;
import COVID_AgentBasedSimulation.Model.DatasetTemplate;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.RecordTemplate;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
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
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objenesis.strategy.StdInstantiatorStrategy;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author user
 */
public class Safegraph extends Dataset implements Serializable {

    static final long serialVersionUID = softwareVersion;

    public AllPatterns allPatterns;
    public AllSafegraphPlaces allSafegraphPlaces;

    @Override
    public void requestDataset(AllGISData allGISData, int year, int month, boolean isParallel, int numCPU) {
        clearPatternsPlaces();
        loadPatternsPlacesSet(String.valueOf(year) + "_" + String.valueOf(month), allGISData, isParallel, numCPU);
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
            temp.name = PatternsRecordProcessed.class.getFields()[i].getName();
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
            temp.name = SafegraphPlace.class.getFields()[i].getName();
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
        String[] patternsDirectories = AllPatterns.detectAllPatterns("./datasets");
        String[] placesDirectories = AllSafegraphPlaces.detectAllPlaces("./datasets");
        for (int i = 0; i < patternsDirectories.length; i++) {
            for (int j = 0; j < placesDirectories.length; j++) {
                if (patternsDirectories[i].substring(9).equals(placesDirectories[j].substring(9))) {
                    File processedPatterns = new File("./datasets/" + patternsDirectories[i] + "/processedData.bin");
                    File processedPlaces = new File("./datasets/" + placesDirectories[i] + "/processedData.bin");
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
            input = new Input(new FileInputStream(passed_file_path));
            Patterns patterns = kryo.readObject(input, Patterns.class);
            input.close();

            return patterns;
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
            input = new Input(new FileInputStream(passed_file_path));
            SafegraphPlaces safegraphPlaces = kryo.readObject(input, SafegraphPlaces.class);
            input.close();

            return safegraphPlaces;
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

    public void loadPatternsPlacesSet(String date, AllGISData allGISData, boolean isParallel, int numCPU) {
        Patterns patterns = loadPatternsKryo("./datasets/patterns_" + date + "/processedData.bin");
        SafegraphPlaces safegraphPlaces = loadSafegraphPlacesKryo("./datasets/core_poi_" + date + "/processedData.bin");

//        for(int i=0;i<20;i++){
//            System.out.println(patterns.records.get(i).placeKey);
//        }
//        System.out.println("***");
//        for(int i=0;i<20;i++){
//            System.out.println(patterns.records.get(i).placeKey);
//        }
//        System.out.println("$$$");
//        
//        for(int i=0;i<20;i++){
//            System.out.println(safegraphPlaces.records.get(i).placeKey);
//        }
//        System.out.println("***");
//        for(int i=0;i<20;i++){
//            System.out.println(safegraphPlaces.records.get(i).placeKey);
//        }
//        System.out.println("$$$");
        System.out.println("Data loaded. Connecting patterns and places ...");
        connectPatternsAndPlaces(patterns, safegraphPlaces, allGISData, isParallel, numCPU);
        System.out.println("Connection done");
        if (allPatterns != null) {
            allPatterns.monthlyPatternsList.add(patterns);
        } else {
            allPatterns = new AllPatterns();
            allPatterns.monthlyPatternsList.add(patterns);
        }
        if (allSafegraphPlaces != null) {
            allSafegraphPlaces.monthlySafegraphPlacesList.add(safegraphPlaces);
        } else {
            allSafegraphPlaces = new AllSafegraphPlaces();
            allSafegraphPlaces.monthlySafegraphPlacesList.add(safegraphPlaces);
        }

    }

    public void connectPatternsAndPlaces(Patterns patterns, SafegraphPlaces places, AllGISData allGISData, boolean isParallel, int numCPU) {
        for (int i = patterns.patternRecords.size() - 1; i >= 0; i--) {
            if (patterns.patternRecords.get(i).poi_cbg == 0) {
                patterns.patternRecords.remove(i);
                places.placesRecords.remove(i);
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
                for (int j = 0; j < places.placesRecords.size(); j++) {
                    int plusSign = -1;
                    int minusSign = -1;
                    if (i + j < places.placesRecords.size()) {
                        plusSign = i + j + lastValidIndex;
                    }
                    if (i - j > -1) {
                        minusSign = i - j + lastValidIndex;
                    }
                    if (plusSign > -1) {
//                    System.out.println("PATTERNS: "+patterns.records.get(i).placeKey);
//                    System.out.println("PLACES: "+places.records.get(plusSign).placeKey);
                        if (patterns.patternRecords.get(i).placeKey.equals(places.placesRecords.get(plusSign).placeKey)) {
//                    if(counter==87919){
//                        System.out.println("!!!!");
//                    }
                            CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).poi_cbg);
                            lastValidIndex = lastValidIndex + j;
                            if (temp == null) {
//                                patterns.records.remove(i);
//                                places.records.remove(i);
                                System.out.println("CENSUS BLOCK GROUP NOT FOUND!");
                                break;
                            }
                            places.placesRecords.get(plusSign).censusBlock = temp;
                            break;
                        }
                    }
                    if (minusSign > -1) {
//                    System.out.println("PATTERNS: "+patterns.records.get(i).placeKey);
//                    System.out.println("PLACES: "+places.records.get(minusSign).placeKey);
                        if (patterns.patternRecords.get(i).placeKey.equals(places.placesRecords.get(minusSign).placeKey)) {
//                    if(counter==83308){
//                        System.out.println("!!!!");
//                    }
                            CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).poi_cbg);
                            lastValidIndex = lastValidIndex - j;
                            if (temp == null) {
//                                patterns.records.remove(i);
//                                places.records.remove(i);
                                System.out.println("CENSUS BLOCK GROUP NOT FOUND!");
                                break;
                            }
                            places.placesRecords.get(minusSign).censusBlock = temp;
                            break;
                        }
                    }
                }
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
//        counter = 0;
//        for (int i = 0; i < patterns.records.size(); i++) {
//            if (patterns.records.get(i).visitor_daytime_cbgs != null) {
//                patterns.records.get(i).visitor_daytime_cbgs_place = new ArrayList();
//                for (int k = 0; k < patterns.records.get(i).visitor_daytime_cbgs.size(); k++) {
//                    CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.records.get(i).visitor_daytime_cbgs.get(k).key);
//                    patterns.records.get(i).visitor_daytime_cbgs_place.add(new CensusBlockGroupIntegerTuple(temp, patterns.records.get(i).visitor_daytime_cbgs.get(k).value));
//                }
//            }
//            if (patterns.records.get(i).visitor_home_cbgs != null) {
//                patterns.records.get(i).visitor_home_cbgs_place = new ArrayList();
//                for (int k = 0; k < patterns.records.get(i).visitor_home_cbgs.size(); k++) {
//                    CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.records.get(i).visitor_home_cbgs.get(k).key);
//                    patterns.records.get(i).visitor_home_cbgs_place.add(new CensusBlockGroupIntegerTuple(temp, patterns.records.get(i).visitor_home_cbgs.get(k).value));
//                }
//            }
//            counter = counter + 1;
//            System.out.println("P2: " + counter);
//        }
        }
    }

    public void initAllPatternsAllPlaces() {
        allPatterns = new AllPatterns();
        allPatterns.monthlyPatternsList = new ArrayList();
        allSafegraphPlaces = new AllSafegraphPlaces();
        allSafegraphPlaces.monthlySafegraphPlacesList = new ArrayList();
    }

}
