/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chickenleg.remote;

import java.io.*;
import java.util.*;
import java.nio.charset.*;
import java.text.*;

/**
 *
 * @author piccolo
 */
public class Log {
    public static boolean enabledlog = true;
    private static DateTimeHelper dth = new DateTimeHelper();

    public static void log(Object... s) {
        logWithStackPos(3,s);
    }
    
    public static void logWithStackPos(int pos,Object... s) {
        if (enabledlog) {
            String ss = "";
            if (s != null && s.length > 0) {
                for (int i = 0; i < s.length; i++) {
                    ss += s[i] + ",";
                }
                ss = ss.substring(0, ss.length() - 1);
            }
            write(ss, pos);
        }
    }

    public static void log(Object s) {
        if (enabledlog) {
            write(s, 2);
        }
    }

    private static void write(Object s, int stackpos) {
        Thread t = Thread.currentThread();
        StackTraceElement[] st = new Exception().getStackTrace();
        long id = t.getId();
        String print = dth.getTimeAsString() + " [" + ((st != null && st.length > stackpos) ? st[stackpos] : "???") + "]#" + id + ": " + s;
        System.out.println(print);
    }
}
