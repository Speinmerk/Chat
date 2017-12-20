package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Chat");
        Scene scene = new Scene(root, 300, 400);
        scene.getStylesheets().add(0, "styles/style.css");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(316);
        primaryStage.setMinHeight(439);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
