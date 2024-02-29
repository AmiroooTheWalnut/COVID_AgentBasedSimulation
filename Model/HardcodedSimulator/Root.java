/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer;
import static COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog.isShop;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.Agent;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.County;
import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.DailyConfirmedCases;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController.endDay;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController.startDay;
//import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController.updateHour;
import COVID_AgentBasedSimulation.Model.Structure.CBGVDCell;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Scope;
import COVID_AgentBasedSimulation.Model.Structure.TessellationCell;
import COVID_AgentBasedSimulation.Model.Structure.VDCell;
import com.opencsv.CSVWriter;
import de.siegmar.fastcsv.writer.CsvWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
//import java.util.Random;
import java.util.SplittableRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class Root extends Agent {

    public int numTravels;
    public int numRealTravels;
    public int numContacts;

    public double pTSFraction;//USED FOR FUZZY STATUS

    public ShamilSimulatorController shamilSimulatorController = new ShamilSimulatorController();

    public enum statusEnum {
        SUSCEPTIBLE, INFECTED_SYM, INFECTED_ASYM, RECOVERED, DEAD;
    }

    public SplittableRandom rnd = new SplittableRandom(System.currentTimeMillis());
//    public Random rnd = new Random(1);

    Root currentAgent = this;

    public ArrayList<String[]> infectionPoll = new ArrayList();
    public ArrayList<String[]> mobilityPoll = new ArrayList();

    public ArrayList<Person> people = new ArrayList();
    public ArrayList<Person> travelers = new ArrayList();

    public int residentPopulation;
    public int nonResidentPopulation;
    public int startCountyIndex;
    public int endCountyIndex;

    ArrayList<DailyConfirmedCases> relevantDailyConfirmedCases;

    public ArrayList<Region> cBGregions;
    public ArrayList<Region> regions;
    public RegionImageLayer regionsLayer = new RegionImageLayer();
    public int sumRegionsPopulation = 0;
    public LinkedHashMap<String, POI> pOIs;

    public int counter;
    public int numAgents;

    public boolean isLocalAllowed = true;
    public int agentPairContact[][];
    public String regionType;

    public ArrayList<ScheduleListExact> scheduleListArray;
    public ArrayList<ScheduleListExact> scheduleListArrayClustering;

    public float[] sumHomeScheduleDifferences;
    public float[] sumHomeScheduleDifferencesClustering;
    public float[] sumWorkScheduleDifferences;
    public float[] sumWorkScheduleDifferencesClustering;

    public LinkedHashMap<String, Long> travelsToAllPOIsFreqs = new LinkedHashMap();

//    ShamilSimulatorController shamilSimulatorController;
    public Root(MainModel modelRoot) {
        myType = "root";
    }

    /**
     * The initialization of the simulation starts from this function.
     *
     * @param modelRoot
     * @param passed_numAgents The number of agents to generate
     * @param passed_regionType The type of the regions to generate: "CBG",
     * "VD", "CBGVD" or "RANDOM". The "RANDOM" option requires explicit number
     * of regions.
     * @param passed_numRandomRegions The number of regions which is used only
     * when "RANDOM" type is selected for regions.
     * @param isCompleteInfection
     * @param isInfectCBGOnly
     * @param initialInfectionRegionIndex
     */
    public void constructor(MainModel modelRoot, int passed_numAgents, String passed_regionType, int passed_numRandomRegions, boolean isCompleteInfection, boolean isInfectCBGOnly, ArrayList<Integer> initialInfectionRegionIndex, int fixedNumInfected) {
        myModelRoot = modelRoot;
        regionType = passed_regionType;

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

        generateRegions(modelRoot, passed_regionType, passed_numRandomRegions);
//        if (isInfectCBGOnly == true) {
        int cBGTessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "CBG");
        cBGregions = makeByVDTessellation(modelRoot, cBGTessellationIndex, false);
//        }

        generateSchedules(modelRoot, passed_regionType, regions);
        generateAgents(modelRoot, passed_numAgents);
        if (modelRoot.ABM.isReportContactRate == true) {
            agentPairContact = new int[people.size()][people.size()];
        }
        if (modelRoot.ABM.isShamilABMActive == true) {
            if (modelRoot.ABM.isOurABMActive == true) {
                ShamilSimulatorController.shamilAgentGenerationSpatial(modelRoot, regions, people);
                initiallyInfect(isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
//                initiallyInfectDummy();
                reportConsoleOurABMInfection(true);
                for (int i = 0; i < people.size(); i++) {
                    people.get(i).isActive = true;
                }
            } else {
                ShamilSimulatorController.shamilAgentGeneration(modelRoot, people);
                ShamilSimulatorController.shamilInitialInfection(modelRoot, people);
            }
        } else {
            if (modelRoot.ABM.isOurABMActive == true) {
                initiallyInfect(isCompleteInfection, isInfectCBGOnly, initialInfectionRegionIndex, fixedNumInfected);
//                initiallyInfectDummy();
                reportConsoleOurABMInfection(true);
                for (int i = 0; i < people.size(); i++) {
                    people.get(i).isActive = true;
                }
            }
        }
    }

    public void generateRegions(MainModel modelRoot, String type, int n) {
        int tessellationIndex = -1;
        switch (type) {
            case "CBG":
//                regions = makeByCBGs(modelRoot);
                tessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "CBG");
                regions = makeByVDTessellation(modelRoot, tessellationIndex, true);
                break;
            case "VDFMTH":
//                regions = makeByVDs(modelRoot);
                tessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "VDFMTH");
                regions = makeByVDTessellation(modelRoot, tessellationIndex, true);
                break;
            case "CBGVDFMTH":
//                regions = makeByCBGVDs(modelRoot);
                tessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "CBGVDFMTH");
                regions = makeByVDTessellation(modelRoot, tessellationIndex, true);
                break;
            case "VD_CBG":
//                regions = makeByCBGVDs(modelRoot);
                tessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "VD_CBG_num_cells");
                regions = makeByVDTessellation(modelRoot, tessellationIndex, true);
                break;
            case "VD_CBGVD":
//                regions = makeByCBGVDs(modelRoot);
                tessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "VD_CBGVD_num_cells");
                regions = makeByVDTessellation(modelRoot, tessellationIndex, true);
                break;
            case "RMCBG":
                regions = makeByRandomCBGs(modelRoot, n);
//                tessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "RMCBG_"+n);
//                regions=makeByVDTessellation(modelRoot, tessellationIndex);
                break;
            case "VDFNC":
//                regions = makeByRandomCBGs(modelRoot, n);
                tessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "VDFNC_" + n);
                regions = makeByVDTessellation(modelRoot, tessellationIndex, true);
                break;
            case "AVDFMTH":
//                regions = makeByRandomCBGs(modelRoot, n);
                tessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "VDFMTH");
                regions = makeByVDTessellation(modelRoot, tessellationIndex, true);
                break;
            default:

        }
    }

    public static int getTessellationLayerIndex(Scope scope, String scenarioName) {
        for (int i = 0; i < scope.tessellations.size(); i++) {
            if (scope.tessellations.get(i).scenarioName.toLowerCase().equals(scenarioName.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    //COULD BE REMOVED AND PUSHED INTO PREPROCESSING SECTION. NOW IT'S TRANSIENT.
    public void calcCumulativeMonthWeekHour(PatternsRecordProcessed record) {
        record.sumVisitsByDayOfMonth = 0;
        for (int i = 0; i < record.visits_by_day.length; i++) {
            record.sumVisitsByDayOfMonth += record.visits_by_day[i];
        }
        record.sumVisitsByDayOfWeek = 0;
        for (byte i = 0; i < 7; i++) {
            record.sumVisitsByDayOfWeek += record.popularity_by_day.get(i);
        }
        record.sumVisitsByHourofDay = 0;
        for (byte i = 0; i < record.popularity_by_hour.length; i++) {
            record.sumVisitsByHourofDay += record.popularity_by_hour[i];
        }
        record.sumDwellTime = 0;
        for (byte i = 0; i < record.bucketed_dwell_times.size(); i++) {
            record.sumDwellTime += record.bucketed_dwell_times.get(i).number;
        }
    }

    public void generateRegionSchedules(MainModel modelRoot, ArrayList<Region> regions, ArrayList<PatternsRecordProcessed> patternRecords, String type) {
        for (int j = 0; j < patternRecords.size(); j++) {
            if (type.equals("AVDFMTH")) {
                if (!(isFoodAndGrocery(patternRecords.get(j).place.naics_code) || isReligiousOrganization(patternRecords.get(j).place.naics_code) || isSchool(patternRecords.get(j).place.naics_code))) {
                    continue;
                }
            }
            calcCumulativeMonthWeekHour(patternRecords.get(j));// COULD BE AVOIDED IF ALL DATA BE PREPROCESSED AND TRANSIENTS ARE REMOVED

            if (patternRecords.get(j).visitor_home_cbgs_place != null) {
                for (int k = 0; k < patternRecords.get(j).visitor_home_cbgs_place.size(); k++) {
                    if (isLocalAllowed == false) {
                        int naics_code = patternRecords.get(j).place.naics_code;
                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
                            ArrayList<Region> selectedSourceRegions = new ArrayList();
                            ArrayList<Float> selectSourceRegionsPercentages = new ArrayList();
                            for (int o = 0; o < regions.size(); o++) {
                                for (int p = 0; p < regions.get(o).cBGsInvolved.size(); p++) {
                                    if (regions.get(o).cBGsInvolved.get(p).id == patternRecords.get(j).visitor_home_cbgs_place.get(k).key.id) {
                                        selectedSourceRegions.add(regions.get(o));
                                        selectSourceRegionsPercentages.add(regions.get(o).cBGsPercentageInvolved.get(p).floatValue());
                                        //CAN'T BREAK HERE BECAUSE THERE IS A CHANCE THAT THE PLACE'S CBG IS REPEATED IN ANOTHER REGION
                                    }
                                }
                            }
                            for (int u = 0; u < selectedSourceRegions.size(); u++) {
                                selectedSourceRegions.get(u).scheduleList.originalDestinations.add(patternRecords.get(j));
                                double freq = patternRecords.get(j).visitor_home_cbgs_place.get(k).value * selectSourceRegionsPercentages.get(u) * ((double) (patternRecords.get(j).raw_visit_counts) / (double) (patternRecords.get(j).raw_visitor_counts));
                                selectedSourceRegions.get(u).scheduleList.originalFrequencies.add(freq);
                                selectedSourceRegions.get(u).scheduleList.originalSumFrequencies += freq;
                            }
                        }
                    } else {
                        ArrayList<Region> selectedSourceRegions = new ArrayList();
                        ArrayList<Float> selectSourceRegionsPercentages = new ArrayList();
                        for (int o = 0; o < regions.size(); o++) {
                            for (int p = 0; p < regions.get(o).cBGsInvolved.size(); p++) {
                                if (regions.get(o).cBGsInvolved.get(p).id == patternRecords.get(j).visitor_home_cbgs_place.get(k).key.id) {
                                    selectedSourceRegions.add(regions.get(o));
                                    selectSourceRegionsPercentages.add(regions.get(o).cBGsPercentageInvolved.get(p).floatValue());
                                    //CAN'T BREAK HERE BECAUSE THERE IS A CHANCE THAT THE PLACE'S CBG IS REPEATED IN ANOTHER REGION
                                }
                            }
                        }
                        for (int u = 0; u < selectedSourceRegions.size(); u++) {
                            selectedSourceRegions.get(u).scheduleList.originalDestinations.add(patternRecords.get(j));
                            double freq = patternRecords.get(j).visitor_home_cbgs_place.get(k).value * selectSourceRegionsPercentages.get(u) * ((double) (patternRecords.get(j).raw_visit_counts) / (double) (patternRecords.get(j).raw_visitor_counts));
                            selectedSourceRegions.get(u).scheduleList.originalFrequencies.add(freq);
                            selectedSourceRegions.get(u).scheduleList.originalSumFrequencies += freq;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < regions.size(); i++) {
            ScheduleList scheduleList = regions.get(i).scheduleList;
            for (int k = 0; k < scheduleList.originalDestinations.size(); k++) {
                if (scheduleList.originalDestinations.get(k).visitor_daytime_cbgs_place != null) {
                    for (int j = 0; j < scheduleList.originalDestinations.get(k).visitor_daytime_cbgs_place.size(); j++) {
                        regions.get(i).workPopulation = regions.get(i).workPopulation + scheduleList.originalDestinations.get(k).visitor_daytime_cbgs_place.get(j).value * ((double) (scheduleList.originalDestinations.get(k).raw_visit_counts) / (double) (scheduleList.originalDestinations.get(k).raw_visitor_counts));
                    }
                }
            }
        }
    }

    public void generateSchedules(MainModel modelRoot, String type, ArrayList<Region> regions) {
        ArrayList<PatternsRecordProcessed> patternRecords = new ArrayList();
        if (pOIs == null) {
            pOIs = new LinkedHashMap();
        }
        for (int i = 0; i < modelRoot.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            ArrayList<PatternsRecordProcessed> patternRecordsTemp = modelRoot.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords;
            for (int j = 0; j < patternRecordsTemp.size(); j++) {
                if (isFoodAndGrocery(patternRecordsTemp.get(j).place.naics_code) == true) {//FOR NOW ONLY GROCERIES ARE IMPLEMENTED
                    patternRecords.add(patternRecordsTemp.get(j));
                    if (pOIs.containsKey(patternRecordsTemp.get(j).placeKey)) {
                        pOIs.get(patternRecordsTemp.get(j).placeKey).patternsRecord = patternRecordsTemp.get(j);
                    } else {
                        POI tempPOI = new POI();
                        tempPOI.patternsRecord = patternRecordsTemp.get(j);
                        pOIs.put(patternRecordsTemp.get(j).placeKey, tempPOI);
                    }
                } else if (isSchool(patternRecordsTemp.get(j).place.naics_code) == true) {
                    patternRecords.add(patternRecordsTemp.get(j));
                    if (pOIs.containsKey(patternRecordsTemp.get(j).placeKey)) {
                        pOIs.get(patternRecordsTemp.get(j).placeKey).patternsRecord = patternRecordsTemp.get(j);
                    } else {
                        POI tempPOI = new POI();
                        tempPOI.patternsRecord = patternRecordsTemp.get(j);
                        pOIs.put(patternRecordsTemp.get(j).placeKey, tempPOI);
                    }
                }
                else {
                    if (Math.random() > 0.9) {
                        patternRecords.add(patternRecordsTemp.get(j));
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
        }

        for (POI value : pOIs.values()) {
//            String key = mapElement.getKey();
//            POI value = mapElement.getValue();
            travelsToAllPOIsFreqs.put(value.patternsRecord.placeKey, 0L);
        }

        generateRegionSchedules(modelRoot, regions, patternRecords, type);
//        switch (type) {
//            case "CBG":
//                generateRegionSchedules(modelRoot, regions, patternRecords);
//            case "VD":
//
//            case "CBGVD":
//
//            case "RANDOM":
//
//            default:
//
//        }

    }

    public void generateAgents(MainModel modelRoot, int passed_numAgents) {
        int cumulativePopulation = 0;
        for (int i = 0; i < regions.size(); i++) {
            cumulativePopulation = cumulativePopulation + regions.get(i).population;
        }
        pTSFraction = (double) ((City) (modelRoot.ABM.studyScopeGeography)).population / (double) passed_numAgents;
        if (pTSFraction < 1) {
            System.out.println("THE NUMBER OF AGENTS IS MORE THAN REAL NUMBER OF PEOPLE!!!");
        }
        sumRegionsPopulation = cumulativePopulation;
        for (int i = 0; i < passed_numAgents; i++) {
            Person person = new Person(i);
            selectHomeRegion(person, sumRegionsPopulation, 0);
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

    public void selectHomeRegion(Person person, int cumulativePopulation, int retry) {
        int indexCumulativePopulation = (int) ((rnd.nextDouble() * (cumulativePopulation - 1)));
        int cumulativePopulationRun = 0;
        for (int j = 0; j < regions.size(); j++) {
            cumulativePopulationRun = cumulativePopulationRun + regions.get(j).population;
            if (cumulativePopulationRun > indexCumulativePopulation) {
                if (regions.get(j).scheduleList.originalDestinations.size() > 0) {
                    person.properties.homeRegion = regions.get(j);
                    regions.get(j).residents.add(person);
                    break;
                } else {
                    if (retry < 20) {
                        retry = retry + 1;
                        selectHomeRegion(person, cumulativePopulation, retry);
                        break;
                    } else {
                        System.out.println("Failed to assign a home region with a schedule!");
                    }
                }
            }
        }
    }

    public void selectWorkRegion(Person person, Region homeRegion) {
        ScheduleList scheduleList = homeRegion.scheduleList;
        Region workRegion;
        for (int i = 0; i < 30; i++) {
            workRegion = sampleWorkRegion(scheduleList, person, homeRegion);
            if (workRegion != null) {
                if (workRegion.sumWorkFreqs > 0) {
                    person.properties.workRegion = workRegion;
                    break;
                }
            }
        }
        if (person.properties.workRegion == null) {
            person.properties.workRegion = person.properties.homeRegion;
        }
        person.properties.workRegion.workers.add(person);
    }

    public Region sampleWorkRegion(ScheduleList scheduleList, Person person, Region homeRegion) {
        int indexCumulativeWorkCBG = (int) ((rnd.nextDouble() * (homeRegion.workPopulation - 1)));
        double cumulativeDayCBGPopulation = 0;
        for (int k = 0; k < scheduleList.originalDestinations.size(); k++) {
            if (scheduleList.originalDestinations.get(k).visitor_daytime_cbgs_place != null) {
                for (int j = 0; j < scheduleList.originalDestinations.get(k).visitor_daytime_cbgs_place.size(); j++) {
                    cumulativeDayCBGPopulation = cumulativeDayCBGPopulation + scheduleList.originalDestinations.get(k).visitor_daytime_cbgs_place.get(j).value * ((double) (scheduleList.originalDestinations.get(k).raw_visit_counts) / (double) (scheduleList.originalDestinations.get(k).raw_visitor_counts));
                    if (cumulativeDayCBGPopulation > indexCumulativeWorkCBG) {
                        CensusBlockGroup selectedWorkCBG = scheduleList.originalDestinations.get(k).visitor_daytime_cbgs_place.get(j).key;
                        double cumulativeCBGPercentages = 0;
                        for (int u = 0; u < regions.size(); u++) {
                            for (int y = 0; y < regions.get(u).cBGsIDsInvolved.size(); y++) {
                                if (selectedWorkCBG.id == regions.get(u).cBGsIDsInvolved.get(y)) {
                                    cumulativeCBGPercentages = cumulativeCBGPercentages + regions.get(u).cBGsPercentageInvolved.get(y);
                                }
                            }
                        }
                        double selectedPercentage = (rnd.nextDouble() * (cumulativeCBGPercentages - 0.0000001d));

                        Region selectedWorkRegion = null;
                        cumulativeCBGPercentages = 0;
                        boolean isWorkFound = false;
                        for (int u = 0; u < regions.size(); u++) {
                            for (int y = 0; y < regions.get(u).cBGsIDsInvolved.size(); y++) {
                                if (selectedWorkCBG.id == regions.get(u).cBGsIDsInvolved.get(y)) {
                                    cumulativeCBGPercentages = cumulativeCBGPercentages + regions.get(u).cBGsPercentageInvolved.get(y);
                                    if (cumulativeCBGPercentages > selectedPercentage) {
                                        selectedWorkRegion = regions.get(u);
                                        isWorkFound = true;
                                        break;
                                    }
                                }
                            }
                            if (isWorkFound == true) {
                                break;
                            }
                        }
                        if (selectedWorkRegion == null) {
//                            CensusBlockGroup cBG = ((City) (modelRoot.ABM.studyScopeGeography)).findCBG(selectedWorkCBG.id);

                        } else {
                            return selectedWorkRegion;
                        }
                    }
                }
            }
        }
        return null;
    }

    ArrayList<Region> makeByCBGs(MainModel modelRoot) {
        ArrayList<CensusBlockGroup> cBGsListRaw = modelRoot.safegraph.getCBGsFromCaseStudy(modelRoot.ABM.studyScopeGeography);
        ArrayList regionsList = new ArrayList();
        Scope scope = (Scope) (modelRoot.ABM.studyScopeGeography);
        for (int i = 0; i < cBGsListRaw.size(); i++) {
            Region region = new Region(i);
            region.lat = cBGsListRaw.get(i).getLat();
            region.lon = cBGsListRaw.get(i).getLon();

            region.population = cBGsListRaw.get(i).population;

            region.workPopulation = 0;
            region.cBGsIDsInvolved = new ArrayList();
            region.cBGsIDsInvolved.add(cBGsListRaw.get(i).id);
            region.cBGsInvolved = new ArrayList();
            region.cBGsInvolved.add(cBGsListRaw.get(i));
            region.cBGsPercentageInvolved = new ArrayList();
            region.cBGsPercentageInvolved.add(1d);
            region.polygons.add(scope.cBGPolygons.get(cBGsListRaw.get(i).id));
            regionsList.add(region);
        }
        regionsLayer = scope.cBGRegionLayer;
        return regionsList;
    }

    public ArrayList makeByVDTessellation(MainModel modelRoot, int tessellationIndex, boolean isSetRegionLayer) {
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
        if (isSetRegionLayer == true) {
            regionsLayer = scope.tessellations.get(tessellationIndex).regionImageLayer;
        }
        return regionsList;
    }

    ArrayList makeByVDs(MainModel modelRoot) {
        ArrayList<VDCell> vDsListRaw = modelRoot.getSafegraph().getVDsFromCaseStudy(modelRoot.getABM().getStudyScopeGeography());
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

            region.polygons.add(scope.vDPolygons.get(vDsListRaw.get(i).myIndex));
            regionsList.add(region);
        }
        regionsLayer = scope.vDRegionLayer;
        return regionsList;
    }

    ArrayList makeByCBGVDTessellation(MainModel modelRoot, int tessellationIndex, boolean isSetRegionLayer) {
        ArrayList<TessellationCell> cBGVDsListRaw = modelRoot.getSafegraph().getCBGVDTessellationFromCaseStudy(modelRoot.getABM().getStudyScopeGeography(), tessellationIndex);
        ArrayList regionsList = new ArrayList();
        Scope scope = (Scope) (modelRoot.ABM.studyScopeGeography);
        for (int i = 0; i < cBGVDsListRaw.size(); i++) {
            Region region = new Region(i);
//            agent.cbgVal = cBGsListRaw.get(i);
//            region.cbgvdVal = cBGsListRaw.get(i);
            region.lat = cBGVDsListRaw.get(i).getLat();
            region.lon = cBGVDsListRaw.get(i).getLon();

            region.population = cBGVDsListRaw.get(i).population;
            region.workPopulation = 0;
            region.cBGsIDsInvolved = cBGVDsListRaw.get(i).cBGsIDsInvolved;
            region.cBGsInvolved = cBGVDsListRaw.get(i).cBGsInvolved;
            region.cBGsPercentageInvolved = cBGVDsListRaw.get(i).cBGsPercentageInvolved;
            regionsList.add(region);
        }
        if (isSetRegionLayer == true) {
            regionsLayer = scope.tessellations.get(tessellationIndex).regionImageLayer;
        }
//        regionsLayer = scope.cBGVDRegionLayer;
        return regionsList;
    }

    ArrayList makeByCBGVDs(MainModel modelRoot) {
        ArrayList<CBGVDCell> cBGVDsListRaw = modelRoot.getSafegraph().getCBGVDsFromCaseStudy(modelRoot.getABM().getStudyScopeGeography());
        ArrayList regionsList = new ArrayList();
        Scope scope = (Scope) (modelRoot.ABM.studyScopeGeography);
        for (int i = 0; i < cBGVDsListRaw.size(); i++) {
            Region region = new Region(i);
//            agent.cbgVal = cBGsListRaw.get(i);
//            region.cbgvdVal = cBGsListRaw.get(i);
            region.lat = cBGVDsListRaw.get(i).getLat();
            region.lon = cBGVDsListRaw.get(i).getLon();

            region.population = cBGVDsListRaw.get(i).population;
            region.workPopulation = 0;
            region.cBGsIDsInvolved = cBGVDsListRaw.get(i).cBGsIDsInvolved;
            region.cBGsInvolved = cBGVDsListRaw.get(i).cBGsInvolved;
            region.cBGsPercentageInvolved = cBGVDsListRaw.get(i).cBGsPercentageInvolved;
            regionsList.add(region);
        }
        regionsLayer = scope.cBGVDRegionLayer;
        return regionsList;
    }

    ArrayList makeByRandomCBGs(MainModel modelRoot, int numRegions) {
        //        ArrayList<CensusBlockGroup> cBGsListRaw = modelRoot.safegraph.getCBGsFromCaseStudy(modelRoot.ABM.studyScopeGeography);

        int cBGTessellationIndex = getTessellationLayerIndex((Scope) (modelRoot.ABM.studyScopeGeography), "CBG");
        ArrayList<Region> TtempRegionsList = makeByVDTessellation(modelRoot, cBGTessellationIndex, false);

        ArrayList<CensusBlockGroup> cBGsListRaw = new ArrayList();

        for (int i = 0; i < TtempRegionsList.size(); i++) {
            cBGsListRaw.add(TtempRegionsList.get(i).cBGsInvolved.get(0));
        }

        ArrayList<Region> tempRegionsList = new ArrayList();
        Scope scope = (Scope) (modelRoot.ABM.studyScopeGeography);
        int CBGTessellation = -1;
        for (int i = 0; i < scope.tessellations.size(); i++) {
            if (scope.tessellations.get(i).scenarioName.equals("CBG")) {
                CBGTessellation = i;
            }
        }
        regionsLayer = scope.tessellations.get(CBGTessellation).regionImageLayer;
        for (int i = 0; i < cBGsListRaw.size(); i++) {
            Region tempRegion = new Region(i);
            tempRegion.lat = cBGsListRaw.get(i).getLat();
            tempRegion.lon = cBGsListRaw.get(i).getLon();

            tempRegion.population = cBGsListRaw.get(i).population;

            tempRegion.workPopulation = 0;
            tempRegion.cBGsIDsInvolved = new ArrayList();
            tempRegion.cBGsIDsInvolved.add(cBGsListRaw.get(i).id);
            tempRegion.cBGsInvolved = new ArrayList();
            tempRegion.cBGsInvolved.add(cBGsListRaw.get(i));
            tempRegion.cBGsPercentageInvolved = new ArrayList();
            tempRegion.cBGsPercentageInvolved.add(1d);
            tempRegion.polygons.add(scope.cBGPolygons.get(cBGsListRaw.get(i).id));

            tempRegionsList.add(tempRegion);
        }
        for (int i = 0; i < cBGsListRaw.size(); i++) {
            setNeighborRegions(tempRegionsList, i, regionsLayer.indexedImage);
        }
        ArrayList regionsList = new ArrayList();
        ArrayList<ArrayList<Integer>> groups = getCBGMergingIndices(cBGsListRaw, tempRegionsList, numRegions);

        int[][] indexedImage = new int[regionsLayer.indexedImage.length][regionsLayer.indexedImage[0].length];

        for (int i = 0; i < groups.size(); i++) {
            for (int a = 0; a < regionsLayer.indexedImage.length; a++) {
                for (int b = 0; b < regionsLayer.indexedImage[0].length; b++) {
                    for (int j = 0; j < groups.get(i).size(); j++) {
                        if (regionsLayer.indexedImage[a][b] == groups.get(i).get(j) + 1) {
                            indexedImage[a][b] = i + 1;
                        }
                    }
                }
            }

            Region region = new Region(i);
            region.cBGsIDsInvolved = new ArrayList();
            region.cBGsInvolved = new ArrayList();
            region.cBGsPercentageInvolved = new ArrayList();
            float avgLat = 0;
            float avgLon = 0;
            int sumPopulation = 0;
            for (int j = 0; j < groups.get(i).size(); j++) {
                int index = groups.get(i).get(j);
                avgLat += cBGsListRaw.get(index).lat;
                avgLon += cBGsListRaw.get(index).lon;
                sumPopulation = sumPopulation + cBGsListRaw.get(index).population;
                region.cBGsIDsInvolved.add(cBGsListRaw.get(index).id);
                region.cBGsInvolved.add(cBGsListRaw.get(index));
                region.cBGsPercentageInvolved.add(1d);
                region.polygons.add(scope.cBGPolygons.get(cBGsListRaw.get(index).id));
            }
            avgLat = avgLat / (float) (groups.get(i).size());
            avgLon = avgLon / (float) (groups.get(i).size());
            region.lat = avgLat;
            region.lon = avgLon;

            region.population = sumPopulation;

            region.workPopulation = 0;

            regionsList.add(region);
        }

        regionsLayer = new RegionImageLayer();
        regionsLayer.endLat = scope.tessellations.get(CBGTessellation).regionImageLayer.endLat;
        regionsLayer.endLon = scope.tessellations.get(CBGTessellation).regionImageLayer.endLon;
        regionsLayer.startLat = scope.tessellations.get(CBGTessellation).regionImageLayer.startLat;
        regionsLayer.startLon = scope.tessellations.get(CBGTessellation).regionImageLayer.startLon;
        regionsLayer.indexedImage = indexedImage;
        regionsLayer.imageBoundaries = RegionImageLayer.getImageBoundaries(indexedImage);
        regionsLayer.severities = new double[groups.size()];

//        regionsList.add(region);
        return regionsList;
    }

    public ArrayList<ArrayList<Integer>> getCBGMergingIndices(ArrayList<CensusBlockGroup> cBGsListRaw, ArrayList<Region> tempRegionsList, int numRegions) {
//        rnd.setSeed(System.currentTimeMillis());
//        rnd.setSeed(123);//FOR DEBUGGING
        ArrayList<ArrayList<Integer>> groups = new ArrayList();
        for (int m = 0; m < cBGsListRaw.size(); m++) {
            ArrayList<Integer> group = new ArrayList();
            group.add(m);
            groups.add(group);
        }
        int maxTry = 100;
        int tryCounter = 0;
        int currentNumRegions = groups.size();
        while (currentNumRegions > numRegions) {
            int selectedGroup = (int) (Math.floor(rnd.nextDouble() * groups.size()));
            int selectedRegion = (int) (Math.floor(rnd.nextDouble() * groups.get(selectedGroup).size()));
            int selectedRegionToMergeInHashMap = (int) (Math.floor(rnd.nextDouble() * tempRegionsList.get(groups.get(selectedGroup).get(selectedRegion)).neighbors.size()));
            ArrayList<Integer> keys = new ArrayList(tempRegionsList.get(groups.get(selectedGroup).get(selectedRegion)).neighbors.keySet());
            if (!keys.isEmpty()) {
                int key = keys.get(selectedRegionToMergeInHashMap);
                int selectedGroupToMerge = findGroupIndexOfRegionIndex(groups, key);
                if (selectedGroupToMerge == -1) {
                    System.out.println("SEVERE ERROR: REGION ID IS NOT FOUND IN ALL GROUPS");
                    throw (new ArithmeticException("REGION ID IS NOT FOUND IN ALL GROUPS"));
                }
                if (selectedGroupToMerge != selectedGroup) {
                    for (int i = 0; i < groups.get(selectedGroupToMerge).size(); i++) {
                        groups.get(selectedGroup).add(groups.get(selectedGroupToMerge).get(i));
                    }
                    groups.remove(selectedGroupToMerge);
                }
                currentNumRegions = groups.size();
                tryCounter = 0;
            } else {
                tryCounter = tryCounter + 1;
                if (tryCounter > maxTry) {
                    System.out.println("Failed to reach the number of regions");
                    break;
                }
            }

        }
        return groups;
    }

    public int findGroupIndexOfRegionIndex(ArrayList<ArrayList<Integer>> groups, int inputRegion) {
        for (int i = 0; i < groups.size(); i++) {
            for (int j = 0; j < groups.get(i).size(); j++) {
                if (groups.get(i).get(j) == inputRegion) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void setNeighborRegions(ArrayList<Region> regionsList, int targetRegionIndex, int[][] input) {
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                int selfValue = input[i][j];
                if (targetRegionIndex == selfValue - 1) {
                    int values[] = new int[8];
                    for (int k = 0; k < 8; k++) {
                        values[k] = -1;
                    }
                    if (i == 0) {
                        if (j == 0) {
                            values[2] = input[i + 1][j];//E
                            values[3] = input[i + 1][j + 1];//SE
                            values[4] = input[i][j + 1];//S
                        } else if (j == input[0].length - 1) {
                            values[0] = input[i][j - 1];//N
                            values[1] = input[i + 1][j - 1];//NE
                            values[2] = input[i + 1][j];//E
                        } else {
                            values[0] = input[i][j - 1];//N
                            values[1] = input[i + 1][j - 1];//NE
                            values[2] = input[i + 1][j];//E
                            values[3] = input[i + 1][j + 1];//SE
                            values[4] = input[i][j + 1];//S
                        }
                    } else if (i == input.length - 1) {
                        if (j == 0) {
                            values[4] = input[i][j + 1];//S
                            values[5] = input[i - 1][j + 1];//SW
                            values[6] = input[i - 1][j];//W
                        } else if (j == input[0].length - 1) {
                            values[6] = input[i - 1][j];//W
                            values[7] = input[i - 1][j - 1];//NW
                            values[0] = input[i][j - 1];//N
                        } else {
                            values[4] = input[i][j + 1];//S
                            values[5] = input[i - 1][j + 1];//SW
                            values[6] = input[i - 1][j];//W
                            values[7] = input[i - 1][j - 1];//NW
                            values[0] = input[i][j - 1];//N
                        }
                    } else {
                        if (j == 0) {
                            values[2] = input[i + 1][j];//E
                            values[3] = input[i + 1][j + 1];//SE
                            values[4] = input[i][j + 1];//S
                            values[5] = input[i - 1][j + 1];//SW
                            values[6] = input[i - 1][j];//W
                        } else if (j == input[0].length - 1) {
                            values[6] = input[i - 1][j];//W
                            values[7] = input[i - 1][j - 1];//NW
                            values[0] = input[i][j - 1];//N
                            values[1] = input[i + 1][j - 1];//NE
                            values[2] = input[i + 1][j];//E
                        } else {
                            values[0] = input[i][j - 1];//N
                            values[1] = input[i + 1][j - 1];//NE
                            values[2] = input[i + 1][j];//E
                            values[3] = input[i + 1][j + 1];//SE
                            values[4] = input[i][j + 1];//S
                            values[5] = input[i - 1][j + 1];//SW
                            values[6] = input[i - 1][j];//W
                            values[7] = input[i - 1][j - 1];//NW
                        }
                    }
//                    System.out.println("NOTHING!!!");
                    for (int m = 0; m < values.length; m++) {
                        if (values[m] != selfValue && values[m] > 0) {
                            regionsList.get(targetRegionIndex).neighbors.put(values[m] - 1, regionsList.get(values[m] - 1));
                        }
                    }
                }
            }
        }
    }

    public void initiallyInfectDummy() {
        Scope scope = (Scope) (myModelRoot.ABM.studyScopeGeography);
        detectRelevantCounties(scope);
        for (int m = 0; m < people.get(0).insidePeople.size(); m++) {
            people.get(0).insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
        }
//        boolean isBreak=false;
//        for(int i=0;i<regions.size();i++){
//            for(int j=0;j<regions.get(i).residents.size();j++){
//                regions.get(i).residents.get(j).properties.status = statusEnum.INFECTED_ASYM.ordinal();
//                isBreak=true;
//                break;
//            }
//            if(isBreak==true){
//                break;
//            }
//        }

    }

    public void initiallyInfect(boolean isCompleteInfection, boolean isInfectCBGOnly, ArrayList<Integer> initialInfectionRegionIndex, int fixedNumInfected) {
        Scope scope = (Scope) (myModelRoot.ABM.studyScopeGeography);
        if (isInfectCBGOnly == false) {
            if (isCompleteInfection == true) {
                detectRelevantCounties(scope);
                int sumRelevantCountiesPopulation = 0;
                int sumRelevantCountiesInfection = 0;
                for (int i = 0; i < relevantDailyConfirmedCases.size(); i++) {
                    if ((myModelRoot.ABM.currentTime).truncatedTo(ChronoUnit.DAYS).isEqual(relevantDailyConfirmedCases.get(i).date.truncatedTo(ChronoUnit.DAYS)) == true) {
                        sumRelevantCountiesPopulation += relevantDailyConfirmedCases.get(i).county.population;
                        sumRelevantCountiesInfection += relevantDailyConfirmedCases.get(i).numActiveCases * (10f / 3f);
                    }
                }
                int expectedInfectionInScope = -1;
                if (fixedNumInfected == -1) {
                    expectedInfectionInScope = (int) (((double) sumRelevantCountiesInfection / (double) sumRelevantCountiesPopulation) * (double) (scope.population));
                } else {
                    expectedInfectionInScope = fixedNumInfected;
                }
                double expectedInfectionPercentage = (double) (expectedInfectionInScope) / (double) (scope.population);
                initialRecovered(expectedInfectionPercentage);
                double currentInfections = 0;
                double currentInfectionPercentage = 0;
                int maxTry = 2000;
                int currentNumTry = 0;
                while (currentInfectionPercentage < expectedInfectionPercentage) {
//            System.out.println("S currentInfectionPercentage: "+currentInfectionPercentage +" expectedInfectionPercentage: "+expectedInfectionPercentage);
                    double selectedRegion = (rnd.nextDouble() * (sumRegionsPopulation));
                    double cumulativeRegionPopulation = 0;
                    for (int j = 0; j < regions.size(); j++) {
                        cumulativeRegionPopulation += regions.get(j).population;
                        if (cumulativeRegionPopulation > selectedRegion) {
                            if (!regions.get(j).residents.isEmpty()) {
                                int selectedResident = (int) ((rnd.nextDouble() * (regions.get(j).residents.size() - 1)));
//                                double randFillingChance = rnd.nextDouble();
                                if (rnd.nextDouble() > 0.7) {
                                    for (int m = 0; m < regions.get(j).residents.get(selectedResident).insidePeople.size(); m++) {
//                                        if (randFillingChance < rnd.nextDouble()) {
                                        if (regions.get(j).residents.get(selectedResident).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                                            regions.get(j).residents.get(selectedResident).insidePeople.get(m).fpp.status = statusEnum.INFECTED_SYM.ordinal();
                                            regions.get(j).residents.get(selectedResident).insidePeople.get(m).sfpp.infectedDays = 4 + (int) (rnd.nextDouble() * 10);
                                            //regions.get(j).residents.get(selectedResident).shamilPersonProperties.infectedDays = 4 + (int) (rnd.nextDouble() * 10);
                                            currentInfections = currentInfections + 1;
                                            currentInfectionPercentage = currentInfections / (people.size() * pTSFraction);
                                            //break;
                                        }
//                                        }
                                    }
                                } else {
                                    for (int m = 0; m < regions.get(j).residents.get(selectedResident).insidePeople.size(); m++) {
//                                        if (randFillingChance < rnd.nextDouble()) {
                                        if (regions.get(j).residents.get(selectedResident).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                                            regions.get(j).residents.get(selectedResident).insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
                                            regions.get(j).residents.get(selectedResident).insidePeople.get(m).sfpp.infectedDays = 3 + (int) (rnd.nextDouble() * 3);
                                            //regions.get(j).residents.get(selectedResident).shamilPersonProperties.infectedDays = 3 + (int) (rnd.nextDouble() * 3);
                                            currentInfections = currentInfections + 1;
                                            currentInfectionPercentage = currentInfections / (people.size() * pTSFraction);
                                            //break;
                                        }
//                                        }
                                    }
                                }
//                                currentInfections = currentInfections + 1;
//                                currentInfectionPercentage = currentInfections / (people.size());
                                break;
                            }
                        }
                    }
//            System.out.println("E currentInfectionPercentage: "+currentInfectionPercentage +" expectedInfectionPercentage: "+expectedInfectionPercentage);
                    currentNumTry = currentNumTry + 1;
                    if (currentNumTry > maxTry) {
                        System.out.println("SEVERE ISSUE: MAXIMUM INFECTION TRY IS REACHED!");
                        break;
                    }
                }
            } else {
                int regionPopulation = 0;
                for (int i = 0; i < initialInfectionRegionIndex.size(); i++) {
                    regionPopulation += regions.get(initialInfectionRegionIndex.get(i)).population;
                }
                int aBMRegionPopulation = 0;
                for (int i = 0; i < initialInfectionRegionIndex.size(); i++) {
                    aBMRegionPopulation += regions.get(initialInfectionRegionIndex.get(i)).residents.size() * pTSFraction;
                }
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
//                double expectedInfectionPercentage = ((double) (expectedInfectionInScope) / (double) (scope.population)) * ((double) (regionPopulation) / (double) (scope.population));
                double expectedInfectionPercentage = ((double) (expectedInfectionInScope) / (double) (scope.population));
                initialRecovered(expectedInfectionPercentage);
                double currentInfections = 0;
                double currentInfectionPercentage = 0;
                int maxRetry = 60;
                int tryCounter = 0;
                while (currentInfectionPercentage < expectedInfectionPercentage) {
//            System.out.println("S currentInfectionPercentage: "+currentInfectionPercentage +" expectedInfectionPercentage: "+expectedInfectionPercentage);
                    double selectedRegion = (rnd.nextDouble() * (regionPopulation));
                    double cumulativeRegionPopulation = 0;
                    for (int j = 0; j < initialInfectionRegionIndex.size(); j++) {
                        cumulativeRegionPopulation += regions.get(initialInfectionRegionIndex.get(j)).population;
                        if (cumulativeRegionPopulation > selectedRegion) {
                            if (!regions.get(initialInfectionRegionIndex.get(j)).residents.isEmpty()) {
                                int selectedResident = (int) ((rnd.nextDouble() * (regions.get(initialInfectionRegionIndex.get(j)).residents.size() - 1)));
                                for (int m = 0; m < regions.get(initialInfectionRegionIndex.get(j)).residents.get(selectedResident).insidePeople.size(); m++) {
                                    if (regions.get(initialInfectionRegionIndex.get(j)).residents.get(selectedResident).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                                        if (rnd.nextDouble() > 0.7) {
                                            if (regions.get(initialInfectionRegionIndex.get(j)).residents.get(selectedResident).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                                                regions.get(initialInfectionRegionIndex.get(j)).residents.get(selectedResident).insidePeople.get(m).fpp.status = statusEnum.INFECTED_SYM.ordinal();
                                                regions.get(initialInfectionRegionIndex.get(j)).residents.get(selectedResident).insidePeople.get(m).sfpp.infectedDays = 4 + (int) (rnd.nextDouble() * 10);
                                            }
                                        } else {
                                            if (regions.get(initialInfectionRegionIndex.get(j)).residents.get(selectedResident).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                                                regions.get(initialInfectionRegionIndex.get(j)).residents.get(selectedResident).insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
                                                regions.get(initialInfectionRegionIndex.get(j)).residents.get(selectedResident).insidePeople.get(m).sfpp.infectedDays = 3 + (int) (rnd.nextDouble() * 3);
                                            }
                                        }
                                        currentInfections = currentInfections + 1;
//                                        currentInfectionPercentage = (double) currentInfections / (double) aBMRegionPopulation;
                                        currentInfectionPercentage = (double) currentInfections / (double) (scope.population);
                                        tryCounter = 0;
//                                        break;
                                    } else {
                                        tryCounter += 1;
                                    }
                                }
//                            break;
                            } else {
                                tryCounter += 1;
                            }

                        }
                    }
                    if (tryCounter > maxRetry) {
                        System.out.println("SEVERE ISSUE: MAXIMUM INFECTION TRY IS REACHED!");
                        break;
                    }
//            System.out.println("E currentInfectionPercentage: "+currentInfectionPercentage +" expectedInfectionPercentage: "+expectedInfectionPercentage);
                }
            }
        } else {
            int subPop = 0;
            subPop = cBGregions.get(initialInfectionRegionIndex.get(0)).population;

            detectRelevantCounties(scope);
            int aBMRegionPopulation = 0;
//            System.out.println(cBGregions.get(initialInfectionRegionIndex.get(0)).cBGsIDsInvolved.get(0));
            for (int i = 0; i < regions.size(); i++) {
                for (int j = 0; j < regions.get(i).cBGsIDsInvolved.size(); j++) {
//                    System.out.println(regions.get(i).cBGsIDsInvolved.get(j));
                    if (regions.get(i).cBGsIDsInvolved.get(j).equals(cBGregions.get(initialInfectionRegionIndex.get(0)).cBGsIDsInvolved.get(0))) {
                        int regPop = regions.get(i).population;
                        double frac = ((double) subPop / (double) regPop) * regions.get(i).cBGsPercentageInvolved.get(j);
                        aBMRegionPopulation += regions.get(i).residents.size() * frac * pTSFraction;
                    }
                }
            }
            int numInfected = 0;
            int maxRetry = 2000;
            int tryCounter = 0;
            while (numInfected < aBMRegionPopulation) {
                for (int i = 0; i < regions.size(); i++) {
                    for (int j = 0; j < regions.get(i).cBGsIDsInvolved.size(); j++) {
                        if (regions.get(i).cBGsIDsInvolved.get(j).equals(cBGregions.get(initialInfectionRegionIndex.get(0)).cBGsIDsInvolved.get(0))) {
                            if (!regions.get(i).residents.isEmpty()) {
                                if (rnd.nextDouble() < regions.get(i).cBGsPercentageInvolved.get(j)) {
                                    ArrayList<Integer> residentIndices = new ArrayList();
                                    for (int o = 0; o < regions.get(i).residents.size(); o++) {
                                        for (int m = 0; m < regions.get(i).residents.get(o).insidePeople.size(); m++) {
                                            if (regions.get(i).residents.get(o).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                                                residentIndices.add(o);
                                                break;
                                            }
                                        }
                                    }
                                    if (residentIndices.isEmpty()) {
                                        continue;
                                    }
                                    int selectedResidentRaw = (int) ((rnd.nextDouble() * (residentIndices.size() - 1)));
                                    int selectedResident = residentIndices.get(selectedResidentRaw);
                                    for (int m = 0; m < regions.get(i).residents.get(selectedResident).insidePeople.size(); m++) {
                                        if (regions.get(i).residents.get(selectedResident).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                                            if (rnd.nextDouble() > 0.7) {
                                                regions.get(i).residents.get(selectedResident).insidePeople.get(m).fpp.status = statusEnum.INFECTED_SYM.ordinal();
                                                regions.get(i).residents.get(selectedResident).insidePeople.get(m).sfpp.infectedDays = 4 + (int) (rnd.nextDouble() * 10);
                                            } else {
                                                regions.get(i).residents.get(selectedResident).insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
                                                regions.get(i).residents.get(selectedResident).insidePeople.get(m).sfpp.infectedDays = 3 + (int) (rnd.nextDouble() * 3);
                                            }
                                            numInfected = numInfected + 1;
                                            tryCounter = 0;
                                            break;
                                        } else {
                                            tryCounter += 1;
                                        }
                                        break;
                                    }
                                }
                            } else {
                                tryCounter += 1;
                            }

                        }
                    }
                }
                if (tryCounter > maxRetry) {
                    System.out.println("MAXIMUM INFECTION TRY IS REACHED!");
                    break;
                }
            }
            int sumRelevantCountiesPopulation = 0;
            for (int i = 0; i < relevantDailyConfirmedCases.size(); i++) {
                if ((myModelRoot.ABM.currentTime).truncatedTo(ChronoUnit.DAYS).isEqual(relevantDailyConfirmedCases.get(i).date.truncatedTo(ChronoUnit.DAYS)) == true) {
                    sumRelevantCountiesPopulation += relevantDailyConfirmedCases.get(i).county.population;
                }
            }
            initialRecovered((double) numInfected / (double) sumRelevantCountiesPopulation);
        }
    }

    public void initialRecovered(double percentage) {
        int numRecovered = (int) Math.round(people.size() * percentage);
        int currentRecovered = 0;
        int counter = 0;
        int maxCounter = 150;
        while (currentRecovered < numRecovered) {
            int selectedResident = (int) ((rnd.nextDouble() * (people.size() - 1)));
            for (int m = 0; m < people.get(selectedResident).insidePeople.size(); m++) {
                if (people.get(selectedResident).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                    people.get(selectedResident).insidePeople.get(m).fpp.status = statusEnum.RECOVERED.ordinal();
                    people.get(selectedResident).insidePeople.get(m).sfpp.infectedDays = 28 + (int) (rnd.nextDouble() * 32);
                    currentRecovered = currentRecovered + 1;
                    counter = 0;
                } else {
                    counter = counter + 1;
                }
            }
            if (counter > maxCounter) {
                break;
            }
        }
    }

    public void detectRelevantCounties(Scope scope) {
        ArrayList<County> counties = new ArrayList();
        for (int i = 0; i < scope.censusTracts.size(); i++) {
            boolean isCountyFound = false;
            for (int j = 0; j < counties.size(); j++) {
                if (counties.get(j).id == scope.censusTracts.get(i).county.id) {
                    isCountyFound = true;
                    break;
                }
            }
            if (isCountyFound == false) {
                counties.add(scope.censusTracts.get(i).county);
            }
        }

        ArrayList<DailyConfirmedCases> dailyConfirmedCases = myModelRoot.covidCsseJhu.casesList;
        relevantDailyConfirmedCases = new ArrayList();

        for (int d = 0; d < dailyConfirmedCases.size(); d++) {
            for (int j = 0; j < counties.size(); j++) {
                if (dailyConfirmedCases.get(d).county.id == counties.get(j).id) {
                    relevantDailyConfirmedCases.add(dailyConfirmedCases.get(d));
                }
            }
        }
    }

    public void runShamil(boolean isSpatial, boolean isFuzzyStatus, double pTSFraction) {
        Duration d = Duration.between(myModelRoot.ABM.startTime, myModelRoot.ABM.currentTime);
        int day = (int) (d.toDays());
        if (myModelRoot.ABM.currentTime.getHour() == 0 && myModelRoot.ABM.currentTime.getMinute() == 0) {
            startDay(myModelRoot, people, day);
        }
        if (myModelRoot.ABM.currentTime.getMinute() == 0) {
            boolean debug = false;
            //if (myModelRoot.ABM.currentTime.getHour() == 9) {
            //    debug = true;
            //}
            shamilSimulatorController.updateHour(people, regions, myModelRoot.ABM.currentTime.getHour(), day, isSpatial, debug, myModelRoot);
        }
        if (myModelRoot.ABM.currentTime.getHour() == 23 && myModelRoot.ABM.currentTime.getMinute() == 59) {
            endDay(myModelRoot, people, day, isFuzzyStatus, pTSFraction);
            int sumInfected = 0;
            for (int i = 0; i < people.size(); i++) {
//                if (people.get(i).shamilPersonProperties.isInfected == true) {
//                    System.out.println();
//                    System.out.println("isInfected: " + people.get(i).shamilPersonProperties.isInfected);
//                    System.out.println("state: " + people.get(i).shamilPersonProperties.state);
//                    System.out.println("isAlive: " + people.get(i).shamilPersonProperties.isAlive);
//                    System.out.println();
//                }
//                if (people.get(i).shamilPersonProperties.isInfected == true && !people.get(i).shamilPersonProperties.state.equals("recovered") && people.get(i).shamilPersonProperties.isAlive == true) {
//                    sumInfected = sumInfected + 1;
//                }

                double numInfectedFound = 0;
                for (int m = 0; m < people.get(i).insidePeople.size(); m++) {
                    if (people.get(i).insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_SYM.ordinal() || people.get(i).insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_ASYM.ordinal()) {
                        numInfectedFound += 1;
                    }
                }
//                if (numInfectedFound / pTSFraction > 0.5) {
//                    sumInfected = sumInfected + 1;
//                }
                sumInfected = sumInfected + (int) numInfectedFound;
            }
            System.out.println("Day: " + day + " sumInfected: " + sumInfected);
        }
    }

    @Override
    public void behavior() {

        if (myModelRoot.ABM.isOurABMActive == true && myModelRoot.ABM.isShamilABMActive == true) {
//            int counter1=0;
//            for (int i = 0; i < people.size(); i++) {
//                if (people.get(i).shamilPersonProperties.profession.name.equals("Hospitalized")) {
//                     counter1=counter1+1;
//                }
//            }
//            System.out.println("num hospitalized outside "+counter1);//TEMP
//            ShamilSimulatorController.convertOurToShamil(people);
            ShamilSimulatorController.convertOurToShamilParallel(people, myModelRoot);
//            int counter1=0;
//            for (int i = 0; i < people.size(); i++) {
//                if (people.get(i).shamilPersonProperties.profession.name.equals("Hospitalized")) {
//                     counter1=counter1+1;
//                }
//            }
//            System.out.println("num hospitalized outside "+counter1);//TEMP
            runShamil(true, myModelRoot.ABM.isFuzzyStatus, myModelRoot.ABM.root.pTSFraction);
//            ShamilSimulatorController.convertShamilToOur(people);
            ShamilSimulatorController.convertShamilToOurParallel(people, myModelRoot);
            ArrayList<POI> values = new ArrayList<>(pOIs.values());
            for (int i = 0; i < values.size(); i++) {
                values.get(i).updateContamination();
            }
            prepareHourlyRegionSnapshotData();
            if (myModelRoot.ABM.isReportContactRate == true) {
                pollContactAllPeople();
            }
            myModelRoot.ABM.measureHolder.handlePTAVSP(myModelRoot);
        } else if (myModelRoot.ABM.isOurABMActive == false && myModelRoot.ABM.isShamilABMActive == true) {
            runShamil(false, false, 0);
//            ShamilSimulatorController.convertShamilToOur(people);
            ShamilSimulatorController.convertShamilToOurParallel(people, myModelRoot);
        } else if (myModelRoot.ABM.isOurABMActive == true && myModelRoot.ABM.isShamilABMActive == false) {
            handleHomeWorkActivities(myModelRoot.ABM.currentTime);
            handleInfectionProgress();
            ArrayList<POI> values = new ArrayList<>(pOIs.values());
            for (int i = 0; i < values.size(); i++) {
                values.get(i).updateContamination();
            }
            reportConsoleOurABMInfection(false);
            prepareHourlyRegionSnapshotData();
            if (myModelRoot.ABM.isReportContactRate == true) {
                pollContactAllPeople();
            }
            myModelRoot.ABM.measureHolder.handlePTAVSP(myModelRoot);
        }
        if (myModelRoot.ABM.currentTime.getHour() == 0 && myModelRoot.ABM.currentTime.getMinute() == 0) {
            if (myModelRoot.isDebugging == true) {
                System.out.println("infectedByPOIContactDaily: " + myModelRoot.ABM.infectedByPOIContactDaily);
                System.out.println("infectedByPOISuperspreadDaily: " + myModelRoot.ABM.infectedByPOISuperspreadDaily);
                System.out.println("infectedByPOIEnvDaily: " + myModelRoot.ABM.infectedByPOIEnvDaily);
                System.out.println("infectedByShamilDaily: " + myModelRoot.ABM.infectedByShamilDaily);
                System.out.println("infectedPOIDaily: " + myModelRoot.ABM.infectedPOIDaily);
                System.out.println("shamilInf1: " + myModelRoot.ABM.shamilInf1);
                System.out.println("shamilInf2: " + myModelRoot.ABM.shamilInf2);
                System.out.println("shamilInf3: " + myModelRoot.ABM.shamilInf3);

                myModelRoot.ABM.infectedByPOIContactDaily = 0;
                myModelRoot.ABM.infectedByPOISuperspreadDaily = 0;
                myModelRoot.ABM.infectedByPOIEnvDaily = 0;
                myModelRoot.ABM.infectedByShamilDaily = 0;
                myModelRoot.ABM.infectedPOIDaily = 0;
                myModelRoot.ABM.shamilInf1.set(0);// = 0;
                myModelRoot.ABM.shamilInf2.set(0);// = 0;
                myModelRoot.ABM.shamilInf3.set(0);// = 0;
            }
            pollDailyInfection();
        }
    }

    public void pollContactAllPeople() {
        for (int i = 0; i < people.size(); i++) {
            people.get(i).pollContact();
        }
    }

    public void reportConsoleOurABMInfection(boolean forceReport) {
        if (forceReport == true) {
            Duration d = Duration.between(myModelRoot.ABM.startTime, myModelRoot.ABM.currentTime);
            int day = (int) (d.toDays());
            int sumInfected = 0;
            for (int i = 0; i < people.size(); i++) {
                double numInfectedFound = 0;
                for (int m = 0; m < people.get(i).insidePeople.size(); m++) {
                    if (people.get(i).insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_SYM.ordinal() || people.get(i).insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_ASYM.ordinal()) {
                        numInfectedFound += 1;
                    }
                }

                sumInfected = sumInfected + (int) numInfectedFound;
            }
            System.out.println("Day: " + day + " sumInfected: " + sumInfected);
        } else {
            if (myModelRoot.ABM.currentTime.getHour() == 23 && myModelRoot.ABM.currentTime.getMinute() == 59) {
                Duration d = Duration.between(myModelRoot.ABM.startTime, myModelRoot.ABM.currentTime);
                int day = (int) (d.toDays());
                int sumInfected = 0;
                for (int i = 0; i < people.size(); i++) {
                    double numInfectedFound = 0;
                    for (int m = 0; m < people.get(i).insidePeople.size(); m++) {
                        if (people.get(i).insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_SYM.ordinal() || people.get(i).insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_ASYM.ordinal()) {
                            numInfectedFound += 1;
                        }
                    }
//                    if (numInfectedFound / pTSFraction > 0.5) {
//                        sumInfected = sumInfected + 1;
//                    }
                    sumInfected = sumInfected + (int) numInfectedFound;
                }

                System.out.println("Day: " + day + " sumInfected: " + sumInfected);
            }
        }
    }

    public void handleInfectionProgress() {
        for (int i = 0; i < people.size(); i++) {
            handleInfectionProgressPerson(people.get(i));
        }
    }

    public void handleInfectionProgressPerson(Person person) {
        for (int m = 0; m < person.insidePeople.size(); m++) {
            if (person.insidePeople.get(m).fpp.status == statusEnum.INFECTED_SYM.ordinal() || person.insidePeople.get(m).fpp.status == statusEnum.INFECTED_ASYM.ordinal()) {//if infected
                if (person.properties.minutesSick == -1) {
                    person.properties.minutesSick = 1;
                    if (rnd.nextDouble() < 0.0018) {//0.0018
                        person.properties.isDestinedToDeath = true;
                    } else {
                        person.properties.isDestinedToDeath = false;
                    }
                } else {
                    int minsSick = person.properties.minutesSick;
                    person.properties.minutesSick = minsSick + 1;
                    if (person.properties.isDestinedToDeath == false) {
                        if (minsSick > 20160) {
                            if (rnd.nextDouble() < Math.pow((double) (minsSick - 30240) / (double) (30240), 5)) {
//                            person.properties.minutesSick = -1;
                                person.insidePeople.get(m).fpp.status = statusEnum.RECOVERED.ordinal();//RECOVERED
                            }
                        }
                    } else {
                        if (minsSick > 10080) {
                            if (rnd.nextDouble() < Math.pow((double) (minsSick - 20160) / (double) (20160), 5)) {
                                person.properties.minutesSick = -1;
                                person.insidePeople.get(m).fpp.status = statusEnum.DEAD.ordinal();
                            }
                        }
                    }
                }
            }
            if (person.insidePeople.get(m).fpp.status == statusEnum.RECOVERED.ordinal()) {
                int minsSick = person.properties.minutesSick;
                person.properties.minutesSick = minsSick + 1;
//                if (minsSick > 86400) {
//                    person.properties.minutesSick = -1;
//                    person.insidePeople.get(m).fpp.status = statusEnum.SUSCEPTIBLE.ordinal();
//                }
                if (minsSick > 864000) {
                    person.properties.minutesSick = -1;
                    person.insidePeople.get(m).fpp.status = statusEnum.SUSCEPTIBLE.ordinal();
                }
            }
        }
    }

    public void handleHomeWorkActivities(ZonedDateTime currentTime) {
        for (int i = 0; i < people.size(); i++) {
            goToWork(currentTime, people.get(i));
            returnFromWork(currentTime, people.get(i));
        }
    }

    public void goToWork(ZonedDateTime currentTime, Person person) {
        if (currentTime.getDayOfWeek().getValue() < 6) {
            if (person.properties.isAtWork == false) {
                if (currentTime.getHour() <= 9 && currentTime.getHour() >= 7) {
                    if (person.properties.minutesTravelToWorkFrom7 == -1) {
                        int minutesTravelToWorkFrom7 = (int) (rnd.nextDouble() * 120);
                        person.properties.minutesTravelToWorkFrom7 = minutesTravelToWorkFrom7;
                    } else {
                        int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 7) * 60;
                        if (passed > person.properties.minutesTravelToWorkFrom7) {
                            lat = person.properties.workRegion.lat;
                            lon = person.properties.workRegion.lon;
                            person.properties.isAtWork = true;
                            person.properties.isAtHome = false;
                            person.properties.minutesTravelFromWorkFrom16 = -1;
                        }
                    }
                }
            }
        }
    }

    public void returnFromWork(ZonedDateTime currentTime, Person person) {
        if (person.properties.isAtWork == true) {
            if (currentTime.getHour() <= 18 && currentTime.getHour() >= 16) {
                if (person.properties.minutesTravelFromWorkFrom16 == -1) {
                    int minutesTravelFromWorkFrom16 = (int) (rnd.nextDouble() * 120);
                    person.properties.minutesTravelFromWorkFrom16 = minutesTravelFromWorkFrom16;
                } else {
                    int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 16) * 60;
                    if (passed > person.properties.minutesTravelFromWorkFrom16) {
                        lat = person.properties.homeRegion.lat;
                        lon = person.properties.homeRegion.lon;
                        person.properties.isAtWork = false;
                        person.properties.isAtHome = true;
                        person.properties.minutesTravelFromWorkFrom16 = -1;
                    }
                }
            }
        }
    }

//    public void calcContactRate(MainModel modelRoot) {
//        if (modelRoot.ABM.isReportContactRate == true) {
//            for (int i = 0; i < modelRoot.ABM.agents.size(); i++) {
//                if (modelRoot.ABM.agents.get(i).myType.equals("Person")) {
//
//                }
//            }
//        }
//    }
    public void pollDailyInfection() {
        String[] row = new String[10];
        row[0] = myModelRoot.ABM.currentTime.format(DateTimeFormatter.ISO_DATE);
        int numSUSCEPTIBLE = 0;
        int numINFECTED_SYM = 0;
        int numINFECTED_ASYM = 0;
        int numRECOVERED = 0;
        int numDEAD = 0;
        for (int i = 0; i < people.size(); i++) {
            for (int m = 0; m < people.get(i).insidePeople.size(); m++) {
                if (people.get(i).insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
                    numSUSCEPTIBLE += 1;
                } else if (people.get(i).insidePeople.get(m).fpp.status == statusEnum.INFECTED_SYM.ordinal()) {
                    numINFECTED_SYM += 1;
                } else if (people.get(i).insidePeople.get(m).fpp.status == statusEnum.INFECTED_ASYM.ordinal()) {
                    numINFECTED_ASYM += 1;
                } else if (people.get(i).insidePeople.get(m).fpp.status == statusEnum.RECOVERED.ordinal()) {
                    numRECOVERED += 1;
                } else if (people.get(i).insidePeople.get(m).fpp.status == statusEnum.DEAD.ordinal()) {
                    numDEAD += 1;
                }
            }
        }
        row[1] = String.valueOf(numSUSCEPTIBLE);
        row[2] = String.valueOf(numINFECTED_SYM);
        row[3] = String.valueOf(numINFECTED_ASYM);
        row[4] = String.valueOf(numRECOVERED);
        row[5] = String.valueOf(numDEAD);

        int pop = numSUSCEPTIBLE + numINFECTED_SYM + numINFECTED_ASYM + numRECOVERED + numDEAD;

        Scope scope = (Scope) (myModelRoot.ABM.studyScopeGeography);

        int sumRelevantCountiesPopulation = 0;
        int sumRelevantCountiesInfection = 0;
        for (int i = 0; i < relevantDailyConfirmedCases.size(); i++) {
            if ((myModelRoot.ABM.currentTime.truncatedTo(ChronoUnit.DAYS)).isEqual(relevantDailyConfirmedCases.get(i).date.truncatedTo(ChronoUnit.DAYS)) == true) {
                sumRelevantCountiesPopulation += relevantDailyConfirmedCases.get(i).county.population;
                sumRelevantCountiesInfection += relevantDailyConfirmedCases.get(i).numActiveCases * (10f / 3f);
            }
        }
        int expectedInfectionInScope = (int) (((double) sumRelevantCountiesInfection / (double) sumRelevantCountiesPopulation) * (double) (scope.population));
        float iPRJHU = (float) expectedInfectionInScope / (float) (scope.population);

        row[6] = String.valueOf(iPRJHU);
        row[7] = String.valueOf(iPRJHU / (10f / 3f));
        row[8] = String.valueOf((float) (numINFECTED_SYM + numINFECTED_ASYM) / (float) pop);
        row[9] = String.valueOf((float) (numINFECTED_SYM) / (float) pop);

        infectionPoll.add(row);
    }

    public void writeDailyInfection(String filePath) {
//        CsvWriter writer = new CsvWriter();
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath + ".csv"));
            writer.writeAll(infectionPoll);
            writer.close();
//            writer.write(new File(filePath + ".csv"), Charset.forName("US-ASCII"), infectionPoll);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeDailyMobility(String filePath) {
        ArrayList<String[]> rows = new ArrayList();
        String[] header = new String[1];
        header[0] = "Average daily travels";
        rows.add(header);
        for (int i = 0; i < mobilityPoll.size(); i++) {
            rows.add(mobilityPoll.get(i));
        }
//        CsvWriter writer = new CsvWriter();
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath + ".csv"));
            writer.writeAll(rows);
            writer.close();
//            writer.write(new File(filePath + ".csv"), Charset.forName("US-ASCII"), rows);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeTotalContacts(String filePath) {
        if (myModelRoot.ABM.isReportContactRate == true) {
            ArrayList<String[]> rows = new ArrayList();
            for (int i = 0; i < agentPairContact.length; i++) {
                String[] row = new String[agentPairContact[0].length];
                for (int j = 0; j < agentPairContact[0].length; j++) {
                    row[j] = String.valueOf(agentPairContact[i][j]);
                }
                rows.add(row);
            }

            try {
//                CsvWriter writer = new CsvWriter();
//                writer.write(new File(filePath + ".csv"), Charset.forName("US-ASCII"), rows);

                CSVWriter writer = new CSVWriter(new FileWriter(filePath + ".csv"));
                writer.writeAll(rows);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Root.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void writeSimulationSummary(String filePath) {
        ArrayList<String[]> rows = new ArrayList();
        String[] header = new String[13];
        header[0] = "Start Date";
        header[1] = "End Date";
        header[2] = "Scope";
        header[3] = "Scenario";
        header[4] = "Is number of cells fixed";
        header[5] = "Number of cells";
        header[6] = "Sum Infected";
        header[7] = "Sum Deaths";
        header[8] = "Sum Recovered";
        header[9] = "Sum Contacts";
        header[10] = "Average Contacts";
        header[11] = "Variance Contacts";
        header[12] = "Runtime";
        rows.add(header);
        int sumInfected = 0;
        int sumDeaths = 0;
        int sumRecovered = 0;
        int sumContacts = 0;
        float avgContacts = 0;
        double varContacts = 0;
        for (int i = 1; i < infectionPoll.size(); i++) {
            sumInfected = sumInfected + Integer.valueOf(infectionPoll.get(i)[2]) + Integer.valueOf(infectionPoll.get(i)[3]);
            sumDeaths = sumDeaths + Integer.valueOf(infectionPoll.get(i)[5]);
            sumRecovered = sumRecovered + Integer.valueOf(infectionPoll.get(i)[4]);
        }
        if (myModelRoot.ABM.isReportContactRate == true) {
            for (int i = 0; i < agentPairContact.length; i++) {
                for (int j = 0; j < agentPairContact[0].length; j++) {
                    sumContacts = sumContacts + agentPairContact[i][j];
                }
            }
            avgContacts = (float) sumContacts / (float) (agentPairContact.length * agentPairContact[0].length);
            for (int i = 0; i < agentPairContact.length; i++) {
                for (int j = 0; j < agentPairContact[0].length; j++) {
                    varContacts = varContacts + Math.pow(agentPairContact[i][j] - sumContacts, 2);
                }
            }

            varContacts = (double) varContacts / (double) (agentPairContact.length * agentPairContact[0].length);
        }
        String[] row = new String[13];
        row[0] = myModelRoot.ABM.startTime.format(DateTimeFormatter.ISO_DATE);
        row[1] = myModelRoot.ABM.endTime.format(DateTimeFormatter.ISO_DATE);
        row[2] = myModelRoot.ABM.studyScope;
        row[3] = myModelRoot.scenario.scenarioName;
        row[4] = String.valueOf(myModelRoot.scenario.isForceFixedNumberOfCells);
        row[5] = String.valueOf(myModelRoot.scenario.numRegions);
        row[6] = String.valueOf(sumInfected);
        row[7] = String.valueOf(sumDeaths);
        row[8] = String.valueOf(sumRecovered);
        row[9] = String.valueOf(sumContacts);
        if (myModelRoot.ABM.isReportContactRate == true) {
            row[10] = String.valueOf(avgContacts);
            row[11] = String.valueOf(varContacts);
        } else {
            row[10] = "NA";
            row[11] = "NA";
        }
        row[12] = String.valueOf(myModelRoot.elapsed);
        rows.add(row);
        try {
//            CsvWriter writer = new CsvWriter();
//            writer.write(new File(filePath + ".csv"), Charset.forName("US-ASCII"), rows);

            CSVWriter writer = new CSVWriter(new FileWriter(filePath + ".csv"));
            writer.writeAll(rows);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeDailyContactRate(MainModel modelRoot) {
        if (modelRoot.ABM.isReportContactRate == true) {
            ZonedDateTime currentDate = modelRoot.getABM().getCurrentTime();
            if (currentDate.getHour() == 0 && currentDate.getMinute() == 1) {
                //System.out.println("FFF: "+counter);
                if (currentAgent.counter == 110) {
                    System.out.println("GOING TO WRITE DOWN THE DATA!!!! " + currentAgent.counter);

                    Writer writer = null;

                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(modelRoot.ABM.studyScope + "_agentPairContact_" + modelRoot.scenario + "_" + numAgents + ".csv"), "ascii"));
                        int sum = 0;
                        for (int i = 0; i < agentPairContact.length; i++) {
                            for (int j = 0; j < agentPairContact[i].length - 1; j++) {
                                sum = sum + agentPairContact[i][j];
                                writer.write(String.valueOf(agentPairContact[i][j]));
                                writer.write(",");
                            }
                            writer.write(String.valueOf(agentPairContact[i][agentPairContact[i].length - 1]));
                            writer.write(System.lineSeparator());
                        }
                        System.out.println("sum: " + sum);
                    } catch (IOException ex) {
                        // Report
                    } finally {
                        try {
                            writer.close();
                        } catch (Exception ex) {/*ignore*/
                        }
                    }
                    System.gc();
                }
            }
        }
    }

//@CompileStatic
//    int getGeneralInformation(ArrayList patternRecords) {
//        //GET ALL NUM PEOPLE (VISITS)
//        System.out.println("one pass on all patterns to detect number of people ...");
//        int numAllVisits = 0;
//        for (int i = 0; i < patternRecords.size(); i++) {
//            if (isLocalAllowed == false) {
//                int naics_code = ((PatternsRecordProcessed) patternRecords.get(i)).place.naics_code;
//                if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                    numAllVisits = numAllVisits + ((PatternsRecordProcessed) patternRecords.get(i)).getRaw_visitor_counts();
//                }
//            } else {
//                numAllVisits = numAllVisits + ((PatternsRecordProcessed) patternRecords.get(i)).getRaw_visitor_counts();
//            }
//            //println("lat: "+patternRecords.get(i).getPlace().getLat());
//            //println("lon: "+patternRecords.get(i).getPlace().getLon());
//        }
//        System.out.println("numAllVisits: " + numAllVisits);
//        return numAllVisits;
//    }
    public void prepareHourlyRegionSnapshotData() {
//        System.out.println("\\/\\/\\/\\/");
//        if(people.get(0).properties.status==0){
//            System.out.println("SUSCEPTIBLE");
//        }else if(people.get(0).properties.status==1){
//            System.out.println("INFECTED_SYM");
//        }else if(people.get(0).properties.status==2){
//            System.out.println("INFECTED_ASYM");
//        }else if(people.get(0).properties.status==3){
//            System.out.println("RECOVERED");
//        }else if(people.get(0).properties.status==4){
//            System.out.println("DEAD");
//        }

//        System.out.println(people.get(0).shamilPersonProperties.state);
//        switch (people.get(0).properties.status) {
//            case 0:
//                System.out.println("SUSCEPTIBLE");
//                break;
//            case 1:
//                System.out.println("INFECTED_SYM");
//                break;
//            case 2:
//                System.out.println("INFECTED_ASYM");
//                break;
//            case 3:
//                System.out.println("RECOVERED");
//                break;
//            case 4:
//                System.out.println("DEAD");
//                break;
//            default:
//                break;
//        }
//        for (int i = 0; i < regions.size(); i++) {
//            for (int j = 0; j < regions.get(i).residents.size(); j++) {
//                if(regions.get(i).residents.get(j).properties.status==1){
//                    System.out.println("Region: "+i);
//                    System.out.println("Resident: "+j);
//                    System.out.println("INFECTED_SYM");
//                }
//                if(regions.get(i).residents.get(j).properties.status==2){
//                    System.out.println("Region: "+i);
//                    System.out.println("Resident: "+j);
//                    System.out.println("INFECTED_ASYM");
//                }
//            }
//        }
        if (myModelRoot.ABM.currentTime.getMinute() == 0) {
            for (int i = 0; i < regions.size(); i++) {
                RegionSnapshot snapshot = new RegionSnapshot();
                snapshot.population=regions.get(i).population;
                if (regions.get(i).hourlyRegionSnapshot.size() > 0) {
                    snapshot.rate = regions.get(i).hourlyRegionSnapshot.get(regions.get(i).hourlyRegionSnapshot.size() - 1).rate;
                }
                for (int j = 0; j < regions.get(i).residents.size(); j++) {
                    for (int m = 0; m < regions.get(i).residents.get(j).insidePeople.size(); m++) {
                        switch (regions.get(i).residents.get(j).insidePeople.get(m).fpp.status) {
                            case 0:
                                snapshot.N += 1;
                                snapshot.S += 1;
                                break;
                            case 1:
                                snapshot.N += 1;
                                snapshot.IS += 1;
                                snapshot.sick += 1;
                                snapshot.rate += 1;
                                break;
                            case 2:
                                snapshot.N += 1;
                                snapshot.IAS += 1;
                                snapshot.sick += 1;
                                snapshot.rate += 1;
                                break;
                            case 3:
                                snapshot.N += 1;
                                snapshot.R += 1;
                                break;
                            case 4:
                                snapshot.D += 1;
                                break;
                            default:
                                break;
                        }
                    }
                }
                regions.get(i).hourlyRegionSnapshot.add(snapshot);
            }
        }
    }

    public static boolean isFoodAndGrocery(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if ((naicsString.startsWith("44") || naicsString.startsWith("45")) && !naicsString.startsWith("4411") && !naicsString.startsWith("4412") && !naicsString.startsWith("4413")) {
            return true;
        }
        return false;
    }

    public static boolean isReligiousOrganization(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if (naicsString.startsWith("8131")) {
            return true;
        }
        return false;
    }

    public static boolean isSchool(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if (naicsString.startsWith("61")) {
            return true;
        }
        return false;
    }

    public void writeConvertedToCBGInfection(String filePath) {
        ArrayList<String[]> rows = new ArrayList();
        String[] row1 = new String[cBGregions.size()];
        String[] row2 = new String[cBGregions.size()];
        for (int i = 0; i < cBGregions.size(); i++) {
            if (!cBGregions.get(i).cBGsIDsInvolved.isEmpty()) {
                row1[i] = String.valueOf(cBGregions.get(i).cBGsIDsInvolved.get(0));
                double infection = 0;
                for (int j = 0; j < regions.size(); j++) {
                    for (int m = 0; m < regions.get(j).cBGsPercentageInvolved.size(); m++) {
                        if (regions.get(j).cBGsIDsInvolved.get(m).equals(cBGregions.get(i).cBGsIDsInvolved.get(0))) {
                            double addedInfection = 0;
                            for (int k = 0; k < regions.get(j).residents.size(); k++) {
                                for (int g = 0; g < regions.get(j).residents.get(k).insidePeople.size(); g++) {
                                    switch (regions.get(j).residents.get(k).insidePeople.get(g).fpp.status) {
                                        case 0:
                                            break;
                                        case 1:
                                            addedInfection += 1;
                                            break;
                                        case 2:
                                            addedInfection += 1;
                                            break;
                                        case 3:
                                            break;
                                        case 4:
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            addedInfection = addedInfection * regions.get(j).cBGsPercentageInvolved.get(m);
                            infection += addedInfection;
                        }
                    }
                }
                row2[i] = String.valueOf(infection);
            }
        }
        rows.add(row1);
        rows.add(row2);
        try {
//            CsvWriter writer = new CsvWriter();
//            writer.write(new File(filePath + ".csv"), Charset.forName("US-ASCII"), rows);

            CSVWriter writer = new CSVWriter(new FileWriter(filePath + ".csv"));
            writer.writeAll(rows);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Root.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeAllMobilityCounts(String filePath) {
        ArrayList<String[]> rows = new ArrayList();
        String[] header = new String[2];
        header[0] = "POI";
        header[1] = "Num visits";
        rows.add(header);
        int counter = 0;
        for (HashMap.Entry<String, Long> entry : travelsToAllPOIsFreqs.entrySet()) {
//            POI key = entry.getKey();
            Long value = entry.getValue();
            String[] row = new String[2];
            row[0] = String.valueOf(counter);
            row[1] = String.valueOf(value);
            counter = counter + 1;
            rows.add(row);
        }
//        CsvWriter writer = new CsvWriter();
        try {
//            writer.write(new File(filePath + ".csv"), Charset.forName("US-ASCII"), rows);

            CSVWriter writer = new CSVWriter(new FileWriter(filePath + ".csv"));
            writer.writeAll(rows);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
