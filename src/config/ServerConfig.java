package config;

import com.jfinal.config.*;

import controller.*;

public class ServerConfig extends JFinalConfig{
	public void configConstant(Constants me){
		me.setDevMode(true);
	}
	public void configRoute(Routes me){
		me.add("/user", UserController.class);
		me.add("/file", FileController.class);
	}
	public void configPlugin(Plugins me){
		
	}
	public void configInterceptor(Interceptors me){
		
	}
	public void configHandler(Handlers me){
		
	}
}
