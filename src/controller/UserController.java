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

			render("注册成功", 200);
		}
		catch(Exception e){
			render("注册失败", 503);
		}
	}
}
