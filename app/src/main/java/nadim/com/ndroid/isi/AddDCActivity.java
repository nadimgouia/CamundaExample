package nadim.com.ndroid.isi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ndroid.CoolButton;
import com.ndroid.CoolEditText;
import com.ndroid.nadim.sahel.CoolToast;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class AddDCActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    OkHttpClient client;
    String credentials, auth;
    public  String FORM_URL = "http://bps.isiforge.tn:8080/engine-rest/process-definition/";
    public   String SUBMIT_FORM_URL = "http://bps.isiforge.tn:8080/engine-rest/process-definition/key/";
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    String id, key;

    LinearLayout layoutParent;

    CoolButton btnConfirm;
    CoolToast coolToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dc);

        sharedPreferences = getSharedPreferences("authentification", MODE_PRIVATE);

        client = new OkHttpClient();
        credentials = sharedPreferences.getString("login","")+":"+sharedPreferences.getString("mdp","");
        auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        key = getIntent().getStringExtra("key");
        id = getIntent().getStringExtra("id");

        FORM_URL = FORM_URL+id+"/form-variables";
        SUBMIT_FORM_URL = SUBMIT_FORM_URL+key+"/submit-form";

        layoutParent = findViewById(R.id.layoutParent);
        coolToast = new CoolToast(this);

        if(isNetworkAvailable()){
            callApiWithOkHttp();
        }else {
            coolToast.make("Verifier votre connexion internet", CoolToast.INFO, CoolToast.SHORT);
        }


    }


    private void callApiWithOkHttp() {


        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .header("Content-Type", "application/json")
                .header("Authorization", auth)
                .url(FORM_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // do something wih the result
                    String result = response.body().string();
                    try {
                        final JSONObject jsonObject = new JSONObject(result);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(isNetworkAvailable()){
                                        makeForm(jsonObject);
                                    }else {
                                        coolToast.make("Verifier votre connexion internet", CoolToast.INFO, CoolToast.SHORT);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    private void makeForm(final JSONObject result) throws JSONException {


        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180);
        LinearLayout.LayoutParams layoutParamsTv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (Iterator<String> iter = result.keys(); iter.hasNext(); ) {
            String key = iter.next();
            JSONObject jo = result.getJSONObject(key);

            TextView textView = new TextView(this);
            textView.setText(key + " :");
            textView.setTextSize(16);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setAllCaps(true);
            layoutParamsTv.setMargins(0, 0, 0, 20);
            textView.setLayoutParams(layoutParamsTv);

            layoutParent.addView(textView);

            if (key.equals("niveau")) {
                Spinner spinner = new Spinner(this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.niveau));
                spinner.setAdapter(adapter);
                layoutParams.setMargins(0, 0, 0, 50);
                spinner.setLayoutParams(layoutParams);
                spinner.setTag(key);
                layoutParent.addView(spinner);
            } else {
                CoolEditText edit1 = new CoolEditText(this, null);
                edit1.setMaxLines(1);
                edit1.setTag(key);
                edit1.setHint(jo.getString("value") + " ...");
                edit1.setTextSize(14);
                edit1.setBorderStroke(0);
                edit1.setBorderColor(Color.parseColor("#000000"));
                edit1.setBorderStroke(1);
                layoutParams.setMargins(0, 0, 0, 50);
                edit1.setLayoutParams(layoutParams);

                layoutParent.addView(edit1);
            }
        }

        btnConfirm = new CoolButton(this, null);
        btnConfirm.setLayoutParams(layoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnConfirm.setBackgroundColor(getColor(R.color.colorPrimary));
        } else {
            btnConfirm.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        btnConfirm.setText("Envoyer");
        btnConfirm.setTextColor(Color.parseColor("#ffffff"));

        layoutParent.addView(btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean canSend = true;

                for (int i = 0; i < layoutParent.getChildCount(); i++) {
                    View child = layoutParent.getChildAt(i);

                    if (child instanceof CoolEditText) {
                        try {
                            // test if there is an empty field ...
                            // get the value
                            String value = ((CoolEditText) child).getText().toString();
                            if (value.trim().isEmpty()) {
                                canSend = false;
                                ((CoolEditText) child).setBorderStroke(4);
                                ((CoolEditText) child).setBorderColor(Color.parseColor("#FF0000"));
                            } else {

                                JSONObject jo = result.getJSONObject(child.getTag().toString());
                                jo.put("value", ((CoolEditText) child).getText().toString());

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (child instanceof Spinner) {
                        try {

                            JSONObject jo = result.getJSONObject(child.getTag().toString());
                            jo.put("value", ((Spinner) child).getSelectedItem().toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                if (canSend) {
                    if(isNetworkAvailable()){
                        submitForm(result);
                        cleanEditBorder();
                    }else {
                        coolToast.make("Verifier votre connexion internet", CoolToast.INFO, CoolToast.SHORT);
                    }

                }
                else
                    coolToast.make("Eviter les champs vide !", CoolToast.DANGER, CoolToast.SHORT);

            }
        });
    }

    public void cleanEditBorder() {
        for (int i = 0; i < layoutParent.getChildCount(); i++) {
            View child = layoutParent.getChildAt(i);

            if (child instanceof CoolEditText) {

                ((CoolEditText) child).setBorderStroke(1);
                ((CoolEditText) child).setBorderColor(Color.parseColor("#000000"));

            }
        }
    }

    private void submitForm(JSONObject result) {

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("variables", result);
            postdata.put("businessKey", "myBusinessKey");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d("before submit", postdata.toString());

        RequestBody body = RequestBody.create(MEDIA_TYPE,
                postdata.toString());


        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(SUBMIT_FORM_URL)
                .post(body)
                .header("Content-Type", "application/json")
                .header("Authorization", auth)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // do something wih the result
                    String result = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            coolToast.make("Demande Envoyée ", CoolToast.SUCCESS, CoolToast.SHORT);

                        }
                    });

                }

            }
        });


    }



    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


}
