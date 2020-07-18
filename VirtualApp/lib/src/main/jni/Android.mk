LOCAL_PATH := $(call my-dir)
MAIN_LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := va++

LOCAL_CFLAGS := -Wno-error=format-security -fpermissive -DLOG_TAG=\"VA++\"
LOCAL_CFLAGS += -fno-rtti -fno-exceptions

LOCAL_C_INCLUDES += $(MAIN_LOCAL_PATH)

LOCAL_SRC_FILES := Bypass.cpp

LOCAL_LDLIBS := -llog -latomic
LOCAL_STATIC_LIBRARIES := fb

include $(BUILD_SHARED_LIBRARY)
include $(MAIN_LOCAL_PATH)/fb/Android.mk
