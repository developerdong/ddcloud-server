package controller;

import com.jfinal.core.Controller;
import model.User;

public class UserController extends Controller {
	public void login(){
		
	}
	public void logout(){

	}
	public void signup(){
		try {
			new User().set("username", getPara("username")).set("password", getPara("password")).save();
			render("注册成功", 200);
		}
		catch(Exception e){
			render("注册失败", 503);
		}
	}
}
