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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class Patterns implements Serializable {

    static final long serialVersionUID = softwareVersion;

    public String name;

    public ArrayList<PatternsRecordProcessed> patternRecords;

    public void preprocessMonthPatterns(String directoryName, String patternName, boolean isParallel, int numCPU) {
        name = patternName;
        patternRecords = new ArrayList();
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
                ArrayList<PatternsRecordProcessed> recordsLocal;
                try {
                    recordsLocal = readData(cSVfileList[i].getCanonicalPath(), isParallel, numCPU);
                    patternRecords = recordsLocal;
                    Safegraph.savePatternsKryo(directoryName + "/ProcessedData_" + cSVfileList[i].getName(), this);
                    patternRecords.clear();
                    patternRecords = new ArrayList();
                    System.gc();
                } catch (IOException ex) {
                    Logger.getLogger(Patterns.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        File[] binFileListRecheck = directory.listFiles(binFilesFilter);
        patternRecords = new ArrayList();
        for (int i = 0; i < binFileListRecheck.length; i++) {
            Patterns patterns = Safegraph.loadPatternsKryo(binFileListRecheck[i].getPath());
            patternRecords.addAll(patterns.patternRecords);
            patterns = null;
            System.out.println("Data read: " + i);
        }
        Collections.sort(patternRecords);
        Safegraph.savePatternsKryo(directoryName + "/processedData", this);
        for (int i = 0; i < binFileListRecheck.length; i++) {
            binFileListRecheck[i].delete();
        }
        System.gc();
    }

    public ArrayList<PatternsRecordProcessed> readData(String fileName, boolean isParallel, int numCPU) {
        ArrayList<PatternsRecordProcessed> recordsLocal = new ArrayList();
        File patternFile = new File(fileName);
        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(patternFile, StandardCharsets.UTF_8);
            if (isParallel == true) {
                int numProcessors = numCPU;
//                if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//                    numProcessors = Runtime.getRuntime().availableProcessors();
//                }
                ParallelPatternParser parallelPatternParsers[] = new ParallelPatternParser[numProcessors];

                for (int i = 0; i < numProcessors - 1; i++) {
                    parallelPatternParsers[i] = new ParallelPatternParser(i, recordsLocal, data, (int) Math.floor(i * ((data.getRowCount()) / numProcessors)), (int) Math.floor((i + 1) * ((data.getRowCount()) / numProcessors)));
                }
                parallelPatternParsers[numProcessors - 1] = new ParallelPatternParser(numProcessors - 1, recordsLocal, data, (int) Math.floor((numProcessors - 1) * ((data.getRowCount()) / numProcessors)), data.getRowCount());

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
                int counter = 0;
                int largerCounter = 0;
                int counterInterval = 1000;
                for (int i = 0; i < data.getRowCount(); i++) {
                    CsvRow row = data.getRow(i);
                    PatternsRecordProcessed patternsRecordProcessed = new PatternsRecordProcessed();
                    String field = row.getField("placekey");
                    if (field == null) {
                        field = row.getField("safegraph_place_id");
                        if (field.length() > 0) {
                            patternsRecordProcessed.placeKey = field;
                        }
                    } else {
                        if (field.length() > 0) {
                            patternsRecordProcessed.placeKey = field;
                        }
                    }
                    field = row.getField("poi_cbg");
                    if (field.length() > 0) {
                        patternsRecordProcessed.poi_cbg = Long.parseLong(field);
                    }
                    field = row.getField("date_range_start");
                    if (field.length() > 0) {
                        int startOffset = Integer.parseInt(field.substring(19, 21));
                        DateTimeFormatter startFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                                .withZone(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(startOffset)));
                        LocalDateTime startDate = LocalDateTime.parse(field.substring(0, 19), startFormatter);
                        ZonedDateTime zonedStartDate = startDate.atZone(ZoneOffset.ofHours(startOffset));
                        patternsRecordProcessed.date_range_start = zonedStartDate;
                    }

                    field = row.getField("date_range_end");
                    if (field.length() > 0) {
                        int endOffset = Integer.parseInt(field.substring(19, 21));
                        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                                .withZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(endOffset)));
                        LocalDateTime endDate = LocalDateTime.parse(field.substring(0, 19), endFormatter);
                        ZonedDateTime zonedEndDate = endDate.atZone(ZoneOffset.ofHours(endOffset));
                        patternsRecordProcessed.date_range_end = zonedEndDate;
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
                            ArrayList<LongIntTuple> temp = new ArrayList();
                            for (int k = 0; k < names.length(); k++) {
                                temp.add(new LongIntTuple(Long.parseLong(names.getString(k)), object.getInt(names.getString(k))));
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
                            ArrayList<LongIntTuple> temp = new ArrayList();
                            for (int k = 0; k < names.length(); k++) {
                                temp.add(new LongIntTuple(Long.parseLong(names.getString(k)), object.getInt(names.getString(k))));
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
                            ArrayList<DwellTime> dwellData=new ArrayList();
                            for (int k = 0; k < names.length(); k++) {
                                DwellTime temp = new DwellTime();
                                temp.number = object.getInt(names.getString(k));
                                temp.dwellDuration = DwellTime.getDwellDuration(names.getString(k));
                                dwellData.add(temp);
                            }
                            patternsRecordProcessed.bucketed_dwell_times=dwellData;
                            
//                            System.out.println("!!!");
                            
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
