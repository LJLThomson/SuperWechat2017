package cn.ucai.superwechat.video.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SettingActivity;


/**
 * Created by Administrator on 2017/1/10 0010.
 */

public class MFGT {
    public static void finish(FragmentActivity context) {
        context.finish();
    }

    public static void startActivity(FragmentActivity context, Class<?> clz) {
        context.startActivity(new Intent(context, clz));
        context.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);//进入，出去anim属性
    }

    public static void startActivity(Activity context, Intent intent) {
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void gotoLoginActivity(FragmentActivity loginAndRegisterActivity) {
        startActivity(loginAndRegisterActivity, LoginActivity.class);
    }

    public static void gotoRegisterActivity(FragmentActivity loginAndRegisterActivity) {
        startActivity(loginAndRegisterActivity, RegisterActivity.class);
    }

    public static void gotoSettingActivity(FragmentActivity activity) {
        startActivity(activity, SettingActivity.class);
    }

    public static void gotoNewLoginActivity(FragmentActivity activity) {
        Intent intent = new Intent(activity,LoginActivity.class);
//        进入登录界面，并清掉该栈task中所有的activity,进入登录界面，finish只是关闭当前界面，一次back会回到倒数第二次页面
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(activity,intent);
    }
}
