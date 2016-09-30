package controller;

import com.jfinal.core.Controller;
import handler.StorageHandler;
import model.User;

import java.security.MessageDigest;
import java.util.Date;
import java.util.WeakHashMap;


public class UserController extends Controller {
	private static WeakHashMap<String, Integer> tokenMap = new WeakHashMap<>();
	public void login() throws Exception{
		User user = User.dao.findFirst("select * from user where username='" + getPara("username") + "'");
		if(user.getStr("password").equals(new HashGeneratorUtils().hashString(getPara("password"), "MD5"))){
            String token = new HashGeneratorUtils().hashString(getPara("username") + new Date().getTime(), "MD5");
			tokenMap.put(token, user.getInt("id"));

            setAttr("status", 200);
            setAttr("token", token);
            renderJson();
		}
		else{
            setAttr("status", 403);
            setAttr("error", "用户名或密码错误");
            renderJson();
		}
	}
	public void logout(){
        if(tokenMap.remove(getPara("token")) != null){
            setAttr("status", 200);
            setAttr("result", "登出成功");
            renderJson();
        }
        else{
            setAttr("status", 400);
            setAttr("error", "您未登录");
            renderJson();
        }
	}
	public void signup(){
		try {
			User user = new User();
			user.set("username", getPara("username")).set("password", new HashGeneratorUtils().hashString(getPara("password"), "MD5")).save();
			new StorageHandler("hdfs://localhost:9000", user.getInt("id")).createUserRoot();
            setAttr("status", 200);
            setAttr("result", "注册成功");
            renderJson();
		}
		catch(Exception e){
            setAttr("status", 403);
			setAttr("error", "用户名已存在");
            renderJson();
		}
	}

	public class HashGeneratorUtils {
		private HashGeneratorUtils() {

		}

		public String hashString(String message, String algorithm)
				throws Exception {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));

            return convertByteArrayToHexString(hashedBytes);

		}

		private String convertByteArrayToHexString(byte[] arrayBytes) {
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < arrayBytes.length; i++) {
				stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return stringBuffer.toString();
		}
	}
}
