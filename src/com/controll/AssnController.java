package com.controll;

import com.dao.MySql;
import com.aotmd.Tools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 社团控制器
 *
 * @author aotmd
 */
@Controller
public class AssnController {
    private MySql ms;
    public AssnController(MySql ms) {this.ms = ms;}
    @RequestMapping("/assnInsert")
    public String assnInsert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //noinspection unchecked
        LinkedHashMap<String,Object> user=(LinkedHashMap<String,Object>)request.getSession().getAttribute("user");
        if("管理员".equals(user.get("rank").toString())){
            ms.setSql("SELECT * FROM `t_assn_info`");
        }else{
            response.getWriter().print("<script>alert('非法访问!');window.location='Logout'</script>");
        }
        //获取社团信息
        LinkedHashMap<String, Object> assnInsert=ms.runList().get(0);
        //获取数据库字段信息,删除主键字段
        String[] top=Tools.delString(ms.getTop(), ms.getTop()[0]);
        System.out.println(Arrays.toString(top));
        request.setAttribute("assnMore",assnInsert);
        request.getSession().setAttribute("top",top);
        return "assnInsert";
    }
    @RequestMapping("/AssnInsert")
    public void assnInsert(HttpServletRequest request, HttpServletResponse response,String assn_name) throws IOException {
        HttpSession session = request.getSession();
        //noinspection unchecked ------------是否管理员--------------------------------------------------------
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
        if (!("管理员".equals(user.get("rank").toString()))) {
            response.getWriter().print("<script>alert('你不是管理员');window.location='assnList'</script>");
            return;
        }
        //-----------------------------------判断是否有传值----------------------------------------------------------
        if (assn_name == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='assnList'</script>");
            return;
        }
        //-----------------------------------判断社团名称是否重复------------------------------------------------------
        int i =ms.setSql("SELECT * FROM `t_assn_info` where assn_name=?").set(assn_name).runList().size();
        if (i > 0) {
            response.getWriter().print("<script>alert('社团名字重复');window.location='assnList'</script>");
            return;
        }
        //-------------------------------------------添加--------------------------------------------------------
        String[] top = (String[]) session.getAttribute("top");
        ms.setSql("INSERT INTO t_assn_info VALUES(0,?,?,?,?,?,?,?)");
        for (String s : top) {
            ms.set(request.getParameter(s));
        }
        //调试方法
        System.out.println('\n'+ms.getSql());
        if (ms.run() > 0) {
            response.setHeader("refresh", "0;URL=assnList");
        } else {
            response.getWriter().print("<script>alert('添加失败');window.location='assnList'</script>");
        }
    }
    @RequestMapping("/assnChange")
    public String assnChange(HttpServletRequest request, HttpServletResponse response,String aid) throws IOException {
        HttpSession session=request.getSession();
        //noinspection unchecked
        LinkedHashMap<String,Object> user=(LinkedHashMap<String,Object>)session.getAttribute("user");
        if(aid!=null&& "管理员".equals(user.get("rank").toString())){
            ms.setSql("SELECT * FROM `t_assn_info` where aid=?").set(Integer.parseInt(aid));
        }else{
            response.getWriter().print("<script>alert('非法访问!');window.location='Logout'</script>");
        }
        //获取社团信息
        LinkedHashMap<String, Object> assnChange=ms.runList().get(0);
        //获取数据库字段信息,删除主键字段
        String []top=Tools.delString(ms.getTop(), ms.getTop()[0]);
        session.setAttribute("assnChange", assnChange);
        session.setAttribute("top", top);
        return "assnChange";
    }
    @RequestMapping("/AssnChange")
    public void assnChange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        // ---------------------------------------是否管理员--------------------------------------------------------
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
        if (!("管理员".equals(user.get("rank").toString()))) {
            response.getWriter().print("<script>alert('你不是管理员');window.location='assnList'</script>");
            return;
        }
        //-----------------------------------判断是否有传值----------------------------------------------------------
        String assn_name = request.getParameter("assn_name");
        if (assn_name == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='assnList'</script>");
            return;
        }
        //-----------------------------------判断社团名称是否重复------------------------------------------------------
        //noinspection unchecked 社团原信息
        Map<String, Object> map = (Map<String, Object>) session.getAttribute("assnChange");
        String assn_name0 = (String) map.get("assn_name");
        if (!(assn_name.equals(assn_name0))) {
            int i = ms.setSql("SELECT * FROM `t_assn_info` where assn_name=?").set(assn_name).runList().size();
            if (i > 0) {
                response.getWriter().print("<script>alert('社团名字重复');window.location='assnList'</script>");
                return;
            }
        }
        //-------------------------------------------修改--------------------------------------------------------
        String[] top = (String[]) session.getAttribute("top");
        int aid = (int) map.get("aid");
        ms.setSql("UPDATE t_assn_info SET assn_name=?,assn_founder=?,assn_leader=?,"
                + "assn_time=?,assn_content=?,assn_brief=?,assn_address=? WHERE aid=?");
        for (String string : top) {
            ms.set(request.getParameter(string));
        }
        ms.set(aid);
        //调试方法
        System.out.println('\n'+ms.getSql());
        if ( ms.run() > 0) {
            response.setHeader("refresh", "0;URL=assnList");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.location='assnList'</script>");
        }
    }
    @RequestMapping("/assnDel")
    public void assnDel(HttpServletRequest request, HttpServletResponse response,String aid) throws IOException {
        HttpSession session = request.getSession();
        //noinspection unchecked --------------是否管理员--------------------------------------------------------
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
        if (!("管理员".equals(user.get("rank").toString()))) {
            response.getWriter().print("<script>alert('你不是管理员');window.location='assnList'</script>");
            return;
        }
        //-----------------------------------判断是否有传值-----------------------------------------------------------------
        if (aid == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='assnList'</script>");
            return;
        }
        //--------------------------------判断是否为由当前网页链接访问-----------------------------------------------------------
        Map<String, Object> assn = null;
        boolean notWeb = true;
        //noinspection unchecked 所有社团信息
        List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) session.getAttribute("list");
        for (Map<String, Object> temp : list) {
            if (temp.get("aid").toString().equals(aid)) {
                notWeb = false;
                //找到指定map
                assn = temp;
                break;
            }
        }
        if (notWeb) {
            response.getWriter().print("<script>alert('非法访问');window.location='assnList'</script>");
            return;
        }
        //--------------------------------再次判断当前社团课程数量与学生数量为0(防止开发者工具删除js代码)---------------------------------
        boolean notZero = true;
        String[] top = (String[]) session.getAttribute("top");
        int n1 = Integer.parseInt(assn.get(top[5]).toString());
        int n2 = Integer.parseInt(assn.get(top[6]).toString());
        if (n1 == 0 && n2 == 0) {
            notZero = false;
        }
        if (notZero) {
            response.getWriter().print("<script>alert('课程数量或学生数量不为0');window.location='assnList'</script>");
            return;
        }
        //----------------------------------------删除-------------------------------------------------------------------
        ms.setSql("DELETE FROM t_assn_info where aid=?").set(Integer.parseInt(aid));
        //调试方法
        System.out.println('\n'+ms.getSql());
        if (ms.run() > 0) {
            response.setHeader("refresh", "0;URL=assnList");
        } else {
            response.getWriter().print("<script>alert('删除失败');window.location='assnList'</script>");
        }
    }
    @RequestMapping("/assnList")
    public String assnList(HttpServletRequest request) {
        //noinspection SqlRedundantOrderingDirection --查询所有社团信息---------------------------------------------------------
        ms.setSql("SELECT aid,assn_name,assn_founder,assn_leader,assn_time," +
                "(select count(cid) from t_course_info as c where  a.aid=c.aid) as numberOfCourses," +
                "(select count(sid) from t_student_info as s where  a.aid=s.aid) as numberOfStudents " +
                "from t_assn_info as a order by aid asc limit ?,?");
        ms.runPagination(request, "/assnList", 10);
        String []top=ms.getTop();
        String name = null;
        //noinspection unchecked
        Map<String, Object> user = (Map<String, Object>) request.getSession().getAttribute("user");
        String rank= user.get("rank").toString();
        if("学生".equals(rank)){
            top= Tools.delString(top,"phone");
            name=user.get("realname").toString();
        }else if("管理员".equals(rank)|| "老师".equals(rank)){
            name=user.get("t_realname").toString();
        }
        request.setAttribute("name",name);
        request.setAttribute("sum", ms.getSum());
        request.getSession().setAttribute("top", top);
        return "assnList";
    }
    @RequestMapping("/assnJoin")
    public void assnJoin(HttpServletRequest request, HttpServletResponse response,String aid) throws IOException {
        HttpSession session = request.getSession();
        //-----------------------------------判断是否有传值-----------------------------------------------------------------
        if (aid == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='assnList'</script>");
            return;
        }
        //noinspection unchecked -----------取学生主键--------------------------------------------------------------------
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
        if (user.get("sid") == null) {
            response.getWriter().print("<script>alert('没有学生数据!');window.location='assnList'</script>");
            return;
        }
        int sid = (int) user.get("sid");
        //noinspection unchecked ---------再次判断选课是否为0(防止开发者工具删除js代码)---------------------------------------------
        int cidMapSize = ((Map<String, String>) session.getAttribute("cidmap")).size();
        if (cidMapSize != 0) {
            response.getWriter().print("<script>alert('你在当前的社团有选课,无法更换社团!');window.location='assnList'</script>");
            return;
        }
        // -------------------------------修改-------------------------------------------------------------------------
        ms.setSql("UPDATE t_student_info set aid=? where sid=?").set(Integer.parseInt(aid)).set(sid);
        //调试方法
        System.out.println('\n'+ms.getSql());
        if (ms.run() > 0) {
            response.setHeader("refresh", "0;URL=Logout");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.location='assnList'</script>");
        }
    }
    @RequestMapping("/assnMore")
    public String assnMore(HttpServletRequest request,String aid){
        //noinspection unchecked
        LinkedHashMap<String,Object> user=(LinkedHashMap<String,Object>)request.getSession().getAttribute("user");
        String[] top;
        //哪来的回哪去
        String url="";
        if(aid!=null){
            url="assnList";
            ms.setSql("SELECT * FROM `t_assn_info` where aid=?").set(Integer.parseInt(aid));
        }
        else if("学生".equals(user.get("rank").toString())){
            url="courseList";
            ms.setSql("SELECT * FROM `t_assn_info` where aid=?").set(Integer.parseInt(user.get("aid").toString()));
        }
        //获取社团信息
        LinkedHashMap<String, Object> assnMore=ms.runList().get(0);
        //获取数据库字段信息,删除主键字段
        top=Tools.delString(ms.getTop(), ms.getTop()[0]);
        request.setAttribute("assnMore",assnMore);
        request.setAttribute("top",top);
        request.setAttribute("url",url);
        return "assnMore";
    }
}
