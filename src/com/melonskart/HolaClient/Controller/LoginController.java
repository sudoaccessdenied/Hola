package com.melonskart.HolaClient.Controller;

import com.jfoenix.controls.*;
import com.melonskart.HolaClient.ClientBinder;
import com.melonskart.HolaServer.MongoConnect;
import com.melonskart.HolaServer.ServerInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private Stage mPrimaryStage;


    @FXML
    private PasswordField password;



    @FXML
    private TextField mobileNumber;

    @FXML
    private Label passwordError;

    @FXML
    private Text serverError;

    @FXML
    private StackPane stackPane;




    @FXML
    void minimizeButton(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);

    }

    @FXML
    void closeButton(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        System.exit(0);

    }

    @FXML
    void loginButton(ActionEvent event) {


        ServerInterface server = ClientBinder.getInstance();
        boolean success = false;
        try {
            success = server.loginUser(
                    Long.parseLong(mobileNumber.getText()),
                    password.getText()
            );

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (success) {
//            forward to next page and registor client

            System.out.println("Login Successful");
            Parent root = null;
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setLocation(getClass().getResource("/com/melonskart/HolaClient/FXML/main.fxml"));
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MainController controller = loader.getController();

            controller.setUser(Long.parseLong(mobileNumber.getText()));
            controller.loadChat();
            controller.setStage(mPrimaryStage);



            Scene scene = new Scene(root, 800 , 600);
            scene.getStylesheets().add
                    (getClass().getResource("/com/melonskart/HolaClient/FXML/main.css").toExternalForm());
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            mPrimaryStage.setX((primScreenBounds.getWidth() - mPrimaryStage.getWidth()) / 3);
            mPrimaryStage.setY((primScreenBounds.getHeight() - mPrimaryStage.getHeight()) / 3);

            mPrimaryStage.setScene(scene);
            mPrimaryStage.show();



        } else {

            passwordError.setVisible(true);
            passwordError.setText("Wrong UserName or Password");
        }



    }

    @FXML
    void signUpButton(MouseEvent event) {

        Parent root = null;
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource("/com/melonskart/HolaClient/FXML/signUp.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SignUpController controller = loader.getController();
        controller.setStage(mPrimaryStage);
        Scene scene = new Scene(root, 600 , 400);
        scene.getStylesheets().add
                (getClass().getResource("/com/melonskart/HolaClient/FXML/login.css").toExternalForm());
        mPrimaryStage.setScene(scene);
        mPrimaryStage.show();
    }


    private void ipAddressPicker() {


        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
        jfxDialogLayout.setHeading(new Text("Enter the server IP Address"));
        JFXTextField user = new JFXTextField();
        user.setText("localhost");
//            user.setCacheHint(new CacheHint("asdf"));

        jfxDialogLayout.setBody(user);
        JFXButton okayButton = new JFXButton("Okay");
        okayButton.setButtonType(JFXButton.ButtonType.RAISED);

        jfxDialogLayout.setActions(okayButton);
        JFXDialog jfxDialog = new JFXDialog(stackPane, jfxDialogLayout, JFXDialog.DialogTransition.CENTER);

        okayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                ClientBinder.setHost(user.getText());
                try {
                    ClientBinder.connectToServer();

                } catch (RemoteException e) {
//                    e.printStackTrace();
                    serverError.setText("Unable to Connect to Server");

                    Notifications.create().title("Server error").text("Unable to Connect to Server").darkStyle().showError();
                } catch (NotBoundException e) {
//                    e.printStackTrace();
                    serverError.setText("Unable to Connect to Server");

                    Notifications.create().title("Server error").text("Unable to Connect to Server").darkStyle().showError();
                }
                jfxDialog.close();


            }
        });

        jfxDialog.show();
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {

            ipAddressPicker();
            passwordError.setText("");
            passwordError.setVisible(false);



    }


    public void setStage(Stage primaryStage) {
        mPrimaryStage = primaryStage;
    }

}