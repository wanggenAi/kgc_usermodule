package com.zb.util.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

public class FtpUtil {
    private static FTPClient ftp;
    // ftp的实体类
    private static Ftp f;

    static {
        Properties p = new Properties();
        InputStream inputStream = FtpUtil.class.getClassLoader().getResourceAsStream("config/ftp.properties");
        try {
            p.load(inputStream);
            f = new Ftp();
            f.setIpAddr(p.getProperty("ftp.host"));
            f.setUserName(p.getProperty("ftp.username"));
            f.setPwd(p.getProperty("ftp.password"));
            f.setPath(p.getProperty("ftp.path"));
            f.setPort(Integer.parseInt(p.getProperty("ftp.port")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取ftp连接
     *
     * @return
     * @throws Exception
     */
    public static boolean connectFtp() throws Exception {
        ftp = new FTPClient();
        boolean flag = false;
        int reply;
        if (f.getPort() == null) {
            ftp.connect(f.getIpAddr(), 21);
        } else {
            ftp.connect(f.getIpAddr(), f.getPort());
        }
        ftp.login(f.getUserName(), f.getPwd());
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return flag;
        }
        ftp.changeWorkingDirectory(f.getPath());
        flag = true;
        return flag;
    }

    /**
     * 关闭ftp连接
     */
    public static void closeFtp() {
        if (ftp != null && ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ftp上传文件
     *
     * @param f
     * @throws Exception
     */
    public static void upload(File f) throws Exception {
        if (f.isDirectory()) {
            ftp.makeDirectory(f.getName());
            ftp.changeWorkingDirectory(f.getName());
            String[] files = f.list();
            for (String fstr : files) {
                File file1 = new File(f.getPath() + "/" + fstr);
                if (file1.isDirectory()) {
                    upload(file1);
                    ftp.changeToParentDirectory();
                } else {
                    File file2 = new File(f.getPath() + "/" + fstr);
                    FileInputStream input = new FileInputStream(file2);
                    ftp.storeFile(file2.getName(), input);
                    input.close();
                }
            }
        } else {
            File file2 = new File(f.getPath());
            FileInputStream input = new FileInputStream(file2);
            ftp.storeFile(file2.getName(), input);
            input.close();
        }
    }

    /**
     * ftp上传文件
     *
     * @param part
     * @throws Exception
     */
    public static String uploadHeaderImg(Part part) throws Exception {
        connectFtp();
        String pName = part.getSubmittedFileName();
        String uniqueName = Base64.getEncoder().encodeToString(pName.getBytes("utf-8")) +
                pName.substring(pName.lastIndexOf("."));
        InputStream input = part.getInputStream();
        ftp.storeFile(uniqueName, input);
        input.close();
        closeFtp();
        return uniqueName;
    }


    public static void main(String[] args) throws Exception {
        FtpUtil.connectFtp();
        File file = new File("D:/1d35791c70b674ef138c862da37b60c5.jpg");
        FtpUtil.upload(file);//把文件上传在ftp上
        System.out.println("上传文件完成。。。。");
    }
}
