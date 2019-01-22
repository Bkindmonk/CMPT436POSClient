package com.cmpt436.nick.cmpt436posclient;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;





import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class AddNewItem extends AppCompatActivity {

    String authToken = null;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Bitmap photo = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_item);

        if(getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            String iauthToken = bundle.getString("auth_token");
            authToken = iauthToken;
        }
    }

    public void submit(View view) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        String url = "http://35.237.184.186:80/item";
        //String url = "http://localhost:80/item";
        String name = ((EditText) findViewById(R.id.itemName)).getText().toString();
        String description = ((EditText) findViewById(R.id.itemDescription)).getText().toString();
        int quantity;

        try {
            quantity = Integer.parseInt(((EditText) findViewById(R.id.itemQuantity)).getText().toString());
        }catch (Exception e){
            return;
        }

        //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //boolean decompressable = this.photo.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        String image = BitMapToString(this.photo);


        //image = Base64.encodeToString(image.getBytes(), Base64.DEFAULT);
        Log.d("IMAGE","Encoded Output");
        Log.d("IMAGE",image);
        //if(decompressable) {
        //    Log.d("IMAGE", "Decompressable");
        //}
        final View v = view;
        try {
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.PUT, url,put(name, description, quantity, image), new Response.Listener<JSONObject>() {
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

        startActivity(new Intent(AddNewItem.this,FrontPage.class));
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

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, outputStream);
        byte [] b=outputStream.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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

    public void picture(View v){
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA},MY_CAMERA_PERMISSION_CODE);
            Log.d("PICTURE","DIDN'T HAVE PERMISSION");

        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
            Log.d("PICTURE","HAD PERMISSION");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            this.photo = photo.copy(photo.getConfig(), true);
            //this.photo.setHeight(250);
            //this.photo.setWidth(250);
            ((ImageButton)findViewById(R.id.imageButton)).setImageBitmap(this.photo);
            Log.d("PICTURE","Taken a picture");
        }
    }

    private JSONObject put(String name, String description, int quantity, String image){
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("description", description);
            obj.put("quantity", quantity);
            obj.put("auth_token", authToken);
            obj.put("image", image);
        }catch (Exception e){
            Log.d("JSON Error",e.toString());
        }
        return obj;
    }
}
