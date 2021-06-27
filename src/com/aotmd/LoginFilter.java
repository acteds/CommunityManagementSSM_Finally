package com.aotmd;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截器,全局拦截未登陆
 * @author aotmd
 */
public class LoginFilter implements HandlerInterceptor {
	/**
	 *preHandle方法在控制器的处理请求方法前执行
	 * @param request 请求对象
	 * @param response 应答对象
	 * @param handler 处理器
	 * @return 返回true表示继续向下执行,返回false表示中断后续操作
	 * @throws IOException IO流异常
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		/*URL网址*/
		String requestUri = request.getRequestURI();
		/*项目名称*/
		String ctxPath = request.getContextPath();
		/*排除拦截的网址*/
		String[] path = { "/login", "/Login", "/reg", "/Reg","/getVerifyCode"};
		/*排除的扩展名*/
		String[] extensionName = { ".css", ".js", ".png", ".jpg" };
		/*设置网址取不到点号时的默认值*/
		int uriLast = requestUri.lastIndexOf(".") != -1 ? requestUri.lastIndexOf(".") : 0;

		for (String value : extensionName) {
			if (requestUri.substring(uriLast).equals(value)) {
				System.out.print("在排除的扩展名内");
				return true;
			}
		}
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		for (String s : path) {
			if (requestUri.substring(ctxPath.length()).equals(s)) {
				System.out.print("网址在排除拦截的网址已通过拦截器 ");
				return true;
			}
		}
		if (request.getSession().getAttribute("user") != null) {
			System.out.print("正常通过拦截器 ");
			return true;
		} else {
			response.getWriter().print("<script>alert('你没有登录!');window.location='"+ctxPath+"/'</script>");
			return false;
		}
	}
}