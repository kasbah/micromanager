/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.micromanager.acquiremultipleregions;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractListModel;
import mmcorej.DeviceType;
import mmcorej.StrVector;
import org.micromanager.api.MultiStagePosition;
import org.micromanager.api.PositionList;
import org.micromanager.api.ScriptInterface;
import org.micromanager.api.SequenceSettings;
import org.micromanager.api.StagePosition;
import org.micromanager.utils.FileDialogs;
import org.micromanager.utils.MMScriptException;
import org.micromanager.utils.ReportingUtils;



/**
 *
 * @author kthorn
 */
public class AcquireMultipleRegionsForm extends javax.swing.JFrame {
   private final ScriptInterface gui_;
   private final mmcorej.CMMCore mmc_;
   private RegionListModel rlm_;
   private Region currentRegion_;
   private static final String msgPrefix_ = "AcquireMultipleRegions: ";
   
    /**
     * Creates new form AcquireMultipleRegionsForm
     * @param gui
     */
    public AcquireMultipleRegionsForm(ScriptInterface gui) {
        rlm_ = new RegionListModel();
        gui_ = gui;
        mmc_ = gui_.getMMCore();
        initComponents();        
        currentRegion_ = new Region(new PositionList(), DirectoryText.getText(), FilenameText.getText());
        setBackground(gui_.getBackgroundColor());   
    }
    
    private class RegionListModel extends AbstractListModel {
        public RegionList regions_;

        private RegionListModel() {
            this.regions_ = new RegionList();
        }

        @Override
        public int getSize() {
            return regions_.getNumberOfRegions();
        }

        @Override
        public Object getElementAt(int index) {
            Region r = regions_.getRegion(index);
            return r.name();
        }
        
        public void addRegion(Region r) {
            regions_.addRegion(r);
            fireIntervalAdded(this, regions_.getNumberOfRegions(), regions_.getNumberOfRegions());
        }
        
        public void deleteRegion(int index) {
            regions_.removeRegion(index);
            fireIntervalRemoved(this, index, index);
        }
        
        public Region getRegion(int index) {
            Region r = regions_.getRegion(index);
            return r;
        }
        
        public void clearRegions() {
            this.regions_ = new RegionList();
            fireIntervalAdded(this, 0, 0);
        }
        
    }
    
    class acqThread extends Thread {
        @Override
	public void run() {
        for (int i=0; i<rlm_.getSize(); i++){
            Region currRegion = rlm_.getRegion(i);
            try {
                statusText.setText("Acquiring region " + String.valueOf(i));
                //turn on position list, turn off time lapse
                SequenceSettings currSettings = gui_.getAcquisitionSettings();
                currSettings.usePositionList = true;
                currSettings.numFrames = 1;
                gui_.setAcquisitionSettings(currSettings);
                gui_.setPositionList(currRegion.tileGrid(getXFieldSize(), getYFieldSize()));               
                gui_.refreshGUI();
                String acqName = gui_.runAcquisition(currRegion.filename, currRegion.directory);
                gui_.closeAcquisitionWindow(acqName);
            } catch (MMScriptException ex) {
                handleError(ex);
            }
        }
        statusText.setText("Acquisition finished");
        }
    }
 /* 
   Convenience function for logging errors.
   */
   private void handleError(Exception e) {
      ReportingUtils.showError(e);
   }
   
 /* 
   Convenience function for logging messages with prefix indicating message 
   is from AcquireMultipleRegions.
   */
   private void logMessage(String message){
       ReportingUtils.logMessage(msgPrefix_ + message);
   }
        
    private Region makeUniqueRegionName (Region r) {
        //update region filename and directory so its name is unique in regions_
        String filenameprefix;
        int trailingnumber;
        //regular expression for finding trailing _number
        Pattern trailingdigit = Pattern.compile("(.+_)(\\d+)");

        while (!rlm_.regions_.isFileNameUnique(r.directory, r.filename)){
           //append _1 to filename if it doesn't end with _number
           //otherwise increment trailing number until it is unique
           Matcher matcher = trailingdigit.matcher(r.filename);            
           if (matcher.matches()){
               //update trailing number
               filenameprefix = matcher.group(1);
               trailingnumber = Integer.parseInt(matcher.group(2));
               r.filename = filenameprefix.concat(String.valueOf(trailingnumber + 1));
           } else {
               //append trailing digit
               r.filename = r.filename.concat("_1");
           }
       }
        return r;
    }
    
    private double getXFieldSize(){
        double fieldOverlap = Double.parseDouble(overlapText.getText())/100;  
        double xFieldSize = mmc_.getPixelSizeUm() * mmc_.getImageWidth() * (1 - fieldOverlap);
        return xFieldSize;
    }
    
    private double getYFieldSize(){
        double fieldOverlap = Double.parseDouble(overlapText.getText())/100;  
        double yFieldSize = mmc_.getPixelSizeUm() * mmc_.getImageHeight()* (1 - fieldOverlap);
        return yFieldSize;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        AcquireList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        AddPositionList = new javax.swing.JButton();
        FilenameText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        StartAcquisition = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        GotoTop = new javax.swing.JButton();
        GotoCenter = new javax.swing.JButton();
        GotoBottom = new javax.swing.JButton();
        GotoRight = new javax.swing.JButton();
        GotoLeft = new javax.swing.JButton();
        DeleteRegion = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        DirectoryText = new javax.swing.JTextField();
        DirectoryButton = new javax.swing.JButton();
        statusText = new javax.swing.JLabel();
        deleteAllButton = new javax.swing.JButton();
        overlapText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        addPointToRegion = new javax.swing.JButton();
        regionText = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        AcquireList.setModel(rlm_);
        AcquireList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(AcquireList);

        jLabel1.setText("Regions to Acquire");
        jLabel1.setFocusable(false);

        AddPositionList.setText("Save Region for Acquisition");
        AddPositionList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddPositionListActionPerformed(evt);
            }
        });

        jLabel2.setText("Filename:");
        jLabel2.setFocusable(false);

        StartAcquisition.setText("Acquire All Regions");
        StartAcquisition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartAcquisitionActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("Go To Selected Region");

        GotoTop.setText("Top");
        GotoTop.setMaximumSize(new java.awt.Dimension(67, 23));
        GotoTop.setMinimumSize(new java.awt.Dimension(67, 23));
        GotoTop.setPreferredSize(new java.awt.Dimension(67, 23));
        GotoTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GotoTopActionPerformed(evt);
            }
        });

        GotoCenter.setText("Center");
        GotoCenter.setMaximumSize(new java.awt.Dimension(67, 23));
        GotoCenter.setMinimumSize(new java.awt.Dimension(67, 23));
        GotoCenter.setPreferredSize(new java.awt.Dimension(67, 23));
        GotoCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GotoCenterActionPerformed(evt);
            }
        });

        GotoBottom.setText("Bottom");
        GotoBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GotoBottomActionPerformed(evt);
            }
        });

        GotoRight.setText("Right");
        GotoRight.setMaximumSize(new java.awt.Dimension(67, 23));
        GotoRight.setMinimumSize(new java.awt.Dimension(67, 23));
        GotoRight.setPreferredSize(new java.awt.Dimension(67, 23));
        GotoRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GotoRightActionPerformed(evt);
            }
        });

        GotoLeft.setText("Left");
        GotoLeft.setToolTipText("");
        GotoLeft.setMaximumSize(new java.awt.Dimension(67, 23));
        GotoLeft.setMinimumSize(new java.awt.Dimension(67, 23));
        GotoLeft.setPreferredSize(new java.awt.Dimension(67, 23));
        GotoLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GotoLeftActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(GotoLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(GotoCenter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(3, 3, 3)
                        .add(GotoRight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, GotoBottom)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, GotoTop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel3)
                .add(56, 56, 56))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GotoTop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(GotoCenter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(GotoRight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(GotoLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GotoBottom)
                .add(0, 12, Short.MAX_VALUE))
        );

        DeleteRegion.setText("Delete Selected Region");
        DeleteRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteRegionActionPerformed(evt);
            }
        });

        jLabel4.setText("Directory:");
        jLabel4.setFocusable(false);

        DirectoryText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DirectoryTextActionPerformed(evt);
            }
        });

        DirectoryButton.setText("...");
        DirectoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DirectoryButtonActionPerformed(evt);
            }
        });

        statusText.setText("Waiting for user to enter regions...");

        deleteAllButton.setText("Clear Regions");
        deleteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllButtonActionPerformed(evt);
            }
        });

        overlapText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        overlapText.setText("10");

        jLabel5.setText("% overlap between tiles");

        addPointToRegion.setText("Add Point to Current Region");
        addPointToRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPointToRegionActionPerformed(evt);
            }
        });

        regionText.setText("Current Region: 0 images");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(addPointToRegion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(AddPositionList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                            .add(DeleteRegion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(StartAcquisition, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel4)
                            .add(layout.createSequentialGroup()
                                .add(DirectoryText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(DirectoryButton))
                            .add(statusText)
                            .add(deleteAllButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(FilenameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel2)
                            .add(layout.createSequentialGroup()
                                .add(overlapText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel5))
                            .add(regionText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(0, 3, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 232, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .add(0, 1, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 274, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(DirectoryText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(DirectoryButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)
                        .add(1, 1, 1)
                        .add(FilenameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(overlapText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addPointToRegion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(regionText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(AddPositionList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(37, 37, 37)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(DeleteRegion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteAllButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(StartAcquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(statusText))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AddPositionListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddPositionListActionPerformed
        currentRegion_.directory = DirectoryText.getText();
        currentRegion_.filename = FilenameText.getText();
        //make sure region name is unique      
        currentRegion_ = makeUniqueRegionName(currentRegion_);
        FilenameText.setText(currentRegion_.filename); //in case filename has changed
        rlm_.addRegion(currentRegion_);
        currentRegion_ = new Region(new PositionList(), DirectoryText.getText(), FilenameText.getText()); //clear        
        regionText.setText("Current Region: 0 images");
        logMessage("Starting new Region");
    }//GEN-LAST:event_AddPositionListActionPerformed

    private void GotoLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GotoLeftActionPerformed
        //Go to min X and center Y coordinate
        Region r;
        PositionList bBox;
        MultiStagePosition center, minPos;
        r = rlm_.getRegion(AcquireList.getSelectedIndex());
        bBox = r.boundingBox();
        center = r.center();
        minPos = bBox.getPosition(0);
       try {
           gui_.setXYStagePosition(minPos.getX(), center.getY());
       } catch (MMScriptException ex) {
           handleError(ex);
       }
    }//GEN-LAST:event_GotoLeftActionPerformed

    private void DeleteRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteRegionActionPerformed
        rlm_.deleteRegion(AcquireList.getSelectedIndex());
    }//GEN-LAST:event_DeleteRegionActionPerformed

    private void GotoCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GotoCenterActionPerformed
        //Go to center X and Y coordinate
        Region r;
        MultiStagePosition center;
        r = rlm_.getRegion(AcquireList.getSelectedIndex());
        center = r.center();
       try {
           gui_.setXYStagePosition(center.getX(), center.getY());
       } catch (MMScriptException ex) {
           handleError(ex);
       }
    }//GEN-LAST:event_GotoCenterActionPerformed

    private void GotoTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GotoTopActionPerformed
        //Go to center X and max Y coordinate
        Region r;
        PositionList bBox;
        MultiStagePosition center, maxPos;
        r = rlm_.getRegion(AcquireList.getSelectedIndex());
        bBox = r.boundingBox();
        center = r.center();
        maxPos = bBox.getPosition(1);
       try {
           gui_.setXYStagePosition(center.getX(), maxPos.getY());
       } catch (MMScriptException ex) {
           handleError(ex);
       }
    }//GEN-LAST:event_GotoTopActionPerformed

    private void GotoBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GotoBottomActionPerformed
        //Go to center X and min Y coordinate
        int index;
        Region r;
        PositionList bBox;
        MultiStagePosition center, minPos;
        index = AcquireList.getSelectedIndex();
        r = rlm_.getRegion(index);
        bBox = r.boundingBox();
        center = r.center();
        minPos = bBox.getPosition(0);
       try {
           gui_.setXYStagePosition(center.getX(), minPos.getY());
       } catch (MMScriptException ex) {
           handleError(ex);
       }
    }//GEN-LAST:event_GotoBottomActionPerformed

    private void GotoRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GotoRightActionPerformed
        //Go to max X and center Y coordinate
        Region r;
        PositionList bBox;
        MultiStagePosition center, maxPos;
        r = rlm_.getRegion(AcquireList.getSelectedIndex());
        bBox = r.boundingBox();
        center = r.center();
        maxPos = bBox.getPosition(1);
       try {
           gui_.setXYStagePosition(maxPos.getX(), center.getY());
       } catch (MMScriptException ex) {
            handleError(ex);
       }
    }//GEN-LAST:event_GotoRightActionPerformed

    private void DirectoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DirectoryButtonActionPerformed
      File f = FileDialogs.openDir(this, "Directory to save to",
      new FileDialogs.FileType("SaveDir", "Save Directory",
          "D:\\Data", true, ""));
      DirectoryText.setText(f.getAbsolutePath());
    }//GEN-LAST:event_DirectoryButtonActionPerformed

    private void StartAcquisitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartAcquisitionActionPerformed
        //Loop over saved regions, updating Acquisition position list and filename and acquiring
        //start separate thread for acquisition
        acqThread a = new acqThread();
        a.start();
    }//GEN-LAST:event_StartAcquisitionActionPerformed

    private void deleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllButtonActionPerformed
        rlm_.clearRegions();
    }//GEN-LAST:event_deleteAllButtonActionPerformed

    private void addPointToRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPointToRegionActionPerformed
        // Record coordinates of core XY stage and all single axis (Z) stages
        // Build grid in XY and fit a plane to each Z axis
        // could consider adding checkboxes for which axes to track
        
      MultiStagePosition msp = new MultiStagePosition();
      msp.setDefaultXYStage(mmc_.getXYStageDevice());
      msp.setDefaultZStage(mmc_.getFocusDevice());
      String message ="";

      // read 1-axis stages
      try {
         StrVector stages = mmc_.getLoadedDevicesOfType(DeviceType.StageDevice);
         for (int i=0; i<stages.size(); i++) {
               StagePosition sp = new StagePosition();
               sp.stageName = stages.get(i);
               sp.numAxes = 1;
               sp.x = mmc_.getPosition(stages.get(i));
               msp.add(sp);
               message = message + sp.stageName + ": " + Double.toString(sp.x) + " ";
         }

         StagePosition sp = new StagePosition();
         sp.stageName = mmc_.getXYStageDevice();
         sp.numAxes = 2;
         sp.x = mmc_.getXPosition(mmc_.getXYStageDevice());
         sp.y = mmc_.getYPosition(mmc_.getXYStageDevice());
         msp.add(sp);
         message = "Added point X: " + Double.toString(sp.x) + " Y: " 
                 + Double.toString(sp.y) + " " + message;
         
         currentRegion_.positions.addPosition(msp);         
         //update text
         int nX = currentRegion_.getNumXTiles(getXFieldSize());
         int nY = currentRegion_.getNumYTiles(getYFieldSize());
         String fieldSize = "Current Region: " + String.valueOf(nX*nY) 
                 + " images (" + String.valueOf(nX) + " x " +String.valueOf(nY) +")";
         regionText.setText(fieldSize);
         //log position added and new grid coordinates
         logMessage(message);
         logMessage("Grid now " + Integer.toString(nX) + " x " +
                 Integer.toString(nY)); 
      } catch (Exception e) {
         handleError(e);
      }

    }//GEN-LAST:event_addPointToRegionActionPerformed

    private void DirectoryTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DirectoryTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DirectoryTextActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(AcquireMultipleRegionsForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(AcquireMultipleRegionsForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(AcquireMultipleRegionsForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(AcquireMultipleRegionsForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new AcquireMultipleRegionsForm().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList AcquireList;
    private javax.swing.JButton AddPositionList;
    private javax.swing.JButton DeleteRegion;
    private javax.swing.JButton DirectoryButton;
    private javax.swing.JTextField DirectoryText;
    private javax.swing.JTextField FilenameText;
    private javax.swing.JButton GotoBottom;
    private javax.swing.JButton GotoCenter;
    private javax.swing.JButton GotoLeft;
    private javax.swing.JButton GotoRight;
    private javax.swing.JButton GotoTop;
    private javax.swing.JButton StartAcquisition;
    private javax.swing.JButton addPointToRegion;
    private javax.swing.JButton deleteAllButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField overlapText;
    private javax.swing.JLabel regionText;
    private javax.swing.JLabel statusText;
    // End of variables declaration//GEN-END:variables
}
