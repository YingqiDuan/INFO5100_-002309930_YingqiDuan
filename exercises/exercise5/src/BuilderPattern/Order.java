package BuilderPattern;

import FactoryMethodPattern.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<MenuItem> items;
    private double totalPrice;

    private Order(Builder builder) {
        this.items = builder.items;
        this.totalPrice = builder.totalPrice;
    }

    public void displayOrder() {
        System.out.println("\n--- Order Details ---");
        for (MenuItem item : items) {
            item.display();
        }
        System.out.println("Total Price: $" + String.format("%.2f",totalPrice));
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public static class Builder {
        private List<MenuItem> items = new ArrayList<>();
        private double totalPrice = 0.0;

        public Builder addItem(MenuItem item) {
            items.add(item);
            totalPrice += item.getPrice();
            return  this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
