/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.MainModel;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import de.siegmar.fastcsv.writer.CsvWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class BatchRun {

    public void runPostProcess(ArrayList<String> runs, MainModel model) {
        ArrayList<String[]> aVDRows = runAVD(runs, model);
        ArrayList<String[]> iNFRows = runCBGInf(runs, model);
        ArrayList<String[]> infectionReportRows = runInfectionReport(runs, model);
        ArrayList<String[]> nOVRows = runNOV(runs, model);
        ArrayList<String[]> pTAVSPRows = runPTAVSP(runs, model);

        ArrayList<String[]> mRRows = null;
        try {
            FileReader filereader = new FileReader(runs.get(0) + File.separator + "mobilityReport.csv");
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(0)
                    .build();
            List<String[]> mRRowstemp = csvReader.readAll();
            mRRows = new ArrayList(mRRowstemp);
            csvReader.close();
        } catch (IOException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<String[]> rMSRows = null;
        try {
            File f = new File(runs.get(0) + File.separator + "regionMobilitySimilarity.csv");
            if (f.exists() && !f.isDirectory()) {
                FileReader filereader = new FileReader(runs.get(0) + File.separator + "regionMobilitySimilarity.csv");
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(0)
                        .build();
                List<String[]> rMSRowstemp = csvReader.readAll();
                rMSRows = new ArrayList(rMSRowstemp);
                csvReader.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<String[]> rMSAVGRows = null;
        try {
            File f = new File(runs.get(0) + File.separator + "regionMobilitySimilarity_avg.csv");
            if (f.exists() && !f.isDirectory()) {
                FileReader filereader = new FileReader(runs.get(0) + File.separator + "regionMobilitySimilarity_avg.csv");
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(0)
                        .build();
                List<String[]> rMSAVGRowstemp = csvReader.readAll();
                rMSAVGRows = new ArrayList(rMSAVGRowstemp);
                csvReader.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<String[]> sSRows = null;
        try {
            FileReader filereader = new FileReader(runs.get(0) + File.separator + "simulationSummary.csv");
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(0)
                    .build();
            List<String[]> sSRowstemp = csvReader.readAll();
            sSRows = new ArrayList(sSRowstemp);
            csvReader.close();
        } catch (IOException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<String[]> tTAPRows = null;
        try {
            FileReader filereader = new FileReader(runs.get(0) + File.separator + "travelToAllPOIs.csv");
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(0)
                    .build();
            List<String[]> tTAPRowstemp = csvReader.readAll();
            tTAPRows = new ArrayList(tTAPRowstemp);
            csvReader.close();
        } catch (IOException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
        }

        String testPath = "projects" + File.separator + model.ABM.filePath.substring(model.ABM.filePath.lastIndexOf(File.separator) + 1, model.ABM.filePath.length());

        for (int i = 0; i < runs.size(); i++) {
            try {
                File file = new File(runs.get(i));
                FileUtils.deleteDirectory(file);
            } catch (IOException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String avgFolderName=runs.get(0)+"_A";
        File testDirectory = new File(avgFolderName);
        if (!testDirectory.exists()) {
            testDirectory.mkdirs();
        }

        CsvWriter writer = new CsvWriter();
        try {
            writer.write(new File(avgFolderName + File.separator + "AVD" + ".csv"), Charset.forName("US-ASCII"), aVDRows);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        writer = new CsvWriter();
        try {
            writer.write(new File(avgFolderName + File.separator + "CBGInf" + ".csv"), Charset.forName("US-ASCII"), iNFRows);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        writer = new CsvWriter();
        try {
            writer.write(new File(avgFolderName + File.separator + "infectionReport" + ".csv"), Charset.forName("US-ASCII"), infectionReportRows);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        writer = new CsvWriter();
        try {
            writer.write(new File(avgFolderName + File.separator + "mobilityReport" + ".csv"), Charset.forName("US-ASCII"), mRRows);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        writer = new CsvWriter();
        try {
            writer.write(new File(avgFolderName + File.separator + "NOV" + ".csv"), Charset.forName("US-ASCII"), nOVRows);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        writer = new CsvWriter();
        try {
            writer.write(new File(avgFolderName + File.separator + "PTAVSP" + ".csv"), Charset.forName("US-ASCII"), pTAVSPRows);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        writer = new CsvWriter();
        try {
            if (rMSRows != null) {
                writer.write(new File(avgFolderName + File.separator + "regionMobilitySimilarity" + ".csv"), Charset.forName("US-ASCII"), rMSRows);
            }
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        writer = new CsvWriter();
        try {
            if (rMSAVGRows != null) {
                writer.write(new File(avgFolderName + File.separator + "regionMobilitySimilarity_avg" + ".csv"), Charset.forName("US-ASCII"), rMSAVGRows);
            }
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        writer = new CsvWriter();
        try {
            writer.write(new File(avgFolderName + File.separator + "simulationSummary" + ".csv"), Charset.forName("US-ASCII"), sSRows);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        writer = new CsvWriter();
        try {
            writer.write(new File(avgFolderName + File.separator + "travelToAllPOIs" + ".csv"), Charset.forName("US-ASCII"), tTAPRows);
        } catch (IOException ex) {
            Logger.getLogger(Root.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<String[]> runInfectionReport(ArrayList<String> runs, MainModel model) {
        ArrayList<ArrayList<Double>> dataVals = new ArrayList();
        String[] header = null;
        String[] firstColumn = null;
        for (int i = 0; i < runs.size(); i++) {
            try {
                FileReader filereader = new FileReader(runs.get(i) + File.separator + "infectionReport.csv");
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(0)
                        .build();
                List<String[]> data = csvReader.readAll();
                if (i == 0) {
                    firstColumn = new String[data.size()];
                    header = data.get(0);
                    for (int m = 1; m < data.size(); m++) {
                        ArrayList<Double> row = new ArrayList<Double>();
                        for (int n = 1; n < data.get(m).length; n++) {
                            row.add(Double.valueOf(data.get(m)[n]));
                        }
                        dataVals.add(row);
                        firstColumn[m-1] = data.get(m)[0];
                    }
                } else {
                    for (int m = 1; m < data.size(); m++) {
                        for (int n = 1; n < data.get(m).length; n++) {
                            dataVals.get(m-1).set(n - 1, dataVals.get(m-1).get(n - 1) + Double.valueOf(data.get(m)[n]));
                        }
                    }
                }
                csvReader.close();
            } catch (IOException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CsvException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int m = 0; m < dataVals.size(); m++) {
            for (int n = 0; n < dataVals.get(m).size(); n++) {
                dataVals.get(m).set(n, dataVals.get(m).get(n) / runs.size());
            }
        }
        ArrayList<String[]> rows = new ArrayList();
        rows.add(header);
        for (int m = 0; m < dataVals.size(); m++) {
            String[] row = new String[dataVals.get(m).size() + 1];
            row[0] = firstColumn[m];
            for (int n = 0; n < dataVals.get(m).size(); n++) {
                row[n + 1] = String.valueOf(dataVals.get(m).get(n));
            }
            rows.add(row);
        }
        return rows;
    }

    public ArrayList<String[]> runCBGInf(ArrayList<String> runs, MainModel model) {
        ArrayList<ArrayList<Double>> infs = new ArrayList();
        ArrayList<Double> avgs = new ArrayList();
        ArrayList<Double> vars = new ArrayList();
        String[] header = null;
        String[] firstRow = null;
        String[] secondRow = null;
        for (int i = 0; i < runs.size(); i++) {
            try {
                FileReader filereader = new FileReader(runs.get(i) + File.separator + "CBGInf.csv");
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(0)
                        .build();
                List<String[]> data = csvReader.readAll();
                if (i == 0) {
                    firstRow = new String[data.get(0).length];
                    secondRow = new String[data.get(0).length];
                    header = data.get(0);
                }
                infs.add(new ArrayList<Double>());
                for (int m = 0; m < data.get(0).length; m++) {
                    infs.get(i).add(Double.parseDouble(data.get(1)[m]));

                }
                csvReader.close();
            } catch (IOException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CsvException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ArrayList<Double> sum = new ArrayList();
        for (int i = 0; i < infs.size(); i++) {
            for (int j = 0; j < infs.get(i).size(); j++) {
                if(i==0){
                    sum.add(infs.get(i).get(j));
                }else{
                    sum.set(j,sum.get(j)+infs.get(i).get(j));
                }
            }
        }
        for (int i = 0; i < sum.size(); i++) {
            avgs.add(sum.get(i)/infs.size());
        }
        for (int i = 0; i < infs.size(); i++) {
            for (int j = 0; j < infs.get(i).size(); j++) {
                if(i==0){
                vars.add(Math.pow(infs.get(i).get(j)-avgs.get(j),2));
                }else{
                   vars.set(j,Math.pow(infs.get(i).get(j)-avgs.get(j),2)); 
                }
            }
        }
        for (int i = 0; i < vars.size(); i++) {
            vars.set(i, vars.get(i)/(infs.size()-1));
        }
        for (int i = 0; i < avgs.size(); i++) {
            firstRow[i] = String.valueOf(avgs.get(i));
            secondRow[i] = String.valueOf(vars.get(i));
        }

        ArrayList<String[]> rows = new ArrayList();
        rows.add(header);
        rows.add(firstRow);
        rows.add(secondRow);
        return rows;
    }

    public ArrayList<String[]> runPTAVSP(ArrayList<String> runs, MainModel model) {
        ArrayList<Double> vNM = new ArrayList();
        ArrayList<Double> vM = new ArrayList();
        ArrayList<Double> vMP = new ArrayList();
        double sumVNM = 0;
        double sumVM = 0;
        double sumVMP = 0;
        String[] header = new String[7];
        String[] firstRow = new String[7];
        String[] secondRow = new String[7];
        String[] thirdRow = new String[runs.size()];
        for (int i = 0; i < runs.size(); i++) {
            try {
                FileReader filereader = new FileReader(runs.get(i) + File.separator + "PTAVSP.csv");
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(0)
                        .build();
                List<String[]> data = csvReader.readAll();
                header = data.get(0);
                Double vnm = Double.valueOf(data.get(1)[4]);
                Double vm = Double.valueOf(data.get(1)[5]);
                Double vmp = Double.valueOf(data.get(1)[6]);
                vNM.add(vnm);
                vM.add(vm);
                vMP.add(vmp);
                sumVNM = sumVNM + vnm;
                sumVM = sumVM + vm;
                sumVMP = sumVMP + vmp;
                firstRow[0] = data.get(1)[0];
                firstRow[1] = data.get(1)[1];
                firstRow[2] = data.get(1)[2];
                firstRow[3] = data.get(1)[3];
                thirdRow[i] = String.valueOf(vmp);
                csvReader.close();
            } catch (IOException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CsvException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        double avgVNM = sumVNM / vNM.size();
        double avgVM = sumVM / vM.size();
        double avgVMP = sumVMP / vMP.size();
        firstRow[4] = String.valueOf(sumVNM / vNM.size());
        firstRow[5] = String.valueOf(sumVM / vM.size());
        firstRow[6] = String.valueOf(sumVMP / vMP.size());
        double varVNM = 0;
        double varVM = 0;
        double varVMP = 0;
        for (int i = 0; i < vNM.size(); i++) {
            varVNM = varVNM + Math.pow(vNM.get(i) - avgVNM, 2);
        }
        for (int i = 0; i < vM.size(); i++) {
            varVM = varVM + Math.pow(vM.get(i) - avgVM, 2);
        }
        for (int i = 0; i < vMP.size(); i++) {
            varVMP = varVMP + Math.pow(vMP.get(i) - avgVMP, 2);
        }
        varVNM = varVNM / (vNM.size() - 1);
        varVM = varVM / (vM.size() - 1);
        varVMP = varVMP / (vMP.size() - 1);
        secondRow[4] = String.valueOf(varVNM);
        secondRow[5] = String.valueOf(varVM);
        secondRow[6] = String.valueOf(varVMP);

        ArrayList<String[]> rows = new ArrayList();
        rows.add(header);
        rows.add(firstRow);
        rows.add(secondRow);
        rows.add(thirdRow);
        return rows;
    }

    public ArrayList<String[]> runNOV(ArrayList<String> runs, MainModel model) {
        ArrayList<Double> nOVs = new ArrayList();
        double sum = 0;
        String[] header = new String[3];
        String[] firstRow = new String[3];
        String[] secondRow = new String[3];
        String[] thirdRow = new String[runs.size()];
        for (int i = 0; i < runs.size(); i++) {
            try {
                FileReader filereader = new FileReader(runs.get(i) + File.separator + "NOV.csv");
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(0)
                        .build();
                List<String[]> data = csvReader.readAll();
                header = data.get(0);
                Double nOV = Double.valueOf(data.get(1)[2]);
                nOVs.add(nOV);
                sum = sum + nOV;
                firstRow[0] = data.get(1)[0];
                firstRow[1] = data.get(1)[1];
                thirdRow[i] = String.valueOf(nOV);
                csvReader.close();
            } catch (IOException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CsvException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        double avg = sum / nOVs.size();
        firstRow[2] = String.valueOf(avg);
        double var = 0;
        for (int i = 0; i < nOVs.size(); i++) {
            var = var + Math.pow(nOVs.get(i) - avg, 2);
        }
        var = var / (nOVs.size() - 1);
        secondRow[2] = String.valueOf(var);

        ArrayList<String[]> rows = new ArrayList();
        rows.add(header);
        rows.add(firstRow);
        rows.add(secondRow);
        rows.add(thirdRow);
        return rows;
    }

    public ArrayList<String[]> runAVD(ArrayList<String> runs, MainModel model) {
        ArrayList<Double> aVDs = new ArrayList();
        double sum = 0;
        String[] header = new String[3];
        String[] firstRow = new String[3];
        String[] secondRow = new String[3];
        String[] thirdRow = new String[runs.size()];
        for (int i = 0; i < runs.size(); i++) {
            try {
                FileReader filereader = new FileReader(runs.get(i) + File.separator + "AVD.csv");
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(0)
                        .build();
                List<String[]> data = csvReader.readAll();
                header = data.get(0);
                Double aVd = Double.valueOf(data.get(1)[2]);
                aVDs.add(aVd);
                sum = sum + aVd;
                firstRow[0] = data.get(1)[0];
                firstRow[1] = data.get(1)[1];
                thirdRow[i] = String.valueOf(aVd);
                csvReader.close();
            } catch (IOException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CsvException ex) {
                Logger.getLogger(BatchRun.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        double avg = sum / aVDs.size();
        firstRow[2] = String.valueOf(avg);
        double var = 0;
        for (int i = 0; i < aVDs.size(); i++) {
            var = var + Math.pow(aVDs.get(i) - avg, 2);
        }
        var = var / (aVDs.size() - 1);
        secondRow[2] = String.valueOf(var);

        ArrayList<String[]> rows = new ArrayList();
        rows.add(header);
        rows.add(firstRow);
        rows.add(secondRow);
        rows.add(thirdRow);
        return rows;
    }

}
