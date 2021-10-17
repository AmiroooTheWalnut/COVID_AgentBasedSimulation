/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil;

import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.randn;
import static COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonManager.rnd;

/**
 *
 * @author user
 */
public class ShamilGroupSimulator {

    public static ShamilGroup groupInteraction(ShamilGroup grp) {

        Double ACTION_AFFECTING_PROBABILITY = ShamilPersonManager.thresholds_df.get("ACTION_AFFECTING_PROBABILITY");
        Double ACTION_INFECT_THRESHOLD = ShamilPersonManager.thresholds_df.get("ACTION_INFECT_THRESHOLD");

//        ACTION_AFFECTING_PROBABILITY = dfToFloat(thresholds_df,"ACTION_AFFECTING_PROBABILITY")
//        ACTION_INFECT_THRESHOLD = dfToFloat(thresholds_df,"ACTION_INFECT_THRESHOLD")
        int next_timestamp = 10;

        for (int i = 0; i < grp.actions.size(); i++) { // actn in grp.actions:
            ShamilAction actn = grp.actions.get(i);
            if (actn.timestamp >= next_timestamp) {

                grp.updateProximity();

                next_timestamp += 10;
            }

            int acting_person_id = actn.person_id;
            Person acting_person = grp.persons.get(grp.person_mapper.get(acting_person_id));
//            acting_person = grp.persons[grp.person_mapper[acting_person_id]];

            for (int j = 0; j < grp.persons.size(); j++) {// prsn in grp.persons:
                if (randn(actn.min_prob_affect , actn.max_prob_affect) > ACTION_AFFECTING_PROBABILITY) {
//                if(randn(actn.min_prob_affect,actn.max_prob_affect)>ACTION_AFFECTING_PROBABILITY):
                    if (grp.persons.get(j).shamilPersonProperties.id == acting_person_id) {
                        if (actn.min_effect_self >= 0) {

                            double infection = (1 - grp.persons.get(j).shamilPersonProperties.protectionLevel) * randn(actn.min_effect_self , actn.max_effect_self);

                            // #print(infection)
                            if (infection > ACTION_INFECT_THRESHOLD) {

                                grp.persons.get(j).shamilPersonProperties.infectionLevel = Math.max(Math.min(grp.persons.get(j).shamilPersonProperties.infectionLevel + infection, 1), 0);
                                // #print("\n" + str(actn.name) + " the infection case for self infection is - " + str(prsn.infection_level));
                            }
                        } else {

                            double infection = (1 - grp.persons.get(j).shamilPersonProperties.protectionLevel) * randn(actn.min_effect_self , actn.max_effect_self);
                            // #print("the infection case is - " + str(infection))
                            double infection_pos = (-1) * infection;

                            if (infection_pos > ACTION_INFECT_THRESHOLD) {

                                grp.persons.get(j).shamilPersonProperties.infectionLevel = Math.max(Math.min(grp.persons.get(j).shamilPersonProperties.infectionLevel + infection, 1), 0);
                            }
                        }
                    } else {
                        if (acting_person.shamilPersonProperties.state.equals("contagious_asymptomatic") || acting_person.shamilPersonProperties.state.equals("contagious_symptomatic")) {

                            if (grp.persons.get(j).shamilPersonProperties.isInfected == false) {

                                // #print("the people - " + str(acting_person_id) + " " +str(actn.person_id) + " " + str(prsn.id))
                                double proximit = grp.getProximity(acting_person_id, grp.persons.get(j).shamilPersonProperties.id);
                                // #proximit = 1

                                double infection = (1 - grp.persons.get(j).shamilPersonProperties.protectionLevel) * randn(actn.min_effect_others , actn.max_effect_others);
//                                infection = (1-grp.persons.get(j).shamilPersonProperties.protectionLevel) * randn(actn.min_effect_others,actn.max_effect_others)
                                // #print("infection before = " + str(infection) + " proximity - " + str(proximit))

                                infection = infection * proximit;

                                if (infection > ACTION_INFECT_THRESHOLD) {

                                    grp.persons.get(j).shamilPersonProperties.infectionLevel = Math.max(Math.min(grp.persons.get(j).shamilPersonProperties.infectionLevel + infection, 1), 0);

                                    grp.persons.get(j).shamilPersonProperties.infectedBy = acting_person_id;

                                    grp.persons.get(j).shamilPersonProperties.infectedByUpdt += 1;
                                }
                            }
                        }
                    }
                }
            }
        }

        return grp;
    }
}
