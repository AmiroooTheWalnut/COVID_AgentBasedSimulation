/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.HardcodedSimulator;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.DwellTime;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Shamil.ShamilPersonProperties;

/**
 *
 * @author user
 */
public class PersonProperties {
    
    public static double infectionDistances[] = {0.125825731, 0.147845234, 0.209185278, 0.264234036, 0.309845863, 0.350739226, 0.388486946, 0.424661843, 0.462409563, 0.500157282, 0.539477823, 0.581944008, 0.629128657, 0.677886128, 0.736080528, 0.802139037, 0.877634476, 0.896508336, 0.959421202, 0.979867883, 1.04435357, 1.066373073, 1.135577226, 1.157596729, 1.226800881, 1.248820384, 1.318024536, 1.340044039, 1.412393835, 1.432840516, 1.50361749, 1.525636993, 1.607423718, 1.698647373, 1.79144385, 1.884240327, 1.975463982, 2.068260459, 2.161056936, 2.252280591, 2.343504247, 2.436300723};
    public static double infectionProbabilities[] = {0.629424019, 0.627712467, 0.605544261, 0.568491183, 0.528141159, 0.485879701, 0.443518805, 0.40159828, 0.35807253, 0.315569579, 0.273876342, 0.231813762, 0.190889203, 0.153456048, 0.117188034, 0.085150685, 0.058392628, 0.05161957, 0.038360245, 0.034925089, 0.024547303, 0.021799178, 0.015218142, 0.01384408, 0.009504936, 0.008805851, 0.006561301, 0.005888982, 0.004153324, 0.003502453, 0.001911433, 0.001615923, 0.001311433, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923, 0.001115923};
    
    public Region homeRegion;
    public Region workRegion;
    public ShamilPersonProperties shamilProperties;
    
    public PatternsRecordProcessed currentPattern;
    
    public int status;
    
    public int minutesStayed;
    public DwellTime dwellTime;
    public boolean isAtWork=false;
    public boolean isAtHome=true;
    public boolean isInTravel;
    public boolean didTravelFromHome;
    public boolean didTravelFromWork;
    public int minutesSick;
    public boolean isDestinedToDeath=false;
    
    public int minutesTravelToWorkFrom7;
    public int minutesTravelFromWorkFrom16;
    
    public boolean isInitiallyInfectedEnteringPOI=false;
    
    public POI currentPOI;
    
}
