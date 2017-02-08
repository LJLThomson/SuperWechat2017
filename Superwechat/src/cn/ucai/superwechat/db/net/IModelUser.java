package cn.ucai.superwechat.db.net;

import android.content.Context;

/**
 * Created by Administrator on 2017/2/8 0008.
 */

public interface IModelUser {
    void RegisterEnter(Context context, String userName, String NickName, String password, OnCompleteListener<String> OnCompleteListener);
}
