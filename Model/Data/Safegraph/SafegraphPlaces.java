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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Content;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.jdom2.Element;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class SafegraphPlaces implements Serializable {

    static final long serialVersionUID = softwareVersion;

    public ArrayList<SafegraphPlace> placesRecords;
    public String name;
    public Categories categories;
    public Brands brands;

    public void preprocessMonthCorePlaces(String directoryName, String patternName, boolean isParallel, int numCPU) {
        name = patternName;
        placesRecords = new ArrayList();
        categories = new Categories();
        brands = new Brands();
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
                ArrayList<SafegraphPlace> recordsLocal;
                try {
                    recordsLocal = readData(cSVfileList[i].getCanonicalPath(), isParallel, numCPU);
                    placesRecords = recordsLocal;
                    Safegraph.saveSafegraphPlacesKryo(directoryName + "/ProcessedData_" + cSVfileList[i].getName(), this);
                    placesRecords.clear();
                } catch (IOException ex) {
                    Logger.getLogger(SafegraphPlaces.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        File[] binFileListRecheck = directory.listFiles(binFilesFilter);
        placesRecords = new ArrayList();
        for (int i = 0; i < binFileListRecheck.length; i++) {
            SafegraphPlaces safegraphPlaces = Safegraph.loadSafegraphPlacesKryo(binFileListRecheck[i].getPath());
            placesRecords.addAll(safegraphPlaces.placesRecords);
            safegraphPlaces = null;
            System.out.println("Data read: " + i);
        }
//        for(int i=0;i<20;i++){
//            System.out.println(records.get(i).placeKey);
//        }
//        System.out.println("***");
        Collections.sort(placesRecords);
        Safegraph.saveSafegraphPlacesKryo(directoryName + "/processedData", this);
        for (int i = 0; i < binFileListRecheck.length; i++) {
            binFileListRecheck[i].delete();
        }
//        for(int i=0;i<20;i++){
//            System.out.println(records.get(i).placeKey);
//        }
//        System.out.println("$$$");
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
//                if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//                    numProcessors = Runtime.getRuntime().availableProcessors();
//                }
                ParallelSafegraphParser parallelSafegraphParsers[] = new ParallelSafegraphParser[numProcessors];

                for (int i = 0; i < numProcessors - 1; i++) {
                    parallelSafegraphParsers[i] = new ParallelSafegraphParser(i, recordsLocal, data, (int) Math.floor(i * ((data.getRowCount()) / numProcessors)), (int) Math.floor((i + 1) * ((data.getRowCount()) / numProcessors)));
                }
                parallelSafegraphParsers[numProcessors - 1] = new ParallelSafegraphParser(numProcessors - 1, recordsLocal, data, (int) Math.floor((numProcessors - 1) * ((data.getRowCount()) / numProcessors)), data.getRowCount());

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
                    brands = parallelSafegraphParsers[i].brands;
                    categories = parallelSafegraphParsers[i].categories;
                }
            } else {
                int counter = 0;
                int largerCounter = 0;
                int counterInterval = 1000;
                for (int i = 0; i < data.getRowCount(); i++) {
                    CsvRow row = data.getRow(i);
                    SafegraphPlace safegraphPlaceProcessed = new SafegraphPlace();
                    String field = row.getField("placekey");

                    if (field == null) {
                        field = row.getField("safegraph_place_id");
                        if (field.length() > 0) {
                            safegraphPlaceProcessed.placeKey = field;
                        }
                    } else {
                        if (field.length() > 0) {
                            safegraphPlaceProcessed.placeKey = field;
                        }
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
                        ArrayList<Brand> brandsGenerated = new ArrayList();
                        for (int j = 0; j < brandsStrings.length; j++) {
                            Brand tempBrand = brands.findAndInsertCategory(brandsStrings[j]);
                            brandsGenerated.add(tempBrand);
                        }
                        safegraphPlaceProcessed.brands = brandsGenerated;
                    }
                    field = row.getField("top_category");
                    if (field.length() > 0) {
                        safegraphPlaceProcessed.category = categories.findAndInsertCategory(field);
                    }
                    field = row.getField("naics_code");
                    if (field.length() > 0) {
                        safegraphPlaceProcessed.naics_code = Integer.parseInt(field);
                    }

                    //float result = getBuildingAreaOnline(safegraphPlaceProcessed.lat, safegraphPlaceProcessed.lon, false);
                    //safegraphPlaceProcessed.landArea = result;
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

    public static void connectToOSMBuildingAreaLevels(ArrayList<SafegraphPlace> input, int numSamples) {
        int counter = 0;
        int largerCounter = 0;
        int counterInterval = 10;
        int maxIter = Math.min(numSamples, input.size());
        if (maxIter == 0) {
            maxIter = input.size();
        }
        for (int i = 0; i < maxIter; i++) {
            float[] result = getBuildingAreaLevelsOnline(input.get(i).lat, input.get(i).lon, false);
            input.get(i).landArea = result[0];

            input.get(i).buldingLevels = (int) result[1];

            counter = counter + 1;
            if (counter > counterInterval) {
                largerCounter = largerCounter + 1;
                counter = 0;
                System.out.println("Num places area calculated: " + largerCounter * counterInterval);
            }

        }
        double avgArea = 0;
        double stdArea = 0;

        double avgNumLevels = 0;
        double stdNumLevels = 0;

        int avgAreaCounter = 0;
        int avgNumLevelsCounter = 0;

        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).landArea != -1 && input.get(i).landArea != 0 && !Double.isNaN(input.get(i).landArea)) {
                avgArea = avgArea + input.get(i).landArea;
                avgAreaCounter = avgAreaCounter + 1;
            }
            if (input.get(i).buldingLevels != -1 && input.get(i).buldingLevels != 0 && !Double.isNaN(input.get(i).buldingLevels)) {
                avgNumLevels = avgNumLevels + input.get(i).buldingLevels;
                avgNumLevelsCounter = avgNumLevelsCounter + 1;
            }
        }
        avgArea = avgArea / (double) avgAreaCounter;
        avgNumLevels = avgNumLevels / (double) avgNumLevelsCounter;
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).landArea != -1 && input.get(i).landArea != 0 && !Double.isNaN(input.get(i).landArea)) {
                stdArea = stdArea + Math.pow(input.get(i).landArea - avgArea, 2);
            }
            if (input.get(i).buldingLevels != -1 && input.get(i).buldingLevels != 0 && !Double.isNaN(input.get(i).buldingLevels)) {
                stdNumLevels = stdNumLevels + Math.pow(input.get(i).buldingLevels - avgNumLevels, 2);
            }
        }
        stdArea = stdArea / (avgAreaCounter - 1);
        stdArea = Math.sqrt(stdArea);

        stdNumLevels = stdNumLevels / (avgNumLevelsCounter - 1);
        stdNumLevels = Math.sqrt(stdNumLevels);
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).landArea == -1 || input.get(i).landArea == 0 || Double.isNaN(input.get(i).landArea)) {
                input.get(i).landArea = (float) (avgArea + Math.random() * 0.5 * stdArea);
            }
            if (input.get(i).buldingLevels == -1 || input.get(i).buldingLevels == 0 || Double.isNaN(input.get(i).buldingLevels)) {
                if (avgNumLevels > 0 && stdNumLevels > 0) {
                    input.get(i).buldingLevels = (int) (Math.max(avgNumLevels + (Math.random() - 0.7) * stdNumLevels, 1));
                } else {
                    input.get(i).buldingLevels = 1;
                }
            }
        }

        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).landArea == -1 || input.get(i).landArea == 0 || Double.isNaN(input.get(i).landArea)) {
                System.out.println("!"+input.get(i).landArea);
            }
            if (input.get(i).buldingLevels == -1 || input.get(i).buldingLevels == 0 || Double.isNaN(input.get(i).buldingLevels)) {
                System.out.println("!!"+input.get(i).buldingLevels);
            }
        }

        System.out.println("FINISHED GETTING BUILDING AREA LEVELS");
    }

    public static float[] getBuildingAreaLevelsOnline(float lat, float lon, boolean isDebug) {
        try {
            RequestResponse ret = findBuildingSize(lat, lon, isDebug);
            if (ret == null) {
                float[] output = new float[2];
                output[0] = -1;
                output[1] = -1;
                return output;
            } else {
                Coordinate[] coords = ret.finalPolygon.getCoordinates();
                float dist = distFrom((float) coords[0].x, (float) coords[0].y, (float) coords[1].x, (float) coords[1].y);
                float latLonDist = (float) Math.sqrt(Math.pow((float) coords[0].x - (float) coords[1].x, 2) + Math.pow((float) coords[0].y - (float) coords[1].y, 2));
                float adjustedArea = (float) ret.finalPolygon.getArea() * ((float) Math.pow(dist / latLonDist, 2));
                float[] output = new float[2];
                output[0] = adjustedArea;
                output[1] = ret.numLevels;
                return output;
            }
        } catch (Exception ex) {
            System.out.println("BUILDING AREA FAILED TO OBTAIN FROM OPENSTREETMAP!");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            float[] output = new float[2];
            output[0] = -1;
            output[1] = -1;
            return output;
        }
    }

    private static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371008.8; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    private static RequestResponse findBuildingSize(float lat, float lon, boolean isDebug) {
        float initSize = 0.002f;
        RequestResponse outputPolygonRes;
        Polygon anyPolygon = null;

        try {
            for (int i = 0; i < 10; i++) {
                if (isDebug == true) {
                    System.out.println("Search for location with search radius: " + initSize);
                }

                ArrayList<Polygon> polygons;
                RequestResponse reponse;
                reponse = requestRegion(initSize, lat, lon);

                if (reponse.needRetry == true) {
                    System.out.println("SERVER IS TIRED! WAITING FOR ONE SECOND!");
                    Thread.sleep(2000);
                }

                polygons = reponse.data;

                if (polygons != null) {
                    if (polygons.size() > 0) {
                        anyPolygon = polygons.get(0);
                    }
                }

                if (reponse.needRetry == false) {
                    if (polygons != null) {
                        Polygon ret = getPolygonContaining(lat, lon, polygons);
                        initSize = initSize * 2;

                        Thread.sleep(10);

                        if (ret != null) {
                            outputPolygonRes = new RequestResponse();
                            outputPolygonRes.finalPolygon = ret;
                            outputPolygonRes.numLevels = reponse.numLevels;
                            if (isDebug == true) {
                                System.out.println("Location polygon found with size: " + initSize);
                            }
                            return outputPolygonRes;
                        } else if (anyPolygon != null) {
                            outputPolygonRes = new RequestResponse();
                            outputPolygonRes.finalPolygon = anyPolygon;
                            outputPolygonRes.numLevels = reponse.numLevels;
                            if (isDebug == true) {
                                System.out.println("Close polygon found with size: " + initSize);
                            }
                            return outputPolygonRes;
                        }
                    }
                }

            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SafegraphPlaces.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private static RequestResponse requestRegion(float size, float lat, float lon) {
        try {
            // Create a neat value object to hold the URL
            URL url;
            String req = "https://overpass.kumi.systems/api/interpreter?data=[bbox];(node;way[building];);out;&bbox=" + (lon - size / 2) + "," + (lat - size / 2) + "," + (lon + size / 2) + "," + (lat + size / 2);
            url = new URL(req);

            // Open a connection(?) on the URL(??) and cast the response(???)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Now it's "open", we can set the request method, headers etc.
            connection.setRequestProperty("accept", "application/json");

            int res = connection.getResponseCode();

            if (res != HttpURLConnection.HTTP_OK) {
                connection.disconnect();
                return new RequestResponse(null, true);
            }

            // This line makes the request
            InputStream responseStream = connection.getInputStream();

            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(responseStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }

            //System.out.println(textBuilder.toString());
            ArrayList<Polygon> polygons = new ArrayList();

            int numLevels = -1;

            SAXBuilder saxBuilder = new SAXBuilder();
            org.jdom2.Document document = saxBuilder.build(new StringReader(textBuilder.toString()));
            Element cl = (Element) document.getContent().get(0);
            //int nodeCounter = 0;
            int wayCounter = 0;
            boolean hasAtLeastOneComplete = false;
            for (int i = 0; i < cl.getContent().size(); i++) {
                if (cl.getContent().get(i).getCType() == Content.CType.Element) {
                    if (((Element) cl.getContent(i)).getName().equals("way")) {

                        //Polygon poly = new Polygon();
                        ArrayList<Coordinate> coordsArrayList = new ArrayList();
                        boolean isComplete = true;

                        for (int j = 0; j < ((Element) cl.getContent(i)).getContentSize(); j++) {
                            if ((((Element) cl.getContent(i)).getContent(j)).getCType() == Content.CType.Element) {

                                if (((Element) ((Element) cl.getContent(i)).getContent(j)).getName().equals("nd")) {
                                    long nodeID = Long.parseLong(((Element) ((Element) cl.getContent(i)).getContent(j)).getAttributeValue("ref"));
                                    boolean isFound = false;
                                    double nodeLat = -10000;
                                    double nodeLon = -10000;
                                    for (int k = 0; k < cl.getContent().size(); k++) {
                                        if (cl.getContent().get(k).getCType() == Content.CType.Element) {
                                            if (((Element) cl.getContent(k)).getName().equals("node")) {
                                                long id = Long.parseLong(((Element) cl.getContent(k)).getAttributeValue("id"));
                                                if (id == nodeID) {
                                                    isFound = true;
                                                    nodeLat = Double.parseDouble(((Element) cl.getContent(k)).getAttributeValue("lat"));
                                                    nodeLon = Double.parseDouble(((Element) cl.getContent(k)).getAttributeValue("lon"));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (isFound == true) {
                                        //poly.points.add(new Location(nodeLat, nodeLon));
                                        coordsArrayList.add(new Coordinate(nodeLat, nodeLon));
                                    } else {
                                        isComplete = false;
                                    }

                                }
                                if (((Element) ((Element) cl.getContent(i)).getContent(j)).getName().equals("tag")) {
//                                    System.out.println(((Element) ((Element) cl.getContent(i)).getContent(j)).getAttributes().get(0));
//                                    System.out.println(((Element) ((Element) cl.getContent(i)).getContent(j)).getAttributes().get(0).getValue());
                                    if (((Element) ((Element) cl.getContent(i)).getContent(j)).getAttributes().get(0).getValue().equals("building:levels")) {
                                        try {
                                            float temp = Float.valueOf(((Element) ((Element) cl.getContent(i)).getContent(j)).getAttributes().get(1).getValue());
                                            numLevels = (int) (temp);
                                        } catch (NumberFormatException e) {

                                        }
                                    }
                                }
                            }
                        }

                        if (isComplete == true) {
                            hasAtLeastOneComplete = true;
                        }

                        wayCounter = wayCounter + 1;

                        if (coordsArrayList.size() > 2) {
                            Coordinate coords[] = new Coordinate[coordsArrayList.size()];
                            for (int m = 0; m < coordsArrayList.size(); m++) {
                                coords[m] = coordsArrayList.get(m);
                            }

                            GeometryFactory geomFactory = new GeometryFactory();

                            try {
                                LinearRing linearRing = geomFactory.createLinearRing(coords);
                                Polygon poly = geomFactory.createPolygon(linearRing);

                                polygons.add(poly);
                            } catch (Exception ex) {

                            }
                        }
                    }

                }
            }
            if (hasAtLeastOneComplete == true) {
                RequestResponse out = new RequestResponse();
                out.data = polygons;
                out.needRetry = false;
                out.numLevels = numLevels;
                return out;
            } else {
                polygons = new ArrayList();
                RequestResponse out = new RequestResponse();
                out.data = polygons;
                out.needRetry = false;
                out.numLevels = numLevels;
                return out;
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(SafegraphPlaces.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SafegraphPlaces.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(SafegraphPlaces.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private static Polygon getPolygonContaining(float lat, float lon, ArrayList<Polygon> polygons) {
        for (int i = 0; i < polygons.size(); i++) {
            boolean isValid = isInside(lat, lon, polygons.get(i));
            if (isValid == true) {
                return polygons.get(i);
            }
        }
        return null;
    }

    private static boolean isInside(float lat, float lon, Polygon polygon) {
        Geometry point = new GeometryFactory().createPoint(new Coordinate(lat, lon));
        return polygon.contains(point);
    }

}
