package controller.directionCalculator;

public class Vector {

    double x;
    double y;

    double module;

    public Vector(double[][] trajectory) {

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

    public double scalarProduct(Vector secondVector){
        double xScalar = x * secondVector.getX();
        double yScalar = y * secondVector.getY();
        return xScalar + yScalar;
    }

    public double calculateAngle(Vector secondVector){
        double result = Math.acos(this.scalarProduct(secondVector)/(module * secondVector.getModule()));
        return result;
    }

}
