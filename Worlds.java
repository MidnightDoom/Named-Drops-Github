import java.util.ArrayList;

public class Worlds {

    public static final World lunaBurns = new World(PlanetBody[]{
        new PlanetBody(20000, 50, new Vector2D(0,0), new Vector2D(200,200), 80, 80, 0, 0),
        new PlanetBody(0, 5, new Vector2D(50,0), new Vector2D(200,300), 40, 35, 0, .1),
        new PlanetBody(0, 4, new Vector2D(42, 10), new Vector2D(180, 280), 35, 32, 0, .2)
    });
}