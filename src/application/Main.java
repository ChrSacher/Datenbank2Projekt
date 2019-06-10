package application;



import java.io.File;
import java.net.URL;

import com.sun.javafx.css.StyleManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application
{

	public static Stage primStage;
	public static Scene primScene;

	@Override
	public void start(Stage primaryStage)
	{
		try
		{
    		    
			Pane root = FXMLLoader.load(getClass().getResource("view/LoginView.fxml"));
			Scene scene = new Scene(root, 800, 600);
			File style  = new File("./res/FlatBee.css");
			if(style.exists()) {
			    scene.getStylesheets().add("./res/FlatBee.css");
			}
			
			primaryStage.setScene(scene);
			primaryStage.show();
			primStage = primaryStage;
			primScene = scene;

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}

	public static Stage getPrimStage()
	{
		return primStage;
	}

	public static void setPrimStage(Stage primStage)
	{
		Main.primStage = primStage;
	}

	public static Scene getPrimScene()
	{
		return primScene;
	}

	public static void setPrimScene(Scene primScene)
	{
		Main.primScene = primScene;
	}

}
