package cn.ucai.superwechat.parse;

import android.content.Context;

import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.superwechat.SuperWechatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.db.net.IModelUser;
import cn.ucai.superwechat.db.net.ModelUser;
import cn.ucai.superwechat.db.net.OnCompleteListener;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.PreferenceManager;

public class UserProfileManager {
    private static final String TAG = UserProfileManager.class.getSimpleName();
    /**
     * application context
     */
    protected Context appContext = null;

    /**
     * init flag: test if the sdk has been inited before, we don't need to init
     * again
     */
    private boolean sdkInited = false;

    /**
     * HuanXin sync contact nick and avatar listener
     */
    private List<SuperWechatHelper.DataSyncListener> syncContactInfosListeners;

    private boolean isSyncingContactInfosWithServer = false;

    private EaseUser currentUser;

    public UserProfileManager() {
    }

    public synchronized boolean init(Context context) {
        if (sdkInited) {
            return true;
        }
        ParseManager.getInstance().onInit(context);
        syncContactInfosListeners = new ArrayList<SuperWechatHelper.DataSyncListener>();
        sdkInited = true;
        return true;
    }

    public void addSyncContactInfoListener(SuperWechatHelper.DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactInfosListeners.contains(listener)) {
            syncContactInfosListeners.add(listener);
        }
    }

    public void removeSyncContactInfoListener(SuperWechatHelper.DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactInfosListeners.contains(listener)) {
            syncContactInfosListeners.remove(listener);
        }
    }

    public void asyncFetchContactInfosFromServer(List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {
        if (isSyncingContactInfosWithServer) {
            return;
        }
        isSyncingContactInfosWithServer = true;
        ParseManager.getInstance().getContactInfos(usernames, new EMValueCallBack<List<EaseUser>>() {

            @Override
            public void onSuccess(List<EaseUser> value) {
                isSyncingContactInfosWithServer = false;
                // in case that logout already before server returns,we should
                // return immediately
                if (!SuperWechatHelper.getInstance().isLoggedIn()) {
                    return;
                }
                if (callback != null) {
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                isSyncingContactInfosWithServer = false;
                if (callback != null) {
                    callback.onError(error, errorMsg);
                }
            }

        });

    }

    public void notifyContactInfosSyncListener(boolean success) {
        for (SuperWechatHelper.DataSyncListener listener : syncContactInfosListeners) {
            listener.onSyncComplete(success);
        }
    }

    public boolean isSyncingContactInfoWithServer() {
        return isSyncingContactInfosWithServer;
    }

    public synchronized void reset() {
        isSyncingContactInfosWithServer = false;
        currentUser = null;
        PreferenceManager.getInstance().removeCurrentUserInfo();
    }

    public synchronized EaseUser getCurrentUserInfo() {
        if (currentUser == null) {
            String username = EMClient.getInstance().getCurrentUser();
            currentUser = new EaseUser(username);
            String nick = getCurrentUserNick();
            currentUser.setNick((nick != null) ? nick : username);
            currentUser.setAvatar(getCurrentUserAvatar());
        }
        return currentUser;
    }

    public boolean updateCurrentUserNickName(final String nickname) {
        boolean isSuccess = ParseManager.getInstance().updateParseNickName(nickname);
        if (isSuccess) {
            setCurrentUserNick(nickname);
        }
        return isSuccess;
    }

    public String uploadUserAvatar(byte[] data) {
        String avatarUrl = ParseManager.getInstance().uploadParseAvatar(data);
        if (avatarUrl != null) {
            setCurrentUserAvatar(avatarUrl);
        }
        return avatarUrl;
    }

    public void asyncGetCurrentUserInfo(Context context) {
//		得到当前用户信息
//		ParseManager.getInstance().asyncGetCurrentUserInfo后面运行，比modelUser.getUserByName要晚，会挤掉本地保存的数据,给出一个随机昵称
        ParseManager.getInstance().asyncGetCurrentUserInfo(new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser value) {
                L.e(TAG, "asyncGetCurrentUserInfo" + value);
                if (value != null) {
//					保存了用户昵称
//    				setCurrentUserNick(value.getNick());
//    				setCurrentUserAvatar(value.getAvatar());
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
//		EMClient.getInstance().getCurrentUser()登录之后自动保存到环信中的
        L.e("UserProfileManager", "asyncGetCurrentUserInfo,userName" + EMClient.getInstance().getCurrentUser());

        IModelUser modelUser = new ModelUser();
        modelUser.getUserByName(context, EMClient.getInstance().getCurrentUser(), new OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                L.e("UserProfileManager", "result=" + result);
                if (result != null) {
                    User user = new Gson().fromJson(result.getRetData().toString(), User.class);
                    if (user != null) {
                        //	保存到SharePreference，也就是内存中
                        setCurrentUserNick(user.getMUserNick());
//                        user.getAvatar得到头像
//                        String path = "http://101.251.196.90:8000/SuperWeChatServerV2.0/downloadAvatar?name_or_hxid="+getMUserName()+"&avatarType=user_avatar&m_avatar_suffix="+getMAvatarSuffix()+"&updatetime="+getMAvatarLastUpdateTime();
                        // name_or_hxid avatarType m_avatar_suffix
                        setCurrentUserAvatar(user.getAvatar());
                        //保存到本地(自行创建)数据库 ,同时保存到集合中去了appContactList中，user，所以以后昵称，可以从user中得到
                        SuperWechatHelper.getInstance().saveAppContact(user);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void asyncGetUserInfo(final String username, final EMValueCallBack<EaseUser> callback) {
        ParseManager.getInstance().asyncGetUserInfo(username, callback);
    }

    private void setCurrentUserNick(String nickname) {
//      通过内存查找，如果有用户昵称，则用用户昵称，否则用用户名
//        下面这句没什么用，原因是后面每次写getCurrentUserInfo(),覆盖了此信息
        getCurrentUserInfo().setNick(nickname);
        PreferenceManager.getInstance().setCurrentUserNick(nickname);
    }

    private void setCurrentUserAvatar(String avatar) {
        getCurrentUserInfo().setAvatar(avatar);
        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }

    private String getCurrentUserNick() {
        return PreferenceManager.getInstance().getCurrentUserNick();
    }

    private String getCurrentUserAvatar() {
        return PreferenceManager.getInstance().getCurrentUserAvatar();
    }

}
