import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import ij.measure.ResultsTable;

import org.apache.commons.math.analysis.*;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.direct.NelderMead;
import org.apache.commons.math.optimization.direct.MultiDirectional;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;


import java.lang.Math;
import java.awt.Rectangle;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.IJ;

public class GaussianTrack_ implements PlugIn {
	double[] params0_ = {16000.0, 5.0, 5.0, 1.0, 850.0};
	double[] steps_ = new double[5];
	String [] paramNames_ = {"A", "x_c", "y_c", "sigma", "b"};

   GaussianResidual gs_;
   NelderMead nm_;
   SimpleScalarValueChecker convergedChecker_;;

	private void print(String myText) {
		ij.IJ.log(myText);
	}

   public class GaussianResidual implements MultivariateRealFunction {
       short[] data_;
       int nx_;
       int ny_;
       int count_ = 0;


       public void setImage(short[] data, int width, int height) {
          data_ = data;
          nx_ = width;
          ny_ = height;
       }

       public double value(double[] params) {
          double residual = 0.0;
          for (int i = 0; i < nx_; i++) {
             for (int j = 0; j < ny_; j++) {
                residual += sqr(gaussian(params, i, j) - data_[(i*nx_) + j]);
             }
          }
   /*
         for (int i=0; i< params.length; i++)
                  print(" " + paramNames_[i] + ": " + params[i]);

         print("Residual: " + residual);
   */
         return residual;
      }

      public double sqr(double val) {
         return val*val;
      }

      public double gaussian(double[] params, int x, int y) {

                  /* Gaussian function of the form:
                   * A *  exp(-((x-xc)^2+(y-yc)^2)/(2 sigy^2))+b
                   * A = params[0]  (total intensity)
                   * xc = params[1]
                   * yc = params[2]
                   * sig = params[3]
                   * b = params[4]  (background)
                   */

         if (params.length < 5) {
                          // Problem, what do we do???
                          //MMScriptException e;
                          //e.message = "Params for Gaussian function has too few values";
                          //throw (e);
         }

         double exponent = (sqr(x - params[1])  + sqr(y - params[2])) / (2 * sqr(params[3]));
         double res = params[0] * Math.exp(-exponent) + params[4];
         return res;
      }
   }



	public double[] doGaussianFit (ImageProcessor siProc) {

      short[] imagePixels = (short[])siProc.getPixels();
		gs_.setImage((short[])siProc.getPixels(), siProc.getWidth(), siProc.getHeight());

      // Hard code estimate for sigma:
      params0_[3] = 1.115;

      // estimate background by averaging pixels at the edge
      double bg = 0.0;
      int n = 0;
      int lastRowOffset = (siProc.getHeight() - 1) * siProc.getWidth();
      for (int i =0; i < siProc.getWidth(); i++) {
         bg += imagePixels[i];
         bg += imagePixels[i + lastRowOffset];
         n += 2;
      }
      for (int i = 1; i < siProc.getHeight() - 1; i++) {
         bg += imagePixels[i * siProc.getWidth()];
         bg += imagePixels[(i + 1) *siProc.getWidth() - 1];
         n += 2;
      }
      params0_[4] = bg / n;

      // estimate signal by subtracting background from total intensity 
      double ti = 0.0;
      double mt = 0.0;
      for (int i = 0; i < siProc.getHeight() * siProc.getWidth(); i++) {
         mt += imagePixels[i];
      }
      ti = mt - ( (bg / n) * siProc.getHeight() * siProc.getWidth());
      params0_[0] = ti / (2 * Math.PI * params0_[3] * params0_[3]);
      // print("Total signal: " + ti + "Estimate: " + params0_[0]);

      // estimate center of mass
      double mx = 0.0;
      double my = 0.0;
      for (int i = 0; i < siProc.getHeight() * siProc.getWidth(); i++) {
         mx += imagePixels[i] * (i % siProc.getWidth() );
         my += imagePixels[i] * (Math.floor (i / siProc.getWidth()));
      }
      params0_[1] = mx/mt;
      params0_[2] = my/mt;

      // set step size during estimate
		for (int i=0;i<params0_.length;++i)
			steps_[i] = params0_[i]*0.3;

		nm_.setStartConfiguration(steps_);
		nm_.setConvergenceChecker(convergedChecker_);

		nm_.setMaxIterations(200);
		double[] paramsOut = {0.0};
		try {
			RealPointValuePair result = nm_.optimize(gs_, GoalType.MINIMIZE, params0_);
			paramsOut = result.getPoint();
		} catch (Exception e) {}

      return paramsOut;
	}

	public void run(String arg) {

		gs_ = new GaussianResidual();
		nm_ = new NelderMead();
		convergedChecker_ = new SimpleScalarValueChecker(1e-5,-1);

      // Filters for results of Gaussian fit
      double intMin = 100;
      double intMax = 1E7;
      double sigmaMin = 0.8;
      double sigmaMax = 2.1;

      // initial setting for Maximum Finder
      int noiseTolerance = 100;

      // for now, take the active ImageJ image (this should be an image of a difraction limited spot
		ImagePlus siPlus = IJ.getImage();

      Roi originalRoi = siPlus.getRoi();
      int sliceN = siPlus.getSlice();

      ResultsTable rt = new ResultsTable();

      Rectangle rect = originalRoi.getBounds();
      int xc = (int) (rect.getX() + 0.5 * rect.getWidth());
      int yc = (int) (rect.getY() + 0.5 * rect.getHeight());

      // half the size of the box used for Gaussian fitting in pixels
      int halfSize = 7;

		long startTime = System.nanoTime();

      for (int i = sliceN; i < siPlus.getNSlices(); i++) {
IJ.log ("X: " + xc + " Y: " + yc);
         Roi spotRoi = new Roi(xc - halfSize, yc - halfSize, 2 * halfSize, 2*halfSize);
         siPlus.setSlice(i);
         siPlus.setRoi(spotRoi);
         ImageProcessor ip = siPlus.getProcessor().crop();

         IJ.run("Find Maxima...", "noise=" + noiseTolerance + " output=List");
         ResultsTable rtS = ResultsTable.getResultsTable();
         if (rtS.getCounter() >=1) {
            xc = (int)rtS.getValueAsDouble(0, 0);
            yc = (int) rtS.getValueAsDouble(1, 0);
         }
             
         
         double[]paramsOut = doGaussianFit(ip);
         if (paramsOut.length >= 4) {                                         
            double anormalized = paramsOut[0] * (2 * Math.PI * paramsOut[3] * paramsOut[3]);
            boolean report = anormalized > intMin && anormalized < intMax &&  
                              paramsOut[3] > sigmaMin && paramsOut[3] < sigmaMax;

            rt.incrementCounter();                                         
            rt.addValue("Frame", i);                                     
            rt.addValue("Intensity", anormalized);                         
            rt.addValue("Background", paramsOut[4]);                       
            rt.addValue("X", paramsOut[1] - halfSize + xc);     
            rt.addValue("Y", paramsOut[2] - halfSize + yc);     
            rt.addValue("Sigma", paramsOut[3]);                            
            if (report) {                                                     
               IJ.log (i + " " + xc + " " + paramsOut[1] + " " + halfSize);
               IJ.log (xc + " ");
            } 
         }  
      }

      rt.show("Gaussian Fit Tracking Result:");

      long endTime = System.nanoTime();
		double took = (endTime - startTime) / 1E6;

		print("Calculation took: " + took + " milli seconds"); 
   }
}
