/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class ProjectManager {

    public ProjectDefaults projectDefaults;

    public void checkDefaults(MainFrame mainFrame) {
        projectDefaults = new ProjectDefaults();
        File file = new File("./ABMDefaults.json");
        if (file.exists() == true) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileReader in;
            try {
                in = new FileReader("./ABMDefaults.json");
                BufferedReader br = new BufferedReader(in);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();

                ProjectDefaults result = gson.fromJson(sb.toString(), ProjectDefaults.class);
                projectDefaults = result;
                if (projectDefaults != null) {
                    if (projectDefaults.defaultProjectFileLocation != null) {
                        //projectDefaults.currentDefaultProjectFileLocation=projectDefaults.defaultProjectFileLocation;
                        String[] temp = projectDefaults.defaultProjectFileLocation.split("\\\\");
                        mainFrame.jLabel4.setText(temp[temp.length - 1]);
                        if (projectDefaults.defaultProjectFileLocation.length() > 0) {
                            mainFrame.mainModel.ABM.loadModel(projectDefaults.defaultProjectFileLocation);
                            mainFrame.mainModel.ABM.filePath = projectDefaults.defaultProjectFileLocation;
                            mainFrame.jLabel7.setText(temp[temp.length - 1]);
                        }
                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
