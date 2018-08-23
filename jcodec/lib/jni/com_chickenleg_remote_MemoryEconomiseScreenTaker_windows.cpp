#include "com_chickenleg_remote_MemoryEconomiseScreenTaker.h"
#include <windows.h>

JNIEXPORT void JNICALL Java_com_chickenleg_remote_MemoryEconomiseScreenTaker_take(JNIEnv *env, jobject _this, jint imgx, jint imgy, jint w, jint h, jintArray dest) {
    jint* ints = checkParameters(env, _this, imgx, imgy, w, h, dest);

    int release_type = JNI_ABORT;
    if (!ints) {
        return;
    }

    jclass jcls = NULL;
    HDC hScreen = GetDC(GetDesktopWindow());
    if (!hScreen) {
        jcls = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(jcls, "GetDC-screen error");
        goto finish;
    }

    HDC hdcMem = CreateCompatibleDC(hScreen);
    if (!hdcMem) {
        jcls = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(jcls, "CreateCompatibleDC error");
        goto finish;
    }

    HBITMAP hBitmap = CreateCompatibleBitmap(hScreen, w, h);
    if (!hBitmap) {
        jcls = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(jcls, "CreateCompatibleBitmap error");
        goto finish;
    }

    BOOL ret = FALSE;
    HGDIOBJ hOld = SelectObject(hdcMem, hBitmap);

    ret = BitBlt(hdcMem, imgx, imgy, w, h, hScreen, 0, 0, SRCCOPY);
    SelectObject(hdcMem, hOld);
    if (!ret) {
        jcls = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(jcls, "BitBlt error");
        goto finish;
    }

    char *bytes = (char*) ints;

    BITMAPINFOHEADER bmi = {0};
    bmi.biSize = sizeof (BITMAPINFOHEADER);
    bmi.biPlanes = 1;
    bmi.biBitCount = 32;
    bmi.biWidth = w;
    bmi.biHeight = -h;
    bmi.biCompression = BI_RGB;

    GetDIBits(hdcMem, hBitmap, 0, h, bytes, (BITMAPINFO*) & bmi, DIB_RGB_COLORS);
    release_type = 0;

finish:
    env->ReleaseIntArrayElements(dest, ints, release_type);

    if (hBitmap) {
        DeleteObject(hBitmap);
    }

    if (hdcMem) {
        DeleteDC(hdcMem);
    }

    if (hScreen) {
        ReleaseDC(GetDesktopWindow(), hScreen);
    }


}

/*
 * 64 bit
 * "c:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\vcvarsall.bat" x86_amd64
 * cl.exe /I "c:\Program Files\Java\jdk1.8.0_102\include" /I "c:\Program Files\Java\jdk1.8.0_102\include\win32" /LD util.cpp com_chickenleg_remote_MemoryEconomiseScreenTaker_windows.cpp  gdi32.lib user32.lib
 * 
 * 32bit
 * "c:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\vcvarsall.bat" x86
 cl.exe /I "c:\Program Files\Java\jdk1.8.0_20\include" /I "c:\Program Files\Java\jdk1.8.0_20\include\win32" /LD *.cpp gdi32.lib user32.lib
 */