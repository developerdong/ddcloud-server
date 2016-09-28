package config;

import com.jfinal.config.*;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;

import controller.*;
import model.User;


public class ServerConfig extends JFinalConfig{
	public void configConstant(Constants me){
		me.setDevMode(true);
	}
	public void configRoute(Routes me){
		me.add("/user", UserController.class);
		me.add("/file", FileController.class);
	}
	public void configPlugin(Plugins me){
		final String MYSQL_CONNECTION = "jdbc:mysql://127.0.0.1:3306/yun";
		final String MYSQL_USERNAME = "root";
		final String MYSQL_PASSWORD = "1234567890";

		C3p0Plugin cp = new C3p0Plugin(MYSQL_CONNECTION, MYSQL_USERNAME, MYSQL_PASSWORD);
		ActiveRecordPlugin arp =new ActiveRecordPlugin(cp);
		me.add(cp);
		me.add(arp);

		arp.addMapping("user", User.class);
		
	}
	public void configInterceptor(Interceptors me){
		
	}
	public void configHandler(Handlers me){
		
	}
}
