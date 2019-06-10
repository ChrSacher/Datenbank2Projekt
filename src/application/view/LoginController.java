package application.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Stream;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private Button loginButton;
    
    @FXML
    private TextField userText;
    
    @FXML
    private TextField passwordText;
    
    @FXML
    private TextField dataBaseText;
    
    @FXML
    private ComboBox<String> serverCombo;
    
    @FXML
    public void initialize()
    {
	loginButton.setOnMouseClicked((v) -> login());
	final ObservableList<String> obs =  FXCollections.observableArrayList();
	try (Stream<String> stream = Files.lines(Paths.get("src/application/res/serverList.txt"))) {
	        stream.forEach(line -> obs.add(line.trim()));
	}catch(Exception e) {
	    e.printStackTrace();
	    Alert alert = new Alert(AlertType.ERROR); 
	    alert.setTitle("Server Error");
	    alert.setHeaderText("Serverlist File has not been found! Using default servers");
	    alert.setContentText(e.getMessage());	     
	    alert.showAndWait();
	    obs.clear();
	    obs.add("jdbc:sqlserver://localhost:1433;");
	    obs.add("jdbc:sqlserver://DESKTOP-K7FKTFD\\\\\\\\SQLEXPRESS:61180;");
	}
	
	
	serverCombo.setItems(obs);
	
	serverCombo.getSelectionModel().select(0);
    }
    
    
    public void login() {
	String userName = userText.getText();
	String passWord = passwordText.getText();
	String dataBase = dataBaseText.getText();
	String server = serverCombo.getSelectionModel().getSelectedItem();
	String connectionUrl = server + "database="+dataBase+";" + "user="+userName+";" + "password=" + passWord +";";
	try {
	    Connection con = DriverManager.getConnection(connectionUrl);
	    
	    try {
		Pane root;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
		Pane p = fxmlLoader.load();
		MainController fooController = (MainController) fxmlLoader.getController();

		fooController.login(con,userName);
		Main.primScene.setRoot(p);
		 
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	   
	    
	} catch (SQLException e) {
	    Alert alert = new Alert(AlertType.ERROR);
	    
	    alert.setTitle("Connection Error");
	    alert.setHeaderText("Could not establish a connection to the given DB!");
	    alert.setContentText(e.getMessage());
	     
	    alert.showAndWait();
	}
    }
}
