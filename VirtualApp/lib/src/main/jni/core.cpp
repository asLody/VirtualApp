//
// VirtualApp Native Project
//
#include "core.h"


JavaVM *g_vm;
jclass g_jclass;



void hook_native(JNIEnv *env, jclass jclazz, jobject javaMethod, jboolean isArt, jint apiLevel) {
    static bool hasHooked = false;
    if (hasHooked) {
        return;
    }
    hookNative(javaMethod, isArt, apiLevel);
    hasHooked = true;
}



static JNINativeMethod gMethods[] = {
        NATIVE_METHOD((void *) hook_native, "nativeHookNative", "(Ljava/lang/Object;ZI)V"),
};



JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jclass javaClass = env->FindClass(JAVA_CLASS);
    if (javaClass == NULL) {
        LOGE("Ops: Unable to find hook class.");
        return JNI_ERR;
    }
    if (env->RegisterNatives(javaClass, gMethods, NELEM(gMethods)) < 0) {
        LOGE("Ops: Unable to register the native methods.");
        return JNI_ERR;
    }
    g_vm = vm;
    g_jclass = (jclass) env->NewGlobalRef(javaClass);
    env->DeleteLocalRef(javaClass);
    return JNI_VERSION_1_6;
}



JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }
    env->DeleteGlobalRef((jobject)g_vm);
    env->DeleteGlobalRef((jobject)g_jclass);
}

