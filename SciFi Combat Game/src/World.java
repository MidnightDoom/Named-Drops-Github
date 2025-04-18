import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class World {
    private ArrayList<PlanetBody> bodies;

    public void update() {
        for (PlanetBody body : this.bodies) {
            body.update(this.bodies);
        }
    }

    public void paint(Graphics g) {
        for (PlanetBody body : this.bodies) {
            g.setColor(Color.WHITE);
            body.paint((Graphics2D) g);
        }
    }

    public World(ArrayList<PlanetBody> bodies) {
        this.bodies = bodies;
    }

    public World(PlanetBody[] bodies) {
        this();
        for (PlanetBody body : bodies) {
            this.bodies.add(body);
        }
    }

    public World() {
        this.bodies = new ArrayList<>();
    }

    public World(PlanetBody[] bodies) {
        this();
        Collections.addAll(this.bodies, bodies);
    }

    public void addBody(PlanetBody planetBody) {
        bodies.add(planetBody);
    }
}
