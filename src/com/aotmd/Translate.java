package com.aotmd;

import java.util.HashMap;
import java.util.Map;

/**
 * 翻译器
 * @author aotmd
 */
public class Translate {
	public static Map<String, String> getTranslate() {
		Map<String, String> map= new HashMap<>(16);
		map.put("aid","社团编号");
		map.put("assn_name","社团名称");
		map.put("assn_founder","社团创始人");
		map.put("assn_leader","现任社长");
		map.put("assn_time","社团成立时间");
		map.put("assn_content","社团内容");
		map.put("assn_brief","社团简介");
		map.put("assn_address","社团地址");
		map.put("cid","课程编号");
		map.put("c_name","课程名称");
		map.put("c_time","开课学期");
		map.put("c_credit","学分");
		map.put("c_place","授课地点");
		map.put("c_selected","已选人数");
		map.put("c_amount","课程总人数");
		map.put("sid","学生编号");
		map.put("username","学生账号");
		map.put("password","学生密码");
		map.put("realname","学生姓名");
		map.put("sex","学生性别");
		map.put("year","学生年龄");
		map.put("faculty","所属院系");
		map.put("phone","电话号码");
		map.put("tid","教师编号");
		map.put("t_username","教师账号");
		map.put("t_password","教师密码");
		map.put("t_realname","教师姓名");
		map.put("t_sex","教师性别");
		map.put("t_age","教师年龄");
		map.put("t_phone","电话号码");
		map.put("eid","选课编号");
		map.put("numberOfStudents", "学生数量");
		map.put("numberOfCourses", "课程数量");
		return map;
	}

}
