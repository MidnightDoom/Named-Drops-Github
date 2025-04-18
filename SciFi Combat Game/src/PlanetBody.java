import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class PlanetBody {
    // Force this body exerts on other bodies
    private double gravity;
    // This body's resistance to force
    private double inertia;
    // This body's velocity vector
    private Vector2D velocity;
    // This body's position
    private Vector2D position;
    // One diameter of this body (intended to be smallest)
    private int minorAxis;
    // One diameter of this body (intended to be largest)
    // Bodies within the major axis of this body won't be affected by this body's gravity (prevents weird behavior)
    private int majorAxis;
    // This body's current rotation in radians
    private double angle;
    // This body's rotational speed in radians / step
    private double rotationalVelocity;

    public PlanetBody(double gravity, double inertia, Vector2D velocity, Vector2D position, int majorAxis, int minorAxis, double angle, double rotationalVelocity) {
        this.gravity = gravity;
        this.inertia = inertia;
        this.velocity = velocity;
        this.position = position;
        this.majorAxis = majorAxis;
        this.minorAxis = minorAxis;
        this.angle = angle;
        this.rotationalVelocity = rotationalVelocity;
    }

    public PlanetBody(double gravity, double inertia, Vector2D velocity, Vector2D position, int radius) {
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

    public double getGravity() {
        return gravity * Main.gravityMultiplier;
    }

    // Used to prevent force being applied when bodies intersect (prevents odd behavior)
    public boolean withinMajorAxis(Vector2D d) {
        return d.magnitude() <= majorAxis * Main.distanceMultiplier;
    }

    // Applies acceleration to this body from all other bodies, modifying its velocity
    // Then moves according to velocity
    public void update(ArrayList<PlanetBody> bodies) {
        for (PlanetBody otherBody : bodies) {
            if (otherBody == this) continue;
            applyGravity(otherBody);
        }
        position = position.add(velocity.scale(Main.timeMultiplier).scale(Main.distanceMultiplier));
        angle += rotationalVelocity * Main.timeMultiplier;
    }

    public void applyGravity(PlanetBody otherBody) {

        // Calculate square of distance
        Vector2D delta = otherBody.getPos().subtract(position).scale(Main.distanceMultiplier);

        if (otherBody.withinMajorAxis(delta)) return;

        double distanceSquared = delta.magnitudeSquared();

        // Prevent / 0 error
        if (distanceSquared == 0) return;

        // Calculate force and acceleration using other bodies gravity, this bodies inertia, and the distance squared
        double force = otherBody.getGravity() / distanceSquared;
        double accelerationMagnitude = force / inertia;
        // Applies acceleration to velocity
        Vector2D acceleration = delta.normalized().scale(accelerationMagnitude).scale(Main.timeMultiplier);
        velocity = velocity.add(acceleration);
    }

    public void paint(Graphics2D g) {
        // Handles rotation
        AffineTransform old = g.getTransform();
        g.rotate(angle, position.getX(), position.getY());
        // Body's shape
        g.drawOval(
                (int) Math.round(position.getX() - (double) majorAxis / 2),
                (int) Math.round(position.getY() - (double) minorAxis / 2),
                majorAxis,
                minorAxis
        );
        g.setTransform(old);

        // Velocity vector
        g.setColor(Color.GRAY);
        g.drawLine(
                (int) position.getX(),
                (int) position.getY(),
                (int) (position.add(velocity.scale(Main.distanceMultiplier)).getX()),
                (int) (position.add(velocity.scale(Main.distanceMultiplier)).getY())
        );
    }
}
