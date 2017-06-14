package cn.ucai.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.L;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class PersonalActivity extends BaseActivity {
    private static final String TAG = "PersonalActivity";

    String s;
    @BindView(R.id.userAvatar)
    EaseImageView userAvatar;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.userPrice)
    TextView userPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_profile);
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
                    if (result != null && result.isRetMsg()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                L.e(TAG, "initData result=" + result.getRetData().getBalance());
                                userPrice.setText(String.valueOf(result.getRetData().getBalance()));
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
        L.e(TAG, "initView s=" + s);
        EaseUserUtils.setAppUserNick(s, username);
        EaseUserUtils.setAppUserAvatar(PersonalActivity.this, s, userAvatar);
    }

    @OnClick(R.id.price_layout)
    public void priceOnClick() {
        RechargeDialog dialog = RechargeDialog.newInstance();
        dialog.show(getSupportFragmentManager(), "RoomUserGiftDialog");
        dialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String money = (String) v.getTag();
                final int i = Integer.parseInt(money);
                L.e(TAG, "money=" + money);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Result<Wallet> result = LiveManager.getInstance().getRecharge(
                                    String.valueOf(s.toString()), i);
                            if (result != null && result.isRetMsg()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonUtils.showLongToast("您充值的" + money + "已到賬");
                                    }
                                });
                            }
                        } catch (LiveException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @OnClick(R.id.btn_logout)
    public void exitClick() {
        finish();
        startLogin();
        // startActivity(new Intent(PersonalActivity.this,LoginActivity.class));
    }

    private void startLogin() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                LiveHelper.getInstance().reset();
                finish();
                startActivity(new Intent(PersonalActivity.this, LoginActivity.class));
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    @OnClick({R.id.giving_gift, R.id.recy_gift})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.giving_gift:
                GivingDialog dialog = GivingDialog.newInstance();
                dialog.show(getSupportFragmentManager(), "RoomUserGiftDialog");
                break;
            case R.id.recy_gift:
                break;
        }
    }
}
