/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.Agent;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.DwellTime;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonProperties;
import COVID_AgentBasedSimulation.Model.MainModel;
import java.time.ZonedDateTime;

/**
 *
 * @author user
 */
public class Person extends Agent {

    Person currentAgent = this;
    
    MainModel myModelRoot;

    public PersonProperties properties = new PersonProperties();
    public ShamilPersonProperties shamilPersonProperties = new ShamilPersonProperties();

    public boolean isActive = false;//IF OUR ABM IS ACTIVE, THEN THIS CLASS'S BEHAVIOR WILL RUN

    public Person() {
        myType = "Person";
    }

    @Override
    public void constructor(MainModel modelRoot) {
        myModelRoot=modelRoot;
    }

    @Override
    public void behavior() {
        if (isActive == true) {
            if (properties.isAtHome == true) {
                travelFromHome(myModelRoot.ABM.currentTime);
            }
            if (properties.isAtWork == true) {
                travelFromWork(myModelRoot.ABM.currentTime);
            }
            if (properties.isInTravel == true) {
                properties.minutesStayed += 1;
                myModelRoot.ABM.root.pOIs.get(properties.currentPattern.placeKey).contact(myModelRoot.ABM.currentTime, this);
                returnFromTravel();
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

    public void returnAction( ) {
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
        myModelRoot.ABM.root.pOIs.get(properties.currentPattern.placeKey).peopleInPOI.remove(this);
        properties.currentPattern = null;
    }
    
    public void travelFromWork(ZonedDateTime currentTime) {
        PatternsRecordProcessed dest = chooseDestination(properties.workRegion);
        boolean decision = decideToTravel(dest, currentTime);
        if (decision == true) {
            properties.dwellTime = decideDwellTime(dest);
            lat = dest.place.lat;
            lon = dest.place.lon;
            properties.isAtWork = false;
            properties.isInTravel = true;
            properties.minutesStayed = 0;
            properties.didTravelFromHome = false;
            properties.didTravelFromWork = true;
            properties.currentPattern = dest;
            myModelRoot.ABM.root.pOIs.get(properties.currentPattern.placeKey).peopleInPOI.add(this);
        }
    }

    public void travelFromHome(ZonedDateTime currentTime) {
        PatternsRecordProcessed dest = chooseDestination(properties.homeRegion);
        boolean decision = decideToTravel(dest, currentTime);
        if (decision == true) {
            properties.dwellTime = decideDwellTime(dest);
            lat = dest.place.lat;
            lon = dest.place.lon;
            properties.isAtHome = false;
            properties.isInTravel = true;
            properties.minutesStayed = 0;
            properties.didTravelFromHome = true;
            properties.didTravelFromWork = false;
            properties.currentPattern = dest;
            myModelRoot.ABM.root.pOIs.get(properties.currentPattern.placeKey).peopleInPOI.add(this);
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
}
