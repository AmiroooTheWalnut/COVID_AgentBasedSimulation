package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objenesis.strategy.StdInstantiatorStrategy;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class Safegraph implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public AllPatterns allPatterns;
    public AllSafegraphPlaces allSafegraphPlaces;
    
    public static void savePatternsSerializable(String passed_file_path,Patterns allData) {
        FileOutputStream f_out;
        try {
            f_out = new FileOutputStream(passed_file_path + ".data");
            ObjectOutputStream obj_out;
            try {
                obj_out = new ObjectOutputStream(f_out);
                obj_out.writeObject(allData);
                obj_out.close();
            } catch (IOException ex) {
                Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void savePatternsKryo(String passed_file_path,Patterns patterns) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed.class);
        kryo.register(java.time.LocalDateTime.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path+".bin"));
            kryo.writeObject(output, patterns);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void saveSafegraphPlacesKryo(String passed_file_path,SafegraphPlaces safegraphPlaces) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlaces.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Brands.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Brand.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Categories.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Category.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace.class);
        kryo.register(java.lang.String.class);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path+".bin"));
            kryo.writeObject(output, safegraphPlaces);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void loadPatternsSerializable(String passed_file_path,Patterns allData) {

        FileOutputStream f_out;
        try {
            f_out = new FileOutputStream(passed_file_path + ".data");
            ObjectOutputStream obj_out;
            try {
                obj_out = new ObjectOutputStream(f_out);
                obj_out.writeObject(allData);
                obj_out.close();
            } catch (IOException ex) {
                Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Patterns loadPatternsKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed.class);
        kryo.register(java.time.LocalDateTime.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Input input;
        try {
            input = new Input(new FileInputStream(passed_file_path));
            Patterns patterns = kryo.readObject(input, Patterns.class);
            input.close();

            return patterns;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static SafegraphPlaces loadSafegraphPlacesKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlaces.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Brands.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Brand.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Categories.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.Category.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlace.class);
        kryo.register(java.lang.String.class);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Input input;
        try {
            input = new Input(new FileInputStream(passed_file_path));
            SafegraphPlaces safegraphPlaces = kryo.readObject(input, SafegraphPlaces.class);
            input.close();

            return safegraphPlaces;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void saveAllPatternsSerializable(String passed_file_path) {

        FileOutputStream f_out;
        try {
            f_out = new FileOutputStream(passed_file_path + ".data");
            ObjectOutputStream obj_out;
            try {
                obj_out = new ObjectOutputStream(f_out);
                obj_out.writeObject(allPatterns);
                obj_out.close();
            } catch (IOException ex) {
                Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveAllPatternsKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path+".bin"));
            kryo.writeObject(output, allPatterns);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Safegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initAllPatterns(){
        allPatterns=new AllPatterns();
        allPatterns.patternsList=new ArrayList();
    }
    
}
