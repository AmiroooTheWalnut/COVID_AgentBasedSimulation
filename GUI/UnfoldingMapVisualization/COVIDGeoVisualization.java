/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization;

import COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PFont;

/**
 *
 * @author user
 */
public class COVIDGeoVisualization extends PApplet {

    GISLocationDialog parent;
    
    public ArrayList<MyPolygons> polygons = new ArrayList();
    
    UnfoldingMap map;
    MapSources mapSources;
    
    public void startRendering() {
        String[] myArgs = {""};
        PApplet.runSketch(myArgs, this);
    }

    public COVIDGeoVisualization() {
    }

    public COVIDGeoVisualization(GISLocationDialog passed_parent) {
        this.parent = passed_parent;
    }
    
    @Override
    public void settings() {
        size(1000, 1000, "processing.opengl.PGraphics3D");
    }
    
    @Override
    public void setup() {
//        size(500, 500, "processing.opengl.PGraphics3D");

        this.mapSources = new MapSources(this);
        final String[] mapNames = new String[this.mapSources.maps.size()];
        for (int i = 0; i < this.mapSources.maps.size(); i++) {
            mapNames[i] = ((MapSourse) this.mapSources.maps.get(i)).name;
        }
        for (int i = 0; i < this.mapSources.maps.size(); i++) {
            if (((MapSourse) this.mapSources.maps.get(i)).map != null) {
                this.map = ((MapSourse) this.mapSources.maps.get(i)).map;

                break;
            }
        }

        PFont font = createFont("serif-bold", 14.0F);
        textFont(font);
        textMode(4);

        this.parent.renderer = this;
        
        MapUtils.createDefaultEventDispatcher(this, new UnfoldingMap[]{this.map});
        
    }
    
    public void setCaseStudyPanZoom(float zoom, Location location){
        this.map.zoomTo(zoom);
        this.map.panTo(location);
    }
    
    @Override
    public void draw() {
        background(0);
        this.map.draw();
        
        drawPolygons();
    }
    
    public void drawPolygons() {
        fill(220, 0, 0, 20);
        for (int i = 0; i < polygons.size(); i++) {
            beginShape();
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
