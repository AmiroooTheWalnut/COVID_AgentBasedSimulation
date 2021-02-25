package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentBasedModel;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Safegraph;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.Person;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
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
public class MainModel {

    public static final long softwareVersion = 1L;
    static final long serialVersionUID = softwareVersion;

    public Safegraph safegraph;
    public AllGISData allGISData;

    public ArrayList<Person> people;
    public ZonedDateTime startTime;
    
    public AgentBasedModel agentBasedModel;

    public void initData() {
        initSafegraph();
    }

    public void initModel() {
        startTime = safegraph.findEarliestPatternTime();
    }

    public void run() {
        if (people != null) {
            if (people.size() > 0) {

                System.out.println(startTime.toString());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }

                });
                thread.start();
            }
        }
    }

    

    public void generatePeopleFromPatterns() {
        people = new ArrayList();
        int counter = 0;
        for (int i = 0; i < safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            for (int j = 0; j < safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                if (safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs_place != null) {
                    for (int k = 0; k < safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs_place.size(); k++) {
                        Person person = new Person();
                        person.index = counter;
                        person.home = safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs_place.get(k).key;
                        people.add(person);
                        counter = counter + 1;
                    }
                }
            }
        }
    }

    public void reset() {
        generatePeopleFromPatterns();
    }

    public static AllGISData loadAllGISDataKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.AllGISData.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.CensusTract.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.City.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.Country.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.County.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.State.class);
        kryo.register(COVID_AgentBasedSimulation.Model.Structure.ZipCode.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.register(java.lang.Long.class);
        kryo.register(java.lang.Float.class);
        kryo.register(java.time.ZonedDateTime.class);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(java.time.ZonedDateTime.class);
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Input input;
        try {
            input = new Input(new FileInputStream(passed_file_path));
            AllGISData allGISData = kryo.readObject(input, AllGISData.class);
            input.close();

            return allGISData;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AllGISData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void initSafegraph() {
        safegraph = new Safegraph();
        safegraph.initAllPatternsAllPlaces();
        safegraph.setDatasetTemplate();
        allGISData = new AllGISData();
        allGISData.setDatasetTemplate();
    }

}
