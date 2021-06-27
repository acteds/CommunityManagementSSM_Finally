package com.controll;

import com.dao.MySql;
import com.aotmd.Tools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程控制器
 * @author aotmd
 */
@Controller
public class CidController {
    /**数据库操作类*/
    private MySql ms;
    public CidController(MySql ms) {this.ms = ms;}
    @RequestMapping("/cidList")
    public String cidList(HttpServletRequest request, HttpServletResponse response,String cid) throws IOException {
        HttpSession session =request.getSession();
        if(cid!= null) {
            session.setAttribute("cid",cid);
        }else if(session.getAttribute("cid")!=null) {
            cid=(String) session.getAttribute("cid");
        }else{
            response.getWriter().print("<script>alert('非法访问');window.location='courseList'</script>");
            return null;
        }
        //noinspection SqlRedundantOrderingDirection -查询选该门课的学生---------------------------------------------------------
        ms.setSql("SELECT s.sid,realname,sex,year,faculty,phone FROM t_elective_info as e,t_student_info as s "
                +"where cid=? and e.sid=s.sid  order by s.sid asc limit ?,?");
        ms.set(Integer.parseInt(cid)).runPagination(request,"/cidList", 10);
        request.setAttribute("sum", ms.getSum());
        String []top=ms.getTop();
        String name = null;
        Map<String, Object> user = (Map<String, Object>) request.getSession().getAttribute("user");
        String rank= user.get("rank").toString();
        if("学生".equals(rank)){
            top= Tools.delString(top,"phone");
            name=user.get("realname").toString();
        }else if("管理员".equals(rank)|| "老师".equals(rank)){
            name=user.get("t_realname").toString();
            request.setAttribute("rankUp",1);
        }
        request.setAttribute("name",name);
        request.setAttribute("top",top);
        //---------------------------------------查询课程名字-----------------------------------------------------------
        String cName=ms.setSql("SELECT c_name from t_course_info where cid=?").set(cid).runList().get(0).get("c_name").toString();
        request.setAttribute("cName", cName);
        return "cidList";
    }
    @RequestMapping("/cidUp")
    public void cidUp(HttpServletRequest request, HttpServletResponse response,@RequestParam String cid) throws IOException {
        HttpSession session = request.getSession();
        // noinspection unchecked
        Map <String,Object> user=(Map<String, Object>) session.getAttribute("user");
        List<LinkedHashMap<String, Object>> list;//存放查询到的数据
        // noinspection unchecked ---------判断是否为由网页链接访问------------------------------------------------------
        list=(List<LinkedHashMap<String, Object>>) session.getAttribute("list");
        boolean notWeb=true;
        for(LinkedHashMap<String,Object> temp:list) {
            if(temp.get("cid").toString().equals(cid)) {
                notWeb=false;
                break;
            }
        }
        if (notWeb){
            response.getWriter().print("<script>alert('非法访问');window.location='courseList'</script>");
            return;
        }
        // noinspection unchecked ---判断是否已选课----------------------------------------------------------------
        Map<String,String> cidmap=(Map<String,String>) session.getAttribute("cidmap");
        if(cidmap.get(cid)!=null){
            response.getWriter().print("<script>alert('你已选了本课程');window.location='courseList'</script>");
            return;
        }
        //--------------------------------判断课程是否满员--------------------------------------------------------------
        LinkedHashMap<String,Object> course=ms.setSql("Select * from t_course_info where cid=?").set(Integer.parseInt(cid)).runList().get(0);
        boolean isFull=true;
        if((int)course.get("c_selected")<(int)course.get("c_amount")){isFull=false;}
        if (isFull){
            response.getWriter().print("<script>alert('人数已满');window.location='courseList'</script>");
            return;
        }
        //--------------------------------添加记录----------------------------------------------------------------------
        int i=ms.setSql("INSERT INTO t_elective_info VALUES(0,?,?,?)")
                .set(user.get("sid").toString())
                .set(cid)
                .set(user.get("aid").toString())
                .run();
        if(i>0) {
            i=ms.setSql("UPDATE t_course_info SET c_selected=c_selected+1 WHERE cid=?")
                    .set(Integer.parseInt(cid))
                    .run();
            if (i>0) {
                response.setHeader("refresh", "0;URL=courseList");
            } else {
                response.getWriter().print("<script>alert('课程信息表更改失败,请立即通知管理员');window.location='courseList'</script>");
            }
        }
        else {
            response.getWriter().print("<script>alert('选课失败');window.location='courseList'</script>");
        }
    }
    @RequestMapping("/cidDown")
    public void cidDown(HttpServletRequest request, HttpServletResponse response,@RequestParam String cid) throws IOException {
        List<LinkedHashMap<String, Object>> list;//存放查询到的数据
        // noinspection unchecked -------------判断是否为由网页链接访问------------------------------------------------------
        list=(List<LinkedHashMap<String, Object>>) request.getSession().getAttribute("list");
        boolean notWeb=true;
        for(LinkedHashMap<String,Object> temp:list) {
            if(temp.get("cid").toString().equals(cid)) {
                notWeb=false;
                break;
            }
        }
        if (notWeb){
            response.getWriter().print("<script>alert('非法访问');window.location='courseList'</script>");
            return;
        }
        // noinspection unchecked --------判断是否已选课----------------------------------------------------------------
        Map<String,String> cidmap=(Map<String,String>)request.getSession().getAttribute("cidmap");
        if(cidmap.get(cid)==null){
            response.getWriter().print("<script>alert('你没有选本课程');window.location='courseList'</script>");
            return;
        }
        // noinspection unchecked ---------删除记录----------------------------------------------------------------------
        Map <String,Object> user=(Map<String, Object>) request.getSession().getAttribute("user");
        int i=ms.setSql("DELETE FROM t_elective_info where sid=? and cid=?")
                .set(user.get("sid").toString())
                .set(cid)
                .run();
        if(i>0) {
            i=ms.setSql("UPDATE t_course_info SET c_selected=c_selected-1 WHERE cid=?")
                    .set(Integer.parseInt(cid))
                    .run();
            if (i>0) {
                response.setHeader("refresh", "0;URL=courseList");
            } else {
                response.getWriter().print("<script>alert('课程信息表更改失败,请立即通知管理员');window.location='courseList'</script>");
            }
        }
        else {
            response.getWriter().print("<script>alert('退课失败');window.location='courseList'</script>");
        }
    }
    @RequestMapping("/t_cidDown")
    public void tCidDown(HttpServletRequest request, HttpServletResponse response,@RequestParam String sid) throws IOException {
        String cid=request.getSession().getAttribute("cid").toString();
        List<LinkedHashMap<String, Object>> list;//存放查询到的数据
        // noinspection unchecked ---------判断是否为本课程老师或管理员--------------------------------------------------
        Map <String,Object> user=(Map<String, Object>) request.getSession().getAttribute("user");
        boolean isrankup=false;
        if("管理员".equals(user.get("rank"))) {
            isrankup=true;
        } else if("老师".equals(user.get("rank"))) {
            String tid=ms.setSql("select tid from t_course_info where cid=?").set(Integer.parseInt(cid)).runList().get(0).get("tid").toString();
            if(tid.equals(user.get("tid").toString())) {
                isrankup=true;
            }
        }
        if (!isrankup) {
            response.getWriter().print("<script>alert('非法访问,你没有权限退课');window.location='cidList'</script>");
            return;
        }
        // noinspection unchecked -------判断是否为由网页链接访问------------------------------------------------------
        list=(List<LinkedHashMap<String, Object>>) request.getSession().getAttribute("list");
        boolean notWeb=true;
        for(LinkedHashMap<String,Object> temp:list) {
            if(temp.get("sid").toString().equals(sid)) {
                notWeb=false;
                break;
            }
        }
        if (notWeb){
            response.getWriter().print("<script>alert('非法访问');window.location='cidList'</script>");
            return;
        }
        //--------------------------------删除记录----------------------------------------------------------------------
        int i=ms.setSql("DELETE FROM t_elective_info where sid=? and cid=?")
                .set(sid)
                .set(cid)
                .run();
        if(i>0) {
            i=ms.setSql("UPDATE t_course_info SET c_selected=c_selected-1 WHERE cid=?")
                    .set(Integer.parseInt(cid))
                    .run();
            if (i>0) {
                response.setHeader("refresh", "0;URL=cidList");
            } else {
                response.getWriter().print("<script>alert('课程信息表更改失败,请立即通知管理员');window.location='cidList'</script>");
            }
        }
        else {
            response.getWriter().print("<script>alert('退课失败');window.location='cidList'</script>");
        }
    }
}
