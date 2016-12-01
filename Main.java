
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.scene.image.Image;

public class Main extends Application {

    static final String DATABASE_FILE = "database.db";
    final String FXML_SCENE_FILE = "MainScene.fxml";
    final String SCENE_TITLE = "Music Player";
    final int MIN_HEIGHT = 400, MIN_WIDTH = 600;

    public static DatabaseController database;
    public static LibraryController libraryController;
    public static MusicController musicController;

    public static void main(String[] args) {
        System.out.println("+ Application Started");
        database = new DatabaseController(DATABASE_FILE);
        libraryController = new LibraryController();
        musicController = new MusicController();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_SCENE_FILE));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(SCENE_TITLE);
        stage.setMinHeight(MIN_WIDTH);
        stage.setMinWidth(MIN_HEIGHT);
        stage.getIcons().add(new Image("file:resources/logo.png"));
        stage.show();

        // secure close button
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    terminate();
                    we.consume();
                }
            });
    }

    public static void terminate() {
        database.disconnect();
        System.exit(0);
    }

    public static void openVisualiser() {
        //TODO
        VisualiserSceneController visualiserSceneController = new VisualiserSceneController();
    }

}
