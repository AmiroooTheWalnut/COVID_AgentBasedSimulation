/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvRow;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public class ParallelSafegraphParser extends ParallelProcessor {
    
    public ArrayList<SafegraphPlace> records;
    public Categories categories;
    public Brands brands;
    CsvContainer myData;
    int myThreadIndex;

    public ParallelSafegraphParser(int threadIndex,ArrayList<SafegraphPlace> parent, CsvContainer data, int startIndex, int endIndex) {
        super(parent, data, startIndex, endIndex);
        records=new ArrayList();
        categories=new Categories();
        brands=new Brands();
        myThreadIndex=threadIndex;
        myParent = parent;
        myData = new CsvContainer(data.getHeader(),data.getRows());
        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(myStartIndex);
                System.out.println(myEndIndex);
                ArrayList<SafegraphPlace> localRecords=new ArrayList();
                int counter=0;
                int largerCounter=0;
                int counterInterval=1000;
                for (int i = myStartIndex; i < myEndIndex; i++) {
                    CsvRow row = myData.getRow(i);
                    
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
                    
                    localRecords.add(safegraphPlaceProcessed);
                    counter=counter+1;
                    if(counter>counterInterval){
                        largerCounter=largerCounter+1;
                        counter=0;
                        System.out.println("Thread number: "+myThreadIndex+" num rows read: "+largerCounter*counterInterval);
                    }
                }
                records.addAll(localRecords);
            }
        });
    }
    
}
