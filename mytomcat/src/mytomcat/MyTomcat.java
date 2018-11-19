package mytomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MyTomcat {

	public static void main(String[] args) {
		new MyTomcat(8080).start();
	}

	private int port = 8080;
	private Map<String,String> urlServletMap = new HashMap<>();
	public MyTomcat(int port) {
		this.port = port;
	}
	
	public void start(){
		this.initServletMapping();
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("my tomcat start...");
			while(true){
				Socket socket = serverSocket.accept();
				InputStream inputStream = socket.getInputStream();
				OutputStream outputStream = socket.getOutputStream();
				MyRequest myRequest = new MyRequest(inputStream);
				MyResponse myResponse = new MyResponse(outputStream);
				this.dispatch(myRequest, myResponse);
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initServletMapping(){
		for(ServletMapping servletMapping : ServletMappingConfig.servletMappingList){
			urlServletMap.put(servletMapping.getUrl(), servletMapping.getClazz());
		}
	}
	
	private void dispatch(MyRequest myRequest,MyResponse myResponse){
		String clazz = urlServletMap.get(myRequest.getUrl());
		Class<MyServlet> myServletClass = null;
		try {
			myServletClass = (Class<MyServlet>) Class.forName(clazz);
			MyServlet myServlet = myServletClass.newInstance();
			myServlet.service(myRequest, myResponse);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
