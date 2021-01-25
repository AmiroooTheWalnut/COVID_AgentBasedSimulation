/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;

/**
 *
 * @author user
 */
public class OpenHours implements Serializable {
    static final long serialVersionUID = softwareVersion;
    public float startTime;
    public float endTimne;
}
