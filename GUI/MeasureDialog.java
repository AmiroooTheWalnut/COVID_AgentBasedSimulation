/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package COVID_AgentBasedSimulation.GUI;

import COVID_AgentBasedSimulation.Model.HistoricalRun;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.Scope;
import de.siegmar.fastcsv.writer.CsvWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class MeasureDialog extends javax.swing.JDialog {

    MainFrame myParent;

    String baseDirectory;
    String selectedLeftDirectory;
    String selectedRightDirectory;
    String[] allDirectories;

    /**
     * Creates new form SABDialog
     */
    public MeasureDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        myParent = (MainFrame) parent;

        String filePath = myParent.mainModel.ABM.filePath;
        baseDirectory = "projects" + File.separator + filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.length());
        File directory = new File(baseDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
            return;
        }

        allDirectories = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        jLabel2.setText(filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.length()));

        jList1.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return allDirectories.length;
            }

            @Override
            public Object getElementAt(int index) {
                return allDirectories[index];
            }
        });
        jList2.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return allDirectories.length;
            }

            @Override
            public Object getElementAt(int index) {
                return allDirectories[index];
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton1.setText("Get SAB");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Similarity save output");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Assuming left is larger.");

        jLabel4.setText("Right side is CBG.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jButton2)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new java.awt.GridLayout(1, 2));

        jScrollPane1.setViewportView(jList1);

        jPanel2.add(jScrollPane1);

        jScrollPane2.setViewportView(jList2);

        jPanel2.add(jScrollPane2);

        jLabel1.setText("Case study:");

        jLabel2.setText("Empty");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int leftIndex = jList1.getSelectedIndex();
        int rightIndex = jList2.getSelectedIndex();
        if (leftIndex != -1 && rightIndex != -1) {
            HashMap<Long, Double> leftConvertedCBGInfected = new HashMap();
            HashMap<Long, Double> rightConvertedCBGInfected = new HashMap();
            selectedLeftDirectory = allDirectories[leftIndex];
            selectedRightDirectory = allDirectories[rightIndex];
            HistoricalRun leftHR = HistoricalRun.loadHistoricalRunKryo(baseDirectory + File.separator + selectedLeftDirectory + File.separator + "data.bin");
            HistoricalRun rightHR = HistoricalRun.loadHistoricalRunKryo(baseDirectory + File.separator + selectedRightDirectory + File.separator + "data.bin");
            for (int i = 0; i < leftHR.regions.size(); i++) {
                for (int k = 0; k < leftHR.regions.get(i).hourlyRegionSnapshot.size(); k++) {
                    for (int j = 0; j < leftHR.regions.get(i).cBGsPercentageInvolved.size(); j++) {
                        if (leftConvertedCBGInfected.containsKey(leftHR.regions.get(i).cBGsIDsInvolved.get(j)) == true) {
                            double a = leftHR.regions.get(i).cBGsPercentageInvolved.get(j) * (leftHR.regions.get(i).hourlyRegionSnapshot.get(k).IAS + leftHR.regions.get(i).hourlyRegionSnapshot.get(k).IS);
                            double b = leftConvertedCBGInfected.get(leftHR.regions.get(i).cBGsIDsInvolved.get(j));
                            leftConvertedCBGInfected.put(leftHR.regions.get(i).cBGsIDsInvolved.get(j), a + b);
                        } else {
                            leftConvertedCBGInfected.put(leftHR.regions.get(i).cBGsIDsInvolved.get(j), leftHR.regions.get(i).cBGsPercentageInvolved.get(j) * (leftHR.regions.get(i).hourlyRegionSnapshot.get(k).IAS + leftHR.regions.get(i).hourlyRegionSnapshot.get(k).IS));
                        }
                    }
                }
            }
            for (int i = 0; i < rightHR.regions.size(); i++) {
                for (int k = 0; k < rightHR.regions.get(i).hourlyRegionSnapshot.size(); k++) {
                    for (int j = 0; j < rightHR.regions.get(i).cBGsPercentageInvolved.size(); j++) {
                        if (rightConvertedCBGInfected.containsKey(rightHR.regions.get(i).cBGsIDsInvolved.get(j)) == true) {
                            double a = rightHR.regions.get(i).cBGsPercentageInvolved.get(j) * (rightHR.regions.get(i).hourlyRegionSnapshot.get(k).IAS + rightHR.regions.get(i).hourlyRegionSnapshot.get(k).IS);
                            double b = rightConvertedCBGInfected.get(rightHR.regions.get(i).cBGsIDsInvolved.get(j));
                            rightConvertedCBGInfected.put(rightHR.regions.get(i).cBGsIDsInvolved.get(j), a + b);
                        } else {
                            rightConvertedCBGInfected.put(rightHR.regions.get(i).cBGsIDsInvolved.get(j), rightHR.regions.get(i).cBGsPercentageInvolved.get(j) * (rightHR.regions.get(i).hourlyRegionSnapshot.get(k).IAS + rightHR.regions.get(i).hourlyRegionSnapshot.get(k).IS));
                        }
                    }
                }
            }
            double SAB = 0;
            for (Map.Entry<Long, Double> entry : leftConvertedCBGInfected.entrySet()) {
                Long key = entry.getKey();
                Double valueL = entry.getValue();
                Double valueR = rightConvertedCBGInfected.get(key);
                SAB = SAB + Math.abs(valueL - valueR);
            }

            System.out.println(SAB);

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int leftIndex = jList1.getSelectedIndex();
        int rightIndex = jList2.getSelectedIndex();
        if (leftIndex != -1 && rightIndex != -1) {
            selectedLeftDirectory = allDirectories[leftIndex];
            selectedRightDirectory = allDirectories[rightIndex];
            HistoricalRun leftHR = HistoricalRun.loadHistoricalRunKryo(baseDirectory + File.separator + selectedLeftDirectory + File.separator + "data.bin");
            HistoricalRun rightHR = HistoricalRun.loadHistoricalRunKryo(baseDirectory + File.separator + selectedRightDirectory + File.separator + "data.bin");
            ArrayList<HashMap<Long, Double>> infectionsInsideRegion = new ArrayList();
            for (int i = 0; i < leftHR.regions.size(); i++) {
                HashMap<Long, Double> infs = new HashMap();
                for (int j = 0; j < leftHR.regions.get(i).cBGsIDsInvolved.size(); j++) {
                    for (int m = 0; m < rightHR.regions.size(); m++) {
                        for (int k = 0; k < leftHR.regions.get(i).hourlyRegionSnapshot.size(); k++) {
                            if (leftHR.regions.get(i).cBGsIDsInvolved.get(j).equals(rightHR.regions.get(m).cBGsIDsInvolved.get(0))) {
                                if (infs.containsKey(leftHR.regions.get(i).cBGsIDsInvolved.get(j)) == true) {
                                    double a = leftHR.regions.get(i).cBGsPercentageInvolved.get(j) * (rightHR.regions.get(i).hourlyRegionSnapshot.get(k).IAS + rightHR.regions.get(i).hourlyRegionSnapshot.get(k).IS);
                                    double b = infs.get(leftHR.regions.get(i).cBGsIDsInvolved.get(j));
                                    double pop = ((Scope)(myParent.mainModel.ABM.studyScopeGeography)).findCBG(rightHR.regions.get(m).cBGsIDsInvolved.get(0)).population;
                                    infs.put(leftHR.regions.get(i).cBGsIDsInvolved.get(j), (a + b)/pop);
                                } else {
                                    double pop = ((Scope)(myParent.mainModel.ABM.studyScopeGeography)).findCBG(rightHR.regions.get(m).cBGsIDsInvolved.get(0)).population;
                                    infs.put(leftHR.regions.get(i).cBGsIDsInvolved.get(j), (leftHR.regions.get(i).cBGsPercentageInvolved.get(j) * (rightHR.regions.get(i).hourlyRegionSnapshot.get(k).IAS + rightHR.regions.get(i).hourlyRegionSnapshot.get(k).IS))/pop);
                                }
                            }
                        }
                    }
                }
                infectionsInsideRegion.add(infs);
            }
            CsvWriter writer = new CsvWriter();
            ArrayList<String[]> rows = new ArrayList();
            String[] header = new String[3];
            header[0] = "Region";
            header[1] = "CBG";
            header[2] = "Infection";
            rows.add(header);
            for (int i = 0; i < infectionsInsideRegion.size(); i++) {
                for (Map.Entry<Long, Double> entry : infectionsInsideRegion.get(i).entrySet()) {
                    String[] row = new String[3];
                    Long key = entry.getKey();
                    row[0] = String.valueOf(i);
                    row[1] = String.valueOf(key);
                    row[2] = String.valueOf(entry.getValue());
                    rows.add(row);
                }
            }
            try {
                writer.write(new File("foldedInfection.csv"), Charset.forName("US-ASCII"), rows);
            } catch (IOException ex) {
                Logger.getLogger(MeasureDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
