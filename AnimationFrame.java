import java.awt.*;

public interface AnimationFrame {

    /**
     * Asks the animation frame to render itself at the given time.
     * @param g The {@code Graphics2D} object to render the frame in.
     * @param width Width of the frame in pixels.
     * @param height Height of the frame in pixels.
     * @param t The moment in time that this frame is a snapshot of.
     */
    void render(Graphics2D g, int width, int height, double t);
    
    /**
     * In circular motion around the center {@code (cx, cy)} with radius
     * {@code r}, when one rotation takes one unit of time starting from
     * zero, compute the x-coordinate of motion at time {@code t}.
     * @param t The current time.
     * @return The x-coordinate of circular motion at time {@code t}.
     */
    default double circleX(double cx, double r, double t) {
        return cx + r * Math.cos(2 * Math.PI * t);
    }
    
    /**
     * In circular motion around the center {@code (cx, cy)} with radius
     * {@code r}, when one rotation takes one unit of time starting from
     * zero, compute the y-coordinate of motion at time {@code t}.
     * @param t The current time.
     * @return The y-coordinate of circular motion at time {@code t}.
     */
    default double circleY(double cy, double r, double t) {
        return cy + r * Math.sin(2 * Math.PI * t);
    }
    
    /** 
     * Computes cosine interpolation for t. In any motion or other
     * formula where t goes from 0 to 1, try using smooth(t) instead.
     * @param t The time value to smoothe.
     * @return The smoothed version of t.
     */
    default double smooth(double t) {
        return smooth(t, 1);
    }
    
    /** 
     * Computes cosine interpolation for t. In any motion or other
     * formula where t goes from 0 to 1, try using smooth(t) instead.
     * @param t The time value to smoothe.
     * @param w The weight of smoothing.
     * @return The smoothed version of t.
     */
    default double smooth(double t, double w) {
        double tf = Math.floor(t);
        double tt = t - Math.floor(t);
        double ts = (1 - Math.cos(tt * Math.PI)) / 2;
        return tf + w * ts + (1 - w) * tt;
    }
}