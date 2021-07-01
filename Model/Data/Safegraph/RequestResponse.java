/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import java.util.ArrayList;
import org.locationtech.jts.geom.Polygon;

/**
 *
 * @author user
 */
public class RequestResponse {
    public ArrayList<Polygon> data;
    public Polygon finalPolygon;
    public int numLevels;
    public boolean needRetry=false;
    
    public RequestResponse(){
        
    }
    
    public RequestResponse(ArrayList<Polygon> passed_data, boolean passed_needRetry){
        data=passed_data;
        needRetry=passed_needRetry;
    }
}
