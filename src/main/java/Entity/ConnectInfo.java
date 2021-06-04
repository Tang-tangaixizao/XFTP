package Entity;

public class ConnectInfo {
    private String IP;
    private int port;
    private String userName;
    private String password;
    private String label;

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getLabel() {
        return label;
    }
}
