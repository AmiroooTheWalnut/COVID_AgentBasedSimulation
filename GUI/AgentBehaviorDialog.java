/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI;

import COVID_AgentBasedSimulation.Model.MainModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author user
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
        myMainModel=mainModel;
        behaviorEditIndex=passed_behaviorEditIndex;
        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree2.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree3.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree4.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        DefaultTreeModel model = (DefaultTreeModel) jTree2.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        model.reload();

        root = new DefaultMutableTreeNode("Properties");
        
        for(int i=0;i<myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).agentProperties.size();i++){
            root = new DefaultMutableTreeNode(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).agentProperties.get(i).propertyName+"["+myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).agentProperties.get(i).propertyType+"]");
        }

        model.setRoot(root);
        
        
        construcorJavaTextArea = new RSyntaxTextArea(20, 60);
        construcorJavaTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        construcorJavaTextArea.setCodeFoldingEnabled(true);
        construcorJavaScrollPane = new RTextScrollPane(construcorJavaTextArea);
        
        construcorPythonTextArea = new RSyntaxTextArea(20, 60);
        construcorPythonTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        construcorPythonTextArea.setCodeFoldingEnabled(true);
        construcorPythonScrollPane = new RTextScrollPane(construcorPythonTextArea);
        
        if(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive==true){
            construcorJavaTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.javaScript.script);
            jPanel2.removeAll();
            jPanel2.add(construcorJavaScrollPane);
            jPanel2.invalidate();
            jPanel2.validate();
            jPanel2.repaint();
        }else{
            construcorPythonTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.pythonScript.script);
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
        
        if(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive==true){
            behaviorJavaTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.javaScript.script);
            jPanel4.removeAll();
            jPanel4.add(behaviorJavaScrollPane);
            jPanel4.invalidate();
            jPanel4.validate();
            jPanel4.repaint();
        }else{
            behaviorPythonTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.pythonScript.script);
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
        
        if(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive==true){
            destructorJavaTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.javaScript.script);
            jPanel5.removeAll();
            jPanel5.add(destructorJavaScrollPane);
            jPanel5.invalidate();
            jPanel5.validate();
            jPanel5.repaint();
        }else{
            destructorPythonTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.pythonScript.script);
            jPanel5.removeAll();
            jPanel5.add(destructorPythonScrollPane);
            jPanel5.invalidate();
            jPanel5.validate();
            jPanel5.repaint();
            jRadioButton6.setSelected(true);
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
        jPanel10 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jPanel12 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jPanel13 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel3.setPreferredSize(new java.awt.Dimension(700, 900));
        jPanel3.setLayout(new java.awt.GridLayout(3, 1));

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
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addContainerGap(158, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel11);

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

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton4)
                .addContainerGap(158, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel12);

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
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton6)
                .addContainerGap(158, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel13);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Current agent properties"));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree2.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane2.setViewportView(jTree2);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Current agent properties", jPanel8);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("All variables"));

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
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
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
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
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("All agent definitions", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        if(jRadioButton1.isSelected()==true && myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive==false){
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive=true;
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.pythonScript.script=construcorPythonTextArea.getText();
            construcorJavaTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.javaScript.script);
            jPanel2.removeAll();
            jPanel2.add(construcorJavaScrollPane);
            jPanel2.invalidate();
            jPanel2.validate();
            jPanel2.repaint();
        }
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        if(jRadioButton2.isSelected()==true && myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive==true){
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.isJavaScriptActive=false;
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.javaScript.script=construcorJavaTextArea.getText();
            construcorPythonTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).constructor.pythonScript.script);
            jPanel2.removeAll();
            jPanel2.add(construcorPythonScrollPane);
            jPanel2.invalidate();
            jPanel2.validate();
            jPanel2.repaint();
        }
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        if(jRadioButton3.isSelected()==true && myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive==false){
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive=true;
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.pythonScript.script=behaviorPythonTextArea.getText();
            behaviorJavaTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.javaScript.script);
            jPanel4.removeAll();
            jPanel4.add(behaviorJavaScrollPane);
            jPanel4.invalidate();
            jPanel4.validate();
            jPanel4.repaint();
        }
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        if(jRadioButton4.isSelected()==true && myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive==true){
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.isJavaScriptActive=false;
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.javaScript.script=behaviorJavaTextArea.getText();
            behaviorPythonTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).behavior.pythonScript.script);
            jPanel4.removeAll();
            jPanel4.add(behaviorPythonScrollPane);
            jPanel4.invalidate();
            jPanel4.validate();
            jPanel4.repaint();
        }
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
        if(jRadioButton5.isSelected()==true && myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive==false){
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive=true;
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.pythonScript.script=destructorPythonTextArea.getText();
            destructorJavaTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.javaScript.script);
            jPanel5.removeAll();
            jPanel5.add(destructorJavaScrollPane);
            jPanel5.invalidate();
            jPanel5.validate();
            jPanel5.repaint();
        }
    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed
        if(jRadioButton6.isSelected()==true && myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive==true){
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.isJavaScriptActive=false;
            myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.javaScript.script=destructorJavaTextArea.getText();
            destructorPythonTextArea.setText(myMainModel.agentBasedModel.agentTemplates.get(behaviorEditIndex).destructor.pythonScript.script);
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
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
    private javax.swing.JTree jTree1;
    private javax.swing.JTree jTree2;
    private javax.swing.JTree jTree3;
    private javax.swing.JTree jTree4;
    // End of variables declaration//GEN-END:variables
}
