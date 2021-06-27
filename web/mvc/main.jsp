<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>社团选课管理系统</title>

<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>

<!--阿里图标库-->
<link rel="stylesheet" type="text/css" href="https://at.alicdn.com/t/font_2267517_7isks1rcss9.css?1583918713" />

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

</head>
<body>
<%--@elvariable id="name" type="java.lang.String"--%>
<%--@elvariable id="user" type="java.util.Map"--%>
<div class="body_con">
    <div class="body_top">
        <div style="display: inline-block;">社团选课管理系统</div>
        <%--<div style="display: inline-block;font-size: 14px;">当前用户:&nbsp;<span style="color: #9de614;">${user.realname!=null?user.realname:user.t_realname}</span>&nbsp;权限:${user.rank}</div>--%>
    </div>
    <div class="body_left">
        <ul class="body_left_list">
            <li>
                <label>
                    <span>个人中心</span>
                    <i class="iconfont icon-xiangqing1"></i>
                    <a href="javascript:;"></a>
                </label>
                <ul>
                    <li>
                        <label>
                            <i class="iconfont icon-xiugai"></i>
                            <span>修改信息</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="userChange" target="iframe_a"></a>
                        </label>
                    </li>
                    <li>
                        <label>
                            <i class="iconfont icon-zhuxiaologout11"></i>
                            <span>注销</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="Logout"></a>
                        </label>
                    </li>
                    <c:if test="${user.rank=='学生'}">
                    <li>
                        <label>
                            <i class="iconfont icon-xiangqing"></i>
                            <span>所在社团信息</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="assnMore" target="iframe_a"></a>
                        </label>
                    </li>
                    </c:if>
                </ul>
            </li>
            <li>
                <label>
                    <span>子系统</span>
                    <i class="iconfont icon-xiangqing1"></i>
                    <a href="javascript:;"></a>
                </label>
                <ul>
                    <li>
                        <label>
                            <i class="iconfont icon-kecheng"></i>
                            <span>课程管理</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="courseList" target="iframe_a"></a>
                        </label>
                    </li>
                    <c:if test="${user.rank=='管理员'}" var="if1" scope="page">
                    <li>
                        <label>
                            <i class="iconfont icon-shetuan"></i>
                            <span>社团管理</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="assnList" target="iframe_a"></a>
                        </label>
                    </li>
                    <li>
                        <label>
                            <i class="iconfont icon-xuesheng"></i>
                            <span>学生账号管理</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="userList?t=1" target="iframe_a"></a>
                        </label>
                    </li>
                    <li>
                        <label>
                            <i class="iconfont icon-jiaolian1"></i>
                            <span>老师账号管理</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="userList?t=2" target="iframe_a"></a>
                        </label>
                    </li>
                    </c:if>
                    <c:if test="${not if1}">
                    <li>
                        <label>
                            <i class="iconfont icon-shetuan"></i>
                            <span>查看所有社团</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="assnList" target="iframe_a"></a>
                        </label>
                    </li>
                    </c:if>
                </ul>
            </li>
        </ul>
    </div>
    <div class="body_right">
        <iframe src="courseList" width="100%" height="100%" name="iframe_a" style="border: 0"></iframe>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
