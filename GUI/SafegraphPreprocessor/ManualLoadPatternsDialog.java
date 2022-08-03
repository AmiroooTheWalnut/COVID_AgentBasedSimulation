/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.SafegraphPreprocessor;

import COVID_AgentBasedSimulation.GUI.MainFrame;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.AllPatterns;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Patterns;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Safegraph;
import java.util.ArrayList;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class ManualLoadPatternsDialog extends javax.swing.JDialog {

    MainFrame myMainFrameParent;
    SafeGraphPreprocessDialog myParent;
    String[] patternsList;

    /**
     * Creates new form ManualLoadSafegraphDialog
     */
    public ManualLoadPatternsDialog(java.awt.Frame parent, boolean modal, SafeGraphPreprocessDialog passed_SafeGraphPreprocessDialog) {
        super(parent, modal);
        initComponents();
        myMainFrameParent = (MainFrame) parent;
        myParent = passed_SafeGraphPreprocessDialog;
    }

    public void refreshList() {
        patternsList = AllPatterns.detectAllPatterns("./datasets/Safegraph/FullData");
        jList1.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return patternsList.length;
            }

            @Override
            public Object getElementAt(int index) {
                return patternsList[index];
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Patterns"));

        jButton1.setText("Load compressed raw data");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (jList1.getSelectedIndex() > -1) {
            myParent.mainModel.safegraph.clearPatternsPlaces();
            Patterns patterns = Safegraph.loadPatternsKryo("./datasets/Safegraph/FullData/" + jList1.getSelectedValue() + "/processedData.bin");
            System.out.println("PATTERNS SIZE: " + patterns.patternRecords.size());
            double avg_num_visitors = 0;
            for (int i = 0; i < patterns.patternRecords.size(); i++) {
                avg_num_visitors = avg_num_visitors + patterns.patternRecords.get(i).raw_visitor_counts;
            }
            avg_num_visitors = avg_num_visitors / (double) patterns.patternRecords.size();
            System.out.println("PATTERNS AVERAGE VISITORS: " + avg_num_visitors);

            double avg_num_visits = 0;
            for (int i = 0; i < patterns.patternRecords.size(); i++) {
                avg_num_visits = avg_num_visits + patterns.patternRecords.get(i).raw_visit_counts;
            }
            avg_num_visits = avg_num_visits / (double) patterns.patternRecords.size();
            System.out.println("PATTERNS AVERAGE VISITS: " + avg_num_visits);

            boolean isUnique = true;
            if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList != null) {
                for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
                    if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).name.equals(patterns.name)) {
                        isUnique = false;
                    }
                }
            }else{
                myParent.mainModel.safegraph.allPatterns.monthlyPatternsList=new ArrayList();
            }
            if (isUnique == true) {
                myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.add(patterns);
                //byte a=0;
                myParent.refreshPatternsList();
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
