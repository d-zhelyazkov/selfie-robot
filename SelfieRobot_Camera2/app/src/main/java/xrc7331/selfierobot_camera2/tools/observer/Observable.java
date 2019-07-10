package xrc7331.selfierobot_camera2.tools.observer;

/**
 * Created by XRC_7331 on 4/12/2016.
 */
public interface Observable<T extends Observable>{
    void addObserver(Observer<T> observer);
    void notifyObservers();
}
