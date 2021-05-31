package exodiasolutions.buzz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import exodiasolutions.buzz.Custom.TV_heading;
import exodiasolutions.buzz.Custom.TV_normaltext;
import exodiasolutions.buzz.Loader.CircleRefreshLayout;
import exodiasolutions.buzz.Modal.FeedsData;
import exodiasolutions.buzz.Util.EndlessRecyclerViewScrollListener;
import exodiasolutions.buzz.Util.InternetChecker;
import exodiasolutions.buzz.Util.RecyclerItemClickListener;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class HomeActivity extends AppCompatActivity {
    TV_heading name;
    SimpleDraweeView draweeView;
    int num = 0;
    ProgressBar pb;
    static String username;
// this is sunny
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    TextView liked,addcomment;
    RecyclerView recyclerView;
    RecyclerCustomAdapter adapter;
    ArrayList<FeedsData> arrayList = new ArrayList<>();
    private CircleRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_home);
        mRefreshLayout =  findViewById(R.id.refresh_layout);
        getSupportActionBar().hide();
        name = findViewById(R.id.name);
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String name_str =  sharedPreferences.getString("name", null);
       // Toast.makeText(this, ""+name_str, Toast.LENGTH_SHORT).show();
        pb = findViewById(R.id.progressbar);
        username = sharedPreferences.getString("username", null);
        name.setText(name_str);
        liked = findViewById(R.id.liked);
        addcomment = findViewById(R.id.addcomments);
        recyclerView = (RecyclerView) findViewById(R.id.report_recycler);
        adapter=new RecyclerCustomAdapter(HomeActivity.this,arrayList);

        recyclerView.setAdapter(adapter);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(Report.this,LinearLayoutManager.VERTICAL,false);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
/*
        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(HomeActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                // TODO Handle item click
                /*AttendanceData a  = arrayList.get(position);
                // Toast.makeText(HomeActivity.this, ""+a.getId(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(HomeActivity.this, ViewFullReport.class);
                i.putExtra("batch",batch);
                i.putExtra("id",a.getId());
                startActivity(i);
                if (view.getId() == liked.getId()){
                    Toast.makeText(view.getContext(), "LIKE", Toast.LENGTH_SHORT).show();
                } else if(view.getId() == addcomment.getId()) {
                    Toast.makeText(view.getContext(), "COMMENT", Toast.LENGTH_SHORT).show();
                }
                /*
                FeedsData a = arrayList.get(position);
                //Toast.makeText(HomeActivity.this, ""+a.getId(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(HomeActivity.this,Comments.class);
                Toast.makeText(HomeActivity.this, ""+a.getLiked(), Toast.LENGTH_SHORT).show();
                i.putExtra("id",a.getId());
                startActivity(i);



            }

        }));*/

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                if(InternetChecker.isNetworkAvailable(HomeActivity.this)){
                    num+=20;
                    getdata();
                }
                else{
                    Toast.makeText(HomeActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mRefreshLayout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
                    @Override
                    public void refreshing() {
                        arrayList.clear();
                        num=0;
                        getdata();
                    }

                    @Override
                    public void completeRefresh() {
                        // do something when refresh complete
                    }
                });


        draweeView = (SimpleDraweeView) findViewById(R.id.profile_image);
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
        Uri imageUri = Uri.parse("https://pro-jainvikas013.c9users.io/php/img/"+sharedPreferences.getString("photo", null));
        Fresco.getImagePipeline().evictFromCache(imageUri);
        draweeView.setImageURI(imageUri);
       getdata();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

        public void post(View v){
        startActivity(new Intent(HomeActivity.this,PostFeeds.class));
        }

        public void getdata(){


            pb.setVisibility(View.VISIBLE);
            final MyHttpClient myHttpClient = new MyHttpClient(HomeActivity.this,"https://pro-jainvikas013.c9users.io/php/getfeeds.php",new String[]{"num",num+"","username",username});
            myHttpClient.execute();
            myHttpClient.callback = new MyCallback() {
                @Override
                public void callbackCall() {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(myHttpClient.getStatus()!= AsyncTask.Status.RUNNING)
                            mRefreshLayout.finishRefreshing();

                        }
                    }, 1000);
                    mRefreshLayout.finishRefreshing();
                   // Toast.makeText(HomeActivity.this, "1", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.INVISIBLE);
                        if(JSONChecker.isJSONValid(myHttpClient.result)){
                            try {
                                JSONArray jsonArray = new JSONArray(myHttpClient.result);
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                FeedsData feedsData = new FeedsData(obj.getString("id"),obj.getString("name"),obj.getString("text"),obj.getString("image"),obj.getString("datetime"),obj.getString("likes"),obj.getString("comments"),obj.getString("photo"),obj.getString("location"),obj.getString("liked"));
                                arrayList.add(feedsData);
                                }
                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(HomeActivity.this, "No Feeds!", Toast.LENGTH_SHORT).show();
                        }
                }
            };




        }
    public class RecyclerCustomAdapter extends
            RecyclerView.Adapter<RecyclerCustomAdapter.ViewHolder> {

        Context mContext;
        ArrayList<FeedsData> mArrayList;
    public RecyclerCustomAdapter(Context context, ArrayList<FeedsData> marrayList) {
        mContext = context;
        mArrayList = marrayList;
    }

    //easy access to context items objects in recyclerView
    private Context getContext() {
        return mContext;
    }

    @Override
    public RecyclerCustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.feeds_card, parent, false);

        // Return a new holder instance
        RecyclerCustomAdapter.ViewHolder viewHolder = new RecyclerCustomAdapter.ViewHolder(contactView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerCustomAdapter.ViewHolder viewHolder, int position) {

        // Get the data model based on position
        FeedsData a = mArrayList.get(position);

        // Set item views based on your views and data model

       TextView date = viewHolder.date;
       TextView likes = viewHolder.likes;
       TextView comments = viewHolder.comments;
       TV_normaltext name = viewHolder.name;
       TV_normaltext status = viewHolder.status;
       SimpleDraweeView profile_img = viewHolder.profile_img;
       SimpleDraweeView status_img = viewHolder.status_img;
        TextView liked = viewHolder.liked;
        TextView addcomment = viewHolder.addcomment;
       date.setText(a.getDatetime()+" at "+a.getLocation());

       likes.setText(a.getLikes()+" Likes");
       if(a.getLiked().equalsIgnoreCase("1")){
           liked.setText("Liked");
           liked.setTag("liked");
           //liked.setTextColor(Color.parseColor("#FFFF4E45"));
       }
       else{
           liked.setTag("like");
           liked.setText("Like");
       }


       comments.setText(a.getComments()+" Comments");
       name.setText(a.getName());
       status.setText(a.getText());

       Drawable myIcon = getContext().getResources().getDrawable( R.drawable.place);
        Drawable myIcon2 = getContext().getResources().getDrawable( R.drawable.user);
        myIcon.setAlpha(10);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getContext().getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(myIcon2)
                .build();
        profile_img.setHierarchy(hierarchy);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f).setBorder(getContext().getResources().getColor(R.color.border), 5.0f);;
        roundingParams.setRoundAsCircle(true);
        profile_img.getHierarchy().setRoundingParams(roundingParams);
        Uri imageUri = Uri.parse("https://pro-jainvikas013.c9users.io/php/img/"+a.getProfile_img());
        Fresco.getImagePipeline().evictFromCache(imageUri);
        profile_img.setImageURI(imageUri);


        if(a.getImage().equalsIgnoreCase("")){
            if(status_img!=null&& ((ViewGroup)status_img.getParent())!=null){
            ((ViewGroup)status_img.getParent()).removeView(status_img);}
        }
        else{
        GenericDraweeHierarchyBuilder builder2 =
                new GenericDraweeHierarchyBuilder(getContext().getResources());
        GenericDraweeHierarchy hierarchy2 = builder2
                .setFadeDuration(300)
                .setPlaceholderImage(myIcon)
                .build();
        status_img.setHierarchy(hierarchy2);
        Uri imageUri2 = Uri.parse("https://pro-jainvikas013.c9users.io/php/img/"+a.getImage());
        Fresco.getImagePipeline().evictFromCache(imageUri2);
       status_img.setImageURI(imageUri2);}


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // public TextView stripe1, stripe2;
        public TextView date,likes,comments,liked,addcomment;
        public TV_normaltext name,status;
        LinearLayout like_ll,comment_ll;
        SimpleDraweeView profile_img,status_img;
        public ViewHolder(View itemView) {
            super(itemView);
            liked = itemView.findViewById(R.id.liked);
            addcomment = itemView.findViewById(R.id.addcomments);
            date = itemView.findViewById(R.id.date);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            profile_img = (SimpleDraweeView) itemView.findViewById(R.id.profile_image);
            status_img = (SimpleDraweeView) itemView.findViewById(R.id.post_image);
            like_ll = itemView.findViewById(R.id.like_ll);
            comment_ll = itemView.findViewById(R.id.comment_ll);
            like_ll.setOnClickListener(this);
            comment_ll.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            final FeedsData a = mArrayList.get(getAdapterPosition());
            if (view.getId() == like_ll.getId()){

                if(liked.getText().toString().equalsIgnoreCase("Like")) {
                    //Toast.makeText(view.getContext(), "LIKE " + a.getId(), Toast.LENGTH_SHORT).show();
                    final MyHttpClient myHttpClient = new MyHttpClient(mContext, "https://pro-jainvikas013.c9users.io/php/likepost.php", new String[]{"username", HomeActivity.username, "feed_id", a.getId()});
                    myHttpClient.execute();
                    myHttpClient.callback = new MyCallback() {
                        @Override
                        public void callbackCall() {
                            //Toast.makeText(mContext, "" + myHttpClient.result, Toast.LENGTH_SHORT).show();
                            if (myHttpClient.result.equalsIgnoreCase("1")) {
                                a.setLikes((Integer.parseInt(a.getLikes()) + 1) + "");
                                likes.setText(a.getLikes() + " Likes");
                                liked.setText("Liked");
                                //liked.setTextColor(Color.parseColor("#FFFF4E45"));
                            } else {

                            }
                        }
                    };
                }
                else if(liked.getText().toString().equalsIgnoreCase("Liked")){
                    //Toast.makeText(view.getContext(), "UNLIKE " + a.getId(), Toast.LENGTH_SHORT).show();
                    final MyHttpClient myHttpClient = new MyHttpClient(mContext, "https://pro-jainvikas013.c9users.io/php/unlikepost.php", new String[]{"username", HomeActivity.username, "feed_id", a.getId()});
                    myHttpClient.execute();
                    myHttpClient.callback = new MyCallback() {
                        @Override
                        public void callbackCall() {
                           // Toast.makeText(mContext, "" + myHttpClient.result, Toast.LENGTH_SHORT).show();
                            if (myHttpClient.result.equalsIgnoreCase("1")) {
                                a.setLikes((Integer.parseInt(a.getLikes()) - 1) + "");
                                likes.setText(a.getLikes() + " Likes");
                                liked.setText("Like");
                                //liked.setTextColor(Color.parseColor("#FFFF4E45"));
                            } else {

                            }
                        }
                    };
                }


            } else if(view.getId() == comment_ll.getId()) {

                Intent i = new Intent(mContext,Comments.class);
               // Toast.makeText(mContext, ""+a.getLiked(), Toast.LENGTH_SHORT).show();
                i.putExtra("id",a.getId());
                startActivity(i);
                //Toast.makeText(view.getContext(), "COMMENT " + a.getId(), Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }

}

public void logout(View v){
    SharedPreferences sharedPreferences;
    sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.clear();
    editor.apply();
    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
    HomeActivity.this.finish();
}

public void ChatList(View v){
    Intent i = new Intent(HomeActivity.this,ChatList.class);
    startActivity(i);
}




}

