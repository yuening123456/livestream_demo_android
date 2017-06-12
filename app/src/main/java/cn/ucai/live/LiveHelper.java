package cn.ucai.live;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EasePreferenceManager;
import com.hyphenate.easeui.model.User;
import com.hyphenate.util.EMLog;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cn.ucai.live.data.local.LiveDao;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.ui.activity.MainActivity;
import cn.ucai.live.utils.L;

import static com.hyphenate.easeui.utils.EaseUserUtils.getUserInfo;
import static com.ucloud.ulive.UStreamingContext.appContext;

/**
 * Created by clawpo on 2017/6/8.
 */

public class LiveHelper {
    private static final String TAG = "LiveHelper";

    private static LiveHelper instance = null;
    private String username;
    private Map<String, User> appContactList;
    LiveModel model = null;
    private User currentAppUser= null;
    private Map<Integer,Gift> giftMap;
    private EaseUI easeUI;

    private LiveHelper() {
    }

    public synchronized static LiveHelper getInstance() {
        if (instance == null) {
            instance = new LiveHelper();
        }
        return instance;
    }

    public void init(final Context context){
        model = new LiveModel();
        EaseUI.getInstance().init(context, null);
        easeUI = EaseUI.getInstance();
        //to set user's profile and avatar
        setEaseUIProviders();
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
        getGiftListFromServer();
    }

    private void setEaseUIProviders() {
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }

            @Override
            public User getAppUser(String username) {
                return getAppUserInfo(username);
            }
        });
    }

    private User getAppUserInfo(String username) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        User user = null;
        if(username.equals(EMClient.getInstance().getCurrentUser()))
            return getCurrentAppUserInfo();
         user = getAppContactList().get(username);
//
        // if user is not in your contacts, set inital letter for him/her
        if(user == null){
            user = new User(username);
//            EaseCommonUtils.setAppUserInitialLetter(user);
        }
        return user;
    }

    public Map<String,User> getAppContactList() {
        if(appContactList == null){
            appContactList=new Hashtable<String, User>();
        }

        return appContactList;
    }
    public void saveAppContact(User user){
        appContactList.put(user.getMUserName(), user);
    }
    /**
     * user met some exception: conflict, removed or forbidden
     */
    protected void onUserException(String exception){
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

    /**
     * set current username
     * @param username
     */
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

    public void syncUserInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user = LiveManager.getInstance().loadUserInfo(EMClient.getInstance().getCurrentUser());
                    if (user!=null){
                        setCurrentAppUserNick(user.getMUserNick());
                        setCurrentAppUserAvatar(user.getAvatar());
                    }
                } catch (LiveException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void setCurrentAppUserNick(String nickname) {
        getCurrentAppUserInfo().setMUserNick(nickname);
        EasePreferenceManager.getInstance().setCurrentUserNick(nickname);
    }

    private void setCurrentAppUserAvatar(String avatar) {
        getCurrentAppUserInfo().setAvatar(avatar);
        EasePreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }

    public synchronized User getCurrentAppUserInfo() {
        if (currentAppUser == null) {
            String username = EMClient.getInstance().getCurrentUser();
            currentAppUser = new User(username);
            String nick = getCurrentUserNick();
            currentAppUser.setMUserNick((nick != null) ? nick : username);
            currentAppUser.setAvatar(getCurrentUserAvatar());
        }
        return currentAppUser;
    }

    private String getCurrentUserNick() {
        return EasePreferenceManager.getInstance().getCurrentUserNick();
    }

    private String getCurrentUserAvatar() {
        return EasePreferenceManager.getInstance().getCurrentUserAvatar();
    }

    public synchronized void reset() {
        currentAppUser = null;
        EasePreferenceManager.getInstance().removeCurrentUserInfo();
    }

    public void setGiftList(Map<Integer,Gift> list) {
        L.e(TAG,"setGiftList to cache");
        if(list == null){
            if (giftMap != null) {
                giftMap.clear();
            }
            return;
        }

        giftMap = list;
    }

    public Map<Integer,Gift> getGiftList() {
        if (giftMap == null) {
            giftMap = model.getGiftList();
        }

        // return a empty non-null object to avoid app crash
        if(giftMap == null){
            return new Hashtable<Integer,Gift>();
        }

        return giftMap;
    }

    public void getGiftListFromServer(){
        L.e(TAG,"getGiftListFromServer...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                L.e(TAG,"getGiftListFromServer...getGiftList()="+getGiftList().size());
                if (getGiftList().size()==0) {
                    try {
                        List<Gift> list = LiveManager.getInstance().loadGiftList();
                        L.e(TAG, "getGiftListFromServer...list=" + list);
                        if (list != null) {
                            Map<Integer, Gift> map = new HashMap<>();
                            for (Gift gift : list) {
                                L.e(TAG, "getGiftListFromServer...gift=" + gift);
                                map.put(gift.getId(), gift);
                            }
                            //save data to cache
                            setGiftList(map);
                            //save data to databases
                            LiveDao dao = new LiveDao();
                            dao.setGiftList(list);
                        }
                    } catch (LiveException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
