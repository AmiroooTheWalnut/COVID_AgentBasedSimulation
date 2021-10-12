/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.Settings;

import COVID_AgentBasedSimulation.Model.DatasetTemplate;
import COVID_AgentBasedSimulation.Model.MainModel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class AgentBehaviorDialog extends javax.swing.JDialog {

    private int behaviorEditIndex;
    MainModel myMainModel;

    RTextScrollPane construcorJavaScrollPane;
    RTextScrollPane construcorPythonScrollPane;

    RTextScrollPane behaviorJavaScrollPane;
    RTextScrollPane behaviorPythonScrollPane;

    RTextScrollPane destructorJavaScrollPane;
    RTextScrollPane destructorPythonScrollPane;

    RSyntaxTextArea construcorJavaTextArea;
    RSyntaxTextArea construcorPythonTextArea;

    RSyntaxTextArea behaviorJavaTextArea;
    RSyntaxTextArea behaviorPythonTextArea;

    RSyntaxTextArea destructorJavaTextArea;
    RSyntaxTextArea destructorPythonTextArea;

    /**
     * Creates new form AgentBehaviorDialog
     */
    public AgentBehaviorDialog(MainModel mainModel, java.awt.Frame parent, boolean modal, int passed_behaviorEditIndex) {
        super(parent, modal);
        initComponents();
        expandAllNodes(jTree1);

        DefaultTreeModel modelDatasets = (DefaultTreeModel) jTree3.getModel();
        DefaultMutableTreeNode rootDatasets = (DefaultMutableTreeNode) modelDatasets.getRoot();
        rootDatasets.removeAllChildren();
        modelDatasets.reload();

        rootDatasets = new DefaultMutableTreeNode("Datasets");

        modelDatasets.setRoot(rootDatasets);

//        DefaultMutableTreeNode javaNode = new DefaultMutableTreeNode("Java");
        DefaultMutableTreeNode safegraphNode = new DefaultMutableTreeNode("Safegraph");
        DefaultMutableTreeNode gISDataNode = new DefaultMutableTreeNode("GISData");
        
        DefaultMutableTreeNode covidCssJhuNode = new DefaultMutableTreeNode("CasesByCountyCsseJhu");

        rootDatasets.add(new DefaultMutableTreeNode("Object"));
        rootDatasets.add(new DefaultMutableTreeNode("Integer"));
        rootDatasets.add(new DefaultMutableTreeNode("Double"));
        rootDatasets.add(new DefaultMutableTreeNode("String"));
        rootDatasets.add(new DefaultMutableTreeNode("Boolean"));
        rootDatasets.add(new DefaultMutableTreeNode("ArrayList"));

//        root.add(javaNode);
//        model.reload(root);
        setDatasetTree(mainModel.safegraph.datasetTemplate, safegraphNode);
        setDatasetTree(mainModel.allGISData.datasetTemplate, gISDataNode);
        setDatasetTree(mainModel.covidCsseJhu.datasetTemplate, covidCssJhuNode);

        rootDatasets.add(safegraphNode);
//        model.reload(root);
        rootDatasets.add(gISDataNode);
        rootDatasets.add(covidCssJhuNode);
        modelDatasets.reload(rootDatasets);

        expandAllNodes(jTree3);

        myMainModel = mainModel;
        behaviorEditIndex = passed_behaviorEditIndex;
        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree2.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree3.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree4.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        DefaultTreeModel modelCurrentAgent = (DefaultTreeModel) jTree2.getModel();
        DefaultMutableTreeNode rootCurrentAgent = (DefaultMutableTreeNode) modelCurrentAgent.getRoot();
        rootCurrentAgent.removeAllChildren();
        modelCurrentAgent.reload();

        rootCurrentAgent = new DefaultMutableTreeNode("Properties");

        for (int i = 0; i < myMainModel.ABM.agentTemplates.get(behaviorEditIndex).agentProperties.size(); i++) {
            rootCurrentAgent.add(new DefaultMutableTreeNode(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).agentProperties.get(i).propertyName + "[" + myMainModel.ABM.agentTemplates.get(behaviorEditIndex).agentProperties.get(i).propertyType + "]"));
        }

        modelCurrentAgent.setRoot(rootCurrentAgent);

        expandAllNodes(jTree2);

        DefaultTreeModel modelAgentTemplates = (DefaultTreeModel) jTree4.getModel();
        DefaultMutableTreeNode rootAgentTemplates = (DefaultMutableTreeNode) modelAgentTemplates.getRoot();
        rootAgentTemplates.removeAllChildren();
        modelAgentTemplates.reload();

        rootAgentTemplates = new DefaultMutableTreeNode("All Properties");

        for (int i = 0; i < myMainModel.ABM.agentTemplates.size(); i++) {
            DefaultMutableTreeNode temp = new DefaultMutableTreeNode(myMainModel.ABM.agentTemplates.get(i).agentTypeName);
            rootAgentTemplates.add(temp);
            for (int j = 0; j < myMainModel.ABM.agentTemplates.get(i).agentProperties.size(); j++) {
                temp.add(new DefaultMutableTreeNode(myMainModel.ABM.agentTemplates.get(i).agentProperties.get(j).propertyName + "[" + myMainModel.ABM.agentTemplates.get(i).agentProperties.get(j).propertyType + "]"));
            }
        }

        modelAgentTemplates.setRoot(rootAgentTemplates);

        expandAllNodes(jTree4);

        construcorJavaTextArea = new RSyntaxTextArea(20, 60);
        construcorJavaTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        construcorJavaTextArea.setCodeFoldingEnabled(true);
        construcorJavaScrollPane = new RTextScrollPane(construcorJavaTextArea);

        construcorPythonTextArea = new RSyntaxTextArea(20, 60);
        construcorPythonTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        construcorPythonTextArea.setCodeFoldingEnabled(true);
        construcorPythonScrollPane = new RTextScrollPane(construcorPythonTextArea);

        if (myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive == true) {
            construcorJavaTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.javaScript.script);
            jPanel2.removeAll();
            jPanel2.add(construcorJavaScrollPane);
            jPanel2.invalidate();
            jPanel2.validate();
            jPanel2.repaint();
        } else {
            construcorPythonTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.pythonScript.script);
            jPanel2.removeAll();
            jPanel2.add(construcorPythonScrollPane);
            jPanel2.invalidate();
            jPanel2.validate();
            jPanel2.repaint();
            jRadioButton2.setSelected(true);
        }

        behaviorJavaTextArea = new RSyntaxTextArea(20, 60);
        behaviorJavaTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        behaviorJavaTextArea.setCodeFoldingEnabled(true);
        behaviorJavaScrollPane = new RTextScrollPane(behaviorJavaTextArea);

        behaviorPythonTextArea = new RSyntaxTextArea(20, 60);
        behaviorPythonTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        behaviorPythonTextArea.setCodeFoldingEnabled(true);
        behaviorPythonScrollPane = new RTextScrollPane(behaviorPythonTextArea);

        if (myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive == true) {
            behaviorJavaTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.javaScript.script);
            jPanel4.removeAll();
            jPanel4.add(behaviorJavaScrollPane);
            jPanel4.invalidate();
            jPanel4.validate();
            jPanel4.repaint();
        } else {
            behaviorPythonTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.pythonScript.script);
            jPanel4.removeAll();
            jPanel4.add(behaviorPythonScrollPane);
            jPanel4.invalidate();
            jPanel4.validate();
            jPanel4.repaint();
            jRadioButton4.setSelected(true);
        }

        destructorJavaTextArea = new RSyntaxTextArea(20, 60);
        destructorJavaTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        destructorJavaTextArea.setCodeFoldingEnabled(true);
        destructorJavaScrollPane = new RTextScrollPane(destructorJavaTextArea);

        destructorPythonTextArea = new RSyntaxTextArea(20, 60);
        destructorPythonTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        destructorPythonTextArea.setCodeFoldingEnabled(true);
        destructorPythonScrollPane = new RTextScrollPane(destructorPythonTextArea);

        if (myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive == true) {
            destructorJavaTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.javaScript.script);
            jPanel5.removeAll();
            jPanel5.add(destructorJavaScrollPane);
            jPanel5.invalidate();
            jPanel5.validate();
            jPanel5.repaint();
        } else {
            destructorPythonTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.pythonScript.script);
            jPanel5.removeAll();
            jPanel5.add(destructorPythonScrollPane);
            jPanel5.invalidate();
            jPanel5.validate();
            jPanel5.repaint();
            jRadioButton6.setSelected(true);
        }
        
        jCheckBox2.setSelected(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).canRunBehaviorParallel);
    }

    private void expandAllNodes(JTree tree) {
        int j = tree.getRowCount();
        int i = 0;
        while (i < j) {
            tree.expandRow(i);
            i += 1;
            j = tree.getRowCount();
        }
    }

    public void setDatasetTree(DatasetTemplate datasetTemplate, DefaultMutableTreeNode treeNode) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(datasetTemplate.name);
        treeNode.add(root);
        if (datasetTemplate.recordTemplates != null) {
            for (int i = 0; i < datasetTemplate.recordTemplates.size(); i++) {
                root.add(new DefaultMutableTreeNode(datasetTemplate.recordTemplates.get(i).name));
            }
        }
        if (datasetTemplate.innerDatasetTemplates != null) {
            for (int j = 0; j < datasetTemplate.innerDatasetTemplates.size(); j++) {
                setDatasetTree(datasetTemplate.innerDatasetTemplates.get(j), root);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree2 = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree3 = new javax.swing.JTree();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTree4 = new javax.swing.JTree();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel11 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jPanel12 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel13 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Current agent properties"));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree2.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane2.setViewportView(jTree2);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Current agent properties", jPanel8);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("All variables"));

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Variables and function");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("currentAgent");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("[Object]=getPropertyValue(String propertyName)");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("[]=setPropertyValue(String propertyName, Object value)");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("rootModel[MainModel]");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("safegraph[refer to data types]");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("allGISData[refer to data types]");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("ABM");
        javax.swing.tree.DefaultMutableTreeNode treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("rootAgent[Agent]");
        javax.swing.tree.DefaultMutableTreeNode treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("[Object]=getPropertyValue(String propertyName)");
        treeNode4.add(treeNode5);
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("[]=setPropertyValue(String propertyName, Object value)");
        treeNode4.add(treeNode5);
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("agents[ArrayList<Agent>]");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("startTime[ZonedDateTime]");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("currentTime[ZonedDateTime]");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("endTime[ZonedDateTime]");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("[Agent]=makeAgent(String agentTemplate)");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("[]=removeAgent(Agent agent)");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.setLargeModel(true);
        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("All variables", jPanel1);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("All data types"));

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree3.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane3.setViewportView(jTree3);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("All data types", jPanel6);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("All agent definitions"));

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree4.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane4.setViewportView(jTree4);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("All agent definitions", jPanel7);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Constructor"));
        jPanel2.setPreferredSize(new java.awt.Dimension(600, 300));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Java");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Python");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 896, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Constructor", jPanel11);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Behavior"));
        jPanel4.setPreferredSize(new java.awt.Dimension(600, 300));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setSelected(true);
        jRadioButton3.setText("Java");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setText("Python");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("Can run parallel?");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton3)
                            .addComponent(jRadioButton4)))
                    .addComponent(jCheckBox2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Behavior", jPanel12);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Destructor"));
        jPanel5.setPreferredSize(new java.awt.Dimension(600, 300));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        buttonGroup3.add(jRadioButton5);
        jRadioButton5.setSelected(true);
        jRadioButton5.setText("Java");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioButton6);
        jRadioButton6.setText("Python");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton5)
                    .addComponent(jRadioButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 896, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Destructor", jPanel13);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addComponent(jTabbedPane2)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        if (jRadioButton1.isSelected() == true && myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive == false) {
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive = true;
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.pythonScript.script = construcorPythonTextArea.getText();
            construcorJavaTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.javaScript.script);
            jPanel2.removeAll();
            jPanel2.add(construcorJavaScrollPane);
            jPanel2.invalidate();
            jPanel2.validate();
            jPanel2.repaint();
        }
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        if (jRadioButton2.isSelected() == true && myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive == true) {
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive = false;
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.javaScript.script = construcorJavaTextArea.getText();
            construcorPythonTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).constructor.pythonScript.script);
            jPanel2.removeAll();
            jPanel2.add(construcorPythonScrollPane);
            jPanel2.invalidate();
            jPanel2.validate();
            jPanel2.repaint();
        }
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        if (jRadioButton3.isSelected() == true && myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive == false) {
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive = true;
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.pythonScript.script = behaviorPythonTextArea.getText();
            behaviorJavaTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.javaScript.script);
            jPanel4.removeAll();
            jPanel4.add(behaviorJavaScrollPane);
            jPanel4.invalidate();
            jPanel4.validate();
            jPanel4.repaint();
        }
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        if (jRadioButton4.isSelected() == true && myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive == true) {
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive = false;
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.javaScript.script = behaviorJavaTextArea.getText();
            behaviorPythonTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).behavior.pythonScript.script);
            jPanel4.removeAll();
            jPanel4.add(behaviorPythonScrollPane);
            jPanel4.invalidate();
            jPanel4.validate();
            jPanel4.repaint();
        }
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
        if (jRadioButton5.isSelected() == true && myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive == false) {
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive = true;
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.pythonScript.script = destructorPythonTextArea.getText();
            destructorJavaTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.javaScript.script);
            jPanel5.removeAll();
            jPanel5.add(destructorJavaScrollPane);
            jPanel5.invalidate();
            jPanel5.validate();
            jPanel5.repaint();
        }
    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed
        if (jRadioButton6.isSelected() == true && myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive == true) {
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive = false;
            myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.javaScript.script = destructorJavaTextArea.getText();
            destructorPythonTextArea.setText(myMainModel.ABM.agentTemplates.get(behaviorEditIndex).destructor.pythonScript.script);
            jPanel5.removeAll();
            jPanel5.add(destructorPythonScrollPane);
            jPanel5.invalidate();
            jPanel5.validate();
            jPanel5.repaint();
        }
    }//GEN-LAST:event_jRadioButton6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTree jTree1;
    private javax.swing.JTree jTree2;
    private javax.swing.JTree jTree3;
    private javax.swing.JTree jTree4;
    // End of variables declaration//GEN-END:variables
}
