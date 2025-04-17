import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class PlanetBody {
    private int gravity;
    private int inertia;
    private Vector2D velocity;
    private Vector2D position;
    private int minorAxis;
    private int majorAxis;
    private double angle;
    private double rotationalVelocity;

    public PlanetBody(int gravity, int inertia, Vector2D velocity, Vector2D position, int majorAxis, int minorAxis, double angle, double rotationalVelocity) {
        this.gravity = gravity;
        this.inertia = inertia;
        this.velocity = velocity;
        this.position = position;
        this.majorAxis = majorAxis;
        this.minorAxis = minorAxis;
        this.angle = angle;
        this.rotationalVelocity = rotationalVelocity;
    }

    public PlanetBody(int gravity, int inertia, Vector2D velocity, Vector2D position, int radius) {
        this.gravity = gravity;
        this.inertia = inertia;
        this.position = position;
        this.velocity = velocity;
        this.majorAxis = radius;
        this.minorAxis = radius;
        this.angle = 0;
        this.rotationalVelocity = 0;
    }

    public Vector2D getPos() {
        return position;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public int getGravity() {
        return gravity;
    }

    public void update(ArrayList<PlanetBody> bodies) {
        for (PlanetBody otherBody : bodies) {
            if (otherBody == this) continue;
            applyGravity(otherBody);
        }
        position = position.add(velocity);
        angle += rotationalVelocity;
    }

    public void applyGravity(PlanetBody otherBody) {

        Vector2D delta = otherBody.getPos().subtract(position);
        double distanceSquared = delta.magnitudeSquared();

        if (distanceSquared == 0) return;

        double force = otherBody.getGravity() / distanceSquared;
        double accelerationMagnitude = force / inertia;
        Vector2D acceleration = delta.normalized().scale(accelerationMagnitude);
        velocity = velocity.add(acceleration);
    }

    public void paint(Graphics2D g) {
        AffineTransform old = g.getTransform();
        g.rotate(angle, position.getX(), position.getY());
        g.drawOval(
                (int) Math.round(position.getX() - (double) majorAxis / 2),
                (int) Math.round(position.getY() - (double) minorAxis / 2),
                majorAxis,
                minorAxis
        );
        g.setTransform(old);

        g.setColor(Color.GRAY);
        g.drawLine((int) position.getX(), (int) position.getY(), (int) (position.add(velocity).getX()), (int) (position.add(velocity).getY()));
    }
}
