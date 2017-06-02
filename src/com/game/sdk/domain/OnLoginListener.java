package com.game.sdk.domain;

/**
 * author janecer 2014-3-29上午10:12:47
 */
public interface OnLoginListener {

	/**
	 * 成功登录后的回调
	 * 
	 * @param logincallback
	 */
	void loginSuccess(LogincallBack logincallback);

	/**
	 * 登录失败的回调 有可能是用户名与密码不正确，也有可能是服务端临时出问题
	 * 
	 * @param msg
	 *            登录失败时返回的消息提示
	 */
	void loginError(LoginErrorMsg errorMsg);

	// /**
	// * 用来捕获用户在登录界面按下了返回键
	// * @param flag
	// */
	// void loginIsBack();
}
