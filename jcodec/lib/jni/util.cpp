#include "com_chickenleg_remote_MemoryEconomiseScreenTaker.h"
JNIEXPORT jint* JNICALL checkParameters(JNIEnv *env, jobject _this, jint imgx, jint imgy, jint w, jint h, jintArray dest) {    
    if (dest == NULL) {
        env->ThrowNew(env->FindClass("java/lang/NullPointerException"), "destination buffer is null");
        return NULL;
    }

    int destlength = env->GetArrayLength(dest);
    int l = w * h;    

    if (l > destlength) {        
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "destination buffer is too small");
        return NULL;
    }

    if (w <= 0) {
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "width <= 0");
        return NULL;
    }

    if (h <= 0) {
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "height <= 0");
        return NULL;
    }

    jint* ints = env->GetIntArrayElements(dest, 0);
    if (ints == NULL) {
        env->ThrowNew( env->FindClass("java/lang/NullPointerException"), "destination pointer is null");
        return NULL;
    }  
    
    return ints;
}
