LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := iohook

LOCAL_CFLAGS := -Wno-error=format-security -fpermissive
LOCAL_CFLAGS += -fno-rtti -fno-exceptions

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/hook
LOCAL_C_INCLUDES += $(LOCAL_PATH)/helper
LOCAL_C_INCLUDES += $(LOCAL_PATH)/MSHook

LOCAL_SRC_FILES := core.cpp \
				   HookNative.cpp

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)


