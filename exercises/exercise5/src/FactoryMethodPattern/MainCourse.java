package FactoryMethodPattern;

public class MainCourse implements MenuItem{
    private String name;
    private double price;

    public MainCourse(String name, double price) {
        this.name=name;
        this.price=price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void display() {
        System.out.println("Main Course: " + name + " -$" + price);
    }
}
