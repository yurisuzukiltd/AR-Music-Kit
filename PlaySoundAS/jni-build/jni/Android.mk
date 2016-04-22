LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE           := main
LOCAL_SRC_FILES        := main.c
LOCAL_SHARED_LIBRARIES := fmodex

LOCAL_C_INCLUDES += $(LOCAL_PATH)/inc

LOCAL_LDLIBS    := \
	-llog \
	$(LOCAL_PATH)/lib/$(TARGET_ARCH_ABI)/libfmodex.so \
	$(LOCAL_PATH)/lib/$(TARGET_ARCH_ABI)/libfmodexL.so \

NDK_MODULE_PATH := $(call my-dir)

include $(BUILD_SHARED_LIBRARY)
