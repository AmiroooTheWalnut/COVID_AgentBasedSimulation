/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model;

import java.util.ArrayList;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class DatasetTemplate {
    public String name;
    public ArrayList<DatasetTemplate> innerDatasetTemplates=new ArrayList();
    public ArrayList<RecordTemplate> recordTemplates=new ArrayList();
}
