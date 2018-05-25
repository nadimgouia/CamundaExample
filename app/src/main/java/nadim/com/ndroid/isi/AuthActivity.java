package nadim.com.ndroid.isi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ndroid.CoolButton;
import com.ndroid.CoolEditText;
import com.ndroid.nadim.sahel.CoolToast;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

public class AuthActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    CoolButton btnConnect;
    CoolEditText editEmail, editMdp;
    CheckBox checkRemember;

    CoolToast coolToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        sharedPreferences = getSharedPreferences("authentification", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Boolean isConnected = sharedPreferences.getBoolean("is_connected", false);

        if (isConnected) {
            startActivity(new Intent(AuthActivity.this, StudentHomeActivity.class));
        }

        btnConnect = findViewById(R.id.btnConnect);
        editEmail = findViewById(R.id.editEmail);
        editMdp = findViewById(R.id.editMdp);
        checkRemember = findViewById(R.id.checkRemember);

        coolToast = new CoolToast(this);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editEmail.getText().toString().trim();
                String mdp = editMdp.getText().toString().trim();

                if (email.isEmpty() || mdp.isEmpty()) {

                    coolToast.make("Eviter les champs vide !", CoolToast.DANGER, CoolToast.SHORT);

                } else {

                    if(isNetworkAvailable()){
                        checkLogin(email, mdp);
                    }else {
                        coolToast.make("Verifier votre connexion internet", CoolToast.INFO, CoolToast.SHORT);
                    }
                    //startActivity(new Intent(AuthActivity.this, StudentHomeActivity.class));

                }

            }
        });

    }

    private void checkLogin(final String email, final String mdp) {

        OkHttpClient client = new OkHttpClient();


        String url = "http://bps.isiforge.tn:8080/engine-rest/process-definition?latest=true";

        String credentials = email + ":" + mdp;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .header("Content-Type", "application/json")
                .header("Authorization", auth)
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                if (!response.isSuccessful()) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            coolToast.make("Verifier vos informations", CoolToast.INFO, CoolToast.SHORT);

                        }
                    });

                } else {
                    // do something wih the result

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //reset inputs...
                            editEmail.setText("");
                            editMdp.setText("");

                            Intent intent = new Intent(AuthActivity.this, StudentHomeActivity.class);
                            intent.putExtra("login", email);
                            intent.putExtra("mdp", mdp);
                            intent.putExtra("remember", checkRemember.isChecked());
                            startActivity(intent);
                            finish();

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
