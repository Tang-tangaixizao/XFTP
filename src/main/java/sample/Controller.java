package sample;

import Entity.FileInfo;
import Entity.LinuxFileInfo;
import Util.SftpConfig;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller implements Initializable {
    public TableView tableView;
    public TableColumn fileName;
    public TableColumn fileDate;
    public TableColumn fileSize;
    public ComboBox comboBoxSystemPartition;
    public TableView linuxTableView;
    public TableColumn linuxFileName;
    public TableColumn linuxFileType;
    public TableColumn linuxFileDate;
    public TableColumn linuxFileSize;
    public TableColumn linuxFileOwner;
    public TableColumn linuxFileAttr;
    public ProgressBar progressBar;
    public Label UPfileName;
    public TextField sreach;
    private ObservableList<FileInfo> list;
    private ObservableList<LinuxFileInfo> linuxList;
    private FileInfo fileInfo;
    private String fileType;
    private String dragIcon;
    private String prohibitDragFileName;
    private final static String DEFAULT_PATH="C:\\";
    private String comboBoxSelect="C:";
    private String spaceAfterFileName="";
    private String linuxGetFileName="";
    private FileInfo winSelectFileInfo;
    private LinuxFileInfo linuxSelectFileInfo;
    public String linuxCurrentPath="/";
    private String username;
    private String password;
    private int port;
    private String IP;
    /**标准拖拽到对方的通道
     * */
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/face-object");
    /**linux文件拖拽到本地系统的标志
     * */
    private static final DataFormat LINUX_ITSELF_SERIALIZED_MIME_TYPE = new DataFormat("application/linuxitself-object");
    /**windows文件拖拽到本地系统的标标志
     * */
    private static final DataFormat WINDOWS_ITSELF_SERIALIZED_MIME_TYPE = new DataFormat("application/windowsitself-object");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**默认不显示进度条区域的控件
         * */
        progressBar.setVisible(false);
        UPfileName.setVisible(false);
        /**修改表格无内容文本
         * */
        Label tabelText=new Label("无检索结果");
        tabelText.setStyle("-fx-font-weight: bold;-fx-font-family: 'Microsoft YaHei UI';" +
                "-fx-font-size: 16px;");
        tableView.setPlaceholder(tabelText);
        Label linuxTabelText=new Label("请连接主机");
        linuxTabelText.setStyle("-fx-font-weight: bold;-fx-font-family: 'Microsoft YaHei UI';" +
                "-fx-font-size: 16px;");
        linuxTableView.setPlaceholder(linuxTabelText);
        //将此Controller添加到容器中
        Main.controllers.put(this.getClass().getSimpleName(), this);
        /**下拉框
         * */
        ArrayList<File> systemPartitionList=getSystemPartition();
        for (int i = 0; i < systemPartitionList.size(); i++) {
            comboBoxSystemPartition.getItems().add(systemPartitionList.get(i));
        }
        /**下拉框选中
         * */
        comboBoxSystemPartition.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        comboBoxSelect=newValue.toString().substring(0,
                                newValue.toString().lastIndexOf("\\"));
                        getAllFileInfo(newValue.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        /**搜索框功能
         * */
        sreach.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)){
                    if(!"".equals(sreach.getText())){
                        try {
                            /**如果输入路径以冒号结尾
                             * 则视为不合法
                             * */
                            String sreachPathTesting = ".*:$";
                            Pattern rcompile = Pattern.compile(sreachPathTesting);
                            Matcher mMatcher = rcompile.matcher(sreach.getText());
                            if(mMatcher.matches()){
                                // 创建一个警告对话框
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                // 设置对话框的头部文本
                                alert.setHeaderText("请输入合法路径！");
                                // 显示对话框
                                alert.show();
                            }else{
                                /**如果访问目录以\结尾去除结尾的\
                                 * */
                                String pattern = ".*\\\\$";
                                Pattern r = Pattern.compile(pattern);
                                Matcher m = r.matcher(sreach.getText());
                                if(m.matches()){
                                    comboBoxSelect=sreach.getText().substring(0,sreach.getText()
                                            .lastIndexOf("\\"));
                                }
                                getAllFileInfo(sreach.getText());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        // 创建一个警告对话框
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        // 设置对话框的头部文本
                        alert.setHeaderText("请输入需要访问的路径！");
                        // 显示对话框
                        alert.show();
                    }
                }
        });
        list = FXCollections.observableArrayList();
        linuxList = FXCollections.observableArrayList();
        /**映射到实体类中的属性
         * */
        fileName.setCellValueFactory(new PropertyValueFactory("fileName"));
        fileDate.setCellValueFactory(new PropertyValueFactory("fileDate"));
        fileSize.setCellValueFactory(new PropertyValueFactory("fileSize"));

        linuxFileName.setCellValueFactory(new PropertyValueFactory("linuxFileName"));
        linuxFileType.setCellValueFactory(new PropertyValueFactory("linuxFileType"));
        linuxFileDate.setCellValueFactory(new PropertyValueFactory("linuxFileDate"));
        linuxFileSize.setCellValueFactory(new PropertyValueFactory("linuxFileSize"));
        linuxFileOwner.setCellValueFactory(new PropertyValueFactory("linuxFileOwner"));
        linuxFileAttr.setCellValueFactory(new PropertyValueFactory("linuxFileAttr"));

        try {
            getAllFileInfo(DEFAULT_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        linuxTableView.setItems(linuxList);
        /**tableView添加list
         * */
        tableView.setItems(list);
        /**tableView选中一行
         * */
        tableView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<FileInfo>)
                (observable, oldValue, newValue) -> {
            if(newValue!=null){
                dragIcon=newValue.getFileSize();
                prohibitDragFileName=newValue.getFileName();
                winSelectFileInfo=newValue;
            }
        });
        /**linuxTableView选中一行
         * */
        linuxTableView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<LinuxFileInfo>)
                (observable, oldValue, newValue) -> {
            if(newValue!=null){
                linuxSelectFileInfo=newValue;
            }
        });
        linuxTableView.setRowFactory(tv -> {
            TableRow<FileInfo> row = new TableRow<>();
            /**行拖拽检测
             * */
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(LINUX_ITSELF_SERIALIZED_MIME_TYPE, index);
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });
            /**拖拽释放验证
             * */
            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer) db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        event.consume();
                    }
                }else if(db.hasContent(LINUX_ITSELF_SERIALIZED_MIME_TYPE)){
                    if (row.getIndex() != ((Integer) db.getContent(LINUX_ITSELF_SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY);
                        event.consume();
                    }
                }
            });
            /**拖拽释放时执行代码
             * */
            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                /**拖拽到本地就移动文件
                 * */
                if (db.hasContent(LINUX_ITSELF_SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(LINUX_ITSELF_SERIALIZED_MIME_TYPE);
                    LinuxFileInfo draggedPerson =(LinuxFileInfo) linuxTableView.getItems().remove(draggedIndex);
                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = linuxTableView.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }
                    linuxTableView.getItems().add(dropIndex, draggedPerson);
                    event.setDropCompleted(true);
                    linuxTableView.getSelectionModel().select(dropIndex);
                    event.consume();
                }else if(db.hasContent(SERIALIZED_MIME_TYPE)){
                    /**检测到释放就上传文件
                     * */
                    new Thread(() -> {

                        SftpConfig sftpConfig = new SftpConfig();
                        sftpConfig.getConnection();
                        /**当前路径末尾添加\
                         * */
                        comboBoxSelect+="\\";
                        String uploadFile = comboBoxSelect+
                                winSelectFileInfo.getFileName().substring(0,
                                        winSelectFileInfo.getFileName().lastIndexOf("|"));
                        File file = new File(uploadFile);
                        sftpConfig.setMovePath(linuxCurrentPath);
                        sftpConfig.upload(sftpConfig.getMovePath(), file);
                    }).start();
                    /**同时显示文件名
                     * */
                    UPfileName.setText(winSelectFileInfo.getFileName()
                            .substring(0,winSelectFileInfo.getFileName().lastIndexOf("|")));
                    LinuxFileInfo linuxFileInfo=new LinuxFileInfo();
                    linuxFileInfo.setLinuxFileName("-"+winSelectFileInfo.getFileName()
                            .substring(0,winSelectFileInfo.getFileName().lastIndexOf("|")));
                    /**目标格式:
                     * ?月?日 ??:??
                     * */
                    String substring=winSelectFileInfo.getFileDate()
                            .substring(winSelectFileInfo.getFileDate().indexOf("/")+1);
                    String date=substring.replace("/","月");
                    date=date.replace(" ","日 ");
                    linuxFileInfo.setLinuxFileDate(date);
                    /**获取登录当前系统的用户名
                     * */
                    Map<String, String> map = System.getenv();
                    String systemUserName = map.get("USERNAME");
                    linuxFileInfo.setLinuxFileOwner(systemUserName);
                    linuxFileInfo.setLinuxFileSize(winSelectFileInfo.getFileSize());
                    linuxFileInfo.setLinuxFileType("文件");
                    linuxFileInfo.setLinuxFileAttr("-rw-r--r--");
                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = linuxTableView.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }
                    linuxTableView.getItems().add(dropIndex, linuxFileInfo);
                    event.setDropCompleted(true);
                    linuxTableView.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });
            return row;
        });
        addLinuxTableViewIcon(linuxFileName);
        tableView.setRowFactory(tv -> {
            TableRow<FileInfo> row = new TableRow<>();
            /**拖拽检测
             * */
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    cc.put(WINDOWS_ITSELF_SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });
            /**拖拽缩放检验
             * */
            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer) db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY);
                        event.consume();
                    }
                }else if(db.hasContent(WINDOWS_ITSELF_SERIALIZED_MIME_TYPE)){
                    if (row.getIndex() != ((Integer) db.getContent(WINDOWS_ITSELF_SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        event.consume();
                    }
                }
            });
            /**拖拽释放执行代码
             * */
            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                /**拖拽到本地就移动文件
                 * */
                if (db.hasContent(WINDOWS_ITSELF_SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(WINDOWS_ITSELF_SERIALIZED_MIME_TYPE);
                    FileInfo draggedPerson =(FileInfo) tableView.getItems().remove(draggedIndex);
                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = tableView.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }
                    tableView.getItems().add(dropIndex, draggedPerson);
                    event.setDropCompleted(true);
                    tableView.getSelectionModel().select(dropIndex);
                    event.consume();
                }else if(db.hasContent(SERIALIZED_MIME_TYPE)){
                    /**拖拽释放就下载文件
                     * */
                    new Thread(() -> {
                        SftpConfig sftpConfig = new SftpConfig();
                        ChannelSftp sftp= sftpConfig.getConnection();
                        if(comboBoxSelect.lastIndexOf("/")==-1){
                            comboBoxSelect+="/";
                        }
                        String downloadFile =linuxCurrentPath+"/"+linuxSelectFileInfo
                                .getLinuxFileName().substring(1);
                        String substring=linuxSelectFileInfo.getLinuxFileSize().substring(0,
                                linuxSelectFileInfo.getLinuxFileSize().lastIndexOf("byte"));
                        long selectFileSize=Long.valueOf(substring);
                        sftpConfig.download(linuxCurrentPath,downloadFile,selectFileSize,comboBoxSelect,sftp);
                    }).start();
                    /**同时设置下载的文件名
                     * */
                    UPfileName.setText(linuxSelectFileInfo.getLinuxFileName().substring(1));
                    FileInfo winFileInfo=new FileInfo();
                    winFileInfo.setFileName(linuxSelectFileInfo.getLinuxFileName().substring(1)
                            +"|"+linuxSelectFileInfo.getLinuxFileSize());
                    winFileInfo.setFileSize(linuxSelectFileInfo.getLinuxFileSize());
                    /**获取系统年份
                     * */
                    SimpleDateFormat df = new SimpleDateFormat("yyyy");
                    /**目标格式:
                     * ????/??/?? ??:??
                     * */
                    String mouth=linuxSelectFileInfo.getLinuxFileDate().substring(0,1);
                    String day=linuxSelectFileInfo.getLinuxFileDate().substring(2,3);
                    String hour=linuxSelectFileInfo.getLinuxFileDate().substring(
                            linuxSelectFileInfo.getLinuxFileDate().indexOf(" "));
                    winFileInfo.setFileDate(df.format(new Date())+"/"+mouth+"/"+day+" "+hour);
                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = linuxTableView.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }
                    tableView.getItems().add(dropIndex, winFileInfo);
                    event.setDropCompleted(true);
                    tableView.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });
            return row;
        });
        fileName.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                TableCell<String, String> cell = new TableCell<String,String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty){
                            HBox hBox=new HBox(10);
                            /**获取文件名
                             * */
                            String fileNameLabel=item.substring(0,item.indexOf("|"));
                            /**获取文件后缀名
                             * */
                            String fileSuffix=fileNameLabel.substring(fileNameLabel
                                    .lastIndexOf(".")+1);
                            /**获取文件类型
                             * */
                            String fileTypeLabel=item.substring(item.indexOf("|")+1);

                            Label label=new Label(fileNameLabel);
                            ImageView folderIcon = new ImageView("file-b-0.png");
                            ImageView fileIcon = new ImageView("file-b-6.png");
                            ImageView zipFileIcon=new ImageView("zip (1).png");
                            ImageView exeFileIcon=new ImageView("exe (1).png");
                            ImageView docFileIcon=new ImageView("doc (1).png");
                            ImageView pptFileIcon=new ImageView("ppt.png");
                            ImageView excelFileIcon=new ImageView("excel (2).png");
                            ImageView mdFileIcon=new ImageView("md.png");
                            ImageView rarFileIcon=new ImageView("zip.png");
                            ImageView htmlFileIcon=new ImageView("html (1).png");
                            ImageView cssFileIcon=new ImageView("css (1).png");
                            ImageView jsFileIcon=new ImageView("JavaScript.png");
                            ImageView javaFileIcon=new ImageView("java.png");
                            ImageView pythonFileIcon=new ImageView("python.png");
                            ImageView unknownFileIcon=new ImageView("未知文件 (4).png");
                            ImageView cmdFileIcon=new ImageView("powershell.png");
                            ImageView shFileIcon=new ImageView("cloudshell 云命令行.png");
                            ImageView xmlFileIcon=new ImageView("xml.png");
                            ImageView logFileIcon=new ImageView("log.png");
                            ImageView imageFileIcon=new ImageView("file-b-9.png");
                            ImageView musicFileIcon=new ImageView("file-b-1 (1).png");
                            ImageView videoFileIcon=new ImageView("file-b-10.png");
                            ImageView batFileIcon=new ImageView("exe (2).png");

                            folderIcon.setFitHeight(25);
                            folderIcon.setFitWidth(25);
                            fileIcon.setFitHeight(25);
                            fileIcon.setFitWidth(25);
                            zipFileIcon.setFitWidth(25);
                            zipFileIcon.setFitHeight(25);
                            exeFileIcon.setFitHeight(25);
                            exeFileIcon.setFitWidth(25);
                            docFileIcon.setFitWidth(25);
                            docFileIcon.setFitHeight(25);
                            pptFileIcon.setFitHeight(25);
                            pptFileIcon.setFitWidth(25);
                            excelFileIcon.setFitWidth(25);
                            excelFileIcon.setFitHeight(25);
                            mdFileIcon.setFitHeight(25);
                            mdFileIcon.setFitWidth(25);
                            rarFileIcon.setFitWidth(25);
                            rarFileIcon.setFitHeight(25);
                            htmlFileIcon.setFitHeight(25);
                            htmlFileIcon.setFitWidth(25);
                            cssFileIcon.setFitWidth(25);
                            cssFileIcon.setFitHeight(25);
                            jsFileIcon.setFitHeight(25);
                            jsFileIcon.setFitWidth(25);
                            javaFileIcon.setFitWidth(25);
                            javaFileIcon.setFitHeight(25);
                            pythonFileIcon.setFitHeight(25);
                            pythonFileIcon.setFitWidth(25);
                            unknownFileIcon.setFitWidth(25);
                            unknownFileIcon.setFitHeight(25);
                            cmdFileIcon.setFitHeight(25);
                            cmdFileIcon.setFitWidth(25);
                            xmlFileIcon.setFitWidth(25);
                            xmlFileIcon.setFitHeight(25);
                            logFileIcon.setFitHeight(25);
                            logFileIcon.setFitWidth(25);
                            shFileIcon.setFitWidth(25);
                            shFileIcon.setFitHeight(25);
                            imageFileIcon.setFitHeight(25);
                            imageFileIcon.setFitWidth(25);
                            musicFileIcon.setFitWidth(25);
                            musicFileIcon.setFitHeight(25);
                            videoFileIcon.setFitHeight(25);
                            videoFileIcon.setFitWidth(25);
                            batFileIcon.setFitWidth(25);
                            batFileIcon.setFitHeight(25);

                            if("<DIR>".equals(fileTypeLabel)){
                                hBox.getChildren().addAll(folderIcon,label);
                            }else{
                                if("zip".equals(fileSuffix)){
                                    hBox.getChildren().addAll(zipFileIcon,label);
                                }else if("exe".equals(fileSuffix)){
                                    hBox.getChildren().addAll(exeFileIcon,label);
                                }else if("doc".equals(fileSuffix)||"docx".equals(fileSuffix)){
                                    hBox.getChildren().addAll(docFileIcon,label);
                                }else if("ppt".equals(fileSuffix)||"pptx".equals(fileSuffix)||
                                "pps".equals(fileSuffix)){
                                    hBox.getChildren().addAll(pptFileIcon,label);
                                }else if("xlsx".equals(fileSuffix)||"xls".equals(fileSuffix)||
                                "csv".equals(fileSuffix)){
                                    hBox.getChildren().addAll(excelFileIcon,label);
                                }else if("md".equals(fileSuffix)){
                                    hBox.getChildren().addAll(mdFileIcon,label);
                                }else if("rar".equals(fileSuffix)){
                                    hBox.getChildren().addAll(rarFileIcon,label);
                                }else if("html".equals(fileSuffix)||"htm".equals(fileSuffix)||
                                "jsp".equals(fileSuffix)){
                                    hBox.getChildren().addAll(htmlFileIcon,label);
                                }else if("css".equals(fileSuffix)||"qss".equals(fileSuffix)){
                                    hBox.getChildren().addAll(cssFileIcon,label);
                                }else if("js".equals(fileSuffix)){
                                    hBox.getChildren().addAll(jsFileIcon,label);
                                }else if("java".equals(fileSuffix)||"jar".equals(fileSuffix)||
                                "war".equals(fileSuffix)||"class".equals(fileSuffix)){
                                    hBox.getChildren().addAll(javaFileIcon,label);
                                }else if("py".equals(fileSuffix)){
                                    hBox.getChildren().addAll(pythonFileIcon,label);
                                }else if("txt".equals(fileSuffix)){
                                    hBox.getChildren().addAll(fileIcon,label);
                                }else if("cmd".equals(fileSuffix)){
                                    hBox.getChildren().addAll(cmdFileIcon,label);
                                }else if("sh".equals(fileSuffix)){
                                    hBox.getChildren().addAll(shFileIcon,label);
                                }else if("xml".equals(fileSuffix)){
                                    hBox.getChildren().addAll(xmlFileIcon,label);
                                }else if("log".equals(fileSuffix)){
                                    hBox.getChildren().addAll(logFileIcon,label);
                                }else if("jpg".equals(fileSuffix)||"png".equals(fileSuffix)||
                                "gif".equals(fileSuffix)||"svg".equals(fileSuffix)||
                                "jpeg".equals(fileSuffix)||"bmp".equals(fileSuffix)){
                                    hBox.getChildren().addAll(imageFileIcon,label);
                                }else if("mp3".equals(fileSuffix)){
                                    hBox.getChildren().addAll(musicFileIcon,label);
                                }else if("mp4".equals(fileSuffix)||"avi".equals(fileSuffix)){
                                    hBox.getChildren().addAll(videoFileIcon,label);
                                }else if("bat".equals(fileSuffix)){
                                    hBox.getChildren().addAll(batFileIcon,label);
                                }else{
                                    hBox.getChildren().addAll(unknownFileIcon,label);
                                }
                            }
                            this.setGraphic(hBox);
                        }
                    }
                };
                /**tableView双击事件
                 * */
                cell.setOnMouseClicked(event -> {
                    /**如果是文件夹就执行双击的逻辑
                     * */
                    if(event.getClickCount()==2&&"".equals(dragIcon)){
                        try {
                            String accessFolder=prohibitDragFileName
                                        .substring(0,prohibitDragFileName.indexOf("|"));
                            /**实现记录当前目录路径
                             * */
                            if("..|<DIR>".equals(prohibitDragFileName)){
                                comboBoxSelect=comboBoxSelect.substring(0,comboBoxSelect.lastIndexOf("\\"));
                                /**返回到跟目录添加\
                                 * */
                                if(comboBoxSelect.lastIndexOf("\\")==-1){
                                    comboBoxSelect+="\\";
                                }
                            }else{
                                /**如果访问目录以\结尾去除结尾的\
                                 * */
                                String pattern = ".*\\\\$";
                                Pattern r = Pattern.compile(pattern);
                                Matcher m = r.matcher(comboBoxSelect);
                                if(m.matches()){
                                    comboBoxSelect=comboBoxSelect.substring(0,comboBoxSelect
                                            .lastIndexOf("\\"));
                                }
                                comboBoxSelect+="\\"+accessFolder;
                            }
                            getAllFileInfo(comboBoxSelect);
                            /**如果访问目录以\结尾去除结尾的\
                             * */
                            String pattern = ".*\\\\$";
                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(comboBoxSelect);
                            if(m.matches()){
                                comboBoxSelect=comboBoxSelect.substring(0,comboBoxSelect
                                        .lastIndexOf("\\"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return cell;
            }
        });
    }

    /**获取系统文件
     * */
    public void getAllFileInfo(String accessPath)throws Exception{
        accessPath="\""+accessPath+"\"";
        list.clear();
        tableView.refresh();
        String cmdDir = "cmd.exe /c dir "+accessPath;
        Process pDir;
        pDir = Runtime.getRuntime().exec(cmdDir);
        String lineDir;
        BufferedReader readerDir = new BufferedReader(
                new InputStreamReader(pDir.getInputStream(),"gbk"));
        while((lineDir = readerDir.readLine()) != null) {
            /**匹配是否以2开头
             **/
            String pattern = "^[2].*$";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(lineDir);
            if(m.matches()){
                /**匹配以空格分隔字符串
                 * 文件夹:
                 * 2020/04/11
                 * 17:28
                 * <DIR>
                 * AstonUbuntu160403-64
                 * 文件:
                 * 2021/01/30
                 * 23:26
                 * 2,856
                 * nginx.conf
                 * 2021/01/30
                 * */
                spaceAfterFileName="";
                //构建值对象
                fileInfo = new FileInfo();
                String [] arr = lineDir.split("\\s+");
                /**处理文件夹有空格的情况
                 * */
                if(arr.length>=5){
                    for (int i = 4; i < arr.length; i++) {
                        spaceAfterFileName+=" "+arr[i];
                    }
                }
                fileType="|"+arr[2];
                if (!".".equals(arr[3])){
                    if(arr.length>=5){
                        fileInfo.setFileName(arr[3]+spaceAfterFileName+fileType);
                    }else{
                        fileInfo.setFileName(arr[3]+fileType);
                    }
                    fileInfo.setFileDate(arr[0]+" "+arr[1]);
                    fileInfo.setFileSize("<DIR>".equals(arr[2])?"":arr[2]+"byte");
                    list.add(fileInfo);
                }
            }
        }
        readerDir.close();
        pDir.destroy();
    }

    /**获取系统盘符
     * */
    public ArrayList<File> getSystemPartition(){
        ArrayList<File> diskPartions = new ArrayList<>();

        File[] disks = File.listRoots();
        if (disks.length == 0) {
            return null;
        }

        for (int a = 0; a < disks.length; a++) {
            diskPartions.add(disks[a]);
        }
        return diskPartions;
    }

    /**刷新按钮
     * */
    public void fileSystemRefresh() {
       new Thread(() -> {
           try {
               /**如果输入路径以冒号结尾
                * 则视为不合法
                * */
               String sreachPathTesting = ".*:$";
               Pattern rcompile = Pattern.compile(sreachPathTesting);
               Matcher mMatcher = rcompile.matcher(comboBoxSelect);
               if(mMatcher.matches()){
                    comboBoxSelect+="\\";
               }
               getAllFileInfo(comboBoxSelect);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }).start();
    }

    public void hostAddBtnClick() {
        HostAddPage hostAddPage=new HostAddPage();
        try {
            hostAddPage.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**管理主机按钮
     * */
    public void adminHostBtnClick() {
        AdminHostPane adminHostPane=new AdminHostPane();
        try {
            adminHostPane.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**将表格中添加图标
     * */
    public void addLinuxTableViewIcon(TableColumn column){
        column.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell<String,String> cell=new TableCell<String,String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty){
                            HBox hBox=new HBox(10);
                            /**获取文件类型
                             * */
                            String fileType=item.substring(0,1);
                            /**获取文件名
                             * */
                            String fileName=item.substring(1);

                            Label label=new Label(fileName);
                            ImageView fileIcon=new ImageView("file (4).png");
                            ImageView folderIcon=new ImageView("folder (1).png");
                            fileIcon.setFitHeight(26);
                            fileIcon.setFitWidth(26);
                            folderIcon.setFitWidth(26);
                            folderIcon.setFitHeight(26);
                            if("d".equals(fileType)){
                                hBox.getChildren().addAll(folderIcon,label);
                            }else{
                                hBox.getChildren().addAll(fileIcon,label);
                            }
                            this.setGraphic(hBox);
                        }
                    }
                };
                cell.setOnMouseClicked(event -> {
                    if(event.getClickCount()==2&&"文件夹".equals(linuxSelectFileInfo.getLinuxFileType())){
                        linuxCurrentPath+="/"+linuxSelectFileInfo.getLinuxFileName().substring(1);/**以/..结尾说明用户点击了上一级目录
                         * 匹配/..结尾的
                         * */
                        String pattern = "(.*/..)$";
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(linuxCurrentPath);
                        if("/opt/rh".equals(linuxCurrentPath)||"//opt/rh".equals(linuxCurrentPath)){
                            // 创建一个错误对话框
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            // 设置对话框的头部文本
                            alert.setHeaderText("无访问权限");
                            // 设置对话框的内容文本
                            alert.show();
                            linuxCurrentPath="/opt";
                        }else if("//ww".equals(linuxCurrentPath)||"/ww".equals(linuxCurrentPath)){
                            // 创建一个错误对话框
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            // 设置对话框的头部文本
                            alert.setHeaderText("无访问权限");
                            // 设置对话框的内容文本
                            alert.show();
                            linuxCurrentPath="/";
                        }else{
                            if(m.matches()){
                                linuxCurrentPath=linuxCurrentPath.substring(0,
                                        linuxCurrentPath.lastIndexOf("/.."));
                                if(linuxCurrentPath.lastIndexOf("/")==0){
                                    linuxCurrentPath="/";
                                }else{
                                    linuxCurrentPath=linuxCurrentPath.substring(0,linuxCurrentPath
                                            .lastIndexOf("/"));
                                }
                            }
                            getLinuxFileSystem(linuxCurrentPath);
                        }
                    }
                });
                return cell;
            }
        });
    }

    /**连接Linux
     * */
    public void connectLinuxFileSystem(String IP,int port,String username,String password){
        try {
            String cmdPing = "cmd.exe /c ping "+IP;
            Process pPing;
            pPing = Runtime.getRuntime().exec(cmdPing);
            String linePing;
            BufferedReader readerDir = new BufferedReader(
                    new InputStreamReader(pPing.getInputStream(),"gbk"));
            while ((linePing = readerDir.readLine()) != null) {
                String matchSpaces = "^[^\\s].*。";
                String mathchPingSuccess = "^来自.*";
                Pattern execmathchPingSuccess = Pattern.compile(mathchPingSuccess);
                Matcher matchPingSuccessResult = execmathchPingSuccess.matcher(linePing);
                Pattern execMatchSpaces = Pattern.compile(matchSpaces);
                Matcher matchSpacesResult = execMatchSpaces.matcher(linePing);

                if (matchSpacesResult.matches()) {
                    // 创建一个错误对话框
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    // 设置对话框的头部文本
                    alert.setHeaderText("连接失败");
                    // 设置对话框的内容文本
                    alert.show();
                    break;
                }
                if (matchPingSuccessResult.matches()) {
                    this.username=username;
                    this.port=port;
                    this.password=password;
                    this.IP=IP;
                    getLinuxFileSystem("/");
                    break;
                }
            }
            readerDir.close();
            pPing.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**获取linux文件系统
     * */
    public void getLinuxFileSystem(String currentPath) {
        linuxList.clear();
        linuxTableView.refresh();
        String result;
        Session session = null;
        ChannelExec openChannel = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, IP, port);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);
            session.connect();
            openChannel = (ChannelExec) session.openChannel("exec");
            openChannel.setCommand("ls -l " + currentPath);
            openChannel.connect();
            InputStream in = openChannel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String buf;
            /**不为/
             * 则添加上一级目录
             * */
            if (!"/".equals(currentPath)) {
                LinuxFileInfo superiorPath = new LinuxFileInfo();
                superiorPath.setLinuxFileAttr("dr-xr-xr-x.");
                superiorPath.setLinuxFileType("文件夹");
                superiorPath.setLinuxFileOwner("root");
                superiorPath.setLinuxFileSize("3byte");
                superiorPath.setLinuxFileDate("2月3日 11:48");
                superiorPath.setLinuxFileName("d..");
                linuxList.add(superiorPath);
            }
            while ((buf = reader.readLine()) != null) {
                LinuxFileInfo linuxFileInfo = new LinuxFileInfo();
                result = new String(buf.getBytes("UTF-8"), "UTF-8");
                /**匹配-、l、b、s、d、c、p
                 * */
                String matchResultStart = "^-.*|^d.*|^l.*|^s.*|^p.*|^c.*|^b.*";
                Pattern execMatchStart = Pattern.compile(matchResultStart);
                Matcher matchStartResult = execMatchStart.matcher(result);
                if (matchStartResult.matches()) {
                    /**按空格分割
                     **/
                    String[] arr = result.split("\\s+");
                    linuxFileInfo.setLinuxFileAttr(arr[0]);
                    linuxFileInfo.setLinuxFileOwner(arr[2]);
                    linuxFileInfo.setLinuxFileSize(arr[4] + "byte");
                    linuxFileInfo.setLinuxFileDate(arr[5] + arr[6] + "日 " + arr[7]);
                    if ("d".equals(arr[0].substring(0, 1))) {
                        linuxFileInfo.setLinuxFileType("文件夹");
                    } else {
                        linuxFileInfo.setLinuxFileType("文件");
                    }
                    for (int i = 8; i < arr.length; i++) {
                        linuxGetFileName += arr[i];
                    }
                    linuxGetFileName = arr[0].substring(0, 1) + linuxGetFileName;
                    linuxFileInfo.setLinuxFileName(linuxGetFileName);
                    linuxList.add(linuxFileInfo);
                    linuxGetFileName = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (openChannel != null && !openChannel.isClosed()) {
                openChannel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}