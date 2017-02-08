package cn.ucai.superwechat.video.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.RegisterActivity;


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
}
