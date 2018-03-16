package com.taobao.pamirs.schedule;

public class TraceId {
    static ThreadLocal traceId = new ThreadLocal();

    public static String set(String id) {
        traceId.set(id);
        return id;
    }

    public static String get() {
        return String.valueOf(traceId.get());
    }
}
