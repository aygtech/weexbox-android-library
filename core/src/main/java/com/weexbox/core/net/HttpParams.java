package com.weexbox.core.net;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpParams implements Serializable {

    private final LinkedHashMap<String, Object> urlParams = new LinkedHashMap<String, Object>();
    private LinkedHashMap<String, File> fileParams;

    public HttpParams(Map<String, String> source) {
        if (source != null) {
            for (Map.Entry<String, String> entry : source.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    public HttpParams(final String key, final String value) {
        this(new HashMap<String, String>() {
            {
                put(key, value);
            }
        });
    }

    public HttpParams(Object... keysAndValues) {
        int len = keysAndValues.length;
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Supplied arguments must be even");
        }
        for (int i = 0; i < len; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    public void put(String key, Object value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    public void put(String key, File file) {
        if (key != null) {
            if (fileParams == null) {
                fileParams = new LinkedHashMap<>();
            }
            fileParams.put(key, file);
        }
    }

    public void remove(String key) {
        urlParams.remove(key);
    }

    public boolean has(String key) {
        return urlParams.get(key) != null;
    }

    public String convertToJson() {
        return JSON.toJSONString(urlParams);
    }

    public LinkedHashMap<String, String> getUrlParams() {
        final LinkedHashMap<String, String> temp = new LinkedHashMap<>();
        for (ConcurrentHashMap.Entry<String, Object> entry : urlParams.entrySet()) {
            final Object object = entry.getValue();
            temp.put(entry.getKey(), String.valueOf(object));
        }
        return temp;
    }

    public LinkedHashMap<String, File> getFileParams() {
        return fileParams;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, Object> entry : urlParams.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue().toString());
        }
        return result.toString();
    }
}
