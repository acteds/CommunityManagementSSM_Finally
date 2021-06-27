<%--@elvariable id="top" type="java.lang.String"--%>
<%--@elvariable id="user" type="java.util.List"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>所有社团信息</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/List.css" rel="stylesheet">
    <style type="text/css">.c1 {
        width: 960px;
    }</style>
</head>
<body>
<center>
<div class="c1"><%--@elvariable id="name" type="java.lang.String"--%>
	<h2>所有社团信息</h2><%--@elvariable id="bar" type="java.lang.String"--%>
	<table border="1" class="table table-striped table-bordered table-condensed table-hover">
		<tr><%--@elvariable id="sum" type="int"--%>
			<td colspan="${fn:length(top)-2+1}" align="left">当前用户:&nbsp;<span class="fonts2">${name}</span>
				&nbsp;权限:${user.rank}&nbsp;<a href="Logout">注销</a>&nbsp;共${sum}个社团&nbsp;<a href="courseList">返回</a>
			</td>
			<td align="center" colspan="2">${bar}</td>
		</tr>
		<tr class="fonts3"><%--@elvariable id="translate" type="java.util.Map"--%>
		<c:forEach var="temp" items="${top}">
			<td>${translate.get(temp)}</td>
		</c:forEach>
			<td>操作</td>
		</tr><%--@elvariable id="list" type="java.util.List"--%>
		<c:forEach var="temp" items="${list}">
			<tr>
			<c:forEach var="key" items="${top}">
				<td>${temp.get(key)}</td>
			</c:forEach>
				<td align="center"><a href="assnMore?aid=${temp.get('aid')}">详情</a>
				<c:if test="${user.rank =='学生'}">
					<c:if test="${user.aid == temp.get('aid')}" var="if1_1" scope="page">
						|<span class="fonts2">当前社团</span>
					</c:if>
					<c:if test="${not if1_1}"><%--@elvariable id="cidmap" type="java.util.Map"--%>
						|<a onClick="return assnJoin(${cidmap.size()==0?0:cidmap.size()})" href="assnJoin?aid=${temp.get('aid')}">更换社团</a>
					</c:if>
				</c:if>
				<c:if test="${user.rank == '管理员'}">
					|<a href="assnChange?aid=${temp.get('aid')}">修改</a>|<a
					onClick="return deleteDemo('${temp.get('assn_name')}',${temp.get(top[5])},${temp.get(top[6])})"
					href="assnDel?aid=${temp.get('aid')}">删除</a>
				</c:if>
				</td>
			</tr>
		</c:forEach>
		<c:if test="${user.rank == '管理员'}" var="if1" scope="page">
			<tr><td colspan="${fn:length(top)+1}" align="center"><a class="fonts1" href="assnInsert">添加社团</a></td></tr>
		</c:if>
		<c:if test="${not if1}">
			<tr><td colspan="${fn:length(top)+1}" align="center">&nbsp;</td></tr>
		</c:if>
	</table>
</div>
</center>
<script type="text/javascript">
    //删除检测
    function deleteDemo(name, n1, n2) {
        var sum = "";
        var flag = 0;
        if (n1 != 0) {
            sum += '课程数量不为0,无法删除' + name + '\n';
            flag = 1;
        }
        if (n2 != 0) {
            sum += '学生数量不为0,无法删除' + name + '\n';
            flag = 1;
        }
        if (flag == 1) {
            alert(sum);
            return false;
        }
        if (window.confirm('你确定要删除' + name + '吗？')) {//alert("确定");
            return true;
        } else {//alert("取消");
            return false;
        }
    }
    //社团选课检测
    function assnJoin(size) {
        if (size > 0) {
            alert('你在当前的社团有选课,无法更换社团');
            return false;
        }
        return true;
    }
</script>
<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>