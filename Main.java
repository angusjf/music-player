import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	static final String DATABASE_FILE = "data/database.db";
	final String FXML_SCENE_FILE = "data/MainScene.fxml";

	public static DatabaseController database;
	public static MusicController musicController; //should this be static?

	public static void main(String[] args) {
		database = new DatabaseController(DATABASE_FILE);
		musicController = new MusicController();
		launch(args);
	}

	@Override public void start(Stage stage) throws Exception /* BUT MAKE IT MORE CATCHY HAHA */ {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_SCENE_FILE));
		System.out.println("ERROR: loader.getController() == '" + loader.getController() + "' (<- prints 'null')");
		//((MainSceneController) loader.getController()).setStage(stage, new Scene(loader.load()));
	}

	public static void terminate() {
		database.disconnect();
		System.exit(0);
	}

	public static void openVisualiser() {
		VisualiserSceneController visualiserSceneController = new VisualiserSceneController();
	}

}
