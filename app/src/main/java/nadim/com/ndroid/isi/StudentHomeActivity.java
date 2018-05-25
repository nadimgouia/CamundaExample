package nadim.com.ndroid.isi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ndroid.nadim.sahel.CoolToast;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;

public class StudentHomeActivity extends AppCompatActivity implements View.OnClickListener {


    LinearLayout slideImage;
    ListView listProcess;
    CoolToast coolToast;

    ArrayList<ProcessModel> values;
    OkHttpClient client;
    String credentials, auth;
    public static final String FORM_URL = "http://bps.isiforge.tn:8080/engine-rest/process-definition?latest=true";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageView slideLeft, slideRight;
    TextView slideText;

    int[] images;
    String[] imagesText;
    int slideIndex = 0;
    ProcessAdapter processAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        values = new ArrayList<>();
        coolToast = new CoolToast(this);

        images = new int[]{R.drawable.slide_img1, R.drawable.slide_img2, R.drawable.slide_img3};
        imagesText = new String[]{"#CondingCamp - ISI GOOGLE CLUB", "Conference - ISI ARIANA", "Event - IEE ISI"};

        listProcess = findViewById(R.id.listProcess);
        slideImage = findViewById(R.id.slideImage);
        slideLeft = findViewById(R.id.slideLeft);
        slideRight = findViewById(R.id.slideRight);
        slideText = findViewById(R.id.slideText);

        slideLeft.setOnClickListener(this);
        slideRight.setOnClickListener(this);

        processAdapter = new ProcessAdapter(this, R.layout.item_process, values);
        listProcess.setAdapter(processAdapter);

        sharedPreferences = getSharedPreferences("authentification", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (getIntent() != null) {
            String login = getIntent().getStringExtra("login");
            String mdp = getIntent().getStringExtra("mdp");
            editor.putString("login", login);
            editor.putString("mdp", mdp);

            // check if user whant to remember ...
            if (getIntent().getBooleanExtra("remember", false)) {

                editor.putBoolean("is_connected", true);

            }
        }

        editor.commit();

        client = new OkHttpClient();
        credentials = sharedPreferences.getString("login", "") + ":" + sharedPreferences.getString("mdp", "");
        Log.d("credentials",credentials);

        auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);


        if (isNetworkAvailable()) {
            callApiWithOkHttp();
        } else {
            coolToast.make("Verifier votre connexion internet", CoolToast.INFO, CoolToast.SHORT);
        }


        listProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ProcessModel processModel = (ProcessModel) parent.getItemAtPosition(position);
                Log.d("key", processModel.getKey());
                Log.d("id", processModel.getId());
                Toast.makeText(StudentHomeActivity.this, processModel.getKey(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(StudentHomeActivity.this, AddDCActivity.class);
                intent.putExtra("key", processModel.getKey());
                intent.putExtra("id", processModel.getId());
                startActivity(intent);

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemLogout) {
            // logout ...
            showQuitDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showQuitDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("voulez vous vraiment déconnecter ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                editor.clear();
                editor.commit();

                values.clear();
                processAdapter.clear();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.slideLeft:
                Log.d("index", slideIndex + "");
                if (slideIndex != 0) {
                    slideIndex--;
                    slideImage.setBackgroundResource(images[slideIndex]);
                    slideText.setText(imagesText[slideIndex]);
                }
                break;

            case R.id.slideRight:
                Log.d("index", slideIndex + "");
                if (slideIndex != 2) {
                    slideIndex++;
                    slideImage.setBackgroundResource(images[slideIndex]);
                    slideText.setText(imagesText[slideIndex]);
                }
                break;
        }

    }


    private void callApiWithOkHttp() {


        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .cacheControl(new CacheControl.Builder().noCache().build())
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
                    final String result = response.body().string();

                    final Gson gson = new Gson();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProcessModel[] processModels = gson.fromJson(result, ProcessModel[].class);
                            for (ProcessModel p : processModels) {
                                values.add(p);
                                processAdapter.notifyDataSetChanged();
                            }
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
