/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.Agent;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentBasedModel;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.DwellTime;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.CBGVDCell;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.VDCell;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author user
 */
public class Person extends Agent {

    Person currentAgent = this;

    public CensusBlockGroup homeCBG;
    public VDCell homeVD;
    public CBGVDCell homeCBGVD;
    public int status;
    public CensusBlockGroup currentLocationCBG;
    public VDCell currentLocationVD;
    public CBGVDCell currentLocationCBGVD;

    public int minutesStayed;
    public ArrayList<SafegraphPlace> destinationPlaces;
    public ArrayList<Integer> destinationPlacesFreq;
    public int travelStartDecisionCounter;
    public int cumulativeDestinationFreqs;
    public CBG cBG;
    public VD vD;
    public CBGVD cBGVD;

    public SafegraphPlace currentSafegraphPlace;

    public double infectionDistances[] = {0.125825731, 0.147845234, 0.209185278, 0.264234036, 0.309845863, 0.350739226, 0.388486946, 0.424661843, 0.462409563, 0.500157282, 0.539477823, 0.581944008, 0.629128657, 0.677886128, 0.736080528, 0.802139037, 0.877634476, 0.896508336, 0.959421202, 0.979867883, 1.04435357, 1.066373073, 1.135577226, 1.157596729, 1.226800881, 1.248820384, 1.318024536, 1.340044039, 1.412393835, 1.432840516, 1.50361749, 1.525636993, 1.607423718, 1.698647373, 1.79144385, 1.884240327, 1.975463982, 2.068260459, 2.161056936, 2.252280591, 2.343504247, 2.436300723};
    public double infectionProbabilities[] = {0.629424019, 0.627712467, 0.605544261, 0.568491183, 0.528141159, 0.485879701, 0.443518805, 0.40159828, 0.35807253, 0.315569579, 0.273876342, 0.231813762, 0.190889203, 0.153456048, 0.117188034, 0.085150685, 0.058392628, 0.05161957, 0.038360245, 0.034925089, 0.024547303, 0.021799178, 0.015218142, 0.01384408, 0.009504936, 0.008805851, 0.006561301, 0.005888982, 0.004153324, 0.003502453, 0.001911433, 0.001615923, 0.001311433, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923};

    public int dstIndex;
    public DwellTime dwellTime;
    public CensusBlockGroup dayCBG;
    public VDCell dayVD;
    public CBGVDCell dayCBGVD;
    public boolean isAtWork;
    public int minutesSick;
    public int minutesTravelToWorkFrom7;
    public int minutesTravelFromWorkFrom16;
    public boolean isDestinedToDeath;
    public boolean isPotentiallyWorkAtHome;

    public boolean isGeometryBuildingUsed = false;

    enum statusEnum {
        SUSCEPTIBLE, EXPOSED, INFECTED_SYM, INFECTED_ASYM, RECOVERED, DEAD;
    }

    public Person() {
        myType = "Person";
    }

    public void constructor(MainModel modelRoot) {

    }

    public void behavior(MainModel modelRoot) {
        if (modelRoot.scenario.equals("CBG")) {
            runCBG(currentAgent, modelRoot, modelRoot.getABM().getCurrentTime());
        } else if (modelRoot.scenario.equals("VD")) {
            runVD(currentAgent, modelRoot, modelRoot.getABM().getCurrentTime());
        } else if (modelRoot.scenario.equals("CBGVD")) {
            runCBGVD(currentAgent, modelRoot, modelRoot.getABM().getCurrentTime());
        } else if (modelRoot.scenario.equals("ABSVD")) {
            runVD(currentAgent, modelRoot, modelRoot.getABM().getCurrentTime());
        }

    }

    //@CompileStatic
    void runCBG(Person currentAgent, MainModel rootModel, ZonedDateTime currentTime) {

//println((((AgentTemplate)(currentAgent.getMyTemplate())).getAgentTypeName()));
//if(currentAgent.getPropertyValue("status")==null){
//	println("!!!: ");
//	println(currentAgent.getPropertyValue("status"));
//}
        if ((int) (currentAgent.status) == statusEnum.DEAD.ordinal()) {
            return;//YOU ARE DEAD ALREADY
        }

//Agent currentAgent=modelRoot.getABM().getCurrentAgent();
//println("S");
//println(currentAgent.getPropertyValue("travelStartDecisionCounter"));
        int travelStartDecisionCounter = (Integer) (currentAgent.travelStartDecisionCounter);
        CensusBlockGroup currentLocation = (CensusBlockGroup) (currentAgent.currentLocationCBG);
        travelStartDecisionCounter = travelStartDecisionCounter + 1;
        int dstIndex = (Integer) (currentAgent.dstIndex);

//int dstIndex=-1;
//println(dstIndex);
        if (dstIndex == -1) {
            //println("@@@");
            if (travelStartDecisionCounter > Math.random() * 0) {
                int cumulativeDestinationFreqs = (Integer) (currentAgent.cumulativeDestinationFreqs);
                int destinationCumulativeThresh = (int) (Math.floor(Math.random() * cumulativeDestinationFreqs));
                //println("cumulativeDestinationFreqs: "+cumulativeDestinationFreqs);
                //println("destinationCumulativeThreshooo: "+destinationCumulativeThresh);
                int destinationCumulative = 0;
                int destination = -1;
                ArrayList destFreqs = (ArrayList) (currentAgent.destinationPlacesFreq);
                for (int i = 0; i < destFreqs.size(); i++) {
                    destinationCumulative = destinationCumulative + (int) destFreqs.get(i);
                    //println("dest loop: "+destinationCumulative);
                    //println("destinationCumulativeThresh: "+(destinationCumulativeThresh-1));
                    if (destinationCumulative >= (destinationCumulativeThresh - 1)) {
                        destination = i;
                        //println("dest break: "+destination);
                        break;
                    }
                }

                int dayInMonth = currentTime.getDayOfMonth() - 1;
                int sumCDF = 0;
                for (int i = 0; i < ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).visits_by_day.length; i++) {
                    sumCDF = sumCDF + ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).visits_by_day[i];
                }
                int numVisitInDay = ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).visits_by_day[dayInMonth];
                if (Math.random() < ((float) numVisitInDay / (float) sumCDF)) {
                    //println("123");
                    byte dayInWeek = (byte) ((currentTime.getDayOfWeek().getValue()) - 1);
                    sumCDF = 0;
                    for (byte i = 0; i < 7; i++) {
                        if ((((HashMap) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_day).get(i)) == null) {
                            System.out.println("PROBLEM in index: " + i);
                        }
                        sumCDF = sumCDF + (Integer) (((HashMap) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_day).get(i));
                        //println("345");
                    }
                    int numVisitsInWeekDay = ((HashMap<Byte, Integer>) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_day).get(dayInWeek);
                    if (Math.random() < ((float) numVisitsInWeekDay / (float) sumCDF)) {
                        int hourInDay = currentTime.getHour();
                        sumCDF = 0;
                        for (int i = 0; i < ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_hour.length; i++) {
                            sumCDF = sumCDF + ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_hour[i];
                        }
                        int numVisitInHour = ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_hour[hourInDay];
                        //\/\/\/ GO TO TRAVEL
                        if (Math.random() < ((float) numVisitInHour / (float) sumCDF)) {
                            //println("traveled!!! "+currentAgent.getMyIndex());
                            //println(currentTime.getMinute());
                            travelStartDecisionCounter = 0;
                            currentLocation = ((CensusBlockGroup) ((SafegraphPlace) (((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).getPlace())).getCensusBlock());
                            currentAgent.dstIndex = destination;
                            currentAgent.lat = currentLocation.getLat();
                            currentAgent.lon = currentLocation.getLon();
                            currentAgent.currentSafegraphPlace = ((SafegraphPlace) (((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).getPlace()));

                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) - 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) - 1;
                                }
                            }

                            for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                                //println("%%%");
                                if (rootModel.getABM().agents.get(n).myType.equals("CBG")) {
                                    if (currentLocation.getId() == ((CensusBlockGroup) (((CBG) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n))).cbgVal)).getId()) {
                                        currentAgent.cBG = ((CBG) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                        if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                            currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
                                        }
                                        break;
                                    }
                                }
                            }

                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) + 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) + 1;
                                }
                            }

                            //CALCULATE THE STAYING TIME
                            sumCDF = 0;
                            for (int i = 0; i < ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).size(); i++) {
                                sumCDF = sumCDF + ((DwellTime) ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).get(i)).getNumber();
                            }
                            int stayTimeThresh = (int) (Math.floor(Math.random() * sumCDF));
                            int stayTimeCumulative = 0;

                            //println(stayTimeThresh);
                            for (int i = 0; i < ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).size(); i++) {
                                stayTimeCumulative = stayTimeCumulative + ((DwellTime) ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).get(i)).getNumber();
                                //println("stay loop: "+stayTimeCumulative);
                                //println("stayTimeThresh: "+(stayTimeThresh-1));
                                if (stayTimeCumulative >= (stayTimeThresh - 1)) {
                                    //destination=i;
                                    //println("DWELL TIME SET! "+currentAgent.getMyIndex());
                                    currentAgent.dwellTime = ((ArrayList<DwellTime>) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).get(i);
                                    break;
                                }
                            }
                        }
                        //^^^ GO TO TRAVEL
                    }
                }

                //println("dest size: "+currentAgent.getPropertyValue("destinationPlaces").size());
                //println("dest: "+destination);
                //currentLocation=((CensusBlockGroup)((SafegraphPlace)(((PatternsRecordProcessed)(((ArrayList)currentAgent.getPropertyValue("destinationPlaces")).get(destination))).getPlace())).getCensusBlock());
                //currentAgent.setPropertyValue("dstIndex",destination);
                //currentAgent.setPropertyValue("lat",currentLocation.getLat());
                //currentAgent.setPropertyValue("lon",currentLocation.getLon());
            }
        } else {
            //println("%%%");
            //UNIFORMLY RETURN BASED ON DWELL DURATION DETERMINED AT THE BEGINING OF THE TRAVEL	
            DwellTime dwellTime = (DwellTime) (currentAgent.dwellTime);
            //if(dwellTime==null){
            //println("DWELL TIME NOT FOUND! "+currentAgent.getMyIndex());
            //}
            //\/\/\/ RETURN FROM TRAVEL
            if (travelStartDecisionCounter > dwellTime.dwellDuration[0]) {
                if (Math.random() < (float) (travelStartDecisionCounter - dwellTime.dwellDuration[0]) / (float) (dwellTime.dwellDuration[1] - dwellTime.dwellDuration[0])) {
                    if ((boolean) currentAgent.isAtWork == false) {
                        currentLocation = (CensusBlockGroup) (currentAgent.homeCBG);
//                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
//                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
//                        }
                    } else {
                        currentLocation = (CensusBlockGroup) (currentAgent.dayCBG);
                    }

                    currentAgent.lat = currentLocation.getLat();
                    currentAgent.lon = currentLocation.getLon();
                    travelStartDecisionCounter = 0;
                    currentAgent.dstIndex = -1;
                    currentAgent.currentSafegraphPlace = null;

                    if (((Agent) (currentAgent.cBG)) != null) {
                        ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) - 1;
                        if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                            ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                            ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                            ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                            ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) - 1;
                        }
                    }

                    boolean isFound = false;
                    for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                        //println("%%%");
                        if (rootModel.getABM().agents.get(n).myType.equals("CBG")) {
                            if (currentLocation.getId() == ((CensusBlockGroup) (((CBG) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n))).cbgVal)).getId()) {
                                currentAgent.cBG = ((CBG) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                isFound = true;
                                break;
                            }
                        }
                    }
                    if (isFound == false) {
                        currentAgent.cBG = null;
                    }
                    if ((boolean) currentAgent.isAtWork == false) {
                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
                        }
                    }

                    if (((Agent) (currentAgent.cBG)) != null) {
                        ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) + 1;
                        if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                            ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                            ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                            ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                            ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) + 1;
                        }
                    }

                    //println("returned");
                    //println("returned!!! "+currentAgent.getMyIndex());
                    //println(currentTime.getMinute());
                }
            }
            //^^^ RETURN FROM TRAVEL
        }

        currentAgent.travelStartDecisionCounter = travelStartDecisionCounter;
//println(currentAgent.getPropertyValue("travelStartDecisionCounter"));
//println("E");

        if (currentTime.getDayOfWeek().getValue() < 6) {
            //\/\/\/ GO TO WORK
            if ((boolean) (currentAgent.isAtWork) == false) {
                if (currentTime.getHour() <= 9 && currentTime.getHour() >= 7) {
                    if (((int) (currentAgent.minutesTravelToWorkFrom7)) == -1) {
                        int minutesTravelToWorkFrom7 = (int) (Math.random() * 120);
                        currentAgent.minutesTravelToWorkFrom7 = minutesTravelToWorkFrom7;
                    } else {
                        int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 7) * 60;
                        if (passed > (int) currentAgent.minutesTravelToWorkFrom7) {
                            currentLocation = (CensusBlockGroup) currentAgent.dayCBG;
                            currentAgent.lat = currentLocation.getLat();
                            currentAgent.lon = currentLocation.getLon();
                            currentAgent.isAtWork = true;
                            currentAgent.minutesTravelFromWorkFrom16 = -1;
//                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
//                                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
//                            }

                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) - 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) - 1;
                                }
                            }

                            for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                                //println("%%%");
                                if (rootModel.getABM().agents.get(n).myType.equals("CBG")) {
                                    if (currentLocation.getId() == ((CensusBlockGroup) (((CBG) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n))).cbgVal)).getId()) {
                                        currentAgent.cBG = ((CBG) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                        break;
                                    }
                                }
                            }

                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
                            }

                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) + 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) + 1;
                                }
                            }

                            //println("travelToWork!!!");
                        }
                    }
                }
            }
            //^^^ GO TO WORK
            if ((boolean) (currentAgent.isAtWork) == true) {
                //\/\/\/ RETURN FROM WORK
                if (currentTime.getHour() <= 18 && currentTime.getHour() >= 16) {
                    if ((int) (currentAgent.minutesTravelFromWorkFrom16) == -1) {
                        int minutesTravelFromWorkFrom16 = (int) (Math.random() * 120);
                        currentAgent.minutesTravelFromWorkFrom16 = minutesTravelFromWorkFrom16;
                    } else {
                        int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 16) * 60;
                    }
                    int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 16) * 60;
                    if (passed > (int) currentAgent.minutesTravelFromWorkFrom16) {
                        currentLocation = (CensusBlockGroup) currentAgent.homeCBG;
                        currentAgent.lat = currentLocation.getLat();
                        currentAgent.lon = currentLocation.getLon();
                        currentAgent.isAtWork = false;
                        currentAgent.minutesTravelToWorkFrom7 = -1;

                        if (((Agent) (currentAgent.cBG)) != null) {
                            ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) - 1;
                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) - 1;
                            }
                        }

                        boolean isFound = false;
                        for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                            //println("%%%");
                            if (rootModel.getABM().agents.get(n).myType.equals("CBG")) {
                                if (currentLocation.getId() == ((CensusBlockGroup) (((CBG) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n))).cbgVal)).getId()) {
                                    currentAgent.cBG = ((CBG) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                    isFound = true;
                                    break;
                                }
                            }
                        }
                        if (isFound == false) {
                            currentAgent.cBG = null;
                        }

                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
                        }

                        if (((Agent) (currentAgent.cBG)) != null) {
                            ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) + 1;
                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) + 1;
                            } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) + 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) + 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) + 1;
                            } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) + 1;
                            }
                        }

                        //println("returnFromWork");
                    }
                }
                //^^^ RETURN FROM WORK
            }
        }

        if (((int) currentAgent.status) == statusEnum.INFECTED_SYM.ordinal() || ((int) currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {//if infected
            if (((int) currentAgent.minutesSick) == -1) {
                currentAgent.minutesSick = 1;
                if (Math.random() < 0.02) {//0.0018
                    currentAgent.isDestinedToDeath = true;
                } else {
                    currentAgent.isDestinedToDeath = false;
                }
            } else {
                int minsSick = (int) (currentAgent.minutesSick);
                currentAgent.minutesSick = minsSick + 1;
                if ((boolean) (currentAgent.isDestinedToDeath) == false) {
                    if (minsSick > 20160) {
                        if (Math.random() < Math.pow((double) (minsSick - 30240) / (double) (30240), 5)) {
                            currentAgent.minutesSick = -1;
                            if (((Agent) (currentAgent.cBG)) != null) {
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) - 1;
                                }
                            }
                            currentAgent.status = statusEnum.RECOVERED.ordinal();//RECOVERED
                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) + 1;
                            }
                        }
                    }
                } else {
                    if (minsSick > 10080) {
                        if (Math.random() < Math.pow((double) (minsSick - 20160) / (double) (20160), 5)) {
                            currentAgent.minutesSick = -1;
                            if (((Agent) (currentAgent.cBG)) != null) {
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) - 1;
                                }
                            }
                            currentAgent.status = statusEnum.DEAD.ordinal();
                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) - 1;
                            }
                        }
                    }
                }
            }
        }
        if ((int) (currentAgent.dstIndex) != -1 || (boolean) (currentAgent.isAtWork) == true) {
            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                if (((Agent) (currentAgent.cBG)) != null) {
                    ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) - 1;
                    ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) + 1;
                }
                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
            }
        }

        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
            if (currentAgent.dstIndex != -1) {
                if (isGeometryBuildingUsed == true) {
                    if (((Agent) (currentAgent.cBG)) != null) {
                        int numIS = (int) (((CBG) (currentAgent.cBG)).IS);
                        int numIAS = (int) (((CBG) (currentAgent.cBG)).IAS);
                        int N = (int) (((CBG) (currentAgent.cBG)).N);

                        if (Math.random() < (((float) (numIS + numIAS) / (float) (N))) * 0.00002) {
                            if (Math.random() > 0.7) {
                                if (((Agent) (currentAgent.cBG)) != null) {
                                    ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                    ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) + 1;
                                }
                                currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                                currentAgent.minutesSick = -1;
                            } else {
                                if (((Agent) (currentAgent.cBG)) != null) {
                                    ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                    ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) + 1;
                                }
                                currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                                currentAgent.minutesSick = -1;
                            }
                        }
                    }
                } else {
                    int hourInDay = currentTime.getHour();
                    int numVisitsInHour = ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(currentAgent.dstIndex))).popularity_by_hour[hourInDay];
                    double distance = Math.sqrt(currentSafegraphPlace.landArea / numVisitsInHour) * 0.98d;
                    double probability = 0.0001;
                    for (int d = 0; d < infectionDistances.length; d++) {
                        if (distance < infectionDistances[d]) {
                            probability = infectionProbabilities[d];
                            break;
                        }
                    }
                    if (Math.random() < (1 / 20) * probability) {
                        if (Math.random() > 0.7) {
                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                            currentAgent.minutesSick = -1;
                        } else {
                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                            currentAgent.minutesSick = -1;
                        }
                    }
                }
            } else {
                if (((Agent) (currentAgent.cBG)) != null) {
                    int numIS = (int) (((CBG) (currentAgent.cBG)).IS);
                    int numIAS = (int) (((CBG) (currentAgent.cBG)).IAS);
                    int N = (int) (((CBG) (currentAgent.cBG)).N);

                    if (Math.random() < (((float) (numIS + numIAS) / (float) (N))) * 0.00002) {
                        if (Math.random() > 0.7) {
                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                            currentAgent.minutesSick = -1;
                        } else {
                            if (((Agent) (currentAgent.cBG)) != null) {
                                ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                                ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                            currentAgent.minutesSick = -1;
                        }
                    }
                }
            }
        }
    }

    void runVD(Person currentAgent, MainModel rootModel, ZonedDateTime currentTime) {

//println((((AgentTemplate)(currentAgent.getMyTemplate())).getAgentTypeName()));
//if(currentAgent.getPropertyValue("status")==null){
//	println("!!!: ");
//	println(currentAgent.getPropertyValue("status"));
//}
        if ((int) (currentAgent.status) == statusEnum.DEAD.ordinal()) {
            return;//YOU ARE DEAD ALREADY
        }

//Agent currentAgent=modelRoot.getABM().getCurrentAgent();
//println("S");
//println(currentAgent.getPropertyValue("travelStartDecisionCounter"));
        int travelStartDecisionCounter = (Integer) (currentAgent.travelStartDecisionCounter);
        VDCell currentLocation = (VDCell) (currentAgent.currentLocationVD);
        travelStartDecisionCounter = travelStartDecisionCounter + 1;
        int dstIndex = (Integer) (currentAgent.dstIndex);

//int dstIndex=-1;
//println(dstIndex);
        if (dstIndex == -1) {
            //println("@@@");
            if (travelStartDecisionCounter > Math.random() * 0) {
                int cumulativeDestinationFreqs = (Integer) (currentAgent.cumulativeDestinationFreqs);
                int destinationCumulativeThresh = (int) (Math.floor(Math.random() * cumulativeDestinationFreqs));
                //println("cumulativeDestinationFreqs: "+cumulativeDestinationFreqs);
                //println("destinationCumulativeThreshooo: "+destinationCumulativeThresh);
                int destinationCumulative = 0;
                int destination = -1;
                ArrayList destFreqs = (ArrayList) (currentAgent.destinationPlacesFreq);
                for (int i = 0; i < destFreqs.size(); i++) {
                    destinationCumulative = destinationCumulative + ((Integer)(destFreqs.get(i)));
                    //println("dest loop: "+destinationCumulative);
                    //println("destinationCumulativeThresh: "+(destinationCumulativeThresh-1));
                    if (destinationCumulative >= (destinationCumulativeThresh - 1)) {
                        destination = i;
                        //println("dest break: "+destination);
                        break;
                    }
                }

                int dayInMonth = currentTime.getDayOfMonth() - 1;
                int sumCDF = 0;
                for (int i = 0; i < ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).visits_by_day.length; i++) {
                    sumCDF = sumCDF + ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).visits_by_day[i];
                }
                int numVisitInDay = ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).visits_by_day[dayInMonth];
                if (Math.random() < ((float) numVisitInDay / (float) sumCDF)) {
                    //println("123");
                    byte dayInWeek = (byte) ((currentTime.getDayOfWeek().getValue()) - 1);
                    sumCDF = 0;
                    for (byte i = 0; i < 7; i++) {
                        if ((((HashMap) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_day).get(i)) == null) {
                            System.out.println("PROBLEM in index: " + i);
                        }
                        sumCDF = sumCDF + (Integer) (((HashMap) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_day).get(i));
                        //println("345");
                    }
                    int numVisitsInWeekDay = ((HashMap<Byte, Integer>) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_day).get(dayInWeek);
                    if (Math.random() < ((float) numVisitsInWeekDay / (float) sumCDF)) {
                        int hourInDay = currentTime.getHour();
                        sumCDF = 0;
                        for (int i = 0; i < ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_hour.length; i++) {
                            sumCDF = sumCDF + ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_hour[i];
                        }
                        int numVisitInHour = ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_hour[hourInDay];
                        //\/\/\/ GO TO TRAVEL
                        if (Math.random() < ((float) numVisitInHour / (float) sumCDF)) {
                            //println("traveled!!! "+currentAgent.getMyIndex());
                            //println(currentTime.getMinute());
                            travelStartDecisionCounter = 0;
                            Object[] vd = ((City) (rootModel.ABM.studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((SafegraphPlace) (((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).getPlace())).getCensusBlock()));
                            currentLocation = (VDCell) (vd[0]);
                            currentAgent.dstIndex = destination;
                            currentAgent.lat = currentLocation.getLat();
                            currentAgent.lon = currentLocation.getLon();
                            currentAgent.currentSafegraphPlace = ((SafegraphPlace) (((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).getPlace()));

                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).N = (int) (((VD) (currentAgent.vD)).N) - 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) - 1;
                                }
                            }

                            for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                                //println("%%%");
                                if (rootModel.getABM().agents.get(n).myType.equals("VD")) {
                                    if (currentLocation.shopPlacesKeys.get(0).equals(((VD) (((AgentBasedModel) (rootModel.getABM())).getAgents().get(n))).vdVal.shopPlacesKeys.get(0))) {
                                        currentAgent.vD = ((VD) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                        if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                            currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
                                        }
                                        break;
                                    }
                                }
                            }

                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).N = (int) (((VD) (currentAgent.vD)).N) + 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) + 1;
                                }
                            }

                            //CALCULATE THE STAYING TIME
                            sumCDF = 0;
                            for (int i = 0; i < ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).size(); i++) {
                                sumCDF = sumCDF + ((DwellTime) ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).get(i)).getNumber();
                            }
                            int stayTimeThresh = (int) (Math.floor(Math.random() * sumCDF));
                            int stayTimeCumulative = 0;

                            //println(stayTimeThresh);
                            for (int i = 0; i < ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).size(); i++) {
                                stayTimeCumulative = stayTimeCumulative + ((DwellTime) ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).get(i)).getNumber();
                                //println("stay loop: "+stayTimeCumulative);
                                //println("stayTimeThresh: "+(stayTimeThresh-1));
                                if (stayTimeCumulative >= (stayTimeThresh - 1)) {
                                    //destination=i;
                                    //println("DWELL TIME SET! "+currentAgent.getMyIndex());
                                    currentAgent.dwellTime = ((ArrayList<DwellTime>) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).get(i);
                                    break;
                                }
                            }
                        }
                        //^^^ GO TO TRAVEL
                    }
                }

                //println("dest size: "+currentAgent.getPropertyValue("destinationPlaces").size());
                //println("dest: "+destination);
                //currentLocation=((CensusBlockGroup)((SafegraphPlace)(((PatternsRecordProcessed)(((ArrayList)currentAgent.getPropertyValue("destinationPlaces")).get(destination))).getPlace())).getCensusBlock());
                //currentAgent.setPropertyValue("dstIndex",destination);
                //currentAgent.setPropertyValue("lat",currentLocation.getLat());
                //currentAgent.setPropertyValue("lon",currentLocation.getLon());
            }
        } else {
            //println("%%%");
            //UNIFORMLY RETURN BASED ON DWELL DURATION DETERMINED AT THE BEGINING OF THE TRAVEL	
            DwellTime dwellTime = (DwellTime) (currentAgent.dwellTime);
            //if(dwellTime==null){
            //println("DWELL TIME NOT FOUND! "+currentAgent.getMyIndex());
            //}
            //\/\/\/ RETURN FROM TRAVEL
            if (travelStartDecisionCounter > dwellTime.dwellDuration[0]) {
                if (Math.random() < (float) (travelStartDecisionCounter - dwellTime.dwellDuration[0]) / (float) (dwellTime.dwellDuration[1] - dwellTime.dwellDuration[0])) {
                    if ((boolean) currentAgent.isAtWork == false) {
                        currentLocation = (VDCell) (currentAgent.homeVD);
//                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
//                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
//                        }
                    } else {
                        currentLocation = (VDCell) (currentAgent.dayVD);
                    }

                    currentAgent.lat = currentLocation.getLat();
                    currentAgent.lon = currentLocation.getLon();
                    travelStartDecisionCounter = 0;
                    currentAgent.dstIndex = -1;
                    currentAgent.currentSafegraphPlace = null;

                    if (((Agent) (currentAgent.vD)) != null) {
                        ((VD) (currentAgent.vD)).N = (int) (((VD) (currentAgent.vD)).N) - 1;
                        if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                            ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                            ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                            ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                            ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) - 1;
                        }
                    }

                    boolean isFound = false;
                    for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                        //println("%%%");
                        if (rootModel.getABM().agents.get(n).myType.equals("VD")) {
                            if (currentLocation.shopPlacesKeys.get(0).equals((((VD) (rootModel.getABM().getAgents().get(n))).vdVal).shopPlacesKeys.get(0))) {
                                currentAgent.vD = ((VD) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                isFound = true;
                                break;
                            }
                        }
                    }
                    if (isFound == false) {
                        currentAgent.vD = null;
                    }
                    if ((boolean) currentAgent.isAtWork == false) {
                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
                        }
                    }

                    if (((Agent) (currentAgent.vD)) != null) {
                        ((VD) (currentAgent.vD)).N = (int) (((VD) (currentAgent.vD)).N) + 1;
                        if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                            ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                            ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                            ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                            ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) + 1;
                        }
                    }

                    //println("returned");
                    //println("returned!!! "+currentAgent.getMyIndex());
                    //println(currentTime.getMinute());
                }
            }
            //^^^ RETURN FROM TRAVEL
        }

        currentAgent.travelStartDecisionCounter = travelStartDecisionCounter;
//println(currentAgent.getPropertyValue("travelStartDecisionCounter"));
//println("E");

        if (currentTime.getDayOfWeek().getValue() < 6) {
            //\/\/\/ GO TO WORK
            if ((boolean) (currentAgent.isAtWork) == false) {
                if (currentTime.getHour() <= 9 && currentTime.getHour() >= 7) {
                    if (((int) (currentAgent.minutesTravelToWorkFrom7)) == -1) {
                        int minutesTravelToWorkFrom7 = (int) (Math.random() * 120);
                        currentAgent.minutesTravelToWorkFrom7 = minutesTravelToWorkFrom7;
                    } else {
                        int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 7) * 60;
                        if (passed > (int) currentAgent.minutesTravelToWorkFrom7) {
                            currentLocation = (VDCell) currentAgent.dayVD;
                            currentAgent.lat = currentLocation.getLat();
                            currentAgent.lon = currentLocation.getLon();
                            currentAgent.isAtWork = true;
                            currentAgent.minutesTravelFromWorkFrom16 = -1;
//                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
//                                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
//                            }

                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).N = (int) (((VD) (currentAgent.vD)).N) - 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) - 1;
                                }
                            }

                            for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                                //println("%%%");
                                if (rootModel.getABM().agents.get(n).myType.equals("VD")) {
                                    if (currentLocation.shopPlacesKeys.get(0).equals((((VD) (rootModel.getABM().getAgents().get(n))).vdVal).shopPlacesKeys.get(0))) {
                                        currentAgent.vD = ((VD) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                        break;
                                    }
                                }
                            }

                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
                            }

                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).N = (int) (((VD) (currentAgent.vD)).N) + 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) + 1;
                                }
                            }

                            //println("travelToWork!!!");
                        }
                    }
                }
            }
            //^^^ GO TO WORK
            if ((boolean) (currentAgent.isAtWork) == true) {
                //\/\/\/ RETURN FROM WORK
                if (currentTime.getHour() <= 18 && currentTime.getHour() >= 16) {
                    if ((int) (currentAgent.minutesTravelFromWorkFrom16) == -1) {
                        int minutesTravelFromWorkFrom16 = (int) (Math.random() * 120);
                        currentAgent.minutesTravelFromWorkFrom16 = minutesTravelFromWorkFrom16;
                    } else {
                        int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 16) * 60;
                    }
                    int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 16) * 60;
                    if (passed > (int) currentAgent.minutesTravelFromWorkFrom16) {
                        currentLocation = (VDCell) currentAgent.homeVD;
                        currentAgent.lat = currentLocation.getLat();
                        currentAgent.lon = currentLocation.getLon();
                        currentAgent.isAtWork = false;
                        currentAgent.minutesTravelToWorkFrom7 = -1;

                        if (((Agent) (currentAgent.vD)) != null) {
                            ((VD) (currentAgent.vD)).N = (int) (((VD) (currentAgent.vD)).N) - 1;
                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) - 1;
                            }
                        }

                        boolean isFound = false;
                        for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                            //println("%%%");
                            if (rootModel.getABM().agents.get(n).myType.equals("VD")) {
                                if (currentLocation.shopPlacesKeys.get(0).equals((((VD) (rootModel.getABM().getAgents().get(n))).vdVal).shopPlacesKeys.get(0))) {
                                    currentAgent.vD = ((VD) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                    isFound = true;
                                    break;
                                }
                            }
                        }
                        if (isFound == false) {
                            currentAgent.vD = null;
                        }

                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
                        }

                        if (((Agent) (currentAgent.vD)) != null) {
                            ((VD) (currentAgent.vD)).N = (int) (((VD) (currentAgent.vD)).N) - 1;
                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) - 1;
                            }
                        }

                        //println("returnFromWork");
                    }
                }
                //^^^ RETURN FROM WORK
            }
        }

        if (((int) currentAgent.status) == statusEnum.INFECTED_SYM.ordinal() || ((int) currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {//if infected
            if (((int) currentAgent.minutesSick) == -1) {
                currentAgent.minutesSick = 1;
                if (Math.random() < 0.02) {//0.0018
                    currentAgent.isDestinedToDeath = true;
                } else {
                    currentAgent.isDestinedToDeath = false;
                }
            } else {
                int minsSick = (int) (currentAgent.minutesSick);
                currentAgent.minutesSick = minsSick + 1;
                if ((boolean) (currentAgent.isDestinedToDeath) == false) {
                    if (minsSick > 20160) {
                        if (Math.random() < Math.pow((double) (minsSick - 30240) / (double) (30240), 5)) {
                            currentAgent.minutesSick = -1;
                            if (((Agent) (currentAgent.vD)) != null) {
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) - 1;
                                }
                            }
                            currentAgent.status = statusEnum.RECOVERED.ordinal();//RECOVERED
                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) + 1;
                            }
                        }
                    }
                } else {
                    if (minsSick > 10080) {
                        if (Math.random() < Math.pow((double) (minsSick - 20160) / (double) (20160), 5)) {
                            currentAgent.minutesSick = -1;
                            if (((Agent) (currentAgent.vD)) != null) {
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) - 1;
                                }
                            }
                            currentAgent.status = statusEnum.RECOVERED.ordinal();//RECOVERED
                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).R = (int) (((VD) (currentAgent.vD)).R) + 1;
                            }
                        }
                    }
                }
            }
        }
        if ((int) (currentAgent.dstIndex) != -1 || (boolean) (currentAgent.isAtWork) == true) {
            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                if (((Agent) (currentAgent.vD)) != null) {
                    ((VD) (currentAgent.vD)).S = (int) (((VD) (currentAgent.vD)).S) - 1;
                    ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) + 1;
                }
                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
            }
        }

        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
            if (currentAgent.dstIndex != -1) {
                if (isGeometryBuildingUsed == true) {
                    if (((Agent) (currentAgent.vD)) != null) {
                        int numIS = (int) (((VD) (currentAgent.vD)).IS);
                        int numIAS = (int) (((VD) (currentAgent.vD)).IAS);
                        int N = (int) (((VD) (currentAgent.vD)).N);

                        if (Math.random() < (((float) (numIS + numIAS) / (float) (N))) * 0.00002) {
                            if (Math.random() > 0.7) {
                                if (((Agent) (currentAgent.vD)) != null) {
                                    ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                    ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) + 1;
                                }
                                currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                                currentAgent.minutesSick = -1;
                            } else {
                                if (((Agent) (currentAgent.vD)) != null) {
                                    ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                    ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) + 1;
                                }
                                currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                                currentAgent.minutesSick = -1;
                            }
                        }
                    }
                } else {
                    int hourInDay = currentTime.getHour();
                    int numVisitsInHour = ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(currentAgent.dstIndex))).popularity_by_hour[hourInDay];
                    double distance = Math.sqrt(currentSafegraphPlace.landArea / numVisitsInHour) * 0.98d;
                    double probability = 0.0001;
                    for (int d = 0; d < infectionDistances.length; d++) {
                        if (distance < infectionDistances[d]) {
                            probability = infectionProbabilities[d];
                            break;
                        }
                    }
                    if (Math.random() < (1 / 20) * probability) {
                        if (Math.random() > 0.7) {
                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                            currentAgent.minutesSick = -1;
                        } else {
                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                            currentAgent.minutesSick = -1;
                        }
                    }
                }
            } else {
                if (((Agent) (currentAgent.vD)) != null) {
                    int numIS = (int) (((VD) (currentAgent.vD)).IS);
                    int numIAS = (int) (((VD) (currentAgent.vD)).IAS);
                    int N = (int) (((VD) (currentAgent.vD)).N);

                    if (Math.random() < (((float) (numIS + numIAS) / (float) (N))) * 0.00002) {
                        if (Math.random() > 0.7) {
                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                ((VD) (currentAgent.vD)).IAS = (int) (((VD) (currentAgent.vD)).IAS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                            currentAgent.minutesSick = -1;
                        } else {
                            if (((Agent) (currentAgent.vD)) != null) {
                                ((VD) (currentAgent.vD)).E = (int) (((VD) (currentAgent.vD)).E) - 1;
                                ((VD) (currentAgent.vD)).IS = (int) (((VD) (currentAgent.vD)).IS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                            currentAgent.minutesSick = -1;
                        }
                    }
                }
            }
        }
    }

    void runCBGVD(Person currentAgent, MainModel rootModel, ZonedDateTime currentTime) {

//println((((AgentTemplate)(currentAgent.getMyTemplate())).getAgentTypeName()));
//if(currentAgent.getPropertyValue("status")==null){
//	println("!!!: ");
//	println(currentAgent.getPropertyValue("status"));
//}
        if ((int) (currentAgent.status) == statusEnum.DEAD.ordinal()) {
            return;//YOU ARE DEAD ALREADY
        }

//Agent currentAgent=modelRoot.getABM().getCurrentAgent();
//println("S");
//println(currentAgent.getPropertyValue("travelStartDecisionCounter"));
        int travelStartDecisionCounter = (Integer) (currentAgent.travelStartDecisionCounter);
        CBGVDCell currentLocation = (CBGVDCell) (currentAgent.currentLocationCBGVD);
        travelStartDecisionCounter = travelStartDecisionCounter + 1;
        int dstIndex = (Integer) (currentAgent.dstIndex);

//int dstIndex=-1;
//println(dstIndex);
        if (dstIndex == -1) {
            //println("@@@");
            if (travelStartDecisionCounter > Math.random() * 0) {
                int cumulativeDestinationFreqs = (Integer) (currentAgent.cumulativeDestinationFreqs);
                int destinationCumulativeThresh = (int) (Math.floor(Math.random() * cumulativeDestinationFreqs));
                //println("cumulativeDestinationFreqs: "+cumulativeDestinationFreqs);
                //println("destinationCumulativeThreshooo: "+destinationCumulativeThresh);
                int destinationCumulative = 0;
                int destination = -1;
                ArrayList destFreqs = (ArrayList) (currentAgent.destinationPlacesFreq);
                for (int i = 0; i < destFreqs.size(); i++) {
                    destinationCumulative = destinationCumulative + (int) destFreqs.get(i);
                    //println("dest loop: "+destinationCumulative);
                    //println("destinationCumulativeThresh: "+(destinationCumulativeThresh-1));
                    if (destinationCumulative >= (destinationCumulativeThresh - 1)) {
                        destination = i;
                        //println("dest break: "+destination);
                        break;
                    }
                }

                int dayInMonth = currentTime.getDayOfMonth() - 1;
                int sumCDF = 0;
                for (int i = 0; i < ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).visits_by_day.length; i++) {
                    sumCDF = sumCDF + ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).visits_by_day[i];
                }
                int numVisitInDay = ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).visits_by_day[dayInMonth];
                if (Math.random() < ((float) numVisitInDay / (float) sumCDF)) {
                    //println("123");
                    byte dayInWeek = (byte) ((currentTime.getDayOfWeek().getValue()) - 1);
                    sumCDF = 0;
                    for (byte i = 0; i < 7; i++) {
                        if ((((HashMap) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_day).get(i)) == null) {
                            System.out.println("PROBLEM in index: " + i);
                        }
                        sumCDF = sumCDF + (Integer) (((HashMap) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_day).get(i));
                        //println("345");
                    }
                    int numVisitsInWeekDay = ((HashMap<Byte, Integer>) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_day).get(dayInWeek);
                    if (Math.random() < ((float) numVisitsInWeekDay / (float) sumCDF)) {
                        int hourInDay = currentTime.getHour();
                        sumCDF = 0;
                        for (int i = 0; i < ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_hour.length; i++) {
                            sumCDF = sumCDF + ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_hour[i];
                        }
                        int numVisitInHour = ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).popularity_by_hour[hourInDay];
                        //\/\/\/ GO TO TRAVEL
                        if (Math.random() < ((float) numVisitInHour / (float) sumCDF)) {
                            //println("traveled!!! "+currentAgent.getMyIndex());
                            //println(currentTime.getMinute());
                            travelStartDecisionCounter = 0;
                            Object[] cbgvd = ((City) (rootModel.ABM.studyScopeGeography)).getVDFromCBG(((CensusBlockGroup) ((SafegraphPlace) (((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).getPlace())).getCensusBlock()));
                            currentLocation = (CBGVDCell) (cbgvd[0]);
                            currentAgent.dstIndex = destination;
                            currentAgent.lat = currentLocation.getLat();
                            currentAgent.lon = currentLocation.getLon();
                            currentAgent.currentSafegraphPlace = ((SafegraphPlace) (((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).getPlace()));

                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).N = (int) (((CBGVD) (currentAgent.cBGVD)).N) - 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) - 1;
                                }
                            }

                            for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                                //println("%%%");
                                if (rootModel.getABM().agents.get(n).myType.equals("CBGVD")) {
                                    if (currentLocation.shopPlacesKeys.get(0).equals((((CBGVD) (rootModel.getABM().getAgents().get(n))).cbgvdVal).shopPlacesKeys.get(0))) {
                                        currentAgent.cBGVD = ((CBGVD) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                        if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                            currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
                                        }
                                        break;
                                    }
                                }
                            }

                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).N = (int) (((CBGVD) (currentAgent.cBGVD)).N) + 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) + 1;
                                }
                            }

                            //CALCULATE THE STAYING TIME
                            sumCDF = 0;
                            for (int i = 0; i < ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).size(); i++) {
                                sumCDF = sumCDF + ((DwellTime) ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).get(i)).getNumber();
                            }
                            int stayTimeThresh = (int) (Math.floor(Math.random() * sumCDF));
                            int stayTimeCumulative = 0;

                            //println(stayTimeThresh);
                            for (int i = 0; i < ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).size(); i++) {
                                stayTimeCumulative = stayTimeCumulative + ((DwellTime) ((ArrayList) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).get(i)).getNumber();
                                //println("stay loop: "+stayTimeCumulative);
                                //println("stayTimeThresh: "+(stayTimeThresh-1));
                                if (stayTimeCumulative >= (stayTimeThresh - 1)) {
                                    //destination=i;
                                    //println("DWELL TIME SET! "+currentAgent.getMyIndex());
                                    currentAgent.dwellTime = ((ArrayList<DwellTime>) ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(destination))).bucketed_dwell_times).get(i);
                                    break;
                                }
                            }
                        }
                        //^^^ GO TO TRAVEL
                    }
                }

                //println("dest size: "+currentAgent.getPropertyValue("destinationPlaces").size());
                //println("dest: "+destination);
                //currentLocation=((CensusBlockGroup)((SafegraphPlace)(((PatternsRecordProcessed)(((ArrayList)currentAgent.getPropertyValue("destinationPlaces")).get(destination))).getPlace())).getCensusBlock());
                //currentAgent.setPropertyValue("dstIndex",destination);
                //currentAgent.setPropertyValue("lat",currentLocation.getLat());
                //currentAgent.setPropertyValue("lon",currentLocation.getLon());
            }
        } else {
            //println("%%%");
            //UNIFORMLY RETURN BASED ON DWELL DURATION DETERMINED AT THE BEGINING OF THE TRAVEL	
            DwellTime dwellTime = (DwellTime) (currentAgent.dwellTime);
            //if(dwellTime==null){
            //println("DWELL TIME NOT FOUND! "+currentAgent.getMyIndex());
            //}
            //\/\/\/ RETURN FROM TRAVEL
            if (travelStartDecisionCounter > dwellTime.dwellDuration[0]) {
                if (Math.random() < (float) (travelStartDecisionCounter - dwellTime.dwellDuration[0]) / (float) (dwellTime.dwellDuration[1] - dwellTime.dwellDuration[0])) {
                    if ((boolean) currentAgent.isAtWork == false) {
                        currentLocation = (CBGVDCell) (currentAgent.homeCBGVD);
//                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
//                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
//                        }
                    } else {
                        currentLocation = (CBGVDCell) (currentAgent.dayCBGVD);
                    }

                    currentAgent.lat = currentLocation.getLat();
                    currentAgent.lon = currentLocation.getLon();
                    travelStartDecisionCounter = 0;
                    currentAgent.dstIndex = -1;
                    currentAgent.currentSafegraphPlace = null;

                    if (((Agent) (currentAgent.cBG)) != null) {
                        ((CBG) (currentAgent.cBG)).N = (int) (((CBG) (currentAgent.cBG)).N) - 1;
                        if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                            ((CBG) (currentAgent.cBG)).S = (int) (((CBG) (currentAgent.cBG)).S) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            ((CBG) (currentAgent.cBG)).E = (int) (((CBG) (currentAgent.cBG)).E) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                            ((CBG) (currentAgent.cBG)).IS = (int) (((CBG) (currentAgent.cBG)).IS) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                            ((CBG) (currentAgent.cBG)).IAS = (int) (((CBG) (currentAgent.cBG)).IAS) - 1;
                        } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                            ((CBG) (currentAgent.cBG)).R = (int) (((CBG) (currentAgent.cBG)).R) - 1;
                        }
                    }

                    boolean isFound = false;
                    for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                        //println("%%%");
                        if (rootModel.getABM().agents.get(n).myType.equals("CBGVD")) {
                            if (currentLocation.shopPlacesKeys.get(0).equals((((CBGVD) (rootModel.getABM().getAgents().get(n))).cbgvdVal).shopPlacesKeys.get(0))) {
                                currentAgent.cBGVD = ((CBGVD) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                isFound = true;
                                break;
                            }
                        }
                    }
                    if (isFound == false) {
                        currentAgent.cBGVD = null;
                    }
                    if ((boolean) currentAgent.isAtWork == false) {
                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
                        }
                    }

                    if (((Agent) (currentAgent.cBGVD)) != null) {
                        ((CBGVD) (currentAgent.cBGVD)).N = (int) (((CBGVD) (currentAgent.cBGVD)).N) + 1;
                        if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                            ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                            ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                            ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) + 1;
                        } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                            ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) + 1;
                        }
                    }

                    //println("returned");
                    //println("returned!!! "+currentAgent.getMyIndex());
                    //println(currentTime.getMinute());
                }
            }
            //^^^ RETURN FROM TRAVEL
        }

        currentAgent.travelStartDecisionCounter = travelStartDecisionCounter;
//println(currentAgent.getPropertyValue("travelStartDecisionCounter"));
//println("E");

        if (currentTime.getDayOfWeek().getValue() < 6) {
            //\/\/\/ GO TO WORK
            if ((boolean) (currentAgent.isAtWork) == false) {
                if (currentTime.getHour() <= 9 && currentTime.getHour() >= 7) {
                    if (((int) (currentAgent.minutesTravelToWorkFrom7)) == -1) {
                        int minutesTravelToWorkFrom7 = (int) (Math.random() * 120);
                        currentAgent.minutesTravelToWorkFrom7 = minutesTravelToWorkFrom7;
                    } else {
                        int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 7) * 60;
                        if (passed > (int) currentAgent.minutesTravelToWorkFrom7) {
                            currentLocation = (CBGVDCell) currentAgent.dayCBGVD;
                            currentAgent.lat = currentLocation.getLat();
                            currentAgent.lon = currentLocation.getLon();
                            currentAgent.isAtWork = true;
                            currentAgent.minutesTravelFromWorkFrom16 = -1;
//                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
//                                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
//                            }

                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).N = (int) (((CBGVD) (currentAgent.cBGVD)).N) - 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) - 1;
                                }
                            }

                            for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                                //println("%%%");
                                if (rootModel.getABM().agents.get(n).myType.equals("CBGVD")) {
                                    if (currentLocation.shopPlacesKeys.get(0).equals((((CBGVD) (rootModel.getABM().getAgents().get(n))).cbgvdVal).shopPlacesKeys.get(0))) {
                                        currentAgent.cBGVD = ((CBGVD) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                        break;
                                    }
                                }
                            }

                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
                            }

                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).N = (int) (((CBGVD) (currentAgent.cBGVD)).N) + 1;
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) + 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) + 1;
                                }
                            }

                            //println("travelToWork!!!");
                        }
                    }
                }
            }
            //^^^ GO TO WORK
            if ((boolean) (currentAgent.isAtWork) == true) {
                //\/\/\/ RETURN FROM WORK
                if (currentTime.getHour() <= 18 && currentTime.getHour() >= 16) {
                    if ((int) (currentAgent.minutesTravelFromWorkFrom16) == -1) {
                        int minutesTravelFromWorkFrom16 = (int) (Math.random() * 120);
                        currentAgent.minutesTravelFromWorkFrom16 = minutesTravelFromWorkFrom16;
                    } else {
                        int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 16) * 60;
                    }
                    int passed = currentTime.getMinute() + ((int) (currentTime.getHour()) - 16) * 60;
                    if (passed > (int) currentAgent.minutesTravelFromWorkFrom16) {
                        currentLocation = (CBGVDCell) currentAgent.homeCBGVD;
                        currentAgent.lat = currentLocation.getLat();
                        currentAgent.lon = currentLocation.getLon();
                        currentAgent.isAtWork = false;
                        currentAgent.minutesTravelToWorkFrom7 = -1;

                        if (((Agent) (currentAgent.cBGVD)) != null) {
                            ((CBGVD) (currentAgent.cBGVD)).N = (int) (((CBGVD) (currentAgent.cBGVD)).N) - 1;
                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) - 1;
                            } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) - 1;
                            }
                        }

                        boolean isFound = false;
                        for (int n = 0; n < ((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).size(); n++) {
                            //println("%%%");
                            if (rootModel.getABM().agents.get(n).myType.equals("CBGVD")) {
                                if (currentLocation.shopPlacesKeys.get(0).equals((((CBGVD) (rootModel.getABM().getAgents().get(n))).cbgvdVal).shopPlacesKeys.get(0))) {
                                    currentAgent.cBGVD = ((CBGVD) (((CopyOnWriteArrayList<Agent>) (((AgentBasedModel) (rootModel.getABM())).getAgents())).get(n)));
                                    isFound = true;
                                    break;
                                }
                            }
                        }
                        if (isFound == false) {
                            currentAgent.cBGVD = null;
                        }

                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
                        }

                        if (((Agent) (currentAgent.cBGVD)) != null) {
                            ((CBGVD) (currentAgent.cBGVD)).N = (int) (((CBGVD) (currentAgent.cBGVD)).N) + 1;
                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) + 1;
                            } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) + 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) + 1;
                            } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) + 1;
                            } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) + 1;
                            }
                        }

                        //println("returnFromWork");
                    }
                }
                //^^^ RETURN FROM WORK
            }
        }

        if (((int) currentAgent.status) == statusEnum.INFECTED_SYM.ordinal() || ((int) currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {//if infected
            if (((int) currentAgent.minutesSick) == -1) {
                currentAgent.minutesSick = 1;
                if (Math.random() < 0.02) {//0.0018
                    currentAgent.isDestinedToDeath = true;
                } else {
                    currentAgent.isDestinedToDeath = false;
                }
            } else {
                int minsSick = (int) (currentAgent.minutesSick);
                currentAgent.minutesSick = minsSick + 1;
                if ((boolean) (currentAgent.isDestinedToDeath) == false) {
                    if (minsSick > 20160) {
                        if (Math.random() < Math.pow((double) (minsSick - 30240) / (double) (30240), 5)) {
                            currentAgent.minutesSick = -1;
                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) - 1;
                                }
                            }
                            currentAgent.status = statusEnum.RECOVERED.ordinal();//RECOVERED
                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) + 1;
                            }
                        }
                    }
                } else {
                    if (minsSick > 10080) {
                        if (Math.random() < Math.pow((double) (minsSick - 20160) / (double) (20160), 5)) {
                            currentAgent.minutesSick = -1;
                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_SYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.INFECTED_ASYM.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) - 1;
                                } else if ((int) (currentAgent.status) == statusEnum.RECOVERED.ordinal()) {
                                    ((CBGVD) (currentAgent.cBGVD)).R = (int) (((CBGVD) (currentAgent.cBGVD)).R) - 1;
                                }
                            }
                            currentAgent.status = statusEnum.DEAD.ordinal();
                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).N = (int) (((CBGVD) (currentAgent.cBGVD)).N) - 1;
                            }
                        }
                    }
                }
            }
        }
        if ((int) (currentAgent.dstIndex) != -1 || (boolean) (currentAgent.isAtWork) == true) {
            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                if (((Agent) (currentAgent.cBGVD)) != null) {
                    ((CBGVD) (currentAgent.cBGVD)).S = (int) (((CBGVD) (currentAgent.cBGVD)).S) - 1;
                    ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) + 1;
                }
                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
            }
        }

        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
            if (currentAgent.dstIndex != -1) {
                if (isGeometryBuildingUsed == true) {
                    if (((Agent) (currentAgent.cBGVD)) != null) {
                        int numIS = (int) (((CBGVD) (currentAgent.cBGVD)).IS);
                        int numIAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS);
                        int N = (int) (((CBGVD) (currentAgent.cBGVD)).N);

                        if (Math.random() < (((float) (numIS + numIAS) / (float) (N))) * 0.00002) {
                            if (Math.random() > 0.7) {
                                if (((Agent) (currentAgent.cBGVD)) != null) {
                                    ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                    ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) + 1;
                                }
                                currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                                currentAgent.minutesSick = -1;
                            } else {
                                if (((Agent) (currentAgent.cBGVD)) != null) {
                                    ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                    ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) + 1;
                                }
                                currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                                currentAgent.minutesSick = -1;
                            }
                        }
                    }
                } else {
                    int hourInDay = currentTime.getHour();
                    int numVisitsInHour = ((PatternsRecordProcessed) (((ArrayList) currentAgent.destinationPlaces).get(currentAgent.dstIndex))).popularity_by_hour[hourInDay];
                    double distance = Math.sqrt(currentSafegraphPlace.landArea / numVisitsInHour) * 0.98d;
                    double probability = 0.0001;
                    for (int d = 0; d < infectionDistances.length; d++) {
                        if (distance < infectionDistances[d]) {
                            probability = infectionProbabilities[d];
                            break;
                        }
                    }
                    if (Math.random() < (1 / 20) * probability) {
                        if (Math.random() > 0.7) {
                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                            currentAgent.minutesSick = -1;
                        } else {
                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                            currentAgent.minutesSick = -1;
                        }
                    }
                }
            } else {
                if (((Agent) (currentAgent.cBGVD)) != null) {
                    int numIS = (int) (((CBGVD) (currentAgent.cBGVD)).IS);
                    int numIAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS);
                    int N = (int) (((CBGVD) (currentAgent.cBGVD)).N);

                    if (Math.random() < (((float) (numIS + numIAS) / (float) (N))) * 0.00002) {
                        if (Math.random() > 0.7) {
                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                ((CBGVD) (currentAgent.cBGVD)).IAS = (int) (((CBGVD) (currentAgent.cBGVD)).IAS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                            currentAgent.minutesSick = -1;
                        } else {
                            if (((Agent) (currentAgent.cBGVD)) != null) {
                                ((CBGVD) (currentAgent.cBGVD)).E = (int) (((CBGVD) (currentAgent.cBGVD)).E) - 1;
                                ((CBGVD) (currentAgent.cBGVD)).IS = (int) (((CBGVD) (currentAgent.cBGVD)).IS) + 1;
                            }
                            currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                            currentAgent.minutesSick = -1;
                        }
                    }
                }
            }
        }
    }

}
