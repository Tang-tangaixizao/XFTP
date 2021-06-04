package ViewModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SendViewModel {
    private StringProperty hostName= new SimpleStringProperty();
    private StringProperty IPArr = new SimpleStringProperty();
    private IntegerProperty port = new SimpleIntegerProperty();
    private StringProperty userName = new SimpleStringProperty();
    private StringProperty userPassword = new SimpleStringProperty();

    private static SendViewModel viewModel = new SendViewModel();

    private SendViewModel(){}

    public static SendViewModel getInstance() {
        return viewModel;
    }
    public void setReceiveData() {
        ReceiveViewModel viewModel = ReceiveViewModel.getInstance();
        viewModel.setHostName(hostName.get());
        viewModel.setIPArr(IPArr.get());
        viewModel.setPort(port.get());
        viewModel.setUserName(userName.get());
        viewModel.setUserPassword(userPassword.get());
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
