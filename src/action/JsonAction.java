package action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;


import utils.StrutsUtil;

public class JsonAction extends ActionSupport{
	

	public void write() throws IOException {  
		HttpServletResponse response = ServletActionContext.getResponse();   
		
		StrutsUtil.write(response,"12434343434343433");
	} 
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("this is execute");
		return super.execute();
	}
}
