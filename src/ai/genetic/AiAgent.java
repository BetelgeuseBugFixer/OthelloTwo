package ai.genetic;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AiAgent implements Comparable<AiAgent> {
    int[] weights;
    AtomicInteger points;

    public AiAgent(int weightSize) {
        this.points=new AtomicInteger();

        Random rnd = new Random();
        this.weights = new int[weightSize];
        for (int i = 0; i < weightSize; i++) {
            int newWeight = rnd.nextInt(200);
            weights[i] = newWeight - 100;
        }
    }

    public void resetPoints(){
        this.points.set(0);
    }

    public AiAgent(int[] weights){
        this.weights=weights;
    }

    public void addWin(){
        this.points.getAndAdd(3);
    }
    public void addDraw(){
        this.points.getAndAdd(1);
    }

    @Override
    public int compareTo(AiAgent o) {
        return Integer.compare(this.points.get(),o.points.get());
    }

    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        for (int weight : weights) {
            sb.append(weight);
            sb.append("\t");
        }
        return sb.substring(0,sb.length()-2);
    }
}
