/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DetectorJDialog.java
 *
 * Created on Feb 14, 2011, 9:48:28 AM
 */

package org.micromanager.conf;

/**
 *
 * @author karlhoover
 */
public class DetectorJDialog extends javax.swing.JDialog {

    public boolean CancelRequest(){
        return cancelRequest_;
    }

    public void ProgressText( String t){
        detectionTextArea_.setText(t);
    }

    public String ProgressText(){
        return detectionTextArea_.getText();
    }

    private boolean cancelRequest_;


    /** Creates new form DetectorJDialog */
    public DetectorJDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cancelRequest_ = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jScrollPane1 = new javax.swing.JScrollPane();
        detectionTextArea_ = new javax.swing.JTextArea();
        cancelButton_ = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        detectionTextArea_.setColumns(20);
        detectionTextArea_.setRows(5);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, detectionTextArea_, org.jdesktop.beansbinding.ELProperty.create("${text}"), detectionTextArea_, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(detectionTextArea_);

        cancelButton_.setText("Cancel");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cancelButton_, org.jdesktop.beansbinding.ELProperty.create("${selected}"), cancelButton_, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        cancelButton_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButton_ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                    .add(cancelButton_))
                .add(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(24, 24, 24)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .add(37, 37, 37)
                .add(cancelButton_)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButton_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButton_ActionPerformed

        System.out.print("cancelButton_ActionPerformed");
        cancelRequest_ = true;

    }//GEN-LAST:event_cancelButton_ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        cancelRequest_ = false;
    }//GEN-LAST:event_formComponentShown

 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton_;
    private javax.swing.JTextArea detectionTextArea_;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
