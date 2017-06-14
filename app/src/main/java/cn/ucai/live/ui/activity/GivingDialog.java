package cn.ucai.live.ui.activity;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.R;
import cn.ucai.live.data.model.GiftStatements;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.utils.L;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class GivingDialog extends DialogFragment {
    private static final String TAG = "GivingDialog";
    @BindView(R.id.rv_gift)
    RecyclerView rvGift;
    Unbinder unbinder;
    MoneyAdapter adapter;
    List<GiftStatements> list;
    LinearLayoutManager lm;
    View.OnClickListener clickListener;
    protected int pageId = 1;

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public GivingDialog() {
    }

    public static GivingDialog newInstance() {
        GivingDialog dialog = new GivingDialog();

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recharge, container, false);
        L.e(TAG, "onCreateView...");
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list=new ArrayList<>();
        initData();

    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    L.e(TAG,"retData...");
                    Result<List<GiftStatements>> result = LiveManager.getInstance().givingGiftStatements(
                            EMClient.getInstance().getCurrentUser(), pageId, 10);
                    if (result != null && result.isRetMsg()) {
                        final List<GiftStatements> retData = result.getRetData();
                        L.e(TAG,"retData="+retData);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            list=retData;
                            initView();
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
        adapter = new MoneyAdapter(getActivity(), list);
        L.e(TAG,"list="+list);
        rvGift.setAdapter(adapter);
        lm = new LinearLayoutManager(getActivity());
        rvGift.setLayoutManager(lm);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class MoneyAdapter extends RecyclerView.Adapter<MoneyAdapter.GivingViewHolder> {
        Context context;
        List<GiftStatements> list;

        public MoneyAdapter(Context context, List<GiftStatements> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public GivingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.item_giving_gift, null);
            return new GivingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GivingViewHolder holder, int position) {
            String s="http://101.251.196.90:8080/SuperWeChatServerV2.0/gift/";
            EaseUserUtils.setAppGiftAvatar(context,s+list.get(position).getGurl(),holder.giftAvatar);
            holder.giftName.setText(list.get(position).getGname());
            holder.giftNum.setText(String.valueOf(list.get(position).getGiftnum()));

        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class GivingViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.gift_avatar)
            ImageView giftAvatar;
            @BindView(R.id.gift_name)
            TextView giftName;
            @BindView(R.id.gift_num)
            TextView giftNum;

            GivingViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
