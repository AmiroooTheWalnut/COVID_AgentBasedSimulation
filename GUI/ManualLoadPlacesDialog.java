/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI;

import COVID_AgentBasedSimulation.Model.Data.Safegraph.AllSafegraphPlaces;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.SafegraphPlaces;
import COVID_AgentBasedSimulation.Model.Data.Safegraph.Safegraph;

/**
 *
 * @author user
 */
public class ManualLoadPlacesDialog extends javax.swing.JDialog {

    MainFrame myParent;
    String[] patternsList;

    /**
     * Creates new form ManualLoadSafegraphDialog
     */
    public ManualLoadPlacesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        myParent = (MainFrame) parent;
    }

    public void refreshList() {
        patternsList = AllSafegraphPlaces.detectAllPlaces("./datasets/");
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
            SafegraphPlaces safegraphPlaces = Safegraph.loadSafegraphPlacesKryo("./datasets/" + jList1.getSelectedValue() + "/processedData.bin");
            boolean isUnique = true;
            for (int i = 0; i < myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.size(); i++) {
                if (myParent.mainModel.safegraph.allPatterns.monthlyPatternsList.get(i).name.equals(safegraphPlaces.name)) {
                    isUnique = false;
                }
            }
            if (isUnique == true) {
                myParent.mainModel.safegraph.allSafegraphPlaces.monthlySafegraphPlacesList.add(safegraphPlaces);
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
