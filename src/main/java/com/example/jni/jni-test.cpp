#include "com_example_jni_CppLibraryJniTest.h"

JNIEXPORT jint JNICALL Java_com_example_jni_CppLibraryJniTest_test_1add
        (JNIEnv *env, jclass clazz, jint a, jint b) {
    // 参数1:class 参数2:方法名 参数3:方法返回类型签名(通过 'javap -s 类名' 获取)
    jmethodID method = env->GetMethodID(clazz, "obj_test", "()V");
    // 创建class实例
    jobject obj = env->AllocObject(clazz);
    // 调用方法
    env->CallVoidMethod(obj, method);

    jmethodID static_method = env->GetStaticMethodID(clazz, "class_test", "(Ljava/lang/String;)V");
    env->CallStaticVoidMethod(clazz, static_method, env->NewStringUTF("cpp-text"));
    return a + b;
}