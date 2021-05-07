/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvRow;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class ParallelPatternParser extends ParallelProcessor {
    public ArrayList<PatternsRecordProcessed> records;
    CsvContainer myData;
    int myThreadIndex;

    public ParallelPatternParser(int threadIndex,ArrayList<PatternsRecordProcessed> parent, CsvContainer data, int startIndex, int endIndex) {
        super(parent, data, startIndex, endIndex);
        records=new ArrayList();
        myThreadIndex=threadIndex;
        myParent = parent;
        myData = new CsvContainer(data.getHeader(),data.getRows());
        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread: "+threadIndex+" startIndex: "+myStartIndex);
                System.out.println("Thread: "+threadIndex+" endIndex: "+myEndIndex);
                ArrayList<PatternsRecordProcessed> localRecords=new ArrayList();
                int counter=0;
                int largerCounter=0;
                int counterInterval=1000;
                for (int i = myStartIndex; i < myEndIndex; i++) {
                    CsvRow row = myData.getRow(i);
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
                    localRecords.add(patternsRecordProcessed);
                    counter = counter + 1;
                    if (counter > counterInterval) {
                        largerCounter = largerCounter + 1;
                        counter = 0;
                        System.out.println("Num rows read: " + largerCounter * counterInterval);
                    }
                }
                records.addAll(localRecords);
            }
        });
    }

}
