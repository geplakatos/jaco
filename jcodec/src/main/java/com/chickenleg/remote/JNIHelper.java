package com.chickenleg.remote;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.zip.*;
import java.security.*;

public class JNIHelper {

    public JNIHelper() {
    }

    public void load(String pathwithoutsuffix) throws Exception {

        CodeSource src = getClass().getProtectionDomain().getCodeSource();
        if (src != null) {
            byte[] b = new byte[2048];
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null) {
                    break;
                }
                String name = e.getName();
                if (name.indexOf(pathwithoutsuffix) != -1) {
                    File tmp = File.createTempFile("csl-jni-lib", ".tmp");
                    FileOutputStream fos = new FileOutputStream(tmp);
                    tmp.deleteOnExit();
    
                    int l = 0;
                    while ((l = zip.read(b)) > 0) {
                        fos.write(b, 0, l);
                    }
                    fos.close();
                    
                    try {
                        System.load(tmp.getAbsolutePath());
                        Log.log("loaded: " + name + " as " + tmp.getAbsolutePath());
                    } catch (Throwable t) {
                        //t.printStackTrace();
                    }
                }
            }
        }
    }
}
