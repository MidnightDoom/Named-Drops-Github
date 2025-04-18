import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class MainPanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {

    private double zoom = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    private Point lastMouseDrag = null;

    public MainPanel() {
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    // Paints background and objects
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        AffineTransform original = g2.getTransform();

        // Apply zoom and pan
        g2.translate(getWidth() / 2.0 + offsetX, getHeight() / 2.0 + offsetY);
        g2.scale(zoom, zoom);

        Main.world.paint(g);

        g2.setTransform(original);
    }

    // Zoom with mouse wheel
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double delta = 0.1f * e.getPreciseWheelRotation();
        double scaleFactor = 1 - delta;

        // Zoom around mouse position
        int mouseX = e.getX();
        int mouseY = e.getY();

        double dx = (mouseX - getWidth() / 2.0 - offsetX) / zoom;
        double dy = (mouseY - getHeight() / 2.0 - offsetY) / zoom;

        zoom *= scaleFactor;

        offsetX -= dx * (scaleFactor - 1) * zoom;
        offsetY -= dy * (scaleFactor - 1) * zoom;

        repaint();
    }

    // Panning with mouse drag
    @Override
    public void mousePressed(MouseEvent e) {
        lastMouseDrag = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastMouseDrag != null) {
            int dx = e.getX() - lastMouseDrag.x;
            int dy = e.getY() - lastMouseDrag.y;

            offsetX += dx;
            offsetY += dy;

            lastMouseDrag = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastMouseDrag = null;
    }

    // Unused methods
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
