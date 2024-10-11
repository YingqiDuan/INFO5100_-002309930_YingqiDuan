// Session.java
import java.util.ArrayList;
import java.util.Collections;

public class Session {
    private final ArrayList<Student> students;

    public Session() {
        students = new ArrayList<Student>();
    }

    public void addStudent(Student s) {
        students.add(s);
    }

    // Calculate average quiz scores
    public void calculateAverageQuizScoresPerStudent() {
        System.out.println("Average quiz scores per student:");
        for (Student s : students) {
            double avg = s.calculateAverageQuizScore();
            System.out.println(s.getName() + ": " + avg);
        }
    }

    // Print the list of quiz scores in ascending order
    public void printQuizScoresInAscendingOrder() {
        ArrayList<Double> allScores = new ArrayList<Double>();
        for (Student s : students) {
            allScores.addAll(s.getQuizScores());
        }
        Collections.sort(allScores);
        System.out.println("Quiz scores in ascending order:");
        for (Double score : allScores) {
            System.out.println(score);
        }
    }

    // Print names of part-time students
    public void printNamesOfPartTimeStudents() {
        System.out.println("Names of Part-Time Students:");
        for (Student s : students) {
            if (s instanceof PartTimeStudent) {
                System.out.println(s.getName());
            }
        }
    }

    // Print exam scores of full-time students
    public void printExamScoresOfFullTimeStudents() {
        System.out.println("Exam scores of Full-Time Students:");
        for (Student s : students) {
            if (s instanceof FullTimeStudent) {
                FullTimeStudent ft = (FullTimeStudent) s;
                System.out.println("Student: " + ft.getName() + ", Exam Scores: " + ft.getExamScore1() + ", " + ft.getExamScore2());
            }
        }
    }
}
