package com.wy.config;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.hbase.thirdparty.com.google.common.cache.Cache;
import org.apache.hbase.thirdparty.com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wy.enums.SystemRole;
import com.wy.model.Token;
import com.wy.model.User;
import com.wy.service.AuthService;
import com.wy.service.UserService;
import com.wy.util.ContextUtil;

/**
 * @apiNote 登录拦截验证
 * @author ParadiseWY
 * @date 2020年2月10日 下午12:21:25
 */
@Configuration
public class SecurityInterceptor implements HandlerInterceptor {

	@Autowired
	@Qualifier("authServiceImpl")
	private AuthService authService;

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	private Cache<String, User> userCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getRequestURI().equals("/loginPost")) {
			return true;
		}
		String token = "";
		HttpSession httpSession = request.getSession();
		if (httpSession.getAttribute(ContextUtil.SESSION_KEY) != null) {
			token = httpSession.getAttribute(ContextUtil.SESSION_KEY).toString();
		} else {
			token = request.getHeader("X-Auth-Token");
		}
		Token token2 = authService.getTokenInfo(token);
		if (token2 == null) {
			String url = "/loginPost";
			response.sendRedirect(url);
			return false;
		}
		User present = userCache.getIfPresent(token2);
		if (present == null) {
			present = userService.getUserInfo(token);
			if (present == null) {
				present = new User();
				present.setSystemRole(SystemRole.VISITOR);
				present.setUsername("visitor");
				present.setDetail("this is a visitor");
				present.setUserId(token);
			}
			userCache.put(token2.getToken(), present);
		}
		ContextUtil.setUser(present);
		return true;
	}
}