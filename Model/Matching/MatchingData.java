/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.Matching;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.POI;
import COVID_AgentBasedSimulation.Model.MainModel;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class MatchingData {

    public int[] permuteData;
    public float[] pOIType1Lats;
    public float[] pOIType1Lons;

    public float[] pOIType2Lats;
    public float[] pOIType2Lons;
    
    public ArrayList<Integer> pOIType1Options=new ArrayList();
    public ArrayList<Integer> pOIType1NotOptions=new ArrayList();
    
    public ArrayList<Integer> pOIType2Options=new ArrayList();
    public ArrayList<Integer> pOIType2NotOptions=new ArrayList();
    
    public ArrayList<PatternsRecordProcessed>[] foundType1POIs;
    public ArrayList<PatternsRecordProcessed>[] foundType2POIs;
    
    public void parseNAICSRules(String pOIType1,String pOIType2){
        String[] pOISplit = pOIType1.split("-");
        for(int i=0;i<pOISplit.length;i++){
            if(pOISplit[i].startsWith("!")){
                pOIType1NotOptions.add(Integer.valueOf(pOISplit[i].substring(1)));
            }else{
                pOIType1Options.add(Integer.valueOf(pOISplit[i]));
            }
        }
        pOISplit = pOIType2.split("-");
        for(int i=0;i<pOISplit.length;i++){
            if(pOISplit[i].startsWith("!")){
                pOIType2NotOptions.add(Integer.valueOf(pOISplit[i].substring(1)));
            }else{
                pOIType2Options.add(Integer.valueOf(pOISplit[i]));
            }
        }
    }

    public void readData(String matchingFilePath, String geoFilePath) {
        FileReader filereader;
        try {
            filereader = new FileReader(matchingFilePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(0)
                .build();
        List<String[]> matchingData = csvReader.readAll();
        
        permuteData=new int[matchingData.get(0).length];
        for(int i=0;i<matchingData.get(0).length;i++){
            permuteData[i]=Integer.parseInt(matchingData.get(0)[i]);
        }
        System.out.println("Finished reading matching data!");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MatchingData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MatchingData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(MatchingData.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            filereader = new FileReader(geoFilePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(0)
                .build();
        List<String[]> geoData = csvReader.readAll();
        
        pOIType1Lats=new float[geoData.size()];
        pOIType1Lons=new float[geoData.size()];
        pOIType2Lats=new float[geoData.size()];
        pOIType2Lons=new float[geoData.size()];
        for(int i=0;i<geoData.size();i++){
            pOIType1Lats[i]=Float.parseFloat(geoData.get(i)[0]);
            pOIType1Lons[i]=Float.parseFloat(geoData.get(i)[1]);
            pOIType2Lats[i]=Float.parseFloat(geoData.get(i)[2]);
            pOIType2Lons[i]=Float.parseFloat(geoData.get(i)[3]);
        }
        System.out.println("Finished reading geo data!");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MatchingData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MatchingData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(MatchingData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void findPOIs(MainModel model){
        foundType1POIs=new ArrayList[model.ABM.matchingData.pOIType1Options.size()];
        foundType2POIs=new ArrayList[model.ABM.matchingData.pOIType2Options.size()];
        for (int i = 0; i < model.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            for (int j = 0; j < model.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                int naics=model.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.naics_code;
                boolean isFoundValidType1 = false;
                boolean isFoundValidType2 = false;
                for (int m = 0; m < model.ABM.matchingData.pOIType1Options.size(); m++) {
                    if (naics == model.ABM.matchingData.pOIType1Options.get(m)) {
                        isFoundValidType1 = true;
                        break;
                    }
                }
                for (int m = 0; m < model.ABM.matchingData.pOIType1NotOptions.size(); m++) {
                    if (naics == model.ABM.matchingData.pOIType1NotOptions.get(m)) {
                        isFoundValidType1 = false;
                        break;
                    }
                }
                for (int m = 0; m < model.ABM.matchingData.pOIType2Options.size(); m++) {
                    if (naics == model.ABM.matchingData.pOIType2Options.get(m)) {
                        isFoundValidType2 = true;
                        break;
                    }
                }
                for (int m = 0; m < model.ABM.matchingData.pOIType2NotOptions.size(); m++) {
                    if (naics == model.ABM.matchingData.pOIType2NotOptions.get(m)) {
                        isFoundValidType2 = false;
                        break;
                    }
                }
                if(isFoundValidType1==true){
                    float lat=model.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lat;
                    float lon=model.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lon;
                    float minDist=Float.MAX_VALUE;
                    int minDistIndex=-1;
                    for(int n=0;n<model.ABM.matchingData.pOIType1Lats.length;n++){
                        float lat1=model.ABM.matchingData.pOIType1Lats[n];
                        float lon1=model.ABM.matchingData.pOIType1Lons[n];
                        float dist = (float) Math.sqrt(Math.pow(lat - lat1, 2) + Math.pow(lon - lon1, 2));
                        if(dist<minDist){
                            minDist=dist;
                            minDistIndex=n;
                        }
                    }
                    foundType1POIs[minDistIndex].add(model.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j));
                }
                if(isFoundValidType2==true){
                    float lat=model.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lat;
                    float lon=model.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lon;
                    float minDist=Float.MAX_VALUE;
                    int minDistIndex=-1;
                    for(int n=0;n<model.ABM.matchingData.pOIType2Lats.length;n++){
                        float lat1=model.ABM.matchingData.pOIType2Lats[n];
                        float lon1=model.ABM.matchingData.pOIType2Lons[n];
                        float dist = (float) Math.sqrt(Math.pow(lat - lat1, 2) + Math.pow(lon - lon1, 2));
                        if(dist<minDist){
                            minDist=dist;
                            minDistIndex=n;
                        }
                    }
                    foundType2POIs[minDistIndex].add(model.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j));
                }
            }
        }
    }

}
