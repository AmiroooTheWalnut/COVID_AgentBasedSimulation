/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentPropertyTemplate;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentTemplate;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.BehaviorScript;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.JavaScript;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.PythonScript;
import COVID_AgentBasedSimulation.Model.DatasetTemplate;
import COVID_AgentBasedSimulation.Model.MainModel;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author user
 */
public class AgentTemplateDialog extends javax.swing.JDialog {

    MainModel myMainModel;
    AgentTemplate workingAgentTemplate;
    SimulatorSettingsDialog myParent;
    AgentTemplatePanel selectedAgentTemplatePanel;
    AgentPropertyTemplate selectedAgentPropertyTemplate;

    private int globalRowIndex = 0;
    private TreePath expandablePath;
    private int templateEditIndex;
    private boolean isEdit;

    /**
     * Creates new form AgentTemplateDialog
     */
    public AgentTemplateDialog(MainModel mainModel, SimulatorSettingsDialog simulatorSettingsDialog, java.awt.Frame parent, boolean modal, int passed_templateEditIndex) {
        super(parent, modal);
        initComponents();
        myParent = simulatorSettingsDialog;
        myMainModel = mainModel;
        templateEditIndex = passed_templateEditIndex;
        if (templateEditIndex > -1) {
            isEdit = true;
        } else {
            isEdit = false;
        }
        if (templateEditIndex == 0) {
            jTextField1.setEnabled(false);
        } else {
            jTextField1.setEnabled(true);
        }
        if (isEdit == false) {
            workingAgentTemplate = new AgentTemplate();
        }

        DefaultTreeModel model = (DefaultTreeModel) jTree1.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        model.reload();

        root = new DefaultMutableTreeNode("Datasets");

        model.setRoot(root);

//        DefaultMutableTreeNode javaNode = new DefaultMutableTreeNode("Java");
        DefaultMutableTreeNode safegraphNode = new DefaultMutableTreeNode("Safegraph");
        DefaultMutableTreeNode gISDataNode = new DefaultMutableTreeNode("GISData");

        root.add(new DefaultMutableTreeNode("Object"));
        root.add(new DefaultMutableTreeNode("Integer"));
        root.add(new DefaultMutableTreeNode("Double"));
        root.add(new DefaultMutableTreeNode("String"));
        root.add(new DefaultMutableTreeNode("ArrayList"));

//        root.add(javaNode);
//        model.reload(root);
        setDatasetTree(mainModel.safegraph.datasetTemplate, safegraphNode);
        setDatasetTree(mainModel.allGISData.datasetTemplate, gISDataNode);

        root.add(safegraphNode);
//        model.reload(root);
        root.add(gISDataNode);
        model.reload(root);

        expandAllNodes(jTree1);

        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                System.out.println("EDITTED STRING111");
                workingAgentTemplate.agentTypeName = jTextField1.getText();
            }
        });
        jTree1.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    if (selectedNode.getLevel() == 1) {
                        if (selectedNode.isLeaf() == true) {

                        }
                    } else if (selectedNode.getLevel() > 1) {

                    }
                }
            }
        });
        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public void updatePropertiestTemplateList() {
        jTextField1.setText(workingAgentTemplate.agentTypeName);
        for (int i = 0; i < workingAgentTemplate.agentProperties.size(); i++) {
            AgentTemplatePanel temp = new AgentTemplatePanel(myMainModel, this, workingAgentTemplate.agentProperties.get(i), i);
            temp.jTextField1.setText(workingAgentTemplate.agentProperties.get(i).propertyName);
            temp.jLabel2.setText(workingAgentTemplate.agentProperties.get(i).propertyType);
            jPanel1.add(temp);
            jPanel1.repaint();
            jPanel1.invalidate();
            jPanel1.revalidate();
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

    private void expandAllNodes(JTree tree) {
        int j = tree.getRowCount();
        int i = 0;
        while (i < j) {
            tree.expandRow(i);
            i += 1;
            j = tree.getRowCount();
        }
    }

    public void setVariableSelection(String input) {
        DefaultTreeModel model = (DefaultTreeModel) jTree1.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        String processedInput = input;
        if (input.contains("(")) {
            String variableName[] = input.split("\\(");
            processedInput = variableName[0];
        }
        expandablePath = new TreePath(root);
        globalRowIndex = 0;
        findVariableNode(processedInput, root, false);
//        jTree1.expandPath(expandablePath);
        jTree1.setSelectionRow(globalRowIndex);
    }

    public boolean findVariableNode(String input, DefaultMutableTreeNode root, boolean hasReached) {
        for (int i = 0; i < root.getChildCount(); i++) {
            globalRowIndex = globalRowIndex + 1;
            if (root.getChildAt(i).isLeaf()) {
                if (((String) ((DefaultMutableTreeNode) root.getChildAt(i)).getUserObject()).contains("(")) {
                    String variableName[] = ((String) ((DefaultMutableTreeNode) root.getChildAt(i)).getUserObject()).split("\\(");
                    if (variableName.length > 0) {
                        if (variableName[0].equals(input)) {
                            hasReached = true;
                            return hasReached;
                        }
                    }
                } else {
                    String variableName = ((String) ((DefaultMutableTreeNode) root.getChildAt(i)).getUserObject());
                    if (variableName.equals(input)) {
                        hasReached = true;
                        return hasReached;
                    }
                }

            } else {
                if (((String) ((DefaultMutableTreeNode) root.getChildAt(i)).getUserObject()).equals(input)) {
                    hasReached = true;
                    return hasReached;
                }
                expandablePath = expandablePath.pathByAddingChild((DefaultMutableTreeNode) root.getChildAt(i));
                hasReached = findVariableNode(input, (DefaultMutableTreeNode) root.getChildAt(i), hasReached);
                if (hasReached == true) {
                    return hasReached;
                }
            }
        }
        return hasReached;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Agent type name:");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Property definitions"));

        jButton1.setText("+");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("-");
        jButton2.setFocusable(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Types available"));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.setFocusable(false);
        jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTree1MouseReleased(evt);
            }
        });
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton3.setText("Confirm");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        workingAgentTemplate.agentProperties.add(new AgentPropertyTemplate());
        AgentTemplatePanel temp = new AgentTemplatePanel(myMainModel, this, workingAgentTemplate.agentProperties.get(workingAgentTemplate.agentProperties.size() - 1), workingAgentTemplate.agentProperties.size() - 1);
        jPanel1.add(temp);
        jPanel1.repaint();
        jPanel1.invalidate();
        jPanel1.revalidate();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTree1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MouseReleased

    }//GEN-LAST:event_jTree1MouseReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        workingAgentTemplate.agentTypeName = jTextField1.getText();
        for (int i = 0; i < jPanel1.getComponentCount(); i++) {
            workingAgentTemplate.agentProperties.get(i).propertyName = ((AgentTemplatePanel) jPanel1.getComponent(i)).jTextField1.getText();
        }
        if (isEdit == false) {
            myMainModel.ABM.agentTemplates.add(workingAgentTemplate);

            workingAgentTemplate.constructor = new BehaviorScript();
            workingAgentTemplate.constructor.javaScript = new JavaScript();
            workingAgentTemplate.constructor.javaScript.script = "";
            workingAgentTemplate.constructor.pythonScript = new PythonScript();
            workingAgentTemplate.constructor.pythonScript.script = "";
            workingAgentTemplate.constructor.isJavaScriptActive = true;

            workingAgentTemplate.behavior = new BehaviorScript();
            workingAgentTemplate.behavior.javaScript = new JavaScript();
            workingAgentTemplate.behavior.javaScript.script = "";
            workingAgentTemplate.behavior.pythonScript = new PythonScript();
            workingAgentTemplate.behavior.pythonScript.script = "";
            workingAgentTemplate.behavior.isJavaScriptActive = true;

            workingAgentTemplate.destructor = new BehaviorScript();
            workingAgentTemplate.destructor.javaScript = new JavaScript();
            workingAgentTemplate.destructor.javaScript.script = "";
            workingAgentTemplate.destructor.pythonScript = new PythonScript();
            workingAgentTemplate.destructor.pythonScript.script = "";
            workingAgentTemplate.destructor.isJavaScriptActive = true;

        } else {
            myMainModel.ABM.agentTemplates.set(templateEditIndex, workingAgentTemplate);
        }

        myParent.updateAgentTemplateList();
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        if (selectedAgentTemplatePanel != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
            if (selectedNode != null) {
                if (!(selectedNode.getLevel() <= 1 && selectedNode.isLeaf() == false)) {
                    selectedAgentPropertyTemplate.propertyType = selectedNode.toString();
                    selectedAgentTemplatePanel.jLabel2.setText(selectedNode.toString());
                }
            }
        }
    }//GEN-LAST:event_jTree1ValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
