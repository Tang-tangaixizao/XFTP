package Controller;

import Util.JsonUtil;
import ViewModel.SendViewModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.Controller;
import sample.HostAddPage;
import sample.Main;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static Util.JsonUtil.readJsonFile;

public class AdminHostController implements Initializable {
    public TableView adminHostList;
    public TableColumn hostNameList;
    public TableColumn hostNameEdit;
    public TableColumn hostNameDel;
    private ObservableList<String> hostInfo;
    private Controller control = (Controller) Main.controllers.get(Controller.class.getSimpleName());
    private SendViewModel sendViewModel=SendViewModel.getInstance();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**修改表格无内容文本
         * */
        Label tabelText=new Label("请添加主机");
        tabelText.setStyle("-fx-font-weight: bold;-fx-font-family: 'Microsoft YaHei UI';" +
                "-fx-font-size: 16px;");
        adminHostList.setPlaceholder(tabelText);
        hostInfo= FXCollections.observableArrayList();
        hostNameEdit.setCellFactory((col)->{
            TableCell<String, String> cell = new TableCell<String, String>(){
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    String ip="";
                    String path = "src\\main\\resources\\Json\\info.json";
                    String s = readJsonFile(path);
                    JSONObject jsonObject = JSON.parseObject(s);
                    //构建JSONArray数组
                    JSONArray movies1 = jsonObject.getJSONArray("userInfo");
                    if(!empty){
                        /**获取IP地址
                         * */
                        ip=item.substring(item.indexOf("`"));
                    }
                    //按钮显示文字
                    Button hostNameBtn = new Button("编辑");
                    hostNameBtn.setId("hostNameEditBtn");
                    //按钮点击事件
                    String finalIp = ip;
                    hostNameBtn.setOnMouseClicked((col) -> {
                            /**编辑操作弹出添加主机窗口
                             * */
                        HostAddPage hostAddPage=new HostAddPage();
                        for (int i = 0; i < movies1.size(); i++) {
                            JSONObject object= (JSONObject) movies1.get(i);
                            /**如果输入的IP地址和json中的IP地址相同就执行
                             * 修改操作
                             * */
                            if(finalIp.substring(1).equals(object.get("iP"))){
                                /**从json文件中获取对应的值
                                 * */
                                String jsonGetIp= (String) object.get("iP");
                                int jsonGetPort=(int)object.get("port");
                                String jsonGetPassword=(String)object.get("password");
                                String jsonGetLabel=(String)object.get("label");
                                String jsonGetUserName=(String)object.get("userName");
                                sendViewModel.hostNameProperty().setValue(jsonGetLabel);
                                sendViewModel.IPArrProperty().setValue(jsonGetIp);
                                sendViewModel.userPasswordProperty().setValue(jsonGetPassword);
                                sendViewModel.portProperty().setValue(jsonGetPort);
                                sendViewModel.userNameProperty().setValue(jsonGetUserName);
                                sendViewModel.setReceiveData();
                            }
                        }
                        try {
                            hostAddPage.start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                    if (empty) {
                        //如果此列为空默认不添加元素
                        setText(null);
                        setGraphic(null);
                    } else {
                        //加载按钮
                        this.setGraphic(hostNameBtn);
                    }
                }
            };
            return cell;
        });
        hostNameDel.setCellFactory((col)->{
            TableCell<String, String> cell = new TableCell<String, String>(){
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    String ip="";
                    String path = "src\\main\\resources\\Json\\info.json";
                    String s = readJsonFile(path);
                    JSONObject jsonObject = JSON.parseObject(s);
                    //构建JSONArray数组
                    JSONArray movies1 = jsonObject.getJSONArray("userInfo");
                    if(!empty){
                        /**获取IP地址
                         * */
                        ip=item.substring(item.indexOf("`"));
                    }
                    //按钮显示文字
                    Button hostNameBtn = new Button("删除");
                    hostNameBtn.setId("hostNameDelBtn");
                    //按钮点击事件
                    String finalIp = ip;
                    hostNameBtn.setOnMouseClicked((col) -> {
                        for (int i = 0; i < movies1.size(); i++) {
                            JSONObject object=(JSONObject) movies1.get(i);
                            if(finalIp.substring(1).equals(object.get("iP"))){
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // 创建一个确认对话框
                                alert.setHeaderText("确定删除该主机信息？"); // 设置对话框的头部文本
                                // 显示对话框，并等待按钮返回
                                Optional<ButtonType> buttonType = alert.showAndWait();
                                // 判断返回的按钮类型是确定还是取消，再据此分别进一步处理
                                if (buttonType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) { // 单击了确定按钮OK_DONE
                                    hostInfo.remove(i);
                                    movies1.remove(i);
                                    jsonObject.put("userInfo",movies1);
                                    /**回写到Json文件中
                                     * */
                                    JsonUtil.writeFile("src\\main\\resources\\Json\\info.json",jsonObject.toJSONString());
                                }
                                break;
                            }
                        }
                    });
                    if (empty) {
                        //如果此列为空默认不添加元素
                        setText(null);
                        setGraphic(null);
                    } else {
                        //加载按钮
                        this.setGraphic(hostNameBtn);
                    }
                }
            };
            return cell;
        });
        hostNameList.setCellFactory((col)->{
            TableCell<String, String> cell = new TableCell<String, String>(){
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    String label="";
                    String ip="";
                    String path = "src\\main\\resources\\Json\\info.json";
                    String s = readJsonFile(path);
                    JSONObject jsonObject = JSON.parseObject(s);
                    //构建JSONArray数组
                    JSONArray movies1 = jsonObject.getJSONArray("userInfo");
                    if(!empty){
                        /**获取标签
                         * */
                        label=item.substring(0,item.lastIndexOf("`"));
                        ip=item.substring(item.indexOf("`"));
                    }
                    //按钮显示文字
                    Button hostNameBtn = new Button(label);
                    hostNameBtn.setId("hostNameBtn");
                    //按钮点击事件
                    String finalIp = ip;
                    hostNameBtn.setOnMouseClicked((col) -> {
                        /**点击两次触发连接操作s
                         * */
                        if(col.getClickCount()==2){
                            for (int i = 0; i < movies1.size(); i++) {
                            JSONObject object= (JSONObject) movies1.get(i);
                            /**如果输入的IP地址和json中的IP地址相同就连接
                             * */
                            if(finalIp.substring(1).equals(object.get("iP"))){
                                /**连接时关闭本窗口
                                 * */
                                Stage winClose=(Stage) hostNameBtn.getScene().getWindow();
                                winClose.close();
                                JSONObject jsonSftpConfig=new JSONObject();
                                jsonSftpConfig.put("host",object.get("iP"));
                                jsonSftpConfig.put("password",object.get("password"));
                                jsonSftpConfig.put("port",Integer.valueOf(object.get("port").toString()));
                                jsonSftpConfig.put("username",object.get("userName"));
                                /**写入sftpConfig.json文件
                                 * */
                                JsonUtil.writeFile("src\\main\\resources\\Json\\sftpConfig.json",jsonSftpConfig.toJSONString());
                                 control.connectLinuxFileSystem(object.get("iP").toString()
                                        ,Integer.valueOf(object.get("port").toString())
                                        ,object.get("userName").toString(),object.get("password").toString());
                                    break;
                                }
                            }
                        }
                    });
                    if (empty) {
                        //如果此列为空默认不添加元素
                        setText(null);
                        setGraphic(null);
                    } else {
                        //加载按钮
                        this.setGraphic(hostNameBtn);
                    }
                }
            };
            return cell;
        });
        hostNameList.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                return new SimpleStringProperty(param.getValue().toString());
            }
        });
        hostNameEdit.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                return new SimpleStringProperty(param.getValue().toString());
            }
        });
        hostNameDel.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures param) {
                return new SimpleStringProperty(param.getValue().toString());
            }
        });
        /**对Json文件进行读操作
         * */
        String path = "src\\main\\resources\\Json\\info.json";
        String s = readJsonFile(path);
        JSONObject jobj = JSON.parseObject(s);
        //构建JSONArray数组
        JSONArray movies1 = jobj.getJSONArray("userInfo");
        for (int i = 0; i < movies1.size(); i++) {
            JSONObject key = (JSONObject) movies1.get(i);
            String jsonIP=(String) key.get("iP");
            String jsonLabel = (String) key.get("label");
            hostInfo.add(jsonLabel+"`"+jsonIP);
        }
        adminHostList.setItems(hostInfo);
    }
}
