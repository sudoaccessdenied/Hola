package com.melonskart.HolaClient;

import com.melonskart.HolaClient.Controller.LoginController;
import com.melonskart.HolaClient.Controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/melonskart/HolaClient/FXML/login.fxml"));
        Parent root = (Parent) loader.load();

        LoginController controller = loader.getController();
        controller.setStage(primaryStage);

        Scene scene = new Scene(root, 600 , 400);
        scene.getStylesheets().add
                (getClass().getResource("/com/melonskart/HolaClient/FXML/login.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();


        // implement dialog for error

    }


    public static void main(String[] args) {
        launch(args);
    }
}
