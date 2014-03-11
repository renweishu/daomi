package com.example.nutswall;

import nutswall.db.util.DbService;
import nutswall.entity.User;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author chao11.ma
 * <b>一个用户登录验证的demo
 */
public class LoginActivity extends Activity {


	/**
	 * 一个用户登录的后台验证异步任务
	 */
	private UserLoginTask mAuthTask = null;

	// 用户名（邮箱）
	private String mEmail;
	// 密码
	private String mPassword;

	// 用户名框
	private EditText mEmailView;
	//　密码框
	private EditText mPasswordView;
	// 包含整个用户登录的form框
	private View mLoginFormView;
	// 包含的进度条和消息验证文本框
	private View mLoginStatusView;
	// 消息验证提示框
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		// 获取默认的初期化文字
		mEmailView = (EditText) findViewById(R.id.email);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});
		// 包含整个用户登录的form框
		mLoginFormView = findViewById(R.id.login_form);
		// 包含的进度条和消息验证文本框
		mLoginStatusView = findViewById(R.id.login_status);
		// 消息验证提示框
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		// 点击登录按钮
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}


	/**
	 * <h1>关于用户登录验证的操作
	 */
	public void attemptLogin() {
		// 建立的用户登录验证任务
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.　重置消息
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// 获取界面输入数据
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 6) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (mEmail.length()<6) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// 如果check没有通过   重新定位文本框焦点
			focusView.requestFocus();
		} else {
			// 如果单项目check通过 就开始 进行后台登录验证
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			// 开始显示进度条
			showProgress(true);
			// 建立一个后台验证任务
			mAuthTask = new UserLoginTask();
			// 传入输入的用户名和密码
			mAuthTask.execute(mEmail,mPassword);
		}
	}

	/**
	 * 显示进度条
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}


	/**
	 * @author chao11.ma
	 * 验证用户的正确性
	 */
	public class UserLoginTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {

			String username=params[0];
			String password =params[1];
			// 从数据库获得user
			User user = new User();

			try {
				Thread.sleep(2000);
				// 连接数据库，获取用户数据
				DbService DbService = new DbService(getApplicationContext());
				user= DbService.getUser(username);

			} catch (Exception e) {
				return false;
			}
			// 判断用户数据是否正确
			if (user.getUsername()==null || user.getPassword()==null) {
				return false;
			} else if (!user.getPassword().equals(password)) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				LoginActivity.this.finish();
				// 跳转到成功登录页面
				Toast.makeText(getApplicationContext(), "登录成功",Toast.LENGTH_LONG).show();
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);

			} else {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
