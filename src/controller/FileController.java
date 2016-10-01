package controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import handler.StorageHandler;
import interceptor.tokenInterceptor;

import java.io.File;
import java.io.IOException;

@Before(tokenInterceptor.class)
public class FileController extends Controller {
    private StorageHandler storage;

    public void setStorage(StorageHandler storage) {
        this.storage = storage;
    }

    public void upload() {
        File file = getFile("file").getFile();
        String destDirPath = getPara("destDirPath");
        try {
            if (storage.exists(destDirPath)) {
                storage.createFile(destDirPath, file);
                setAttr("status", 200);
                setAttr("result", "创建成功");
            } else {
                setAttr("status", 403);
                setAttr("error", "目录不存在");
            }
        } catch (IOException e) {
            setAttr("status", 500);
            setAttr("error", "创建失败");
        } finally {
            renderJson();
        }
    }

    public void download() {

    }

    public void delete() {

    }
}
