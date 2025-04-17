import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    private static World world = new World();

    public static void main(String[] args) throws InterruptedException {
		Main theGUI = new Main();
		SwingUtilities.invokeLater(() -> theGUI.createFrame(theGUI));
		synchronized (theGUI ) {
			theGUI.wait();
		}

        world = Worlds.lunaBurns;
		
		while (true) {
			System.out.println("Start Sim");
			theGUI.startSim();
			System.out.println("Sim stopped");
		}
	}

    public void createFrame(Object semaphore) {
        
		this.setTitle("My animation");
		this.setSize(400,400);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    
		MainPanel panel = new MainPanel();
		
		panel.setBounds(0, 0, 400, 400);
		add(panel);
		panel.setVisible(true);
		this.setVisible(true);

        System.out.println("All done creating our frame");
		synchronized (semaphore) {
			semaphore.notify();
		}
	}

    public void startSim() {
		boolean running = true;
		try {			
			while (running) {
                world.update();
				repaint();
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
