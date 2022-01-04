/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MapSourse;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MapSources;
import COVID_AgentBasedSimulation.Model.Structure.Marker;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import processing.awt.PSurfaceAWT;
import processing.awt.PSurfaceAWT.SmoothCanvas;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PSurface;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class ProcessingMapRenderer extends PApplet {

    public UnfoldingMap map;
    MainFrame parentMainFrame;
    JPanel parentPanel;
    PSurface mySurface;
    MapSources mapSources;

    //FOR GIS STUFF
    Location drawingItemLocation;
    ArrayList<Location> drawingChildrenLocations;
    String drawingName;
    String[] drawingChildrenNames;
    //FOR GIS STUFF

    //FOR AGENT_TEMPLATE STUFF
    ArrayList<Location> drawingAgentTemplateLocations;
    ArrayList<Vector3f> colors;
    //FOR AGENT_TEMPLATE STUFF

    //FOR INDIVIDUAL AGENT
    Location drawingIndividualAgent;
    //FOR INDIVIDUAL AGENT

    public ArrayList<MyPolygons> polygons = new ArrayList();
    public RegionImageLayer regionImageLayer;

    boolean isPan = false;
    boolean isReadyPan = false;
    boolean isShowText = false;
    public boolean isShowRegionIndexText = true;

    public boolean isSave = false;

    public boolean isShowGISMarkers = true;
    public boolean isShowAgentMarkers = true;

    public ArrayList<String> regionNames = new ArrayList();
    public ArrayList<Location> regionCenters = new ArrayList();

    ProcessingMapRenderer thisRenderer;

    public ProcessingMapRenderer() {
    }

    public ProcessingMapRenderer(MainFrame mainFrame, JPanel parent) {
        parentMainFrame = mainFrame;
        parentPanel = parent;
    }

    @Override
    public void settings() {
        size(parentPanel.getWidth(), parentPanel.getHeight(), "processing.opengl.PGraphics3D");

    }

    @Override
    public void setup() {
        mySurface = surface;
//        frame.setResizable(true);
//        surface.setResizable(true);
        //get the SmoothCanvas that holds the PSurface
//        SmoothCanvas smoothCanvas = (SmoothCanvas) surface.getNative();

        GLWindow glWindow = (GLWindow) surface.getNative();
        glWindow.setPosition(0, 0);

        NewtCanvasAWT newtCanvasAWT = new NewtCanvasAWT(glWindow);
//        newtCanvasAWT.setLocation(0, 0);
//        newtCanvasAWT.setBounds(0, 0, 200, 200);
        parentPanel.add(newtCanvasAWT);

        parentPanel.invalidate();
        parentPanel.revalidate();

        //SmoothCanvas can be used as a Component
//        parentPanel.add(smoothCanvas);
//        glWindow.destroy();
//        Frame myFrame = ((PSurfaceAWT.SmoothCanvas) ((PSurfaceAWT) surface).getNative()).getFrame();
//        JFrame a = (JFrame) myFrame;
//        a.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        removeExitEvent(getSurface());
//        a.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        mapSources = new MapSources(this);

//        for (int i = 0; i < mapSources.maps.size(); i++) {
//            if (((MapSourse) mapSources.maps.get(i)).map != null) {
//                map = ((MapSourse) mapSources.maps.get(i)).map;
////                parent.jList1.setSelectedIndex(i);
//
//                break;
//            }
//        }
        map = ((MapSourse) mapSources.maps.get(3)).map;
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

//        drawPolygons();
        drawIndexImageShades();
        drawIndexedImageBoundaries();
        drawRegionTexts();

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
                                img.pixels[loc] = color((int) (regionImageLayer.severities[index]), 0, 255 - (int) (regionImageLayer.severities[index]), 120);
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
                    SimplePointMarker locSM = new SimplePointMarker(regionCenters.get(i));
                    ScreenPosition scLocPos = locSM.getScreenPosition(this.map);
                    text(regionNames.get(i), scLocPos.x - textWidth(regionNames.get(i)) / 2.0F, scLocPos.y);
                }
            }
        }
    }

    public void saveFile() {
        isSave = true;
    }

}
