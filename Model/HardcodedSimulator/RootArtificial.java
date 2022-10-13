/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer;
import COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilSimulatorController;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Scope;
import COVID_AgentBasedSimulation.Model.Structure.TessellationCell;
import esmaieeli.gisFastLocationOptimization.GIS3D.LayerDefinition;
import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;
import esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel;
import esmaieeli.gisFastLocationOptimization.Simulation.Routing;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author user
 */
public class RootArtificial extends Root {

    public MainFramePanel mainFParent = new esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel();

    public ArrayList<Person> peopleNoTessellation = new ArrayList();

    public RegionImageLayer cBGRegionsLayer = new RegionImageLayer();

    public boolean isTessellationBuilt = false;

    public boolean isTest = false;

    public RootArtificial(MainModel modelRoot) {
        super(modelRoot);
    }

    @Override
    public void constructor(MainModel modelRoot, int passed_numAgents, String passed_regionType, int passed_numRandomRegions, boolean isCompleteInfection, boolean isInfectCBGOnly, ArrayList<Integer> initialInfectionRegionIndex) {
        myModelRoot = modelRoot;
        myModelRoot.isArtificialExact = true;
        mainFParent.allData = myModelRoot.ABM.allData;
        regionType = passed_regionType;

        switch (regionType) {
            case "no tessellation":
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
            generateAgentsRaw(myModelRoot, passed_numAgents, cBGregions, isTessellationBuilt, isTest);
            selectExactLocations(peopleNoTessellation);
            generatePeopleSchedulesForHome(myModelRoot, peopleNoTessellation);
            generatePeopleSchedulesForWork(myModelRoot, peopleNoTessellation);
        }
    }

    public void generatePostProcessPeople(int passed_numAgents) {
        generateAgentsRaw(myModelRoot, passed_numAgents, cBGregions, false, isTest);
        postGeneratePeopleSchedulesForHome();
    }

    public void generateAgentsRaw(MainModel modelRoot, int passed_numAgents, ArrayList<Region> regions, boolean isTessellationBuilt, boolean isTest) {
        int cumulativePopulation = 0;
        for (int i = 0; i < regions.size(); i++) {
            cumulativePopulation = cumulativePopulation + regions.get(i).population;
        }
        pTSFraction = Math.round((double) ((City) (modelRoot.ABM.studyScopeGeography)).population / (double) passed_numAgents);
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
                modelRoot.ABM.agents.add(person);
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
            modelRoot.ABM.agents.add(person);
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
            pOIs = new HashMap();
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
//                String key = mapElement.getKey();
                POI value = mapElement.getValue();
                float latPOI = value.patternsRecord.place.lat;
                float lonPOI = value.patternsRecord.place.lon;

                double dist = Math.sqrt(Math.pow(latPOI - (float) (people.get(i).exactProperties.exactHomeLocation.lat), 2) + Math.pow(lonPOI - (float) (people.get(i).exactProperties.exactHomeLocation.lon), 2));
                if (maxDist < dist) {
                    maxDist = dist;
                }
                if (minDist > dist) {
                    minDist = dist;
                }
                avg = avg + dist;
                avgCounter = avgCounter + 1;
                people.get(i).exactProperties.pOIHomeProbabilities.put(value, dist);
            }
            avg = avg / (double) avgCounter;
            for (HashMap.Entry<String, POI> mapElement : pOIs.entrySet()) {
                POI value = mapElement.getValue();
                Double pO = people.get(i).exactProperties.pOIHomeProbabilities.get(value);
                if (pO > -1) {
                    Double newP = (pO - minDist + 0.1) / (maxDist + 0.1);
                    people.get(i).exactProperties.pOIHomeProbabilities.put(value, newP);
                    people.get(i).exactProperties.sumHomeFreqs = people.get(i).exactProperties.sumHomeFreqs + newP;
                } else {
                    Double newP = (avg - minDist + 0.1) / (maxDist + 0.1);
                    people.get(i).exactProperties.pOIHomeProbabilities.put(value, newP);
                    System.out.println("ROUTING FAILED! AVERAGE DISTANCE IS USED!");
                }
            }
            System.out.println("total _ i: " + people.size() + " _ " + i);
        }
        System.out.println("DONE SCHEDULES!");
    }

    public void postGeneratePeopleSchedulesForHome() {
        for (int m = 0; m < people.size(); m++) {
            for (int i = 0; i < peopleNoTessellation.size(); i++) {
                int cBGIndex = cBGRegionsLayer.getCellOfAgent(peopleNoTessellation.get(i).exactProperties);
                if (people.get(m).properties.homeRegion.myIndex == cBGIndex) {
                    for (HashMap.Entry<String, POI> mapElement : pOIs.entrySet()) {
                        POI value = mapElement.getValue();
                        Double prob = peopleNoTessellation.get(i).exactProperties.pOIHomeProbabilities.get(value);
                    }
                }
            }
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
//                String key = mapElement.getKey();
                POI value = mapElement.getValue();
                float latPOI = value.patternsRecord.place.lat;
                float lonPOI = value.patternsRecord.place.lon;

                double dist = Math.sqrt(Math.pow(latPOI - (float) (people.get(i).exactProperties.exactWorkLocation.lat), 2) + Math.pow(lonPOI - (float) (people.get(i).exactProperties.exactWorkLocation.lon), 2));
                if (maxDist < dist) {
                    maxDist = dist;
                }
                if (minDist > dist) {
                    minDist = dist;
                }
                avg = avg + dist;
                avgCounter = avgCounter + 1;
                people.get(i).exactProperties.pOIWorkProbabilities.put(value, dist);

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
            for (HashMap.Entry<String, POI> mapElement : pOIs.entrySet()) {
//                String key = mapElement.getKey();
//                System.out.println(key);
                POI value = mapElement.getValue();
                Double pO = people.get(i).exactProperties.pOIWorkProbabilities.get(value);
                if (pO > -1) {
                    Double newP = (pO - minDist + 0.1) / (maxDist + 0.1);
                    people.get(i).exactProperties.pOIWorkProbabilities.put(value, newP);
                    people.get(i).exactProperties.sumWorkFreqs = people.get(i).exactProperties.sumWorkFreqs + newP;
                } else {
                    Double newP = (avg - minDist + 0.1) / (maxDist + 0.1);
                    people.get(i).exactProperties.pOIWorkProbabilities.put(value, newP);
                    System.out.println("ROUTING FAILED! AVERAGE DISTANCE IS USED!");
                }
            }
        }
    }

    public void preprocessNodesInRegions(ArrayList<Region> regions) {
//        MainFramePanel mainFParent = new esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel();
        int cbgLayerIndex = mainFParent.findLayerExactNotCaseSensitive("cbg");
        int baseLayerIndex = mainFParent.findLayerExactNotCaseSensitive("base");
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

}
