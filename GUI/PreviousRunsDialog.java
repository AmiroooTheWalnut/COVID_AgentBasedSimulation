/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.COVIDGeoVisualization;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygons;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.ProcessingMapRenderer;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Region;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.RegionSnapshot;
import COVID_AgentBasedSimulation.Model.HistoricalRun;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.CensusTract;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Marker;
import de.fhpotsdam.unfolding.geo.Location;
import de.siegmar.fastcsv.writer.CsvWriter;
import java.awt.Component;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class PreviousRunsDialog extends javax.swing.JDialog {

    MainFrame myParent;

    Timer zoomTimer;

    HistoricalRun currentHistoricalRun;
    HistoricalRun currentHistoricalRunCBG;
    HistoricalRun currentHistoricalRunVD;

    ProcessingMapRenderer sketch;

    LegendRange legendRange = new LegendRange();

    TreeSelectionEvent lastEvent;

    /**
     * Creates new form PreviousRunsDialog
     */
    public PreviousRunsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        myParent = (MainFrame) parent;

        sketch = new ProcessingMapRenderer(myParent, jPanel2, null);
        sketch.startRendering();

        zoomTimer = new Timer();
        TimerTask runTask = new TimerTask() {
            @Override
            public void run() {
                if (sketch.map != null) {
                    Component[] components = jPanel2.getComponents();
                    for (int i = 0; i < components.length; i++) {
                        System.out.println(components[i]);
                    }
                    if (components.length > 0) {
                        sketch.setCaseStudyPanZoom(((Marker) myParent.mainModel.ABM.studyScopeGeography).size * 52, new Location(((Marker) myParent.mainModel.ABM.studyScopeGeography).lon, ((Marker) myParent.mainModel.ABM.studyScopeGeography).lat));
                        zoomTimer.cancel();
                    }
                }
            }
        };
        zoomTimer.schedule(runTask, 0, 500);
        updateProjects();
        jSlider1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
//                setRendererPolygonShades();//DEPRECATED!!!
                setRendererRegionLayerShades();
                ZonedDateTime temp = currentHistoricalRun.startTime.plusHours((int) (jSlider1.getValue()));
                jLabel2.setText("Current: " + temp.toString());
//                System.out.println("Slider1: " + jSlider1.getValue());
            }
        });
        legendRange.setBounds(0, 0, jPanel6.getWidth(), jPanel6.getHeight());
        jPanel6.add(legendRange);
        jPanel6.repaint();
    }

    public void updateProjects() {
        String filePath = myParent.mainModel.ABM.filePath;
        String directoryPath = "projects" + File.separator + filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.length());
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
            return;
        }
        DefaultTreeModel model = (DefaultTreeModel) jTree1.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        model.reload();

        root = new DefaultMutableTreeNode(filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.length()));
        model.setRoot(root);

        String[] directories = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        for (int i = 0; i < directories.length; i++) {
            DefaultMutableTreeNode safegraphNode = new DefaultMutableTreeNode(directories[i]);
            root.add(safegraphNode);
        }

        model.reload();
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
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jButton4 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSlider1 = new javax.swing.JSlider();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Previous runs"));

        jButton1.setText("Delete");

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jTree1);

        jToggleButton1.setText("Set CBG");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jToggleButton2.setText("Set VD");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Get report (last index)");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel4.setText("Percentage drop:");

        jButton4.setText("Set random CBG arcs");
        jButton4.setToolTipText("");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton1)
                    .addComponent(jToggleButton2)
                    .addComponent(jButton3)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton1)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 670, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Feature"));

        jList2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Number of residents (N)", "Number of susceptibles (S)", "Number of infections symptomatic (IS)", "Number of infections asymptomatic (IAS)", "Number of recovered (R)", "Number of deaths (D)", "Cumulative infections (rate)", "Number of sick (sick)", "Census population (population)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setLayout(new java.awt.GridLayout(1, 3));

        jLabel1.setText("Start:");
        jPanel4.add(jLabel1);

        jLabel2.setText("Current:");
        jPanel4.add(jLabel2);

        jLabel3.setText("End:");
        jPanel4.add(jLabel3);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 831, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 72, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Show text");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jButton2.setText("Save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setText("Save matrix current timestep");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        loadHistoricalRun(evt);

//        setRendererPolygons();
        setRegionImageLayer();
        setRegionIndicesNames();
        setRegionCentralLocations();
        lastEvent = evt;
    }//GEN-LAST:event_jTree1ValueChanged

    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
//        setRendererPolygonShades();
        setRendererRegionLayerShades();
    }//GEN-LAST:event_jList2ValueChanged

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        sketch.isShowRegionIndexText = jCheckBox1.isSelected();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        sketch.saveFile();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        if (jToggleButton1.isSelected()) {
            loadHistoricalRunCBG(lastEvent);
        } else {
            currentHistoricalRunCBG = null;
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        if (jToggleButton1.isSelected()) {
            loadHistoricalRunVD(lastEvent);
        } else {
            currentHistoricalRunVD = null;
        }
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (jList2.getSelectedIndex() > -1) {
            if (currentHistoricalRunVD != null && currentHistoricalRunCBG != null) {
                String splitted[] = jList2.getSelectedValue().split(" [(]");
                try {
                    Field field = RegionSnapshot.class.getDeclaredField(splitted[1].substring(0, splitted[1].length() - 1));
                    ArrayList<String[]> allValues = new ArrayList();
                    for (int i = 0; i < currentHistoricalRunVD.regions.size(); i++) {
                        ArrayList<String> row = new ArrayList();
//                        String[] row = new String[currentHistoricalRunVD.regions.get(i).cBGsPercentageInvolved.size()];
                        for (int h = 0; h < currentHistoricalRunVD.regions.get(i).cBGsPercentageInvolved.size(); h++) {
                            for (int m = 0; m < currentHistoricalRunCBG.regions.size(); m++) {
                                if (currentHistoricalRunCBG.regions.get(m).cBGsIDsInvolved.get(0).equals(currentHistoricalRunVD.regions.get(i).cBGsIDsInvolved.get(h))) {
                                    if (currentHistoricalRunVD.regions.get(i).cBGsPercentageInvolved.get(h) > ((int) (jSpinner1.getValue()) / 100d)) {
                                        String val = String.valueOf(Double.valueOf(String.valueOf(field.get(currentHistoricalRunCBG.regions.get(m).hourlyRegionSnapshot.get(jSlider1.getValue())))) * currentHistoricalRunVD.regions.get(i).cBGsPercentageInvolved.get(h));
                                        row.add(val);
                                    }
                                }
                            }
                        }
                        String[] rowArray = new String[row.size()];
                        for (int y = 0; y < row.size(); y++) {
                            rowArray[y] = row.get(y);
                        }
                        allValues.add(rowArray);
                    }
                    try {
                        CsvWriter writer = new CsvWriter();
                        writer.write(new File("ReportCBGInVD_" + (int) (jSpinner1.getValue()) + ".csv"), Charset.forName("US-ASCII"), allValues);
                    } catch (IOException ex) {
                        Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (NoSuchFieldException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    Field field = RegionSnapshot.class.getDeclaredField(splitted[1].substring(0, splitted[1].length() - 1));
                    ArrayList<String[]> allValues = new ArrayList();
                    for (int i = 0; i < currentHistoricalRunVD.regions.size(); i++) {
                        ArrayList<String> row = new ArrayList();
//                        String[] row = new String[currentHistoricalRunVD.regions.get(i).cBGsPercentageInvolved.size()];
                        for (int h = 0; h < currentHistoricalRunVD.regions.get(i).cBGsPercentageInvolved.size(); h++) {
                            for (int m = 0; m < currentHistoricalRunCBG.regions.size(); m++) {
                                if (currentHistoricalRunCBG.regions.get(m).cBGsIDsInvolved.get(0).equals(currentHistoricalRunVD.regions.get(i).cBGsIDsInvolved.get(h))) {
                                    if (currentHistoricalRunVD.regions.get(i).cBGsPercentageInvolved.get(h) > ((int) (jSpinner1.getValue()) / 100d)) {
                                        String val = String.valueOf(Double.valueOf(String.valueOf(field.get(currentHistoricalRunCBG.regions.get(m).hourlyRegionSnapshot.get(jSlider1.getValue())))) / (double) (currentHistoricalRunCBG.regions.get(m).population));
                                        row.add(val);
                                    }
                                }
                            }

                        }
                        String[] rowArray = new String[row.size()];
                        for (int y = 0; y < row.size(); y++) {
                            rowArray[y] = row.get(y);
                        }
                        allValues.add(rowArray);
                    }
                    try {
                        CsvWriter writer = new CsvWriter();
                        writer.write(new File("ReportCBGInVDRatio_" + (int) (jSpinner1.getValue()) + ".csv"), Charset.forName("US-ASCII"), allValues);
                    } catch (IOException ex) {
                        Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (NoSuchFieldException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        ArrayList<CensusTract> cts = new ArrayList();
        City castedScope = ((City) myParent.mainModel.ABM.studyScopeGeography);
        for (int k = 0; k < castedScope.censusTracts.size(); k++) {
//            for (int m = 0; m < castedScope.censusTracts.get(k).censusBlocks.size(); m++) {
                cts.add(castedScope.censusTracts.get(k));
//            }
        }
        System.out.println("NUM CENSUS TRACTS: "+cts.size());
        sketch.setRandomArc(cts, cts.size());
        sketch.isDrawRandomArcs=true;
    }//GEN-LAST:event_jButton4ActionPerformed

    public void setRendererRegionLayerShades() {
        if (jList2.getSelectedIndex() > -1) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
            if (selectedNode != null) {
                String splitted[] = jList2.getSelectedValue().split(" [(]");
                try {
                    Field field = RegionSnapshot.class.getDeclaredField(splitted[1].substring(0, splitted[1].length() - 1));
                    ArrayList<Double> allValues = new ArrayList();
                    for (int i = 0; i < currentHistoricalRun.regions.size(); i++) {
                        double val = Double.valueOf(String.valueOf(field.get(currentHistoricalRun.regions.get(i).hourlyRegionSnapshot.get(jSlider1.getValue()))));
                        allValues.add(val);
//                        if(val>0){
//                            System.out.println("Region: "+i);
//                        }
                    }
                    ArrayList<Double> allValuesScaled = scaleData(allValues);
                    for (int i = 0; i < currentHistoricalRun.regions.size(); i++) {
                        currentHistoricalRun.regionsLayer.severities[i] = allValuesScaled.get(i);
                    }
                } catch (NoSuchFieldException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void setRendererPolygonShades() {
        if (jList2.getSelectedIndex() > -1) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
            if (selectedNode != null) {
                String splitted[] = jList2.getSelectedValue().split(" [(]");
                try {
                    Field field = RegionSnapshot.class.getDeclaredField(splitted[1].substring(0, splitted[1].length() - 1));
                    ArrayList<Double> allValues = new ArrayList();
                    for (int i = 0; i < currentHistoricalRun.regions.size(); i++) {
                        double val = Double.valueOf(String.valueOf(field.get(currentHistoricalRun.regions.get(i).hourlyRegionSnapshot.get(jSlider1.getValue()))));
                        allValues.add(val);
                    }
                    ArrayList<Double> allValuesScaled = scaleData(allValues);
                    for (int i = 0; i < currentHistoricalRun.regions.size(); i++) {
                        for (int j = 0; j < currentHistoricalRun.regions.get(i).polygons.size(); j++) {
                            currentHistoricalRun.regions.get(i).polygons.get(j).severity = allValuesScaled.get(i).floatValue();
                        }
                    }
                } catch (NoSuchFieldException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(PreviousRunsDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public ArrayList<Double> scaleData(ArrayList<Double> input) {
        ArrayList<Double> output = new ArrayList();
        double min = Collections.min(input);
        double max = Collections.max(input);
        for (int i = 0; i < input.size(); i++) {
            output.add(((input.get(i) - min) / max) * 255);
        }

        legendRange.max = max;
        legendRange.min = min;
        jPanel6.invalidate();
        jPanel6.repaint();

        return output;
    }

    public void loadHistoricalRun(javax.swing.event.TreeSelectionEvent evt) {
//        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
        TreePath treepath = evt.getPath();
        String path = "projects" + File.separator;
        Object elements[] = treepath.getPath();
        for (int i = 0, n = elements.length; i < n; i++) {
            path = path + elements[i] + File.separator;
        }
        currentHistoricalRun = HistoricalRun.loadHistoricalRunKryo(path + "data.bin");

        if (currentHistoricalRun != null) {
            jSlider1.setMinimum(0);
            jSlider1.setMaximum(currentHistoricalRun.regions.get(0).hourlyRegionSnapshot.size() - 1);
            jSlider1.setValue(0);

            jLabel1.setText("Start: " + currentHistoricalRun.startTimeString);
            jLabel2.setText("Current: " + currentHistoricalRun.startTimeString);
            jLabel3.setText("End: " + currentHistoricalRun.endTimeString);
        } else {
            System.out.println("HISTORICAL RUN IS NULL!");
        }

//        System.out.println("!!!!");
    }

    public void loadHistoricalRunCBG(javax.swing.event.TreeSelectionEvent evt) {
        TreePath treepath = evt.getPath();
        String path = "projects" + File.separator;
        Object elements[] = treepath.getPath();
        for (int i = 0, n = elements.length; i < n; i++) {
            path = path + elements[i] + File.separator;
        }
        currentHistoricalRunCBG = HistoricalRun.loadHistoricalRunKryo(path + "data.bin");
//        System.out.println("!!!!");
    }

    public void loadHistoricalRunVD(javax.swing.event.TreeSelectionEvent evt) {
        TreePath treepath = evt.getPath();
        String path = "projects" + File.separator;
        Object elements[] = treepath.getPath();
        for (int i = 0, n = elements.length; i < n; i++) {
            path = path + elements[i] + File.separator;
        }
        currentHistoricalRunVD = HistoricalRun.loadHistoricalRunKryo(path + "data.bin");
//        System.out.println("!!!!");
    }

    public void setRendererPolygons() {
        ArrayList<MyPolygons> polygons = new ArrayList();
//        polygons.add(currentHistoricalRun.regions.get(1).polygons.get(0));
        for (int i = 0; i < currentHistoricalRun.regions.size(); i++) {
//            if(i==80){
//                System.out.println("!!!!");
//                continue;
//            }
            for (int j = 0; j < currentHistoricalRun.regions.get(i).polygons.size(); j++) {
                polygons.add(currentHistoricalRun.regions.get(i).polygons.get(j));
            }
        }
        sketch.polygons = polygons;
    }

    public void setRegionImageLayer() {
        sketch.regionImageLayer = currentHistoricalRun.regionsLayer;
    }

    public void setRegionIndicesNames() {
        ArrayList<String> output = new ArrayList();
        for (int i = 0; i < currentHistoricalRun.regions.size(); i++) {
            output.add(String.valueOf(i));
        }
        sketch.regionNames = output;
    }

    public void setRegionCentralLocations() {
        ArrayList<Location> output = new ArrayList();
        for (int i = 0; i < currentHistoricalRun.regions.size(); i++) {
            output.add(new Location(currentHistoricalRun.regions.get(i).lon, currentHistoricalRun.regions.get(i).lat));
        }
        sketch.regionCenters = output;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
