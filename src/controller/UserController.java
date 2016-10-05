package controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import config.ServerConfig;
import handler.StorageHandler;
import model.User;

import java.security.MessageDigest;
import java.util.Date;


public class UserController extends Controller {
    public static int tokenValidate(String token){
        return CacheKit.get(ServerConfig.CACHE_NAME, token) != null?CacheKit.get(ServerConfig.CACHE_NAME, token):-1;
    }
	public void login() throws Exception{
        String username = getPara("username");
        String password = getPara("password");
        if((username == null)||(password == null)){
            setAttr("status", 400);
            setAttr("result", "请输入用户名和密码");
            renderJson();
        }
        else{
            User user = User.dao.findFirst("select * from user where username='" + username + "'");
            if(user.getStr("password").equals(new HashGeneratorUtils().hashString(password, "MD5"))){
                String token = new HashGeneratorUtils().hashString(username + new Date().getTime(), "MD5");
                CacheKit.put(ServerConfig.CACHE_NAME, token, user.getInt("id"));

                setAttr("status", 200);
                setAttr("token", token);
                setAttr("result", "登录成功");
                renderJson();
            }
            else{
                setAttr("status", 403);
                setAttr("result", "用户名或密码错误");
                renderJson();
            }
        }
	}
	public void logout(){
        if(CacheKit.get(ServerConfig.CACHE_NAME, getPara("token")) != null){
            CacheKit.remove(ServerConfig.CACHE_NAME, getPara("token"));
            setAttr("status", 200);
            setAttr("result", "登出成功");
            renderJson();
        }
        else{
            setAttr("status", 400);
            setAttr("result", "您未登录");
            renderJson();
        }
	}
	public void signup(){
		try {
            String username = getPara("username");
            String password = getPara("password");
            if((username == null)||(password == null)){
                setAttr("status", 400);
                setAttr("result", "请输入用户名和密码");
                renderJson();
            }
            else {
                User user = new User();
                user.set("username", username).set("password", new HashGeneratorUtils().hashString(password, "MD5")).save();
                new StorageHandler(ServerConfig.HADOOP_SERVER_URI, user.getInt("id")).createUserRoot();
                setAttr("status", 200);
                setAttr("result", "注册成功");
                renderJson();
            }
		}
		catch(Exception e){
            setAttr("status", 403);
			setAttr("result", "用户名已存在");
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
