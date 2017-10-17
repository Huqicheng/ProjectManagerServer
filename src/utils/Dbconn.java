package utils;
import java.sql.*;


public class Dbconn {
	public static String driver = "com.mysql.jdbc.Driver";
    public static String URL = "jdbc:mysql://127.0.0.1/faces?useunicode=true&characterEncoding=utf-8";
    public static String user="root";
    public static String password="";
    //jdbc:sqlserver://localhost:1433; DatabaseName=test;user=123;password=123;;user=supplier;password=123
     Connection conn= null; 
     ResultSet rs= null;
     Statement stmt = null;
     //CallableStatement callStmt = null; 
     PreparedStatement callStmt = null;
     public boolean count = false;
     int result;
     
     public static ConnectionPool connPool
     = new ConnectionPool(driver,
                          URL
                          ,user, password);
  public void debug(){
	  this.connPool.debug();
  }
  public Dbconn()
  {
	  try
	  {
		  Class.forName(driver);
	  }
	  catch(Exception e)
	  {
		  System.out.println("加载驱动异常 "+e.toString());
	  }
	  
	  try {
		
		connPool.createPool();
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  
	  
  }
  
  public Connection getConnection()
  {
	try {
		if(conn == null)
			this.conn = connPool.getConnection();
		return conn;
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
  }
  
  public ResultSet query(String sql)  //查询
  {
	  System.out.println(sql);
	  try{
	  //conn = DriverManager.getConnection(URL);
	  conn = getConnection();
	  stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
	  rs = stmt.executeQuery(sql);
	  //System.out.println(rs.next());
	  }
	  catch(Exception e)
	  {
		  this.dispose();
		  System.out.println("查询异常 "+e.toString());
	  }
	  
	  return rs;

  }
  
  public ResultSet Call(String sql,String[]can)
  {
	  System.out.println(sql);  
	  try{
		  conn = getConnection();
	  this.callStmt = conn.prepareCall("{ call "+sql+"}");
	  
	  for(int i=0;i<can.length;i++)
		   if(!can[i].equals("null"))
		  callStmt.setNString(i+1,can[i]);
		   else callStmt.setNull(i+1,Types.NULL);
	    boolean count = callStmt.execute();
	     this.count = true;
	     for(int i=0;i<can.length;i++)
	     {
	 	    rs = callStmt.getResultSet();
	    	 if(count)
	    		 break;
	    	count =  callStmt.getMoreResults();
	     }
	      
        
	  }
	  catch(Exception e)
	  {
		  System.out.println("存储过程执行异常 "+e.toString());
	  }
	  
	  return rs;
  }
  
  public void executeUpdate(String sql) throws Exception //更新
  {
	  rs = null;
	  System.out.println(sql);
	  try
	  {
		  conn = getConnection();
		  stmt = conn.createStatement();
		  result = stmt.executeUpdate(sql);
	  }
	  catch(Exception e)
	  {
		  this.dispose();
		  System.out.println("更新异常 "+e.toString());
		  throw new Exception(e);
	  }
	  
	  
  }
  public int getresult()
  {
	  return this.result;
  }
  
 
  
  public void dispose()
  {
	  if(stmt!=null)
	  {
		  try {
			stmt.close();
			 stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  }
	  
	  
	  
	  if(conn != null)
	  {
		  connPool.returnConnection(this.conn);
		  conn = null;
	  }
	  
  }
  
  
  
  @Override
  protected void finalize() throws Throwable {
  	super.finalize();
  	if(stmt!=null)
  	  {
  		  try {
  			stmt.close();
  			 stmt = null;
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
  	  }
  	if(conn != null)
	  {
		  connPool.returnConnection(this.conn);
		  conn = null;
	  }
  	
  }


  public static void main(String[] args) throws SQLException
  {
	 Dbconn da=new Dbconn();
	 int i = 0;
	 while(i<=2){
	 String sql="select * from person" ;
	 ResultSet rs=da.query(sql);
	 try {
		while(rs.next())
		{
			System.out.println(rs.getString(1));
		}
	 } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	 }finally{
		if(rs!=null)
		{
			rs.close();
		}
		da.dispose();
				
	 }
	 i++;
	 //da.debug();
	 }
	 	 
	 	
	 	 
	 
 	 
  }

}
