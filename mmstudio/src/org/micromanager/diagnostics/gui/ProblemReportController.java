// AUTHOR:       Mark Tsuchida
// COPYRIGHT:    University of California, San Francisco, 2014
// LICENSE:      This file is distributed under the BSD license.
//               License text is included with the source distribution.
//               This file is distributed in the hope that it will be useful,
//               but WITHOUT ANY WARRANTY; without even the implied warranty
//               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.

package org.micromanager.diagnostics.gui;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.micromanager.diagnostics.ProblemReport;
import org.micromanager.diagnostics.ProblemReportFormatter;
import org.micromanager.diagnostics.ReportSender;


/**
 * Controller for "Report Problem" GUI.
 *
 * The only public interface is start().
 *
 * All methods must be called on the EDT.
 */
public class ProblemReportController {
   /*
    * Static fields and methods
    */

   private static ProblemReportController instance_;

   /**
    * Activates the Problem Report window. If one is already open, brings it to
    * the front; if not, creates the window.
    */
   public static void start(final mmcorej.CMMCore core) {
      if (instance_ == null) {
         instance_ = new ProblemReportController(core);
      }
      instance_.showFrame();
   }

   /*
    * Instance fields and methods
    */

   private final mmcorej.CMMCore core_;

   private ProblemReportFrame frame_ = null;
   private boolean hasUnsentContent_ = false;
   private javax.swing.JTextArea descriptionTextArea_;

   // The problem report model may be accessed from a background thread
   private volatile ProblemReport report_;

   // Constructed solely by static method
   private ProblemReportController(final mmcorej.CMMCore core) {
      core_ = core;
   }

   void showFrame() {
      if (frame_ == null) {
         boolean continueLeftover = false;
         if (loadLeftoverReport()) {
            int answer = JOptionPane.showOptionDialog(frame_,
                  "A Problem Report was in progress when Micro-Manager " +
                  "exited. Would you like to load the leftover report?",
                  "Continue Problem Report",
                  JOptionPane.YES_NO_CANCEL_OPTION,
                  JOptionPane.QUESTION_MESSAGE, null,
                  new String[]{"Load Interrupted Report",
                     "Discard and Start New",
                     "Cancel"}, null);
            if (answer == JOptionPane.YES_OPTION) {
               continueLeftover = true;
            }
            else if (answer == JOptionPane.NO_OPTION) {
               report_.deleteStorage();
               report_ = null;
            }
            else {
               return;
            }
         }
         frame_ = new ProblemReportFrame(this);
         if (continueLeftover) {
            frame_.setControlPanel(new SendReportControlPanel(this, false));
            markReportUnsent();
         }
      }

      frame_.setVisible(true);

      int state = frame_.getExtendedState();
      state &= ~javax.swing.JFrame.ICONIFIED;
      frame_.setExtendedState(state);

      frame_.toFront();
      frame_.requestFocus();
   }

   private File getReportDirectory() {
      String homePath = System.getProperty("user.home");
      if (homePath != null && !homePath.isEmpty()) {
         File homeDir = new File(homePath);
         if (homeDir.isDirectory()) {
            return new File(homeDir, "MMProblemReport");
         }
      }
      return null;
   }

   private boolean loadLeftoverReport() {
      File reportDir = getReportDirectory();
      report_ = ProblemReport.LoadFromPersistence(reportDir);
      if (report_.isUsefulReport()) {
         return true;
      }
      report_.deleteStorage();
      return false;
   }

   private boolean isContactInfoValid() {
      if (getName() == null || getName().length() < 1) {
         return false;
      }
      if (getOrganization() == null || getOrganization().length() < 1) {
         return false;
      }
      if (getEmail() == null || !isValidEmailAddress(getEmail())) {
         return false;
      }
      return true;
   }

   private static boolean isValidEmailAddress(String addr) {
      java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
         "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
      return pattern.matcher(addr).matches();
   }

   /*
    * Accessors
    */

   private void markReportUnsent() {
      hasUnsentContent_ = true;

      if (frame_.getControlPanel() instanceof SendReportControlPanel) {
         SendReportControlPanel panel = (SendReportControlPanel)frame_.getControlPanel();
         panel.setUIMode(SendReportControlPanel.UIMode.UNSENT);
      }
   }

   private void markReportSent() {
      hasUnsentContent_ = false;

      if (frame_.getControlPanel() instanceof SendReportControlPanel) {
         SendReportControlPanel panel = (SendReportControlPanel)frame_.getControlPanel();
         panel.setUIMode(SendReportControlPanel.UIMode.SENT);
      }
   }

   void markDescriptionModified() {
      copyDescriptionToReport();
      markReportUnsent();
   }

   void setDescriptionTextArea(javax.swing.JTextArea textArea) {
      descriptionTextArea_ = textArea;
   }

   javax.swing.JTextArea getDescriptionTextArea() {
      return descriptionTextArea_;
   }

   void setName(String name) {
      if (report_ != null)
         report_.setUserName(name);
   }

   String getName() {
      return report_ == null ? null : report_.getUserName();
   }

   void setOrganization(String organization) {
      if (report_ != null)
         report_.setUserOrganization(organization);
   }

   String getOrganization() {
      return report_ == null ? null : report_.getUserOrganization();
   }

   void setEmail(String email) {
      if (report_ != null)
         report_.setUserEmail(email);
   }

   String getEmail() {
      return report_ == null ? null : report_.getUserEmail();
   }

   /*
    * UI actions
    */

   void controlPanelDidChangeSize(ControlPanel panel) {
      frame_.setControlPanel(panel);
   }

   void cancelRequested() {
      if (hasUnsentContent_) {
         int result = JOptionPane.showConfirmDialog(frame_,
               "Discard this report?", "Cancel Problem Report",
               JOptionPane.YES_NO_OPTION);
         if (result != JOptionPane.YES_OPTION) {
            return;
         }
      }

      if (report_ != null) {
         report_.cancelLogCapture();
         report_.deleteStorage();
         report_ = null;
      }

      frame_.close();
      frame_ = null;
   }

   void startLogCapture() {
      if (report_ != null) {
         report_.cancelLogCapture();
         report_.deleteStorage();
         report_ = null;
      }

      final LogCaptureControlPanel panel = new LogCaptureControlPanel(this);
      panel.setStatus("Collecting system information...");
      panel.setButtonsEnabled(false);
      frame_.setControlPanel(panel);

      File reportDir = getReportDirectory();
      report_ = ProblemReport.NewPersistentReport(core_, reportDir);
      copyDescriptionToReport();

      report_.startCapturingLog();
      new SwingWorker<Object, Object>() {
         @Override
         public Object doInBackground() {
            try {
               report_.logSystemInfo(false);
            }
            catch (Exception e) {
               core_.logMessage("Logging system info failed: " + e.getMessage());
            }
            return null;
         }

         @Override
         public void done() {
            if (report_ == null) { // Canceled
               return;
            }

            if (frame_ != null) {
               panel.setStatus("Capturing log...");
               panel.setInstructions("<html><b>Please perform the steps that reproduce the problem.</b></html>");
               panel.setButtonsEnabled(true);
               frame_.setControlPanel(panel);
            }

            core_.logMessage("User has been prompted to reproduce problem");
         }
      }.execute();

      markReportUnsent();
   }

   void finishLogCapture() {
      if (report_ == null) { // Canceled
         return;
      }

      core_.logMessage("User has stopped log capture");

      if (frame_ != null) {
         final LogCaptureControlPanel panel = (LogCaptureControlPanel) frame_.getControlPanel();
         panel.setButtonsEnabled(false);
         panel.setStatus("Collecting system information...");
         panel.setInstructions("Please wait.");
         frame_.setControlPanel(panel);
      }

      new SwingWorker<Object, Object>() {
         @Override
         public Object doInBackground() {
            try {
               report_.logSystemInfo(true);
            }
            catch (Exception e) {
               core_.logMessage("Logging system info failed: " + e.getMessage());
            }
            report_.finishCapturingLog();
            return null;
         }

         @Override
         public void done() {
            if (report_ == null) { // Canceled
               return;
            }
            if (frame_ != null) {
               frame_.setControlPanel(new SendReportControlPanel(ProblemReportController.this));
            }
         }
      }.execute();
   }

   void finishWithoutReproducing() {
      if (report_ == null) { // Canceled
         return;
      }

      core_.logMessage("User has indicated that the problem cannot be reproduced");

      if (frame_ != null) {
         final LogCaptureControlPanel panel = (LogCaptureControlPanel) frame_.getControlPanel();
         panel.setButtonsEnabled(false);
         panel.setStatus("Saving log...");
         panel.setInstructions("Please wait.");
         frame_.setControlPanel(panel);
      }

      new SwingWorker<Object, Object>() {
         @Override
         public Object doInBackground() {
            report_.finishCapturingLog();
            return null;
         }

         @Override
         public void done() {
            if (report_ == null) { // Canceled
               return;
            }
            if (frame_ != null) {
               frame_.setControlPanel(new SendReportControlPanel(ProblemReportController.this));
            }
         }
      }.execute();
   }

   void nameChanged(javax.swing.text.Document nameDocument) {
      try {
         setName(nameDocument.getText(0, nameDocument.getLength()));
         markReportUnsent();
      }
      catch (javax.swing.text.BadLocationException impossible) {
      }
   }

   void organizationChanged(javax.swing.text.Document organizationDocument) {
      try {
         setOrganization(organizationDocument.getText(0, organizationDocument.getLength()));
         markReportUnsent();
      }
      catch (javax.swing.text.BadLocationException impossible) {
      }
   }

   void emailChanged(javax.swing.text.Document emailDocument) {
      try {
         setEmail(emailDocument.getText(0, emailDocument.getLength()));
         markReportUnsent();
      }
      catch (javax.swing.text.BadLocationException impossible) {
      }
   }

   void displayReport() {
      if (report_ == null) {
         return; // Should not happen
      }

      ProblemReportFormatter formatter = new ProblemReportFormatter();
      String reportStr = formatter.format(report_);

      openReportWindow(reportStr);
   }

   void sendRequested() {
      if (report_ == null) {
         return; // Should not happen
      }

      if (descriptionTextArea_.getDocument().getLength() == 0) {
         JOptionPane.showMessageDialog(frame_, "Please enter a description.");
         return;
      }

      if (!isContactInfoValid()) {
         JOptionPane.showMessageDialog(frame_, "Please enter your name, organization, and valid email address.");
         return;
      }

      if (!confirmEmailAddress()) {
         return;
      }

      final SendReportControlPanel panel = (SendReportControlPanel) frame_.getControlPanel();
      panel.setUIMode(SendReportControlPanel.UIMode.SENDING);
      getDescriptionTextArea().setEnabled(false);

      new SwingWorker<Boolean, Object>() {
         @Override
         public Boolean doInBackground() throws Exception {
            ProblemReportFormatter formatter = new ProblemReportFormatter();
            String reportStr = formatter.format(report_);
            String reportFileName = formatter.generateFileName(report_);

            java.net.URL url = ReportSender.getProblemReportUploadURL();

            ReportSender sender = new ReportSender();
            sender.sendReport(reportStr, reportFileName, url);

            return true;
         }

         @Override
         public void done() {
            boolean sendSuccessful = false;
            try {
               sendSuccessful = get();
            }
            catch (Exception e) {
               JOptionPane.showMessageDialog(frame_,
                     "Failed to generate or send report:\n" + e.getMessage());
               panel.setUIMode(SendReportControlPanel.UIMode.UNSENT);
               getDescriptionTextArea().setEnabled(true);
               return;
            }
            if (!sendSuccessful) {
               return; // Should have been an exception
            }

            markReportSent();
            panel.setUIMode(SendReportControlPanel.UIMode.SENT);
            getDescriptionTextArea().setEnabled(true);
         }
      }.execute();
   }

   private boolean confirmEmailAddress() {
      String confirmEmail = JOptionPane.showInputDialog(frame_,
            "Please enter your email address once more:",
            "Send Problem Report", JOptionPane.QUESTION_MESSAGE);
      if (confirmEmail == null) {
         return false;
      }
      if (!confirmEmail.equals(getEmail())) {
         JOptionPane.showMessageDialog(frame_,
               "Email address does not match; please check your typing.");
         return false;
      }
      return true;
   }

   private void copyDescriptionToReport() {
      if (report_ == null) {
         return;
      }

      String description;
      try {
         description = descriptionTextArea_.getDocument().getText(0,
               descriptionTextArea_.getDocument().getLength());
      }
      catch (javax.swing.text.BadLocationException impossible) {
         description = null;
      }

      report_.setDescription(description);
   }

   private void openReportWindow(String report) {
      final int width = 640, height = 480;
      ij.text.TextWindow window = new ij.text.TextWindow("Problem Report", report, width, height);
      ij.text.TextPanel panel = window.getTextPanel();
      panel.scrollToTop();
      window.setVisible(true);
   }
}
