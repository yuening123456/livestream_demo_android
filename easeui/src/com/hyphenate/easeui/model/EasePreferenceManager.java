package com.hyphenate.easeui.model;

import java.util.Set;

import com.hyphenate.easeui.controller.EaseUI;

import android.content.Context;
import android.content.SharedPreferences;

public class EasePreferenceManager {
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private static final String KEY_AT_GROUPS = "AT_GROUPS";

    private SharedPreferences mUserSharedPreferences;
    private SharedPreferences.Editor userEditor;
    private static final String USER_INFO = "userInfo";

    private EasePreferenceManager() {
        mSharedPreferences = EaseUI.getInstance().getContext().getSharedPreferences("EM_SP_AT_MESSAGE", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();

        mUserSharedPreferences = EaseUI.getInstance().getContext().getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        userEditor = mUserSharedPreferences.edit();
    }

    private static EasePreferenceManager instance;

    public synchronized static EasePreferenceManager getInstance() {
        if (instance == null) {
            instance = new EasePreferenceManager();
        }
        return instance;

    }

    public void setCurrentUsername(String username) {
        userEditor.putString("username", username);
        userEditor.apply();
    }

    public void setCurrentUserNick(String userNick) {
        userEditor.putString("userNick", userNick);
        userEditor.apply();
    }

    public void setShowDialog(boolean  isShow) {
        userEditor.putBoolean("isShow", isShow);
        userEditor.apply();
    }

    public boolean getShowDialog() {
        return mUserSharedPreferences.getBoolean("isShow", false);
    }
    public void setCurrentUserAvatar(String avatar) {
        userEditor.putString("avatar", avatar);
        userEditor.apply();
    }

    public String getCurrentUsername() {
        return mUserSharedPreferences.getString("username", null);
    }
    public String getCurrentUserNick() {
        return mUserSharedPreferences.getString("userNick", null);
    }

    public String getCurrentUserAvatar() {
        return mUserSharedPreferences.getString("avatar", null);
    }

    public void removeCurrentUserInfo() {
        userEditor.remove("username");
        userEditor.remove("userNick");
        userEditor.remove("avatar");
        userEditor.apply();
    }


    public void setAtMeGroups(Set<String> groups) {
        editor.remove(KEY_AT_GROUPS);
        editor.commit();
        editor.putStringSet(KEY_AT_GROUPS, groups);
        editor.commit();
    }

    public Set<String> getAtMeGroups() {
        return mSharedPreferences.getStringSet(KEY_AT_GROUPS, null);
    }

}
