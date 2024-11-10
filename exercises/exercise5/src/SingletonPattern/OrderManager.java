package SingletonPattern;

import BuilderPattern.Order;
import ObserverPattern.Observer;
import ObserverPattern.Subject;
import java.util.ArrayList;
import java.util.List;

public class OrderManager implements Subject {
    private static volatile OrderManager instance = null;
    private List<Observer> observers = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    private OrderManager() {
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            synchronized (OrderManager.class) {
                if (instance == null) {
                    instance = new OrderManager();
                }
            }
        }
        return instance;
    }

    public void placeOrder(Order order) {
        orders.add(order);
        notifyObservers();
    }

    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update("New order placed");
        }
    }
}
