/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;

/**
 *
 * @author user
 */
public class RegionImageLayer implements Serializable {

    static final long serialVersionUID = softwareVersion;

    public int[][] indexedImage;
    public long[] cBGIndexs;
    public boolean[][] imageBoundaries;
    public double severities[];

    public double startLat;
    public double endLat;
    public double startLon;
    public double endLon;

    public static boolean[][] getImageBoundaries(int[][] input) {
        boolean[][] output = new boolean[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                int selfValue = input[i][j];
                if (selfValue > 0) {
                    int values[] = new int[8];
                    for (int k = 0; k < 8; k++) {
                        values[k] = -1;
                    }
                    if (i == 0) {
                        if (j == 0) {
                            values[2] = input[i + 1][j];//E
                            values[3] = input[i + 1][j + 1];//SE
                            values[4] = input[i][j + 1];//S
                        } else if (j == input[0].length - 1) {
                            values[0] = input[i][j - 1];//N
                            values[1] = input[i + 1][j - 1];//NE
                            values[2] = input[i + 1][j];//E
                        } else {
                            values[0] = input[i][j - 1];//N
                            values[1] = input[i + 1][j - 1];//NE
                            values[2] = input[i + 1][j];//E
                            values[3] = input[i + 1][j + 1];//SE
                            values[4] = input[i][j + 1];//S
                        }
                    } else if (i == input.length - 1) {
                        if (j == 0) {
                            values[4] = input[i][j + 1];//S
                            values[5] = input[i - 1][j + 1];//SW
                            values[6] = input[i - 1][j];//W
                        } else if (j == input[0].length - 1) {
                            values[6] = input[i - 1][j];//W
                            values[7] = input[i - 1][j - 1];//NW
                            values[0] = input[i][j - 1];//N
                        } else {
                            values[4] = input[i][j + 1];//S
                            values[5] = input[i - 1][j + 1];//SW
                            values[6] = input[i - 1][j];//W
                            values[7] = input[i - 1][j - 1];//NW
                            values[0] = input[i][j - 1];//N
                        }
                    } else {
                        if (j == 0) {
                            values[2] = input[i + 1][j];//E
                            values[3] = input[i + 1][j + 1];//SE
                            values[4] = input[i][j + 1];//S
                            values[5] = input[i - 1][j + 1];//SW
                            values[6] = input[i - 1][j];//W
                        } else if (j == input[0].length - 1) {
                            values[6] = input[i - 1][j];//W
                            values[7] = input[i - 1][j - 1];//NW
                            values[0] = input[i][j - 1];//N
                            values[1] = input[i + 1][j - 1];//NE
                            values[2] = input[i + 1][j];//E
                        } else {
                            values[0] = input[i][j - 1];//N
                            values[1] = input[i + 1][j - 1];//NE
                            values[2] = input[i + 1][j];//E
                            values[3] = input[i + 1][j + 1];//SE
                            values[4] = input[i][j + 1];//S
                            values[5] = input[i - 1][j + 1];//SW
                            values[6] = input[i - 1][j];//W
                            values[7] = input[i - 1][j - 1];//NW
                        }
                    }
                    for(int h=0;h<values.length;h++){
                        if(values[h]>-1){
                            if(values[h]!=selfValue){
                                output[i][j]=true;
                            }
                        }
                    }
                }
            }
        }
        return output;
    }

}
