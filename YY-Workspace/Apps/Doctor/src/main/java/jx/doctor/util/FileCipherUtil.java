package jx.doctor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import lib.ys.YSLog;

/**
 * 实现简单的文件加密解密工具
 *
 * @author CaiXiang
 * @since 2017/7/13
 */
public class FileCipherUtil {

    public static final String KEncrypt = "asdads;";

    /**
     * 加密后的文件的后缀
     */
    public static final String CIPHER_TEXT_SUFFIX = "cipher";

    /**
     * 加密
     *
     * @param fileName 明文文件名
     * @return
     */
    public static boolean encrypt(String fileName, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
//            long fileLength = randomFile.length();
            // 将写文件指针移到文件头。
            randomFile.seek(0);
            randomFile.writeBytes(content);
            randomFile.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            YSLog.d("www", "加密出错: " + e.getMessage());
            return false;
        }
    }


    /**
     * 解密
     *
     * @param filePath 密文文件绝对路径
     * @return
     */
    public static boolean decrypt(String filePath, CipherListener listener) {
        try {
            File fromFile = new File(filePath);
            File toFile = new File(filePath + CIPHER_TEXT_SUFFIX);
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            fosfrom.skip(KEncrypt.length());
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            YSLog.d("www", "解密出错: " + e.getMessage());
            return false;
        }
    }

    /**
     * 用于加解密进度的监听器
     */
    public interface CipherListener {
        void onProgress(long current, long total);
    }

}
