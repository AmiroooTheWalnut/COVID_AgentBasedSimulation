/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;

/**
 *
 * @author user
 */
public class RegionSnapshot implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public int N=0;
    public int S=0;
    public int IS=0;
    public int IAS=0;
    public int R=0;
    public int D=0;
    public double rate=0;
    public int sick=0;
}
