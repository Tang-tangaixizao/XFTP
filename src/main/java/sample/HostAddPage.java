package sample;

import Util.DragUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HostAddPage extends Application {
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("添加主机");
        primaryStage.getIcons().add(new Image("添加主机.png"));

        VBox hostAddRoot=new VBox();
        hostAddRoot.setId("hostAddRoot");
        Parent hostAddBody= FXMLLoader.load(getClass()
                .getResource("/Fxml/hostAdd.fxml"));
        hostAddRoot.getStylesheets().add(getClass().getResource("/Css/hostAddStyle.css").toString());

        /**顶部
         * */
        VBox hostAddTop=new VBox();
        hostAddTop.setId("hostAddTop");
        hostAddTop.setPrefSize(600,25.0);

        /**标题栏
         * */
        AnchorPane hostAddTitle=new AnchorPane();
        Label hostAddClose=new Label();
        hostAddClose.setTooltip(new Tooltip("关闭"));
        Label hostAddMin=new Label();
        hostAddMin.setTooltip(new Tooltip("最小化"));
        Label hostAddTitleText=new Label("添加主机");
        Label hostAddTitleIcon=new Label();

        hostAddClose.setId("hostAddClose");
        hostAddMin.setId("hostAddMin");
        hostAddTitleText.setId("hostAddTitleText");
        hostAddTitleIcon.setId("hostAddTitleIcon");

        hostAddClose.setPrefWidth(20);
        hostAddClose.setPrefHeight(20);
        hostAddMin.setPrefWidth(20);
        hostAddMin.setPrefHeight(20);
        hostAddTitleIcon.setPrefWidth(20);
        hostAddTitleIcon.setPrefHeight(20);
        hostAddTitleText.setPrefWidth(80);
        hostAddTitleText.setPrefHeight(20);

        hostAddTitle.getChildren().add(hostAddClose);
        hostAddTitle.getChildren().add(hostAddMin);
        hostAddTitle.getChildren().add(hostAddTitleIcon);
        hostAddTitle.getChildren().add(hostAddTitleText);

        AnchorPane.setTopAnchor(hostAddClose,5.0);
        AnchorPane.setRightAnchor(hostAddClose,10.0);
        AnchorPane.setTopAnchor(hostAddMin,5.0);
        AnchorPane.setRightAnchor(hostAddMin,40.0);
        AnchorPane.setTopAnchor(hostAddTitleIcon,5.0);
        AnchorPane.setLeftAnchor(hostAddTitleIcon,5.0);
        AnchorPane.setTopAnchor(hostAddTitleText,2.0);
        AnchorPane.setLeftAnchor(hostAddTitleText,35.0);
        hostAddTop.getChildren().add(hostAddTitle);

        VBox hostAddContent=new VBox(hostAddBody);
        hostAddContent.setId("hostAddContent");
        hostAddContent.setPrefWidth(600);
        hostAddContent.setPrefHeight(500);
        hostAddRoot.getChildren().addAll(hostAddTop,hostAddContent);
        Scene hostAddSCene=new Scene(hostAddRoot);
        primaryStage.setScene(hostAddSCene);

        DragUtil.addDragListener(primaryStage,hostAddTop);
        DragUtil.addDrawFunc(primaryStage,hostAddRoot);
        primaryStage.getIcons().add(new Image("添加主机.png"));

        /**关闭按钮
         * */
        hostAddClose.setOnMouseClicked(event -> {
            /**关闭并结束程序
             * */
            primaryStage.close();
        });
        /**最小化按钮
         * */
        hostAddMin.setOnMouseClicked(event -> ((Stage)((Label)event.getSource()).getScene().getWindow()).setIconified(true));
        primaryStage.show();
    }
}
