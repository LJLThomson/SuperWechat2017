package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EasyUtils;

import cn.ucai.superwechat.SuperWechatHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.video.util.MFGT;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {

	private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.em_activity_splash);
		super.onCreate(arg0);

		RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		TextView versionText = (TextView) findViewById(R.id.tv_version);

		versionText.setText(getVersion());
//		设置动画———透明动画
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();
//		开启的子线程，但不能修改view，不然报错，也就是不能修改更新UI线程
		new Thread(new Runnable() {
			public void run() {
//				判断是否登录过，登陆过，则直接进入，没登陆过，则进入欢迎界面
				if (SuperWechatHelper.getInstance().isLoggedIn()) {
					// auto login mode, make sure all group and conversation is loaed before enter the main screen
					long start = System.currentTimeMillis();
					EMClient.getInstance().chatManager().loadAllConversations();
					EMClient.getInstance().groupManager().loadAllGroups();
					long costTime = System.currentTimeMillis() - start;
					//wait
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String topActivityName = EasyUtils.getTopActivityName(EMClient.getInstance().getContext());
					if (topActivityName != null && (topActivityName.equals(VideoCallActivity.class.getName()) || topActivityName.equals(VoiceCallActivity.class.getName()))) {
						// nop
						// avoid main screen overlap Calling Activity
					} else {
						//enter main screen
//						直接进入MainActivity
						startActivity(new Intent(SplashActivity.this, MainActivity.class));
					}
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					MFGT.startActivity(SplashActivity.this, LoginAndRegisterActivity.class);
//					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
			}
		}).start();

	}
	
	/**
	 * get sdk version
	 */
	private String getVersion() {
	    return EMClient.getInstance().VERSION;
	}
}
