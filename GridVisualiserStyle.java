import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

public class GridVisualiserStyle implements VisualiserStyle {

    GraphicsContext gc;
    double[][] points = new double[32][32];

    public GridVisualiserStyle(GraphicsContext gc) {
        this.gc = gc;
    }

    public Color getBackgroundColor() {
        return Color.rgb(33,33,33);
    }

    public void draw() {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                points[j][i] = Maths.lerp(
                    points[j][i],
                    Main.musicController.getMagnitudeOfFrequency(i) * VisualiserSceneController.HEIGHT,
                    0.5
                );
                drawPoint(toGridX(j, i), toGridY(j, i), points[i][j] * 0.5);
            }
        }
    }

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
		gc.setStroke(Color.rgb(240, 39, 94));
		gc.strokeLine(x+height*y/640, y, x+height*y/640, y);
        /*
			Math.round(((double)barNo / (double)points.length) * VisualiserSceneController.WIDTH) * 2 + 2,
			VisualiserSceneController.HEIGHT - 3,
			Math.round(((double)barNo / (double)points.length) * VisualiserSceneController.WIDTH) * 2 + 2 + -3 * (barNo - points.length*0.25),
			VisualiserSceneController.HEIGHT - 3 + height * -2
		);
        */
	}

	public String toString() {
		return "Grid";
	}

}
