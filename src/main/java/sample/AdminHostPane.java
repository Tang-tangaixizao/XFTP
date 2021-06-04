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

public class AdminHostPane extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("管理主机");
        primaryStage.getIcons().add(new Image("集群管理.png"));

        VBox adminHostRoot=new VBox();
        adminHostRoot.setId("adminHostRoot");
        Parent adminHostBody= FXMLLoader.load(getClass()
                .getResource("/Fxml/adminHost.fxml"));
        adminHostRoot.getStylesheets().add(getClass().getResource("/Css/adminHostStyle.css")
                .toString());

        /**顶部
         * */
        VBox adminHostTop=new VBox();
        adminHostTop.setId("adminHostTop");
        adminHostTop.setPrefSize(600,25.0);

        /**标题栏
         * */
        AnchorPane adminHostTitle=new AnchorPane();
        Label adminHostClose=new Label();
        adminHostClose.setTooltip(new Tooltip("关闭"));
        Label adminHostMin=new Label();
        adminHostMin.setTooltip(new Tooltip("最小化"));
        Label adminHostTitleText=new Label("管理主机");
        Label adminHostTitleIcon=new Label();

        adminHostClose.setId("adminHostClose");
        adminHostMin.setId("adminHostMin");
        adminHostTitleText.setId("adminHostTitleText");
        adminHostTitleIcon.setId("adminHostTitleIcon");

        adminHostClose.setPrefWidth(20);
        adminHostClose.setPrefHeight(20);
        adminHostMin.setPrefWidth(20);
        adminHostMin.setPrefHeight(20);
        adminHostTitleIcon.setPrefWidth(20);
        adminHostTitleIcon.setPrefHeight(20);
        adminHostTitleText.setPrefWidth(80);
        adminHostTitleText.setPrefHeight(20);

        adminHostTitle.getChildren().add(adminHostClose);
        adminHostTitle.getChildren().add(adminHostMin);
        adminHostTitle.getChildren().add(adminHostTitleIcon);
        adminHostTitle.getChildren().add(adminHostTitleText);

        AnchorPane.setTopAnchor(adminHostClose,5.0);
        AnchorPane.setRightAnchor(adminHostClose,10.0);
        AnchorPane.setTopAnchor(adminHostMin,5.0);
        AnchorPane.setRightAnchor(adminHostMin,40.0);
        AnchorPane.setTopAnchor(adminHostTitleIcon,5.0);
        AnchorPane.setLeftAnchor(adminHostTitleIcon,5.0);
        AnchorPane.setTopAnchor(adminHostTitleText,2.0);
        AnchorPane.setLeftAnchor(adminHostTitleText,35.0);
        adminHostTop.getChildren().add(adminHostTitle);

        VBox adminHostContent=new VBox(adminHostBody);
        adminHostContent.setId("adminHostContent");
        adminHostContent.setPrefWidth(600);
        adminHostContent.setPrefHeight(600);
        adminHostRoot.getChildren().addAll(adminHostTop,adminHostContent);
        Scene adminHostSCene=new Scene(adminHostRoot);
        primaryStage.setScene(adminHostSCene);

        DragUtil.addDragListener(primaryStage,adminHostTop);
        DragUtil.addDrawFunc(primaryStage,adminHostRoot);
        primaryStage.getIcons().add(new Image("集群管理.png"));

        /**关闭按钮
         * */
        adminHostClose.setOnMouseClicked(event -> {
            /**关闭并结束程序
             * */
            primaryStage.close();
        });
        /**最小化按钮
         * */
        adminHostMin.setOnMouseClicked(event -> ((Stage)((Label)event.getSource())
                .getScene().getWindow()).setIconified(true));
        primaryStage.show();
    }
}
