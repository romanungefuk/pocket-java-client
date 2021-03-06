package client.view;

import client.Main;
import client.controller.ClientController;
import client.utils.Common;
import client.utils.CustomTextArea;
import client.utils.Sound;
import client.view.customFX.*;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable {

    private static ChatViewController instance;


    @FXML
    private BorderPane borderPaneMain;
    @FXML
    private AnchorPane messagePanel;

    @FXML
    private WebView messageWebView;

    @FXML
    private JFXListView<CFXListElement> contactListView;

    @FXML
    private CustomTextArea messageField;

    @FXML
    private Tab chats;

    @FXML
    private Tab contacts;

    @FXML
    private AnchorPane userSearchPane;

    @FXML
    private AnchorPane contactsViewPane;

    @FXML
    private AnchorPane groupSearchPane;

    @FXML
    private AnchorPane groupListPane;

    @FXML
    private AnchorPane groupNewPane;

    @FXML
    private AnchorPane contactSearchPane;

    @FXML
    private ScrollPane myProfileScrollPane;

    @FXML
    private ScrollPane groupProfileScrollPane;

    @FXML
    private ScrollPane otherProfileScrollPane;

    @FXML
    private JFXListView<?> groupListView;

    @FXML
    private JFXListView<?> groupSearchListView;

    @FXML
    private JFXListView<CFXListElement> listViewAddToGroup;

    @FXML
    private Menu menuLeff;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXTextField creategroupName;

    @FXML
    private CFXMyProfile myProfile;

    @FXML
    private CFXGroupProfile groupProfile;

    @FXML
    private CFXOtherProfile othersProfile;

    @FXML
    private JFXTextField tfSearchInput;

    @FXML
    private JFXTextField userSearchText;

    @FXML
    private JFXTabPane tabPane;
    //
    private WebEngine webEngine;

    private ObservableList<CFXListElement> contactsObservList;

    private ClientController clientController;

    private String backgroundImage;

    private Document DOMdocument;

    private String tsOld;

    private int idDivMsg;

    private int idMsg;

    @FXML
    private  JFXButton btnContactSearchCancel;

    @FXML
    private JFXButton btnContactSearchInvite;

    @FXML
    private JFXListView<CFXListElement> searchListView;
    private ObservableList<CFXListElement> searchObsList;

    @FXML
    private CFXMenuLeft cfxMenuLeft;

    @FXML
    private CFXMenuRightGroup cfxMenuRightGroup;

    @FXML
    private JFXButton btnRightMenu;

    public static ChatViewController getInstance() {
        return instance;
    }

    private SingleSelectionModel<Tab> selectionModel;
    //???????????? ???? desktop
    private Desktop desktop;
    ////////////////////////
    HamburgerBasicCloseTransition transition;
    HamburgerBackArrowBasicTransition transitionBack;

    public ChatViewController() {
    }

    public int getIdMsg() {
        return idMsg;
    }

    public void setIdMsg(int idMsg) {
        this.idMsg = idMsg;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DOMdocument = null;
        tsOld = null; //???????????? ????????
        idMsg = 0; //???????????????????????? ID

        webEngine = messageWebView.getEngine(); //?????????????????????????? WebEngine
        initBackgroundWebView();
        initWebView();

        clientController = ClientController.getInstance();
        clientController.setChatViewController(this);
        contactsObservList = FXCollections.observableList(clientController.getContactListOfCards());
        // ?????? ???????????? ???????????? ?????????????????? ?????????????? ?????????????? ???????????????? //todo ???????????????????? ???? ???????????? ???????????
        if (contactsObservList.isEmpty()) contacts.getTabPane().getSelectionModel().select(contacts);
        contactListView.setExpanded(true);
        fillContactListView();
        searchObsList = FXCollections.observableList(new ArrayList<CFXListElement>());
        searchListView.setExpanded(true);
        searchListView.setItems(searchObsList);

        desktop = Desktop.getDesktop();

        messageField.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode().equals(KeyCode.ENTER)) {
                String text = messageField.getText().trim();
                if (!text.isEmpty()) {
//                    messageField.appendText(System.lineSeparator());
                    clientController.sendMessage(messageField.getText());
                    messageField.clear();
                    messageField.requestFocus();
                }
                event.consume();
            }
        });
        transition = new HamburgerBasicCloseTransition(hamburger);
        transitionBack = new HamburgerBackArrowBasicTransition(hamburger);
        PaneProvider.setTransitionBack(transitionBack);
         selectionModel=tabPane.getSelectionModel();
         instance=this;
         CFXMenuLeft.setParentController(instance);
         PaneProvider.setBorderPaneMain(borderPaneMain);

    }


    //  ?????????????????????????? ???????????????? backgrounda
    private void initBackgroundWebView() {
        String path = "client/images/chat-bg.jpg"; //???????????????? ????????
        ClassLoader cl = this.getClass().getClassLoader();
        backgroundImage = "";
        try {
            backgroundImage = cl.getResource(path).toURI().toString();
        }catch (Exception e) {
            //todo ?????????????????? ?? ??????????????????????
            e.printStackTrace();
        }
    }

    // ?????????????????????????? ???????????? HTML ?? WebView.
    private void initWebView() {
        webEngine.loadContent(
                "<!DOCTYPE html> \n"+
                "<html lang=\"en\"> \n"+
                  "<head> \n"+
                    "<meta charset=UTF-8> \n"+
                    "<style> \n"+
                        "body { \n" +
                            "margin: 0; \n"+
                            "padding: 10px; \n"+
                            "background-image: url(" + backgroundImage + "); \n"+
                            "background-attachment: fixed; \n"+
                        "} \n"+
                        //?????????? ??????????
                        //time day
                        ".timeStampDay { \n" +
                            "display: inline-block; \n"+
                            "text-align: center; \n"+
                            //"width: 80px; \n"+
                            "margin: 0 38%;  \n"+
                            "margin-top: 10px;  \n"+
                            "color: #55635A; \n"+
                            "background: #BCDCC9; \n"+
                            "border-radius: 10px; \n"+
                            "padding: 5px 10px; \n"+
                        "} \n"+
                        //
                        ".message { \n"+
                            "display: flex; \n"+
                            "width: 0px; \n"+
                            "align-items: center; \n"+
                            "margin-left: 10px; \n"+
                            "margin-right: 10px; \n"+
                            "margin-top: 10px; \n"+
                            "margin-bottom: 30px; \n"+
                        "} \n"+
                        //div Logo
                        ".msgLogo { \n"+
                            "flex: none; \n"+
                            "align-self: start; \n"+
                            "width: 33px; \n"+
                            "height: 33px; \n"+
                            "background: lightgrey; \n"+
                            "border-radius: 50%; \n"+
                        "} \n"+
                        //div text, 1->2
                        ".msgTxt { \n"+
                            "display: flex \n"+
                            "flex-direction: column; \n"+
                            "flex: auto; \n"+
                            "max-width: 400px; \n"+
                            "min-width: 200px; \n"+
                            "width: 300px; \n"+
                            "border-radius: 20px; \n"+
                            "margin-left: 10px; \n"+
                            "margin-right: 10px; \n"+
                            "padding: 16px; \n"+
                            "box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.15); \n"+
                        "} \n"+
                        //div time
                        ".msgTime { \n"+
                            "flex: auto; \n"+
                        "} \n"+

                        //div msgTxt --> sender
                        ".myUserClass { \n"+
                            "background: #C6FCFF; \n"+
                        "} \n"+"" +
                        ".senderUserClass { \n"+
                            "background: #FFFFFF; \n"+
                        "} \n"+

                        //div text --> div sender
                        ".myUserClassS{ \n"+
                            "display: none; \n"+ //?????????????????????? ???????? ???? ????????????????????
                        "} \n"+

                        ".senderUserClassS{ \n"+
                            "word-wrap: break-word; \n"+    //<!--?????????????? ????????-->
                            "color: #1EA362; \n"+
                        "} \n"+

                        //div text --> div msg
                        ".msg { \n"+
                            "width: auto; \n"+
                            "word-wrap: break-word; \n"+    //<!--?????????????? ????????-->
                        "} \n"+

                        //div time -->sender
                        ".myUserClassT { \n"+
                            "color: #757575; \n"+
                        "} \n"+
                        ".senderUserClassT { \n"+
                            "color: #4285F4; \n"+
                        "} \n"+
                    "</style> \n"+
                  "</head> \n"+
                  "<body></body> \n"+
                "</html> \n");
    }

    private void fillContactListView() {
        contactListView.setItems(contactsObservList);
        //todo: ???????????????? ?????????????????? ?????????????????? ?? body ??????????????????
    }

    public void updateContactListView() {
        if (!contactsViewPane.isVisible() && contactSearchPane.isVisible()) contactSearchBtnCancelClicked();
        contactListView.setItems(null);
        contactListView.setItems(contactsObservList);
        contactListView.refresh();
    }

    //  ?????????????????????????? ???????????????? ??????????????
    //if sex = true, is a woman
    //   sex = false, is a man
    private String initAvatar(boolean sex) {
        String path = "";
        if (sex) {
            path = "client/images/defaultAvatar/girl.png"; //???????????????? ????????
        }else {
            path = "client/images/defaultAvatar/man.png"; //???????????????? ????????
        }
        ClassLoader cl = this.getClass().getClassLoader();
        String avatar = "";
        try {
            avatar = cl.getResource(path).toURI().toString();
        }catch (Exception e) {
            //todo ?????????????????? ?? ??????????????????????
            e.printStackTrace();
        }
        return avatar;
    }

    /**
     *
     * @param pattern
     * @return
     * ?????????????????????????? ???????????? ????????
     */
    private SimpleDateFormat initDateFormat(String pattern){
        return new SimpleDateFormat(pattern);
    }

    /**
     *
     * @param message
     * @param senderName
     * @param timestamp     *
     * @param attrClass
     * ****
     * /* Create module DIV for messenger
     * <div class="timeStampDay"></div>
         * <div class="message">
             * <div class="msgLogo"></div>
             * <div class="attrClass msgTxt">
     *          <div class="'attrClass+S' sender"></div>
     *          <div class="'attrClass+M' msg">
     *              <???????? ???????????? ????
     *              <a href=????????????></a>
     *          </div>
     *        </div>
         * </div>
         * <div class="'attrClass+T' msgTime"></div>
     * </div>
     * Style create in initWebView
     *
     */
    private void createMessageDiv(String message, String senderName, Timestamp timestamp, String attrClass){
        //ID ?????????????????? ?????? ?????????????? ?????????????? ??????????
        idMsg+=1;
        setIdMsg(idMsg);
        //???????????????? ????????????
        //?????? ???? ???????? ???????????? ???? ????????. ???????????????? ?????????? ???????? ?????????????? ???????? ?????????????????????? ?? ?????????????????? ??????????
        String avatar = initAvatar(false); //man
        String styleStr = "background-image: url(" + avatar + "); background-size: cover";
        //

        SimpleDateFormat dateFormatDay = initDateFormat("d MMMM");
        SimpleDateFormat dateFormat = initDateFormat("HH:mm");

        //???????????????? Enter ???? ?????????????? ????????????, ?????? ??????????????????????
        message = message.replaceAll("\n", "<br/>");
        //???????????? ????????????, ???????????????? ???????????? ???????? <a href="message">message</a>
        message = Common.urlToHyperlink(message);

        boolean visibleDateDay=false;
        if (tsOld == null) {
            tsOld = dateFormatDay.format(timestamp);
            visibleDateDay = true;
        }else if (!tsOld.equals(dateFormatDay.format(timestamp))) {
            tsOld = dateFormatDay.format(timestamp);
            visibleDateDay = true;
        }

        Node body = DOMdocument.getElementsByTagName("body").item(0);

        if (visibleDateDay) {
            Element divTimeDay = DOMdocument.createElement("div");
            divTimeDay.setAttribute("class", "timeStampDay");
            divTimeDay.setTextContent(dateFormatDay.format(timestamp));
            body.appendChild(divTimeDay);
        }
        Element div = DOMdocument.createElement("div");
        Element divLogo = DOMdocument.createElement("div");
        Element divTxt = DOMdocument.createElement("div");
        Element divTxtSender = DOMdocument.createElement("div");
        Element divTxtMsg = DOMdocument.createElement("div");
        Element divTime = DOMdocument.createElement("div");
        div.setAttribute("class", "message");
        divLogo.setAttribute("class", "msgLogo");
        divLogo.setAttribute("style", styleStr);
        divTxt.setAttribute("class", attrClass+" msgTxt");
        divTxtSender.setAttribute("class", attrClass+"S sender");
        divTxtMsg.setAttribute("class", attrClass+"M msg");
        divTxtMsg.setAttribute("id", String.valueOf(idMsg)); //id
        divTime.setAttribute("class", attrClass+"T msgTime");
        divTxtSender.setTextContent(senderName);
        divTxtMsg.setTextContent(message);
        divTime.setTextContent(dateFormat.format(timestamp));
        div.appendChild(divLogo);
        divTxt.appendChild(divTxtSender);
        divTxt.appendChild(divTxtMsg);
        div.appendChild(divTxt);
        div.appendChild(divTime);
        body.appendChild(div);
        //Scripts
        //?????????????????? ?????????? ?? ????????????
        webEngine.executeScript("document.getElementById(\"" + idMsg + "\").innerHTML = '" + message+"'");
        //???????????????? ???????????????? ???? ?????????????????? ??????????????
        webEngine.executeScript("document.body.scrollTop = document.body.scrollHeight");
        //???????????????? ???? ?????????????? ???? ???????????????? ????????????
        addListenerLinkExternalBrowser(divTxtMsg);
        //??????????????????, ???????? ???? ?? ?????? ?? ???????????????????? ????????????????
        addImageMessageListener(divTxtMsg);
    }

    public void showMessage(String senderName, String message, Timestamp timestamp, boolean isNew) {
        /*if (isNew) {
            Sound.playSoundNewMessage().join();
        }*/

        String attrClass;
        if (clientController.getSenderName().equals(senderName)) {
            attrClass = "myUserClass";
        } else {
            attrClass = "senderUserClass";
        }

        //todo ???? ???????????????? ???????? ?????????????????????? ???????????????? ???? ?????????????? ?? ???????????? ??????????
        //???????????????? ???? ?????????????? ???????????????? ?????????????????? HTML in WebView
        if (DOMdocument == null) {
            //???????? ???????????????????????? ???????????? ???????????????? ?????????????? ?? ???????????????? ?????? ???? ???????????? ??????????????????
            if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                DOMdocument = webEngine.getDocument();
                createMessageDiv(message, senderName, timestamp, attrClass);
                updateLastMessageInCardsBody(message, senderName);
            }else {
                webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        DOMdocument = webEngine.getDocument(); // ???????????? ???????? ?????????? ??.??. ???????????????? WebEngine ???????????? ??????????????????
                        createMessageDiv(message, senderName, timestamp, attrClass);
                        updateLastMessageInCardsBody(message, senderName);
                    }
                });
            }
        }else {
            createMessageDiv(message, senderName, timestamp, attrClass);
            updateLastMessageInCardsBody(message, senderName);
        }
    }

    private void updateLastMessageInCardsBody(String message, String senderName){
        CFXListElement targetChat = null;

        for (CFXListElement element : contactsObservList){
            if (element.getUser().getAccount_name().equals(senderName)) targetChat = element;
        }
        if (targetChat == null) return; //TODO ???????????????????? ?????????????????????? ?? ???????????????? (?????????? ???????????? ????????????????????????, ??????)
        targetChat.setBody(senderName + ": " + message);
    }

    @FXML
    public void handleDisconnectButton() {
        Stage stage = (Stage) messagePanel.getScene().getWindow();
        stage.close();
        clientController.disconnect();
        Tray.currentStage = null;
        Main.initRootLayout();
        Main.showOverview();
    }

    private void handleExit() {
        clientController.disconnect();
        System.exit(0);
    }

    @FXML
    private void handleSendMessage() {
        if (!messageField.getText().isEmpty()) {
            clientController.sendMessage(messageField.getText());
            messageField.clear();
            messageField.requestFocus();
        }
    }

    @FXML
    private void handleClientChoice(MouseEvent event) {
        long receiver = contactListView.getSelectionModel().getSelectedItem().getUser().getUid();
        if (event.getClickCount() == 1) {
            //showAlert("?????????????????? ?????????? ???????????????????????? ???????????????? " + receiver, Alert.AlertType.INFORMATION);
            clientController.setReceiver(receiver);
            messageField.requestFocus();
            messageField.selectEnd();
        } else if (event.getClickCount() == 2) {
            othersProfile.setUser(
                    contactListView.getSelectionModel().getSelectedItem().getUser());
            othersProfile.setIfFriendly(true);
            PaneProvider.setProfileScrollPane(otherProfileScrollPane);
            paneProvidersProfScrollPaneVisChange(true);
        }
    }

    @FXML
    private void handleFindedClientChoice(MouseEvent event) {
        long receiver = searchListView.getSelectionModel().getSelectedItem().getUser().getUid();
        if (event.getClickCount() == 1) {
            if (clientController.hasReceiver(receiver)) {
                btnContactSearchInvite.setVisible(false);
                clientController.setReceiver(receiver);
                messageField.requestFocus();
                messageField.selectEnd();
            } else {
                clearMessageWebView();
                btnContactSearchInvite.setVisible(true);
            }
        } else if (event.getClickCount() == 2) {
            othersProfile.setUser(
                    searchListView.getSelectionModel().getSelectedItem().getUser());
            othersProfile.setIfFriendly(clientController.hasReceiver(receiver));
            PaneProvider.setProfileScrollPane(otherProfileScrollPane);
            paneProvidersProfScrollPaneVisChange(true);
        }
    }

    private void paneProvidersProfScrollPaneVisChange(boolean newVisStat) {
        PaneProvider.getProfileScrollPane().setVisible(newVisStat);
        if (newVisStat) PaneProvider.getProfileScrollPane().setVvalue(0f); //scroll to top
    }

    //?????????????????? ?????????????????????????????? ????????????????
    private void addImageMessageListener(Element tagElement) {
        NodeList nodeList = tagElement.getElementsByTagName("img");
        for (int i = 0; i < nodeList.getLength(); i++) {
            initSmile();
        }
    }

    //???????????????? ???? ?????????????????? ???????????????? ????????????
    //Element tagElement = <div class="msg">
    private void addListenerLinkExternalBrowser(Element tagElement){
        NodeList nodeList = tagElement.getElementsByTagName("a");
        for (int i = 0; i < nodeList.getLength(); i++) {
            ((EventTarget) nodeList.item(i)).addEventListener("click", listenerLinkExternalBrowser(), false);

        }
    }

    //???????????????????? ???????????????? ???????????? ???? ?????????????? ????????????????
    private EventListener listenerLinkExternalBrowser(){
        EventListener listener = new EventListener() {

            @Override
            public void handleEvent(Event evt) {
                String domEventType = evt.getType();
                if ("click".equals(domEventType)) {
                    String href = ((Element) evt.getTarget()).getAttribute("href");
                    try {
                        // Open URL in Browser:
                        //???? ????????????, ??.??. ???? ?????????? ???? ?????????????? ???????? ??????????
                        //if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(new URI(href.contains("://") ? href : "http://" + href + "/"));
                            //???????????????? ??????????????, ?????????? ???????????? ???? ?????????????????????? ?? ?????????? webView
                            evt.preventDefault();
                        /*} else {
                            System.out.println("Could not load URL: " + href);
                        }*/
                    } catch (IOException | URISyntaxException e) {
                        //todo logger
                        e.printStackTrace();
                    }
                }
            }
        };
        return listener;
    }

    @FXML
    public void handleSendFile() {
        Stage stage = (Stage) messagePanel.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                this.desktop.open(file);//?????????????????????? ???????? ???? ????????????????????
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<File> files = Arrays.asList(file);
            if (files == null || files.isEmpty()) return;
            for(File f : files) {
                messageField .appendText(f.getAbsolutePath() + "\n");
            }
        }
    }

    //?????????? ?????? ?????????????????????????? ????????????????. ???????????????? ???????? ?????? ???????????????? ???????????????? ???? ????
    public void initSmile() {
        String path = "client/smiley/wink.png";//???????? ?????????? ?????????????????????? ?????????????????? ???????????? ???????? ?????? ????????????????

        ClassLoader cl = this.getClass().getClassLoader();
        String emoji = "";
        try {
            emoji = cl.getResource(path).toURI().toString();
            webEngine.executeScript("document.getElementById(\"" + (getIdMsg()) + "\").innerHTML = '" + "<img src = \"" + emoji + "\" width=\"30\" alt=\"lorem\"/>" +"'");
        }catch (Exception e) {
            //todo ?????????????????? ?? ??????????????????????
            e.printStackTrace();
        }

    }

    //?????????? ???????????????????? ??????????????????
    @FXML
    public void handleSendSmile() {
        String img = "";
        File f = new File(getClass().getResource("/client/smiley").getFile());

        for (File fs : f.listFiles()) {
            img += fs.toURI();
            clientController.sendMessage(img);
            webEngine.executeScript("document.getElementById(\"" + idMsg + "\").innerHTML = '" + "<img src = \"" + img + "\" width=\"30\" alt=\"lorem\"/>" +"'");
            setIdMsg(idMsg++);
            webEngine.executeScript("document.getElementById(\"" + (getIdMsg()) + "\").innerHTML = '" + "<img src = \"" + img + "\" width=\"30\" alt=\"lorem\"/>" +"'");
        }
    }

    /**
     * ???????????????????? ?????? ???????????? ?????????????????? ???????????? WebEngine
     * ?????? ???????????? ???????????? ???????????? ??????, ??.??. DOMdocument == null
     * ?????? ???? ???????????????? ???????? ?????? ?????????????????????? (tsOld) ?? ID ?????? DIV
     */
    public void clearMessageWebView() {
        if (DOMdocument != null) {
            //???????????? ??????, ?????? ???????????? ?????????? <body></body>
            Node body = DOMdocument.getElementsByTagName("body").item(0);
            Node fc = body.getFirstChild();
            while (fc != null) {
                body.removeChild(fc);
                fc = body.getFirstChild();
            }
        }

        tsOld = null; //???????????? ????????
        idDivMsg =0; //???????????????????????? ID
    }

    //?????????? ?????????? ????????????
    @FXML
    public void handleOnChatSelected() {
        chats.setGraphic(buildImage("/client/images/chat/chatsActive.png"));
        if (contacts != null) {
            contacts.setGraphic(buildImage("/client/images/chat/contacts.png"));
            contacts.setStyle("-fx-border-width: 0 0 5 0; " +
                    "          -fx-border-color: #3498DB #3498DB transparent #3498DB;" +
                    "-fx-border-insets: 0;" +
                    "          -fx-border-style: solid;");
        }
        chats.setStyle("-fx-border-width: 0 0 5 0; " +
                        "-fx-border-color: transparent transparent #F8D57D transparent;" +
                "-fx-border-insets: 0;" +
                        "-fx-border-style: solid;");
    }

    @FXML
    public void handleOnContactSelected() {
        contacts.setGraphic(buildImage("/client/images/chat/contactsActive.png"));
        chats.setGraphic(buildImage("/client/images/chat/chats.png"));
        contacts.setStyle("-fx-border-width: 0 0 5 0; " +
                "-fx-border-color: transparent transparent #F8D57D transparent;" +
                "-fx-border-insets: 0;" +
                "-fx-border-style: solid;");
        chats.setStyle("-fx-border-width: 0 0 5 0; " +
                "       -fx-border-color: #3498DB #3498DB transparent #3498DB;" +
                "-fx-border-insets: 0;" +
                "       -fx-border-style: solid;");
    }

    private ImageView buildImage(String s) {
        Image i = new Image(s);
        ImageView imageView = new ImageView();
        imageView.setImage(i);
        return imageView;
    }

    @FXML
    public void handleGroupNewButton(MouseEvent mouseEvent) {

        groupListPane.setVisible(false);
    }

    public void onGroupSearchCancelButtonPressed(ActionEvent actionEvent) {
        groupSearchPane.setVisible(false);
        groupListPane.setVisible(true);
    }

    public void onSearchGroupButtonClicked(ActionEvent actionEvent) {
        groupListPane.setVisible(false);
        groupSearchPane.setVisible(true);
    }

    @FXML
    public void onNewGroupClicked(ActionEvent actionEvent) {
        selectionModel.select(0);
        cfxMenuLeft.setVisible(false);
        menuLeff.hide();
        groupListPane.setVisible(false);
        listViewAddToGroup.setExpanded(true);
        groupNewPane.setVisible(true);
    }

    @FXML
    public void onGroupNewCancelButtonPressed(ActionEvent actionEvent) {
        groupNewPane.setVisible(false);
        groupListPane.setVisible(true);
    }

    @FXML
    public void onMyProfileOpen(ActionEvent actionEvent) {
        PaneProvider.setProfileScrollPane(myProfileScrollPane);
        cfxMenuLeft.setVisible(false);
        menuLeff.hide();
        myProfile.setUser(clientController.getMyUser());
        paneProvidersProfScrollPaneVisChange(true);

        PaneProvider.getTransitionBack().setRate(1);
        PaneProvider.getTransitionBack().play();
    }

    @FXML
    public void onHamburgerClicked(MouseEvent mouseEvent) {
        if (myProfileScrollPane.isVisible()) {
            myProfileScrollPane.setVisible(false);
            PaneProvider.getTransitionBack().setRate(-1);
            transitionBack.play();
        }
        else if (!menuLeff.isShowing()){
            transition.setRate(1);
            transition.play();
//            menuLeff.show();
            cfxMenuLeft.setVisible(true);
        } else {
            menuLeff.hide();
            cfxMenuLeft.setVisible(false);
        }

    }

    @FXML
    public void onHideMenuLeft(javafx.event.Event event) {
        transition.setRate(-1);

        transition.play();
    }

    public void handleGroupJoinButton(){
//        clientController.joinGroup(groupName.getText());
    }

    @FXML
    public void handleGroupCreateButton(){
        clientController.addGroup(creategroupName.getText());
    }

    @FXML
    public void findContact(KeyEvent keyEvent) {
        if (tfSearchInput.getText().length()>0) {
            searchObsList.clear();
            contactsViewPane.setVisible(false);
            contactSearchPane.setVisible(true);
            contactsObservList.forEach(elem -> {
                if (elem.getUser().getEmail().contains(tfSearchInput.getText()) ||
                        elem.getUser().getAccount_name().contains(tfSearchInput.getText())) {
                    CFXListElement temp = new CFXListElement();
                    temp.setUser(elem.getUser());
                    temp.setBody(elem.getUser().getEmail());
                    searchObsList.add(temp);
                }
            });
            // todo: ?????????? ???? ?????????????? ???? 2?? ????????????????, ????????????/?????????????????? ???????????????????????
            if (tfSearchInput.getText().length()>=2) {
                List<CFXListElement> searchFromServer = clientController.findContact(tfSearchInput.getText());
                //todo: ???????????? ?????????????????????????? (????????????/????????????) - ?????????? ?????????????????? ?? ?????????????? ?????? ?????????????????????? ???? ???????????????
                if (searchFromServer != null) {
                    searchFromServer.removeAll(searchObsList);
                    searchFromServer.remove(new CFXListElement(clientController.getMyUser()));
                    searchFromServer.forEach(elem -> {
                        CFXListElement temp = new CFXListElement();
                        temp.setUser(elem.getUser());
                        temp.setBody(elem.getUser().getEmail());
                        searchObsList.add(temp);
                    });
                }
            }
            selectionModel.select(1);
            searchListView.refresh();
            if (btnContactSearchInvite.isVisible()) btnContactSearchInvite.setVisible(false);
        } else {
            contactsViewPane.setVisible(true);
            contactSearchPane.setVisible(false);
        }

    }

    @FXML
    private void contactSearchBtnInviteClicked() {
        String receiver = searchListView.getSelectionModel().getSelectedItem().getUser().getEmail();
        clientController.addContact(receiver);
        contactSearchBtnCancelClicked();
    }

    @FXML
    private void contactSearchBtnCancelClicked() {
        contactsViewPane.setVisible(true);
        tfSearchInput.setText("");
        contactSearchPane.setVisible(false);
    }

    @FXML
    public void onMouseExitMenu(MouseEvent mouseEvent) {
        cfxMenuLeft.setVisible(false);
        transition.setRate(-1);

        transition.play();
    }

    @FXML
    public void onMouseExitMenuRight(MouseEvent mouseEvent) {
        cfxMenuRightGroup.setVisible(false);
    }

    @FXML
    public void btnRightMenuClicked(ActionEvent actionEvent) {
        if (cfxMenuRightGroup.isVisible()){
            cfxMenuRightGroup.setVisible(false);
        } else {

            cfxMenuRightGroup.setVisible(true);
        }
    }
    public void alarmGroupQuitGroupExecute(){
        new AlarmGroupQuitGroup();
    }

    public void alarmGroupDeleteGroupExecute(){
        new AlarmDeleteGroup();
    }
    public void alarmDeleteMessageHistoryExecute(){
        new AlarmDeleteMessageHistory();
    }
    public void alarmDeleteProfileExecute(){
        new AlarmDeleteProfile();
    }
    public void alarmExitProfileExecute(){
        new AlarmExitProfile();
    }

}
