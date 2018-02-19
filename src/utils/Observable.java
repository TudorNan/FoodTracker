package utils;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    List<Observer> observers = new ArrayList<>();
    public void addObserver(Observer obs){
        observers.add(obs);
    }
    public void removeObserver(Observer obs){
        observers.remove(obs);
    }
    public void notifyObservers(){
        observers.forEach(e->e.update());
    }
}
