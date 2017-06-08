package cn.ucai.live;

import cn.hyphenate.easeui.model.EasePreferenceManager;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class LiveModel {

    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        EasePreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName(){
        return EasePreferenceManager.getInstance().getCurrentUsername();
    }
}
