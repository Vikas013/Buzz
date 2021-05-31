package exodiasolutions.buzz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                if(sharedPreferences.getString("username", null) != null){
                    Intent i = new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(i);
                }
                SplashActivity.this.finish();





            }
        }, 2000);



    }
}
