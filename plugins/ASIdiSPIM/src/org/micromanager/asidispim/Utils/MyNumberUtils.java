///////////////////////////////////////////////////////////////////////////////
//FILE:          MyNumberUtils.java
//PROJECT:       Micro-Manager 
//SUBSYSTEM:     ASIdiSPIM plugin
//-----------------------------------------------------------------------------
//
// AUTHOR:       Nico Stuurman, Jon Daniels
//
// COPYRIGHT:    University of California, San Francisco, & ASI, 2013
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

package org.micromanager.asidispim.Utils;


/**
 * @author Jon
 */
public class MyNumberUtils {
   
   public MyNumberUtils() {
   }
   
   
   /**
    * Does "equality" test on floats, using locally-defined epsilon (1e-12)
    * @param f1
    * @param f2
    * @return
    */
   public static boolean floatsEqual(float f1, float f2) {
      final float EPS = (float) 1e-12;
      float diff = f2 - f1;
      return((diff < EPS) && (diff > -EPS));
   }
   
   /**
    * "rounds up" to nearest increment of 0.25, e.g. 0 goes to 0 but 0.01 goes to 0.25
    * @param f
    * @return
    */
   public static float ceilToQuarterMs(float f) {
      return (float) (Math.ceil(f*4)/4);
   }
   
   /**
    * "rounds up" to nearest increment of 0.25
    * @param f
    * @return
    */
   public static float roundToQuarterMs(float f) {
      return (float) (((float) Math.round(f*4))/4);
   }
   
   
}
