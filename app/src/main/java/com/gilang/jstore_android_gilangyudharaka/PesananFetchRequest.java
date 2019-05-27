package com.gilang.jstore_android_gilangyudharaka;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


public class PesananFetchRequest extends StringRequest {
    private static final String REGIS_URL = "http://192.168.1.4:8080/invoicecustomer/";

    public PesananFetchRequest(int id_customer, Response.Listener<String> listener) {

        super(Method.GET, REGIS_URL+id_customer, listener, null);
        Log.d("", "PesananFetchRequest: "+id_customer);

    }
}
