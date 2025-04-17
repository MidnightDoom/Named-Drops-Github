import java.awt.*;
import java.util.ArrayList;

public class World {
    private ArrayList<PlanetBody> bodies;

    public void update() {
        for (PlanetBody body : this.bodies) {
            body.update(this.bodies);
        }
    }

    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        for (PlanetBody body : this.bodies) {
            body.paint((Graphics2D) g);
        }
    }

    public World(ArrayList<PlanetBody> bodies) {
        this.bodies = bodies;
    }

    public World() {
        this.bodies = new ArrayList<>();
    }

    public void addBody(PlanetBody planetBody) {
        bodies.add(planetBody);
    }
}
