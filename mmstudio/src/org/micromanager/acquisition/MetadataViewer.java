/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MetadataViewer.java
 *
 * Created on Jul 16, 2010, 11:18:45 AM
 */
package org.micromanager.acquisition;

import ij.CompositeImage;
import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.ImageWindow;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import mmcorej.TaggedImage;
import org.micromanager.api.ImageFocusListener;
import org.micromanager.utils.GUIUtils;
import org.micromanager.utils.MDUtils;
import org.micromanager.utils.ReportingUtils;

/**
 *
 * @author arthur
 */
public class MetadataViewer extends javax.swing.JFrame
        implements ImageListener, ImageFocusListener {

   private static MetadataViewer singletonViewer_ = null;

   private final MetadataTableModel imageMetadataModel_;
   private final MetadataTableModel summaryMetadataModel_;
   private ImageWindow currentWindow_ = null;
   private final String [] columnNames_ = {"Property","Value"};
   private MMImageCache cache_;
   private boolean showUnchangingKeys_;
   private ChannelsModel channelsModel_;

   
   /** Creates new form MetadataViewer */
   public MetadataViewer() {
      initComponents();
      imageMetadataModel_ = new MetadataTableModel();
      summaryMetadataModel_ = new MetadataTableModel();
      ImagePlus.addImageListener(this);
      GUIUtils.registerImageFocusListener(this);
      
      update(ij.IJ.getImage());
      imageMetadataTable.setModel(imageMetadataModel_);
      summaryMetadataTable.setModel(summaryMetadataModel_);

      channelsModel_ = new ChannelsModel();
      ChannelsTable.setModel(channelsModel_);
      ColorCellRenderer colorRenderer = new ColorCellRenderer();
      ChannelsTable.setDefaultRenderer(Color.class, colorRenderer);
      setDisplayState(CompositeImage.COMPOSITE);
   }

   public static MetadataViewer showMetadataViewer() {
      if (singletonViewer_ == null) {
         singletonViewer_ = new MetadataViewer();
         GUIUtils.recallPosition(singletonViewer_);
      }
      singletonViewer_.setVisible(true);
      return singletonViewer_;
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jScrollPane2 = new javax.swing.JScrollPane();
      jTextArea1 = new javax.swing.JTextArea();
      tabbedPane = new javax.swing.JTabbedPane();
      ChannelsTablePanel = new javax.swing.JPanel();
      ChannelsTableScrollPane = new javax.swing.JScrollPane();
      ChannelsTable = new javax.swing.JTable();
      jPanel1 = new javax.swing.JPanel();
      OverlayButton = new javax.swing.JToggleButton();
      ColorButton = new javax.swing.JToggleButton();
      GrayButton = new javax.swing.JToggleButton();
      Comments = new javax.swing.JScrollPane();
      commentsTextArea = new javax.swing.JTextArea();
      Summary = new javax.swing.JScrollPane();
      summaryMetadataTable = new javax.swing.JTable();
      Image = new javax.swing.JPanel();
      metadataTableScrollPane = new javax.swing.JScrollPane();
      imageMetadataTable = new javax.swing.JTable();
      showUnchangingPropertiesCheckbox = new javax.swing.JCheckBox();

      jTextArea1.setColumns(20);
      jTextArea1.setRows(5);
      jScrollPane2.setViewportView(jTextArea1);

      setTitle("Metadata and Comments");

      tabbedPane.setFocusable(false);

      ChannelsTable.setColumnSelectionAllowed(true);
      ChannelsTable.getTableHeader().setReorderingAllowed(false);
      ChannelsTableScrollPane.setViewportView(ChannelsTable);
      ChannelsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

      jPanel1.setLayout(new java.awt.GridLayout());

      OverlayButton.setText("Overlay");
      OverlayButton.setFocusable(false);
      OverlayButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            OverlayButtonActionPerformed(evt);
         }
      });
      jPanel1.add(OverlayButton);

      ColorButton.setText("Color");
      ColorButton.setFocusable(false);
      ColorButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            ColorButtonActionPerformed(evt);
         }
      });
      jPanel1.add(ColorButton);

      GrayButton.setText("Gray");
      GrayButton.setFocusable(false);
      GrayButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            GrayButtonActionPerformed(evt);
         }
      });
      jPanel1.add(GrayButton);

      org.jdesktop.layout.GroupLayout ChannelsTablePanelLayout = new org.jdesktop.layout.GroupLayout(ChannelsTablePanel);
      ChannelsTablePanel.setLayout(ChannelsTablePanelLayout);
      ChannelsTablePanelLayout.setHorizontalGroup(
         ChannelsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
         .add(org.jdesktop.layout.GroupLayout.TRAILING, ChannelsTableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
      );
      ChannelsTablePanelLayout.setVerticalGroup(
         ChannelsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(ChannelsTablePanelLayout.createSequentialGroup()
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(ChannelsTableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE))
      );

      tabbedPane.addTab("Display", ChannelsTablePanel);

      commentsTextArea.setColumns(20);
      commentsTextArea.setLineWrap(true);
      commentsTextArea.setRows(5);
      commentsTextArea.setWrapStyleWord(true);
      commentsTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusLost(java.awt.event.FocusEvent evt) {
            commentsTextAreaFocusLost(evt);
         }
      });
      Comments.setViewportView(commentsTextArea);

      tabbedPane.addTab("Comments", Comments);

      summaryMetadataTable.setModel(new javax.swing.table.DefaultTableModel(
         new Object [][] {
            {null, null},
            {null, null},
            {null, null},
            {null, null}
         },
         new String [] {
            "Property", "Value"
         }
      ) {
         boolean[] canEdit = new boolean [] {
            false, false
         };

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
         }
      });
      Summary.setViewportView(summaryMetadataTable);

      tabbedPane.addTab("Summary", Summary);

      Image.setOpaque(false);

      imageMetadataTable.setModel(new javax.swing.table.DefaultTableModel(
         new Object [][] {

         },
         new String [] {
            "Property", "Value"
         }
      ) {
         Class[] types = new Class [] {
            java.lang.String.class, java.lang.String.class
         };
         boolean[] canEdit = new boolean [] {
            false, false
         };

         public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
         }

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
         }
      });
      imageMetadataTable.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
      imageMetadataTable.setDoubleBuffered(true);
      metadataTableScrollPane.setViewportView(imageMetadataTable);

      showUnchangingPropertiesCheckbox.setText("Show unchanging properties");
      showUnchangingPropertiesCheckbox.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            showUnchangingPropertiesCheckboxActionPerformed(evt);
         }
      });

      org.jdesktop.layout.GroupLayout ImageLayout = new org.jdesktop.layout.GroupLayout(Image);
      Image.setLayout(ImageLayout);
      ImageLayout.setHorizontalGroup(
         ImageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(ImageLayout.createSequentialGroup()
            .add(showUnchangingPropertiesCheckbox)
            .addContainerGap(456, Short.MAX_VALUE))
         .add(metadataTableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
      );
      ImageLayout.setVerticalGroup(
         ImageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(ImageLayout.createSequentialGroup()
            .add(showUnchangingPropertiesCheckbox)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(metadataTableScrollPane))
      );

      tabbedPane.addTab("Image", Image);

      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
            .addContainerGap()
            .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
            .addContainerGap())
      );

      pack();
   }// </editor-fold>//GEN-END:initComponents

   private void showUnchangingPropertiesCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUnchangingPropertiesCheckboxActionPerformed
      showUnchangingKeys_ = showUnchangingPropertiesCheckbox.isSelected();
      update(ij.IJ.getImage());
   }//GEN-LAST:event_showUnchangingPropertiesCheckboxActionPerformed

   private void commentsTextAreaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_commentsTextAreaFocusLost
      cache_.setComment(commentsTextArea.getText());
   }//GEN-LAST:event_commentsTextAreaFocusLost

   private void OverlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OverlayButtonActionPerformed
      setDisplayState(CompositeImage.COMPOSITE);
   }//GEN-LAST:event_OverlayButtonActionPerformed

   private void ColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ColorButtonActionPerformed
      setDisplayState(CompositeImage.COLOR);
   }//GEN-LAST:event_ColorButtonActionPerformed

   private void GrayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GrayButtonActionPerformed
      setDisplayState(CompositeImage.GRAYSCALE);
   }//GEN-LAST:event_GrayButtonActionPerformed


   private CompositeImage getCurrentCompositeImage() {
      ImagePlus imgp = IJ.getImage();
      if (imgp instanceof CompositeImage) {
         return (CompositeImage) imgp;
      } else {
         return null;
      }
   }

   private void setDisplayState(int state) {
      ImagePlus imgp = IJ.getImage();
      if (imgp instanceof CompositeImage) {
         CompositeImage ci = (CompositeImage) imgp;
         ci.setMode(state);
         ci.updateAndDraw();
         updateStateButtons();
      }
   }

   private void updateStateButtons() {
      CompositeImage ci = getCurrentCompositeImage();
      if (ci != null) {
         int displayState = ci.getMode();
         OverlayButton.setSelected(displayState == CompositeImage.COMPOSITE);
         ColorButton.setSelected(displayState == CompositeImage.COLOR);
         GrayButton.setSelected(displayState == CompositeImage.GRAYSCALE);
         OverlayButton.setEnabled(true);
         ColorButton.setEnabled(true);
         GrayButton.setEnabled(true);
      } else {
         OverlayButton.setSelected(false);
         ColorButton.setSelected(false);
         GrayButton.setSelected(false);
         OverlayButton.setEnabled(false);
         ColorButton.setEnabled(false);
         GrayButton.setEnabled(false);
      }
   }

   class MetadataTableModel extends AbstractTableModel {

      Vector<Vector<String>> data_;

      MetadataTableModel() {
         data_ = new Vector<Vector<String>>();
      }

      public int getRowCount() {
         return data_.size();
      }

      public void addRow(Vector<String> rowData) {
         data_.add(rowData);
      }

      public int getColumnCount() {
         return 2;
      }

      public synchronized Object getValueAt(int rowIndex, int columnIndex) {
         if (data_.size() > rowIndex) {
            Vector<String> row = data_.get(rowIndex);
            if (row.size() > columnIndex)
               return data_.get(rowIndex).get(columnIndex);
            else
               return "";
         } else {
            return "";
         }
      }

      public void clear() {
         data_.clear();
      }

      @Override
      public String getColumnName(int colIndex) {
         return columnNames_[colIndex];
      }

      public synchronized void setMetadata(Map<String,String> md) {
         clear();
         if (md != null) {
            Object[] keys = (Object[]) md.keySet().toArray();
            Arrays.sort(keys);

            for (Object key : keys) {
               Vector<String> rowData = new Vector<String>();
               rowData.add((String) key);
               rowData.add(md.get((String) key));
               addRow(rowData);
            }
         }
         fireTableDataChanged();
      }
   }
   
   private Map<String, String> selectChangingTags(Map<String, String> md) {
      Map<String, String> mdChanging = new HashMap<String, String>();
      if (cache_ != null) {
         for (String key : cache_.getChangingKeys()) {
            if (md.containsKey(key)) {
               mdChanging.put(key, md.get(key));
            }
         }
      }
      return mdChanging;
   }

   private AcquisitionVirtualStack getAcquisitionStack(ImagePlus imp) {
      ImageStack stack = imp.getStack();
      if (stack instanceof AcquisitionVirtualStack) {
         return (AcquisitionVirtualStack) stack;
      } else {
         return null;
      }
   }

   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JTable ChannelsTable;
   private javax.swing.JPanel ChannelsTablePanel;
   private javax.swing.JScrollPane ChannelsTableScrollPane;
   private javax.swing.JToggleButton ColorButton;
   private javax.swing.JScrollPane Comments;
   private javax.swing.JToggleButton GrayButton;
   private javax.swing.JPanel Image;
   private javax.swing.JToggleButton OverlayButton;
   private javax.swing.JScrollPane Summary;
   private javax.swing.JTextArea commentsTextArea;
   private javax.swing.JTable imageMetadataTable;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JTextArea jTextArea1;
   private javax.swing.JScrollPane metadataTableScrollPane;
   private javax.swing.JCheckBox showUnchangingPropertiesCheckbox;
   private javax.swing.JTable summaryMetadataTable;
   private javax.swing.JTabbedPane tabbedPane;
   // End of variables declaration//GEN-END:variables


   //Implements ImageListener
   public void imageOpened(ImagePlus imp) {
      update(imp);
   }

   //Implements ImageListener
   public void imageClosed(ImagePlus imp) {
      if (WindowManager.getCurrentWindow() == null) {
         update((ImagePlus) null);
      }
   }

   //Implements ImageListener
   public void imageUpdated(ImagePlus imp) {
      update(imp);
   }

   private MMImageCache getCache(ImagePlus imgp) {
      AcquisitionVirtualStack stack = getAcquisitionStack(imgp);
      if (stack != null)
         return stack.getCache();
      else
         return null;
   }

   /*
    * update(ImagePlus imp) is called every time the image is changed
    * or the sliders have moved.
    */
   public void update(ImagePlus imp) {
      ChannelSpec channelSpec;
      if (this.isVisible()) {
         if (imp == null) {
            imageMetadataModel_.setMetadata(null);
            commentsTextArea.setText(null);
            channelsModel_.clear();
         } else {
            AcquisitionVirtualStack stack = getAcquisitionStack(imp);
            if (stack != null) {
               int slice = imp.getCurrentSlice();
               TaggedImage taggedImg = stack.getTaggedImage(slice);
               if (taggedImg == null) {
                  imageMetadataModel_.setMetadata(null);
               } else {
                  Map<String,String> md = stack.getTaggedImage(slice).tags;
                  if (!showUnchangingKeys_)
                     md = selectChangingTags(md);
                  imageMetadataModel_.setMetadata(md);
               }
               if (imp instanceof CompositeImage) {
                  CompositeImage cimp = (CompositeImage) imp;
                  channelSpec = new ChannelSpec();
                  try {
                     channelSpec.name = MDUtils.getChannelName(taggedImg.tags);
                  } catch (Exception ex) {
                     ReportingUtils.logError(ex);
                  }
                  channelSpec.show = true;
                  channelSpec.color = cimp.getChannelColor();
               }
            } else {
               imageMetadataModel_.setMetadata(null);
            }

         }
      }
   }

   //Implements AWTEventListener
   /*
    * This is called, in contrast to update(), only when the ImageWindow
    * in focus has changed.
    */
   public void focusReceived(ImageWindow focusedWindow) {
      if (currentWindow_ != focusedWindow) {
         ImagePlus imgp = focusedWindow.getImagePlus();
         cache_ = getCache(imgp);

         if (cache_ != null) {
            commentsTextArea.setText(cache_.getComment());
            Map<String,String> md = cache_.getSummaryMetadata();
            summaryMetadataModel_.setMetadata(md);
         } else {
            commentsTextArea.setText(null);
         }

         currentWindow_ = focusedWindow;

         updateStateButtons();
         update(imgp);
      }
   }

   private class ChannelSpec {
      String name;
      int index;
      Color color;
      Boolean show;
   }

   private class ChannelsModel extends ArrayList<ChannelSpec> implements TableModel {
      String [] columnNames = {"Show?", "Channel", "Color"};
      private ChannelSpec channelSpec;
      public int getRowCount() {
         return size();
      }

      public int getColumnCount() {
         return 3;
      }

      public String getColumnName(int columnIndex) {
         return columnNames[columnIndex];
      }

      public Class<?> getColumnClass(int columnIndex) {
         return getValueAt(0, columnIndex).getClass();
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
         return (columnIndex == 0 || columnIndex == 2);
      }

      public Object getValueAt(int rowIndex, int columnIndex) {
         channelSpec = get(rowIndex);
         if (rowIndex == 0) {
            return channelSpec.show;
         } else if (rowIndex == 1) {
            return channelSpec.name;
         } else if (rowIndex == 2) {
            return channelSpec.color;
         } else {
            return null;
         }
      }

      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void addTableModelListener(TableModelListener l) {
         //throw new UnsupportedOperationException("Not supported yet.");
      }

      public void removeTableModelListener(TableModelListener l) {
         //throw new UnsupportedOperationException("Not supported yet.");
      }
      
   }


   private class ColorCellRenderer implements TableCellRenderer {
      private final JLabel colorLabel_;
      ColorCellRenderer() {
         colorLabel_ = new JLabel();
         colorLabel_.setOpaque(true);
      }

      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
         colorLabel_.setBackground((Color) value);
         return colorLabel_;
      }
   }
}
