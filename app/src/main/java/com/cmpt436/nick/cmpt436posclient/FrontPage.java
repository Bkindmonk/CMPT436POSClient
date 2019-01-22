package com.cmpt436.nick.cmpt436posclient;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;


public class FrontPage extends AppCompatActivity {

    private TextView mTextMessage;
    public String authToken = "Sample Auth Token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
    }

    public void listAllItems(View v){

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(v.getContext());
        String url ="http://35.237.184.186:80/list";
        //String url ="http://localhost:8080/list";
        // Request a string response from the provided URL.
        final View view = v;
        try {
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, get(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.d("Response", response.toString());
                    try {

                        LinearLayout list = (LinearLayout) findViewById(R.id.itemButtonList);
                        list.removeAllViews();
                        JSONArray array = response.getJSONArray("items");
                        for (int i = 0; i < array.length(); i++){
                            JSONObject item = array.getJSONObject(i);

                            final String itemName = item.getString("name");
                            final String itemDescription = item.getString("description");
                            final int itemQuantity = item.getInt("quantity");
                            final String image64 = item.getString("image");

                            Button itemButton = new Button(view.getContext());
                            itemButton.setText(itemQuantity + " : " +itemName);
                            itemButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                            itemButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(FrontPage.this,Item.class);
                                    intent.putExtra("iname",itemName);
                                    intent.putExtra("idescription",itemDescription);
                                    intent.putExtra("iquantity",itemQuantity);
                                    intent.putExtra("iimage",image64);
                                    intent.putExtra("auth_token", authToken);
                                    startActivity(intent);
                                }
                            });

                            list.addView(itemButton);

                        }


                    }catch (Exception error){
                        Log.d("Response Error", error.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Response Error", error.toString());
                    dbNotConnectedErrorMessage(view);
                }
            });
            queue.add(stringRequest);
        }catch (Error error){
            dbNotConnectedErrorMessage(v);
        }

    }

    public void addNewItem(View v){
        Intent intent = new Intent(FrontPage.this,AddNewItem.class);
        intent.putExtra("auth_token", authToken);
        startActivity(intent);
    }

    private JSONObject get(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth_token", authToken);
        }catch (Exception e){
            Log.d("JSON Error",e.toString());
        }
        return obj;
    }

    void showItem(View v){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        String url ="http://35.237.184.186:80/item";


        String name = ((EditText) findViewById(R.id.itemName)).getText().toString();
        int quantity = Integer.parseInt(((EditText) findViewById(R.id.itemQuantity)).getText().toString());

        final View view = v;
        try {
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, post(name, quantity), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //TODO Change View
                    Log.d("Response", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Response Error", error.toString());
                    dbNotConnectedErrorMessage(view);
                }
            });
            queue.add(stringRequest);
        }catch (Error error){
            dbNotConnectedErrorMessage(v);
        }
    }

    void receiveItem(View v){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        String url ="http://35.237.184.186:80/item";


        String name = ((EditText) findViewById(R.id.itemName)).getText().toString();
        String description = ((EditText) findViewById(R.id.itemDescription)).getText().toString();
        int quantity = Integer.parseInt(((EditText) findViewById(R.id.itemQuantity)).getText().toString());

        final View view = v;
        try {
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.PUT, url, put(name, description, quantity), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //TODO Notify User
                    Log.d("Response", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Response Error", error.toString());
                    dbNotConnectedErrorMessage(view);
                }
            });
                    queue.add(stringRequest);
        }catch (Error error){
            dbNotConnectedErrorMessage(v);
        }
    }

    private JSONObject post(String name, int adjustment){
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("quantity", adjustment);
            obj.put("auth_token", authToken);
        }catch (Exception e){
            Log.d("JSON Error",e.toString());
        }
        return obj;
    }

    private JSONObject put(String name, String description, int quantity){
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("description", description);
            obj.put("quantity", quantity);
            obj.put("auth_token", authToken);
        }catch (Exception e){
            Log.d("JSON Error",e.toString());
        }
        return obj;
    }

    public void dbNotConnectedErrorMessage(View view){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_windows, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
