package cn.ucai.live.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.ucai.live.I;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.L;

/**
 * Created by Administrator on 2017/6/12 0012.
 */

public class RoomUserGiftDialog extends DialogFragment {
    private static final String TAG = "RoomUserGiftDialog";
    @BindView(R.id.rv_gift)
    RecyclerView rvGift;
    @BindView(R.id.tv_my_bill)
    TextView tvMyBill;
    @BindView(R.id.tv_recharge)
    TextView tvRecharge;
    Unbinder unbinder;
    GiftAdapter adapter;
    static List<Gift> giftLists;
    View.OnClickListener onClickListener;
    AlertDialog.Builder dialog;
    EditText editText;
    public RoomUserGiftDialog() {

    }


    public static RoomUserGiftDialog newInstance() {
        RoomUserGiftDialog dialog = new RoomUserGiftDialog();
        if (giftLists != null) {
            giftLists.clear();
        }
        return dialog;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
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
        dialog=new AlertDialog.Builder(getActivity());
        editText=new EditText(getActivity());
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        giftLists = LiveHelper.getInstance().getGiftLists();
        initView();
        initData(giftLists);

    }


    private void initView() {
        GridLayoutManager gm = new GridLayoutManager(getActivity(), I.GIFT_COLUMN_COUNT);
        rvGift.setLayoutManager(gm);
    }

    private void initData(List<Gift> giftLists) {
        adapter = new GiftAdapter(getActivity(), giftLists);
        rvGift.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @OnClick(R.id.tv_recharge)
    public void RechargeOnClick() {
        L.e(TAG,"RechargeOnClick...");
        dialog.setTitle("充值")
                .setView(editText)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SuiBian();
                    }
                })
                .show();
        L.e(TAG,"RechargeOnClick,editText...");
    }

    private void SuiBian() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess=false;
                try {
                    Result<Wallet> result = LiveManager.getInstance().getRecharge(EMClient.
                            getInstance().getCurrentUser(),Integer.parseInt(editText.getText().toString().trim()));
                    if(result!=null&&result.isRetMsg()){
                        isSuccess=true;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showLongToast("充值成功");
                            }

                        });
                    }
                    if(!isSuccess){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showLongToast("充值失敗");
                            }

                        });
                    }
                } catch (LiveException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.showLongToast("充值失敗");
                        }

                    });
                }
            }
        }).start();
    }

    class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
        private Context context;
        private List<Gift> giftList;

        public GiftAdapter(Context context, List<Gift> gift) {
            this.giftList = gift;
            this.context = context;
        }

        @Override
        public GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GiftViewHolder(View.inflate(context, R.layout.item_gift, null));
        }

        @Override
        public void onBindViewHolder(GiftViewHolder holder, int position) {
            Gift s = giftList.get(position);
            holder.tvGiftName.setText(s.getGname());
            holder.tvGiftPrice.setText("￥" + s.getGprice());
            EaseUserUtils.setAppGiftAvatar(context, s.getGurl(), holder.ivGiftThumb);
            holder.itemView.setTag(s.getId());
            holder.itemView.setOnClickListener(onClickListener);
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
