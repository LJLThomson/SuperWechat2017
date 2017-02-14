package cn.ucai.superwechat.db.net;

import android.content.Context;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * Created by Administrator on 2017/2/8 0008.
 */

public class ModelUser implements IModelUser {
    @Override
    public void RegisterEnter(Context context, String userName, String NickName, String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME, userName)
                .addParam(I.User.NICK, NickName)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .post()
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void UnRegisterEnter(Context context, String userName, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME, userName)
                .post()
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void LoginEnter(Context context, String userName, String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME, userName)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void getUserByName(Context context, String userName, OnCompleteListener<Result> listener) {
        OkHttpUtils<Result> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME, userName)
                .targetClass(Result.class)
                .execute(listener);
    }

    @Override
    public void updateNickName(Context context, String userName, String nickName, OnCompleteListener<Result> listener) {
        OkHttpUtils<Result> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME, userName)
                .addParam(I.User.NICK, nickName)
                .targetClass(Result.class)
                .execute(listener);
    }

    @Override
    public void updateAvator(Context context, String user_name_or_hxid, File file, OnCompleteListener<Result> listener) {
        OkHttpUtils<Result> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID, user_name_or_hxid)
                .addParam(I.AVATAR_TYPE, I.AVATAR_TYPE_USER_PATH)
                .addFile(file)
                .post()
                .targetClass(Result.class)
                .execute(listener);
    }

    @Override
    public void addcontact(Context context, String user_name, String cname, OnCompleteListener<Result> listener) {
        OkHttpUtils<Result> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_CONTACT)
                .addParam(I.Contact.USER_NAME, user_name)
                .addParam(I.Contact.CU_NAME, cname)
                .targetClass(Result.class)
                .execute(listener);
    }
}
