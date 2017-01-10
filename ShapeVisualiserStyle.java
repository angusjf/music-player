import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ShapeVisualiserStyle implements VisualiserStyle {

	GraphicsContext gc;

	final Color fillColor = Color.hsb(0, 0, 0.9);

	final int
		maxRadius = Math.min(VisualiserSceneController.HEIGHT, VisualiserSceneController.WIDTH) / 2,
		minRadius = maxRadius / 3,
		offsetX = VisualiserSceneController.WIDTH / 2,
		offsetY = VisualiserSceneController.HEIGHT / 2;

	final int bandsUsed = Main.musicController.numberOfBands / 4;

	final double lerpAmount = 0.25;

	//2d array for x and y
	double[][] points = new double[2][bandsUsed * 2];

	public ShapeVisualiserStyle(GraphicsContext gc) {
		this.gc = gc;
	}

	public void draw() {
		gc.setFill(fillColor);

		//right half
		for (int f = 0; f < bandsUsed; f++) {
			points[0][f] = Maths.lerp(points[0][f], getXPosFromFreq(f), lerpAmount);
			points[1][f] = Maths.lerp(points[1][f], getYPosFromFreq(f), lerpAmount);
		}

		//left half
		int toCopy = bandsUsed - 1;
		for (int i = bandsUsed; i < points[0].length; i++) {
			points[0][i] = -points[0][toCopy] + VisualiserSceneController.WIDTH;
			points[1][i] = +points[1][toCopy];
			toCopy--;
		}

		gc.fillPolygon(points[0], points[1], points[0].length);

		/*
		//temp point draw
		for (int i = 0; i < points[0].length; i++) {
			gc.setFill(Color.hsb((double)i / points[0].length * 360, 1, 1));
			gc.fillRect(points[0][i], points[1][i], 2, 2);

		}
		*/
	}

	public double getXPosFromFreq(int f) {
		return Math.max(maxRadius * Main.musicController.getMagnitudeOfFrequency(f), minRadius) * Math.sin(Math.PI * (double)f / bandsUsed) + offsetX;
	}

	public double getYPosFromFreq(int f) {
		return Math.max(maxRadius * Main.musicController.getMagnitudeOfFrequency(f), minRadius) * Math.cos(Math.PI * (double)f / bandsUsed) + offsetY;
	}

	public Color getBackgroundColor() {
		return Color.rgb(33, 33, 33);
	}

	public void leftKey() { }

	public void rightKey() { }

	public void upKey() { }

	public void downKey() { }

	public String toString() { return "Shape"; }

}
