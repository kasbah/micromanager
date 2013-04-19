/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProjectorControlForm.java
 *
 * Created on Apr 3, 2010, 12:37:36 PM
 */
package org.micromanager.projector;

import ij.IJ;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import mmcorej.CMMCore;
import org.micromanager.utils.GUIUtils;

/**
 *
 * @author arthur
 */
public class ProjectorControlForm extends javax.swing.JFrame implements OnStateListener {

   private final CMMCore core_;
   private final ProjectorController controller_;
   private final ProjectorPlugin plugin_;
   private int numROIs_;

   /**
    * Creates new form ProjectorControlForm
    */
   public ProjectorControlForm(ProjectorPlugin plugin, ProjectorController controller, CMMCore core) {
      initComponents();
      plugin_ = plugin;
      controller_ = controller;
      core_ = core;
      allPixelsButton.setEnabled(controller_.isSLM());
      GUIUtils.recallPosition(this);
      pointAndShootOffButton.setSelected(true);
      updateROISettings();
      populateChannelComboBox(Preferences.userNodeForPackage(this.getClass()).get("channel", ""));
      this.addWindowFocusListener(new WindowAdapter() {
          public void windowGainedFocus(WindowEvent e) {
              populateChannelComboBox(null);
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

        mainTabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        pointAndShootIntervalSpinner = new javax.swing.JSpinner();
        pointAndShootOnButton = new javax.swing.JToggleButton();
        pointAndShootOffButton = new javax.swing.JToggleButton();
        closeShutterLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        roiLoopLabel = new javax.swing.JLabel();
        roiLoopTimesLabel = new javax.swing.JLabel();
        setRoiButton = new javax.swing.JButton();
        runROIsNowButton = new javax.swing.JButton();
        roiLoopSpinner = new javax.swing.JSpinner();
        repeatCheckBox = new javax.swing.JCheckBox();
        startFrameLabel = new javax.swing.JLabel();
        startFrameSpinner = new javax.swing.JSpinner();
        repeatEveryFrameSpinner = new javax.swing.JSpinner();
        framesLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        useInMDAcheckBox = new javax.swing.JCheckBox();
        roiStatusLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        spotDwellTimeLabel = new javax.swing.JLabel();
        spotDwellTimeSpinner = new javax.swing.JSpinner();
        spotDwellTimeUnitsLabel = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        onButton = new javax.swing.JButton();
        calibrateButton = new javax.swing.JButton();
        offButton = new javax.swing.JButton();
        allPixelsButton = new javax.swing.JButton();
        centerButton = new javax.swing.JButton();
        channelComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Projector Controls");
        setResizable(false);

        mainTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainTabbedPaneStateChanged(evt);
            }
        });

        jLabel1.setText("Point and shoot mode:");

        jLabel2.setText("ms");

        pointAndShootIntervalSpinner.setModel(new SpinnerNumberModel(500, 1, 1000000000, 1));
        pointAndShootIntervalSpinner.setMaximumSize(new java.awt.Dimension(75, 20));
        pointAndShootIntervalSpinner.setMinimumSize(new java.awt.Dimension(75, 20));
        pointAndShootIntervalSpinner.setPreferredSize(new java.awt.Dimension(75, 20));
        pointAndShootIntervalSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                pointAndShootIntervalSpinnerStateChanged(evt);
            }
        });
        pointAndShootIntervalSpinner.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                pointAndShootIntervalSpinnerVetoableChange(evt);
            }
        });

        pointAndShootOnButton.setText("On");
        pointAndShootOnButton.setMaximumSize(new java.awt.Dimension(75, 23));
        pointAndShootOnButton.setMinimumSize(new java.awt.Dimension(75, 23));
        pointAndShootOnButton.setPreferredSize(new java.awt.Dimension(75, 23));
        pointAndShootOnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointAndShootOnButtonActionPerformed(evt);
            }
        });

        pointAndShootOffButton.setText("Off");
        pointAndShootOffButton.setPreferredSize(new java.awt.Dimension(75, 23));
        pointAndShootOffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointAndShootOffButtonActionPerformed(evt);
            }
        });

        closeShutterLabel.setText("Close shutter after");

        jLabel3.setText("(To phototarget, Control + click on the image.)");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(25, 25, 25)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel1)
                            .add(closeShutterLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(pointAndShootOnButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(pointAndShootIntervalSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pointAndShootOffButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel2)))
                    .add(jLabel3))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(pointAndShootOnButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pointAndShootOffButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(pointAndShootIntervalSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(closeShutterLabel))
                .addContainerGap(221, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Point and Shoot", jPanel1);

        roiLoopLabel.setText("Loop:");

        roiLoopTimesLabel.setText("times");

        setRoiButton.setText("Set ROI(s)");
        setRoiButton.setToolTipText("Specify an ROI you wish to be phototargeted by using the ImageJ ROI tools (point, rectangle, oval, polygon). Then press Set ROI(s) to send the ROIs to the phototargeting device. To initiate phototargeting, press Go!");
        setRoiButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setRoiButtonActionPerformed(evt);
            }
        });

        runROIsNowButton.setText("Run ROIs now!");
        runROIsNowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runROIsNowButtonActionPerformed(evt);
            }
        });

        roiLoopSpinner.setModel(new SpinnerNumberModel(1, 1, 1000000000, 1));
        roiLoopSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                roiLoopSpinnerStateChanged(evt);
            }
        });

        repeatCheckBox.setText("Repeat every");
        repeatCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repeatCheckBoxActionPerformed(evt);
            }
        });

        startFrameLabel.setText("Start Frame");

        startFrameSpinner.setModel(new SpinnerNumberModel(1, 1, 1000000000, 1));
        startFrameSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                startFrameSpinnerStateChanged(evt);
            }
        });

        repeatEveryFrameSpinner.setModel(new SpinnerNumberModel(1, 1, 1000000000, 1));
        repeatEveryFrameSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                repeatEveryFrameSpinnerStateChanged(evt);
            }
        });

        framesLabel.setText("frames");

        useInMDAcheckBox.setText("Run ROIs in Multi-Dimensional Acquisition");
        useInMDAcheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useInMDAcheckBoxActionPerformed(evt);
            }
        });

        roiStatusLabel.setText("No ROIs submitted yet");

        jButton1.setText("ROI Manager >>");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        spotDwellTimeLabel.setText("Spot dwell time:");

        spotDwellTimeSpinner.setModel(new SpinnerNumberModel(500, 1, 1000000000, 1));
        spotDwellTimeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spotDwellTimeSpinnerStateChanged(evt);
            }
        });

        spotDwellTimeUnitsLabel.setText("ms");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(runROIsNowButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(15, 15, 15)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPanel3Layout.createSequentialGroup()
                                        .add(29, 29, 29)
                                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jPanel3Layout.createSequentialGroup()
                                                .add(startFrameLabel)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                .add(startFrameSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                            .add(jPanel3Layout.createSequentialGroup()
                                                .add(repeatCheckBox)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                .add(repeatEveryFrameSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(framesLabel))))
                                    .add(useInMDAcheckBox)))
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(15, 15, 15)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPanel3Layout.createSequentialGroup()
                                        .add(spotDwellTimeLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(spotDwellTimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(spotDwellTimeUnitsLabel))
                                    .add(jPanel3Layout.createSequentialGroup()
                                        .add(roiLoopLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(roiLoopSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(roiLoopTimesLabel)))))
                        .add(0, 21, Short.MAX_VALUE)))
                .addContainerGap())
            .add(jPanel3Layout.createSequentialGroup()
                .add(25, 25, 25)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(roiStatusLabel)
                        .addContainerGap())
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(setRoiButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButton1)
                        .add(24, 24, 24))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(setRoiButton)
                            .add(jButton1))
                        .add(18, 18, 18)
                        .add(roiStatusLabel)
                        .add(18, 18, 18)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(roiLoopLabel)
                            .add(roiLoopTimesLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(roiLoopSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(9, 9, 9)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(spotDwellTimeLabel)
                            .add(spotDwellTimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(spotDwellTimeUnitsLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(runROIsNowButton)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(useInMDAcheckBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(startFrameLabel)
                                    .add(startFrameSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(repeatCheckBox)
                                    .add(repeatEveryFrameSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(framesLabel))))))
                .addContainerGap(66, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("ROIs", jPanel3);

        onButton.setText("On");
        onButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onButtonActionPerformed(evt);
            }
        });

        calibrateButton.setText("Calibrate");
        calibrateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calibrateButtonActionPerformed(evt);
            }
        });

        offButton.setText("Off");
        offButton.setSelected(true);
        offButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offButtonActionPerformed(evt);
            }
        });

        allPixelsButton.setText("All Pixels");
        allPixelsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allPixelsButtonActionPerformed(evt);
            }
        });

        centerButton.setText("Show center spot");
        centerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerButtonActionPerformed(evt);
            }
        });

        channelComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        channelComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                channelComboBoxActionPerformed(evt);
            }
        });

        jLabel4.setText("Phototargeting channel:");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(channelComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(onButton)
                        .add(98, 98, 98)
                        .add(calibrateButton))
                    .add(offButton)
                    .add(centerButton)
                    .add(allPixelsButton))
                .addContainerGap(150, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(22, 22, 22)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(onButton)
                    .add(calibrateButton))
                .add(4, 4, 4)
                .add(offButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(centerButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(allPixelsButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 38, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(channelComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(125, 125, 125))
        );

        mainTabbedPane.addTab("Setup", jPanel2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(mainTabbedPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(mainTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 346, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void calibrateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calibrateButtonActionPerformed
       boolean running = controller_.isCalibrating();
       if (running) {
           controller_.stopCalibration();
           calibrateButton.setText("Calibrate");
       } else {
           controller_.calibrate();
           calibrateButton.setText("Stop calibration");
       }
    }//GEN-LAST:event_calibrateButtonActionPerformed

    private void onButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onButtonActionPerformed
       controller_.turnOn();
       offButton.setSelected(false);
       onButton.setSelected(true);
       pointAndShootOffButtonActionPerformed(null);
    }//GEN-LAST:event_onButtonActionPerformed

    private void offButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_offButtonActionPerformed
       controller_.turnOff();
       offButton.setSelected(true);
       onButton.setSelected(false);
    }//GEN-LAST:event_offButtonActionPerformed

    private void allPixelsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allPixelsButtonActionPerformed
       controller_.activateAllPixels();
    }//GEN-LAST:event_allPixelsButtonActionPerformed

   private void mainTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainTabbedPaneStateChanged
      if (controller_ != null) {
         pointAndShootOnButton.setSelected(false);
         pointAndShootOffButton.setSelected(true);
         updatePointAndShoot();
      }
   }//GEN-LAST:event_mainTabbedPaneStateChanged

   private void spotDwellTimeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spotDwellTimeSpinnerStateChanged
      controller_.setSpotInterval(getSpinnerValue(this.spotDwellTimeSpinner) * 1000);
   }//GEN-LAST:event_spotDwellTimeSpinnerStateChanged

   private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      IJ.run("ROI Manager...");
   }//GEN-LAST:event_jButton1ActionPerformed

   private void useInMDAcheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useInMDAcheckBoxActionPerformed
      updateROISettings();
   }//GEN-LAST:event_useInMDAcheckBoxActionPerformed

   private void repeatCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repeatCheckBoxActionPerformed
      controller_.setRoiRepetitions(repeatCheckBox.isSelected()
         ? getRoiRepetitionsSetting() : 0);
      updateROISettings();
   }//GEN-LAST:event_repeatCheckBoxActionPerformed

   private void roiLoopSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_roiLoopSpinnerStateChanged
      controller_.setRoiRepetitions(getRoiRepetitionsSetting());
   }//GEN-LAST:event_roiLoopSpinnerStateChanged

   private void runROIsNowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runROIsNowButtonActionPerformed
      controller_.runPolygons();
   }//GEN-LAST:event_runROIsNowButtonActionPerformed

   private void setRoiButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setRoiButtonActionPerformed
      numROIs_ = controller_.setRois(getRoiRepetitionsSetting(), IJ.getImage());
      this.updateROISettings();
   }//GEN-LAST:event_setRoiButtonActionPerformed

   private void centerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_centerButtonActionPerformed
      offButtonActionPerformed(null);
      controller_.moveToCenter();
   }//GEN-LAST:event_centerButtonActionPerformed

   private void pointAndShootOffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointAndShootOffButtonActionPerformed
      pointAndShootOnButton.setSelected(false);
      pointAndShootOffButton.setSelected(true);
      updatePointAndShoot();
   }//GEN-LAST:event_pointAndShootOffButtonActionPerformed

   private void pointAndShootOnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointAndShootOnButtonActionPerformed
      pointAndShootOnButton.setSelected(true);
      pointAndShootOffButton.setSelected(false);
      offButtonActionPerformed(null);
      updatePointAndShoot();
   }//GEN-LAST:event_pointAndShootOnButtonActionPerformed

   private void pointAndShootIntervalSpinnerVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_pointAndShootIntervalSpinnerVetoableChange
      updatePointAndShoot();
   }//GEN-LAST:event_pointAndShootIntervalSpinnerVetoableChange

   private void pointAndShootIntervalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_pointAndShootIntervalSpinnerStateChanged
      updatePointAndShoot();
   }//GEN-LAST:event_pointAndShootIntervalSpinnerStateChanged

   private void repeatEveryFrameSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_repeatEveryFrameSpinnerStateChanged
      updateROISettings();
   }//GEN-LAST:event_repeatEveryFrameSpinnerStateChanged

   private void startFrameSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_startFrameSpinnerStateChanged
      updateROISettings();
   }//GEN-LAST:event_startFrameSpinnerStateChanged

    private void channelComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_channelComboBoxActionPerformed
        final String channel = (String) channelComboBox.getSelectedItem();
        controller_.setTargetingChannel(channel);
        if (channel != null) {
            Preferences.userNodeForPackage(this.getClass()).put("channel", channel);
        }
    }//GEN-LAST:event_channelComboBoxActionPerformed

   private int getRoiRepetitionsSetting() {
      return getSpinnerValue(roiLoopSpinner);
   }

   private int getSpinnerValue(JSpinner spinner) {
      return Integer.parseInt(spinner.getValue().toString());
   }

   public void updatePointAndShoot() {
      controller_.setPointAndShootInterval(1000 * Double.parseDouble(this.pointAndShootIntervalSpinner.getValue().toString()));
      controller_.enablePointAndShootMode(pointAndShootOnButton.isSelected());
   }

   public void dispose() {
      super.dispose();
   }

   private void formWindowClosing(java.awt.event.WindowEvent evt) {
      plugin_.dispose();
   }
  
   public void updateROISettings() {
      boolean roisSubmitted = false;
      if (numROIs_ == 0) {
         roiStatusLabel.setText("No ROIs submitted");
         roisSubmitted = false;
      } else if (numROIs_ == 1) {
         roiStatusLabel.setText("One ROI submitted");
         roisSubmitted = true;
      } else { // numROIs_ > 1
         roiStatusLabel.setText("" + numROIs_ + " ROIs submitted");
         roisSubmitted = true;
      }

      roiLoopLabel.setEnabled(roisSubmitted);
      roiLoopSpinner.setEnabled(roisSubmitted);
      roiLoopTimesLabel.setEnabled(roisSubmitted);
      spotDwellTimeSpinner.setEnabled(roisSubmitted);
      spotDwellTimeLabel.setEnabled(roisSubmitted);
      spotDwellTimeUnitsLabel.setEnabled(roisSubmitted);
      runROIsNowButton.setEnabled(roisSubmitted);
      useInMDAcheckBox.setEnabled(roisSubmitted);

      boolean useInMDA = roisSubmitted && useInMDAcheckBox.isSelected();
      startFrameLabel.setEnabled(useInMDA);
      startFrameSpinner.setEnabled(useInMDA);
      repeatCheckBox.setEnabled(useInMDA);

      boolean repeatInMDA = useInMDA && repeatCheckBox.isSelected();
      repeatEveryFrameSpinner.setEnabled(repeatInMDA);
      framesLabel.setEnabled(repeatInMDA);
      
      if (useInMDAcheckBox.isSelected()) {
         controller_.attachToMDA(getSpinnerValue(this.startFrameSpinner) - 1,
            this.repeatCheckBox.isSelected(),
            getSpinnerValue(this.repeatEveryFrameSpinner));
      } else {
         controller_.removeFromMDA();
      }
   }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allPixelsButton;
    private javax.swing.JButton calibrateButton;
    private javax.swing.JButton centerButton;
    private javax.swing.JComboBox channelComboBox;
    private javax.swing.JLabel closeShutterLabel;
    private javax.swing.JLabel framesLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JButton offButton;
    private javax.swing.JButton onButton;
    private javax.swing.JSpinner pointAndShootIntervalSpinner;
    private javax.swing.JToggleButton pointAndShootOffButton;
    private javax.swing.JToggleButton pointAndShootOnButton;
    private javax.swing.JCheckBox repeatCheckBox;
    private javax.swing.JSpinner repeatEveryFrameSpinner;
    private javax.swing.JLabel roiLoopLabel;
    private javax.swing.JSpinner roiLoopSpinner;
    private javax.swing.JLabel roiLoopTimesLabel;
    private javax.swing.JLabel roiStatusLabel;
    private javax.swing.JButton runROIsNowButton;
    private javax.swing.JButton setRoiButton;
    private javax.swing.JLabel spotDwellTimeLabel;
    private javax.swing.JSpinner spotDwellTimeSpinner;
    private javax.swing.JLabel spotDwellTimeUnitsLabel;
    private javax.swing.JLabel startFrameLabel;
    private javax.swing.JSpinner startFrameSpinner;
    private javax.swing.JCheckBox useInMDAcheckBox;
    // End of variables declaration//GEN-END:variables

   public void turnedOn() {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            onButton.setSelected(true);
            offButton.setSelected(false);
         }
      });
   }

   public void turnedOff() {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            onButton.setSelected(false);
            offButton.setSelected(true);
         }
      });
   }
   
    void populateChannelComboBox(String initialChannel) {
        if (initialChannel == null) {
           initialChannel = (String) channelComboBox.getSelectedItem();
        }
        channelComboBox.removeAllItems();
        channelComboBox.addItem("");
        for (String preset : core_.getAvailableConfigs(core_.getChannelGroup())) {
            channelComboBox.addItem(preset);
        }
        channelComboBox.setSelectedItem(initialChannel);
    }

    @Override
    public void calibrationDone() {
        calibrateButton.setText("Calibrate");
    }
}
