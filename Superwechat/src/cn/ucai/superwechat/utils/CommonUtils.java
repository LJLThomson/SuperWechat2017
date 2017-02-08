package cn.ucai.superwechat.utils;

import android.widget.Toast;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWechatApplication;
/**
 * Created by clawpo on 16/9/20.
 */
public class CommonUtils {
    public static void showLongToast(String msg){
        Toast.makeText(SuperWechatApplication.getInstance(),msg,Toast.LENGTH_LONG).show();
    }
    public static void showShortToast(String msg){
        Toast.makeText(SuperWechatApplication.getInstance(),msg,Toast.LENGTH_SHORT).show();
    }
    public static void showLongToast(int rId){
        showLongToast(SuperWechatApplication.getInstance().getResources().getString(rId));
    }
    public static void showShortToast(int rId){
        showShortToast(SuperWechatApplication.getInstance().getString(rId));
    }
    public static void showLongResultMsg(int msg){
        showLongToast(getMsgString(msg));
    }
    public static void showShortResultMsg(int msg){
        showShortToast(getMsgString(msg));
    }
    private static int getMsgString(int msg){
        int resId = R.string.msg_1;
        if(msg>0){
            resId = SuperWechatApplication.getInstance().getResources()
                    .getIdentifier(I.MSG_PREFIX_MSG+msg,"string",
                            SuperWechatApplication.getInstance().getPackageName());
        }
        return resId;
    }

    public static String getWeChatNoString(){
        return SuperWechatApplication.getInstance().getString(R.string.userinfo_txt_wechat_no);
    }

    public static String getAddContactPrefixString(){
        return SuperWechatApplication.getInstance().getString(R.string.addcontact_send_msg_prefix);
    }
}
