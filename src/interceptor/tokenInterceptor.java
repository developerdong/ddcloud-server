package interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import controller.FileController;
import controller.UserController;
import handler.StorageHandler;

import java.io.IOException;

public class tokenInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        if(controller.getRequest().getContentType().contains("multipart/form-data")){
            controller.getFile();
        }
        String token = controller.getPara("token");
        if(token == null){
            controller.setAttr("status", 400);
            controller.setAttr("error", "请输入token");
            controller.renderJson();
        }
        else{
            int userId = UserController.tokenValidate(token);
            if(userId == -1){
                controller.setAttr("status", 400);
                controller.setAttr("error", "token不正确");
                controller.renderJson();
            }
            else{
                if(controller instanceof FileController){
                    try {
                        ((FileController) controller).setStorage(new StorageHandler(userId));
                        inv.invoke();
                    } catch (IOException e) {
                        controller.setAttr("status", 500);
                        controller.setAttr("error", "服务器内部错误");
                        controller.renderJson();
                    }
                }
            }
        }
    }
}
