package jason.lubo.com.recorddingdemo.utls;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;




import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jason.lubo.com.recorddingdemo.model.Videomodel;

/**
 * 可以把继承了Serializable的JavaBean持久化成文件
 *
 * @author wy
 */
public class FileUtils {

    private static File fileDir = null;


    public FileUtils(Context context, String cacheFileDirName) {
        fileDir = context.getApplicationContext().getCacheDir();
        fileDir = new File(fileDir.getAbsolutePath() + "/" + cacheFileDirName);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

    }


    public static boolean hasSdCard() {
        String sDcString = Environment.getExternalStorageState();
        boolean has = false;
        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
            has = true;
        }
        return has;
    }
    public static String getDate(){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);//年
        int month=calendar.get(Calendar.MONTH)+1;//月
        int day=calendar.get(Calendar.DATE);//日
        int minute=calendar.get(Calendar.MINUTE);//时
        int hour=calendar.get(Calendar.HOUR);//分
        int second=calendar.get(Calendar.SECOND);//秒

        String time=String.valueOf(year)+String.valueOf(month)+String.valueOf(day)+String.valueOf(minute)+String.valueOf(hour)+String.valueOf(second);


        return time;
    }
/*    *//**
     * 在SD卡上创建目录
     *
     * @paramdir 目录名称
     * @return 返回目录
     */

    public void getLoadMedia(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)); // id
                String displayName =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)); // 专辑
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)); // 艺术家
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 显示名称
                String mimeType =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                String resolution =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public static String getvideopath(){
    String path=null;
    if(hasSdCard()){
   fileDir=  Environment.getExternalStorageDirectory();//根目录

        File file=new File(fileDir.getPath()+"/MVIDEO");
        if (!file.exists()){
            file.mkdirs();
        }
        path=file+"/"+getDate()+".3gp";

    }else {
        fileDir=  Environment.getDataDirectory();
        File file=new File(fileDir.getPath()+"/MVIDEO");
        if (!file.exists()) {
            if (file.mkdirs()) {
                Log.d("BUG","true");

            } else
                Log.d("BUG","false");
        }
        path=file+"/"+getDate()+".3gp";
    }
    return path;
}
    /**
     * 从缓存文件夹中读取图像的方法
     */
    public Bitmap getFileBitmap(String imageUrl) {
        Bitmap bitmap = null;
        // 图像名称(取url后面部分做为文件名)
        String bitmapName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        // 本地缓存文件目录下的所有文件
        File[] cacheFiles = fileDir.listFiles();
        // 目录中不存在文件
        if (cacheFiles == null || cacheFiles.length <= 0) {
            return null;
        }
        // 图像文件 (默认为空)
        File bitmapFile = new File(fileDir, bitmapName);
        if (!bitmapFile.isFile()) {
            return null;
        }
        bitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    /**
     * 保存图像至缓存文件夹中的方法
     */
    public void saveCacheFile(String imageURL, Bitmap bitmap) {

        // 图像的名称 (取自己图像网络地址名后部分)
        String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
        // 创建出些图像在 本地缓存 的文件对象 , 再用流+ bitmap.compress()写入文件中去
        File bitmapFile = new File(fileDir, bitmapName);
        if (!bitmapFile.exists()) {
            try {
                bitmapFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean saveBitmap(Bitmap bitmap, String path)
    {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }


        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 90, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 存储到手机存储，卸载程序文件消失
     *
     * @param obj
     * @param context
     * @return
     */
    public static boolean save(Object obj, Context context) {
        boolean result = false;
        try {
            FileOutputStream stream = context.openFileOutput(obj.getClass()
                    .getName() + ".cs", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(stream);
            oos.writeObject(obj);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * 存储到手机存储，卸载程序文件消失
     *
     * @param obj
     * @param name
     * @param context
     * @return
     */
    public static boolean save(Object obj, String name, Context context) {
        boolean result = false;
        try {
            FileOutputStream stream = context.openFileOutput(name + ".cs",
                    Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(stream);
            oos.writeObject(obj);

            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static boolean save(Object obj, String name) {
        boolean result = false;
        File sdFile = new File(getSavePath(), name + ".cs");
        try {
            FileOutputStream fos = new FileOutputStream(sdFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);// 写入
            fos.close(); // 关闭输出流
            result = true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static Object get(String fname) {
        Object obj = new Object();
        obj = null;
        File sdFile = new File(getSavePath(), fname + ".cs");
        try {
            FileInputStream fis = new FileInputStream(sdFile); // 获得输入流
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj = ois.readObject();
            ois.close();
        } catch (Exception e) {
            // TODO: handle exception
            obj = null;
        }
        return obj;
    }

    public static Object get(String fname, Context context) {
        Object obj = new Object();
        obj = null;
        try {
            FileInputStream stream = context.openFileInput(fname + ".cs");
            ObjectInputStream ois = new ObjectInputStream(stream);
            obj = ois.readObject();
        } catch (Exception e) {
            // TODO: handle exception
            obj = null;
        }
        return obj;
    }

    public static String getSdPath() {
        String path = "";
        if (hasSdCard()) {
            File pathFile = Environment
                    .getExternalStorageDirectory();
            path = pathFile.getPath();
        }
        return path;

    }

    public static String getRootFilePath() {
        if (hasSdCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/";// filePath:/sdcard/
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath:
            // /data/data/
        }
    }

    public static String getSaveFilePath() {
        if (hasSdCard()) {
            return getRootFilePath() + "com.xxtv/caches/";
        } else {
            return getRootFilePath() + "com.xxtv/caches/";
        }
    }

    public static String getSavePicPath() {
        if (hasSdCard()) {
            return getRootFilePath() + "com.xxtv/Pic/";
        } else {
            return getRootFilePath() + "com.xxtv/Pic/";
        }
    }

    public static String getSavePath() {
        if (hasSdCard()) {
            return getRootFilePath() + "xxtv/";
        } else {
            return getRootFilePath() + "xxtv/";
        }
    }

    /**
     * 获得sd卡string
     *
     * @return
     */
    public static String getSDString() {
        if (hasSdCard()) {
            String sdaString = getSDAvailableSize();
            String sdtString = getSDTotalSize();
            String sdAvalible = formateRate(sdaString);
            String sdTotal = formateRate(sdtString);
            return String.format("SD卡空间剩余%sGB，共%sGB", sdAvalible, sdTotal);
        } else {
            String sdaString = getRomAvailableSize();
            String sdtString = getRomTotalSize();
            String sdAvalible = formateRate(sdaString);
            String sdTotal = formateRate(sdtString);
            return String.format("ROM空间剩余%sGB，共%sGB", sdAvalible, sdTotal);
        }

    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static String getSDTotalSize() {
        if (hasSdCard()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return (double) (blockSize * totalBlocks) / (1024 * 1024 * 1024)
                    + "";
        } else {
            return "0";
        }
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static String getSDAvailableSize() {
        if (hasSdCard()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return (double) (blockSize * availableBlocks)
                    / (1024 * 1024 * 1024) + "";
        } else {
            return "0";
        }
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public static String getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return (double) (blockSize * totalBlocks) / (1024 * 1024 * 1024) + "";
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (double) (blockSize * availableBlocks) / (1024 * 1024 * 1024)
                + "";
    }

public static boolean IsExist(String path){

    File f = new File(path);
    if (f == null) {


        return false;
    }
    if (!f.exists() || !f.isDirectory()) {  // 判断目录是否存在
        System.out.println("文件查找失败：" + "不是一个目录！");
        return false;
    }

    // 列出所有文件
    File[] files = f.listFiles();
    // 获取配置文件
    Map info = new HashMap();
    if (files != null) {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.getName().indexOf("png") >= 0) {
                // 只取png文件
            }
        }
        // 判断是否存在动画资源
        if (files.length == 0) {

            return false;
        }

    }else {
        return false;
    }
    return true;
}

    public static boolean isExists(String path) {
        boolean flag = false;
        if (new File(path).exists()) {
            flag = true;
        }
        return flag;
    }

    // 格式化 保留两位
    public static String formateRate(String rateStr) {
        if (rateStr.indexOf(".") != -1) {
            // 获取小数点的位置
            int num = 0;
            num = rateStr.indexOf(".");

            // 获取小数点后面的数字 是否有两位 不足两位补足两位
            String dianAfter = rateStr.substring(0, num + 1);
            String afterData = rateStr.replace(dianAfter, "");
            if (afterData.length() < 2) {
                afterData = afterData + "0";
            } else {
                // afterData = afterData;
            }
            return rateStr.substring(0, num) + "." + afterData.substring(0, 2);
        } else {
            if (rateStr == "1") {
                return "100";
            } else {
                return rateStr;
            }
        }
    }

    public static boolean createFile(String path, String fileName, byte[] content) throws IOException {
        return createFile(new File(path + "/" + fileName), content);
    }

    public static boolean createFile(String path, byte[] content) throws IOException {
        return createFile(new File(path), content);
    }

    public static boolean createFile(File f, byte[] content) throws IOException {
        File dir = f.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fout = new FileOutputStream(f);
        BufferedOutputStream out = new BufferedOutputStream(fout);
        out.write(content);
        out.flush();
        out.close();
        fout.close();
        return true;
    }

    public static byte[][] getFileContents(File path, FilenameFilter filter) throws IOException {
        if (!path.exists()) return new byte[0][];
        File[] files = path.listFiles(filter);
        byte[][] data = new byte[files.length][];
        for (int i = 0; i < files.length; ++i) {
            data[i] = getBytes(files[i]);
        }
        return data;
    }

    public static byte[][] decodeConcatenatedFiles(File f) throws IOException {
        DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
        int len = din.readInt();
        byte[][] out = new byte[len][];
        for (int i = 0; i < len; ++i) {
            byte[] c = new byte[din.readInt()];
            din.readFully(c);
            out[i] = c;
        }
        return out;
    }

    public static void concatFiles(Collection<File> files, File output) throws FileNotFoundException, IOException {
        DataOutputStream dout = new DataOutputStream(new FileOutputStream(output));
        dout.writeInt(files.size());
        for (File f : files) {
            byte[] content = getBytes(f);
            dout.writeInt(content.length);
            dout.write(content);
        }
        dout.flush();
    }

    public static void concatFiles(File[] files, File output) throws FileNotFoundException, IOException {
        DataOutputStream dout = new DataOutputStream(new FileOutputStream(output));
        dout.writeInt(files.length);
        for (File f : files) {
            byte[] content = getBytes(f);
            dout.writeInt(content.length);
            dout.write(content);
        }
        dout.flush();
    }

    public static void getAllFilesRecursive(File dir, ArrayList<File> result) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                getAllFilesRecursive(f, result);
            }
            result.add(f);
        }
    }


    public static void createFile(String fileName, byte[] header, byte[] data) throws IOException {
        FileOutputStream fout = new FileOutputStream(fileName);
        fout.write(header);
        fout.flush();
        fout.write(data);
        fout.flush();
        fout.close();
    }


    public static void deleteFile(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                file.delete();
            } else {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
                file.delete();
            }
        }
    }

    public static void deleteFile(String filePath) {
        deleteFile(new File(filePath));
    }

    public static void clearFolder(String path) {
        File[] fs = new File(path).listFiles();
        for (File f : fs) {
            f.delete();
        }
    }

    public static byte[] getBytes(File source) throws IOException {
        long length = source.length();
        if (length == 0) return new byte[0];
        FileInputStream fin = new FileInputStream(source);
        BufferedInputStream in = new BufferedInputStream(fin, (int) length);
        byte[] data = new byte[(int) source.length()];
        in.read(data, 0, data.length);
        fin.close();
        return data;
    }

    public static void copy(File source, File dest) throws IOException {
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        FileInputStream fin = new FileInputStream(source);
        BufferedInputStream in = new BufferedInputStream(fin);
        FileOutputStream fout = null;
        if (dest.isDirectory()) {
            fout = new FileOutputStream(dest.getAbsolutePath() + "/" + source.getName());
        } else {
            fout = new FileOutputStream(dest);
        }
        BufferedOutputStream out = new BufferedOutputStream(fout);
        int a;
        while ((a = in.read()) != -1) {
            out.write(a);
        }
        out.flush();
        fout.flush();
        fout.close();
        fin.close();
    }

    public static void copy(File source, String dest) throws IOException {
        FileInputStream fin = new FileInputStream(source);
        BufferedInputStream in = new BufferedInputStream(fin);
        FileOutputStream fout = new FileOutputStream(dest);
        BufferedOutputStream out = new BufferedOutputStream(fout);
        int a;
        while ((a = in.read()) != -1) {
            out.write(a);
        }
        out.flush();
        fout.flush();
        fout.close();
        fin.close();
    }

    public static File[] getFilesWithExtension(File[] files, String ext) {
        ArrayList<File> fs = new ArrayList<File>();
        for (File f : files) {
            if (f.getName().toLowerCase().endsWith(ext)) {
                fs.add(f);
            }
        }
        File[] data = new File[fs.size()];
        fs.toArray(data);
        return data;
    }

    public static void copyTree(String source, String dest) throws IOException {
        File target = new File(dest);
        if (!target.exists()) {
            target.mkdirs();
        }
        copyTree(new File(source), target);
    }

    public static void copyTree(File dir, File targetDir) throws IOException {
        File[] files = dir.listFiles();
        for (File f : files) {
            System.out.println(f);
            if (f.isDirectory()) {
                File subDir = new File(targetDir.getAbsolutePath() + "/" + f.getName());
                if (!subDir.exists()) {
                    subDir.mkdirs();
                }
                copyTree(f, subDir);
            } else {
                copy(f, targetDir.getAbsolutePath() + "/" + f.getName());
            }
        }
    }

    public static void copy(String source, String dest) throws IOException {
        FileInputStream fin = new FileInputStream(source);
        BufferedInputStream in = new BufferedInputStream(fin);
        FileOutputStream fout = new FileOutputStream(dest);
        BufferedOutputStream out = new BufferedOutputStream(fout);
        int a;
        while ((a = in.read()) != -1) {
            out.write(a);
        }
        out.flush();
        fout.flush();
        fout.close();
        fin.close();
    }

    /**
     * 递归查找文件
     *
     * @param baseDirName    查找的文件夹路径
     * @param targetFileName 需要查找的文件名
     * @param fileList       查找到的文件集合
     */
    public static void findFiles(String baseDirName, String targetFileName, List<File> fileList) {

        File baseDir = new File(baseDirName);       // 创建一个File对象
        if (!baseDir.exists() || !baseDir.isDirectory()) {  // 判断目录是否存在
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
            return;
        }
        String tempName = null;
        //判断目录是否存在
        File tempFile;
        File[] files = baseDir.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            if (tempFile.isDirectory()) {
                findFiles(tempFile.getAbsolutePath(), targetFileName, fileList);
            } else if (tempFile.isFile()) {
                tempName = tempFile.getName();
                if (wildcardMatch(targetFileName, tempName)) {
                    // 匹配成功，将文件名添加到结果集


                    fileList.add(tempFile.getAbsoluteFile());
                }
            }
        }
    }

    /**
     * 通配符匹配
     *
     * @param pattern 通配符模式
     * @param str     待匹配的字符串
     * @return 匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                //通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                //通配符问号?表示匹配任意一个字符


                strIndex++;
                if (strIndex > strLength) {
                    //表示str中已经没有字符匹配?了。


                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }

    public static boolean isVideoFile(File file) {
        String fileName = file.getName();
        if (fileName != null && !fileName.isEmpty()) {
            String filetype = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            switch (filetype) {
                case "avi":
                case "wmv":
                case "3gp":
                case "mpeg":
                case "mkv":
                case "flv":
                case "rmvb":
                case "mp4":
                    return true;
            }
        }
        return false;
    }

    public static boolean isImageFile(File file) {
        String fileName = file.getName();
        if (fileName != null && !fileName.isEmpty()) {
            String filetype = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            switch (filetype) {
                case "jpg":
                case "jpeg":
                case "png":
                case "bmp":
                case "gif":
                    return true;
            }
        }
        return false;
    }

    //根据byte数组，生成文件


    public static void createFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void fileAppend(String fileName, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //以键值对的形式保存到文件中
    public static void saveNameValuePairs(String filePath, Map<String, String> pairsMap) {
        if (!new File(filePath).getParentFile().exists()) {
            new File(filePath).getParentFile().mkdirs();
        }
        FileWriter w = null;
        try {
            w = new FileWriter(new File(filePath));
            for (String key : pairsMap.keySet()) {
                w.write(key + "=" + pairsMap.get(key) + "\r\n");
            }
        } catch (Exception ex) {
        }
        try {
            w.flush();
        } catch (Exception ex) {
        }
        try {
            w.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //从文件中加载所有的键值对
    public static Map<String, String> getNameValuePairs(String filePath) {
        Map<String, String> resultMap = new LinkedHashMap<String, String>();
        try {
            FileReader r = new FileReader(new File(filePath));
            LineNumberReader lines = new LineNumberReader(r);
            String line = null;
            while ((line = lines.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                int i = line.indexOf("=");
                if (i >= 0) {
                    String n = line.substring(0, i);
                    String v = line.substring(i + 1, line.length());
                    resultMap.put(n, v);
                }
            }
            r.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultMap;
    }

    /***
     * 在SD卡上创建文件
     *
     * @param fileName 文件名
     * @param dir      文件存储目录
     * @return 文件
     * @throws IOException
     */
    public File createFileInSDCard(String fileName, String dir) throws IOException {
        String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        //File.separator是系统有关的默认名称分隔符“/”
        File file = new File(SDCardRoot + File.separator + dir + File.separator + fileName);
        //System.out.println(file);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dir 目录名称
     * @return 返回目录
     */
    public File createSDDir(String dir) {
        String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dirFile = new File(SDCardRoot + File.separator + dir + File.separator);
        //System.out.println(dirFile);
        dirFile.mkdirs();
        return dirFile;
    }

    /***
     * 判断文件是否存在
     *
     * @param fileNameString 文件名
     * @param
     * @return 存在返回true 不存在返回false
     */
    public boolean isFileExist(String fileNameString, String path) {
        String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(SDCardRoot + "/" + path + File.separator + fileNameString);
        //System.out.println("isExist ->" + file);
        return file.exists();
    }

    /***
     * 将InputStream里面的数据写入到SD卡的文件中
     *
     * @param path           文件的路径
     * @param fileNameString 文件名
     * @param inputStream    数据流
     * @return
     */
    public File write2SDFromInput(String path, String fileNameString, InputStream inputStream) {
        File file = null;
        OutputStream outputStream = null;
        try {
            createSDDir(path);
            file = createFileInSDCard(fileNameString, path);

            outputStream = new FileOutputStream(file);
            //创建一个4*1024大小的缓冲区
            byte buffer[] = new byte[4 * 1024];
            int temp;
            //循环读取InputStream里的数据
            while ((temp = inputStream.read(buffer)) != -1) {
                //把流按照buffer的大小写入到文件中
                outputStream.write(buffer, 0, temp);

            }
            //提交缓冲区的内容
            outputStream.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();

            } catch (Exception e2) {
                // TODO: handle exception
                e2.printStackTrace();
            }
        }

        return file;

    }


  /**
     * 写文件到sd卡上
     *
     * @param context
     */
    public static   void writeFileToSD(String context) {
      //使用RandomAccessFile 写文件 还是蛮好用的..推荐给大家使用...
       String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return;
           }
       try {
        /*     String pathName = "/sdcard/";
           String fileName = "log.txt";
            File path = new File(pathName);
            File file = new File(pathName + fileName);
           if (!path.exists()) {
           Log.d("TestFile", "Create the path:" + pathName);
             path.mkdir();
               }
           if (!file.exists()) {
             Log.d("TestFile", "Create the file:" + fileName);
          file.createNewFile();
            }
          RandomAccessFile raf = new RandomAccessFile(file, "rw");
      raf.seek(file.length());
 raf.write(context.getBytes());
raf.close();*/
//注释的也是写文件..但是每次写入都会把之前的覆盖..
String pathName = "/sdcard/";
 String fileName = "log.txt";
 File path = new File(pathName);
           File file = new File(pathName + fileName);
if (!path.exists()) {
Log.d("TestFile", "Create the path:" + pathName);
path.mkdir();
 }
if (!file.exists()) {
Log.d("TestFile", "Create the file:" + fileName);
file.createNewFile();
 }
 FileOutputStream stream = new FileOutputStream(file);
 String s = context;
 byte[] buf = s.getBytes();
 stream.write(buf);
 stream.close();
  } catch (Exception e) {
 Log.e("TestFile", "Error on writeFilToSD.");
}
    }
}