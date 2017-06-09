package cn.ucai.live;

import android.app.Application;
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
    LiveHelper.getInstance().init(this);

  }

}
