package utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

public class StrutsUtil {
	public static void write(HttpServletResponse response,String jsonString) throws IOException{
		//=ServletActionContext.getResponse();  
	    /* 
	     * set charset
	     * */  
	    response.setContentType("text/html;charset=utf-8");  
	    //response.setCharacterEncoding("UTF-8");  
	    PrintWriter out = response.getWriter();  
	    
	    out.println(jsonString);  
	    out.flush();  
	    out.close(); 
	}
}
