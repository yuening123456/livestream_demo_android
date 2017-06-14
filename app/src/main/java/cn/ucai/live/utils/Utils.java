package cn.ucai.live.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import cn.ucai.live.LiveApplication;

/**
 * Created by wei on 2016/6/2.
 */
public class Utils {
    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) LiveApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) LiveApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }


    public static void showToast(final Activity activity, final String toastContent){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, toastContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showLongToast(final Activity activity, final String toastContent){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, toastContent, Toast.LENGTH_LONG).show();
            }
        });
    }
}
