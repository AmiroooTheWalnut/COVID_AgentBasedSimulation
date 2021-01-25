package COVID_AgentBasedSimulation.Model.Structure;

import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author user
 */
public class State {

    public String name;
    public byte id;
    public float lat;
    public float lon;
    public ArrayList<County> counties;

    public boolean isNewCountyUnique(String input) {
        if (counties == null) {
            counties = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).name.equals(input)) {
                    return false;
                }
            }
        }
        return true;
    }

    public County findCounty(String input) {
        if (counties == null) {
            counties = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).name.equals(input)) {
                    return counties.get(i);
                }
            }
        }
        return null;
    }

    public County findAndInsertCounty(String input) {
        if (counties == null) {
            counties = new ArrayList();
            County temp = new County();
            temp.name = input;
            counties.add(temp);
            return counties.get(0);
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (!counties.get(i).name.equals("NULL")) {
                    if (counties.get(i).name.equals(input)) {
                        return counties.get(i);
                    }
                } else {
                    return null;
                }
            }
        }
        County temp = new County();
        temp.name = input;
        counties.add(temp);
        return counties.get(counties.size() - 1);
    }

    public boolean isNewCountyUnique(int input) {
        if (counties == null) {
            counties = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).fipsCode == input) {
                    return false;
                }
            }
        }
        return true;
    }

    public County findCounty(int input) {
        if (counties == null) {
            counties = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).fipsCode == input) {
                    return counties.get(i);
                }
            }
        }
        return null;
    }

    public County findAndInsertCounty(int input) {
        if (counties == null) {
            counties = new ArrayList();
            County temp = new County();
            temp.fipsCode = input;
            counties.add(temp);
            return counties.get(0);
        } else {
            for (int i = 0; i < counties.size(); i++) {
                if (counties.get(i).fipsCode == input) {
                    return counties.get(i);
                }
            }
        }
        County temp = new County();
        temp.fipsCode = input;
        counties.add(temp);
        return counties.get(counties.size() - 1);
    }

}
