package cn.ucai.live.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public class LiveDBOpenHelper extends SQLiteOpenHelper {
    private static final int versionNumber = 1;
    private static LiveDBOpenHelper instance;
    private static final String GIFT_TABLE_CREATE = "CREATE TABLE "
            + LiveDao.GIFT_TABLE_NAME + " ("
            + LiveDao.GIFT_COLUME_NAME + " TEXT, "
            + LiveDao.GIFT_COLUME_URL + " TEXT, "
            + LiveDao.GIFT_COLUME_PRICE + " INTEGER "
            + LiveDao.GIFT_COLUME_ID + " TEXT PRIMARY KEY);";

    public LiveDBOpenHelper(Context context) {
        super(context, getDatabaseNames(context), null, versionNumber);
    }

    private static String getDatabaseNames(Context context) {
        return context.getPackageName() + ".db";
    }

    public static LiveDBOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LiveDBOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GIFT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}
