/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI;

import COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization.ProcessingMapRenderer;
import COVID_AgentBasedSimulation.Model.Structure.Marker;
import de.fhpotsdam.unfolding.geo.Location;
import java.util.ArrayList;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class PreviewDialog extends javax.swing.JDialog {

    MainFrame myParent;

    Location oldLocationDebugging;

    /**
     * Creates new form PreviewDialog
     */
    public PreviewDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        myParent = (MainFrame) parent;
        refreshLists();
        ArrayList<String> result = myParent.mainModel.safegraph.checkMonthAvailability();
        jList6.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return result.size();
            }

            @Override
            public Object getElementAt(int index) {
                return result.get(index);
            }
        });

        ProcessingMapRenderer pt = new ProcessingMapRenderer(myParent, jPanel7, null);
        pt.startRendering();
    }

    public void refreshLists() {
        jList1.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                return myParent.mainModel.allGISData.countries.size();
            }

            @Override
            public Object getElementAt(int index) {
                return myParent.mainModel.allGISData.countries.get(index).name;
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList7 = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList<>();
        jPanel7 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList<>();
        jSlider1 = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Country"));

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Country", jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("State/Province"));

        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("State/Province", jPanel2);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("County"));

        jList3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList3.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList3ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jList3);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("County", jPanel3);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("City"));

        jList7.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList7.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList7ValueChanged(evt);
            }
        });
        jScrollPane7.setViewportView(jList7);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jTabbedPane1.addTab("City", jPanel8);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Census tract"));

        jList4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList4.setToolTipText("");
        jList4.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList4ValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jList4);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Census tract", jPanel4);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Census block group"));

        jList5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList5.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList5ValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(jList5);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5)
        );

        jTabbedPane1.addTab("Census block group", jPanel5);

        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel7.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanel7.setPreferredSize(new java.awt.Dimension(893, 1000));
        jPanel7.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                jPanel7AncestorResized(evt);
            }
        });
        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Month"));

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
            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6)
        );

        jSlider1.setValue(4);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLabel1.setText("jLabel1");

        jLabel2.setText("Size:");

        jLabel3.setText("jLabel3");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(0, 138, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        if (jList1.getSelectedIndex() > -1) {
            jList2.clearSelection();
            jList3.clearSelection();
            jList4.clearSelection();
            jList5.clearSelection();
            jList7.clearSelection();
            jList2.setModel(new javax.swing.AbstractListModel() {
                @Override
                public int getSize() {
                    if (jList1.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.size();
                    } else {
                        return 0;
                    }
                }

                @Override
                public Object getElementAt(int index) {
                    if (jList1.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(index).name + " " + myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(index).id;
                    } else {
                        return null;
                    }
                }
            });

            Location loc = new Location(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).lon, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).lat);
            myParent.child.panZoomTo(loc, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).size);
            jLabel3.setText(String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).size));
            String[] childrenNames = new String[myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.size()];
            ArrayList<Marker> markers = new ArrayList();
            for (int i = 0; i < childrenNames.length; i++) {
                childrenNames[i] = myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(i).name;
                markers.add(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(i));
            }
            myParent.child.setDrawingGISMarkers(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()), markers, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).name, childrenNames);
        }
    }//GEN-LAST:event_jList1ValueChanged

    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
        if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1) {
            jList3.clearSelection();
            jList4.clearSelection();
            jList5.clearSelection();
            jList7.clearSelection();
            jList3.setModel(new javax.swing.AbstractListModel() {
                @Override
                public int getSize() {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.size();
                    } else {
                        return 0;
                    }
                }

                @Override

                public Object getElementAt(int index) {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(index).name + " " + myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(index).id;
                    } else {
                        return null;
                    }
                }
            }
            );

            Location loc = new Location(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).lon, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).lat);
            myParent.child.panZoomTo(loc, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).size);
            jLabel3.setText(String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).size));
            String[] childrenNames = new String[myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.size()];
            ArrayList<Marker> markers = new ArrayList();
            for (int i = 0; i < childrenNames.length; i++) {
                childrenNames[i] = myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(i).name;
                markers.add(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(i));
            }
            myParent.child.setDrawingGISMarkers(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()), markers, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).name, childrenNames);
        }
    }//GEN-LAST:event_jList2ValueChanged

    private void jList3ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList3ValueChanged
        if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1) {
            jList4.clearSelection();
            jList5.clearSelection();
            jList7.clearSelection();
            jList4.setModel(new javax.swing.AbstractListModel() {
                @Override
                public int getSize() {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.size();
                    } else {
                        return 0;
                    }
                }

                @Override
                public Object getElementAt(int index) {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(index).id;
                    } else {
                        return null;
                    }
                }
            });

            jList7.setModel(new javax.swing.AbstractListModel() {
                @Override
                public int getSize() {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1) {
                        if (myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities != null) {
                            return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.size();
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                }

                @Override
                public Object getElementAt(int index) {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1) {
                        if (myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities != null) {
                            return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(index).name;
                        } else {
                            return 0;
                        }
                    } else {
                        return null;
                    }
                }
            });

            Location loc = new Location(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).lon, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).lat);
            myParent.child.panZoomTo(loc, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).size);
            jLabel3.setText(String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).size));
            String[] childrenNames = new String[myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.size()];
            ArrayList<Marker> markers = new ArrayList();
            for (int i = 0; i < childrenNames.length; i++) {
                childrenNames[i] = String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(i).id);
                markers.add(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(i));
            }
            myParent.child.setDrawingGISMarkers(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()), markers, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).name, childrenNames);
        }
    }//GEN-LAST:event_jList3ValueChanged

    private void jList4ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList4ValueChanged
        if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1) {
            jList5.clearSelection();
            jList5.setModel(new javax.swing.AbstractListModel() {
                @Override
                public int getSize() {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.size();
                    } else {
                        return 0;
                    }
                }

                @Override
                public Object getElementAt(int index) {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.get(index).id;
                    } else {
                        return null;
                    }
                }
            });

            Location loc = new Location(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).lon, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).lat);
            myParent.child.panZoomTo(loc, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).size);
            jLabel3.setText(String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).size));
            String[] childrenNames = new String[myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.size()];
            ArrayList<Marker> markers = new ArrayList();
            for (int i = 0; i < childrenNames.length; i++) {
                childrenNames[i] = String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.get(i).id);
                markers.add(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.get(i));
            }
            myParent.child.setDrawingGISMarkers(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()), markers, String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).id), childrenNames);
        }
    }//GEN-LAST:event_jList4ValueChanged

    private void jList6ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList6ValueChanged

    }//GEN-LAST:event_jList6ValueChanged

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        oldLocationDebugging = myParent.child.map.getCenter();
        float sliderZoom = (((int) jSlider1.getValue() / 100f) * (16 - 1)) + 1;
        jLabel1.setText(String.valueOf(sliderZoom));
        myParent.child.map.zoomTo(sliderZoom);
        myParent.child.map.panTo(oldLocationDebugging);
    }//GEN-LAST:event_jSlider1StateChanged

    private void jList5ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList5ValueChanged
        if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1 && jList4.getSelectedIndex() > -1 && jList5.getSelectedIndex() > -1) {
            Location loc = new Location(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.get(jList5.getSelectedIndex()).lon, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.get(jList5.getSelectedIndex()).lat);
            myParent.child.panZoomTo(loc, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.get(jList5.getSelectedIndex()).size);
            jLabel3.setText(String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.get(jList5.getSelectedIndex()).size));
            myParent.child.setDrawingGISMarkers(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.get(jList5.getSelectedIndex()), null, String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).censusTracts.get(jList4.getSelectedIndex()).censusBlocks.get(jList5.getSelectedIndex()).id), null);
        }
    }//GEN-LAST:event_jList5ValueChanged

    private void jList7ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList7ValueChanged
        if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1 && jList7.getSelectedIndex() > -1) {
            jList4.clearSelection();
            jList5.clearSelection();

            jList4.setModel(new javax.swing.AbstractListModel() {
                @Override
                public int getSize() {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1 && jList7.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).censusTracts.size();
                    } else {
                        return 0;
                    }
                }

                @Override
                public Object getElementAt(int index) {
                    if (jList1.getSelectedIndex() > -1 && jList2.getSelectedIndex() > -1 && jList3.getSelectedIndex() > -1 && jList7.getSelectedIndex() > -1) {
                        return myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).censusTracts.get(index).id;
                    } else {
                        return null;
                    }
                }
            });

            Location loc = new Location(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).lon, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).lat);
            myParent.child.panZoomTo(loc, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).size);
            jLabel3.setText(String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).size));
            String[] childrenNames = new String[myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).censusTracts.size()];
            ArrayList<Marker> markers = new ArrayList();
            for (int i = 0; i < childrenNames.length; i++) {
                childrenNames[i] = String.valueOf(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).censusTracts.get(i).id);
                markers.add(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).censusTracts.get(i));
            }
            myParent.child.setDrawingGISMarkers(myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()), markers, myParent.mainModel.allGISData.countries.get(jList1.getSelectedIndex()).states.get(jList2.getSelectedIndex()).counties.get(jList3.getSelectedIndex()).cities.get(jList7.getSelectedIndex()).name, childrenNames);
        }
    }//GEN-LAST:event_jList7ValueChanged

    private void jPanel7AncestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_jPanel7AncestorResized
//        if (myParent != null) {
//            if (myParent.child != null) {
//                if (myParent.child.mySurface != null) {
//                    myParent.child.mySurface.setSize(jPanel7.getWidth(), jPanel7.getHeight());
//                }
//            }
//        }
    }//GEN-LAST:event_jPanel7AncestorResized


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JList<String> jList5;
    private javax.swing.JList<String> jList6;
    private javax.swing.JList<String> jList7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    public javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
