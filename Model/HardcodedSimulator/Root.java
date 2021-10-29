/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import static COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog.isReligiousOrganization;
import static COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog.isSchool;
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
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController.updateHour;
import COVID_AgentBasedSimulation.Model.Structure.CBGVDCell;
import COVID_AgentBasedSimulation.Model.Structure.Scope;
import COVID_AgentBasedSimulation.Model.Structure.VDCell;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

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

    public ArrayList<Region> regions;
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
                reportConsoleOurABMInfection(true);
                for (int i = 0; i < people.size(); i++) {
                    people.get(i).isActive = true;
                }
            } else {
                ShamilSimulatorController.shamilInitialInfection(people);
            }
        } else {
            if (modelRoot.ABM.isOurABMActive == true) {
                initiallyInfect();
                reportConsoleOurABMInfection(true);
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
        ArrayList<CensusBlockGroup> cBGsListRaw = modelRoot.safegraph.getCBGsFromCaseStudy(modelRoot.ABM.studyScopeGeography);
        ArrayList regionsList = new ArrayList();
        Scope scope=(Scope)(modelRoot.ABM.studyScopeGeography);
        for (int i = 0; i < cBGsListRaw.size(); i++) {
            Region region = new Region();
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
        return regionsList;
    }
    
    

    ArrayList makeByVDs(MainModel modelRoot) {
        ArrayList<VDCell> vDsListRaw = modelRoot.getSafegraph().getVDsFromCaseStudy(modelRoot.getABM().getStudyScopeGeography());
        ArrayList regionsList = new ArrayList();
        Scope scope=(Scope)(modelRoot.ABM.studyScopeGeography);
        for (int i = 0; i < vDsListRaw.size(); i++) {
            Region region = new Region();
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
            prepareHourlyRegionSnapshotData();
        } else if (myModelRoot.ABM.isOurABMActive == false && myModelRoot.ABM.isShamilABMActive == true) {
            runShamil();
        } else if (myModelRoot.ABM.isOurABMActive == true && myModelRoot.ABM.isShamilABMActive == false) {
            handleHomeWorkActivities(myModelRoot.ABM.currentTime);
            handleInfectionProgress();
            ArrayList<POI> values = new ArrayList<>(pOIs.values());
            for (int i = 0; i < values.size(); i++) {
                values.get(i).updateContamination();
            }
            reportConsoleOurABMInfection(false);
            prepareHourlyRegionSnapshotData();
        }
//        writeMinuteRecord(modelRoot, currentAgent, modelRoot.getABM().getCurrentTime());
//        writeDailyContactRate(modelRoot);
    }

    public void reportConsoleOurABMInfection(boolean forceReport) {
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
    
    public void prepareHourlyRegionSnapshotData(){
        if(myModelRoot.ABM.currentTime.getMinute() == 0){
        for(int i=0;i<regions.size();i++){
            RegionSnapshot snapshot=new RegionSnapshot();
            if(regions.get(i).hourlyRegionSnapshot.size()>0){
                snapshot.rate=regions.get(i).hourlyRegionSnapshot.get(regions.get(i).hourlyRegionSnapshot.size()-1).rate;
            }
            for(int j=0;j<regions.get(i).residents.size();j++){
                switch (regions.get(i).residents.get(j).properties.status) {
                case 0:
                    snapshot.N+=1;
                    snapshot.S+=1;
                    break;
                case 1:
                    snapshot.N+=1;
                    snapshot.IS+=1;
                    snapshot.rate+=1;
                    break;
                case 2:
                    snapshot.N+=1;
                    snapshot.IAS+=1;
                    snapshot.rate+=1;
                    break;
                case 3:
                    snapshot.N+=1;
                    snapshot.R+=1;
                    break;
                case 4:
                    snapshot.D+=1;
                    break;
                default:
                    break;
                }
            }
            regions.get(i).hourlyRegionSnapshot.add(snapshot);
        }
        }
    }
}
