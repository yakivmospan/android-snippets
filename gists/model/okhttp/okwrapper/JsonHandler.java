package com.ls.skiresort.model.util;

import org.json.JSONObject;

public abstract class JsonHandler<ResultType> {
    public abstract void parse(String js);
    public abstract ResultType getResult();

    public String optString(String theKey, JSONObject theJson) {
        return theJson.isNull(theKey) ? null : theJson.optString(theKey);
    }

    public String optString(String theKey, JSONObject theJson, String theDefaultValue) {
        return theJson.isNull(theKey) ? null : theJson.optString(theKey, theDefaultValue);
    }
}
