package com.cmpt436.nick.cmpt436posclient;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;



public class Item extends AppCompatActivity {
    private TextView mTextMessage;

    String name = "NULL BUNDLE";
    String description = "NULL BUNDLE";
    int quantity = 0;
    String authToken = "";
    String image = null;
    Bitmap decoded_image = null;

    public Item(){

    }

    public Item(String iname, String idescription, int iquantity) {
        name = iname;
        description = idescription;
        quantity = iquantity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        if(getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            String iname = bundle.getString("iname");
            String idescription = bundle.getString("idescription");
            int iquantity = bundle.getInt("iquantity");
            String iauthToken = bundle.getString("auth_token");
            String iimage = bundle.getString("iimage");
            name = iname;
            description = idescription;
            quantity = iquantity;
            authToken = iauthToken;
            image = iimage;
        }

        ((TextView) findViewById(R.id.itemName)).setText(name);
        ((TextView) findViewById(R.id.itemQuantity)).setText(String.valueOf(quantity));
        ((TextView) findViewById(R.id.itemDescription)).setText(description);


        if(image != null) {
            Log.d("IMAGE", "FOUND!");
            Log.d("IMAGE",image);
            Bitmap decoded = StringToBitMap(image);
            ImageView item_pic = ((ImageView) findViewById(R.id.imageView));
            item_pic.setImageBitmap(decoded);
        }else{
            Log.d("IMAGE", "NOT FOUND!");
        }

    }


    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void submit(View view) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        String url = "http://35.237.184.186:80/itemQty";
        //String url = "http://localhost:80/itemQty";

        int adjustment = Integer.parseInt(((TextView) findViewById(R.id.adjustment)).getText().toString());
        int itemQuantity = Integer.parseInt(((TextView) findViewById(R.id.itemQuantity)).getText().toString());
        RadioButton sold = (RadioButton) findViewById(R.id.SoldButton);

        int tobeset = itemQuantity;
        if(sold.isChecked()){
            Log.d("SELL UNREASONABLE", "Sold Set");

            if(adjustment > itemQuantity){
                Log.d("SELL UNREASONABLE", "Too many items sold");
                return;
            }
            adjustment = -1* adjustment;
        }
        tobeset = tobeset + adjustment;

        final View v = view;
        try {
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, post(name, tobeset), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dbOK(v);
                    Log.d("Response", response.toString());
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Response Error", error.toString());
                    dbNotConnectedErrorMessage(v);
                }
            });

            queue.add(stringRequest);
        } catch (Error error) {
            dbNotConnectedErrorMessage(view);
        }
    }


    public void cancel(View view){

        startActivity(new Intent(Item.this,FrontPage.class));
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
    public void dbNotConnectedErrorMessage(View v){
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
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void dbOK(View v){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_ok, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                cancel(v);
                return true;
            }
        });
    }



}
