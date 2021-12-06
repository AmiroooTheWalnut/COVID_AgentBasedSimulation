/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Region;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 *
 * @author user
 */
public class HistoricalRun implements Serializable {

    static final long serialVersionUID = softwareVersion;

    public ArrayList<Region> regions;
    public RegionImageLayer regionsLayer;
    public transient ZonedDateTime startTime;
    public transient ZonedDateTime endTime;
    public String startTimeString;
    public String endTimeString;

    public void saveHistoricalRunJson(String path) {
        startTimeString = startTime.toString() + "[UTC]";
        endTimeString = endTime.toString() + "[UTC]";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result = gson.toJson(this);
        //System.out.println(result);
        BufferedWriter writer;
        try {
            FileWriter out = new FileWriter(path);
            writer = new BufferedWriter(out);
            writer.write(result);

            writer.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static HistoricalRun loadHistoricalRunJson(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileReader in;
        try {
            in = new FileReader(path);
            BufferedReader br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            in.close();

            HistoricalRun result = gson.fromJson(sb.toString(), HistoricalRun.class);
            if (result.startTimeString != null) {
                if (result.startTimeString.length() > 0) {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(result.startTimeString);
                    result.startTime = zonedDateTime;
                }
            }
            if (result.endTimeString != null) {
                if (result.endTimeString.length() > 0) {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(result.endTimeString);
                    result.endTime = zonedDateTime;
                }
            }
            return result;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void saveHistoricalRunKryo(String passed_file_path, HistoricalRun patterns) {
        patterns.startTimeString = patterns.startTime.toString();
        patterns.endTimeString = patterns.endTime.toString();
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.HardcodedSimulator.Region.class);
        kryo.register(COVID_AgentBasedSimulation.Model.HardcodedSimulator.RegionSnapshot.class);
        kryo.register(COVID_AgentBasedSimulation.Model.HistoricalRun.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygons.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer.class);
        kryo.register(double[].class);
        kryo.register(int[].class);
        kryo.register(long[].class);
        kryo.register(int[][].class);
        kryo.register(boolean[][].class);
        kryo.register(boolean[].class);
        kryo.register(de.fhpotsdam.unfolding.geo.Location.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.lang.String.class);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path + ".bin"));
            kryo.writeObject(output, patterns);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HistoricalRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static HistoricalRun loadHistoricalRunKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.HardcodedSimulator.Region.class);
        kryo.register(COVID_AgentBasedSimulation.Model.HardcodedSimulator.RegionSnapshot.class);
        kryo.register(COVID_AgentBasedSimulation.Model.HistoricalRun.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygons.class);
        kryo.register(COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer.class);
        kryo.register(double[].class);
        kryo.register(int[].class);
        kryo.register(long[].class);
        kryo.register(int[][].class);
        kryo.register(boolean[][].class);
        kryo.register(boolean[].class);
        kryo.register(de.fhpotsdam.unfolding.geo.Location.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.lang.String.class);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Input input;
        try {
            File temp = new File(passed_file_path);
            if (temp.exists() == true) {
                input = new Input(new FileInputStream(passed_file_path));
                HistoricalRun historicalRun = kryo.readObject(input, HistoricalRun.class);
                input.close();

                if (historicalRun.startTimeString != null) {
                    if (historicalRun.startTimeString.length() > 0) {
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(historicalRun.startTimeString);
                        historicalRun.startTime = zonedDateTime;
                    }
                }
                if (historicalRun.endTimeString != null) {
                    if (historicalRun.endTimeString.length() > 0) {
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(historicalRun.endTimeString);
                        historicalRun.endTime = zonedDateTime;
                    }
                }

                return historicalRun;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HistoricalRun.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
