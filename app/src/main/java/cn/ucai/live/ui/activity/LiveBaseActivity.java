package cn.ucai.live.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.model.EasePreferenceManager;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.live.I;
import cn.ucai.live.LiveConstants;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.ThreadPoolManager;
import cn.ucai.live.data.TestAvatarRepository;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.data.model.LiveRoom;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.ui.widget.LiveLeftGiftView;
import cn.ucai.live.ui.widget.PeriscopeLayout;
import cn.ucai.live.ui.widget.RoomMessagesView;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.L;
import cn.ucai.live.utils.Utils;

/**
 * Created by wei on 2016/6/12.
 */
public abstract class LiveBaseActivity extends BaseActivity {
    protected static final String TAG = "LiveActivity";
    @BindView(R.id.left_gift_view1)
    LiveLeftGiftView leftGiftView;
    @BindView(R.id.left_gift_view2)
    LiveLeftGiftView leftGiftView2;
    @BindView(R.id.message_view)
    RoomMessagesView messageView;
    @BindView(R.id.periscope_layout)
    PeriscopeLayout periscopeLayout;
    @BindView(R.id.bottom_bar)
    View bottomBar;

    //@BindView(R.id.barrage_layout) BarrageLayout barrageLayout;
    @BindView(R.id.horizontal_recycle_view)
    RecyclerView horizontalRecyclerView;
    @BindView(R.id.audience_num)
    TextView audienceNumView;
    //@BindView(R.id.new_messages_warn) ImageView newMsgNotifyImage;

    @BindView(R.id.user_manager_image)
    ImageView userManagerView;
    @BindView(R.id.gift_images)
    ImageView userGiftView;
    @BindView(R.id.switch_camera_image)
    ImageView switchCameraView;
    @BindView(R.id.like_image)
    ImageView likeImageView;
    @BindView(R.id.txt_live_id)
    TextView liveIdView;
    @BindView(R.id.tv_username)
    TextView usernameView;
    @BindView((R.id.iv_anchor_avatar))
    EaseImageView useravatarView;

    protected String anchorId;

    protected LiveRoom liveRoom;

    protected int watchedCount;
    protected int membersCount;


    /**
     * 环信聊天室id
     */
    protected String chatroomId = "";
    /**
     * ucloud直播id
     */
    protected String liveId = "";
    protected boolean isMessageListInited;
    protected EMChatRoomChangeListener chatRoomChangeListener;

    volatile boolean isGiftShowing = false;
    volatile boolean isGift2Showing = false;
    List<EMMessage> toShowList = Collections.synchronizedList(new LinkedList<EMMessage>());

    protected EMChatRoom chatroom;
    private static final int MAX_SIZE = 10;
    LinkedList<String> memberList = new LinkedList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        liveRoom = (LiveRoom) getIntent().getSerializableExtra("liveroom");
        liveId = liveRoom.getId();
        chatroomId = liveRoom.getChatroomId();
        anchorId = liveRoom.getAnchorId();
        onActivityCreate(savedInstanceState);
        //EaseUserUtils.setAppUserAvatar(LiveBaseActivity.this,anchorId,useravatarView);
        //loadanchorUserInfo
        loadAnchor(anchorId);
        intAnchorInfo();
        liveIdView.setText(liveId);
        audienceNumView.setText(String.valueOf(liveRoom.getAudienceNum()));
        watchedCount = liveRoom.getAudienceNum();
    }

    public void intAnchorInfo() {
        L.e(TAG, "intAnchorInfo.....");
        //set nick

        if (liveRoom.getNickName() == null) {
            usernameView.setText(anchorId);
        } else {
            usernameView.setText(liveRoom.getNickName());
        }
        EaseUserUtils.setAppUserAvatar(LiveBaseActivity.this, anchorId, useravatarView);
    }

    protected abstract void loadAnchor(String anchorId);

    protected Handler handler = new Handler();

    protected abstract void onActivityCreate(@Nullable Bundle savedInstanceState);


    protected void showPraise(final int count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    if (!isFinishing())
                        periscopeLayout.addHeart();
                }
            }
        });

    }


    protected void addChatRoomChangeListener() {
        chatRoomChangeListener = new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(chatroomId)) {
                    finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                onRoomMemberAdded(participant);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                //                showChatroomToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
                onRoomMemberExited(participant);
            }

            @Override
            public void onRemovedFromChatRoom(String roomId, String roomName, String participant) {
                if (roomId.equals(chatroomId)) {
                    String curUser = EMClient.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);
                        //postUserChangeEvent(StatisticsType.LEAVE, curUser);
                        showToast("你已被移除出此房间");
                        finish();
                    } else {
                        //                        showChatroomToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                        onRoomMemberExited(participant);
                    }
                }
            }

            @Override
            public void onMuteListAdded(String chatRoomId, List<String> mutes, long expireTime) {
                for (String name : mutes) {
                    showMemberChangeEvent(name, "被禁言");
                }
            }

            @Override
            public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
                for (String name : mutes) {
                    showMemberChangeEvent(name, "被解除禁言");
                }
            }

            @Override
            public void onAdminAdded(String chatRoomId, String admin) {
                if (admin.equals(EMClient.getInstance().getCurrentUser())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userManagerView.setVisibility(View.VISIBLE);

                        }
                    });
                }
                showMemberChangeEvent(admin, "被提升为房管");
            }

            @Override
            public void onAdminRemoved(String chatRoomId, String admin) {
                if (admin.equals(EMClient.getInstance().getCurrentUser())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userManagerView.setVisibility(View.INVISIBLE);


                        }
                    });
                }
                showMemberChangeEvent(admin, "被解除房管");
            }

            @Override
            public void onOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {

            }
        };

        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);
    }

    private void showMemberChangeEvent(String username, String event) {
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        message.setTo(chatroomId);
        message.setFrom(username);
        EMTextMessageBody textMessageBody = new EMTextMessageBody(event);
        message.addBody(textMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().saveMessage(message);
        messageView.refreshSelectLast();
    }


    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat
                        || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(chatroomId)) {
                    //if (message.getBooleanAttribute(DemoConstants.EXTRA_IS_BARRAGE_MSG, false)) {
                    //    barrageLayout.addBarrage(
                    //            ((EMTextMessageBody) message.getBody()).getMessage(),
                    //            message.getFrom());
                    //}
                    messageView.refreshSelectLast();
                } else {
                    //if(message.getChatType() == EMMessage.ChatType.Chat && message.getTo().equals(EMClient.getInstance().getCurrentUser())){
                    //  runOnUiThread(new Runnable() {
                    //    @Override public void run() {
                    //      newMsgNotifyImage.setVisibility(View.VISIBLE);
                    //    }
                    //  });
                    //}
                    //// 如果消息不是和当前聊天ID的消息
                    //EaseUI.getInstance().getNotifier().onNewMsg(message);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            EMMessage message = messages.get(messages.size() - 1);
            if (LiveConstants.CMD_GIFT.equals(((EMCmdMessageBody) message.getBody()).action())) {
                showLeftGiftVeiw(message);
            } else if (LiveConstants.CMD_PRAISE.equals(((EMCmdMessageBody) message.getBody()).action())) {
                showPraise(message.getIntAttribute(LiveConstants.EXTRA_PRAISE_COUNT, 1));
            }
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            if (isMessageListInited) {
                messageView.refresh();
            }
        }
    };

    protected void onMessageListInit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageView.init(chatroomId);
                messageView.setMessageViewListener(new RoomMessagesView.MessageViewListener() {
                    @Override
                    public void onMessageSend(String content) {
                        EMMessage message = EMMessage.createTxtSendMessage(content, chatroomId);
                        //if (messageView.isBarrageShow) {
                        //    message.setAttribute(DemoConstants.EXTRA_IS_BARRAGE_MSG, true);
                        //    barrageLayout.addBarrage(content,
                        //            EMClient.getInstance().getCurrentUser());
                        //}
                        message.setChatType(EMMessage.ChatType.ChatRoom);
                        EMClient.getInstance().chatManager().sendMessage(message);
                        message.setMessageStatusCallback(new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //刷新消息列表
                                messageView.refreshSelectLast();
                            }

                            @Override
                            public void onError(int i, String s) {
                                showToast("消息发送失败！");
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }

                    @Override
                    public void onItemClickListener(final EMMessage message) {
                        //if(message.getFrom().equals(EMClient.getInstance().getCurrentUser())){
                        //    return;
                        //}
                        String clickUsername = message.getFrom();
                        showUserDetailsDialog(clickUsername);
                    }

                    @Override
                    public void onHiderBottomBar() {
                        bottomBar.setVisibility(View.VISIBLE);
                    }
                });
                messageView.setVisibility(View.VISIBLE);
                bottomBar.setVisibility(View.VISIBLE);
                if (!chatroom.getAdminList().contains(EMClient.getInstance().getCurrentUser())
                        && !chatroom.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                    userManagerView.setVisibility(View.INVISIBLE);

                }
                isMessageListInited = true;
                updateUnreadMsgView();
                showMemberList();
            }
        });
    }

    protected void updateUnreadMsgView() {
        //if(isMessageListInited) {
        //  for (EMConversation conversation : EMClient.getInstance()
        //      .chatManager()
        //      .getAllConversations()
        //      .values()) {
        //    if (conversation.getType() == EMConversation.EMConversationType.Chat
        //        && conversation.getUnreadMsgCount() > 0) {
        //      newMsgNotifyImage.setVisibility(View.VISIBLE);
        //      return;
        //    }
        //  }
        //  newMsgNotifyImage.setVisibility(View.INVISIBLE);
        //}
    }

    private void showUserDetailsDialog(String username) {
        RoomUserDetailsDialog dialog = RoomUserDetailsDialog.newInstance(username, liveRoom);
        dialog.setManageEventListener(new RoomUserDetailsDialog.RoomManageEventListener() {
            @Override
            public void onKickMember(String username) {
                onRoomMemberExited(username);
            }

            @Override
            public void onAddBlacklist(String username) {
                onRoomMemberExited(username);
            }
        });
        dialog.show(getSupportFragmentManager(), "RoomUserDetailsDialog");
    }

    private void showInputView() {
        bottomBar.setVisibility(View.INVISIBLE);
        messageView.setShowInputView(true);
        messageView.getInputView().requestFocus();
        messageView.getInputView().requestFocusFromTouch();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showKeyboard(messageView.getInputView());
            }
        }, 200);
    }

    private LinearLayoutManager layoutManager;

    void showMemberList() {
        layoutManager = new LinearLayoutManager(LiveBaseActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalRecyclerView.setLayoutManager(layoutManager);
        horizontalRecyclerView.setAdapter(new AvatarAdapter(LiveBaseActivity.this, memberList));
        executeTask(new ThreadPoolManager.Task<Void>() {
            @Override
            public Void onRequest() throws HyphenateException {
                try {
                    chatroom = EMClient.getInstance()
                            .chatroomManager()
                            .fetchChatRoomFromServer(chatroomId, true);
                    memberList.clear();
                    List<String> tempList = new ArrayList<>();
                    tempList.addAll(chatroom.getAdminList());
                    tempList.addAll(chatroom.getMemberList());
                    if (tempList.contains(chatroom.getOwner())) {
                        tempList.remove(chatroom.getOwner());
                    }
                    if (tempList.size() > MAX_SIZE) {
                        for (int i = 0; i < MAX_SIZE; i++) {
                            memberList.add(i, tempList.get(i));
                        }
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onSuccess(Void aVoid) {
                int size = chatroom.getMemberCount();
                membersCount = liveRoom.getAudienceNum();
                showMember();
                //观看人数不包含主播
                watchedCount = membersCount - 1;
                audienceNumView.setText(String.valueOf(membersCount));
                notifyDataSetChanged();
            }

            @Override
            public void onError(HyphenateException exception) {

            }
        });
    }

    protected void showMember() {
        if (chatroom != null) {
            memberList.addAll(chatroom.getMemberList());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    audienceNumView.setText(String.valueOf(membersCount));
                    notifyDataSetChanged();
                }
            });
        }
    }


    private synchronized void onRoomMemberAdded(String name) {
        watchedCount++;
        if (!memberList.contains(name)) {
            membersCount++;
            if (memberList.size() >= MAX_SIZE)
                memberList.removeLast();
            memberList.addFirst(name);
            showMemberChangeEvent(name, "来了");
            EMLog.d(TAG, name + "added");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    audienceNumView.setText(String.valueOf(membersCount));
                    notifyDataSetChanged();
                }
            });
        }

    }

    private void notifyDataSetChanged() {
        if (memberList.size() > 4) {
            layoutManager.setStackFromEnd(false);
        } else {
            layoutManager.setStackFromEnd(true);
        }
        horizontalRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private synchronized void onRoomMemberExited(final String name) {
        if (memberList.remove(name))
            membersCount--;
        EMLog.e(TAG, name + "exited");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                audienceNumView.setText(String.valueOf(membersCount));
                horizontalRecyclerView.getAdapter().notifyDataSetChanged();
                if (name.equals(anchorId)) {
                    showLongToast("主播已结束直播");
                }
            }
        });
    }


    //@OnClick(R.id.root_layout) void onRootLayoutClick() {
    //    periscopeLayout.addHeart();
    //}

    @OnClick(R.id.comment_image)
    void onCommentImageClick() {
        showInputView();
    }

    @OnClick(R.id.user_manager_image)
    void showUserList() {
        RoomUserManagementDialog managementDialog = RoomUserManagementDialog.newInstance(chatroomId);
        managementDialog.show(getSupportFragmentManager(), "RoomUserManagementDialog");
    }

    @OnClick(R.id.gift_images)
    void showUserGift() {
        final RoomUserGiftDialog giftDialog = RoomUserGiftDialog.newInstance();
        giftDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int giftId = (int) v.getTag();
               showSendDialogPre(giftId);
//                 sendGift(giftId);
            }
        });
        giftDialog.show(getSupportFragmentManager(), "RoomUserGiftDialog");
    }
   protected void showSendDialogPre(int giftId){
       if(EasePreferenceManager.getInstance().getShowDialog()){
           sendGift(giftId);
       }else{
            showSendDialog(giftId);
       }
    }
    protected  void showSendDialog(final int giftId){
        final Gift gift = LiveHelper.getInstance().getGiftList().get(giftId);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        CheckBox cb = new CheckBox(this);
        cb.setText("点击下次不会提醒");
        builder.setTitle("提示")
                .setMessage("你确定打赏"+gift.getGname()+"吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendGift(giftId);
                    }
                })
                .setView(cb)
                .show();
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    EasePreferenceManager.getInstance().setShowDialog(isChecked);
                }
            });
    }

    protected void sendGift(final int giftId) {
        Gift gift = LiveHelper.getInstance().getGiftList().get(giftId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isSuccess=false;
                    Result<Wallet> result=LiveManager.getInstance().givingGifts(
                            EMClient.getInstance().getCurrentUser(),anchorId,giftId,1);
                    if(result!=null){
                        if(result.isRetMsg()){
                            isSuccess=true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onPresentImageClick(giftId);
                                }
                            });
                        }
                    }
                    if (!isSuccess){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showLongToast(R.string.no_money);
                            }
                        });
                    }
                } catch (LiveException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.showLongToast(R.string.no_money);
                                }
                            });
                        }
                    });
                }
            }
        }).start();

    }

    //@OnClick(R.id.present_image)
    void onPresentImageClick(int giftId) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setTo(chatroomId);
        message.setAttribute(I.User.NICK, LiveHelper.getInstance().getCurrentAppUserInfo().getMUserNick());
        message.setAttribute(LiveConstants.CMD_GIFT, giftId);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(LiveConstants.CMD_GIFT);
        message.addBody(cmdMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        EMClient.getInstance().chatManager().sendMessage(message);
        showLeftGiftVeiw(message);
    }


    //@OnClick(R.id.chat_image) void onChatImageClick() {
    //  ConversationListFragment fragment = ConversationListFragment.newInstance(anchorId, false);
    //  getSupportFragmentManager().beginTransaction()
    //      .replace(R.id.message_container, fragment)
    //      .commit();
    //
    //}

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class AvatarAdapter extends RecyclerView.Adapter<AvatarViewHolder> {
        List<String> namelist;
        Context context;
        TestAvatarRepository avatarRepository;

        public AvatarAdapter(Context context, List<String> namelist) {
            this.namelist = namelist;
            this.context = context;
            avatarRepository = new TestAvatarRepository();
        }

        @Override
        public AvatarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AvatarViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.avatar_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AvatarViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUserDetailsDialog(namelist.get(position));
                }
            });
            EaseUserUtils.setAppUserAvatar(context, namelist.get(position), holder.Avatar);
            //暂时使用测试数据
           /* Glide.with(context)
                    .load(avatarRepository.getAvatar())
                    .placeholder(R.drawable.ease_default_avatar)
                    .into(holder.Avatar);*/
        }

        @Override
        public int getItemCount() {
            return namelist.size();
        }
    }

    static class AvatarViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar)
        ImageView Avatar;

        public AvatarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    protected synchronized void showLeftGiftVeiw(EMMessage message) {
        if (!isGift2Showing) {
            showGift2Direct(message);
        } else if (!isGiftShowing) {
            showGift1Direct(message);
        } else {
            toShowList.add(message);
        }
    }

    private void showGift1Direct(final EMMessage message) {
        isGiftShowing = true;
        animateGiftView(message, leftGiftView, new AnimationListener.Stop() {
            @Override public void onStop() {
                EMMessage pollName = null;
                try {
                    pollName = toShowList.remove(0);
                } catch (Exception e) {

                }
                if (pollName != null) {
                    showGift1Direct(pollName);
                } else {
                    isGiftShowing = false;
                }
            }
        });
    }

    private void showGift2Direct(final EMMessage message) {
        isGift2Showing = true;
        animateGiftView(message, leftGiftView2, new AnimationListener.Stop() {
            @Override public void onStop() {
                EMMessage pollName = null;
                try {
                    pollName = toShowList.remove(0);
                } catch (Exception e) {

                }
                if (pollName != null) {
                    showGift2Direct(pollName);
                } else {
                    isGift2Showing = false;
                }
            }
        });
    }

    private void animateGiftView(final EMMessage message, final LiveLeftGiftView giftView, final AnimationListener.Stop animationStop) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                int giftId = 0;
                String nick = message.getFrom();
                try {
                    giftId = message.getIntAttribute(LiveConstants.CMD_GIFT,0);
                    nick = message.getStringAttribute(I.User.NICK);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                giftView.setVisibility(View.VISIBLE);
                giftView.setName(nick);
                giftView.setAvatar(message.getFrom());
                giftView.setGift(giftId);
                giftView.setTranslationY(0);
                ViewAnimator.animate(giftView)
                        .alpha(0, 1)
                        .translationX(-giftView.getWidth(), 0)
                        .duration(600)
                        .thenAnimate(giftView)
                        .alpha(1, 0)
                        .translationY(-1.5f * giftView.getHeight())
                        .duration(800)
                        .onStop(animationStop)
                        .startDelay(2000)
                        .start();
                ViewAnimator.animate(giftView.getGiftImageView())
                        .translationX(-giftView.getGiftImageView().getX(), 0)
                        .duration(1100)
                        .start();
            }
        });
    }
}