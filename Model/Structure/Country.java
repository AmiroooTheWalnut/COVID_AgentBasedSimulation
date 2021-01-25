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
public class Country {

    public String name;
    public float lat;
    public float lon;
    ArrayList<State> states;

    public boolean isNewStateUnique(String input) {
        if (states == null) {
            states = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).name.equals(input)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isNewStateUnique(byte input) {
        if (states == null) {
            states = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).id==input) {
                    return false;
                }
            }
        }
        return true;
    }

    public State findState(String input) {
        if (states == null) {
            states = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).name.equals(input)) {
                    return states.get(i);
                }
            }
        }
        return null;
    }
    
    public State findState(byte input) {
        if (states == null) {
            states = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).id==input) {
                    return states.get(i);
                }
            }
        }
        return null;
    }

    public State findAndInsertState(String input) {
        if (states == null) {
            states = new ArrayList();
            State temp = new State();
            temp.name = input;
            states.add(temp);
            return states.get(0);
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).name.equals(input)) {
                    return states.get(i);
                }
            }
        }
        State temp = new State();
        temp.name = input;
        states.add(temp);
        return states.get(states.size()-1);
    }
    
    public State findAndInsertState(byte input) {
        if (states == null) {
            states = new ArrayList();
            State temp = new State();
            temp.id = input;
            states.add(temp);
            return states.get(0);
        } else {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).id==input) {
                    return states.get(i);
                }
            }
        }
        State temp = new State();
        temp.id = input;
        states.add(temp);
        return states.get(states.size()-1);
    }

}
