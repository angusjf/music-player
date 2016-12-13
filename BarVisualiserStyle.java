import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

public class BarVisualiserStyle implements VisualiserStyle {

    GraphicsContext gc;
    double[] bars = new double[128];
    int tick = 0;

    public BarVisualiserStyle(GraphicsContext gc) {
        this.gc = gc;
    }

    public Color getBackgroundColor() {
        return Color.rgb(12,12,12);
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
	    tick -= 1;
    }

	private void drawBar(int barNo, double height) {
		gc.setFill(Color.hsb(barNo * 360 / bars.length + tick, 1, 1));
		gc.fillRect(
            getXPos(barNo),
			VisualiserSceneController.HEIGHT * 0.5 - height,
			VisualiserSceneController.WIDTH/bars.length,
			height
		);
		gc.setFill(Color.hsb(barNo*360/bars.length + tick, 1, 0.5));
		gc.fillRect(
            getXPos(barNo),
			VisualiserSceneController.HEIGHT * 0.5,
			VisualiserSceneController.WIDTH/bars.length,
			height
		);
	}

    private double getXPos (int barNo) {
        double ret = 0;
		ret = (double)barNo;
        //ret *= 1.2;
        ret *= (double) VisualiserSceneController.WIDTH / bars.length;
        //ret += 3;
        ret = Math.round(ret);
        return ret;
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
		return "Rainbow Bars";
	}

}
