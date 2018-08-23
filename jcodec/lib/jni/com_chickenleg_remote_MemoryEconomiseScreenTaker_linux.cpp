#include "com_chickenleg_remote_MemoryEconomiseScreenTaker.h"
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <string.h>

JNIEXPORT void JNICALL Java_com_chickenleg_remote_MemoryEconomiseScreenTaker_take(JNIEnv *env, jobject _this, jint imgx, jint imgy, jint w, jint h, jintArray dest) {
    jclass jcls = NULL;
    XImage *image = NULL;
    Window root = {0};
    Display *display = NULL;
    jint* ints = checkParameters(env, _this, imgx, imgy, w, h, dest);
    int release_type = JNI_ABORT;
    if (!ints) {
        return;
    }

    display = XOpenDisplay(NULL);
    if (display == NULL) {
        jcls = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(jcls, "XOpenDisplay failed");
        goto finish;
    }

    root = DefaultRootWindow(display);
    image = XGetImage(display, root, imgx, imgy, w, h, AllPlanes, ZPixmap);
    if (!image) {
        jcls = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(jcls, "XGetImage failed");
        goto finish;
    }
    
    if (image->bits_per_pixel != 32) {
        jcls = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(jcls, "XGetImage invalid bit depth");
        goto finish;
    }
    
    memcpy(ints, image->data, 4 * w * h);
    release_type = 0;

finish:
    if (ints) {
        env->ReleaseIntArrayElements(dest, ints, release_type);
    }

    if (image) {
        XDestroyImage(image);
    }

    if (display) {
        XCloseDisplay(display);
    }
}

/*
 sudo dnf install libX11-devel
 https://blogs.oracle.com/moonocean/entry/a_simple_example_of_jni
 http://stackoverflow.com/questions/2065912/core-dumped-but-core-file-is-not-in-current-directory
 $ cat /proc/sys/kernel/core_pattern 
|/usr/lib/systemd/systemd-coredump %p %u %g %s %t %e
This behaviour can be disabled with a simple "hack":

$ ln -s /dev/null /etc/sysctl.d/50-coredump.conf
$ sysctl -w kernel.core_pattern=core      # or just reboot
 * 
 * gdb core.717
 * sharedlibrary
 * bt
 * 
 *  
 g++ -ggdb -shared -Wall -fpic -o libtake.so -I/home/geplakatos/jdk1.8.0_101/include -I/home/geplakatos/jdk1.8.0_101/include/linux *_linux.cpp util.cpp -lX11
 */