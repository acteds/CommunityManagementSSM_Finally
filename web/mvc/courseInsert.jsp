<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >
<html>
<%--@elvariable id="top" type="java.lang.String"--%>
<%--@elvariable id="user" type="java.util.Map"--%>
<%--@elvariable id="translate" type="java.util.Map"--%>
<%--@elvariable id="aidmap" type="java.util.Map"--%>
<%--@elvariable id="tidmap" type="java.util.Map"--%>
<head>
    <title>添加课程</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media.css">
    <style type="text/css">.fonts2 {background-color: #EBEBE4;}</style>
</head>
<body>
<center>
<div class="message">
<form name="form" action="CourseInsert" method="post" onsubmit="return NotNull(this);">
    <table width="317">
        <tr><td colspan="2" class="title">添加课程</td></tr>
        <!--<tr><td width="100" align="right"></td><td width="217"></td></tr>-->
<c:forEach var="st" items="${top}">
    <c:if test="${st == 'aid'}" var="if1" scope="page">
        <tr><%--@elvariable id="aidkey" type="java.lang.String"--%>
            <td align="right">所属社团:</td>
            <td><select name="${st}" size="1">
                <c:forEach var="key" items="${aidkey}">
                    <option value='${key}'>${aidmap.get(key)}</option>
                </c:forEach>
            </select>
            </td>
        </tr>
    </c:if>
    <c:if test="${st == 'tid'}" var="if2" scope="page">
        <c:if test="${user.rank == '管理员'}" var="if2_2" scope="page">
            <tr><%--@elvariable id="tidkey" type="java.lang.String"--%>
                <td align="right">授课老师:</td>
                <td><select name="${st}" size="1">
                    <c:forEach var="key" items="${tidkey}">
                        <option value='${key}'>${tidmap.get(key)}</option>
                    </c:forEach>
                </select>
                </td>
            </tr>
        </c:if>
        <c:if test="${not if2_2}">
            <tr>
                <td align="right">授课老师:</td>
                <td><input name="${st}" class="fonts2" type="text" readonly="readonly" value="${user.t_realname}"></td>
            </tr>
        </c:if>
    </c:if>
    <c:if test="${not if1 && not if2}">
        <tr>
            <td align="right">${translate.get(st)}:</td>
            <td><input name="${st}" type="text"></td>
        </tr>
    </c:if>
</c:forEach>
        <tr>
            <td colspan="2" align="center" style="padding-top: 10px">
                <input name="start" type="submit" value="添加" class="botton">
                <input name="reset" type="reset" value="重置" class="botton">
                <a href="courseList"><input name="button" type="button" value="返回" class="botton"></a>
            </td>
        </tr>
    </table>
</form>
</div>
</center>
<script type="text/javascript">
    //判断是否为空(动态)
    function NotNull() {
        var flag = 0, sum = "";
        <c:forEach var="st" items="${top}">
        <c:if test="${st == 'aid'}" var="if1" scope="page">
        if (form.${st}.value == "") {
            sum += "所属社团不能为空\n";
            form.${st}.focus();
            flag = 1;
        }
        </c:if>
        <c:if test="${st == 'tid'}" var="if2" scope="page">
        if (form.${st}.value == "") {
            sum += "授课老师不能为空\n";
            form.${st}.focus();
            flag = 1;
        }
        </c:if>
        <c:if test="${not if1 && not if2}">
        if (form.${st}.value == "") {
            sum += "${translate.get(st)}不能为空\n";
            form.${st}.focus();
            flag = 1;
        }
        </c:if>
        </c:forEach>
        /*数据校验*/
        if (Math.round(form.c_amount.value) != form.c_amount.value) {
            sum += "${translate.c_amount}不是一个数\n";
            form.c_amount.focus();
            flag = 1;
        }
        if (form.c_amount.value < 0) {
            sum += "${translate.c_amount}不能是负数\n";
            form.c_amount.focus();
            flag = 1;
        }
        if (flag == 1) {
            window.alert(sum);
            return false;
        } else return true;
    }
</script>
</body>
</html>