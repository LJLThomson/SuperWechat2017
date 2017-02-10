package cn.ucai.superwechat.ui;

import android.os.Bundle;

import cn.ucai.superwechat.R;

/**
 * Created by Administrator on 2017/2/10 0010.
 */

public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.setting_activity,new SettingsFragment())
                .commit();
    }
}
