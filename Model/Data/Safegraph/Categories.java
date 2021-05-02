/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Data.Safegraph;

import static COVID_AgentBasedSimulation.Model.MainModel.softwareVersion;
import COVID_AgentBasedSimulation.Model.Structure.City;
import java.io.Serializable;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
@Getter @Setter
public class Categories implements Serializable {
    static final long serialVersionUID = softwareVersion;
    
    public ArrayList<Category> categories;
    
    public Category findAndInsertCategory(String input) {
        if (categories == null) {
            categories = new ArrayList();
            Category temp = new Category();
            temp.name = input;
            categories.add(temp);
            return categories.get(0);
        } else {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).name.equals(input)) {
                    return categories.get(i);
                }
            }
        }
        Category temp = new Category();
        temp.name = input;
        categories.add(temp);
        return categories.get(categories.size()-1);
    }
    
}
