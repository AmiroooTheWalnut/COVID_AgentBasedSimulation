/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Marker;
import COVID_AgentBasedSimulation.Model.Structure.Scope;
import COVID_AgentBasedSimulation.Model.Structure.TessellationCell;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import de.siegmar.fastcsv.writer.CsvWriter;
import esmaieeli.gisFastLocationOptimization.GIS3D.AllData;
import esmaieeli.gisFastLocationOptimization.GIS3D.LayerDefinition;
import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;
import esmaieeli.gisFastLocationOptimization.GIS3D.NumericLayer;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.XMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

/**
 *
 * @author user
 */
public class RootArtificial extends Root {
    
    public AllData allDataGIS;

//    public MainFramePanel mainFParent = new esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel();
    public ArrayList<Person> peopleNoTessellation = new ArrayList();
    
    public RegionImageLayer cBGRegionsLayer = new RegionImageLayer();
    
    public boolean isTessellationBuilt = false;
    
    public boolean isTest = false;
    
    public int numNoTessellation = -1;//MUST BE ASSIGNED BEFORE CONSTRUCTOR IS CALLED!

    public ArrayList<ScheduleListExact> scheduleListExactArray;
    public ArrayList<ScheduleListExact> scheduleListExactArrayClustering;
    
    public ArrayList<Double> sumHomeScheduleDifferences = new ArrayList();
    public ArrayList<Double> sumHomeScheduleDifferencesClustering = new ArrayList();
    public ArrayList<Double> sumWorkScheduleDifferences = new ArrayList();
    public ArrayList<Double> sumWorkScheduleDifferencesClustering = new ArrayList();
    public ArrayList<Integer> isFoundHomeRegion = new ArrayList();
    public ArrayList<Integer> isFoundWorkRegion = new ArrayList();
    
    HashMap<String, Double> pOIProbs = new HashMap();
    
    HashMap<Integer, Long> exhaustiveNAICSFreqs = new HashMap();
    
    protected Instances m_Instances;
    public ClustererManager clustererManager;

//    public LinkedHashMap<String, POI> pOIsLHM = new LinkedHashMap();
    public RootArtificial(MainModel modelRoot) {
        super(modelRoot);
    }
    
    @Override
    public void constructor(MainModel modelRoot, int passed_numAgents, String passed_regionType, int passed_numRandomRegions, boolean isCompleteInfection, boolean isInfectCBGOnly, ArrayList<Integer> initialInfectionRegionIndex) {
        myModelRoot = modelRoot;
        myModelRoot.isArtificialExact = true;
        allDataGIS = myModelRoot.ABM.allData;
        regionType = passed_regionType;
        clustererManager = new ClustererManager();
        
        switch (regionType) {
            case "noTessellation":
                isTessellationBuilt = false;
                break;
            case "CBG":
                isTessellationBuilt = true;
                break;
            case "VDFMTH":
                isTessellationBuilt = true;
                break;
            case "CBGVDFMTH":
                isTessellationBuilt = true;
                break;
            case "RMCBG":
                isTessellationBuilt = true;
                break;
            case "VDFNC":
                isTessellationBuilt = true;
                break;
            case "AVDFMTH":
                isTessellationBuilt = true;
                break;
            default:
            
        }
        
        String[] header = new String[10];
        header[0] = "Date";
        header[1] = "SUSCEPTIBLE";
        header[2] = "INFECTED_SYM";
        header[3] = "INFECTED_ASYM";
        header[4] = "RECOVERED";
        header[5] = "DEAD";
        header[6] = "IPR_JHU_S_AS";
        header[7] = "IPR_JHU_S";
        header[8] = "IPR_S_AS_SIM";
        header[9] = "IPR_S_SIM";
        
        infectionPoll.add(header);

//        myModelRoot.ABM.agents=new CopyOnWriteArrayList(passed_numAgents);
        generateRegions(modelRoot, passed_regionType, passed_numRandomRegions);
//        if (isInfectCBGOnly == true) {
        int cBGTessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "CBG");
        cBGregions = makeByVDTessellationCBG(modelRoot, cBGTessellationIndex);
//        }

        generateNoTessellationPeople(isTessellationBuilt, passed_numAgents);
        if (isTessellationBuilt == true) {
            generatePostProcessPeople(passed_numAgents);
        } else {
            regions = cBGregions;
        }
//        generateSchedules(modelRoot, passed_regionType, regions);
        if (modelRoot.ABM.isReportContactRate == true) {
            agentPairContact = new int[people.size()][people.size()];
        }
        if (modelRoot.ABM.isShamilABMActive == true) {
            if (modelRoot.ABM.isOurABMActive == true) {
                ShamilSimulatorController.shamilAgentGenerationSpatial(modelRoot, regions, people);
                initiallyInfect(isCompleteInfection, isInfectCBGOnly, true, initialInfectionRegionIndex);
//                initiallyInfectDummy();
                reportConsoleOurABMInfection(true);
                for (int i = 0; i < people.size(); i++) {
                    people.get(i).isActive = true;
                }
            } else {
                System.out.println("NOT IMPLEMENTED: ONLY SHAMIL AND OURS IS IMPLEMENTED");
                //ShamilSimulatorController.shamilAgentGeneration(modelRoot, people);
                //ShamilSimulatorController.shamilInitialInfection(modelRoot, people);
            }
        } else {
            if (modelRoot.ABM.isOurABMActive == true) {
                System.out.println("NOT IMPLEMENTED: ONLY SHAMIL AND OURS IS IMPLEMENTED");
//                initiallyInfect(isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex);
////                initiallyInfectDummy();
//                reportConsoleOurABMInfection(true);
//                for (int i = 0; i < people.size(); i++) {
//                    people.get(i).isActive = true;
//                }
            }
        }
    }
    
    public void generateNoTessellationPeople(boolean isTessellationBuilt, int passed_numAgents) {
        generatePOIs(myModelRoot);
        
        preprocessNodesInRegions(cBGregions);
        
        if (isTessellationBuilt == false) {
            generateAgentsRaw(myModelRoot, passed_numAgents, cBGregions, isTessellationBuilt, isTest);
            selectExactLocations(people);
            generatePeopleSchedulesForHome(myModelRoot, people);
            generatePeopleSchedulesForWork(myModelRoot, people);
        } else {
            int numNoTessellationV;
            if (numNoTessellation > 0) {
                numNoTessellationV = numNoTessellation;
            } else {
                numNoTessellationV = ((Marker) (myModelRoot.ABM.studyScopeGeography)).population;
            }
            generateAgentsRaw(myModelRoot, numNoTessellationV, cBGregions, isTessellationBuilt, isTest);
            selectExactLocations(peopleNoTessellation);
            generatePeopleSchedulesForHome(myModelRoot, peopleNoTessellation);
            generatePeopleSchedulesForWork(myModelRoot, peopleNoTessellation);
        }
    }
    
    public void generatePostProcessPeople(int passed_numAgents) {
        calculateRegionSchedules();
        generateAgentsRaw(myModelRoot, passed_numAgents, regions, false, isTest);
        postGeneratePeopleSchedulesForHome();
        postGeneratePeopleSchedulesForWork();
    }
    
    public void calculateRegionSchedules() {
        scheduleListExactArray = new ArrayList();
        ArrayList<Integer> avgCounterHomeArray = new ArrayList();
        ArrayList<Integer> avgCounterWorkArray = new ArrayList();
        for (int i = 0; i < regions.size(); i++) {
            ScheduleListExact scheduleListExact = new ScheduleListExact();
            scheduleListExact.regionIndex = i;
            for (int m = 0; m < peopleNoTessellation.get(0).exactProperties.pOIs.size(); m++) {
                scheduleListExact.fromHomeFreqs.add(0d);
                scheduleListExact.fromWorkFreqs.add(0d);
            }
            avgCounterHomeArray.add(0);
            avgCounterWorkArray.add(0);
            scheduleListExact.pOIs = peopleNoTessellation.get(0).exactProperties.pOIs;
            scheduleListExactArray.add(scheduleListExact);
        }
        
        for (int i = 0; i < peopleNoTessellation.size(); i++) {
            int cellIndexHome = regionsLayer.getCellOfLatLon(peopleNoTessellation.get(i).exactProperties.exactHomeLocation.lat, peopleNoTessellation.get(i).exactProperties.exactHomeLocation.lon);
            if (cellIndexHome != -1) {
                for (int k = 0; k < peopleNoTessellation.get(i).exactProperties.fromHomeFreqs.size(); k++) {
                    Double newValue = scheduleListExactArray.get(cellIndexHome).fromHomeFreqs.get(k) + peopleNoTessellation.get(i).exactProperties.fromHomeFreqs.get(k);
                    scheduleListExactArray.get(cellIndexHome).fromHomeFreqs.set(k, newValue);
                }
                avgCounterHomeArray.set(cellIndexHome, avgCounterHomeArray.get(cellIndexHome) + 1);
            }
            int cellIndexWork = regionsLayer.getCellOfLatLon(peopleNoTessellation.get(i).exactProperties.exactWorkLocation.lat, peopleNoTessellation.get(i).exactProperties.exactWorkLocation.lon);
            if (cellIndexWork != -1) {
                for (int k = 0; k < peopleNoTessellation.get(i).exactProperties.fromWorkFreqs.size(); k++) {
                    Double newValue = scheduleListExactArray.get(cellIndexWork).fromWorkFreqs.get(k) + peopleNoTessellation.get(i).exactProperties.fromWorkFreqs.get(k);
                    scheduleListExactArray.get(cellIndexWork).fromWorkFreqs.set(k, newValue);
                }
                avgCounterWorkArray.set(cellIndexWork, avgCounterWorkArray.get(cellIndexWork) + 1);
            }
        }
        
        for (int i = 0; i < regions.size(); i++) {
            for (int j = 0; j < scheduleListExactArray.get(i).fromHomeFreqs.size(); j++) {
                scheduleListExactArray.get(i).fromHomeFreqs.set(j, scheduleListExactArray.get(i).fromHomeFreqs.get(j) / avgCounterHomeArray.get(i));
            }
            for (int j = 0; j < scheduleListExactArray.get(i).fromWorkFreqs.size(); j++) {
                scheduleListExactArray.get(i).fromWorkFreqs.set(j, scheduleListExactArray.get(i).fromWorkFreqs.get(j) / avgCounterWorkArray.get(i));
            }
        }
        
        for (int i = 0; i < scheduleListExactArray.size(); i++) {
            double SH=0;
            for (int j = 0; j < scheduleListExactArray.get(i).fromHomeFreqs.size(); j++) {
                SH=SH+scheduleListExactArray.get(i).fromHomeFreqs.get(j);
                scheduleListExactArray.get(i).fromHomeFreqsCDF.add(SH);
            }
            scheduleListExactArray.get(i).sumHomeFreqs=SH;
            double SW=0;
            for (int j = 0; j < scheduleListExactArray.get(i).fromWorkFreqs.size(); j++) {
                SW=SW+scheduleListExactArray.get(i).fromWorkFreqs.get(j);
                scheduleListExactArray.get(i).fromWorkFreqsCDF.add(SW);
            }
            scheduleListExactArray.get(i).sumWorkFreqs=SW;
        }
        
        sumHomeScheduleDifferences = new ArrayList<Double>(Collections.nCopies(regions.size(), 0.0));
        sumWorkScheduleDifferences = new ArrayList<Double>(Collections.nCopies(regions.size(), 0.0));
        isFoundHomeRegion = new ArrayList<Integer>(Collections.nCopies(regions.size(), 0));
        isFoundWorkRegion = new ArrayList<Integer>(Collections.nCopies(regions.size(), 0));
        
        ArrayList<PsudoRegion> psudoHomeRegions = new ArrayList(regions.size());
        for (int i = 0; i < regions.size(); i++) {
            psudoHomeRegions.add(new PsudoRegion());
        }
        for (int i = 0; i < peopleNoTessellation.size(); i++) {
            int cellIndexHome = regionsLayer.getCellOfLatLon(peopleNoTessellation.get(i).exactProperties.exactHomeLocation.lat, peopleNoTessellation.get(i).exactProperties.exactHomeLocation.lon);
            if (cellIndexHome != -1) {
                psudoHomeRegions.get(cellIndexHome).people.add(peopleNoTessellation.get(i));
                isFoundHomeRegion.set(cellIndexHome, 1);
            }
        }
        
        ArrayList<PsudoRegion> psudoWorkRegions = new ArrayList(regions.size());
        for (int i = 0; i < regions.size(); i++) {
            psudoWorkRegions.add(new PsudoRegion());
        }
        for (int i = 0; i < peopleNoTessellation.size(); i++) {
            int cellIndexWork = regionsLayer.getCellOfLatLon(peopleNoTessellation.get(i).exactProperties.exactWorkLocation.lat, peopleNoTessellation.get(i).exactProperties.exactWorkLocation.lon);
            if (cellIndexWork != -1) {
                psudoWorkRegions.get(cellIndexWork).people.add(peopleNoTessellation.get(i));
                isFoundWorkRegion.set(cellIndexWork, 1);
            }
        }
        
        for (int i = 0; i < psudoHomeRegions.size(); i++) {
            double varReg = 0;
            for (int k = 0; k < scheduleListExactArray.get(i).fromHomeFreqs.size(); k++) {
                double varPOI = 0;
                for (int j = 0; j < psudoHomeRegions.get(i).people.size(); j++) {
                    varPOI = varPOI + Math.pow(scheduleListExactArray.get(i).fromHomeFreqs.get(k) - psudoHomeRegions.get(i).people.get(j).exactProperties.fromHomeFreqs.get(k), 2);
                }
                varPOI = varPOI / avgCounterHomeArray.get(i);
                varReg = varReg + varPOI;
            }
            if (Double.isNaN(varReg) == true) {
                varReg = 0;
            }
            sumHomeScheduleDifferences.set(i, varReg);
            
            varReg = 0;
            for (int k = 0; k < scheduleListExactArray.get(i).fromWorkFreqs.size(); k++) {
                double varPOI = 0;
                for (int j = 0; j < psudoWorkRegions.get(i).people.size(); j++) {
                    varPOI = varPOI + Math.pow(scheduleListExactArray.get(i).fromWorkFreqs.get(k) - psudoWorkRegions.get(i).people.get(j).exactProperties.fromWorkFreqs.get(k), 2);
                }
                varPOI = varPOI / avgCounterWorkArray.get(i);
                varReg = varReg + varPOI;
            }
            if (Double.isNaN(varReg) == true) {
                varReg = 0;
            }
            sumWorkScheduleDifferences.set(i, varReg);
        }

    }
    
    public void generateAgentsRaw(MainModel modelRoot, int passed_numAgents, ArrayList<Region> regions, boolean isTessellationBuilt, boolean isTest) {
        int cumulativePopulation = 0;
        for (int i = 0; i < regions.size(); i++) {
            cumulativePopulation = cumulativePopulation + regions.get(i).population;
        }
        pTSFraction = (double) ((City) (modelRoot.ABM.studyScopeGeography)).population / (double) passed_numAgents;
        if (pTSFraction < 1) {
            System.out.println("THE NUMBER OF AGENTS IS MORE THAN REAL NUMBER OF PEOPLE!!!");
        }
        sumRegionsPopulation = cumulativePopulation;
        if (isTest == false) {
            for (int i = 0; i < passed_numAgents; i++) {
                Person person = new Person(i);
                selectHomeRegionScheduleLess(person, sumRegionsPopulation, regions, isTest);
                selectWorkRegion(person, person.properties.homeRegion);
                if (modelRoot.ABM.isFuzzyStatus == false) {
                    person.insidePeople = new ArrayList();
                    person.insidePeople.add(new FuzzyPerson());
                    pTSFraction = 1;
                } else {
                    person.insidePeople = new ArrayList();
                    for (int m = 0; m < pTSFraction; m++) {
                        person.insidePeople.add(new FuzzyPerson());
                    }
                }
                //modelRoot.ABM.agents.add(person);
                modelRoot.ABM.agentsRaw.add(person);
                person.constructor(modelRoot);
                if (isTessellationBuilt == false) {
                    people.add(person);
                } else {
                    peopleNoTessellation.add(person);
                }
            }
        } else {
            Person person = new Person(0);
            selectHomeRegionScheduleLess(person, sumRegionsPopulation, regions, isTest);
            selectWorkRegion(person, person.properties.homeRegion);
            if (modelRoot.ABM.isFuzzyStatus == false) {
                person.insidePeople = new ArrayList();
                person.insidePeople.add(new FuzzyPerson());
                pTSFraction = 1;
            } else {
                person.insidePeople = new ArrayList();
                for (int m = 0; m < pTSFraction; m++) {
                    person.insidePeople.add(new FuzzyPerson());
                }
            }
            //modelRoot.ABM.agents.add(person);
            modelRoot.ABM.agentsRaw.add(person);
            person.constructor(modelRoot);
            people.add(person);
        }
    }
    
    public void selectHomeRegionScheduleLess(Person person, int cumulativePopulation, ArrayList<Region> regions, boolean isTest) {
        if (isTest == false) {
            int indexCumulativePopulation = (int) ((rnd.nextDouble() * (cumulativePopulation - 1)));
            int cumulativePopulationRun = 0;
            for (int j = 0; j < regions.size(); j++) {
                cumulativePopulationRun = cumulativePopulationRun + regions.get(j).population;
                if (cumulativePopulationRun > indexCumulativePopulation) {
                    person.properties.homeRegion = regions.get(j);
                    regions.get(j).residents.add(person);
                    break;
                }
            }
        } else {
            person.properties.homeRegion = regions.get(0);
            regions.get(0).residents.add(person);
        }
    }
    
    public void generatePOIs(MainModel modelRoot) {
//        ArrayList<PatternsRecordProcessed> patternRecords = new ArrayList();
        if (pOIs == null) {
            pOIs = new LinkedHashMap();
        }
        for (int i = 0; i < modelRoot.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            ArrayList<PatternsRecordProcessed> patternRecordsTemp = modelRoot.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords;
            for (int j = 0; j < patternRecordsTemp.size(); j++) {
                if (isFoodAndGrocery(patternRecordsTemp.get(j).place.naics_code) == true) {//FOR NOW ONLY GROCERIES ARE IMPLEMENTED
//                    patternRecords.add(patternRecordsTemp.get(j));
                    if (pOIs.containsKey(patternRecordsTemp.get(j).placeKey)) {
                        pOIs.get(patternRecordsTemp.get(j).placeKey).patternsRecord = patternRecordsTemp.get(j);
                    } else {
                        POI tempPOI = new POI();
                        tempPOI.patternsRecord = patternRecordsTemp.get(j);
                        pOIs.put(patternRecordsTemp.get(j).placeKey, tempPOI);
                    }
                }
                
            }
        }
        
        for (POI value : pOIs.values()) {
//            String key = mapElement.getKey();
//            POI value = mapElement.getValue();
            travelsToAllPOIsFreqs.put(value.patternsRecord.placeKey, 0L);
        }
        
        try {
            FileReader filereader = new FileReader(myModelRoot.datasetDirectory + File.separator + "FullScale_exhaustiveNAICS.csv");
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(0)
                    .build();
            List<String[]> dataRaw = csvReader.readAll();
            for (int i = 0; i < dataRaw.size(); i++) {
                exhaustiveNAICSFreqs.put(Integer.valueOf(dataRaw.get(i)[0]), Long.valueOf(dataRaw.get(i)[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Long> freqs = new ArrayList();
        ArrayList<String> pOIIds = new ArrayList();
        long sum = 0;
        for (HashMap.Entry<String, POI> mapElement : pOIs.entrySet()) {
            String key = mapElement.getKey();
            POI value = mapElement.getValue();
            if (exhaustiveNAICSFreqs.containsKey(value.patternsRecord.place.naics_code)) {
                freqs.add(exhaustiveNAICSFreqs.get(value.patternsRecord.place.naics_code));
                pOIIds.add(key);
                sum = sum + exhaustiveNAICSFreqs.get(value.patternsRecord.place.naics_code);
            }
        }
        for (int i = 0; i < freqs.size(); i++) {
            pOIProbs.put(pOIIds.get(i), (double) (freqs.get(i)) / (double) sum);
        }

//        generateRegionSchedules(modelRoot, regions, patternRecords, type);
    }
    
    public void generatePeopleSchedulesForHome(MainModel modelRoot, ArrayList<Person> people) {
//        int trafficeLayerIndex = mainFParent.findLayerContains("traffic");
        for (int i = 0; i < people.size(); i++) {
            double maxDist = Double.NEGATIVE_INFINITY;
            double minDist = Double.POSITIVE_INFINITY;
            double avg = 0;
            int avgCounter = 0;
            for (HashMap.Entry<String, POI> mapElement : pOIs.entrySet()) {
                String key = mapElement.getKey();
                POI value = mapElement.getValue();
                float latPOI = value.patternsRecord.place.lat;
                float lonPOI = value.patternsRecord.place.lon;
                
                double dist = Math.sqrt(Math.pow(latPOI - (float) (people.get(i).exactProperties.exactHomeLocation.lat), 2) + Math.pow(lonPOI - (float) (people.get(i).exactProperties.exactHomeLocation.lon), 2));
                dist = dist * pOIProbs.get(key);
                if (maxDist < dist) {
                    maxDist = dist;
                }
                if (minDist > dist) {
                    minDist = dist;
                }
                avg = avg + dist;
                avgCounter = avgCounter + 1;
                people.get(i).exactProperties.pOIs.add(value);
                people.get(i).exactProperties.fromHomeFreqs.add(dist);
//                people.get(i).exactProperties.pOIHomeProbabilities.put(value, dist);
            }
            avg = avg / (double) avgCounter;
            for (int m = 0; m < people.get(i).exactProperties.pOIs.size(); m++) {
//                POI value = mapElement.getValue();
                Double pO = people.get(i).exactProperties.fromHomeFreqs.get(m);
                if (pO > -1) {
                    Double newP = (pO - minDist + 0.001) / (maxDist + 0.001);
                    people.get(i).exactProperties.fromHomeFreqs.set(m, newP);
                    people.get(i).exactProperties.sumHomeFreqs = people.get(i).exactProperties.sumHomeFreqs + newP;
                    people.get(i).exactProperties.fromHomeFreqsCDF.add(people.get(i).exactProperties.sumHomeFreqs);
                } else {
                    Double newP = (avg - minDist + 0.1) / (maxDist + 0.1);
                    people.get(i).exactProperties.fromHomeFreqs.set(m, newP);
                    people.get(i).exactProperties.sumHomeFreqs = people.get(i).exactProperties.sumHomeFreqs + newP;
                    people.get(i).exactProperties.fromHomeFreqsCDF.add(people.get(i).exactProperties.sumHomeFreqs);
                    System.out.println("ROUTING FAILED! AVERAGE DISTANCE IS USED!");
                }
            }
            //System.out.println("total _ i: " + people.size() + " _ " + i);
        }
        System.out.println("DONE SCHEDULES!");
    }
    
    public void postGeneratePeopleSchedulesForHome() {

        for (int m = 0; m < people.size(); m++) {
            ScheduleListExact schedule = scheduleListExactArray.get(people.get(m).properties.homeRegion.myIndex);
            people.get(m).exactProperties.fromHomeFreqs = schedule.fromHomeFreqs;
            people.get(m).exactProperties.fromHomeFreqsCDF = schedule.fromHomeFreqsCDF;
            people.get(m).exactProperties.pOIs = schedule.pOIs;
//            people.get(m).exactProperties.fromWorkFreqsCDF = schedule.fromWorkFreqsCDF;

            people.get(m).exactProperties.sumHomeFreqs = schedule.sumHomeFreqs;
//            people.get(m).exactProperties.sumWorkFreqs = schedule.sumWorkFreqs;
        }
    }
    
    public void postGeneratePeopleSchedulesForWork() {
        for (int m = 0; m < people.size(); m++) {
            ScheduleListExact schedule = scheduleListExactArray.get(people.get(m).properties.workRegion.myIndex);
            people.get(m).exactProperties.fromWorkFreqs = schedule.fromWorkFreqs;
            people.get(m).exactProperties.fromWorkFreqsCDF = schedule.fromWorkFreqsCDF;
            people.get(m).exactProperties.pOIs = schedule.pOIs;

            people.get(m).exactProperties.sumWorkFreqs = schedule.sumWorkFreqs;
        }

    }
    
    public void initiallyInfect(boolean isCompleteInfection, boolean isInfectCBGOnly, boolean isExactInfection, ArrayList<Integer> initialInfectionRegionIndex) {
        if (isExactInfection == true) {
            Scope scope = (Scope) (myModelRoot.ABM.studyScopeGeography);
            detectRelevantCounties(scope);
            int sumRelevantCountiesPopulation = 0;
            int sumRelevantCountiesInfection = 0;
            for (int i = 0; i < relevantDailyConfirmedCases.size(); i++) {
                if ((myModelRoot.ABM.currentTime).truncatedTo(ChronoUnit.DAYS).isEqual(relevantDailyConfirmedCases.get(i).date.truncatedTo(ChronoUnit.DAYS)) == true) {
                    sumRelevantCountiesPopulation += relevantDailyConfirmedCases.get(i).county.population;
                    sumRelevantCountiesInfection += relevantDailyConfirmedCases.get(i).numActiveCases * (10f / 3f);
                }
            }
            int expectedInfectionInScope = (int) (((double) sumRelevantCountiesInfection / (double) sumRelevantCountiesPopulation) * (double) (scope.population));
            double expectedInfectionPercentage = (double) (expectedInfectionInScope) / (double) (scope.population);
            initialRecovered(expectedInfectionPercentage);
            double currentInfections = 0;
            double currentInfectionPercentage = 0;
            while (currentInfectionPercentage < expectedInfectionPercentage) {
                int selectedResident = (int) (rnd.nextDouble() * people.size() - 1);
                if (rnd.nextDouble() > 0.7) {
                    for (int m = 0; m < people.get(selectedResident).insidePeople.size(); m++) {
                        if (people.get(selectedResident).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                            people.get(selectedResident).insidePeople.get(m).fpp.status = statusEnum.INFECTED_SYM.ordinal();
                            people.get(selectedResident).insidePeople.get(m).sfpp.infectedDays = 4 + (int) (rnd.nextDouble() * 10);
                            currentInfections = currentInfections + 1;
                            currentInfectionPercentage = currentInfections / (people.size() * pTSFraction);
                        }
                    }
                } else {
                    for (int m = 0; m < people.get(selectedResident).insidePeople.size(); m++) {
                        if (people.get(selectedResident).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                            people.get(selectedResident).insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
                            people.get(selectedResident).insidePeople.get(m).sfpp.infectedDays = 3 + (int) (rnd.nextDouble() * 3);
                            currentInfections = currentInfections + 1;
                            currentInfectionPercentage = currentInfections / (people.size() * pTSFraction);
                        }
                    }
                }
            }
        } else {
            super.initiallyInfect(isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex);
        }
    }
    
    public void generatePeopleSchedulesForWork(MainModel modelRoot, ArrayList<Person> people) {
//        MainFramePanel mainFParent = new esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel();
//        int trafficeLayerIndex = mainFParent.findLayerContains("traffic");
        for (int i = 0; i < people.size(); i++) {
            double maxDist = Double.NEGATIVE_INFINITY;
            double minDist = Double.POSITIVE_INFINITY;
            double avg = 0;
            int avgCounter = 0;
            for (HashMap.Entry<String, POI> mapElement : pOIs.entrySet()) {
                String key = mapElement.getKey();
                POI value = mapElement.getValue();
                float latPOI = value.patternsRecord.place.lat;
                float lonPOI = value.patternsRecord.place.lon;
                
                double dist = Math.sqrt(Math.pow(latPOI - (float) (people.get(i).exactProperties.exactWorkLocation.lat), 2) + Math.pow(lonPOI - (float) (people.get(i).exactProperties.exactWorkLocation.lon), 2));
                dist = dist * pOIProbs.get(key);
                if (maxDist < dist) {
                    maxDist = dist;
                }
                if (minDist > dist) {
                    minDist = dist;
                }
                avg = avg + dist;
                avgCounter = avgCounter + 1;
                people.get(i).exactProperties.fromWorkFreqs.add(dist);
//                people.get(i).exactProperties.pOIWorkProbabilities.put(value, dist);

//                LocationNode nodePOI = GISLocationDialog.getNearestNode(mainFParent, latPOI, lonPOI, null);
//                LocationNode nodeHome = GISLocationDialog.getNearestNode(mainFParent, (float) (people.get(i).exactProperties.exactWorkLocation.lat), (float) (people.get(i).exactProperties.exactWorkLocation.lon), null);
//                Routing route = new Routing(modelRoot.ABM.allData, trafficeLayerIndex, 0);
//                route.findPath(nodeHome, nodePOI);
//                if (route.path != null) {
//                    if (maxDist < route.pathDistance) {
//                        maxDist = route.pathDistance;
//                    }
//                    if (minDist > route.pathDistance) {
//                        minDist = route.pathDistance;
//                    }
//                    avg = avg + route.pathDistance;
//                    avgCounter = avgCounter + 1;
//                    people.get(i).exactProperties.pOIWorkProbabilities.put(value, route.pathDistance);
//                } else {
//                    people.get(i).exactProperties.pOIWorkProbabilities.put(value, -1.0);
//                }
            }
            avg = avg / (double) avgCounter;
//            System.out.println("****");
            for (int m = 0; m < people.get(i).exactProperties.pOIs.size(); m++) {
//                POI value = mapElement.getValue();
                Double pO = people.get(i).exactProperties.fromWorkFreqs.get(m);
                if (pO > -1) {
                    Double newP = (pO - minDist + 0.1) / (maxDist + 0.1);
                    people.get(i).exactProperties.fromWorkFreqs.set(m, newP);
                    people.get(i).exactProperties.sumWorkFreqs = people.get(i).exactProperties.sumWorkFreqs + newP;
                    people.get(i).exactProperties.fromWorkFreqsCDF.add(people.get(i).exactProperties.sumWorkFreqs);
                } else {
                    Double newP = (avg - minDist + 0.1) / (maxDist + 0.1);
                    people.get(i).exactProperties.fromWorkFreqs.set(m, newP);
                    people.get(i).exactProperties.sumWorkFreqs = people.get(i).exactProperties.sumWorkFreqs + newP;
                    people.get(i).exactProperties.fromWorkFreqsCDF.add(people.get(i).exactProperties.sumWorkFreqs);
                    System.out.println("ROUTING FAILED! AVERAGE DISTANCE IS USED!");
                }
            }
        }
    }
    
    public void preprocessNodesInRegions(ArrayList<Region> regions) {
//        MainFramePanel mainFParent = new esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel();
        int cbgLayerIndex = findLayerExactNotCaseSensitive("cbg");
        int baseLayerIndex = findLayerExactNotCaseSensitive("base");
        LayerDefinition cbgLayer = (LayerDefinition) (myModelRoot.ABM.allData.all_Layers.get(cbgLayerIndex));
        LayerDefinition baseLayer = (LayerDefinition) (myModelRoot.ABM.allData.all_Layers.get(baseLayerIndex));
        for (int r = 0; r < regions.size(); r++) {
            for (int c = 0; c < regions.get(r).cBGsIDsInvolved.size(); c++) {
                for (int i = 0; i < cbgLayer.values.length; i++) {
                    if (regions.get(r).cBGsIDsInvolved.get(c) == cbgLayer.values[i]) {
                        for (int m = 0; m < myModelRoot.ABM.allData.all_Nodes.length; m++) {
                            if (((short[]) (myModelRoot.ABM.allData.all_Nodes[m].layers.get(cbgLayerIndex)))[0] - 1 == i) {
                                regions.get(r).locationNodes.add(myModelRoot.ABM.allData.all_Nodes[m]);
                                //ADD FREQS HERE!
                                int nodeType = ((short[]) (myModelRoot.ABM.allData.all_Nodes[m].layers.get(baseLayerIndex)))[0] - 1;
                                String nodeTypeStr = baseLayer.categories[nodeType];
                                switch (nodeTypeStr) {
                                    case "motorway":
                                        regions.get(r).locationNodeHomeFreqs.add(1);
                                        regions.get(r).locationNodeWorkFreqs.add(5);
                                        regions.get(r).sumHomeFreqs = regions.get(r).sumHomeFreqs + 1;
                                        regions.get(r).sumWorkFreqs = regions.get(r).sumWorkFreqs + 5;
                                        break;
                                    case "trunk":
                                        regions.get(r).locationNodeHomeFreqs.add(2);
                                        regions.get(r).locationNodeWorkFreqs.add(8);
                                        regions.get(r).sumHomeFreqs = regions.get(r).sumHomeFreqs + 2;
                                        regions.get(r).sumWorkFreqs = regions.get(r).sumWorkFreqs + 8;
                                        break;
                                    case "primary":
                                        regions.get(r).locationNodeHomeFreqs.add(4);
                                        regions.get(r).locationNodeWorkFreqs.add(20);
                                        regions.get(r).sumHomeFreqs = regions.get(r).sumHomeFreqs + 4;
                                        regions.get(r).sumWorkFreqs = regions.get(r).sumWorkFreqs + 20;
                                        break;
                                    case "secondary":
                                        regions.get(r).locationNodeHomeFreqs.add(5);
                                        regions.get(r).locationNodeWorkFreqs.add(20);
                                        regions.get(r).sumHomeFreqs = regions.get(r).sumHomeFreqs + 5;
                                        regions.get(r).sumWorkFreqs = regions.get(r).sumWorkFreqs + 20;
                                        break;
                                    case "tertiary":
                                        regions.get(r).locationNodeHomeFreqs.add(5);
                                        regions.get(r).locationNodeWorkFreqs.add(18);
                                        regions.get(r).sumHomeFreqs = regions.get(r).sumHomeFreqs + 5;
                                        regions.get(r).sumWorkFreqs = regions.get(r).sumWorkFreqs + 18;
                                        break;
                                    case "unclassified":
                                        regions.get(r).locationNodeHomeFreqs.add(3);
                                        regions.get(r).locationNodeWorkFreqs.add(6);
                                        regions.get(r).sumHomeFreqs = regions.get(r).sumHomeFreqs + 3;
                                        regions.get(r).sumWorkFreqs = regions.get(r).sumWorkFreqs + 6;
                                        break;
                                    case "residential":
                                        regions.get(r).locationNodeHomeFreqs.add(60);
                                        regions.get(r).locationNodeWorkFreqs.add(5);
                                        regions.get(r).sumHomeFreqs = regions.get(r).sumHomeFreqs + 60;
                                        regions.get(r).sumWorkFreqs = regions.get(r).sumWorkFreqs + 5;
                                        break;
                                    case "service":
                                        regions.get(r).locationNodeHomeFreqs.add(17);
                                        regions.get(r).locationNodeWorkFreqs.add(10);
                                        regions.get(r).sumHomeFreqs = regions.get(r).sumHomeFreqs + 17;
                                        regions.get(r).sumWorkFreqs = regions.get(r).sumWorkFreqs + 10;
                                        break;
                                    case "foot":
                                        regions.get(r).locationNodeHomeFreqs.add(3);
                                        regions.get(r).locationNodeWorkFreqs.add(8);
                                        regions.get(r).sumHomeFreqs = regions.get(r).sumHomeFreqs + 3;
                                        regions.get(r).sumWorkFreqs = regions.get(r).sumWorkFreqs + 8;
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void selectExactLocations(ArrayList<Person> inPeople) {
        for (int i = 0; i < inPeople.size(); i++) {
            selectExactHomeLocation(inPeople.get(i));
            selectExactWorkLocation(inPeople.get(i));
        }
    }
    
    public void selectExactHomeLocation(Person person) {
        int indexCumulativePopulation = (int) ((rnd.nextDouble() * (person.properties.homeRegion.sumHomeFreqs - 1)));
        int cumulativeVal = 0;
        for (int i = 0; i < person.properties.homeRegion.locationNodeHomeFreqs.size(); i++) {
            cumulativeVal = cumulativeVal + person.properties.homeRegion.locationNodeHomeFreqs.get(i);
            if (cumulativeVal >= indexCumulativePopulation) {
                person.exactProperties.exactHomeLocation = person.properties.homeRegion.locationNodes.get(i);
                break;
            }
        }
    }
    
    public void selectExactWorkLocation(Person person) {
        int indexCumulativePopulation = (int) ((rnd.nextDouble() * (person.properties.workRegion.sumWorkFreqs - 1)));
        int cumulativeVal = 0;
        for (int i = 0; i < person.properties.workRegion.locationNodeWorkFreqs.size(); i++) {
            cumulativeVal = cumulativeVal + person.properties.workRegion.locationNodeWorkFreqs.get(i);
            if (cumulativeVal >= indexCumulativePopulation) {
                person.exactProperties.exactWorkLocation = person.properties.workRegion.locationNodes.get(i);
                break;
            }
        }
    }
    
    ArrayList makeByVDTessellationCBG(MainModel modelRoot, int tessellationIndex) {
        ArrayList<TessellationCell> vDsListRaw = modelRoot.getSafegraph().getVDTessellationFromCaseStudy(modelRoot.getABM().getStudyScopeGeography(), tessellationIndex);
        ArrayList regionsList = new ArrayList();
        Scope scope = (Scope) (modelRoot.ABM.studyScopeGeography);
        for (int i = 0; i < vDsListRaw.size(); i++) {
            Region region = new Region(i);
            region.lat = vDsListRaw.get(i).getLat();
            region.lon = vDsListRaw.get(i).getLon();
            
            region.population = vDsListRaw.get(i).population;
            region.workPopulation = 0;
            region.cBGsIDsInvolved = vDsListRaw.get(i).cBGsIDsInvolved;
            region.cBGsInvolved = vDsListRaw.get(i).cBGsInvolved;
            region.cBGsPercentageInvolved = vDsListRaw.get(i).cBGsPercentageInvolved;

//            region.polygons.add(scope.vDPolygons.get(vDsListRaw.get(i).myIndex));
            regionsList.add(region);
        }
        cBGRegionsLayer = scope.tessellations.get(tessellationIndex).regionImageLayer;
        return regionsList;
    }
    
    public int findLayerExactNotCaseSensitive(String layerName) {
        for (int i = 0; i < allDataGIS.all_Layers.size(); i++) {
            if (((LayerDefinition) allDataGIS.all_Layers.get(i)).layerName.toLowerCase().equals(layerName.toLowerCase())) {
                return i;
            }
        }
        System.out.println("Layer not found!: " + layerName);
        return -1;
    }
    
    public double getLayerValue(LocationNode node, int layerIndex) {
        double output = Double.NEGATIVE_INFINITY;
        if (((LayerDefinition) allDataGIS.all_Layers.get(layerIndex)) instanceof NumericLayer) {
            if (node.layers == null) {
                System.out.println(node.id);
            }
            output = (double) node.layers.get(layerIndex);
        } else {
            output = ((LayerDefinition) allDataGIS.all_Layers.get(layerIndex)).values[((short[]) node.layers.get(layerIndex))[0] - 1];
        }
        return output;
    }
    
    public void writeScheduleSimilarityArtifitialDebug(ArrayList<ScheduleListExact> regionAvgs, ArrayList<Person> people) {
        ArrayList<String[]> rows = new ArrayList();
        String[] header = new String[regionAvgs.get(0).fromHomeFreqs.size() + 1];
        header[0] = "Region index";
        for (int i = 0; i < regionAvgs.get(0).fromHomeFreqs.size(); i++) {
            header[i + 1] = "POI " + i;
        }
        rows.add(header);
        for (int i = 0; i < regionAvgs.size(); i++) {
            String[] row = new String[regionAvgs.get(i).fromHomeFreqs.size() + 1];
            row[0] = String.valueOf(i);
            for (int j = 0; j < regionAvgs.get(i).fromHomeFreqs.size(); j++) {
                row[j + 1] = String.valueOf(regionAvgs.get(i).fromHomeFreqs.get(j));
            }
            rows.add(row);
        }
        CsvWriter writer = new CsvWriter();
        try {
            writer.write(new File("testRegionAvgs.csv"), Charset.forName("US-ASCII"), rows);
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        
        ArrayList<PsudoRegion> psudoRegions = new ArrayList(regions.size());
        for (int i = 0; i < regions.size(); i++) {
            psudoRegions.add(new PsudoRegion());
        }
        for (int i = 0; i < peopleNoTessellation.size(); i++) {
            int cellIndexHome = regionsLayer.getCellOfLatLon(peopleNoTessellation.get(i).exactProperties.exactHomeLocation.lat, peopleNoTessellation.get(i).exactProperties.exactHomeLocation.lon);
            if (cellIndexHome != -1) {
                psudoRegions.get(cellIndexHome).people.add(peopleNoTessellation.get(i));
            }
        }
        
        rows = new ArrayList();
        header = new String[people.get(0).exactProperties.fromHomeFreqs.size() + 2];
        header[0] = "Region index";
        header[1] = "Person index";
        for (int i = 0; i < people.get(0).exactProperties.fromHomeFreqs.size(); i++) {
            header[i + 2] = "POI " + i;
        }
        rows.add(header);
        for (int i = 0; i < psudoRegions.size(); i++) {
            for (int j = 0; j < psudoRegions.get(i).people.size(); j++) {
                String[] row = new String[regionAvgs.get(i).fromHomeFreqs.size() + 2];
                row[0] = String.valueOf(i);
                row[1] = String.valueOf(j);
                for (int k = 0; k < psudoRegions.get(i).people.get(j).exactProperties.fromHomeFreqs.size(); k++) {
                    row[k + 2] = String.valueOf(psudoRegions.get(i).people.get(j).exactProperties.fromHomeFreqs.get(k));
                }
                rows.add(row);
            }
            
        }
        writer = new CsvWriter();
        try {
            writer.write(new File("testRegionPeople.csv"), Charset.forName("US-ASCII"), rows);
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void writeScheduleSimilarityArtifitial(String filePath) {
        ArrayList<String[]> rows = new ArrayList();
        String[] header = new String[5];
        header[0] = "Region index";
        header[1] = "Diff from home";
        header[2] = "Diff from work";
        header[3] = "is home region found";
        header[4] = "is work region found";
        rows.add(header);
        for (int i = 0; i < sumHomeScheduleDifferences.size(); i++) {
            String[] row = new String[5];
            row[0] = String.valueOf(i);
            row[1] = String.valueOf(sumHomeScheduleDifferences.get(i));
            row[2] = String.valueOf(sumWorkScheduleDifferences.get(i));
            row[3] = String.valueOf(isFoundHomeRegion.get(i));
            row[4] = String.valueOf(isFoundWorkRegion.get(i));
            rows.add(row);
        }
        CsvWriter writer = new CsvWriter();
        try {
            writer.write(new File(filePath + ".csv"), Charset.forName("US-ASCII"), rows);
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        
        rows = new ArrayList();
        header = new String[2];
        header[0] = "From home";
        header[1] = "From work";
        rows.add(header);
        int numH = 0;
        int numW = 0;
        double sumH = 0;
        double sumW = 0;
        for (int i = 0; i < sumHomeScheduleDifferences.size(); i++) {
            if (isFoundHomeRegion.get(i) == 1) {
                numH = numH + 1;
            }
            if (isFoundWorkRegion.get(i) == 1) {
                numW = numW + 1;
            }
            sumH = sumH + sumHomeScheduleDifferences.get(i);
            sumW = sumW + sumWorkScheduleDifferences.get(i);
        }
        String[] row = new String[2];
        row[0] = String.valueOf(sumH / numH);
        row[1] = String.valueOf(sumW / numW);
        rows.add(row);
        writer = new CsvWriter();
        try {
            writer.write(new File(filePath + "_avg.csv"), Charset.forName("US-ASCII"), rows);
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void runXmeans(String filePath, int minC, int maxC) {
        try {
            scheduleListExactArrayClustering = new ArrayList();
            prepareClusteringData();
            
            XMeans xMeans = new XMeans();
            xMeans.setMaxNumClusters(maxC);
            xMeans.setMinNumClusters(minC);
            xMeans.setUseKDTree(false);
            xMeans.setMaxIterations(200);
            xMeans.setMaxKMeans(2000);
            xMeans.setMaxKMeansForChildren(2000);
            xMeans.setCutOffFactor(0.5);
            xMeans.buildClusterer(m_Instances);
            
            ClusterEvaluation eval = new ClusterEvaluation();
            eval.setClusterer(xMeans);
            eval.evaluateClusterer(m_Instances);
            
            double[] assignments = eval.getClusterAssignments();
            
            System.out.println("Finished Clustering");
            ArrayList<Person> activePeople;
            if (isTessellationBuilt == true) {
                activePeople = peopleNoTessellation;
            } else {
                activePeople = people;
            }
            ArrayList<Integer> avgCounterArray = new ArrayList();
            for (int i = 0; i < eval.getNumClusters(); i++) {
                ScheduleListExact scheduleListExact = new ScheduleListExact();
                scheduleListExact.regionIndex = i;
                for (int m = 0; m < activePeople.get(0).exactProperties.pOIs.size(); m++) {
                    scheduleListExact.fromHomeFreqs.add(0d);
                    scheduleListExact.fromWorkFreqs.add(0d);
                }
                avgCounterArray.add(0);
                scheduleListExact.pOIs = activePeople.get(0).exactProperties.pOIs;
                scheduleListExactArrayClustering.add(scheduleListExact);
            }
            
            for (int i = 0; i < activePeople.size(); i++) {
                int cellIndexHome = (int) (assignments[i]);
                if (cellIndexHome != -1) {
                    for (int k = 0; k < activePeople.get(i).exactProperties.fromHomeFreqs.size(); k++) {
                        Double newValue = scheduleListExactArrayClustering.get(cellIndexHome).fromHomeFreqs.get(k) + activePeople.get(i).exactProperties.fromHomeFreqs.get(k);
                        scheduleListExactArrayClustering.get(cellIndexHome).fromHomeFreqs.set(k, newValue);
                    }
                    avgCounterArray.set(cellIndexHome, avgCounterArray.get(cellIndexHome) + 1);
                }
                Instance inst = prepareOneInstance(activePeople.get(i).exactProperties.exactWorkLocation.lat, activePeople.get(i).exactProperties.exactWorkLocation.lon);
                double[] distOfCluster = xMeans.distributionForInstance(inst);
                int cellIndexWork = -1;
                double maxVal = Double.NEGATIVE_INFINITY;
                for (int h = 0; h < distOfCluster.length; h++) {
                    if (distOfCluster[h] > maxVal) {
                        maxVal = distOfCluster[h];
                        cellIndexWork = h;
                    }
                }
                if (cellIndexWork != -1) {
                    for (int k = 0; k < activePeople.get(i).exactProperties.fromWorkFreqs.size(); k++) {
                        Double newValue = scheduleListExactArrayClustering.get(cellIndexWork).fromWorkFreqs.get(k) + activePeople.get(i).exactProperties.fromWorkFreqs.get(k);
                        scheduleListExactArrayClustering.get(cellIndexWork).fromWorkFreqs.set(k, newValue);
                    }
                    avgCounterArray.set(cellIndexWork, avgCounterArray.get(cellIndexWork) + 1);
                }
            }
            
            for (int i = 0; i < eval.getNumClusters(); i++) {
                for (int j = 0; j < scheduleListExactArrayClustering.get(i).fromHomeFreqs.size(); j++) {
                    scheduleListExactArrayClustering.get(i).fromHomeFreqs.set(j, scheduleListExactArrayClustering.get(i).fromHomeFreqs.get(j) / avgCounterArray.get(i));
                }
                for (int j = 0; j < scheduleListExactArrayClustering.get(i).fromWorkFreqs.size(); j++) {
                    scheduleListExactArrayClustering.get(i).fromWorkFreqs.set(j, scheduleListExactArrayClustering.get(i).fromWorkFreqs.get(j) / avgCounterArray.get(i));
                }
            }
            
            sumHomeScheduleDifferencesClustering = new ArrayList<Double>(Collections.nCopies(eval.getNumClusters(), 0.0));
            sumWorkScheduleDifferencesClustering = new ArrayList<Double>(Collections.nCopies(eval.getNumClusters(), 0.0));
            
            ArrayList<PsudoRegion> psudoHomeRegions = new ArrayList(eval.getNumClusters());
            for (int i = 0; i < eval.getNumClusters(); i++) {
                psudoHomeRegions.add(new PsudoRegion());
            }
            for (int i = 0; i < activePeople.size(); i++) {
                int cellIndexHome = (int) (assignments[i]);
                if (cellIndexHome != -1) {
                    psudoHomeRegions.get(cellIndexHome).people.add(activePeople.get(i));
                    isFoundHomeRegion.set(cellIndexHome, 1);
                }
            }
            
            ArrayList<PsudoRegion> psudoWorkRegions = new ArrayList(eval.getNumClusters());
            for (int i = 0; i < eval.getNumClusters(); i++) {
                psudoWorkRegions.add(new PsudoRegion());
            }
            for (int i = 0; i < activePeople.size(); i++) {
                Instance inst = prepareOneInstance(activePeople.get(i).exactProperties.exactWorkLocation.lat, activePeople.get(i).exactProperties.exactWorkLocation.lon);
                double[] distOfCluster = xMeans.distributionForInstance(inst);
                int cellIndexWork = -1;
                double maxVal = Double.NEGATIVE_INFINITY;
                for (int h = 0; h < distOfCluster.length; h++) {
                    if (distOfCluster[h] > maxVal) {
                        maxVal = distOfCluster[h];
                        cellIndexWork = h;
                    }
                }
                if (cellIndexWork != -1) {
                    psudoWorkRegions.get(cellIndexWork).people.add(activePeople.get(i));
                    isFoundWorkRegion.set(cellIndexWork, 1);
                }
            }
            
            for (int i = 0; i < psudoHomeRegions.size(); i++) {
                double varReg = 0;
                for (int k = 0; k < scheduleListExactArrayClustering.get(i).fromHomeFreqs.size(); k++) {
                    double varPOI = 0;
                    for (int j = 0; j < psudoHomeRegions.get(i).people.size(); j++) {
                        varPOI = varPOI + Math.pow(scheduleListExactArrayClustering.get(i).fromHomeFreqs.get(k) - psudoHomeRegions.get(i).people.get(j).exactProperties.fromHomeFreqs.get(k), 2);
                    }
                    varPOI = varPOI / avgCounterArray.get(i);
                    varReg = varReg + varPOI;
                }
                if (Double.isNaN(varReg) == true) {
                    varReg = 0;
                }
                sumHomeScheduleDifferencesClustering.set(i, varReg);
            }
            
            for (int i = 0; i < psudoWorkRegions.size(); i++) {
                double varReg = 0;
                for (int k = 0; k < scheduleListExactArrayClustering.get(i).fromWorkFreqs.size(); k++) {
                    double varPOI = 0;
                    for (int j = 0; j < psudoWorkRegions.get(i).people.size(); j++) {
                        varPOI = varPOI + Math.pow(scheduleListExactArrayClustering.get(i).fromWorkFreqs.get(k) - psudoWorkRegions.get(i).people.get(j).exactProperties.fromWorkFreqs.get(k), 2);
                    }
                    varPOI = varPOI / avgCounterArray.get(i);
                    varReg = varReg + varPOI;
                }
                if (Double.isNaN(varReg) == true) {
                    varReg = 0;
                }
                sumWorkScheduleDifferencesClustering.set(i, varReg);
            }
            
            writeScheduleSimilarityArtificialClustering(filePath+File.separator+"Xmeans");
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void runClusterers(String filePath) {
        for (int i = 0; i < clustererManager.activeClustererNames.size(); i++) {
            if (clustererManager.activeClustererNames.get(i).clustererName.equals("xmeans")) {
                runXmeans(filePath,clustererManager.activeClustererNames.get(i).minCluster,clustererManager.activeClustererNames.get(i).maxCluster);
            }
        }
    }
    
    public void writeScheduleSimilarityArtificialClustering(String filePath) {
        ArrayList<String[]> rows = new ArrayList();
        String[] header = new String[5];
        header[0] = "Region index";
        header[1] = "Diff from home";
        header[2] = "Diff from work";
        header[3] = "is home region found";
        header[4] = "is work region found";
        rows.add(header);
        for (int i = 0; i < sumHomeScheduleDifferencesClustering.size(); i++) {
            String[] row = new String[5];
            row[0] = String.valueOf(i);
            row[1] = String.valueOf(sumHomeScheduleDifferencesClustering.get(i));
            row[2] = String.valueOf(sumWorkScheduleDifferencesClustering.get(i));
            row[3] = String.valueOf(isFoundHomeRegion.get(i));
            row[4] = String.valueOf(isFoundWorkRegion.get(i));
            rows.add(row);
        }
        CsvWriter writer = new CsvWriter();
        try {
            writer.write(new File(filePath + ".csv"), Charset.forName("US-ASCII"), rows);
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        
        rows = new ArrayList();
        header = new String[2];
        header[0] = "From home";
        header[1] = "From work";
        rows.add(header);
        int numH = 0;
        int numW = 0;
        double sumH = 0;
        double sumW = 0;
        for (int i = 0; i < sumHomeScheduleDifferencesClustering.size(); i++) {
            if (isFoundHomeRegion.get(i) == 1) {
                numH = numH + 1;
            }
            if (isFoundWorkRegion.get(i) == 1) {
                numW = numW + 1;
            }
            sumH = sumH + sumHomeScheduleDifferencesClustering.get(i);
            sumW = sumW + sumWorkScheduleDifferencesClustering.get(i);
        }
        String[] row = new String[2];
        row[0] = String.valueOf(sumH / numH);
        row[1] = String.valueOf(sumW / numW);
        rows.add(row);
        writer = new CsvWriter();
        try {
            writer.write(new File(filePath + "_avg.csv"), Charset.forName("US-ASCII"), rows);
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void prepareClusteringData() {
        StringBuilder arffData = new StringBuilder();
        arffData.append("@RELATION data" + "\n");
//        arffData.append("@ATTRIBUTE ").append("lat").append(" numeric").append("\n");
//        arffData.append("@ATTRIBUTE ").append("lon").append(" numeric").append("\n");
        //System.out.println("header: "+myParent.baseDataDetails.headers.length);
//        for (int i = 0; i < headers.length - 1; i++) {
        arffData.append("@ATTRIBUTE ").append("lat").append(" numeric").append("\n");
        arffData.append("@ATTRIBUTE ").append("lon").append(" numeric").append("\n");
//        }
        arffData.append("@DATA").append("\n");
        ArrayList<Person> activePeople;
        if (isTessellationBuilt == true) {
            activePeople = peopleNoTessellation;
        } else {
            activePeople = people;
        }
        for (int i = 0; i < activePeople.size(); i++) {
            arffData.append(activePeople.get(i).exactProperties.exactHomeLocation.lat);
            arffData.append(",");
            arffData.append(activePeople.get(i).exactProperties.exactHomeLocation.lon);
            arffData.append("\n");
        }
        
        String str = arffData.toString();
        InputStream is = new ByteArrayInputStream(str.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        System.out.println("DATA READY!");
        ArffReader arff;
        try {
            arff = new ArffReader(br);
            m_Instances = arff.getData();
//            for (int i = 0; i < m_Instances.numInstances(); i++) {
//                m_Instances.instance(i).setWeight(Double.parseDouble(data[i][2]));//IMPIMPIMPIMPIMP
////                System.out.println(data[i][2]);
//            }
//            System.out.println("DATA READY!");
        } catch (IOException | NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public Instance prepareOneInstance(double lat, double lon) {
        StringBuilder arffData = new StringBuilder();
        arffData.append("@RELATION data" + "\n");
//        arffData.append("@ATTRIBUTE ").append("lat").append(" numeric").append("\n");
//        arffData.append("@ATTRIBUTE ").append("lon").append(" numeric").append("\n");
        //System.out.println("header: "+myParent.baseDataDetails.headers.length);
//        for (int i = 0; i < headers.length - 1; i++) {
        arffData.append("@ATTRIBUTE ").append("lat").append(" numeric").append("\n");
        arffData.append("@ATTRIBUTE ").append("lon").append(" numeric").append("\n");
//        }
        arffData.append("@DATA").append("\n");
        
        arffData.append(lat);
        arffData.append(",");
        arffData.append(lon);
        arffData.append("\n");
        
        String str = arffData.toString();
        InputStream is = new ByteArrayInputStream(str.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

//        System.out.println("DATA READY!");
        ArffReader arff;
        try {
            arff = new ArffReader(br);
            Instances instances = arff.getData();
            return instances.get(0);
        } catch (IOException | NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    private class PsudoRegion {
        
        public ArrayList<Person> people = new ArrayList();
    }
    
}
