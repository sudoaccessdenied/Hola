package com.melonskart.HolaClient.Controller;

import com.melonskart.HolaClient.ClientBinder;
import com.melonskart.HolaServer.ServerInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.w3c.dom.css.CSS2Properties;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {


    @FXML
    private PasswordField password;

    @FXML
    private TextField mobileNumber;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField name;
    @FXML
    private Button sendOTPButton;

    @FXML
    private Pane passwordPane;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private PasswordField otp;

    @FXML
    private Label status;
    private Stage mPrimaryStage;
    private ServerInterface server;
    @FXML
    private Pane wrapPane;



    @FXML
    public void minimizeButton(MouseEvent mouseEvent) {
        ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).setIconified(true);

    }

    @FXML
    public void closeButton(MouseEvent mouseEvent) {
        ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).close();
        System.exit(0);

    }

    @FXML
    public void backButton(MouseEvent mouseEvent) {


        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        try {
            loader.setLocation(getClass().getResource("/com/melonskart/HolaClient/FXML/login.fxml"));
            root = (Parent) loader.load();
            LoginController controller = loader.getController();
            controller.setStage(mPrimaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 600 , 400);
        scene.getStylesheets().add
                (getClass().getResource("/com/melonskart/HolaClient/FXML/login.css").toExternalForm());
        mPrimaryStage.setScene(scene);
        mPrimaryStage.show();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        passwordPane.setVisible(false);
        status.setVisible(false);
        signUpButton.setVisible(false);
        mobileNumber.setFocusTraversable(false);
        server = ClientBinder.getInstance();
    }




    public void sendOTPButton(ActionEvent actionEvent) {


        boolean otpstatus = false;
        if (mobileNumber.getLength() == 10) {
            status.setVisible(false);


            try {
                otpstatus = server.sendOTP(Long.parseLong(mobileNumber.getText()));
                status.setVisible(true);

                if (otpstatus) {

                    passwordPane.setVisible(true);
                    mobileNumber.setDisable(true);
                    signUpButton.setVisible(true);
                    sendOTPButton.setText("Resend");
                    status.setText("Message Sent Successfully");
                }else
                    status.setText("User already exist");
            } catch (RemoteException e) {
                status.setText("server Error");
                e.printStackTrace();
            }


        }else
        {
            status.setVisible(true);
            status.setText("Mobile number digit error");
        }

    }

    public void setStage(Stage primaryStage) {
        mPrimaryStage = primaryStage;
    }

    @FXML
    void passwordMatch(KeyEvent event) {

        if (!password.getText().equals(confirmPassword.getText())) {

            status.setVisible(true);
            status.setText("Password did Not match");

        }else {

            status.setVisible(false);
        }

    }


    @FXML
    void signUpButtonAction(ActionEvent event) {

        status.setVisible(true);
        if (!password.getText().equals(confirmPassword.getText())) {

            status.setText("Password did Not match");
            return;
        } else if (password.getLength() == 0) {
            status.setText("Password cannot be blank");
            return;
        } else if (otp.getLength() < 4) {
            status.setText("OTP cannot be blank");
            return;
        } else if (name.getLength() == 0) {
            status.setText("Name cannot be blank");
            return;
        } else {

            try {
                boolean creationStatus =
                        server.createUser(Long.parseLong(mobileNumber.getText()), name.getText()
                                , password.getText(), Integer.parseInt(otp.getText()));


                if (creationStatus) {
                    status.setText("User Created Successfully");
                    wrapPane.setVisible(false);

                } else {
                    status.setText("Wrong OTP");
                }
            } catch (RemoteException e) {

                status.setText("Server Error Occure");
                e.printStackTrace();
            }


        }



    }
}
