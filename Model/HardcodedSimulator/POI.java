/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.PersonProperties.infectionDistances;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.PersonProperties.infectionProbabilities;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Root.statusEnum;
import COVID_AgentBasedSimulation.Model.MainModel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author user
 */
public class POI {

    public static double CONTACT_RATE = 0.6;//0.55;//0.23;//CONTACT PER MINUTE
    public static double CHANCE_OF_ENV_CONTAMINATION = 0.0025;//0.00055;//0.00015;

    public PatternsRecordProcessed patternsRecord;
    public double contaminatedTime = 0;
//    public int numInfectedPeopleInPOI=0;
    public List<Person> peopleInPOI = Collections.synchronizedList(new ArrayList());

    double numInfected = 0;
    //double numInfectedFuzzy = 0;

    final double fixedTransmissionRate = 0.000001;

    public void contact(MainModel mainModel, ZonedDateTime currentTime, Person person, boolean isUseBuildingLogic, double pTSFraction) {
        if (isUseBuildingLogic == true) {
            double probability = getProbabilityOfInfection(currentTime);

            //probability=10;//DEBUG
            infectedByEnvironment(mainModel, person, pTSFraction);
            infectByContact(mainModel, probability, person, pTSFraction);
        } else {
            if (Math.random() < fixedTransmissionRate) {
                for (int m = 0; m < person.insidePeople.size(); m++) {
                    if (person.insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
//                  System.out.println("CONTACT INFECTION");
                        if (Math.random() > 0.7) {
                            person.insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
                        } else {
                            person.insidePeople.get(m).fpp.status = statusEnum.INFECTED_SYM.ordinal();
                        }
                    }
                }
            }
        }
    }

    public double getProbabilityOfInfection(ZonedDateTime currentTime) {
        int dayInMonth = currentTime.getDayOfMonth() - 1;
        byte dayInWeek = (byte) ((currentTime.getDayOfWeek().getValue()) - 1);
        int hourInDay = currentTime.getHour();
        double percentDayInMonth = 1;
        if (dayInMonth < patternsRecord.visits_by_day.length) {
            percentDayInMonth = (double) (patternsRecord.visits_by_day[dayInMonth]) / (double) (patternsRecord.sumVisitsByDayOfMonth);
        }
        double percentDayInWeek = (double) (patternsRecord.popularity_by_day.get(dayInWeek)) / (double) (patternsRecord.sumVisitsByDayOfWeek);
        double percentHourInDay = (double) (patternsRecord.popularity_by_hour[hourInDay]) / (double) (patternsRecord.sumVisitsByHourofDay);
        int numPeopleInPOI = (int) Math.ceil(patternsRecord.raw_visit_counts * percentDayInMonth * percentDayInWeek * percentHourInDay);
        double estimatedDistance = Math.sqrt(patternsRecord.place.landArea / numPeopleInPOI) * 0.98d;
        double probability = 0.0001;
        for (int d = 0; d < infectionDistances.length; d++) {
            if (estimatedDistance < infectionDistances[d]) {
                probability = infectionProbabilities[d];
                return probability;
            }
        }
        return probability;
    }

    public void infectByContact(MainModel mainModel, double probability, Person person, double pTSFraction) {
//        double numInfected = 0;
//        for (int i = 0; i < peopleInPOI.size(); i++) {
//            if (peopleInPOI.get(i).properties.status == statusEnum.INFECTED_ASYM.ordinal() || peopleInPOI.get(i).properties.status == statusEnum.INFECTED_SYM.ordinal()) {
//                if (Math.random() < CHANCE_OF_ENV_CONTAMINATION) {
//                    contaminatedTime = 1440;
//                }
//                numInfected += 1;
//            }
//        }

        if (Math.random() < CONTACT_RATE) {
            person.numContacts = person.numContacts + 1;

            for (int m = 0; m < person.insidePeople.size(); m++) {
                if (Math.random() < (numInfected / (double) (peopleInPOI.size() * pTSFraction)) * probability * 1.1) {
                    if (person.insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
//                  System.out.println("CONTACT INFECTION");
                        if (Math.random() > 0.7) {
                            person.insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
                        } else {
                            person.insidePeople.get(m).fpp.status = statusEnum.INFECTED_SYM.ordinal();
                        }
                        if (mainModel.isDebugging == true) {
                            mainModel.ABM.infectedByPOIContactDaily += 1;
                        }
                    }
                }
            }
        }
    }

    public void infectedByEnvironment(MainModel mainModel, Person person, double pTSFraction) {
        if (contaminatedTime > 0) {
//            System.out.println("awarenessLevel: "+person.shamilPersonProperties.awarenessLevel);
//            System.out.println("protectionLevel: "+person.shamilPersonProperties.protectionLevel);

            for (int m = 0; m < person.insidePeople.size(); m++) {
                if (Math.random() < CONTACT_RATE*0.05) {
                    double r = Math.random();
                    if (r < (1 - person.shamilPersonProperties.protectionLevel) * 0.00085) {
                        if (person.insidePeople.get(m).fpp.status == statusEnum.SUSCEPTIBLE.ordinal()) {
//                      System.out.println("ENV INFECTION");
                            if (Math.random() > 0.7) {
                                person.insidePeople.get(m).fpp.status = statusEnum.INFECTED_ASYM.ordinal();
                            } else {
                                person.insidePeople.get(m).fpp.status = statusEnum.INFECTED_SYM.ordinal();
                            }
                            if (mainModel.isDebugging == true) {
                                mainModel.ABM.infectedByPOIEnvDaily += 1;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateContamination() {
        if (contaminatedTime > 0) {
            contaminatedTime -= 1;
        }
    }

}
