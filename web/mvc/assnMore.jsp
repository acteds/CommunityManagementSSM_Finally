<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>社团信息显示</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media.css">
</head>
<body><%--@elvariable id="assnMore" type="java.util.Map"--%>
<center><%--@elvariable id="translate" type="java.util.Map"--%>
<div class="message">
    <table width="317"><%--@elvariable id="top" type="java.lang.String"--%>
        <tr><td colspan="2" class="title">${assnMore.assn_name}信息显示</td></tr>
        <tr><td width="100" align="right"></td><td width="217"></td></tr>
        <c:forEach var="st" items="${top}">
            <c:if test="${st== 'assn_content'||st=='assn_brief'||st=='assn_address'}" var="if1" scope="page">
                <tr>
                    <td align="right">${translate.get(st)}:</td>
                    <td><textarea readonly="readonly" style="width: 166px;">${assnMore.get(st)}</textarea></td>
                </tr>
            </c:if>
            <c:if test="${not if1}">
                <tr>
                    <td align="right">${translate.get(st)}:</td>
                    <td><input name="${st}" type="text" readonly="readonly" style="width: 167px;" value="${assnMore.get(st)}"></td>
                </tr>
            </c:if>
        </c:forEach>
        <tr><%--@elvariable id="url" type="java.lang.String"--%>
            <td colspan="2" align="center" style="padding-top: 10px">
                <a href="${url}"><input name="button" type="button" value="确定" class="botton"></a>
            </td>
        </tr>
    </table>
</div>
</center>
</body>
</html>