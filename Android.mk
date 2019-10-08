LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under,src)
LOCAL_PACKAGE_NAME := DoovReg
LOCAL_CERTIFICATE := platform
LOCAL_MULTILIB := 32
LOCAL_STATIC_JAVA_LIBRARIES += httpcore-4.4.4 lqops imeicore gson-2.2.4
LOCAL_JAVA_LIBRARIES += telephony-common
LOCAL_DEX_PREOPT := false 
#LOCAL_PRIVILEGED_MODULE := true
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += httpcore-4.4.4:libs/httpcore-4.4.4.jar lqops:libs/lqops.jar imeicore:libs/imeicore.jar gson-2.2.4:libs/gson-2.2.4.jar
LOCAL_MULTILIB := 32
include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))
