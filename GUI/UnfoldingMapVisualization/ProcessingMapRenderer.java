/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import COVID_AgentBasedSimulation.GUI.TupleIntFloat;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.CensusTract;
import COVID_AgentBasedSimulation.Model.Structure.Marker;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JDialog;
import javax.swing.JPanel;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PSurface;
import processing.core.PVector;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class ProcessingMapRenderer extends PApplet {

    public UnfoldingMap map;
    MainFrame parentMainFrame;
    JPanel parentPanel;
    JDialog superParentDialog;
    PSurface mySurface;
    MapSources mapSources;
    ProcessingMapRenderer thisProcessingMapRenderer;

    public ArrayList<Location[]> genericLocationsGroup;
    public ArrayList<Float[]> genericColorsGroup;

    //FOR GIS STUFF
    Location drawingItemLocation;
    ArrayList<Location> drawingChildrenLocations;
    String drawingName;
    String[] drawingChildrenNames;
    //FOR GIS STUFF

    //FOR ONE POI
    Location drawingLocation;
    //FOR ONE POI

    //FOR AGENT_TEMPLATE STUFF
    ArrayList<Location> drawingAgentTemplateLocations;
    ArrayList<Vector3f> colors;
    //FOR AGENT_TEMPLATE STUFF

    //FOR INDIVIDUAL AGENT
    Location drawingIndividualAgent;
    //FOR INDIVIDUAL AGENT

    public boolean isAccessedByMobilityDialog = false;

    //FOR PTAVSPMeasure
    public boolean isShowTravelsPTAVSP = false;
    public boolean isShowCBGIDsPTAVSP = false;
    public boolean isShowPopPTAVSP = false;
    //FOR PTAVSPMeasure

    public ArrayList<MyPolygons> polygons = new ArrayList();
    public RegionImageLayer regionImageLayer;

    boolean isPan = false;
    boolean isReadyPan = false;
    public boolean isShowText = false;
    public boolean isShowRegionIndexText = true;

    public boolean isSave = false;

    public boolean isShowGISMarkers = true;
    public boolean isShowAgentMarkers = true;
    public boolean isShowSimpleMarker = false;

    //RANDOM ARCS
    ArrayList<TupleIntFloat> sortedFlowIndices;
    ArrayList<TupleIntFloat> sortedFlowIndicesReduced;
    ArrayList<Boolean> isDrawRandomArc;
    ArrayList<Boolean> isDrawRandomArcReduced;
    public float[][] rawFlowData;
    float[][] thirdPointDeviationPercentages;
    float[][] thirdPointDeviationPercentagesReduced;
    public float[][] quarantinedFlowData;
    Location[] latLons;
    public boolean isDrawRandomArcs = false;
    //RANDOM ARCS

    public ArrayList<String> regionNames = new ArrayList();
    public ArrayList<Location> regionCenters = new ArrayList();

    ProcessingMapRenderer thisRenderer;

    public MainModel model;

    public ProcessingMapRenderer() {
    }

    public ProcessingMapRenderer(MainFrame mainFrame, JPanel parent, JDialog superParent) {
        parentMainFrame = mainFrame;
        parentPanel = parent;
        superParentDialog = superParent;
    }

    @Override
    public void settings() {
        size(parentPanel.getWidth(), parentPanel.getHeight(), "processing.opengl.PGraphics3D");

    }

    @Override
    public void frameResized(int w, int h) {

        System.out.println("PANEL RESIZED!");
    }

    @Override
    public void setup() {
        thisProcessingMapRenderer = this;
        mySurface = surface;
//        frame.setResizable(true);
//        surface.setResizable(true);
        //get the SmoothCanvas that holds the PSurface
//        SmoothCanvas smoothCanvas = (SmoothCanvas) surface.getNative();
//        smoothCanvas.set

        GLWindow glWindow = (GLWindow) surface.getNative();
        glWindow.setPosition(0, 0);
//        glWindow.setSize(10, 10);

        NewtCanvasAWT newtCanvasAWT = new NewtCanvasAWT(glWindow);
//        newtCanvasAWT.setLocation(0, 0);
//        newtCanvasAWT.setBounds(0, 0, 20, 20);
        parentPanel.add(newtCanvasAWT);

        parentPanel.invalidate();
        parentPanel.revalidate();

//        ComponentListener resizeListener = new ComponentAdapter() {
//            public void componentResized(ActionEvent e) {
//                parentPanel.invalidate();
//                parentPanel.revalidate();
//
//                System.out.println("PANEL RESIZED!");
//            }
//        };
        if (superParentDialog != null) {
            superParentDialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    surface.setSize((superParentDialog.getWidth() / 2), superParentDialog.getHeight());
                    if (map != null) {
                        mapSources = new MapSources(thisProcessingMapRenderer, (superParentDialog.getWidth() / 2), superParentDialog.getHeight());
                        map = ((MapSourse) mapSources.maps.get(0)).map;
                        MapUtils.createDefaultEventDispatcher(thisProcessingMapRenderer, new UnfoldingMap[]{thisProcessingMapRenderer.map});
//                    map.mapDisplay.resize((superParentDialog.getWidth()/2)-5, superParentDialog.getHeight()-5);
                    }
                }
            });
        }

//        parentPanel.addComponentListener(resizeListener);
//        parentPanel
        //SmoothCanvas can be used as a Component
//        parentPanel.add(smoothCanvas);
//        glWindow.destroy();
//        Frame myFrame = ((PSurfaceAWT.SmoothCanvas) ((PSurfaceAWT) surface).getNative()).getFrame();
//        JFrame a = (JFrame) myFrame;
//        a.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        removeExitEvent(getSurface());
//        a.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        if (superParentDialog == null) {
            mapSources = new MapSources(this);
        } else {
            mapSources = new MapSources(this, (superParentDialog.getWidth() / 2), superParentDialog.getHeight());
        }

//        for (int i = 0; i < mapSources.maps.size(); i++) {
//            if (((MapSourse) mapSources.maps.get(i)).map != null) {
//                map = ((MapSourse) mapSources.maps.get(i)).map;
////                parent.jList1.setSelectedIndex(i);
//
//                break;
//            }
//        }
        map = ((MapSourse) mapSources.maps.get(4)).map;
        MapUtils.createDefaultEventDispatcher(this, new UnfoldingMap[]{this.map});

        parentMainFrame.child = this;
    }

    @Override
    public void draw() {
        background(100);
        map.draw();

        if (isShowGISMarkers == true) {
            drawGISMarkers();
        }
        if (isShowAgentMarkers == true) {
            drawIndividualMarker();
            drawAgentTemplatesMarker();
        }
        if (isShowSimpleMarker == true) {
            drawSimpleMarker();
        }
        if (isDrawRandomArcs == true) {
            drawArcs();
        }

//        drawPolygons();
        drawIndexImageShades();
        drawIndexedImageBoundaries();
        drawRegionTexts();

        if (isAccessedByMobilityDialog == false) {
            if (model != null) {
                drawPTAVSPMeasure();
            }
        } else {
            drawGenericMarkers();
        }

//        if (isPan == true) {
//            isPan = false;
//            isReadyPan = true;
//        }
//        if (isReadyPan != true) {
//
//        }
        if (isSave == true) {
            saveFrame("output.jpg");
            isSave = false;
        }
    }

    public void panZoomTo(Location loc, float size) {
        map.panTo(loc);
        map.zoomTo(getZoomConverted(size));
    }

    public void setDrawingSimpleGISMarker(Marker input) {
        drawingLocation = new Location(input.lat, input.lon);
    }

    public void setDrawingGISMarkers(Marker input, ArrayList<Marker> children, String inputName, String[] childrenNames) {
        drawingItemLocation = new Location(input.lon, input.lat);
        drawingChildrenLocations = new ArrayList();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                drawingChildrenLocations.add(new Location(children.get(i).lon, children.get(i).lat));
            }
        } else {
            drawingChildrenLocations = null;
        }
        drawingName = inputName;
        drawingChildrenNames = childrenNames;
    }

    public void setDrawingAgentTemplatesMarkers(ArrayList<Float> lats, ArrayList<Float> lons) {
        drawingAgentTemplateLocations = new ArrayList();
        colors = new ArrayList();
        for (int i = 0; i < lats.size(); i++) {
            drawingAgentTemplateLocations.add(new Location(lons.get(i), lats.get(i)));
        }
    }

    public void setDrawingAgentTemplatesMarkers(ArrayList<Float> lats, ArrayList<Float> lons, ArrayList<Vector3f> passed_colors) {
        drawingAgentTemplateLocations = new ArrayList();
        colors = new ArrayList();
        for (int i = 0; i < lats.size(); i++) {
            drawingAgentTemplateLocations.add(new Location(lons.get(i), lats.get(i)));
            colors.add(new Vector3f(passed_colors.get(i).x, passed_colors.get(i).y, passed_colors.get(i).z));
        }
    }

    public void setDrawingAgetnMarker(float lat, float lon) {
        drawingIndividualAgent = new Location(lat, lon);
    }

    public void drawSimpleMarker() {
        if (drawingLocation != null) {
            SimplePointMarker locSM = new SimplePointMarker(drawingLocation);
            ScreenPosition scLocPos = locSM.getScreenPosition(map);
            fill(200.0F, 20.0F, 0.0F, 100.0F);
            ellipse(scLocPos.x, scLocPos.y, 30, 30);
        }
    }

    public void drawGISMarkers() {
        if (drawingItemLocation != null) {
            SimplePointMarker locSM = new SimplePointMarker(drawingItemLocation);
            ScreenPosition scLocPos = locSM.getScreenPosition(map);
            fill(200.0F, 20.0F, 0.0F, 100.0F);
            ellipse(scLocPos.x, scLocPos.y, 30, 30);
            if (isShowText == true) {
                fill(200.0F, 0.0F, 0.0F, 256.0F);
                text(drawingName, scLocPos.x - textWidth(drawingName) / 2.0F, scLocPos.y + 4.0F);
            }
        }
        if (drawingChildrenLocations != null) {
            for (int i = 0; i < drawingChildrenLocations.size(); i++) {
                SimplePointMarker locSM = new SimplePointMarker(drawingChildrenLocations.get(i));
                ScreenPosition scLocPos = locSM.getScreenPosition(map);
                fill(0.0F, 200.0F, 0.0F, 100.0F);
                ellipse(scLocPos.x, scLocPos.y, 30, 30);
                if (isShowText == true) {
                    fill(200.0F, 0.0F, 0.0F, 256.0F);
                    text(drawingChildrenNames[i], scLocPos.x - textWidth(drawingChildrenNames[i]) / 2.0F, scLocPos.y + 4.0F);
                }
            }
        }
    }

    public void drawAgentTemplatesMarker() {
        if (drawingAgentTemplateLocations != null) {
            for (int i = 0; i < drawingAgentTemplateLocations.size(); i++) {
                SimplePointMarker locSM = new SimplePointMarker(drawingAgentTemplateLocations.get(i));
                ScreenPosition scLocPos = locSM.getScreenPosition(map);
                if (colors != null) {
                    if (colors.size() > 0) {
                        noStroke();
                        fill(colors.get(i).x, colors.get(i).y, colors.get(i).z, 255f);
                        ellipse(scLocPos.x, scLocPos.y, 10, 10);
                    } else {
                        fill(0.0F, 200.0F, 0.0F, 100.0F);
                        ellipse(scLocPos.x, scLocPos.y, 30, 30);
                    }
                } else {
                    fill(0.0F, 200.0F, 0.0F, 100.0F);
                    ellipse(scLocPos.x, scLocPos.y, 30, 30);
                }
            }
        }
    }

    public void drawIndividualMarker() {
        if (drawingIndividualAgent != null) {
            SimplePointMarker locSM = new SimplePointMarker(drawingIndividualAgent);
            ScreenPosition scLocPos = locSM.getScreenPosition(map);
            stroke(0, 0, 0, 0);
            fill(180.0F, 0.0F, 0.0F, 80.0F);
            ellipse(scLocPos.x, scLocPos.y, 10, 10);
        }
    }

    public float getZoomConverted(float size) {
//        float f = (float) (((3.199) * Math.pow(size, -1.231)) + 3.988);
        float f = (float) (Math.pow(((size + 0.18911917d) / 45428.2489d), 1 / -4.48120598d));
        return f;
    }

    public void removeExitEvent(final PSurface surf) {
        final java.awt.Window win
                = ((processing.awt.PSurfaceAWT.SmoothCanvas) surf.getNative()).getFrame();

        for (final java.awt.event.WindowListener evt : win.getWindowListeners()) {
            win.removeWindowListener(evt);
        }
    }

    public void startRendering() {
        thisRenderer = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] myArgs = {""};
                PApplet.runSketch(myArgs, thisRenderer);
            }
        });

        thread.start();
    }

    public static class Vector3f {

        float x;
        float y;
        float z;

        public Vector3f(float passed_x, float passed_y, float passed_z) {
            x = passed_x;
            y = passed_y;
            z = passed_z;
        }
    }

    public void setCaseStudyPanZoom(float zoom, Location location) {
        this.map.zoomTo(zoom);
        this.map.panTo(location);
    }

    public void drawPolygons() {
        for (int i = 0; i < polygons.size(); i++) {
            if (polygons.get(i) != null) {
                fill(polygons.get(i).severity, 0, 255 - polygons.get(i).severity, 120);
                for (int k = 0; k < polygons.get(i).polygons.size(); k++) {
                    beginShape();
                    for (int j = 0; j < polygons.get(i).polygons.get(k).points.size(); j++) {

                        SimplePointMarker startMarker = new SimplePointMarker(polygons.get(i).polygons.get(k).points.get(j));

                        ScreenPosition scStartPos = startMarker.getScreenPosition(map);

                        vertex(scStartPos.x, scStartPos.y);
                    }
                    endShape(CLOSE);
                }
            }
        }

    }

    public void drawIndexImageShades() {
        if (regionImageLayer != null) {
            if (regionImageLayer.indexedImage != null) {
                if (regionImageLayer.severities != null) {
                    int width = regionImageLayer.indexedImage.length;
                    int height = regionImageLayer.indexedImage[0].length;
                    PImage img = createImage(width, height, ARGB);
                    loadPixels();
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            int loc = x + y * width;
                            int index = regionImageLayer.indexedImage[x][height - y - 1] - 1;
                            if (index >= 0) {
                                img.pixels[loc] = color((int) (regionImageLayer.severities[index]), 0, 255 - (int) (regionImageLayer.severities[index]), 30 + (int) regionImageLayer.severities[index]);
                            } else {
                                img.pixels[loc] = color(0, 0, 0, 0);
                            }
                        }
                    }
                    updatePixels();
                    SimplePointMarker startMarker = new SimplePointMarker(new Location(regionImageLayer.startLon, regionImageLayer.startLat));
                    ScreenPosition scStartXY = startMarker.getScreenPosition(map);
                    SimplePointMarker endMarker = new SimplePointMarker(new Location(regionImageLayer.endLon, regionImageLayer.endLat));
                    ScreenPosition scEndXY = endMarker.getScreenPosition(map);
                    image(img, scStartXY.x, scStartXY.y, scEndXY.x - scStartXY.x, scEndXY.y - scStartXY.y);
                }
            }
        }
    }

    public void drawIndexedImageBoundaries() {
        if (regionImageLayer != null) {
            if (regionImageLayer.imageBoundaries != null) {

                int width = regionImageLayer.imageBoundaries.length;
                int height = regionImageLayer.imageBoundaries[0].length;
                PImage img = createImage(width, height, ARGB);
                loadPixels();
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int loc = x + y * width;
                        if (regionImageLayer.imageBoundaries[x][height - y - 1] == true) {
                            img.pixels[loc] = color(0, 0, 0, 200);
                        } else {
                            img.pixels[loc] = color(0, 0, 0, 0);
                        }
                    }
                }
                updatePixels();
                SimplePointMarker startMarker = new SimplePointMarker(new Location(regionImageLayer.startLon, regionImageLayer.startLat));
                ScreenPosition scStartXY = startMarker.getScreenPosition(map);
                SimplePointMarker endMarker = new SimplePointMarker(new Location(regionImageLayer.endLon, regionImageLayer.endLat));
                ScreenPosition scEndXY = endMarker.getScreenPosition(map);
                image(img, scStartXY.x, scStartXY.y, scEndXY.x - scStartXY.x, scEndXY.y - scStartXY.y);
            }
        }
    }

    public void drawRegionTexts() {
        if (isShowRegionIndexText == true) {
            if (regionNames != null && regionCenters != null) {
                for (int i = 0; i < regionNames.size(); i++) {
                    int cBGIndex = regionImageLayer.getCellOfLatLon(regionCenters.get(i).getLat(), regionCenters.get(i).getLon());
                    SimplePointMarker locSM = new SimplePointMarker(regionCenters.get(i));
                    ScreenPosition scLocPos = locSM.getScreenPosition(this.map);
//                    text(regionNames.get(i), scLocPos.x - textWidth(regionNames.get(i)) / 2.0F, scLocPos.y);
                    text(cBGIndex, scLocPos.x - textWidth(regionNames.get(i)) / 2.0F, scLocPos.y);
                }
            }
        }
    }

//    public void drawAllArcs() {
//        
//        
//        
//        for (int h = 0; h < sortedFlowIndices.size(); h++) {
//            int y = (sortedFlowIndices.get(h).index) % (rawFlowData.length);
//            int x = (int) (Math.floor(sortedFlowIndices.get(h).index / rawFlowData.length));
//            strokeWeight(1 + quarantinedFlowData[x][y] * 6.7f);
//            stroke(40 + (float) Math.pow(quarantinedFlowData[x][y], 0.4) * 220, 0, 0, Math.max(0, -1 + (float) Math.pow(quarantinedFlowData[x][y] * 7.5, 4)));
//            drawArc(x, y, latLons[x], latLons[y], 0.00025f);
//        }
//        
////        for (int h = 0; h < sortedFlowIndices.size(); h++) {
////            int y = (sortedFlowIndices.get(h).index) % (rawFlowData.length);
////            int x = (int) (Math.floor(sortedFlowIndices.get(h).index / rawFlowData.length));
////            strokeWeight(1 + quarantinedFlowData[x][y] * 6.7f);
////            stroke(40 + (float) Math.pow(quarantinedFlowData[x][y], 0.4) * 220, 0, 0, Math.max(0, -1 + (float) Math.pow(quarantinedFlowData[x][y] * 7.5, 4)));
////            drawArc(x, y, latLons[x], latLons[y], 0.00025f);
////        }
//    }
//
//    public void drawArc(float angle, int sourceIndex, int destinationIndex, Location start, Location end, float deviationMultiplier) {
//        float distance = start.dist((PVector) end);
//        Location middlePoint = new Location(start.x + (end.x - start.x) / 2.0F, start.y + (end.y - start.y) / 2.0F);
//        float deltaX = end.x - start.x;
//        Location slope;
//        Location inverseSlope;
//        if (Math.abs(deltaX) < 0.00001) {
//            slope = new Location(0, 1);
//            inverseSlope = new Location(1, 0);
//        } else {
//            slope = new Location(1, (end.y - start.y) / deltaX);
//            inverseSlope = new Location(slope.x, -1.0F / slope.y);
//        }
//
//        float sum = (float) Math.sqrt(Math.pow(inverseSlope.x, 2) + Math.pow(inverseSlope.y, 2) + Math.pow(inverseSlope.z, 2));
//        inverseSlope.x = inverseSlope.x / sum;
//        inverseSlope.y = inverseSlope.y / sum;
//        inverseSlope.z = inverseSlope.z / sum;
//
//        Location thirdPoint = new Location(middlePoint.x + distance * this.thirdPointDeviationPercentages[sourceIndex][destinationIndex] / 10.0F, middlePoint.y + distance * this.thirdPointDeviationPercentages[sourceIndex][destinationIndex] / 10.0F);
//
//        Location startCopy = new Location(start.x, start.y);
//        startCopy.add(inverseSlope.x * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.y * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.z * deviationMultiplier / (this.map.getZoomLevel() / 20f));
//
//        Location endCopy = new Location(end.x, end.y);
//        endCopy.add(inverseSlope.x * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.y * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.z * deviationMultiplier / (this.map.getZoomLevel() / 20f));
//
//        Location middleCopy = new Location(thirdPoint.x, thirdPoint.y);
//        middleCopy.add(inverseSlope.x * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.y * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.z * deviationMultiplier / (this.map.getZoomLevel() / 20f));
//
//        SimplePointMarker startMarker = new SimplePointMarker(startCopy);
//        SimplePointMarker endMarker = new SimplePointMarker(endCopy);
//        SimplePointMarker middleMarker = new SimplePointMarker(middleCopy);
//
//        ScreenPosition scStartPos = startMarker.getScreenPosition(this.map);
//        ScreenPosition scEndPos = endMarker.getScreenPosition(this.map);
//        ScreenPosition scMiddlePos = middleMarker.getScreenPosition(this.map);
//
//        beginShape();
//        curveVertex(scStartPos.x, scStartPos.y);
//        curveVertex(scStartPos.x, scStartPos.y);
//        curveVertex(scMiddlePos.x, scMiddlePos.y);
//        curveVertex(scEndPos.x, scEndPos.y);
//        curveVertex(scEndPos.x, scEndPos.y);
//        endShape();
//    }
//
//    public void setTrajectories() {
//
//    }
    public void drawPTAVSPMeasure() {
        Location loc = new Location(model.ABM.measureHolder.pTAVSPMeasure.get(0).destination.patternsRecord.poi_cbg_censusBlock.lon, model.ABM.measureHolder.pTAVSPMeasure.get(0).destination.patternsRecord.poi_cbg_censusBlock.lat);
        SimplePointMarker locSM = new SimplePointMarker(loc);
        ScreenPosition scLocPos = locSM.getScreenPosition(this.map);
        fill(180.0F, 0.0F, 0.0F, 80.0F);
        square(scLocPos.x, scLocPos.y, 10);

        Location locS1 = new Location(model.ABM.measureHolder.pTAVSPMeasure.get(0).source1.lon, model.ABM.measureHolder.pTAVSPMeasure.get(0).source1.lat);
        SimplePointMarker locSMS1 = new SimplePointMarker(locS1);
        ScreenPosition scLocPosS1 = locSMS1.getScreenPosition(this.map);
        fill(0.0F, 180.0F, 0.0F, 80.0F);
        ellipse(scLocPosS1.x, scLocPosS1.y, 10, 10);

        Location locS2 = new Location(model.ABM.measureHolder.pTAVSPMeasure.get(0).source2.lon, model.ABM.measureHolder.pTAVSPMeasure.get(0).source2.lat);
        SimplePointMarker locSMS2 = new SimplePointMarker(locS2);
        ScreenPosition scLocPosS2 = locSMS2.getScreenPosition(this.map);
        fill(0.0F, 180.0F, 0.0F, 80.0F);
        ellipse(scLocPosS2.x, scLocPosS2.y, 10, 10);

        if (isShowCBGIDsPTAVSP == true) {
            for (int i = 0; i < model.ABM.root.regions.size(); i++) {
                for (int j = 0; j < model.ABM.root.regions.get(i).cBGsIDsInvolved.size(); j++) {
                    Location locCBG = new Location(model.ABM.root.regions.get(i).cBGsInvolved.get(j).lon, model.ABM.root.regions.get(i).cBGsInvolved.get(j).lat);
                    SimplePointMarker locSMCBG = new SimplePointMarker(locCBG);
                    ScreenPosition scLocPosCBG = locSMCBG.getScreenPosition(this.map);
//                    text(regionNames.get(i), scLocPos.x - textWidth(regionNames.get(i)) / 2.0F, scLocPos.y);
                    String text = String.valueOf(model.ABM.root.regions.get(i).cBGsIDsInvolved.get(j));
                    fill(0.0F, 0.0F, 0.0F, 255.0F);
                    text(text, scLocPosCBG.x - textWidth(text) / 2.0F, scLocPosCBG.y + j);
                }
            }
        }
        if (isShowTravelsPTAVSP == true) {
            int maxVal = 0;
            for (int i = 0; i < model.ABM.root.regions.size(); i++) {
                if (maxVal < model.ABM.root.regions.get(i).debugNumTravelPTAVSP) {
                    maxVal = model.ABM.root.regions.get(i).debugNumTravelPTAVSP;
                }
            }
            for (int i = 0; i < model.ABM.root.regions.size(); i++) {
                Location locR = new Location(model.ABM.root.regions.get(i).lon, model.ABM.root.regions.get(i).lat);
                SimplePointMarker locSMR = new SimplePointMarker(locR);
                ScreenPosition scLocPosR = locSMR.getScreenPosition(this.map);
                fill(0.0F, 0.0F, ((float) (model.ABM.root.regions.get(i).debugNumTravelPTAVSP) / (float) maxVal) * 255, 200.0F);
                stroke(0, 0, 0, 0);
                ellipse(scLocPosR.x, scLocPosR.y, 15, 5);
                String text = String.valueOf(model.ABM.root.regions.get(i).debugNumTravelPTAVSP);
                fill(0.0F, 0.0F, 0.0F, 255.0F);
                text(text, scLocPosR.x - textWidth(text) / 2.0F, scLocPosR.y);
            }
        }
        if (isShowPopPTAVSP == true) {
            int maxVal = 0;
            for (int i = 0; i < model.ABM.root.regions.size(); i++) {
                if (maxVal < model.ABM.root.regions.get(i).population) {
                    maxVal = model.ABM.root.regions.get(i).population;
                }
            }
            for (int i = 0; i < model.ABM.root.regions.size(); i++) {
                Location locR = new Location(model.ABM.root.regions.get(i).lon, model.ABM.root.regions.get(i).lat);
                SimplePointMarker locSMR = new SimplePointMarker(locR);
                ScreenPosition scLocPosR = locSMR.getScreenPosition(this.map);
                fill(0.0F, 0.0F, ((float) (model.ABM.root.regions.get(i).population) / (float) maxVal) * 255, 200.0F);
                stroke(0, 0, 0, 0);
                ellipse(scLocPosR.x, scLocPosR.y, 15, 5);
                String text = String.valueOf(model.ABM.root.regions.get(i).population);
                fill(0.0F, 0.0F, 0.0F, 255.0F);
                text(text, scLocPosR.x - textWidth(text) / 2.0F, scLocPosR.y);
            }
        }
    }

    public void drawGenericMarkers() {
        if (genericLocationsGroup != null && genericColorsGroup != null) {
            for (int i = 0; i < genericLocationsGroup.size(); i++) {
                for (int j = 0; j < genericLocationsGroup.get(i).length; j++) {
                    Location locR = genericLocationsGroup.get(i)[j];
                    SimplePointMarker locSMR = new SimplePointMarker(locR);
                    ScreenPosition scLocPosR = locSMR.getScreenPosition(this.map);
                    fill(genericColorsGroup.get(i)[0], genericColorsGroup.get(i)[1], genericColorsGroup.get(i)[2]);
                    noStroke();
                    ellipse(scLocPosR.x, scLocPosR.y, 6, 3);
                }
            }
        }
    }

    public void setRandomArcGMSeattle(ArrayList<CensusBlockGroup> cBGs, int numCBGs, int seed, float[][] dataF, int testCBG, int[] impCBGs) {
        Random rnd = new Random(seed);
        latLons = new Location[numCBGs];
        for (int j = 0; j < numCBGs; j++) {
            latLons[j] = new Location(cBGs.get(j).lon, cBGs.get(j).lat);
        }
        float maxThirdPointCurveDeviationPercent = 2.0F;
        float minThirdPointCurveDeviationPercent = -2.0F;
        thirdPointDeviationPercentages = new float[numCBGs][numCBGs];
        rawFlowData = new float[numCBGs][numCBGs];
        quarantinedFlowData = new float[numCBGs][numCBGs];
        sortedFlowIndices = new ArrayList();
        sortedFlowIndicesReduced = new ArrayList();
        isDrawRandomArc = new ArrayList();
        isDrawRandomArcReduced = new ArrayList();
        for (int j = 0; j < numCBGs; j++) {
            for (int k = j + 1; k < numCBGs; k++) {
                boolean isFamous=false;
                for (int m = 0; m < impCBGs.length; m++){
                    if (j == impCBGs[m] || k == impCBGs[m]){
                        if (rnd.nextDouble() < 0.04) {
                            isFamous = true;
                        }
                    }
                }
                if (isFamous == true){
//                    rawFlowData[j][k] = dataF[j][k]/500.0f + 0.4f+(rnd.nextFloat() * 0.8f);
                    rawFlowData[j][k] = 0.01f+(rnd.nextFloat() * 0.7f);
                }else{
//                    rawFlowData[j][k] = dataF[j][k]/500.0f + 0.2f+(rnd.nextFloat() * 0.4f);
                    rawFlowData[j][k] = 0;
                }
                for (int m = 0; m < impCBGs.length; m++){
                    if (j == impCBGs[m] || k == impCBGs[m]) {
                        if (rawFlowData[j][k] <0.669) {
                            if (rnd.nextDouble() < 0.8) {
                                isFamous = false;
                            }
                        } else {
                            if (rnd.nextDouble() < 0.1) {
                                isFamous = false;
                            }
                        }
                    }
                }
                rawFlowData[j][k]=(float)Math.pow(rawFlowData[j][k],1.4);
                sortedFlowIndices.add(new TupleIntFloat(j * (numCBGs) + k, this.rawFlowData[j][k]));
                thirdPointDeviationPercentages[j][k] = (float) (minThirdPointCurveDeviationPercent + rnd.nextFloat() * (maxThirdPointCurveDeviationPercent - minThirdPointCurveDeviationPercent));
                quarantinedFlowData[j][k] = rawFlowData[j][k] + ((rnd.nextFloat() - 0.5f) * 0.6f);
                sortedFlowIndicesReduced.add(new TupleIntFloat(j * (numCBGs) + k, this.quarantinedFlowData[j][k]));
                if (isFamous == true) {
                    isDrawRandomArc.add(true);
                    isDrawRandomArcReduced.add(true);
                } else {
                    if (rnd.nextDouble() < 0.000001) {
                        if (rawFlowData[j][k] < 0.3 && rnd.nextDouble() < 0.01) {
                            isDrawRandomArc.add(false);
                            isDrawRandomArcReduced.add(false);
                        } else {
                            isDrawRandomArc.add(true);
                            isDrawRandomArcReduced.add(true);
                        }
                    } else {
                        isDrawRandomArc.add(false);
                        isDrawRandomArcReduced.add(false);
                    }
                    if (j == testCBG) {
                        isDrawRandomArc.add(true);
                        isDrawRandomArcReduced.add(true);
                    }
                    
                }
            }
        }
        for (int j = 0; j < isDrawRandomArcReduced.size(); j++) {
            if (rnd.nextDouble() < 0.0001) {
                isDrawRandomArcReduced.set(j, !isDrawRandomArcReduced.get(j));
            }
        }
    }
    
    public void setRandomArcGMTucson(ArrayList<CensusBlockGroup> cBGs, int numCBGs, int seed, float[][] dataF, int testCBG, int[] impCBGs) {
        Random rnd = new Random(seed);
        latLons = new Location[numCBGs];
        for (int j = 0; j < numCBGs; j++) {
            latLons[j] = new Location(cBGs.get(j).lon, cBGs.get(j).lat);
        }
        float maxThirdPointCurveDeviationPercent = 2.0F;
        float minThirdPointCurveDeviationPercent = -2.0F;
        thirdPointDeviationPercentages = new float[numCBGs][numCBGs];
        rawFlowData = new float[numCBGs][numCBGs];
        quarantinedFlowData = new float[numCBGs][numCBGs];
        sortedFlowIndices = new ArrayList();
        sortedFlowIndicesReduced = new ArrayList();
        isDrawRandomArc = new ArrayList();
        isDrawRandomArcReduced = new ArrayList();
        for (int j = 0; j < numCBGs; j++) {
            for (int k = j + 1; k < numCBGs; k++) {
                rawFlowData[j][k] = 0.02f+(rnd.nextFloat() * 0.4f);
                
                sortedFlowIndices.add(new TupleIntFloat(j * (numCBGs) + k, this.rawFlowData[j][k]));
                thirdPointDeviationPercentages[j][k] = (float) (minThirdPointCurveDeviationPercent + rnd.nextFloat() * (maxThirdPointCurveDeviationPercent - minThirdPointCurveDeviationPercent));
                quarantinedFlowData[j][k] = rawFlowData[j][k] + ((rnd.nextFloat() - 0.5f) * 0.6f);
                sortedFlowIndicesReduced.add(new TupleIntFloat(j * (numCBGs) + k, this.quarantinedFlowData[j][k]));
                if (rnd.nextDouble() < 0.00001) {
                        isDrawRandomArc.add(true);
                        isDrawRandomArcReduced.add(true);
                    } else {
                        isDrawRandomArc.add(false);
                        isDrawRandomArcReduced.add(false);
                    }
                    if (j == testCBG) {
                        isDrawRandomArc.add(true);
                        isDrawRandomArcReduced.add(true);
                    }
                    if (j == 27 || j == 43 || j == 88) {
                        if (rnd.nextDouble() < 0.05) {
                            isDrawRandomArc.add(true);
                            isDrawRandomArcReduced.add(true);
                        }
                    }
            }
        }
        for (int j = 0; j < isDrawRandomArcReduced.size(); j++) {
            if (rnd.nextDouble() < 0.0001) {
                isDrawRandomArcReduced.set(j, !isDrawRandomArcReduced.get(j));
            }
        }
    }

    public void setRandomArc(ArrayList<CensusTract> cBGs, int numCBGs) {
        Random rnd = new Random();
        this.latLons = new Location[numCBGs];
        for (int j = 0; j < numCBGs; j++) {
            latLons[j] = new Location(cBGs.get(j).lon, cBGs.get(j).lat);
        }
        float maxThirdPointCurveDeviationPercent = 2.0F;
        float minThirdPointCurveDeviationPercent = -2.0F;
        thirdPointDeviationPercentages = new float[numCBGs][numCBGs];
        rawFlowData = new float[numCBGs][numCBGs];
        quarantinedFlowData = new float[numCBGs][numCBGs];
        sortedFlowIndices = new ArrayList();
        isDrawRandomArc = new ArrayList();
        for (int j = 0; j < numCBGs; j++) {
            for (int k = j + 1; k < numCBGs; k++) {
                this.rawFlowData[j][k] = (rnd.nextFloat() * 0.1f);
                sortedFlowIndices.add(new TupleIntFloat(j * (numCBGs) + k, this.rawFlowData[j][k]));
                if (rnd.nextDouble() < 0.2) {
                    isDrawRandomArc.add(true);
                } else {
                    isDrawRandomArc.add(false);
                }
                thirdPointDeviationPercentages[j][k] = (float) (minThirdPointCurveDeviationPercent + rnd.nextFloat() * (maxThirdPointCurveDeviationPercent - minThirdPointCurveDeviationPercent));
                quarantinedFlowData[j][k] = (rnd.nextFloat() * 0.5f);
            }
        }
    }

    public void drawArcs() {
//        for (int h = 0; h < 20000; h++) {
        for (int h = 0; h < sortedFlowIndices.size(); h++) {
            int y = (sortedFlowIndices.get(h).index) % (rawFlowData.length);
            int x = (int) (Math.floor(sortedFlowIndices.get(h).index / rawFlowData.length));
//            strokeWeight(1 + quarantinedFlowData[x][y] * 6.7f);
            strokeWeight(1 + rawFlowData[x][y] * 6.7f);
            stroke(40 + (float) Math.pow(rawFlowData[x][y], 0.4) * 220, 0, 0, Math.max(0, -1 + (float) Math.pow(rawFlowData[x][y] * 4, 5)+51f));
//            stroke(40 + (float) Math.pow(rawFlowData[x][y], 0.4) * 220, 0, 0, 250);
            if (isDrawRandomArc.get(h) == true) {
                drawArc(x, y, latLons[x], latLons[y], 0.00045f);
            }

            y = (sortedFlowIndicesReduced.get(h).index) % (quarantinedFlowData.length);
            x = (int) (Math.floor(sortedFlowIndicesReduced.get(h).index / quarantinedFlowData.length));
//            strokeWeight(1 + quarantinedFlowData[x][y] * 6.7f);
            strokeWeight(1 + quarantinedFlowData[x][y] * 6.7f);
            stroke(0, 0, 40 + (float) Math.pow(quarantinedFlowData[x][y], 0.4) * 220, Math.max(0, -1 + (float) Math.pow(quarantinedFlowData[x][y] * 4, 3.9f)+51f));
            if (isDrawRandomArcReduced.get(h) == true) {
                drawArc(x, y, latLons[x], latLons[y], -0.00045f);
            }

//            if (isShowReductionFrame == true) {
//                if (frames.size() > 0) {
//                    float[][] reducedMatrix = frames.get(parent.jSlider3.getValue()).reducedTravelNumbers;
//                    y = (sortedFlowIndices.get(h).index) % (rawFlowData.length);
//                    x = (int) (Math.floor(sortedFlowIndices.get(h).index / rawFlowData.length));
//                    strokeWeight(1 + reducedMatrix[x][y] * 3.7f);
//                    stroke(0, 0, 40 + (float) Math.pow(reducedMatrix[x][y], 0.4) * 220, Math.max(0, -1 + (float) Math.pow(reducedMatrix[x][y] * 7.5, 4)));
//                    drawArc(x, y, latLons[x], latLons[y], -0.00025f);
//                }
//            }
        }
    }

    public void drawArc(int sourceIndex, int destinationIndex, Location start, Location end, float deviationMultiplier) {
        noFill();
        float distance = start.dist((PVector) end);
        Location middlePoint = new Location(start.x + (end.x - start.x) / 2.0F, start.y + (end.y - start.y) / 2.0F);
        float deltaX = end.x - start.x;
        Location slope;
        Location inverseSlope;
        if (Math.abs(deltaX) < 0.00001) {
            slope = new Location(0, 1);
            inverseSlope = new Location(1, 0);
        } else {
            slope = new Location(1, (end.y - start.y) / deltaX);
            inverseSlope = new Location(slope.x, -1.0F / slope.y);
        }

        float sum = (float) Math.sqrt(Math.pow(inverseSlope.x, 2) + Math.pow(inverseSlope.y, 2) + Math.pow(inverseSlope.z, 2));
        inverseSlope.x = inverseSlope.x / sum;
        inverseSlope.y = inverseSlope.y / sum;
        inverseSlope.z = inverseSlope.z / sum;

        Location thirdPoint = new Location(middlePoint.x + distance * this.thirdPointDeviationPercentages[sourceIndex][destinationIndex] / 10.0F, middlePoint.y + distance * this.thirdPointDeviationPercentages[sourceIndex][destinationIndex] / 10.0F);

        Location startCopy = new Location(start.x, start.y);
        startCopy.add(inverseSlope.x * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.y * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.z * deviationMultiplier / (this.map.getZoomLevel() / 20f));

        Location endCopy = new Location(end.x, end.y);
        endCopy.add(inverseSlope.x * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.y * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.z * deviationMultiplier / (this.map.getZoomLevel() / 20f));

        Location middleCopy = new Location(thirdPoint.x, thirdPoint.y);
        middleCopy.add(inverseSlope.x * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.y * deviationMultiplier / (this.map.getZoomLevel() / 20f), inverseSlope.z * deviationMultiplier / (this.map.getZoomLevel() / 20f));

        SimplePointMarker startMarker = new SimplePointMarker(startCopy);
        SimplePointMarker endMarker = new SimplePointMarker(endCopy);
        SimplePointMarker middleMarker = new SimplePointMarker(middleCopy);

        ScreenPosition scStartPos = startMarker.getScreenPosition(this.map);
        ScreenPosition scEndPos = endMarker.getScreenPosition(this.map);
        ScreenPosition scMiddlePos = middleMarker.getScreenPosition(this.map);

//        beginDraw();
        beginShape();
        curveVertex(scStartPos.x, scStartPos.y);
        curveVertex(scStartPos.x, scStartPos.y);
        curveVertex(scMiddlePos.x, scMiddlePos.y);
        curveVertex(scEndPos.x, scEndPos.y);
        curveVertex(scEndPos.x, scEndPos.y);
        endShape();
//        endDraw();
    }

    public void saveFile() {
        isSave = true;
    }

}
