package exodiasolutions.buzz;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.client.Firebase;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import exodiasolutions.buzz.Custom.CEditText;

public class RegisterActivity extends AppCompatActivity {
    int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap = null;
    String user,pass;
    CEditText username,password,name,college,phone,email;
    SimpleDraweeView draweeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_register);
        Firebase.setAndroidContext(this);
        getSupportActionBar().hide();
        username = findViewById(R.id.user);
        password = findViewById(R.id.pass);
        name = findViewById(R.id.fullname);
        college = findViewById(R.id.college);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);

        draweeView = findViewById(R.id.profile_image);
        Drawable myIcon = getResources().getDrawable( R.drawable.user);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(myIcon)
                .build();
        draweeView.setHierarchy(hierarchy);

        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f).setBorder(getResources().getColor(R.color.border), 5.0f);;
        roundingParams.setRoundAsCircle(true);
        draweeView.getHierarchy().setRoundingParams(roundingParams);
        }


    public void selectImage(View v){

        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            // Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Explain to the add_photo why we need to read the contacts
                    }

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            0);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant that should be quite unique

                    return;
                }
            }

            UCrop.of(uri,Uri.fromFile(new File(getCacheDir(), "lol.jpeg")))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(500,500)
                    .start(RegisterActivity.this);


        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                draweeView.setImageURI(resultUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
            // Log.d(TAG, String.valueOf(bitmap));


        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            final Throwable cropError = UCrop.getError(data);
        }



    }

    public void register(View v){

        String encodedImage="";
        if(bitmap!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

            if(getGender().equalsIgnoreCase("0")){
                Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show();
            }
            else{
                String[] data = {"name",name.getText().toString(),"username",username.getText().toString(),"password",password.getText().toString(),"college",college.getText().toString(),"phone",phone.getText().toString(),"gender",getGender(),"email",email.getText().toString(),"image",encodedImage};


                final MyHttpClient myHttpClient = new MyHttpClient(RegisterActivity.this,"https://pro-jainvikas013.c9users.io/php/register.php",data);
                myHttpClient.execute();
                myHttpClient.callback = new MyCallback() {
                    @Override
                    public void callbackCall() {
                        if(myHttpClient.result.equalsIgnoreCase("1")){

                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                            RegisterActivity.this.finish();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Wrong Username/ Password", Toast.LENGTH_SHORT).show();
                        }

                      //  Toast.makeText(RegisterActivity.this, ""+myHttpClient.result, Toast.LENGTH_SHORT).show();
                    }
                };


            }
            Toast.makeText(this, ""+getGender(), Toast.LENGTH_SHORT).show();
    
    }


    public String getGender(){

        RadioGroup radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);


        // get selected radio button from radioGroup
        int selectedId = radioSexGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
        if(radioSexButton!=null) {
            return radioSexButton.getText() + "";
        }
        else{
            return "0";
        }


    }

}
