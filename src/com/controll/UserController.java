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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author aotmd
 */
@Controller
public class UserController {
    /**数据库操作类*/
    private MySql ms;
    public UserController(MySql ms) {this.ms = ms;}
    @RequestMapping("/main")
    public String main(){
        return "main";
    }
    @RequestMapping("/login")
    public String login(HttpServletRequest request){
        //设置翻译图
        request.getSession().setAttribute("translate", Translate.getTranslate());
        //初始化MyBatis 连接池
/*        ms.setSql("select * from t_teacher_info").runList();*/
        return "login";
    }
    @RequestMapping("/Login")
    public String login(HttpServletRequest request, HttpServletResponse response,String username,String password,String verifyInput) throws IOException {
        List<LinkedHashMap<String, Object>> list;
        LinkedHashMap<String, Object> user;
        // ------------------------------------验证码-----------------------------------
        if (!request.getSession().getAttribute("verifyCode").toString().equals(verifyInput)){
            response.getWriter().print("<script>alert('登录失败,验证码错误');window.location='login'</script>");
            return null;
        }
        // ----------------------判断是否为学生账号---------------------------------------
        list = ms.setSql("SELECT * FROM t_student_info where username=? and password=?").set(username).set(password).runList();
        if (list.size() != 0) {
            user = list.get(0);
            if ((user.get("username")).equals(username)) {
                user.put("rank", "学生");
                request.getSession().setAttribute("user", user);
                return "redirect:main";
            }
        }
        // ---------------------判断是否为老师账号-------------------------------------------
        list = ms.setSql("SELECT * FROM t_teacher_info where t_username=? and t_password=?").set(username).set(password).runList();

        if (list.size() != 0) {
            user = list.get(0);
            if ((user.get("t_username")).equals(username)) {
                if ("1".equals(user.get("tid").toString())) {
                    user.put("rank", "管理员");
                } else {
                    user.put("rank", "老师");
                }
                request.getSession().setAttribute("user", user);
                return "redirect:main";
            }
        }
        response.getWriter().print("<script>alert('登录失败');window.location='login'</script>");
        return null;
    }
    @RequestMapping("/Logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:login";
    }
    @RequestMapping("reg")
    public String reg(HttpServletRequest request){
        HttpSession session=request.getSession();
        boolean isAdmin=false;
        if(session.getAttribute("user")!=null){
            //noinspection unchecked
            isAdmin= "管理员".equals(((LinkedHashMap<String,Object>)session.getAttribute("user")).get("rank"));
        }
        String title;
        String returnUrl;
        if (isAdmin) {
            ms.setSql("SELECT * FROM `t_teacher_info`");
            title = "老师";
            returnUrl = "userList?t=2";
        } else {
            //是学生注册则接收社团信息
            List<LinkedHashMap<String, Object>> aid = ms.setSql("SELECT aid,assn_name FROM `t_assn_info`").runList();
            request.setAttribute("aid", aid);
            ms.setSql("SELECT * FROM `t_student_info`");
            title = "学生";
            returnUrl = "login";
        }
        ms.runList();
        //获取数据库字段信息,删除主键字段
        String[]top= Tools.delString(ms.getTop(), ms.getTop()[0]);
        request.getSession().setAttribute("top", top);
        request.setAttribute("title",title);
        request.setAttribute("returnUrl",returnUrl);
        return "reg";
    }
    @RequestMapping("/Reg")
    public void reg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        // ------------------------是否管理员----------------------------------
        boolean isAdmin=false;
        if(session.getAttribute("user")!=null){
            //noinspection unchecked
            isAdmin= "管理员".equals(((LinkedHashMap<String,Object>)session.getAttribute("user")).get("rank"));
        }
        // ------------------------查重--------------------------------------
        String username=isAdmin?request.getParameter("t_username"):request.getParameter("username");
        ms.setSql("SELECT * FROM t_student_info,t_teacher_info where username=? or t_username=?").set(username).set(username).runList();
        if (ms.getSum()!= 0) {
            response.getWriter().print("<script>alert('用户名称重复');window.location='reg'</script>");
            return;
        }
        // ------------------------新建--------------------------------------
        String[]top=(String[]) request.getSession().getAttribute("top");
        if (isAdmin) {
            ms.setSql("INSERT INTO t_teacher_info VALUES(0,?,?,?,?,?,?)");
        } else {
            ms.setSql("INSERT INTO t_student_info VALUES(0,?,?,?,?,?,?,?,?)");
        }
        System.out.println(Arrays.toString(top));
        for (String string : top) {
            ms.set(request.getParameter(string));
        }
        System.out.println('\n'+ms.getSql());
        int i=ms.run();
        if(i>0) {
            response.setHeader("refresh", "0;URL=login");
        } else {
            response.getWriter().print("<script>alert('注册失败');window.location='reg'</script>");
        }
    }
    @RequestMapping("/userChange")
    public String userChange(HttpServletRequest request){
        HttpSession session=request.getSession();
        //noinspection unchecked
        LinkedHashMap<String,Object> user=(LinkedHashMap<String,Object>)session.getAttribute("user");
        String rank=user.get("rank").toString();
        String[] top;
        if("管理员".equals(rank)|| "老师".equals(rank)){
            ms.setSql("SELECT * FROM `t_teacher_info`");
        }
        else if("学生".equals(rank)){
            ms.setSql("SELECT * FROM `t_student_info`");
        }
        ms.runList();
        //获取数据库字段信息,删除主键字段
        top=Tools.delString(ms.getTop(), ms.getTop()[0]);
        //存入session
        session.setAttribute("top", top);
        return "userChange";
    }
    @RequestMapping("/UserChange")
    public void userChange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        //noinspection unchecked
        LinkedHashMap<String, Object> user=(LinkedHashMap<String,Object>)session.getAttribute("user");
        // ---------------------------------------是否管理员和教师---------------------------------------------
        boolean teacher=false;
        if("管理员".equals(user.get("rank"))|| "老师".equals(user.get("rank"))){
            teacher=true;
        }
        //----------------------------------------查看旧密码是否正确-------------------------------------------
        //用户登录时记录的密码
        String password0=teacher?user.get("t_password").toString():user.get("password").toString();
        //用户表单提交的旧密码
        String password=teacher?request.getParameter("t_password0"):request.getParameter("password0");
        if(!(password0.equals(password))) {
            response.getWriter().print("<script>alert('原密码错误');window.location='userChange'</script>");
            return;
        }
        //----------------------------------------是学生则判断是否有选课,有则无法更改社团---------------------------------
        if(!teacher) {
            //noinspection unchecked 选课门数
            int truesize=((Map<String,String>)session.getAttribute("cidmap")).size();
            String aid=request.getParameter("aid");
            String aid0=user.get("aid").toString();
            if(truesize>0&&!(aid.equals(aid0))) {
                response.getWriter().print("<script>alert('当前所在社团有选课无法更改社团');window.location='userChange'</script>");
                return;
            }
        }
        // ---------------------------------------查重(并可避免修改账号时登录其他人的账号与密码)-----------------------------
        //用户修改后的登录名字
        String username=teacher?request.getParameter("t_username"):request.getParameter("username");
        //用户原登录名字
        String username0=teacher?user.get("t_username").toString():user.get("username").toString();
        ms.setSql("SELECT * FROM t_student_info,t_teacher_info where username=? or t_username=?").set(username).set(username).runList();
        //有记录且原名字与新名字不同
        if (ms.getSum()!= 0&&!(username0.equals(username))) {
            response.getWriter().print("<script>alert('用户名称重复');window.location='userChange'</script>");
            return;
        }
        // --------------------------------------------修改----------------------------------------------------
        if (teacher) {
            ms.setSql("UPDATE t_teacher_info SET t_username=?,t_password=?,t_realname=?,t_sex=?,t_age=?,t_phone=? WHERE tid=?");
        }
        else {
            ms.setSql("UPDATE t_student_info SET aid=?,username=?,password=?,realname=?,sex=?,year=?,faculty=?,phone=? WHERE sid=?");
        }
        String[]top=(String[]) session.getAttribute("top");
        for (String string : top) {
            ms.set(request.getParameter(string));
        }
        if (teacher) {
            ms.set(Integer.parseInt(user.get("tid").toString()));
        }
        else {
            ms.set(Integer.parseInt(user.get("sid").toString()));
        }
        //调试方法
        System.out.println('\n'+ms.getSql());
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=Logout");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.location='userChange'</script>");
        }
    }
    @SuppressWarnings("SqlRedundantOrderingDirection")
    @RequestMapping("/userList")
    public String userList(HttpServletRequest request, HttpServletResponse response, String t) throws IOException {
        HttpSession session = request.getSession();
        String username;
        //noinspection unchecked ---------------是否管理员-----------------------------------------------------------
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
        if (!("管理员".equals(user.get("rank").toString()))) {
            response.getWriter().print("<script>alert('你不是管理员');window.location='courseList'</script>");
            return null;
        }
        //----------------------------------------判断传值并写入session------------------------------------------------
        if (t != null) {
            if ("1".equals(t)) {
                username = "学生";
            } else if ("2".equals(t)) {
                username = "教师";
            } else {
                response.getWriter().print("<script>alert('非法访问');window.location='courseList'</script>");
                return null;
            }
            session.setAttribute("username", username);
        } else if (session.getAttribute("username") != null) {
            username = (String) session.getAttribute("username");
        } else {
            response.getWriter().print("<script>alert('非法访问');window.location='courseList'</script>");
            return null;
        }
        //----------------------------------分别查询查询---------------------------------------------------------------
        boolean isteacher = "教师".equals(username);
        if (isteacher) {
            ms.setSql("SELECT * from t_teacher_info where tid !=1 order by tid  asc limit ?,?");
        } else {
            ms.setSql("SELECT * from t_student_info order by sid  asc limit ?,?");
        }
        ms.runPagination(request, "/userList", 10);
        request.setAttribute("sum", ms.getSum());
        request.setAttribute("top", ms.getTop());
        //----------------------------------转发------------------------------------------------------------
        return "userList";
    }
    @RequestMapping("/userReset")
    public void userReset(HttpServletRequest request, HttpServletResponse response,String tid,String sid) throws IOException {
        HttpSession session=request.getSession();
        String username=(String) session.getAttribute("username");
        boolean isteacher= "教师".equals(username);
        int id=0;
        //noinspection unchecked ----------------是否管理员-----------------------------------------------------------
        LinkedHashMap<String, Object> user=(LinkedHashMap<String,Object>)session.getAttribute("user");
        if(!("管理员".equals(user.get("rank").toString()))){
            response.getWriter().print("<script>alert('你不是管理员');window.location='courseList'</script>");
            return;
        }
        //--------------------------------------------接收传值---------------------------------------------------------
        boolean isnull=false;
        if(isteacher){
            if(tid!=null) {id=Integer.parseInt(tid);}
            else {isnull=true;}
        }else{
            if(sid!=null) {id=Integer.parseInt(sid);}
            else {isnull=true;}
        }
        if (isnull) {
            response.getWriter().print("<script>alert('非法访问,没有值');window.location='userList'</script>");
            return;
        }
        //noinspection unchecked ----判断是否为由当前网页链接访问-------------------------------------------------
        List<LinkedHashMap<String, Object>> list=(List<LinkedHashMap<String, Object>>) request.getSession().getAttribute("list");
        boolean notWeb=true;
        for(LinkedHashMap<String,Object> temp:list) {
            if (isteacher) {
                if(temp.get("tid").toString().equals(id+"")) {
                    notWeb=false;
                    break;
                }
            }else {
                if(temp.get("sid").toString().equals(id+"")) {
                    notWeb=false;
                    break;
                }
            }
        }
        if (notWeb){
            response.getWriter().print("<script>alert('非法访问');window.location='courseList'</script>");
            return;
        }
        //----------------------------------分别重置---------------------------------------------------------------
        int i;
        if (isteacher) {
            i=ms.setSql("update t_teacher_info set t_password=t_username where tid=?").set(id).run();
        }else {
            i=ms.setSql("update t_student_info set password=username where sid=?").set(id).run();
        }
        System.out.println('\n'+ms.getSql());
        if(i>0) {
            response.getWriter().print("<script>alert('已重置密码为账号名');window.location='userList'</script>");
        } else {
            response.getWriter().print("<script>alert('重置密码失败');window.location='userList'</script>");
        }
    }
    @RequestMapping("/userDel")
    public void userDel(HttpServletRequest request, HttpServletResponse response,String tid,String sid) throws IOException {
        HttpSession session=request.getSession();
        String username=(String) session.getAttribute("username");
        boolean teacher= "教师".equals(username);
        int id=0;
        //noinspection unchecked ----------------是否管理员-----------------------------------------------------------
        LinkedHashMap<String, Object> user=(LinkedHashMap<String,Object>)session.getAttribute("user");
        if(!("管理员".equals(user.get("rank").toString()))){
            response.getWriter().print("<script>alert('你不是管理员');window.location='courseList'</script>");
            return;
        }
        //--------------------------------------------接收传值---------------------------------------------------------
        boolean isnull=false;
        if(teacher){
            if(tid!=null) {id=Integer.parseInt(tid);}
            else {isnull=true;}
        }else{
            if(sid!=null) {id=Integer.parseInt(sid);
            }else {isnull=true;}
        }
        if (isnull) {
            response.getWriter().print("<script>alert('非法访问,没有值');window.location='userList'</script>");
            return;
        }
        //noinspection unchecked ---------判断是否为由当前网页链接访问-------------------------------------------------
        List<LinkedHashMap<String, Object>> list=(List<LinkedHashMap<String, Object>>) request.getSession().getAttribute("list");
        boolean notWeb=true;
        for(LinkedHashMap<String,Object> temp:list) {
            if (teacher) {
                if(temp.get("tid").toString().equals(id+"")) {
                    notWeb=false;
                    break;
                }
            }else {
                if(temp.get("sid").toString().equals(id+"")) {
                    notWeb=false;
                    break;
                }
            }
        }
        if (notWeb){
            response.getWriter().print("<script>alert('非法访问');window.location='courseList'</script>");
            return;
        }
        //--------------------------------------------查询选课信息或开课情况-------------------------------------------
        if (teacher) {
            int i=ms.setSql("SELECT * from t_course_info where tid=?").set(id+"").runList().size();
            if(i!=0) {
                response.getWriter().print("<script>alert('当前教师账号下有课程,无法删除!');window.location='userList'</script>");
                return;
            }
        }else {
            int i=ms.setSql("SELECT * from t_elective_info where sid=?").set(id+"").runList().size();
            if(i!=0) {
                response.getWriter().print("<script>alert('当前学生账号已选课,无法删除!');window.location='userList'</script>");
                return;
            }
        }
        //----------------------------------分别删除查询---------------------------------------------------------------
        int i;
        if (teacher) {
            i=ms.setSql("DELETE FROM t_teacher_info where tid=?").set(id).run();
        }
        else {
            i=ms.setSql("DELETE FROM t_student_info where sid=?").set(id).run();
        }
        System.out.println('\n'+ms.getSql());
        if(i>0) {
            response.getWriter().print("<script>alert('已删除');window.location='userList'</script>");
        } else {
            response.getWriter().print("<script>alert('删除失败');window.location='userList'</script>");
        }
    }
}
