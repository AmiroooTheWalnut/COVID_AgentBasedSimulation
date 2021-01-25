/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.Country;
import COVID_AgentBasedSimulation.Model.Structure.State;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class CorePlaces {
    
    
    //ON HOLD!
    public static void appendBaseGISData(AllGISData allGISData,String corePlacesFile){
        File patternFile = new File(corePlacesFile);
        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(patternFile, StandardCharsets.UTF_8);
            List<String> header = data.getHeader();
            for (int i = 0; i < data.getRowCount(); i++) {
//                List<String> oneRow = data.getRow(i).getFields();
                Country country=allGISData.findAndInsertCountry(data.getRow(0).getField("iso_country_code"));
                State state=country.findAndInsertState(data.getRow(0).getField("region"));
//                state.findAndInsertCounty(i)
            }
        } catch (IOException ex) {
            Logger.getLogger(Patterns.class.getName()).log(Level.SEVERE, (String) null, ex);
        }
    }
}
