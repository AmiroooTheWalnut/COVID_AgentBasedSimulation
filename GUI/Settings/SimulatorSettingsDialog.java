/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.Settings;

import COVID_AgentBasedSimulation.GUI.Settings.AgentTemplateDialog;
import COVID_AgentBasedSimulation.GUI.Settings.AgentBehaviorDialog;
import COVID_AgentBasedSimulation.Model.MainModel;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class SimulatorSettingsDialog extends javax.swing.JDialog {

    MainModel myMainModel;

    /**
     * Creates new form SimulatorSettingsDialog
     */
    public SimulatorSettingsDialog(MainModel mainModel, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        myMainModel = mainModel;
        ArrayList<String> result = mainModel.safegraph.checkMonthAvailability();
        jList6.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return result.size();
            }

            @Override
            public Object getElementAt(int index) {
                return result.get(index).substring(9);
            }
        });
        jList7.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return result.size();
            }

            @Override
            public Object getElementAt(int index) {
                return result.get(index).substring(9);
            }
        });
        if (myMainModel.ABM.isPatternBasedTime == false) {
            jCheckBox2.doClick();
        }
        jList2.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return mainModel.allGISData.countries.size();
            }

            @Override
            public Object getElementAt(int index) {
                return mainModel.allGISData.countries.get(index).name;
            }
        });
        if (myMainModel.ABM.studyScope != null) {
            if (myMainModel.ABM.studyScope.length() > 0) {
                String sections[] = myMainModel.ABM.studyScope.split("_");
                if (sections.length > 0) {
                    for (int i = 0; i < myMainModel.allGISData.countries.size(); i++) {
                        if (sections[0].equals(myMainModel.allGISData.countries.get(i).name)) {
                            jList2.ensureIndexIsVisible(i);
                            jList2.setSelectedIndex(i);
                        }
                    }
                }
                if (sections.length > 1) {
                    for (int i = 0; i < myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.size(); i++) {
                        if (sections[1].equals(myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(i).name)) {
                            jList4.ensureIndexIsVisible(i);
                            jList4.setSelectedIndex(i);
                        }
                    }
                }
                if (sections.length > 2) {
                    for (int i = 0; i < myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.size(); i++) {
                        if (sections[2].equals(myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.get(i).name)) {
                            jList5.ensureIndexIsVisible(i);
                            jList5.setSelectedIndex(i);
                        }
                    }
                }
                if (sections.length > 3) {
                    for (int i = 0; i < myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.get(jList5.getSelectedIndex()).cities.size(); i++) {
                        if (sections[3].equals(myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.get(jList5.getSelectedIndex()).cities.get(i).name)) {
                            jList8.ensureIndexIsVisible(i);
                            jList8.setSelectedIndex(i);
                        }
                    }
                }
            }
        }

        if (myMainModel.ABM.isPatternBasedTime == true) {
            if (myMainModel.ABM.startTime != null) {
                int year = myMainModel.ABM.startTime.getYear();
                int month = myMainModel.ABM.startTime.getMonthValue();
                String monthString = String.valueOf(month);
                if (monthString.length() < 2) {
                    monthString = "0" + monthString;
                }
                String name = year + "_" + monthString;
                for (int i = 0; i < jList6.getModel().getSize(); i++) {
                    if (jList6.getModel().getElementAt(i).equals(name)) {
                        jList6.setSelectedIndex(i);
                    }
                }
            }
            if (myMainModel.ABM.endTime != null) {
                int year = myMainModel.ABM.endTime.getYear();
                int month = myMainModel.ABM.endTime.getMonthValue();
                String monthString = String.valueOf(month);
                if (monthString.length() < 2) {
                    monthString = "0" + monthString;
                }
                String name = year + "_" + monthString;
                for (int i = 0; i < jList7.getModel().getSize(); i++) {
                    if (jList7.getModel().getElementAt(i).equals(name)) {
                        jList7.setSelectedIndex(i);
                    }
                }
            }
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String formattedString = myMainModel.ABM.startTime.format(formatter);
            jFormattedTextField1.setText(formattedString);

            formattedString = myMainModel.ABM.endTime.format(formatter);
            jFormattedTextField2.setText(formattedString);
        }
        
        jCheckBox1.setSelected(myMainModel.ABM.isOurABMActive);
        jCheckBox3.setSelected(myMainModel.ABM.isShamilABMActive);
        jCheckBox4.setSelected(myMainModel.ABM.isAirQualityActive);

        updateAgentTemplateList();
    }

    public void updateAgentTemplateList() {
        jList1.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return myMainModel.ABM.agentTemplates.size();
            }

            @Override
            public Object getElementAt(int index) {
                return myMainModel.ABM.agentTemplates.get(index).agentTypeName;
            }
        });
        jList3.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return myMainModel.ABM.agentTemplates.size();
            }

            @Override
            public Object getElementAt(int index) {
                return myMainModel.ABM.agentTemplates.get(index).agentTypeName;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList<>();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList7 = new javax.swing.JList<>();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel14 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList<>();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jList8 = new javax.swing.JList<>();
        jPanel9 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 3));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Agent templates"));

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jList1);

        jButton1.setText("Add template");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Delete template");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Edit template");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2)
                            .addComponent(jButton1)
                            .addComponent(jButton3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );

        getContentPane().add(jPanel1);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Agent behavior"));

        jLabel2.setText("Agent templates:");

        jButton5.setText("Edit");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jList3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane3.setViewportView(jList3);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jButton5))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel3);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Start/End of simulator"));

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Safegraph"));
        jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Available months: Select starting month"));

        jList6.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList6.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList6ValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(jList6);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        jPanel12.add(jPanel6);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Available months: Select ending month"));

        jList7.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList7.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList7ValueChanged(evt);
            }
        });
        jScrollPane7.setViewportView(jList7);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        jPanel12.add(jPanel13);

        jCheckBox2.setSelected(true);
        jCheckBox2.setText("Is start by safegraph");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("Start date:");
        jLabel3.setEnabled(false);

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))));
        jFormattedTextField1.setEnabled(false);
        jFormattedTextField1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField1PropertyChange(evt);
            }
        });

        jLabel4.setText("End date:");
        jLabel4.setEnabled(false);

        jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))));
        jFormattedTextField2.setEnabled(false);
        jFormattedTextField2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField2PropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextField1))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextField2)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jCheckBox2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel11);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Study scope"));
        jPanel2.setLayout(new java.awt.GridLayout(2, 2));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Country"));

        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel4);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("State"));

        jList4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList4.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList4ValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jList4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel5);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("County"));

        jList5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList5.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList5ValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(jList5);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel7);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("City"));

        jList8.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList8.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList8ValueChanged(evt);
            }
        });
        jScrollPane8.setViewportView(jList8);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel8);

        getContentPane().add(jPanel2);

        jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jCheckBox1.setText("Our ABM");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("Shamil's ABM");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jCheckBox4.setText("Air quality");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox4))
                .addContainerGap(138, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox3)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox4)
                .addContainerGap(428, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel9);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        AgentTemplateDialog agentTemplateDialog = new AgentTemplateDialog(myMainModel, this, (JFrame) SwingUtilities.getWindowAncestor(this), true, -1);
        agentTemplateDialog.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (jList1.getSelectedIndex() > -1) {
            AgentTemplateDialog agentTemplateDialog = new AgentTemplateDialog(myMainModel, this, (JFrame) SwingUtilities.getWindowAncestor(this), true, jList1.getSelectedIndex());
            agentTemplateDialog.workingAgentTemplate = myMainModel.ABM.agentTemplates.get(jList1.getSelectedIndex());
            agentTemplateDialog.updatePropertiestTemplateList();
            agentTemplateDialog.updateStatusList();
            agentTemplateDialog.setVisible(true);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (jList3.getSelectedIndex() > -1) {
            AgentBehaviorDialog agentBehaviorDialog = new AgentBehaviorDialog(myMainModel, (JFrame) SwingUtilities.getWindowAncestor(this), true, jList3.getSelectedIndex());
            agentBehaviorDialog.setVisible(true);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        jPanel12.setEnabled(jCheckBox2.isSelected());
        jPanel6.setEnabled(jCheckBox2.isSelected());
        jPanel13.setEnabled(jCheckBox2.isSelected());
        jList6.setEnabled(jCheckBox2.isSelected());
        jList7.setEnabled(jCheckBox2.isSelected());

        jLabel3.setEnabled(!jCheckBox2.isSelected());
        jLabel4.setEnabled(!jCheckBox2.isSelected());
        jFormattedTextField1.setEnabled(!jCheckBox2.isSelected());
        jFormattedTextField2.setEnabled(!jCheckBox2.isSelected());

        myMainModel.ABM.isPatternBasedTime = jCheckBox2.isSelected();
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jList6ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList6ValueChanged
        if (jList6.getSelectedIndex() > -1) {
            String startPatternDate = jList6.getSelectedValue();
            String splitted[] = startPatternDate.split("_");
            int year = Integer.parseInt(splitted[0]);
            int month = Integer.parseInt(splitted[1]);
            myMainModel.ABM.startTime = ZonedDateTime.of(year, month, 01, 0, 0, 0, 0, ZoneId.of("UTC"));
            myMainModel.ABM.startTimeString = myMainModel.ABM.startTime.toString();
        }
    }//GEN-LAST:event_jList6ValueChanged

    private void jList7ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList7ValueChanged
        if (jList7.getSelectedIndex() > -1) {
            String startPatternDate = jList7.getSelectedValue();
            String splitted[] = startPatternDate.split("_");
            int year = Integer.parseInt(splitted[0]);
            int month = Integer.parseInt(splitted[1]);
            myMainModel.ABM.endTime = ZonedDateTime.of(year, month, 01, 0, 0, 0, 0, ZoneId.of("UTC"));
            myMainModel.ABM.endTimeString = myMainModel.ABM.endTime.toString();
        }
    }//GEN-LAST:event_jList7ValueChanged

    private void jFormattedTextField1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField1PropertyChange
        if (evt.getNewValue() != null) {
            if (jFormattedTextField1.getText().length() > 0) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(jFormattedTextField1.getText() + "+00:00");
                myMainModel.ABM.startTime = zonedDateTime;
                myMainModel.ABM.startTimeString = zonedDateTime.toString() + "[UTC]";
            }
        }
    }//GEN-LAST:event_jFormattedTextField1PropertyChange

    private void jFormattedTextField2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField2PropertyChange
        if (evt.getNewValue() != null) {
            if (jFormattedTextField2.getText().length() > 0) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(jFormattedTextField2.getText() + "+00:00");
                myMainModel.ABM.endTime = zonedDateTime;
                myMainModel.ABM.endTimeString = zonedDateTime.toString() + "[UTC]";
            }
        }
    }//GEN-LAST:event_jFormattedTextField2PropertyChange

    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
        if (jList2.getSelectedIndex() > -1) {
            jList4.clearSelection();
            jList5.clearSelection();
            jList8.clearSelection();
            DefaultListModel listmodel = new DefaultListModel();
            jList5.setModel(listmodel);
            listmodel = new DefaultListModel();
            jList8.setModel(listmodel);
            myMainModel.ABM.studyScope = jList2.getSelectedValue();
            myMainModel.ABM.calculateStudyScopeGeography();
            jList4.setModel(new javax.swing.AbstractListModel() {
                @Override
                public int getSize() {
                    if (jList2.getSelectedIndex() > -1) {
                        return myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.size();
                    } else {
                        return 0;
                    }
                }

                @Override
                public Object getElementAt(int index) {
                    if (jList2.getSelectedIndex() > -1) {
                        return myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(index).name;
                    } else {
                        return null;
                    }
                }
            });
        }
    }//GEN-LAST:event_jList2ValueChanged

    private void jList4ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList4ValueChanged
        if (jList2.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1) {
            jList5.clearSelection();
            jList8.clearSelection();
            DefaultListModel listmodel = new DefaultListModel();
            jList8.setModel(listmodel);
            myMainModel.ABM.studyScope = jList2.getSelectedValue() + "_" + jList4.getSelectedValue();
            myMainModel.ABM.calculateStudyScopeGeography();
            jList5.setModel(new javax.swing.AbstractListModel() {
                @Override
                public int getSize() {
                    if (jList2.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1) {
                        return myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.size();
                    } else {
                        return 0;
                    }
                }

                @Override
                public Object getElementAt(int index) {
                    if (jList2.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1) {
                        return myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.get(index).name;
                    } else {
                        return null;
                    }
                }
            });
        }
    }//GEN-LAST:event_jList4ValueChanged

    private void jList5ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList5ValueChanged
        if (jList2.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1 && jList5.getSelectedIndex() > -1) {
            jList8.clearSelection();
            myMainModel.ABM.studyScope = jList2.getSelectedValue() + "_" + jList4.getSelectedValue() + "_" + jList5.getSelectedValue();
            myMainModel.ABM.calculateStudyScopeGeography();
            jList8.setModel(new javax.swing.AbstractListModel() {
                @Override
                public int getSize() {
                    if (jList2.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1 && jList5.getSelectedIndex() > -1) {
                        if (myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.get(jList5.getSelectedIndex()).cities != null) {
                            return myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.get(jList5.getSelectedIndex()).cities.size();
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                }

                @Override
                public Object getElementAt(int index) {
                    if (jList2.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1 && jList5.getSelectedIndex() > -1) {
                        if (myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.get(jList5.getSelectedIndex()).cities != null) {
                            return myMainModel.allGISData.countries.get(jList2.getSelectedIndex()).states.get(jList4.getSelectedIndex()).counties.get(jList5.getSelectedIndex()).cities.get(index).name;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            });
        }
    }//GEN-LAST:event_jList5ValueChanged

    private void jList8ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList8ValueChanged
        if (jList2.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1 && jList5.getSelectedIndex() > -1 && jList8.getSelectedIndex() > -1) {
            myMainModel.ABM.studyScope = jList2.getSelectedValue() + "_" + jList4.getSelectedValue() + "_" + jList5.getSelectedValue() + "_" + jList8.getSelectedValue();
            myMainModel.ABM.calculateStudyScopeGeography();
        }
    }//GEN-LAST:event_jList8ValueChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (jList1.getSelectedIndex() > -1) {
            myMainModel.ABM.agentTemplates.remove(jList1.getSelectedIndex());
            updateAgentTemplateList();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        myMainModel.ABM.isOurABMActive=jCheckBox1.isSelected();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        myMainModel.ABM.isShamilABMActive=jCheckBox3.isSelected();
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
        myMainModel.ABM.isAirQualityActive=jCheckBox4.isSelected();
    }//GEN-LAST:event_jCheckBox4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JList<String> jList5;
    private javax.swing.JList<String> jList6;
    private javax.swing.JList<String> jList7;
    private javax.swing.JList<String> jList8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    // End of variables declaration//GEN-END:variables
}
