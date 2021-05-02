/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns;
import COVID_AgentBasedSimulation.Model.Dataset;
import java.util.ArrayList;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.County;
import COVID_AgentBasedSimulation.Model.Structure.State;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class CovidCsseJhu extends Dataset implements Serializable {

    static final long serialVersionUID = softwareVersion;

    ArrayList<DailyConfirmedCases> dailyConfirmedCasesList = new ArrayList();

    @Override
    public void requestDataset(AllGISData allGISData, String project, String year, String month, boolean isParallel, int numCPU) {

    }

    @Override
    public void requestDatasetRange(AllGISData allGISData, String project, String years[], String months[][], boolean isParallel, int numCPU) {

    }

    @Override
    public void setDatasetTemplate() {

    }

    public void preprocessDailyCountyInfections(String fileName, AllGISData gisData) {
        dailyConfirmedCasesList = new ArrayList();
        File patternFile = new File(fileName);
        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(patternFile, StandardCharsets.UTF_8);
            for (int i = 0; i < data.getRowCount(); i++) {
                CsvRow row = data.getRow(i);
                String rowLatValidation = row.getField("Lat");
                if(!rowLatValidation.equals(0)){
                    String fipsCode = row.getField("FIPS");
                    fipsCode=String.valueOf((int)Double.parseDouble(fipsCode));
                    String countyCode=fipsCode.substring(fipsCode.length()-3, fipsCode.length());
                    String stateCode=fipsCode.substring(0, fipsCode.length()-3);
                    
                    State state=gisData.countries.get(0).findState(Byte.valueOf(stateCode));
                    County county=state.findCounty(Integer.parseInt(countyCode));
                    
                    for(int j=11;j<row.getFieldCount();j++){
                        String splitted[]=data.getHeader().get(j).split("/");
                        String month=splitted[0];
                        if(month.length()==1){
                            month="0"+month;
                        }
                        String day=splitted[1];
                        if(day.length()==1){
                            day="0"+day;
                        }
                        String year=splitted[2];
                        String isoTime="20"+year+"-"+month+"-"+day+"T00:00Z[UTC]";
                        ZonedDateTime date=ZonedDateTime.parse(isoTime);
                        int confirmedCased = Integer.parseInt(row.getField(j));
                        DailyConfirmedCases dailyConfirmedCases=new DailyConfirmedCases(date,confirmedCased,county);
                        dailyConfirmedCasesList.add(dailyConfirmedCases);
                    }
                    
//                    System.out.println("!!!");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Patterns.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
    }

//    public ArrayList<String> checkDayAvailability() {
//        
//    }
    public static void saveDailyConfirmedCasesListSerializable(String passed_file_path, CovidCsseJhu data) {
        FileOutputStream f_out;
        try {
            f_out = new FileOutputStream(passed_file_path + ".data");
            ObjectOutputStream obj_out;
            try {
                obj_out = new ObjectOutputStream(f_out);
                obj_out.writeObject(data);
                obj_out.close();
            } catch (IOException ex) {
                Logger.getLogger(CovidCsseJhu.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CovidCsseJhu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveDailyConfirmedCasesListKryo(String passed_file_path, CovidCsseJhu data) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.time.LocalDateTime.class);
        kryo.register(int.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Output output;
        try {
            output = new Output(new FileOutputStream(passed_file_path + ".bin"));
            kryo.writeObject(output, data);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CovidCsseJhu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loadDailyConfirmedCasesListSerializable(String passed_file_path, CovidCsseJhu data) {

    }

    public static CovidCsseJhu loadDailyConfirmedCasesListKryo(String passed_file_path) {
        Kryo kryo = new Kryo();
        kryo.register(COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.time.LocalDateTime.class);
        kryo.register(int.class);
        kryo.register(int[].class);
        kryo.register(java.lang.String[].class);
        kryo.register(java.lang.String.class);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(COVID_AgentBasedSimulation.Model.DatasetTemplate.class);
        kryo.register(COVID_AgentBasedSimulation.Model.RecordTemplate.class);
        Input input;
        try {
            File temp = new File(passed_file_path);
            if (temp.exists() == true) {
                input = new Input(new FileInputStream(passed_file_path));
                CovidCsseJhu data = kryo.readObject(input, CovidCsseJhu.class);
                input.close();

                return data;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CovidCsseJhu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ArrayList getCountyCases(County input){
        ArrayList output=new ArrayList();
        for(int i=0;i<dailyConfirmedCasesList.size();i++){
            if(dailyConfirmedCasesList.get(i).county.id==input.id){
                output.add(dailyConfirmedCasesList.get(i));
            }
        }
        return output;
    }

    public void clearCovidCsseJhu() {

    }

    public void initCovidCsseJhu() {

    }
}