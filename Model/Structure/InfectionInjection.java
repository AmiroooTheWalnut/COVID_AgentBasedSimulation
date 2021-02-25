/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import java.time.LocalDateTime;

/**
 *
 * @author user
 */
public class InfectionInjection {
    public LocalDateTime injectionDates;
    public int numInfected;
    public CensusBlockGroup startCensusBlockGroup;
    public CensusTract startCensusTract;
    public County startCounty;
    public State startState;
    public Country startCountry;
}
