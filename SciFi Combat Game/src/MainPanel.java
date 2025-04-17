import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    
    public MainPanel() {
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        Main.world.paint(g);
    }
}
