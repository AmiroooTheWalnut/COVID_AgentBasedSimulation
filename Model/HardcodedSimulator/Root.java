/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import static COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog.isReligiousOrganization;
import static COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog.isSchool;
import static COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog.isShop;
import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentBasedModel;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.Agent;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.CensusBlockGroupIntegerTuple;
import COVID_AgentBasedSimulation.Model.Structure.Country;
import COVID_AgentBasedSimulation.Model.Structure.State;
import COVID_AgentBasedSimulation.Model.Structure.County;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.CensusTract;
import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.DailyConfirmedCases;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController.endDay;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController.startDay;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController.updateHour;
import COVID_AgentBasedSimulation.Model.Structure.CBGVDCell;
import COVID_AgentBasedSimulation.Model.Structure.Scope;
import COVID_AgentBasedSimulation.Model.Structure.VDCell;
import com.opencsv.CSVWriter;
import de.siegmar.fastcsv.writer.CsvWriter;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class Root extends Agent {

    public enum statusEnum {
        SUSCEPTIBLE, INFECTED_SYM, INFECTED_ASYM, RECOVERED, DEAD;
    }

    Root currentAgent = this;

    public ArrayList<Person> people = new ArrayList();
    public ArrayList<Person> travelers = new ArrayList();

    public int residentPopulation;
    public int nonResidentPopulation;
    public int startCountyIndex;
    public int endCountyIndex;

    ArrayList<DailyConfirmedCases> relevantDailyConfirmedCases;

    ArrayList<Region> regions;
    int sumRegionsPopulation = 0;
    HashMap<String, POI> pOIs;

    public int counter;
    int numAgents = 10000;

    public boolean isLocalAllowed = true;

    public int agentPairContact[][];

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
     */
    public void constructor(MainModel modelRoot, int passed_numAgents, String passed_regionType, int passed_numRandomRegions) {
        myModelRoot = modelRoot;
        numAgents = passed_numAgents;
        generateRegions(modelRoot, passed_regionType, passed_numRandomRegions);
        generateSchedules(modelRoot, passed_regionType, regions);
        generateAgents(modelRoot, passed_numAgents);

        if (modelRoot.ABM.isShamilABMActive == true) {
//            shamilSimulatorController=new ShamilSimulatorController();
            ShamilSimulatorController.shamilAgentGeneration(people);
            if (modelRoot.ABM.isOurABMActive == true) {
                initiallyInfect();
                reportOurABMInfection(true);
                for (int i = 0; i < people.size(); i++) {
                    people.get(i).isActive = true;
                }
            } else {
                ShamilSimulatorController.shamilInitialInfection(people);
            }
        } else {
            if (modelRoot.ABM.isOurABMActive == true) {
                initiallyInfect();
                reportOurABMInfection(true);
                for (int i = 0; i < people.size(); i++) {
                    people.get(i).isActive = true;
                }
            }
        }
    }

    public void generateRegions(MainModel modelRoot, String type, int n) {
        switch (type) {
            case "CBG":
                regions = makeByCBGs(modelRoot);
                break;
            case "VD":
                regions = makeByVDs(modelRoot);
                break;
            case "CBGVD":
                regions = makeByCBGVDs(modelRoot);
                break;
            case "RANDOM":

                break;
            default:

        }
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

    public void generateRegionSchedules(MainModel modelRoot, ArrayList<Region> regions, ArrayList<PatternsRecordProcessed> patternRecords) {
        for (int j = 0; j < patternRecords.size(); j++) {

            calcCumulativeMonthWeekHour(patternRecords.get(j));// COULD BE AVOIDED IF ALL DATA BE PREPROCESSED AND TRANSIENTS ARE REMOVED

            if (patternRecords.get(j).visitor_home_cbgs_place != null) {
                for (int k = 0; k < patternRecords.get(j).visitor_home_cbgs_place.size(); k++) {
                    if (isLocalAllowed == false) {
                        int naics_code = patternRecords.get(j).place.naics_code;
                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
                            ArrayList<Region> selectedSourceRegions = new ArrayList();
                            ArrayList<Double> selectSourceRegionsPercentages = new ArrayList();
                            for (int o = 0; o < regions.size(); o++) {
                                for (int p = 0; p < regions.get(o).cBGsInvolved.size(); p++) {
                                    if (regions.get(o).cBGsInvolved.get(p).id == patternRecords.get(j).visitor_home_cbgs_place.get(k).key.id) {
                                        selectedSourceRegions.add(regions.get(o));
                                        selectSourceRegionsPercentages.add(regions.get(o).cBGsPercentageInvolved.get(p));
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
                        ArrayList<Double> selectSourceRegionsPercentages = new ArrayList();
                        for (int o = 0; o < regions.size(); o++) {
                            for (int p = 0; p < regions.get(o).cBGsInvolved.size(); p++) {
                                if (regions.get(o).cBGsInvolved.get(p).id == patternRecords.get(j).visitor_home_cbgs_place.get(k).key.id) {
                                    selectedSourceRegions.add(regions.get(o));
                                    selectSourceRegionsPercentages.add(regions.get(o).cBGsPercentageInvolved.get(p));
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
        pOIs = new HashMap();
        for (int i = 0; i < modelRoot.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            ArrayList<PatternsRecordProcessed> patternRecordsTemp = modelRoot.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords;
            for (int j = 0; j < patternRecordsTemp.size(); j++) {
                patternRecords.add(patternRecordsTemp.get(j));
                POI tempPOI = new POI();
                tempPOI.patternsRecord = patternRecordsTemp.get(j);
                pOIs.put(patternRecordsTemp.get(j).placeKey, tempPOI);
            }
        }
        generateRegionSchedules(modelRoot, regions, patternRecords);
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
        sumRegionsPopulation = cumulativePopulation;
        for (int i = 0; i < passed_numAgents; i++) {
            Person person = new Person();
            selectHomeRegion(person, sumRegionsPopulation);
            selectWorkRegion(person, person.properties.homeRegion);

            modelRoot.ABM.agents.add(person);
            person.constructor(modelRoot);
            people.add(person);
        }
    }

    private void selectHomeRegion(Person person, int cumulativePopulation) {
        int indexCumulativePopulation = (int) ((Math.random() * (cumulativePopulation - 1)));
        int cumulativePopulationRun = regions.get(0).population;
        for (int j = 0; j < regions.size(); j++) {
            cumulativePopulationRun = cumulativePopulationRun + regions.get(j).population;
            if (cumulativePopulationRun > indexCumulativePopulation) {
                person.properties.homeRegion = regions.get(j);
                regions.get(j).residents.add(person);
                break;
            }
        }
    }

    private void selectWorkRegion(Person person, Region homeRegion) {
        ScheduleList scheduleList = homeRegion.scheduleList;
        Region workRegion;
        for (int i = 0; i < 20; i++) {
            workRegion = sampleWorkRegion(scheduleList, person, homeRegion);
            if (workRegion != null) {
                person.properties.workRegion = workRegion;
                break;
            }
        }
        if (person.properties.workRegion == null) {
            person.properties.workRegion = person.properties.homeRegion;
        }
        person.properties.workRegion.workers.add(person);
    }

    private Region sampleWorkRegion(ScheduleList scheduleList, Person person, Region homeRegion) {
        int indexCumulativeWorkCBG = (int) ((Math.random() * (homeRegion.workPopulation - 1)));
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
                        double selectedPercentage = (Math.random() * (cumulativeCBGPercentages - 0.0000001d));

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
        ArrayList<CensusBlockGroup> cBGsListRaw = modelRoot.getSafegraph().getCBGsFromCaseStudy(modelRoot.getABM().getStudyScopeGeography());
        ArrayList regionsList = new ArrayList();
        for (int i = 0; i < cBGsListRaw.size(); i++) {
            Region region = new Region();
            region.lat = cBGsListRaw.get(i).getLat();
            region.lon = cBGsListRaw.get(i).getLon();
            region.N = 0;
            region.S = 0;
            region.IS = 0;
            region.IAS = 0;
            region.R = 0;
            region.population = cBGsListRaw.get(i).population;
            region.workPopulation = 0;
            region.cBGsIDsInvolved = new ArrayList();
            region.cBGsIDsInvolved.add(cBGsListRaw.get(i).id);
            region.cBGsInvolved = new ArrayList();
            region.cBGsInvolved.add(cBGsListRaw.get(i));
            region.cBGsPercentageInvolved = new ArrayList();
            region.cBGsPercentageInvolved.add(1d);
//            region.polygon
            regionsList.add(region);
        }
        return regionsList;
    }
    
    

    ArrayList makeByVDs(MainModel modelRoot) {
        ArrayList<VDCell> vDsListRaw = modelRoot.getSafegraph().getVDsFromCaseStudy(modelRoot.getABM().getStudyScopeGeography());
        ArrayList regionsList = new ArrayList();
        for (int i = 0; i < vDsListRaw.size(); i++) {
            Region region = new Region();
            region.lat = vDsListRaw.get(i).getLat();
            region.lon = vDsListRaw.get(i).getLon();
            region.N = 0;
            region.S = 0;
            region.IS = 0;
            region.IAS = 0;
            region.R = 0;
            region.population = vDsListRaw.get(i).population;
            region.workPopulation = 0;
            region.cBGsIDsInvolved = vDsListRaw.get(i).cBGsIDsInvolved;
            region.cBGsInvolved = vDsListRaw.get(i).cBGsInvolved;
            region.cBGsPercentageInvolved = vDsListRaw.get(i).cBGsPercentageInvolved;
            regionsList.add(region);
        }
        return regionsList;
    }

    ArrayList makeByCBGVDs(MainModel modelRoot) {
        ArrayList<CBGVDCell> cBGVDsListRaw = modelRoot.getSafegraph().getCBGVDsFromCaseStudy(modelRoot.getABM().getStudyScopeGeography());
        ArrayList regionsList = new ArrayList();
        for (int i = 0; i < cBGVDsListRaw.size(); i++) {
            Region region = new Region();
//            agent.cbgVal = cBGsListRaw.get(i);
//            region.cbgvdVal = cBGsListRaw.get(i);
            region.lat = cBGVDsListRaw.get(i).getLat();
            region.lon = cBGVDsListRaw.get(i).getLon();
            region.N = 0;
            region.S = 0;
            region.IS = 0;
            region.IAS = 0;
            region.R = 0;
            region.population = cBGVDsListRaw.get(i).population;
            region.workPopulation = 0;
            region.cBGsIDsInvolved = cBGVDsListRaw.get(i).cBGsIDsInvolved;
            region.cBGsInvolved = cBGVDsListRaw.get(i).cBGsInvolved;
            region.cBGsPercentageInvolved = cBGVDsListRaw.get(i).cBGsPercentageInvolved;
            regionsList.add(region);
        }
        return regionsList;
    }

    public void initiallyInfect() {
        Scope scope = (Scope) (myModelRoot.ABM.studyScopeGeography);
        detectRelevantCounties(scope);

        int sumRelevantCountiesPopulation = 0;
        int sumRelevantCountiesInfection = 0;
        for (int i = 0; i < relevantDailyConfirmedCases.size(); i++) {
            if ((myModelRoot.ABM.currentTime).equals(relevantDailyConfirmedCases.get(i).date) == true) {
                sumRelevantCountiesPopulation += relevantDailyConfirmedCases.get(i).county.population;
                sumRelevantCountiesInfection += relevantDailyConfirmedCases.get(i).numActiveCases;
            }
        }
        int expectedInfectionInScope = (int) (((double) sumRelevantCountiesInfection / (double) sumRelevantCountiesPopulation) * (double) (scope.population));
        double expectedInfectionPercentage = (double) (expectedInfectionInScope) / (double) (scope.population);
        double currentInfections = 0;
        double currentInfectionPercentage = 0;
        while (currentInfectionPercentage < expectedInfectionPercentage) {
//            System.out.println("S currentInfectionPercentage: "+currentInfectionPercentage +" expectedInfectionPercentage: "+expectedInfectionPercentage);
            double selectedRegion = (Math.random() * (sumRegionsPopulation));
            double cumulativeRegionPopulation = 0;
            for (int j = 0; j < regions.size(); j++) {
                cumulativeRegionPopulation += regions.get(j).population;
                if (cumulativeRegionPopulation > selectedRegion) {
                    if (!regions.get(j).residents.isEmpty()) {
                        int selectedResident = (int) ((Math.random() * (regions.get(j).residents.size() - 1)));
                        if (Math.random() > 0.7) {
                            regions.get(j).residents.get(selectedResident).properties.status = statusEnum.INFECTED_SYM.ordinal();
                        } else {
                            regions.get(j).residents.get(selectedResident).properties.status = statusEnum.INFECTED_ASYM.ordinal();
                        }
                        currentInfections = currentInfections + 1;
                        currentInfectionPercentage = currentInfections / people.size();
                        break;
                    }
                }
            }
//            System.out.println("E currentInfectionPercentage: "+currentInfectionPercentage +" expectedInfectionPercentage: "+expectedInfectionPercentage);
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

        ArrayList<DailyConfirmedCases> dailyConfirmedCases = myModelRoot.covidCsseJhu.dailyConfirmedCasesList;
        relevantDailyConfirmedCases = new ArrayList();

        for (int d = 0; d < dailyConfirmedCases.size(); d++) {
            for (int j = 0; j < counties.size(); j++) {
                if (dailyConfirmedCases.get(d).county.id == counties.get(j).id) {
                    relevantDailyConfirmedCases.add(dailyConfirmedCases.get(d));
                }
            }
        }
    }

    public void constructorVD(MainModel modelRoot) {
//        System.out.println("ROOT CONSTRUCTOR VD: " + Math.random());
//
//        ArrayList patternRecords = modelRoot.getSafegraph().getAllPatterns().getMonthlyPatternsList().get(0).getPatternRecords();
//
//        int numAllVisits = getGeneralInformation(patternRecords);
//
//        long start = System.currentTimeMillis();
//
//        ArrayList vDsList = makeVDs(modelRoot);
//        System.out.println("VD size: " + vDsList.size());
//
//        runGenPeople(numAgents, modelRoot, patternRecords, numAllVisits, vDsList, 1);
////        runGenPeopleSerially(numAgents, modelRoot, patternRecords, numAllVisits, cBGsList);
//
//        long end = System.currentTimeMillis();
//        System.out.println("Generating people elapsed time: " + (end - start) + " miliSeconds.");
//
//        System.out.println("Initially infect people:");
//        start = System.currentTimeMillis();
//
//        ArrayList<DailyConfirmedCases> dailyConfirmedCases = ((CovidCsseJhu) (modelRoot.covidCsseJhu)).dailyConfirmedCasesList;
//        if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof County) {
//
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof State) {
//            State scope = (State) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            int numActiveInfected = 0;
//            int scopePopulation = scope.getPopulation();
//            for (int j = 0; j < ((ArrayList<County>) (scope.getCounties())).size(); j++) {
//                for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//                    if (((String) (((State) (((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(scope.getName())) {
//                        if (((County) (((ArrayList<County>) (scope.getCounties())).get(j))).getId() == ((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getId()) {
//                            if (((ZonedDateTime) (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).truncatedTo(ChronoUnit.DAYS))).equals(((ZonedDateTime) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getDate()))) == true) {
//                                numActiveInfected = numActiveInfected + ((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getNumActiveCases();
//                                //println(d);
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof Country) {
//            System.out.println("Infection for country level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof City) {
//            City scope = (City) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            int scopePopulation = scope.getPopulation();
//            boolean hasStarted = false;
//            boolean hasEnded = false;
//            for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//                if (((String) (((State) (((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(((State) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(0))).getState())).getName())) {
//                    if (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(0))).getCounty())).getId() == ((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getId()) {
//                        if (hasStarted == false) {
//                            System.out.println("STARTED!");
//                            startCountyIndex = d;
//                            hasStarted = true;
//                        }
//                    } else {
//                        if (hasStarted == true) {
//                            endCountyIndex = d;
//                            hasEnded = true;
//                            break;
//                        }
//                    }
//                } else {
//                    if (hasStarted == true) {
//                        endCountyIndex = d;
//                        hasEnded = true;
//                        break;
//                    }
//                }
//            }
//        } else {
//            System.out.println("Infection for less than county level not implemented yet!");
//        }
//
//        ArrayList<County> counties = new ArrayList();
//        for (int i = 0; i < ((City) modelRoot.ABM.studyScopeGeography).censusTracts.size(); i++) {
//            boolean isCountyFound = false;
//            for (int j = 0; j < counties.size(); j++) {
//                if (counties.get(j).id == ((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county.id) {
//                    isCountyFound = true;
//                    break;
//                }
//            }
//            if (isCountyFound == false) {
//                counties.add(((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county);
//            }
//        }
//        int numCounties = counties.size();
//
//        relevantDailyConfirmedCases = new ArrayList();
//
//        for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//            for (int j = 0; j < counties.size(); j++) {
//                if (dailyConfirmedCases.get(d).county.id == counties.get(j).id) {
//                    relevantDailyConfirmedCases.add(dailyConfirmedCases.get(d));
//                }
//            }
//        }
//
//        initialInfectPeopleVDs(modelRoot, vDsList);
//        end = System.currentTimeMillis();
//        System.out.println("Finished infecting people: " + (end - start) + " miliSeconds.");
//
//        initOutputFileHeader(modelRoot, counties);
//
//        counter = 0;
    }

    public void constructorABSVD(MainModel modelRoot) {
//        System.out.println("ROOT CONSTRUCTOR VD: " + Math.random());
//
//        ArrayList patternRecords = modelRoot.getSafegraph().getAllPatterns().getMonthlyPatternsList().get(0).getPatternRecords();
//
//        ArrayList vDsList = makeVDs(modelRoot);
//        System.out.println("VD size: " + vDsList.size());
//
//        runGenPeople(numAgents, modelRoot, patternRecords, -1, vDsList, 1);
//
//        ArrayList<County> counties = new ArrayList();
//        for (int i = 0; i < ((City) modelRoot.ABM.studyScopeGeography).censusTracts.size(); i++) {
//            boolean isCountyFound = false;
//            for (int j = 0; j < counties.size(); j++) {
//                if (counties.get(j).id == ((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county.id) {
//                    isCountyFound = true;
//                    break;
//                }
//            }
//            if (isCountyFound == false) {
//                counties.add(((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county);
//            }
//        }
//        int numCounties = counties.size();
//
//        ArrayList<DailyConfirmedCases> dailyConfirmedCases = ((CovidCsseJhu) (modelRoot.covidCsseJhu)).dailyConfirmedCasesList;
//
//        relevantDailyConfirmedCases = new ArrayList();
//
//        for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//            for (int j = 0; j < counties.size(); j++) {
//                if (dailyConfirmedCases.get(d).county.id == counties.get(j).id) {
//                    relevantDailyConfirmedCases.add(dailyConfirmedCases.get(d));
//                }
//            }
//        }
//
//        initialInfectPeopleVDs(modelRoot, vDsList);
//
//        initOutputFileHeader(modelRoot, counties);
    }

    public void initOutputFileHeader(MainModel modelRoot, ArrayList<County> counties) {
//        int numCounties = counties.size();
//
//        if (modelRoot.ABM.studyScope.equals("USA_NY_Richmond County_New York")) {
//
//            try {
//                //String data = "Date,Susceptible,Exposed,Infected_sym,Infected_asym,Recovered,Dead,UNKNOWN,SimulatedInfectedToPop,REALInfected,RealInfectedToPop\n";
//                String data = "Date";
//                for (int n = 0; n < numCounties; n++) {
//                    data = data + "," + counties.get(n).name + "_Susceptible,";
//                    data = data + counties.get(n).name + "_Exposed,";
//                    data = data + counties.get(n).name + "_Infected_sym,";
//                    data = data + counties.get(n).name + "_Infected_asym,";
//                    data = data + counties.get(n).name + "_Recovered,";
//                    data = data + counties.get(n).name + "_Dead,";
//                    data = data + counties.get(n).name + "_UNKNOWN,";
//                    data = data + counties.get(n).name + "_SimulatedInfectedToPop,";
//                    data = data + counties.get(n).name + "_REALInfected,";
//                    data = data + counties.get(n).name + "_RealInfectedToPop";
//                }
//                data = data + "\n";
//                File f1 = new File("./output_NEWYORK_EXCEPTION_" + modelRoot.ABM.studyScope + "_" + modelRoot.scenario + "_" + modelRoot.ABM.startTime.getYear() + "_" + modelRoot.ABM.startTime.getMonth() + "_" + modelRoot.sparsifyFraction + ".csv");
//                if (!f1.exists()) {
//                    f1.createNewFile();
//                }
//
//                FileWriter fileWritter = new FileWriter(f1.getName(), false);
//                BufferedWriter bw = new BufferedWriter(fileWritter);
//                bw.write(data);
//                bw.close();
//                System.out.println("Done");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        try {
//            String data = "Date,Susceptible,Exposed,Infected_sym,Infected_asym,Recovered,Dead,UNKNOWN,SimulatedInfectedToPop,REALInfected,RealInfectedToPop\n";
//            File f1 = new File("./output_" + modelRoot.ABM.studyScope + "_" + modelRoot.scenario + "_" + modelRoot.ABM.startTime.getYear() + "_" + modelRoot.ABM.startTime.getMonth() + "_" + modelRoot.sparsifyFraction + ".csv");
//            if (!f1.exists()) {
//                f1.createNewFile();
//            }
//
//            FileWriter fileWritter = new FileWriter(f1.getName(), false);
//            BufferedWriter bw = new BufferedWriter(fileWritter);
//            bw.write(data);
//            bw.close();
//            System.out.println("Done");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void constructorCBG(MainModel modelRoot, double sparsifyPercentage) {
//        System.out.println("ROOT CONSTRUCTOR CBG: " + Math.random());
//
//        ArrayList patternRecords = modelRoot.getSafegraph().getAllPatterns().getMonthlyPatternsList().get(0).getPatternRecords();
//
//        int numAllVisits = getGeneralInformation(patternRecords);
//
//        long start = System.currentTimeMillis();
//
//        ArrayList cBGsList = makeCBGs(modelRoot);
//        System.out.println("CBG size: " + cBGsList.size());
//
//        runGenPeople(numAgents, modelRoot, patternRecords, numAllVisits, cBGsList, sparsifyPercentage);
////        runGenPeopleSerially(numAgents, modelRoot, patternRecords, numAllVisits, cBGsList);
//
//        long end = System.currentTimeMillis();
//        System.out.println("Generating people elapsed time: " + (end - start) + " miliSeconds.");
//
//        System.out.println("Initially infect people:");
//        start = System.currentTimeMillis();
//
//        ArrayList<DailyConfirmedCases> dailyConfirmedCases = ((CovidCsseJhu) (modelRoot.covidCsseJhu)).dailyConfirmedCasesList;
//        if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof County) {
//
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof State) {
//            State scope = (State) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            int numActiveInfected = 0;
//            int scopePopulation = scope.getPopulation();
//            for (int j = 0; j < ((ArrayList<County>) (scope.getCounties())).size(); j++) {
//                for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//                    if (((String) (((State) (((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(scope.getName())) {
//                        if (((County) (((ArrayList<County>) (scope.getCounties())).get(j))).getId() == ((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getId()) {
//                            if (((ZonedDateTime) (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).truncatedTo(ChronoUnit.DAYS))).equals(((ZonedDateTime) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getDate()))) == true) {
//                                numActiveInfected = numActiveInfected + ((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getNumActiveCases();
//                                //println(d);
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof Country) {
//            System.out.println("Infection for country level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof City) {
//            City scope = (City) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            int scopePopulation = scope.getPopulation();
//            boolean hasStarted = false;
//            boolean hasEnded = false;
//            for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//                if (((String) (((State) (((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(((State) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(0))).getState())).getName())) {
//                    if (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(0))).getCounty())).getId() == ((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getId()) {
//                        if (hasStarted == false) {
//                            System.out.println("STARTED!");
//                            startCountyIndex = d;
//                            hasStarted = true;
//                        }
//                    } else {
//                        if (hasStarted == true) {
//                            endCountyIndex = d;
//                            hasEnded = true;
//                            break;
//                        }
//                    }
//                } else {
//                    if (hasStarted == true) {
//                        endCountyIndex = d;
//                        hasEnded = true;
//                        break;
//                    }
//                }
//            }
//        } else {
//            System.out.println("Infection for less than county level not implemented yet!");
//        }
//
//        ArrayList<County> counties = new ArrayList();
//        for (int i = 0; i < ((City) modelRoot.ABM.studyScopeGeography).censusTracts.size(); i++) {
//            boolean isCountyFound = false;
//            for (int j = 0; j < counties.size(); j++) {
//                if (counties.get(j).id == ((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county.id) {
//                    isCountyFound = true;
//                    break;
//                }
//            }
//            if (isCountyFound == false) {
//                counties.add(((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county);
//            }
//        }
//        int numCounties = counties.size();
//
//        relevantDailyConfirmedCases = new ArrayList();
//
//        for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//            for (int j = 0; j < counties.size(); j++) {
//                if (dailyConfirmedCases.get(d).county.id == counties.get(j).id) {
//                    relevantDailyConfirmedCases.add(dailyConfirmedCases.get(d));
//                }
//            }
//        }
//
//        initialInfectPeopleCBG(modelRoot, cBGsList);
//        end = System.currentTimeMillis();
//        System.out.println("Finished infecting people: " + (end - start) + " miliSeconds.");
//
//        initOutputFileHeader(modelRoot, counties);
//
//        counter = 0;
    }

    public void constructorCBGVD(MainModel modelRoot) {
//        System.out.println("ROOT CONSTRUCTOR VD: " + Math.random());
//
//        ArrayList patternRecords = modelRoot.getSafegraph().getAllPatterns().getMonthlyPatternsList().get(0).getPatternRecords();
//
//        int numAllVisits = getGeneralInformation(patternRecords);
//
//        long start = System.currentTimeMillis();
//
//        ArrayList cBGVDsList = makeCBGVDs(modelRoot);
//        System.out.println("CBGVD size: " + cBGVDsList.size());
//        
//        if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof Country) {
//            System.out.println("PREPROCESS OF CBG TO CBGVD FOR COUNTRY is not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof State) {
//            System.out.println("PREPROCESS OF CBG TO CBGVD FOR STATE is not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof County) {
//            System.out.println("PREPROCESS OF CBG TO CBGVD FOR COUNTY is not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof City) {
//            System.out.println("START PREPROCESS CBG TO CBGVD CONNECTION");
//            ((City)(modelRoot.ABM.studyScopeGeography)).getCBGVDFromCBGForAllCBGs();
//            System.out.println("FINISHED PREPROCESS CBG TO CBGVD CONNECTION");
//        } else {
//            System.out.println("Infection for less than city level not implemented yet!");
//        }
//
//        runGenPeople(numAgents, modelRoot, patternRecords, numAllVisits, cBGVDsList, 1);
////        runGenPeopleSerially(numAgents, modelRoot, patternRecords, numAllVisits, cBGsList);
//
//        long end = System.currentTimeMillis();
//        System.out.println("Generating people elapsed time: " + (end - start) + " miliSeconds.");
//
//        System.out.println("Initially infect people:");
//        start = System.currentTimeMillis();
//
//        ArrayList<DailyConfirmedCases> dailyConfirmedCases = ((CovidCsseJhu) (modelRoot.covidCsseJhu)).dailyConfirmedCasesList;
//        if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof County) {
//
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof State) {
//            State scope = (State) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            int numActiveInfected = 0;
//            int scopePopulation = scope.getPopulation();
//            for (int j = 0; j < ((ArrayList<County>) (scope.getCounties())).size(); j++) {
//                for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//                    if (((String) (((State) (((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(scope.getName())) {
//                        if (((County) (((ArrayList<County>) (scope.getCounties())).get(j))).getId() == ((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getId()) {
//                            if (((ZonedDateTime) (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).truncatedTo(ChronoUnit.DAYS))).equals(((ZonedDateTime) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getDate()))) == true) {
//                                numActiveInfected = numActiveInfected + ((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getNumActiveCases();
//                                //println(d);
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof Country) {
//            System.out.println("Infection for country level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof City) {
//            City scope = (City) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            int scopePopulation = scope.getPopulation();
//            boolean hasStarted = false;
//            boolean hasEnded = false;
//            for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//                if (((String) (((State) (((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(((State) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(0))).getState())).getName())) {
//                    if (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(0))).getCounty())).getId() == ((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getId()) {
//                        if (hasStarted == false) {
//                            System.out.println("STARTED!");
//                            startCountyIndex = d;
//                            hasStarted = true;
//                        }
//                    } else {
//                        if (hasStarted == true) {
//                            endCountyIndex = d;
//                            hasEnded = true;
//                            break;
//                        }
//                    }
//                } else {
//                    if (hasStarted == true) {
//                        endCountyIndex = d;
//                        hasEnded = true;
//                        break;
//                    }
//                }
//            }
//        } else {
//            System.out.println("Infection for less than city level not implemented yet!");
//        }
//
//        ArrayList<County> counties = new ArrayList();
//        for (int i = 0; i < ((City) modelRoot.ABM.studyScopeGeography).censusTracts.size(); i++) {
//            boolean isCountyFound = false;
//            for (int j = 0; j < counties.size(); j++) {
//                if (counties.get(j).id == ((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county.id) {
//                    isCountyFound = true;
//                    break;
//                }
//            }
//            if (isCountyFound == false) {
//                counties.add(((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county);
//            }
//        }
//        int numCounties = counties.size();
//
//        relevantDailyConfirmedCases = new ArrayList();
//
//        for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//            for (int j = 0; j < counties.size(); j++) {
//                if (dailyConfirmedCases.get(d).county.id == counties.get(j).id) {
//                    relevantDailyConfirmedCases.add(dailyConfirmedCases.get(d));
//                }
//            }
//        }
//
//        initialInfectPeopleCBGVDs(modelRoot, cBGVDsList);
//        end = System.currentTimeMillis();
//        System.out.println("Finished infecting people: " + (end - start) + " miliSeconds.");
//
//        initOutputFileHeader(modelRoot, counties);
//
//        counter = 0;
    }

    public void runShamil() {
        Duration d = Duration.between(myModelRoot.ABM.startTime, myModelRoot.ABM.currentTime);
        int day = (int) (d.toDays());
        if (myModelRoot.ABM.currentTime.getHour() == 0 && myModelRoot.ABM.currentTime.getMinute() == 0) {
            startDay(people, day);
        }
        if (myModelRoot.ABM.currentTime.getMinute() == 0) {
            updateHour(people, myModelRoot.ABM.currentTime.getHour(), day);
        }
        if (myModelRoot.ABM.currentTime.getHour() == 23 && myModelRoot.ABM.currentTime.getMinute() == 59) {
            endDay(people, day);
            int sumInfected = 0;
            for (int i = 0; i < people.size(); i++) {
//                if (people.get(i).shamilPersonProperties.isInfected == true) {
//                    System.out.println();
//                    System.out.println("isInfected: " + people.get(i).shamilPersonProperties.isInfected);
//                    System.out.println("state: " + people.get(i).shamilPersonProperties.state);
//                    System.out.println("isAlive: " + people.get(i).shamilPersonProperties.isAlive);
//                    System.out.println();
//                }
                if (people.get(i).shamilPersonProperties.isInfected == true && !people.get(i).shamilPersonProperties.state.equals("recovered") && people.get(i).shamilPersonProperties.isAlive == true) {
                    sumInfected = sumInfected + 1;
                }
            }
            System.out.println("Day: " + day + " sumInfected: " + sumInfected);
        }
    }

    @Override
    public void behavior() {
        if (myModelRoot.ABM.isOurABMActive == true && myModelRoot.ABM.isShamilABMActive == true) {
            ShamilSimulatorController.convertOurToShamil(people);
            runShamil();
            ShamilSimulatorController.convertShamilToOur(people);
            ArrayList<POI> values = new ArrayList<>(pOIs.values());
            for (int i = 0; i < values.size(); i++) {
                values.get(i).updateContamination();
            }
        } else if (myModelRoot.ABM.isOurABMActive == false && myModelRoot.ABM.isShamilABMActive == true) {
            runShamil();
        } else if (myModelRoot.ABM.isOurABMActive == true && myModelRoot.ABM.isShamilABMActive == false) {
            handleHomeWorkActivities(myModelRoot.ABM.currentTime);
            handleInfectionProgress();
            ArrayList<POI> values = new ArrayList<>(pOIs.values());
            for (int i = 0; i < values.size(); i++) {
                values.get(i).updateContamination();
            }
            reportOurABMInfection(false);
        }
//        writeMinuteRecord(modelRoot, currentAgent, modelRoot.getABM().getCurrentTime());
//        writeDailyContactRate(modelRoot);
    }

    public void reportOurABMInfection(boolean forceReport) {
        if (forceReport == true) {
            Duration d = Duration.between(myModelRoot.ABM.startTime, myModelRoot.ABM.currentTime);
            int day = (int) (d.toDays());
            int sumInfected = 0;
            for (int i = 0; i < people.size(); i++) {
                if (people.get(i).properties.status == statusEnum.INFECTED_ASYM.ordinal() || people.get(i).properties.status == statusEnum.INFECTED_SYM.ordinal()) {
                    sumInfected = sumInfected + 1;
                }
            }
            System.out.println("Day: " + day + " sumInfected: " + sumInfected);
        } else {
            if (myModelRoot.ABM.currentTime.getHour() == 23 && myModelRoot.ABM.currentTime.getMinute() == 59) {
                Duration d = Duration.between(myModelRoot.ABM.startTime, myModelRoot.ABM.currentTime);
                int day = (int) (d.toDays());
                int sumInfected = 0;
                for (int i = 0; i < people.size(); i++) {
                    if (people.get(i).properties.status == statusEnum.INFECTED_ASYM.ordinal() || people.get(i).properties.status == statusEnum.INFECTED_SYM.ordinal()) {
                        sumInfected = sumInfected + 1;
                    }
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
        if (person.properties.status == statusEnum.INFECTED_SYM.ordinal() || person.properties.status == statusEnum.INFECTED_ASYM.ordinal()) {//if infected
            if (person.properties.minutesSick == -1) {
                person.properties.minutesSick = 1;
                if (Math.random() < 0.02) {//0.0018
                    person.properties.isDestinedToDeath = true;
                } else {
                    person.properties.isDestinedToDeath = false;
                }
            } else {
                int minsSick = person.properties.minutesSick;
                person.properties.minutesSick = minsSick + 1;
                if (person.properties.isDestinedToDeath == false) {
                    if (minsSick > 20160) {
                        if (Math.random() < Math.pow((double) (minsSick - 30240) / (double) (30240), 5)) {
//                            person.properties.minutesSick = -1;
                            person.properties.status = statusEnum.RECOVERED.ordinal();//RECOVERED
                        }
                    }
                } else {
                    if (minsSick > 10080) {
                        if (Math.random() < Math.pow((double) (minsSick - 20160) / (double) (20160), 5)) {
                            person.properties.minutesSick = -1;
                            person.properties.status = statusEnum.DEAD.ordinal();
                        }
                    }
                }
            }
        }
        if (person.properties.status == statusEnum.RECOVERED.ordinal()) {
            int minsSick = person.properties.minutesSick;
            person.properties.minutesSick = minsSick + 1;
            if (minsSick > 86400) {
                person.properties.minutesSick = -1;
                person.properties.status = statusEnum.SUSCEPTIBLE.ordinal();
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
                        int minutesTravelToWorkFrom7 = (int) (Math.random() * 120);
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
                    int minutesTravelFromWorkFrom16 = (int) (Math.random() * 120);
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

    public void calcContactRate(MainModel modelRoot) {
        if (modelRoot.ABM.isReportContactRate == true) {
            for (int i = 0; i < modelRoot.ABM.agents.size(); i++) {
                if (modelRoot.ABM.agents.get(i).myType.equals("Person")) {

                }
            }
        }
    }

    public void writeDailyContactRate(MainModel modelRoot) {
        if (modelRoot.ABM.isReportContactRate == true) {
            ZonedDateTime currentDate = modelRoot.getABM().getCurrentTime();
            if (currentDate.getHour() == 0 && currentDate.getMinute() == 1) {
                //System.out.println("FFF: "+counter);
                if (currentAgent.counter == 110) {
//                ArrayList<String[]> data = new ArrayList();
//                for (int i = 0; i < agentPairContact.length; i++) {
//                    String[] row = new String[agentPairContact[i].length];
//                    for (int j = 0; j < agentPairContact[i].length; j++) {
//                        row[j] = String.valueOf(agentPairContact[i][j]);
////                        if (agentPairContact[i][j] != 0) {
////                            System.out.println("!!!!!!!!!!!!!!!!!!!!");
////                        }
//                    }
//                    data.add(row);
//                }
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

//                CSVWriter writer;
//                try {
//                    writer = new CSVWriter(new FileWriter(modelRoot.ABM.studyScope + "_agentPairContact.csv"));
//                    for (int i = 0; i < agentPairContact.length; i++) {
//                        String[] row = new String[agentPairContact[i].length];
//                        for (int j = 0; j < agentPairContact[i].length; j++) {
//                            row[j] = String.valueOf(agentPairContact[i][j]);
////                        if (agentPairContact[i][j] != 0) {
////                            System.out.println("!!!!!!!!!!!!!!!!!!!!");
////                        }
//                        }
//                        writer.writeNext(row);
//                    }
//                    writer.flush();
//                    writer.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Root.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                writer=null;
                    System.gc();

//                try {
//                    CSVWriter writer;
//                    writer = new CSVWriter(new FileWriter(modelRoot.ABM.studyScope + "_agentPairContact.csv"));
//                    writer.writeAll(data);
//                    writer.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Root.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                CsvWriter writer = new CsvWriter();
//                try {
//                    writer.write(new File(modelRoot.ABM.studyScope + "_agentPairContact.csv"), Charset.forName("US-ASCII"), data);
//                    //writer.
//                } catch (IOException ex) {
//                    Logger.getLogger(Root.class.getName()).log(Level.SEVERE, null, ex);
//                }
                }
            }
        }
    }

//@CompileStatic
    int getGeneralInformation(ArrayList patternRecords) {
        //GET ALL NUM PEOPLE (VISITS)
        System.out.println("one pass on all patterns to detect number of people ...");
        int numAllVisits = 0;
        for (int i = 0; i < patternRecords.size(); i++) {
            if (isLocalAllowed == false) {
                int naics_code = ((PatternsRecordProcessed) patternRecords.get(i)).place.naics_code;
                if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
                    numAllVisits = numAllVisits + ((PatternsRecordProcessed) patternRecords.get(i)).getRaw_visitor_counts();
                }
            } else {
                numAllVisits = numAllVisits + ((PatternsRecordProcessed) patternRecords.get(i)).getRaw_visitor_counts();
            }
            //println("lat: "+patternRecords.get(i).getPlace().getLat());
            //println("lon: "+patternRecords.get(i).getPlace().getLon());
        }
        System.out.println("numAllVisits: " + numAllVisits);
        return numAllVisits;
    }

////@CompileStatic
//    public class ParallelAgentGenerator extends ParallelProcessor {
//
//        public int threadIndex;
//        public int numAllVisits;
//        public MainModel modelRoot;
//        public ArrayList patternRecords;
//        public ArrayList cBGs;
//
//        //@CompileStatic
//        public ParallelAgentGenerator(int passed_threadIndex, int passed_numAllVisits, MainModel passed_modelRoot, ArrayList passed_patternRecords, Object parent, Object data, ArrayList passed_cBGs, int startIndex, int endIndex, double sparsifyPercentage) {
//            super(parent, data, startIndex, endIndex);
//            threadIndex = passed_threadIndex;
//            numAllVisits = passed_numAllVisits;
//            modelRoot = passed_modelRoot;
//            patternRecords = passed_patternRecords;
//            cBGs = passed_cBGs;
//            myThread = new Thread(new Runnable() {
//                @Override
//                //@CompileStatic
//                public void run() {
//                    System.out.println("generate people");
//                    if (modelRoot.scenario.equals("CBG")) {
//                        runParallelCBG(modelRoot, patternRecords, numAllVisits, cBGs, startIndex, endIndex, threadIndex, sparsifyPercentage);
//                    } else if (modelRoot.scenario.equals("VD")) {
//                        runParallelVD(modelRoot, patternRecords, numAllVisits, cBGs, startIndex, endIndex, threadIndex);
//                    } else if (modelRoot.scenario.equals("CBGVD")) {
//                        runParallelCBGVD(modelRoot, patternRecords, numAllVisits, cBGs, startIndex, endIndex, threadIndex);
//                    } else if (modelRoot.scenario.equals("ABSVD")) {
//                        runParallelABSVD(modelRoot, patternRecords, cBGs, startIndex, endIndex, threadIndex);
//                    }
//
//                }
//            });
//        }
//
//        void runParallelABSVD(MainModel modelRoot, ArrayList patternRecords, ArrayList cBGs, int startIndex, int endIndex, int threadIndex) {
//            for (int i = startIndex; i < endIndex; i++) {
//
//                if (modelRoot.ABM.studyScopeGeography instanceof Country) {
//
//                }
//                if (modelRoot.ABM.studyScopeGeography instanceof State) {
//
//                }
//                if (modelRoot.ABM.studyScopeGeography instanceof County) {
//
//                }
//                if (modelRoot.ABM.studyScopeGeography instanceof City) {
////                    long start = System.currentTimeMillis();
//                    int sumPopulation = 0;
//                    City scope = (City) (modelRoot.ABM.studyScopeGeography);
//                    sumPopulation = scope.population;
//                    int popGen = (int) (Math.floor(Math.random() * sumPopulation));
//                    int cumulativePop = 0;
//                    int CTIndex = -1;
//                    for (int k = 0; k < scope.censusTracts.size(); k++) {
//                        cumulativePop = cumulativePop + scope.censusTracts.get(k).population;
//                        if (cumulativePop > popGen) {
//                            CTIndex = k;
//                            break;
//                        }
//                    }
//
//                    int cBGIndex = (int) (Math.floor(Math.random() * scope.censusTracts.get(CTIndex).censusBlocks.size()));
//                    CensusBlockGroup selectedCBG = scope.censusTracts.get(CTIndex).censusBlocks.get(cBGIndex);
//
////                    long end = System.currentTimeMillis();
////                    System.out.println("CBG SELECTED: "+(end-start));
////                    start = System.currentTimeMillis();
//                    Person agent = (Person) modelRoot.getABM().makeAgentByType("Person");
//
//                    System.out.println("AGENT GET INDEX: " + agent.getMyIndex());
//
//                    agent.isAtWork = false;
//
//                    agent.homeCBG = selectedCBG;
//                    agent.homeVD = (VDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(selectedCBG, true))[0];
//
//                    for (int n = 0; n < cBGs.size(); n++) {
//                        //println("%%%");
////                    System.out.println(selectedCBG);
////                    System.out.println(cBGs.get(n));
//                        if (((VD) (cBGs.get(n))).vdVal == null) {
//                            System.out.println(cBGs.get(n));
//                        }
//                        if (((CensusBlockGroup) selectedCBG) == null) {
//                            System.out.println(((CensusBlockGroup) selectedCBG));
//                        }
//                        if (selectedCBG == null) {
//                            System.out.println(selectedCBG);
//                        }
//                        if (cBGs.get(n) == null) {
//                            System.out.println(cBGs.get(n));
//                        }
//                        if (((VDCell) agent.homeVD).shopPlacesKeys.get(0).equals((((VD) (cBGs.get(n))).vdVal).shopPlacesKeys.get(0))) {
//                            ((VD) (cBGs.get(n))).N = (int) (((VD) (cBGs.get(n))).N) + 1;
//                            ((VD) (cBGs.get(n))).S = (int) (((VD) (cBGs.get(n))).S) + 1;
//                            agent.vD = ((VD) (cBGs.get(n)));
//                            //println("AGENT CBG SET: "+agent.getMyIndex());
//                            //println("AGENT IN THREAD: "+threadIndex);
//                            break;
//                        }
//                    }
//
////                    end = System.currentTimeMillis();
////
////                    System.out.println("ADD AGENT TO VD: "+(end-start));
////                    
////                    start = System.currentTimeMillis();
//                    agent.dayVD = agent.homeVD;
//                    agent.status = statusEnum.SUSCEPTIBLE.ordinal();
//                    agent.minutesSick = -1;
//                    agent.minutesTravelToWorkFrom7 = -1;
//                    agent.minutesTravelFromWorkFrom16 = -1;
//                    agent.currentLocationVD = agent.homeVD;
//
//                    agent.lat = (agent.currentLocationVD).getLat();
//                    agent.lon = (agent.currentLocationVD).getLon();
//
//                    //println("lat: "+agent.getPropertyValue("currentLocation").getLat());
//                    //println("lon: "+agent.getPropertyValue("currentLocation").getLon());
//                    ArrayList destinationPlaces = new ArrayList();
//                    ArrayList destinationPlacesFreq = new ArrayList();
//
//                    for (int j = 0; j < patternRecords.size(); j++) {
//                        if (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place() != null) {
//                            int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                            if (isShop(naics_code) || isSchool(naics_code) || isReligiousOrganization(naics_code)) {
//                                for (int k = 0; k < ((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).size(); k++) {
//                                    Object[] vDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()), false));
//                                    VDCell vd = (VDCell) (vDReturn[0]);
//                                    if (vd != null) {
//                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (vDReturn[1])));
//                                    }
//
//                                }
//
//                            }
//                        }
//                    }
//
//                    double first = Double.POSITIVE_INFINITY;
//                    double second = Double.POSITIVE_INFINITY;
//                    double third = Double.POSITIVE_INFINITY;
//                    double fourth = Double.POSITIVE_INFINITY;
//                    double fith = Double.POSITIVE_INFINITY;
//                    double sxith = Double.POSITIVE_INFINITY;
//                    PatternsRecordProcessed firstPattern = null;
//                    PatternsRecordProcessed secondPattern = null;
//                    PatternsRecordProcessed thirdPattern = null;
//                    PatternsRecordProcessed fourthPattern = null;
//                    PatternsRecordProcessed fithPattern = null;
//                    PatternsRecordProcessed sxithPattern = null;
////                    double firstFreq;
////                    double secondFreq;
////                    double thirdFreq;
////                    double fourthFreq;
////                    double fithFreq;
////                    double sxithFreq;
//                    for (int j = 0; j < destinationPlaces.size(); j++) {
//                        double dist = Math.sqrt(Math.pow(((PatternsRecordProcessed) (destinationPlaces.get(j))).place.lat - agent.homeVD.lat, 2) + Math.pow(((PatternsRecordProcessed) (destinationPlaces.get(j))).place.lon - agent.homeVD.lon, 2));
//                        if (dist < first) {
//                            firstPattern = (PatternsRecordProcessed) (destinationPlaces.get(j));
//                        }
//                        if (dist < second) {
//                            secondPattern = (PatternsRecordProcessed) (destinationPlaces.get(j));
//                        }
//                        if (dist < third) {
//                            thirdPattern = (PatternsRecordProcessed) (destinationPlaces.get(j));
//                        }
//                        if (dist < fourth) {
//                            fourthPattern = (PatternsRecordProcessed) (destinationPlaces.get(j));
//                        }
//                        if (dist < fith) {
//                            fithPattern = (PatternsRecordProcessed) (destinationPlaces.get(j));
//                        }
//                        if (dist < sxith) {
//                            sxithPattern = (PatternsRecordProcessed) (destinationPlaces.get(j));
//                        }
//                    }
//
//                    destinationPlaces = new ArrayList();
//                    destinationPlaces.add(firstPattern);
//                    destinationPlaces.add(secondPattern);
//                    destinationPlaces.add(thirdPattern);
//                    destinationPlaces.add(fourthPattern);
//                    destinationPlaces.add(fithPattern);
//                    destinationPlaces.add(sxithPattern);
//                    destinationPlacesFreq = new ArrayList();
//                    destinationPlacesFreq.add(30);
//                    destinationPlacesFreq.add(14);
//                    destinationPlacesFreq.add(11);
//                    destinationPlacesFreq.add(9);
//                    destinationPlacesFreq.add(6);
//                    destinationPlacesFreq.add(3);
//
////                    end = System.currentTimeMillis();
////
////                    System.out.println("SET DESTINATIONS: "+(end-start));
////                    
////                    start = System.currentTimeMillis();
//                    agent.destinationPlaces = destinationPlaces;
//                    agent.destinationPlacesFreq = destinationPlacesFreq;
//                    agent.travelStartDecisionCounter = 0;
//                    agent.dstIndex = -1;//THIS IS USED TO DETECT IF THE PERSON IS AT HOME OR NOT
//
//                    int cumulativeDestinationFreqs = 0;
//                    for (int k = 0; k < destinationPlacesFreq.size(); k++) {
//                        cumulativeDestinationFreqs = cumulativeDestinationFreqs + ((Integer) (destinationPlacesFreq.get(k)));
//                    }
//
////                    end = System.currentTimeMillis();
////                    System.out.println("CUMULATIVE FREQS: "+(end-start));
//                    agent.cumulativeDestinationFreqs = cumulativeDestinationFreqs;
//
//                }
//            }
//        }
//
////@CompileStatic
//        void runParallelCBG(MainModel modelRoot, ArrayList patternRecords, int numAllVisits, ArrayList cBGs, int startIndex, int endIndex, int threadIndex, double sparsifyPercentage) {
//
//            for (int i = startIndex; i < endIndex; i++) {
//                int cumulativeNumPeopleIndex = (int) ((Math.random() * (numAllVisits - 1)));
//                //println("cumulativeNumPeopleIndex: "+cumulativeNumPeopleIndex);
//                int cumulativeNumPeople = 0;
//                int selectedIndex = -1;
//                for (int k = 0; k < patternRecords.size(); k++) {
//                    if (isLocalAllowed == false) {
//                        int naics_code = ((PatternsRecordProcessed) patternRecords.get(k)).place.naics_code;
//                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                            cumulativeNumPeople = cumulativeNumPeople + ((PatternsRecordProcessed) (patternRecords.get(k))).getRaw_visitor_counts();
//                            //println("cumulativeNumPeople: "+cumulativeNumPeople);
//                            if (cumulativeNumPeople >= cumulativeNumPeopleIndex) {
//                                if (((PatternsRecordProcessed) (patternRecords.get(k))).getVisitor_home_cbgs_place() != null) {
//                                    selectedIndex = k;
//                                    break;
//                                }
//                            }
//                        }
//                    } else {
//                        cumulativeNumPeople = cumulativeNumPeople + ((PatternsRecordProcessed) (patternRecords.get(k))).getRaw_visitor_counts();
//                        //println("cumulativeNumPeople: "+cumulativeNumPeople);
//                        if (cumulativeNumPeople >= cumulativeNumPeopleIndex) {
//                            if (((PatternsRecordProcessed) (patternRecords.get(k))).getVisitor_home_cbgs_place() != null) {
//                                selectedIndex = k;
//                                break;
//                            }
//                        }
//                    }
//                }
//                //println("selectedIndex: "+selectedIndex);
//                Person agent = (Person) modelRoot.getABM().makeAgentByType("Person");
//
//                System.out.println("AGENT GET INDEX: " + agent.getMyIndex());
//
//                agent.isAtWork = false;
//
//                //\/\/\/ Select home of agent
//                int numAllPeopleHomes = 0;
//                for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).size(); j++) {
//                    if (isLocalAllowed == false) {
//                        int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                            numAllPeopleHomes = numAllPeopleHomes + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j))).getValue());
//                        }
//                    } else {
//                        numAllPeopleHomes = numAllPeopleHomes + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j))).getValue());
//                    }
//
//                }
//
//                int cumulativePeopleHomesIndex = (int) ((Math.random() * (numAllPeopleHomes - 1)));
//                cumulativeNumPeople = 0;
//                CensusBlockGroup selectedCBG = null;
//                for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).size(); j++) {
//                    if (isLocalAllowed == false) {
//                        int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                            cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getValue());
//                            if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                                //println("j: "+j);
//                                selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getKey());
//                                break;
//                            }
//                        }
//                    } else {
//                        cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getValue());
//                        if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                            //println("j: "+j);
//                            selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getKey());
//                            break;
//                        }
//                    }
//
//                }
//                if (selectedCBG == null) {
//                    System.out.println("SEVERE ERROR: HOME CENSUS BLOCK NOT FOUND!");
//                }
//                agent.homeCBG = selectedCBG;
//
//                //println("@@@");
//                for (int n = 0; n < cBGs.size(); n++) {
//                    //println("%%%");
////                    System.out.println(selectedCBG);
////                    System.out.println(cBGs.get(n));
//                    if (((CBG) (cBGs.get(n))).cbgVal == null) {
//                        System.out.println(cBGs.get(n));
//                    }
//                    if (((CensusBlockGroup) selectedCBG) == null) {
//                        System.out.println(((CensusBlockGroup) selectedCBG));
//                    }
//                    if (selectedCBG == null) {
//                        System.out.println(selectedCBG);
//                    }
//                    if (cBGs.get(n) == null) {
//                        System.out.println(cBGs.get(n));
//                    }
//                    if (((CensusBlockGroup) selectedCBG).getId() == ((CensusBlockGroup) (((CBG) (cBGs.get(n))).cbgVal)).getId()) {
//                        ((CBG) (cBGs.get(n))).N = (int) (((CBG) (cBGs.get(n))).N) + 1;
//                        ((CBG) (cBGs.get(n))).S = (int) (((CBG) (cBGs.get(n))).S) + 1;
//                        agent.cBG = ((CBG) (cBGs.get(n)));
//                        //println("AGENT CBG SET: "+agent.getMyIndex());
//                        //println("AGENT IN THREAD: "+threadIndex);
//                        break;
//                    }
//                }
//
//                //println("###");
//                //^^^ Select home of agent
//                //\/\/\/ Select daytime cbg of agent
//                int numAllPeopleWorkplaces = 0;
//                if (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()) != null) {
//                    for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).size(); j++) {
//                        numAllPeopleWorkplaces = numAllPeopleWorkplaces + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j))).getValue());
//                    }
//                    int cumulativePeopleDaytimeIndex = (int) ((Math.random() * (numAllPeopleWorkplaces - 1)));
//                    cumulativeNumPeople = 0;
//                    selectedCBG = null;
//                    for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).size(); j++) {
//                        cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j)).getValue());
//                        if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                            //println("j: "+j);
//                            selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j)).getKey());
//                            break;
//                        }
//                    }
//                    if (selectedCBG == null) {
//                        System.out.println("KNOWN EXCEPTION: DAYTIME CENSUS BLOCK NOT FOUND!");
//                        System.out.println("USING HOME AS WORK CENSUS BLOCK");
//
//                        selectedCBG = (CensusBlockGroup) (agent.homeCBG);
//
//                    }
//                } else {
//                    if (selectedCBG == null) {
//                        System.out.println("KNOWN EXCEPTION: DAYTIME CENSUS BLOCK NOT FOUND!");
//                        System.out.println("USING HOME AS WORK CENSUS BLOCK");
//                        selectedCBG = (CensusBlockGroup) (agent.homeCBG);
//                    }
//                }
//
//                long start = System.currentTimeMillis();
//
//                agent.dayCBG = selectedCBG;
//
//                //^^^ Select daytime cbg of agent
//                //println("lat: "+selectedCBG.getLat());
//                //println("lon: "+selectedCBG.getLon());
//                agent.status = statusEnum.SUSCEPTIBLE.ordinal();
//                agent.minutesSick = -1;
//                agent.minutesTravelToWorkFrom7 = -1;
//                agent.minutesTravelFromWorkFrom16 = -1;
//                agent.currentLocationCBG = agent.homeCBG;
//
//                agent.lat = ((CensusBlockGroup) (agent.currentLocationCBG)).getLat();
//                agent.lon = ((CensusBlockGroup) (agent.currentLocationCBG)).getLon();
//
//                //println("lat: "+agent.getPropertyValue("currentLocation").getLat());
//                //println("lon: "+agent.getPropertyValue("currentLocation").getLon());
//                ArrayList destinationPlaces = new ArrayList();
//                ArrayList destinationPlacesFreq = new ArrayList();
//
//                //println("&&&&");
//                for (int j = 0; j < patternRecords.size(); j++) {
//                    if (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place() != null) {
//                        for (int k = 0; k < ((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).size(); k++) {
//                            if (isLocalAllowed == false) {
//                                int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                                if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                                    if (((CensusBlockGroup) (agent.homeCBG)).id == ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).getId()) {
//                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue());
//                                    }
//                                }
//                            } else {
//                                if (((CensusBlockGroup) (agent.homeCBG)).id == ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).getId()) {
//                                    destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                    destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue());
//                                }
//                            }
//
//                        }
//                    }
//                }
//
//                for (int m = 0; m < destinationPlacesFreq.size(); m++) {
//                    destinationPlacesFreq.set(m, (int) (Math.floor(((int) (destinationPlacesFreq.get(m))) * sparsifyPercentage)));
//                }
//
//                agent.destinationPlaces = destinationPlaces;
//                agent.destinationPlacesFreq = destinationPlacesFreq;
//                agent.travelStartDecisionCounter = 0;
//                agent.dstIndex = -1;//THIS IS USED TO DETECT IF THE PERSON IS AT HOME OR NOT
//
//                long end = System.currentTimeMillis();
//
//                System.out.println("SET DEST DURATION: " + (end - start));
//
//                int cumulativeDestinationFreqs = 0;
//                for (int k = 0; k < destinationPlacesFreq.size(); k++) {
//                    cumulativeDestinationFreqs = cumulativeDestinationFreqs + (Integer) (destinationPlacesFreq.get(k));
//                }
//                agent.cumulativeDestinationFreqs = cumulativeDestinationFreqs;
//            }
//
//        }
//
//        void runParallelVD(MainModel modelRoot, ArrayList patternRecords, int numAllVisits, ArrayList cBGs, int startIndex, int endIndex, int threadIndex) {
//
//            for (int i = startIndex; i < endIndex; i++) {
//                int cumulativeNumPeopleIndex = (int) ((Math.random() * (numAllVisits - 1)));
//                //println("cumulativeNumPeopleIndex: "+cumulativeNumPeopleIndex);
//                int cumulativeNumPeople = 0;
//                int selectedIndex = -1;
//                for (int k = 0; k < patternRecords.size(); k++) {
//                    if (isLocalAllowed == false) {
//                        int naics_code = ((PatternsRecordProcessed) patternRecords.get(k)).place.naics_code;
//                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                            cumulativeNumPeople = cumulativeNumPeople + ((PatternsRecordProcessed) (patternRecords.get(k))).getRaw_visitor_counts();
//                            //println("cumulativeNumPeople: "+cumulativeNumPeople);
//                            if (cumulativeNumPeople >= cumulativeNumPeopleIndex) {
//                                if (((PatternsRecordProcessed) (patternRecords.get(k))).getVisitor_home_cbgs_place() != null) {
//                                    selectedIndex = k;
//                                    break;
//                                }
//                            }
//                        }
//                    } else {
//                        cumulativeNumPeople = cumulativeNumPeople + ((PatternsRecordProcessed) (patternRecords.get(k))).getRaw_visitor_counts();
//                        //println("cumulativeNumPeople: "+cumulativeNumPeople);
//                        if (cumulativeNumPeople >= cumulativeNumPeopleIndex) {
//                            if (((PatternsRecordProcessed) (patternRecords.get(k))).getVisitor_home_cbgs_place() != null) {
//                                selectedIndex = k;
//                                break;
//                            }
//                        }
//                    }
//                }
//                //println("selectedIndex: "+selectedIndex);
//                Person agent = (Person) modelRoot.getABM().makeAgentByType("Person");
//
//                System.out.println("AGENT GET INDEX: " + agent.getMyIndex());
//
//                agent.isAtWork = false;
//
//                //\/\/\/ Select home of agent
//                int numAllPeopleHomes = 0;
//                for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).size(); j++) {
//                    if (isLocalAllowed == false) {
//                        int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                            numAllPeopleHomes = numAllPeopleHomes + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j))).getValue());
//                        }
//                    } else {
//                        numAllPeopleHomes = numAllPeopleHomes + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j))).getValue());
//                    }
//
//                }
//
//                int missingNumTravels = ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).raw_visit_counts - numAllPeopleHomes;
//
//                int cumulativePeopleHomesIndex = (int) ((Math.random() * (numAllPeopleHomes - 1)));
//                cumulativeNumPeople = 0;
//                CensusBlockGroup selectedCBG = null;
//                VDCell selectedVD = null;
//                for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).size(); j++) {
//                    if (isLocalAllowed == false) {
//                        int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                            cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getValue());
//                            if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                                //println("j: "+j);
//                                selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getKey());
//                                break;
//                            }
//                        }
//                    } else {
//                        cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getValue());
//                        if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                            //println("j: "+j);
//                            selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getKey());
//                            break;
//                        }
//                    }
//
//                }
//                if (selectedCBG == null) {
//                    System.out.println("SEVERE ERROR: HOME CENSUS BLOCK NOT FOUND!");
//                }
//                agent.homeCBG = selectedCBG;
//                agent.homeVD = (VDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(selectedCBG, true))[0];
//
//                for (int n = 0; n < cBGs.size(); n++) {
//                    //println("%%%");
////                    System.out.println(selectedCBG);
////                    System.out.println(cBGs.get(n));
//                    if (((VD) (cBGs.get(n))).vdVal == null) {
//                        System.out.println(cBGs.get(n));
//                    }
//                    if (((CensusBlockGroup) selectedCBG) == null) {
//                        System.out.println(((CensusBlockGroup) selectedCBG));
//                    }
//                    if (selectedCBG == null) {
//                        System.out.println(selectedCBG);
//                    }
//                    if (cBGs.get(n) == null) {
//                        System.out.println(cBGs.get(n));
//                    }
//                    if (((VDCell) agent.homeVD).shopPlacesKeys.get(0).equals((((VD) (cBGs.get(n))).vdVal).shopPlacesKeys.get(0))) {
//                        ((VD) (cBGs.get(n))).N = (int) (((VD) (cBGs.get(n))).N) + 1;
//                        ((VD) (cBGs.get(n))).S = (int) (((VD) (cBGs.get(n))).S) + 1;
//                        agent.vD = ((VD) (cBGs.get(n)));
//                        //println("AGENT CBG SET: "+agent.getMyIndex());
//                        //println("AGENT IN THREAD: "+threadIndex);
//                        break;
//                    }
//                }
//
//                //println("###");
//                //^^^ Select home of agent
//                //\/\/\/ Select daytime cbg of agent
//                int numAllPeopleWorkplaces = 0;
//                if (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()) != null) {
//                    for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).size(); j++) {
//                        numAllPeopleWorkplaces = numAllPeopleWorkplaces + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j))).getValue());
//                    }
//                    int cumulativePeopleDaytimeIndex = (int) ((Math.random() * (numAllPeopleWorkplaces - 1)));
//                    cumulativeNumPeople = 0;
//                    selectedCBG = null;
//                    for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).size(); j++) {
//                        cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j)).getValue());
//                        if (cumulativeNumPeople >= cumulativePeopleDaytimeIndex) {
//                            //println("j: "+j);
//                            selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j)).getKey());
//                            break;
//                        }
//                    }
//                    selectedVD = (VDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(selectedCBG, true))[0];
//                    if (selectedCBG == null) {
//                        System.out.println("KNOWN EXCEPTION: DAYTIME CENSUS BLOCK NOT FOUND!");
//                        System.out.println("USING HOME AS WORK CENSUS BLOCK");
//
//                        selectedVD = agent.homeVD;
//
//                    }
//                } else {
//                    if (selectedVD == null) {
//                        System.out.println("KNOWN EXCEPTION: DAYTIME VD NOT FOUND!");
//                        System.out.println("USING HOME AS WORK VD");
//                        selectedVD = agent.homeVD;
//                    }
//                }
//                agent.dayVD = selectedVD;
//
//                //^^^ Select daytime cbg of agent
//                //println("lat: "+selectedCBG.getLat());
//                //println("lon: "+selectedCBG.getLon());
//                agent.status = statusEnum.SUSCEPTIBLE.ordinal();
//                agent.minutesSick = -1;
//                agent.minutesTravelToWorkFrom7 = -1;
//                agent.minutesTravelFromWorkFrom16 = -1;
//                agent.currentLocationVD = agent.homeVD;
//
//                agent.lat = (agent.currentLocationVD).getLat();
//                agent.lon = (agent.currentLocationVD).getLon();
//
//                //println("lat: "+agent.getPropertyValue("currentLocation").getLat());
//                //println("lon: "+agent.getPropertyValue("currentLocation").getLon());
//                ArrayList destinationPlaces = new ArrayList();
//                ArrayList destinationShopPlaces = new ArrayList();
//                ArrayList destinationSchoolPlaces = new ArrayList();
//                ArrayList destinationReligiousPlaces = new ArrayList();
//                ArrayList destinationPlacesFreq = new ArrayList();
//                ArrayList destinationShopPlacesFreq = new ArrayList();
//                ArrayList destinationSchoolPlacesFreq = new ArrayList();
//                ArrayList destinationReligiousPlacesFreq = new ArrayList();
//
//                //println("&&&&");
//                for (int j = 0; j < patternRecords.size(); j++) {
//                    if (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place() != null) {
//                        for (int k = 0; k < ((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).size(); k++) {
//                            if (isLocalAllowed == false) {
//                                int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                                if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                                    Object[] vDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()), false));
//                                    VDCell vd = (VDCell) (vDReturn[0]);
//                                    if (vd != null) {
//                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * (((Double) (vDReturn[1])).floatValue()));
//                                    }
//
//                                }
//                            } else {
//                                int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                                if (isShop(naics_code) == true) {
//                                    Object[] vDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()), false));
//                                    VDCell vd = (VDCell) (vDReturn[0]);
//                                    if (vd != null) {
//                                        destinationShopPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationShopPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * (((Double) (vDReturn[1])).floatValue()));
//                                    }
//                                } else if (isSchool(naics_code) == true) {
//                                    Object[] vDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()), false));
//                                    VDCell vd = (VDCell) (vDReturn[0]);
//                                    if (vd != null) {
//                                        destinationSchoolPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationSchoolPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * (((Double) (vDReturn[1])).floatValue()));
//                                    }
//                                } else if (isReligiousOrganization(naics_code) == true) {
//                                    Object[] vDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()), false));
//                                    VDCell vd = (VDCell) (vDReturn[0]);
//                                    if (vd != null) {
//                                        destinationReligiousPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationReligiousPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * (((Double) (vDReturn[1])).floatValue()));
//                                    }
//                                } else {
//
//                                    if (((CensusBlockGroup) (agent.homeCBG)).id == ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).getId()) {
//                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue());
//                                    }
//
////                                    Object[] vDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()), false));
////                                    VDCell vd = (VDCell) (vDReturn[0]);
////                                    if (vd != null) {
////                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
////                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * (((Double) (vDReturn[1])).floatValue()));
////                                    }
//                                }
//                            }
//
//                        }
//                    }
//                }
//
//                Object shopResults[] = getClosenessAdjustmentVD(agent, destinationShopPlaces, destinationShopPlacesFreq);
//                Object schoolResults[] = getClosenessAdjustmentVD(agent, destinationSchoolPlaces, destinationSchoolPlacesFreq);
//                Object religiousResults[] = getClosenessAdjustmentVD(agent, destinationReligiousPlaces, destinationReligiousPlacesFreq);
//                for (int h = 0; h < ((ArrayList<PatternsRecordProcessed>) shopResults[0]).size(); h++) {
//                    destinationPlaces.add(((ArrayList<PatternsRecordProcessed>) shopResults[0]).get(h));
//                    destinationPlacesFreq.add(((ArrayList<Double>) shopResults[1]).get(h));
//                }
//                for (int h = 0; h < ((ArrayList<PatternsRecordProcessed>) schoolResults[0]).size(); h++) {
//                    destinationPlaces.add(((ArrayList<PatternsRecordProcessed>) schoolResults[0]).get(h));
//                    destinationPlacesFreq.add(((ArrayList<Double>) schoolResults[1]).get(h));
//                }
//                for (int h = 0; h < ((ArrayList<PatternsRecordProcessed>) religiousResults[0]).size(); h++) {
//                    destinationPlaces.add(((ArrayList<PatternsRecordProcessed>) religiousResults[0]).get(h));
//                    destinationPlacesFreq.add(((ArrayList<Double>) religiousResults[1]).get(h));
//                }
//
//                destinationShopPlaces.clear();
//                destinationSchoolPlaces.clear();
//                destinationReligiousPlaces.clear();
//                shopResults = null;
//                schoolResults = null;
//                religiousResults = null;
//
//                agent.destinationPlaces = destinationPlaces;
//                agent.destinationPlacesFreq = destinationPlacesFreq;
//                agent.travelStartDecisionCounter = 0;
//                agent.dstIndex = -1;//THIS IS USED TO DETECT IF THE PERSON IS AT HOME OR NOT
//
//                int cumulativeDestinationFreqs = 0;
//                for (int k = 0; k < destinationPlacesFreq.size(); k++) {
//                    if ((destinationPlacesFreq.get(k)) instanceof Double) {
//                        destinationPlacesFreq.set(k, ((Double) (destinationPlacesFreq.get(k))).floatValue());
//                    }else if ((destinationPlacesFreq.get(k)) instanceof Integer) {
//                        destinationPlacesFreq.set(k, ((Integer) (destinationPlacesFreq.get(k))).floatValue());
//                    }
//                    cumulativeDestinationFreqs = cumulativeDestinationFreqs + ((Float) (destinationPlacesFreq.get(k))).intValue();
//                }
//                agent.cumulativeDestinationFreqs = cumulativeDestinationFreqs;
//            }
//
//        }
//
//        void runParallelCBGVD(MainModel modelRoot, ArrayList patternRecords, int numAllVisits, ArrayList cBGs, int startIndex, int endIndex, int threadIndex) {
//
//            for (int i = startIndex; i < endIndex; i++) {
//                int cumulativeNumPeopleIndex = (int) ((Math.random() * (numAllVisits - 1)));
//                //println("cumulativeNumPeopleIndex: "+cumulativeNumPeopleIndex);
//                int cumulativeNumPeople = 0;
//                int selectedIndex = -1;
//                for (int k = 0; k < patternRecords.size(); k++) {
//                    if (isLocalAllowed == false) {
//                        int naics_code = ((PatternsRecordProcessed) patternRecords.get(k)).place.naics_code;
//                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                            cumulativeNumPeople = cumulativeNumPeople + ((PatternsRecordProcessed) (patternRecords.get(k))).getRaw_visitor_counts();
//                            //println("cumulativeNumPeople: "+cumulativeNumPeople);
//                            if (cumulativeNumPeople >= cumulativeNumPeopleIndex) {
//                                if (((PatternsRecordProcessed) (patternRecords.get(k))).getVisitor_home_cbgs_place() != null) {
//                                    selectedIndex = k;
//                                    break;
//                                }
//                            }
//                        }
//                    } else {
//                        cumulativeNumPeople = cumulativeNumPeople + ((PatternsRecordProcessed) (patternRecords.get(k))).getRaw_visitor_counts();
//                        //println("cumulativeNumPeople: "+cumulativeNumPeople);
//                        if (cumulativeNumPeople >= cumulativeNumPeopleIndex) {
//                            if (((PatternsRecordProcessed) (patternRecords.get(k))).getVisitor_home_cbgs_place() != null) {
//                                selectedIndex = k;
//                                break;
//                            }
//                        }
//                    }
//                }
//                //println("selectedIndex: "+selectedIndex);
//                Person agent = (Person) modelRoot.getABM().makeAgentByType("Person");
//
//                System.out.println("AGENT GET INDEX: " + agent.getMyIndex());
//
//                agent.isAtWork = false;
//
//                //\/\/\/ Select home of agent
//                int numAllPeopleHomes = 0;
//                for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).size(); j++) {
//                    if (isLocalAllowed == false) {
//                        int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                            numAllPeopleHomes = numAllPeopleHomes + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j))).getValue());
//                        }
//                    } else {
//                        numAllPeopleHomes = numAllPeopleHomes + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j))).getValue());
//                    }
//
//                }
//
//                int missingNumTravels = ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).raw_visit_counts - numAllPeopleHomes;
//
//                int cumulativePeopleHomesIndex = (int) ((Math.random() * (numAllPeopleHomes - 1)));
//                cumulativeNumPeople = 0;
//                CensusBlockGroup selectedCBG = null;
//                //VDCell selectedVD = null;
//                CBGVDCell selectedCBGVD = null;
//                for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).size(); j++) {
//                    if (isLocalAllowed == false) {
//                        int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                        if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                            cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getValue());
//                            if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                                //println("j: "+j);
//                                selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getKey());
//                                break;
//                            }
//                        }
//                    } else {
//                        cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getValue());
//                        if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                            //println("j: "+j);
//                            selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getKey());
//                            break;
//                        }
//                    }
//
//                }
//                if (selectedCBG == null) {
//                    System.out.println("SEVERE ERROR: HOME CENSUS BLOCK NOT FOUND!");
//                }
//                agent.homeCBG = selectedCBG;
//                if(selectedCBG.cBGVDFromCBGResultFound!=null){
//                    agent.homeCBGVD = (CBGVDCell) (selectedCBG.cBGVDFromCBGResultFound[0]);
//                }else{
//                    if(selectedCBG.cBGVDFromCBGResultClosest==null){
//                        agent.homeCBGVD = (CBGVDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(selectedCBG,true))[0];
//                    }else{
//                        agent.homeCBGVD = (CBGVDCell) (selectedCBG.cBGVDFromCBGResultClosest[0]);
//                    }
//                }
//                
////                if (modelRoot.scenario.equals("CBG")) {
////                    agent.homeCBG = selectedCBG;
////
////                    //println("@@@");
////                    for (int n = 0; n < cBGs.size(); n++) {
////                        //println("%%%");
//////                    System.out.println(selectedCBG);
//////                    System.out.println(cBGs.get(n));
////                        if (((CBG) (cBGs.get(n))).cbgVal == null) {
////                            System.out.println(cBGs.get(n));
////                        }
////                        if (((CensusBlockGroup) selectedCBG) == null) {
////                            System.out.println(((CensusBlockGroup) selectedCBG));
////                        }
////                        if (selectedCBG == null) {
////                            System.out.println(selectedCBG);
////                        }
////                        if (cBGs.get(n) == null) {
////                            System.out.println(cBGs.get(n));
////                        }
////                        if (((CensusBlockGroup) selectedCBG).getId() == ((CensusBlockGroup) (((CBG) (cBGs.get(n))).cbgVal)).getId()) {
////                            ((CBG) (cBGs.get(n))).N = (int) (((CBG) (cBGs.get(n))).N) + 1;
////                            ((CBG) (cBGs.get(n))).S = (int) (((CBG) (cBGs.get(n))).S) + 1;
////                            agent.cBG = ((CBG) (cBGs.get(n)));
////                            //println("AGENT CBG SET: "+agent.getMyIndex());
////                            //println("AGENT IN THREAD: "+threadIndex);
////                            break;
////                        }
////                    }
////
////                } else if (modelRoot.scenario.equals("VD")) {
////                    agent.homeVD = (VDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(selectedCBG))[0];
////
////                    for (int n = 0; n < cBGs.size(); n++) {
////                        //println("%%%");
//////                    System.out.println(selectedCBG);
//////                    System.out.println(cBGs.get(n));
////                        if (((VD) (cBGs.get(n))).vdVal == null) {
////                            System.out.println(cBGs.get(n));
////                        }
////                        if (((CensusBlockGroup) selectedCBG) == null) {
////                            System.out.println(((CensusBlockGroup) selectedCBG));
////                        }
////                        if (selectedCBG == null) {
////                            System.out.println(selectedCBG);
////                        }
////                        if (cBGs.get(n) == null) {
////                            System.out.println(cBGs.get(n));
////                        }
////                        if (((CensusBlockGroup) selectedCBG).getId() == ((CensusBlockGroup) (((CBG) (cBGs.get(n))).cbgVal)).getId()) {
////                            ((CBG) (cBGs.get(n))).N = (int) (((CBG) (cBGs.get(n))).N) + 1;
////                            ((CBG) (cBGs.get(n))).S = (int) (((CBG) (cBGs.get(n))).S) + 1;
////                            agent.cBG = ((CBG) (cBGs.get(n)));
////                            //println("AGENT CBG SET: "+agent.getMyIndex());
////                            //println("AGENT IN THREAD: "+threadIndex);
////                            break;
////                        }
////                    }
////                } else if (modelRoot.scenario.equals("CBGVD")) {
////                    
////                }
//
//                for (int n = 0; n < cBGs.size(); n++) {
//                    //println("%%%");
////                    System.out.println(selectedCBG);
////                    System.out.println(cBGs.get(n));
//                    if (cBGs.get(n) == null) {
//                        System.out.println("NULL CBGVD: " + cBGs.get(n));
//                    }
//                    if (((CBGVD) (cBGs.get(n))).cbgvdVal == null) {
//                        System.out.println("NULL CBGVD VALUE: " + cBGs.get(n));
//                    }
//                    if (selectedCBG == null) {
//                        System.out.println("NULL SELECTED CBG: " + ((CensusBlockGroup) selectedCBG));
//                    }
//
//                    if (((CBGVDCell) agent.homeCBGVD).shopPlacesKeys.get(0).equals((((CBGVD) (cBGs.get(n))).cbgvdVal).shopPlacesKeys.get(0))) {
//                        ((CBGVD) (cBGs.get(n))).N = (int) (((CBGVD) (cBGs.get(n))).N) + 1;
//                        ((CBGVD) (cBGs.get(n))).S = (int) (((CBGVD) (cBGs.get(n))).S) + 1;
//                        agent.cBGVD = ((CBGVD) (cBGs.get(n)));
//                        //println("AGENT CBG SET: "+agent.getMyIndex());
//                        //println("AGENT IN THREAD: "+threadIndex);
//                        break;
//                    }
//                }
//
//                //println("###");
//                //^^^ Select home of agent
//                //\/\/\/ Select daytime cbg of agent
//                int numAllPeopleWorkplaces = 0;
//                if (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()) != null) {
//                    for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).size(); j++) {
//                        numAllPeopleWorkplaces = numAllPeopleWorkplaces + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j))).getValue());
//                    }
//                    int cumulativePeopleDaytimeIndex = (int) ((Math.random() * (numAllPeopleWorkplaces - 1)));
//                    cumulativeNumPeople = 0;
//                    selectedCBG = null;
//                    for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).size(); j++) {
//                        cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j)).getValue());
//                        if (cumulativeNumPeople >= cumulativePeopleDaytimeIndex) {
//                            //println("j: "+j);
//                            selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j)).getKey());
//                            break;
//                        }
//                    }
//                    
//                    
//                    //selectedCBGVD = (CBGVDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(selectedCBG,true))[0];
//                    if (selectedCBG == null) {
//                        System.out.println("KNOWN EXCEPTION: DAYTIME CENSUS BLOCK NOT FOUND!");
//                        System.out.println("USING HOME AS WORK CENSUS BLOCK");
//
//                        selectedCBGVD = agent.homeCBGVD;
//
//                    }else{
//                        if(selectedCBG.cBGVDFromCBGResultFound!=null){
//                            selectedCBGVD = (CBGVDCell) (selectedCBG.cBGVDFromCBGResultFound[0]);
//                        }else{
//                            if(selectedCBG.cBGVDFromCBGResultClosest==null){
//                                selectedCBGVD = (CBGVDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(selectedCBG,true))[0];
//                            }else{
//                                selectedCBGVD = (CBGVDCell) (selectedCBG.cBGVDFromCBGResultClosest[0]);
//                            }
//                        }
//                    }
//                } else {
//                    if (selectedCBGVD == null) {
//                        System.out.println("KNOWN EXCEPTION: DAYTIME VD NOT FOUND!");
//                        System.out.println("USING HOME AS WORK VD");
//                        selectedCBGVD = agent.homeCBGVD;
//                    }
//                }
//                agent.dayCBGVD = selectedCBGVD;
//
//                //^^^ Select daytime cbg of agent
//                //println("lat: "+selectedCBG.getLat());
//                //println("lon: "+selectedCBG.getLon());
//                agent.status = statusEnum.SUSCEPTIBLE.ordinal();
//                agent.minutesSick = -1;
//                agent.minutesTravelToWorkFrom7 = -1;
//                agent.minutesTravelFromWorkFrom16 = -1;
//                if (modelRoot.scenario.equals("CBG")) {
//                    agent.currentLocationCBG = agent.homeCBG;
//
//                    agent.lat = ((CensusBlockGroup) (agent.currentLocationCBG)).getLat();
//                    agent.lon = ((CensusBlockGroup) (agent.currentLocationCBG)).getLon();
//                } else if (modelRoot.scenario.equals("VD")) {
//                    agent.currentLocationVD = agent.homeVD;
//
//                    //NOT COMPLETE! VDs HAVE 0 LAT LON
//                    agent.lat = (agent.currentLocationVD).getLat();
//                    agent.lon = (agent.currentLocationVD).getLon();
//                } else if (modelRoot.scenario.equals("CBGVD")) {
//                    agent.currentLocationCBGVD = agent.homeCBGVD;
//
//                    //NOT COMPLETE! VDs HAVE 0 LAT LON
//                    agent.lat = (agent.currentLocationCBGVD).getLat();
//                    agent.lon = (agent.currentLocationCBGVD).getLon();
//                }
//
//                //println("lat: "+agent.getPropertyValue("currentLocation").getLat());
//                //println("lon: "+agent.getPropertyValue("currentLocation").getLon());
//                ArrayList destinationPlaces = new ArrayList();
//                ArrayList destinationShopPlaces = new ArrayList();
//                ArrayList destinationSchoolPlaces = new ArrayList();
//                ArrayList destinationReligiousPlaces = new ArrayList();
//                ArrayList destinationPlacesFreq = new ArrayList();
//                ArrayList destinationShopPlacesFreq = new ArrayList();
//                ArrayList destinationSchoolPlacesFreq = new ArrayList();
//                ArrayList destinationReligiousPlacesFreq = new ArrayList();
//
//                //println("&&&&");
//                for (int j = 0; j < patternRecords.size(); j++) {
//                    if (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place() != null) {
//                        for (int k = 0; k < ((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).size(); k++) {
//                            if (isLocalAllowed == false) {
//                                int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                                if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                                    if(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).cBGVDFromCBGResultFound!=null){
//                                        Object[] cBGVDReturn=((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).cBGVDFromCBGResultFound;
//                                        if (cBGVDReturn != null) {
//                                            CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                            if (cbgvd != null) {
//                                                destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                                destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                            }
//                                        }
//                                    }else{
//                                        Object[] cBGVDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()),false));
//                                        CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                        if (cbgvd != null) {
//                                            destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                            destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                        }
//                                    }
//                                    
////                                    
//
//                                }
//                            } else {
//                                int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                                if (isShop(naics_code) == true) {
//                                    if(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).cBGVDFromCBGResultFound!=null)
//                                    {
//                                        Object[] cBGVDReturn = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).cBGVDFromCBGResultFound;
//                                        if (cBGVDReturn != null) {
//                                            CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                            if(cbgvd!=null){
//                                                destinationShopPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                                destinationShopPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                            }
//                                        }
//                                    }else{
//                                        Object[] cBGVDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()),false));
//                                        CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                        if (cbgvd != null) {
//                                            destinationShopPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                            destinationShopPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                        }
//                                    }
//                                } else if (isSchool(naics_code) == true) {
//                                    if(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).cBGVDFromCBGResultFound!=null){
//                                        Object[] cBGVDReturn = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).cBGVDFromCBGResultFound;
//                                        if (cBGVDReturn != null) {
//                                            CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                            if (cbgvd != null) {
//                                                destinationSchoolPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                                destinationSchoolPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                            }
//                                        }
//                                    }else{
//                                        Object[] cBGVDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()),false));
//                                        CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                        if (cbgvd != null) {
//                                            destinationSchoolPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                            destinationSchoolPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                        }
//                                    }
//                                } else if (isReligiousOrganization(naics_code) == true) {
//                                    if(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).cBGVDFromCBGResultFound!=null){
//                                        Object[] cBGVDReturn = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).cBGVDFromCBGResultFound;
//                                        if (cBGVDReturn != null) {
//                                            CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                            if(cbgvd!=null){
//                                                destinationReligiousPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                                destinationReligiousPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                            }
//                                        }
//                                    }else{
//                                        Object[] cBGVDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()),false));
//                                        CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                        if (cbgvd != null) {
//                                            destinationReligiousPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                            destinationReligiousPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                        }
//                                    }
//                                    
//                                } else {
//                                    
//                                    if (((CensusBlockGroup) (agent.homeCBG)).id == ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).getId()) {
//                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue());
//                                    }
//                                    
////                                    Object[] cBGVDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey())));
////                                    CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
////                                    if (cbgvd != null) {
////                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
////                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
////                                    }
//                                }
//                            }
//
//                        }
//                    }
//                }
//
//                Object shopResults[] = getClosenessAdjustmentCBGVD(agent, destinationShopPlaces, destinationShopPlacesFreq);
//                Object schoolResults[] = getClosenessAdjustmentCBGVD(agent, destinationSchoolPlaces, destinationSchoolPlacesFreq);
//                Object religiousResults[] = getClosenessAdjustmentCBGVD(agent, destinationReligiousPlaces, destinationReligiousPlacesFreq);
//                for (int h = 0; h < ((ArrayList<PatternsRecordProcessed>) shopResults[0]).size(); h++) {
//                    destinationPlaces.add(((ArrayList<PatternsRecordProcessed>) shopResults[0]).get(h));
//                    destinationPlacesFreq.add(((ArrayList<Double>) shopResults[1]).get(h));
//                }
//                for (int h = 0; h < ((ArrayList<PatternsRecordProcessed>) schoolResults[0]).size(); h++) {
//                    destinationPlaces.add(((ArrayList<PatternsRecordProcessed>) schoolResults[0]).get(h));
//                    destinationPlacesFreq.add(((ArrayList<Double>) schoolResults[1]).get(h));
//                }
//                for (int h = 0; h < ((ArrayList<PatternsRecordProcessed>) religiousResults[0]).size(); h++) {
//                    destinationPlaces.add(((ArrayList<PatternsRecordProcessed>) religiousResults[0]).get(h));
//                    destinationPlacesFreq.add(((ArrayList<Double>) religiousResults[1]).get(h));
//                }
//
//                agent.destinationPlaces = destinationPlaces;
//                agent.destinationPlacesFreq = destinationPlacesFreq;
//                agent.travelStartDecisionCounter = 0;
//                agent.dstIndex = -1;//THIS IS USED TO DETECT IF THE PERSON IS AT HOME OR NOT
//
//                int cumulativeDestinationFreqs = 0;
//                for (int k = 0; k < destinationPlacesFreq.size(); k++) {
//                    if ((destinationPlacesFreq.get(k)) instanceof Double) {
//                        destinationPlacesFreq.set(k, ((Double) (destinationPlacesFreq.get(k))).floatValue());
//                    }else if ((destinationPlacesFreq.get(k)) instanceof Integer) {
//                        destinationPlacesFreq.set(k, ((Integer) (destinationPlacesFreq.get(k))).floatValue());
//                    }
//                    cumulativeDestinationFreqs = cumulativeDestinationFreqs + ((Float) (destinationPlacesFreq.get(k))).intValue();
//                }
//                agent.cumulativeDestinationFreqs = cumulativeDestinationFreqs;
//            }
//
//        }
//
//    }
//
////@CompileStatic
//    void runGenPeople(int numAgents, MainModel modelRoot, ArrayList patternRecords, int numAllVisits, ArrayList cBGs, double sparsifyPercentage) {
////int numAgents=1000;
////SAMPLE FROM PATTERNS
//
//        int numProcessors = modelRoot.getNumCPUs();
////        if (numProcessors > Runtime.getRuntime().availableProcessors()) {
////            numProcessors = Runtime.getRuntime().availableProcessors();
////        }
//        ParallelAgentGenerator[] parallelAgentGenerator = new ParallelAgentGenerator[numProcessors];
//
//        for (int i = 0; i < numProcessors - 1; i++) {
//            parallelAgentGenerator[i] = new ParallelAgentGenerator(i, numAllVisits, modelRoot, patternRecords, null, null, cBGs, (int) Math.floor((double) (i * ((numAgents) / numProcessors))), (int) Math.floor((double) ((i + 1) * ((numAgents) / numProcessors))), sparsifyPercentage);
//        }
//        parallelAgentGenerator[numProcessors - 1] = new ParallelAgentGenerator(numProcessors - 1, numAllVisits, modelRoot, patternRecords, null, null, cBGs, (int) Math.floor((double) ((numProcessors - 1) * ((numAgents) / numProcessors))), numAgents, sparsifyPercentage);
//
////for (int i = 0; i < numProcessors; i++) {
////	parallelAgentGenerator[i].myThread = new Thread(new Runnable() {
////            @Override
////            public void run() {
////            	println("generate people ***");
////            	runInternally(i, modelRoot,patternRecords,numAllVisits,parallelAgentGenerator[i].myStartIndex,parallelAgentGenerator[i].myEndIndex);
////            }
////	});
////}
//        for (int i = 0; i < numProcessors; i++) {
//            parallelAgentGenerator[i].myThread.start();
//            System.out.println("thread " + i + " started!");
//        }
//        for (int i = 0; i < numProcessors; i++) {
//            try {
//                System.out.println("####");
//                parallelAgentGenerator[i].myThread.join();
//                System.out.println("thread " + i + " finished for records: " + parallelAgentGenerator[i].myStartIndex + " | " + parallelAgentGenerator[i].myEndIndex);
//            } catch (InterruptedException ie) {
//                System.out.println(ie.toString());
//            }
//        }
//
//    }
//
////@CompileStatic
//    void runGenPeopleSerially(int numAgents, MainModel modelRoot, ArrayList patternRecords, int numAllVisits, ArrayList cBGs) {
//
////int numAgents=1000;
//        for (int i = 0; i < numAgents; i++) {
//            int cumulativeNumPeopleIndex = (int) ((Math.random() * (numAllVisits - 1)));
//            //println("cumulativeNumPeopleIndex: "+cumulativeNumPeopleIndex);
//            int cumulativeNumPeople = 0;
//            int selectedIndex = -1;
//            for (int k = 0; k < patternRecords.size(); k++) {
//                if (isLocalAllowed == false) {
//                    int naics_code = ((PatternsRecordProcessed) patternRecords.get(k)).place.naics_code;
//                    if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                        cumulativeNumPeople = cumulativeNumPeople + ((PatternsRecordProcessed) (patternRecords.get(k))).getRaw_visitor_counts();
//                        //println("cumulativeNumPeople: "+cumulativeNumPeople);
//                        if (cumulativeNumPeople >= cumulativeNumPeopleIndex) {
//                            if (((PatternsRecordProcessed) (patternRecords.get(k))).getVisitor_home_cbgs_place() != null) {
//                                selectedIndex = k;
//                                break;
//                            }
//                        }
//                    }
//                } else {
//                    cumulativeNumPeople = cumulativeNumPeople + ((PatternsRecordProcessed) (patternRecords.get(k))).getRaw_visitor_counts();
//                    //println("cumulativeNumPeople: "+cumulativeNumPeople);
//                    if (cumulativeNumPeople >= cumulativeNumPeopleIndex) {
//                        if (((PatternsRecordProcessed) (patternRecords.get(k))).getVisitor_home_cbgs_place() != null) {
//                            selectedIndex = k;
//                            break;
//                        }
//                    }
//                }
//            }
//            //println("selectedIndex: "+selectedIndex);
//            Person agent = (Person) modelRoot.getABM().makeAgentByType("Person");
//
//            System.out.println("AGENT GET INDEX: " + agent.getMyIndex());
//
//            agent.isAtWork = false;
//
//            //\/\/\/ Select home of agent
//            int numAllPeopleHomes = 0;
//            for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).size(); j++) {
//                if (isLocalAllowed == false) {
//                    int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                    if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                        numAllPeopleHomes = numAllPeopleHomes + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j))).getValue());
//                    }
//                } else {
//                    numAllPeopleHomes = numAllPeopleHomes + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j))).getValue());
//                }
//
//            }
//
//            int missingNumTravels = ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).raw_visit_counts - numAllPeopleHomes;
//
//            int cumulativePeopleHomesIndex = (int) ((Math.random() * (numAllPeopleHomes - 1)));
//            cumulativeNumPeople = 0;
//            CensusBlockGroup selectedCBG = null;
//            VDCell selectedVD = null;
//            CBGVDCell selectedCBGVD = null;
//            for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).size(); j++) {
//                if (isLocalAllowed == false) {
//                    int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                    if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                        cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getValue());
//                        if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                            //println("j: "+j);
//                            selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getKey());
//                            break;
//                        }
//                    }
//                } else {
//                    cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getValue());
//                    if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                        //println("j: "+j);
//                        selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_home_cbgs_place()).get(j)).getKey());
//                        break;
//                    }
//                }
//
//            }
//            if (selectedCBG == null) {
//                System.out.println("SEVERE ERROR: HOME CENSUS BLOCK NOT FOUND!");
//            }
//            if (modelRoot.scenario.equals("CBG")) {
//                agent.homeCBG = selectedCBG;
//            } else if (modelRoot.scenario.equals("VD")) {
//                agent.homeVD = (VDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(selectedCBG, true))[0];
//            } else if (modelRoot.scenario.equals("CBGVD")) {
//                agent.homeCBGVD = (CBGVDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(selectedCBG,true))[0];
//            }
//
//            //println("@@@");
//            for (int n = 0; n < cBGs.size(); n++) {
//                //println("%%%");
////                    System.out.println(selectedCBG);
////                    System.out.println(cBGs.get(n));
//                if (((CBG) (cBGs.get(n))).cbgVal == null) {
//                    System.out.println(cBGs.get(n));
//                }
//                if (((CensusBlockGroup) selectedCBG) == null) {
//                    System.out.println(((CensusBlockGroup) selectedCBG));
//                }
//                if (selectedCBG == null) {
//                    System.out.println(selectedCBG);
//                }
//                if (cBGs.get(n) == null) {
//                    System.out.println(cBGs.get(n));
//                }
//                if (((CensusBlockGroup) selectedCBG).getId() == ((CensusBlockGroup) (((CBG) (cBGs.get(n))).cbgVal)).getId()) {
//                    ((CBG) (cBGs.get(n))).N = (int) (((CBG) (cBGs.get(n))).N) + 1;
//                    ((CBG) (cBGs.get(n))).S = (int) (((CBG) (cBGs.get(n))).S) + 1;
//                    agent.cBG = ((CBG) (cBGs.get(n)));
//                    //println("AGENT CBG SET: "+agent.getMyIndex());
//                    //println("AGENT IN THREAD: "+threadIndex);
//                    break;
//                }
//            }
//            //println("###");
//            //^^^ Select home of agent
//
//            //\/\/\/ Select daytime cbg of agent
//            int numAllPeopleWorkplaces = 0;
//            if (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()) != null) {
//                for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).size(); j++) {
//                    numAllPeopleWorkplaces = numAllPeopleWorkplaces + ((Integer) ((CensusBlockGroupIntegerTuple) (((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j))).getValue());
//                }
//                int cumulativePeopleDaytimeIndex = (int) ((Math.random() * (numAllPeopleWorkplaces - 1)));
//                cumulativeNumPeople = 0;
//                selectedCBG = null;
//                for (int j = 0; j < ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).size(); j++) {
//                    cumulativeNumPeople = cumulativeNumPeople + ((Integer) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j)).getValue());
//                    if (cumulativeNumPeople >= cumulativePeopleHomesIndex) {
//                        //println("j: "+j);
//                        selectedCBG = ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(selectedIndex))).getVisitor_daytime_cbgs_place()).get(j)).getKey());
//                        break;
//                    }
//                }
//                if (selectedCBG == null) {
//                    System.out.println("KNOWN EXCEPTION: DAYTIME CENSUS BLOCK NOT FOUND!");
//                    System.out.println("USING HOME AS WORK CENSUS BLOCK");
//
//                    if (modelRoot.scenario.equals("CBG")) {
//                        selectedCBG = (CensusBlockGroup) (agent.homeCBG);
//                    } else if (modelRoot.scenario.equals("VD")) {
//                        selectedVD = (VDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(agent.homeCBG, true))[0];
//                    } else if (modelRoot.scenario.equals("CBGVD")) {
//                        selectedCBGVD = (CBGVDCell) (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(selectedCBG,true))[0];
//                    }
//
//                }
//            } else {
//                if (modelRoot.scenario.equals("CBG")) {
//                    if (selectedCBG == null) {
//                        System.out.println("KNOWN EXCEPTION: DAYTIME CENSUS BLOCK NOT FOUND!");
//                        System.out.println("USING HOME AS WORK CENSUS BLOCK");
//                        selectedCBG = (CensusBlockGroup) (agent.homeCBG);
//                    }
//                } else if (modelRoot.scenario.equals("VD")) {
//                    if (selectedVD == null) {
//                        System.out.println("KNOWN EXCEPTION: DAYTIME VD NOT FOUND!");
//                        System.out.println("USING HOME AS WORK VD");
//                        selectedVD = (agent.homeVD);
//                    }
//                } else if (modelRoot.scenario.equals("CBGVD")) {
//                    if (selectedCBGVD == null) {
//                        System.out.println("KNOWN EXCEPTION: DAYTIME CBGVD NOT FOUND!");
//                        System.out.println("USING HOME AS WORK CBGVD");
//                        selectedCBGVD = (agent.homeCBGVD);
//                    }
//                }
//            }
//            if (modelRoot.scenario.equals("CBG")) {
//                agent.dayCBG = selectedCBG;
//            } else if (modelRoot.scenario.equals("VD")) {
//                agent.dayVD = selectedVD;
//            } else if (modelRoot.scenario.equals("CBGVD")) {
//                agent.dayCBGVD = selectedCBGVD;
//            }
//
//            //^^^ Select daytime cbg of agent
//            //println("lat: "+selectedCBG.getLat());
//            //println("lon: "+selectedCBG.getLon());
//            agent.status = statusEnum.SUSCEPTIBLE.ordinal();
//            agent.minutesSick = -1;
//            agent.minutesTravelToWorkFrom7 = -1;
//            agent.minutesTravelFromWorkFrom16 = -1;
//            if (modelRoot.scenario.equals("CBG")) {
//                agent.currentLocationCBG = agent.homeCBG;
//
//                agent.lat = ((CensusBlockGroup) (agent.currentLocationCBG)).getLat();
//                agent.lon = ((CensusBlockGroup) (agent.currentLocationCBG)).getLon();
//            } else if (modelRoot.scenario.equals("VD")) {
//                agent.currentLocationVD = agent.homeVD;
//
//                //NOT COMPLETE! VDs HAVE 0 LAT LON
//                agent.lat = (agent.currentLocationVD).getLat();
//                agent.lon = (agent.currentLocationVD).getLon();
//            } else if (modelRoot.scenario.equals("CBGVD")) {
//                agent.currentLocationCBGVD = agent.homeCBGVD;
//
//                //NOT COMPLETE! VDs HAVE 0 LAT LON
//                agent.lat = (agent.currentLocationCBGVD).getLat();
//                agent.lon = (agent.currentLocationCBGVD).getLon();
//            }
//
//            //println("lat: "+agent.getPropertyValue("currentLocation").getLat());
//            //println("lon: "+agent.getPropertyValue("currentLocation").getLon());
//            ArrayList destinationPlaces = new ArrayList();
//            ArrayList destinationPlacesFreq = new ArrayList();
//
//            //println("&&&&");
//            for (int j = 0; j < patternRecords.size(); j++) {
//                if (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place() != null) {
//                    for (int k = 0; k < ((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).size(); k++) {
//                        if (isLocalAllowed == false) {
//                            int naics_code = ((PatternsRecordProcessed) patternRecords.get(j)).place.naics_code;
//                            if (!isShop(naics_code) && !isSchool(naics_code) && !isReligiousOrganization(naics_code)) {
//                                if (modelRoot.scenario.equals("CBG")) {
//                                    if (((CensusBlockGroup) (agent.homeCBG)).id == ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).getId()) {
//                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue());
//                                    }
//                                } else if (modelRoot.scenario.equals("VD")) {
//                                    Object[] vDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()), false));
//                                    VDCell vd = (VDCell) (vDReturn[0]);
//                                    if (vd != null) {
//                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (vDReturn[1])));
//                                    }
//                                } else if (modelRoot.scenario.equals("CBGVD")) {
//                                    Object[] cBGVDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()),false));
//                                    CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                    if (cbgvd != null) {
//                                        destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                        destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                    }
//                                }
//
//                            }
//                        } else {
//                            if (modelRoot.scenario.equals("CBG")) {
//                                if (((CensusBlockGroup) (agent.homeCBG)).id == ((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()).getId()) {
//                                    destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                    destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue());
//                                }
//                            } else if (modelRoot.scenario.equals("VD")) {
//                                Object[] vDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()), false));
//                                VDCell vd = (VDCell) (vDReturn[0]);
//                                if (vd != null) {
//                                    destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                    destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (vDReturn[1])));
//                                }
//                            } else if (modelRoot.scenario.equals("CBGVD")) {
//                                Object[] cBGVDReturn = (((City) (modelRoot.getABM().studyScopeGeography)).getCBGVDFromCBG(((CensusBlockGroup) ((CensusBlockGroupIntegerTuple) ((ArrayList) ((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place()).get(k)).getKey()),false));
//                                CBGVDCell cbgvd = (CBGVDCell) (cBGVDReturn[0]);
//                                if (cbgvd != null) {
//                                    destinationPlaces.add(((PatternsRecordProcessed) (patternRecords.get(j))));
//                                    destinationPlacesFreq.add(((CensusBlockGroupIntegerTuple) (((ArrayList) (((PatternsRecordProcessed) (patternRecords.get(j))).getVisitor_home_cbgs_place())).get(k))).getValue() * ((double) (cBGVDReturn[1])));
//                                }
//                            }
//                        }
//
//                    }
//                }
//            }
//            agent.destinationPlaces = destinationPlaces;
//            agent.destinationPlacesFreq = destinationPlacesFreq;
//            agent.travelStartDecisionCounter = 0;
//            agent.dstIndex = -1;//THIS IS USED TO DETECT IF THE PERSON IS AT HOME OR NOT
//
//            int cumulativeDestinationFreqs = 0;
//            for (int k = 0; k < destinationPlacesFreq.size(); k++) {
//                cumulativeDestinationFreqs = cumulativeDestinationFreqs + (Integer) (destinationPlacesFreq.get(k));
//            }
//            agent.cumulativeDestinationFreqs = cumulativeDestinationFreqs;
//        }
//
//    }
//
//    void initialInfectPeopleABSVD(MainModel modelRoot, ArrayList cBGs) {
//
//    }
//
//@CompileStatic
//    void initialInfectPeopleCBG(MainModel modelRoot, ArrayList cBGs) {
//        //println("1");
//
//        //println("2");
//        //println(dailyConfirmedCases);
//        if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof County) {
//
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof State) {
//            ArrayList<DailyConfirmedCases> dailyConfirmedCases = ((CovidCsseJhu) (modelRoot.covidCsseJhu)).dailyConfirmedCasesList;
//            State scope = (State) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            double[] percentageSickInCounties = new double[((ArrayList<County>) (scope.getCounties())).size()];
//            for (int i = 0; i < percentageSickInCounties.length; i++) {
//                int startingDateIndex = -1;
//                for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//                    //println(((County)(((ArrayList<County>)(scope.getCounties())).get(i))));
//                    //println((((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getCounty()));
//                    //if((((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getCounty())==null){
//                    //	println("NULL FOUND!!!");
//                    //}
//
//                    if (((County) (((ArrayList<County>) (scope.getCounties())).get(i))).getId() == ((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getId()) {
//                        //if(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getDate()==null){
//                        //	println("NULL FOUND!");
//                        //}
//                        if (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).equals(((ZonedDateTime) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getDate()))) == true) {
//                            percentageSickInCounties[i] = (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getNumActiveCases()) / ((County) (((ArrayList<County>) (scope.getCounties())).get(i))).getPopulation();
//                        }
//                    }
//
//                }
//            }
//            //println("3");
//
//            double[] generatedPopulationInCounties = new double[((ArrayList<County>) (scope.getCounties())).size()];
//            for (int i = 0; i < ((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).size(); i++) {
//                if (modelRoot.getABM().agents.get(i).myType.equals("Person")) {
//                    for (int j = 0; j < ((ArrayList<County>) (scope.getCounties())).size(); j++) {
//                        if (((County) (((CensusBlockGroup) (((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeCBG)).getCounty())).getId() == ((County) (((ArrayList<County>) (scope.getCounties())).get(j))).getId()) {
//                            generatedPopulationInCounties[j] = generatedPopulationInCounties[j] + 1;
//                            residents.add((Person) (modelRoot.getABM().agents.get(i)));
//                        }
//                    }
//                }
//            }
//            agentPairContact = new int[residents.size()][residents.size()];
//            int sumResident = 0;
//            for (int i = 0; i < generatedPopulationInCounties.length; i++) {
//                sumResident = sumResident + (int) generatedPopulationInCounties[i];
//            }
//            //println(sumResident);
//            ((Root) ((((AgentBasedModel) (modelRoot.getABM())).getRootAgent()))).residentPopulation = sumResident;
//
//            double[] currentNumberOfInfectedInCounty = new double[((ArrayList<County>) (scope.getCounties())).size()];
//            for (int i = 0; i < ((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).size(); i++) {
//                if (modelRoot.getABM().agents.get(i).myType.equals("Person")) {
//                    for (int j = 0; j < ((ArrayList<County>) (scope.getCounties())).size(); j++) {
//                        if (((County) (((CensusBlockGroup) (((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeCBG)).getCounty())).getId() == ((County) (((ArrayList<County>) (scope.getCounties())).get(j))).getId()) {
//                            //println("before infecting");
//                            if ((currentNumberOfInfectedInCounty[j] / generatedPopulationInCounties[j]) < percentageSickInCounties[j]) {
//                                if (Math.random() < 0.7) {
//                                    //println("IS");
//                                    ((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).status = statusEnum.INFECTED_SYM.ordinal();
//                                    for (int n = 0; n < cBGs.size(); n++) {
//                                        //println("%%%");
//                                        if (((CensusBlockGroup) (((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeCBG)).getId() == ((CensusBlockGroup) (((CBG) (cBGs.get(n))).cbgVal)).getId()) {
//                                            ((CBG) (cBGs.get(n))).IS = (int) (((CBG) (cBGs.get(n))).IS) + 1;
//                                            ((CBG) (cBGs.get(n))).S = (int) (((CBG) (cBGs.get(n))).S) - 1;
//                                            break;
//                                        }
//                                    }
//
//                                } else {
//                                    //println("IAS");
//                                    ((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).status = statusEnum.INFECTED_ASYM.ordinal();
//                                    for (int n = 0; n < cBGs.size(); n++) {
//                                        //println("%%%");
//                                        if ((((CensusBlockGroup) (((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeCBG)).getId() == ((CensusBlockGroup) (((CBG) (cBGs.get(n))).cbgVal)).getId())) {
//                                            ((CBG) (cBGs.get(n))).IAS = (int) (((CBG) (cBGs.get(n))).IAS) + 1;
//                                            ((CBG) (cBGs.get(n))).S = (int) (((CBG) (cBGs.get(n))).S) - 1;
//                                            break;
//                                        }
//                                    }
//                                }
//
//                                currentNumberOfInfectedInCounty[j] = currentNumberOfInfectedInCounty[j] + 1;
//                                //println("did infecting");
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof Country) {
//            System.out.println("Infection for country level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof City) {
//            City scope = (City) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            //ArrayList<Double> percentageSickInCounties=new ArrayList();
//            double[] percentageSickInTracts = new double[((ArrayList<CensusTract>) (scope.getCensusTracts())).size()];
//            for (int i = 0; i < percentageSickInTracts.length; i++) {
//                for (int d = 0; d < relevantDailyConfirmedCases.size(); d++) {
//                    if ((relevantDailyConfirmedCases.get(d).county.state.name).equals(scope.censusTracts.get(i).state.name)) {
//                        if (scope.censusTracts.get(i).county.id == relevantDailyConfirmedCases.get(d).county.id) {
//                            //if(((ZonedDateTime)(((AgentBasedModel)(modelRoot.getABM())).getCurrentTime()))==null){
//                            //	println("777");
//                            //}
//                            //if(((ZonedDateTime)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getDate()))==null){
//                            //	println("888");
//                            //}
//                            if ((modelRoot.ABM.currentTime).equals(relevantDailyConfirmedCases.get(d).date) == true) {
//                                //percentageSickInCounties[i]=((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()/((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(i))).getCounty())).getPopulation();
//                                percentageSickInTracts[i] = (((float) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).numActiveCases)) * ((float) ((((CensusTract) (((ArrayList<CensusTract>) (scope.censusTracts)).get(i))).population)) / (float) (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.censusTracts)).get(i))).county)).population))) / ((float) ((((CensusTract) (((ArrayList<CensusTract>) (scope.censusTracts)).get(i))).population)));
////                                System.out.println(i);
//                            }
//                        }
//                    }
//                }
//            }
//
//            double[] generatedPopulationInTracts = new double[((ArrayList<CensusTract>) (scope.censusTracts)).size()];
//            for (int i = 0; i < modelRoot.ABM.agents.size(); i++) {
//                if (modelRoot.ABM.agents.get(i).myType.equals("Person")) {
//                    for (int j = 0; j < ((ArrayList<CensusTract>) (scope.censusTracts)).size(); j++) {
//                        if (((CensusTract) (((CensusBlockGroup) (((Person) (((List) ((AgentBasedModel) (modelRoot.ABM)).agents).get(i))).homeCBG)).censusTract)).getId() == ((CensusTract) (((ArrayList<CensusTract>) (scope.censusTracts)).get(j))).id) {
//                            generatedPopulationInTracts[j] = generatedPopulationInTracts[j] + 1;
//                            residents.add((Person) (modelRoot.getABM().agents.get(i)));
//                        }
//                    }
//                }
//            }
//            agentPairContact = new int[residents.size()][residents.size()];
//            int sumResident = 0;
//            for (int i = 0; i < generatedPopulationInTracts.length; i++) {
//                sumResident = sumResident + (int) generatedPopulationInTracts[i];
//            }
//            //println(sumResident);
//            ((Root) ((((AgentBasedModel) (modelRoot.ABM)).rootAgent))).residentPopulation = sumResident;
//
//            int sumAllInfections = 0;
//
//            int numActiveInfected = 0;
//
//            int scopePopulation = scope.getPopulation();
//            int start = currentAgent.startCountyIndex;
//            int end = currentAgent.endCountyIndex;
//            for (int j = 0; j < ((ArrayList<CensusTract>) (scope.censusTracts)).size(); j++) {
//                for (int d = 0; d < relevantDailyConfirmedCases.size(); d++) {
//                    if (((String) (((State) (((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).county)).state)).name)).equals(((State) (((CensusTract) (((ArrayList<CensusTract>) (scope.censusTracts)).get(j))).state)).name)) {
//                        if (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.censusTracts)).get(j))).county)).id == ((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).county)).id) {
//                            if (((ZonedDateTime) (((ZonedDateTime) (((AgentBasedModel) (modelRoot.ABM)).currentTime)).truncatedTo(ChronoUnit.DAYS))).equals(((ZonedDateTime) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).date))) == true) {
//                                //println("daily county cases: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()));
//                                //println("county population: "+(float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation()));
//                                //println("census tract population: "+(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()));
//                                //println("numActiveInfected: "+numActiveInfected);
//                                //println("fraction: "+((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation())));
//                                //println("add: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()*((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()))));
//                                numActiveInfected = numActiveInfected + (int) ((((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).numActiveCases) * ((float) (((CensusTract) (((ArrayList<CensusTract>) (scope.censusTracts)).get(j))).population) / (float) (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.censusTracts)).get(j))).county)).population)));
////                                System.out.println(numActiveInfected);
//                            }
//                        }
//                    }
//                }
//            }
//
//            double[] currentNumberOfInfectedInTract = new double[((ArrayList<CensusTract>) (scope.censusTracts)).size()];
//            for (int u = 0; u < 5; u++) {
//                for (int i = 0; i < ((List) ((AgentBasedModel) (modelRoot.ABM)).agents).size(); i++) {
//                    if (modelRoot.ABM.agents.get(i).myType.equals("Person")) {
//                        for (int j = 0; j < ((ArrayList<CensusTract>) (scope.censusTracts)).size(); j++) {
//                            if (((CensusTract) (((CensusBlockGroup) (((Person) (((List) ((AgentBasedModel) (modelRoot.ABM)).agents).get(i))).homeCBG)).censusTract)).id == ((CensusTract) (((ArrayList<CensusTract>) (scope.censusTracts)).get(j))).id) {
//                                //println("before infecting");
//                                if (((double) currentNumberOfInfectedInTract[j] / (double) generatedPopulationInTracts[j]) <= percentageSickInTracts[j] && (((double) sumAllInfections / (double) ((Root) (modelRoot.ABM.rootAgent)).residentPopulation)) <= ((double) numActiveInfected / (double) scopePopulation)) {
//                                    if (Math.random() < 0.7) {
//                                        //println("IS");
//                                        ((Person) (((List) ((AgentBasedModel) (modelRoot.ABM)).agents).get(i))).status = statusEnum.INFECTED_SYM.ordinal();
//                                        for (int n = 0; n < cBGs.size(); n++) {
//                                            //println("%%%");
//                                            if ((((CensusBlockGroup) (((Person) (((List) ((AgentBasedModel) (modelRoot.ABM)).agents).get(i))).homeCBG)).id == ((CensusBlockGroup) (((CBG) (cBGs.get(n))).cbgVal)).id)) {
//                                                ((CBG) (cBGs.get(n))).IS = (int) (((CBG) (cBGs.get(n))).IS) + 1;
//                                                ((CBG) (cBGs.get(n))).S = (int) (((CBG) (cBGs.get(n))).S) - 1;
//                                                break;
//                                            }
//                                        }
//
//                                    } else {
//                                        //println("IAS");
//                                        ((Person) (((List) ((AgentBasedModel) (modelRoot.ABM)).agents).get(i))).status = statusEnum.INFECTED_ASYM.ordinal();
//                                        for (int n = 0; n < cBGs.size(); n++) {
//                                            //println("%%%");
//                                            if ((((CensusBlockGroup) (((Person) (((List) ((AgentBasedModel) (modelRoot.ABM)).agents).get(i))).homeCBG)).id == ((CensusBlockGroup) (((CBG) (cBGs.get(n))).cbgVal)).id)) {
//                                                ((CBG) (cBGs.get(n))).IAS = (int) (((CBG) (cBGs.get(n))).IAS) + 1;
//                                                ((CBG) (cBGs.get(n))).S = (int) (((CBG) (cBGs.get(n))).S) - 1;
//                                                break;
//                                            }
//                                        }
//                                    }
//
//                                    currentNumberOfInfectedInTract[j] = currentNumberOfInfectedInTract[j] + 1;
//                                    sumAllInfections = sumAllInfections + 1;
//                                    //println("did infecting");
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            double sumCurrentNumberOfInfectedInTract = 0;
//            for (int j = 0; j < currentNumberOfInfectedInTract.length; j++) {
//                sumCurrentNumberOfInfectedInTract = sumCurrentNumberOfInfectedInTract + currentNumberOfInfectedInTract[j];
//            }
////            System.out.println(sumCurrentNumberOfInfectedInTract + " " + sumCurrentNumberOfInfectedInTract / ((Root) ((((AgentBasedModel) (modelRoot.getABM())).getRootAgent()))).residentPopulation);
////            System.out.println("END INFECTION!");
//        } else {
//            System.out.println("Infection for less than county level not implemented yet!");
//        }
//
//    }
//
//    void initialInfectPeopleVDs(MainModel modelRoot, ArrayList vDs) {
//        //println("1");
//
//        //println("2");
//        //println(dailyConfirmedCases);
//        if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof County) {
//            System.out.println("Infection for county level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof State) {
//            ArrayList<DailyConfirmedCases> dailyConfirmedCases = ((CovidCsseJhu) (modelRoot.covidCsseJhu)).dailyConfirmedCasesList;
//            System.out.println("Infection for State level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof Country) {
//            System.out.println("Infection for country level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof City) {
//            City scope = (City) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            //ArrayList<Double> percentageSickInCounties=new ArrayList();
//            double[] percentageSickInVDs = new double[scope.vDCells.size()];
//            for (int i = 0; i < percentageSickInVDs.length; i++) {
//                if (scope.vDCells.get(i).cBGsInvolved.size() == 0) {
//                    continue;
//                }
//                for (int d = 0; d < relevantDailyConfirmedCases.size(); d++) {
//                    if (((String) (((State) (((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(scope.censusTracts.get(0).state.name)) {
//                        if (((County) (((((scope.vDCells).get(i)).cBGsInvolved.get(0))).getCounty())).getId() == ((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getId()) {
//                            //if(((ZonedDateTime)(((AgentBasedModel)(modelRoot.getABM())).getCurrentTime()))==null){
//                            //	println("777");
//                            //}
//                            //if(((ZonedDateTime)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getDate()))==null){
//                            //	println("888");
//                            //}
//                            if (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).equals(((ZonedDateTime) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getDate()))) == true) {
//                                //percentageSickInCounties[i]=((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()/((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(i))).getCounty())).getPopulation();
////                                System.out.println("D");
////                                System.out.println(d);
////                                System.out.println(dailyConfirmedCases.get(d).getNumActiveCases());
////
////                                System.out.println("VD");
////                                System.out.println(i);
////                                System.out.println(scope.vDCells.size());
////
////                                System.out.println("VD CBGs involved");
////                                System.out.println(scope.vDCells.get(i).cBGsInvolved.size());
////                                if(scope.vDCells.get(i).cBGsInvolved.size()==0){
////                                    System.out.println("!!!!!");
////                                }
//                                percentageSickInVDs[i] = (((float) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getNumActiveCases())) * ((float) (((scope.vDCells.get(i)).getPopulation())) / (float) (((County) ((scope.vDCells.get(i).cBGsInvolved.get(0)).getCounty())).getPopulation()))) / ((float) (((scope.vDCells.get(i)).getPopulation())));
//
//                            }
//                        }
//                    }
//                }
//            }
//
//            double[] generatedPopulationInVDs = new double[scope.vDCells.size()];
//            for (int i = 0; i < modelRoot.getABM().agents.size(); i++) {
//                if (modelRoot.getABM().agents.get(i).myType.equals("Person")) {
//                    for (int j = 0; j < scope.vDCells.size(); j++) {
//                        if ((((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeVD).shopPlacesKeys.get(0).equals(scope.vDCells.get(j).shopPlacesKeys.get(0))) {
//                            generatedPopulationInVDs[j] = generatedPopulationInVDs[j] + 1;
//                            residents.add((Person) (modelRoot.getABM().agents.get(i)));
//                            break;
//                        }
//                    }
//                }
//            }
//            agentPairContact = new int[residents.size()][residents.size()];
//            int sumResident = 0;
//            for (int i = 0; i < generatedPopulationInVDs.length; i++) {
//                sumResident = sumResident + (int) generatedPopulationInVDs[i];
//            }
//            //println(sumResident);
//            ((Root) ((((AgentBasedModel) (modelRoot.getABM())).getRootAgent()))).residentPopulation = sumResident;
//
//            int sumAllInfections = 0;
//
//            int numActiveInfected = 0;
//
//            int scopePopulation = scope.getPopulation();
//            int start = currentAgent.startCountyIndex;
//            int end = currentAgent.endCountyIndex;
//            for (int j = 0; j < ((ArrayList<CensusTract>) (scope.getCensusTracts())).size(); j++) {
//                for (int d = 0; d < relevantDailyConfirmedCases.size(); d++) {
//                    if (((String) (((State) (((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(((State) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getState())).getName())) {
//                        if (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getCounty())).getId() == ((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getId()) {
//                            if (((ZonedDateTime) (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).truncatedTo(ChronoUnit.DAYS))).equals(((ZonedDateTime) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getDate()))) == true) {
//                                //println("daily county cases: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()));
//                                //println("county population: "+(float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation()));
//                                //println("census tract population: "+(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()));
//                                //println("numActiveInfected: "+numActiveInfected);
//                                //println("fraction: "+((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation())));
//                                //println("add: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()*((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()))));
//                                numActiveInfected = numActiveInfected + (int) ((((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getNumActiveCases()) * ((float) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getPopulation()) / (float) (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getCounty())).getPopulation())));
////                                System.out.println(numActiveInfected);
//                            }
//                        }
//                    }
//                }
//            }
//
//            double[] currentNumberOfInfectedInVD = new double[scope.vDCells.size()];
//            for (int u = 0; u < 5; u++) {
//                for (int i = 0; i < ((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).size(); i++) {
//                    if (modelRoot.getABM().agents.get(i).myType.equals("Person")) {
//                        for (int j = 0; j < scope.vDCells.size(); j++) {
//                            if ((((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeVD).shopPlacesKeys.get(0).equals(scope.vDCells.get(j).shopPlacesKeys.get(0))) {
//                                //println("before infecting");
//                                if (((double) currentNumberOfInfectedInVD[j] / (double) generatedPopulationInVDs[j]) <= percentageSickInVDs[j] && ((double) sumAllInfections / (double) ((Root) (modelRoot.getABM().rootAgent)).residentPopulation) <= ((double) numActiveInfected / (double) scopePopulation)) {
//                                    if (Math.random() < 0.7) {
//                                        //println("IS");
//                                        ((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).status = statusEnum.INFECTED_SYM.ordinal();
//                                        for (int n = 0; n < vDs.size(); n++) {
//                                            //println("%%%");
//                                            if (((Person) (modelRoot.getABM().agents.get(i))).homeVD.shopPlacesKeys.get(0).equals((((VD) (vDs.get(n))).vdVal).shopPlacesKeys.get(0))) {
//                                                ((VD) (vDs.get(n))).IS = (int) (((VD) (vDs.get(n))).IS) + 1;
//                                                ((VD) (vDs.get(n))).S = (int) (((VD) (vDs.get(n))).S) - 1;
//                                                break;
//                                            }
//                                        }
//
//                                    } else {
//                                        //println("IAS");
//                                        ((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).status = statusEnum.INFECTED_ASYM.ordinal();
//                                        for (int n = 0; n < vDs.size(); n++) {
//                                            //println("%%%");
//                                            if ((((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeVD).shopPlacesKeys.get(0).equals((((VD) (vDs.get(n))).vdVal).shopPlacesKeys.get(0))) {
//                                                ((VD) (vDs.get(n))).IAS = (int) (((VD) (vDs.get(n))).IAS) + 1;
//                                                ((VD) (vDs.get(n))).S = (int) (((VD) (vDs.get(n))).S) - 1;
//                                                break;
//                                            }
//                                        }
//                                    }
//
//                                    currentNumberOfInfectedInVD[j] = currentNumberOfInfectedInVD[j] + 1;
//                                    sumAllInfections = sumAllInfections + 1;
//                                    //println("did infecting");
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            double sumCurrentNumberOfInfectedInVD = 0;
//            for (int j = 0; j < currentNumberOfInfectedInVD.length; j++) {
//                sumCurrentNumberOfInfectedInVD = sumCurrentNumberOfInfectedInVD + currentNumberOfInfectedInVD[j];
//            }
////            System.out.println(sumCurrentNumberOfInfectedInTract + " " + sumCurrentNumberOfInfectedInTract / ((Root) ((((AgentBasedModel) (modelRoot.getABM())).getRootAgent()))).residentPopulation);
////            System.out.println("END INFECTION!");
//        } else {
//            System.out.println("Infection for less than county level not implemented yet!");
//        }
//
//    }
//
//    void initialInfectPeopleCBGVDs(MainModel modelRoot, ArrayList cBGVDs) {
//        //println("1");
//
//        //println("2");
//        //println(dailyConfirmedCases);
//        if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof County) {
//            System.out.println("Infection for County level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof State) {
//            ArrayList<DailyConfirmedCases> dailyConfirmedCases = ((CovidCsseJhu) (modelRoot.covidCsseJhu)).dailyConfirmedCasesList;
//            System.out.println("Infection for State level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof Country) {
//            System.out.println("Infection for country level not implemented yet!");
//        } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof City) {
//            City scope = (City) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//            //ArrayList<Double> percentageSickInCounties=new ArrayList();
//            double[] percentageSickInCBGVDs = new double[scope.cBGVDCells.size()];
//            for (int i = 0; i < percentageSickInCBGVDs.length; i++) {
//                if (scope.cBGVDCells.get(i).cBGsInvolved.size() == 0) {
//                    continue;
//                }
//                for (int d = 0; d < relevantDailyConfirmedCases.size(); d++) {
//                    if (((String) (((State) (((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(scope.censusTracts.get(0).state.name)) {
//                        if (((County) (((((scope.cBGVDCells).get(i)).cBGsInvolved.get(0))).getCounty())).getId() == ((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getId()) {
//                            //if(((ZonedDateTime)(((AgentBasedModel)(modelRoot.getABM())).getCurrentTime()))==null){
//                            //	println("777");
//                            //}
//                            //if(((ZonedDateTime)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getDate()))==null){
//                            //	println("888");
//                            //}
//                            if (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).equals(((ZonedDateTime) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getDate()))) == true) {
//                                //percentageSickInCounties[i]=((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()/((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(i))).getCounty())).getPopulation();
//                                percentageSickInCBGVDs[i] = (((float) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getNumActiveCases())) * ((float) (((scope.cBGVDCells.get(i)).getPopulation())) / (float) (((County) ((scope.cBGVDCells.get(i).cBGsInvolved.get(0)).getCounty())).getPopulation()))) / ((float) (((scope.cBGVDCells.get(i)).getPopulation())));
//                                
////                                System.out.println(i);
//                            }
//                        }
//                    }
//                }
//            }
//
//            double[] generatedPopulationInCBGVDs = new double[scope.cBGVDCells.size()];
//            for (int i = 0; i < modelRoot.getABM().agents.size(); i++) {
//                if (modelRoot.getABM().agents.get(i).myType.equals("Person")) {
//                    for (int j = 0; j < scope.cBGVDCells.size(); j++) {
//                        if ((((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeCBGVD).shopPlacesKeys.get(0).equals(scope.cBGVDCells.get(j).shopPlacesKeys.get(0))) {
//                            generatedPopulationInCBGVDs[j] = generatedPopulationInCBGVDs[j] + 1;
//                            residents.add((Person) (modelRoot.getABM().agents.get(i)));
//                            break;
//                        }
//                    }
//                }
//            }
//            agentPairContact = new int[residents.size()][residents.size()];
//            int sumResident = 0;
//            for (int i = 0; i < generatedPopulationInCBGVDs.length; i++) {
//                sumResident = sumResident + (int) generatedPopulationInCBGVDs[i];
//            }
//            //println(sumResident);
//            ((Root) ((((AgentBasedModel) (modelRoot.getABM())).getRootAgent()))).residentPopulation = sumResident;
//
//            int sumAllInfections = 0;
//
//            int numActiveInfected = 0;
//
//            int scopePopulation = scope.getPopulation();
//            int start = currentAgent.startCountyIndex;
//            int end = currentAgent.endCountyIndex;
//            for (int j = 0; j < ((ArrayList<CensusTract>) (scope.getCensusTracts())).size(); j++) {
//                for (int d = 0; d < relevantDailyConfirmedCases.size(); d++) {
//                    if (((String) (((State) (((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(((State) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getState())).getName())) {
//                        if (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getCounty())).getId() == ((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getId()) {
//                            if (((ZonedDateTime) (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).truncatedTo(ChronoUnit.DAYS))).equals(((ZonedDateTime) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getDate()))) == true) {
//                                //println("daily county cases: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()));
//                                //println("county population: "+(float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation()));
//                                //println("census tract population: "+(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()));
//                                //println("numActiveInfected: "+numActiveInfected);
//                                //println("fraction: "+((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation())));
//                                //println("add: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()*((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()))));
//                                numActiveInfected = numActiveInfected + (int) ((((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getNumActiveCases()) * ((float) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getPopulation()) / (float) (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getCounty())).getPopulation())));
////                                System.out.println(numActiveInfected);
//                            }
//                        }
//                    }
//                }
//            }
//
//            double[] currentNumberOfInfectedInCBGVD = new double[scope.cBGVDCells.size()];
//            for (int u = 0; u < 5; u++) {
//                for (int i = 0; i < ((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).size(); i++) {
//                    if (modelRoot.getABM().agents.get(i).myType.equals("Person")) {
//                        for (int j = 0; j < scope.cBGVDCells.size(); j++) {
//                            if ((((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeCBGVD).shopPlacesKeys.get(0).equals(scope.cBGVDCells.get(j).shopPlacesKeys.get(0))) {
//                                //println("before infecting");
//                                if (((double) currentNumberOfInfectedInCBGVD[j] / (double) generatedPopulationInCBGVDs[j]) <= percentageSickInCBGVDs[j] && ((double) sumAllInfections / (double) ((Root) (modelRoot.getABM().rootAgent)).residentPopulation) <= ((double) numActiveInfected / (double) scopePopulation)) {
//                                    if (Math.random() < 0.7) {
//                                        //println("IS");
//                                        ((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).status = statusEnum.INFECTED_SYM.ordinal();
//                                        for (int n = 0; n < cBGVDs.size(); n++) {
//                                            //println("%%%");
//                                            if ((((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeCBGVD).shopPlacesKeys.get(0).equals((((CBGVD) (cBGVDs.get(n))).cbgvdVal).shopPlacesKeys.get(0))) {
//                                                ((CBGVD) (cBGVDs.get(n))).IS = (int) (((CBGVD) (cBGVDs.get(n))).IS) + 1;
//                                                ((CBGVD) (cBGVDs.get(n))).S = (int) (((CBGVD) (cBGVDs.get(n))).S) - 1;
//                                                break;
//                                            }
//                                        }
//
//                                    } else {
//                                        //println("IAS");
//                                        ((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).status = statusEnum.INFECTED_ASYM.ordinal();
//                                        for (int n = 0; n < cBGVDs.size(); n++) {
//                                            //println("%%%");
//                                            if ((((Person) (((List) ((AgentBasedModel) (modelRoot.getABM())).getAgents()).get(i))).homeCBGVD).shopPlacesKeys.get(0).equals((((CBGVD) (cBGVDs.get(n))).cbgvdVal).shopPlacesKeys.get(0))) {
//                                                ((CBGVD) (cBGVDs.get(n))).IAS = (int) (((CBGVD) (cBGVDs.get(n))).IAS) + 1;
//                                                ((CBGVD) (cBGVDs.get(n))).S = (int) (((CBGVD) (cBGVDs.get(n))).S) - 1;
//                                                break;
//                                            }
//                                        }
//                                    }
//
//                                    currentNumberOfInfectedInCBGVD[j] = currentNumberOfInfectedInCBGVD[j] + 1;
//                                    sumAllInfections = sumAllInfections + 1;
//                                    //println("did infecting");
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            double sumCurrentNumberOfInfectedInTract = 0;
//            for (int j = 0; j < currentNumberOfInfectedInCBGVD.length; j++) {
//                sumCurrentNumberOfInfectedInTract = sumCurrentNumberOfInfectedInTract + currentNumberOfInfectedInCBGVD[j];
//            }
////            System.out.println(sumCurrentNumberOfInfectedInTract + " " + sumCurrentNumberOfInfectedInTract / ((Root) ((((AgentBasedModel) (modelRoot.getABM())).getRootAgent()))).residentPopulation);
////            System.out.println("END INFECTION!");
//        } else {
//            System.out.println("Infection for less than county level not implemented yet!");
//        }
//
//    }
//
////@CompileStatic
//    public class ParallelAgentStatusReporter extends ParallelProcessor {
//
//        public int threadIndex;
//        public MainModel modelRoot;
//        public int statusSusceptible;
//        public int statusExposed;
//        public int statusInfected_sym;
//        public int statusInfected_asym;
//        public int statusRecovered;
//        public int statusDead;
//        public int statusUnknown;
//
//        //@CompileStatic
//        public ParallelAgentStatusReporter(int passed_threadIndex, MainModel passed_modelRoot, Object parent, Object data, int startIndex, int endIndex) {
//            super(parent, data, startIndex, endIndex);
//            threadIndex = passed_threadIndex;
//            modelRoot = passed_modelRoot;
//            myThread = new Thread(new Runnable() {
//                @Override
//                //@CompileStatic
//                public void run() {
//                    System.out.println("POLL STATUSES");
//                    runParallelAgentStatuses(modelRoot, startIndex, endIndex, threadIndex);
//                }
//            });
//        }
//
//        //@CompileStatic
//        void runParallelAgentStatuses(MainModel modelRoot, int startIndex, int endIndex, int threadIndex) {
//            CopyOnWriteArrayList<Agent> agents = ((AgentBasedModel) (modelRoot.getABM())).getAgents();
//            statusSusceptible = 0;
//            statusExposed = 0;
//            statusInfected_sym = 0;
//            statusInfected_asym = 0;
//            statusRecovered = 0;
//            statusDead = 0;
//            statusUnknown = 0;
//            for (int i = startIndex; i < endIndex; i++) {
//                //println("!!!");
//                if (modelRoot.getABM().agents.get(i).myType.equals("Person")) {
//                    //if(((Agent)agents.get(i)).getPropertyValue("cBG")!=null){
//                    int status = (int) (((Person) (agents.get(i))).status);
//                    //println("status: "+status);
//                    if (status == 0) {
//                        statusSusceptible = statusSusceptible + 1;
//                    } else if (status == 1) {
//                        statusExposed = statusExposed + 1;
//                    } else if (status == 2) {
//                        if (((Person) (modelRoot.getABM().agents.get(i))).homeCBG != null) {
//                            if (isInScope(modelRoot, ((Person) (modelRoot.getABM().agents.get(i)))) == true) {
//                                statusInfected_sym = statusInfected_sym + 1;
//                            }
//                        }
//                        statusInfected_sym = statusInfected_sym + 1;
//                    } else if (status == 3) {
//                        if (((Person) (modelRoot.getABM().agents.get(i))).homeCBG != null) {
//                            if (isInScope(modelRoot, ((Person) (modelRoot.getABM().agents.get(i)))) == true) {
//                                statusInfected_asym = statusInfected_asym + 1;
//                            }
//                        }
//                        statusInfected_asym = statusInfected_asym + 1;
//                    } else if (status == 4) {
//                        statusRecovered = statusRecovered + 1;
//                    } else if (status == 5) {
//                        statusDead = statusDead + 1;
//                    } else if (status == -1) {
//                        statusUnknown = statusUnknown + 1;
//                    }
//                    //}
//                }
//            }
//        }
//
//    }
//
//    /*
//@CompileStatic
//public class ParallelStateReporter extends ParallelProcessor {
//
//	public int threadIndex;
//	public MainModel modelRoot;
//	public int statusSusceptible;
//	public int statusExposed;
//	public int statusInfected_sym;
//	public int statusInfected_asym;
//	public int statusRecovered;
//	public int statusDead;
//	public int statusUnknown;
//	
//
//	@CompileStatic
//	public ParallelAgentStatusReporter(int passed_threadIndex, MainModel passed_modelRoot, Object parent, Object data, int startIndex, int endIndex) {
//		super(parent, data, startIndex, endIndex);
//		threadIndex=passed_threadIndex;
//		modelRoot=passed_modelRoot;
//		myThread = new Thread(new Runnable() {
//            @Override
//            @CompileStatic
//            public void run() {
//            	println("POLL STATUSES");
//            	runParallelAgentStatuses(modelRoot,startIndex,endIndex,threadIndex);
//            }
//		});
//	}
//
//	@CompileStatic
//	def runParallelAgentStatuses(MainModel modelRoot, int startIndex, int endIndex, int threadIndex){
//		CopyOnWriteArrayList<Agent> agents=((AgentBasedModel)(modelRoot.getABM())).getAgents();
//		statusSusceptible=0;
//		statusExposed=0;
//		statusInfected_sym=0;
//		statusInfected_asym=0;
//		statusRecovered=0;
//		statusDead=0;
//		statusUnknown=0;
//		for(int i=startIndex;i<endIndex;i++){
//			//println("!!!");
//			if(((String)(((AgentTemplate)(agents.get(i).getMyTemplate())).getAgentTypeName())).equals("Person")){
//				if(((Agent)agents.get(i)).getPropertyValue("cBG")!=null){
//					int status=(int)(((Agent)(agents.get(i))).getPropertyValue("status"));
//					//println("status: "+status);
//					if(status==0){
//						statusSusceptible=statusSusceptible+1;
//					}else if(status==1){
//						statusExposed=statusExposed+1;
//					}else if(status==2){
//						statusInfected_sym=statusInfected_sym+1;
//					}else if(status==3){
//						statusInfected_asym=statusInfected_asym+1;
//					}else if(status==4){
//						statusRecovered=statusRecovered+1;
//					}else if(status==5){
//						statusDead=statusDead+1;
//					}else if(status==-1){
//						statusUnknown=statusUnknown+1;
//					}
//				}
//			}
//		}
//	}
//
//	
//}
//     */
////@CompileStatic
//    void writeMinuteRecord(MainModel modelRoot, Root currentAgent, ZonedDateTime currentDate) {
//        if (currentDate.getHour() == 0 && currentDate.getMinute() == 1) {
//            if (modelRoot.ABM.studyScope.equals("USA_NY_Richmond County_New York")) {
//
//                ArrayList<County> counties = new ArrayList();
//                for (int i = 0; i < ((City) modelRoot.ABM.studyScopeGeography).censusTracts.size(); i++) {
//                    boolean isCountyFound = false;
//                    for (int j = 0; j < counties.size(); j++) {
//                        if (counties.get(j).id == ((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county.id) {
//                            isCountyFound = true;
//                            break;
//                        }
//                    }
//                    if (isCountyFound == false) {
//                        counties.add(((City) modelRoot.ABM.studyScopeGeography).censusTracts.get(i).county);
//                    }
//                }
//                int numCounties = counties.size();
//
//                CopyOnWriteArrayList<Agent> agents = ((AgentBasedModel) (modelRoot.getABM())).getAgents();
//                int numAgents = agents.size();
//                int statusSusceptible[] = new int[numCounties];
//                int statusExposed[] = new int[numCounties];
//                int statusInfected_sym[] = new int[numCounties];
//                int statusInfected_asym[] = new int[numCounties];
//                int statusRecovered[] = new int[numCounties];
//                int statusDead[] = new int[numCounties];
//                int statusUnknown[] = new int[numCounties];
//                for (int i = 0; i < numAgents; i++) {
//                    if (modelRoot.getABM().agents.get(i).myType.equals("Person")) {
//                        int status = (int) (((Person) (agents.get(i))).status);
//                        int countyIndex = -1;
//                        if (modelRoot.scenario.equals("CBG")) {
//                            for (int k = 0; k < counties.size(); k++) {
//                                if (((Person) (modelRoot.getABM().agents.get(i))).homeCBG.county.id == counties.get(k).id) {
//                                    countyIndex = k;
//                                    break;
//                                }
//                            }
//                        } else if (modelRoot.scenario.equals("VD")) {
//                            for (int k = 0; k < counties.size(); k++) {
//                                if (((Person) (modelRoot.getABM().agents.get(i))).homeCBG.county.id == counties.get(k).id) {
//                                    countyIndex = k;
//                                    break;
//                                }
//                            }
//                        } else if (modelRoot.scenario.equals("CBGVD")) {
//                            for (int k = 0; k < counties.size(); k++) {
//                                if (((Person) (modelRoot.getABM().agents.get(i))).homeCBG.county.id == counties.get(k).id) {
//                                    countyIndex = k;
//                                    break;
//                                }
//                            }
//                        }
//                        if (countyIndex == -1) {
//                            //System.out.println("COUNTY NOT FOUND!!!");
//                            continue;
//                        }
//                        if (status == 0) {
//                            statusSusceptible[countyIndex] = statusSusceptible[countyIndex] + 1;
//                        } else if (status == 1) {
//                            statusExposed[countyIndex] = statusExposed[countyIndex] + 1;
//                        } else if (status == 2) {
//                            statusInfected_sym[countyIndex] = statusInfected_sym[countyIndex] + 1;
//                        } else if (status == 3) {
//                            statusInfected_asym[countyIndex] = statusInfected_asym[countyIndex] + 1;
//                        } else if (status == 4) {
//                            statusRecovered[countyIndex] = statusRecovered[countyIndex] + 1;
//                        } else if (status == 5) {
//                            statusDead[countyIndex] = statusDead[countyIndex] + 1;
//                        } else if (status == -1) {
//                            statusUnknown[countyIndex] = statusUnknown[countyIndex] + 1;
//                        }
//                    }
//                }
//                if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof County) {
//                    System.out.println("Infection for County level not implemented for NEWYORK!");
//                } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof State) {
//                    System.out.println("Infection for State level not implemented for NEWYORK!");
//                } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof Country) {
//                    System.out.println("Infection for County level not implemented for NEWYORK!");
//                } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof City) {
//                    ArrayList<DailyConfirmedCases> dailyConfirmedCases = ((CovidCsseJhu) (modelRoot.covidCsseJhu)).dailyConfirmedCasesList;
//                    int realInfection[] = new int[numCounties];
//                    int numActiveInfected[] = new int[numCounties];
//                    int scopePopulation[] = new int[numCounties];
//                    City scope = (City) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//                    for (int n = 0; n < numCounties; n++) {
//                        scopePopulation[n] = counties.get(n).population;
//                    }
//
//                    int start = currentAgent.startCountyIndex;
//                    int end = currentAgent.endCountyIndex;
//
//                    for (int j = 0; j < counties.size(); j++) {
//                        for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//                            if (((String) (((State) (((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(counties.get(j).state.name)) {
//                                if (counties.get(j).id == ((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getId()) {
//                                    if (((ZonedDateTime) (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).truncatedTo(ChronoUnit.DAYS))).equals(((ZonedDateTime) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getDate()))) == true) {
//                                        //println("daily county cases: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()));
//                                        //println("county population: "+(float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation()));
//                                        //println("census tract population: "+(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()));
//                                        //println("numActiveInfected: "+numActiveInfected);
//                                        //println("fraction: "+((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation())));
//                                        //println("add: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()*((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()))));
//                                        numActiveInfected[j] = (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getNumActiveCases());
//                                        //System.out.println(numActiveInfected);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    try {
//                        StringBuilder data = new StringBuilder();
//
//                        data.append(currentDate.toString());
//
//                        for (int n = 0; n < numCounties; n++) {
//                            data.append(",");
//                            data.append(statusSusceptible[n]);
//                            data.append(",");
//                            data.append(statusExposed[n]);
//                            data.append(",");
//                            data.append(statusInfected_sym[n]);
//                            data.append(",");
//                            data.append(statusInfected_asym[n]);
//                            data.append(",");
//                            data.append(statusRecovered[n]);
//                            data.append(",");
//                            data.append(statusDead[n]);
//                            data.append(",");
//                            data.append(statusUnknown[n]);
//                            data.append(",");
//                            int residentPop = (int) (((Root) ((((AgentBasedModel) (modelRoot.getABM())).getRootAgent()))).residentPopulation);
//                            data.append((float) (statusInfected_sym[n] + statusInfected_asym[n]) / (float) (residentPop));
//                            data.append(",");
//                            data.append(numActiveInfected[n]);
//                            data.append(",");
//                            data.append((float) (numActiveInfected[n]) / (float) (scopePopulation[n]));
//                        }
//                        data.append("\n");
//                        File f1 = new File("./output_NEWYORK_EXCEPTION_" + modelRoot.ABM.studyScope + "_" + modelRoot.scenario + "_" + modelRoot.ABM.startTime.getYear() + "_" + modelRoot.ABM.startTime.getMonth() + "_" + modelRoot.sparsifyFraction + ".csv");
//                        if (!f1.exists()) {
//                            f1.createNewFile();
//                        }
//
//                        FileWriter fileWritter = new FileWriter(f1.getName(), true);
//                        BufferedWriter bw = new BufferedWriter(fileWritter);
//                        bw.write(data.toString());
//                        bw.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//            try {
//                CopyOnWriteArrayList<Agent> agents = ((AgentBasedModel) (modelRoot.getABM())).getAgents();
//                StringBuilder data = new StringBuilder();
//                int statusSusceptible = 0;
//                int statusExposed = 0;
//                int statusInfected_sym = 0;
//                int statusInfected_asym = 0;
//                int statusRecovered = 0;
//                int statusDead = 0;
//                int statusUnknown = 0;
//
//                //println("!!!");
//                int numAgents = agents.size();
//                int numProcessors = modelRoot.getNumCPUs();
////                if (numProcessors > Runtime.getRuntime().availableProcessors()) {
////                    numProcessors = Runtime.getRuntime().availableProcessors();
////                }
//                ParallelAgentStatusReporter[] parallelAgentStatusReporter = new ParallelAgentStatusReporter[numProcessors];
//
//                for (int i = 0; i < numProcessors - 1; i++) {
//                    parallelAgentStatusReporter[i] = new ParallelAgentStatusReporter(i, modelRoot, null, null, (int) Math.floor((double) (i * ((numAgents) / numProcessors))), (int) Math.floor((double) ((i + 1) * ((numAgents) / numProcessors))));
//                }
//                parallelAgentStatusReporter[numProcessors - 1] = new ParallelAgentStatusReporter(numProcessors - 1, modelRoot, null, null, (int) Math.floor((double) ((numProcessors - 1) * ((numAgents) / numProcessors))), numAgents);
//
//                for (int i = 0; i < numProcessors; i++) {
//                    parallelAgentStatusReporter[i].myThread.start();
//                    System.out.println("thread " + i + " started for statuses!");
//                }
//                for (int i = 0; i < numProcessors; i++) {
//                    try {
//                        System.out.println("####");
//                        parallelAgentStatusReporter[i].myThread.join();
//                        System.out.println("thread " + i + " finished for agents: " + parallelAgentStatusReporter[i].myStartIndex + " | " + parallelAgentStatusReporter[i].myEndIndex);
//                        statusSusceptible = statusSusceptible + parallelAgentStatusReporter[i].statusSusceptible;
//                        statusExposed = statusExposed + parallelAgentStatusReporter[i].statusExposed;
//                        statusInfected_sym = statusInfected_sym + parallelAgentStatusReporter[i].statusInfected_sym;
//                        statusInfected_asym = statusInfected_asym + parallelAgentStatusReporter[i].statusInfected_asym;
//                        statusRecovered = statusRecovered + parallelAgentStatusReporter[i].statusRecovered;
//                        statusDead = statusDead + parallelAgentStatusReporter[i].statusDead;
//                        statusUnknown = statusUnknown + parallelAgentStatusReporter[i].statusUnknown;
//                    } catch (InterruptedException ie) {
//                        System.out.println(ie.toString());
//                    }
//                }
//
//                int realInfection = 0;
//                int numActiveInfected = 0;
//                int scopePopulation = 0;
//
//                //DEBUG
//                int counterNullCBG = 0;
//                for (int i = 0; i < agents.size(); i++) {
//                    if (agents.get(i).myType.equals("Person")) {
//                        if (((Person) (agents.get(i))).cBG == null) {
//                            counterNullCBG = counterNullCBG + 1;
//                        }
//                    }
//                }
//                System.out.println("counterNullCBG: " + counterNullCBG);
//                //DEBUG
//
//                /* 	//SERIALLY GET AGENT STATUSES
//			for(int i=0;i<agents.size();i++){
//				//println("!!!");
//				if(((String)(((AgentTemplate)(agents.get(i).getMyTemplate())).getAgentTypeName())).equals("Person")){
//					if(((Agent)agents.get(i)).getPropertyValue("cBG")!=null){
//						int status=(int)(((Agent)(agents.get(i))).getPropertyValue("status"));
//						//println("status: "+status);
//						if(status==0){
//							statusSusceptible=statusSusceptible+1;
//						}else if(status==1){
//							statusExposed=statusExposed+1;
//						}else if(status==2){
//							statusInfected_sym=statusInfected_sym+1;
//						}else if(status==3){
//							statusInfected_asym=statusInfected_asym+1;
//						}else if(status==4){
//							statusRecovered=statusRecovered+1;
//						}else if(status==5){
//							statusDead=statusDead+1;
//						}else if(status==-1){
//							statusUnknown=statusUnknown+1;
//						}
//					}
//				}
//			}
//                 */
//                if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof County) {
//
//                } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof State) {
//                    ArrayList<DailyConfirmedCases> dailyConfirmedCases = ((CovidCsseJhu) (modelRoot.covidCsseJhu)).dailyConfirmedCasesList;
//                    State scope = (State) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//                    scopePopulation = scope.getPopulation();
//                    for (int j = 0; j < ((ArrayList<County>) (scope.getCounties())).size(); j++) {
//                        for (int d = 0; d < dailyConfirmedCases.size(); d++) {
//                            if (((String) (((State) (((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(scope.getName())) {
//                                if (((County) (((ArrayList<County>) (scope.getCounties())).get(j))).getId() == ((County) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getCounty())).getId()) {
//                                    if (((ZonedDateTime) (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).truncatedTo(ChronoUnit.DAYS))).equals(((ZonedDateTime) (((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getDate()))) == true) {
//                                        numActiveInfected = numActiveInfected + ((DailyConfirmedCases) (dailyConfirmedCases.get(d))).getNumActiveCases();
//                                        //println(d);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof Country) {
//                    System.out.println("Infection for country level not implemented yet!");
//                } else if (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography instanceof City) {
//                    City scope = (City) (((AgentBasedModel) (modelRoot.getABM())).studyScopeGeography);
//                    scopePopulation = scope.getPopulation();
//                    int start = currentAgent.startCountyIndex;
//                    int end = currentAgent.endCountyIndex;
//                    for (int j = 0; j < ((ArrayList<CensusTract>) (scope.getCensusTracts())).size(); j++) {
//                        for (int d = 0; d < relevantDailyConfirmedCases.size(); d++) {
//                            if (((String) (((State) (((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getState())).getName())).equals(((State) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(0))).getState())).getName())) {
//                                if (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getCounty())).getId() == ((County) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getCounty())).getId()) {
//                                    if (((ZonedDateTime) (((ZonedDateTime) (((AgentBasedModel) (modelRoot.getABM())).getCurrentTime())).truncatedTo(ChronoUnit.DAYS))).equals(((ZonedDateTime) (((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getDate()))) == true) {
//                                        //println("daily county cases: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()));
//                                        //println("county population: "+(float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation()));
//                                        //println("census tract population: "+(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()));
//                                        //println("numActiveInfected: "+numActiveInfected);
//                                        //println("fraction: "+((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation())));
//                                        //println("add: "+(int)(((DailyConfirmedCases)(dailyConfirmedCases.get(d))).getNumActiveCases()*((float)(((County)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getCounty())).getPopulation())/(float)(((CensusTract)(((ArrayList<CensusTract>)(scope.getCensusTracts())).get(j))).getPopulation()))));
//                                        numActiveInfected = numActiveInfected + (int) ((((DailyConfirmedCases) (relevantDailyConfirmedCases.get(d))).getNumActiveCases()) * ((float) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getPopulation()) / (float) (((County) (((CensusTract) (((ArrayList<CensusTract>) (scope.getCensusTracts())).get(j))).getCounty())).getPopulation())));
//                                        //System.out.println(numActiveInfected);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    System.out.println("Infection for less than county level not implemented yet!");
//                }
//
//                data.append(currentDate.toString());
//                data.append(",");
//                data.append(statusSusceptible);
//                data.append(",");
//                data.append(statusExposed);
//                data.append(",");
//                data.append(statusInfected_sym);
//                data.append(",");
//                data.append(statusInfected_asym);
//                data.append(",");
//                data.append(statusRecovered);
//                data.append(",");
//                data.append(statusDead);
//                data.append(",");
//                data.append(statusUnknown);
//                data.append(",");
//                int residentPop = (int) (((Root) ((((AgentBasedModel) (modelRoot.getABM())).getRootAgent()))).residentPopulation);
//                data.append((float) (statusInfected_sym + statusInfected_asym) / (float) (residentPop));
//                data.append(",");
//                //println("^^^");
//                //println(numActiveInfected);
//                data.append(numActiveInfected);
//                data.append(",");
//                data.append((float) (numActiveInfected) / (float) (scopePopulation));
//                data.append("\n");
//                File f1 = new File("./output_" + modelRoot.ABM.studyScope + "_" + modelRoot.scenario + "_" + modelRoot.ABM.startTime.getYear() + "_" + modelRoot.ABM.startTime.getMonth() + "_" + modelRoot.sparsifyFraction + ".csv");
//                if (!f1.exists()) {
//                    f1.createNewFile();
//                }
//
//                FileWriter fileWritter = new FileWriter(f1.getName(), true);
//                BufferedWriter bw = new BufferedWriter(fileWritter);
//                bw.write(data.toString());
//                bw.close();
//                //System.out.println("Done");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            currentAgent.counter = (int) (currentAgent.counter) + 1;
//            if (((int) (currentAgent.counter)) == 50) {
//
//                CopyOnWriteArrayList<Agent> agents = ((AgentBasedModel) (modelRoot.getABM())).getAgents();
//                for (int i = 0; i < agents.size(); i++) {
//                    if (modelRoot.getABM().agents.get(i).myType.equals("CBG") || modelRoot.getABM().agents.get(i).myType.equals("VD") || modelRoot.getABM().agents.get(i).myType.equals("CBGVD")) {
//                        if (modelRoot.scenario.equals("CBG")) {
//                            ((CBG) (agents.get(i))).N = 0;
//                            ((CBG) (agents.get(i))).S = 0;
//                            ((CBG) (agents.get(i))).E = 0;
//                            ((CBG) (agents.get(i))).IS = 0;
//                            ((CBG) (agents.get(i))).IAS = 0;
//                            ((CBG) (agents.get(i))).R = 0;
//                        } else if (modelRoot.scenario.equals("VD")) {
//                            ((VD) (agents.get(i))).N = 0;
//                            ((VD) (agents.get(i))).S = 0;
//                            ((VD) (agents.get(i))).E = 0;
//                            ((VD) (agents.get(i))).IS = 0;
//                            ((VD) (agents.get(i))).IAS = 0;
//                            ((VD) (agents.get(i))).R = 0;
//                        } else if (modelRoot.scenario.equals("CBGVD")) {
//                            ((CBGVD) (agents.get(i))).N = 0;
//                            ((CBGVD) (agents.get(i))).S = 0;
//                            ((CBGVD) (agents.get(i))).E = 0;
//                            ((CBGVD) (agents.get(i))).IS = 0;
//                            ((CBGVD) (agents.get(i))).IAS = 0;
//                            ((CBGVD) (agents.get(i))).R = 0;
//                        }
//                    }
//                }
//
//                for (int i = 0; i < agents.size(); i++) {
//                    //println("!!!");
//                    if (modelRoot.getABM().agents.get(i).myType.equals("Person")) {
//                        if (modelRoot.scenario.equals("CBG")) {
//                            if (((Person) agents.get(i)).cBG != null) {
//                                int status = (int) (((Person) (agents.get(i))).status);
//                                //println("status: "+status);
//                                if (status == 0) {
////							statusSusceptible=statusSusceptible+1;
//                                    ((CBG) (((Person) agents.get(i)).cBG)).N = ((int) (((CBG) (((Person) agents.get(i)).cBG)).N)) + 1;
//                                } else if (status == 1) {
//                                    ((CBG) (((Person) agents.get(i)).cBG)).E = ((int) (((CBG) (((Person) agents.get(i)).cBG)).E)) + 1;
//                                } else if (status == 2) {
//                                    ((CBG) (((Person) agents.get(i)).cBG)).IS = ((int) (((CBG) (((Person) agents.get(i)).cBG)).IS)) + 1;
//                                } else if (status == 3) {
//                                    ((CBG) (((Person) agents.get(i)).cBG)).IAS = ((int) (((CBG) (((Person) agents.get(i)).cBG)).IAS)) + 1;
//                                } else if (status == 4) {
//                                    ((CBG) (((Person) agents.get(i)).cBG)).R = ((int) (((CBG) (((Person) agents.get(i)).cBG)).R)) + 1;
//                                } else if (status == 5) {
//                                    //statusDead=statusDead+1;
//                                } else if (status == -1) {
//                                    //statusUnknown=statusUnknown+1;
//                                }
//                            } else {
//                                CensusBlockGroup homeCBG = ((CensusBlockGroup) (((Person) agents.get(i)).homeCBG));
//                                int status = (int) (((Person) (agents.get(i))).status);
//                                for (int j = 0; j < agents.size(); j++) {
//                                    if (modelRoot.getABM().agents.get(j).myType.equals("CBG")) {
//                                        if (((CensusBlockGroup) (((CBG) (agents.get(j))).cbgVal)).getId() == homeCBG.getId()) {
//                                            if (status == 0) {
////										statusSusceptible=statusSusceptible+1;
//                                                ((CBG) agents.get(j)).N = ((int) (((CBG) agents.get(j)).N)) + 1;
//                                            } else if (status == 1) {
//                                                ((CBG) agents.get(j)).E = ((int) (((CBG) agents.get(j)).E)) + 1;
//                                            } else if (status == 2) {
//                                                ((CBG) agents.get(j)).IS = ((int) (((CBG) agents.get(j)).IS)) + 1;
//                                            } else if (status == 3) {
//                                                ((CBG) agents.get(j)).IAS = ((int) (((CBG) agents.get(j)).IAS)) + 1;
//                                            } else if (status == 4) {
//                                                ((CBG) agents.get(j)).R = ((int) (((CBG) agents.get(j)).R)) + 1;
//                                            } else if (status == 5) {
//                                                //statusDead=statusDead+1;
//                                            } else if (status == -1) {
//                                                //statusUnknown=statusUnknown+1;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        } else if (modelRoot.scenario.equals("VD")) {
//                            if (((Person) agents.get(i)).vD != null) {
//                                int status = (int) (((Person) (agents.get(i))).status);
//                                //println("status: "+status);
//                                if (status == 0) {
////							statusSusceptible=statusSusceptible+1;
//                                    ((VD) (((Person) agents.get(i)).vD)).N = ((int) (((VD) (((Person) agents.get(i)).vD)).N)) + 1;
//                                } else if (status == 1) {
//                                    ((VD) (((Person) agents.get(i)).vD)).E = ((int) (((VD) (((Person) agents.get(i)).vD)).E)) + 1;
//                                } else if (status == 2) {
//                                    ((VD) (((Person) agents.get(i)).vD)).IS = ((int) (((VD) (((Person) agents.get(i)).vD)).IS)) + 1;
//                                } else if (status == 3) {
//                                    ((VD) (((Person) agents.get(i)).vD)).IAS = ((int) (((VD) (((Person) agents.get(i)).vD)).IAS)) + 1;
//                                } else if (status == 4) {
//                                    ((VD) (((Person) agents.get(i)).vD)).R = ((int) (((VD) (((Person) agents.get(i)).vD)).R)) + 1;
//                                } else if (status == 5) {
//                                    //statusDead=statusDead+1;
//                                } else if (status == -1) {
//                                    //statusUnknown=statusUnknown+1;
//                                }
//                            } else {
//                                VDCell homeVD = ((VDCell) (((Person) agents.get(i)).homeVD));
//                                int status = (int) (((Person) (agents.get(i))).status);
//                                for (int j = 0; j < agents.size(); j++) {
//                                    if (modelRoot.getABM().agents.get(j).myType.equals("CBG")) {
//                                        if (((VDCell) (((VD) (agents.get(j))).vdVal)).shopPlacesKeys.get(0).equals(homeVD.shopPlacesKeys.get(0))) {
//                                            if (status == 0) {
////										statusSusceptible=statusSusceptible+1;
//                                                ((VD) agents.get(j)).N = ((int) (((VD) agents.get(j)).N)) + 1;
//                                            } else if (status == 1) {
//                                                ((VD) agents.get(j)).E = ((int) (((VD) agents.get(j)).E)) + 1;
//                                            } else if (status == 2) {
//                                                ((VD) agents.get(j)).IS = ((int) (((VD) agents.get(j)).IS)) + 1;
//                                            } else if (status == 3) {
//                                                ((VD) agents.get(j)).IAS = ((int) (((VD) agents.get(j)).IAS)) + 1;
//                                            } else if (status == 4) {
//                                                ((VD) agents.get(j)).R = ((int) (((VD) agents.get(j)).R)) + 1;
//                                            } else if (status == 5) {
//                                                //statusDead=statusDead+1;
//                                            } else if (status == -1) {
//                                                //statusUnknown=statusUnknown+1;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        } else if (modelRoot.scenario.equals("CBGVD")) {
//                            if (((Person) agents.get(i)).cBGVD != null) {
//                                int status = (int) (((Person) (agents.get(i))).status);
//                                //println("status: "+status);
//                                if (status == 0) {
////							statusSusceptible=statusSusceptible+1;
//                                    ((CBGVD) (((Person) agents.get(i)).cBGVD)).N = ((int) (((CBGVD) (((Person) agents.get(i)).cBGVD)).N)) + 1;
//                                } else if (status == 1) {
//                                    ((CBGVD) (((Person) agents.get(i)).cBGVD)).E = ((int) (((CBGVD) (((Person) agents.get(i)).cBGVD)).E)) + 1;
//                                } else if (status == 2) {
//                                    ((CBGVD) (((Person) agents.get(i)).cBGVD)).IS = ((int) (((CBGVD) (((Person) agents.get(i)).cBGVD)).IS)) + 1;
//                                } else if (status == 3) {
//                                    ((CBGVD) (((Person) agents.get(i)).cBGVD)).IAS = ((int) (((CBGVD) (((Person) agents.get(i)).cBGVD)).IAS)) + 1;
//                                } else if (status == 4) {
//                                    ((CBGVD) (((Person) agents.get(i)).cBGVD)).R = ((int) (((CBGVD) (((Person) agents.get(i)).cBGVD)).R)) + 1;
//                                } else if (status == 5) {
//                                    //statusDead=statusDead+1;
//                                } else if (status == -1) {
//                                    //statusUnknown=statusUnknown+1;
//                                }
//                            } else {
//                                CBGVDCell homeCBGVD = ((CBGVDCell) (((Person) agents.get(i)).homeCBGVD));
//                                int status = (int) (((Person) (agents.get(i))).status);
//                                for (int j = 0; j < agents.size(); j++) {
//                                    if (modelRoot.getABM().agents.get(j).myType.equals("CBG")) {
//                                        if (((CBGVDCell) (((CBGVD) (agents.get(j))).cbgvdVal)).shopPlacesKeys.get(0).equals(homeCBGVD.shopPlacesKeys.get(0))) {
//                                            if (status == 0) {
////										statusSusceptible=statusSusceptible+1;
//                                                ((CBGVD) agents.get(j)).N = ((int) (((CBGVD) agents.get(j)).N)) + 1;
//                                            } else if (status == 1) {
//                                                ((CBGVD) agents.get(j)).E = ((int) (((CBGVD) agents.get(j)).E)) + 1;
//                                            } else if (status == 2) {
//                                                ((CBGVD) agents.get(j)).IS = ((int) (((CBGVD) agents.get(j)).IS)) + 1;
//                                            } else if (status == 3) {
//                                                ((CBGVD) agents.get(j)).IAS = ((int) (((CBGVD) agents.get(j)).IAS)) + 1;
//                                            } else if (status == 4) {
//                                                ((CBGVD) agents.get(j)).R = ((int) (((CBGVD) agents.get(j)).R)) + 1;
//                                            } else if (status == 5) {
//                                                //statusDead=statusDead+1;
//                                            } else if (status == -1) {
//                                                //statusUnknown=statusUnknown+1;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            if (currentAgent.counter == 25) {
//                StringBuilder data = new StringBuilder();
//                data.append("CBG,N,S,E,IS,IAS,R\n");
//                CopyOnWriteArrayList<Agent> agents = ((AgentBasedModel) (modelRoot.getABM())).getAgents();
//                for (int i = 0; i < agents.size(); i++) {
//                    if (agents.get(i).myType.equals("CBG")) {
//                        if (modelRoot.scenario.equals("CBG")) {
//                            int N = ((int) (((CBG) (agents.get(i))).N));
//                            int S = ((int) (((CBG) (agents.get(i))).S));
//                            int E = ((int) (((CBG) (agents.get(i))).E));
//                            int IS = ((int) (((CBG) (agents.get(i))).IS));
//                            int IAS = ((int) (((CBG) (agents.get(i))).IAS));
//                            int R = ((int) (((CBG) (agents.get(i))).R));
//                            data.append(((CensusBlockGroup) (((CBG) (agents.get(i))).cbgVal)).getId());
//                            data.append(",");
//                            data.append(N);
//                            data.append(",");
//                            data.append(S);
//                            data.append(",");
//                            data.append(E);
//                            data.append(",");
//                            data.append(IS);
//                            data.append(",");
//                            data.append(IAS);
//                            data.append(",");
//                            data.append(R);
//                            data.append("\n");
//                        } else if (modelRoot.scenario.equals("VD")) {
//                            int N = ((int) (((VD) (agents.get(i))).N));
//                            int S = ((int) (((VD) (agents.get(i))).S));
//                            int E = ((int) (((VD) (agents.get(i))).E));
//                            int IS = ((int) (((VD) (agents.get(i))).IS));
//                            int IAS = ((int) (((VD) (agents.get(i))).IAS));
//                            int R = ((int) (((VD) (agents.get(i))).R));
//                            data.append(((VDCell) (((VD) (agents.get(i))).vdVal)).shopPlacesKeys.get(0));
//                            data.append(",");
//                            data.append(N);
//                            data.append(",");
//                            data.append(S);
//                            data.append(",");
//                            data.append(E);
//                            data.append(",");
//                            data.append(IS);
//                            data.append(",");
//                            data.append(IAS);
//                            data.append(",");
//                            data.append(R);
//                            data.append("\n");
//                        } else if (modelRoot.scenario.equals("CBGVD")) {
//                            int N = ((int) (((CBGVD) (agents.get(i))).N));
//                            int S = ((int) (((CBGVD) (agents.get(i))).S));
//                            int E = ((int) (((CBGVD) (agents.get(i))).E));
//                            int IS = ((int) (((CBGVD) (agents.get(i))).IS));
//                            int IAS = ((int) (((CBGVD) (agents.get(i))).IAS));
//                            int R = ((int) (((CBGVD) (agents.get(i))).R));
//                            data.append(((CBGVDCell) (((CBGVD) (agents.get(i))).cbgvdVal)).shopPlacesKeys.get(0));
//                            data.append(",");
//                            data.append(N);
//                            data.append(",");
//                            data.append(S);
//                            data.append(",");
//                            data.append(E);
//                            data.append(",");
//                            data.append(IS);
//                            data.append(",");
//                            data.append(IAS);
//                            data.append(",");
//                            data.append(R);
//                            data.append("\n");
//                        }
//                    }
//                    System.out.println("WRITING CBG NUMBERS!");
//
//                    File f1 = new File("./output_CBGs" + "_" + modelRoot.sparsifyFraction + ".csv");
//                    //if (!f1.exists()) {
//                    try {
//                        f1.createNewFile();
//                        FileWriter fileWritter = new FileWriter(f1.getName(), false);
//                        BufferedWriter bw = new BufferedWriter(fileWritter);
//                        bw.write(data.toString());
//                        bw.close();
//                    } catch (IOException ex) {
//                        Logger.getLogger(Root.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    //}
//                }
//            }
//
//        }
//
//    }
//
//    public boolean isInScope(MainModel modelRoot, Person person) {
//        if ((modelRoot.ABM).studyScopeGeography instanceof Country) {
//
//        } else if ((modelRoot.ABM).studyScopeGeography instanceof State) {
//
//        } else if ((modelRoot.ABM).studyScopeGeography instanceof County) {
//
//        } else if ((modelRoot.ABM).studyScopeGeography instanceof City) {
//            City scope = (City) ((modelRoot.ABM).studyScopeGeography);
//            for (int i = 0; i < scope.censusTracts.size(); i++) {
//                for (int j = 0; j < scope.censusTracts.get(i).censusBlocks.size(); j++) {
//                    if (person.homeCBG.id == scope.censusTracts.get(i).censusBlocks.get(j).id) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    public Object[] getClosenessAdjustmentVD(Person agent, ArrayList<PatternsRecordProcessed> dests, ArrayList<Float> destFreqs) {
//        double first = Double.POSITIVE_INFINITY;
//        double second = Double.POSITIVE_INFINITY;
//        double third = Double.POSITIVE_INFINITY;
//        double fourth = Double.POSITIVE_INFINITY;
//        double fith = Double.POSITIVE_INFINITY;
//        double sxith = Double.POSITIVE_INFINITY;
//        PatternsRecordProcessed firstPattern = null;
//        PatternsRecordProcessed secondPattern = null;
//        PatternsRecordProcessed thirdPattern = null;
//        PatternsRecordProcessed fourthPattern = null;
//        PatternsRecordProcessed fithPattern = null;
//        PatternsRecordProcessed sxithPattern = null;
//
//        ArrayList destinationPlaces = new ArrayList();
//
//        if (firstPattern != null) {
//            destinationPlaces.add(firstPattern);
//            if (!secondPattern.placeKey.equals(firstPattern.placeKey)) {
//                destinationPlaces.add(secondPattern);
//                if (!thirdPattern.placeKey.equals(secondPattern.placeKey)) {
//                    destinationPlaces.add(thirdPattern);
//                    if (!fourthPattern.placeKey.equals(thirdPattern.placeKey)) {
//                        destinationPlaces.add(fourthPattern);
//                        if (!fithPattern.placeKey.equals(fourthPattern.placeKey)) {
//                            destinationPlaces.add(fithPattern);
//                            if (!sxithPattern.placeKey.equals(fithPattern.placeKey)) {
//                                destinationPlaces.add(sxithPattern);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        for (int i = 0; i < dests.size(); i++) {
//            if (firstPattern != null) {
//                if (!dests.get(i).placeKey.equals(firstPattern.placeKey)) {
//                    if (!dests.get(i).placeKey.equals(secondPattern.placeKey)) {
//                        if (!dests.get(i).placeKey.equals(thirdPattern.placeKey)) {
//                            if (!dests.get(i).placeKey.equals(fourthPattern.placeKey)) {
//                                if (!dests.get(i).placeKey.equals(fithPattern.placeKey)) {
//                                    if (!dests.get(i).placeKey.equals(sxithPattern.placeKey)) {
//                                        destinationPlaces.add(dests.get(i));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            } else {
//                destinationPlaces.add(dests.get(i));
//            }
//        }
//
//        ArrayList destinationPlacesFreq = new ArrayList();
//        ArrayList<Float> destinationPlacesVDFreq = new ArrayList();
//        float sumVD = Math.max(73, 73 + destFreqs.size() - 6);
//
//        switch (destFreqs.size()) {
//            case 0:
//                break;
//            case 1:
//                destinationPlacesVDFreq.add(30f / sumVD);
//                break;
//            case 2:
//                destinationPlacesVDFreq.add(30f / sumVD);
//                destinationPlacesVDFreq.add(14f / sumVD);
//                break;
//            case 3:
//                destinationPlacesVDFreq.add(30f / sumVD);
//                destinationPlacesVDFreq.add(14f / sumVD);
//                destinationPlacesVDFreq.add(11f / sumVD);
//                break;
//            case 4:
//                destinationPlacesVDFreq.add(30f / sumVD);
//                destinationPlacesVDFreq.add(14f / sumVD);
//                destinationPlacesVDFreq.add(11f / sumVD);
//                destinationPlacesVDFreq.add(9f / sumVD);
//                break;
//            case 5:
//                destinationPlacesVDFreq.add(30f / sumVD);
//                destinationPlacesVDFreq.add(14f / sumVD);
//                destinationPlacesVDFreq.add(11f / sumVD);
//                destinationPlacesVDFreq.add(9f / sumVD);
//                destinationPlacesVDFreq.add(6f / sumVD);
//                break;
//            case 6:
//                destinationPlacesVDFreq.add(30f / sumVD);
//                destinationPlacesVDFreq.add(14f / sumVD);
//                destinationPlacesVDFreq.add(11f / sumVD);
//                destinationPlacesVDFreq.add(9f / sumVD);
//                destinationPlacesVDFreq.add(6f / sumVD);
//                destinationPlacesVDFreq.add(3f / sumVD);
//                break;
//            default:
//                destinationPlacesVDFreq.add(30f / sumVD);
//                destinationPlacesVDFreq.add(14f / sumVD);
//                destinationPlacesVDFreq.add(11f / sumVD);
//                destinationPlacesVDFreq.add(9f / sumVD);
//                destinationPlacesVDFreq.add(6f / sumVD);
//                destinationPlacesVDFreq.add(3f / sumVD);
//                for (int i = 0; i < destFreqs.size() - 6; i++) {
//                    destinationPlacesVDFreq.add(1 / sumVD);
//                }
//        }
//
//        double sumOriginal = 0;
//        for (int i = 0; i < destFreqs.size(); i++) {
//            sumOriginal = sumOriginal + destFreqs.get(i);
//        }
//        for (int i = 0; i < destFreqs.size(); i++) {
//            destinationPlacesFreq.add((destinationPlacesVDFreq.get(i)) * sumOriginal);
//        }
//
//        Object[] output = new Object[2];
//        output[0] = destinationPlaces;
//        output[1] = destinationPlacesFreq;
//        return output;
//    }
//
//    public Object[] getClosenessAdjustmentCBGVD(Person agent, ArrayList<PatternsRecordProcessed> dests, ArrayList<Double> destFreqs) {
//        double first = Double.POSITIVE_INFINITY;
//        double second = Double.POSITIVE_INFINITY;
//        double third = Double.POSITIVE_INFINITY;
//        double fourth = Double.POSITIVE_INFINITY;
//        double fith = Double.POSITIVE_INFINITY;
//        double sxith = Double.POSITIVE_INFINITY;
//        PatternsRecordProcessed firstPattern = null;
//        PatternsRecordProcessed secondPattern = null;
//        PatternsRecordProcessed thirdPattern = null;
//        PatternsRecordProcessed fourthPattern = null;
//        PatternsRecordProcessed fithPattern = null;
//        PatternsRecordProcessed sxithPattern = null;
//
//        for (int j = 0; j < dests.size(); j++) {
//            double dist = Math.sqrt(Math.pow(((PatternsRecordProcessed) (dests.get(j))).place.lat - agent.homeCBGVD.lat, 2) + Math.pow(((PatternsRecordProcessed) (dests.get(j))).place.lon - agent.homeCBGVD.lon, 2));
//            if (dist < first) {
//                firstPattern = (PatternsRecordProcessed) (dests.get(j));
//            }
//            if (dist < second) {
//                secondPattern = (PatternsRecordProcessed) (dests.get(j));
//            }
//            if (dist < third) {
//                thirdPattern = (PatternsRecordProcessed) (dests.get(j));
//            }
//            if (dist < fourth) {
//                fourthPattern = (PatternsRecordProcessed) (dests.get(j));
//            }
//            if (dist < fith) {
//                fithPattern = (PatternsRecordProcessed) (dests.get(j));
//            }
//            if (dist < sxith) {
//                sxithPattern = (PatternsRecordProcessed) (dests.get(j));
//            }
//        }
//
//        ArrayList destinationPlaces = new ArrayList();
//
//        if (firstPattern != null) {
//            destinationPlaces.add(firstPattern);
//            if (!secondPattern.placeKey.equals(firstPattern.placeKey)) {
//                destinationPlaces.add(secondPattern);
//                if (!thirdPattern.placeKey.equals(secondPattern.placeKey)) {
//                    destinationPlaces.add(thirdPattern);
//                    if (!fourthPattern.placeKey.equals(thirdPattern.placeKey)) {
//                        destinationPlaces.add(fourthPattern);
//                        if (!fithPattern.placeKey.equals(fourthPattern.placeKey)) {
//                            destinationPlaces.add(fithPattern);
//                            if (!sxithPattern.placeKey.equals(fithPattern.placeKey)) {
//                                destinationPlaces.add(sxithPattern);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        for (int i = 0; i < dests.size(); i++) {
//            if (!dests.get(i).placeKey.equals(firstPattern.placeKey)) {
//                if (!dests.get(i).placeKey.equals(secondPattern.placeKey)) {
//                    if (!dests.get(i).placeKey.equals(thirdPattern.placeKey)) {
//                        if (!dests.get(i).placeKey.equals(fourthPattern.placeKey)) {
//                            if (!dests.get(i).placeKey.equals(fithPattern.placeKey)) {
//                                if (!dests.get(i).placeKey.equals(sxithPattern.placeKey)) {
//                                    destinationPlaces.add(dests.get(i));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        ArrayList destinationPlacesFreq = new ArrayList();
//        ArrayList<Double> destinationPlacesVDFreq = new ArrayList();
//        double sumCBGVD = Math.max(73, 73 + destFreqs.size() - 6);
//
//        switch (destFreqs.size()) {
//            case 0:
//                break;
//            case 1:
//                destinationPlacesVDFreq.add(30d / sumCBGVD);
//                break;
//            case 2:
//                destinationPlacesVDFreq.add(30d / sumCBGVD);
//                destinationPlacesVDFreq.add(14d / sumCBGVD);
//                break;
//            case 3:
//                destinationPlacesVDFreq.add(30d / sumCBGVD);
//                destinationPlacesVDFreq.add(14d / sumCBGVD);
//                destinationPlacesVDFreq.add(11d / sumCBGVD);
//                break;
//            case 4:
//                destinationPlacesVDFreq.add(30d / sumCBGVD);
//                destinationPlacesVDFreq.add(14d / sumCBGVD);
//                destinationPlacesVDFreq.add(11d / sumCBGVD);
//                destinationPlacesVDFreq.add(9d / sumCBGVD);
//                break;
//            case 5:
//                destinationPlacesVDFreq.add(30d / sumCBGVD);
//                destinationPlacesVDFreq.add(14d / sumCBGVD);
//                destinationPlacesVDFreq.add(11d / sumCBGVD);
//                destinationPlacesVDFreq.add(9d / sumCBGVD);
//                destinationPlacesVDFreq.add(6d / sumCBGVD);
//                break;
//            case 6:
//                destinationPlacesVDFreq.add(30d / sumCBGVD);
//                destinationPlacesVDFreq.add(14d / sumCBGVD);
//                destinationPlacesVDFreq.add(11d / sumCBGVD);
//                destinationPlacesVDFreq.add(9d / sumCBGVD);
//                destinationPlacesVDFreq.add(6d / sumCBGVD);
//                destinationPlacesVDFreq.add(3d / sumCBGVD);
//                break;
//            default:
//                destinationPlacesVDFreq.add(30d / sumCBGVD);
//                destinationPlacesVDFreq.add(14d / sumCBGVD);
//                destinationPlacesVDFreq.add(11d / sumCBGVD);
//                destinationPlacesVDFreq.add(9d / sumCBGVD);
//                destinationPlacesVDFreq.add(6d / sumCBGVD);
//                destinationPlacesVDFreq.add(3d / sumCBGVD);
//                for (int i = 0; i < destFreqs.size() - 6; i++) {
//                    destinationPlacesVDFreq.add(1 / sumCBGVD);
//                }
//        }
//
//        double sumOriginal = 0;
//        for (int i = 0; i < destFreqs.size(); i++) {
//            sumOriginal = sumOriginal + destFreqs.get(i);
//        }
//        for (int i = 0; i < destFreqs.size(); i++) {
//            destinationPlacesFreq.add(((destinationPlacesVDFreq.get(i) + (destFreqs.get(i) / sumOriginal)) / 2d) * sumOriginal);
//        }
//
//        Object[] output = new Object[2];
//        output[0] = destinationPlaces;
//        output[1] = destinationPlacesFreq;
//        return output;
//    }
}
