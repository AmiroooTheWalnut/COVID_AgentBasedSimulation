/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.GUI.GraphicalModel;

import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.City;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class GenerateData {
    
    private MainModel mainModel;
    
    public GenerateData(MainModel m){
        mainModel=m;
    }
    
    public void run(){
        
    }
    
    public void writeCBGPopulation(){
        ArrayList<String[]> output=new ArrayList();
        for(int i=0;i<((City)(mainModel.ABM.studyScopeGeography)).censusTracts.size();i++){
            for(int j=0;j<((City)(mainModel.ABM.studyScopeGeography)).censusTracts.get(i).censusBlocks.size();j++){
                String[] row=new String[1];
            }
        }
//        try {
//            Files.createDirectories(Paths.get("mobilityInferenceV2" + File.separator + mainModel.ABM.studyScope));
//            CSVWriter writer = new CSVWriter(new FileWriter("mobilityInference" + File.separator + mainModel.ABM.studyScope + File.separator + "FullSimple_" + jList1.getSelectedValue() + ".csv"));
//            writer.writeAll(data);
//            writer.close();
//        } catch (IOException ex) {
//            Logger.getLogger(GenerateData.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    public void writeCBGPopulationAge(){
        
    }
    
    public void writeCBGPopulationOccupation(){
        
    }
    
    public void writeCBGPOIVisitByDistance(){
        
    }
    
}
