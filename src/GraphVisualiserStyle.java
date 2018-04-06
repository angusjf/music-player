import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

public class GraphVisualiserStyle implements VisualiserStyle {

    GraphicsContext gc;
    double[] bars = new double[128];
    double lastHeight = 0;

    public GraphVisualiserStyle(GraphicsContext gc) {
        this.gc = gc;
    }

    public Color getBackgroundColor() {
        return Color.rgb(33,33,33);
    }

    public void draw() {
        for (int i = 0; i < bars.length; i++) {
            bars[i] = Maths.lerp(
                bars[i],
                Main.musicController.getMagnitudeOfFrequency(i) * VisualiserSceneController.HEIGHT,
                0.5
            );
            drawBar(i, bars[i] * 0.5);
        }
    }

	private void drawBar(int barNo, double height) {
		gc.setStroke(Color.rgb(81, 191, 133));
		gc.strokeOval(
			Math.round(((double)barNo/ (double)bars.length) * VisualiserSceneController.WIDTH) * 2 + 6*VisualiserSceneController.SCALE,
			VisualiserSceneController.HEIGHT - height - 3,
			4,
			4
		);
		gc.setLineWidth(2);
		gc.strokeLine(
			Math.round(((double)(barNo-1) / (double)bars.length) * VisualiserSceneController.WIDTH) * 2 + 6*VisualiserSceneController.SCALE,
			VisualiserSceneController.HEIGHT - lastHeight - 5,
			Math.round(((double)(barNo) / (double)bars.length) * VisualiserSceneController.WIDTH) * 2 + 6*VisualiserSceneController.SCALE,
			VisualiserSceneController.HEIGHT - height - 5
		);
		lastHeight = height;
	}

    /*
	private void drawGraphPart(int barNo, double height) {
		gc.setLineWidth(3);
		gc.setStroke(Color.rgb(81, 191, 133));
		useHeight = (graphTick % 2 == 0) ? lastHeight1 : lastHeight2;
		gc.strokeLine( Math.round(((double)barNo / (double)noOfBars) * WIDTH) * 2 + 2, HEIGHT * 0.5 + useHeight * -1, Math.round(((double)barNo/ (double)noOfBars) * WIDTH) * 2 + 6*SCALE, HEIGHT * 0.5 + height * -1);
		gc.strokeLine(
			Math.round(((double)barNo/ (double)noOfBars) * WIDTH) * 2 + 6*SCALE,
			HEIGHT - height - 3,
			Math.round(((double)barNo/ (double)noOfBars) * WIDTH) * 2 + 6*SCALE,
			HEIGHT - height - 3
		);
		graphTick++;
		if (graphTick % 2 == 0) lastHeight1 = height;
		else lastHeight2 = height;
	}
    */

    public void leftKey() {
    }

    public void rightKey() {
    }

    public void upKey() {
    }

    public void downKey() {
    }

	public String toString() {
		return "Line Graph";
	}

}
