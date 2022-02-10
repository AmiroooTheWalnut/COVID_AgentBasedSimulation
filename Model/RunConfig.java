/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class RunConfig {
    public int numRuns=1;
    
    public boolean isReportContactRate;
    public boolean isSpecialScenarioActive;
    public boolean isSpecificRegionInfected;
    public String infectedRegionIndicesString;
    public int CBGIndexToInfect;// "isSpecificRegionInfected" must be true to work
    public String scenarioName;
    public boolean isParallelBehaviorEvaluation;
    public int numResidents;
    public int numCPUs;
    
    public static RunConfig loadModel(String path){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileReader in;
        try {
            in = new FileReader(path);
            BufferedReader br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            in.close();

            RunConfig result = gson.fromJson(sb.toString(), RunConfig.class);
            return result;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RunConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RunConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void saveModel(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result = gson.toJson(this);
        //System.out.println(result);
        BufferedWriter writer;
        try {
            FileWriter out = new FileWriter(path);
            writer = new BufferedWriter(out);
            writer.write(result);

            writer.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(RunConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
