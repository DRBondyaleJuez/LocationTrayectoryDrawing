package controller.directionCalculator;

/**
 * Provides an encapsulation of coordinate parameters in the form of a vector. this means that provided
 * two coordinates it converts them into a vector from the origin to facilitate direction calculation
 * when compared with other vectors.
 */
public class DirectionalVector {

    double x;
    double y;

    double module;

    /**
     * This is the constructor.
     * It provides a value for x and y attributes by moving the provided trajectory values to the origin
     * by subtracting the first coordinate and calculates the module of the vector following pythagoras
     * mod = sqrt(x^2 + y^2)
     * @param trajectory a 2 dimensional matrix of doubles
     */
    public DirectionalVector(double[][] trajectory) {

        double currentOriginX = trajectory[0][1];
        double currentOriginY = trajectory[0][0];
        double currentFinalX = trajectory[trajectory.length-1][1];
        double currentFinalY = trajectory[trajectory.length-1][0];
        x = currentFinalX - currentOriginX;
        y = currentFinalY - currentOriginY;

        module = Math.sqrt(x*x + y*y);

    }

    //Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getModule(){
        return module;
    }

    /**
     * Calculate the angle between this and another vectors which have the same origin 0,0
     * @param secondDirectionalVector DirectionalVector other than this that will participate in the calculation
     * @return double corresponding to the resulting angle in radians
     */
    public double calculateAngle(DirectionalVector secondDirectionalVector){
        //https://www.cuemath.com/geometry/angle-between-vectors/
        double result = Math.acos(this.scalarProduct(secondDirectionalVector)/(module * secondDirectionalVector.getModule()));
        return result;
    }

    /**
     * Calculate the ZComponent based on a cross product calculation
     * <p>
     *     Cross product formula: z = (x1*y2) - (y1*x2)
     * </p>
     * @param secondDirectionalVector
     * @return double corresponding to the result of the cross product
     */
    public double calculateCrossProductZComponent(DirectionalVector secondDirectionalVector){
        double zComponentCrossProduct = x * secondDirectionalVector.getY() - y * secondDirectionalVector.getX();
        return zComponentCrossProduct;
    }

    /**
     * Calculate the scalar product between this and another vector
     * <p>
     *     Scalar Formula: s = (x1*x2) + (y1*y2)
     * </p>
     * @param secondDirectionalVector DirectionalVector object to multiply with this
     * @return double corresponding to the result of the scalar product
     */
    private double scalarProduct(DirectionalVector secondDirectionalVector){
        double xScalar = x * secondDirectionalVector.getX();
        double yScalar = y * secondDirectionalVector.getY();
        return xScalar + yScalar;
    }
}
