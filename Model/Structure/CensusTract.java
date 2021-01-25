/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Structure;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class CensusTract {
    public int id;
    public float size;
    ArrayList<CensusBlock> censusBlocks;
    
    public boolean isNewCensusBlockUnique(long input) {
        if (censusBlocks == null) {
            censusBlocks = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < censusBlocks.size(); i++) {
                if (censusBlocks.get(i).id==input) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public CensusBlock findCensusBlock(long input) {
        if (censusBlocks == null) {
            censusBlocks = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < censusBlocks.size(); i++) {
                if (censusBlocks.get(i).id==input) {
                    return censusBlocks.get(i);
                }
            }
        }
        return null;
    }
    
    public CensusBlock findAndInsertCensusBlock(long input) {
        if (censusBlocks == null) {
            censusBlocks = new ArrayList();
            CensusBlock temp = new CensusBlock();
            temp.id = input;
            censusBlocks.add(temp);
            return censusBlocks.get(0);
        } else {
            for (int i = 0; i < censusBlocks.size(); i++) {
                if (censusBlocks.get(i).id==input) {
                    return censusBlocks.get(i);
                }
            }
        }
        CensusBlock temp = new CensusBlock();
        temp.id = input;
        censusBlocks.add(temp);
        return censusBlocks.get(censusBlocks.size()-1);
    }
    
    
    
}
