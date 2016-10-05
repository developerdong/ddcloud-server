package interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import controller.FileController;
import controller.UserController;
import handler.StorageHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TokenInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        HttpServletRequest request = controller.getRequest();
        if((request.getMethod().equals("POST"))&&(request.getContentType().contains("multipart/form-data"))){
            controller.getFile();
        }
        String token = controller.getPara("token");
        if(token == null){
            controller.setAttr("status", 400);
            controller.setAttr("result", "请登录");
            controller.renderJson();
        }
        else{
            int userId = UserController.tokenValidate(token);
            if(userId == -1){
                controller.setAttr("status", 400);
                controller.setAttr("result", "请登录");
                controller.renderJson();
            }
            else{
                if(controller instanceof FileController){
                    try {
                        ((FileController) controller).setStorage(new StorageHandler(userId));
                        inv.invoke();
                    } catch (IOException e) {
                        controller.setAttr("status", 500);
                        controller.setAttr("result", "服务器内部错误");
                        controller.renderJson();
                    }
                }
            }
        }
    }
}
