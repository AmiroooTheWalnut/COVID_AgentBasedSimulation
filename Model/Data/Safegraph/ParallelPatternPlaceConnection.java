/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import de.siegmar.fastcsv.reader.CsvContainer;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class ParallelPatternPlaceConnection extends ParallelProcessor {

    Patterns patterns;
    SafegraphPlaces places;
    AllGISData allGISData;

    public ParallelPatternPlaceConnection(int threadIndex, Object parent, Patterns passed_patterns, SafegraphPlaces passed_places, AllGISData passed_allGISData, int startIndex, int endIndex) {
        super(parent, null, startIndex, endIndex);
        patterns = passed_patterns;
        places = passed_places;
        allGISData = passed_allGISData;
        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread: " + threadIndex + " startIndex: " + myStartIndex);
                System.out.println("Thread: " + threadIndex + " endIndex: " + myEndIndex);
                int counter = 0;
                int largerCounter = 0;
                int counterInterval = 100000;
                int lastValidIndex = 0;
                for (int i = myStartIndex; i < myEndIndex; i++) {
                    for (int j = 0; j < places.placesRecords.size(); j++) {
                        int plusSign = -1;
                        int minusSign = -1;
                        if (i + j + lastValidIndex< places.placesRecords.size()) {
                            plusSign = i + j + lastValidIndex;
                        }
                        if (i - j + lastValidIndex > -1) {
                            minusSign = i - j + lastValidIndex;
                        }
                        if (plusSign > -1) {
                            if (patterns.patternRecords.get(i).placeKey.equals(places.placesRecords.get(plusSign).placeKey)) {
                                CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).poi_cbg);
                                lastValidIndex = lastValidIndex + j;
                                if (temp == null) {
                                    System.out.println("CENSUS BLOCK GROUP NOT FOUND!");
                                    break;
                                }
                                places.placesRecords.get(plusSign).censusBlock = temp;
                                break;
                            }
                        }
                        if (minusSign > -1) {
                            if (patterns.patternRecords.get(i).placeKey.equals(places.placesRecords.get(minusSign).placeKey)) {
                                CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).poi_cbg);
                                lastValidIndex = lastValidIndex - j;
                                if (temp == null) {
                                    System.out.println("CENSUS BLOCK GROUP NOT FOUND!");
                                    break;
                                }
                                places.placesRecords.get(minusSign).censusBlock = temp;
                                break;
                            }
                        }
                    }
                    if (patterns.patternRecords.get(i).visitor_daytime_cbgs != null) {
                        for (int k = 0; k < patterns.patternRecords.get(i).visitor_daytime_cbgs.size(); k++) {
                            CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).visitor_daytime_cbgs.get(k).key);
                            patterns.patternRecords.get(i).visitor_daytime_cbgs_place.add(new CensusBlockGroupIntegerTuple(temp, patterns.patternRecords.get(i).visitor_daytime_cbgs.get(k).value));
                        }
                    }
                    if (patterns.patternRecords.get(i).visitor_home_cbgs != null) {
                        for (int k = 0; k < patterns.patternRecords.get(i).visitor_home_cbgs.size(); k++) {
                            CensusBlockGroup temp = allGISData.findCensusBlockGroup(patterns.patternRecords.get(i).visitor_home_cbgs.get(k).key);
                            patterns.patternRecords.get(i).visitor_home_cbgs_place.add(new CensusBlockGroupIntegerTuple(temp, patterns.patternRecords.get(i).visitor_home_cbgs.get(k).value));
                        }
                    }
                    counter = counter + 1;
                    if (counter > counterInterval) {
                        largerCounter = largerCounter + 1;
                        counter = 0;
                        System.out.println("Num patterns processed: " + largerCounter * counterInterval);
                    }
                }
            }
        });

    }

}
