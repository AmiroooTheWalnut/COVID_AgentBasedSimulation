/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.GraphicalModel;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public class AgeOccupationDists {
    public double mean0_18_Edu_Stu_Edu=(5.96590575*20/(5.96590575+1.99212059))+0;
    public double mean19_64_Edu_Stu_Edu=(1.838544989*18/(1.838544989+1.181657948))+(4.938544989*18/(4.938544989+1.191657948));
    public double mean65_inf_Edu_Stu_Edu=(0.080843702*0/(0.080843702+3.385806799))+(0.080743702*0/(0.080743702+3.985806799));
    
    public double mean0_18_Service_Edu=(0.56590575*20/(0.56590575+2.90212059));
    public double mean19_64_Service_Edu=(0.205506399*4/(0.205506399+1.331934214));
    public double mean65_inf_Service_Edu=(0.205506399*1/(0.205506399+1.331934214));
    
//    double mean0_18_Health_Edu=(0.080843702*0/(0.080843702+3.385806799));
//    double mean19_64_Health_Edu=(0.080843702*1/(0.080843702+3.385806799));
//    double mean65_inf_Health_Edu=(0.080943702*0/(0.080943702+3.335806799));
//    
//    double mean0_18_Driver_Edu=(0.080843702*0/(0.080843702+3.385806799));
//    double mean19_64_Driver_Edu=(0.080843702*1/(0.080843702+3.385806799));
//    double mean65_inf_Driver_Edu=(0.080943702*0/(0.080943702+3.335806799));
    
    public double mean19_64_All_Shop=((3.010140973*6/(3.010140973+1.332434416)))+((9.52536124*12/(9.52536124+1.998624563)))+((1.525302172*12/(1.525302172+1.998849392)))+((5.42025*12/(5.42025+2.09365)))+((2.4203*12/(2.4203+2.09365)));
    public double mean65_inf_All_Shop=((0.210148*9/(0.210148+1.332392)))+((0.210136*9/(0.210136+1.332497)));
    
    float[][] CBGSchoolDists;
    float[][] CBGShopDists;
    
    int bestCBGSericeEdu=43-1;
    public AgeOccupationDists(){
        try {
            FileReader filereader = new FileReader("CBGSchoolDists_2020_01.csv");

            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(0)
                    .build();
            List<String[]> allData_1 = csvReader.readAll();
            //System.out.println(allData_1.getClass().getTypeName());
            ArrayList<String[]> allData = new ArrayList(allData_1);
            CBGSchoolDists=new float[allData.size()][allData.get(0).length];
            for (int i = 0; i < allData.size(); i++) {
                for (int j = 0; j < allData.get(i).length; j++) {
                    CBGSchoolDists[i][j]=Float.parseFloat(allData.get(i)[j]);
                }
                //System.out.println(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileReader filereader = new FileReader("CBGSchoolDists_2020_01.csv");

            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(0)
                    .build();
            List<String[]> allData_1 = csvReader.readAll();
            //System.out.println(allData_1.getClass().getTypeName());
            ArrayList<String[]> allData = new ArrayList(allData_1);
            CBGShopDists=new float[allData.size()][allData.get(0).length];
            for (int i = 0; i < allData.size(); i++) {
                for (int j = 0; j < allData.get(i).length; j++) {
                    CBGShopDists[i][j]=Float.parseFloat(allData.get(i)[j]);
                }
                //System.out.println(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public float[] getSchoolDistsFromCBG(int index){
        float[] ouput = new float[CBGSchoolDists[index].length];
        for(int i=0;i<ouput.length;i++){
            ouput[i]=CBGSchoolDists[index][i];
        }
        return ouput;
    }
    
    public float[] getShopDistsFromCBG(int index){
        float[] ouput = new float[CBGShopDists[index].length];
        for(int i=0;i<ouput.length;i++){
            ouput[i]=CBGShopDists[index][i];
        }
        return ouput;
    }
    
}
