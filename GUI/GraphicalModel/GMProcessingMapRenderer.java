/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.GUI.GraphicalModel;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MapSources;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MapSourse;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import javax.swing.JDialog;
import processing.core.PApplet;
import processing.core.PSurface;

/**
 *
 * @author user
 */
public class GMProcessingMapRenderer extends PApplet {
    
    public UnfoldingMap map;
//    MainFrame parentMainFrame;
    JPanel parentPanel;
    JDialog superParentDialog;
    PSurface mySurface;
    MapSources mapSources;
    GMProcessingMapRenderer thisProcessingMapRenderer;
    
    public GMProcessingMapRenderer() {
    }

    public GMProcessingMapRenderer(MainFrame mainFrame, JPanel parent, JDialog superParent) {
//        parentMainFrame = mainFrame;
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

        GLWindow glWindow = (GLWindow) surface.getNative();
        glWindow.setPosition(0, 0);

        NewtCanvasAWT newtCanvasAWT = new NewtCanvasAWT(glWindow);
        parentPanel.add(newtCanvasAWT);

        parentPanel.invalidate();
        parentPanel.revalidate();

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
        if (superParentDialog == null) {
            mapSources = new MapSources(this);
        } else {
            mapSources = new MapSources(this, (superParentDialog.getWidth() / 2), superParentDialog.getHeight());
        }

        map = ((MapSourse) mapSources.maps.get(4)).map;
        MapUtils.createDefaultEventDispatcher(this, new UnfoldingMap[]{this.map});

//        parentMainFrame.child = this;
    }
    
    @Override
    public void draw() {
        background(100);
        map.draw();
    }
    
    public void startRendering() {
        thisProcessingMapRenderer = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] myArgs = {""};
                PApplet.runSketch(myArgs, thisProcessingMapRenderer);
            }
        });

        thread.start();
    }
    
}
