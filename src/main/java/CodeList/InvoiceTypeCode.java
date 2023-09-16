package CodeList;

public enum InvoiceTypeCode {
    invoice("388"),
    debit("383"),
    credit("381"),
    prepayment("386");

    public final String label;

    private InvoiceTypeCode(String label) {
        this.label = label;

    }
}
