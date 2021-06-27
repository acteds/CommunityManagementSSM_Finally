package com.controll;

import com.dao.MySql;
import com.aotmd.Tools;
import com.aotmd.Translate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aotmd
 */
@Controller
public class CourseController {
    /**数据库操作类*/
    private MySql ms;
    public CourseController(MySql ms) {this.ms = ms;}
    @SuppressWarnings({"SqlRedundantOrderingDirection", "StringConcatenationInLoop"})
    @RequestMapping("/courseList")
    public String courseList(HttpServletRequest request,String aidFilter) {
        HttpSession session=request.getSession();
        //------------------------------对不同身份设置不同响应----------------------------------------------
        //noinspection unchecked
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
        //获取身份
        String rank = (String) user.get("rank");
        String title = null, name = null;
        if ("学生".equals(rank)) {
            //----------------------------------学生选课信息打包------------------------------------------------
            ms.setSql("SELECT cid FROM `t_elective_info` where sid=?").set(user.get("sid").toString());
            //打包学生选课信息
            Map<String, String> cidmap = new HashMap<>(16);
            for (LinkedHashMap<String, Object> temp : ms.runList()) {
                cidmap.put( temp.get("cid").toString(), "true");
            }
            session.setAttribute("cidmap", cidmap);
            ms.setSql("SELECT * FROM `t_course_info` where aid=? order by cid asc limit ?,?").set(user.get("aid").toString());
            title = "学生选课";
            name = user.get("realname").toString();
        } else if ("老师".equals(rank)) {
            ms.setSql("SELECT * FROM `t_course_info` where tid=? order by cid asc limit ?,?").set(user.get("tid").toString());
            title = "课程管理";
            name = user.get("t_realname").toString();
        } else if ("管理员".equals(rank)) {
            ms.setSql("SELECT * FROM `t_course_info` order by cid asc limit ?,?");
            title = "课程管理";
            name = user.get("t_realname").toString();
            //-------------------------------响应筛选----------------------------------------------------
            if (aidFilter != null && !("".equals(aidFilter))) {
                session.setAttribute("aidFilter", aidFilter);
                ms.setSql("SELECT * FROM `t_course_info` where aid=? order by cid asc limit ?,?").set(aidFilter);
            } else if ("".equals(aidFilter)) {
                session.removeAttribute("aidFilter");
            } else if (session.getAttribute("aidFilter") != null) {
                aidFilter = (String) session.getAttribute("aidFilter");
                ms.setSql("SELECT * FROM `t_course_info` where aid=? order by cid asc limit ?,?").set(aidFilter);
            }
            //----------------------------------------------------------------------------------------
        }
        ms.runPagination(request, "/courseList", 10);
        request.setAttribute("listsum", ms.getSum());
        request.setAttribute("title",title);
        request.setAttribute("name",name);
        //------------------------------重新打包社团信息---------------------------------------------------
        List<LinkedHashMap<String, Object>> aidlist = ms.setSql("SELECT aid,assn_name FROM `t_assn_info`").runList();
        LinkedHashMap<String, String> aidmap = new LinkedHashMap<>(16);
        String s = "";
        for (LinkedHashMap<String, Object> temp : aidlist) {
            aidmap.put(temp.get("aid").toString(), temp.get("assn_name").toString());
            s += temp.get("aid").toString() + " ";
        }
        String[] aidkey = s.split(" ");
        session.setAttribute("aidmap", aidmap);
        session.setAttribute("aidkey", aidkey);
        //------------------------------重新打包教师信息----------------------------------------------------
        List<LinkedHashMap<String, Object>> tidlist = ms.setSql("SELECT tid,t_realname FROM `t_teacher_info`").runList();
        LinkedHashMap<String, String> tidmap = new LinkedHashMap<>(16);
        String s2 = "";
        for (LinkedHashMap<String, Object> temp : tidlist) {
            tidmap.put(temp.get("tid").toString(), temp.get("t_realname").toString());
            s2 += temp.get("tid").toString() + " ";
        }
        String[] tidkey = s2.split(" ");
        session.setAttribute("tidmap", tidmap);
        session.setAttribute("tidkey", tidkey);
        return "courseList";
    }
    @RequestMapping("/courseInsert")
    public String courseInsert(HttpServletRequest request,HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        //noinspection unchecked
        LinkedHashMap<String,Object> user= (LinkedHashMap<String,Object>)session.getAttribute("user");
        //获取用户等级
        String rank=user.get("rank").toString();
        if("学生".equals(rank)){
            response.getWriter().print("<script>alert('非法访问,你没有权限添加课程信息');window.location='courseList'</script>");
        }
        //-----------------------获取课程信息头---------------------------
        ms.setSql("select * from t_course_info").runList();
        //课程信息头
        String[] top =ms.getTop();
        //删除主键
        top=Tools.delString(top,top[0]);
        //删除已选人数
        top=Tools.delString(top,"c_selected");
        session.setAttribute("top", top);
        return "courseInsert";
    }
    @RequestMapping("/CourseInsert")
    public void courseInsert(HttpServletRequest request, HttpServletResponse response,String c_name) throws IOException {
        HttpSession session=request.getSession();
        //-----------------------------------判断是否有传值-----------------------------------------------------------------
        if (c_name == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='courseInsert'</script>");
            return;
        }
        //--------------------------------再次判断课程总人数是否大于等于0(防止开发者工具删除js代码)---------------------------
        //翻译哈希map
        Map<String, String> ts=Translate.getTranslate();
        if(Integer.parseInt(request.getParameter("c_amount"))<0) {
            response.getWriter().print("<script>alert('"+ts.get("c_amount")+"不能是负数"+"');window.location='courseInsert'</script>");
            return;
        }
        // noinspection unchecked -----------添加(只有管理员才能修改授课老师)-------------------------------------------------
        LinkedHashMap<String, Object> user=(LinkedHashMap<String,Object>)session.getAttribute("user");
        boolean admin="管理员".equals(user.get("rank"));
        String[] top =(String[]) session.getAttribute("top");
        ms.setSql("INSERT INTO t_course_info VALUES(0,?,?,?,?,?,?,'0',?)");
        for (String s : top) {
            if (!admin && "tid".equals(s)) {
                ms.set(user.get("tid").toString());
            } else {
                ms.set(request.getParameter(s));
            }
        }
        //调试方法
        System.out.println('\n'+ms.getSql());
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=courseList");
        } else {
            response.getWriter().print("<script>alert('添加失败');window.location='courseInsert'</script>");
        }
    }
    @RequestMapping("/CourseDel")
    public void curseDel(HttpServletRequest request, HttpServletResponse response,String cid) throws IOException {
        // noinspection unchecked ------------判断是否有传值-----------------------------------------------------------------
        if (cid==null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='courseList'</script>");
            return;
        }
        // noinspection unchecked ---------判断是否为本课程老师或管理员-----------------------------------------------------------
         Map <String,Object> user=(Map<String, Object>) request.getSession().getAttribute("user");
        boolean isrankup=false;
        if("管理员".equals(user.get("rank"))) {
            isrankup=true;
        } else if("老师".equals(user.get("rank"))) {
            String tid=ms.setSql("select tid from t_course_info where cid=?").set(Integer.parseInt(cid))
                    .runList().get(0).get("tid").toString();
            if(tid.equals(user.get("tid").toString())) {
                isrankup=true;
            }
        }
        if (!isrankup) {
            response.getWriter().print("<script>alert('非法访问,你没有权限修改课程信息');window.location='courseList'</script>");
            return;
        }
        //--------------------------------判断是否为由当前网页链接访问-----------------------------------------------------------
        boolean notweb=true;
        List<LinkedHashMap<String, Object>> list=(List<LinkedHashMap<String, Object>>) request.getSession().getAttribute("list");
        for(LinkedHashMap<String,Object> temp:list) {
            if(temp.get("cid").toString().equals(cid)) {
                notweb=false;
                break;
            }
        }
        if (notweb){
            response.getWriter().print("<script>alert('非法访问');window.location='courseList'</script>");
            return;
        }
        //--------------------------------再次判断当前选课是否有学生已选课,有则不能删除课程(防止开发者工具删除js代码)--------------------------
        String c_selected=ms.setSql("SELECT * FROM `t_course_info` where cid=?").set(Integer.parseInt(cid))
                .runList().get(0).get("c_selected").toString();
        if(!"0".equals(c_selected)){
            response.getWriter().print("<script>alert('选课人数不为0,无法删除');window.location='courseList'</script>");
            return;
        }
        //----------------------------------------删除-------------------------------------------------------------------
        //调试方法
        System.out.println('\n'+ms.getSql());
        int i=ms.setSql("DELETE FROM t_course_info where cid =?").set(Integer.parseInt(cid)).run();
        if(i>0) {
            response.setHeader("refresh", "0;URL=courseList");
        } else {
            response.getWriter().print("<script>alert('删除失败');window.location='courseList'</script>");
        }
    }
    @RequestMapping("/courseChange")
    public String courseChange(HttpServletRequest request, HttpServletResponse response,String cid) throws IOException {
        //-----------------------------------判断是否有传值----------------------------------------------------------
        if (cid == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='courseList'</script>");
            return null;
        }
        // noinspection unchecked ---------判断是否为本课程老师或管理员----------------------------------------------------
        Map <String,Object> user=(Map<String, Object>) request.getSession().getAttribute("user");
        boolean change=false;
        if("管理员".equals(user.get("rank"))) {
            change=true;
        } else if("老师".equals(user.get("rank"))) {
            //获取该课程的老师id
            String tid=ms.setSql("select tid from t_course_info where cid=?").set(Integer.parseInt(cid)).runList().get(0).get("tid").toString();
            if(tid.equals(user.get("tid").toString())) {
                change=true;
            }
        }
        if (!change) {
            response.getWriter().print("<script>alert('非法访问,你没有权限修改课程信息');window.location='courseList'</script>");
            return null;
        }
        //--------------------------------判断是否为由当前网页链接访问------------------------------------------------------
        boolean notweb=true;
        // noinspection unchecked 存放查询到的数据
        List<LinkedHashMap<String, Object>> list=(List<LinkedHashMap<String, Object>>) request.getSession().getAttribute("list");
        for(LinkedHashMap<String,Object> temp:list) {
            if(temp.get("cid").toString().equals(cid)) {
                notweb=false;break;
            }
        }
        if (notweb){
            response.getWriter().print("<script>alert('非法访问');window.location='courseList'</script>");
            return null;
        }
        //---------------------------------------返回课程源信息----------------------------------------------------------
        ms.setSql("select * from t_course_info where cid=?").set(Integer.parseInt(cid));
        LinkedHashMap<String, Object> courseOrigin=ms.runList().get(0);
        //获取数据库字段信息,删除主键字段
        String[] top =Tools.delString(ms.getTop(), ms.getTop()[0]);
        request.getSession().setAttribute("courseOrigin", courseOrigin);
        request.getSession().setAttribute("top", top);
        return "courseChange";
    }
    @RequestMapping("/CourseChange")
    public void courseChange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        // noinspection unchecked 课程原信息
        LinkedHashMap<String, Object> courseOrigin=(LinkedHashMap<String, Object>) session.getAttribute("courseOrigin");
        // noinspection unchecked ----------------是否管理员---------------------------------------------------------
        LinkedHashMap<String, Object> user=(LinkedHashMap<String,Object>)session.getAttribute("user");
        boolean admin=false;
        if(session.getAttribute("user")!=null){
            admin= "管理员".equals(user.get("rank").toString());
        }
        //--------------------------------再次判断课程总人数是否大于等于已选人数(防止开发者工具删除js代码)---------------------------
        //翻译哈希map
        Map<String, String> ts=Translate.getTranslate();
        int c_amount=Integer.parseInt(request.getParameter("c_amount"));
        int c_selected=Integer.parseInt(courseOrigin.get("c_selected").toString());
        if(c_amount < c_selected) {
            response.getWriter().print("<script>alert('"+ts.get("c_amount")+"不能小于"
                    +ts.get("c_selected")+"');window.location='courseChange'</script>");
            return;
        }
        //--------------------------------再次判断当前选课是否有学生已选课,有则不能更改所属社团(防止开发者工具删除js代码)-----------------
        String aid=request.getParameter("aid");
        String aid0=courseOrigin.get("aid").toString();
        if(!(aid.equals(aid0))&&c_selected!=0){
            response.getWriter().print("<script>alert('当前课程已有学生选课,无法更改所属社团');window.location='courseChange'</script>");
            return;
        }
        // -------------------------------修改(只有管理员才能修改授课老师)-------------------------------------------------
        String[]top=(String[]) session.getAttribute("top");
        //删除不需要的字段
        top=Tools.delString(top, "c_selected");
        if (admin) {
            ms.setSql("UPDATE t_course_info SET aid=?,tid=?,c_name=?,c_time=?,c_credit=?,c_place=?,c_amount=? WHERE cid=?");
        }
        else {
            ms.setSql("UPDATE t_course_info SET aid=?,c_name=?,c_time=?,c_credit=?,c_place=?,c_amount=? WHERE cid=?");
            //删除不需要的字段,非管理员无法修改任课老师
            top=Tools.delString(top, "tid");
        }
        for (String s : top) {
            ms.set(request.getParameter(s));
        }
        ms.set((int)courseOrigin.get("cid"));
        //调试方法
        System.out.println('\n'+ms.getSql());
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=courseList");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.location='courseChange'</script>");
        }
    }
}
