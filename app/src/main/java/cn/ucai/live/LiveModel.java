package cn.ucai.live;


import com.hyphenate.easeui.model.EasePreferenceManager;

import java.util.Map;

import cn.ucai.live.data.local.LiveDao;
import cn.ucai.live.data.model.Gift;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class LiveModel {

    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        EasePreferenceManager.getInstance().setCurrentUsername(username);
    }

    public String getCurrentUsernName(){
        return EasePreferenceManager.getInstance().getCurrentUsername();
    }


    public Map<Integer,Gift> getGiftList() {
        LiveDao dao=new LiveDao();
       return dao.getGiftList();
    }
}
