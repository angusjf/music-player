import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

public class GridVisualiserStyle implements VisualiserStyle {

    GraphicsContext gc;
    double[] bars = new double[128];

    public GridVisualiserStyle(GraphicsContext gc) {
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
		gc.setLineWidth(3);
		gc.setStroke(Color.rgb(240, 39, 94));
		gc.strokeLine(
			Math.round(((double)barNo / (double)bars.length) * VisualiserSceneController.WIDTH) * 2 + 2,
			VisualiserSceneController.HEIGHT - 3,
			Math.round(((double)barNo / (double)bars.length) * VisualiserSceneController.WIDTH) * 2 + 2 + -3 * (barNo - bars.length*0.25),
			VisualiserSceneController.HEIGHT - 3 + height * -2
		);

	}

}
