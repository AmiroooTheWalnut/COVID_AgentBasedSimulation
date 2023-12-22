/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.VoronoiGIS;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygon;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.COVIDGeoVisualization;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.MyPolygons;
import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.RegionImageLayer;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.LocationNodeSafegraph;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.PatternsRecordProcessed;
import static COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlaces.getBuildingAreaLevelsOnline;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.Person;
import COVID_AgentBasedSimulation.Model.HardcodedSimulator.RootArtificial;
import COVID_AgentBasedSimulation.Model.MainModel;
import COVID_AgentBasedSimulation.Model.Structure.AllGISData;
import COVID_AgentBasedSimulation.Model.Structure.CBGVDCell;
import COVID_AgentBasedSimulation.Model.Structure.CensusBlockGroup;
import COVID_AgentBasedSimulation.Model.Structure.City;
import COVID_AgentBasedSimulation.Model.Structure.Country;
import COVID_AgentBasedSimulation.Model.Structure.County;
import COVID_AgentBasedSimulation.Model.Structure.Marker;
import COVID_AgentBasedSimulation.Model.Structure.State;
import COVID_AgentBasedSimulation.Model.Structure.SupplementaryCaseStudyData;
import COVID_AgentBasedSimulation.Model.Structure.Tessellation;
import COVID_AgentBasedSimulation.Model.Structure.TessellationCell;
import COVID_AgentBasedSimulation.Model.Structure.VDCell;
import esmaieeli.gisFastLocationOptimization.GIS3D.Grid;
import esmaieeli.gisFastLocationOptimization.GIS3D.LayerDefinition;
import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;
import esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel;
import esmaieeli.gisFastLocationOptimization.Simulation.FacilityLocation;
import esmaieeli.gisFastLocationOptimization.Simulation.Routing;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import de.fhpotsdam.unfolding.geo.Location;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.writer.CsvWriter;
import esmaieeli.gisFastLocationOptimization.GIS3D.AllData;
import esmaieeli.gisFastLocationOptimization.GIS3D.NumericLayer;
import esmaieeli.gisFastLocationOptimization.GIS3D.StoreProcessedData;
import esmaieeli.gisFastLocationOptimization.Simulation.SimplePolygons;
import esmaieeli.gisFastLocationOptimization.Simulation.VectorToPolygon;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.XMeans;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class GISLocationDialog extends javax.swing.JDialog {

    MainFrame myParent;

    MainFramePanel mainFParent;
    public COVIDGeoVisualization renderer;

    FacilityLocation[] shopFacilities;
    FacilityLocation[] schoolFacilities;
    FacilityLocation[] relFacilities;

    ArrayList<LocationNodeSafegraph> shopLocationNodes;
    ArrayList<LocationNodeSafegraph> schoolLocationNodes;
    ArrayList<LocationNodeSafegraph> templeLocationNodes;

    float shopMergeThreshold = 0.009f;
    float schoolMergeThreshold = 0.008f;
    float templeMergeThreshold = 0.012f;

    double CBGToShopDistances[][];
    double CBGToShopNumbers[][];

    COVIDGeoVisualization sketch;

    float sampleRate = 0.01f;

    public ExecutorService routingThreadPool;

    protected Instances m_Instances;

    /**
     * Creates new form GISLocationDialog
     */
    public GISLocationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        myParent = (MainFrame) parent;

        initComponents();
        mainFParent = new esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel();
        jPanel1.add(mainFParent);
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
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jSpinner1 = new javax.swing.JSpinner();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton24 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton20 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jSpinner3 = new javax.swing.JSpinner();
        jButton38 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton1.setText("Generate shops voronoi");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Show shops");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Show schools");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Load months");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Generate VDFMTH");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Generate schools voronoi");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Generate shops");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Generate schools");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Show shops and schools");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Generate CBG");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Food & grocery 1,2,3 nearest home");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("School 1,2,3 nearest");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Religious 1,2,3 nearest");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Food & grocery 1,2,3 nearest work");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Generate CBGVDFMTH");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton21.setText("CBGs infections");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setText("VDs infections");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setText("CBGs VDs infections");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton21)
                    .addComponent(jButton22)
                    .addComponent(jButton23))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton23)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton29.setText("Generate VDFNC");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(20, 1, null, 1));

        jButton30.setText("VDFNC temp 100");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jButton31.setText("VDFNC temp 70");
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton32.setText("Generate VD_CBG");
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jButton33.setText("Generate VD_CBGVD");
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jButton36.setText("Gyms 1,2,3 nearest home");
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jButton37.setText("Generate rel");
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                        .addComponent(jButton37))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jButton4)
                            .addComponent(jButton3)
                            .addComponent(jButton6)
                            .addComponent(jButton7)
                            .addComponent(jButton5)
                            .addComponent(jButton9)
                            .addComponent(jButton10)
                            .addComponent(jButton12)
                            .addComponent(jButton13)
                            .addComponent(jButton14)
                            .addComponent(jButton15)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jButton29)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jButton11, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(jButton30)
                            .addComponent(jButton31)
                            .addComponent(jButton32)
                            .addComponent(jButton33)
                            .addComponent(jButton36))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4)
                .addGap(18, 18, 18)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addGap(18, 18, 18)
                .addComponent(jButton5)
                .addGap(18, 18, 18)
                .addComponent(jButton9)
                .addGap(18, 18, 18)
                .addComponent(jButton10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton15)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton29)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton33)
                .addGap(22, 22, 22)
                .addComponent(jButton30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton31)
                .addGap(18, 18, 18)
                .addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton14)
                .addGap(18, 18, 18)
                .addComponent(jButton36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton24.setText("Update city sup by app");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jLabel1.setText("STATUS");

        jButton25.setText("Update city sup by data");
        jButton25.setToolTipText("");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton26.setText("Save VD travels");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jButton27.setText("Read Rent locs");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jButton28.setText("Write Rent proximities");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton20.setText("Show map");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton16.setText("Misc: Draw CBGs of infected");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton19.setText("Pan zoom to CaseStudy");
        jButton19.setToolTipText("");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton18.setText("Misc: Draw CBGs_VDs of infected");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton17.setText("Misc: Draw VDs of infected");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton20)
                    .addComponent(jButton16)
                    .addComponent(jButton19)
                    .addComponent(jButton18)
                    .addComponent(jButton17))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton34.setText("Process all commuting times");
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        jButton35.setText("Save all nodes to text");
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        jLabel2.setText("Sample fraction:");

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00000"))));
        jFormattedTextField1.setText("0.001");

        jLabel3.setText("Num shops:");

        jLabel4.setText("Num schools:");

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(10, 1, null, 1));

        jSpinner3.setModel(new javax.swing.SpinnerNumberModel(10, 1, null, 1));

        jButton38.setText("Create K-means layer");
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jButton39.setText("CBG to sh POIs");
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        jButton40.setText("Save FACS_style data");
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextField1))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner2))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner3))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton24)
                            .addComponent(jLabel1)
                            .addComponent(jButton25)
                            .addComponent(jButton26)
                            .addComponent(jButton27)
                            .addComponent(jButton28)
                            .addComponent(jButton34)
                            .addComponent(jButton35)
                            .addComponent(jButton38)
                            .addComponent(jButton39)
                            .addComponent(jButton40))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jButton25)
                .addGap(18, 18, 18)
                .addComponent(jButton26)
                .addGap(18, 18, 18)
                .addComponent(jButton27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jSpinner3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton34)
                .addGap(18, 18, 18)
                .addComponent(jButton35)
                .addGap(18, 18, 18)
                .addComponent(jButton39)
                .addGap(18, 18, 18)
                .addComponent(jButton40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton38)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        makeVDs(shopFacilities, "shops_v_VDFMTH");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        String years[] = new String[1];
//        years[0] = "2020";
//        years[1] = "2021";
//        String months[][] = new String[2][12];
////        months[0][0] = "09";
////        months[0][1] = "10";
////        months[0][2] = "11";
//
////        months[0][0] = "03";
////        months[0][1] = "04";
//        months[0][0] = "05";
//        months[0][1] = "06";
//        months[0][2] = "07";
//        months[0][3] = "08";
//
//        months[0][4] = "09";
//        months[0][5] = "10";
//        months[0][6] = "11";
//        months[0][7] = "12";
//        months[1][0] = "01";
//        months[1][1] = "02";
//        months[1][2] = "03";
//        months[1][3] = "04";
//        months[1][4] = "05";
//        months[1][5] = "06";
//        months[1][6] = "07";

        years[0] = "2021";
        String months[][] = new String[1][4];
        months[0][0] = "01";
        months[0][1] = "02";
        months[0][2] = "03";
        months[0][3] = "04";
        myParent.mainModel.safegraph.requestDatasetRange(myParent.mainModel.datasetDirectory, myParent.mainModel.allGISData, myParent.mainModel.ABM.studyScope, years, months, true, myParent.numProcessors);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        (mainFParent.app).enqueue(new Callable() {
            public Object call() throws Exception {
                if (shopFacilities != null) {
                    mainFParent.app.removeAllHeadquarters();
                    for (int i = 0; i < shopFacilities.length; i++) {
                        mainFParent.app.headquarter(shopFacilities[i].renderingLocation, 0.4f, "center");
                    }
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        makeVDs(schoolFacilities, "schools_v_VDFMTH");
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        (mainFParent.app).enqueue(new Callable() {
            public Object call() throws Exception {
                if (schoolFacilities != null) {
                    mainFParent.app.removeAllHeadquarters();
                    for (int i = 0; i < schoolFacilities.length; i++) {
                        mainFParent.app.headquarter(schoolFacilities[i].renderingLocation, 0.4f, "candidate");
                    }
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        makeVDCombination("shops_v_VDFMTH", "schools_v_VDFMTH", "VDFMTH");
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        shopFacilities = initShopFacilities(shopMergeThreshold);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        schoolFacilities = initSchoolFacilities(schoolMergeThreshold, null);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        (mainFParent.app).enqueue(new Callable() {
            public Object call() throws Exception {
                mainFParent.app.removeAllHeadquarters();
                if (shopFacilities != null) {
//                    mainFParent.app.removeAllHeadquarters();
                    for (int i = 0; i < shopFacilities.length; i++) {
                        mainFParent.app.headquarter(shopFacilities[i].renderingLocation, 0.4f, "center");
                    }
                }
                if (schoolFacilities != null) {
//                    mainFParent.app.removeAllHeadquarters();
                    for (int i = 0; i < schoolFacilities.length; i++) {
                        mainFParent.app.headquarter(schoolFacilities[i].renderingLocation, 0.4f, "candidate");
                    }
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        myParent.mainModel.allGISData.readCensusBlockGroupPolygon("./datasets");

        System.out.println("num nodes: " + mainFParent.allData.all_Nodes.length);

        Long cBGForNodes[] = new Long[mainFParent.allData.all_Nodes.length];

        int numProcessors = myParent.mainModel.numCPUs;
//        if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//            numProcessors = Runtime.getRuntime().availableProcessors();
//        }
        ParallelLocationNodeCBGIdConnector parallelLocationNodeCBGIdConnector[] = new ParallelLocationNodeCBGIdConnector[numProcessors];

        for (int i = 0; i < numProcessors - 1; i++) {
            parallelLocationNodeCBGIdConnector[i] = new ParallelLocationNodeCBGIdConnector(i, this, cBGForNodes, (int) Math.floor(i * ((mainFParent.allData.all_Nodes.length) / numProcessors)), (int) Math.floor((i + 1) * ((mainFParent.allData.all_Nodes.length) / numProcessors)));
        }
        parallelLocationNodeCBGIdConnector[numProcessors - 1] = new ParallelLocationNodeCBGIdConnector(numProcessors - 1, this, cBGForNodes, (int) Math.floor((numProcessors - 1) * ((mainFParent.allData.all_Nodes.length) / numProcessors)), mainFParent.allData.all_Nodes.length);

        for (int i = 0; i < numProcessors; i++) {
            parallelLocationNodeCBGIdConnector[i].myThread.start();
        }
        for (int i = 0; i < numProcessors; i++) {
            try {
                parallelLocationNodeCBGIdConnector[i].myThread.join();
                System.out.println("thread " + i + "finished for location nodes: " + parallelLocationNodeCBGIdConnector[i].myStartIndex + " | " + parallelLocationNodeCBGIdConnector[i].myEndIndex);
            } catch (InterruptedException ie) {
                System.out.println(ie.toString());
            }
        }

        /*
        for (int u = 0; u < mainFParent.allData.all_Nodes.length; u++) {
            isCBGFound = false;
            for (int i = 0; i < myParent.mainModel.allGISData.countries.size(); i++) {
                for (int j = 0; j < myParent.mainModel.allGISData.countries.get(i).states.size(); j++) {
                    for (int k = 0; k < myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.size(); k++) {
                        for (int m = 0; m < myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.size(); m++) {
                            for (int v = 0; v < myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.size(); v++) {
                                for (int y = 0; y < myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v).shape.size(); y++) {
                                    GeometryFactory geomFactory = new GeometryFactory();
                                    Point point = geomFactory.createPoint(new Coordinate(mainFParent.allData.all_Nodes[u].lon, mainFParent.allData.all_Nodes[u].lat));
                                    if (myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v).shape.get(y).covers(point) == true) {
                                        censusBlockGroupsEncounteredList.add(myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v).id);
                                        cBGForNodes[i] = myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v).id;
                                        isCBGFound = true;
                                        break;
                                    }
                                }
                                if (isCBGFound == true) {
                                    break;
                                }
                            }
                            if (isCBGFound == true) {
                                break;
                            }
                        }
                        if (isCBGFound == true) {
                            break;
                        }
                    }
                    if (isCBGFound == true) {
                        break;
                    }
                }
                if (isCBGFound == true) {
                    break;
                }
            }
            if (isCBGFound == false) {
                System.out.println("SEVERE PROBLEM! LOCATION NODE HAS NOT CBG!");
            }
            System.out.println(counter);
            counter = counter + 1;
        }
         */
//        ArrayList<Long> censusBlockGroupsEncounteredList = new ArrayList();
        List<Long> censusBlockGroupsEncounteredList = Arrays.asList(cBGForNodes);

        LinkedHashSet<Long> censusBlockGroupsEncounteredUniqueSetHS = new LinkedHashSet(censusBlockGroupsEncounteredList);
        ArrayList<Long> censusBlockGroupsEncounteredUniqueList = new ArrayList(censusBlockGroupsEncounteredUniqueSetHS);
        for (int i = censusBlockGroupsEncounteredUniqueList.size() - 1; i >= 0; i--) {
            if (censusBlockGroupsEncounteredUniqueList.get(i) == null) {
                censusBlockGroupsEncounteredUniqueList.set(i, -1l);
//                System.out.println(i);
            }
        }
        for (int u = 0; u < mainFParent.allData.all_Nodes.length; u++) {
            if (cBGForNodes[u] == null) {
                cBGForNodes[u] = -1l;
            }
        }
        Collections.sort(censusBlockGroupsEncounteredUniqueList);
        int numCBGs = censusBlockGroupsEncounteredUniqueList.size();
        int indices[] = new int[numCBGs];
        for (int i = 0; i < numCBGs; i++) {
            indices[i] = i + 1;
        }

        for (int u = 0; u < mainFParent.allData.all_Nodes.length; u++) {
            for (int j = 0; j < censusBlockGroupsEncounteredUniqueList.size(); j++) {
                if (cBGForNodes[u].equals(-1)) {
                    short[] val = new short[1];
                    val[0] = (short) (0);
                    mainFParent.allData.all_Nodes[u].layers.add(val);
                } else {
                    if (cBGForNodes[u].equals(censusBlockGroupsEncounteredUniqueList.get(j)) == true) {
                        short[] val = new short[1];
                        val[0] = (short) (indices[j]);
                        mainFParent.allData.all_Nodes[u].layers.add(val);
                    }
                }

            }
        }

        LayerDefinition tempLayer = new LayerDefinition("category", "CBG");

        tempLayer.categories = new String[numCBGs];
        tempLayer.colors = new Color[numCBGs];
        tempLayer.values = new double[numCBGs];

        tempLayer.categories[0] = "NOT ASSIGNED";
        tempLayer.colors[0] = new Color(2, 2, 2);
        tempLayer.values[0] = Double.valueOf(0);
        for (int i = 1; i < numCBGs; i++) {
            tempLayer.categories[i] = "CBG " + String.valueOf(i);
            tempLayer.colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numCBGs - 1, 1, 1));
            tempLayer.values[i] = Double.valueOf(censusBlockGroupsEncounteredUniqueList.get(i));
        }

        mainFParent.allData.all_Layers.add(tempLayer);
        mainFParent.refreshLayersList();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        ArrayList<LocationNodeSafegraph> shopLocations = initShopLocations();
        Integer indices[] = labelMergedFacilities(shopLocations, shopMergeThreshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);
        ArrayList<LocationNodeSafegraph> shopMergedLocations = mergeFacilitiesWithIndices(shopLocations, indicesList, indicesUniqueList, null);

        int trafficLayerIndex = -1;
        for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
            if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                trafficLayerIndex = i;
                break;
            }
        }

        int counter[] = new int[shopMergedLocations.size()];

        int numProcessors = myParent.mainModel.numCPUs;
//        if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//            numProcessors = Runtime.getRuntime().availableProcessors();
//        }

        ArrayList<CensusBlockGroup> cbgs = new ArrayList();
        if (myParent.mainModel.ABM.studyScopeGeography instanceof Country) {
            System.out.println("COUNTRY LEVEL IS NOT IMPLEMENTED!");
            return;
        } else if (myParent.mainModel.ABM.studyScopeGeography instanceof State) {
            System.out.println("STATE LEVEL IS NOT IMPLEMENTED!");
            return;
        } else if (myParent.mainModel.ABM.studyScopeGeography instanceof County) {
            System.out.println("COUNTY LEVEL IS NOT IMPLEMENTED!");
            return;
        } else if (myParent.mainModel.ABM.studyScopeGeography instanceof City) {
            City scope = ((City) (myParent.mainModel.ABM.studyScopeGeography));
            for (int i = 0; i < scope.censusTracts.size(); i++) {
                for (int j = 0; j < scope.censusTracts.get(i).censusBlocks.size(); j++) {
                    cbgs.add(scope.censusTracts.get(i).censusBlocks.get(j));
                }
            }
        }

        CBGToShopDistances = new double[cbgs.size()][shopMergedLocations.size()];
        CBGToShopNumbers = new double[cbgs.size()][shopMergedLocations.size()];

        System.out.println(cbgs.size());
        System.out.println(shopMergedLocations.size());

        File file = new File(myParent.mainModel.ABM.studyScope + "_CBGShopDistances.csv");
        if (file.exists() == false) {
            mainFParent.allData.setParallelLayers(numProcessors, -1);

            routingThreadPool = Executors.newFixedThreadPool(numProcessors);
            ArrayList<Callable<Object>> calls[] = new ArrayList[numProcessors];
            for (int f = 0; f < numProcessors; f++) {
                calls[f] = new ArrayList();
            }
            for (int i = 0; i < cbgs.size(); i++) {
                ParallelRouting2 parallelRouting2[] = new ParallelRouting2[numProcessors];

                for (int f = 0; f < numProcessors - 1; f++) {
                    parallelRouting2[f] = new ParallelRouting2(f, this, CBGToShopDistances, (int) Math.floor(f * ((shopMergedLocations.size()) / numProcessors)), (int) Math.floor((f + 1) * ((shopMergedLocations.size()) / numProcessors)), trafficLayerIndex, shopMergedLocations, i, cbgs);
                }
                parallelRouting2[numProcessors - 1] = new ParallelRouting2(numProcessors - 1, this, CBGToShopDistances, (int) Math.floor((numProcessors - 1) * ((shopMergedLocations.size()) / numProcessors)), shopMergedLocations.size(), trafficLayerIndex, shopMergedLocations, i, cbgs);
//                ArrayList<Callable<Object>> calls = new ArrayList<Callable<Object>>();
                for (int f = 0; f < numProcessors; f++) {
                    parallelRouting2[f].addRunnableToQueue(calls[f]);
                }
            }

            List<Future<Object>> futures[] = new ArrayList[numProcessors];
            for (int p = 0; p < numProcessors; p++) {
                futures[p] = new ArrayList();
                for (int f = 0; f < calls[p].size(); f++) {
                    futures[p].add(routingThreadPool.submit(calls[p].get(f)));
                }
            }
            for (int p = 0; p < numProcessors; p++) {
                for (int f = 0; f < futures[p].size(); f++) {
                    try {
                        futures[p].get(f).get();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
//                System.out.println("CBGs percentage processed: "+((float)i/(cbgs.size())));

            // OLD THREAD BASED PARALLEL RUNNING
//            for (int i = 0; i < cbgs.size(); i++) {
//                ParallelRouting2 parallelRouting2[] = new ParallelRouting2[numProcessors];
//                for (int f = 0; f < numProcessors - 1; f++) {
//                    parallelRouting2[f] = new ParallelRouting2(f, this, CBGToShopDistances, (int) Math.floor(f * ((shopMergedLocations.size()) / numProcessors)), (int) Math.floor((f + 1) * ((shopMergedLocations.size()) / numProcessors)), trafficLayerIndex, shopMergedLocations, i, cbgs);
//                }
//                parallelRouting2[numProcessors - 1] = new ParallelRouting2(numProcessors - 1, this, CBGToShopDistances, (int) Math.floor((numProcessors - 1) * ((shopMergedLocations.size()) / numProcessors)), shopMergedLocations.size(), trafficLayerIndex, shopMergedLocations, i, cbgs);
//
//                for (int f = 0; f < numProcessors; f++) {
//                    parallelRouting2[f].myThread.start();
//                }
//                for (int f = 0; f < numProcessors; f++) {
//                    try {
//                        parallelRouting2[f].myThread.join();
//                    } catch (InterruptedException ie) {
//                        System.out.println(ie.toString());
//                    }
//                }
////                System.out.println(i);
//            }
            ArrayList<String[]> data = new ArrayList();
            for (int i = 0; i < CBGToShopDistances.length; i++) {
                String[] row = new String[CBGToShopDistances[i].length];
                for (int j = 0; j < CBGToShopDistances[i].length; j++) {
                    row[j] = String.valueOf(CBGToShopDistances[i][j]);
                }
                data.add(row);
            }

            try {
                CSVWriter writer = new CSVWriter(new FileWriter(myParent.mainModel.ABM.studyScope + "_CBGShopDistances.csv"));
                writer.writeAll(data);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("TRAVEL DISTANCES' FILE EXISTS");
            try {
                CSVReader csvReader = new CSVReader(new FileReader(myParent.mainModel.ABM.studyScope + "_CBGShopDistances.csv"));
                List<String[]> list = new ArrayList<>();
                list = csvReader.readAll();
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list.get(i).length; j++) {
                        CBGToShopDistances[i][j] = Double.valueOf(list.get(i)[j]);
                    }
                }
                csvReader.close();
                System.out.println("TRAVEL DISTANCES SUCCESSFULLY READ");
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("TRAVEL DISTANCES FAILED TO READ");
            } catch (CsvException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("TRAVEL DISTANCES FAILED TO READ");
            }

        }

        file = new File(myParent.mainModel.ABM.studyScope + "_CBGShopNumbers.csv");
        if (file.exists() == false) {
            System.out.println(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size());
            for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
                System.out.println(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size());
                for (int j = 0; j < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                    if (isFoodAndGrocery(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.naics_code) == true) {
                        LocationNode place = getNearestNode(mainFParent, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lat, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lon, null);
                        if (place != null) {

                            boolean isUniqueLocationNode = true;
                            int shopIndex = -1;
                            for (int b = 0; b < shopLocations.size(); b++) {
                                if (shopLocations.get(b).node.lat == place.lat && shopLocations.get(b).node.lon == place.lon) {
                                    isUniqueLocationNode = false;
                                    shopIndex = b;
                                    break;
                                }
                            }

                            int shopGroupIndex = -1;
                            for (int y = 0; y < indicesUniqueList.size(); y++) {
                                if (indicesUniqueList.get(y).equals(indicesList.get(shopIndex))) {
                                    shopGroupIndex = y;
                                    break;
                                }
                            }

//                        int shopGroupIndex = indicesList.get(shopIndex);
                            LocationNodeSafegraph targetShopGroup = shopMergedLocations.get(shopGroupIndex);

                            if (isUniqueLocationNode == false) {
                                if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs != null) {
                                    for (int k = 0; k < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.size(); k++) {
                                        CensusBlockGroup cBG = myParent.mainModel.allGISData.findCensusBlockGroup(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.get(k).key);
                                        int cBGIndex = -1;
                                        for (int m = 0; m < cbgs.size(); m++) {
                                            if (cBG.id == cbgs.get(m).id) {
                                                cBGIndex = m;
                                                break;
                                            }
                                        }
                                        if (shopGroupIndex > -1 && cBGIndex > -1) {
                                            CBGToShopNumbers[cBGIndex][shopGroupIndex] = CBGToShopNumbers[cBGIndex][shopGroupIndex] + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.get(k).value;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println(j);
                }
            }

            ArrayList<String[]> data = new ArrayList();
            for (int i = 0; i < CBGToShopNumbers.length; i++) {
                String[] row = new String[CBGToShopNumbers[i].length];
                for (int j = 0; j < CBGToShopNumbers[i].length; j++) {
                    row[j] = String.valueOf(CBGToShopNumbers[i][j]);
                }
                data.add(row);
            }

            try {
                CSVWriter writer = new CSVWriter(new FileWriter(myParent.mainModel.ABM.studyScope + "_CBGShopNumbers.csv"));
                writer.writeAll(data);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("TRAVEL NUMBERS' FILE EXISTS");
            try {
                CSVReader csvReader = new CSVReader(new FileReader(myParent.mainModel.ABM.studyScope + "_CBGShopNumbers.csv"));
                List<String[]> list = new ArrayList<>();
                list = csvReader.readAll();
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list.get(i).length; j++) {
                        CBGToShopNumbers[i][j] = Double.valueOf(list.get(i)[j]);
                    }
                }
                csvReader.close();
                System.out.println("TRAVEL NUMBERS SUCCESSFULLY READ");
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("TRAVEL NUMBERS FAILED TO READ");
            } catch (CsvException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("TRAVEL NUMBERS FAILED TO READ");
            }
        }

        int numVisitsToNearestOrder[] = new int[shopMergedLocations.size()];
        double distanceByNearestOrder[] = new double[shopMergedLocations.size()];

        int numVisitsToNearestOrderByCBGShop[][] = new int[cbgs.size()][shopMergedLocations.size()];
        double distanceByNearestOrderByCBGShop[][] = new double[cbgs.size()][shopMergedLocations.size()];

        for (int i = 0; i < cbgs.size(); i++) {
            Integer idx[] = new Integer[shopMergedLocations.size()];
            for (int j = 0; j < shopMergedLocations.size(); j++) {
                idx[j] = j;
            }

            final int passed_i = i;
            Arrays.sort(idx, new Comparator<Integer>() {
                @Override
                public int compare(final Integer o1, final Integer o2) {
                    return Double.compare(CBGToShopDistances[passed_i][o1], CBGToShopDistances[passed_i][o2]);
                }
            });

            for (int j = 0; j < shopMergedLocations.size(); j++) {
                numVisitsToNearestOrder[j] = numVisitsToNearestOrder[j] + (int) (CBGToShopNumbers[i][idx[j]] * cbgs.get(i).population);
                distanceByNearestOrder[j] = distanceByNearestOrder[j] + CBGToShopDistances[i][idx[j]];
                numVisitsToNearestOrderByCBGShop[i][j] = (int) (CBGToShopNumbers[i][idx[j]] * cbgs.get(i).population);
                distanceByNearestOrderByCBGShop[i][j] = CBGToShopDistances[i][idx[j]];
            }
        }

        for (int j = 0; j < shopMergedLocations.size(); j++) {
            numVisitsToNearestOrder[j] = numVisitsToNearestOrder[j] / shopMergedLocations.size();
            distanceByNearestOrder[j] = distanceByNearestOrder[j] / shopMergedLocations.size();
        }

        writeLoadAggregatedOrderData(numVisitsToNearestOrder, distanceByNearestOrder);
        writeLoadDetailedOrderData(numVisitsToNearestOrderByCBGShop, distanceByNearestOrderByCBGShop);

    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        ArrayList<LocationNodeSafegraph> schoolLocations = initSchoolLocations();
        Integer indices[] = labelMergedFacilities(schoolLocations, shopMergeThreshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);
        ArrayList<LocationNodeSafegraph> schoolMergedLocations = mergeFacilitiesWithIndices(schoolLocations, indicesList, indicesUniqueList, null);

        int trafficLayerIndex = -1;
        for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
            if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                trafficLayerIndex = i;
            }
        }

        int numVisitsToNearestOrder[] = new int[schoolMergedLocations.size()];

        int numProcessors = myParent.mainModel.numCPUs;
//        if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//            numProcessors = Runtime.getRuntime().availableProcessors();
//        }

        mainFParent.allData.setParallelLayers(numProcessors, -1);

        for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                if (isSchool(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.naics_code) == true) {
                    LocationNode place = getNearestNode(mainFParent, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lat, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lon, null);
                    if (place != null) {

                        boolean isUniqueLocationNode = true;
                        int shopIndex = -1;
                        for (int b = 0; b < schoolLocations.size(); b++) {
                            if (schoolLocations.get(b).node.lat == place.lat && schoolLocations.get(b).node.lon == place.lon) {
                                isUniqueLocationNode = false;
                                shopIndex = b;
                                break;
                            }
                        }

                        int schoolGroupIndex = -1;
                        for (int y = 0; y < indicesUniqueList.size(); y++) {
                            if (indicesUniqueList.get(y).equals(indicesList.get(shopIndex))) {
                                schoolGroupIndex = y;
                                break;
                            }
                        }

//                        int shopGroupIndex = indicesList.get(shopIndex);
                        LocationNodeSafegraph targetShopGroup = schoolMergedLocations.get(schoolGroupIndex);

                        if (isUniqueLocationNode == false) {
                            if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs != null) {
                                for (int k = 0; k < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.size(); k++) {
                                    CensusBlockGroup cBG = myParent.mainModel.allGISData.findCensusBlockGroup(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.get(k).key);
                                    LocationNode home = getNearestNode(mainFParent, cBG.lon, cBG.lat, null);

                                    if (home == null) {
                                        float collisionPositionx = cBG.lat;
                                        float collisionPositiony = cBG.lon;
                                        double leastDistance = Double.POSITIVE_INFINITY;
                                        LocationNode nearestNode = mainFParent.allData.all_Nodes[0];
                                        for (int g = 0; g < mainFParent.allData.all_Nodes.length; g++) {
                                            //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
                                            double dist = Math.sqrt(Math.pow(collisionPositionx - mainFParent.allData.all_Nodes[g].lat, 2) + Math.pow(collisionPositiony - mainFParent.allData.all_Nodes[g].lon, 2));
                                            if (dist < leastDistance) {
                                                nearestNode = mainFParent.allData.all_Nodes[g];
                                                leastDistance = dist;
                                            }
                                        }
                                        home = nearestNode;
                                    }

                                    Routing routing = new Routing(mainFParent.allData, trafficLayerIndex, 0);

                                    routing.findPath(home, targetShopGroup.node);
                                    double distanceToTarget = routing.pathDistance;

                                    double distancesToOtherTargets[] = new double[schoolMergedLocations.size()];

                                    ParallelRouting parallelRouting[] = new ParallelRouting[numProcessors];

                                    for (int f = 0; f < numProcessors - 1; f++) {
                                        parallelRouting[f] = new ParallelRouting(f, this, distancesToOtherTargets, (int) Math.floor(f * ((schoolMergedLocations.size()) / numProcessors)), (int) Math.floor((f + 1) * ((schoolMergedLocations.size()) / numProcessors)), trafficLayerIndex, home, schoolMergedLocations);
                                    }
                                    parallelRouting[numProcessors - 1] = new ParallelRouting(numProcessors - 1, this, distancesToOtherTargets, (int) Math.floor((numProcessors - 1) * ((schoolMergedLocations.size()) / numProcessors)), schoolMergedLocations.size(), trafficLayerIndex, home, schoolMergedLocations);

                                    for (int f = 0; f < numProcessors; f++) {
                                        parallelRouting[f].myThread.start();
                                    }
                                    for (int f = 0; f < numProcessors; f++) {
                                        try {
                                            parallelRouting[f].myThread.join();
//                                            for (int d = 0; d < parallelRouting[f].myData.length; d++) {
//                                                distancesToOtherTargets[d]=distancesToOtherTargets[d]+parallelRouting[f].myData[d];
//                                            }
//                                            System.out.println("thread " + f + "finished for location nodes: " + parallelRouting[f].myStartIndex + " | " + parallelRouting[f].myEndIndex);
                                        } catch (InterruptedException ie) {
                                            System.out.println(ie.toString());
                                        }
                                    }

//                                    for (int h = 0; h < shopMergedLocations.size(); h++) {
////                                    if (h != shopGroupIndex) {
//                                        Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, 0);
//                                        routingToOthers.findPath(home, shopMergedLocations.get(h));
//                                        distancesToOtherTargets[h] = routingToOthers.pathDistance;
////                                    }
//                                    }
                                    Arrays.sort(distancesToOtherTargets);
                                    int orderNumber = -1;
                                    for (int m = 0; m < distancesToOtherTargets.length; m++) {
                                        if (distancesToOtherTargets[m] == distanceToTarget && distanceToTarget != Double.POSITIVE_INFINITY) {
                                            orderNumber = m;
                                            break;
                                        }
                                    }
                                    if (orderNumber != -1) {
                                        numVisitsToNearestOrder[orderNumber] = numVisitsToNearestOrder[orderNumber] + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.get(k).value;
                                    }
                                }
                                for (int r = 0; r < numVisitsToNearestOrder.length; r++) {
                                    System.out.println(r + " " + numVisitsToNearestOrder[r]);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < numVisitsToNearestOrder.length; i++) {
            System.out.println(i + " " + numVisitsToNearestOrder[i]);
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        ArrayList<LocationNodeSafegraph> templeLocations = initTempleLocations();
        Integer indices[] = labelMergedFacilities(templeLocations, templeMergeThreshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);
        ArrayList<LocationNodeSafegraph> templeMergedLocations = mergeFacilitiesWithIndices(templeLocations, indicesList, indicesUniqueList, null);

        int trafficLayerIndex = -1;
        for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
            if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                trafficLayerIndex = i;
            }
        }

        int numVisitsToNearestOrder[] = new int[templeMergedLocations.size()];

        int numProcessors = myParent.mainModel.numCPUs;
//        if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//            numProcessors = Runtime.getRuntime().availableProcessors();
//        }

        mainFParent.allData.setParallelLayers(numProcessors, -1);

        for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                if (isReligiousOrganization(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.naics_code) == true) {
                    LocationNode place = getNearestNode(mainFParent, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lat, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lon, null);
                    if (place != null) {

                        boolean isUniqueLocationNode = true;
                        int templeIndex = -1;
                        for (int b = 0; b < templeLocations.size(); b++) {
                            if (templeLocations.get(b).node.lat == place.lat && templeLocations.get(b).node.lon == place.lon) {
                                isUniqueLocationNode = false;
                                templeIndex = b;
                                break;
                            }
                        }

                        int templeGroupIndex = -1;
                        for (int y = 0; y < indicesUniqueList.size(); y++) {
                            if (indicesUniqueList.get(y).equals(indicesList.get(templeIndex))) {
                                templeGroupIndex = y;
                                break;
                            }
                        }

//                        int shopGroupIndex = indicesList.get(shopIndex);
                        LocationNodeSafegraph targetShopGroup = templeMergedLocations.get(templeGroupIndex);

                        if (isUniqueLocationNode == false) {
                            if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs != null) {
                                for (int k = 0; k < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.size(); k++) {
                                    CensusBlockGroup cBG = myParent.mainModel.allGISData.findCensusBlockGroup(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.get(k).key);
                                    LocationNode home = getNearestNode(mainFParent, cBG.lon, cBG.lat, null);

                                    if (home == null) {
                                        float collisionPositionx = cBG.lat;
                                        float collisionPositiony = cBG.lon;
                                        double leastDistance = Double.POSITIVE_INFINITY;
                                        LocationNode nearestNode = mainFParent.allData.all_Nodes[0];
                                        for (int g = 0; g < mainFParent.allData.all_Nodes.length; g++) {
                                            //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
                                            double dist = Math.sqrt(Math.pow(collisionPositionx - mainFParent.allData.all_Nodes[g].lat, 2) + Math.pow(collisionPositiony - mainFParent.allData.all_Nodes[g].lon, 2));
                                            if (dist < leastDistance) {
                                                nearestNode = mainFParent.allData.all_Nodes[g];
                                                leastDistance = dist;
                                            }
                                        }
                                        home = nearestNode;
                                    }

                                    Routing routing = new Routing(mainFParent.allData, trafficLayerIndex, 0);

                                    routing.findPath(home, targetShopGroup.node);
                                    double distanceToTarget = routing.pathDistance;

                                    double distancesToOtherTargets[] = new double[templeMergedLocations.size()];

                                    ParallelRouting parallelRouting[] = new ParallelRouting[numProcessors];

                                    for (int f = 0; f < numProcessors - 1; f++) {
                                        parallelRouting[f] = new ParallelRouting(f, this, distancesToOtherTargets, (int) Math.floor(f * ((templeMergedLocations.size()) / numProcessors)), (int) Math.floor((f + 1) * ((templeMergedLocations.size()) / numProcessors)), trafficLayerIndex, home, templeMergedLocations);
                                    }
                                    parallelRouting[numProcessors - 1] = new ParallelRouting(numProcessors - 1, this, distancesToOtherTargets, (int) Math.floor((numProcessors - 1) * ((templeMergedLocations.size()) / numProcessors)), templeMergedLocations.size(), trafficLayerIndex, home, templeMergedLocations);

                                    for (int f = 0; f < numProcessors; f++) {
                                        parallelRouting[f].myThread.start();
                                    }
                                    for (int f = 0; f < numProcessors; f++) {
                                        try {
                                            parallelRouting[f].myThread.join();
//                                            for (int d = 0; d < parallelRouting[f].myData.length; d++) {
//                                                distancesToOtherTargets[d]=distancesToOtherTargets[d]+parallelRouting[f].myData[d];
//                                            }
//                                            System.out.println("thread " + f + "finished for location nodes: " + parallelRouting[f].myStartIndex + " | " + parallelRouting[f].myEndIndex);
                                        } catch (InterruptedException ie) {
                                            System.out.println(ie.toString());
                                        }
                                    }

//                                    for (int h = 0; h < shopMergedLocations.size(); h++) {
////                                    if (h != shopGroupIndex) {
//                                        Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, 0);
//                                        routingToOthers.findPath(home, shopMergedLocations.get(h));
//                                        distancesToOtherTargets[h] = routingToOthers.pathDistance;
////                                    }
//                                    }
                                    Arrays.sort(distancesToOtherTargets);
                                    int orderNumber = -1;
                                    for (int m = 0; m < distancesToOtherTargets.length; m++) {
                                        if (distancesToOtherTargets[m] == distanceToTarget && distanceToTarget != Double.POSITIVE_INFINITY) {
                                            orderNumber = m;
                                            break;
                                        }
                                    }
                                    if (orderNumber != -1) {
                                        numVisitsToNearestOrder[orderNumber] = numVisitsToNearestOrder[orderNumber] + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.get(k).value;
                                    }
                                }
                                for (int r = 0; r < numVisitsToNearestOrder.length; r++) {
                                    System.out.println(r + " " + numVisitsToNearestOrder[r]);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < numVisitsToNearestOrder.length; i++) {
            System.out.println(i + " " + numVisitsToNearestOrder[i]);
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        ArrayList<LocationNodeSafegraph> shopLocations = initShopLocations();
        Integer indices[] = labelMergedFacilities(shopLocations, shopMergeThreshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);
        ArrayList<LocationNodeSafegraph> shopMergedLocations = mergeFacilitiesWithIndices(shopLocations, indicesList, indicesUniqueList, null);

        int trafficLayerIndex = -1;
        for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
            if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                trafficLayerIndex = i;
            }
        }

        int numVisitsToNearestOrder[] = new int[shopMergedLocations.size()];

        int numProcessors = myParent.mainModel.numCPUs;
//        if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//            numProcessors = Runtime.getRuntime().availableProcessors();
//        }

        mainFParent.allData.setParallelLayers(numProcessors, -1);

        for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                if (isFoodAndGrocery(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.naics_code) == true) {
                    LocationNode place = getNearestNode(mainFParent, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lat, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lon, null);
                    if (place != null) {

                        boolean isUniqueLocationNode = true;
                        int shopIndex = -1;
                        for (int b = 0; b < shopLocations.size(); b++) {
                            if (shopLocations.get(b).node.lat == place.lat && shopLocations.get(b).node.lon == place.lon) {
                                isUniqueLocationNode = false;
                                shopIndex = b;
                                break;
                            }
                        }

                        int shopGroupIndex = -1;
                        for (int y = 0; y < indicesUniqueList.size(); y++) {
                            if (indicesUniqueList.get(y).equals(indicesList.get(shopIndex))) {
                                shopGroupIndex = y;
                                break;
                            }
                        }

//                        int shopGroupIndex = indicesList.get(shopIndex);
                        LocationNodeSafegraph targetShopGroup = shopMergedLocations.get(shopGroupIndex);

                        if (isUniqueLocationNode == false) {
                            if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_daytime_cbgs != null) {
                                for (int k = 0; k < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_daytime_cbgs.size(); k++) {
                                    CensusBlockGroup cBG = myParent.mainModel.allGISData.findCensusBlockGroup(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_daytime_cbgs.get(k).key);
                                    LocationNode work = getNearestNode(mainFParent, cBG.lon, cBG.lat, null);

                                    if (work == null) {
                                        float collisionPositionx = cBG.lat;
                                        float collisionPositiony = cBG.lon;
                                        double leastDistance = Double.POSITIVE_INFINITY;
                                        LocationNode nearestNode = mainFParent.allData.all_Nodes[0];
                                        for (int g = 0; g < mainFParent.allData.all_Nodes.length; g++) {
                                            //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
                                            double dist = Math.sqrt(Math.pow(collisionPositionx - mainFParent.allData.all_Nodes[g].lat, 2) + Math.pow(collisionPositiony - mainFParent.allData.all_Nodes[g].lon, 2));
                                            if (dist < leastDistance) {
                                                nearestNode = mainFParent.allData.all_Nodes[g];
                                                leastDistance = dist;
                                            }
                                        }
                                        work = nearestNode;
                                    }

                                    Routing routing = new Routing(mainFParent.allData, trafficLayerIndex, 0);

                                    routing.findPath(work, targetShopGroup.node);
                                    double distanceToTarget = routing.pathDistance;

                                    double distancesToOtherTargets[] = new double[shopMergedLocations.size()];

                                    ParallelRouting parallelRouting[] = new ParallelRouting[numProcessors];

                                    for (int f = 0; f < numProcessors - 1; f++) {
                                        parallelRouting[f] = new ParallelRouting(f, this, distancesToOtherTargets, (int) Math.floor(f * ((shopMergedLocations.size()) / numProcessors)), (int) Math.floor((f + 1) * ((shopMergedLocations.size()) / numProcessors)), trafficLayerIndex, work, shopMergedLocations);
                                    }
                                    parallelRouting[numProcessors - 1] = new ParallelRouting(numProcessors - 1, this, distancesToOtherTargets, (int) Math.floor((numProcessors - 1) * ((shopMergedLocations.size()) / numProcessors)), shopMergedLocations.size(), trafficLayerIndex, work, shopMergedLocations);

                                    for (int f = 0; f < numProcessors; f++) {
                                        parallelRouting[f].myThread.start();
                                    }
                                    for (int f = 0; f < numProcessors; f++) {
                                        try {
                                            parallelRouting[f].myThread.join();
//                                            for (int d = 0; d < parallelRouting[f].myData.length; d++) {
//                                                distancesToOtherTargets[d]=distancesToOtherTargets[d]+parallelRouting[f].myData[d];
//                                            }
//                                            System.out.println("thread " + f + "finished for location nodes: " + parallelRouting[f].myStartIndex + " | " + parallelRouting[f].myEndIndex);
                                        } catch (InterruptedException ie) {
                                            System.out.println(ie.toString());
                                        }
                                    }

//                                    for (int h = 0; h < shopMergedLocations.size(); h++) {
////                                    if (h != shopGroupIndex) {
//                                        Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, 0);
//                                        routingToOthers.findPath(home, shopMergedLocations.get(h));
//                                        distancesToOtherTargets[h] = routingToOthers.pathDistance;
////                                    }
//                                    }
                                    Arrays.sort(distancesToOtherTargets);
                                    int orderNumber = -1;
                                    for (int m = 0; m < distancesToOtherTargets.length; m++) {
                                        if (distancesToOtherTargets[m] == distanceToTarget && distanceToTarget != Double.POSITIVE_INFINITY) {
                                            orderNumber = m;
                                            break;
                                        }
                                    }
                                    if (orderNumber != -1) {
                                        numVisitsToNearestOrder[orderNumber] = numVisitsToNearestOrder[orderNumber] + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_daytime_cbgs.get(k).value;
                                    }
                                }
                                for (int r = 0; r < numVisitsToNearestOrder.length; r++) {
                                    System.out.println(r + " " + numVisitsToNearestOrder[r]);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < numVisitsToNearestOrder.length; i++) {
            System.out.println(i + " " + numVisitsToNearestOrder[i]);
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        int comLayer = mainFParent.findLayerExactNotCaseSensitive("VDFMTH");
        int cBGLayer = mainFParent.findLayerExactNotCaseSensitive("CBG");

        int shopLayer = mainFParent.findLayerExactNotCaseSensitive("shops_v_VDFMTH");
        int schoolLayer = mainFParent.findLayerExactNotCaseSensitive("schools_v_VDFMTH");

        ArrayList<LocationNodeSafegraph> shopLocations = initShopLocations();
        Integer indices[] = labelMergedFacilities(shopLocations, shopMergeThreshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);

        ArrayList<LocationNodeSafegraph> shops = mergeFacilitiesWithIndices(shopLocations, indicesList, indicesUniqueList, null);

        ArrayList<LocationNodeSafegraph> schoolLocations = initSchoolLocations();
        indices = labelMergedFacilities(schoolLocations, schoolMergeThreshold);
        indicesRawList = Arrays.asList(indices);
        indicesList = new ArrayList(indicesRawList);
        indicesUniqueSetHS = new LinkedHashSet(indicesList);
        indicesUniqueList = new ArrayList(indicesUniqueSetHS);

        ArrayList<LocationNodeSafegraph> schools = mergeFacilitiesWithIndices(schoolLocations, indicesList, indicesUniqueList, null);

        System.out.println("ALL CBGS SIZE: " + ((LayerDefinition) mainFParent.allData.all_Layers.get(cBGLayer)).values.length);
        for (int i = 0; i < ((LayerDefinition) mainFParent.allData.all_Layers.get(cBGLayer)).values.length; i++) {
            System.out.println(i);
            if ((long) (((LayerDefinition) mainFParent.allData.all_Layers.get(cBGLayer)).values[i]) > 0) {
                CensusBlockGroup cBG = myParent.mainModel.allGISData.findCensusBlockGroup((long) (((LayerDefinition) mainFParent.allData.all_Layers.get(cBGLayer)).values[i]));
                if (cBG != null) {
                    for (int k = 0; k < mainFParent.allData.all_Nodes.length; k++) {
                        short cBGIndex = (short) (((short[]) mainFParent.allData.all_Nodes[k].layers.get(cBGLayer))[0] - 1);

                        if ((long) (((LayerDefinition) mainFParent.allData.all_Layers.get(cBGLayer)).values[cBGIndex]) < 1) {
                            continue;
                        }

                        CensusBlockGroup nodeCBG = myParent.mainModel.allGISData.findCensusBlockGroup((long) (((LayerDefinition) mainFParent.allData.all_Layers.get(cBGLayer)).values[cBGIndex]));

//                        if(nodeCBG==null){
//                            System.out.println(i);
//                        }
                        if (nodeCBG.id == cBG.id) {
                            short shopIndex = (short) (((short[]) mainFParent.allData.all_Nodes[k].layers.get(shopLayer))[0] - 1);
                            short schoolIndex = (short) (((short[]) mainFParent.allData.all_Nodes[k].layers.get(schoolLayer))[0] - 1);

                            if (shopIndex > 0) {
                                LocationNodeSafegraph targetShopGroup = shops.get(shopIndex - 1);
                                if (cBG.vDsPlacesShops == null) {
                                    cBG.vDsPlacesShops = new ArrayList();
                                    cBG.proportionOfVDsShops = new ArrayList();
                                    cBG.vDsPlacesShops.add(targetShopGroup.places);
                                    cBG.proportionOfVDsShops.add(1d);
                                } else {
                                    boolean isFound = false;
                                    for (int m = 0; m < cBG.vDsPlacesShops.size(); m++) {
                                        if (targetShopGroup.places.get(0) == cBG.vDsPlacesShops.get(m).get(0)) {
                                            cBG.proportionOfVDsShops.set(m, cBG.proportionOfVDsShops.get(m) + 1d);
                                            isFound = true;
                                            break;
                                        }
                                    }
                                    if (isFound == false) {
                                        cBG.vDsPlacesShops.add(targetShopGroup.places);
                                        cBG.proportionOfVDsShops.add(1d);
                                    }
                                }
                            }

                            if (schoolIndex > 0) {
                                LocationNodeSafegraph targetSchoolGroup = schools.get(schoolIndex - 1);
                                if (cBG.vDsPlacesSchools == null) {
                                    cBG.vDsPlacesSchools = new ArrayList();
                                    cBG.proportionOfVDsSchools = new ArrayList();
                                    cBG.vDsPlacesSchools.add(targetSchoolGroup.places);
                                    cBG.proportionOfVDsSchools.add(1d);
                                } else {
                                    boolean isFound = false;
                                    for (int m = 0; m < cBG.vDsPlacesSchools.size(); m++) {
                                        if (targetSchoolGroup.places.get(0) == cBG.vDsPlacesSchools.get(m).get(0)) {
                                            cBG.proportionOfVDsSchools.set(m, cBG.proportionOfVDsSchools.get(m) + 1d);
                                            isFound = true;
                                            break;
                                        }
                                    }
                                    if (isFound == false) {
                                        cBG.vDsPlacesSchools.add(targetSchoolGroup.places);
                                        cBG.proportionOfVDsSchools.add(1d);
                                    }
                                }
                            }

                        }

                    }

                }
            }
        }

        HashMap<String, String> perms = new HashMap();
        HashMap<String, Integer> usedPerms = new HashMap();
        HashMap<String, Integer> refinedPermsById = new HashMap();
        HashMap<String, Integer> refinedPermsbyUse = new HashMap();
        ArrayList<String> refinedPermsKeys = new ArrayList();
        int counter = 0;
        for (int i = 1; i < ((LayerDefinition) mainFParent.allData.all_Layers.get(comLayer)).categories.length; i++) {
            for (int j = 1; j < ((LayerDefinition) mainFParent.allData.all_Layers.get(cBGLayer)).categories.length; j++) {
                perms.put(i + "_" + j, String.valueOf(counter));
                usedPerms.put(i + "_" + j, 0);
                counter = counter + 1;
            }
        }
        usedPerms.put("0", 0);

        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            short shopIndex = (short) (((short[]) mainFParent.allData.all_Nodes[i].layers.get(comLayer))[0] - 1);
            short schoolIndex = (short) (((short[]) mainFParent.allData.all_Nodes[i].layers.get(cBGLayer))[0] - 1);
            int currentCounter = -1;
            if (shopIndex == 0 || schoolIndex == 0) {//NOT ASSIGNED SCENARIOS
                currentCounter = usedPerms.get("0");
            } else {
                currentCounter = usedPerms.get(shopIndex + "_" + schoolIndex);
            }

            usedPerms.put(shopIndex + "_" + schoolIndex, currentCounter + 1);
        }

        counter = 1;
        int refinedCounter = 0;
        for (Map.Entry<String, Integer> entry : usedPerms.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value > 0) {
                refinedPermsbyUse.put(key, value);
                refinedPermsById.put(key, refinedCounter + 1);
                refinedCounter = refinedCounter + 1;
            }
            counter = counter + 1;
        }

        LayerDefinition tempLayer = new LayerDefinition("category", "CBGVDFMTH");
        tempLayer.categories = new String[refinedCounter + 1];
        tempLayer.colors = new Color[refinedCounter + 1];
        tempLayer.values = new double[refinedCounter + 1];

        tempLayer.categories[0] = "NOT ASSIGNED";
        tempLayer.colors[0] = new Color(2, 2, 2);
        tempLayer.values[0] = Double.valueOf(1);
//        for (int i = 1; i < counter + 1; i++) {
//            tempLayer.categories[i] = "combination " + String.valueOf(i);
//            tempLayer.colors[i] = new Color(Color.HSBtoRGB((float) i / (float) counter + 1 - 1, 1, 1));
//            tempLayer.values[i] = Double.valueOf(i + 1);
//        }

        int combinationCounter = 1;
        for (Map.Entry<String, Integer> entry : refinedPermsById.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            tempLayer.categories[combinationCounter] = "combination " + key;
            tempLayer.colors[combinationCounter] = new Color(Color.HSBtoRGB((float) combinationCounter / (float) refinedCounter + 1 - 1, 1, 1));
            tempLayer.values[combinationCounter] = Double.valueOf(value);
            combinationCounter = combinationCounter + 1;
        }

        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            short shopIndex = (short) (((short[]) mainFParent.allData.all_Nodes[i].layers.get(comLayer))[0] - 1);
            short schoolIndex = (short) (((short[]) mainFParent.allData.all_Nodes[i].layers.get(cBGLayer))[0] - 1);
            String combinationIndex;
            if (shopIndex == 0 || schoolIndex == 0) {//NOT ASSIGNED SCENARIOS
                combinationIndex = "0";
            } else {
                combinationIndex = String.valueOf(refinedPermsById.get(shopIndex + "_" + schoolIndex));
            }
            short[] val = new short[1];
//            if(combinationIndex==null){
//                System.out.println("combinationIndex: "+combinationIndex);
//            }
            val[0] = (short) (Short.valueOf(combinationIndex) + 1);
//            if (val[0] == 0) {
//                System.out.println("!!!");
//            }
//            if (val[0] > 64) {
//                System.out.println("!!!");
//            }
            mainFParent.allData.all_Nodes[i].layers.add(val);
        }

        mainFParent.allData.all_Layers.add(tempLayer);
        mainFParent.refreshLayersList();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        int cBGIndex = mainFParent.findLayerExactNotCaseSensitive("CBG");
        ArrayList<MyPolygons> polygons = new ArrayList();
        ConvexHull convHull;
//        for (int i = 1; i < 3; i++) {
        for (int i = 1; i < ((LayerDefinition) (mainFParent.allData.all_Layers.get(cBGIndex))).categories.length; i++) {
            ArrayList<Coordinate> coordsArrayList = new ArrayList();
            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
                if (((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(cBGIndex))))[0] == i + 1) {
                    coordsArrayList.add(new Coordinate(mainFParent.allData.all_Nodes[j].lat, mainFParent.allData.all_Nodes[j].lon));
                }
            }
            GeometryFactory geomFactory = new GeometryFactory();
            Coordinate coords[] = new Coordinate[coordsArrayList.size()];
            for (int m = 0; m < coordsArrayList.size(); m++) {
                coords[m] = coordsArrayList.get(m);
            }
            convHull = new ConvexHull(coords, geomFactory);
            Coordinate coordsConvex[] = convHull.getConvexHull().getCoordinates();
            MyPolygons myPolies = new MyPolygons();
            myPolies.polygons.add(new MyPolygon());
            myPolies.polygons.get(0).points = new ArrayList();
            myPolies.severity = 1;
            for (int h = 0; h < coordsConvex.length; h++) {
                myPolies.polygons.get(0).points.add(new Location(coordsConvex[h].x, coordsConvex[h].y));
            }

//            LinearRing linearRing = geomFactory.createLinearRing(coordsConvex);
//            Polygon poly = geomFactory.createPolygon(linearRing);
            polygons.add(myPolies);
        }
        sketch.polygons = polygons;
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        sketch.setCaseStudyPanZoom(((Marker) myParent.mainModel.ABM.studyScopeGeography).size * 52, new Location(((Marker) myParent.mainModel.ABM.studyScopeGeography).lon, ((Marker) myParent.mainModel.ABM.studyScopeGeography).lat));
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        sketch = new COVIDGeoVisualization(this);
        sketch.startRendering();
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        int vDsIndex = mainFParent.findLayerExactNotCaseSensitive("VDs_CBGs");
        ArrayList<MyPolygons> polygons = new ArrayList();
        ConvexHull convHull;
//        for (int i = 1; i < 3; i++) {
        for (int i = 1; i < ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsIndex))).categories.length; i++) {
            ArrayList<Coordinate> coordsArrayList = new ArrayList();
            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
                if (((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(vDsIndex))))[0] == i + 1) {
                    coordsArrayList.add(new Coordinate(mainFParent.allData.all_Nodes[j].lat, mainFParent.allData.all_Nodes[j].lon));
                }
            }
            GeometryFactory geomFactory = new GeometryFactory();
            Coordinate coords[] = new Coordinate[coordsArrayList.size()];
            for (int m = 0; m < coordsArrayList.size(); m++) {
                coords[m] = coordsArrayList.get(m);
            }
            convHull = new ConvexHull(coords, geomFactory);
            Coordinate coordsConvex[] = convHull.getConvexHull().getCoordinates();
            MyPolygons myPolies = new MyPolygons();
            myPolies.polygons.add(new MyPolygon());
            myPolies.polygons.get(0).points = new ArrayList();
            myPolies.severity = 1;
            for (int h = 0; h < coordsConvex.length; h++) {
                myPolies.polygons.get(0).points.add(new Location(coordsConvex[h].x, coordsConvex[h].y));
            }

//            LinearRing linearRing = geomFactory.createLinearRing(coordsConvex);
//            Polygon poly = geomFactory.createPolygon(linearRing);
            polygons.add(myPolies);
        }
        sketch.polygons = polygons;
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        int vDsIndex = mainFParent.findLayerExactNotCaseSensitive("voronoi_combination_v");
        ArrayList<MyPolygons> polygons = new ArrayList();
        ConvexHull convHull;
//        for (int i = 1; i < 3; i++) {
        for (int i = 1; i < ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsIndex))).categories.length; i++) {
            ArrayList<Coordinate> coordsArrayList = new ArrayList();
            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
                if (((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(vDsIndex))))[0] == i + 1) {
                    coordsArrayList.add(new Coordinate(mainFParent.allData.all_Nodes[j].lat, mainFParent.allData.all_Nodes[j].lon));
                }
            }
            GeometryFactory geomFactory = new GeometryFactory();
            Coordinate coords[] = new Coordinate[coordsArrayList.size()];
            for (int m = 0; m < coordsArrayList.size(); m++) {
                coords[m] = coordsArrayList.get(m);
            }
            convHull = new ConvexHull(coords, geomFactory);
            Coordinate coordsConvex[] = convHull.getConvexHull().getCoordinates();
            MyPolygons myPolies = new MyPolygons();
            myPolies.polygons.add(new MyPolygon());
            myPolies.polygons.get(0).points = new ArrayList();
            myPolies.severity = 1;
            for (int h = 0; h < coordsConvex.length; h++) {
                myPolies.polygons.get(0).points.add(new Location(coordsConvex[h].x, coordsConvex[h].y));
            }

//            LinearRing linearRing = geomFactory.createLinearRing(coordsConvex);
//            Polygon poly = geomFactory.createPolygon(linearRing);
            polygons.add(myPolies);
        }
        sketch.polygons = polygons;
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        String GISDataFileName = "./output_CBGs.csv";
        File GISDataFile = new File(GISDataFileName);
        ArrayList<Long> cBGIds = new ArrayList();
        ArrayList<Integer> cBGN = new ArrayList();
        ArrayList<Integer> cBGI = new ArrayList();
        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(GISDataFile, StandardCharsets.UTF_8);

            for (int j = 0; j < data.getRows().size(); j++) {
                cBGIds.add(Long.parseLong(data.getRow(j).getField("CBG")));
                cBGN.add(Integer.parseInt(data.getRow(j).getField("N")));
                cBGI.add(Integer.parseInt(data.getRow(j).getField("IS")) + Integer.parseInt(data.getRow(j).getField("IAS")));
            }
        } catch (IOException ex) {
            Logger.getLogger(COVIDGeoVisualization.class
                    .getName()).log(Level.SEVERE, (String) null, ex);
        }

        int cBGIndex = mainFParent.findLayerExactNotCaseSensitive("CBG");

        int numCBGs = ((LayerDefinition) (mainFParent.allData.all_Layers.get(cBGIndex))).categories.length;

        ArrayList<Float> colorValue = new ArrayList();

        for (int i = 0; i < numCBGs; i++) {
            colorValue.add(0f);
        }

        int sumColor = 0;

        int maxValue = 0;

        for (int i = 1; i < cBGI.size(); i++) {
            if (maxValue < cBGI.get(i)) {
                maxValue = cBGI.get(i);
            }
            sumColor = sumColor + cBGI.get(i);
        }

//        for (int i = 1; i < 3; i++) {
        for (int i = 1; i < ((LayerDefinition) (mainFParent.allData.all_Layers.get(cBGIndex))).categories.length; i++) {
            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
                if (((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(cBGIndex))))[0] == i + 1) {
                    for (int k = 0; k < cBGIds.size(); k++) {
                        if (Double.valueOf(((LayerDefinition) (mainFParent.allData.all_Layers.get(cBGIndex))).values[i]).longValue() == cBGIds.get(k)) {
                            colorValue.set(i, (float) Math.pow((float) cBGI.get(k) / (float) maxValue, 0.5));
                        }
                    }
                }
            }
        }

        for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
            short[] newVal = new short[1];
            newVal[0] = ((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(cBGIndex))))[0];
            mainFParent.allData.all_Nodes[j].layers.add(newVal);
        }

        LayerDefinition tempLayer = new LayerDefinition("category", "CBG infections");

        tempLayer.categories = new String[numCBGs];
        tempLayer.colors = new Color[numCBGs];
        tempLayer.values = new double[numCBGs];

        tempLayer.categories[0] = "NOT ASSIGNED";
        tempLayer.colors[0] = new Color(2, 2, 2);
        tempLayer.values[0] = Double.valueOf(0);
        for (int i = 1; i < numCBGs; i++) {
            tempLayer.categories[i] = "CBG " + String.valueOf(i);
            tempLayer.colors[i] = new Color(colorValue.get(i), 0, 1 - colorValue.get(i));
            tempLayer.values[i] = ((LayerDefinition) (mainFParent.allData.all_Layers.get(cBGIndex))).values[i];
        }

        mainFParent.allData.all_Layers.add(tempLayer);
        mainFParent.refreshLayersList();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        int vDsIndex = mainFParent.findLayerExactNotCaseSensitive("voronoi_combination_v");
        ArrayList<MyPolygons> polygons = new ArrayList();
        ConvexHull convHull;
//        for (int i = 1; i < 3; i++) {
        for (int i = 1; i < ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsIndex))).categories.length; i++) {
            ArrayList<Coordinate> coordsArrayList = new ArrayList();
            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
                if (((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(vDsIndex))))[0] == i + 1) {
                    coordsArrayList.add(new Coordinate(mainFParent.allData.all_Nodes[j].lat, mainFParent.allData.all_Nodes[j].lon));
                }
            }
            GeometryFactory geomFactory = new GeometryFactory();
            Coordinate coords[] = new Coordinate[coordsArrayList.size()];
            for (int m = 0; m < coordsArrayList.size(); m++) {
                coords[m] = coordsArrayList.get(m);
            }
            convHull = new ConvexHull(coords, geomFactory);
            Coordinate coordsConvex[] = convHull.getConvexHull().getCoordinates();
            MyPolygons myPolies = new MyPolygons();
            myPolies.polygons.add(new MyPolygon());
            myPolies.polygons.get(0).points = new ArrayList();
            myPolies.severity = 1;
            for (int h = 0; h < coordsConvex.length; h++) {
                myPolies.polygons.get(0).points.add(new Location(coordsConvex[h].x, coordsConvex[h].y));
            }

//            LinearRing linearRing = geomFactory.createLinearRing(coordsConvex);
//            Polygon poly = geomFactory.createPolygon(linearRing);
            polygons.add(myPolies);
        }

        String GISDataFileName = "./output_CBGs.csv";
        File GISDataFile = new File(GISDataFileName);
        ArrayList<Long> cBGIds = new ArrayList();
        ArrayList<Integer> cBGN = new ArrayList();
        ArrayList<Integer> cBGI = new ArrayList();

        int numVDs = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsIndex))).categories.length;

        ArrayList<Integer> vDN = new ArrayList();
        ArrayList<Integer> vDI = new ArrayList();

        for (int i = 0; i < numVDs; i++) {
            vDN.add(0);
            vDI.add(0);
        }

        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(GISDataFile, StandardCharsets.UTF_8);

            for (int j = 0; j < data.getRows().size(); j++) {
                cBGIds.add(Long.parseLong(data.getRow(j).getField("CBG")));
                cBGN.add(Integer.parseInt(data.getRow(j).getField("N")));
                cBGI.add(Integer.parseInt(data.getRow(j).getField("IS")) + Integer.parseInt(data.getRow(j).getField("IAS")));

                CensusBlockGroup cBGGeo = myParent.mainModel.allGISData.findCensusBlockGroup(Long.parseLong(data.getRow(j).getField("CBG")));
                for (int l = 0; l < polygons.size(); l++) {
                    Polygon polyConverted = MyPolygon.myPolygonToJTSPolygon(polygons.get(l).polygons.get(0));
                    if (polyConverted != null) {
                        if (isInside(cBGGeo.lon, cBGGeo.lat, polyConverted) == true) {
                            vDN.set(l, vDN.get(l) + Integer.parseInt(data.getRow(j).getField("N")));
                            vDI.set(l, vDI.get(l) + Integer.parseInt(data.getRow(j).getField("IS")) + Integer.parseInt(data.getRow(j).getField("IAS")));
                        }
                    }
//                    if(isInside(cBGGeo.lon,cBGGeo.lat,MyPolygon.myPolygonToJTSPolygon(polygons.get(l)))==true){
//                        vDN.set(l,vDN.get(l)+Integer.parseInt(data.getRow(j).getField("N")));
//                        vDI.set(l,vDI.get(l)+Integer.parseInt(data.getRow(j).getField("IS")) + Integer.parseInt(data.getRow(j).getField("IAS")));
//                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(COVIDGeoVisualization.class
                    .getName()).log(Level.SEVERE, (String) null, ex);
        }

        ArrayList<Float> colorValue = new ArrayList();

        for (int i = 0; i < numVDs; i++) {
            colorValue.add(0f);
        }

        int sumColor = 0;

        int maxValue = 0;

        for (int i = 1; i < vDI.size(); i++) {
            if (maxValue < vDI.get(i)) {
                maxValue = vDI.get(i);
            }
            sumColor = sumColor + vDI.get(i);
        }

//        for (int i = 1; i < 3; i++) {
        for (int i = 1; i < ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsIndex))).categories.length; i++) {
            colorValue.set(i, (float) Math.pow((float) vDI.get(i) / (float) maxValue, 0.5));
//            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
//                if (((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(vDIndex))))[0] == i + 1) {
//                    for (int k = 0; k < cBGIds.size(); k++) {
//                        if (Double.valueOf(((LayerDefinition) (mainFParent.allData.all_Layers.get(vDIndex))).values[i]).longValue() == cBGIds.get(k)) {
//                            colorValue.set(i, (float)Math.pow((float) vDI.get(k) / (float) maxValue,0.5));
//                        }
//                    }
//                }
//            }
        }

        for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
            short[] newVal = new short[1];
            newVal[0] = ((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(vDsIndex))))[0];
            mainFParent.allData.all_Nodes[j].layers.add(newVal);
        }

        LayerDefinition tempLayer = new LayerDefinition("category", "VD infections");

        tempLayer.categories = new String[numVDs];
        tempLayer.colors = new Color[numVDs];
        tempLayer.values = new double[numVDs];

        tempLayer.categories[0] = "NOT ASSIGNED";
        tempLayer.colors[0] = new Color(2, 2, 2);
        tempLayer.values[0] = Double.valueOf(0);
        for (int i = 1; i < numVDs; i++) {
            tempLayer.categories[i] = "VD " + String.valueOf(i);
            tempLayer.colors[i] = new Color(colorValue.get(i), 0, 1 - colorValue.get(i));
            tempLayer.values[i] = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsIndex))).values[i];
        }

        mainFParent.allData.all_Layers.add(tempLayer);
        mainFParent.refreshLayersList();
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        int vDsCBGsIndex = mainFParent.findLayerExactNotCaseSensitive("VDs_CBGs");
        ArrayList<MyPolygons> polygons = new ArrayList();
        ConvexHull convHull;
//        for (int i = 1; i < 3; i++) {
        for (int i = 1; i < ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsCBGsIndex))).categories.length; i++) {
            ArrayList<Coordinate> coordsArrayList = new ArrayList();
            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
                if (((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(vDsCBGsIndex))))[0] == i + 1) {
                    coordsArrayList.add(new Coordinate(mainFParent.allData.all_Nodes[j].lat, mainFParent.allData.all_Nodes[j].lon));
                }
            }
            GeometryFactory geomFactory = new GeometryFactory();
            Coordinate coords[] = new Coordinate[coordsArrayList.size()];
            for (int m = 0; m < coordsArrayList.size(); m++) {
                coords[m] = coordsArrayList.get(m);
            }
            convHull = new ConvexHull(coords, geomFactory);
            Coordinate coordsConvex[] = convHull.getConvexHull().getCoordinates();
            MyPolygons myPolies = new MyPolygons();
            myPolies.polygons.add(new MyPolygon());
            myPolies.polygons.get(0).points = new ArrayList();
            myPolies.severity = 1;
            for (int h = 0; h < coordsConvex.length; h++) {
                myPolies.polygons.get(0).points.add(new Location(coordsConvex[h].x, coordsConvex[h].y));
            }

//            LinearRing linearRing = geomFactory.createLinearRing(coordsConvex);
//            Polygon poly = geomFactory.createPolygon(linearRing);
            polygons.add(myPolies);
        }

        String GISDataFileName = "./output_CBGs.csv";
        File GISDataFile = new File(GISDataFileName);
        ArrayList<Long> cBGIds = new ArrayList();
        ArrayList<Integer> cBGN = new ArrayList();
        ArrayList<Integer> cBGI = new ArrayList();

        int numVDsCBGs = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsCBGsIndex))).categories.length;

        ArrayList<Integer> vDCBGN = new ArrayList();
        ArrayList<Integer> vDCBGI = new ArrayList();

        for (int i = 0; i < numVDsCBGs; i++) {
            vDCBGN.add(0);
            vDCBGI.add(0);
        }

        try {
            CsvReader cSVReader = new CsvReader();
            cSVReader.setContainsHeader(true);
            CsvContainer data = cSVReader.read(GISDataFile, StandardCharsets.UTF_8);

            for (int j = 0; j < data.getRows().size(); j++) {
                cBGIds.add(Long.parseLong(data.getRow(j).getField("CBG")));
                cBGN.add(Integer.parseInt(data.getRow(j).getField("N")));
                cBGI.add(Integer.parseInt(data.getRow(j).getField("IS")) + Integer.parseInt(data.getRow(j).getField("IAS")));

                CensusBlockGroup cBGGeo = myParent.mainModel.allGISData.findCensusBlockGroup(Long.parseLong(data.getRow(j).getField("CBG")));
                for (int l = 0; l < polygons.size(); l++) {
                    Polygon polyConverted = MyPolygon.myPolygonToJTSPolygon(polygons.get(l).polygons.get(0));
                    if (polyConverted != null) {
                        if (isInside(cBGGeo.lon, cBGGeo.lat, polyConverted) == true) {
                            vDCBGN.set(l, vDCBGN.get(l) + Integer.parseInt(data.getRow(j).getField("N")));
                            vDCBGI.set(l, vDCBGI.get(l) + Integer.parseInt(data.getRow(j).getField("IS")) + Integer.parseInt(data.getRow(j).getField("IAS")));
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(COVIDGeoVisualization.class
                    .getName()).log(Level.SEVERE, (String) null, ex);
        }

        ArrayList<Float> colorValue = new ArrayList();

        for (int i = 0; i < numVDsCBGs; i++) {
            colorValue.add(0f);
        }

        int sumColor = 0;

        int maxValue = 0;

        for (int i = 1; i < vDCBGI.size(); i++) {
            if (maxValue < vDCBGI.get(i)) {
                maxValue = vDCBGI.get(i);
            }
            sumColor = sumColor + vDCBGI.get(i);
        }

//        for (int i = 1; i < 3; i++) {
        for (int i = 1; i < ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsCBGsIndex))).categories.length; i++) {
            colorValue.set(i, (float) Math.pow((float) vDCBGI.get(i) / (float) maxValue, 0.5));
//            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
//                if (((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(vDIndex))))[0] == i + 1) {
//                    for (int k = 0; k < cBGIds.size(); k++) {
//                        if (Double.valueOf(((LayerDefinition) (mainFParent.allData.all_Layers.get(vDIndex))).values[i]).longValue() == cBGIds.get(k)) {
//                            colorValue.set(i, (float)Math.pow((float) vDI.get(k) / (float) maxValue,0.5));
//                        }
//                    }
//                }
//            }
        }

        for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
            short[] newVal = new short[1];
            newVal[0] = ((short[]) ((mainFParent.allData.all_Nodes[j].layers.get(vDsCBGsIndex))))[0];
            mainFParent.allData.all_Nodes[j].layers.add(newVal);
        }

        LayerDefinition tempLayer = new LayerDefinition("category", "VD_CBG infections");

        tempLayer.categories = new String[numVDsCBGs];
        tempLayer.colors = new Color[numVDsCBGs];
        tempLayer.values = new double[numVDsCBGs];

        tempLayer.categories[0] = "NOT ASSIGNED";
        tempLayer.colors[0] = new Color(2, 2, 2);
        tempLayer.values[0] = Double.valueOf(0);
        for (int i = 1; i < numVDsCBGs; i++) {
            tempLayer.categories[i] = "VD_CBG " + String.valueOf(i);
            tempLayer.colors[i] = new Color(colorValue.get(i), 0, 1 - colorValue.get(i));
            tempLayer.values[i] = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDsCBGsIndex))).values[i];
        }

        mainFParent.allData.all_Layers.add(tempLayer);
        mainFParent.refreshLayersList();
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        saveTessellations();
    }//GEN-LAST:event_jButton24ActionPerformed

    @Deprecated
    public void writeSupplementaryData() {
        if (myParent.mainModel.ABM.studyScopeGeography instanceof City) {
            City city = (City) (myParent.mainModel.ABM.studyScopeGeography);

            city.vDCells = new ArrayList();
            int vDLayerIndex = mainFParent.findLayerExactNotCaseSensitive("VDFMTH");
            int cBGLayerIndex = mainFParent.findLayerExactNotCaseSensitive("CBG");
            int cBGVDLayerIndex = mainFParent.findLayerExactNotCaseSensitive("CBGVDFMTH");

            System.out.println("num VDs: " + ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories.length);

            for (int vdIndex = 1; vdIndex < ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories.length; vdIndex++) {
                city.vDCells.add(new VDCell());
                city.vDCells.get(vdIndex - 1).cBGsInvolved = new ArrayList();
                city.vDCells.get(vdIndex - 1).cBGsIDsInvolved = new ArrayList();
                city.vDCells.get(vdIndex - 1).cBGsPercentageInvolved = new ArrayList();
                city.vDCells.get(vdIndex - 1).myIndex = vdIndex;

                HashMap<CensusBlockGroup, Integer> cBGNumNodesHashMap = null;

                cBGNumNodesHashMap = getHashNumNodeForCBG(mainFParent.allData, myParent.mainModel.allGISData, vdIndex, vDLayerIndex, cBGLayerIndex);

//                for (int h = vdIndex + 1; h < ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories.length; h++) {
//                    cBGNumNodesHashMap = getHashNumNodeForCBG(vdIndex, vDLayerIndex, cBGLayerIndex);
//
////                if(vdIndex==7){
////                    System.out.println("!!!!!!!!!!!");
////                }
//                    if (cBGNumNodesHashMap.isEmpty() == true) {//REMOVE THIS VD CELL BECAUSE NO NODE HAS IT
//                        removeVDCell(vdIndex, vDLayerIndex);
//                    }
//                }
                double sumNodes = 0;
                for (Map.Entry<CensusBlockGroup, Integer> set : cBGNumNodesHashMap.entrySet()) {
                    sumNodes += set.getValue();
                }
                for (Map.Entry<CensusBlockGroup, Integer> set : cBGNumNodesHashMap.entrySet()) {
                    city.vDCells.get(vdIndex - 1).cBGsInvolved.add(set.getKey());
                    if (set.getKey() != null) {
                        city.vDCells.get(vdIndex - 1).cBGsIDsInvolved.add(set.getKey().id);
                    } else {
                        city.vDCells.get(vdIndex - 1).cBGsIDsInvolved.add(-1l);
                    }
                    city.vDCells.get(vdIndex - 1).cBGsPercentageInvolved.add((double) (set.getValue()) / sumNodes);
                }
                if (vdIndex >= 1) {
                    String[] shopSchoolTemp = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories[vdIndex].split(" ");
                    String[] shopSchool = shopSchoolTemp[1].split("_");
                    int shopIndex = Integer.valueOf(shopSchool[0]);
                    int schoolIndex = Integer.valueOf(shopSchool[1]);
//                    System.out.println(shopIndex);
//                    System.out.println(schoolIndex);
//                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
                    if (shopIndex > 0 && schoolIndex > 0) {
                        city.vDCells.get(vdIndex - 1).shopPlaces = shopLocationNodes.get(shopIndex - 1).places;
                        city.vDCells.get(vdIndex - 1).shopPlacesKeys = shopLocationNodes.get(shopIndex - 1).placeKeys;

                        System.out.println("vdIndex: " + vdIndex);
                        System.out.println("city.vDCells.size(): " + city.vDCells.size());
                        System.out.println("schoolIndex: " + schoolIndex);
                        System.out.println("shopLocationNodes.size(): " + shopLocationNodes.size());
                        city.vDCells.get(vdIndex - 1).schoolPlaces = schoolLocationNodes.get(schoolIndex - 1).places;
                        city.vDCells.get(vdIndex - 1).schoolPlacesKeys = schoolLocationNodes.get(schoolIndex - 1).placeKeys;
                    }
                }
            }

            city.cBGVDCells = new ArrayList();

//            int cBGLayerIndex=mainFParent.findLayer("censusBlockGroups");
            for (int cBGVDIndex = 1; cBGVDIndex < ((LayerDefinition) (mainFParent.allData.all_Layers.get(cBGVDLayerIndex))).categories.length; cBGVDIndex++) {
                city.cBGVDCells.add(new CBGVDCell());
                city.cBGVDCells.get(cBGVDIndex - 1).cBGsInvolved = new ArrayList();
                city.cBGVDCells.get(cBGVDIndex - 1).cBGsIDsInvolved = new ArrayList();
                city.cBGVDCells.get(cBGVDIndex - 1).cBGsPercentageInvolved = new ArrayList();
                city.cBGVDCells.get(cBGVDIndex - 1).myIndex = cBGVDIndex;

                HashMap<CensusBlockGroup, Integer> cBGNumNodesHashMap = new HashMap();
                for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
                    if (((short[]) (mainFParent.allData.all_Nodes[i].layers.get(cBGVDLayerIndex)))[0] == cBGVDIndex) {
                        Double value = Double.valueOf(Math.round(((LayerDefinition) (mainFParent.allData.all_Layers.get(cBGLayerIndex))).values[((short[]) (mainFParent.allData.all_Nodes[i].layers.get(cBGLayerIndex)))[0] - 1]));
                        CensusBlockGroup cBG = myParent.mainModel.allGISData.findCensusBlockGroup(value.longValue());
                        if (cBGNumNodesHashMap.containsKey(cBG)) {
                            cBGNumNodesHashMap.put(cBG, cBGNumNodesHashMap.get(cBG) + 1);
                        } else {
                            cBGNumNodesHashMap.put(cBG, 1);
                        }
                    }
                }

                double sumNodes = 0;
                for (Map.Entry<CensusBlockGroup, Integer> set : cBGNumNodesHashMap.entrySet()) {
                    sumNodes += set.getValue();
                }

                for (Map.Entry<CensusBlockGroup, Integer> set : cBGNumNodesHashMap.entrySet()) {
                    city.cBGVDCells.get(cBGVDIndex - 1).cBGsInvolved.add(set.getKey());
                    if (set.getKey() != null) {
                        city.cBGVDCells.get(cBGVDIndex - 1).cBGsIDsInvolved.add(set.getKey().id);
                    } else {
                        city.cBGVDCells.get(cBGVDIndex - 1).cBGsIDsInvolved.add(-1l);
                    }
                    city.cBGVDCells.get(cBGVDIndex - 1).cBGsPercentageInvolved.add((double) (set.getValue()) / sumNodes);
                }

                if (cBGVDIndex > 1) {
                    String[] comCbgTemp = ((LayerDefinition) (mainFParent.allData.all_Layers.get(cBGVDLayerIndex))).categories[cBGVDIndex - 1].split(" ");
                    String[] comCbg = comCbgTemp[1].split("_");
                    int comIndex = Integer.valueOf(comCbg[0]);
                    //int schoolIndex = Integer.valueOf(comCbg[1]);
                    if (comIndex > 1) {
                        String[] shopSchoolTemp = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories[comIndex - 1].split(" ");
                        String[] shopSchool = shopSchoolTemp[1].split("_");
                        int shopIndex = Integer.valueOf(shopSchool[0]);
                        int schoolIndex = Integer.valueOf(shopSchool[1]);
//                    System.out.println(shopIndex);
//                    System.out.println(schoolIndex);
//                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
                        if (shopIndex > 0 && schoolIndex > 0) {
                            city.cBGVDCells.get(cBGVDIndex - 1).shopPlaces = shopLocationNodes.get(shopIndex - 1).places;
                            city.cBGVDCells.get(cBGVDIndex - 1).shopPlacesKeys = shopLocationNodes.get(shopIndex - 1).placeKeys;

                            city.cBGVDCells.get(cBGVDIndex - 1).schoolPlaces = schoolLocationNodes.get(schoolIndex - 1).places;
                            city.cBGVDCells.get(cBGVDIndex - 1).schoolPlacesKeys = schoolLocationNodes.get(schoolIndex - 1).placeKeys;
                        }
                    }
                }

            }

            SupplementaryCaseStudyData scsd = new SupplementaryCaseStudyData();
            scsd.shopMergePrecision = shopMergeThreshold;
            scsd.schoolMergePrecision = schoolMergeThreshold;
            scsd.templeMergePrecision = templeMergeThreshold;
            scsd.vDCells = new ArrayList(city.vDCells.size());
            for (int i = 0; i < city.vDCells.size(); i++) {
//                if (i == 5) {
//                    System.out.println("!!!!!!!!!!!");
//                }
//                if (city.vDCells.get(i).shopPlacesKeys.size() > 0 && city.vDCells.get(i).schoolPlacesKeys.size() > 0) {//THIS IS CAUSED BY A BUG POTENTIALLY! BUT REMOVED FOR NOW
                VDCell vDCell = new VDCell();
                vDCell.cBGsIDsInvolved = new ArrayList();
                vDCell.cBGsPercentageInvolved = new ArrayList();
                vDCell.shopPlacesKeys = new ArrayList();
                vDCell.schoolPlacesKeys = new ArrayList();
                vDCell.templePlacesKeys = new ArrayList();
                vDCell.remainingFreqs = new ArrayList();

                vDCell.myIndex = city.vDCells.get(i).myIndex;

                for (int j = 0; j < city.vDCells.get(i).cBGsInvolved.size(); j++) {
                    if (city.vDCells.get(i).cBGsInvolved.get(j) != null) {
                        vDCell.cBGsIDsInvolved.add(city.vDCells.get(i).cBGsInvolved.get(j).id);
                        vDCell.cBGsPercentageInvolved.add(city.vDCells.get(i).cBGsPercentageInvolved.get(j));
                    }
                }
//                    System.out.println(city.vDCells.get(i).shopPlaces.size());
                for (int j = 0; j < city.vDCells.get(i).shopPlacesKeys.size(); j++) {
                    vDCell.shopPlacesKeys.add(city.vDCells.get(i).shopPlacesKeys.get(j));
                }
                for (int j = 0; j < city.vDCells.get(i).schoolPlacesKeys.size(); j++) {
                    vDCell.schoolPlacesKeys.add(city.vDCells.get(i).schoolPlacesKeys.get(j));
                }
                scsd.vDCells.add(vDCell);
//                }
            }

            scsd.cBGVDCells = new ArrayList(city.cBGVDCells.size());
            for (int i = 0; i < city.cBGVDCells.size(); i++) {
                if (city.cBGVDCells.get(i).shopPlacesKeys.size() > 0 && city.cBGVDCells.get(i).schoolPlacesKeys.size() > 0) {
                    CBGVDCell cBGVDCell = new CBGVDCell();
                    cBGVDCell.cBGsIDsInvolved = new ArrayList();
                    cBGVDCell.cBGsPercentageInvolved = new ArrayList();
                    cBGVDCell.shopPlacesKeys = new ArrayList();
                    cBGVDCell.schoolPlacesKeys = new ArrayList();
                    cBGVDCell.templePlacesKeys = new ArrayList();
                    cBGVDCell.remainingFreqs = new ArrayList();

                    cBGVDCell.myIndex = city.cBGVDCells.get(i).myIndex;

                    for (int j = 0; j < city.cBGVDCells.get(i).cBGsInvolved.size(); j++) {
                        if (city.cBGVDCells.get(i).cBGsInvolved.get(j) != null) {
                            cBGVDCell.cBGsIDsInvolved.add(city.cBGVDCells.get(i).cBGsInvolved.get(j).id);
                            cBGVDCell.cBGsPercentageInvolved.add(city.cBGVDCells.get(i).cBGsPercentageInvolved.get(j));
                        }
                    }
                    for (int j = 0; j < city.cBGVDCells.get(i).shopPlacesKeys.size(); j++) {
                        cBGVDCell.shopPlacesKeys.add(city.cBGVDCells.get(i).shopPlacesKeys.get(j));
                    }
                    for (int j = 0; j < city.cBGVDCells.get(i).schoolPlacesKeys.size(); j++) {
                        cBGVDCell.schoolPlacesKeys.add(city.cBGVDCells.get(i).schoolPlacesKeys.get(j));
                    }
                    scsd.cBGVDCells.add(cBGVDCell);
                }
            }

//            int layer=layersList.getSelectedIndex();
            if (!(mainFParent.allData.all_Layers.get(cBGLayerIndex) instanceof NumericLayer)) {
                RegionImageLayer cBGLayer = new RegionImageLayer();

                VectorToPolygon vp = new VectorToPolygon();
                int[][] indexedImage = vp.layerToIndexedImage(mainFParent.allData, cBGLayerIndex, true);

                cBGLayer.indexedImage = indexedImage;
                cBGLayer.cBGIndexs = vp.cBGlayerToIndexedIDImage(mainFParent.allData, cBGLayerIndex, indexedImage);
                cBGLayer.startLat = vp.scaleOffsetX;
                cBGLayer.startLon = vp.scaleOffsetY;
                cBGLayer.endLat = vp.scaleOffsetX + vp.scaleWidth;
                cBGLayer.endLon = vp.scaleOffsetY + vp.scaleHeight;

                int numCBGs = ((LayerDefinition) (mainFParent.allData.all_Layers.get(cBGLayerIndex))).categories.length - 1;

                cBGLayer.severities = new double[numCBGs];
                cBGLayer.imageBoundaries = RegionImageLayer.getImageBoundaries(indexedImage);
                city.cBGRegionLayer = cBGLayer;
            } else {
                System.out.println("ONLY CATEGORICAL LAYERS!");
            }

            if (!(mainFParent.allData.all_Layers.get(vDLayerIndex) instanceof NumericLayer)) {

                System.out.println("city.vDCells.size(): " + city.vDCells.size());
                RegionImageLayer vDLayer = new RegionImageLayer();

                VectorToPolygon vp = new VectorToPolygon();
                int[][] indexedImage = vp.layerToIndexedImage(mainFParent.allData, vDLayerIndex, true);

                vDLayer.indexedImage = indexedImage;
                vDLayer.startLat = vp.scaleOffsetX;
                vDLayer.startLon = vp.scaleOffsetY;
                vDLayer.endLat = vp.scaleOffsetX + vp.scaleWidth;
                vDLayer.endLon = vp.scaleOffsetY + vp.scaleHeight;

                vDLayer.severities = new double[city.vDCells.size()];
                vDLayer.imageBoundaries = RegionImageLayer.getImageBoundaries(indexedImage);
                city.vDRegionLayer = vDLayer;

                //                HashMap<Integer, SimplePolygons> polies = vp.imageToPolygons(indexedImage, mainFParent.allData, vDLayerIndex, false, true);
//
//                polies.forEach((key, val) -> {
////                    System.out.println(key);
////                    if(key==238){
////                        System.out.println("!!!!!!!!!!!");
////                    }
////                    if (city.vDCells.get(key-1).shopPlacesKeys.size() > 0 && city.vDCells.get(key-1).schoolPlacesKeys.size() > 0) {//THIS IS CAUSED BY A BUG POTENTIALLY! BUT REMOVED FOR NOW
//                        MyPolygons myPolygons = new MyPolygons();
//                        for (int y = 0; y < val.polygons.size(); y++) {
//                            MyPolygon myPolygon = new MyPolygon();
//                            for (int z = 0; z < val.polygons.get(y).points.size(); z++) {
//                                myPolygon.points.add(new Location(val.polygons.get(y).points.get(z).xM, val.polygons.get(y).points.get(z).yM));
//                            }
//                            myPolygons.polygons.add(myPolygon);
//                        }
//                        city.vDPolygons.put(key, myPolygons);
////                    }
//                });
//                for(int k=0;k<polies.size();k++){
//                    MyPolygons myPolygons=new MyPolygons();
//                    for(int y=0;y<polies.get(k).polygons.size();y++){
//                        MyPolygon myPolygon=new MyPolygon();
//                        for(int z=0;z<polies.get(k).polygons.get(y).points.size();z++){
//                            myPolygon.points.add(new Location(polies.get(k).polygons.get(y).points.get(z).xM,polies.get(k).polygons.get(y).points.get(z).yM));
//                        }
//                        myPolygons.polygons.add(myPolygon);
//                    }
//                    city.vDPolygons.put(k, myPolygons);
//                }
            } else {
                System.out.println("ONLY CATEGORICAL LAYERS!");
            }

            if (!(mainFParent.allData.all_Layers.get(cBGVDLayerIndex) instanceof NumericLayer)) {
                RegionImageLayer cBGVDLayer = new RegionImageLayer();

                VectorToPolygon vp = new VectorToPolygon();
                int[][] indexedImage = vp.layerToIndexedImage(mainFParent.allData, cBGVDLayerIndex, true);

                cBGVDLayer.indexedImage = indexedImage;
                cBGVDLayer.startLat = vp.scaleOffsetX;
                cBGVDLayer.startLon = vp.scaleOffsetY;
                cBGVDLayer.endLat = vp.scaleOffsetX + vp.scaleWidth;
                cBGVDLayer.endLon = vp.scaleOffsetY + vp.scaleHeight;

                cBGVDLayer.severities = new double[city.cBGVDCells.size()];
                cBGVDLayer.imageBoundaries = RegionImageLayer.getImageBoundaries(indexedImage);
                city.cBGVDRegionLayer = cBGVDLayer;

            } else {
                System.out.println("ONLY CATEGORICAL LAYERS!");
            }

            myParent.mainModel.allGISData.loadScopeCBGPolygons(city);

            scsd.cBGPolygons = city.cBGPolygons;
            scsd.vDPolygons = city.vDPolygons;

            scsd.cBGRegionImageLayer = city.cBGRegionLayer;
            scsd.vDRegionImageLayer = city.vDRegionLayer;
            scsd.cBGVDRegionImageLayer = city.cBGVDRegionLayer;

            myParent.mainModel.supplementaryCaseStudyData = scsd;

            String directoryPath = "./datasets/Safegraph/" + myParent.mainModel.ABM.studyScope;
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir();
            }

            MainModel.saveSupplementaryCaseStudyDataKryo(directoryPath + "/supplementaryGIS", scsd);

//            myParent.mainModel.loadAndConnectSupplementaryCaseStudyDataKryo("./datasets/Safegraph/" + myParent.mainModel.ABM.studyScope + "/supplementaryGIS.bin");//TESTING!
        } else {
            jLabel1.setText("HALT! ONLY CITY SCOPE IS IMPLEMENTED!");
        }
    }

    public void saveTessellations() {
        if (myParent.mainModel.ABM.studyScopeGeography instanceof City) {
            City city = (City) (myParent.mainModel.ABM.studyScopeGeography);
            SupplementaryCaseStudyData scsd = new SupplementaryCaseStudyData();
            scsd.tessellations = new ArrayList();
            int cBGLayerIndex = mainFParent.findLayerExactNotCaseSensitive("CBG");
            for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
                if (!((LayerDefinition) (mainFParent.allData.all_Layers.get(i))).layerName.toLowerCase().contains("Base".toLowerCase())) {
                    if (!((LayerDefinition) (mainFParent.allData.all_Layers.get(i))).layerName.toLowerCase().contains("traffic".toLowerCase())) {
                        if (!(mainFParent.allData.all_Layers.get(i) instanceof NumericLayer)) {
                            Tessellation tessellation = new Tessellation();
                            tessellation.scenarioName = ((LayerDefinition) (mainFParent.allData.all_Layers.get(i))).layerName;
                            for (int vdIndex = 1; vdIndex < ((LayerDefinition) (mainFParent.allData.all_Layers.get(i))).categories.length; vdIndex++) {
                                tessellation.cells.add(new TessellationCell());
                                tessellation.cells.get(vdIndex - 1).cBGsInvolved = new ArrayList();
                                tessellation.cells.get(vdIndex - 1).cBGsIDsInvolved = new ArrayList();
                                tessellation.cells.get(vdIndex - 1).cBGsPercentageInvolved = new ArrayList();
                                tessellation.cells.get(vdIndex - 1).myIndex = vdIndex;

                                HashMap<CensusBlockGroup, Integer> cBGNumNodesHashMap = null;

//                                if(((LayerDefinition)(mainFParent.allData.all_Layers.get(i))).layerName.equals("CBG")){
//                                    System.out.println("DEBUG123");
//                                }
                                cBGNumNodesHashMap = getHashNumNodeForCBG(mainFParent.allData, myParent.mainModel.allGISData, vdIndex, i, cBGLayerIndex);

                                double sumNodes = 0;
                                for (Map.Entry<CensusBlockGroup, Integer> set : cBGNumNodesHashMap.entrySet()) {
                                    sumNodes += set.getValue();
                                }
                                for (Map.Entry<CensusBlockGroup, Integer> set : cBGNumNodesHashMap.entrySet()) {
                                    tessellation.cells.get(vdIndex - 1).cBGsInvolved.add(set.getKey());
                                    if (set.getKey() != null) {
                                        tessellation.cells.get(vdIndex - 1).cBGsIDsInvolved.add(set.getKey().id);
                                    } else {
                                        tessellation.cells.get(vdIndex - 1).cBGsIDsInvolved.add(-1l);
                                    }
                                    tessellation.cells.get(vdIndex - 1).cBGsPercentageInvolved.add((double) (set.getValue()) / sumNodes);
                                }
//                            if (vdIndex >= 1) {
//                                String[] shopSchoolTemp = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories[vdIndex].split(" ");
//                                String[] shopSchool = shopSchoolTemp[1].split("_");
//                                int shopIndex = Integer.valueOf(shopSchool[0]);
//                                int schoolIndex = Integer.valueOf(shopSchool[1]);
////                    System.out.println(shopIndex);
////                    System.out.println(schoolIndex);
////                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
//                                if (shopIndex > 0 && schoolIndex > 0) {
//                                    city.vDCells.get(vdIndex - 1).shopPlaces = shopLocationNodes.get(shopIndex - 1).places;
//                                    city.vDCells.get(vdIndex - 1).shopPlacesKeys = shopLocationNodes.get(shopIndex - 1).placeKeys;
//
//                                    System.out.println("vdIndex: " + vdIndex);
//                                    System.out.println("city.vDCells.size(): " + city.vDCells.size());
//                                    System.out.println("schoolIndex: " + schoolIndex);
//                                    System.out.println("shopLocationNodes.size(): " + shopLocationNodes.size());
//                                    city.vDCells.get(vdIndex - 1).schoolPlaces = schoolLocationNodes.get(schoolIndex - 1).places;
//                                    city.vDCells.get(vdIndex - 1).schoolPlacesKeys = schoolLocationNodes.get(schoolIndex - 1).placeKeys;
//                                }
//                            }
                            }

                            RegionImageLayer vDLayer = new RegionImageLayer();

                            VectorToPolygon vp = new VectorToPolygon();
                            int[][] indexedImage = vp.layerToIndexedImage(mainFParent.allData, i, false);

                            vDLayer.indexedImage = indexedImage;
                            if (((LayerDefinition) (mainFParent.allData.all_Layers.get(i))).layerName.toLowerCase().equals("cbg")) {
                                vDLayer.cBGIndexs = vp.cBGlayerToIndexedIDImage(mainFParent.allData, cBGLayerIndex, indexedImage);
                            }
                            vDLayer.startLat = vp.scaleOffsetX;
                            vDLayer.startLon = vp.scaleOffsetY;
                            vDLayer.endLat = vp.scaleOffsetX + vp.scaleWidth;
                            vDLayer.endLon = vp.scaleOffsetY + vp.scaleHeight;

                            vDLayer.severities = new double[tessellation.cells.size()];
                            vDLayer.imageBoundaries = RegionImageLayer.getImageBoundaries(indexedImage);
                            tessellation.regionImageLayer = vDLayer;

                            scsd.tessellations.add(tessellation);
                        }
                    }
                }
            }

            myParent.mainModel.supplementaryCaseStudyData = scsd;

            String directoryPath = "./datasets/Safegraph/" + myParent.mainModel.ABM.studyScope;
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir();
            }

            MainModel.saveSupplementaryCaseStudyDataKryo(directoryPath + "/supplementaryGIS", scsd);
        }
    }

//    public String getScenarioName(String input){
//        if(input.contains(input))
//    }

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        myParent.mainModel.loadAndConnectSupplementaryCaseStudyDataKryo("./datasets/Safegraph/" + myParent.mainModel.ABM.studyScope + "/supplementaryGIS.bin");
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        if (myParent.mainModel.ABM.studyScopeGeography instanceof City) {
            City city = (City) (myParent.mainModel.ABM.studyScopeGeography);
            double[][] travels = new double[city.vDCells.size()][city.vDCells.size()];
            String[][] travelsStr = new String[city.vDCells.size()][city.vDCells.size()];
            for (int i = 0; i < city.vDCells.size(); i++) {
                for (int h = 0; h < city.vDCells.size(); h++) {
                    for (int j = 0; j < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); j++) {
                        for (int k = 0; k < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.size(); k++) {
                            CensusBlockGroup destinationCBG = myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).place.censusBlock;
                            double dstPercentage = getCBGPercentageInvolvedForVD(city, city.vDCells.get(i), destinationCBG);
                            travels[i][h] = travels[i][h] + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).raw_visit_counts * dstPercentage;
                            travelsStr[i][h] = String.valueOf(travels[i][h]);
                            if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).visitor_home_cbgs_place != null) {
                                for (int m = 0; m < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).visitor_home_cbgs_place.size(); m++) {
                                    CensusBlockGroup sourceCBG = myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).visitor_home_cbgs_place.get(m).key;

                                    double srcPercentage = getCBGPercentageInvolvedForVD(city, city.vDCells.get(h), sourceCBG);
                                    if (srcPercentage > 0) {
                                        travels[i][h] = travels[i][h] + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).visitor_home_cbgs_place.get(m).value * srcPercentage;
                                        travelsStr[i][h] = String.valueOf(travels[i][h]);
                                    }

                                }
                            }

                            destinationCBG = myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).place.censusBlock;
                            dstPercentage = getCBGPercentageInvolvedForVD(city, city.vDCells.get(h), destinationCBG);
                            travels[i][h] = travels[i][h] + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).raw_visit_counts * dstPercentage;
                            travelsStr[i][h] = String.valueOf(travels[i][h]);
                            if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).visitor_home_cbgs_place != null) {
                                for (int m = 0; m < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).visitor_home_cbgs_place.size(); m++) {
                                    CensusBlockGroup sourceCBG = myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).visitor_home_cbgs_place.get(m).key;

                                    double srcPercentage = getCBGPercentageInvolvedForVD(city, city.vDCells.get(i), sourceCBG);
                                    if (srcPercentage > 0) {
                                        travels[i][h] = travels[i][h] + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(j).patternRecords.get(k).visitor_home_cbgs_place.get(m).value * srcPercentage;
                                        travelsStr[i][h] = String.valueOf(travels[i][h]);
                                    }

                                }
                            }
                        }
                    }
                }
            }
            ArrayList<String[]> travelsData = new ArrayList();
            for (int i = 0; i < city.vDCells.size(); i++) {
                travelsData.add(travelsStr[i]);
            }
            CsvWriter writer = new CsvWriter();
            try {
                writer.write(new File(myParent.mainModel.ABM.studyScope + "_VDTravel.csv"), Charset.forName("US-ASCII"), travelsData);
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("ONLY CITY SCOPE IS IMPLEMENTED!");
        }
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        ArrayList<LocationNodeSafegraph> shopLocations = new ArrayList();
        ArrayList<LocationNodeSafegraph> schoolLocations = new ArrayList();
        String gISDataFileName = "./datasets/brent_buildings.csv";
        try {
//            CsvReader cSVReader = new CsvReader();
            CSVReader reader = new CSVReader(new FileReader(gISDataFileName));
//            cSVReader.setContainsHeader(false);
//            CsvContainer data = cSVReader.read(GISDataFile, StandardCharsets.UTF_8);
            List<String[]> data = reader.readAll();

            for (int j = 0; j < data.size(); j++) {
                if (/*data.get(j)[0].equals("shopping") ||*/data.get(j)[0].equals("supermarket")) {
                    float lon = Float.parseFloat(data.get(j)[1]);
                    float lat = Float.parseFloat(data.get(j)[2]);
                    LocationNode node = getNearestNode(mainFParent, lat, lon, null);
                    if (node != null) {
                        LocationNodeSafegraph tempLoc = new LocationNodeSafegraph();

                        tempLoc.node = node;
                        shopLocations.add(tempLoc);
                    }
                }
                if (data.get(j)[0].equals("school")) {
                    float lon = Float.parseFloat(data.get(j)[1]);
                    float lat = Float.parseFloat(data.get(j)[2]);
                    LocationNode node = getNearestNode(mainFParent, lat, lon, null);
                    if (node != null) {
                        LocationNodeSafegraph tempLoc = new LocationNodeSafegraph();

                        tempLoc.node = node;
                        schoolLocations.add(tempLoc);
                    }
                }
            }

            Integer indices[] = labelMergedFacilities(shopLocations, shopMergeThreshold);
            List<Integer> indicesRawList = Arrays.asList(indices);
            ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
            LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
            ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);

            System.out.println("Initial number of shops: " + shopLocations.size());
            ArrayList<LocationNodeSafegraph> shops = mergeFacilitiesWithIndices(shopLocations, indicesList, indicesUniqueList, null);
            shopLocationNodes = shops;
            System.out.println("Refined number of shops: " + shops.size());

            indices = labelMergedFacilities(schoolLocations, schoolMergeThreshold);
            indicesRawList = Arrays.asList(indices);
            indicesList = new ArrayList(indicesRawList);
            indicesUniqueSetHS = new LinkedHashSet(indicesList);
            indicesUniqueList = new ArrayList(indicesUniqueSetHS);

            System.out.println("Initial number of schools: " + schoolLocations.size());
            ArrayList<LocationNodeSafegraph> schools = mergeFacilitiesWithIndices(schoolLocations, indicesList, indicesUniqueList, null);
            schoolLocationNodes = schools;
            System.out.println("Refined number of schools: " + schools.size());

            int numShopFacilities = shopLocationNodes.size();
            shopFacilities = new FacilityLocation[numShopFacilities];
            Color colors[] = new Color[numShopFacilities];
            for (int i = 0; i < numShopFacilities; i++) {
                colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numShopFacilities - 1, 1, 1));

            }
            for (int i = 0; i < numShopFacilities; i++) {
                shopFacilities[i] = new FacilityLocation(mainFParent, shopLocationNodes.get(i).node, shopLocationNodes.get(i).node.myWays[0], 20d);
                shopFacilities[i].color = colors[i];
                shopFacilities[i].isDecoyable = true;
                shopFacilities[i].tollOff = 0.5;//IMP
            }

            int numSchoolFacilities = schoolLocationNodes.size();
            schoolFacilities = new FacilityLocation[numSchoolFacilities];
            colors = new Color[numSchoolFacilities];
            for (int i = 0; i < numSchoolFacilities; i++) {
                colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numSchoolFacilities - 1, 1, 1));

            }
            for (int i = 0; i < numSchoolFacilities; i++) {
                schoolFacilities[i] = new FacilityLocation(mainFParent, schoolLocationNodes.get(i).node, schoolLocationNodes.get(i).node.myWays[0], 20d);
                schoolFacilities[i].color = colors[i];
                schoolFacilities[i].isDecoyable = true;
                schoolFacilities[i].tollOff = 0.5;//IMP
            }
            System.out.println("Finished reading buildings");
        } catch (IOException ex) {
            Logger.getLogger(COVIDGeoVisualization.class
                    .getName()).log(Level.SEVERE, (String) null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(GISLocationDialog.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        ArrayList<LocationNodeSafegraph> shopLocations = new ArrayList();
        ArrayList<LocationNodeSafegraph> schoolLocations = new ArrayList();
        String gISDataFileName = "./datasets/brent_buildings.csv";
        try {
//            CsvReader cSVReader = new CsvReader();
            CSVReader reader = new CSVReader(new FileReader(gISDataFileName));
//            cSVReader.setContainsHeader(false);
//            CsvContainer data = cSVReader.read(GISDataFile, StandardCharsets.UTF_8);
            List<String[]> data = reader.readAll();

            for (int j = 0; j < data.size(); j++) {
                if (/*data.get(j)[0].equals("shopping") ||*/data.get(j)[0].equals("supermarket")) {
                    float lon = Float.parseFloat(data.get(j)[1]);
                    float lat = Float.parseFloat(data.get(j)[2]);
                    LocationNode node = getNearestNode(mainFParent, lat, lon, null);
                    if (node != null) {
                        LocationNodeSafegraph tempLoc = new LocationNodeSafegraph();

                        tempLoc.node = node;
                        shopLocations.add(tempLoc);
                    }
                }
                if (data.get(j)[0].equals("school")) {
                    float lon = Float.parseFloat(data.get(j)[1]);
                    float lat = Float.parseFloat(data.get(j)[2]);
                    LocationNode node = getNearestNode(mainFParent, lat, lon, null);
                    if (node != null) {
                        LocationNodeSafegraph tempLoc = new LocationNodeSafegraph();

                        tempLoc.node = node;
                        schoolLocations.add(tempLoc);
                    }
                }
            }

            Integer indices[] = labelMergedFacilities(shopLocations, shopMergeThreshold);
            List<Integer> indicesShopRawList = Arrays.asList(indices);
            ArrayList<Integer> indicesShopList = new ArrayList(indicesShopRawList);
            LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesShopList);
            ArrayList<Integer> indicesShopUniqueList = new ArrayList(indicesUniqueSetHS);

            //System.out.println("Initial number of shops: " + shopLocations.size());
            ArrayList<LocationNodeSafegraph> shops = mergeFacilitiesWithIndices(shopLocations, indicesShopList, indicesShopUniqueList, null);
            //shopLocationNodes = shops;
            //System.out.println("Refined number of shops: " + shops.size());

            indices = labelMergedFacilities(schoolLocations, schoolMergeThreshold);
            List<Integer> indicesSchoolRawList = Arrays.asList(indices);
            ArrayList<Integer> indicesSchoolList = new ArrayList(indicesSchoolRawList);
            indicesUniqueSetHS = new LinkedHashSet(indicesSchoolList);
            ArrayList<Integer> indicesSchoolUniqueList = new ArrayList(indicesUniqueSetHS);

            //System.out.println("Initial number of schools: " + schoolLocations.size());
            ArrayList<LocationNodeSafegraph> schools = mergeFacilitiesWithIndices(schoolLocations, indicesSchoolList, indicesSchoolUniqueList, null);
            //schoolLocationNodes = schools;
            //System.out.println("Refined number of schools: " + schools.size());

            String revisedGISDataFileName = "./datasets/brent_buildings.csv";
            List<String[]> revisedData;
//            CsvReader cSVReader = new CsvReader();
            CSVReader secondReader = new CSVReader(new FileReader(revisedGISDataFileName));
//            cSVReader.setContainsHeader(false);
//            CsvContainer data = cSVReader.read(GISDataFile, StandardCharsets.UTF_8);
            revisedData = secondReader.readAll();

            //int shopLayer = mainFParent.findLayer("shops_v");
            //int schoolLayer = mainFParent.findLayer("schools_v");
            int trafficLayer = mainFParent.findLayerContains("traffic");

            int numProcessors = myParent.mainModel.numCPUs;
            mainFParent.allData.setParallelLayers(numProcessors, -1);

            for (int i = 0; i < revisedData.size(); i++) {
                System.out.println(i);
                if (revisedData.get(i)[0].equals("house")) {
                    float lon = Float.parseFloat(data.get(i)[1]);
                    float lat = Float.parseFloat(data.get(i)[2]);
                    LocationNode node = getNearestNode(mainFParent, lat, lon, null);
                    //short shopIndex = (short) (((short[]) node.layers.get(shopLayer))[0] - 1);
                    //if (shopIndex == 0) {
                    //    System.out.println("ERROR: THE HOUSE HAS NO SHOP!");
                    //}

                    ParallelRoutingBrent parallelRoutingBrent[] = new ParallelRoutingBrent[numProcessors];

                    for (int f = 0; f < numProcessors - 1; f++) {
                        parallelRoutingBrent[f] = new ParallelRoutingBrent(f, this, null, (int) Math.floor(f * ((shops.size()) / numProcessors)), (int) Math.floor((f + 1) * ((shops.size()) / numProcessors)), trafficLayer, node, shops);
                    }
                    parallelRoutingBrent[numProcessors - 1] = new ParallelRoutingBrent(numProcessors - 1, this, null, (int) Math.floor((numProcessors - 1) * ((shops.size()) / numProcessors)), shops.size(), trafficLayer, node, shops);

                    for (int f = 0; f < numProcessors; f++) {
                        parallelRoutingBrent[f].myThread.start();
                    }
                    for (int f = 0; f < numProcessors; f++) {
                        try {
                            parallelRoutingBrent[f].myThread.join();
                        } catch (InterruptedException ie) {
                            System.out.println(ie.toString());
                        }
                    }

                    double firstShopDist = Double.POSITIVE_INFINITY;
                    double secondShopDist = Double.POSITIVE_INFINITY;
                    double thirdShopDist = Double.POSITIVE_INFINITY;
                    int firstShopGroupIndex = -1;
                    int secondShopGroupIndex = -1;
                    int thirdShopGroupIndex = -1;

                    for (int g = 0; g < numProcessors; g++) {
                        if (parallelRoutingBrent[g].firstDist < firstShopDist) {
                            firstShopDist = parallelRoutingBrent[g].firstDist;
                            firstShopGroupIndex = parallelRoutingBrent[g].firstGroupIndex;
                        }
                        if (parallelRoutingBrent[g].secondDist < secondShopDist) {
                            secondShopDist = parallelRoutingBrent[g].secondDist;
                            secondShopGroupIndex = parallelRoutingBrent[g].secondGroupIndex;
                        }
                        if (parallelRoutingBrent[g].thirdDist < thirdShopDist) {
                            thirdShopDist = parallelRoutingBrent[g].thirdDist;
                            thirdShopGroupIndex = parallelRoutingBrent[g].thirdGroupIndex;
                        }
                    }

                    String[] row = revisedData.get(i);
                    String[] augmentedRow = new String[10];
                    for (int j = 0; j < row.length; j++) {
                        augmentedRow[j] = row[j];
                    }

                    String fistOrderGroup = "";
                    String secondOrderGroup = "";
                    String thirdOrderGroup = "";
                    for (int k = 0; k < indicesShopRawList.size(); k++) {
                        if (firstShopGroupIndex != -1) {
                            if (indicesShopRawList.get(k) == indicesShopUniqueList.get(firstShopGroupIndex)) {
                                fistOrderGroup = fistOrderGroup + k + "_";
                            }
                        }
                        if (secondShopGroupIndex != -1) {
                            if (indicesShopRawList.get(k) == indicesShopUniqueList.get(secondShopGroupIndex)) {
                                secondOrderGroup = secondOrderGroup + k + "_";
                            }
                        }
                        if (thirdShopGroupIndex != -1) {
                            if (indicesShopRawList.get(k) == indicesShopUniqueList.get(thirdShopGroupIndex)) {
                                thirdOrderGroup = thirdOrderGroup + k + "_";
                            }
                        }
                    }

                    augmentedRow[4] = fistOrderGroup;
                    augmentedRow[5] = secondOrderGroup;
                    augmentedRow[6] = thirdOrderGroup;
                    //((LayerDefinition)(mainFParent.allData.all_Layers.get(shopLayer))).

                    //short schoolIndex = (short) (((short[]) node.layers.get(schoolLayer))[0] - 1);
                    //if (schoolIndex == 0) {
                    //    System.out.println("ERROR: THE HOUSE HAS NO SCHOOL!");
                    //}
                    parallelRoutingBrent = new ParallelRoutingBrent[numProcessors];

                    for (int f = 0; f < numProcessors - 1; f++) {
                        parallelRoutingBrent[f] = new ParallelRoutingBrent(f, this, null, (int) Math.floor(f * ((schools.size()) / numProcessors)), (int) Math.floor((f + 1) * ((schools.size()) / numProcessors)), trafficLayer, node, schools);
                    }
                    parallelRoutingBrent[numProcessors - 1] = new ParallelRoutingBrent(numProcessors - 1, this, null, (int) Math.floor((numProcessors - 1) * ((schools.size()) / numProcessors)), schools.size(), trafficLayer, node, schools);

                    for (int f = 0; f < numProcessors; f++) {
                        parallelRoutingBrent[f].myThread.start();
                    }
                    for (int f = 0; f < numProcessors; f++) {
                        try {
                            parallelRoutingBrent[f].myThread.join();
                        } catch (InterruptedException ie) {
                            System.out.println(ie.toString());
                        }
                    }

                    double firstSchoolDist = Double.POSITIVE_INFINITY;
                    double secondSchoolDist = Double.POSITIVE_INFINITY;
                    double thirdSchoolDist = Double.POSITIVE_INFINITY;
                    int firstSchoolGroupIndex = -1;
                    int secondSchoolGroupIndex = -1;
                    int thirdSchoolGroupIndex = -1;

                    for (int g = 0; g < numProcessors; g++) {
                        if (parallelRoutingBrent[g].firstDist < firstSchoolDist) {
                            firstSchoolDist = parallelRoutingBrent[g].firstDist;
                            firstSchoolGroupIndex = parallelRoutingBrent[g].firstGroupIndex;
                        }
                        if (parallelRoutingBrent[g].secondDist < secondSchoolDist) {
                            secondSchoolDist = parallelRoutingBrent[g].secondDist;
                            secondSchoolGroupIndex = parallelRoutingBrent[g].secondGroupIndex;
                        }
                        if (parallelRoutingBrent[g].thirdDist < thirdSchoolDist) {
                            thirdSchoolDist = parallelRoutingBrent[g].thirdDist;
                            thirdSchoolGroupIndex = parallelRoutingBrent[g].thirdGroupIndex;
                        }
                    }

                    fistOrderGroup = "";
                    secondOrderGroup = "";
                    thirdOrderGroup = "";
                    for (int k = 0; k < indicesSchoolRawList.size(); k++) {
                        if (firstShopGroupIndex != -1) {
                            if (indicesSchoolRawList.get(k) == indicesSchoolUniqueList.get(firstSchoolGroupIndex)) {
                                fistOrderGroup = fistOrderGroup + k + "_";
                            }
                        }
                        if (secondSchoolGroupIndex != -1) {
                            if (indicesSchoolRawList.get(k) == indicesSchoolUniqueList.get(secondSchoolGroupIndex)) {
                                secondOrderGroup = secondOrderGroup + k + "_";
                            }
                        }
                        if (thirdSchoolGroupIndex != -1) {
                            if (indicesSchoolRawList.get(k) == indicesSchoolUniqueList.get(thirdSchoolGroupIndex)) {
                                thirdOrderGroup = thirdOrderGroup + k + "_";
                            }
                        }
                    }

                    augmentedRow[7] = fistOrderGroup;
                    augmentedRow[8] = secondOrderGroup;
                    augmentedRow[9] = thirdOrderGroup;

                    revisedData.set(i, augmentedRow);
                }
            }

            CsvWriter writer = new CsvWriter();
            writer.write(new File("./datasets/brent_buildings_VD.csv"), StandardCharsets.UTF_8, revisedData);
        } catch (IOException ex) {
            Logger.getLogger(COVIDGeoVisualization.class
                    .getName()).log(Level.SEVERE, (String) null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(GISLocationDialog.class
                    .getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        float currentShopMergeThreshold = shopMergeThreshold;
        float currentSchoolMergeThreshold = schoolMergeThreshold;
        int targetNumCells = (int) (jSpinner1.getValue());
        float stepSize = 0.2f;
        int maxIterations = 20;

        for (int iter = 0; iter < maxIterations; iter++) {
            shopFacilities = initShopFacilities(currentShopMergeThreshold);
            schoolFacilities = initSchoolFacilities(currentSchoolMergeThreshold, null);
            makeVDs(shopFacilities, "shops_v_VDFNC_" + targetNumCells);
            makeVDs(schoolFacilities, "schools_v_VDFNC_" + targetNumCells);

            makeVDCombination("shops_v_VDFNC_" + targetNumCells, "schools_v_VDFNC_" + targetNumCells, "VDFNC_" + targetNumCells);
            int generatedLayer = mainFParent.findLayerExactNotCaseSensitive("VDFNC_" + targetNumCells);
            int currentNumCells = ((LayerDefinition) (mainFParent.allData.all_Layers.get(generatedLayer))).categories.length - 1;
            System.out.println("shopFacilities.length: " + shopFacilities.length);
            System.out.println("schoolFacilities.length: " + schoolFacilities.length);
            System.out.println("currentNumCells: " + currentNumCells);
            System.out.println("currentShopMergeThreshold: " + currentShopMergeThreshold);
            System.out.println("currentSchoolMergeThreshold: " + currentSchoolMergeThreshold);

            ((LayerDefinition) mainFParent.allData.all_Layers.get(generatedLayer)).layerName = "VDFNC_" + currentNumCells;

            int shop_ind = mainFParent.findLayerExactNotCaseSensitive("shops_v_VDFNC_" + targetNumCells);
            ((LayerDefinition) mainFParent.allData.all_Layers.get(shop_ind)).layerName = "shops_v_VDFNC_" + currentNumCells;

            int school_ind = mainFParent.findLayerExactNotCaseSensitive("schools_v_VDFNC_" + targetNumCells);
            ((LayerDefinition) mainFParent.allData.all_Layers.get(school_ind)).layerName = "schools_v_VDFNC_" + currentNumCells;

            if (currentNumCells < targetNumCells) {
                currentShopMergeThreshold = currentShopMergeThreshold * (1 - stepSize);
                currentSchoolMergeThreshold = currentSchoolMergeThreshold * (1 - stepSize);
                stepSize = stepSize * 0.95f;

            } else if (currentNumCells > targetNumCells) {
                currentShopMergeThreshold = currentShopMergeThreshold * (1 + stepSize);
                currentSchoolMergeThreshold = currentSchoolMergeThreshold * (1 + stepSize);
                stepSize = stepSize * 0.95f;
            } else {
                break;
            }
//            if (iter < maxIterations - 1) {
//                deleteLayer("shops_v_VDFNC_"+ targetNumCells);
//                deleteLayer("schools_v_VDFNC_"+ targetNumCells);
//                deleteLayer("VDFNC_"+ targetNumCells);
//            }
            StoreProcessedData saving = new StoreProcessedData();
            saving.save_allData_kryo("C:" + File.separator + "Users" + File.separator + "user" + File.separator + "Documents" + File.separator + "Seattle_temp_" + iter, mainFParent.allData);
        }

    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        temp_VDFNC_100();
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        temp_VDFNC_70();
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        ArrayList<LocationNodeSafegraph> pOILocationNodes = initAllLocations();

        int cbgLayerIndex = RootArtificial.findLayerExactNotCaseSensitive(mainFParent.allData, "cbg");
        LayerDefinition cbgLayer = (LayerDefinition) (mainFParent.allData.all_Layers.get(cbgLayerIndex));
        int targetNumCells = cbgLayer.categories.length - 1;

        Collections.sort(pOILocationNodes, Collections.reverseOrder());

        int maxTry = 10;
        int cellRange = 3;
        int counter = 0;
        ArrayList<Integer> observedFacilities;
        FacilityLocation pOIFacilities[] = new FacilityLocation[1];
        for (int m = 0; m < maxTry; m++) {
            int numFacilities = targetNumCells + counter;
            pOIFacilities = new FacilityLocation[numFacilities];
            Color colors[] = new Color[numFacilities];
            for (int i = 0; i < numFacilities; i++) {
                colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numFacilities - 1, 1, 1));
            }
            for (int i = 0; i < numFacilities; i++) {
                pOIFacilities[i] = new FacilityLocation(mainFParent, pOILocationNodes.get(i).node, pOILocationNodes.get(i).node.myWays[0], 20d);
                pOIFacilities[i].color = colors[i];
                pOIFacilities[i].isDecoyable = true;
                pOIFacilities[i].tollOff = 0.5;//IMP
            }
            System.out.println("POIs generated: " + pOIFacilities.length);

            observedFacilities = new ArrayList();
            mainFParent.flowControl.tollOff = 5;
//        System.out.println("VD CONSTRUCTION START!");
            mainFParent.flowControl.simulateOneLayerCompetingFacilityBased(pOIFacilities, mainFParent.findLayerContains("traffic"), myParent.numProcessors, -1, false);
//        System.out.println("SIMULATION DONE!");
            mainFParent.flowControl.correctFacilityLava(mainFParent.findLayerContains("traffic"), myParent.numProcessors);
//        System.out.println("SIMULATION CORRECTION DONE!");

            for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
                short[] val = new short[1];
                if (mainFParent.allData.all_Nodes[i].isBurned == true) {
                    for (int k = 0; k < mainFParent.allData.all_Nodes[i].burntBy.length; k++) {
                        for (int j = 0; j < pOIFacilities.length; j++) {
                            if (mainFParent.allData.all_Nodes[i].burntBy[k] == pOIFacilities[j]) {
                                observedFacilities.add(j);
                            }
                        }
                    }
                }
            }
            LinkedHashSet<Integer> uniqueObservedFacilities = new LinkedHashSet(observedFacilities);
            if (uniqueObservedFacilities.size() > targetNumCells - cellRange && uniqueObservedFacilities.size() < targetNumCells + cellRange) {
                System.out.println("FOUND ALMOST SAME NUMBER OF CELLS");
                break;
            }
        }

        observedFacilities = new ArrayList();
        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            short[] val = new short[1];
            if (mainFParent.allData.all_Nodes[i].isBurned == true) {
                for (int k = 0; k < mainFParent.allData.all_Nodes[i].burntBy.length; k++) {
                    for (int j = 0; j < pOIFacilities.length; j++) {
                        if (mainFParent.allData.all_Nodes[i].burntBy[k] == pOIFacilities[j]) {
                            observedFacilities.add(j);
                            val[0] = (short) (j + 1 + 1);
                        }
                    }
                }
                mainFParent.allData.all_Nodes[i].layers.add(val);
            } else {
                val[0] = 1;
                mainFParent.allData.all_Nodes[i].layers.add(val);
            }
        }

        LinkedHashSet<Integer> uniqueObservedFacilities = new LinkedHashSet(observedFacilities);

        String outputLayerName = "VD_CBG_num_cells";

        LayerDefinition tempLayer = new LayerDefinition("category", outputLayerName);
        int numShops = uniqueObservedFacilities.size();
        tempLayer.categories = new String[numShops + 1];
        tempLayer.colors = new Color[numShops + 1];
        tempLayer.values = new double[numShops + 1];

        tempLayer.categories[0] = "NOT ASSIGNED";
        tempLayer.colors[0] = new Color(2, 2, 2);
        tempLayer.values[0] = Double.valueOf(0);
        for (int i = 1; i < numShops + 1; i++) {
            tempLayer.categories[i] = outputLayerName + " " + String.valueOf(i);
            tempLayer.colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numShops + 1 - 1, 1, 1));
            tempLayer.values[i] = Double.valueOf(i);
        }

        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            if (((short[]) mainFParent.allData.all_Nodes[i].layers.get(mainFParent.allData.all_Nodes[i].layers.size() - 1))[0] < 1) {
                ((short[]) mainFParent.allData.all_Nodes[i].layers.get(mainFParent.allData.all_Nodes[i].layers.size() - 1))[0] = 1;
            }
        }

        mainFParent.allData.all_Layers.add(tempLayer);
        mainFParent.refreshLayersList();
    }//GEN-LAST:event_jButton32ActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        ArrayList<LocationNodeSafegraph> pOILocationNodes = initAllLocations();

        int cbgvdLayerIndex = RootArtificial.findLayerExactNotCaseSensitive(mainFParent.allData, "cbgvdfmth");
        LayerDefinition cbgLayer = (LayerDefinition) (mainFParent.allData.all_Layers.get(cbgvdLayerIndex));
        int targetNumCells = cbgLayer.categories.length - 1;

        Collections.sort(pOILocationNodes, Collections.reverseOrder());

        int maxTry = 10;
        int cellRange = 3;
        int counter = 0;
        ArrayList<Integer> observedFacilities;
        FacilityLocation pOIFacilities[] = new FacilityLocation[1];
        for (int m = 0; m < maxTry; m++) {
            int numFacilities = targetNumCells + counter;
            pOIFacilities = new FacilityLocation[numFacilities];
            Color colors[] = new Color[numFacilities];
            for (int i = 0; i < numFacilities; i++) {
                colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numFacilities - 1, 1, 1));
            }
            for (int i = 0; i < numFacilities; i++) {
                pOIFacilities[i] = new FacilityLocation(mainFParent, pOILocationNodes.get(i).node, pOILocationNodes.get(i).node.myWays[0], 20d);
                pOIFacilities[i].color = colors[i];
                pOIFacilities[i].isDecoyable = true;
                pOIFacilities[i].tollOff = 0.5;//IMP
            }
            System.out.println("POIs generated: " + pOIFacilities.length);

            observedFacilities = new ArrayList();
            mainFParent.flowControl.tollOff = 5;
//        System.out.println("VD CONSTRUCTION START!");
            mainFParent.flowControl.simulateOneLayerCompetingFacilityBased(pOIFacilities, mainFParent.findLayerContains("traffic"), myParent.numProcessors, -1, false);
//        System.out.println("SIMULATION DONE!");
            mainFParent.flowControl.correctFacilityLava(mainFParent.findLayerContains("traffic"), myParent.numProcessors);
//        System.out.println("SIMULATION CORRECTION DONE!");

            for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
                short[] val = new short[1];
                if (mainFParent.allData.all_Nodes[i].isBurned == true) {
                    for (int k = 0; k < mainFParent.allData.all_Nodes[i].burntBy.length; k++) {
                        for (int j = 0; j < pOIFacilities.length; j++) {
                            if (mainFParent.allData.all_Nodes[i].burntBy[k] == pOIFacilities[j]) {
                                observedFacilities.add(j);
                            }
                        }
                    }
                }
            }
            LinkedHashSet<Integer> uniqueObservedFacilities = new LinkedHashSet(observedFacilities);
            if (uniqueObservedFacilities.size() > targetNumCells - cellRange && uniqueObservedFacilities.size() < targetNumCells + cellRange) {
                System.out.println("FOUND ALMOST SAME NUMBER OF CELLS");
                break;
            }
        }

        observedFacilities = new ArrayList();
        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            short[] val = new short[1];
            if (mainFParent.allData.all_Nodes[i].isBurned == true) {
                for (int k = 0; k < mainFParent.allData.all_Nodes[i].burntBy.length; k++) {
                    for (int j = 0; j < pOIFacilities.length; j++) {
                        if (mainFParent.allData.all_Nodes[i].burntBy[k] == pOIFacilities[j]) {
                            observedFacilities.add(j);
                            val[0] = (short) (j + 1 + 1);
                        }
                    }
                }
                mainFParent.allData.all_Nodes[i].layers.add(val);
            } else {
                val[0] = 1;
                mainFParent.allData.all_Nodes[i].layers.add(val);
            }
        }

        LinkedHashSet<Integer> uniqueObservedFacilities = new LinkedHashSet(observedFacilities);

        String outputLayerName = "VD_CBGVD_num_cells";

        LayerDefinition tempLayer = new LayerDefinition("category", outputLayerName);
        int numShops = uniqueObservedFacilities.size();
        tempLayer.categories = new String[numShops + 1];
        tempLayer.colors = new Color[numShops + 1];
        tempLayer.values = new double[numShops + 1];

        tempLayer.categories[0] = "NOT ASSIGNED";
        tempLayer.colors[0] = new Color(2, 2, 2);
        tempLayer.values[0] = Double.valueOf(0);
        for (int i = 1; i < numShops + 1; i++) {
            tempLayer.categories[i] = outputLayerName + " " + String.valueOf(i);
            tempLayer.colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numShops + 1 - 1, 1, 1));
            tempLayer.values[i] = Double.valueOf(i);
        }

        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            if (((short[]) mainFParent.allData.all_Nodes[i].layers.get(mainFParent.allData.all_Nodes[i].layers.size() - 1))[0] < 1) {
                ((short[]) mainFParent.allData.all_Nodes[i].layers.get(mainFParent.allData.all_Nodes[i].layers.size() - 1))[0] = 1;
            }
        }

        mainFParent.allData.all_Layers.add(tempLayer);
        mainFParent.refreshLayersList();
    }//GEN-LAST:event_jButton33ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        String years[] = new String[2];
        years[0] = "2020";
        years[1] = "2021";
        String months[][] = new String[2][12];
//        months[0][0] = "09";
//        months[0][1] = "10";
//        months[0][2] = "11";

//        months[0][0] = "03";
//        months[0][1] = "04";
        months[0][0] = "05";
        months[0][1] = "06";
        months[0][2] = "07";
        months[0][3] = "08";

        months[0][4] = "09";
        months[0][5] = "10";
        months[0][6] = "11";
        months[0][7] = "12";
        months[1][0] = "01";
        months[1][1] = "02";
        months[1][2] = "03";
        months[1][3] = "04";
        months[1][4] = "05";
        months[1][5] = "06";
        months[1][6] = "07";
        myParent.mainModel.safegraph.requestDatasetRange(myParent.mainModel.datasetDirectory, myParent.mainModel.allGISData, myParent.mainModel.ABM.studyScope, years, months, true, myParent.numProcessors);

        ArrayList<LocationNode> bannedNodes = new ArrayList();
        ArrayList<Long> cursedTucsonNodes = new ArrayList();
        cursedTucsonNodes.add(6948564736l);
        cursedTucsonNodes.add(6881882718l);
        cursedTucsonNodes.add(6881882717l);
        cursedTucsonNodes.add(6881882719l);
        cursedTucsonNodes.add(6881882716l);
        cursedTucsonNodes.add(6881882720l);
        cursedTucsonNodes.add(6881882721l);
        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            for (int j = 0; j < cursedTucsonNodes.size(); j++) {
                if (mainFParent.allData.all_Nodes[i].id == cursedTucsonNodes.get(j)) {
                    bannedNodes.add(mainFParent.allData.all_Nodes[i]);
                }
            }
        }

        shopFacilities = initShopsByExactNumber((int) jSpinner2.getValue());
        schoolFacilities = initSchoolsByExactNumber((int) jSpinner3.getValue(), bannedNodes);

        int trafficLayerIndex = -1;
        for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
            if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                trafficLayerIndex = i;
            }
        }
//        int numProcessors = myParent.mainModel.numCPUs;
        ArrayList<Boolean> subSample = new ArrayList();
        ArrayList<Integer> subsampleNodeIndices = new ArrayList();
        Random rnd = new Random(1);
        sampleRate = Float.parseFloat(jFormattedTextField1.getText());
        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            if (rnd.nextDouble() < sampleRate) {
                if (!(i == 293800 || i == 357731 || i == 320050 || i == 59047)) {
                    subSample.add(true);
                    subsampleNodeIndices.add(i);
                } else {
                    subSample.add(false);
                }
            } else {
                subSample.add(false);
            }
        }
        ArrayList<ArrayList<Double>> distancesToShops = new ArrayList();
        for (int s = 0; s < shopFacilities.length; s++) {
            ArrayList nodesDistas = new ArrayList();
            for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
                if (subSample.get(i) == true) {
                    nodesDistas.add(0);
                }
            }
            distancesToShops.add(nodesDistas);
        }
        ArrayList<ArrayList<Double>> distancesToSchools = new ArrayList();
        for (int s = 0; s < schoolFacilities.length; s++) {
            ArrayList nodesDistas = new ArrayList();
            for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
                if (subSample.get(i) == true) {
                    nodesDistas.add(0);
                }
            }
            distancesToSchools.add(nodesDistas);
        }

        int numProcessors = myParent.mainModel.numCPUs;

        routingThreadPool = Executors.newFixedThreadPool(numProcessors);

        for (int s = 0; s < shopFacilities.length; s++) {
            mainFParent.allData.setParallelLayers(numProcessors, -1);
            AdvancedParallelRouting parallelRouting[] = new AdvancedParallelRouting[numProcessors];

            for (int i = 0; i < numProcessors - 1; i++) {
                parallelRouting[i] = new AdvancedParallelRouting(mainFParent, distancesToShops, (int) Math.floor(i * ((subsampleNodeIndices.size()) / numProcessors)), (int) Math.floor((i + 1) * ((subsampleNodeIndices.size()) / numProcessors)), "shop", s, shopFacilities, subsampleNodeIndices, trafficLayerIndex, i);
            }
            parallelRouting[numProcessors - 1] = new AdvancedParallelRouting(mainFParent, distancesToShops, (int) Math.floor((numProcessors - 1) * ((subsampleNodeIndices.size()) / numProcessors)), subsampleNodeIndices.size(), "shop", s, shopFacilities, subsampleNodeIndices, trafficLayerIndex, numProcessors - 1);
            ArrayList<Callable<Object>> calls = new ArrayList<Callable<Object>>();
            for (int i = 0; i < numProcessors; i++) {
                parallelRouting[i].addRunnableToQueue(calls);
            }
            try {
                List<Future<Object>> futures = routingThreadPool.invokeAll(calls);
                for (int i = 0; i < futures.size(); i++) {
                    futures.get(i).get();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int s = 0; s < schoolFacilities.length; s++) {
            mainFParent.allData.setParallelLayers(numProcessors, -1);
            AdvancedParallelRouting parallelRouting[] = new AdvancedParallelRouting[numProcessors];
            for (int i = 0; i < numProcessors - 1; i++) {
                parallelRouting[i] = new AdvancedParallelRouting(mainFParent, distancesToSchools, (int) Math.floor(i * ((subsampleNodeIndices.size()) / numProcessors)), (int) Math.floor((i + 1) * ((subsampleNodeIndices.size()) / numProcessors)), "school", s, schoolFacilities, subsampleNodeIndices, trafficLayerIndex, i);
            }
            parallelRouting[numProcessors - 1] = new AdvancedParallelRouting(mainFParent, distancesToSchools, (int) Math.floor((numProcessors - 1) * ((subsampleNodeIndices.size()) / numProcessors)), subsampleNodeIndices.size(), "school", s, schoolFacilities, subsampleNodeIndices, trafficLayerIndex, numProcessors - 1);
            ArrayList<Callable<Object>> calls = new ArrayList<Callable<Object>>();
            for (int i = 0; i < numProcessors; i++) {
                parallelRouting[i].addRunnableToQueue(calls);
            }
            try {
                List<Future<Object>> futures = routingThreadPool.invokeAll(calls);
                for (int i = 0; i < futures.size(); i++) {
                    futures.get(i).get();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//        for (int s = 0; s < shopFacilities.length; s++) {
//            int counter=0;
//            for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
//                if (subSample.get(i) == true) {
//                    mainFParent.allData.setParallelLayers(1, -1);
//                    Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, 0);
//                    routingToOthers.findPath(mainFParent.allData.all_Nodes[i], shopFacilities[s].nodeLocation);
//                    double distance = routingToOthers.pathDistance;
//                    distancesToShops.get(s).set(counter, distance);
//                    counter=counter+1;
//                    System.out.println("shop: " + s + " Total shops: " + shopFacilities.length + " Node: " + i + " Total nodes: " + mainFParent.allData.all_Nodes.length);
//                }
//            }
//        }
//        for (int s = 0; s < schoolFacilities.length; s++) {
//            int counter = 0;
//            for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
//                if (subSample.get(i) == true) {
//                    mainFParent.allData.setParallelLayers(1, -1);
//                    Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, 0);
//                    routingToOthers.findPath(mainFParent.allData.all_Nodes[i], schoolFacilities[s].nodeLocation);
//                    double distance = routingToOthers.pathDistance;
//                    distancesToSchools.get(s).set(counter, distance);
//                    counter = counter + 1;
//                    System.out.println("school: " + s + " Total schools: " + schoolFacilities.length + " Node: " + i + " Total nodes: " + mainFParent.allData.all_Nodes.length);
//                }
//            }
//        }
        GISLocationDialog.writeDoubleArrayList(distancesToShops, "NetworkDistances" + File.separator + "shopsNodesDistances.csv");
        GISLocationDialog.writeDoubleArrayList(distancesToSchools, "NetworkDistances" + File.separator + "schoolsNodesDistances.csv");
    }//GEN-LAST:event_jButton34ActionPerformed

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
//        ArrayList<ArrayList<Byte>> connections=new ArrayList();
//        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
//            ArrayList<Byte> row=new ArrayList();
//            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
//                row.add((byte)0);
//            }
//            connections.add(row);
//        }
//        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
//            for (int j = 0; j < mainFParent.allData.all_Nodes.length; j++) {
//                for (int k = 0; k < mainFParent.allData.all_Ways.length; k++) {
//                    for (int m = 0; m < mainFParent.allData.all_Ways[k].myNodes.length; m++) {
//                        if(mainFParent.allData.all_Ways[k].myNodes[m].id==mainFParent.allData.all_Nodes[i].id){
//                            if(m>0){
//                                if(mainFParent.allData.all_Ways[k].myNodes[m-1].id==mainFParent.allData.all_Nodes[j].id){
//                                    connections.get(i).set(j, (byte)1);
//                                }
//                            }else if(m<mainFParent.allData.all_Ways[k].myNodes.length-1){
//                                if(mainFParent.allData.all_Ways[k].myNodes[m+1].id==mainFParent.allData.all_Nodes[j].id){
//                                    connections.get(i).set(j, (byte)1);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        GISLocationDialog.writeByteArrayList(connections,"NetworkDistances"+File.separator+"nodeConnections.csv");

        ArrayList<Boolean> subSample = new ArrayList();
        Random rnd = new Random(1);
        sampleRate = Float.parseFloat(jFormattedTextField1.getText());
        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            if (rnd.nextDouble() < sampleRate) {
                if (!(i == 293800 || i == 357731 || i == 320050 || i == 59047)) {
                    subSample.add(true);
                } else {
                    subSample.add(false);
                }
            } else {
                subSample.add(false);
            }
        }
        ArrayList<ArrayList<Double>> latlons = new ArrayList();
        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            if (subSample.get(i) == true) {
                ArrayList<Double> latlon = new ArrayList();
                latlon.add(mainFParent.allData.all_Nodes[i].lat);
                latlon.add(mainFParent.allData.all_Nodes[i].lon);
                latlons.add(latlon);
            }
        }
        GISLocationDialog.writeDoubleArrayList(latlons, "NetworkDistances" + File.separator + "nodeLocations.csv");
    }//GEN-LAST:event_jButton35ActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        ArrayList<LocationNodeSafegraph> gymLocations = initGymLocations();
        Integer indices[] = labelMergedFacilities(gymLocations, shopMergeThreshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);
        ArrayList<LocationNodeSafegraph> gymMergedLocations = mergeFacilitiesWithIndices(gymLocations, indicesList, indicesUniqueList, null);

        int trafficLayerIndex = -1;
        for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
            if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                trafficLayerIndex = i;
            }
        }

        int numVisitsToNearestOrder[] = new int[gymMergedLocations.size()];

        int numProcessors = myParent.mainModel.numCPUs;
//        if (numProcessors > Runtime.getRuntime().availableProcessors()) {
//            numProcessors = Runtime.getRuntime().availableProcessors();
//        }

        mainFParent.allData.setParallelLayers(numProcessors, -1);

        for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                if (isGym(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.naics_code) == true) {
                    LocationNode place = getNearestNode(mainFParent, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lat, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lon, null);
                    if (place != null) {

                        boolean isUniqueLocationNode = true;
                        int gymIndex = -1;
                        for (int b = 0; b < gymMergedLocations.size(); b++) {
                            if (gymMergedLocations.get(b).node.lat == place.lat && gymMergedLocations.get(b).node.lon == place.lon) {
                                isUniqueLocationNode = false;
                                gymIndex = b;
                                break;
                            }
                        }

                        if (gymIndex == -1) {
                            continue;
                        }

                        int gymGroupIndex = -1;
                        for (int y = 0; y < indicesUniqueList.size(); y++) {
                            if (indicesUniqueList.get(y).equals(indicesList.get(gymIndex))) {
                                gymGroupIndex = y;
                                break;
                            }
                        }

//                        int shopGroupIndex = indicesList.get(shopIndex);
                        LocationNodeSafegraph targetGymGroup = gymMergedLocations.get(gymGroupIndex);

                        if (isUniqueLocationNode == false) {
                            if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs != null) {
                                for (int k = 0; k < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.size(); k++) {
                                    CensusBlockGroup cBG = myParent.mainModel.allGISData.findCensusBlockGroup(myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.get(k).key);
                                    LocationNode home = getNearestNode(mainFParent, cBG.lon, cBG.lat, null);

                                    if (home == null) {
                                        float collisionPositionx = cBG.lat;
                                        float collisionPositiony = cBG.lon;
                                        double leastDistance = Double.POSITIVE_INFINITY;
                                        LocationNode nearestNode = mainFParent.allData.all_Nodes[0];
                                        for (int g = 0; g < mainFParent.allData.all_Nodes.length; g++) {
                                            //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
                                            double dist = Math.sqrt(Math.pow(collisionPositionx - mainFParent.allData.all_Nodes[g].lat, 2) + Math.pow(collisionPositiony - mainFParent.allData.all_Nodes[g].lon, 2));
                                            if (dist < leastDistance) {
                                                nearestNode = mainFParent.allData.all_Nodes[g];
                                                leastDistance = dist;
                                            }
                                        }
                                        home = nearestNode;
                                    }

                                    Routing routing = new Routing(mainFParent.allData, trafficLayerIndex, 0);

                                    routing.findPath(home, targetGymGroup.node);
                                    double distanceToTarget = routing.pathDistance;

                                    double distancesToOtherTargets[] = new double[gymMergedLocations.size()];

                                    ParallelRouting parallelRouting[] = new ParallelRouting[numProcessors];

                                    for (int f = 0; f < numProcessors - 1; f++) {
                                        parallelRouting[f] = new ParallelRouting(f, this, distancesToOtherTargets, (int) Math.floor(f * ((gymMergedLocations.size()) / numProcessors)), (int) Math.floor((f + 1) * ((gymMergedLocations.size()) / numProcessors)), trafficLayerIndex, home, gymMergedLocations);
                                    }
                                    parallelRouting[numProcessors - 1] = new ParallelRouting(numProcessors - 1, this, distancesToOtherTargets, (int) Math.floor((numProcessors - 1) * ((gymMergedLocations.size()) / numProcessors)), gymMergedLocations.size(), trafficLayerIndex, home, gymMergedLocations);

                                    for (int f = 0; f < numProcessors; f++) {
                                        parallelRouting[f].myThread.start();
                                    }
                                    for (int f = 0; f < numProcessors; f++) {
                                        try {
                                            parallelRouting[f].myThread.join();
//                                            for (int d = 0; d < parallelRouting[f].myData.length; d++) {
//                                                distancesToOtherTargets[d]=distancesToOtherTargets[d]+parallelRouting[f].myData[d];
//                                            }
//                                            System.out.println("thread " + f + "finished for location nodes: " + parallelRouting[f].myStartIndex + " | " + parallelRouting[f].myEndIndex);
                                        } catch (InterruptedException ie) {
                                            System.out.println(ie.toString());
                                        }
                                    }

//                                    for (int h = 0; h < shopMergedLocations.size(); h++) {
////                                    if (h != shopGroupIndex) {
//                                        Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, 0);
//                                        routingToOthers.findPath(home, shopMergedLocations.get(h));
//                                        distancesToOtherTargets[h] = routingToOthers.pathDistance;
////                                    }
//                                    }
                                    Arrays.sort(distancesToOtherTargets);
                                    int orderNumber = -1;
                                    for (int m = 0; m < distancesToOtherTargets.length; m++) {
                                        if (distancesToOtherTargets[m] == distanceToTarget && distanceToTarget != Double.POSITIVE_INFINITY) {
                                            orderNumber = m;
                                            break;
                                        }
                                    }
                                    if (orderNumber != -1) {
                                        numVisitsToNearestOrder[orderNumber] = numVisitsToNearestOrder[orderNumber] + myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).visitor_home_cbgs.get(k).value;
                                    }
                                }
                                for (int r = 0; r < numVisitsToNearestOrder.length; r++) {
                                    System.out.println(r + " " + numVisitsToNearestOrder[r]);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < numVisitsToNearestOrder.length; i++) {
            System.out.println(i + " " + numVisitsToNearestOrder[i]);
        }
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        relFacilities = initRelFacilities(templeMergeThreshold, null);
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        prepareClusteringData();
        try {
            prepareClusteringData();

            XMeans xMeans = new XMeans();
            xMeans.setMaxNumClusters(242);
            xMeans.setMinNumClusters(242);
            xMeans.setUseKDTree(false);
            xMeans.setMaxIterations(100);
            xMeans.setMaxKMeans(100);
            xMeans.setMaxKMeansForChildren(100);
            xMeans.setCutOffFactor(0.0);
            xMeans.buildClusterer(m_Instances);

            ClusterEvaluation eval = new ClusterEvaluation();
            eval.setClusterer(xMeans);
            eval.evaluateClusterer(m_Instances);

            double[] assignments = eval.getClusterAssignments();
            ArrayList<Integer> assignmentsArrayList = new ArrayList();

            ArrayList<Double> lats = new ArrayList();
            ArrayList<Double> lons = new ArrayList();

            System.out.println("Finished Clustering");
//            ArrayList<Person> activePeople;
//            activePeople = peopleNoTessellation;

//            for (int i = 0; i < assignments.length; i++) {
//                assignmentsArrayList.add((int) (assignments[i]) + 1);
//                lats.add(activePeople.get(i).exactProperties.exactHomeLocation.lat);
//                lons.add(activePeople.get(i).exactProperties.exactHomeLocation.lon);
//            }
            for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
                short[] val = new short[1];
                val[0] = (short) (1);
                mainFParent.allData.all_Nodes[i].layers.add(val);
            }
            for (int i = 0; i < assignments.length; i++) {
                short[] val = new short[1];
                val[0] = (short) (assignments[i]);
                mainFParent.allData.all_Nodes[i].layers.set(mainFParent.allData.all_Nodes[i].layers.size() - 1, val);
            }

//            LinkedHashSet<Integer> uniqueObservedFacilities = new LinkedHashSet(observedFacilities);
            String outputLayerName = "242-meansLayer";

            LayerDefinition tempLayer = new LayerDefinition("category", outputLayerName);
            int numCats = 242;
            tempLayer.categories = new String[numCats + 1];
            tempLayer.colors = new Color[numCats + 1];
            tempLayer.values = new double[numCats + 1];

            tempLayer.categories[0] = "NOT ASSIGNED";
            tempLayer.colors[0] = new Color(2, 2, 2);
            tempLayer.values[0] = Double.valueOf(0);
            for (int i = 1; i < numCats + 1; i++) {
                tempLayer.categories[i] = outputLayerName + " " + String.valueOf(i);
                tempLayer.colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numCats + 1 - 1, 1, 1));
                tempLayer.values[i] = Double.valueOf(i);
            }

            for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
                if (((short[]) mainFParent.allData.all_Nodes[i].layers.get(mainFParent.allData.all_Nodes[i].layers.size() - 1))[0] < 1) {
                    ((short[]) mainFParent.allData.all_Nodes[i].layers.get(mainFParent.allData.all_Nodes[i].layers.size() - 1))[0] = 1;
                }
            }

            mainFParent.allData.all_Layers.add(tempLayer);
            mainFParent.refreshLayersList();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton38ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
//        Patterns monthPatterns = mainModel.safegraph.allPatterns.monthlyPatternsList.get(0);
//        City castedScope = (City) (mainModel.ABM.studyScopeGeography);
//        ArrayList<ArrayList<Integer>> matrix = new ArrayList();
//        ArrayList<CensusBlockGroup> items = new ArrayList();
//        for (int i = 0; i < ((City) (myParent.mainModel.ABM.studyScopeGeography)).censusTracts.size(); i++) {
//            for (int j = 0; j < ((City) (myParent.mainModel.ABM.studyScopeGeography)).censusTracts.get(i).censusBlocks.size(); j++) {
//                items.add(((City) (myParent.mainModel.ABM.studyScopeGeography)).censusTracts.get(i).censusBlocks.get(j));
//            }
//        }
//        int numShops = 0;
//        for (int i = 0; i < monthPatterns.patternRecords.size(); i++) {
//            if (GISLocationDialog.isShop(monthPatterns.patternRecords.get(i).place.naics_code) == true) {
//                numShops = numShops + 1;
//            }
//        }
//        for (int i = 0; i < numShops; i++) {
//            matrix.add(new ArrayList());
//            for (int j = 0; j < items.size(); j++) {
//                matrix.get(i).add(0);
//            }
//        }
//        int shopIndex = -1;
//        for (int i = 0; i < monthPatterns.patternRecords.size(); i++) {
//            if (GISLocationDialog.isShop(monthPatterns.patternRecords.get(i).place.naics_code) == true) {
//                shopIndex = shopIndex + 1;
//                if (monthPatterns.patternRecords.get(i).visitor_home_cbgs_place != null) {
////                    CensusBlockGroup foundCBGSource = castedScope.findCBG(monthPatterns.patternRecords.get(i).poi_cbg_censusBlock.id);
////                    if (foundCBGSource != null) {
//                    for (int j = 0; j < monthPatterns.patternRecords.get(i).visitor_home_cbgs_place.size(); j++) {
//                        CensusBlockGroup foundCBGDestination = castedScope.findCBG(monthPatterns.patternRecords.get(i).visitor_home_cbgs_place.get(j).getKey().id);
//                        if (foundCBGDestination != null) {
//                            int sourceIndex = shopIndex;
//                            int destIndex = -1;
////                                for (int k = 0; k < items.size(); k++) {
////                                    if (((CensusBlockGroup) items.get(k)).id == monthPatterns.patternRecords.get(i).poi_cbg_censusBlock.id) {
////                                        sourceIndex = k;
////                                        break;
////                                    }
////                                }
//                            for (int k = 0; k < items.size(); k++) {
//                                if (((CensusBlockGroup) items.get(k)).id == monthPatterns.patternRecords.get(i).visitor_home_cbgs_place.get(j).getKey().id) {
//                                    destIndex = k;
//                                    break;
//                                }
//                            }
//                            if (sourceIndex >= 0 && destIndex >= 0) {
//                                matrix.get(sourceIndex).set(destIndex, matrix.get(sourceIndex).get(destIndex) + monthPatterns.patternRecords.get(i).visitor_home_cbgs_place.get(j).value);
//                            }
//                        }
//                    }
////                    }
//                }
//            }
//        }
//        try {
//            FileWriter myWriter = new FileWriter(castedScope.name + "_CBGShop_travelMatrix.csv");
//            for (int i = 0; i < matrix.size(); i++) {
//                for (int j = 0; j < matrix.get(i).size(); j++) {
//                    myWriter.write(String.valueOf(matrix.get(i).get(j)));
//                    if (j != matrix.get(i).size() - 1) {
//                        myWriter.write(",");
//                    }
//                }
//                myWriter.write("\n");
//            }
//            myWriter.close();
//            System.out.println("Successfully wrote to the file.");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        ArrayList<String[]> rows=new ArrayList();
        for (int i = 0; i < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size(); i++) {
            System.out.println("Month "+i);
            for (int j = 0; j < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size(); j++) {
                System.out.println("Month "+i+" place percentage "+(float)j/myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size());
                if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).censusBlock != null) {
                    if (isHospital_FACS(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true) {
//                        LocationNode node = getNearestNode(mainFParent, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, null);
                        float[] result = getBuildingAreaLevelsOnline(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, false);
                        float landArea = result[0];
                        int buldingLevels = (int) result[1];
                        float s=buldingLevels*landArea;
                        String[] row = new String[4];
                        row[0]="hospital";
                        row[1]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat);
                        row[2]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon);
                        row[3]=String.valueOf(s);
                        rows.add(row);
                    }else if(isLeisure_FACS(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true){
                        float[] result = getBuildingAreaLevelsOnline(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, false);
                        float landArea = result[0];
                        int buldingLevels = (int) result[1];
                        float s=buldingLevels*landArea;
                        String[] row = new String[4];
                        row[0]="leisure";
                        row[1]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat);
                        row[2]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon);
                        row[3]=String.valueOf(s);
                        rows.add(row);
                    }else if(isOffice_FACS(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true){
                        float[] result = getBuildingAreaLevelsOnline(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, false);
                        float landArea = result[0];
                        int buldingLevels = (int) result[1];
                        float s=buldingLevels*landArea;
                        String[] row = new String[4];
                        row[0]="office";
                        row[1]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat);
                        row[2]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon);
                        row[3]=String.valueOf(s);
                        rows.add(row);
                    }else if(isPark_FACS(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true){
                        float[] result = getBuildingAreaLevelsOnline(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, false);
                        float landArea = result[0];
                        int buldingLevels = (int) result[1];
                        float s=buldingLevels*landArea;
                        String[] row = new String[4];
                        row[0]="park";
                        row[1]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat);
                        row[2]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon);
                        row[3]=String.valueOf(s);
                        rows.add(row);
                    }else if(isSchool_FACS(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true){
                        float[] result = getBuildingAreaLevelsOnline(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, false);
                        float landArea = result[0];
                        int buldingLevels = (int) result[1];
                        float s=buldingLevels*landArea;
                        String[] row = new String[4];
                        row[0]="school";
                        row[1]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat);
                        row[2]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon);
                        row[3]=String.valueOf(s);
                        rows.add(row);
                    }else if(isShopping_FACS(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true){
                        float[] result = getBuildingAreaLevelsOnline(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, false);
                        float landArea = result[0];
                        int buldingLevels = (int) result[1];
                        float s=buldingLevels*landArea;
                        String[] row = new String[4];
                        row[0]="shopping";
                        row[1]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat);
                        row[2]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon);
                        row[3]=String.valueOf(s);
                        rows.add(row);
                    }else if(isSupermarket_FACS(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true){
                        float[] result = getBuildingAreaLevelsOnline(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, false);
                        float landArea = result[0];
                        int buldingLevels = (int) result[1];
                        float s=buldingLevels*landArea;
                        String[] row = new String[4];
                        row[0]="supermarket";
                        row[1]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat);
                        row[2]=String.valueOf(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon);
                        row[3]=String.valueOf(s);
                        rows.add(row);
                    }
                }
            }
        }
        CsvWriter writer = new CsvWriter();
        try {
            Files.createDirectories(Paths.get("FACS_generated" + File.separator + myParent.mainModel.ABM.studyScope));
            writer.write(new File("FACS_generated" + File.separator + myParent.mainModel.ABM.studyScope + File.separator + "_buildings.csv_"), Charset.forName("US-ASCII"), rows);
        } catch (IOException ex) {
            Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton40ActionPerformed

    public static void writeDoubleArrayList(ArrayList<ArrayList<Double>> input, String path) {
        ArrayList<String[]> data = new ArrayList();
        for (int i = 0; i < input.size(); i++) {
            String[] row = new String[input.get(i).size()];
            for (int j = 0; j < input.get(i).size(); j++) {
                row[j] = String.valueOf(input.get(i).get(j));
            }
            data.add(row);
        }
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(path));
            writer.writeAll(data);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void prepareClusteringData() {
        StringBuilder arffData = new StringBuilder();
        arffData.append("@RELATION data" + "\n");

        arffData.append("@ATTRIBUTE ").append("lat").append(" numeric").append("\n");
        arffData.append("@ATTRIBUTE ").append("lon").append(" numeric").append("\n");

        arffData.append("@DATA").append("\n");

        int baseLayer = mainFParent.findLayerExactNotCaseSensitive("base");

        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
//        for (int i = 0; i < 9000; i++) {
            arffData.append(mainFParent.allData.all_Nodes[i].lat);
            arffData.append(",");
            arffData.append(mainFParent.allData.all_Nodes[i].lon);
            arffData.append("\n");
        }

        String str = arffData.toString();
        InputStream is = new ByteArrayInputStream(str.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        System.out.println("DATA READY!");
        ArffLoader.ArffReader arff;
        try {
            arff = new ArffLoader.ArffReader(br);
            m_Instances = arff.getData();
//            for (int i = 0; i < m_Instances.numInstances(); i++) {
//                m_Instances.instance(i).setWeight(Double.parseDouble(data[i][2]));//IMPIMPIMPIMPIMP
////                System.out.println(data[i][2]);
//            }
//            System.out.println("DATA READY!");
        } catch (IOException | NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void writeByteArrayList(ArrayList<ArrayList<Byte>> input, String path) {
        ArrayList<String[]> data = new ArrayList();
        for (int i = 0; i < input.size(); i++) {
            String[] row = new String[input.get(i).size()];
            for (int j = 0; j < input.get(i).size(); j++) {
                row[j] = String.valueOf(input.get(i).get(j));
            }
            data.add(row);
        }
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(path));
            writer.writeAll(data);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FacilityLocation[] initShopsByExactNumber(int input) {
        FacilityLocation[] output = null;
        float tempThresh = shopMergeThreshold;
        int numTries = 0;
        int numMaxTries = 100;
        float step = tempThresh * 0.2f;
        boolean isMore = false;
        ArrayList<LocationNodeSafegraph> shopLocations = initShopLocations();
        while (numTries < numMaxTries) {
            FacilityLocation[] facilites = initShopFacilities(shopLocations, tempThresh);
            if (facilites.length == input) {
                output = facilites;
                break;
            } else if (facilites.length > input) {
                if (isMore == false) {
                    step = step * 0.9f;
                }
                isMore = true;
                tempThresh = tempThresh + step;
                numTries = numTries + 1;
            } else {
                if (isMore == true) {
                    step = step * 0.9f;
                }
                isMore = false;
                tempThresh = tempThresh - step;
                numTries = numTries + 1;
            }
        }
        return output;
    }

    public FacilityLocation[] initSchoolsByExactNumber(int input, ArrayList<LocationNode> banedNodes) {
        FacilityLocation[] output = null;
        float tempThresh = schoolMergeThreshold;
        int numTries = 0;
        int numMaxTries = 100;
        float step = tempThresh * 0.2f;
        boolean isMore = false;
        ArrayList<LocationNodeSafegraph> schoolLocations = initSchoolLocations();
        while (numTries < numMaxTries) {
            FacilityLocation[] facilites = initSchoolFacilities(schoolLocations, tempThresh, banedNodes);
            if (facilites.length == input) {
                output = facilites;
                break;
            } else if (facilites.length > input) {
                if (isMore == false) {
                    step = step * 0.9f;
                }
                isMore = true;
                tempThresh = tempThresh + step;
                numTries = numTries + 1;
            } else {
                if (isMore == true) {
                    step = step * 0.9f;
                }
                isMore = false;
                tempThresh = tempThresh - step;
                numTries = numTries + 1;
            }
        }
        return output;
    }

    public void temp_VDFNC_70() {
        float currentShopMergeThreshold = 0.01072975f;
        float currentSchoolMergeThreshold = 0.009574889f;

        shopFacilities = initShopFacilities(currentShopMergeThreshold);
        schoolFacilities = initSchoolFacilities(currentSchoolMergeThreshold, null);
        makeVDs(shopFacilities, "shops_v_VDFNC_" + 70);
        makeVDs(schoolFacilities, "schools_v_VDFNC_" + 70);

        makeVDCombination("shops_v_VDFNC_" + 70, "schools_v_VDFNC_" + 70, "VDFNC_" + 70);

        int generatedLayer = mainFParent.findLayerExactNotCaseSensitive("VDFNC_" + 70);
        int currentNumCells = ((LayerDefinition) (mainFParent.allData.all_Layers.get(generatedLayer))).categories.length - 1;
        System.out.println("shopFacilities.length: " + shopFacilities.length);
        System.out.println("schoolFacilities.length: " + schoolFacilities.length);
        System.out.println("currentNumCells: " + currentNumCells);
        System.out.println("currentShopMergeThreshold: " + currentShopMergeThreshold);
        System.out.println("currentSchoolMergeThreshold: " + currentSchoolMergeThreshold);
    }

    public void temp_VDFNC_100() {
        float currentShopMergeThreshold = shopMergeThreshold;
        float currentSchoolMergeThreshold = schoolMergeThreshold;

        float stepSize = 0.2f;

        currentShopMergeThreshold = currentShopMergeThreshold * (1 + stepSize);
        currentSchoolMergeThreshold = currentSchoolMergeThreshold * (1 + stepSize);
        stepSize = stepSize * 0.95f;

        currentShopMergeThreshold = currentShopMergeThreshold * (1 + stepSize);
        currentSchoolMergeThreshold = currentSchoolMergeThreshold * (1 + stepSize);
        stepSize = stepSize * 0.95f;

        currentShopMergeThreshold = currentShopMergeThreshold * (1 - stepSize);
        currentSchoolMergeThreshold = currentSchoolMergeThreshold * (1 - stepSize);
        stepSize = stepSize * 0.95f;

        currentShopMergeThreshold = currentShopMergeThreshold * (1 + stepSize);
        currentSchoolMergeThreshold = currentSchoolMergeThreshold * (1 + stepSize);
        stepSize = stepSize * 0.95f;

        currentShopMergeThreshold = currentShopMergeThreshold * (1 - stepSize);
        currentSchoolMergeThreshold = currentSchoolMergeThreshold * (1 - stepSize);
        stepSize = stepSize * 0.95f;

        shopFacilities = initShopFacilities(currentShopMergeThreshold);
        schoolFacilities = initSchoolFacilities(currentSchoolMergeThreshold, null);
        makeVDs(shopFacilities, "shops_v_VDFNC_" + 100);
        makeVDs(schoolFacilities, "schools_v_VDFNC_" + 100);

        makeVDCombination("shops_v_VDFNC_" + 100, "schools_v_VDFNC_" + 100, "VDFNC_" + 100);

        int generatedLayer = mainFParent.findLayerExactNotCaseSensitive("VDFNC_" + 100);
        int currentNumCells = ((LayerDefinition) (mainFParent.allData.all_Layers.get(generatedLayer))).categories.length - 1;
        System.out.println("shopFacilities.length: " + shopFacilities.length);
        System.out.println("schoolFacilities.length: " + schoolFacilities.length);
        System.out.println("currentNumCells: " + currentNumCells);
        System.out.println("currentShopMergeThreshold: " + currentShopMergeThreshold);
        System.out.println("currentSchoolMergeThreshold: " + currentSchoolMergeThreshold);
    }

    public void deleteLayer(String name) {
        int index = mainFParent.findLayerExactNotCaseSensitive(name);
        mainFParent.allData.all_Layers.remove(index);
        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            mainFParent.allData.all_Nodes[i].layers.remove(index);
        }
        mainFParent.refreshLayersList();
    }

    public void makeVDs(FacilityLocation[] facilities, String outputLayerName) {
        mainFParent.flowControl.tollOff = 5;
//        System.out.println("VD CONSTRUCTION START!");
        mainFParent.flowControl.simulateOneLayerCompetingFacilityBased(facilities, mainFParent.findLayerContains("traffic"), myParent.numProcessors, -1, false);
//        System.out.println("SIMULATION DONE!");
        mainFParent.flowControl.correctFacilityLava(mainFParent.findLayerContains("traffic"), myParent.numProcessors);
//        System.out.println("SIMULATION CORRECTION DONE!");

        ArrayList<Integer> observedFacilities = new ArrayList();

        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            short[] val = new short[1];
            if (mainFParent.allData.all_Nodes[i].isBurned == true) {
                for (int k = 0; k < mainFParent.allData.all_Nodes[i].burntBy.length; k++) {
                    for (int j = 0; j < facilities.length; j++) {
                        if (mainFParent.allData.all_Nodes[i].burntBy[k] == facilities[j]) {
                            observedFacilities.add(j);
                            val[0] = (short) (j + 1 + 1);
                        }
                    }
                }
                mainFParent.allData.all_Nodes[i].layers.add(val);
            } else {
                val[0] = 1;
                mainFParent.allData.all_Nodes[i].layers.add(val);
            }
        }

        LinkedHashSet<Integer> uniqueObservedFacilities = new LinkedHashSet(observedFacilities);

        LayerDefinition tempLayer = new LayerDefinition("category", outputLayerName);
        int numShops = uniqueObservedFacilities.size();
        tempLayer.categories = new String[numShops + 1];
        tempLayer.colors = new Color[numShops + 1];
        tempLayer.values = new double[numShops + 1];

        tempLayer.categories[0] = "NOT ASSIGNED";
        tempLayer.colors[0] = new Color(2, 2, 2);
        tempLayer.values[0] = Double.valueOf(0);
        for (int i = 1; i < numShops + 1; i++) {
            tempLayer.categories[i] = outputLayerName + " " + String.valueOf(i);
            tempLayer.colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numShops + 1 - 1, 1, 1));
            tempLayer.values[i] = Double.valueOf(i);
        }

        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            if (((short[]) mainFParent.allData.all_Nodes[i].layers.get(mainFParent.allData.all_Nodes[i].layers.size() - 1))[0] < 1) {
                ((short[]) mainFParent.allData.all_Nodes[i].layers.get(mainFParent.allData.all_Nodes[i].layers.size() - 1))[0] = 1;
            }
        }

        mainFParent.allData.all_Layers.add(tempLayer);
        mainFParent.refreshLayersList();
    }

    public void makeVDCombination(String firstVDLayerName, String secondVDLayerName, String outputLayerName) {
        int firstLayer = mainFParent.findLayerExactNotCaseSensitive(firstVDLayerName);
        int secondLayer = mainFParent.findLayerExactNotCaseSensitive(secondVDLayerName);
        HashMap<String, String> perms = new HashMap();
        HashMap<String, Integer> usedPerms = new HashMap();
        HashMap<String, Integer> refinedPermsById = new HashMap();
        HashMap<String, Integer> refinedPermsbyUse = new HashMap();
        ArrayList<String> refinedPermsKeys = new ArrayList();
        int counter = 0;
        for (int i = 1; i < ((LayerDefinition) mainFParent.allData.all_Layers.get(firstLayer)).categories.length; i++) {
            for (int j = 1; j < ((LayerDefinition) mainFParent.allData.all_Layers.get(secondLayer)).categories.length; j++) {
                perms.put(i + "_" + j, String.valueOf(counter));
                usedPerms.put(i + "_" + j, 0);
                counter = counter + 1;
            }
        }
        usedPerms.put("0", 0);

        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            short shopIndex = (short) (((short[]) mainFParent.allData.all_Nodes[i].layers.get(firstLayer))[0] - 1);
            short schoolIndex = (short) (((short[]) mainFParent.allData.all_Nodes[i].layers.get(secondLayer))[0] - 1);
            int currentCounter = -1;
            if (shopIndex == 0 || schoolIndex == 0) {//NOT ASSIGNED SCENARIOS
                currentCounter = usedPerms.get("0");
            } else {
                currentCounter = usedPerms.get(shopIndex + "_" + schoolIndex);
            }

            usedPerms.put(shopIndex + "_" + schoolIndex, currentCounter + 1);
        }

        counter = 1;
        int refinedCounter = 0;
        for (Map.Entry<String, Integer> entry : usedPerms.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value > 0) {
                refinedPermsbyUse.put(key, value);
                refinedPermsById.put(key, refinedCounter + 1);
                refinedCounter = refinedCounter + 1;
            }
            counter = counter + 1;
        }

        LayerDefinition tempLayer = new LayerDefinition("category", outputLayerName);
        tempLayer.categories = new String[refinedCounter + 1];
        tempLayer.colors = new Color[refinedCounter + 1];
        tempLayer.values = new double[refinedCounter + 1];

        tempLayer.categories[0] = "NOT ASSIGNED";
        tempLayer.colors[0] = new Color(2, 2, 2);
        tempLayer.values[0] = Double.valueOf(1);
//        for (int i = 1; i < counter + 1; i++) {
//            tempLayer.categories[i] = "combination " + String.valueOf(i);
//            tempLayer.colors[i] = new Color(Color.HSBtoRGB((float) i / (float) counter + 1 - 1, 1, 1));
//            tempLayer.values[i] = Double.valueOf(i + 1);
//        }

        int combinationCounter = 1;
        for (Map.Entry<String, Integer> entry : refinedPermsById.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            tempLayer.categories[combinationCounter] = "combination " + key;
            tempLayer.colors[combinationCounter] = new Color(Color.HSBtoRGB((float) combinationCounter / (float) refinedCounter + 1 - 1, 1, 1));
            tempLayer.values[combinationCounter] = Double.valueOf(value);
            combinationCounter = combinationCounter + 1;
        }

        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            short shopIndex = (short) (((short[]) mainFParent.allData.all_Nodes[i].layers.get(firstLayer))[0] - 1);
            short schoolIndex = (short) (((short[]) mainFParent.allData.all_Nodes[i].layers.get(secondLayer))[0] - 1);
            String combinationIndex;
            if (shopIndex == 0 || schoolIndex == 0) {//NOT ASSIGNED SCENARIOS
                combinationIndex = "0";
            } else {
                combinationIndex = String.valueOf(refinedPermsById.get(shopIndex + "_" + schoolIndex));
            }
            short[] val = new short[1];
//            if(combinationIndex==null){
//                System.out.println("combinationIndex: "+combinationIndex);
//            }
            val[0] = (short) (Short.valueOf(combinationIndex) + 1);
//            if (val[0] == 0) {
//                System.out.println("!!!");
//            }
//            if (val[0] > 64) {
//                System.out.println("!!!");
//            }
            mainFParent.allData.all_Nodes[i].layers.add(val);
        }

        mainFParent.allData.all_Layers.add(tempLayer);
        mainFParent.refreshLayersList();
    }

    public void makeTempVDCombinations() {

    }

    public double getCBGPercentageInvolvedForVD(City city, VDCell cell, CensusBlockGroup input) {
        if (cell != null && input != null) {
            for (int j = 0; j < cell.cBGsIDsInvolved.size(); j++) {
                if (cell.cBGsIDsInvolved.get(j) == input.id) {
                    Double output = cell.cBGsPercentageInvolved.get(j);
                    return output;
                }
            }
        }
        return 0;
    }

    public static boolean isShop(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if (naicsString.startsWith("44511") || naicsString.startsWith("44512") || naicsString.startsWith("44711")) {
            return true;
        }
        return false;
    }

    public static boolean isFoodAndGrocery(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if ((naicsString.startsWith("44") || naicsString.startsWith("45")) && !naicsString.startsWith("4411") && !naicsString.startsWith("4412") && !naicsString.startsWith("4413")) {
            return true;
        }
        return false;
    }

    public static boolean isReligiousOrganization(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if (naicsString.startsWith("8131")) {
            return true;
        }
        return false;
    }

    public static boolean isSchool(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if (naicsString.startsWith("61")) {
            return true;
        }
        return false;
    }

    public static boolean isGym(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if (naicsString.startsWith("71394")) {
            return true;
        }
        return false;
    }

    public static boolean isHospital_FACS(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if ((naicsString.startsWith("6221"))) {
            return true;
        }
        return false;
    }

    public static boolean isLeisure_FACS(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if ((naicsString.startsWith("71")) && !(naicsString.startsWith("712190"))) {
            return true;
        }
        return false;
    }

    public static boolean isOffice_FACS(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if ((naicsString.startsWith("561"))) {
            return true;
        }
        return false;
    }

    public static boolean isPark_FACS(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if ((naicsString.startsWith("712190"))) {
            return true;
        }
        return false;
    }

    public static boolean isSchool_FACS(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if ((naicsString.startsWith("611"))) {
            return true;
        }
        return false;
    }

    public static boolean isShopping_FACS(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if (((naicsString.startsWith("44")) || (naicsString.startsWith("45"))) && !(naicsString.startsWith("4451"))) {
            return true;
        }
        return false;
    }

    public static boolean isSupermarket_FACS(int naicsCode) {
        String naicsString = String.valueOf(naicsCode);
        if ((naicsString.startsWith("4451"))) {
            return true;
        }
        return false;
    }

    public ArrayList<LocationNodeSafegraph> initShopLocations() {
        ArrayList<LocationNodeSafegraph> shops = new ArrayList();
        for (int i = 0; i < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size(); j++) {
                if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).censusBlock != null) {
                    if (isFoodAndGrocery(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true) {
                        LocationNode node = getNearestNode(mainFParent, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, null);
                        if (node != null) {
                            if (isUniqueLocationNode(shops, node)) {
                                LocationNodeSafegraph nodeSafegraph = new LocationNodeSafegraph();
                                nodeSafegraph.node = node;
                                nodeSafegraph.place = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j);
                                nodeSafegraph.placeKey = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).placeKey;
                                shops.add(nodeSafegraph);
                            }
                        }
                    }
                }
//                System.out.println((float) j / myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size());
            }
        }
        return shops;
    }

    public ArrayList<LocationNodeSafegraph> initShopLocationsSlow() {
        int trafficLayerIndex = -1;
        for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
            if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                trafficLayerIndex = i;
            }
        }
        ArrayList<LocationNode> allIMPNodes = new ArrayList();
        for (int u = 0; u < mainFParent.allData.all_Nodes.length; u++) {
            if (((short[]) (mainFParent.allData.all_Nodes[u].layers.get(0)))[0] == 2) {
                allIMPNodes.add(mainFParent.allData.all_Nodes[u]);
            }
        }
        int maxTry = 1;
        ArrayList<LocationNodeSafegraph> shops = new ArrayList();
        for (int i = 0; i < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size(); j++) {
                if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).censusBlock != null) {
                    if (isFoodAndGrocery(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true) {
                        LocationNode node = getNearestNode(mainFParent, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, null);
                        if (node != null) {
                            if (isUniqueLocationNode(shops, node)) {
                                boolean isValid = false;
                                for (int t = 0; t < maxTry; t++) {
                                    mainFParent.allData.setParallelLayers(1, -1);
                                    int nodeIndex = -1;
                                    double minDist = 1000;
                                    for (int u = 0; u < allIMPNodes.size(); u++) {
                                        double dist = Math.abs(node.lat - allIMPNodes.get(u).lat) + Math.abs(node.lon - allIMPNodes.get(u).lon);
                                        if (minDist > dist) {
                                            nodeIndex = u;
                                            minDist = dist;
                                        }
                                    }
                                    Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, 0);
                                    routingToOthers.findPath(mainFParent.allData.all_Nodes[nodeIndex], node);
                                    double dist = routingToOthers.pathDistance;
                                    if (Double.isInfinite(dist) == false) {
//                                        System.out.println("FOUND VALID SHOP WITH "+t+" TRIES!");
                                        isValid = true;
                                        break;
                                    }
                                }
                                if (isValid == true) {
                                    LocationNodeSafegraph nodeSafegraph = new LocationNodeSafegraph();
                                    nodeSafegraph.node = node;
                                    nodeSafegraph.place = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j);
                                    nodeSafegraph.placeKey = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).placeKey;
                                    shops.add(nodeSafegraph);
                                } else {
                                    System.out.println("Shop most likely invalid!");
                                }
                            }
                        }
                    }
                }
                System.out.println((float) j / myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size());
            }
        }
        return shops;
    }

    public ArrayList<LocationNodeSafegraph> initAllLocations() {
        ArrayList<LocationNodeSafegraph> pOILocationNodes = new ArrayList();
        for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.size(); j++) {
                if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.censusBlock != null) {
//                    if (isFoodAndGrocery(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true) {
                    LocationNode node = getNearestNode(mainFParent, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lat, myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place.lon, null);
                    if (node != null) {
                        if (isUniqueLocationNode(pOILocationNodes, node)) {
                            LocationNodeSafegraph nodeSafegraph = new LocationNodeSafegraph();
                            nodeSafegraph.node = node;
                            nodeSafegraph.place = myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).place;
                            nodeSafegraph.placeKey = myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).placeKey;
                            nodeSafegraph.numVisits = myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).patternRecords.get(j).raw_visit_counts;
                            pOILocationNodes.add(nodeSafegraph);
                        }
                    }
//                    }
                }
            }
        }
        return pOILocationNodes;
    }

    public ArrayList<LocationNodeSafegraph> initGymLocations() {
        ArrayList<LocationNodeSafegraph> gyms = new ArrayList();
        for (int i = 0; i < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size(); j++) {
                if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).censusBlock != null) {
                    if (isGym(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true) {
                        LocationNode node = getNearestNode(mainFParent, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, null);
                        if (node != null) {
                            if (isUniqueLocationNode(gyms, node)) {
                                LocationNodeSafegraph locationNodeSafegraph = new LocationNodeSafegraph();
                                locationNodeSafegraph.node = node;
                                locationNodeSafegraph.place = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j);
                                locationNodeSafegraph.placeKey = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).placeKey;
                                gyms.add(locationNodeSafegraph);
                            }
                        }
                    }
                }
            }
        }
        return gyms;
    }

    public ArrayList<LocationNodeSafegraph> initSchoolLocations() {
        ArrayList<LocationNodeSafegraph> schools = new ArrayList();
        for (int i = 0; i < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size(); j++) {
                if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).censusBlock != null) {
                    if (isSchool(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true) {
                        LocationNode node = getNearestNode(mainFParent, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, null);
                        if (node != null) {
                            if (isUniqueLocationNode(schools, node)) {
                                LocationNodeSafegraph locationNodeSafegraph = new LocationNodeSafegraph();
                                locationNodeSafegraph.node = node;
                                locationNodeSafegraph.place = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j);
                                locationNodeSafegraph.placeKey = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).placeKey;
                                schools.add(locationNodeSafegraph);
                            }
                        }
                    }
                }
            }
        }
        return schools;
    }

    public ArrayList<LocationNodeSafegraph> initSchoolLocationsSlow() {
        int trafficLayerIndex = -1;
        for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
            if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                trafficLayerIndex = i;
            }
        }
        int maxTry = 3;
        ArrayList<LocationNodeSafegraph> schools = new ArrayList();
        for (int i = 0; i < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size(); j++) {
                if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).censusBlock != null) {
                    if (isSchool(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true) {
                        LocationNode node = getNearestNode(mainFParent, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, null);
                        if (node != null) {
                            if (isUniqueLocationNode(schools, node)) {
                                boolean isValid = false;
                                for (int t = 0; t < maxTry; t++) {
                                    mainFParent.allData.setParallelLayers(1, -1);
                                    int nodeIndex = (int) (Math.random() * (mainFParent.allData.all_Nodes.length - 1));
                                    Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, 0);
                                    routingToOthers.findPath(mainFParent.allData.all_Nodes[nodeIndex], node);
                                    double dist = routingToOthers.pathDistance;
                                    if (Double.isInfinite(dist) == false) {
                                        isValid = true;
                                        break;
                                    }
                                }
                                if (isValid == true) {
                                    LocationNodeSafegraph locationNodeSafegraph = new LocationNodeSafegraph();
                                    locationNodeSafegraph.node = node;
                                    locationNodeSafegraph.place = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j);
                                    locationNodeSafegraph.placeKey = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).placeKey;
                                    schools.add(locationNodeSafegraph);
                                } else {
                                    System.out.println("School most likely invalid!");
                                }
                            }
                        }
                    }
                }
            }
        }
        return schools;
    }

    public ArrayList<LocationNodeSafegraph> initTempleLocations() {
        ArrayList<LocationNodeSafegraph> temples = new ArrayList();
        for (int i = 0; i < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size(); j++) {
                if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).censusBlock != null) {
                    if (isReligiousOrganization(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true) {
                        LocationNode node = getNearestNode(mainFParent, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, null);
                        if (node != null) {
                            if (isUniqueLocationNode(temples, node)) {
                                LocationNodeSafegraph locationNodeSafegraph = new LocationNodeSafegraph();
                                locationNodeSafegraph.node = node;
                                locationNodeSafegraph.place = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j);
                                locationNodeSafegraph.placeKey = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).placeKey;
                                temples.add(locationNodeSafegraph);
                            }
                        }
                    }
                }
            }
        }
        return temples;
    }

    public ArrayList<LocationNodeSafegraph> initTempleLocationsSlow() {
        int trafficLayerIndex = -1;
        for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
            if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                trafficLayerIndex = i;
            }
        }
        int maxTry = 3;
        ArrayList<LocationNodeSafegraph> temples = new ArrayList();
        for (int i = 0; i < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.size(); i++) {
            for (int j = 0; j < myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.size(); j++) {
                if (myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).censusBlock != null) {
                    if (isReligiousOrganization(myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).naics_code) == true) {
                        LocationNode node = getNearestNode(mainFParent, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lat, myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).lon, null);
                        if (node != null) {
                            if (isUniqueLocationNode(temples, node)) {
                                boolean isValid = false;
                                for (int t = 0; t < maxTry; t++) {
                                    mainFParent.allData.setParallelLayers(1, -1);
                                    int nodeIndex = (int) (Math.random() * (mainFParent.allData.all_Nodes.length - 1));
                                    Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, 0);
                                    routingToOthers.findPath(mainFParent.allData.all_Nodes[nodeIndex], node);
                                    double dist = routingToOthers.pathDistance;
                                    if (Double.isInfinite(dist) == false) {
                                        isValid = true;
                                        break;
                                    }
                                }
                                if (isValid == true) {
                                    LocationNodeSafegraph locationNodeSafegraph = new LocationNodeSafegraph();
                                    locationNodeSafegraph.node = node;
                                    locationNodeSafegraph.place = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j);
                                    locationNodeSafegraph.placeKey = myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.get(i).placesRecords.get(j).placeKey;
                                    temples.add(locationNodeSafegraph);
                                } else {
                                    System.out.println("Temple most likely invalid!");
                                }
                            }
                        }
                    }
                }
            }
        }
        return temples;
    }

    public FacilityLocation[] initShopFacilities(float threshold) {
        ArrayList<LocationNodeSafegraph> shopLocations = initShopLocations();
        Integer indices[] = labelMergedFacilities(shopLocations, threshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);

        System.out.println("Initial number of shops: " + shopLocations.size());
        ArrayList<LocationNodeSafegraph> shops = mergeFacilitiesWithIndices(shopLocations, indicesList, indicesUniqueList, null);

        shopLocationNodes = shops;

        int numFacilities = shops.size();
        FacilityLocation output[] = new FacilityLocation[numFacilities];
        Color colors[] = new Color[numFacilities];
        for (int i = 0; i < numFacilities; i++) {
            colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numFacilities - 1, 1, 1));

        }
        for (int i = 0; i < numFacilities; i++) {
            output[i] = new FacilityLocation(mainFParent, shops.get(i).node, shops.get(i).node.myWays[0], 20d);
            output[i].color = colors[i];
            output[i].isDecoyable = true;
            output[i].tollOff = 0.5;//IMP
        }
        System.out.println("shops generated: " + output.length);
        return output;
    }

    public FacilityLocation[] initShopFacilities(ArrayList<LocationNodeSafegraph> locations, float threshold) {
        Integer indices[] = labelMergedFacilities(locations, threshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);

        System.out.println("Initial number of shops: " + locations.size());
        ArrayList<LocationNodeSafegraph> shops = mergeFacilitiesWithIndices(locations, indicesList, indicesUniqueList, null);

        shopLocationNodes = shops;

        int numFacilities = shops.size();
        FacilityLocation output[] = new FacilityLocation[numFacilities];
        Color colors[] = new Color[numFacilities];
        for (int i = 0; i < numFacilities; i++) {
            colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numFacilities - 1, 1, 1));

        }
        for (int i = 0; i < numFacilities; i++) {
            output[i] = new FacilityLocation(mainFParent, shops.get(i).node, shops.get(i).node.myWays[0], 20d);
            output[i].color = colors[i];
            output[i].isDecoyable = true;
            output[i].tollOff = 0.5;//IMP
        }
        System.out.println("shops generated: " + output.length);
        return output;
    }

    public ArrayList<LocationNodeSafegraph> mergeFacilitiesWithIndices(ArrayList<LocationNodeSafegraph> input, ArrayList<Integer> allIndices, ArrayList<Integer> uniqueIndices, ArrayList<LocationNode> bannedNodes) {
        LocationNodeSafegraph[] centroids = new LocationNodeSafegraph[uniqueIndices.size()];
        for (int i = 0; i < allIndices.size(); i++) {
            int index = -1;
            for (int j = 0; j < uniqueIndices.size(); j++) {
                if (allIndices.get(i).equals(uniqueIndices.get(j))) {
                    index = j;
                    break;
                }
            }
            if (centroids[index] == null) {
                LocationNodeSafegraph locationNodeSafegraph = new LocationNodeSafegraph();
                locationNodeSafegraph.node = input.get(i).node;
                if (locationNodeSafegraph.placeKeys == null) {
                    locationNodeSafegraph.places = new ArrayList();
                    locationNodeSafegraph.places.add(input.get(i).place);
                    locationNodeSafegraph.placeKeys = new ArrayList();
                    locationNodeSafegraph.placeKeys.add(input.get(i).placeKey);
                } else {
                    locationNodeSafegraph.places.add(input.get(i).place);
                    locationNodeSafegraph.placeKeys.add(input.get(i).placeKey);
                }
                centroids[index] = locationNodeSafegraph;
            } else {
                LocationNodeSafegraph locationNodeSafegraph = centroids[index];
                locationNodeSafegraph.node = new LocationNode(0, (centroids[index].node.lat + input.get(i).node.lat) / 2f, (centroids[index].node.lon + input.get(i).node.lon) / 2f, 0);
                if (locationNodeSafegraph.placeKeys == null) {
                    locationNodeSafegraph.places = new ArrayList();
                    locationNodeSafegraph.places.add(input.get(i).place);
                    locationNodeSafegraph.placeKeys = new ArrayList();
                    locationNodeSafegraph.placeKeys.add(input.get(i).placeKey);
                } else {
                    locationNodeSafegraph.places.add(input.get(i).place);
                    locationNodeSafegraph.placeKeys.add(input.get(i).placeKey);
                }
                centroids[index] = locationNodeSafegraph;
            }
        }
        ArrayList<LocationNodeSafegraph> output = new ArrayList();

        for (int i = 0; i < centroids.length; i++) {
            centroids[i].node = getNearestNode(mainFParent, (float) centroids[i].node.lat, (float) centroids[i].node.lon, bannedNodes);
            output.add(centroids[i]);
        }

        return output;
    }

    public Integer[] labelMergedFacilities(ArrayList<LocationNodeSafegraph> input, float threshold) {
        Integer output[] = new Integer[input.size()];
        for (int i = 0; i < input.size(); i++) {
            output[i] = i;
        }
        for (int i = 0; i < input.size(); i++) {
//            boolean tooClose = false;
            for (int j = 0; j < input.size(); j++) {
                if (i != j) {
                    if (Math.sqrt(Math.pow(input.get(i).node.lat - input.get(j).node.lat, 2) + Math.pow(input.get(i).node.lon - input.get(j).node.lon, 2)) < threshold) {
//                        tooClose = true;
                        output[j] = (int) (output[i]);
                    } else {

                    }
                }
            }
//            if (tooClose == true) {
//                
////                checker.add(new LocationNode(input.get(i).id, input.get(i).lat, input.get(i).lon, input.get(i).myOrder));
//            }
        }

//        ArrayList<LocationNode> output = new ArrayList();
//
//        for (int i = 0; i < checker.size(); i++) {
//            output.add(getNearestNode((float) checker.get(i).lat, (float) checker.get(i).lon));
//        }
        return output;
    }

    public FacilityLocation[] initSchoolFacilities(float threshold, ArrayList<LocationNode> banedNodes) {
        ArrayList<LocationNodeSafegraph> schoolLocations = initSchoolLocations();
        Integer indices[] = labelMergedFacilities(schoolLocations, threshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);

        System.out.println("Initial number of schools: " + schoolLocations.size());
        ArrayList<LocationNodeSafegraph> schools = mergeFacilitiesWithIndices(schoolLocations, indicesList, indicesUniqueList, banedNodes);

        schoolLocationNodes = schools;

        int numFacilities = schools.size();
        FacilityLocation output[] = new FacilityLocation[numFacilities];
        Color colors[] = new Color[numFacilities];
        for (int i = 0; i < numFacilities; i++) {
            colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numFacilities - 1, 1, 1));

        }
        for (int i = 0; i < numFacilities; i++) {
            output[i] = new FacilityLocation(mainFParent, schools.get(i).node, schools.get(i).node.myWays[0], 20d);
            output[i].color = colors[i];
            output[i].isDecoyable = true;
            output[i].tollOff = 0.5;//IMP
        }
        System.out.println("schools generated: " + output.length);
        return output;
    }

    public FacilityLocation[] initSchoolFacilities(ArrayList<LocationNodeSafegraph> locations, float threshold, ArrayList<LocationNode> banedNodes) {
        Integer indices[] = labelMergedFacilities(locations, threshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);

        System.out.println("Initial number of schools: " + locations.size());
        ArrayList<LocationNodeSafegraph> schools = mergeFacilitiesWithIndices(locations, indicesList, indicesUniqueList, banedNodes);

        schoolLocationNodes = schools;

        int numFacilities = schools.size();
        FacilityLocation output[] = new FacilityLocation[numFacilities];
        Color colors[] = new Color[numFacilities];
        for (int i = 0; i < numFacilities; i++) {
            colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numFacilities - 1, 1, 1));

        }
        for (int i = 0; i < numFacilities; i++) {
            output[i] = new FacilityLocation(mainFParent, schools.get(i).node, schools.get(i).node.myWays[0], 20d);
            output[i].color = colors[i];
            output[i].isDecoyable = true;
            output[i].tollOff = 0.5;//IMP
        }
        System.out.println("schools generated: " + output.length);
        return output;
    }

    public FacilityLocation[] initRelFacilities(float threshold, ArrayList<LocationNode> banedNodes) {
        ArrayList<LocationNodeSafegraph> templeLocations = initTempleLocations();
        Integer indices[] = labelMergedFacilities(templeLocations, threshold);
        List<Integer> indicesRawList = Arrays.asList(indices);
        ArrayList<Integer> indicesList = new ArrayList(indicesRawList);
        LinkedHashSet<Integer> indicesUniqueSetHS = new LinkedHashSet(indicesList);
        ArrayList<Integer> indicesUniqueList = new ArrayList(indicesUniqueSetHS);

        System.out.println("Initial number of temples: " + templeLocations.size());
        ArrayList<LocationNodeSafegraph> temples = mergeFacilitiesWithIndices(templeLocations, indicesList, indicesUniqueList, banedNodes);

        templeLocationNodes = temples;

        int numFacilities = temples.size();
        FacilityLocation output[] = new FacilityLocation[numFacilities];
        Color colors[] = new Color[numFacilities];
        for (int i = 0; i < numFacilities; i++) {
            colors[i] = new Color(Color.HSBtoRGB((float) i / (float) numFacilities - 1, 1, 1));

        }
        for (int i = 0; i < numFacilities; i++) {
            output[i] = new FacilityLocation(mainFParent, temples.get(i).node, temples.get(i).node.myWays[0], 20d);
            output[i].color = colors[i];
            output[i].isDecoyable = true;
            output[i].tollOff = 0.5;//IMP
        }
        System.out.println("temples generated: " + output.length);
        return output;
    }

    public boolean isUniqueLocationNode(ArrayList<LocationNodeSafegraph> list, LocationNode input) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).node.lat == input.lat && list.get(i).node.lon == input.lon) {
                return false;
            }
        }
        return true;
    }

    public static LocationNode getNearestNode(MainFramePanel mainFParent, float collisionPositionx, float collisionPositiony, ArrayList<LocationNode> bannedNodes) {
        boolean isValidCollition = false;
        Grid outputGrid = new Grid(0, 0, 0, 0, 0, 0, "");
        for (int i = 0; i < mainFParent.allData.grid.length; i++) {
            for (int j = 0; j < mainFParent.allData.grid[0].length; j++) {
                if (collisionPositiony < mainFParent.allData.grid[i][j].max_y_val && collisionPositiony > mainFParent.allData.grid[i][j].min_y_val && collisionPositionx < mainFParent.allData.grid[i][j].max_x_val && collisionPositionx > mainFParent.allData.grid[i][j].min_x_val) {
                    if (mainFParent.allData.grid[i][j].myNodes.length > 0) {
                        isValidCollition = true;
//                                System.out.println("grid x: "+i);
//                                System.out.println("grid y: "+j);
                        outputGrid = mainFParent.allData.grid[i][j];
                        break;
                    }
                }
            }
            if (isValidCollition == true) {
                break;
            }
        }
        LocationNode nearestNode = null;
        if (bannedNodes != null) {
            if (bannedNodes.size() > 0) {
                if (isValidCollition == true) {
                    double leastDistance = Double.POSITIVE_INFINITY;
                    nearestNode = outputGrid.myNodes[0];
                    for (int i = 0; i < outputGrid.myNodes.length; i++) {
                        //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
                        if (outputGrid.myNodes[i] != null) {
                            double dist = Math.sqrt(Math.pow(collisionPositionx - outputGrid.myNodes[i].lat, 2) + Math.pow(collisionPositiony - outputGrid.myNodes[i].lon, 2));
                            if (dist < leastDistance) {
                                boolean isFound = false;
                                for (int h = 0; h < bannedNodes.size(); h++) {
                                    if (outputGrid.myNodes[i].id == bannedNodes.get(h).id) {
                                        isFound = true;
                                    }
                                }
                                if (isFound == false) {
                                    nearestNode = outputGrid.myNodes[i];
                                    leastDistance = dist;
                                }
                            }
                        }
                    }
                }
            } else {
                if (isValidCollition == true) {
                    double leastDistance = Double.POSITIVE_INFINITY;
                    nearestNode = outputGrid.myNodes[0];
                    for (int i = 0; i < outputGrid.myNodes.length; i++) {
                        //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
                        if (outputGrid.myNodes[i] != null) {
                            double dist = Math.sqrt(Math.pow(collisionPositionx - outputGrid.myNodes[i].lat, 2) + Math.pow(collisionPositiony - outputGrid.myNodes[i].lon, 2));
                            if (dist < leastDistance) {
                                nearestNode = outputGrid.myNodes[i];
                                leastDistance = dist;
                            }
                        }
                    }
                }
            }
        } else {
            if (isValidCollition == true) {
                double leastDistance = Double.POSITIVE_INFINITY;
                nearestNode = outputGrid.myNodes[0];
                for (int i = 0; i < outputGrid.myNodes.length; i++) {
                    //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
                    if (outputGrid.myNodes[i] != null) {
                        double dist = Math.sqrt(Math.pow(collisionPositionx - outputGrid.myNodes[i].lat, 2) + Math.pow(collisionPositiony - outputGrid.myNodes[i].lon, 2));
                        if (dist < leastDistance) {
                            nearestNode = outputGrid.myNodes[i];
                            leastDistance = dist;
                        }
                    }
                }
            }
        }

        return nearestNode;

    }

    public static LocationNode getExhaustiveNearestNode(MainFramePanel mainFParent, float lat, float lon) {
        LocationNode nearestNode = null;
        double leastDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < mainFParent.allData.all_Ways.length; i++) {
            if (!mainFParent.allData.all_Ways[i].type.equals("unclassified")) {
                for (int j = 0; j < mainFParent.allData.all_Ways[i].myNodes.length; j++) {
                    double dist = Math.sqrt(Math.pow(lat - mainFParent.allData.all_Ways[i].myNodes[j].lat, 2) + Math.pow(lon - mainFParent.allData.all_Ways[i].myNodes[j].lon, 2));
                    if (dist < leastDistance) {
                        leastDistance = dist;
                        nearestNode = mainFParent.allData.all_Ways[i].myNodes[j];
                    }
                }
            }
        }
        return nearestNode;
    }

    public class ParallelRouting extends ParallelProcessor {

        public ArrayList<PatternsRecordProcessed> records;
        GISLocationDialog myParent;
        double myData[];
        int myThreadIndex;

        public ParallelRouting(int threadIndex, GISLocationDialog parent, double[] data, int startIndex, int endIndex, int trafficLayerIndex, LocationNode home, ArrayList<LocationNodeSafegraph> shopMergedLocations) {
            super(parent, data, startIndex, endIndex);
            records = new ArrayList();
            myThreadIndex = threadIndex;
            myParent = parent;
            myData = data;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int h = startIndex; h < endIndex; h++) {
//                                    if (h != shopGroupIndex) {
                        Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, threadIndex);
                        routingToOthers.findPath(home, shopMergedLocations.get(h).node);
                        myData[h] = routingToOthers.pathDistance;
//                                    }
                    }
                }
            });
        }
    }

    public class ParallelRouting2 extends ParallelProcessor {

        public ArrayList<PatternsRecordProcessed> records;
        GISLocationDialog myParent;
        double myData[][];
        int myThreadIndex;
        Runnable myRunnable;

        LocationNode shopOriginalLocationNode;

        public ParallelRouting2(int threadIndex, GISLocationDialog parent, double[][] data, int startIndex, int endIndex, int trafficLayerIndex, ArrayList<LocationNodeSafegraph> shopMergedLocations, int cBGIndex, ArrayList<CensusBlockGroup> cbgs) {
            super(parent, data, startIndex, endIndex);
            records = new ArrayList();
            myThreadIndex = threadIndex;
            myParent = parent;
            myData = data;
            myRunnable = new Runnable() {
                @Override
                public void run() {
                    int counterSearch = 0;
                    ArrayList<LocationNode> bannedNodes = new ArrayList();
                    for (int h = startIndex; h < endIndex; h++) {
                        System.out.println("Internal parallel routing: " + ((float) (h - startIndex) / (endIndex - startIndex)) + " CBG index: " + cBGIndex);
                        Routing routing = new Routing(mainFParent.allData, trafficLayerIndex, threadIndex);

                        LocationNode home = getNearestNode(mainFParent, cbgs.get(cBGIndex).lon, cbgs.get(cBGIndex).lat, bannedNodes);

                        if (home == null) {
                            float collisionPositionx = cbgs.get(cBGIndex).lat;
                            float collisionPositiony = cbgs.get(cBGIndex).lon;
                            double leastDistance = Double.POSITIVE_INFINITY;
                            LocationNode nearestNode = mainFParent.allData.all_Nodes[0];
                            for (int g = 0; g < mainFParent.allData.all_Nodes.length; g++) {
                                //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
                                double dist = Math.sqrt(Math.pow(collisionPositionx - mainFParent.allData.all_Nodes[g].lat, 2) + Math.pow(collisionPositiony - mainFParent.allData.all_Nodes[g].lon, 2));
                                if (dist < leastDistance) {
                                    for (int m = 0; m < bannedNodes.size(); m++) {
                                        if (mainFParent.allData.all_Nodes[g].id != bannedNodes.get(m).id) {
                                            nearestNode = mainFParent.allData.all_Nodes[g];
                                            leastDistance = dist;
                                        }
                                    }

                                }
                            }
                            home = nearestNode;
                        }

//                        if(home==null || shopMergedLocations.get(h).node==null){
//                            System.out.println("PROBLEM!!!!!!!!!!!!!");
//                        }
                        routing.findPath(home, shopMergedLocations.get(h).node);
                        if (routing.pathDistance == Double.POSITIVE_INFINITY && counterSearch < 100) {
                            bannedNodes.add(home);
                            bannedNodes.add(shopMergedLocations.get(h).node);

                            if (counterSearch == 0) {
                                shopOriginalLocationNode = shopMergedLocations.get(h).node;
                            }

                            LocationNode newShopLocation = getNearestNode(mainFParent, (float) (shopMergedLocations.get(h).node.lat), (float) (shopMergedLocations.get(h).node.lon), bannedNodes);
                            shopMergedLocations.get(h).node = newShopLocation;
                            h = h - 1;
                            counterSearch = counterSearch + 1;
                        } else {
                            CBGToShopDistances[cBGIndex][h] = routing.pathDistance;
                            if (counterSearch > 0) {
                                shopMergedLocations.get(h).node = shopOriginalLocationNode;
                            }
                        }

//                        Routing routing = new Routing(mainFParent.allData, trafficLayerIndex, threadIndex);
//
//                        LocationNode home = getNearestNode(cbgs.get(cBGIndex).lon, cbgs.get(cBGIndex).lat, null);
//
//                        if (home == null) {
//                            float collisionPositionx = cbgs.get(cBGIndex).lat;
//                            float collisionPositiony = cbgs.get(cBGIndex).lon;
//                            double leastDistance = Double.POSITIVE_INFINITY;
//                            LocationNode nearestNode = mainFParent.allData.all_Nodes[0];
//                            for (int g = 0; g < mainFParent.allData.all_Nodes.length; g++) {
//                                //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
//                                double dist = Math.sqrt(Math.pow(collisionPositionx - mainFParent.allData.all_Nodes[g].lat, 2) + Math.pow(collisionPositiony - mainFParent.allData.all_Nodes[g].lon, 2));
//                                if (dist < leastDistance) {
//                                    nearestNode = mainFParent.allData.all_Nodes[g];
//                                    leastDistance = dist;
//                                }
//                            }
//                            home = nearestNode;
//                        }
//                        routing.findPath(home, shopMergedLocations.get(h).node);
//                        myData[cBGIndex][h] = routing.pathDistance;
                    }
                }
            };
            myThread = new Thread(myRunnable);
        }

        public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
            calls.add(Executors.callable(myRunnable));
        }
    }

    public class ParallelRoutingBrent extends ParallelProcessor {

        double myData[];
        int myThreadIndex;

        public double firstDist = Double.POSITIVE_INFINITY;
        public double secondDist = Double.POSITIVE_INFINITY;
        public double thirdDist = Double.POSITIVE_INFINITY;
        public int firstGroupIndex = -1;
        public int secondGroupIndex = -1;
        public int thirdGroupIndex = -1;

        public ParallelRoutingBrent(int threadIndex, GISLocationDialog parent, double[] data, int startIndex, int endIndex, int trafficLayerIndex, LocationNode node, ArrayList<LocationNodeSafegraph> locations) {
            super(parent, data, startIndex, endIndex);
            myThreadIndex = threadIndex;
            myParent = parent;
            myData = data;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int h = startIndex; h < endIndex; h++) {
                        try {
                            Routing routing = new Routing(mainFParent.allData, trafficLayerIndex, threadIndex);

                            routing.findPath(node, locations.get(h).node);
                            if (routing.pathDistance < firstDist) {
                                thirdDist = secondDist;
                                thirdGroupIndex = secondGroupIndex;
                                secondDist = firstDist;
                                secondGroupIndex = firstGroupIndex;
                                firstDist = routing.pathDistance;
                                firstGroupIndex = h;
                            } else if (routing.pathDistance < secondDist) {
                                thirdDist = secondDist;
                                thirdGroupIndex = secondGroupIndex;
                                secondDist = routing.pathDistance;
                                secondGroupIndex = h;
                            } else if (routing.pathDistance < thirdDist) {
                                thirdDist = routing.pathDistance;
                                thirdGroupIndex = h;
                            }
                        } catch (Exception ex) {

                        }
                    }

//                    for (int h = startIndex; h < endIndex; h++) {
////                                    if (h != shopGroupIndex) {
//                        Routing routingToOthers = new Routing(mainFParent.allData, trafficLayerIndex, threadIndex);
//                        routingToOthers.findPath(home, shopMergedLocations.get(h).node);
//                        myData[h] = routingToOthers.pathDistance;
////                                    }
//                    }
                }
            });
        }
    }

    public class ParallelLocationNodeCBGIdConnector extends ParallelProcessor {

        public ArrayList<PatternsRecordProcessed> records;
        GISLocationDialog myParent;
        Long myData[];
        int myThreadIndex;

        public ParallelLocationNodeCBGIdConnector(int threadIndex, GISLocationDialog parent, Long cBGForNodes[], int startIndex, int endIndex) {
            super(parent, cBGForNodes, startIndex, endIndex);
            records = new ArrayList();
            myThreadIndex = threadIndex;
            myParent = parent;
            myData = cBGForNodes;
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {

//                    int counter = 0;
                    boolean isCBGFound = false;
                    for (int u = startIndex; u < endIndex; u++) {
                        isCBGFound = false;
                        for (int i = 0; i < myParent.myParent.mainModel.allGISData.countries.size(); i++) {
                            for (int j = 0; j < myParent.myParent.mainModel.allGISData.countries.get(i).states.size(); j++) {
                                for (int k = 0; k < myParent.myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.size(); k++) {
                                    for (int m = 0; m < myParent.myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.size(); m++) {
                                        for (int v = 0; v < myParent.myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.size(); v++) {
                                            for (int y = 0; y < myParent.myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v).shape.size(); y++) {
                                                GeometryFactory geomFactory = new GeometryFactory();
                                                Point point = geomFactory.createPoint(new Coordinate(mainFParent.allData.all_Nodes[u].lon, mainFParent.allData.all_Nodes[u].lat));
                                                if (myParent.myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v).shape.get(y).covers(point) == true) {
                                                    if (myParent.myParent.mainModel.allGISData.isInScope(myParent.myParent.mainModel.ABM.studyScopeGeography, myParent.myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v)) == true) {
                                                        myData[u] = myParent.myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v).id;
                                                        isCBGFound = true;
                                                        break;
                                                    } else {
                                                        myData[u] = Long.valueOf(-1);
                                                        isCBGFound = true;
                                                        break;
                                                    }
                                                    //censusBlockGroupsEncounteredList.add(myParent.myParent.mainModel.allGISData.countries.get(i).states.get(j).counties.get(k).censusTracts.get(m).censusBlocks.get(v).id);

                                                }
                                            }
                                            if (isCBGFound == true) {
                                                break;
                                            }
                                        }
                                        if (isCBGFound == true) {
                                            break;
                                        }
                                    }
                                    if (isCBGFound == true) {
                                        break;
                                    }
                                }
                                if (isCBGFound == true) {
                                    break;
                                }
                            }
                            if (isCBGFound == true) {
                                break;
                            }
                        }
                        if (isCBGFound == false) {
                            System.out.println("SEVERE PROBLEM! LOCATION NODE HAS NO CBG!");
                        }
//                        System.out.println(counter);
//                        counter = counter + 1;
                    }
                }
            });
        }
    }

    private static boolean isInside(float lat, float lon, Polygon polygon) {
        Geometry point = new GeometryFactory().createPoint(new Coordinate(lat, lon));
        return polygon.contains(point);
    }

    private void removeVDCell(int vdIndex, int vDLayerIndex) {
        String[] tempCategories = new String[((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories.length - 1];
        Color[] tempColors = new Color[((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories.length - 1];
        double[] tempValues = new double[((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories.length - 1];
        for (int y = 0; y < ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories.length; y++) {
            if (y < vdIndex) {
                tempCategories[y] = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories[y];
                tempColors[y] = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).colors[y];
                tempValues[y] = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).values[y];
            } else if (y > vdIndex) {
                tempCategories[y - 1] = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories[y];
                tempColors[y - 1] = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).colors[y];
                tempValues[y - 1] = ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).values[y];
            }
        }
        ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).categories = tempCategories;
        ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).colors = tempColors;
        ((LayerDefinition) (mainFParent.allData.all_Layers.get(vDLayerIndex))).values = tempValues;
        for (int i = 0; i < mainFParent.allData.all_Nodes.length; i++) {
            short val = ((short[]) (mainFParent.allData.all_Nodes[i].layers.get(vDLayerIndex)))[0];
            if (val > vdIndex) {
                short[] edittedVal = new short[1];
                edittedVal[0] = (short) (val - 1);
                mainFParent.allData.all_Nodes[i].layers.set(vDLayerIndex, edittedVal);
            }
        }
    }

    public static HashMap<CensusBlockGroup, Integer> getHashNumNodeForCBG(AllData allData, AllGISData allGISData, int vdIndex, int vDLayerIndex, int cBGLayerIndex) {
        HashMap<CensusBlockGroup, Integer> cBGNumNodesHashMap = new HashMap();
        for (int i = 0; i < allData.all_Nodes.length; i++) {
            if (((short[]) (allData.all_Nodes[i].layers.get(vDLayerIndex)))[0] - 1 == vdIndex) {
                Double value = Double.valueOf(Math.round(((LayerDefinition) (allData.all_Layers.get(cBGLayerIndex))).values[((short[]) (allData.all_Nodes[i].layers.get(cBGLayerIndex)))[0] - 1]));
                CensusBlockGroup cBG = allGISData.findCensusBlockGroup(value.longValue());
                if (cBG != null) {
                    if (cBGNumNodesHashMap.containsKey(cBG)) {
                        cBGNumNodesHashMap.put(cBG, cBGNumNodesHashMap.get(cBG) + 1);
                    } else {
                        cBGNumNodesHashMap.put(cBG, 1);
                    }
                }
            }
        }
        return cBGNumNodesHashMap;
    }

    public static HashMap<CensusBlockGroup, Integer> getHashNumNodeForCluster(AllData allData, AllGISData allGISData, int vdIndex, int[][] clustImg, int cBGLayerIndex) {
        VectorToPolygon vp = new VectorToPolygon();
        vp.setScaleFactors(allData);
        vp.imgWidth = clustImg[0].length;
        vp.imgHeight = clustImg.length;
        HashMap<CensusBlockGroup, Integer> cBGNumNodesHashMap = new HashMap();
        for (int i = 0; i < allData.all_Nodes.length; i++) {
            int[] imgPCl = vp.vectorToImage(allData.all_Nodes[i].lon, allData.all_Nodes[i].lat, clustImg[0].length, clustImg.length);
            int cLIndex = clustImg[imgPCl[1]][imgPCl[0]];
            if (cLIndex == vdIndex) {
                Double value = Double.valueOf(Math.round(((LayerDefinition) (allData.all_Layers.get(cBGLayerIndex))).values[((short[]) (allData.all_Nodes[i].layers.get(cBGLayerIndex)))[0] - 1]));
                CensusBlockGroup cBG = allGISData.findCensusBlockGroup(value.longValue());
                if (cBG != null) {
                    if (cBGNumNodesHashMap.containsKey(cBG)) {
                        cBGNumNodesHashMap.put(cBG, cBGNumNodesHashMap.get(cBG) + 1);
                    } else {
                        cBGNumNodesHashMap.put(cBG, 1);
                    }
                }
            }
        }
        return cBGNumNodesHashMap;
    }

    private void writeLoadAggregatedOrderData(int numVisitsToNearestOrder[], double distanceByNearestOrder[]) {
        File file = new File(myParent.mainModel.ABM.studyScope + "_ShopNumVisitsByOrder.csv");
        if (file.exists() == false) {
            ArrayList<String[]> data = new ArrayList();
            String[] row = new String[numVisitsToNearestOrder.length];
            for (int j = 0; j < numVisitsToNearestOrder.length; j++) {
                row[j] = String.valueOf(numVisitsToNearestOrder[j]);
            }
            data.add(row);
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(myParent.mainModel.ABM.studyScope + "_ShopNumVisitsByOrder.csv"));
                writer.writeAll(data);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("SHOP NUMBER OF VISITS BY ORDER FILE EXISTS");
            try {
                CSVReader csvReader = new CSVReader(new FileReader(myParent.mainModel.ABM.studyScope + "_ShopNumVisitsByOrder.csv"));
                List<String[]> list = new ArrayList<>();
                list = csvReader.readAll();
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list.get(i).length; j++) {
                        numVisitsToNearestOrder[j] = Integer.valueOf(list.get(i)[j]);
                    }
                }
                csvReader.close();
                System.out.println("SHOP NUMBER OF VISITS BY ORDER SUCCESSFULLY READ");
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("SHOP NUMBER OF VISITS BY ORDER FAILED TO READ");
            } catch (CsvException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("SHOP NUMBER OF VISITS BY ORDER FAILED TO READ");
            }
        }

        file = new File(myParent.mainModel.ABM.studyScope + "_DistancesByOrder.csv");
        if (file.exists() == false) {
            ArrayList<String[]> data = new ArrayList();
            String[] row = new String[distanceByNearestOrder.length];
            for (int j = 0; j < distanceByNearestOrder.length; j++) {
                row[j] = String.valueOf(distanceByNearestOrder[j]);
            }
            data.add(row);
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(myParent.mainModel.ABM.studyScope + "_DistancesByOrder.csv"));
                writer.writeAll(data);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("DISTANCE BY ORDER FILE EXISTS");
            try {
                CSVReader csvReader = new CSVReader(new FileReader(myParent.mainModel.ABM.studyScope + "_DistancesByOrder.csv"));
                List<String[]> list = new ArrayList<>();
                list = csvReader.readAll();
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list.get(i).length; j++) {
                        distanceByNearestOrder[j] = Double.valueOf(list.get(i)[j]);
                    }
                }
                csvReader.close();
                System.out.println("DISTANCE BY ORDER SUCCESSFULLY READ");
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("DISTANCE BY ORDER FAILED TO READ");
            } catch (CsvException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("DISTANCE BY ORDER FAILED TO READ");
            }
        }
    }

    private void writeLoadDetailedOrderData(int numVisitsToNearestOrderByCBGShop[][], double distanceByNearestOrderByCBGShop[][]) {
        File file = new File(myParent.mainModel.ABM.studyScope + "_ShopNumVisitsByOrderCBG.csv");
        if (file.exists() == false) {
            ArrayList<String[]> data = new ArrayList();
            for (int i = 0; i < numVisitsToNearestOrderByCBGShop.length; i++) {
                String[] row = new String[numVisitsToNearestOrderByCBGShop[i].length];
                for (int j = 0; j < numVisitsToNearestOrderByCBGShop[i].length; j++) {
                    row[j] = String.valueOf(numVisitsToNearestOrderByCBGShop[i][j]);
                }
                data.add(row);
            }
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(myParent.mainModel.ABM.studyScope + "_ShopNumVisitsByOrderCBG.csv"));
                writer.writeAll(data);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("SHOP NUMBER OF VISITS BY ORDER BY CBG FILE EXISTS");
            try {
                CSVReader csvReader = new CSVReader(new FileReader(myParent.mainModel.ABM.studyScope + "_ShopNumVisitsByOrderCBG.csv"));
                List<String[]> list = new ArrayList<>();
                list = csvReader.readAll();
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list.get(i).length; j++) {
                        numVisitsToNearestOrderByCBGShop[i][j] = Integer.valueOf(list.get(i)[j]);
                    }
                }
                csvReader.close();
                System.out.println("SHOP NUMBER OF VISITS BY ORDER BY CBG SUCCESSFULLY READ");
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("SHOP NUMBER OF VISITS BY ORDER BY CBG FAILED TO READ");
            } catch (CsvException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("SHOP NUMBER OF VISITS BY ORDER BY CBG FAILED TO READ");
            }
        }

        file = new File(myParent.mainModel.ABM.studyScope + "_DistancesByOrderCBG.csv");
        if (file.exists() == false) {
            ArrayList<String[]> data = new ArrayList();
            for (int i = 0; i < distanceByNearestOrderByCBGShop.length; i++) {
                String[] row = new String[distanceByNearestOrderByCBGShop[i].length];
                for (int j = 0; j < distanceByNearestOrderByCBGShop[i].length; j++) {
                    row[j] = String.valueOf(distanceByNearestOrderByCBGShop[i][j]);
                }
                data.add(row);
            }
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(myParent.mainModel.ABM.studyScope + "_DistancesByOrderCBG.csv"));
                writer.writeAll(data);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("DISTANCE BY ORDER FILE BY CBG EXISTS");
            try {
                CSVReader csvReader = new CSVReader(new FileReader(myParent.mainModel.ABM.studyScope + "_DistancesByOrderCBG.csv"));
                List<String[]> list = new ArrayList<>();
                list = csvReader.readAll();
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list.get(i).length; j++) {
                        distanceByNearestOrderByCBGShop[i][j] = Double.valueOf(list.get(i)[j]);
                    }
                }
                csvReader.close();
                System.out.println("DISTANCE BY ORDER BY CBG SUCCESSFULLY READ");
            } catch (IOException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("DISTANCE BY ORDER BY CBG FAILED TO READ");
            } catch (CsvException ex) {
                Logger.getLogger(GISLocationDialog.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("DISTANCE BY ORDER BY CBG FAILED TO READ");
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner3;
    // End of variables declaration//GEN-END:variables
}
