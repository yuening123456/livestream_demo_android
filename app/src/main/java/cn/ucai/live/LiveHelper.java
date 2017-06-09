package cn.ucai.live;

import android.app.Application;
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


/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class LiveHelper {
    public static final String TAG = "LiveHelper";
    private String username;
    private Context appContext;
    private LiveModel model;
    private static LiveHelper instance = null;
    private User currentAppUser = null;
    private Map<Integer, Gift> giftMap;
    private EaseUI easeUI;

    private LiveHelper() {
    }

    public void init(Context context) {
        model = new LiveModel();
        appContext = context;
        EaseUI.getInstance().init(context, null);
        easeUI = EaseUI.getInstance();

        setEaseUIProviders();
        EMClient.getInstance().setDebugMode(true);
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected(int error) {

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
        if (username.equals(EMClient.getInstance().getCurrentUser()))
            return getCurrentAppUserInfo();
        //user = getAppContactList().get(username);
        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new User(username);
            //  EaseCommonUtils.setAppUserInitialLetter(user);
        }
        return user;
    }


    public synchronized static LiveHelper getInstance() {
        if (instance == null) {
            instance = new LiveHelper();
        }
        return instance;
    }

    public void setCurrentUserName(String username) {
        this.username = username;
        model.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName() {
        if (username == null) {
            username = model.getCurrentUsernName();
        }
        return username;
    }

    protected void onUserException(String exception) {
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

    public void syncUserInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user = LiveManager.getInstance().loadUserInfo(username);
                    if (user != null) {
                        setCurrentAppUserNick(user.getMUserNick());
                        setCurrentAppUserAvatar(user.getAvatar());
                    }
                } catch (LiveException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setCurrentAppUserAvatar(String avatar) {
        getCurrentAppUserInfo().setAvatar(avatar);
        EasePreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }

    private void setCurrentAppUserNick(String nickname) {
        getCurrentAppUserInfo().setMUserNick(nickname);
        EasePreferenceManager.getInstance().setCurrentUserNick(nickname);
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

    public void setGiftList(Map<Integer, Gift> list) {

        if (list == null) {
            if (giftMap != null) {
                giftMap.clear();
            }
            return;
        }

        giftMap = list;
    }

    public Map<Integer, Gift> getGiftList() {
        if (giftMap == null) {
            giftMap = model.getGiftList();
        }
        // return a empty non-null object to avoid app crash
        if (giftMap == null) {
            return new Hashtable<Integer, Gift>();
        }
        return giftMap;
    }

    public void getGiftListFromServer() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                L.e(TAG, "getGiftListFromServer...getGiftList()=" + getGiftList().size());
                if (getGiftList().size() == 0) {
                    try {
                        List<Gift> list = LiveManager.getInstance().getAllGifts();
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
                            dao.saveGiftList(list);
                        }
                    } catch (LiveException e) {
                        e.printStackTrace();
                    }
                }
            }


    }).start();

}



}
