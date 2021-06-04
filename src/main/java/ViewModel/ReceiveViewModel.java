package ViewModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReceiveViewModel {
    private StringProperty hostName= new SimpleStringProperty();
    private StringProperty IPArr = new SimpleStringProperty();
    private IntegerProperty port = new SimpleIntegerProperty();
    private StringProperty userName = new SimpleStringProperty();
    private StringProperty userPassword = new SimpleStringProperty();

    public void setHostName(String hostName) {
        this.hostName.set(hostName);
    }

    public void setIPArr(String IPArr) {
        this.IPArr.set(IPArr);
    }

    public void setPort(Integer port) {
        this.port.set(port);
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public void setUserPassword(String userPassword) {
        this.userPassword.set(userPassword);
    }

    private static ReceiveViewModel viewModel = new ReceiveViewModel();

    private ReceiveViewModel(){}

    public static ReceiveViewModel getInstance(){
        return viewModel;
    }
    public StringProperty hostNameProperty(){
        return hostName;
    }
    public StringProperty IPArrProperty(){
        return IPArr;
    }
    public IntegerProperty portProperty(){
        return port;
    }
    public StringProperty userNameProperty(){
        return userName;
    }
    public StringProperty userPasswordProperty(){
        return userPassword;
    }
}
