/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class DwellTime implements Serializable {
    static final long serialVersionUID = softwareVersion;
    public int number;
    public short[] dwellDuration;//FIRST INDEX IS START SECOND INDEX IS END FOR THE RANGE OF DWELL TIME IN MINUTES

    public static short[] getDwellDuration(String input) {
        if (input.length() > 0) {
            if (input.equals("<5")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 0;
                dwellTime[1] = 5;
                return dwellTime;
            } else if (input.equals("5-20")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 5;
                dwellTime[1] = 20;
                return dwellTime;
            } else if (input.equals("21-60")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 21;
                dwellTime[1] = 60;
                return dwellTime;
            } else if (input.equals("61-240")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 61;
                dwellTime[1] = 240;
                return dwellTime;
            } else if (input.equals(">240")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 240;
                dwellTime[1] = 1440;
                return dwellTime;
            } else if (input.equals("5-10")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 5;
                dwellTime[1] = 10;
                return dwellTime;
            } else if (input.equals("11-20")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 11;
                dwellTime[1] = 20;
                return dwellTime;
            } else if (input.equals("21-60")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 21;
                dwellTime[1] = 60;
                return dwellTime;
            } else if (input.equals("61-120")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 61;
                dwellTime[1] = 120;
                return dwellTime;
            } else if (input.equals("121-240")) {
                short[] dwellTime = new short[2];
                dwellTime[0] = 121;
                dwellTime[1] = 240;
                return dwellTime;
            }
            System.out.println("DWELLING ITEM NOT FOUND: " + input);
            return null;
        } else {
            return null;
        }
    }
}
