package exodiasolutions.buzz;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import exodiasolutions.buzz.Custom.CEditText;

public class PostFeeds extends AppCompatActivity {
    int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap = null;
    CEditText status,location;
    String username;
    SimpleDraweeView draweeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_post_feeds);
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        getSupportActionBar().hide();

        username = sharedPreferences.getString("id", null);
        status = findViewById(R.id.status);
        location = findViewById(R.id.location);
        draweeView = findViewById(R.id.profile_image);
        Drawable myIcon = getResources().getDrawable( R.drawable.add);
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
                    .start(PostFeeds.this);


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
    public String getShare(){

        RadioGroup radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);


        // get selected radio button from radioGroup
        int selectedId = radioSexGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
        if(radioSexButton!=null) {
            return radioSexButton.getTag() + "";
        }
        else{
            return "0";
        }


    }

    public void post(View v){

        String encodedImage="";
        if(bitmap!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        if(getShare().equalsIgnoreCase("0")){
            Toast.makeText(this, "Select Privacy", Toast.LENGTH_SHORT).show();
        }
        else{


                String[] data = {"share",getShare(),"text",status.getText().toString(),"image",encodedImage,"username",username,"location",location.getText().toString()+""};



            final MyHttpClient myHttpClient = new MyHttpClient(PostFeeds.this,"https://pro-jainvikas013.c9users.io/php/feeds.php",data);
            myHttpClient.execute();
            myHttpClient.callback = new MyCallback() {
                @Override
                public void callbackCall() {
                    if(myHttpClient.result.equalsIgnoreCase("1")){
                        Toast.makeText(PostFeeds.this, "Posted!", Toast.LENGTH_SHORT).show();
                        // startActivity(new Intent(PostFeeds.this,LoginActivity.class));
                        PostFeeds.this.finish();
                    }
                    else{
                        Toast.makeText(PostFeeds.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                    //  Toast.makeText(PostFeeds.this, ""+myHttpClient.result, Toast.LENGTH_SHORT).show();
                }
            };

        }
        //Toast.makeText(this, ""+getShare(), Toast.LENGTH_SHORT).show();

    }

}
