import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

public interface VisualiserStyle {

    public Color getBackgroundColor();
    public void draw();
    public void leftKey();
    public void rightKey();
    public void upKey();
    public void downKey();

}
