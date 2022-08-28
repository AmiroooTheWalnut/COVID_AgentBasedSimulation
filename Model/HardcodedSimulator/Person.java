/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.Agent;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.DwellTime;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.POI.CHANCE_OF_ENV_CONTAMINATION;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilFuzzyablePersonProperties;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonProperties;
import COVID_AgentBasedSimulation.Model.MainModel;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Person extends Agent {

    Person currentAgent = this;

    public ArrayList<FuzzyPerson> insidePeople;

    public boolean isPolled = false;

    public boolean isActive = false;//IF OUR ABM IS ACTIVE, THEN THIS CLASS'S BEHAVIOR WILL RUN

    public int numTravels = 0;
    public int numTravelsInDay = 0;
    public int numContacts = 0;

    public PersonProperties properties = new PersonProperties();
    public ShamilPersonProperties shamilPersonProperties = new ShamilPersonProperties();

    public Person(int index) {
        myType = "Person";
        myIndex = index;
    }

    @Override
    public void constructor(MainModel modelRoot) {
        myModelRoot = modelRoot;
    }

    @Override
    public void behavior() {
        if (isActive == true) {
            for (int m = 0; m < insidePeople.size(); m++) {
                if (insidePeople.get(m).fpp.status != Root.statusEnum.DEAD.ordinal()) {
                    isPolled = false;
                    if (properties.isAtHome == true) {
                        travelFromHome(myModelRoot.ABM.currentTime);
                    }
                    if (properties.isAtWork == true) {
                        travelFromWork(myModelRoot.ABM.currentTime);
                    }
                    if (properties.isInTravel == true) {
                        properties.minutesStayed += 1;
//                    myModelRoot.ABM.root.pOIs.get(properties.currentPattern.placeKey).contact(myModelRoot.ABM.currentTime, this);
                        properties.currentPOI.contact(myModelRoot.ABM.currentTime, this, myModelRoot.ABM.isBuildingLogicActive,myModelRoot.ABM.root.pTSFraction);
                        returnFromTravel();
                    }
                    break;
//                pollContact();
                }
            }
        }
    }

    public void returnFromTravel() {
        if (properties.minutesStayed > properties.dwellTime.dwellDuration[0]) {
            if (Math.random() < (float) (properties.minutesStayed - properties.dwellTime.dwellDuration[0]) / (float) (properties.dwellTime.dwellDuration[1] - properties.dwellTime.dwellDuration[0])) {
                returnAction();
            }
        }
    }

    public void returnAction() {
        if (properties.didTravelFromHome == true) {
            lat = properties.homeRegion.lat;
            lon = properties.homeRegion.lon;
            properties.isAtHome = true;
        }
        if (properties.didTravelFromWork == true) {
            lat = properties.workRegion.lat;
            lon = properties.workRegion.lon;
            properties.isAtWork = true;
        }
        properties.dwellTime = null;
        properties.isInTravel = false;
        properties.minutesStayed = 0;
        properties.didTravelFromHome = false;
        properties.didTravelFromWork = false;

//        System.out.println("RETURN");
//        System.out.println("mtIndex: "+myIndex);
//        System.out.println("properties.currentPOI.peopleInPOI.size(): before:"+properties.currentPOI.peopleInPOI.size());
//        POI pOI = myModelRoot.ABM.root.pOIs.get(properties.currentPattern.placeKey);
//        pOI.peopleInPOI.remove(this);
        properties.currentPOI.peopleInPOI.remove(this);

//        System.out.println("properties.currentPOI.peopleInPOI.size(): after:"+properties.currentPOI.peopleInPOI.size());
        for (int m = 0; m < insidePeople.size(); m++) {
            if (insidePeople.get(m).fpp.isInitiallyInfectedEnteringPOI == true) {
                properties.currentPOI.numInfected -= 1;
                insidePeople.get(m).fpp.isInitiallyInfectedEnteringPOI = false;
            }
        }
        properties.currentPOI = null;
        properties.currentPattern = null;
    }

    public void travelFromWork(ZonedDateTime currentTime) {
        PatternsRecordProcessed dest = chooseDestination(properties.workRegion);
        boolean decision = decideToTravel(dest, currentTime);
        if (decision == true) {
            numTravels = numTravels + 1;
            numTravelsInDay = numTravelsInDay + 1;
            properties.dwellTime = decideDwellTime(dest);
            lat = dest.place.lat;
            lon = dest.place.lon;
            properties.isAtWork = false;
            properties.isInTravel = true;
            properties.minutesStayed = 0;
            properties.didTravelFromHome = false;
            properties.didTravelFromWork = true;
            properties.currentPattern = dest;
            POI pOI = myModelRoot.ABM.root.pOIs.get(properties.currentPattern.placeKey);

//            System.out.println("TRAVELFROMWORK");
//            System.out.println("mtIndex: "+myIndex);
//        System.out.println("properties.currentPOI.peopleInPOI.size(): before:"+pOI.peopleInPOI.size());
            pOI.peopleInPOI.add(this);

            properties.currentPOI = pOI;

//        System.out.println("properties.currentPOI.peopleInPOI.size(): after:"+properties.currentPOI.peopleInPOI.size());
            for (int m = 0; m < insidePeople.size(); m++) {
                if (insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_ASYM.ordinal() || insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_SYM.ordinal()) {
                    if (Math.random() < CHANCE_OF_ENV_CONTAMINATION) {
                        pOI.contaminatedTime = 1440;
                    }
                    pOI.numInfected += 1;
                    insidePeople.get(m).fpp.isInitiallyInfectedEnteringPOI = true;
                }
            }
        }
    }

    public void travelFromHome(ZonedDateTime currentTime) {
        PatternsRecordProcessed dest = chooseDestination(properties.homeRegion);
        boolean decision = decideToTravel(dest, currentTime);
        if (decision == true) {
            numTravels = numTravels + 1;
            numTravelsInDay = numTravelsInDay + 1;
            properties.dwellTime = decideDwellTime(dest);
            lat = dest.place.lat;
            lon = dest.place.lon;
            properties.isAtHome = false;
            properties.isInTravel = true;
            properties.minutesStayed = 0;
            properties.didTravelFromHome = true;
            properties.didTravelFromWork = false;
            properties.currentPattern = dest;
            POI pOI = myModelRoot.ABM.root.pOIs.get(properties.currentPattern.placeKey);

//            System.out.println("TRAVELFROMHOME");
//            System.out.println("mtIndex: "+myIndex);
//        System.out.println("properties.currentPOI.peopleInPOI.size(): before:"+pOI.peopleInPOI.size());
            pOI.peopleInPOI.add(this);
            properties.currentPOI = pOI;

            int dayInMonth = currentTime.getDayOfMonth() - 1;
            byte dayInWeek = (byte) ((currentTime.getDayOfWeek().getValue()) - 1);
            int hourInDay = currentTime.getHour();
            double percentDayInMonth = 1;
            if (dayInMonth < properties.currentPattern.visits_by_day.length) {
                percentDayInMonth = (double) (properties.currentPattern.visits_by_day[dayInMonth]) / (double) (properties.currentPattern.sumVisitsByDayOfMonth);
            }
            double percentDayInWeek = (double) (properties.currentPattern.popularity_by_day.get(dayInWeek)) / (double) (properties.currentPattern.sumVisitsByDayOfWeek);
            double percentHourInDay = (double) (properties.currentPattern.popularity_by_hour[hourInDay]) / (double) (properties.currentPattern.sumVisitsByHourofDay);
            int numPeopleInPOI = (int) Math.ceil(properties.currentPattern.raw_visit_counts * percentDayInMonth * percentDayInWeek * percentHourInDay);

//            System.out.println("properties.currentPOI.peopleInPOI.size(): after:"+properties.currentPOI.peopleInPOI.size());
            for (int m = 0; m < insidePeople.size(); m++) {
                if (insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_ASYM.ordinal() || insidePeople.get(m).fpp.status == Root.statusEnum.INFECTED_SYM.ordinal()) {
                    pOI.numInfected += 1;
                    insidePeople.get(m).fpp.isInitiallyInfectedEnteringPOI = true;
                }
            }
            double infFrac = Math.min(1, pOI.numInfected / numPeopleInPOI);
            if (Math.random() < CHANCE_OF_ENV_CONTAMINATION * infFrac * shamilPersonProperties.protectionLevel) {
                pOI.contaminatedTime = 1440;
            }

        }
    }

    public DwellTime decideDwellTime(PatternsRecordProcessed record) {
        int selectedDwellTime = (int) (Math.floor(Math.random() * record.sumDwellTime));
        int cumulativeDwellTime = 0;
        for (byte i = 0; i < record.bucketed_dwell_times.size(); i++) {
            cumulativeDwellTime += record.bucketed_dwell_times.get(i).number;
            if (cumulativeDwellTime > selectedDwellTime) {
                return record.bucketed_dwell_times.get(i);
            }
        }
        return null;
    }

    public boolean decideToTravel(PatternsRecordProcessed record, ZonedDateTime currentTime) {
        int dayInMonth = currentTime.getDayOfMonth() - 1;
        try {
            if (Math.random() < 0.02) {
                int selectedDayInMonth = (int) (Math.floor(Math.random() * record.sumVisitsByDayOfMonth));
                int cumulativeDayInMonth = 0;
                for (int i = 0; i < record.visits_by_day.length; i++) {
                    cumulativeDayInMonth += record.visits_by_day[i];
                    if (cumulativeDayInMonth > selectedDayInMonth) {
                        if (i == dayInMonth) {
                            byte dayInWeek = (byte) ((currentTime.getDayOfWeek().getValue()) - 1);
                            int selectedDayInWeek = (int) (Math.floor(Math.random() * record.sumVisitsByDayOfWeek));
                            int cumulativeDayInWeek = 0;
                            for (byte j = 0; j < 7; j++) {
                                cumulativeDayInWeek += record.popularity_by_day.get(j);
                                if (cumulativeDayInWeek > selectedDayInWeek) {
                                    if (j == dayInWeek) {
                                        int hourInDay = currentTime.getHour();
                                        int selectedHourInDay = (int) (Math.floor(Math.random() * record.sumVisitsByHourofDay));
                                        int cumulativeHourInDay = 0;
                                        for (byte k = 0; k < record.popularity_by_hour.length; k++) {
                                            cumulativeHourInDay += record.popularity_by_hour[k];
                                            if (cumulativeHourInDay > selectedHourInDay) {
                                                if (k == hourInDay) {
                                                    return true;
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
        } catch (Exception ex) {
            //ex.printStackTrace(System.out);
            //System.out.println(ex.getMessage());
        }

        return false;
    }

    public PatternsRecordProcessed chooseDestination(Region region) {
        double selectedDestFreq = (Math.floor(Math.random() * region.scheduleList.originalSumFrequencies));
        double cumulativeDestFreqs = 0;
        for (int i = 0; i < region.scheduleList.originalFrequencies.size(); i++) {
            cumulativeDestFreqs += region.scheduleList.originalFrequencies.get(i);
            if (cumulativeDestFreqs > selectedDestFreq) {
                return region.scheduleList.originalDestinations.get(i);
            }
        }
        return null;
    }

    public void pollContact() {
        if (properties.isInTravel == true) {
            if (isPolled == false) {
//            if(myModelRoot.ABM.currentTime.getMinute()==0){
//            System.out.println("properties.currentPOI.peopleInPOI.size() "+properties.currentPOI.peopleInPOI.size());
//            }
                try {
                    if (properties.currentPOI != null) {
                        for (int i = 0; i < properties.currentPOI.peopleInPOI.size(); i++) {
//                if(properties.currentPOI.peopleInPOI==null){
//                    System.out.println("STRANGE!!!");
//                }
//                if(i>=properties.currentPOI.peopleInPOI.size()){
//                    System.out.println("STRANGE2!!!");
//                }
                            myModelRoot.ABM.root.agentPairContact[myIndex][i] = myModelRoot.ABM.root.agentPairContact[myIndex][i] + properties.currentPOI.peopleInPOI.size();
                            myModelRoot.ABM.root.agentPairContact[i][myIndex] = myModelRoot.ABM.root.agentPairContact[i][myIndex] + properties.currentPOI.peopleInPOI.size();

                            properties.currentPOI.peopleInPOI.get(i).isPolled = true;

                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Unsafe thread access!!! Logical polling unsafe access.");
                }
            }
//            for (int i = 0; i < myModelRoot.ABM.root.people.size(); i++) {
//                if (myIndex != myModelRoot.ABM.root.people.get(i).myIndex) {
//                    if (myModelRoot.ABM.root.people.get(i).properties.isInTravel == true) {
//                        if (properties.currentPattern.placeKey.equals(myModelRoot.ABM.root.people.get(i).properties.currentPattern.placeKey)) {
//                            myModelRoot.ABM.root.agentPairContact[myIndex][i] = myModelRoot.ABM.root.agentPairContact[myIndex][i] + 1;
//                        }
//                    }
//                }
//            }
        }
    }
}
