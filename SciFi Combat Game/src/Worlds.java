import java.util.ArrayList;

public class Worlds {
    public static final World lunaBurns = new World(
       new PlanetBody[]{
           new PlanetBody(20, 50, new Vector2D(0,0), new Vector2D(0,0), 200, 200, 0,0),
           new PlanetBody(0, 3, new Vector2D(-25000,4800), new Vector2D(0,500), 80, 75, 0,.1),
           new PlanetBody(0, 3, new Vector2D(-25000,4800), new Vector2D(120,485), 60, 45, 0,1),
           new PlanetBody(0, 3, new Vector2D(-25000,4800), new Vector2D(200,458), 55, 50, 0,5),
           new PlanetBody(0, 3, new Vector2D(-25000,4800), new Vector2D(300,400), 45, 40, 0,10),
           new PlanetBody(0, 3, new Vector2D(-25000,4800), new Vector2D(80,450), 20, 12, 0,1),
           new PlanetBody(0, 3, new Vector2D(-25000,4800), new Vector2D(60,540), 21, 13, 1,1),
           new PlanetBody(0, 3, new Vector2D(-25000,4800), new Vector2D(90,530), 21, 12, 5,1),
           new PlanetBody(0, 3, new Vector2D(-25000,4800), new Vector2D(170,500), 18, 12, 1,1),
           new PlanetBody(0, 3, new Vector2D(-25000,4800), new Vector2D(160,445), 17, 11, 2,1)
       }
    );

    public static World randomWorld() {
        ArrayList<PlanetBody> bodies = new ArrayList<>();
        for (int i = 0; i < Math.random() * 20 + 5; i++) {
            int d = (int) (Math.random() * 200);
            bodies.add(
                new PlanetBody(
                    Math.random() * 20,
                    Math.random() * 30,
                    new Vector2D(Math.random() * 25000 - 50000, Math.random() * 25000 - 50000),
                    new Vector2D(Math.random() * 600 - 1200, Math.random() * 600 - 1200),
                    d,
                    d + (int) (Math.random() * 80),
                    Math.random(),
                    Math.random()
                )
            );
        }
        return new World(bodies);
    }
}
