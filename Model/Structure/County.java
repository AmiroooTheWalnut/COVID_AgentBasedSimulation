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
public class County {
    public String name;
    public int id;
    public float lat;
    public float lon;
    public float size;
    public ArrayList<City> cities;
    public ArrayList<ZipCode> zipcodes;
    public ArrayList<CensusTract> censusTracts;
    
    
    public int fipsCode;
    
    public boolean isNewCityUnique(String input) {
        if (cities == null) {
            cities = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(input)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isNewZipCodeUnique(int input) {
        if (zipcodes == null) {
            zipcodes = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < zipcodes.size(); i++) {
                if (zipcodes.get(i).code==input) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isNewCensusTractUnique(int input) {
        if (censusTracts == null) {
            censusTracts = new ArrayList();
            return true;
        } else {
            for (int i = 0; i < censusTracts.size(); i++) {
                if (censusTracts.get(i).id==input) {
                    return false;
                }
            }
        }
        return true;
    }
    
    
    
    public City findCity(String input) {
        if (cities == null) {
            cities = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(input)) {
                    return cities.get(i);
                }
            }
        }
        return null;
    }
    
    public ZipCode findZipCode(int input) {
        if (zipcodes == null) {
            zipcodes = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < zipcodes.size(); i++) {
                if (zipcodes.get(i).code==input) {
                    return zipcodes.get(i);
                }
            }
        }
        return null;
    }
    
    public CensusTract findCensusTract(int input) {
        if (censusTracts == null) {
            censusTracts = new ArrayList();
            return null;
        } else {
            for (int i = 0; i < censusTracts.size(); i++) {
                if (censusTracts.get(i).id==input) {
                    return censusTracts.get(i);
                }
            }
        }
        return null;
    }
    
    
    
    public City findAndInsertCity(String input) {
        if (cities == null) {
            cities = new ArrayList();
            City temp = new City();
            temp.name = input;
            cities.add(temp);
            return cities.get(0);
        } else {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(input)) {
                    return cities.get(i);
                }
            }
        }
        City temp = new City();
        temp.name = input;
        cities.add(temp);
        return cities.get(cities.size()-1);
    }
    
    public ZipCode findAndInsertZipCode(int input) {
        if (zipcodes == null) {
            zipcodes = new ArrayList();
            ZipCode temp = new ZipCode();
            temp.code = input;
            zipcodes.add(temp);
            return zipcodes.get(0);
        } else {
            for (int i = 0; i < zipcodes.size(); i++) {
                if (zipcodes.get(i).code==input) {
                    return zipcodes.get(i);
                }
            }
        }
        ZipCode temp = new ZipCode();
        temp.code = input;
        zipcodes.add(temp);
        return zipcodes.get(zipcodes.size()-1);
    }
    
    public CensusTract findAndInsertCensusTract(int input) {
        if (censusTracts == null) {
            censusTracts = new ArrayList();
            CensusTract temp = new CensusTract();
            temp.id = input;
            censusTracts.add(temp);
            return censusTracts.get(0);
        } else {
            for (int i = 0; i < censusTracts.size(); i++) {
                if (censusTracts.get(i).id==input) {
                    return censusTracts.get(i);
                }
            }
        }
        CensusTract temp = new CensusTract();
        temp.id = input;
        censusTracts.add(temp);
        return censusTracts.get(censusTracts.size()-1);
    }
    
    
    
    
}
