import javax.swing.*;

public class Main extends JFrame {

    private boolean running = false;

    public static final int WIDTH = 400;
    public static final int HEIGHT = 400;

    // Milliseconds per step
    public static int timeStep = 10;
    // Multiplies all motion and force values (slows sim even at low time steps)
    public static double timeMultiplier = 0.05;
    // Multiplies gravity calculations (think of it as this world's gravitational constant)
    public static double gravityMultiplier = 100;
    // Modifies distances (large distances can be treated as smaller)
    public static double distanceMultiplier = 0.001;

    public static World world;

    public static void main(String[] args) throws InterruptedException {

        Main theGUI = new Main();
        SwingUtilities.invokeLater(() -> theGUI.createFrame(theGUI));
        synchronized (theGUI) {
            theGUI.wait();
        }

        world = Worlds.lunaBurns;
        //world = Worlds.randomWorld();

        while (true) {
            System.out.println("Sim started");
            theGUI.startSim(timeStep);
            System.out.println("Sim stopped");
        }
    }

    public void createFrame(Object semaphore) {
        this.setTitle("Space Sim");
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MainPanel mainPanel = new MainPanel();
        mainPanel.setVisible(true);
        mainPanel.setSize(WIDTH, HEIGHT);
        mainPanel.setBounds(0, 0, WIDTH, HEIGHT);
        this.add(mainPanel);

        this.setVisible(true);
        synchronized (semaphore) {
            semaphore.notify();
        }
    }

    public void startSim(int delay) {
        running = true;

        try {
            while (running) {
                world.update();
                repaint();
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}