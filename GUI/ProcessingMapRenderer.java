/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

/**
 *
 * @author user
 */
public class ProcessingMapRenderer extends PApplet {
    UnfoldingMap map;
    MainFrame parent;
    MapSources mapSources;
    
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
    
    public void panTo(Location loc){
        map.zoomAndPanTo(10, 10, 4);
    }
    
    @Override
    public void draw() {
        background(0);
        map.draw();
    }
    
    public void startRendering() {
        String[] myArgs = {""};
        PApplet.runSketch(myArgs, this);
    }
}
