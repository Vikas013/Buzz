package exodiasolutions.buzz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import exodiasolutions.buzz.Checkers.JSONChecker;
import exodiasolutions.buzz.Custom.CEditText;

public class LoginActivity extends AppCompatActivity {

    CEditText user_et,pass_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_et = findViewById(R.id.user);
        pass_et = findViewById(R.id.pass);
        getSupportActionBar().hide();




    }


    public void login(View v){

        //Toast.makeText(this, ""+user_et.getText().toString()+","+pass_et.getText().toString(), Toast.LENGTH_SHORT).show();
        final MyHttpClient myHttpClient = new MyHttpClient(LoginActivity.this,"https://pro-jainvikas013.c9users.io/php/login.php",new String[]{"username",user_et.getText().toString(),"password",pass_et.getText().toString()});
        myHttpClient.execute();
        myHttpClient.callback = new MyCallback() {
            @Override
            public void callbackCall() {

                if(JSONChecker.isJSONValid(myHttpClient.result)){

                    try {
                        JSONArray jsonArray = new JSONArray(myHttpClient.result);
                        JSONObject obj = jsonArray.getJSONObject(0);
                    SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id",obj.getString("id") );
                    editor.putString("email", obj.getString("email"));
                    editor.putString("phone", obj.getString("phone"));
                    editor.putString("gender", obj.getString("gender"));
                    editor.putString("college", obj.getString("college"));
                    editor.putString("name", obj.getString("name"));
                    editor.putString("photo", obj.getString("photo"));
                    editor.putString("username", obj.getString("username"));
                    

                    editor.apply();
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    LoginActivity.this.finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    
                }
                else if (myHttpClient.result.equalsIgnoreCase("0")){
                    Toast.makeText(LoginActivity.this, "Wrong Username Password", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Error Try Again", Toast.LENGTH_SHORT).show();

                }

                //Toast.makeText(LoginActivity.this, ""+myHttpClient.result, Toast.LENGTH_SHORT).show();
            }
        };

    }

    public void register(View v){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        LoginActivity.this.finish();
    }

}
