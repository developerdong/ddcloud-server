package controller;

import com.jfinal.core.Controller;
import handler.StorageHandler;
import model.User;

public class UserController extends Controller {
	public void login(){
		
	}
	public void logout(){

	}
	public void signup(){
		try {
			User user = new User();
			user.set("username", getPara("username")).set("password", getPara("password")).save();
			new StorageHandler("hdfs://localhost:9000", user.getInt("id")).createUserRoot();
            renderText("注册成功");
		}
		catch(Exception e){
			renderText("用户名已存在");
		}
	}
}
