import java.lang.reflect.Array;
import java.util.ArrayList;

public class SortedReductions {
    private ArrayList<Reduction> reductionsList = new ArrayList<>();

    public Reduction getMax() {
        return reductionsList.get(reductionsList.size()-1);
    }

    public Reduction get(int i) {
        return reductionsList.get(i);
    }

    public void purgeOutliers(double cutoff) {
        int i = 0;
        ArrayList<Reduction> removeList = new ArrayList<>();
        while (reductionsList.get(i).getAmount()<cutoff){
            removeList.add(reductionsList.get(i));
            i++;
        }
        i = reductionsList.size()-1;
        while (reductionsList.get(i).getAmount()>1-cutoff){
            removeList.add(reductionsList.get(i));
            i--;
        }
        reductionsList.removeAll(removeList);
    }

    public boolean insert(Reduction reduction) {
        // TODO make insertion suck less
        int i = 0;
        while (i < reductionsList.size() && reductionsList.get(i).getAmount()<reduction.getAmount()) {
            i++;
        }
        if (i!=reductionsList.size() && reduction.getQuery().equalsIgnoreCase(reductionsList.get(i).getQuery())){
            return false;
        } else {
            reductionsList.add(i, reduction);
            return true;
        }
    }

    public Reduction getClosest(double amount) {
        if (reductionsList.isEmpty()){
            throw new IllegalStateException("Retrieval on empty list");
        }
        // TODO make retrieval suck less
        if (reductionsList.size() == 1) {
            return reductionsList.get(0);
        }
        int i = 0;
        double lastAmount = -1;
        Reduction lastReduction = null;
        while (i < reductionsList.size() && reductionsList.get(i).getAmount()<amount) {
            lastAmount = reductionsList.get(i).getAmount();
            lastReduction = reductionsList.get(i);
            i++;
        }
        if (lastAmount<0){
            return reductionsList.get(0);
        }
        if (i == reductionsList.size()) {
            return getMax();
        }
        double lastDiff = Math.abs(amount-lastAmount);
        double currentDiff = Math.abs(amount-reductionsList.get(i).getAmount());
        return lastDiff<currentDiff?lastReduction:reductionsList.get(i);
    }

    public int size(){
        return reductionsList.size();
    }
}
