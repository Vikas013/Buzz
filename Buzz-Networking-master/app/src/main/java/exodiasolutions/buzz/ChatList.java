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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import exodiasolutions.buzz.Modal.ChatListData;

public class ChatList extends AppCompatActivity {
    CustomAdapter3 adapter;
    ListView likes_list;
    String username;
    ArrayList<ChatListData> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        likes_list = (ListView) findViewById(R.id.comments_list);
        adapter = new CustomAdapter3(ChatList.this,arrayList);
        likes_list.setAdapter(adapter);
        likes_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatListData a= arrayList.get(i);
                UserDetails.chatWith = a.getUsername();
                UserDetails.username = username;

                startActivity(new Intent(ChatList.this,Chat.class));

            }

        });
        String id = getIntent().getStringExtra("id");
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        final MyHttpClient myHttpClient = new MyHttpClient(this,"https://pro-jainvikas013.c9users.io/php/getchatlist.php",new String[]{"username",username});
        myHttpClient.execute();
        myHttpClient.callback = new MyCallback() {
            @Override
            public void callbackCall() {

                try {
                    JSONArray jsonArray = new JSONArray(myHttpClient.result);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        if(!obj.getString("username").equalsIgnoreCase(username))
                        arrayList.add(new ChatListData(obj.getString("name"),obj.getString("username"),obj.getString("photo")));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //  Toast.makeText(ChatList.this, ""+myHttpClient.result, Toast.LENGTH_SHORT).show();
            }
        };

    }
    class CustomAdapter3 extends ArrayAdapter<ChatListData> {
        Context c;

        public CustomAdapter3(Context context, ArrayList<ChatListData> arrayList) {
            super(context, R.layout.commentcard, arrayList);
            this.c = context;
        }


        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            LayoutInflater li = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.likescard, parent, false);

            ChatListData s = getItem(pos);
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
            Uri imageUri = Uri.parse("https://pro-jainvikas013.c9users.io/php/img/"+s.getPhoto());
            Fresco.getImagePipeline().evictFromCache(imageUri);
            image.setImageURI(imageUri);


            // checkBox.setTag(s);
            return convertView;

        }
    }
}
