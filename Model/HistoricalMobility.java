/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.GUI.GraphicalModel.GraphicalModelDialog;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import com.opencsv.CSVWriter;
import esmaieeli.gisFastLocationOptimization.GIS3D.AllData;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Point;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class HistoricalMobility implements Serializable {

    static final long serialVersionUID = softwareVersion;

    public ArrayList<ArrayList<Point>> trajectories = new ArrayList();//Uniformly sampled trajectory
    public ArrayList<ArrayList<Point>> rawTrajectory = new ArrayList();//The POI and home locations
    public ArrayList<ArrayList<Point>> pathedTrajectory = new ArrayList();//Found the street path for the trajectory
    public ArrayList<ArrayList<Point>> streetNodeAllowable = new ArrayList();//Allowable nodes

    public void saveHistoricalMobilityUniformSampledCSV(String path) {
        try {
            ArrayList<String[]> data = new ArrayList();
            for (int i = 0; i < trajectories.size(); i++) {
                String[] rowX = new String[trajectories.get(i).size()];
                String[] rowY = new String[trajectories.get(i).size()];
                for (int j = 0; j < trajectories.get(i).size(); j++) {
                    rowX[j] = String.valueOf(trajectories.get(i).get(j).getX());
                    rowY[j] = String.valueOf(trajectories.get(i).get(j).getY());
                }
                data.add(rowX);
                data.add(rowY);
            }
            CSVWriter writer = new CSVWriter(new FileWriter(path));//mobilityUniformSample
            writer.writeAll(data);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(HistoricalMobility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveHistoricalMobilityJunctionSampledCSV(String path) {
        try {
            ArrayList<String[]> data = new ArrayList();
            for (int i = 0; i < pathedTrajectory.size(); i++) {
                String[] rowX = new String[pathedTrajectory.get(i).size()];
                String[] rowY = new String[pathedTrajectory.get(i).size()];
                for (int j = 0; j < pathedTrajectory.get(i).size(); j++) {
                    rowX[j] = String.valueOf(pathedTrajectory.get(i).get(j).getX());
                    rowY[j] = String.valueOf(pathedTrajectory.get(i).get(j).getY());
                }
                data.add(rowX);
                data.add(rowY);
            }
            CSVWriter writer = new CSVWriter(new FileWriter(path));//mobilityJunctionSample
            writer.writeAll(data);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(HistoricalMobility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveHistoricalMobilityRawSampledCSV(String path) {
        try {
            ArrayList<String[]> data = new ArrayList();
            for (int i = 0; i < rawTrajectory.size(); i++) {
                String[] rowX = new String[rawTrajectory.get(i).size()];
                String[] rowY = new String[rawTrajectory.get(i).size()];
                for (int j = 0; j < rawTrajectory.get(i).size(); j++) {
                    rowX[j] = String.valueOf(rawTrajectory.get(i).get(j).getX());
                    rowY[j] = String.valueOf(rawTrajectory.get(i).get(j).getY());
                }
                data.add(rowX);
                data.add(rowY);
            }
            CSVWriter writer = new CSVWriter(new FileWriter(path));
            writer.writeAll(data);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(HistoricalMobility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveStreetNodes(AllData allData, String path) {
        try {
            ArrayList<String[]> data = new ArrayList();

            for (int i = 0; i < allData.all_Ways.length; i++) {
                String nodeTypeStr = allData.all_Ways[i].type;
                boolean isValid = true;
                if (nodeTypeStr != null) {
                    switch (nodeTypeStr) {
                        case "motorway":
                            isValid = true;
                            break;
                        case "trunk":
                            isValid = true;
                            break;
                        case "primary":
                            isValid = true;
                            break;
                        case "secondary":
                            isValid = true;
                            break;
                        case "tertiary":
                            isValid = true;
                            break;
//                        case "unclassified":
//                            isValid = true;
//                            break;
                        case "residential":
                            isValid = true;
                            break;
//                        case "service":
//                            isValid = true;
//                            break;
//                        case "foot":
//                            isValid = true;
////                            System.out.println(nodeTypeStr);
//                            break;
//                        case "footway":
//                            isValid = true;
////                            System.out.println(nodeTypeStr);
//                            break;
//                        case "cycleway":
//                            isValid = true;
//                            break;
//                        case "pedestrian":
//                            isValid = true;
//                            break;
                        case "secondary_link":
                            isValid = true;
                            break;
                        case "trunk_link":
                            isValid = true;
                            break;
                        case "tertiary_link":
                            isValid = true;
                            break;
                        case "motorway_link":
                            isValid = true;
                            break;
                        case "primary_link":
                            isValid = true;
                            break;
                        default:
                            System.out.println(nodeTypeStr);
                            isValid = false;
                    }
                    if (isValid == true) {
                        for (int j = 0; j < allData.all_Ways[i].myNodes.length; j++) {
                            String[] row = new String[2];
                            row[0] = String.valueOf(allData.all_Ways[i].myNodes[j].lat);
                            row[1] = String.valueOf(allData.all_Ways[i].myNodes[j].lon);
                            data.add(row);
                        }
                    }
                }
            }
            CSVWriter writer = new CSVWriter(new FileWriter(path));
            writer.writeAll(data);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(HistoricalMobility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
