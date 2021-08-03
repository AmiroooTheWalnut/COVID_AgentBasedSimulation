/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class SupplementaryCaseStudyData implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public ArrayList<VDCell> vDCells;
    public ArrayList<CBGVDCell> cBGVDCells;
    public double shopMergePrecision;
    public double schoolMergePrecision;
    public double templeMergePrecision;
}
