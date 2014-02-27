///////////////////////////////////////////////////////////////////////////////
// FILE:          IMMLogger.h
// PROJECT:       Micro-Manager
// SUBSYSTEM:     MMCore
//-----------------------------------------------------------------------------
// DESCRIPTION:   Interface class for logging
//
// COPYRIGHT:     University of California, San Francisco, 2007
// LICENSE:       This file is distributed under the BSD license.
//                License text is included with the source distribution.
//
//                This file is distributed in the hope that it will be useful,
//                but WITHOUT ANY WARRANTY; without even the implied warranty
//                of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//                IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//                CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//                INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.

#pragma once

#include <string>
#include <stdexcept>

/**
* class IMMLogger
* Declares interface to the logger object
* This interface is introduces for de-coupling legacy code from
* specific implementation of the logger
* set of methods is chosen to accomodate legacy code
* 
*/
class IMMLogger
{
protected:
   IMMLogger(){};
public:
   class runtime_exception : public std::runtime_error
   {
   public:
      runtime_exception(std::string msg) : runtime_error(msg) {}
   };

   virtual ~IMMLogger(){};

   /**
   * Initialize
   * Opens log files
   * This method supposed to be called before calling other methods of the logger
   * Returns true on success
   * Must guaranty safe reentrance
   * Throws: IMMLogger::runtime_exception
   */
   virtual bool Initialize(std::string logFileName, std::string logInstanceName)throw(runtime_exception)   =0;


   /**
   * Shutdown
   * Ensures proper flush of the open log files
   * This method must be called last
   * After this method is called, calls of other methods will have no effect
   * Returns true on success
   * Must guaranty safe reentrance
   * Throws: IMMLogger::runtime_exception
   */
   virtual void Shutdown()throw(runtime_exception)     =0;

   /**
   * Reset
   * Reset the logger. 
   * Performed action is defined in particlural implementation of the logger  
   * Returns true on success
   * Must guaranty safe reentrance
   * Throws: IMMLogger::runtime_exception
   */
   virtual bool Reset()throw(IMMLogger::runtime_exception) = 0;

   virtual void SetPriorityLevel(bool includeDebug) throw() = 0;

   /**
   * EnableLogToStderr
   * Enable or disable output of the logged information to standard output
   * Returns previous state: true if logging to standard output was enabled
   * Must guaranty safe reentrance
   * Must not throw exceptions
   */
   virtual bool EnableLogToStderr(bool enable)throw() = 0;

   virtual void Log(bool isDebug, const char*, ...) throw() = 0;

   virtual void LogContents(char**  /*ppContents*/, unsigned long& /*len*/) = 0;
   virtual std::string LogPath(void) = 0;

};
