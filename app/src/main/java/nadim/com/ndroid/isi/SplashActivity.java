package nadim.com.ndroid.isi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Thread timer = new Thread() {

            @Override
            public void run() {
                super.run();

                try {
                    sleep(3000);
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        timer.start();
    }
}
