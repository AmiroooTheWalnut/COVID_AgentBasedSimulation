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
 * @author user
 */
public class Dataset {
    public ZonedDateTime startingDate;
    public ZonedDateTime endingDate;
    
    public DatasetTemplate datasetTemplate;
    
    public void setDatasetTemplate(){
        
    }
    
    public void requestDataset(AllGISData allGISData, int year, int month, boolean isParallel, int numCPU){
        
    }
}
