import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

class Point {
    static final int   maxRadius = Math.min(VisualiserSceneController.HEIGHT, VisualiserSceneController.WIDTH) / 2,
                minRadius = maxRadius / ShapeVisualiserStyle.minCircleDivider;

    static final int   offsetX = VisualiserSceneController.WIDTH / 2,
                offsetY = VisualiserSceneController.HEIGHT / 2;

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
        double fPos = 2 * Math.PI * freq / 128 * ShapeVisualiserStyle.divider;
        xPos = Math.sin(fPos);
        xPos *= minRadius + (maxRadius - minRadius) * f;
        return xPos + offsetX;
    }

    public double getYPosFromFreq(double f) {
        double yPos;
        double fPos = 2 * Math.PI * freq / 128 * ShapeVisualiserStyle.divider;
        yPos = Math.cos(fPos);
        yPos *= minRadius + (maxRadius - minRadius) * f;
        return yPos + offsetY;
    }
}

public class ShapeVisualiserStyle implements VisualiserStyle {
    //THINGS THAT CHANGE THE LOOK (UI OPTION?)
    public static int divider = 4;
    public static int minCircleDivider = 3;

    GraphicsContext gc;
    Point[] points = new Point[128/divider];
    final Color lineColor = Color.rgb(240, 39, 94);

    public ShapeVisualiserStyle(GraphicsContext gc) {
        this.gc = gc;
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(Color.rgb(240, 39, 94), i * 128 / points.length / divider);
        }
    }

    public Color getBackgroundColor() {
        return Color.rgb(33, 33, 33);
    }

    public void draw() {
        double[] xs = new double[points.length], ys = new double[points.length];
		gc.setFill(Color.hsb(0,0,0.9));
        for (int i = 0; i < points.length; i++) {
            points[i].updatePos();
            xs[i] = points[i].x;
            ys[i] = points[i].y;
        }
        gc.fillPolygon(xs, ys, points.length);
		gc.setFill(getBackgroundColor());
        //middle bit
        //gc.fillOval(Point.offsetX - Point.minRadius, Point.offsetY - Point.minRadius, Point.offsetX * 0.75, Point.offsetY);

        //OUTLINE
        /* for (int i = 0; i < points.length; i++) {
            points[i].updatePos();
            drawPoint(points[i]);
        }
        for (int i = 0; i < points.length - 1; i++)
            drawLine(points[i], points[i + 1]);
        drawLine(points[points.length - 1], points[0]); */
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

    public void leftKey() {
        divider -= 1;
    }

    public void rightKey() {
        divider += 1;
    }

    public void upKey() {
        minCircleDivider += 1;
    }

    public void downKey() {
        minCircleDivider -= 1;
    }

	public String toString() {
		return "Shape";
	}

}
