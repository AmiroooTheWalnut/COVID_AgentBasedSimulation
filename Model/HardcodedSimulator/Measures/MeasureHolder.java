/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator.Measures;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.POI;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.PersonProperties;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.RootArtificial;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Scope;
import com.opencsv.CSVWriter;
import de.siegmar.fastcsv.writer.CsvWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class MeasureHolder {

    //ArrayList is used just in case we want to investigate multiple CBGs in one simulation
    public ArrayList<NOVMeasure> nOVMeasure = new ArrayList();
    public ArrayList<ADVMeasure> aDVMeasure = new ArrayList();
    public ArrayList<PTAVSPMeasure> pTAVSPMeasure = new ArrayList();

    public void initializeMeasures(Scope scope, LinkedHashMap<String, POI> pOIs) {
        POI pOI = selectBestPOI(pOIs);
        CensusBlockGroup cBG = selectBestCBGByPop(scope);
//        CensusBlockGroup cBG = selectBestCBGByCloseness(scope, pOI);
        nOVMeasure.add(new NOVMeasure());
        nOVMeasure.get(0).source = cBG;
        nOVMeasure.get(0).destination = pOI;
        aDVMeasure.add(new ADVMeasure());
        aDVMeasure.get(0).source = cBG;
        aDVMeasure.get(0).destination = pOI;
        pTAVSPMeasure.add(new PTAVSPMeasure());
        pTAVSPMeasure.get(0).source1 = cBG;
        ArrayList<Long> exceptions = new ArrayList();
        exceptions.add(cBG.id);
        CensusBlockGroup source2 = selectBestCBGWithExceptionsByPop(scope, exceptions);
//        CensusBlockGroup source2 = selectBestCBGWithExceptionsByCloseness(scope, pOI, exceptions);
        pTAVSPMeasure.get(0).source2 = source2;
        pTAVSPMeasure.get(0).destination = pOI;
    }

    public POI selectBestPOI(LinkedHashMap<String, POI> pOIs) {
        POI bestPOI = null;
        int maxVisit = -1;
        for (POI value : pOIs.values()) {
            if (value.patternsRecord.raw_visit_counts > maxVisit) {
                maxVisit = value.patternsRecord.raw_visit_counts;
                bestPOI = value;
            }
        }
        return bestPOI;
    }

    public void handlePTAVSP(MainModel myModelRoot) {
        ArrayList<Person> peopleFromSource1 = new ArrayList();
        ArrayList<Person> peopleFromSource2 = new ArrayList();
        for (int i = 0; i < pTAVSPMeasure.get(0).destination.peopleInPOI.size(); i++) {
            pTAVSPMeasure.get(0).destination.peopleInPOI.get(i).properties.homeRegion.debugNumTravelPTAVSP += 1;
            for (int j = 0; j < pTAVSPMeasure.get(0).destination.peopleInPOI.get(i).properties.homeRegion.cBGsIDsInvolved.size(); j++) {
                if (myModelRoot.ABM.measureHolder.pTAVSPMeasure.get(0).source1.id == pTAVSPMeasure.get(0).destination.peopleInPOI.get(i).properties.homeRegion.cBGsIDsInvolved.get(j)) {
                    peopleFromSource1.add(pTAVSPMeasure.get(0).destination.peopleInPOI.get(i));
                }
                if (myModelRoot.ABM.measureHolder.pTAVSPMeasure.get(0).source2.id == pTAVSPMeasure.get(0).destination.peopleInPOI.get(i).properties.homeRegion.cBGsIDsInvolved.get(j)) {
                    peopleFromSource2.add(pTAVSPMeasure.get(0).destination.peopleInPOI.get(i));
                }
            }
        }
//        System.out.println(pTAVSPMeasure.get(0).destination.peopleInPOI.size());
//        System.out.println("\\/\\/people there!");
//        for(int m=0;m<pTAVSPMeasure.get(0).destination.peopleInPOI.size();m++){
//            System.out.println("person: "+pTAVSPMeasure.get(0).destination.peopleInPOI.get(m).properties.homeRegion.cBGsIDsInvolved.get(0));
//        }
//        System.out.println("^^^people there!");
//        if(peopleFromSource1.size()>1 && peopleFromSource2.size()>1){
//            System.out.println("\\/\\/found people there!");
//            for(int m=0;m<peopleFromSource1.size();m++){
//                System.out.println(peopleFromSource1.get(m).properties.homeRegion.cBGsIDsInvolved.get(0));
//            }
//            for(int m=0;m<peopleFromSource2.size();m++){
//                System.out.println(peopleFromSource2.get(m).properties.homeRegion.cBGsIDsInvolved.get(0));
//            }
//            System.out.println("^^found people there!");
//        }
        for (int i = 0; i < peopleFromSource1.size(); i++) {
            for (int j = 0; j < peopleFromSource2.size(); j++) {
                if (peopleFromSource1.get(i).isPolledPTAVSP == false || peopleFromSource2.get(j).isPolledPTAVSP == false) {
                    if (peopleFromSource1.get(i).properties.minutesStayed > myModelRoot.ABM.measureHolder.pTAVSPMeasure.get(0).durationOfMeet && peopleFromSource1.get(i).isPolledPTAVSP == false) {
                        myModelRoot.ABM.measureHolder.pTAVSPMeasure.get(0).freqsCoVisit = myModelRoot.ABM.measureHolder.pTAVSPMeasure.get(0).freqsCoVisit + 1;
//                        System.out.println("IMPIMP");
                        peopleFromSource1.get(i).isPolledPTAVSP = true;
                    }
                    if (peopleFromSource2.get(j).properties.minutesStayed > myModelRoot.ABM.measureHolder.pTAVSPMeasure.get(0).durationOfMeet && peopleFromSource2.get(j).isPolledPTAVSP == false) {
                        myModelRoot.ABM.measureHolder.pTAVSPMeasure.get(0).freqsCoVisit = myModelRoot.ABM.measureHolder.pTAVSPMeasure.get(0).freqsCoVisit + 1;
//                        System.out.println("IMPIMP");
                        peopleFromSource2.get(j).isPolledPTAVSP = true;
                    }
                }
            }
        }
    }

    public void handleAVD(MainModel myModelRoot, PersonProperties properties) {
        for (int i = 0; i < properties.homeRegion.cBGsIDsInvolved.size(); i++) {
            if (myModelRoot.ABM.measureHolder.aDVMeasure.get(0).source.id == properties.homeRegion.cBGsIDsInvolved.get(i)) {
                if (properties.currentPOI.patternsRecord.place.placeKey.equals(myModelRoot.ABM.measureHolder.aDVMeasure.get(0).destination.patternsRecord.placeKey)) {
                    myModelRoot.ABM.measureHolder.aDVMeasure.get(0).numVisits = myModelRoot.ABM.measureHolder.aDVMeasure.get(0).numVisits + 1;
                    myModelRoot.ABM.measureHolder.aDVMeasure.get(0).sumDuration = myModelRoot.ABM.measureHolder.aDVMeasure.get(0).sumDuration + properties.minutesStayed;
                }
            }
        }
    }

    public void handleNOV(MainModel myModelRoot, PersonProperties properties) {
        for (int i = 0; i < properties.homeRegion.cBGsIDsInvolved.size(); i++) {
            if (myModelRoot.ABM.measureHolder.nOVMeasure.get(0).source.id == properties.homeRegion.cBGsIDsInvolved.get(i)) {
                if (properties.currentPOI.patternsRecord.place.placeKey.equals(myModelRoot.ABM.measureHolder.nOVMeasure.get(0).destination.patternsRecord.placeKey)) {
                    myModelRoot.ABM.measureHolder.nOVMeasure.get(0).numberOfVisits = myModelRoot.ABM.measureHolder.nOVMeasure.get(0).numberOfVisits + 1;
                }
            }
        }
    }

    public CensusBlockGroup selectBestCBGByCloseness(Scope scope, POI pOI) {
        if (scope instanceof City) {
            double minDist = Double.MAX_VALUE;
            int counter = 0;
            int minDistIndex = -1;
            for (int i = 0; i < ((City) scope).censusTracts.size(); i++) {
                for (int j = 0; j < ((City) scope).censusTracts.get(i).censusBlocks.size(); j++) {
                    double latDiff = Math.abs(((City) scope).censusTracts.get(i).censusBlocks.get(j).lat - pOI.patternsRecord.poi_cbg_censusBlock.lat);
                    double lonDiff = Math.abs(((City) scope).censusTracts.get(i).censusBlocks.get(j).lon - pOI.patternsRecord.poi_cbg_censusBlock.lon);
                    double dist = Math.sqrt(Math.pow(latDiff, 2) + Math.pow(lonDiff, 2));
                    if (dist < minDist) {
                        minDist = dist;
                        minDistIndex = counter;
                    }
                    counter = counter + 1;
                }
            }
            counter = 0;
            for (int i = 0; i < ((City) scope).censusTracts.size(); i++) {
                for (int j = 0; j < ((City) scope).censusTracts.get(i).censusBlocks.size(); j++) {
                    if (counter == minDistIndex) {
                        return ((City) scope).censusTracts.get(i).censusBlocks.get(j);
                    }
                    counter = counter + 1;
                }
            }
        }
        return null;
    }

    public CensusBlockGroup selectBestCBGWithExceptionsByCloseness(Scope scope, POI pOI, ArrayList<Long> exceptions) {
        if (scope instanceof City) {
            double minDist = Double.MAX_VALUE;
            int counter = 0;
            int minDistIndex = -1;
            for (int i = 0; i < ((City) scope).censusTracts.size(); i++) {
                for (int j = 0; j < ((City) scope).censusTracts.get(i).censusBlocks.size(); j++) {
                    for (int m = 0; m < exceptions.size(); m++) {
                        if (((City) scope).censusTracts.get(i).censusBlocks.get(j).id != exceptions.get(m)) {
                            double latDiff = Math.abs(((City) scope).censusTracts.get(i).censusBlocks.get(j).lat - pOI.patternsRecord.poi_cbg_censusBlock.lat);
                            double lonDiff = Math.abs(((City) scope).censusTracts.get(i).censusBlocks.get(j).lon - pOI.patternsRecord.poi_cbg_censusBlock.lon);
                            double dist = Math.sqrt(Math.pow(latDiff, 2) + Math.pow(lonDiff, 2));
                            if (dist < minDist) {
                                minDist = dist;
                                minDistIndex = counter;
                            }
                        }
                    }
                    counter = counter + 1;
                }
            }
            counter = 0;
            for (int i = 0; i < ((City) scope).censusTracts.size(); i++) {
                for (int j = 0; j < ((City) scope).censusTracts.get(i).censusBlocks.size(); j++) {
                    if (counter == minDistIndex) {
                        return ((City) scope).censusTracts.get(i).censusBlocks.get(j);
                    }
                    counter = counter + 1;
                }
            }
        }
        return null;
    }

    public CensusBlockGroup selectBestCBGByPop(Scope scope) {
        if (scope instanceof City) {
            int counter = 0;
            int bestCBGIndex = -1;
            int maxPop = -1;
            for (int i = 0; i < ((City) scope).censusTracts.size(); i++) {
                for (int j = 0; j < ((City) scope).censusTracts.get(i).censusBlocks.size(); j++) {
                    if (((City) scope).censusTracts.get(i).censusBlocks.get(j).population > maxPop) {
                        maxPop = ((City) scope).censusTracts.get(i).censusBlocks.get(j).population;
                        bestCBGIndex = counter;
                    }
                    counter = counter + 1;
                }
            }
            counter = 0;
            for (int i = 0; i < ((City) scope).censusTracts.size(); i++) {
                for (int j = 0; j < ((City) scope).censusTracts.get(i).censusBlocks.size(); j++) {
                    if (counter == bestCBGIndex) {
                        return ((City) scope).censusTracts.get(i).censusBlocks.get(j);
                    }
                    counter = counter + 1;
                }
            }
        }
        return null;
    }

    public CensusBlockGroup selectBestCBGWithExceptionsByPop(Scope scope, ArrayList<Long> exceptions) {
        if (scope instanceof City) {
            int counter = 0;
            int bestCBGIndex = -1;
            int maxPop = -1;
            for (int i = 0; i < ((City) scope).censusTracts.size(); i++) {
                for (int j = 0; j < ((City) scope).censusTracts.get(i).censusBlocks.size(); j++) {
                    for (int m = 0; m < exceptions.size(); m++) {
                        if (((City) scope).censusTracts.get(i).censusBlocks.get(j).id != exceptions.get(m)) {
                            if (((City) scope).censusTracts.get(i).censusBlocks.get(j).population > maxPop) {
                                maxPop = ((City) scope).censusTracts.get(i).censusBlocks.get(j).population;
                                bestCBGIndex = counter;
                            }
                        }
                    }
                    counter = counter + 1;
                }
            }
            counter = 0;
            for (int i = 0; i < ((City) scope).censusTracts.size(); i++) {
                for (int j = 0; j < ((City) scope).censusTracts.get(i).censusBlocks.size(); j++) {
                    if (counter == bestCBGIndex) {
                        return ((City) scope).censusTracts.get(i).censusBlocks.get(j);
                    }
                    counter = counter + 1;
                }
            }
        }
        return null;
    }

    public void writeReports(String filePath) {
        aDVMeasure.get(0).averageDurationOfVisits = aDVMeasure.get(0).sumDuration / aDVMeasure.get(0).numVisits;
        pTAVSPMeasure.get(0).prob=pTAVSPMeasure.get(0).freqsCoVisit/pTAVSPMeasure.get(0).freqsNoCoVisit;
        writeAVD(filePath);
        writeNOV(filePath);
        writePTAVSP(filePath);
    }

    public void writeAVD(String filePath) {
        ArrayList<String[]> rows = new ArrayList();
        String[] header = new String[3];
        header[0] = "POI";
        header[1] = "Source CBG";
        header[2] = "Average duration of visits";
        rows.add(header);
        String[] row = new String[3];
        row[0] = String.valueOf(aDVMeasure.get(0).destination.patternsRecord.placeKey);
        row[1] = String.valueOf(aDVMeasure.get(0).source.id);
        row[2] = String.valueOf(aDVMeasure.get(0).averageDurationOfVisits);
        rows.add(row);

//        CsvWriter writer = new CsvWriter();
        try {
//            writer.write(new File(filePath + "AVD.csv"), Charset.forName("US-ASCII"), rows);
            
            CSVWriter writer = new CSVWriter(new FileWriter(filePath + "AVD.csv"));
            writer.writeAll(rows);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeNOV(String filePath) {
        ArrayList<String[]> rows = new ArrayList();
        String[] header = new String[3];
        header[0] = "POI";
        header[1] = "Source CBG";
        header[2] = "Number of visits";
        rows.add(header);
        String[] row = new String[3];
        row[0] = String.valueOf(nOVMeasure.get(0).destination.patternsRecord.placeKey);
        row[1] = String.valueOf(nOVMeasure.get(0).source.id);
        row[2] = String.valueOf(nOVMeasure.get(0).numberOfVisits);
        rows.add(row);

//        CsvWriter writer = new CsvWriter();
        try {
//            writer.write(new File(filePath + "NOV.csv"), Charset.forName("US-ASCII"), rows);
            
            CSVWriter writer = new CSVWriter(new FileWriter(filePath + "NOV.csv"));
            writer.writeAll(rows);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writePTAVSP(String filePath) {
        ArrayList<String[]> rows = new ArrayList();
        String[] header = new String[7];
        header[0] = "POI";
        header[1] = "Source CBG1";
        header[2] = "Source CBG2";
        header[3] = "Duration of meet";
        header[4] = "Visits no meet";
        header[5] = "Visits meet";
        header[6] = "Visits meet prob";
        rows.add(header);
        String[] row = new String[7];
        row[0] = String.valueOf(pTAVSPMeasure.get(0).destination.patternsRecord.placeKey);
        row[1] = String.valueOf(pTAVSPMeasure.get(0).source1.id);
        row[2] = String.valueOf(pTAVSPMeasure.get(0).source2.id);
        row[3] = String.valueOf(pTAVSPMeasure.get(0).durationOfMeet);
        row[4] = String.valueOf(pTAVSPMeasure.get(0).freqsNoCoVisit);
        row[5] = String.valueOf(pTAVSPMeasure.get(0).freqsCoVisit);
        row[6] = String.valueOf(pTAVSPMeasure.get(0).prob);
        rows.add(row);

//        CsvWriter writer = new CsvWriter();
        try {
//            writer.write(new File(filePath + "PTAVSP.csv"), Charset.forName("US-ASCII"), rows);
            
            CSVWriter writer = new CSVWriter(new FileWriter(filePath + "PTAVSP.csv"));
            writer.writeAll(rows);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(RootArtificial.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
