import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

class Point {
    final int minRadius = Math.min(VisualiserSceneController.HEIGHT, VisualiserSceneController.WIDTH) / 32,
        maxRadius = minRadius * 4;
    final int offsetX = VisualiserSceneController.WIDTH / 2,
        offsetY = (VisualiserSceneController.HEIGHT - minRadius) / 2;
    public double x = 0, y = 0;
    public Color color;
    private int freq;

    Point(Color color, int freq) {
        this.color = color;
        this.freq = freq;
    }

    public void updatePos() {
        x = Maths.lerp( x, getXPosFromFreq(Main.musicController.getMagnitudeOfFrequency(freq)), 0.5 );
        y = Maths.lerp( y, getYPosFromFreq(Main.musicController.getMagnitudeOfFrequency(freq)), 0.5 );
    }

    public double getXPosFromFreq(double f) {
        double xPos;
        double fPos = 2 * Math.PI * freq / 128;
        xPos = Math.sin(fPos);
        xPos *= maxRadius + (maxRadius - minRadius) * f;
        return xPos + offsetX;
    }

    public double getYPosFromFreq(double f) {
        double yPos;
        double fPos = 2 * Math.PI * freq / 128 / 4;
        yPos = Math.cos(fPos);
        yPos *= maxRadius + (maxRadius - minRadius) * f;
        return yPos + offsetY;
    }
}

public class ShapeVisualiserStyle implements VisualiserStyle {

    GraphicsContext gc;
    Point[] points = new Point[32];
    final Color lineColor = Color.rgb(240, 39, 94);

    public ShapeVisualiserStyle(GraphicsContext gc) {
        this.gc = gc;
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(Color.rgb(240, 39, 94), i * 128 / points.length / 4);
        }
    }

    public Color getBackgroundColor() {
        return Color.rgb(33,33,33);
    }

    public void draw() {
        for (int i = 0; i < points.length; i++) {
            points[i].updatePos();
            drawPoint(points[i]);
        }
        for (int i = 0; i < points.length - 1; i++) {
            drawLine(points[i], points[i + 1]);
        }
        drawLine(points[points.length - 1], points[0]);
    }

	private void drawPoint(Point point) {
		gc.setLineWidth(5);
		gc.setStroke(point.color);
		gc.strokeLine(point.x, point.y, point.x, point.y);
	}

	private void drawLine(Point a, Point b) {
		gc.setLineWidth(2);
		gc.setStroke(lineColor);
		gc.strokeLine(a.x, a.y, b.x, b.y);
	}

	public String toString() {
		return "Shape";
	}

}
