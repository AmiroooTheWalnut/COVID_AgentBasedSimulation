package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlock;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author user
 */
public class Patterns implements Serializable {

    static final long serialVersionUID = softwareVersion;

    String[] standardHeaderNames = new String[]{"placekey", "safegraph_place_id", "parent_placekey",
        "parent_safegraph_place_id", "location_name", "street_address", "city", "region", "postal_code",
        "safegraph_brand_ids", "brands", "date_range_start", "date_range_end", "raw_visit_counts",
        "raw_visitor_counts", "visits_by_day", "poi_cbg", "visitor_home_cbgs", "visitor_daytime_cbgs",
        "visitor_country_of_origin", "distance_from_home", "median_dwell", "bucketed_dwell_times",
        "related_same_day_brand", "related_same_month_brand", "popularity_by_hour", "popularity_by_day",
        "device_type", "carrier_name"};

    public String name;

    public ArrayList<PatternsRecordProcessed> records;
    public transient ArrayList<PatternsRecordProcessed> recordsProcessed;

    public void readMultiplePatternData(String directoryName, String patternName, boolean isParallel, int numCPU) {
        name = patternName;
        records = new ArrayList();
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
                ArrayList<PatternsRecordProcessed> recordsLocal = readData(cSVfileList[i].getAbsolutePath(), isParallel, numCPU);
                records = recordsLocal;
                Safegraph.savePatternsKryo(directoryName + "/ProcessedData_" + cSVfileList[i].getName(), this);
                records.clear();
            }
        }

        File[] binFileListRecheck = directory.listFiles(binFilesFilter);
        records = new ArrayList();
        for (int i = 0; i < binFileListRecheck.length; i++) {
            Patterns patterns = Safegraph.loadPatternsKryo(binFileListRecheck[i].getPath());
            records.addAll(patterns.records);
            patterns = null;
            System.out.println("Data read: " + i);
        }

    }

    public ArrayList<PatternsRecordProcessed> readData(String fileName, boolean isParallel, int numCPU) {
        ArrayList<PatternsRecordProcessed> recordsLocal = new ArrayList();
        File patternFile = new File(fileName);
        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(patternFile, StandardCharsets.UTF_8);
            PatternsRecordProcessed tempPatternRow = new PatternsRecordProcessed();
            List<String> header = data.getHeader();

            if (isParallel == true) {
                int numProcessors = numCPU;
                if (numProcessors > Runtime.getRuntime().availableProcessors()) {
                    numProcessors = Runtime.getRuntime().availableProcessors();
                }
                ParallelPatternParser parallelPatternParsers[] = new ParallelPatternParser[numProcessors];

                for (int i = 0; i < numProcessors - 1; i++) {
                    parallelPatternParsers[i] = new ParallelPatternParser(i,recordsLocal, data, (int) Math.floor(i * ((data.getRowCount()) / numProcessors)), (int) Math.floor((i + 1) * ((data.getRowCount()) / numProcessors)));
                }
                parallelPatternParsers[numProcessors - 1] = new ParallelPatternParser(numProcessors - 1,recordsLocal, data, (int) Math.floor((numProcessors - 1) * ((data.getRowCount()) / numProcessors)), data.getRowCount());

                for (int i = 0; i < numProcessors; i++) {
                    parallelPatternParsers[i].myThread.start();
                }
                for (int i = 0; i < numProcessors; i++) {
                    try {
                        parallelPatternParsers[i].myThread.join();
                        System.out.println("thread " + i + "finished for records: " + parallelPatternParsers[i].myStartIndex + " | " + parallelPatternParsers[i].myEndIndex);
                    } catch (InterruptedException ie) {
                        System.out.println(ie.toString());
                    }
                }
                for (int i = 0; i < numProcessors; i++) {
                    recordsLocal.addAll(parallelPatternParsers[i].records);
                }
            } else {
                String[] safegraphPatternFieldNames = new String[tempPatternRow.getClass().getFields().length];
                for (int i = 0; i < safegraphPatternFieldNames.length; i++) {
                    safegraphPatternFieldNames[i] = tempPatternRow.getClass().getFields()[i].getName();
                }
                String[] safegraphPatternFieldTypeNames = new String[tempPatternRow.getClass().getFields().length];
                for (int i = 0; i < safegraphPatternFieldTypeNames.length; i++) {
                    safegraphPatternFieldTypeNames[i] = tempPatternRow.getClass().getFields()[i].getType().getName();
                }
                int counter = 0;
                int largerCounter = 0;
                int counterInterval = 1000;
                for (int i = 0; i < data.getRowCount(); i++) {
                    CsvRow row = data.getRow(i);
                    PatternsRecordProcessed patternsRecordProcessed = new PatternsRecordProcessed();
                    String field = row.getField("date_range_start");
                    if (field.length() > 0) {
                        int startOffset = Integer.parseInt(field.substring(19, 21));
                        DateTimeFormatter startFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                                .withZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(startOffset)));
                        LocalDateTime startDate = LocalDateTime.parse(field.substring(0, 19), startFormatter);
                        patternsRecordProcessed.date_range_start = startDate;
                    }

                    field = row.getField("date_range_end");
                    if (field.length() > 0) {
                        int endOffset = Integer.parseInt(field.substring(19, 21));
                        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                                .withZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(endOffset)));
                        LocalDateTime endDate = LocalDateTime.parse(field.substring(0, 19), endFormatter);
                        patternsRecordProcessed.date_range_end = endDate;
                    }

                    field = row.getField("raw_visit_counts");
                    if (field.length() > 0) {
                        patternsRecordProcessed.raw_visit_counts = Integer.parseInt(field);
                    }

                    field = row.getField("raw_visitor_counts");
                    if (field.length() > 0) {
                        patternsRecordProcessed.raw_visitor_counts = Integer.parseInt(field);
                    }

                    field = row.getField("visits_by_day");
                    if (field.length() > 0) {
                        field = field.substring(1, field.length() - 1);
                        String[] visits_by_dayStrArray = field.split(",", -1);
                        int[] visits_by_day = new int[visits_by_dayStrArray.length];
                        for (int m = 0; m < visits_by_dayStrArray.length; m++) {
                            visits_by_day[m] = Integer.valueOf(visits_by_dayStrArray[m]);
                        }
                        patternsRecordProcessed.visits_by_day = visits_by_day;
                    }

                    field = row.getField("visitor_home_cbgs");
                    if (field.length() > 0) {
                        try {
                            JSONObject object = new JSONObject(field);
                            JSONArray names = object.names();
                            HashMap<Long, Integer> temp = new HashMap();
                            for (int k = 0; k < names.length(); k++) {
                                temp.put(Long.parseLong(names.getString(k)), object.getInt(names.getString(k)));
                            }
                            patternsRecordProcessed.visitor_home_cbgs = temp;
                        } catch (Exception ex) {
//                            System.out.println(ex.getMessage());
                        }
                    }

                    field = row.getField("visitor_daytime_cbgs");
                    if (field.length() > 0) {
                        try {
                            JSONObject object = new JSONObject(field);
                            JSONArray names = object.names();
                            HashMap<Long, Integer> temp = new HashMap();
                            for (int k = 0; k < names.length(); k++) {
                                temp.put(Long.parseLong(names.getString(k)), object.getInt(names.getString(k)));
                            }
                            patternsRecordProcessed.visitor_daytime_cbgs = temp;
                        } catch (Exception ex) {
//                            System.out.println(ex.getMessage());
                        }
                    }

                    field = row.getField("distance_from_home");
                    if (field.length() > 0) {
                        patternsRecordProcessed.distance_from_home = Integer.parseInt(field);
                    }
                    field = row.getField("median_dwell");
                    if (field.length() > 0) {
                        patternsRecordProcessed.median_dwell = Double.parseDouble(field);
                    }
                    if (field.length() > 0) {
                        field = row.getField("bucketed_dwell_times");
                    }
                    if (field.length() > 0) {
                        try {
                            JSONObject object = new JSONObject(field);
                            JSONArray names = object.names();
                            for (int k = 0; k < names.length(); k++) {
                                DwellTime temp = new DwellTime();
                                temp.number = object.getInt(names.getString(k));
                                temp.dwellDuration = DwellTime.getDwellDuration(names.getString(k));
                            }
                        } catch (Exception ex) {
//                            System.out.println(ex.getMessage());
                        }
                    }
                    field = row.getField("popularity_by_hour");
                    if (field.length() > 0) {
                        try {
                            field = field.substring(1, field.length() - 1);
                            String[] strArray = field.split(",", -1);
                            int[] popularity_by_hour = new int[strArray.length];
                            for (int m = 0; m < strArray.length; m++) {
                                popularity_by_hour[m] = Integer.valueOf(strArray[m]);
                            }
                            patternsRecordProcessed.popularity_by_hour = popularity_by_hour;
                        } catch (Exception ex) {
//                            System.out.println(ex.getMessage());
                        }
                    }
                    field = row.getField("popularity_by_day");
                    if (field.length() > 0) {
                        try {
                            JSONObject object = new JSONObject(field);
                            JSONArray names = object.names();
                            HashMap<Byte, Integer> temp = new HashMap();
                            for (int k = 0; k < names.length(); k++) {
                                String str = names.getString(k);
                                byte day = -1;
                                if (str.equals("Monday")) {
                                    day = 0;
                                } else if (str.equals("Tuesday")) {
                                    day = 1;
                                } else if (str.equals("Wednesday")) {
                                    day = 2;
                                } else if (str.equals("Thursday")) {
                                    day = 3;
                                } else if (str.equals("Friday")) {
                                    day = 4;
                                } else if (str.equals("Saturday")) {
                                    day = 5;
                                } else if (str.equals("Sunday")) {
                                    day = 6;
                                }
                                temp.put(day, object.getInt(names.getString(k)));
                            }
                            patternsRecordProcessed.popularity_by_day = temp;
                        } catch (Exception ex) {
//                            System.out.println(ex.getMessage());
                        }
                    }

//                    List<String> oneRow = data.getRow(i).getFields();
//                    PatternsRecordProcessed patternsRecordProcessed = new PatternsRecordProcessed();
//                    int columnCounter = 0;
//                    for (int j = 0; j < oneRow.size(); j++) {
//                        for (int k = 0; k < safegraphPatternFieldNames.length; k++) {
//                            if (header.get(j).equals(safegraphPatternFieldNames[k])) {
//                                if (oneRow.get(columnCounter).length() > 0) {
//                                    switch (safegraphPatternFieldTypeNames[k]) {
//                                        case "java.lang.String":
//                                            try {
//                                                patternsRecordProcessed.getClass().getFields()[k].set(patternsRecordProcessed, oneRow.get(columnCounter));
//                                                columnCounter = columnCounter + 1;
//                                            } catch (Exception ex) {
//                                                System.out.println(oneRow.get(columnCounter));
//                                                columnCounter = columnCounter + 1;
//                                                System.out.println(ex.getMessage());
//                                            }
//                                            break;
//                                        case "java.util.ArrayList": {
//                                            try {
//                                                ArrayList temp = new ArrayList(Arrays.asList(oneRow.get(columnCounter).split(",")));
//                                                patternsRecordProcessed.getClass().getFields()[k].set(patternsRecordProcessed, temp);
//                                                columnCounter = columnCounter + 1;
//                                            } catch (Exception ex) {
//                                                System.out.println(oneRow.get(columnCounter));
//                                                columnCounter = columnCounter + 1;
//                                                System.out.println(ex.getMessage());
//                                            }
//                                            break;
//                                        }
//                                        case "int":
//                                            try {
//                                                patternsRecordProcessed.getClass().getFields()[k].set(patternsRecordProcessed, Integer.parseInt(oneRow.get(columnCounter)));
//                                                columnCounter = columnCounter + 1;
//                                            } catch (Exception ex) {
//                                                System.out.println(oneRow.get(columnCounter));
//                                                columnCounter = columnCounter + 1;
//                                                System.out.println(ex.getMessage());
//                                            }
//                                            break;
//                                        case "double":
//                                            try {
//                                                patternsRecordProcessed.getClass().getFields()[k].set(patternsRecordProcessed, Double.parseDouble(oneRow.get(columnCounter)));
//                                                columnCounter = columnCounter + 1;
//                                            } catch (Exception ex) {
//                                                System.out.println(oneRow.get(columnCounter));
//                                                columnCounter = columnCounter + 1;
//                                                System.out.println(ex.getMessage());
//                                            }
//                                            break;
//                                        case "[I":
//                                            try {
//                                                String visits_by_dayStr = oneRow.get(columnCounter);
//                                                visits_by_dayStr = visits_by_dayStr.substring(1, visits_by_dayStr.length() - 1);
//                                                String[] visits_by_dayStrArray = visits_by_dayStr.split(",", -1);
//                                                int[] visits_by_day = new int[visits_by_dayStrArray.length];
//                                                for (int m = 0; m < visits_by_dayStrArray.length; m++) {
//                                                    visits_by_day[m] = Integer.valueOf(visits_by_dayStrArray[m]);
//                                                }
//                                                patternsRecordProcessed.getClass().getFields()[k].set(patternsRecordProcessed, visits_by_day);
//                                                columnCounter = columnCounter + 1;
//                                            } catch (Exception ex) {
//                                                System.out.println(oneRow.get(columnCounter));
//                                                columnCounter = columnCounter + 1;
//                                                System.out.println(ex.getMessage());
//                                            }
//                                            break;
//                                        case "java.util.HashMap": {
//                                            try {
//                                                JSONObject object = new JSONObject(oneRow.get(columnCounter));
//                                                HashMap<String, Object> temp = (HashMap<String, Object>) object.toMap();
//                                                patternsRecordProcessed.getClass().getFields()[k].set(patternsRecordProcessed, temp);
//                                                columnCounter = columnCounter + 1;
//                                            } catch (Exception ex) {
//                                                System.out.println(oneRow.get(columnCounter));
//                                                columnCounter = columnCounter + 1;
//                                                System.out.println(ex.getMessage());
//                                            }
//                                            break;
//                                        }
//                                        default:
//                                            columnCounter = columnCounter + 1;
//                                            break;
//                                    }
//                                } else {
//                                    columnCounter = columnCounter + 1;
//                                }
//                                break;
//                            }
//                        }
//                    }
                    recordsLocal.add(patternsRecordProcessed);
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
