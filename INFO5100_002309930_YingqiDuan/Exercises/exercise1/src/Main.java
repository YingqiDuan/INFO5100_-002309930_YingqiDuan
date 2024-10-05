// Main.java
import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Session session = new Session();
        Random rand = new Random();

        // 20 students
        for (int i = 1; i <= 20; i++) {
            String name = "Student" + i;
            ArrayList<Double> quizScores = new ArrayList<Double>();
            // Generate 15 dummy quiz scores
            for (int j = 0; j < 15; j++) {
                quizScores.add(rand.nextDouble() * 100);
            }
            // Randomly decide if student is part-time or full-time
            if (rand.nextBoolean()) {
                // PartTime student
                PartTimeStudent ptStudent = new PartTimeStudent(name, quizScores);
                session.addStudent(ptStudent);
            } else {
                // FullTime student
                double examScore1 = rand.nextDouble() * 100;
                double examScore2 = rand.nextDouble() * 100;
                FullTimeStudent ftStudent = new FullTimeStudent(name, quizScores, examScore1, examScore2);
                session.addStudent(ftStudent);
            }
        }

        // Call all public methods
        session.calculateAverageQuizScoresPerStudent();
        session.printQuizScoresInAscendingOrder();
        session.printNamesOfPartTimeStudents();
        session.printExamScoresOfFullTimeStudents();
    }
}
