package controller.locationController;

/**
 * Provides an interface representing one of the components of an observer-observable design pattern. This component
 * represents an object that can be observed this facilitates triggers and interactions with objects which implement
 * the observer equivalent interface.
 */
public interface LocationControllerObservable {

    /**
     * Method to insert observer equivalent to a container to facilitate interactions between this observable and its
     * corresponding observer
     * @param observer LocationControllerObserver object or object implementing LocationControllerObserver
     *                 the is "observing" this observable implementation.
     */
    void addObservers(LocationControllerObserver observer);
}
