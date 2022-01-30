/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu;

import java.time.ZonedDateTime;
import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.Structure.County;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter
@Setter
public class DailyConfirmedCases implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public ZonedDateTime date;
    public int cumulativeCases;
    public int numDailyCases;
    public int numActiveCases;
    public County county;
    
    public DailyConfirmedCases(){
        
    }
    
    public DailyConfirmedCases(ZonedDateTime passed_date, int passed_numDailyCases, County passed_county){
        date=passed_date;
        cumulativeCases=passed_numDailyCases;
        county=passed_county;
    }
    
}
