import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.*;
import javafx.animation.AnimationTimer; 

class VisualiserSceneController {

	Stage stage;
	GraphicsContext gc;
	
	final int SCALE = 2;
	final int WIDTH = 640 * SCALE, HEIGHT = 480 * SCALE;
	final int noOfBars = 128;
	private double[] bars = new double[noOfBars];

	public VisualiserSceneController() {
		new AnimationTimer() {
			public void handle(long now) {
				update();
				draw();
			}
		}.start();

		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		gc = canvas.getGraphicsContext2D();

		Group root = new Group();
		root.getChildren().add(canvas);
		Scene scene = new Scene(root);

		stage = new Stage();
		stage.setScene(scene);
		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT+22);
		stage.setTitle("Visualiser");
		//stage.setAlwaysOnTop(true);
		stage.setOnCloseRequest( we -> {hide(); we.consume();} );
		stage.setResizable(false);
		show();
	}

	public void show() {
		stage.show();
	}

	public void hide() {
		stage.close();
	}

	private void update() {
		for (int i = 0; i < noOfBars; i++) {
			bars[i] = lerp(bars[i], getHeight(i), 0.5);
		}
	}

	int tick = 0;
	double last[] = new double[bars.length];
	private void draw() {
		clear();
		//text and picture
		for (int i = 0; i < bars.length; i++) {
			gc.setFill(Color.hsb(i*360/bars.length + tick, 1, 1));
			drawBar(i, bars[i] * 0.5);
			gc.setFill(Color.hsb(i*360/bars.length + tick, 1, 0.5));
			drawBar(i, -bars[i] * 0.5);

			/* NORMAL
			gc.fillRect( Math.round(((double)i / (double)noOfBars) * WIDTH) * 2 + 2, (HEIGHT - bars[i]) - HEIGHT * 0.5 + bars[i] * 0.5, WIDTH/noOfBars, bars[i]);
			//reflecton mode??
			gc.fillRect( Math.round(((double)i / (double)noOfBars) * WIDTH) * 2 + 2, HEIGHT * 0.5, WIDTH/noOfBars, bars[i] * 0.5);
			gc.setFill(Color.hsb(i*360/bars.length + tick, 0, 0.8));
			gc.fillRect( Math.round(((double)i / (double)noOfBars) * WIDTH) * 2 + 2, HEIGHT * 0.5 + bars[i], WIDTH/noOfBars, bars[i] * 0.5); 
			COOL
			gc.fillRect( Math.round(((double)i / (double)noOfBars) * WIDTH) * 2 + 2, (HEIGHT - bars[i]) - HEIGHT * 0.5 + bars[i], WIDTH/noOfBars, bars[i] * 0.5);
			gc.setFill(Color.hsb(i*360/bars.length + tick, 1, 0.8));
			gc.fillRect( Math.round(((double)i / (double)noOfBars) * WIDTH) * 2 + 2, (HEIGHT - bars[i]) - HEIGHT * 0.5 - bars[i], WIDTH/noOfBars, bars[i] * 0.5); */ 
		}
		tick -= 1;
	}

	private void drawBar(int xPos, double height) {
		if (height < 0) {
			gc.fillRect(
				Math.round(((double)xPos / (double)noOfBars) * WIDTH) * 2 + 2,
				HEIGHT * 0.5,
				WIDTH/noOfBars,
				height * -1
			);
		} else {
			gc.fillRect(
				Math.round(((double)xPos / (double)noOfBars) * WIDTH) * 2 + 2,
				HEIGHT * 0.5 - height,
				WIDTH/noOfBars,
				height
			);
		}
	}

	private void clear() {
		gc.setFill(Color.rgb(12,12,12));
		gc.fillRect(0, 0, WIDTH, HEIGHT);
	}

	private double getHeight(int n) {
		double height;
		height = Main.musicController.getMagnitudeOfFrequency(n) * HEIGHT;
		return height;
	}

	private double lerp(double a, double b, double t) {
 		return a * (1-t) + (b*t);
	}
}
