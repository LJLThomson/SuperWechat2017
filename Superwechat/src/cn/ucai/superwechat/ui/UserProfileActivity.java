package cn.ucai.superwechat.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWechatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.db.net.IModelUser;
import cn.ucai.superwechat.db.net.ModelUser;
import cn.ucai.superwechat.db.net.OnCompleteListener;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.PreferenceManager;

public class UserProfileActivity extends BaseActivity implements OnClickListener {
    private static String TAG = UserProfileActivity.class.getSimpleName();
    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    private ImageView headAvatar;
    private ImageView headPhotoUpdate;
    private ImageView iconRightArrow;
    private TextView tvNickName;
    private TextView tvUsername;
    private ProgressDialog dialog;
    private RelativeLayout rlNickName;
    IModelUser model;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);
        initView();
        initListener();
    }

    private void initView() {
        headAvatar = (ImageView) findViewById(R.id.user_head_avatar);
        headPhotoUpdate = (ImageView) findViewById(R.id.user_head_headphoto_update);
        tvUsername = (TextView) findViewById(R.id.user_username);
        tvNickName = (TextView) findViewById(R.id.user_nickname);
        rlNickName = (RelativeLayout) findViewById(R.id.rl_nickname);
        iconRightArrow = (ImageView) findViewById(R.id.ic_right_arrow);
        model = new ModelUser();
    }

    private void initListener() {
        Intent intent = getIntent();
//		得到的用户名为ljl938271,本来用户名应该是LJL938271，由此可见
//		EMClient.getInstance().getCurrentUser()得到的用户名为ljl938271
//		所以应该从内存中sharePerence中取出用户名
        String username = intent.getStringExtra("username");
        boolean enableUpdate = intent.getBooleanExtra("setting", false);
        if (enableUpdate) {
//			头像可见
            headPhotoUpdate.setVisibility(View.VISIBLE);
//			昵称可见
            iconRightArrow.setVisibility(View.VISIBLE);
//			昵称设置监听事件
            rlNickName.setOnClickListener(this);
//			头像监听事件
            headAvatar.setOnClickListener(this);
        } else {
            headPhotoUpdate.setVisibility(View.GONE);
            iconRightArrow.setVisibility(View.INVISIBLE);
        }
        if (username != null) {
//			用户名相同
            if (username.equals(EMClient.getInstance().getCurrentUser())) {
//    			tvUsername.setText(EMClient.getInstance().getCurrentUser());
                tvUsername.setText(PreferenceManager.getInstance().getCurrentUsername());
//				从环信中取出自己要的昵称
                EaseUserUtils.setUserNick(username, tvNickName);
                EaseUserUtils.setUserAvatar(this, username, headAvatar);
            } else {
                tvUsername.setText(username);
                EaseUserUtils.setUserNick(username, tvNickName);
                EaseUserUtils.setUserAvatar(this, username, headAvatar);
                asyncFetchUserInfo(username);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_avatar:
//			更新头像
                uploadHeadPhoto();
                break;
//		更新昵称
            case R.id.rl_nickname:
                final EditText editText = new EditText(this);

////			自定义对话框对话框形式更新昵称，setView(View);在环信中更新
//			new AlertDialog.Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info)
//					.setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							String nickString = editText.getText().toString();
//							if (TextUtils.isEmpty(nickString)) {
//								Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
//								return;
//							}
////							更新昵称
//							updateRemoteNick(nickString);
//						}
//					}).setNegativeButton(R.string.dl_cancel, null).show();
//			break;
//
//			自定义的方法来更新昵称
                final String username = SuperWechatHelper.getInstance().getCurrentUsernName();
                L.e(TAG, "username>>>" + username);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.setting_nickname)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String nickString = editText.getText().toString();
                                L.e(TAG, "AlertDialog>>>>" + nickString);
                                if (TextUtils.isEmpty(nickString)) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                updateNIck(username, nickString);
                            }
                        })
                        .setNegativeButton(R.string.dl_cancel, null).show();
                break;

            default:
                break;
        }

    }

    private void updateNIck(String userName, String nickName) {
//		final ProgressDialog dialog = new ProgressDialog(this);
//		dialog.show();
        model.updateNickName(this, userName, nickName, new OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                L.e(TAG, "updateNIck>>>" + result.toString());
                if (result != null) {
                    L.e(TAG, "updateNIck>>>" + result.getRetData().toString());
                    if (result.isRetMsg() && result.getRetData() != null) {
                        User user = new Gson().fromJson(result.getRetData().toString(), User.class);
//						昵称要保存到内存，通过内存可以调用
                        PreferenceManager.getInstance().setCurrentUserNick(user.getMUserNick());
//						保存到集合和数据库（自己的数据库）中，用户名相同则进行覆盖数据库，
                        SuperWechatHelper.getInstance().saveAppContact(user);
                        tvNickName.setText(user.getMUserNick());
//						发送广播形式改变其他页面昵称
                        Intent intent = new Intent(I.REQUEST_UPDATE_USER_NICK);
                        intent.putExtra("nick", user.getMUserNick());
                        sendBroadcast(intent);
                    } else {
                        if (result.getRetCode() == I.MSG_USER_SAME_NICK) {
//							dialog.dismiss();
                            CommonUtils.showLongToast("昵称未修改");
                        } else {
//							dialog.dismiss();
                            CommonUtils.showLongToast(R.string.toast_updatenick_fail);
                        }
                    }
                } else {
//					dialog.dismiss();
                    CommonUtils.showLongToast(R.string.toast_updatenick_fail);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void asyncFetchUserInfo(String username) {
        SuperWechatHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser user) {
                if (user != null) {
                    SuperWechatHelper.getInstance().saveContact(user);
                    if (isFinishing()) {
                        return;
                    }
                    tvNickName.setText(user.getNick());
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(headAvatar);
                    } else {
                        Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(headAvatar);
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }


    private void uploadHeadPhoto() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
//		列表型对话框
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        new Thread(new Runnable() {
            @Override
            public void run() {
//				从内存中取出昵称信息
                boolean updatenick = SuperWechatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
                if (UserProfileActivity.this.isFinishing()) {
                    return;
                }
                if (!updatenick) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                    .show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                                    .show();
                            tvNickName.setText(nickName);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//			图片剪切
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
    }

    public void startPhotoZoom(Uri uri) {
        L.e(TAG, "startPhotoZoom" + uri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
//		Bundle extras = picdata.getExtras();
//		if (extras != null) {
//			Bitmap photo = extras.getParcelable("data");
//			Drawable drawable = new BitmapDrawable(getResources(), photo);
//			headAvatar.setImageDrawable(drawable);
////			下载头像
////			uploadUserAvatar(Bitmap2Bytes(photo));
//			update_UserAvatar(Bitmap2Bytes(photo));
//		}
        update_UserAvatar(picdata);
    }

    private void update_UserAvatar(Intent picdata) {
        String username = PreferenceManager.getInstance().getCurrentUsername();
        File file = savaBitmapFile(picdata);
        L.e(TAG, "update_UserAvatar:>>>>>" + file);
        if (file == null) {
            return;
        }
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        model.updateAvator(this, username, file, new OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                L.e(TAG, "updateAvator:>>>>>" + result);
                if (result != null) {
                    User user = new Gson().fromJson(result.getRetData().toString(), User.class);
                    if (user != null) {
//						保存到集合和数据库中
                        SuperWechatHelper.getInstance().saveAppContact(user);
                        EaseUserUtils.setAppUserAvatar(UserProfileActivity.this, user.getMUserName(), headAvatar);
                        CommonUtils.showShortToast(R.string.toast_updatenick_success);
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                CommonUtils.showShortToast(R.string.toast_updatenick_fail);
            }
        });
    }

    private File savaBitmapFile(Intent picdata) {
        String username = PreferenceManager.getInstance().getCurrentUsername();
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            String imagePath = EaseImageUtils.getImagePath(username + I.AVATAR_SUFFIX_PNG);
            File file = new File(imagePath);
            L.e("file path=" + file.getAbsolutePath());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }

    private void uploadUserAvatar(final byte[] data) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String avatarUrl = SuperWechatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (avatarUrl != null) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }).start();

        dialog.show();
    }


    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
