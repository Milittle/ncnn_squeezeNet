LOCAL_PATH := $(call my-dir)

# change this folder path to yours
NCNN_INSTALL_PATH := C:\\Users\\milittle\\Desktop\\build-android-armv7\\install

# module one
include $(CLEAR_VARS)
LOCAL_MODULE := ncnn
LOCAL_SRC_FILES := $(NCNN_INSTALL_PATH)\\lib\\libncnn.a
include $(PREBUILT_STATIC_LIBRARY)

# module two
include $(CLEAR_VARS)
LOCAL_MODULE := defect_ncnn
LOCAL_SRC_FILES := defect_jni.cpp
LOCAL_C_INCLUDES := $(NCNN_INSTALL_PATH)\\include
LOCAL_STATIC_LIBRARIES := ncnn

LOCAL_CFLAGS := -O2 -fvisibility=hidden -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math
LOCAL_CPPFLAGS := -O2 -fvisibility=hidden -fvisibility-inlines-hidden -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math
LOCAL_LDFLAGS += -Wl,--gc-sections

LOCAL_CFLAGS += -fopenmp
LOCAL_CPPFLAGS += -fopenmp
LOCAL_LDFLAGS += -fopenmp

LOCAL_LDLIBS := -lz -llog -ljnigraphics

# build the shared library
include $(BUILD_SHARED_LIBRARY)
