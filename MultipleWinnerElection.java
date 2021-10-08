import java.util.*;

public class MultipleWinnerElection {
    
    public static int[] DHondt(int[] votes, int seats) {
        return DHondt(votes, seats, 0);
    }
    
    public static int[] webster(int[] votes, int seats) {
        return DHondt(votes, seats, 1);
    }
    
    public static int[] imperiali(int[] votes, int seats) {
        return DHondt(votes, seats, 2);
    }
    
    private static int[] DHondt(int[] votes, int seats, int mode) {
        int C = votes.length;
        int[] result = new int[C];
        Fraction[] priorities = new Fraction[C];
        class MyComp implements Comparator<Integer> {
            public int compare(Integer c1, Integer c2) {
                if(c1.equals(c2)) { return 0; }
                int r = priorities[c2].compareTo(priorities[c1]);
                if(r == 0) { return c1 < c2 ? +1: -1; }
                else { return r; }
            }
        }
        PriorityQueue<Integer> queue = new PriorityQueue<>(new MyComp());
        for(int c = 0; c < C; c++) {
            priorities[c] = new Fraction(votes[c], 1);
            queue.offer(c);
        }
        while(seats-- > 0) {
            assert queue.size() > 0;
            int next = queue.poll();
            result[next]++;
            int r = result[next];
            if(mode == 0) { // D'Hondt
                priorities[next] = new Fraction(votes[next], r + 1);
            }
            else if(mode == 1) { // Webster
                priorities[next] = new Fraction(votes[next], 2 * r + 1);
            }
            else { // Imperiali
                priorities[next] = new Fraction(votes[next] * 2, r + 2);
            }
            queue.offer(next);
        }
        return result;
    }
}