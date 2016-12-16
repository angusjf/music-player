import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

class Point {
    static int   maxRadius = Math.min(VisualiserSceneController.HEIGHT, VisualiserSceneController.WIDTH) / 2,
                minRadius = maxRadius / ShapeVisualiserStyle.minCircleDivider;

    static final int   offsetX = VisualiserSceneController.WIDTH / 2,
                offsetY = VisualiserSceneController.HEIGHT / 2;

    static double lerpAmount = 1;

    public double x = 0, y = 0;
    public Color color;
    private int freq;

    Point(Color color, int freq) {
        this.color = color;
        this.freq = freq;
    }

    public void updatePos() {
        x = Maths.lerp( x, getXPosFromFreq(Main.musicController.getMagnitudeOfFrequency(freq)), lerpAmount );
        y = Maths.lerp( y, getYPosFromFreq(Main.musicController.getMagnitudeOfFrequency(freq)), lerpAmount );
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
    private boolean debugMode = false;

    GraphicsContext gc;
    Point[] points = new Point[Main.musicController.numberOfBands / divider]; //TODO understand more
    final Color lineColor = Color.rgb(240, 39, 94);

    public ShapeVisualiserStyle(GraphicsContext gc) {
	    System.out.println(points.length);
        this.gc = gc;
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(
                Color.hsb(0, (double)i / points.length, 1),
                i * 128 / points.length / divider
            );
        }
    }

    public Color getBackgroundColor() {
        return Color.rgb(33, 33, 33);
    }

    public void draw() {
        double[] xs = new double[points.length], ys = new double[points.length];
        for (int i = 0; i < points.length; i++) {
            points[i].updatePos();
            xs[i] = points[i].x;
            ys[i] = points[i].y;
        }
		gc.setFill(Color.hsb(0,0,0.9));
        gc.fillPolygon(xs, ys, points.length);
		gc.setFill(getBackgroundColor());
        //middle bit
        //gc.fillOval(Point.offsetX - Point.minRadius, Point.offsetY - Point.minRadius, Point.offsetX * 0.75, Point.offsetY);

        //OUTLINE
        if (debugMode) {
            drawLine(points[points.length - 1], points[0]);
            for (int i = 0; i < points.length; i++) {
                points[i].updatePos();
                if (i != points.length - 1) {
                    drawLine(points[i], points[i + 1]);
                }
                drawPoint(points[i]);
            }
        }
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

    public void leftKey() {/*
        divider -= 1;
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(
                Color.hsb(0, (double)i / points.length, 1),
                i * 128 / points.length / divider
            );
        }
        System.out.println("% > set visualiser to show 1/" + divider + " of the spectrum, with");
	*/
	Point.lerpAmount -= 0.1;
        System.out.println("% > set visualiser to lerp " + divider);
    }

    public void rightKey() {
	/*
        divider += 1;
        System.out.println("% > set visualiser to show 1/" + divider + " of the spectrum");
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(
                Color.hsb(0, (double)i / points.length, 1),
                i * 128 / points.length / divider
            );
        }
	*/
	Point.lerpAmount += 0.1;
        System.out.println("% > set visualiser to lerp " + divider);
    }

    public void upKey() {
        minCircleDivider += 1;
        Point.minRadius = Point.maxRadius / ShapeVisualiserStyle.minCircleDivider;
        System.out.println("% > set min radius to be 1/" + minCircleDivider + " of max radius");
    }

    public void downKey() {
        minCircleDivider -= 1;
        Point.minRadius = Point.maxRadius / ShapeVisualiserStyle.minCircleDivider;
        System.out.println("% > set min radius to be 1/" + minCircleDivider + " of max radius");
        debugMode = !debugMode;
    }

	public String toString() {
		return "Shape";
	}

}
