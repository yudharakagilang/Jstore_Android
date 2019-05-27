package com.gilang.jstore_android_gilangyudharaka;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private ArrayList<Supplier> listSupplier = new ArrayList<>();
    private ArrayList<Item> listItem = new ArrayList<>();
    private HashMap<Supplier, ArrayList<Item>> childMapping = new HashMap<>();

    private ArrayList<Invoice> invoices = new ArrayList<>();

    private static int currentUserId;
    private static String currentUserName;


    MainListAdapter mainListAdapter;
    ExpandableListView expListView;
    TextView tvTest;

    RecyclerView rvOrder;

    Toolbar toolbar;
    Button ivHistory;

    public static int getCustomerId() {
        return currentUserId;
    }


    public static String getCustomerName() {
        return currentUserName;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            currentUserId = extras.getInt("currentUserId");
            currentUserName = extras.getString("currentUserName");
        }

        expListView = findViewById(R.id.lvExp);
        tvTest = findViewById(R.id.tvTest);
        rvOrder = findViewById(R.id.rvOrder);


        ivHistory = findViewById(R.id.history_button);
        setSupportActionBar(toolbar);

        refreshList();
        initRecyclerView();

        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
                intent.putExtra("currentUserId",currentUserId);
                startActivity(intent);
            }
        });
        tvTest.setText("Customer Name :"+currentUserName);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Intent intent = new Intent(MainActivity.this, BuatPesananActivity.class);

                int item_id = childMapping.get(listSupplier.get(i)).get(i1).getId();
                String item_name = childMapping.get(listSupplier.get(i)).get(i1).getName();
                String item_category = childMapping.get(listSupplier.get(i)).get(i1).getCategory();
                String item_status = childMapping.get(listSupplier.get(i)).get(i1).getStatus();
                int item_price = childMapping.get(listSupplier.get(i)).get(i1).getPrice();

                intent.putExtra("item_id",item_id);
                intent.putExtra("item_name",item_name);
                intent.putExtra("item_category",item_category);
                intent.putExtra("item_status",item_status);
                intent.putExtra("item_price",item_price);

                intent.putExtra("currentUserId", currentUserId);
                intent.putExtra("currentUserName", currentUserName);

                startActivity(intent);
                return true;
            }
        });

    }

    private void initRecyclerView() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    Log.d("reg_resp", "Register Response: " + response.toString());
                    for(int i=0;i<jsonResponse.length();i++){
                        JSONObject invoice = jsonResponse.getJSONObject(i);
                        int id = invoice.getInt("id");
                        String date = invoice.getString("date");
                        JSONArray item_json = invoice.getJSONArray("item");


                        ArrayList<String> items = new ArrayList<>();
                        for(int y=0; y<item_json.length();y++){
                            for (Item item : listItem){
                                if (item.getId() == Integer.valueOf(item_json.get(y).toString().trim())){
                                    items.add(item.getName());
                                }
                            }
                        }


                        String invoiceType = invoice.getString("invoiceType");
                        String invoiceStatus = invoice.getString("invoiceStatus");
                        Integer totalPrice = invoice.getInt("totalPrice");

                        if(invoiceStatus.equals("Paid")){
                            Invoice temp = new Invoice(id,date,items,totalPrice, invoiceType, invoiceStatus);
                            temp.setActive(false);
                            invoices.add(temp);
                        }else if(invoiceStatus.equals("Unpaid")){
                            Invoice temp = new Invoice(id,date,items,totalPrice, invoiceType, invoiceStatus);
                            temp.setActive(true);
                            invoices.add(temp);
                        }else if(invoiceStatus.equals("Installment")){
                            int installmentPeriod = invoice.getInt("installmentPeriod");
                            int installmentPrice = invoice.getInt("installmentPrice");
                            Invoice temp = new Invoice(id,date,items,totalPrice, invoiceType, invoiceStatus,installmentPeriod,installmentPrice);
                            temp.setActive(true);
                            invoices.add(temp);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OrderRecyclerViewAdapter adapter = new OrderRecyclerViewAdapter(invoices,MainActivity.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                rvOrder.setLayoutManager(layoutManager);
                rvOrder.setAdapter(adapter);
            }
        };

        PesananFetchRequest request = new PesananFetchRequest(currentUserId,responseListener);
        RequestQueue queue = new Volley().newRequestQueue(MainActivity.this);
        queue.add(request);


    }

    protected void refreshList(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for(int i=0;i<jsonResponse.length();i++){
                        //parsing JSON data
                        JSONObject item = jsonResponse.getJSONObject(i);

                        JSONObject supplier = item.getJSONObject("supplier");
                        JSONObject location = supplier.getJSONObject("location");

                        //parsing location data and creating new object
                        String city = location.getString("city");
                        String province = location.getString("province");
                        String description = location.getString("description");

                        Location location1 = new Location(city,province,description);



                        int supplierId = supplier.getInt("id");
                        String supplierName = supplier.getString("name");
                        String supplierEmail = supplier.getString("email");
                        String supplierNumber = supplier.getString("phoneNumber");

                        Supplier newSupplier = new Supplier(supplierId, supplierName, supplierEmail, supplierNumber, location1);


                        if(listSupplier.size()>0){
                            boolean success = true;
                            for (Supplier object : listSupplier){
                                if((object.getId()==(newSupplier.getId()))){
                                    success = false;
                                }
                            }
                            if(success){
                                listSupplier.add(newSupplier);
                            }
                        }
                        else{
                            listSupplier.add(newSupplier);
                        }

                        //parsing item and creating new object
                        int itemId = item.getInt("id");
                        int itemPrice = item.getInt("price");
                        String itemName = item.getString("name");
                        String itemCategory = item.getString("category");
                        String itemStatus = item.getString("status");

                        Item newItem = new Item(itemId,itemName,itemPrice, itemCategory, itemStatus,newSupplier);


                        listItem.add(newItem);

                    } //end of jsonResponse loop


                    //creating childMapping
                    for(Supplier supplier:listSupplier){
                        ArrayList<Item> tmp = new ArrayList<>();
                        for(Item item:listItem){
                            if(item.getSupplier().getId() == supplier.getId()){
                                tmp.add(item);
                            }
                        }
                        childMapping.put(supplier,tmp);
                    }

                } catch (JSONException e) {
                }



                ArrayList<String> listDataHeader= new ArrayList<>();
                HashMap<String, ArrayList<String>> listDataChild = new HashMap<>();

                for(Supplier s : listSupplier){
                    listDataHeader.add(s.getName());
                    ArrayList<Item> tmpItem = childMapping.get(s);
                    ArrayList<String> item = new ArrayList<>();
                    for(Item i : tmpItem){
                        item.add(i.getName());
                        Log.d("items", String.valueOf(i.getName()));
                    }
                    listDataChild.put(s.getName(),item);


                }


                mainListAdapter = new MainListAdapter(MainActivity.this,listDataHeader,listDataChild);
                expListView.setAdapter(mainListAdapter);


            }
        };

        MenuRequest menuRequest = new MenuRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(menuRequest);



    }

}

