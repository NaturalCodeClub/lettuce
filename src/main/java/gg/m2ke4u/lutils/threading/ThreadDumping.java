package gg.m2ke4u.lutils.threading;

import java.util.StringJoiner;

public class ThreadDumping {
    /**
     * Forked from bilibili.Link:https://www.bilibili.com/video/BV1e44y1677h?spm_id_from=333.999.0.0
     * A simple method can print the thread stats and current time and current thread's name
     * @param action
     * @return
     */
    public static String genCurrentThreadInfo(String action){
        return new StringJoiner("\t|\t")
                .add(String.valueOf(System.currentTimeMillis()))
                .add(String.valueOf(Thread.currentThread().getId()))
                .add(String.valueOf(Thread.currentThread().getName()))
                .add(String.valueOf(Thread.currentThread().getState()))
                .add(action).toString();
    }

    public static String geOtherThreadInfo(Thread thread){
        return new StringJoiner("\t|\t")
                .add(String.valueOf(System.currentTimeMillis()))
                .add(String.valueOf(thread.getId()))
                .add(String.valueOf(thread.getName()))
                .add(String.valueOf(thread.getState())).toString();
    }
}
