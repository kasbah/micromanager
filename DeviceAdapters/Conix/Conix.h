///////////////////////////////////////////////////////////////////////////////
// FILE:       Conix.h
// PROJECT:    MicroManage
// SUBSYSTEM:  DeviceAdapters
//-----------------------------------------------------------------------------
// DESCRIPTION:
// Conix adapter
//                
// AUTHOR: Nico Stuurman, 02/27/2006
//		   Trevor Osborn (ConixXYStage), trevor@conixresearch.com, 02/10/2010
//         

#ifndef _CONIX_H_
#define _CONIX_H_

#include "../../MMDevice/MMDevice.h"
#include "../../MMDevice/DeviceBase.h"
#include <string>
#include <map>

//////////////////////////////////////////////////////////////////////////////
// Error codes
//
#define ERR_UNKNOWN_COMMAND          10002
#define ERR_UNKNOWN_POSITION         10003
#define ERR_HALT_COMMAND             10004
#define ERR_UNRECOGNIZED_ANSWER      10005
#define ERR_PORT_CHANGE_FORBIDDEN    10006
#define ERR_OFFSET                   11000

class QuadFluor : public CStateDeviceBase<QuadFluor>
{
public:
   QuadFluor();
   ~QuadFluor();
  
   // Device API
   // ----------
   int Initialize();
   int Shutdown();
  
   void GetName(char* pszName) const;
   bool Busy();
   unsigned long GetNumberOfPositions()const {return numPos_;}

    

   // action interface
   // ----------------
   int OnPort(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnState(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnCommand(MM::PropertyBase* pProp, MM::ActionType eAct);

private:
   int GetPosition(int& position);
   int SetPosition(int position);
   int ExecuteCommand(const std::string& cmd);

   bool initialized_;
   unsigned numPos_;
   // MMCore name of serial port
   std::string port_;
   // Command exchange with MMCore
   std::string command_;
   // Has a command been sent to which no answer has been received yet?
   bool pendingCommand_;
};



//////////////////////////////////////////////////////////////////////////////
// ConixXYStage class
//
//////////////////////////////////////////////////////////////////////////////

class ConixXYStage : public CXYStageBase<ConixXYStage>
{
public:
	ConixXYStage();
	~ConixXYStage();

	bool Busy();
	void GetName(char* pszName) const;

	int Initialize();
	int Shutdown();
	 
	// XYStage API
	int SetPositionUm(double x, double y);
	int GetPositionUm(double& x, double& y);
	double GetStepSize() {return stepSize_um_;}
	int SetPositionSteps(long x, long y)
	{
		posX_um_ = x * stepSize_um_;
		posY_um_ = y * stepSize_um_;
		return DEVICE_OK;
	}
	int GetPositionSteps(long& x, long& y)
	{
		x = (long)(posX_um_ / stepSize_um_);
		y = (long)(posY_um_ / stepSize_um_);
		return DEVICE_OK;
	}
	int Home();
	int Stop();
	int SetOrigin();
	int SetAdapterOriginUm(double x, double y) {posX_um_ = x; posY_um_ = y; return DEVICE_OK;}
	int GetLimits(double& lower, double& upper)
	{
		lower = lowerLimit_;
		upper = upperLimit_;
		return DEVICE_OK;
	}
	int GetLimitsUm(double& xMin, double& xMax, double& yMin, double& yMax)
	{
		xMin = lowerLimit_; xMax = upperLimit_;
		yMin = lowerLimit_; yMax = upperLimit_;
		return DEVICE_OK;
	}

	int GetStepLimits(long& /*xMin*/, long& /*xMax*/, long& /*yMin*/, long& /*yMax*/)
	{
		return DEVICE_UNSUPPORTED_COMMAND;
	}
	double GetStepSizeXUm() {return stepSize_um_;}
	double GetStepSizeYUm() {return stepSize_um_;}
	int Move(double /*vx*/, double /*vy*/) {return DEVICE_OK;} // ok

	// action interface
	// ----------------
	int OnPort(MM::PropertyBase* pProp, MM::ActionType eAct);

private:
	int SetComUnits(std::string unit_type = "UM");
	
	// MMCore name of serial port
	std::string port_;

	double stepSize_um_;
	double posX_um_;
	double posY_um_;
	bool busy_;
	bool initialized_;
	double lowerLimit_;
	double upperLimit_;
};



#endif //_CONIX_H_
