/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import java.io.Serializable;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class Brands implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public ArrayList<Brand> brands;
    
    public Brand findAndInsertCategory(String input) {
        if (brands == null) {
            brands = new ArrayList();
            Brand temp = new Brand();
            temp.name = input;
            brands.add(temp);
            return brands.get(0);
        } else {
            for (int i = 0; i < brands.size(); i++) {
                if (brands.get(i).name.equals(input)) {
                    return brands.get(i);
                }
            }
        }
        Brand temp = new Brand();
        temp.name = input;
        brands.add(temp);
        return brands.get(brands.size()-1);
    }
}
