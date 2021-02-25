/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI;

import COVID_AgentBasedSimulation.Model.Structure.Marker;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.util.ArrayList;
import processing.core.PApplet;

/**
 *
 * @author user
 */
public class ProcessingMapRenderer extends PApplet {

    UnfoldingMap map;
    MainFrame parent;
    MapSources mapSources;
    Location drawingItemLocation;
    ArrayList<Location> drawingChildrenLocations;
    ArrayList<Location> drawingIndividuals;
    String drawingName;
    String[] drawingChildrenNames;

    boolean isPan = false;
    boolean isReadyPan = false;
    boolean isShowText = false;

    boolean isShowGISMarkers = false;
    boolean isShowIndividuals = false;

    ProcessingMapRenderer() {
    }

    ProcessingMapRenderer(MainFrame mainFrame) {
        this.parent = mainFrame;
    }

    @Override
    public void settings() {
        size(1000, 1000, "processing.opengl.PGraphics3D");
    }

    @Override
    public void setup() {
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

        parent.child = this;
    }

    @Override
    public void draw() {
        background(0);
        map.draw();
        if (isShowGISMarkers == true) {
            drawGISMarkers();
        }
        if (isShowIndividuals == true) {
            drawIndividualMarkers();
        }
//        if (isPan == true) {
//            isPan = false;
//            isReadyPan = true;
//        }
//        if (isReadyPan != true) {
//
//        }
    }

    public void panZoomTo(Location loc, float size) {
        map.panTo(loc);
        map.zoomTo(getZoomConverted(size));
    }

    public void setDrawingMarkers(Marker input, ArrayList<Marker> children, String inputName, String[] childrenNames, ArrayList<Marker> individuals) {
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
        
        drawingIndividuals=new ArrayList();
        if (individuals != null) {
            for (int i = 0; i < individuals.size(); i++) {
                drawingIndividuals.add(new Location(individuals.get(i).lon+(Math.random()-0.5d)*individuals.get(i).size/5f, individuals.get(i).lat+(Math.random()-0.5d)*individuals.get(i).size/5f));
            }
        } else {
            drawingIndividuals = null;
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
    
    public void drawIndividualMarkers() {
        if (drawingIndividuals != null) {
            for (int i = 0; i < drawingIndividuals.size(); i++) {
                SimplePointMarker locSM = new SimplePointMarker(drawingIndividuals.get(i));
                ScreenPosition scLocPos = locSM.getScreenPosition(map);
                stroke(0,0,0,0);
                fill(180.0F, 0.0F, 0.0F, 80.0F);
                ellipse(scLocPos.x, scLocPos.y, 10, 10);
            }
        }
    }

    public float getZoomConverted(float size) {
//        float f = (float) (((3.199) * Math.pow(size, -1.231)) + 3.988);
        float f = (float) (Math.pow(((size + 0.18911917d) / 45428.2489d), 1 / -4.48120598d));
        return f;
    }

    public void startRendering() {
        String[] myArgs = {""};
        PApplet.runSketch(myArgs, this);
    }
}
