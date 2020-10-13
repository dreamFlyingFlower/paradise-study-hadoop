package com.wy.crl;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.wy.enums.SystemRole;
import com.wy.model.Auth;
import com.wy.model.Token;
import com.wy.model.User;
import com.wy.result.Result;
import com.wy.result.ResultException;
import com.wy.service.AuthService;
import com.wy.service.OperationAccessService;
import com.wy.service.UserService;
import com.wy.util.ContextUtil;

@RestController
@RequestMapping("user")
public class UserCrl {

	@Autowired
	private OperationAccessService operationAccessService;

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("authServiceImpl")
	private AuthService authService;

	@PostMapping("loginPost")
	public Object loginPost(String username, String password, HttpSession session) {
		if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
			throw new ResultException("用户名或密码不能为空");
		}
		User user = operationAccessService.checkLogin(username, password);
		if (user != null) {
			session.setAttribute(ContextUtil.SESSION_KEY, user.getUserId());
			return Result.ok();
		} else {
			return Result.error();
		}
	}

	@GetMapping("logout")
	public Object logout(HttpSession session) {
		session.removeAttribute(ContextUtil.SESSION_KEY);
		return Result.ok();
	}

	@PostMapping("addUser")
	public Result<?> createUser(@RequestParam String username, @RequestParam String password,
			@RequestParam(required = false, defaultValue = "") String detail,
			@RequestParam(required = false, defaultValue = "USER") String role) {
		User currentUser = ContextUtil.getCurrentUser();
		if (operationAccessService.checkSystemRole(currentUser.getSystemRole(), SystemRole.valueOf(role))) {
			User user = new User(username, password, detail, SystemRole.valueOf(role));
			userService.addUser(user);
			return Result.ok();
		}
		return Result.error();
	}

	@GetMapping("deleteUser")
	public Result<?> deleteUser(String userId) {
		User currentUser = ContextUtil.getCurrentUser();
		if (operationAccessService.checkSystemRole(currentUser.getSystemRole(), userId)) {
			userService.deleteUser(userId);
			return Result.ok();
		}
		return Result.error();
	}

	@PostMapping("createToken")
	public Result<?> createToken(@RequestParam(required = false, defaultValue = "7") String expireTime,
			@RequestParam(required = false, defaultValue = "true") String isActive) {
		User currentUser = ContextUtil.getCurrentUser();
		if (!currentUser.getSystemRole().equals(SystemRole.VISITOR)) {
			Token token = new Token(currentUser.getUsername());
			token.setExpireTime(Integer.parseInt(expireTime));
			token.setActive(Boolean.parseBoolean(isActive));
			authService.addToken(token);
			return Result.ok();
		}
		return Result.error();
	}

	@GetMapping("deleteToken")
	public Result<?> deleteToken(String token) {
		User currentUser = ContextUtil.getCurrentUser();
		if (operationAccessService.checkTokenOwner(currentUser.getUsername(), token)) {
			authService.deleteToken(token);
			return Result.ok();
		}
		return Result.error();
	}

	@PostMapping("addAuth")
	public Result<?> addAuth(@RequestBody Auth auth) {
		User currentUser = ContextUtil.getCurrentUser();
		if (operationAccessService.checkBucketOwner(currentUser.getUsername(), auth.getBucketName())
				&& operationAccessService.checkTokenOwner(currentUser.getUsername(), auth.getTargetToken())) {
			authService.addAuth(auth);
			return Result.ok();
		}
		return Result.error();
	}

	@GetMapping("deleteAuth")
	public Result<?> deleteAuth(@RequestParam String bucket, @RequestParam String token) {
		User currentUser = ContextUtil.getCurrentUser();
		if (operationAccessService.checkBucketOwner(currentUser.getUsername(), bucket)
				&& operationAccessService.checkTokenOwner(currentUser.getUsername(), token)) {
			authService.deleteAuth(bucket, token);
			return Result.ok();
		}
		return Result.error();
	}
}