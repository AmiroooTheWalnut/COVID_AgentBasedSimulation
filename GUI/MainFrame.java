package COVID_AgentBasedSimulation.GUI;

import COVID_AgentBasedSimulation.GUI.Settings.SimulatorSettingsDialog;
import COVID_AgentBasedSimulation.GUI.VoronoiGIS.GISLocationDialog;
import COVID_AgentBasedSimulation.GUI.Simulator.SimulatorDialog;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.ProcessingMapRenderer;
import COVID_AgentBasedSimulation.GUI.SafegraphPreprocessor.SafeGraphPreprocessDialog;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentBasedModel;
import COVID_AgentBasedSimulation.Model.Data.CovidCsseJhu.CovidCsseJhu;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Country;
import COVID_AgentBasedSimulation.Model.Structure.County;
import COVID_AgentBasedSimulation.Model.Structure.State;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class MainFrame extends javax.swing.JFrame {

    public MainModel mainModel;
    public ProjectDefaults projectDefaults;
    public ProcessingMapRenderer child;
    public int numProcessors;

    //public esmaieeli.gisFastLocationOptimization.GUI.MainFrame OSMGISMainFrame;
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Runtime.getRuntime().availableProcessors() / 2, 1, 1000, 1));

        numProcessors = (int) jSpinner1.getValue();

        mainModel = new MainModel();
        mainModel.numCPUs = numProcessors;
        mainModel.initData();
        mainModel.initAgentBasedModel();

        try {
            File geoDataFile = new File("./datasets/ProcessedGeoData.bin");
            if (geoDataFile.exists()) {
                AllGISData geoData = MainModel.loadAllGISDataKryo("./datasets/ProcessedGeoData.bin");
                mainModel.allGISData = geoData;
                jLabel1.setText("Geographical data loaded");
            } else {
                jLabel1.setText("<html>No processed geographical data detected.<br/>You can preprocess the data.</html>");
            }
        } catch (Exception ex) {
            System.out.println("Error in reading GIS data!");
            jLabel1.setText("Error in reading");
        }

        try {
            File casesDataFile = new File("./datasets/ProcessedCasesData.bin");
            if (casesDataFile.exists()) {
                CovidCsseJhu casesData = MainModel.loadCasesDataKryo("./datasets/ProcessedCasesData.bin");
                mainModel.covidCsseJhu = casesData;
                jLabel5.setText("Cases data loaded");
            } else {
                jLabel5.setText("<html>No processed cases data detected.<br/>You can preprocess the data.</html>");
            }
        } catch (Exception ex) {
            System.out.println("Error in reading cases data!");
            jLabel5.setText("Error in reading");
        }
        try {
            checkDefaults();
        } catch (Exception ex) {
            System.out.println("Error in reading defaults!");
        }
    }

    public void checkDefaults() {
        projectDefaults = new ProjectDefaults();
        File file = new File("./ABMDefaults.json");
        if (file.exists() == true) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileReader in;
            try {
                in = new FileReader("./ABMDefaults.json");
                BufferedReader br = new BufferedReader(in);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();

                ProjectDefaults result = gson.fromJson(sb.toString(), ProjectDefaults.class);
                projectDefaults = result;
                if (projectDefaults != null) {
                    if (projectDefaults.defaultProjectFileLocation != null) {
                        //projectDefaults.currentDefaultProjectFileLocation=projectDefaults.defaultProjectFileLocation;
                        String[] temp = projectDefaults.defaultProjectFileLocation.split("\\\\");
                        jLabel4.setText(temp[temp.length - 1]);
                        if (projectDefaults.defaultProjectFileLocation.length() > 0) {
                            mainModel.ABM.loadModel(projectDefaults.defaultProjectFileLocation);
                            mainModel.ABM.filePath = projectDefaults.defaultProjectFileLocation;
                            jLabel7.setText(temp[temp.length - 1]);
                        }
                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel12 = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        loadProjectButton = new javax.swing.JButton();
        setDefaultProjectButton = new javax.swing.JButton();
        saveProjectButton = new javax.swing.JButton();
        removeDefaultProjectButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jButton6 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton20 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Agent based simulator"));

        jButton11.setText("Simulator");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Settings");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Project"));

        loadProjectButton.setText("Load project");
        loadProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadProjectButtonActionPerformed(evt);
            }
        });

        setDefaultProjectButton.setText("Set default project on startup");
        setDefaultProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDefaultProjectButtonActionPerformed(evt);
            }
        });

        saveProjectButton.setText("Save project as");
        saveProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectButtonActionPerformed(evt);
            }
        });

        removeDefaultProjectButton.setText("Remove default project");
        removeDefaultProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeDefaultProjectButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Default project:");

        jLabel6.setText("Current project:");

        jButton3.setText("Save project");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(loadProjectButton)
                            .addComponent(setDefaultProjectButton)
                            .addComponent(saveProjectButton)
                            .addComponent(removeDefaultProjectButton)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jButton3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loadProjectButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveProjectButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(setDefaultProjectButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeDefaultProjectButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton11)
                    .addComponent(jButton12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton12)
                .addGap(18, 18, 18)
                .addComponent(jButton11)
                .addContainerGap())
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Num parallel processors:");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 8, 1));
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        jButton6.setText("Contact rate");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton6)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6))
                .addContainerGap())
        );

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Safegraph"));

        jButton20.setText("Preprocess");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton20)
                .addContainerGap())
        );

        jPanel3.add(jPanel1);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Geography"));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Preprocess geography"));

        jButton5.setText("Start");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton10.setText("Preview");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel1.setText("Geography status");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton10)
                .addContainerGap())
        );

        jPanel3.add(jPanel13);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Historical county infections"));

        jButton2.setText("Preprocess");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel5.setText("Infection data status");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 59, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 359, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );

        jPanel3.add(jPanel2);

        jPanel5.setLayout(new java.awt.GridLayout(0, 1));

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Voronoi diagrams"));

        jButton1.setText("Open app");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel16);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Artificial mobility"));

        jButton4.setText("Generate");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4)
                .addContainerGap(63, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4)
                .addContainerGap(158, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (mainModel.allGISData == null) {
            mainModel.allGISData = new AllGISData();
        }
        mainModel.allGISData.processUSData("./datasets");
        //\/\/\/ OLD DATASET: ONLY CONTAINS SAFEGRAPH PLACES/CENSUS BLOCKS/CENSUS TRACTS/COUNTIES/STATES
        //mainModel.allGISData.processUSData_Old("./datasets/cbg.geojson");
        //^^^ OLD DATASET: ONLY CONTAINS SAFEGRAPH PLACES/CENSUS BLOCKS/CENSUS TRACTS/COUNTIES/STATES
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        PreviewDialog swingGui = new PreviewDialog(this, false);
        swingGui.setVisible(true);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        SimulatorDialog simulatorDialog = new SimulatorDialog(this, false);
        simulatorDialog.setVisible(true);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged
        numProcessors = (int) jSpinner1.getValue();
        mainModel.numCPUs = numProcessors;
    }//GEN-LAST:event_jSpinner1StateChanged

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        SimulatorSettingsDialog simulatorSettingsDialog = new SimulatorSettingsDialog(mainModel, this, true);
        simulatorSettingsDialog.setVisible(true);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void saveProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProjectButtonActionPerformed
        JFileChooser fcSave = new JFileChooser(".");
        fcSave.setAcceptAllFileFilterUsed(false);
        int returnVal = fcSave.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String saveFilePath = fcSave.getSelectedFile().getAbsolutePath();
            mainModel.ABM.saveModel(saveFilePath);
            mainModel.ABM.filePath = saveFilePath;
            String[] temp = saveFilePath.split("\\\\");
            jLabel7.setText(temp[temp.length - 1]);
        }
    }//GEN-LAST:event_saveProjectButtonActionPerformed

    private void loadProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadProjectButtonActionPerformed
        JFileChooser fcLoad = new javax.swing.JFileChooser(".");
        fcLoad.setAcceptAllFileFilterUsed(false);
        int returnVal = fcLoad.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String loadFilePath = fcLoad.getSelectedFile().getAbsolutePath();
            mainModel.ABM.loadModel(loadFilePath);
            mainModel.ABM.filePath = loadFilePath;
            String[] temp = loadFilePath.split("\\\\");
            jLabel7.setText(temp[temp.length - 1]);
        }
    }//GEN-LAST:event_loadProjectButtonActionPerformed

    private void setDefaultProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setDefaultProjectButtonActionPerformed
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        projectDefaults.defaultProjectFileLocation = mainModel.ABM.filePath;
        String result = gson.toJson(projectDefaults);
        BufferedWriter writer;
        try {
            FileWriter out = new FileWriter("./ABMDefaults.json");
            writer = new BufferedWriter(out);
            writer.write(result);

            writer.close();
            out.close();
            String[] temp = projectDefaults.defaultProjectFileLocation.split("\\\\");
            jLabel4.setText(temp[temp.length - 1]);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_setDefaultProjectButtonActionPerformed

    private void removeDefaultProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeDefaultProjectButtonActionPerformed
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        projectDefaults.defaultProjectFileLocation = "";
        String result = gson.toJson(projectDefaults);
        BufferedWriter writer;
        try {
            FileWriter out = new FileWriter("./ABMDefaults.json");
            writer = new BufferedWriter(out);
            writer.write(result);

            writer.close();
            out.close();
            jLabel4.setText("NO PROJECT");
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_removeDefaultProjectButtonActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        SafeGraphPreprocessDialog safeGraphPreprocessDialog = new SafeGraphPreprocessDialog(this, false);
        safeGraphPreprocessDialog.setVisible(true);
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        GISLocationDialog gISLocationDialog = new GISLocationDialog(this, false);
        gISLocationDialog.setVisible(true);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (mainModel.covidCsseJhu == null) {
            mainModel.covidCsseJhu = new CovidCsseJhu();
        }
        mainModel.covidCsseJhu.preprocessDailyCountyInfections("./datasets/COUNTIES_INFECTIONS_time_series_covid19_confirmed_US.csv", mainModel.allGISData);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        mainModel.ABM.saveModel(mainModel.ABM.filePath);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        ContactRateDialog contactRateDialog=new ContactRateDialog(this,false);
        contactRateDialog.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JButton loadProjectButton;
    private javax.swing.JButton removeDefaultProjectButton;
    private javax.swing.JButton saveProjectButton;
    private javax.swing.JButton setDefaultProjectButton;
    // End of variables declaration//GEN-END:variables
}
