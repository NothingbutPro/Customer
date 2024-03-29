package com.pyckup.app.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pyckup.app.Helper.ConnectionHelper;
import com.pyckup.app.Helper.CustomDialog;
import com.pyckup.app.Helper.SharedHelper;
import com.pyckup.app.Helper.URLHelper;
import com.pyckup.app.R;
import com.pyckup.app.XuberApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    String TAG = "ChangePasswordActivity";
    public Context context = ChangePassword.this;
    public Activity activity = ChangePassword.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Button changePasswordBtn;
    ImageView backArrow;
    EditText current_password, new_password, confirm_new_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        findViewByIdandInitialization();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current_password_value = current_password.getText().toString();
                String new_password_value = new_password.getText().toString();
                String confirm_password_value = confirm_new_password.getText().toString();
                if(current_password_value == null || current_password_value.equalsIgnoreCase("")){
                    displayMessage(getString(R.string.please_enter_current_pass));
                }else if(new_password_value == null || new_password_value.equalsIgnoreCase("")){
                    displayMessage(getString(R.string.please_enter_new_pass));
                }else if(confirm_password_value == null || confirm_password_value.equalsIgnoreCase("")){
                    displayMessage(getString(R.string.please_enter_confirm_pass));
                }else if(!new_password_value.equals(confirm_password_value)){
                    displayMessage(getString(R.string.different_passwords));
                }else{
                    changePassword(current_password_value, new_password_value, confirm_password_value);
                }
            }
        });

    }

    public void findViewByIdandInitialization(){
        current_password = (EditText)findViewById(R.id.current_password);
        new_password = (EditText)findViewById(R.id.new_password);
        confirm_new_password = (EditText) findViewById(R.id.confirm_password);
        changePasswordBtn = (Button) findViewById(R.id.changePasswordBtn);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
    }



    private void changePassword(String current_pass, String new_pass, String confirm_new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if(customDialog != null)
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("password", new_pass);
            object.put("password_confirmation", confirm_new_pass);
            object.put("old_password", current_pass);
            Log.e("ChangePasswordAPI",""+object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHANGE_PASSWORD_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
                displayMessage(response.optString("message"));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log.e("MyTest",""+error);
                Log.e("MyTestError",""+error.networkResponse);
                Log.e("MyTestError1",""+response.statusCode);
                if(response != null && response.data != null){
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI",""+errorObj.toString());

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.optString("error"));
                            }catch (Exception e){
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        }else if(response.statusCode == 401){
                                GoToBeginActivity();
                        }else if(response.statusCode == 422){
                            json = XuberApplication.trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(getString(R.string.please_try_again));
                            }
                        }else{
                            displayMessage(getString(R.string.please_try_again));
                        }

                    }catch (Exception e){
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void GoToBeginActivity(){
        SharedHelper.putKey(activity,"loggedIn",getString(R.string.False));
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString){
        Log.e("displayMessage",""+toastString);
        Snackbar.make(getCurrentFocus(),toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
