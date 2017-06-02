package com.game.sdk.ui;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.domain.ResultInfo;
import com.game.sdk.domain.UpdateInfo;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.engin.UpdateAvaterEngin;
import com.game.sdk.engin.UpdateUserInfoEngin;
import com.game.sdk.engin.UserInfoEngin;
import com.game.sdk.net.entry.Response;
import com.game.sdk.net.listeners.Callback;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.StringUtils;
import com.game.sdk.utils.TimeUtils;
import com.game.sdk.utils.Util;
import com.game.sdk.view.CustomDialog;
import com.game.sdk.view.CustomRoundImageView;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import okhttp3.OkHttpClient;

public class UserInfoActivity extends BaseActivity implements OnClickListener {

	private TextView titleTv;

	private ImageView backIv;

	private CustomRoundImageView userHeadIv;

	private TextView nicknNameTv;

	private TextView sexTv;

	private TextView birthTv;

	private TextView mobileTv;

	private TextView emailTv;

	private TextView qqTv;

	private RelativeLayout headLayout;

	private RelativeLayout nickNameLayout;

	private RelativeLayout sexLayout;

	private RelativeLayout birthLayout;

	private RelativeLayout mobileLayout;

	private RelativeLayout emailLayout;

	private RelativeLayout qqLayout;

	private UserInfoEngin userInfoEngin;

	CustomDialog updateDialog;

	private UserInfo updateUserInfo;

	OkHttpClient mOkHttpClient;

	Bitmap photo = null;

	Bitmap headBitmap = null;

	private int year;

	private int month;

	private int day;

	private UpdateUserInfoEngin updateUserInfoEngin;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.face)) {
					Picasso.with(UserInfoActivity.this).load(GoagalInfo.userInfo.face).into(userHeadIv);
					Util.toast(UserInfoActivity.this, "修改成功");
				}

				break;
			case 2:
				if (GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.face)) {
					Picasso.with(UserInfoActivity.this).load(GoagalInfo.userInfo.face).into(userHeadIv);
				}
				
				nicknNameTv.setText(StringUtils.isEmpty(GoagalInfo.userInfo.nickName) ? GoagalInfo.userInfo.username
						: GoagalInfo.userInfo.nickName);
				String tempSex = "";
				if (GoagalInfo.userInfo.sex == 1) {
					tempSex = "男";
				} else if (GoagalInfo.userInfo.sex == 2) {
					tempSex = "女";
				} else {
					tempSex = "未知";
				}
				sexTv.setText(tempSex);
				birthTv.setText(GoagalInfo.userInfo.birth);
				mobileTv.setText(GoagalInfo.userInfo.mobile);
				emailTv.setText(GoagalInfo.userInfo.email);
				qqTv.setText(GoagalInfo.userInfo.qq);
				break;
			case 3:
				Util.toast(UserInfoActivity.this, "修改失败");
				break;
			default:
				break;
			}
		};
	};

	@Override
	public String getLayoutId() {
		return "fysdk_activity_user_info";
	}

	@Override
	public void initVars() {
		super.initVars();
	};

	@Override
	public void initViews() {
		super.initViews();

		mOkHttpClient = new OkHttpClient();
		updateDialog = new CustomDialog(this, "正在修改");
		updateUserInfo = new UserInfo();

		titleTv = findTextViewByString("title_tv");
		backIv = findImageViewByString("back_iv");
		titleTv.setText(findStringByResId("update_user_info_text"));

		userHeadIv = (CustomRoundImageView) findViewByString("user_head_iv");
		nicknNameTv = findTextViewByString("nick_name_tv");
		sexTv = findTextViewByString("sex_tv");
		birthTv = findTextViewByString("birth_tv");
		mobileTv = findTextViewByString("mobile_tv");
		emailTv = findTextViewByString("email_tv");
		qqTv = findTextViewByString("qq_tv");

		headLayout = (RelativeLayout) findViewByString("head_layout");
		nickNameLayout = (RelativeLayout) findViewByString("nick_name_layout");
		sexLayout = (RelativeLayout) findViewByString("sex_layout");
		birthLayout = (RelativeLayout) findViewByString("birth_layout");
		mobileLayout = (RelativeLayout) findViewByString("mobile_layout");
		emailLayout = (RelativeLayout) findViewByString("email_layout");
		qqLayout = (RelativeLayout) findViewByString("qq_layout");

		backIv.setOnClickListener(this);
		headLayout.setOnClickListener(this);
		nickNameLayout.setOnClickListener(this);
		sexLayout.setOnClickListener(this);
		birthLayout.setOnClickListener(this);
		mobileLayout.setOnClickListener(this);
		emailLayout.setOnClickListener(this);
		qqLayout.setOnClickListener(this);

		userInfoEngin = new UserInfoEngin(this, GoagalInfo.userInfo.userId);
		
		String birth = "";
		if(GoagalInfo.userInfo != null && !StringUtils.isEmpty(GoagalInfo.userInfo.birth)){
			birth = GoagalInfo.userInfo.birth;
		}
		
		// 初始化Calendar日历对象
		Calendar mycalendar = Calendar.getInstance();
		mycalendar.setTime(TimeUtils.getDateTime(birth, TimeUtils.DATE_FORMAT_DATE));
		year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
		month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
		day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
	};

	@Override
	public void initData() {
		new InitUserInfoTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("UserInfoActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("UserInfoActivity");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == findIdByString("back_iv")) {
			finish();
			return;
		}

		if (v.getId() == findIdByString("head_layout")) {
			updateAvatar();
			return;
		}

		if (v.getId() == findIdByString("nick_name_layout")) {
			Intent intent = new Intent(UserInfoActivity.this, CommonInfoActivity.class);
			intent.putExtra("infoType", "nickname");
			startActivityForResult(intent, 0);
			return;
		}

		if (v.getId() == findIdByString("email_layout")) {
			Intent intent = new Intent(UserInfoActivity.this, CommonInfoActivity.class);
			intent.putExtra("infoType", "email");
			startActivityForResult(intent, 0);
			return;
		}

		if (v.getId() == findIdByString("qq_layout")) {
			Intent intent = new Intent(UserInfoActivity.this, CommonInfoActivity.class);
			intent.putExtra("infoType", "qq");
			startActivityForResult(intent, 0);
			return;
		}

		if (v.getId() == findIdByString("sex_layout")) {
			Intent intent = new Intent(UserInfoActivity.this, CommonInfoActivity.class);
			intent.putExtra("infoType", "sex");
			startActivityForResult(intent, 0);
			return;
		}
		if (v.getId() == findIdByString("birth_layout")) {
			DatePickerDialog dateDialog = new DatePickerDialog(UserInfoActivity.this, DatePickerDialog.THEME_HOLO_LIGHT,
					datelistener, year, month, day);
			dateDialog.show();
			return;
		}
		if (v.getId() == findIdByString("mobile_layout")) {
			Intent intent = new Intent(UserInfoActivity.this, UserInfoPhoneUpdateActivity.class);
			startActivityForResult(intent, 0);
			return;
		}
	}

	/**
	 * 修改用户生日
	 * 
	 * @author admin
	 *
	 */
	private class UpdateUserBirthTask extends AsyncTask<String, Integer, Boolean> {
		UserInfo uInfo;

		public UpdateUserBirthTask(UserInfo uInfo) {
			this.uInfo = uInfo;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			updateUserInfoEngin = new UpdateUserInfoEngin(UserInfoActivity.this, uInfo);
			return updateUserInfoEngin.updateUserInfo();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			updateDialog.dismiss();
			if (result) {
				Util.toast(UserInfoActivity.this, "修改成功");
			} else {
				Util.toast(UserInfoActivity.this, "修改失败");
			}
		}
	}

	private DatePickerDialog.OnDateSetListener datelistener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {

			// 修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
			year = myyear;
			month = monthOfYear;
			day = dayOfMonth;
			String newBirth = year + "-" + (month + 1) + "-" + day;
			// 更新日期
			updateDate(newBirth);

			UserInfo uinfo = new UserInfo();
			uinfo.birth = newBirth;

			new UpdateUserBirthTask(uinfo).execute();
		}

		private void updateDate(String newBirth) {
			birthTv.setText(newBirth);
		}
	};

	public void updateAvatar() {
		Intent intent = new Intent(Intent.ACTION_PICK);// 打开相册
		intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
		this.startActivityForResult(intent, 1);
	}

	/**
	 * 获取用户信息
	 * 
	 * @author admin
	 *
	 */
	private class InitUserInfoTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return userInfoEngin.getUserInfo();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				// 更新用户信息
				Message msg = new Message();
				msg.what = 2;
				handler.sendMessage(msg);
			}
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK && null != data) {
			try {
				Bundle extras = data.getExtras();
				photo = null;
				if (extras != null) {
					photo = extras.getParcelable("data");
				}

				if (photo == null) {
					Uri selectedImage = data.getData();

					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
					String picturePath = "";
					if (cursor != null) {
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
						picturePath = cursor.getString(columnIndex);
						cursor.close();
					} else {
						picturePath = selectedImage.getPath();
					}
					if (requestCode == 1) {
						Intent intent = new Intent("com.android.camera.action.CROP");
						intent.setDataAndType(selectedImage, "image/*");
						intent.putExtra("crop", "true");
						intent.putExtra("aspectX", 1);
						intent.putExtra("aspectY", 1);
						intent.putExtra("outputX", 160);
						intent.putExtra("outputY", 160);
						intent.putExtra("return-data", true);
						startActivityForResult(intent, 2);
						return;
					}
					photo = BitmapFactory.decodeFile(picturePath);
				}

				if (photo == null) {
					Util.toast(this, "获取图片失败");
					return;
				}

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);// (0 -
																		// //
																		// 100)压缩文件
				byte[] byteArray = stream.toByteArray();
				String streamStr = Base64.encodeToString(byteArray, Base64.DEFAULT);
				String face = "data:image/png;base64," + streamStr;

				updateDialog.show();
				updateUserInfo.face = face;

				Map<String, String> params = new HashMap<String, String>();
				params.put("user_id", GoagalInfo.userInfo.userId);
				params.put("face", face);

				UpdateAvaterEngin.getImpl(this).agetResultInfo(UpdateInfo.class, params, new Callback<UpdateInfo>() {

					@Override
					public void onSuccess(ResultInfo<UpdateInfo> resultInfo) {
						updateDialog.dismiss();
						Message msg = new Message();

						if (resultInfo != null && resultInfo.data != null) {
							GoagalInfo.userInfo.face = resultInfo.data.face;
							msg.what = 1;
						} else {
							msg.what = 3;
						}
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(Response response) {
						updateDialog.dismiss();
						Message msg = new Message();
						msg.what = 3;
						handler.sendMessage(msg);
					}
				});

			} catch (Exception e) {
				Message msg = new Message();
				msg.what = 3;
				handler.sendMessage(msg);
			}
		}

		if (resultCode == Constants.UPDATE_SUCCESS) {
			new InitUserInfoTask().execute();
		}
	}

	/**
	 * 修改用户头像
	 * 
	 * @author admin
	 *
	 */
	private class UpdateAvaterTask extends AsyncTask<String, Integer, Boolean> {

		public UserInfo updateUserInfo;

		public UpdateAvaterTask(UserInfo uUserInfo) {
			this.updateUserInfo = uUserInfo;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			UpdateAvaterEngin acaterEngin = new UpdateAvaterEngin(UserInfoActivity.this, updateUserInfo.face);
			return acaterEngin.updateUserAvater();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			updateDialog.dismiss();
			if (result) {

				// 更新头像
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);

				Logger.msg("修改用户信息成功----");
			} else {
				Util.toast(UserInfoActivity.this, "修改失败");
				Logger.msg("修改用户信息失败----");
			}
		}
	}

}
