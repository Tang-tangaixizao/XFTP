package Entity;

public class SftpConfigInfo {
    private final static String privateKey="/root/.ssh";
    private final static String passphrase="id_rsa";
    private String username;
    private int port;
    private String password;
    private String host;


    public void setPort(int port) {
        this.port = port;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public static String getPrivateKey() {
        return privateKey;
    }

    public static String getPassphrase() {
        return passphrase;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }
}
