package ObserverPattern;

public class KitchenStaff implements Observer {
    private String name;

    public KitchenStaff(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println("Kitchen Staff (" +name+") received notification: "+message);
    }
}
