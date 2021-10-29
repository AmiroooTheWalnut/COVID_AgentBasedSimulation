/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import de.fhpotsdam.unfolding.geo.Location;
import java.io.Serializable;
import java.util.ArrayList;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

/**
 *
 * @author user
 */
public class MyPolygon implements Serializable{
    static final long serialVersionUID = softwareVersion;

    public ArrayList<Location> points = new ArrayList();
    

    public MyPolygon() {

    }

    public MyPolygon(MyPolygon passed_polygon) {
        points = passed_polygon.points;
    }

    public boolean contains(Location point) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            if ((points.get(i).y > point.y) != (points.get(j).y > point.y)
                    && (point.x < (points.get(j).x - points.get(i).x) * (point.y - points.get(i).y) / (points.get(j).y - points.get(i).y) + points.get(i).x)) {
                result = !result;
            }
        }
        return result;
    }

    public static Polygon myPolygonToJTSPolygon(MyPolygon input) {
        Coordinate coords[] = new Coordinate[input.points.size()];
        for (int m = 0; m < input.points.size(); m++) {
            coords[m] = new Coordinate(input.points.get(m).x, input.points.get(m).y);
        }

        GeometryFactory geomFactory = new GeometryFactory();
        
        if(coords.length<3){
            return null;
        }

        LinearRing linearRing = geomFactory.createLinearRing(coords);
        Polygon poly = geomFactory.createPolygon(linearRing);
        
        return poly;
    }
}
