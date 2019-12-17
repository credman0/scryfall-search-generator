public class Reduction implements Comparable<Reduction>{
    private double amount;
    private String query;

    public double getAmount() {
        return amount;
    }

    public String getQuery() {
        return query;
    }

    public Reduction(double amount, String term) {
        this.amount = amount;
        this.query = term;
    }

    public String toString(){
        return getAmount() +"--"+ getQuery();
    }

    @Override
    public int compareTo(Reduction reduction) {
        return Double.compare(amount, reduction.amount);
    }
}
