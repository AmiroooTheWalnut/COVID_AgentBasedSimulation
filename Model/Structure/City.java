 package COVID_AgentBasedSimulation.Model.Structure;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 

/**
 *
 * @author user
 */
public class City implements Serializable {
    static final long serialVersionUID = softwareVersion;
    public String name;
    public float lat;
    public float lon;
    public float size;
}
