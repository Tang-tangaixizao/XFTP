package Controller;


import Entity.ConnectInfo;
import Entity.SftpConfigInfo;
import Util.JsonUtil;
import ViewModel.ReceiveViewModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import sample.Controller;
import sample.Main;
import java.net.URL;
import java.util.ResourceBundle;
import static Util.JsonUtil.readJsonFile;


public class HostAddController implements Initializable {

    public Button hostAddClearBtn;
    public TextField hostName;
    public TextField IPArr;
    public TextField port;
    public TextField userName;
    public PasswordField userPassword;
    private ConnectInfo connectInfo;
    private ReceiveViewModel receiveViewModel=ReceiveViewModel.getInstance();
    /**标记释放检索结束
     * */
    private boolean isSeachEnd=true;
    public void onHostAddClear() {
        Stage winClose=(Stage) hostAddClearBtn.getScene().getWindow();
        winClose.close();
    }
    public void connectBtnClick() {
        Stage winClose=(Stage) hostAddClearBtn.getScene().getWindow();
        Controller control = (Controller) Main.controllers.get(Controller.class.getSimpleName());
        winClose.close();
        connectInfo=new ConnectInfo();
        connectInfo.setIP(IPArr.getText());
        connectInfo.setUserName(userName.getText());
        connectInfo.setLabel(hostName.getText());
        connectInfo.setPassword(userPassword.getText());
        if("".equals(port.getText())){
            control.connectLinuxFileSystem(IPArr.getText(),
                    22,userName.getText(),userPassword.getText());
            connectInfo.setPort(22);
        }else{
            control.connectLinuxFileSystem(IPArr.getText(),
                    Integer.parseInt(port.getText()),userName.getText(),userPassword.getText());
            connectInfo.setPort(Integer.valueOf(port.getText()));
        }
        /**对Json文件进行写操作
         * */
        /**目标格式:
         * {
         *     "userInfo":[
         *         {
         *             "iP":"192.168.79.140",
         *             "label":"测试添加",
         *             "password":"123456",
         *             "port":22,
         *             "userName":"root"
         *         },
         *         {
         *             "iP":"192.168.79.141",
         *             "label":"测试添加",
         *             "password":"123456",
         *             "port":22,
         *             "userName":"root"
         *         }
         *     ]
         * }
         * 通过json对象全套json数组，再数值中添加实体类来实现
         **/
        String path = "src\\main\\resources\\Json\\info.json";
        String s = readJsonFile(path);
        JSONObject jsonObject = JSON.parseObject(s);
        //构建JSONArray数组
        JSONArray movies1 = jsonObject.getJSONArray("userInfo");
        /**不为null代表json文件中有数据
         * */
        if(movies1!=null){
            for (int i = 0; i < movies1.size(); i++) {
                JSONObject object= (JSONObject) movies1.get(i);
                /**如果输入的IP地址和json中的IP地址相同就执行
                 * 修改操作
                 * */
                if(IPArr.getText().equals(object.get("iP"))){
                    object.put("password",userPassword.getText());
                    object.put("label",hostName.getText());
                    object.put("userName",userName.getText());
                    object.put("port",Integer.valueOf(port.getText()));
                    isSeachEnd=false;
                    break;
                }
            }
            if(isSeachEnd){
                movies1.add(connectInfo);
            }
        }else{
            //构建JSONArray数组
            JSONArray movies = new JSONArray();
            movies.add(connectInfo);
            jsonObject=new JSONObject();
            jsonObject.put("userInfo",movies);
        }
        JSONObject jsonSftpConfig=new JSONObject();
        jsonSftpConfig.put("host",hostName.getText());
        jsonSftpConfig.put("password",userPassword.getText());
        jsonSftpConfig.put("port",Integer.valueOf(port.getText()));
        jsonSftpConfig.put("username",userName.getText());
        /**写入sftpConfig.json文件
         * */
        JsonUtil.writeFile("src\\main\\resources\\Json\\sftpConfig.json",jsonSftpConfig.toJSONString());
        control.linuxCurrentPath="/";
        /**写入json文件中
         * */
        JsonUtil.writeFile("src\\main\\resources\\Json\\info.json",jsonObject.toJSONString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**对Json文件进行读操作
         * */
        String path = "src\\main\\resources\\Json\\info.json";
        String s = readJsonFile(path);
        JSONObject jobj = JSON.parseObject(s);
        //构建JSONArray数组
        JSONArray movies1 = jobj.getJSONArray("userInfo");
        JSONObject key = (JSONObject) movies1.get(movies1.size() - 1);
        String jsonIP = (String) key.get("iP");
        String jsonLabel = (String) key.get("label");
        int jsonPort = ((int) key.get("port"));
        String jsonUserName = ((String) key.get("userName"));
        String jsonPassword = ((String) key.get("password"));
        IPArr.setText(jsonIP);
        hostName.setText(jsonLabel);
        port.setText(String.valueOf(jsonPort));
        userName.setText(jsonUserName);
        userPassword.setText(jsonPassword);
        /**获取AdminHostController传递过来的值
         * 来实现编辑功能
         * */
        if(receiveViewModel.IPArrProperty().getValue()!=null
        ||"".equals(receiveViewModel.IPArrProperty().getValue())){
            hostName.setText(receiveViewModel.hostNameProperty().getValue());
            IPArr.setText(receiveViewModel.IPArrProperty().getValue());
            port.setText(String.valueOf(receiveViewModel.portProperty().getValue()));
            userName.setText(receiveViewModel.userNameProperty().getValue());
            userPassword.setText(receiveViewModel.userPasswordProperty().getValue());
        }
        /**第一个文本框对焦
         * 和最后一个文本框对焦
         * 检测是否接收到回车键
         * */
        hostName.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                connectBtnClick();
            }
        });
        userPassword.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                connectBtnClick();
            }
        });
    }
}
