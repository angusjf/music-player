import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.effect.BlendMode;

public class ShapeVisualiserStyle implements VisualiserStyle {

	GraphicsContext gc;

	final Color fillColor = Color.hsb(0, 0, 0.95);

	final int
		maxRadius = Math.min(VisualiserSceneController.HEIGHT, VisualiserSceneController.WIDTH) / 2,
		minRadius = maxRadius / 3,
		offsetX = VisualiserSceneController.WIDTH / 2,
		offsetY = VisualiserSceneController.HEIGHT / 2;

	final int bandsUsed = Main.musicController.numberOfBands / 4;

	double lerpAmount = 0.30;

	//2d array for x and y
	double[][] points = new double[2][bandsUsed * 2];

	public ShapeVisualiserStyle(GraphicsContext gc) {
		this.gc = gc;
	}

	public void draw() {
		//create right half
		for (int f = 0; f < bandsUsed; f++) {
			points[0][f] = Maths.lerp(points[0][f], getXPosFromFreq(f), lerpAmount);
			points[1][f] = Maths.lerp(points[1][f], getYPosFromFreq(f), lerpAmount);
		}

		//create left half
		for (int i = bandsUsed; i < points[0].length; i++) {
			points[0][i] = -points[0][points[0].length - 1 - i] + VisualiserSceneController.WIDTH;
			points[1][i] = +points[1][points[0].length - 1 - i];
		}

		//draw a load of times for color effect
		final int
			xOffset = VisualiserSceneController.SCALE*4*
			(int)(points[1][0]-320*VisualiserSceneController.SCALE)/(100*VisualiserSceneController.SCALE),
			yOffset = VisualiserSceneController.SCALE*7*
			(int)(points[1][0]-320*VisualiserSceneController.SCALE)/(100*VisualiserSceneController.SCALE);

		gc.setGlobalBlendMode(BlendMode.ADD);

		gc.translate(0, -yOffset);
		gc.setFill(Color.rgb(255,0,0));
		gc.fillPolygon(points[0], points[1], points[0].length);

		gc.translate(-xOffset, yOffset);
		gc.setFill(Color.rgb(0,255,0));
		gc.fillPolygon(points[0], points[1], points[0].length);

		gc.translate(xOffset*2, 0);
		gc.setFill(Color.rgb(0,0,255));
		gc.fillPolygon(points[0], points[1], points[0].length);

		gc.translate(-xOffset, 0);

		gc.setGlobalBlendMode(BlendMode.SRC_OVER);
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
