/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class AllSafegraphPlaces implements Serializable {
    static final long serialVersionUID = softwareVersion;
    public ArrayList<SafegraphPlaces> monthlySafegraphPlacesList;
    
    public static String[] detectAllPlaces(String directoryString){
        File directory=new File(directoryString);
        String[] fileDirectoryList = directory.list();
        ArrayList<String> detectedDirectoriesArrayList=new ArrayList();
        for(int i=0;i<fileDirectoryList.length;i++){
            if(fileDirectoryList[i].toLowerCase().contains("core_poi")){
                detectedDirectoriesArrayList.add(fileDirectoryList[i]);
            }
        }
        String[] detectedDirectories=new String[detectedDirectoriesArrayList.size()];
        for(int i=0;i<detectedDirectories.length;i++){
            detectedDirectories[i]=detectedDirectoriesArrayList.get(i);
        }
        return detectedDirectories;
    }
    
}
