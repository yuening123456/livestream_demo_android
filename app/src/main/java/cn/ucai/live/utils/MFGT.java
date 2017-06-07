package cn.ucai.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.ucai.live.R;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class MFGT  {
    public static void finish(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }
    public static void startActivity(Context context,Intent intent){
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }
    private static void startActivity(Context context,Class clazz){
        context.startActivity(new Intent(context,clazz));
        ((Activity)context).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }

}
