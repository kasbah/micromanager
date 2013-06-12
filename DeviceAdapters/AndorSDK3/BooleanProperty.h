#ifndef _BOOLEANPROPERTY_H_
#define _BOOLEANPROPERTY_H_

#include "MMDeviceConstants.h"
#include "Property.h"

class ICallBackManager;

namespace andor {
   class IBool;
   class ISubject;
};

class TBooleanProperty
{
public:
   TBooleanProperty(const std::string & MM_name, andor::IBool* bool_feature,
                       ICallBackManager* callback, bool readOnly);
   ~TBooleanProperty();

   //Update not currently implemented as no attach within SDK3 required at present
   void Update(andor::ISubject* Subject);
   int OnBoolean(MM::PropertyBase* pProp, MM::ActionType eAct);
   typedef MM::Action<TBooleanProperty> CPropertyAction;

private:
   void setFeature(const std::string & value);

private:
   andor::IBool* boolean_feature_;
   ICallBackManager* callback_;
};

#endif // include only once
