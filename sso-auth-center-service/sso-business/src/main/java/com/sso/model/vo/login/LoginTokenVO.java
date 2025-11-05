package com.sso.model.vo.login;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录token 返回
 * @author 程序员小强
 */
@Data
public class LoginTokenVO implements Serializable {

	private static final long serialVersionUID = 226785375056951167L;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 用户密码
	 */
	private String password;

	/**
	 * 请求标识
	 */
	private String requestId;

	/**
	 * token
	 */
	private String token;

	public LoginTokenVO(String username, String requestId, String token,String password) {
		this.username = username;
		this.requestId = requestId;
		this.token = token;
		this.password = password;
	}

}
