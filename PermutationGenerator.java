// Abstract superclass for students to extend the concrete subclasses
// LexicographicComparator and ZaksComparator from.

public abstract class PermutationGenerator {

    private int[] perm;
    private int pos = 0;
    public PermutationGenerator(int[] perm) {
        this.perm = perm;
        for(int i = 0; i < perm.length; i++) { perm[i] = i; }
    }

    public boolean next() {
        return next(pos++, perm);
    }

    protected abstract boolean next(int pos, int[] perm);
}