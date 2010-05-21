#include "MMAcquisition.h"
#include "../MMDevice/ImageMetadata.h"
#include "boost/foreach.hpp"

///////////////////////////////////
// MMAquisitionEngine::ImageTask //
///////////////////////////////////

ImageTask::ImageTask(MMAcquisitionEngine * eng, ImageRequest imageRequest)
{
   eng_ = eng;
   imageRequest_ = imageRequest;
   type = IMAGE;
}

void ImageTask::run()
{
   closeShutterIfNeeded();
   updatePosition();
   updateSlice();
   updateChannel();
   wait();
   autofocus();
   acquireImage();

}

void ImageTask::closeShutterIfNeeded()
{
   if (imageRequest_.closeShutter)
      eng_->core_->setShutterOpen(false);
}

void ImageTask::updateSlice()
{
   double chanZOffset = 0;
   double sliceZ;

   if (imageRequest_.channelIndex > -1)
      chanZOffset = imageRequest_.channel.zOffset;

   if (imageRequest_.sliceIndex > -1)
      sliceZ = imageRequest_.slicePosition;
   else
      eng_->coreCallback_->GetFocusPosition(sliceZ);

   if ((imageRequest_.sliceIndex > -1) || (chanZOffset != 0))
   {
      eng_->coreCallback_->SetFocusPosition(sliceZ + chanZOffset);
      printf("z position set\n");
   }
}

void ImageTask::updatePosition()
{
   if (imageRequest_.positionIndex > -1)
   {
      map<string, double>::iterator it1;

      MultiAxisPosition pos = imageRequest_.multiAxisPosition;
      for (it1 = pos.singleAxisPositions.begin(); it1 != pos.singleAxisPositions.end(); ++it1)
      {
         eng_->core_->setPosition(it1->first.c_str(), it1->second);
      }

      map<string, pair<double, double> >::iterator it2;

      for (it2 = pos.doubleAxisPositions.begin(); it2 != pos.doubleAxisPositions.end(); ++it2)
      {
         point2D xy = it2->second;
         eng_->core_->setXYPosition(it2->first.c_str(), xy.first, xy.second);
      }
      printf("position set\n");
   }
}

void ImageTask::updateChannel() {
   if (imageRequest_.channelIndex > -1)
   {
      eng_->coreCallback_->SetExposure(imageRequest_.channel.exposure);
      eng_->coreCallback_->SetConfig(imageRequest_.channel.group.c_str(), imageRequest_.channel.name.c_str());
      printf("channel set\n");
   }
}

void ImageTask::wait() {
   if (imageRequest_.timeIndex > -1)
   {
      if (eng_->lastWakeTime_ > 0)
      {
         MM::MMTime sleepTime = (eng_->lastWakeTime_ + imageRequest_.waitTime) - eng_->coreCallback_->GetCurrentMMTime();
         if (sleepTime > MM::MMTime(0, 0))
            eng_->coreCallback_->Sleep(NULL, sleepTime.getMsec());
         printf("waited\n");
      }

      eng_->lastWakeTime_ = eng_->coreCallback_->GetCurrentMMTime();
   }
}

void ImageTask::autofocus() {
   if (imageRequest_.runAutofocus && imageRequest_.channelIndex == 0 && imageRequest_.positionIndex == 0)
      eng_->core_->fullFocus();
}

void ImageTask::acquireImage() {
   int w, h, d;
   eng_->core_->setShutterOpen(true);
   const char * img = eng_->coreCallback_->GetImage(); // Snaps and retrieves image.

   Metadata md;
   md.sliceIndex = max(0,imageRequest_.sliceIndex);
   md.channelIndex = max(0,imageRequest_.channelIndex);
   md.positionIndex = max(0,imageRequest_.positionIndex);
   md.frameIndex = max(0,imageRequest_.timeIndex);

   eng_->coreCallback_->GetImageDimensions(w, h, d);
   eng_->coreCallback_->InsertImage(NULL, (const unsigned char *) img, w, h, d, &md);
   printf("Grabbed image.\n");
}



/////////////////////////
// MMAcquisitionRunner //
/////////////////////////

void MMAcquisitionEngine::Start() {
   stopRequested_ = false;
   pauseRequested_ = false;
   finished_ = false;

   activate();
}

void MMAcquisitionEngine::Run() {
   bool initialAutoshutter = core_->getAutoShutter();

   for (unsigned int i = 0; i < tasks_.size(); ++i) {
      if (stopRequested_)
         break;
      printf("Task #%d started, type %d\n", i, tasks_[i]->type);
      tasks_[i]->run();
   }

   core_->setAutoShutter(initialAutoshutter);

   finished_ = true;
}

bool MMAcquisitionEngine::IsFinished() {
   return finished_;
}

void MMAcquisitionEngine::Stop() {
   stopRequested_ = true;
}

void MMAcquisitionEngine::Pause() {
   pauseRequested_ = true;
}

void MMAcquisitionEngine::Resume() {
   pauseRequested_ = false;
}

void MMAcquisitionEngine::Step() {
}

void MMAcquisitionEngine::SetTasks(TaskVector tasks) {
   tasks_ = tasks;
}

void MMAcquisitionEngine::GenerateSequence(AcquisitionSettings acquisitionSettings)
{
   ImageRequest imageRequest;
   imageRequest.runAutofocus = acquisitionSettings.useAutofocus;
   imageRequest.closeShutter = true;

   if (acquisitionSettings.positionList.size() == 0 && acquisitionSettings.timeSeries.size() == 0)
   { 
      imageRequest.positionIndex = -1;
      imageRequest.timeIndex = -1;
      GenerateSlicesAndChannelsSubsequence(acquisitionSettings, imageRequest);
   }
   else if (acquisitionSettings.positionList.size() == 0)
   {
      imageRequest.positionIndex = -1;
      for(imageRequest.timeIndex = 0; imageRequest.timeIndex < acquisitionSettings.timeSeries.size(); ++imageRequest.timeIndex)
      {
         imageRequest.waitTime = MM::MMTime(acquisitionSettings.timeSeries[imageRequest.timeIndex] * 1000);
         GenerateSlicesAndChannelsSubsequence(acquisitionSettings, imageRequest);
      }
   }
   else if (acquisitionSettings.timeSeries.size() == 0)
   {
      imageRequest.timeIndex = -1;
      for(imageRequest.positionIndex = 0; imageRequest.positionIndex < acquisitionSettings.positionList.size(); ++imageRequest.positionIndex)
      {
         imageRequest.multiAxisPosition = acquisitionSettings.positionList[imageRequest.positionIndex];
         GenerateSlicesAndChannelsSubsequence(acquisitionSettings, imageRequest);
      }
   }
   else // times and positions are both specified
   {
      if (acquisitionSettings.positionsFirst)
      {
         for(imageRequest.timeIndex = 0; imageRequest.timeIndex < acquisitionSettings.timeSeries.size(); ++imageRequest.timeIndex)
         {
            imageRequest.waitTime = MM::MMTime(acquisitionSettings.timeSeries[imageRequest.timeIndex] * 1000);
            for(imageRequest.positionIndex = 0; imageRequest.positionIndex < acquisitionSettings.positionList.size(); ++imageRequest.positionIndex)
            {
               imageRequest.multiAxisPosition = acquisitionSettings.positionList[imageRequest.positionIndex];
               GenerateSlicesAndChannelsSubsequence(acquisitionSettings, imageRequest);
            }
         }
      }
      else // time first
      {
         for(imageRequest.positionIndex = 0; imageRequest.positionIndex < acquisitionSettings.positionList.size(); ++imageRequest.positionIndex)
         {
            imageRequest.multiAxisPosition = acquisitionSettings.positionList[imageRequest.positionIndex];
            for(imageRequest.timeIndex = 0; imageRequest.timeIndex < acquisitionSettings.timeSeries.size(); ++imageRequest.timeIndex)
            {
               imageRequest.waitTime = MM::MMTime(acquisitionSettings.timeSeries[imageRequest.timeIndex] * 1000);
               GenerateSlicesAndChannelsSubsequence(acquisitionSettings, imageRequest);
            }
         }
      }
   }
}

void MMAcquisitionEngine::GenerateSlicesAndChannelsSubsequence(AcquisitionSettings acquisitionSettings, ImageRequest imageRequest)
{
   imageRequest.runAutofocus = (acquisitionSettings.useAutofocus // &&
      && (0 == (imageRequest.timeIndex % (1 + acquisitionSettings.autofocusSkipFrames))));

   if (acquisitionSettings.zStack.size() == 0 && acquisitionSettings.channelList.size() == 0)
   {
      imageRequest.sliceIndex = -1;
      imageRequest.channelIndex = -1;
      tasks_.push_back(new ImageTask(this, imageRequest));
   }
   else if (acquisitionSettings.zStack.size() == 0)
   {
      imageRequest.sliceIndex = -1;
      for(imageRequest.channelIndex = 0; imageRequest.channelIndex < acquisitionSettings.channelList.size(); ++imageRequest.channelIndex)			
      {
         if (acquisitionSettings.keepShutterOpenChannels && imageRequest.channelIndex > 0)
            imageRequest.closeShutter = false;
         imageRequest.channel = acquisitionSettings.channelList[imageRequest.channelIndex];
         if (0 == (imageRequest.timeIndex % (imageRequest.channel.skipFrames + 1)))
            tasks_.push_back(new ImageTask(this, imageRequest));
      }
   }
   else if (acquisitionSettings.channelList.size() == 0)
   {
      imageRequest.channelIndex = -1;
      for(imageRequest.sliceIndex = 0; imageRequest.sliceIndex < acquisitionSettings.zStack.size(); ++imageRequest.sliceIndex)
      {
         if (acquisitionSettings.keepShutterOpenSlices && imageRequest.sliceIndex > 0)
            imageRequest.closeShutter = false;
         imageRequest.slicePosition = acquisitionSettings.zStack[imageRequest.sliceIndex];
         tasks_.push_back(new ImageTask(this, imageRequest));
      }
   }
   else // slices and channels are both specified
   {
      if (acquisitionSettings.channelsFirst)
      {
         for(imageRequest.sliceIndex = 0; imageRequest.sliceIndex < acquisitionSettings.zStack.size(); ++imageRequest.sliceIndex)
         {
            imageRequest.slicePosition = acquisitionSettings.zStack[imageRequest.sliceIndex];
            for(imageRequest.channelIndex = 0; imageRequest.channelIndex < acquisitionSettings.channelList.size(); ++imageRequest.channelIndex)			
            {
               if (acquisitionSettings.keepShutterOpenSlices && imageRequest.sliceIndex > 0 && imageRequest.channel.useZStack)
                  imageRequest.closeShutter = false;
               if (acquisitionSettings.keepShutterOpenChannels && imageRequest.channelIndex > 0)
                  imageRequest.closeShutter = false;
               imageRequest.channel = acquisitionSettings.channelList[imageRequest.channelIndex];
               if (imageRequest.channel.useZStack || (imageRequest.sliceIndex == (acquisitionSettings.zStack.size()-1)/2))
               {
                  if (0 == (imageRequest.timeIndex % (imageRequest.channel.skipFrames + 1)))
                     tasks_.push_back(new ImageTask(this, imageRequest));
               }
            }
         }
      }
      else // slices first
      {
         for(imageRequest.channelIndex = 0; imageRequest.channelIndex < acquisitionSettings.channelList.size(); ++imageRequest.channelIndex)			
         {
            if (0 == (imageRequest.timeIndex % (imageRequest.channel.skipFrames + 1)))
            {
               if (acquisitionSettings.keepShutterOpenChannels && imageRequest.channelIndex > 0)
                  imageRequest.closeShutter = false;
               imageRequest.channel = acquisitionSettings.channelList[imageRequest.channelIndex];
               for(imageRequest.sliceIndex = 0; imageRequest.sliceIndex < acquisitionSettings.zStack.size(); ++imageRequest.sliceIndex)
               {
                  if (imageRequest.channel.useZStack || (imageRequest.sliceIndex == (acquisitionSettings.zStack.size()-1)/2))
                  {
                     if (acquisitionSettings.keepShutterOpenSlices && imageRequest.sliceIndex > 0 && imageRequest.channel.useZStack)
                        imageRequest.closeShutter = false;
                     imageRequest.slicePosition = acquisitionSettings.zStack[imageRequest.sliceIndex];
                     tasks_.push_back(new ImageTask(this, imageRequest));
                  }
               }
            }
         }
      }
   }
}
