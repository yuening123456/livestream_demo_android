package cn.ucai.live.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cn.ucai.live.LiveApplication;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.utils.L;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public class LiveDBManager {
    private static final String TAG = "LiveDBManager";
    static private LiveDBManager dbMgr = new LiveDBManager();
    private LiveDBOpenHelper dbHelper;

    private LiveDBManager(){
        L.e(TAG,"LiveDBManager()...");
        dbHelper = LiveDBOpenHelper.getInstance(LiveApplication.getInstance().getApplicationContext());
    }

    public static synchronized LiveDBManager getInstance(){
        if(dbMgr == null){
            dbMgr = new LiveDBManager();
        }
        return dbMgr;
    }
    synchronized public void saveGiftList(List<Gift> list) {
        L.e(TAG,"LiveDBManager...saveGiftList..");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(LiveDao.GIFT_TABLE_NAME, null, null);
            for (Gift gift : list) {
                ContentValues values = new ContentValues();
                values.put(LiveDao.GIFT_COLUME_ID, gift.getId());
                if(gift.getGname() != null)
                    values.put(LiveDao.GIFT_COLUME_NAME, gift.getGname());
                if(gift.getGurl() != null)
                    values.put(LiveDao.GIFT_COLUME_URL, gift.getGurl());
                if(gift.getGprice() != null)
                    values.put(LiveDao.GIFT_COLUME_PRICE, gift.getGprice());
                db.replace(LiveDao.GIFT_TABLE_NAME, null, values);
            }
        }
    }

    /**
     * get contact list
     *
     * @return
     */
    synchronized public Map<Integer, Gift> getGiftList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<Integer, Gift> gifts = new Hashtable<Integer, Gift>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + LiveDao.GIFT_TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                int giftId = cursor.getInt(cursor.getColumnIndex(LiveDao.GIFT_COLUME_ID));
                String giftName = cursor.getString(cursor.getColumnIndex(LiveDao.GIFT_TABLE_NAME));
                String giftUrl = cursor.getString(cursor.getColumnIndex(LiveDao.GIFT_COLUME_URL));
                int giftPrice = cursor.getInt(cursor.getColumnIndex(LiveDao.GIFT_COLUME_PRICE));
                Gift gift = new Gift();
                gift.setId(giftId);
                gift.setGname(giftName);
                gift.setGurl(giftUrl);
                gift.setGprice(giftPrice);

                gifts.put(giftId, gift);
            }
            cursor.close();
        }
        return gifts;
    }

}
