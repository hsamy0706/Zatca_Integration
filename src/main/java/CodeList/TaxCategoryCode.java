package CodeList;

public enum TaxCategoryCode {
    E("Exempt From TaxE"),
    S("Standard"),
    O("Out Of Scope"),
    Z("Zero Rated");

    public final String label;

    private TaxCategoryCode(String label) {
        this.label = label;
    }
}
