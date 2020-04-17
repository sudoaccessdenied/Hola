package com.melonskart.HolaServer;

import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {


    @FXML
    private JFXToggleButton toggle ;


    @FXML
    private Text errorText;

    @FXML
    void minimizeButton(MouseEvent event) {
        ((Stage)   ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    void closeButton(MouseEvent event) {
        ((Stage)   ((Node) event.getSource()).getScene().getWindow()).close();
        System.exit(0);

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        toggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (toggle.isSelected() == true) {
                    toggle.setText("Server is Running");
                    boolean start = ServerBinder.startServer();
                    if (start == false) {
//                        toggle.setSelected(false);
                        toggle.setText("Server is Not Running");
                        errorText.setText("Error Occure");
                    }

                }
                else
                {

                    toggle.setText("Server is Not Running");
                    boolean stop =ServerBinder.stopServer();
                    if (stop == false) {
//                        toggle.setSelected(true);
                        toggle.setText("Server is  Running");
                        errorText.setText("Error Occures");

                    }

                    errorText.setText("Restart the Application to Run Server");
                }
            }
        });
    }

}
