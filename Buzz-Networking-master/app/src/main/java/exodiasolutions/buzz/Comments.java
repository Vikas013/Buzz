package exodiasolutions.buzz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import exodiasolutions.buzz.Checkers.JSONChecker;
import exodiasolutions.buzz.Custom.CEditText;
import exodiasolutions.buzz.Custom.TV_Muskan;
import exodiasolutions.buzz.Custom.TV_normaltext;
import exodiasolutions.buzz.Modal.CommentUser;

public class Comments extends AppCompatActivity {
    CEditText comment_et;
    ListView comments_list;
    TextView likes;
    String id,username;
    CustomAdapter3 adapter;
    ArrayList<CommentUser> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        id = getIntent().getStringExtra("id");
        comments_list = (ListView) findViewById(R.id.comments_list);
        likes = findViewById(R.id.likes);

        adapter = new CustomAdapter3(Comments.this,arrayList);
        comments_list.setAdapter(adapter);
        comment_et = findViewById(R.id.comment_et);
        getcomments();


    }
    class CustomAdapter3 extends ArrayAdapter<CommentUser> {
        Context c;

        public CustomAdapter3(Context context, ArrayList<CommentUser> arrayList) {
            super(context, R.layout.commentcard, arrayList);
            this.c = context;
        }


        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            LayoutInflater li = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.commentcard, parent, false);

            CommentUser s = getItem(pos);
            TV_Muskan name = convertView.findViewById(R.id.name);
            TV_normaltext comment = convertView.findViewById(R.id.comment);
            SimpleDraweeView image = convertView.findViewById(R.id.profile_image);
            TextView date = convertView.findViewById(R.id.datetime);
            name.setText(s.getName());
            comment.setText(s.getComment());
            date.setText(s.getDatetime());

            Drawable myIcon2 = getContext().getResources().getDrawable( R.drawable.user);
            myIcon2.setAlpha(10);
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(getContext().getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(300)
                    .setPlaceholderImage(myIcon2)
                    .build();
            image.setHierarchy(hierarchy);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f).setBorder(getContext().getResources().getColor(R.color.border), 5.0f);;
            roundingParams.setRoundAsCircle(true);
            image.getHierarchy().setRoundingParams(roundingParams);
            Uri imageUri = Uri.parse("https://pro-jainvikas013.c9users.io/php/img/"+s.getImage());
            Fresco.getImagePipeline().evictFromCache(imageUri);
            image.setImageURI(imageUri);


            // checkBox.setTag(s);
            return convertView;

        }
    }
    public void postcomment(View v){
        if(comment_et.getText().toString().length()>0){
            final MyHttpClient myHttpClient = new MyHttpClient(Comments.this,"https://pro-jainvikas013.c9users.io/php/postcomment.php",new String[]{"feed_id",id,"username",username,"comment",comment_et.getText().toString()});
            myHttpClient.execute();
            myHttpClient.callback = new MyCallback() {
                @Override
                public void callbackCall() {
                    comment_et.setText("");
                   // Toast.makeText(Comments.this, ""+ myHttpClient.result, Toast.LENGTH_SHORT).show();
                    if(myHttpClient.result.equalsIgnoreCase("1")){
                        getcomments();
                    }
                }
            };
        }
        else{
            comment_et.setError("Enter Comment");
        }
    }

    public void getcomments(){
        final MyHttpClient myHttpClient = new MyHttpClient(Comments.this,"https://pro-jainvikas013.c9users.io/php/getcomments.php",new String[]{"feed_id",id});
        myHttpClient.execute();
        myHttpClient.callback = new MyCallback() {
            @Override
            public void callbackCall() {
                String[] data = myHttpClient.result.split("&&");
                likes.setText(data[1]+" Likes");
                if(JSONChecker.isJSONValid(data[0])){
                    arrayList.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(myHttpClient.result);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            arrayList.add(new CommentUser(obj.getString("name"),obj.getString("photo"),obj.getString("datetime"),obj.getString("comment")));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else if(myHttpClient.result.contains("0&&")){}
                else{
                    Toast.makeText(Comments.this, "Error", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(Comments.this, ""+myHttpClient.result, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void Likes(View v){
        Intent i = new Intent(Comments.this,LikesActivity.class);
        i.putExtra("id",id);
        startActivity(i);
    }

}
