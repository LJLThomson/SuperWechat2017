package cn.ucai.superwechat.db.net;

import android.content.Context;

import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * Created by Administrator on 2017/2/8 0008.
 */

public interface IModelUser {
    void RegisterEnter(Context context, String userName, String NickName, String password, OnCompleteListener<String> OnCompleteListener);

    void UnRegisterEnter(Context context, String userName, OkHttpUtils.OnCompleteListener<String> onCompleteListener);
    void LoginEnter(Context context, String userName, String password, OkHttpUtils.OnCompleteListener<String> listener);
}
