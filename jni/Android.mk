LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# OpenCV
#OPENCV_CAMERA_MODULES:=on
#OPENCV_INSTALL_MODULES:=on
#OPENCV_LIB_TYPE:=STATIC
include /home/amnipar/Android/workspace/opencv-2.4.3/sdk/native/jni/OpenCV.mk
LOCAL_C_INCLUDES := $(LOCAL_PATH)/cvsu $(LOCAL_PATH)/../../opencv-2.4.3/sdk/native/jni/include

#LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../opencv-2.4.3/sdk/native/jni/include
LOCAL_MODULE    := cvsu
LOCAL_SRC_FILES := cvsu_test.cpp cvsu/cvsu_output.c cvsu/cvsu_memory.c cvsu/cvsu_types.c cvsu/cvsu_list.c cvsu/cvsu_pixel_image.c cvsu/cvsu_integral.c cvsu/cvsu_filter.c cvsu/cvsu_edges.c cvsu/cvsu_quad_forest.c
LOCAL_LDLIBS := -llog -ldl -lz

#LOCAL_LDLIBS += -L/home/amnipar/Android/workspace/opencv-2.4.3/sdk/native/libs/armeabi-v7a
#LOCAL_LDLIBS += -ltbb -lopencv_calib3d -lopencv_contrib -lopencv_flann -opencv_highgui -lopencv_video -lopencv_legacy -lopencv_ml -lopencv_objdetect -lopencv_features2d -lopencv_imgproc -lopencv_core

include $(BUILD_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE := libopencv_java
#LOCAL_SRC_FILES := libopencv_java.so
#include $(PREBUILT_SHARED_LIBRARY)
