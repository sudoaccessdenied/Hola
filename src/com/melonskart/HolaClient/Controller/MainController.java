package com.melonskart.HolaClient.Controller;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.melonskart.HolaClient.ClientBinder;
import com.melonskart.HolaServer.MongoConnect;
import com.melonskart.HolaServer.ServerInterface;
import com.mongodb.BasicDBObject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;
import org.controlsfx.control.Notifications;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.swing.*;
import javax.swing.event.CaretListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Logger;

public class MainController  extends UnicastRemoteObject  implements Initializable ,ClientCallBackInterface {


    @FXML
    private Pane headingPane;

    @FXML
    private TextArea textToSend;
    @FXML
    private Text headName;

    @FXML
    private JFXListView<Label> conversationPane;


    @FXML
    private Text headMobile;



    @FXML
    private JFXListView<Label> chatList;


    @FXML
    private StackPane stackPane;


    @FXML
    private JFXHamburger hamb;

    @FXML
    private Text userNameTitle;


    private MenuItem logout;
    private MenuItem addContact;
    private Stage mPrimaryStage;
    private long mobile;

    @FXML
    private  Pane textPane;
    private long toMobile;

    @FXML
    private Text myMobile;

    public MainController() throws RemoteException {
    }
//
//     MainController() throws RemoteException {
//        super();
//    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        headingPane.setVisible(false);
        conversationPane.setVisible(false);
        textPane.setVisible(false);

        iniPopUP();

    }




    private void registorClient() {
        ServerInterface server = ClientBinder.getInstance();
        try {
            server.registorClient(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public long getMobile() {
        return this.mobile;
    }

    @Override
    public boolean notifyChanges(Long from, String msg, int messageId, Date date) throws RemoteException {


                System.out.println("Updating messages from" + from + "  Msg :"+msg );

            addMessageToList(
                    from,msg,messageId,date
            );

        return true;
    }

    @Override
    public boolean messageAlert(Long from, String msg, int messageId, Date date) throws RemoteException {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Notifications.create()
                            .title("New Message from " + ClientBinder.getInstance().getName(from))
                            .text(msg)
                            .position(Pos.BOTTOM_RIGHT)
                            .darkStyle()
                            .showInformation();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });

        return true;
    }


    public void setStage(Stage primaryStage) {

        mPrimaryStage = primaryStage;
    }
//
    public void setUser(long mobile)
    {
//        System.out.println(mobile);

        this.mobile = mobile;
        myMobile.setText("+91 "+String.valueOf(mobile));
//        System.out.println(this.mobile);

        registorClient();
        try {

            String name = ClientBinder.getInstance().getName(this.mobile);
            userNameTitle.setText(name);
            userNameTitle.setVisible(true);

            System.out.println(this.mobile);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleMouseClick(MouseEvent event) {

        String userName ;
//        boolean blocked;
        ArrayList<Document> messages;


        if (!headingPane.isVisible() || !conversationPane.isVisible() || !textPane.isVisible()) {
            headingPane.setVisible(true);
            conversationPane.setVisible(true);
            textPane.setVisible(true);
        }

        conversationPane.getItems().clear();

        this.toMobile = Long.parseLong(chatList.getSelectionModel().getSelectedItem().getId());
        System.out.println("ListView Clicked Element" + chatList.getSelectionModel().getSelectedItem().getId()
                + " " + chatList.getSelectionModel().getSelectedIndex());


        ServerInterface server = ClientBinder.getInstance();
        try {
            Document sw =
                    server.messages(mobile, Long.parseLong(chatList.getSelectionModel().getSelectedItem().getId()));

            ArrayList<Document> doc = (ArrayList<Document>) sw.get("friends");


            System.out.println("Getting messages "+ doc);

            Document data = null;
            if (doc != null) {

                data = doc.get(0);
            }

            if (data != null) {

                headName.setText(server.getName(data.getLong("contact")));
                headMobile.setText("+91 " + data.getLong("contact").toString());




                messages = (ArrayList<Document>) data.get("messages");

//                System.out.println("Message " + messages);

//                System.out.println("asdfadfsadf" + messages.get(0).getString("msg"));


                if (!messages.isEmpty()) {
                    for (Document message : messages) {

                        Label label = new Label(server.getName(((Document) message).getLong("msg_by")) +
                                "\n" + message.getString("msg") +
                                "\n" + message.getDate("send_at")
                        );
                        label.setId(String.valueOf(message.getInteger("msg_id")));
                        label.setGraphic(new ImageView(
                                new Image(
                                        "/com/melonskart/HolaClient/res/baseline_account_circle_white_18dp.png")));
                        conversationPane.getItems().add(label);
                    }
                }
            }
//
//            System.out.println("meesage method have been called" + sbc.get(0));

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.out.println("Error Class cast");
        }


    }





    public void loadChat() {

        HashMap<Long, String> lists = null;
        try {
            lists = ClientBinder.getInstance().friend(this.mobile);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        chatList.getItems().clear();
        for (Map.Entry<Long, String> entry : lists.entrySet()) {
            long key = entry.getKey();
            String value = entry.getValue();


            Label label = new Label(value + "\n" + key);
            label.setId(String.valueOf(key));

            label.setGraphic(new ImageView(
                    new Image(
                            "/com/melonskart/HolaClient/res/baseline_account_circle_white_18dp.png")));
            chatList.getItems().add(label);
        }
    }


    private void iniPopUP() {

//
        ContextMenu contextMenu = new ContextMenu();


        logout = new MenuItem("Logout");
        addContact = new MenuItem("Add Contact");

        logout.setOnAction(event -> {
            try {
                ClientBinder.getInstance().unRegistorClient(this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }//            write logout function here

            // go back to login menu


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

        });


        addContact.setOnAction(event -> {
            System.out.println("addcontanct");
//            write addcontact function here


            JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
            jfxDialogLayout.setHeading(new Text("Enter 10 digit Mobile number"));
            JFXTextField user = new JFXTextField();

//            user.setCacheHint(new CacheHint("asdf"));

            Text addStatus = new Text("No status yet");
            addStatus.setVisible(false);
            jfxDialogLayout.setBody( user ,addStatus);


            JFXButton okayButton = new JFXButton("Okay");
            okayButton.setButtonType(JFXButton.ButtonType.RAISED);
            JFXButton cancelButton = new JFXButton("Cancel");
            cancelButton.setButtonType(JFXButton.ButtonType.RAISED);

            JFXDialog jfxDialog = new JFXDialog(stackPane, jfxDialogLayout, JFXDialog.DialogTransition.CENTER);



            jfxDialogLayout.setActions(okayButton,cancelButton);
            okayButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    String code = null;

                    System.out.println("Okay button pressed" + user.getText());

                    addStatus.setVisible(true);
                    user.setVisible(false);
                    okayButton.setVisible(false);

                    ServerInterface serverInterface = ClientBinder.getInstance();
                    try {
                        code =serverInterface.addContacts(MainController.this.mobile,
                                Long.parseLong(user.getText()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    if (code.equals("404")) {
                        addStatus.setText("Contact already added");
                    }else {

                        addStatus.setText("Contact Added successfully");

                    }

                    //add contacts method call

                    cancelButton.setText("Okay");

                    loadChat();




                }
            });

            cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    jfxDialog.close();
                }

            });


            jfxDialog.show();

        });
        contextMenu.getItems().addAll(logout, addContact);

//        HamburgerBasicCloseTransition transition = new HamburgerBasicCloseTransition(hamb);
//        transition.setRate(-1);
        hamb.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{

            if (!contextMenu.isShowing()) {

//                transition.setRate(transition.getRate() * -1);
                contextMenu.show(hamb, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
//                transition.setRate(-1);

            }
//            transition.play();

        });
    }

    @FXML
    void minimizeButton(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    void closeButton(MouseEvent event) {
        try {
            ClientBinder.getInstance().unRegistorClient(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        System.exit(0);

    }


    private void addMessageToList(Long from, String msg, int messageId, Date date) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Update UI here.

                Label label = null;
                try {
                    label = new Label(ClientBinder.getInstance().getName(from)+
                            "\n" + msg+
                            "\n" + date
                    );
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                label.setId(String.valueOf(messageId));
                label.setGraphic(new ImageView(
                        new Image(
                                "/com/melonskart/HolaClient/res/baseline_account_circle_white_18dp.png")));
                conversationPane.getItems().add(label);

            }
        });


    }


    @FXML
    void handleSendButton(ActionEvent event) {

// write send message here
        ServerInterface server = ClientBinder.getInstance();
        try {
            server.sendMessage(this.mobile, this.toMobile, textToSend.getText());
            addMessageToList(this.mobile, textToSend.getText(), 99, new Date());
            textToSend.setText("");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


}
