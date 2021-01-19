package COVID_AgentBasedSimulation.Model.Structure;

import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class County {
    public String name;
    public ArrayList<City> cities;
    public ArrayList<ZipCode> zipcodes;
    public ArrayList<CensusTract> censusTracts;
    
    public int fipsCode;
    
}
