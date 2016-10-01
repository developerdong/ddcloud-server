package controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
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
        UploadFile uploadFile = getFile("file");
        String destDirPath = getPara("destDirPath");
        String destFilePath = destDirPath + "/" + uploadFile.getOriginalFileName();
        try {
            if (storage.exists(destDirPath)) {
                if(!storage.exists(destFilePath)){
                    storage.createFile(destFilePath, uploadFile.getFile());
                    setAttr("status", 200);
                    setAttr("result", "创建成功");
                }
                else{
                    setAttr("status", 403);
                    setAttr("error", "文件已存在");
                }
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
