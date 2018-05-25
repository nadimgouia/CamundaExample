package nadim.com.ndroid.isi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {



    ImageView ivEnseignant,ivEtudiant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ivEnseignant = findViewById(R.id.ivEnseignant);
        ivEtudiant = findViewById(R.id.ivEtudiant);

        ivEnseignant.setOnClickListener(this);
        ivEtudiant.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        if (v == ivEnseignant){
            startActivity(new Intent(HomeActivity.this, AuthActivity.class));
        }
        else if( v == ivEtudiant ){
            startActivity(new Intent(HomeActivity.this, AuthActivity.class));
        }

    }
}
