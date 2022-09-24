package swapper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.Objects;

public class Main extends Application
{
    public static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Main.mainStage = primaryStage;

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("swapper.fxml")));

        Scene scene = new Scene(root);
        new JMetro(scene, Style.LIGHT);

        primaryStage.setTitle("Word Swap");
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args)
    {
        Application.launch(args);
    }
}
