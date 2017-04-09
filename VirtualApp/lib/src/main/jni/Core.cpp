//
// VirtualApp Native Project
//
#include "Core.h"


JavaVM *gVm;
jclass gClass;


void Java_nativeHookNative(JNIEnv *env, jclass jclazz, jobjectArray javaMethods,
                           jstring packageName,
                           jboolean isArt, jint apiLevel, jint cameraMethodType) {
    patchAndroidVM(javaMethods, packageName, isArt, apiLevel, cameraMethodType);
}


void Java_nativeHook(JNIEnv *env, jclass jclazz, jint apiLevel) {
    IOUniformer::startUniformer(apiLevel);
}

void Java_nativeReadOnly(JNIEnv *env, jclass jclazz, jstring _path) {
    const char *path = env->GetStringUTFChars(_path, NULL);
    IOUniformer::readOnly(path);
}

void Java_nativeRedirect(JNIEnv *env, jclass jclazz, jstring orgPath, jstring newPath) {
    const char *org_path = env->GetStringUTFChars(orgPath, NULL);
    const char *new_path = env->GetStringUTFChars(newPath, NULL);
    IOUniformer::redirect(org_path, new_path);
}

jstring Java_nativeQuery(JNIEnv *env, jclass jclazz, jstring orgPath) {
    const char *org_path = env->GetStringUTFChars(orgPath, NULL);
    const char *redirected_path = IOUniformer::query(org_path);
    return env->NewStringUTF(redirected_path);
}

jstring Java_nativeRestore(JNIEnv *env, jclass jclazz, jstring redirectedPath) {
    const char *redirected_path = env->GetStringUTFChars(redirectedPath, NULL);
    const char *org_path = IOUniformer::restore(redirected_path);
    return env->NewStringUTF(org_path);
}


static JNINativeMethod gMethods[] = {
        NATIVE_METHOD((void *) Java_nativeHook, "nativeHook", "(I)V"),
        NATIVE_METHOD((void *) Java_nativeReadOnly, "nativeReadOnly", "(Ljava/lang/String;)V"),
        NATIVE_METHOD((void *) Java_nativeRedirect, "nativeRedirect",
                      "(Ljava/lang/String;Ljava/lang/String;)V"),
        NATIVE_METHOD((void *) Java_nativeQuery, "nativeGetRedirectedPath",
                      "(Ljava/lang/String;)Ljava/lang/String;"),
        NATIVE_METHOD((void *) Java_nativeRestore, "nativeRestoreRedirectedPath",
                      "(Ljava/lang/String;)Ljava/lang/String;"),

        NATIVE_METHOD((void *) Java_nativeHookNative, "nativeHookNative",
                      "(Ljava/lang/Object;Ljava/lang/String;ZII)V"),
};


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jclass javaClass = env->FindClass(JAVA_CLASS);
    if (javaClass == NULL) {
        LOGE("Error: Unable to find the IOHook class.");
        return JNI_ERR;
    }
    if (env->RegisterNatives(javaClass, gMethods, NELEM(gMethods)) < 0) {
        LOGE("Error: Unable to register the native methods.");
        return JNI_ERR;
    }
    gVm = vm;
    gClass = (jclass) env->NewGlobalRef(javaClass);
    env->DeleteLocalRef(javaClass);
    return JNI_VERSION_1_6;
}


JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }
    env->DeleteGlobalRef((jobject) gVm);
    env->DeleteGlobalRef((jobject) gClass);
}

