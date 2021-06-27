package com.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;

interface MySqlDao{
	@SelectProvider(type = UserMapper.class, method = "returnSql")
	List<LinkedHashMap<String,Object>> select(String sql);
	@UpdateProvider(type = UserMapper.class, method = "returnSql")
	int update(String sql);
	@InsertProvider(type = UserMapper.class, method = "returnSql")
	int insert(String sql);
	@DeleteProvider(type = UserMapper.class, method = "returnSql")
	int delete(String sql);
}
/**
 * 	自定义Sql运行类
 * <p>
 * 	常见用法:List&lt;LinkedHashMap&lt;String, Object&gt;&gt; list=ms.setSql("select * from linkman where
 * name=?").set("张三").runlist();
 * <p>
 *
 * @author aotmd
 */
@Repository
public class MySql {
	MySqlDao mySqlDao;
	private String sql = "";
	private PreparedStatement ps = null;
	private Connection conn = null;
	 /**总条目数*/
	private int sum = 0;
	/** 索引*/
	private int index=0;
	/**数据库表字段(Map键值)*/
	private String[] top = null;

	public MySql(MySqlDao mySqlDao) {
		this.mySqlDao = mySqlDao;
		login();
	}

//	/**
//	 * 默认构造方法
//	 */
//	public MySql() {login();}
	/**
	 * 	反复使用时的更新Sql语句方法
	 * @param sql mysql语句
	 * @return this
	 */
	public MySql setSql(String sql) {
		sum = 0;
		index=1;
		try {
			ps = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	/**
	 *	 调试用方法
	 * @return 返回sql语句
	 */
	public String getSql() {
		return sql;
	}
	/**
	 *	 连接数据库
	 *
	 */
	private void login() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/community_management2?useUnicode=true&characterRncoding=utf-8";
			conn = DriverManager.getConnection(url, "root", "123456");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从前往后一个一个修改
	 * @param object 修改数据库?号所对值
	 * @return this
	 */
	public MySql set(Object object) {
		try {
			ps.setObject(index++,object);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 	更新的形式提交
	 * @return (1) SQL 数据操作语言 (DML) 语句的行数 (2) 对于无返回内容的 SQL 语句，返回 0
	 */
	public int run() {
		int i = 0;
		try {
			String temp = ps.toString();
			ps.close();
        	sql=temp.substring(temp.indexOf(": ")+2);
        	if (sql.contains("?")){
				System.out.println("sql语句错误!有多余的?号");
        		i=0;
			}
			String type=sql.trim().split("\\s+")[0];
        	if (type.equalsIgnoreCase("update")){
        		i=mySqlDao.update(sql);
			}else if (type.equalsIgnoreCase("insert")){
				i=mySqlDao.insert(sql);
			}else if (type.equalsIgnoreCase("delete")){
				i=mySqlDao.delete(sql);
			}else {
				System.out.println("不是增删改的数据库语句");
				i=0;
			}
        	index=1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * 	运行executeQuery(),即查询内容
	 * 	<P>调用方法:for(LinkedHashMap&lt;String, Object&gt; map:list){map.get(key)}
	 * <p>
	 * 	示例:topname为getName()取得的String[]
	 * <ul>
	 * <li>for(LinkedHashMap&lt;String, Object&gt; map:list) {</li>
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;for(int j = 0; j < topname.length; j++){</li>
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print(map.get(topname[j])+"\t");</li>
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;}</li>
	 * <li>System.out.println();</li> }
	 * </ul>
	 *
	 * @return 返回List对象
	 */
	public List<LinkedHashMap<String, Object>> runList() {
		// 声明返回的对象
		List<LinkedHashMap<String, Object>> list = new ArrayList<>();
		try {
			String temp = ps.toString();
			sql=temp.substring(temp.indexOf(": ")+2);
			if (sql.contains("?")){
				System.out.println("sql语句错误!有多余的?号");
				System.out.println(sql);
				ps.close();
				return list;
			}
			String type=sql.trim().split("\\s+")[0];
			if (type.equalsIgnoreCase("select")){
				list=mySqlDao.select(sql);
			}else {
				System.out.println("不是查询的数据库语句");
				System.out.println(sql);
				ps.close();
				return list;
			}
			sum=list.size();
			//当MyBatis取得到值时获取数据库列字段,取不到时使用JDBC取数据库列字段
			if (list.size()!=0){
				top=list.get(0).keySet().toArray(new String[0]);
			} else {
				System.out.println("调用JDBC获取字段");
				ResultSet resu = ps.executeQuery();
				// 分析结果集
				ResultSetMetaData rsmd = resu.getMetaData();
				// 获取列数
				int cols = rsmd.getColumnCount();
				// 初始化
				top = new String[cols];
				for (int i = 0; i < cols; i++) {
					// 获取列名存入Sring[]
					top[i] = rsmd.getColumnName(i + 1);
				}
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		index=1;
		return list;
	}
	/**
	 * 	调用该方法需正确设置sql语句,<p>"select * from linkman where uid=? order by id asc limit ?,?"<p>
	 * 	<b>order后的两个?号不需要set()设置</b>
	 * @param request request
	 * @param url 当前Servlet网址
	 * @param pageMax 以多少记录为一页
	 */
	@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
	public void runPagination(HttpServletRequest request, String url, int pageMax) {
		//当前页
		int currentPage = 1;
		// 判断传递页码是否有效
		if (request.getParameter("currentPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		List<LinkedHashMap<String, Object>> list = this.set((currentPage - 1) * pageMax).set(pageMax).runList();
		request.getSession().setAttribute("list", list);
		//重新查询未分页时的记录数(更新sum值)
		this.setSql(sql.substring(0,sql.indexOf("order")-1)).runList();
		// 总页数
		int pages;
		if (this.getSum() % pageMax == 0) {
			pages = this.getSum() / pageMax;
		} else {
			pages = this.getSum() / pageMax + 1;
		}
		StringBuilder sb = new StringBuilder();
		//格式化url,传值有/则截去
		if(url.indexOf("/")==0) {url=url.substring(1);}
		// 构建分页条
		for (int i = 1; i <= pages; i++) {
			if (i == currentPage) {
				sb.append("<b>" + i + "</b>");
			} else {
				sb.append("<a href='"+url+"?currentPage=" + i + "'>" + i + "</a>");
			}
			if (i != pages) {
				sb.append("　");
			}
		}
		index=1;
		request.getSession().setAttribute("bar", sb.toString());
	}
	/**
	 *  	 获取数据库表字段(Map键值)
	 *
	 * @return 返回String[](Map键值)
	 */
	public String[] getTop() {
		return top;
	}

	/**
	 *   	返回executeQuery()获得记录的行数,
	 *   <p>若使用分页方法则返回的是未分页时的记录条数
	 * @return 返回executeQuery()获得记录的行数
	 */
	public int getSum() {
		return sum;
	}

//	public static void main(String[] args) {
	/*	MySql ms = new MySql("select * from linkman where uid=?");
		List<Map<String, Object>> list = ms.set(1).runlist();
		//-------------------------获取表头(Map的Key)----------------------------
		String[] topname = ms.getTop();
		for (int i = 0; i < topname.length; i++) {
			System.out.print(topname[i] + "\t");
		}
		System.out.println();
		//----------------------------------------------------------------------
		for (Map<String, Object> map : list) {
			for (int j = 0; j < topname.length; j++) {
				System.out.print(map.get(topname[j]) + "\t");
			}
			System.out.println();
		}
		System.out.println("共" + ms.getSum() + "条数据");
		System.out.println(ms.getSql());*/
		/*
		 * 	//下为增强for实际代码
		 * for(int i=0;i<list.size();i++) {
		 * Map<String, Object> map=list.get(i);
		 * for (int j = 0; j < topname.length; j++) {//指定key
		 * System.out.print(map.get(topname[j])+"\t"); }
		 * System.out.println(); }
		 * 	//增强for
		 * for (String key : map.keySet())
		 * {//map为无序输出,因此不一定会按加入顺序输出,要按顺序输出请指定key或者改为list
		 * System.out.print(key + " ：" +map.get(key));
		 * }
		 * System.out.println();
		 * 	//下为增强for实际代码
		 *  for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
		 *   String key=iterator.next();
		 *   System.out.print(key + " ：" + map.get(key));
		 *   }
		 */
//	}
}
