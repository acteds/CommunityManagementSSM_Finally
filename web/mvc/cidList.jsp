<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><%--@elvariable id="cName" type="java.lang.String"--%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${cName}</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/List.css" rel="stylesheet">
    <style type="text/css">.c1 {width: 960px;}</style>
</head>
<body>
<%--@elvariable id="rankUp" type="int"--%>
<%--@elvariable id="sum" type="java.lang.String"--%>
<%--@elvariable id="top" type="java.lang.String"--%>
<%--@elvariable id="name" type="java.lang.String"--%>
<%--@elvariable id="bar" type="java.lang.String"--%>
<%--@elvariable id="list" type="java.util.List"--%>
<%--@elvariable id="translate" type="java.util.Map"--%>
<%--@elvariable id="user" type="java.util.Map"--%>
<center>
<div class="c1">
	<h2>${cName}</h2>
	<table border="1" class="table table-striped table-bordered table-condensed table-hover">
		<tr>
			<td colspan="${fn:length(top)-2+rankUp}" align="left">当前用户:&nbsp;<span class="fonts2">${name}</span>&nbsp;权限:${user.rank}&nbsp;<a
					href="Logout">注销</a>&nbsp;共${sum}个学生&nbsp;<a href="courseList">返回</a></td>
			<td align="center" colspan="2">${bar}</td>
		</tr>
		<tr class="fonts3">
			<c:forEach var="temp" items="${top}">
				<td>${translate.get(temp)}</td>
			</c:forEach>
			<c:if test="${rankUp == 1}">
				<td>操作</td>
			</c:if>
		</tr>
	<c:forEach var="temp" items="${list}">
		<tr>
			<c:forEach var="key" items="${top}">
				<td>${temp.get(key)}</td>
			</c:forEach>
			<c:if test="${rankUp == 1}">
				<td align="center"><a href="t_cidDown?sid=${temp.sid}" class='fonts4'>退课</a></td>
			</c:if>
		</tr>
	</c:forEach>
		<tr><td colspan="${fn:length(top)+rankUp}" align="center">&nbsp;</td></tr>
	</table>
</div>
</center>
<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>