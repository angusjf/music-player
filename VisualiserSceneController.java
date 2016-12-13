import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.animation.AnimationTimer;

class VisualiserSceneController {

	Stage stage;
	GraphicsContext gc;
	VisualiserStyle visualiser;

	static final int SCALE = 1;
	static final int WIDTH = 640 * SCALE, HEIGHT = 480 * SCALE;

	public VisualiserSceneController() {

		Canvas canvas = new Canvas(WIDTH * 2, HEIGHT);
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
		//stage.setResizable(false);
		show();

		new AnimationTimer() {
			public void handle(long now) {
				draw();
			}
		}.start();

		//VisualiserStyle[] //TODO

		visualiser = new ShapeVisualiserStyle(gc);//TODO
	}

	public void show() {
		stage.show();
		stage.toFront();
	}

	public void hide() {
		//stage.close();
		visualiser = new GridVisualiserStyle(gc);//TODO
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
