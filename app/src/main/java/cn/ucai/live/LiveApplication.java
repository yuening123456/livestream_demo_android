package cn.ucai.live;

import android.app.Application;
import android.content.Intent;
import cn.ucai.live.ui.activity.MainActivity;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.ucloud.ulive.UStreamingContext;

/**
 * Created by wei on 2016/5/27.
 */
public class LiveApplication extends Application{

  private static LiveApplication instance;


  @Override public void onCreate() {
    super.onCreate();
    instance = this;


    initChatSdk();

    //UEasyStreaming.initStreaming("publish3-key");

    UStreamingContext.init(getApplicationContext(), "publish3-key");
  }

  public static LiveApplication getInstance(){
    return instance;
  }

  private void initChatSdk(){
    //EMOptions options = new EMOptions();
    //options.enableDNSConfig(false);
    //options.setRestServer("103.241.230.122:31080");
    //options.setIMServer("103.241.230.122");
    //options.setImPort(31097);

    EaseUI.getInstance().init(this, null);
    EMClient.getInstance().setDebugMode(true);

    EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
      @Override public void onConnected() {

      }

      @Override public void onDisconnected(int errorCode) {
        if(errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE)
        {
          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.putExtra("conflict", true);
          startActivity(intent);
        }
      }
    });
  }

}
