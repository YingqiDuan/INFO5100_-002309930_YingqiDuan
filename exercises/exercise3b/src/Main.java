import java.io.*;

// Shape
abstract class Shape implements Serializable {
    private static final long serialVersionUID = 1L;
    static String color = "Red";

    abstract double calculateArea();
    abstract double calculatePerimeter();

    //get the name of the shape
    abstract String getName();

    // Method to display information
    public void displayInfo() {
        System.out.println("Shape Name: " + getName());
        System.out.println("Shape Color: " + color);
        System.out.println("Area: " + calculateArea());
        System.out.println("Perimeter: " + calculatePerimeter());
    }
}

// Triangle
class Triangle extends Shape {
    private static final long serialVersionUID = 1L;
    static String name = "Triangle";

    // the sides of the triangle
    private double a, b, c;

    // Constructor
    public Triangle(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    // Overriding abstract methods
    @Override
    double calculateArea() {
        double s = (a + b + c) / 2; // Semi-perimeter
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Override
    double calculatePerimeter() {
        return a + b + c;
    }

    // Overriding getName method
    @Override
    String getName() {
        return name;
    }
}

// Rectangle
class Rectangle extends Shape {
    private static final long serialVersionUID = 1L;
    static String name = "Rectangle";

    protected double length, width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double calculateArea() {
        return length * width;
    }

    @Override
    double calculatePerimeter() {
        return 2 * (length + width);
    }

    @Override
    String getName() {
        return name;
    }
}

// Square
class Square extends Rectangle {
    private static final long serialVersionUID = 1L;
    static String name = "Square";

    public Square(double side) {
        super(side, side); // A square has equal length and width
    }

    @Override
    String getName() {
        return name;
    }
}

// Circle
class Circle extends Shape {
    private static final long serialVersionUID = 1L;
    static String name = "Circle";

    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double calculateArea() {
        return Math.PI * radius * radius;
    }

    @Override
    double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    String getName() {
        return name;
    }
}

// Main class to demonstrate the concepts
public class Main {
    public static void main(String[] args) {
        // Creating shape instances using polymorphism
        Shape shape1 = new Triangle(3, 4, 5);
        Shape shape2 = new Rectangle(4, 6);
        Shape shape3 = new Circle(5);
        Shape shape4 = new Square(4);

        // Displaying information about each shape
        System.out.println("=== Triangle Info ===");
        shape1.displayInfo();
        System.out.println();

        System.out.println("=== Rectangle Info ===");
        shape2.displayInfo();
        System.out.println();

        System.out.println("=== Circle Info ===");
        shape3.displayInfo();
        System.out.println();

        System.out.println("=== Square Info ===");
        shape4.displayInfo();
        System.out.println();

        // Demonstrating static field usage
        System.out.println("=== Static Field Demonstration ===");
        System.out.println("Default Shape Color: " + Shape.color);

        // Changing the static color field
        Shape.color = "Blue";
        System.out.println("Updated Shape Color: " + Shape.color);

        // All shapes will reflect the updated color
        System.out.println("\nAfter updating Shape.color to 'Blue':");
        shape1.displayInfo();
        System.out.println();
        shape2.displayInfo();
        System.out.println();
        shape3.displayInfo();
        System.out.println();
        shape4.displayInfo();
        System.out.println();

        try {
            FileOutputStream fileOut = new FileOutputStream("shape.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(shape1);
            out.writeObject(shape2);
            out.writeObject(shape3);
            out.writeObject(shape4);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in shapes.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }

        shape1 = null;
        shape2 = null;
        shape3 = null;
        shape4 = null;

        // Static field remains as per the class
        Shape.color = "black";
        System.out.println("\nShape.color changed to 'black' before deserialization.");

        try {
            FileInputStream fileIn = new FileInputStream("shape.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Shape deserializedShape1 = (Shape) in.readObject();
            Shape deserializedShape2 = (Shape) in.readObject();
            Shape deserializedShape3 = (Shape) in.readObject();
            Shape deserializedShape4 = (Shape) in.readObject();
            in.close();
            fileIn.close();

            System.out.println("\nDeserialized Shapes:");
            System.out.println("=== Triangle Info ===");
            deserializedShape1.displayInfo();
            System.out.println();
            System.out.println("=== Rectangle Info ===");
            deserializedShape2.displayInfo();
            System.out.println();
            System.out.println("=== Circle Info ===");
            deserializedShape3.displayInfo();
            System.out.println();
            System.out.println("=== Square Info ===");
            deserializedShape4.displayInfo();
            System.out.println();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Shape class not found");
            c.printStackTrace();
            return;
        }


    }
}
