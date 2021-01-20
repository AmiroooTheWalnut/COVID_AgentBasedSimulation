package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class Patterns {
    public ArrayList<PatternsRecordRaw> records;
    public ArrayList<PatternsRecordProcessed> recordsProcessed;
    
    public void readData(){
        String GISDataFileName = this.caseStudies.get(caseStudyIndex).name + "_GIS_data.csv";
        File GISDataFile = new File(GISDataFileName);
        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(GISDataFile, StandardCharsets.UTF_8);
            this.latLons = new Location[data.getRows().size()];
            this.status = new int[this.latLons.length];
            this.population = new float[data.getRows().size()];
            this.density = new float[data.getRows().size()];
            this.adjustedProbability = new float[latLons.length];//NEED TO BE LOADED LATER
            for (int j = 0; j < data.getRows().size(); j++) {
                try {
                    this.latLons[j] = new Location(Float.parseFloat(((CsvRow) data.getRows().get(j)).getFields().get(1)), Float.parseFloat(((CsvRow) data.getRows().get(j)).getFields().get(2)));
                } catch (Exception ex) {

                }
                try {
                    this.population[j] = Float.parseFloat((String) ((CsvRow) data.getRows().get(j)).getFields().get(3));
                } catch (Exception ex) {

                }
                try {
                    this.density[j] = Float.parseFloat((String) ((CsvRow) data.getRows().get(j)).getFields().get(4));
                } catch (Exception ex) {

                }
            }
            this.parent.jToggleButton1.setEnabled(true);

            this.populationNormalized = normalizeVector(this.population);
            this.densityNormalized = normalizeVector(this.density);
            this.parent.jToggleButton3.setEnabled(true);
            this.parent.jToggleButton4.setEnabled(true);
        } catch (IOException ex) {
            Logger.getLogger(COVIDGeoVisualization.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
    }
    
}
