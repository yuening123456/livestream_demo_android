package cn.ucai.live.ui.activity;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.R;
import cn.ucai.live.utils.L;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class RechargeDialog extends DialogFragment {
    private static final String TAG = "RechargeDialog";
    @BindView(R.id.rv_gift)
    RecyclerView rvGift;
    Unbinder unbinder;
    MoneyAdapter adapter;
    List<String> list;
    GridLayoutManager gm;
    View.OnClickListener clickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public RechargeDialog() {
    }

    public static RechargeDialog newInstance() {
        RechargeDialog dialog = new RechargeDialog();

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recharge, container, false);
        L.e(TAG,"onCreateView...");
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
        initView();
    }
    private void initView() {
        list=new ArrayList<>();
        list.add("10");
        list.add("20");
        list.add("50");
        list.add("100");
        list.add("200");
        list.add("500");
        L.e(TAG,"List="+list);
        adapter=new MoneyAdapter(getActivity(),list);
        rvGift.setAdapter(adapter);
        gm=new GridLayoutManager(getActivity(),3);
        rvGift.setLayoutManager(gm);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class MoneyAdapter extends RecyclerView.Adapter<MoneyAdapter.MoneyViewHolder> {
        Context context;
        List<String> list;

        public MoneyAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public MoneyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View   view = View.inflate(context, R.layout.item_money, null);
            return new MoneyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MoneyViewHolder holder, int position) {
            holder.btnMoney.setText(String.valueOf(list.get(position)));
            holder.btnMoney.setTag(list.get(position));
            holder.btnMoney.setOnClickListener(clickListener);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

         class MoneyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.btn_money)
            Button btnMoney;

             MoneyViewHolder(View view) {
                 super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
