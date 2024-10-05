import java.util.ArrayList;

public class Student {
    protected String name;
    protected ArrayList<Double> quizScores;

    public Student(String name, ArrayList<Double> quizScores) {
        this.name=name;
        this.quizScores=quizScores;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Double> getQuizScores() {
        return quizScores;
    }

    public double calculateAverageQuizScore() {
        double sum = 0.0;
        for (Double score : quizScores) {
            sum += score;
        }
        return sum / quizScores.size();
    }
}
