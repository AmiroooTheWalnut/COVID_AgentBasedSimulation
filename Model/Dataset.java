/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import java.time.ZonedDateTime;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class Dataset {
    public ZonedDateTime startingDate;
    public ZonedDateTime endingDate;
    
    public DatasetTemplate datasetTemplate;
    
    public void setDatasetTemplate(){
        
    }
    
    public void requestDataset(AllGISData allGISData, String project, String year, String month, boolean isParallel, int numCPU){
        
    }
    
    public void requestDatasetRange(AllGISData allGISData, String project, String years[], String months[][], boolean isParallel, int numCPU){
        
    }
}
