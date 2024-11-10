package FactoryMethodPattern;

public class MenuItemFactory {
    public static MenuItem createMenuItem(String type, String name, double price) {
        switch (type.toLowerCase()) {
            case "appetizer":
                return new Appetizer(name, price);
            case "maincourse":
                return new MainCourse(name, price);
            case "dessert":
                return new Dessert(name, price);
            default:
                throw new IllegalArgumentException("Unknown menu item type: " + type);
        }
    }
}
