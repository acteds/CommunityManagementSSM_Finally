<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>社团选课管理系统</title>
<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/List.css" rel="stylesheet">
</head>
<body>
<center><%--@elvariable id="title" type="java.lang.String"--%>
<div style="width:960px;">
<h2>${title}</h2>
<table border="1" class="table table-striped table-bordered table-condensed table-hover">
	<tr><%--@elvariable id="listsum" type="int"--%>
		<%--@elvariable id="name" type="java.lang.String"--%>
		<%--@elvariable id="aidFilter" type="java.lang.String"--%>
		<%--@elvariable id="aidkey" type="java.util.Map"--%>
		<%--@elvariable id="aidmap" type="java.util.Map"--%>
		<%--@elvariable id="cidmap" type="java.util.Map"--%>
		<%--@elvariable id="user" type="java.util.Map"--%>
		<td colspan="7" align="left">当前用户:&nbsp;<span class="fonts2">${name}</span>&nbsp;权限:${user.rank}&nbsp;<%--<a
				href="Logout">注销</a>&nbsp;<a href="userChange">修改信息</a>&nbsp;--%>
			<c:if test="${user.rank=='学生'}">
				当前所属社团:<a href='assnMore'>${aidmap.get(user.get('aid').toString())}</a>&nbsp;已选${cidmap.size()==0?0:cidmap.size()}门课程,
			</c:if>&nbsp;共${listsum}门课程
			<c:if test="${user.rank=='管理员'}" var="if1" scope="page">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 筛选:
				<select name="aid" style="position: relative;top:1px;" id="aid" size="1">
					<option value="">全部</option>
					<c:forEach var="key" items="${aidkey}">
						<option value="${key}"<c:if test="${key==aidFilter}"> selected="selected"</c:if>>
							${aidmap.get(key)}
						</option>
					</c:forEach>
				</select>
				<%--<a href="assnList">社团管理</a>|<a href="userList?t=1">学生账号管理</a>|<a href="userList?t=2">教师账号管理</a>--%>
			</c:if>
			<%--<c:if test="${not if1}"><a href='assnList'>查看所有社团</a></c:if>--%>
		</td>
		<td align="center" colspan="2">${sessionScope.bar}</td>
	</tr>
	<%------------------------------------------- 第一行结束 ---------------------------------------------------%>
	<tr class="fonts3">
		<td>课程编号</td><td>课程名称</td><td>所属社团</td><td>授课老师</td>
		<td>开课学期</td><td>学分</td><td>授课地点</td><td>课程容量</td><td>操作</td>
	</tr><%--@elvariable id="list" type="java.util.List"--%>
	<c:if test="${list.size()==0}">
		<c:if test="${user.rank=='老师'}">
			<tr><td colspan='9' align='center'><b>当前没有开设任何课</b></td></tr>
		</c:if>
		<c:if test="${user.rank=='学生'}">
			<tr><td colspan='9' align='center'><b>当前所在社团没有课</b></td></tr>
		</c:if>
	</c:if>
	<c:forEach var="map" items="${list}">
		<tr><%--@elvariable id="tidmap" type="java.util.Map"--%>
			<td>${map.cid}</td>
			<td>${map.c_name}</td>
			<td>${aidmap.get(map.get('aid').toString())}</td>
			<td>${tidmap.get(map.get('tid').toString())}</td>
			<td>${map.c_time}</td>
			<td>${map.c_credit}</td>
			<td>${map.c_place}</td>
			<td>${map.c_selected}/${map.c_amount}</td>
			<!-- 对删除链接进行js确认处理 -->
			<td align="center">
			<c:if test="${user.rank=='学生'}">
				<a href='cidList?cid=${map.cid}'>查看</a>|
				<c:if test="${cidmap.get(map.get('cid').toString()) == null}" var="if1" scope="page">
					<a href='cidUp?cid=${map.cid}'>选课</a>
				</c:if>
				<c:if test="${not if1}"><a href='cidDown?cid=${map.cid}' class='fonts4'>退课</a></c:if>
			</c:if>
			<c:if test="${user.rank=='管理员' || user.rank=='老师'}">
				<a href="cidList?cid=${map.cid}">查看</a>|<a href="courseChange?cid=${map.cid}">修改</a>|<a
				onClick="return deleteDemo('${map.c_name}',${map.c_selected})" href="CourseDel?cid=${map.cid}">删除</a>
			</c:if>
			</td>
		</tr>
	</c:forEach>
	<c:if test="${user.rank=='管理员' || user.rank=='老师'}">
		<tr><td colspan="9" align="center"><a class="fonts1" href="courseInsert">添加课程</a></td></tr>
	</c:if>
	<c:if test="${user.rank=='学生'}"><tr><td colspan="9" align="center">&nbsp;</td></tr></c:if>
</table>
</div>
</center>
<script type="text/javascript">
	//删除验证
		function deleteDemo(name,sum) {
			if(sum!=0){
				alert('选课人数不为0,无法删除'+name);
				return false;
			}
			if (window.confirm('你确定要删除' + name + '吗？')) {//alert("确定");
				return true;
			} else {//alert("取消");
				return false;
			}
		}
</script>
<script type="text/javascript">
	//响应筛选
    aid.onchange = function() {
    	if(this.value!=""){
        	location.href = "courseList?aidFilter="+this.value;
        } else {
        	location.href = "courseList?aidFilter=";
        }
    };
</script>
<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>