package com.gilang.jstore_android_gilangyudharaka;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MenuRequest extends StringRequest {
    private static final String Regis_URL = "http://10.10.52.135:8080/items";
    private Map<String, String> params;


    public MenuRequest(Response.Listener<String> listener) {
        super(Request.Method.GET, Regis_URL, listener, null);}


    @Override
    public Map<String, String> getParams(){
        return params;
    }
}