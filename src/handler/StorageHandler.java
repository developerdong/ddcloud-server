package handler;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.util.LinkedList;

public class StorageHandler {
    private final Configuration conf;
    private final FileSystem hdfs;
    private final String userRoot;
    public StorageHandler(int userId) throws IOException{
        this.conf = new Configuration();
        this.hdfs = FileSystem.get(conf);
        this.userRoot = '/' + String.valueOf(userId);
    }
    private String getRealPath(String path){
        return userRoot + path;
    }
    public boolean createUserRoot() throws IOException{
        return hdfs.mkdirs(new Path(userRoot));
    }
    public String[] list(String dirPath) throws IOException{
        FileStatus status[] = hdfs.listStatus(new Path(getRealPath(dirPath)));
        LinkedList<String> list = new LinkedList<String>();
        for(int i=0; i<status.length; i++){
            String path = status[i].getPath().toString();
            list.add(path.substring(path.lastIndexOf('/')+1));
        }
        return list.toArray(new String[0]);
    }
    public void createFile(String filePath, byte[] fileContent) throws IOException{
        FSDataOutputStream fileOutputStream = hdfs.create(new Path(getRealPath(filePath)));
        fileOutputStream.write(fileContent);
        fileOutputStream.close();
    }
    public boolean createDir(String dirPath) throws IOException{
        return hdfs.mkdirs(new Path(getRealPath(dirPath)));
    }
    public File getFile(String filePath) throws IOException{
        File tempFile = File.createTempFile(filePath.substring(0, filePath.lastIndexOf('.')), filePath.substring(filePath.lastIndexOf('.')));
        hdfs.copyToLocalFile(new Path(filePath), new Path(tempFile.getAbsolutePath()));
        return tempFile;
    }
    public boolean rename(String oldPath, String newPath) throws IOException{
        return hdfs.rename(new Path(getRealPath(oldPath)), new Path(getRealPath(newPath)));
    }
    public boolean delete(String path) throws IOException{
        return hdfs.delete(new Path(getRealPath(path)), true);
    }
}
