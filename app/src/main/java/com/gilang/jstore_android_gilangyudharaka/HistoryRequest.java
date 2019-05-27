package com.gilang.jstore_android_gilangyudharaka;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class HistoryRequest extends StringRequest {
    private static final String HISTORY_URL = "http://192.168.1.4:8080/invoicehistorycust/";

    public HistoryRequest(int id_customer, Response.Listener<String> listener) {

        super(Method.GET, HISTORY_URL+id_customer, listener, null);


    }
}
