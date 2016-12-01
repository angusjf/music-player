 

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

	final int WIDTH = 640, HEIGHT = 480;
	//backgroundColor;
	//foregroundColor;

	final int noOfBars = (int)(Math.random() * 50);

	private double[] bars = new double[noOfBars];

	public VisualiserSceneController() {
		System.out.println("+ VisualiserSceneController Scene Controller class started");
		
		new AnimationTimer() {
			@Override
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
		stage.show();
	}

	public void show() {
		//TODO
	}

	public void hide() {
		//TODO
	}

	private void update() {
		for (int i = 0; i < noOfBars; i++) {
			bars[i] = getHeight((double)i/(noOfBars-1));
		}
	}

	private void draw() {
		clear();
		gc.setFill(Color.rgb(100,200,240));
		for (int i = 0; i < bars.length; i++) {
			gc.fillRect(
				Math.round(((double)i / (double)noOfBars) * WIDTH) *2 ,
				HEIGHT - bars[i],
				WIDTH/noOfBars,
				bars[i]
			);
		}
	}

	private void clear() {
		gc.setFill(Color.rgb(230,250,230));
		gc.fillRect(0, 0, WIDTH, HEIGHT);
	}

	private double getHeight(double n) {
		System.out.println(Main.musicController.getFrequency(n) * HEIGHT);
		return Main.musicController.getFrequency(n) * HEIGHT;
	}

	private double lerp(double a, double b, double t) {
 		return a * (1-t) + (b*t);
	}

}
