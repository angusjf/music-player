import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class VisualiserSceneController {

	Stage stage;
	GraphicsContext gc;
	VisualiserStyle visualiser;

	static int
	SCALE = 2,
	WIDTH = 640 * SCALE,
	HEIGHT = 480 * SCALE;

	public VisualiserSceneController() {
		Canvas canvas = new Canvas(WIDTH * 2, HEIGHT * 2);
		gc = canvas.getGraphicsContext2D();

		Group root = new Group();
		root.getChildren().add(canvas);
		Scene scene = new Scene(root);

		stage = new Stage();
		stage.setScene(scene);
		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT+22);
		stage.setTitle("Visualiser");
		stage.setOnCloseRequest( we -> {hide(); we.consume();} );
		stage.addEventHandler(KeyEvent.KEY_PRESSED, k -> {
			if (k.getCode() == KeyCode.LEFT) visualiser.leftKey();
			else if (k.getCode() == KeyCode.RIGHT) visualiser.rightKey();
			else if (k.getCode() == KeyCode.UP) visualiser.upKey();
			else if (k.getCode() == KeyCode.DOWN) visualiser.downKey();
			else if (k.getCode() == KeyCode.SPACE) Main.musicController.togglePaused();
		});
		//stage.setResizable(false); TODO
		show();

		new AnimationTimer() {
			public void handle(long now) {
				draw();
			}
		}.start();
	}

	public void show() {
		//visualiser = new BarVisualiserStyle(gc);
		visualiser = new ShapeVisualiserStyle(gc);
		stage.show();
		stage.toFront();
	}

	public void hide() {
		stage.close();
	}

	private void draw() {
		clear();
		visualiser.draw();
	}

	private void clear() {
		gc.setFill(visualiser.getBackgroundColor());
		gc.fillRect(0, 0, WIDTH, HEIGHT);
	}

}
