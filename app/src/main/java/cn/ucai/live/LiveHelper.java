package cn.ucai.live;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.util.EMLog;

import cn.ucai.live.ui.activity.MainActivity;


/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class LiveHelper {
    public static final String TAG = "LiveHelper";
    private String username;
    private Context appContext;
    private LiveModel model;
    private static LiveHelper instance = null;

    private LiveHelper() {
    }
    public void init(Context context) {
        model=new LiveModel();
        appContext=context;
        EaseUI.getInstance().init(context, null);
        EMClient.getInstance().setDebugMode(true);

        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override public void onConnected() {

            }

            @Override public void onDisconnected(int error) {

                    EMLog.d("global listener", "onDisconnect" + error);
                    if (error == EMError.USER_REMOVED) {
                        onUserException(LiveConstants.ACCOUNT_REMOVED);
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        onUserException(LiveConstants.ACCOUNT_CONFLICT);
                    } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                        onUserException(LiveConstants.ACCOUNT_FORBIDDEN);
                    }
            }
        });
    }
    public synchronized static LiveHelper getInstance() {
        if (instance == null) {
            instance = new LiveHelper();
        }
        return instance;
    }
    public void setCurrentUserName(String username){
        this.username = username;
        model.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName(){
        if(username == null){
            username = model.getCurrentUsernName();
        }
        return username;
    }
    protected void onUserException(String exception){
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

}
