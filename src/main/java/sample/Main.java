package sample;

import Util.DragUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    private String TITLE_NAME="XFTP";
    /**标记是否是进行最大化操作
     * */
    private boolean isMax=true;
    /**存放Controller
     * */
    public static Map<String, Object> controllers = new HashMap<String, Object>();
    @Override
    public void start(Stage primaryStage) throws Exception{
        MainPage(primaryStage);
    }
    /**主窗口
     * */
    public void MainPage(Stage stage) throws IOException {
        stage.initStyle(StageStyle.TRANSPARENT);
        VBox root = new VBox();
        root.setId("root");
        Parent body = FXMLLoader.load(getClass()
                .getResource("/sample/sample.fxml"));
        root.getStylesheets().add(getClass().getResource("/Css/style.css").toString());
        //顶部
        VBox top = new VBox();
        top.setId("top");
        top.setPrefSize(1440,25);
        // 标题栏
        AnchorPane title = new AnchorPane();
        Label close = new Label();
        close.setTooltip(new Tooltip("关闭"));
        Label min=new Label();
        min.setTooltip(new Tooltip("最小化"));
        Label max=new Label();
        max.setTooltip(new Tooltip("最大化"));
        Label icon=new Label();
        Label titleText=new Label(TITLE_NAME);
        stage.setTitle(TITLE_NAME);
        titleText.setPrefWidth(200);
        titleText.setPrefHeight(25);
        min.setPrefWidth(33);
        min.setPrefHeight(25);
        max.setPrefWidth(33);
        max.setPrefHeight(25);
        icon.setPrefHeight(25);
        icon.setPrefWidth(50);
        close.setPrefWidth(33);
        close.setPrefHeight(25);
        close.setId("winClose");//winClose css样式Id
        min.setId("winMin");
        max.setId("winMax");
        titleText.setId("winTitleText");
        icon.setId("winIcon");
        title.getChildren().add(close);
        title.getChildren().add(min);
        title.getChildren().add(max);
        title.getChildren().add(icon);
        title.getChildren().add(titleText);
        AnchorPane.setRightAnchor(close, 1.0);
        AnchorPane.setTopAnchor(close, 5.0);
        AnchorPane.setTopAnchor(max,5.0);
        AnchorPane.setRightAnchor(max, 35.0);
        AnchorPane.setTopAnchor(min,5.0);
        AnchorPane.setRightAnchor(min,75.0);
        AnchorPane.setLeftAnchor(titleText, 40.0);
        AnchorPane.setTopAnchor(titleText, 5.0);
        AnchorPane.setLeftAnchor(icon,5.0);
        AnchorPane.setTopAnchor(icon,5.0);
        top.getChildren().add(title);

        // 内容
        VBox content = new VBox(body);
        content.setPrefWidth(1440);
        content.setMinHeight(840);
        // 组装
        root.getChildren().addAll(top, content);
        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setScene(scene);
        // 拖动监听器
        DragUtil.addDragListener(stage, top);
        // 添加窗体拉伸效果
        DragUtil.addDrawFunc(stage, root);
        //设置窗口的图标.
        stage.getIcons().add(new Image("logo-cat.png"));

        /**关闭按钮
         * */
        close.setOnMouseClicked(event -> {
            /**关闭并结束程序
             * */
            stage.close();
        });
        /**最小化按钮
         * */
        min.setOnMouseClicked(event -> ((Stage)((Label)event.getSource()).getScene().getWindow()).setIconified(true));
        /**最大化按钮
         * */
        max.setOnMouseClicked(event -> {
            if(isMax){
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX(primaryScreenBounds.getMinX());
                stage.setY(primaryScreenBounds.getMinY());
                stage.setWidth(primaryScreenBounds.getWidth());
                stage.setHeight(primaryScreenBounds.getHeight());
                max.setStyle("-fx-background-image: url('huanyuan.png');");
                this.isMax=!isMax;
            }else{
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((primaryScreenBounds.getWidth()-1440)/2);
                stage.setY((primaryScreenBounds.getHeight()-870)/2);
                stage.setWidth(1440);
                stage.setHeight(870);
                max.setStyle("-fx-background-image: url('max2.png');");
                this.isMax=!isMax;
            }
        });
        // 显示
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
