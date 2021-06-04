package Util;


import Entity.SftpConfigInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static Util.JsonUtil.readJsonFile;

public class SftpConfig {
    private String privateKey;// 密钥文件路径
    private String passphrase;// 密钥口令
    private String movePath;
    SftpMonitor upMonitor;
    private static ChannelSftp sftp = null;


    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getMovePath() {
        return movePath;
    }

    public void setMovePath(String movePath) {
        this.movePath = movePath;
    }

    public SftpConfig() {
    }

    /**
     * 连接sftp
     *
     * @return
     */
    public ChannelSftp getConnection() {
        JSch jSch = new JSch();
        Channel channel = null;
        try {
            String path = "src\\main\\resources\\Json\\sftpConfig.json";
            String s = readJsonFile(path);
            JSONObject jsonObject = JSON.parseObject(s);
            SftpConfigInfo configInfo=JSON.toJavaObject(jsonObject,SftpConfigInfo.class);
            String username=configInfo.getUsername();
            String host=configInfo.getHost();
            int port=configInfo.getPort();
            String password=configInfo.getPassword();
            // 根据用户名，主机ip，端口获取一个Session对象
            Session session = jSch.getSession(username, host, port);
            if (password != null && !"".equals(password)) {
                // 打开sftp的通道
                session.setPassword(password);
            }
            Properties config = new Properties();
            // SSH连接慢的问题
            config.put("userauth.gssapi-with-mic", "no");
            // 防止远程主机公钥改变导致 SSH 连接失败
            config.put("StrictHostKeyChecking", "no");
            // 为Session对象设置properties
            session.setConfig(config);
            // 请求时长
            session.setServerAliveInterval(92000);
            // 通过session建立连接
            session.connect();
            // 指定连接sftp
            channel = session.openChannel("sftp");
            // 打开sftp的通道
            channel.connect();

        } catch (JSchException e) {
            e.printStackTrace();
        }
        sftp = (ChannelSftp) channel;
        return  sftp;
    }

    /**
     * 文件上传
     *
     * @param directory
     *            上传的目录
     * @param uploadFile
     *            需要上传的文件
     */
    public void upload(String directory, File uploadFile) {
        upMonitor = new SftpMonitor(uploadFile.length());
        try {
            sftp.cd(directory);
            sftp.put(new FileInputStream(uploadFile), uploadFile.getName(),
                    upMonitor);
        } catch (Exception e) {
            e.printStackTrace();
            upMonitor.stop();
        }finally {
            if (sftp != null) {
                try {
                    sftp.getSession().disconnect();
                } catch (JSchException e) {
                    e.printStackTrace();
                }
                sftp.disconnect();
            }
        }
    }

    /**
     * 下载文件
     *
     * @param directory
     *            下载目录
     * @param downloadFile
     *            下载的文件
     * @param saveFile
     *            存在本地的路径
     * @param sftp
     */
    public void download(String directory, String downloadFile,long downFileSize,
                         String saveFile, ChannelSftp sftp) {
        SftpMonitor downMonitor= new SftpMonitor(downFileSize);
        try {
            sftp.cd(directory);
            sftp.get(downloadFile, saveFile,downMonitor);
        } catch (Exception e) {
            e.printStackTrace();
            downMonitor.stop();
        }finally {
            if (sftp != null) {
                try {
                    sftp.getSession().disconnect();
                } catch (JSchException e) {
                    e.printStackTrace();
                }
                sftp.disconnect();
            }
        }
    }
}

