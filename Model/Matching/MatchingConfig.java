/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.Matching;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class MatchingConfig {
    public String shopSchPerm;
    public String geoFile;
    
    public void saveMatchConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result = gson.toJson(this);
        //System.out.println(result);
        BufferedWriter writer;
        try {
            String path="MatchingConfig.json";
            FileWriter out = new FileWriter(path);
            writer = new BufferedWriter(out);
            writer.write(result);

            writer.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(MatchingConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadMatchConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileReader in;
        try {
            String path="MatchingConfig.json";
            in = new FileReader(path.trim());
            BufferedReader br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            in.close();

            MatchingConfig result = gson.fromJson(sb.toString(), MatchingConfig.class);
            if (result.shopSchPerm != null) {
                shopSchPerm = result.shopSchPerm;
            }
            if (result.geoFile != null) {
                geoFile = result.geoFile;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MatchingConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MatchingConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
