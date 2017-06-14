package cn.ucai.live.ui.activity;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.utils.L;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class PersonalActivity extends BaseActivity {
    private static final String TAG = "PersonalActivity";
    @BindView(R.id.userAvatar)
    ImageView userAvatar;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.userPrice)
    TextView userPrice;
    @BindView(R.id.recharge)
    TextView recharge;
    String s;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        s = EMClient.getInstance().getCurrentUser().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Result<Wallet> result = LiveManager.getInstance().getBalance(s);
                    if(result!=null&&result.isRetMsg()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recharge.setText("余額:"+result.getRetData().getBalance());
                            }
                        });

                    }
                } catch (LiveException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        s = EMClient.getInstance().getCurrentUser().toString();
        L.e(TAG,"initView s="+s);
        EaseUserUtils.setAppUserNick(s,username);
        EaseUserUtils.setAppUserAvatar(PersonalActivity.this,s,userAvatar);
    }
}
