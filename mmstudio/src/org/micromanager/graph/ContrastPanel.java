///////////////////////////////////////////////////////////////////////////////
//FILE:          ContrastPanel.java
//PROJECT:       Micro-Manager
//SUBSYSTEM:     mmstudio
//-----------------------------------------------------------------------------
//
// AUTHOR:       Nenad Amodaj, nenad@amodaj.com, October 29, 2006
//
// COPYRIGHT:    University of California, San Francisco, 2006
//
// LICENSE:      This file is distributed under the BSD license.
//               License text is included with the source distribution.
//
//               This file is distributed in the hope that it will be useful,
//               but WITHOUT ANY WARRANTY; without even the implied warranty
//               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
//
// CVS:          $Id$
//
package org.micromanager.graph;

import ij.CompositeImage;
import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.ImageWindow;

import ij.process.ColorProcessor;
import ij.process.ImageStatistics;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ij.process.ShortProcessor;
import ij.process.ByteProcessor;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.micromanager.MMStudioMainFrame;
import org.micromanager.utils.ImageFocusListener;
import org.micromanager.graph.HistogramPanel.CursorListener;

import org.micromanager.utils.ContrastSettings;
import org.micromanager.utils.GUIUtils;
import org.micromanager.utils.GammaSliderCalculator;
import org.micromanager.utils.HistogramUtils;
import org.micromanager.utils.ImageController;
import org.micromanager.utils.MMImageWindow;
import org.micromanager.utils.ReportingUtils;
import org.micromanager.utils.NumberUtils;

/**
 * Slider and histogram panel for adjusting contrast and brightness.
 * 
 */
public class ContrastPanel extends JPanel implements ImageController,
        PropertyChangeListener, ImageFocusListener, ImageListener,
         CursorListener {
	private static final long serialVersionUID = 1L;
	private JComboBox modeComboBox_;
	private HistogramPanel histogramPanel_;
	private JLabel maxLabel_;
	private JLabel minLabel_;
   private JLabel meanLabel_;
   private JLabel stdDevLabel_;
	private SpringLayout springLayout;
	private ImagePlus image_;
	private GraphData histogramData_;
   private GammaSliderCalculator gammaSliderCalculator_;
   private JFormattedTextField gammaValue_;
   private NumberFormat numberFormat_;
   private double gamma_ = 1.0;
	private int maxIntensity_ = 255;
   private double mean_;
   private double stdDev_;
   private double min_ = 0.0;
   private double max_ = 255.0;
	private int binSize_ = 1;
	private static final int HIST_BINS = 256;
	private int numLevels_ = 256;
   //private DecimalFormat twoDForm_ = new DecimalFormat("#.##");
	ContrastSettings cs8bit_;
	ContrastSettings cs16bit_;
	private JCheckBox stretchCheckBox_;
	private JCheckBox rejectOutliersCheckBox_;
   private JCheckBox disableHistogramCheckBox_;
   private boolean disableHistogram_ = false;
	private boolean logScale_ = false;
	private JCheckBox logHistCheckBox_;
   private boolean imageUpdated_;
   private boolean liveWindow_;
   private boolean liveStretchMode_ = true;
   private double lutMin_;
   private double lutMax_;
	private double minAfterRejectingOutliers_;
	private double maxAfterRejectingOutliers_;
   JSpinner rejectOutliersPercentSpinner_;
   private double fractionToReject_;
   JLabel percentOutliersLabel_;
   private int[] histogram_;



	/**
	 * Create the panel
	 */
	public ContrastPanel() {
		super();

      HistogramUtils h = new HistogramUtils(null);
      fractionToReject_ = h.getFractionToReject(); // get the default value
		setToolTipText("Switch between linear and log histogram");
		setFont(new Font("", Font.PLAIN, 10));
		springLayout = new SpringLayout();
		setLayout(springLayout);

      numberFormat_ = NumberFormat.getNumberInstance();

		final JButton fullScaleButton_ = new JButton();
		fullScaleButton_.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				setFullScale();
			}
		});
		fullScaleButton_.setFont(new Font("Arial", Font.PLAIN, 10));
		fullScaleButton_
				.setToolTipText("Set display levels to full pixel range");
		fullScaleButton_.setText("Full");
		add(fullScaleButton_);
		springLayout.putConstraint(SpringLayout.EAST, fullScaleButton_, 80,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, fullScaleButton_, 5,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, fullScaleButton_, 25,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, fullScaleButton_, 5,
				SpringLayout.NORTH, this);

		final JButton autoScaleButton = new JButton();
		autoScaleButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				setAutoScale();
			}
		});
		autoScaleButton.setFont(new Font("Arial", Font.PLAIN, 10));
		autoScaleButton
				.setToolTipText("Set display levels to maximum contrast");
		autoScaleButton.setText("Auto");
		add(autoScaleButton);
		springLayout.putConstraint(SpringLayout.EAST, autoScaleButton, 80,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, autoScaleButton, 5,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, autoScaleButton, 46,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, autoScaleButton, 26,
				SpringLayout.NORTH, this);

		minLabel_ = new JLabel();
		minLabel_.setFont(new Font("", Font.PLAIN, 10));
		add(minLabel_);
		springLayout.putConstraint(SpringLayout.EAST, minLabel_, 95,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, minLabel_, 45,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, minLabel_, 78,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, minLabel_, 64,
				SpringLayout.NORTH, this);

      maxLabel_ = new JLabel();
		maxLabel_.setFont(new Font("", Font.PLAIN, 10));
		add(maxLabel_);
		springLayout.putConstraint(SpringLayout.EAST, maxLabel_, 95,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, maxLabel_, 45,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, maxLabel_, 94,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, maxLabel_, 80,
				SpringLayout.NORTH, this);

		JLabel minLabel = new JLabel();
		minLabel.setFont(new Font("", Font.PLAIN, 10));
		minLabel.setText("Min");
		add(minLabel);
		springLayout.putConstraint(SpringLayout.SOUTH, minLabel, 78,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, minLabel, 64,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, minLabel, 30,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, minLabel, 5,
				SpringLayout.WEST, this);

		JLabel maxLabel = new JLabel();
		maxLabel.setFont(new Font("", Font.PLAIN, 10));
		maxLabel.setText("Max");
		add(maxLabel);
		springLayout.putConstraint(SpringLayout.SOUTH, maxLabel, 94,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, maxLabel, 80,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, maxLabel, 30,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, maxLabel, 5,
				SpringLayout.WEST, this);

      JLabel avgLabel = new JLabel();
      avgLabel.setFont(new Font("", Font.PLAIN, 10));
      avgLabel.setText("Avg");
      add(avgLabel);
      springLayout.putConstraint(SpringLayout.EAST, avgLabel, 42, SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.WEST, avgLabel, 5, SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.SOUTH, avgLabel, 110, SpringLayout.NORTH, this);
      springLayout.putConstraint(SpringLayout.NORTH, avgLabel, 96, SpringLayout.NORTH, this);

      meanLabel_ = new JLabel();                                              
      meanLabel_.setFont(new Font("", Font.PLAIN, 10));                       
      add(meanLabel_);                                                        
      springLayout.putConstraint(SpringLayout.EAST, meanLabel_, 95, SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.WEST, meanLabel_, 45, SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.SOUTH, meanLabel_, 110, SpringLayout.NORTH, this);
      springLayout.putConstraint(SpringLayout.NORTH, meanLabel_, 96, SpringLayout.NORTH, this);
                                                                             
      JLabel varLabel = new JLabel();
      varLabel.setFont(new Font("", Font.PLAIN, 10));
      varLabel.setText("Std Dev");
      add(varLabel);
      springLayout.putConstraint(SpringLayout.SOUTH, varLabel, 126, SpringLayout.NORTH, this);
      springLayout.putConstraint(SpringLayout.NORTH, varLabel, 112, SpringLayout.NORTH, this);
      springLayout.putConstraint(SpringLayout.EAST, varLabel, 42, SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.WEST, varLabel, 5, SpringLayout.WEST, this);

      stdDevLabel_ = new JLabel();                                              
      stdDevLabel_.setFont(new Font("", Font.PLAIN, 10));                       
      add(stdDevLabel_);
      springLayout.putConstraint(SpringLayout.EAST, stdDevLabel_, 95, SpringLayout.WEST, this); 
      springLayout.putConstraint(SpringLayout.WEST, stdDevLabel_, 45, SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.SOUTH, stdDevLabel_, 126, SpringLayout.NORTH, this);
      springLayout.putConstraint(SpringLayout.NORTH, stdDevLabel_, 112, SpringLayout.NORTH, this);

      final int gammaLow = 0;
      final int gammaHigh = 100;
      gammaSliderCalculator_ = new GammaSliderCalculator(gammaLow, gammaHigh);

      JLabel gammaLabel = new JLabel();
      gammaLabel.setFont(new Font("Arial", Font.PLAIN, 10));
      gammaLabel.setPreferredSize(new Dimension(40, 20));
      gammaLabel.setText("Gamma");
      add(gammaLabel);
		springLayout.putConstraint(SpringLayout.WEST, gammaLabel, 5,
				SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.NORTH, gammaLabel, 230,
				SpringLayout.NORTH, this);

      gammaValue_ = new JFormattedTextField(numberFormat_);
      gammaValue_.setFont(new Font("Arial", Font.PLAIN, 10));
      gammaValue_.setValue(gamma_);
      gammaValue_.addPropertyChangeListener("value", this);
      gammaValue_.setPreferredSize(new Dimension(35, 20));

      add(gammaValue_);
		springLayout.putConstraint(SpringLayout.WEST, gammaValue_, 45,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, gammaValue_, 95,
				SpringLayout.WEST, this);
  		springLayout.putConstraint(SpringLayout.NORTH, gammaValue_, 0,
				SpringLayout.NORTH, gammaLabel);

		histogramPanel_ = new HistogramPanel();
		histogramPanel_.setMargins(8, 10);
      histogramPanel_.setTraceStyle(true, new Color(50,50,50));
		histogramPanel_.setTextVisible(false);
		histogramPanel_.setGridVisible(false);

      histogramPanel_.addCursorListener(this);

		add(histogramPanel_);
		springLayout.putConstraint(SpringLayout.EAST, histogramPanel_, -5,
				SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.WEST, histogramPanel_, 100,
				SpringLayout.WEST, this);

		springLayout.putConstraint(SpringLayout.SOUTH, histogramPanel_, -6,
				SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, histogramPanel_, 0,
				SpringLayout.NORTH, fullScaleButton_);

		stretchCheckBox_ = new JCheckBox();
		stretchCheckBox_.setFont(new Font("", Font.PLAIN, 10));
		stretchCheckBox_.setText("Auto-stretch");
		stretchCheckBox_.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
            rejectOutliersCheckBox_.setEnabled(stretchCheckBox_.isSelected());
            boolean rejectControlsEnabled  = stretchCheckBox_.isSelected() && rejectOutliersCheckBox_.isSelected() ;
            percentOutliersLabel_.setEnabled(rejectControlsEnabled);
            rejectOutliersPercentSpinner_.setEnabled(rejectControlsEnabled );
            if (stretchCheckBox_.isSelected()) {
               liveStretchMode_ = true;
               setAutoScale();

            } else {
               liveStretchMode_ = false;
            }
			};
		});
		add(stretchCheckBox_);

		springLayout.putConstraint(SpringLayout.EAST, stretchCheckBox_, 5,
				SpringLayout.WEST, histogramPanel_);
		springLayout.putConstraint(SpringLayout.WEST, stretchCheckBox_, 0,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, stretchCheckBox_, 185,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, stretchCheckBox_, 160,
				SpringLayout.NORTH, this);


	   rejectOutliersCheckBox_ = new JCheckBox();
		rejectOutliersCheckBox_.setFont(new Font("", Font.PLAIN, 10));
		rejectOutliersCheckBox_.setText("");
		rejectOutliersCheckBox_.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
            rejectOutliersPercentSpinner_.setEnabled(rejectOutliersCheckBox_.isSelected());
            percentOutliersLabel_.setEnabled(rejectOutliersCheckBox_.isSelected());
            if (rejectOutliersCheckBox_.isSelected()) {

					; // as with the other check boxes, takes effect when setAutoScale runs...
            }
			};
		});
		add(rejectOutliersCheckBox_);

		springLayout.putConstraint(SpringLayout.EAST, rejectOutliersCheckBox_, 30,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, rejectOutliersCheckBox_, 0,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, rejectOutliersCheckBox_, 210,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, rejectOutliersCheckBox_, 190,
				SpringLayout.NORTH, this);

      
      disableHistogramCheckBox_ = new JCheckBox();
		disableHistogramCheckBox_.setFont(new Font("", Font.PLAIN, 10));
		disableHistogramCheckBox_.setText("<html>Disable<br>Histogram<br>Updates</html>");
		disableHistogramCheckBox_.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
            disableHistogram_ = disableHistogramCheckBox_.isSelected();
			};
		});
		add(disableHistogramCheckBox_);
		springLayout.putConstraint(SpringLayout.EAST, disableHistogramCheckBox_, 5,
				SpringLayout.WEST, histogramPanel_);
		springLayout.putConstraint(SpringLayout.WEST, disableHistogramCheckBox_, 0,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, disableHistogramCheckBox_, 300,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, disableHistogramCheckBox_, 260,
				SpringLayout.NORTH, this);
      
      
      SpinnerModel smodel = new SpinnerNumberModel(100.*fractionToReject_,0.,1.,.01);
      rejectOutliersPercentSpinner_ = new JSpinner();
      rejectOutliersPercentSpinner_.setModel(smodel);
      Dimension sd = rejectOutliersPercentSpinner_.getSize();
      rejectOutliersPercentSpinner_.setFont(new Font("Arial", Font.PLAIN, 9));
      // user sees the fraction as percent
      add(rejectOutliersPercentSpinner_);
      rejectOutliersPercentSpinner_.setEnabled(false);
      rejectOutliersPercentSpinner_.setToolTipText("% pixels dropped or saturated to reject");


		springLayout.putConstraint(SpringLayout.EAST, rejectOutliersPercentSpinner_, 90,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, rejectOutliersPercentSpinner_, 35,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, rejectOutliersPercentSpinner_, 210,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, rejectOutliersPercentSpinner_, 190,
				SpringLayout.NORTH, this);

      percentOutliersLabel_ = new JLabel();
      percentOutliersLabel_.setFont(new Font("Arial", Font.PLAIN, 10));
      percentOutliersLabel_.setText("% outliers to ignore");
      add(percentOutliersLabel_);
		springLayout.putConstraint(SpringLayout.WEST, percentOutliersLabel_, 5,
				SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.NORTH, percentOutliersLabel_, 210,
				SpringLayout.NORTH, this);

 		springLayout.putConstraint(SpringLayout.EAST, percentOutliersLabel_, 5,
				SpringLayout.WEST, histogramPanel_);



		modeComboBox_ = new JComboBox();
		modeComboBox_.setFont(new Font("", Font.PLAIN, 10));
		modeComboBox_.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				setIntensityMode(modeComboBox_.getSelectedIndex()-1,true);
			}
		});
		modeComboBox_.setModel(new DefaultComboBoxModel(new String[] {
				"camera", "8bit", "10bit", "12bit", "14bit", "16bit" }));
		add(modeComboBox_);
		springLayout.putConstraint(SpringLayout.EAST, modeComboBox_, 0,
				SpringLayout.EAST, maxLabel_);
		springLayout.putConstraint(SpringLayout.WEST, modeComboBox_, 0,
				SpringLayout.WEST, minLabel);
		springLayout.putConstraint(SpringLayout.SOUTH, modeComboBox_, 27,
				SpringLayout.SOUTH, varLabel);
		springLayout.putConstraint(SpringLayout.NORTH, modeComboBox_, 5,
				SpringLayout.SOUTH, varLabel);

		logHistCheckBox_ = new JCheckBox();
		logHistCheckBox_.setFont(new Font("", Font.PLAIN, 10));
		logHistCheckBox_.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (logHistCheckBox_.isSelected())
					logScale_ = true;
				else
					logScale_ = false;

				update();
			}
		});
		logHistCheckBox_.setText("Log hist.");
		add(logHistCheckBox_);
		springLayout.putConstraint(SpringLayout.SOUTH, logHistCheckBox_, 0,
				SpringLayout.NORTH, minLabel_);
		springLayout.putConstraint(SpringLayout.NORTH, logHistCheckBox_, -18,
				SpringLayout.NORTH, minLabel_);
		springLayout.putConstraint(SpringLayout.EAST, logHistCheckBox_, 74,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.WEST, logHistCheckBox_, 1,
				SpringLayout.WEST, this);

//      ImagePlus.addImageListener(this);
      GUIUtils.registerImageFocusListener(this);
	}

   public void setPixelBitDepth(int depth, boolean forceDepth) 
   { 
	   // histogram for 32bits is not supported in this implementation 
      if(depth >= 32)
	      depth = 8; 
      numLevels_ = 1 << depth; 
      maxIntensity_ = numLevels_ - 1;
	   binSize_ = (maxIntensity_ + 1)/ HIST_BINS;
	  
	   // override histogram depth based on the selected mode 
	   if (!forceDepth && modeComboBox_.getSelectedIndex() > 0) {
         setIntensityMode(modeComboBox_.getSelectedIndex()-1,true); 
      }
	  
	   if (forceDepth) { // update the mode display to camera-auto
	       modeComboBox_.setSelectedIndex(0); 
       } 
   }

   public void setSingleProcessorGamma(double gamma_, ImageProcessor ip, int colIndex) {
      if (ip == null)
         return;

      double maxValue = 255.0;
      byte[] r = new byte[256];
      byte[] g = new byte[256];
      byte[] b = new byte[256];
      for (int i = 0; i < 256; i++) {
         double val = Math.pow((double) i / maxValue, gamma_) * (double) maxValue;
         r[i] = (byte) ((colIndex == 0 || colIndex == 1) ? val : 0);
         g[i] = (byte) ((colIndex == 0 || colIndex == 2) ? val : 0);
         b[i] = (byte) ((colIndex == 0 || colIndex == 3) ? val : 0);
      }
      LUT lut = new LUT(8, 256, r, g, b);
      ip.setColorModel(lut);
      
      if (liveStretchMode_) 
            setAutoScale();
      else  //setAutoScale calls updateAndDraw itself
         if (!MMStudioMainFrame.getInstance().isLiveModeOn())
            image_.updateAndDraw();
   }

   public void updateHistogram() {
      updateHistogram(image_);
   }

   public void updateHistogram(ImagePlus image) {
      if (image != null && histogram_ != null) {
         histogramData_.setData(histogram_);
         histogramPanel_.setData(histogramData_);
         histogramPanel_.setAutoScale();
         
         maxLabel_.setText(NumberUtils.intToDisplayString((int) max_));
         minLabel_.setText(NumberUtils.intToDisplayString((int) min_));
         meanLabel_.setText(NumberUtils.intToDisplayString((int) mean_));
         stdDevLabel_.setText(NumberUtils.doubleToDisplayString(stdDev_));
         
         histogramPanel_.repaint();
      }
   }
	 
	private void setIntensityMode(int mode, boolean updateContrast) {
		switch (mode) {
		case 0: 
			maxIntensity_ = 255;
			break;
		case 1:
			maxIntensity_ = 1023;
			break;
		case 2:
			maxIntensity_ = 4095;
			break;
		case 3:
			maxIntensity_ = 16383;
			break;
		case 4:
			maxIntensity_ = 65535;
			break;
		default:
			break;
		}
		binSize_ = (maxIntensity_ + 1) / HIST_BINS;
		update(updateContrast);
	}

	protected void onSliderMove() {
		// correct slider relative positions if necessary
		updateCursors();
      applyContrastSettings();
	}


   // only used for Gamma
   public void propertyChange(PropertyChangeEvent e) {
      try { 
         gamma_ = (double) NumberUtils.displayStringToDouble(numberFormat_.format(gammaValue_.getValue()));
      } catch (ParseException p) {
         ReportingUtils.logError(p, "ContrastPanel, Function propertyChange");
      }
      setLutGamma(gamma_);
      updateCursors();
   }

   private void setLutGamma(double gamma_) {
       if (image_ == null)
          return;

       //if (gamma_ == 1)
       //    return;
       
       // TODO: deal with color images
       if (image_.getProcessor() instanceof ColorProcessor)
          return;

       if (!(image_ instanceof CompositeImage)) {
          ImageProcessor ip = image_.getProcessor();
              setSingleProcessorGamma(gamma_, ip, 0);
       } else {
          for (int i=1;i<=3;++i) {
             ImageProcessor ip = ((CompositeImage) image_).getProcessor(i);
             setSingleProcessorGamma(gamma_, ip,  i);
          }
       }
   }

   public void update(boolean updateHistogram) {
      if (image_ == null || image_.getProcessor() == null)
         return;
      if (stretchCheckBox_.isSelected()) {
         setAutoScale();
      }
      if (updateHistogram &&!disableHistogram_)
         updateHistogram();
      setLutGamma(gamma_);

      image_.updateAndDraw();
   }

	public void update() {
      update(true);
	}


	// override from ImageController
	 public void setImagePlus(ImagePlus ip, ContrastSettings cs8bit,
			ContrastSettings cs16bit) {
       setImagePlus(ip,cs8bit,cs16bit,true);
    }
   
   
   private void setImagePlus(ImagePlus ip, ContrastSettings cs8bit,
			ContrastSettings cs16bit, boolean updateContrast) {
      cs8bit_ = cs8bit;
      cs16bit_ = cs16bit;
		image_ = ip;
		setIntensityMode(modeComboBox_.getSelectedIndex()-1,updateContrast);
	}

	/**
	 * Auto-scales image display to clip at minimum and maximum pixel values.
	 * 
	 */
	private void setAutoScale() {
		if (image_ == null || disableHistogram_) {
			return;
      }

//      liveStretchMode_ = true;

      // protect against an 'Unhandled Exception' inside getStatistics
      if ( null != image_.getProcessor()){        
         lutMin_ = min_;
         lutMax_ = max_;

			if(rejectOutliersCheckBox_.isSelected()){
				if( lutMin_ < minAfterRejectingOutliers_  ){
               if( 0 < minAfterRejectingOutliers_){
                  lutMin_ =  minAfterRejectingOutliers_;
               }
				}
				if( maxAfterRejectingOutliers_ < lutMax_){
                  lutMax_ = maxAfterRejectingOutliers_;
				}

			}
      } else {
         ReportingUtils.logError("Internal error: ImageProcessor is null");
      }


      updateCursors();
      if (!MMStudioMainFrame.getInstance().isLiveModeOn())
         image_.updateAndDraw();
	}


	private void setFullScale() {
		if (image_ == null)
			return;
      setContrastStretch(false);
      image_.getProcessor().setMinAndMax(0, maxIntensity_);
      lutMin_ = 0;
      lutMax_ = maxIntensity_;
      updateCursors();
      
		image_.updateAndDraw();
	}

	private void updateCursors() {
		if (image_ == null)
			return;

		histogramPanel_.setCursors(lutMin_ / binSize_,
				lutMax_ / binSize_,
            gamma_);
		histogramPanel_.repaint();
		if (cs8bit_ == null || cs16bit_ == null)
			return;

		if (image_.getProcessor() != null) {
			// record settings
			if (image_.getProcessor() instanceof ShortProcessor) {
				cs16bit_.min = lutMin_;
				cs16bit_.max = lutMax_;
            image_.getProcessor().setMinAndMax(cs16bit_.min, cs16bit_.max);
			} else {
				cs8bit_.min = lutMin_;
				cs8bit_.max = lutMax_;
            image_.getProcessor().setMinAndMax(cs8bit_.min, cs8bit_.max);
			}
		}
	}

	public void setContrastSettings(ContrastSettings cs8bit,
			ContrastSettings cs16bit) {
		cs8bit_ = cs8bit;
		cs16bit_ = cs16bit;

	}

	public void applyContrastSettings() {
		applyContrastSettings(cs8bit_, cs16bit_);
	};

   public void applyContrastSettings(ContrastSettings contrast8,
           ContrastSettings contrast16) {
      applyContrastSettings(image_, contrast8, contrast16);  
   }

	public void applyContrastSettings(ImagePlus img, ContrastSettings contrast8,
			ContrastSettings contrast16) {
		if (img == null)
			return;

      if (!(img instanceof CompositeImage)) {
         applyContrastSettings(img.getProcessor(),
                 contrast8, contrast16);
      } else {
         for (int i=1;i<=3;++i) {
            ImageProcessor proc = ((CompositeImage) img).getProcessor(i);
            applyContrastSettings(proc,
                 contrast8, contrast16);
         }
      }

      img.updateAndDraw();
	}

   public void applyContrastSettings(ImageProcessor proc,
           ContrastSettings contrast8, ContrastSettings contrast16) {
      if (proc == null)
         return;

     if (proc instanceof ShortProcessor) {
        proc.setMinAndMax(contrast16.min, contrast16.max);
     } else if (proc instanceof ByteProcessor) {
        proc.setMinAndMax(contrast8.min, contrast8.max);
     }

   }

	public void setContrastStretch(boolean stretch) {
		stretchCheckBox_.setSelected(stretch);
	}

	public boolean isContrastStretch() {
		return stretchCheckBox_.isSelected();
	}

   public void setRejectOutliers(boolean reject) {
      rejectOutliersCheckBox_.setSelected(reject);
   }

   public boolean isRejectOutliers() {
      return rejectOutliersCheckBox_.isSelected();
   }
   
   public double getFractionToReject() {
      return fractionToReject_;
   }
   
   public void setFractionToReject(double frac) {
      fractionToReject_ = frac;
      // TODO: this does not work
      rejectOutliersPercentSpinner_.setValue(fractionToReject_ / 0.01);
   }
	
   public ContrastSettings getContrastSettings() {
      ContrastSettings ret = cs8bit_;
      if( null != image_) {
         if (image_.getProcessor() instanceof ShortProcessor)
            ret = cs16bit_;
         else
            ret = cs8bit_;
      }
      return ret;
   }

   private void updateStretchBox() {
      if (liveWindow_) {
         stretchCheckBox_.setEnabled(true);
         stretchCheckBox_.setSelected(liveStretchMode_);
      } else {
         stretchCheckBox_.setEnabled(false);
         stretchCheckBox_.setSelected(false);
      }
   }

   public void focusReceived(ImageWindow focusedWindow) {
      if (focusedWindow == null) {
         histogramPanel_.repaint();
         return;
      }
      
      ImagePlus imgp = focusedWindow.getImagePlus();
      liveWindow_ = (focusedWindow instanceof MMImageWindow);
      if (!liveWindow_)
         return;
      updateStretchBox();


//    ImageProcessor proc = imgp.getChannelProcessor();
      double min = imgp.getDisplayRangeMin();
      double max = imgp.getDisplayRangeMax();
        setImagePlus(imgp, new ContrastSettings(min, max), new ContrastSettings(min, max),true);
              imageUpdated(imgp);
//    update();
   }

   public void imageOpened(ImagePlus ip) {
      update();
   }

   public void imageClosed(ImagePlus ip) {
      update();
   }

   public void imageUpdated(ImagePlus ip) {
      if (liveWindow_ && !imageUpdated_) {
         imageUpdated_ = true;

         double beforeMin = lutMin_;
         double beforeMax = lutMax_;

         if (liveStretchMode_) {
            setAutoScale();

         }

         updateCursors();
         //updateHistogram();
         //image_.updateAndDraw();
         imageUpdated_ = false;
      }
   }
   
   private void calcHistogramAndStatistics(ImagePlus ip) {
      if (ip != null) {
         int[] rawHistogram = ip.getProcessor().getHistogram();
         int imgWidth = ip.getWidth();
         int imgHeight = ip.getHeight();
         if (rejectOutliersCheckBox_.isSelected()) {
            // todo handle negative values
            maxAfterRejectingOutliers_ = rawHistogram.length;
            // specified percent of pixels are ignored in the automatic contrast setting
            int totalPoints = imgHeight * imgWidth;
            fractionToReject_ = 0.01 * (Double) rejectOutliersPercentSpinner_.getValue();
            HistogramUtils hu = new HistogramUtils(rawHistogram, totalPoints, fractionToReject_);
            minAfterRejectingOutliers_ = hu.getMinAfterRejectingOutliers();
            maxAfterRejectingOutliers_ = hu.getMaxAfterRejectingOutliers();
         }
         if (histogramData_ == null) {
            histogramData_ = new GraphData();
         } // 256 bins
         
         
          min_ = -1;
         max_ = 0;
         mean_ = 0;
         
         histogram_ = new int[HIST_BINS];
         int limit = Math.min(rawHistogram.length / binSize_, HIST_BINS);
         int total = 0;
         for (int i = 0; i < limit; i++) {
            histogram_[i] = 0;
            for (int j = 0; j < binSize_; j++) {
               int rawHistIndex = i * binSize_ + j;
               int rawHistVal = rawHistogram[rawHistIndex];
               histogram_[i] += rawHistVal;
               if (rawHistVal > 0) {
                  max_ = rawHistIndex;
                  if (min_ == -1) {
                     min_ = rawHistIndex;
                  }
                  mean_ += rawHistIndex * rawHistVal;
               }
            }
            total += histogram_[i];
            if (logScale_) 
               histogram_[i] = histogram_[i] > 0 ? (int) (1000 * Math.log(histogram_[i])) : 0;
         }
         mean_ /= imgWidth*imgHeight;
         if (min_ == max_) 
            if (min_ == 0) 
               max_++;
            else 
               min_--;

         // work around what is apparently a bug in ImageJ
         if (total == 0) {
            if (ip.getProcessor().getMin() == 0) {
               histogram_[0] = imgWidth * imgHeight;
            } else {
               histogram_[limit - 1] = imgWidth * imgHeight;
            }
         }
                      
         stdDev_ = 0;
         for (int i = 0; i < rawHistogram.length; i++) {
           for (int j = 0; j < rawHistogram[i]; j++) {
              stdDev_ += (i - mean_)*(i - mean_);
           }
         }
         stdDev_ = Math.sqrt(stdDev_/(imgWidth*imgHeight));         
      }
   }

   public void updateContrast(ImagePlus ip) {      
      if (!disableHistogram_)
          calcHistogramAndStatistics(ip);
      
      if (stretchCheckBox_.isSelected()) {
         double min = ip.getDisplayRangeMin();
         double max = ip.getDisplayRangeMax();
         setImagePlus(ip, new ContrastSettings(min, max), new ContrastSettings(min, max),false);
      }
      if (!disableHistogram_)
         updateHistogram(ip);
   }

   public void onLeftCursor(double pos) {
      if (liveStretchMode_)
         stretchCheckBox_.setSelected(true);

      lutMin_ = Math.max(0, pos) * binSize_;
      if (lutMax_ < lutMin_)
         lutMax_ = lutMin_;
      updateCursors();
      applyContrastSettings();
   }

   public void onRightCursor(double pos) {
      if (liveStretchMode_)
         stretchCheckBox_.setSelected(false);
      
      lutMax_ = Math.min(255, pos) * binSize_;
      if (lutMin_ > lutMax_)
         lutMin_ = lutMax_;
      updateCursors();
      applyContrastSettings();
   }

   public void onGammaCurve(double gamma) {
      if (gamma != 0) {
         if (gamma > 0.9 & gamma < 1.1)
            gamma_ = 1;
         else
            gamma_ = gamma;
         gammaValue_.setValue(gamma_);
         updateCursors();
         applyContrastSettings();
      }
   }

}