package com.gilang.jstore_android_gilangyudharaka;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class InvoiceFetchRequest extends StringRequest {
    private static final String BASE_URL = "http://192.168.1.4:8080/invoice/";
    public InvoiceFetchRequest(int id, Response.Listener<String> listener) {
        super(Method.GET, BASE_URL+id, listener, null);
    }
}
