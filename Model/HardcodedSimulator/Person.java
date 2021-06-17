/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.Agent;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentBasedModel;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentTemplate;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.DwellTime;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
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

    public CensusBlockGroup home;
    public int status;
    public CensusBlockGroup currentLocation;

    public int minutesStayed;
    public ArrayList<SafegraphPlace> destinationPlaces;
    public ArrayList<Integer> destinationPlacesFreq;
    public int travelStartDecisionCounter;
    public int cumulativeDestinationFreqs;
    public CBG cBG;
    public int dstIndex;
    public DwellTime dwellTime;
    public CensusBlockGroup dayCBG;
    public boolean isAtWork;
    public int minutesSick;
    public int minutesTravelToWorkFrom7;
    public int minutesTravelFromWorkFrom16;
    public boolean isDestinedToDeath;
    public boolean isPotentiallyWorkAtHome;

    enum statusEnum {
        SUSCEPTIBLE, EXPOSED, INFECTED_SYM, INFECTED_ASYM, RECOVERED, DEAD;
    }

    public Person() {
        myType = "Person";
    }

    public void constructor(MainModel modelRoot) {

    }

    public void behavior(MainModel modelRoot) {

        run(currentAgent, modelRoot, modelRoot.getABM().getCurrentTime());
    }

    //@CompileStatic
    void run(Person currentAgent, MainModel rootModel, ZonedDateTime currentTime) {

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
        CensusBlockGroup currentLocation = (CensusBlockGroup) (currentAgent.currentLocation);
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
                        currentLocation = (CensusBlockGroup) (currentAgent.home);
                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
                        }
                    } else {
                        currentLocation = (CensusBlockGroup) (currentAgent.dayCBG);
                    }

                    currentAgent.lat = currentLocation.getLat();
                    currentAgent.lon = currentLocation.getLon();
                    travelStartDecisionCounter = 0;
                    currentAgent.dstIndex = -1;

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
                            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
                            }

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
                        currentLocation = (CensusBlockGroup) currentAgent.home;
                        currentAgent.lat = currentLocation.getLat();
                        currentAgent.lon = currentLocation.getLon();
                        currentAgent.isAtWork = false;
                        currentAgent.minutesTravelToWorkFrom7 = -1;
                        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
                            currentAgent.status = statusEnum.SUSCEPTIBLE.ordinal();//NOW SAFE
                        }

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
                            currentAgent.status = statusEnum.RECOVERED.ordinal();//RECOVERED
                        }
                    }
                } else {
                    if (minsSick > 10080) {
                        if (Math.random() < Math.pow((double) (minsSick - 20160) / (double) (20160), 5)) {
                            currentAgent.minutesSick = -1;
                            currentAgent.status = statusEnum.DEAD.ordinal();
                        }
                    }
                }
            }
        }
        if ((int) (currentAgent.dstIndex) != -1 || (boolean) (currentAgent.isAtWork) == true) {
            if ((int) (currentAgent.status) == statusEnum.SUSCEPTIBLE.ordinal()) {
                currentAgent.status = statusEnum.EXPOSED.ordinal();//NOW EXPOSED
            }
        }

        if ((int) (currentAgent.status) == statusEnum.EXPOSED.ordinal()) {
            if (((Agent) (currentAgent.cBG)) != null) {
                int numIS = (int) (((CBG) (currentAgent.cBG)).IS);
                int numIAS = (int) (((CBG) (currentAgent.cBG)).IAS);
                int N = (int) (((CBG) (currentAgent.cBG)).N);
                if (Math.random() < (((float) (numIS + numIAS) / (float) (N))) * 0.00002f) {
                    if (Math.random() > 0.7) {
                        currentAgent.status = statusEnum.INFECTED_ASYM.ordinal();
                        currentAgent.minutesSick = -1;
                    } else {
                        currentAgent.status = statusEnum.INFECTED_SYM.ordinal();
                        currentAgent.minutesSick = -1;
                    }
                }
            }
        }
    }

}
