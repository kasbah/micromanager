package org.micromanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mmcorej.CMMCore;
import mmcorej.DeviceType;
import mmcorej.MMCoreJ;
import mmcorej.MMEventCallback;
import mmcorej.StrVector;

import org.micromanager.acquisition.AcquisitionWrapperEngine;
import org.micromanager.api.MMListenerInterface;
import org.micromanager.events.EventManager;
import org.micromanager.events.MMListenerProxy;


/**
 * Callback to update Java layer when a change happens in the MMCore.
 */
public class CoreEventCallback extends MMEventCallback {

   private CMMCore core_;
   private AcquisitionWrapperEngine engine_;
   private List<MMListenerInterface> MMListeners_
         = Collections.synchronizedList(new ArrayList<MMListenerInterface>());
   private volatile boolean ignorePropertyChanges_;

   public CoreEventCallback(CMMCore core, AcquisitionWrapperEngine engine) {
      super();
      core_ = core;
      engine_ = engine;
      core_.registerCallback(this);
      addMMListener(new MMListenerProxy(EventManager.getBus()));
   }

   @Override
   public void onPropertiesChanged() {
      // TODO: remove test once acquisition engine is fully multithreaded
      if (engine_ != null && engine_.isAcquisitionRunning()) {
         core_.logMessage("Notification from MMCore ignored because acquistion is running!", true);
      } else {
         if (ignorePropertyChanges_) {
            core_.logMessage("Notification from MMCore ignored since the system is still loading", true);
         } else {
            core_.updateSystemStateCache();
            // update all registered listeners 
            for (MMListenerInterface mmIntf : MMListeners_) {
               mmIntf.propertiesChangedAlert();
            }
            core_.logMessage("Notification from MMCore!", true);
         }
      }
   }

   @Override
   public void onPropertyChanged(String deviceName, String propName, String propValue) {
      core_.logMessage("Notification for Device: " + deviceName + " Property: " +
            propName + " changed to value: " + propValue, true);
      // update all registered listeners
      for (MMListenerInterface mmIntf:MMListeners_) {
         mmIntf.propertyChangedAlert(deviceName, propName, propValue);
      }
   }

   @Override
   public void onConfigGroupChanged(String groupName, String newConfig) {
      try {
         for (MMListenerInterface mmIntf:MMListeners_) {
            mmIntf.configGroupChangedAlert(groupName, newConfig);
         }
      } catch (Exception e) {
      }
   }
   
   @Override
   public void onSystemConfigurationLoaded() {
      for (MMListenerInterface mmIntf:MMListeners_) {
         mmIntf.systemConfigurationLoaded();
      }
   }

   @Override
   public void onPixelSizeChanged(double newPixelSizeUm) {
      for (MMListenerInterface mmIntf:MMListeners_) {
         mmIntf.pixelSizeChangedAlert(newPixelSizeUm);
      }
   }

   @Override
   public void onStagePositionChanged(String deviceName, double pos) {
      // TODO: this check should be in the core, not the java layer!
      if (deviceName.equals(core_.getFocusDevice())) {
         for (MMListenerInterface mmIntf:MMListeners_) {
            mmIntf.stagePositionChangedAlert(deviceName, pos);
         }
      }
   }

   @Override
   public void onXYStagePositionChanged(String deviceName, double xPos, double yPos) {
      // TODO: this check should be in the core, not the java layer!
      if (deviceName.equals(core_.getXYStageDevice())) {
         for (MMListenerInterface mmIntf:MMListeners_) {
            mmIntf.xyStagePositionChanged(deviceName, xPos, yPos);
         }
      }
   }

   @Override
   public void onExposureChanged(String deviceName, double exposure) {
      for (MMListenerInterface mmIntf:MMListeners_) {
         mmIntf.exposureChanged(deviceName, exposure);
      }
   }
   
   @Override
   public void onSLMExposureChanged(String deviceName, double exposure) {
      for (MMListenerInterface mmIntf:MMListeners_) {
         mmIntf.slmExposureChanged(deviceName, exposure);
      }
   }

   public void addMMListener(MMListenerInterface newL) {
      if (MMListeners_.contains(newL)) {
         return;
      }
      MMListeners_.add(newL);
   }

   public void removeMMListener(MMListenerInterface oldL) {
      if (!MMListeners_.contains(oldL)) {
         return;
      }
      MMListeners_.remove(oldL);
   }

   public void setIgnoring(boolean isIgnoring) {
      ignorePropertyChanges_ = isIgnoring;
   }
}
