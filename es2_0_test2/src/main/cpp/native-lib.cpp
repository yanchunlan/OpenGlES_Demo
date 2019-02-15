#include <jni.h>
#include <string>


extern "C"
{
#include "librtmp/rtmp.h"
}


extern "C"
JNIEXPORT void JNICALL
Java_com_opengles_book_es2_10_1test2_push_PushVideo_initPush(JNIEnv *env, jobject instance,
                                                             jstring pushUrl_) {
    const char *pushUrl = env->GetStringUTFChars(pushUrl_, 0);



    env->ReleaseStringUTFChars(pushUrl_, pushUrl);
}