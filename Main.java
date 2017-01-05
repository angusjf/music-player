import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.File;

public class Main extends Application {

	static final String DATABASE_FILE = "user/database.db";
	final String FXML_SCENE_FILE = "data/MainScene.fxml";

	public static DatabaseController database;
	public static MusicController musicController; //should this be static?
	public static MainSceneController mainSceneController;
	public static VisualiserSceneController visualiserSceneController;

	public static void main(String[] args) {
		database = new DatabaseController();

		//check required files exist
		new File("user/").mkdir();
		new File("user/albums/").mkdir();
		new File("user/artists/").mkdir();

		File databaseFile = new File(DATABASE_FILE);
		if (!databaseFile.exists()) database.createDatabase(DATABASE_FILE);
		
		database.connect(DATABASE_FILE);
		musicController = new MusicController();
		launch(args);
	}

	@Override public void start(Stage stage) throws Exception {

		/* NOTES
		 * loader.load MUST be caled before loader.getController
		 * TODO - find a better way to throw / catch fxml errors
		 */

		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_SCENE_FILE));
		Scene scene = new Scene(loader.load());
		mainSceneController = (MainSceneController) loader.getController();
		mainSceneController.setupStage(stage, scene);
	}

	public static void terminate() {
		database.disconnect();
		System.exit(0);
	}

	public static void openVisualiser() {
		if (visualiserSceneController == null)
			visualiserSceneController = new VisualiserSceneController();
		else
			visualiserSceneController.show();
	}

}
