package controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import handler.StorageHandler;
import interceptor.tokenInterceptor;

@Before(tokenInterceptor.class)
public class FileController extends Controller {
    private StorageHandler storage;

    public void setStorage(StorageHandler storage) {
        this.storage = storage;
    }

    public void upload(){

	}
	public void download(){
		
	}
	public void delete(){
		
	}
}
