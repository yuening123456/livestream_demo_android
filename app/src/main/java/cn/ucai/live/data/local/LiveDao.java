package cn.ucai.live.data.local;

import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;
import java.util.Map;

import cn.ucai.live.data.model.Gift;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public class LiveDao {
    public static final String GIFT_TABLE_NAME = "t_superwechat_gift";
    public static final String GIFT_COLUME_ID = "m_gift_id";
    public static final String GIFT_COLUME_NAME = "m_git_name";
    public static final String GIFT_COLUME_URL  = "m_git_url";
    public static final String GIFT_COLUME_PRICE  = "m_git_price";


    public void saveGiftList(List<Gift> list) {
        LiveDBManager.getInstance().saveGiftList(list);
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<Integer, Gift> getGiftList() {

        return LiveDBManager.getInstance().getGiftList();
    }
}
