package com.hyphenate.easeui.ui;

import com.hyphenate.EMGroupChangeListener;
import java.util.List;

/**
 * group change listener
 *
 */
public abstract class EaseGroupRemoveListener implements EMGroupChangeListener{

    @Override
    public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onInvitationAccepted(String groupId, String inviter, String reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onInvitationDeclined(String groupId, String invitee, String reason) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
        // TODO Auto-generated method stub
    }

    @Override public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {

    }

    @Override public void onMuteListRemoved(String groupId, List<String> mutes) {

    }

    @Override public void onAdminAdded(String groupId, String administrator) {

    }

    @Override public void onAdminRemoved(String groupId, String administrator) {

    }

    @Override public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {

    }

    @Override public void onMemberExited(String groupId, String member) {

    }

    @Override public void onMemberJoined(String groupId, String member) {

    }
}
