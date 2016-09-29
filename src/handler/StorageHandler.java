package handler;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.net.URI;
import java.util.LinkedList;

public class StorageHandler {
    private final Configuration conf;
    private final FileSystem hdfs;
    private final String userRoot;
    public StorageHandler(String serverURI, int userId) throws IOException{
        this.conf = new Configuration();
        this.hdfs = FileSystem.get(URI.create(serverURI), conf);
        this.userRoot = "/" + String.valueOf(userId);
    }
    private String getRealPath(String path){
        return userRoot + path;
    }
    private String getParentDirPath(String path){
        return path.substring(0, path.lastIndexOf('/'));
    }
    private String getFileName(String filePath){
        return filePath.substring(filePath.lastIndexOf('/')+1);
    }
    private String getFileExtension(String filePath){
        return filePath.substring(filePath.lastIndexOf('.'));
    }
    private String getFileNameWithoutExtension(String filePath){
        return filePath.substring(filePath.lastIndexOf('/')+1, filePath.lastIndexOf('.'));
    }

    public boolean createUserRoot() throws IOException{
        return hdfs.mkdirs(new Path(userRoot));
    }
    public ItemMetadata[] list(String dirPath) throws IOException{
        FileStatus status[] = hdfs.listStatus(new Path(getRealPath(dirPath)));
        LinkedList<ItemMetadata> itemList = new LinkedList<ItemMetadata>();
        for(int i=0; i<status.length; i++){
            String path = status[i].getPath().toString();
            itemList.add(new ItemMetadata(path.substring(path.lastIndexOf('/') + 1), status[i].getLen(), status[i].isDir(), status[i].getModificationTime(), status[i].getAccessTime()));
        }
        return itemList.toArray(new ItemMetadata[0]);
    }
    public void createFile(String destDirPath, File file) throws IOException {
        hdfs.copyFromLocalFile(false, true, new Path(file.getAbsolutePath()), new Path(getRealPath(destDirPath)));
    }
    public boolean createDir(String dirPath) throws IOException{
        return hdfs.mkdirs(new Path(getRealPath(dirPath)));
    }
    public File getFile(String filePath) throws IOException{
        File tempFile = new File("/tmp", getFileNameWithoutExtension(filePath) + getFileExtension(filePath));
        hdfs.copyToLocalFile(new Path(getRealPath(filePath)), new Path(tempFile.getAbsolutePath()));
        return tempFile;
    }
    public boolean rename(String oldPath, String newPath) throws IOException{
        return hdfs.rename(new Path(getRealPath(oldPath)), new Path(getRealPath(newPath)));
    }
    public boolean delete(String path) throws IOException{
        return hdfs.delete(new Path(getRealPath(path)), true);
    }

    public class ItemMetadata {
        private String name;
        private long length;
        private boolean isDir;
        private long modificationTime;
        private long accessTime;

        public ItemMetadata(String name, long length, boolean isDir, long modificationTime, long accessTime) {
            this.name = name;
            this.length = length;
            this.isDir = isDir;
            this.modificationTime = modificationTime;
            this.accessTime = accessTime;
        }

        public String getName() {
            return name;
        }

        public long getLength() {
            return length;
        }

        public boolean isDir() {
            return isDir;
        }

        public long getModificationTime() {
            return modificationTime;
        }

        public long getAccessTime() {
            return accessTime;
        }
    }
}
