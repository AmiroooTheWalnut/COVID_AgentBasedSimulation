/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author user
 */
public class LegendRange extends JPanel {

    public double max = 0;
    public double min = 0;

    @Override
    public void paintComponent(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        int margin = 5;

        int effectiveHeight = height - (2 * margin);
        int effectiveWidth = width - (8 * margin);
        if (effectiveHeight > 0) {
            int maxTextHeight=2*margin;
            int minTextHeight=effectiveHeight;
            int midTextHeight=(minTextHeight-maxTextHeight)/2;
            for (int i = 0; i < effectiveHeight; i++) {
                g.setColor(new Color(255 - (int)(((double)i / (double)effectiveHeight) * 255), 0, (int)(((double)i / (double)effectiveHeight) * 255)));
                g.fillRect(margin, margin + i, effectiveWidth, 1);
            }
            g.drawString(String.valueOf(max), width - (7 * margin), maxTextHeight);
            g.drawString(String.valueOf((double)(max+min)/(double)2), width - (7 * margin), midTextHeight);
            g.drawString(String.valueOf(min), width - (7 * margin), minTextHeight);
        }
    }

}
