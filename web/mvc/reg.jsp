<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >
<html>
<head><%--@elvariable id="title" type="String"--%>
    <title>社团选课管理系统${title}注册</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media.css">
</head>
<body>
<center>
<div class="message">
	<form name="form1" action="Reg" method="post" onsubmit="return notNull(this);">
	<table width="317">
		<tr><td colspan="2" class="title">社团选课管理系统${title}注册</td></tr>
		<tr><td width="100" align="right"></td><td width="217"></td>
		</tr><%--@elvariable id="top" type="java.lang.String"--%>
		<c:forEach var="st" items="${top}">
			<c:if test="${st=='password'||st=='t_password'}" var="if1" scope="page">
				<tr><%--@elvariable id="translate" type="java.util.Map"--%>
					<td align="right">${translate.get(st)}:</td>
					<td><input name="${st}" type="password"></td>
				</tr>
				<tr>
					<td align="right">确认密码:</td>
					<td><input name="${st}2" type="password"></td>
				</tr>
			</c:if>
			<c:if test="${st=='aid'}" var="if2" scope="page">
				<tr><%--@elvariable id="aid" type="java.util.List"--%>
					<td align="right">所属社团:</td>
					<td><select name="${st}" id="sort" size="1">
						<c:forEach var="aidMap" items="${aid}">
							<option value='${aidMap.aid}'>${aidMap.assn_name}</option>
						</c:forEach>
					</select>
					</td>
				</tr>
			</c:if>
			<c:if test="${st=='sex'||st=='t_sex'}" var="if3" scope="page">
				<tr><%--@elvariable id="translate" type="java.util.Map"--%>
					<td align="right">${translate.get(st)}:</td>
					<td><input name="${st}" type="radio" value="男" checked>男
						<input name="${st}" type="radio" value="女">女
					</td>
				</tr>
			</c:if>
			<c:if test="${not if1 && not if2 && not if3}">
				<tr><%--@elvariable id="translate" type="java.util.Map"--%>
					<td align="right">${translate.get(st)}:</td>
					<td><input name="${st}" type="text"></td>
				</tr>
			</c:if>
		</c:forEach>
		<tr>	<%--@elvariable id="returnUrl" type="String"--%>
			<td colspan="2" align="center" style="padding-top: 10px">
				<input name="start" type="submit" value="注册" class="botton">
				<input name="reset" type="reset" value="重置" class="botton">
				<a href="${returnUrl}"><input name="button" type="button" value="返回" class="botton"></a>
			</td>
		</tr>
	</table>
</form>
</div>
</center>
<script type="text/javascript">
	//判断是否为空(动态)
	function notNull() {
		var flag = 0, sum = "";<%--@elvariable id="top" type="java.lang.String"--%>
		<c:forEach var="st" items="${top}">
		<c:if test="${st=='password'||st=='t_password'}" var="if1" scope="page">
		if (form1.${st}.value == "") {<%--@elvariable id="translate" type="java.util.Map"--%>
			sum += "${translate.get(st)}不能为空\n";
			form1.${st}.focus();
			flag = 1;
		}
		if (form1.${st}${2}.value == "") {
			sum += "确认密码不能为空\n";
			form1.${st}${2}.focus();
			flag = 1;
		}
		if (form1.${st}.value != form1.${st}${2}.value) {
			sum += "密码不一致\n";
			form1.${st}.focus();
			flag = 1;
		}</c:if>
		<c:if test="${st=='aid'}" var="if2" scope="page">
		if (form1.${st}.value == "") {
			sum += "所属社团不能为空\n";
			form1.${st}.focus();
			flag = 1;
		}</c:if><%--@elvariable id="translate" type="java.util.Map"--%>
		<c:if test="${not if1 && not if2}">
		if (form1.${st}.value == "") {
			sum += "${translate.get(st)}不能为空\n";
			form1.${st}.focus();
			flag = 1;
		}</c:if>
		</c:forEach>
		if (flag == 1) {
			window.alert(sum);
			return false;
		} else return true;
	}
</script>
</body>
</html>