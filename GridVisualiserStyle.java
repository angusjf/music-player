import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

class Vector3 {
    public double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void update() {
        /*
            points[j][i] = Maths.lerp(
                points[j][i],
                Main.musicController.getMagnitudeOfFrequency(i) * VisualiserSceneController.HEIGHT,
                0.5
            );*/
    }

    public void add(Vector3 a) {
        x = x + a.x;
        y = y + a.y;
        z = z + a.z;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

}

public class GridVisualiserStyle implements VisualiserStyle {

    GraphicsContext gc;
    Vector3[] points = new Vector3[4];

    private static Vector3
        s = new Vector3(1,1,1),
        c = new Vector3(0,0,0);

    public GridVisualiserStyle(GraphicsContext gc) {
        this.gc = gc;
        {
            double s = 50;
            points[0] = new Vector3(-s, -s, 0);
            points[1] = new Vector3(-s, +s, 0);
            points[2] = new Vector3(+s, +s, 0);
            points[3] = new Vector3(+s, -s, 0);
        }
    }

    public Color getBackgroundColor() {
        return Color.rgb(0, 0, 0);
    }

    public void draw() {
        gc.setStroke(Color.rgb(255, 0, 0));
        for (int i = 0; i < points.length; i++) {
            points[i].update();
            System.out.println(points[i]);
            drawPoint(points[i], 3);
            //gc.setStrokeWidth(2);
            if (i != points.length - 1)
                drawLine(points[i], points[i + 1]);
        }
    }

    private void drawPoint(Vector3 a, int r) {
        double[][] scale_m = new double[2][3];
        scale_m[0][0] = s.x; scale_m[0][1] = 0; scale_m[0][2] = 0;
        scale_m[1][0] = 0;   scale_m[1][1] = 0; scale_m[1][2] = s.y;

        Vector3 b = new Vector3(0,0,0);

        b.x = s.x * a.y;
        b.y = s.y * a.y;

        b.add(c);

        strokePoint(a.x * s.x, a.y * s.y, r);
    }

    private void drawLine(Vector3 a, Vector3 b) {
        gc.strokeLine(
            (a.x * 0.005 + 0.5) * VisualiserSceneController.WIDTH,
            (a.y * 0.005 + 0.5) * VisualiserSceneController.HEIGHT,
            (b.x * 0.005 + 0.5) * VisualiserSceneController.WIDTH,
            (b.y * 0.005 + 0.5) * VisualiserSceneController.HEIGHT
        );
    }

    private void strokePoint(double x, double y, int r) {
        gc.strokeOval(
            (x * 0.005 + 0.5) * VisualiserSceneController.WIDTH - (r * 0.005) * VisualiserSceneController.WIDTH / 2,
            (y * 0.005 + 0.5) * VisualiserSceneController.HEIGHT - (r * 0.005) * VisualiserSceneController.HEIGHT / 2,
            (r * 0.005) * VisualiserSceneController.WIDTH,
            (r * 0.005) * VisualiserSceneController.HEIGHT
        );
    }

    public void leftKey() {
        s.x --;
    }

    public void rightKey() {
        s.x ++;
    }

    public void upKey() {
        s.y ++;
    }

    public void downKey() {
        s.y --;
    }

	public String toString() {
		return "Grid";
	}
}
/*
    private int toGridX(int x, int y) {
        x = x * VisualiserSceneController.WIDTH / points.length;
        //x -= (points.length - x) / 2 * VisualiserSceneController.HEIGHT / points.length;
        //x *= 0.5;
        //x += (points.length - x) / 2 * VisualiserSceneController.HEIGHT / points.length;
        x += 8;
        return x;
    }

    private int toGridY(int x, int y) {
        y = y * VisualiserSceneController.HEIGHT / points.length;
        // - x / 2 * VisualiserSceneController.WIDTH / points.length);
        y += 8;
        return y;
    }

	private void drawPoint(int x, int y, double height) {
		gc.setLineWidth(3);
		gc.setStroke(Color.rgb(255, 0, 0));
		gc.strokeLine(x+height*y/640, y, x+height*y/640, y);
			Math.round(((double)barNo / (double)points.length) * VisualiserSceneController.WIDTH) * 2 + 2,
			VisualiserSceneController.HEIGHT - 3,
			Math.round(((double)barNo / (double)points.length) * VisualiserSceneController.WIDTH) * 2 + 2 + -3 * (barNo - points.length*0.25),
			VisualiserSceneController.HEIGHT - 3 + height * -2
		);
	}

    public void leftKey() {
    }

    public void rightKey() {
    }

    public void upKey() {
    }

    public void downKey() {
    }

	public String toString() {
		return "Grid";
	}

}
*/
