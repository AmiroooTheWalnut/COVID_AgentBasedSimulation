/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI;

import de.fhpotsdam.unfolding.geo.Location;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class MyPolygon {
    public ArrayList<Location> points=new ArrayList();
    public float severity;
    
    MyPolygon(){
        
    }
    
    MyPolygon(MyPolygon passed_polygon){
        points=passed_polygon.points;
        severity=passed_polygon.severity;
    }
    
    public boolean contains(Location point) {
      int i;
      int j;
      boolean result = false;
      for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
        if ((points.get(i).y > point.y) != (points.get(j).y > point.y) &&
            (point.x < (points.get(j).x - points.get(i).x) * (point.y - points.get(i).y) / (points.get(j).y-points.get(i).y) + points.get(i).x)) {
          result = !result;
         }
      }
      return result;
    }
}
