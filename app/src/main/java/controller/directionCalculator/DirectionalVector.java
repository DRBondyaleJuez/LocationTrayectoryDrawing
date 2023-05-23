package controller.directionCalculator;

public class DirectionalVector {

    double x;
    double y;

    double module;

    public DirectionalVector(double[][] trajectory) {

        double currentOriginX = trajectory[0][1];
        double currentOriginY = trajectory[0][0];
        double currentFinalX = trajectory[trajectory.length-1][1];
        double currentFinalY = trajectory[trajectory.length-1][0];
        x = currentFinalX - currentOriginX;
        y = currentFinalY - currentOriginY;

        module = Math.sqrt(x*x + y*y);

    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getModule(){
        return module;
    }

    public double calculateAngle(DirectionalVector secondDirectionalVector){
        //https://www.cuemath.com/geometry/angle-between-vectors/
        double result = Math.acos(this.scalarProduct(secondDirectionalVector)/(module * secondDirectionalVector.getModule()));
        return result;
    }

    public double calculateCrossProductZComponent(DirectionalVector secondDirectionalVector){
        double zComponentCrossProduct = x* secondDirectionalVector.getY() - y* secondDirectionalVector.getX();
        return zComponentCrossProduct;
    }

    private double scalarProduct(DirectionalVector secondDirectionalVector){
        double xScalar = x * secondDirectionalVector.getX();
        double yScalar = y * secondDirectionalVector.getY();
        return xScalar + yScalar;
    }
}
