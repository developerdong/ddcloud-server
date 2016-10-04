package controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import handler.StorageHandler;
import interceptor.TokenInterceptor;

import java.io.IOException;

@Before(TokenInterceptor.class)
public class FileController extends Controller {
    private StorageHandler storage;

    public void setStorage(StorageHandler storage) {
        this.storage = storage;
    }
    public void createDir() throws IOException{
        String dirPath = getPara("dirPath");
        if(storage.exists(dirPath)){
            setAttr("status", 403);
            setAttr("result", "路径已存在");
            renderJson();
        }
        else{
            if(storage.createDir(dirPath)){
                setAttr("status", 200);
                setAttr("result", "创建成功");
                renderJson();
            }
            else{
                setAttr("status", 500);
                setAttr("result", "创建失败");
                renderJson();
            }
        }
    }
    public void list() throws IOException{
        String dirPath = getPara("dirPath");
        if(!storage.exists(dirPath)){
            setAttr("status", 403);
            setAttr("result", "路径不存在");
            renderJson();
        }
        else{
            StorageHandler.ItemMetadata[] items = storage.list(dirPath);
            setAttr("status", 200);
            setAttr("result", items);
            renderJson();
        }
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
                    setAttr("result", "文件已存在");
                }
            } else {
                setAttr("status", 403);
                setAttr("result", "目录不存在");
            }
        } catch (IOException e) {
            setAttr("status", 500);
            setAttr("result", "创建失败");
        } finally {
            renderJson();
        }
    }

    public void download() throws IOException{
        String filePath = getPara("filePath");
        if(!storage.exists(filePath)){
            setAttr("status", 400);
            setAttr("result", "路径不存在");
            renderJson();
        }
        else{
            renderFile(storage.getFile(filePath));
        }
    }

    public void rename() throws IOException{
        String oldPath = getPara("oldPath");
        String newPath = getPara("newPath");
        if(storage.exists(newPath)){
            setAttr("status", 400);
            setAttr("result", "目标路径已存在");
        }
        else if(storage.rename(oldPath, newPath)){
            setAttr("status", 200);
            setAttr("result", "重命名成功");
        }
        else{
            setAttr("status", 400);
            setAttr("result", "重命名失败，请检查路径");
        }
        renderJson();
    }

    public void move() throws IOException{
        String oldPath = getPara("oldPath");
        String newPath = getPara("newPath");
        if(storage.rename(oldPath, newPath)){
            setAttr("status", 200);
            setAttr("result", "移动成功");
        }
        else{
            setAttr("status", 400);
            setAttr("result", "移动失败，请检查路径");
        }
        renderJson();
    }

    public void delete() throws IOException{
        String path = getPara("path");
        if(storage.delete(path)){
            setAttr("status", 200);
            setAttr("result", "删除成功");
        }
        else{
            setAttr("status", 400);
            setAttr("result", "删除失败，请检查路径");
        }
        renderJson();
    }
}
