/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class SafegraphPlaces implements Serializable {

    static final long serialVersionUID = softwareVersion;

    public ArrayList<SafegraphPlace> records;
    public String name;
    public Categories categories;
    public Brands brands;

    public void preprocessMonthCorePlaces(String directoryName, String patternName, boolean isParallel, int numCPU) {
        name = patternName;
        records = new ArrayList();
        categories=new Categories();
        brands=new Brands();
        File directory = new File(directoryName);

        FileFilter binFilesFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(".bin")) {
                    return true;
                }
                return false;
            }
        };
        File[] binFileList = directory.listFiles(binFilesFilter);

        FileFilter cSVFilesFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(".csv")) {
                    return true;
                }
                return false;
            }
        };
        File[] cSVfileList = directory.listFiles(cSVFilesFilter);
        for (int i = 0; i < cSVfileList.length; i++) {
            boolean isRawBinFound = false;
            for (int j = 0; j < binFileList.length; j++) {
                if (binFileList[j].getName().contains(cSVfileList[i].getName())) {
                    isRawBinFound = true;
                    break;
                }
            }
            if (isRawBinFound == false) {
                ArrayList<SafegraphPlace> recordsLocal = readData(cSVfileList[i].getAbsolutePath(), isParallel, numCPU);
                records = recordsLocal;
                Safegraph.saveSafegraphPlacesKryo(directoryName + "/ProcessedData_" + cSVfileList[i].getName(), this);
                records.clear();
            }
        }

        File[] binFileListRecheck = directory.listFiles(binFilesFilter);
        records = new ArrayList();
        for (int i = 0; i < binFileListRecheck.length; i++) {
            SafegraphPlaces safegraphPlaces = Safegraph.loadSafegraphPlacesKryo(binFileListRecheck[i].getPath());
            records.addAll(safegraphPlaces.records);
            safegraphPlaces = null;
            System.out.println("Data read: " + i);
        }
    }

    public ArrayList<SafegraphPlace> readData(String fileName, boolean isParallel, int numCPU) {
        ArrayList<SafegraphPlace> recordsLocal = new ArrayList();
        File patternFile = new File(fileName);
        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(patternFile, StandardCharsets.UTF_8);

            if (isParallel == true) {
                int numProcessors = numCPU;
                if (numProcessors > Runtime.getRuntime().availableProcessors()) {
                    numProcessors = Runtime.getRuntime().availableProcessors();
                }
                ParallelSafegraphParser parallelSafegraphParsers[] = new ParallelSafegraphParser[numProcessors];

                for (int i = 0; i < numProcessors - 1; i++) {
                    parallelSafegraphParsers[i] = new ParallelSafegraphParser(i,recordsLocal, data, (int) Math.floor(i * ((data.getRowCount()) / numProcessors)), (int) Math.floor((i + 1) * ((data.getRowCount()) / numProcessors)));
                }
                parallelSafegraphParsers[numProcessors - 1] = new ParallelSafegraphParser(numProcessors - 1,recordsLocal, data, (int) Math.floor((numProcessors - 1) * ((data.getRowCount()) / numProcessors)), data.getRowCount());

                for (int i = 0; i < numProcessors; i++) {
                    parallelSafegraphParsers[i].myThread.start();
                }
                for (int i = 0; i < numProcessors; i++) {
                    try {
                        parallelSafegraphParsers[i].myThread.join();
                        System.out.println("thread " + i + "finished for records: " + parallelSafegraphParsers[i].myStartIndex + " | " + parallelSafegraphParsers[i].myEndIndex);
                    } catch (InterruptedException ie) {
                        System.out.println(ie.toString());
                    }
                }
                for (int i = 0; i < numProcessors; i++) {
                    recordsLocal.addAll(parallelSafegraphParsers[i].records);
                    brands=parallelSafegraphParsers[i].brands;
                    categories=parallelSafegraphParsers[i].categories;
                }
            } else {
                int counter = 0;
                int largerCounter = 0;
                int counterInterval = 1000;
                for (int i = 0; i < data.getRowCount(); i++) {
                    CsvRow row = data.getRow(i);
                    SafegraphPlace safegraphPlaceProcessed = new SafegraphPlace();
                    String field = row.getField("placekey");
                    if (field.length() > 0) {
                        safegraphPlaceProcessed.placeKey = field;
                    }
                    field = row.getField("latitude");
                    if (field.length() > 0) {
                        safegraphPlaceProcessed.lat = Float.parseFloat(field);
                    }
                    field = row.getField("longitude");
                    if (field.length() > 0) {
                        safegraphPlaceProcessed.lon = Float.parseFloat(field);
                    }
                    field = row.getField("brands");
                    if (field.length() > 0) {
                        String[] brandsStrings = field.split(",");
                        ArrayList<Brand> brandsGenerated=new ArrayList();
                        for (int j = 0; j < brandsStrings.length; j++) {
                            Brand tempBrand=brands.findAndInsertCategory(brandsStrings[j]);
                            brandsGenerated.add(tempBrand);
                        }
                        safegraphPlaceProcessed.brands = brandsGenerated;
                    }
                    field = row.getField("top_category");
                    if (field.length() > 0) {
                        safegraphPlaceProcessed.category=categories.findAndInsertCategory(field);
                    }
                    field = row.getField("naics_code");
                    if (field.length() > 0) {
                        safegraphPlaceProcessed.naics_code=Integer.parseInt(field);
                    }
                    
                    recordsLocal.add(safegraphPlaceProcessed);
                    counter = counter + 1;
                    if (counter > counterInterval) {
                        largerCounter = largerCounter + 1;
                        counter = 0;
                        System.out.println("Num rows read: " + largerCounter * counterInterval);
                    }
                }
            }
            return recordsLocal;
        } catch (IOException ex) {
            Logger.getLogger(Patterns.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
        return null;
    }

}
