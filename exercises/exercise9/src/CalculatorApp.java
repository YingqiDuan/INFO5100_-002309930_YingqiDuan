import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CalculatorApp extends Application {
    private final TextField num1 = new TextField();
    private final TextField num2 = new TextField();
    private final Label result = new Label("Result:");

    @Override
    public void start(Stage stage) {
        stage.setTitle("Calculator");

        // create buttons
        Button addBtn = createBtn("+", '+');
        Button subBtn = createBtn("-", '-');
        Button mulBtn = createBtn("*", '*');
        Button divBtn = createBtn("/", '/');

        // layout
        GridPane layout = new GridPane();
        layout.setVgap(10);
        layout.setHgap(10);
        layout.setAlignment(Pos.CENTER);

        // add elements
        layout.add(new Label("Num 1:"), 0, 0);
        layout.add(num1, 1, 0);
        layout.add(new Label("Num 2:"), 0, 1);
        layout.add(num2, 1, 1);
        layout.add(addBtn, 0, 2);
        layout.add(subBtn, 1, 2);
        layout.add(mulBtn, 0, 3);
        layout.add(divBtn, 1, 3);
        layout.add(result, 0, 4, 2, 1);

        stage.setScene(new Scene(layout, 300, 200));
        stage.show();
    }

    private Button createBtn(String text, char op) {
        Button btn = new Button(text);
        btn.setOnAction(e -> calculate(op));
        return btn;
    }

    private void calculate(char op) {
        try {
            double n1 = Double.parseDouble(num1.getText());
            double n2 = Double.parseDouble(num2.getText());
            double res = switch (op) {
                case '+' -> n1 + n2;
                case '-' -> n1 - n2;
                case '*' -> n1 * n2;
                case '/' -> n2 != 0 ? n1 / n2 : Double.NaN;
                default -> 0;
            };
            result.setText("Result: " + (Double.isNaN(res) ? "Error: Division by zero" : res));
        } catch (NumberFormatException e) {
            result.setText("Error: Invalid input");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

