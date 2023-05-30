/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package COVID_AgentBasedSimulation.GUI.SafegraphPreprocessor;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.AllPatterns;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.AllSafegraphPlaces;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Safegraph;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlaces;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.City;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class SafeGraphNewPreprocessorDialog extends javax.swing.JDialog {

    public MainFrame myParent;
    public MainModel mainModel;

    public String[] patternsList;

    /**
     * Creates new form SafeGraphNewPreprocessorDialog
     */
    public SafeGraphNewPreprocessorDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        myParent = (MainFrame) parent;
        mainModel = myParent.mainModel;
        refreshAllPlacesList();
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
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jButton5 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jButton6 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jSpinner1 = new javax.swing.JSpinner();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Process compress"));

        jButton1.setText("Run manual");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Loaded month"));

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton2.setText("Load full scale kryo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Save restricted area");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Load full scale serializable");

        jButton7.setText("Total mobility");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel2.setText("Total mobility");

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Manual");

        jButton9.setText("Load pattern places study");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Report fracs");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Report total mobility");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 124, Short.MAX_VALUE)
                .addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton7)
                    .addComponent(jLabel2)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton9)
                    .addComponent(jButton10)
                    .addComponent(jButton11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Load Places"));

        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane2.setViewportView(jList2);

        jButton5.setText("Load");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jList3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane3.setViewportView(jList3);

        jButton6.setText("Building area");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel1.setText("Number of samples:");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("0 means no sampling and a number more than places means no sampling");
        jScrollPane4.setViewportView(jTextArea1);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton6)
                        .addComponent(jLabel1)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        jButton8.setText("CBG_shop matrix");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton8))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ManualCompressNewCorePatterns manualCompressNewCorePatterns = new ManualCompressNewCorePatterns(myParent, false);
        manualCompressNewCorePatterns.setVisible(true);
        manualCompressNewCorePatterns.refreshList();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (jList3.getSelectedIndex() != -1) {
            SafegraphPlaces.connectToOSMBuildingAreaLevels(mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(jList3.getSelectedIndex()).placesRecords, (int) jSpinner1.getValue());
            Safegraph.saveSafegraphPlacesKryo("./datasets/Safegraph/FullData/" + jList3.getSelectedValue() + "/processedData_withArea", mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(jList3.getSelectedIndex()));
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (jList2.getSelectedIndex() > -1) {
            myParent.mainModel.safegraph.clearPatternsPlaces();
            SafegraphPlaces safegraphPlaces = Safegraph.loadSafegraphPlacesKryo("./datasets/Safegraph/FullData/" + jList2.getSelectedValue() + "/processedData.bin");
//            SafegraphPlaces safegraphPlaces1 = Safegraph.loadSafegraphPlacesKryo("./datasets/Safegraph/FullData/" + jList1.getSelectedValue() + "/processedData_withArea.bin");
            boolean isUnique = true;
            if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList != null) {
                for (int i = 0; i < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size(); i++) {
                    if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).name.equals(safegraphPlaces.name)) {
                        isUnique = false;
                    }
                }
            } else {
                myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList = new ArrayList();
            }
            if (isUnique == true) {
                myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.add(safegraphPlaces);
                refreshLoadedPlacesList();
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ManualLoadNewPatternsPlacesDialog manualLoadNewPatternsPlacesDialog = new ManualLoadNewPatternsPlacesDialog(myParent, false, "FullData", this);
        manualLoadNewPatternsPlacesDialog.setVisible(true);
        manualLoadNewPatternsPlacesDialog.refreshList();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (jList1.getSelectedIndex() != -1) {
            int totalMobility = 0;
            for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
                for (int j = 0; j < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                    totalMobility = totalMobility + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).raw_visit_counts;
                }
            }
            jLabel2.setText(String.valueOf(totalMobility));
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (jCheckBox1.isSelected()) {
            String directoryPath = "./datasets/Safegraph/" + mainModel.ABM.studyScope;
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            if (jList1.getSelectedIndex() > -1) {
                directoryPath = "./datasets/Safegraph/" + mainModel.ABM.studyScope + "/" + jList1.getSelectedValue();
                directory = new File(directoryPath);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                Patterns patterns = Safegraph.getSubPattern(mainModel.ABM.studyScopeGeography, mainModel.safegraph.allPatterns.monthlyPatternsList.get(jList1.getSelectedIndex()));
                Safegraph.savePatternsKryo(directoryPath + "/processedData", patterns);

                if (jList3.getSelectedIndex() > -1) {
                    directoryPath = "./datasets/Safegraph/" + mainModel.ABM.studyScope + "/" + jList3.getSelectedValue();
                    directory = new File(directoryPath);
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    SafegraphPlaces safegraphPlaces = Safegraph.getSubPlace(mainModel.ABM.studyScopeGeography, mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(jList3.getSelectedIndex()));
                    Safegraph.saveSafegraphPlacesKryo(directoryPath + "/processedData_withArea", safegraphPlaces);
                }
            }
        } else {
            String directoryPath = "./datasets/Safegraph/" + mainModel.ABM.studyScope;
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            String[] patternsList = AllPatterns.detectAllPatterns("./datasets/Safegraph/FullData");
            for (int i = 0; i < patternsList.length; i++) {
                myParent.mainModel.safegraph.clearPatternsPlaces();
                mainModel.safegraph.loadPatternsPlacesSet(mainModel.datasetDirectory, patternsList[i].split("_")[1] + "_" + patternsList[i].split("_")[2], myParent.mainModel.allGISData, "FullData", true, myParent.numProcessors);

                if (mainModel.safegraph.allPatterns.monthlyPatternsList != null && mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList != null) {
                    if (mainModel.safegraph.allPatterns.monthlyPatternsList.size() > 0 && mainModel.safegraph.allPatterns.monthlyPatternsList.size() > 0) {
                        directoryPath = "./datasets/Safegraph/" + mainModel.ABM.studyScope + "/" + patternsList[i];
                        directory = new File(directoryPath);
                        if (!directory.exists()) {
                            directory.mkdir();
                        }

                        Patterns patterns = Safegraph.getSubPattern(mainModel.ABM.studyScopeGeography, mainModel.safegraph.allPatterns.monthlyPatternsList.get(0));
                        Safegraph.savePatternsKryo(directoryPath + "/processedData", patterns);

                        SafegraphPlaces safegraphPlaces = Safegraph.getSubPlace(mainModel.ABM.studyScopeGeography, mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(0));
                        Safegraph.saveSafegraphPlacesKryo(directoryPath + "/processedData", safegraphPlaces);
                    }
                }

                myParent.mainModel.safegraph.clearPatternsPlaces();
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        Patterns monthPatterns = mainModel.safegraph.allPatterns.monthlyPatternsList.get(0);
        City castedScope = (City) (mainModel.ABM.studyScopeGeography);
        ArrayList<ArrayList<Integer>> matrix = new ArrayList();
        ArrayList<CensusBlockGroup> items = new ArrayList();
        for (int i = 0; i < ((City) (myParent.mainModel.ABM.studyScopeGeography)).censusTracts.size(); i++) {
            for (int j = 0; j < ((City) (myParent.mainModel.ABM.studyScopeGeography)).censusTracts.get(i).censusBlocks.size(); j++) {
                items.add(((City) (myParent.mainModel.ABM.studyScopeGeography)).censusTracts.get(i).censusBlocks.get(j));
            }
        }
        int numShops = 0;
        for (int i = 0; i < monthPatterns.patternRecords.size(); i++) {
            if (GISLocationDialog.isShop(monthPatterns.patternRecords.get(i).place.naics_code) == true) {
                numShops = numShops + 1;
            }
        }
        for (int i = 0; i < numShops; i++) {
            matrix.add(new ArrayList());
            for (int j = 0; j < items.size(); j++) {
                matrix.get(i).add(0);
            }
        }
        int shopIndex = -1;
        for (int i = 0; i < monthPatterns.patternRecords.size(); i++) {
            if (GISLocationDialog.isShop(monthPatterns.patternRecords.get(i).place.naics_code) == true) {
                shopIndex = shopIndex + 1;
                if (monthPatterns.patternRecords.get(i).visitor_home_cbgs_place != null) {
//                    CensusBlockGroup foundCBGSource = castedScope.findCBG(monthPatterns.patternRecords.get(i).poi_cbg_censusBlock.id);
//                    if (foundCBGSource != null) {
                    for (int j = 0; j < monthPatterns.patternRecords.get(i).visitor_home_cbgs_place.size(); j++) {
                        CensusBlockGroup foundCBGDestination = castedScope.findCBG(monthPatterns.patternRecords.get(i).visitor_home_cbgs_place.get(j).getKey().id);
                        if (foundCBGDestination != null) {
                            int sourceIndex = shopIndex;
                            int destIndex = -1;
//                                for (int k = 0; k < items.size(); k++) {
//                                    if (((CensusBlockGroup) items.get(k)).id == monthPatterns.patternRecords.get(i).poi_cbg_censusBlock.id) {
//                                        sourceIndex = k;
//                                        break;
//                                    }
//                                }
                            for (int k = 0; k < items.size(); k++) {
                                if (((CensusBlockGroup) items.get(k)).id == monthPatterns.patternRecords.get(i).visitor_home_cbgs_place.get(j).getKey().id) {
                                    destIndex = k;
                                    break;
                                }
                            }
                            if (sourceIndex >= 0 && destIndex >= 0) {
                                matrix.get(sourceIndex).set(destIndex, matrix.get(sourceIndex).get(destIndex) + monthPatterns.patternRecords.get(i).visitor_home_cbgs_place.get(j).value);
                            }
                        }
                    }
//                    }
                }
            }
        }
        try {
            FileWriter myWriter = new FileWriter(castedScope.name + "_CBGShop_travelMatrix.csv");
            for (int i = 0; i < matrix.size(); i++) {
                for (int j = 0; j < matrix.get(i).size(); j++) {
                    myWriter.write(String.valueOf(matrix.get(i).get(j)));
                    if (j != matrix.get(i).size() - 1) {
                        myWriter.write(",");
                    }
                }
                myWriter.write("\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        ManualLoadNewPatternsPlacesDialog manualLoadNewPatternsPlacesDialog = new ManualLoadNewPatternsPlacesDialog(myParent, false, mainModel.ABM.studyScope, this);
        manualLoadNewPatternsPlacesDialog.setVisible(true);
        manualLoadNewPatternsPlacesDialog.refreshList();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        String[] patternsListForScope = AllPatterns.detectAllPatterns("./datasets/Safegraph/" + mainModel.ABM.studyScope);
        System.out.println("Month,FracShop,FracSchool,FracRel");
        for (int m = 0; m < patternsListForScope.length; m++) {
            myParent.mainModel.safegraph.clearPatternsPlaces();
            String[] split=patternsListForScope[m].split("_");
            myParent.mainModel.safegraph.loadPatternsPlacesSet(myParent.mainModel.datasetDirectory, split[1]+"_"+split[2], myParent.mainModel.allGISData, myParent.mainModel.ABM.studyScope, true, myParent.numProcessors);
            float numVisitsShop = 0;
            float numUniqueVisitsShop = 0;
            float numVisitsSchool = 0;
            float numUniqueVisitsSchool = 0;
            float numVisitsRel = 0;
            float numUniqueVisitsRel = 0;
            for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.size(); i++) {
                //for (int i = 0; i < 500; i++) {
                if (GISLocationDialog.isShop(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).place.naics_code) == true) {
                    numVisitsShop = numVisitsShop + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).raw_visit_counts;
                    numUniqueVisitsShop = numUniqueVisitsShop + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).raw_visitor_counts;
                }
                if (GISLocationDialog.isSchool(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).place.naics_code) == true) {
                    numVisitsSchool = numVisitsSchool + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).raw_visit_counts;
                    numUniqueVisitsSchool = numUniqueVisitsSchool + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).raw_visitor_counts;
                }
                if (GISLocationDialog.isReligiousOrganization(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).place.naics_code) == true) {
                    numVisitsRel = numVisitsRel + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).raw_visit_counts;
                    numUniqueVisitsRel = numUniqueVisitsRel + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(0).patternRecords.get(i).raw_visitor_counts;
                }
            }
            System.out.println(m + "," + numVisitsShop / numUniqueVisitsShop + "," + numVisitsSchool / numUniqueVisitsSchool + "," + numVisitsRel / numUniqueVisitsRel);
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        String[] patternsListForScope = AllPatterns.detectAllPatterns("./datasets/Safegraph/" + mainModel.ABM.studyScope);
        System.out.println("Month,Mobility");
        for (int m = 0; m < patternsListForScope.length; m++) {
            myParent.mainModel.safegraph.clearPatternsPlaces();
            String[] split=patternsListForScope[m].split("_");
            myParent.mainModel.safegraph.loadPatternsPlacesSet(myParent.mainModel.datasetDirectory, split[1]+"_"+split[2], myParent.mainModel.allGISData, myParent.mainModel.ABM.studyScope, true, myParent.numProcessors);
            int totalMobility = 0;
            for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
                for (int j = 0; j < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                    totalMobility = totalMobility + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).raw_visit_counts;
                }
            }
            System.out.println(m + "," + totalMobility);
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    public void refreshLoadedPlacesList() {
        jList3.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size();
            }

            @Override
            public Object getElementAt(int index) {
                return mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(index).name;
            }
        });
    }

    public void refreshPatternsList() {
        jList1.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return mainModel.safegraph.allPatterns.monthlyPatternsList.size();
            }

            @Override
            public Object getElementAt(int index) {
                return mainModel.safegraph.allPatterns.monthlyPatternsList.get(index).name;
            }
        });
    }

    public void refreshPlacesList() {
        jList3.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size();
            }

            @Override
            public Object getElementAt(int index) {
                return mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(index).name;
            }
        });
    }

    public void refreshAllPlacesList() {
        String[] placesList = AllSafegraphPlaces.detectAllPlaces("./datasets/Safegraph/FullData");
        jList2.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return placesList.length;
            }

            @Override
            public Object getElementAt(int index) {
                return placesList[index];
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
