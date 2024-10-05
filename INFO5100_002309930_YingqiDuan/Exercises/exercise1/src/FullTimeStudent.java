// FullTimeStudent.java
import java.util.ArrayList;

public class FullTimeStudent extends Student {
    private final double examScore1;
    private final double examScore2;

    public FullTimeStudent(String name, ArrayList<Double> quizScores, double examScore1, double examScore2) {
        super(name, quizScores);
        this.examScore1 = examScore1;
        this.examScore2 = examScore2;
    }

    public double getExamScore1() {
        return examScore1;
    }

    public double getExamScore2() {
        return examScore2;
    }

    public double[] getExamScores() {
        return new double[]{examScore1, examScore2};
    }
}
