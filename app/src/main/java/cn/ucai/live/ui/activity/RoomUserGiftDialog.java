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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.model.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.I;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Gift;

/**
 * Created by Administrator on 2017/6/12 0012.
 */

public class RoomUserGiftDialog extends DialogFragment {
    @BindView(R.id.rv_gift)
    RecyclerView rvGift;
    @BindView(R.id.tv_my_bill)
    TextView tvMyBill;
    @BindView(R.id.tv_recharge)
    TextView tvRecharge;
    Unbinder unbinder;
    GiftAdapter adapter;

    public RoomUserGiftDialog() {

    }


    public static RoomUserGiftDialog newInstance() {
        RoomUserGiftDialog dialog = new RoomUserGiftDialog();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift_list, container, false);
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
        List<Gift> giftLists= LiveHelper.getInstance().getGiftLists();
        initView();
        initData(giftLists);

    }

    private void initView() {
        GridLayoutManager gm=new GridLayoutManager(getActivity(), I.GIFT_COLUMN_COUNT);
        rvGift.setLayoutManager(gm);
    }

    private void initData( List<Gift> giftLists) {
        adapter=new GiftAdapter(getActivity(),giftLists);
        rvGift.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
        private Context context;
        private List<Gift> giftList;

        public GiftAdapter(Context context, List<Gift> gift) {
            this.giftList = gift;
            this.context = context;
        }

        @Override
        public RoomUserGiftDialog.GiftAdapter.GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GiftViewHolder(View.inflate(context, R.layout.item_gift, null));
        }

        @Override
        public void onBindViewHolder(GiftViewHolder holder, int position) {
            Gift s = giftList.get(position);
            holder.tvGiftName.setText(s.getGname());
            holder.tvGiftPrice.setText("￥"+s.getGprice());
            EaseUserUtils.setAppGiftAvatar(context,s.getGurl(),holder.ivGiftThumb);
        }

        @Override
        public int getItemCount() {
            return giftList.size();
        }


        class GiftViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ivGiftThumb)
            ImageView ivGiftThumb;
            @BindView(R.id.tvGiftName)
            TextView tvGiftName;
            @BindView(R.id.tvGiftPrice)
            TextView tvGiftPrice;
            @BindView(R.id.layout_gift)
            LinearLayout layoutGift;

            GiftViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

    }
}
