package exodiasolutions.buzz.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


/**
 * Created by Sunny on 03-09-2017.
 */

public class TV_Muskan extends AppCompatTextView {

    public TV_Muskan(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TV_Muskan(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TV_Muskan(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/IndieFlower.ttf");
        setTypeface(tf ,1);

    }

}