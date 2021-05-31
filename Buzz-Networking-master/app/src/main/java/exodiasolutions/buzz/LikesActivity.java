package exodiasolutions.buzz;

import android.content.Context;
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

import exodiasolutions.buzz.Custom.TV_Muskan;
import exodiasolutions.buzz.Custom.TV_normaltext;
import exodiasolutions.buzz.Modal.CommentUser;
import exodiasolutions.buzz.Modal.LikesData;

public class LikesActivity extends AppCompatActivity {
    CustomAdapter3 adapter;
    ListView likes_list;
    ArrayList<LikesData> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        likes_list = (ListView) findViewById(R.id.comments_list);
        adapter = new CustomAdapter3(LikesActivity.this,arrayList);
        likes_list.setAdapter(adapter);
        String id = getIntent().getStringExtra("id");

        final MyHttpClient myHttpClient = new MyHttpClient(this,"https://pro-jainvikas013.c9users.io/php/getlikes.php",new String[]{"feed_id",id});
        myHttpClient.execute();
        myHttpClient.callback = new MyCallback() {
            @Override
            public void callbackCall() {

                try {
                    JSONArray jsonArray = new JSONArray(myHttpClient.result);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        arrayList.add(new LikesData(obj.getString("name"),obj.getString("photo")));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


              //  Toast.makeText(LikesActivity.this, ""+myHttpClient.result, Toast.LENGTH_SHORT).show();
            }
        };

    }
    class CustomAdapter3 extends ArrayAdapter<LikesData> {
        Context c;

        public CustomAdapter3(Context context, ArrayList<LikesData> arrayList) {
            super(context, R.layout.commentcard, arrayList);
            this.c = context;
        }


        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            LayoutInflater li = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.likescard, parent, false);

            LikesData s = getItem(pos);
            TV_Muskan name = convertView.findViewById(R.id.name);
            SimpleDraweeView image = convertView.findViewById(R.id.profile_image);

            name.setText(s.getName());


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
}
