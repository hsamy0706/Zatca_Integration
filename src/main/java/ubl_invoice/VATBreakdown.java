package ubl_invoice;

import CodeList.TaxExemptionReasonCode;

public class VATBreakdown {
    private double taxableAmount=0.0,taxAmount=0.0,vatRate=0.0;
    private String taxCategory;
    private TaxExemptionReasonCode exemptionReasonCode;

    protected  VATBreakdown(){}

    public double getTaxableAmount() {
        return taxableAmount;
    }
    public void setTaxableAmount(double taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public double getVatRate() {
        return vatRate;
    }
    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    public String getTaxCategory() {
        return taxCategory;
    }
    public void setTaxCategory(String taxCategory) {
        this.taxCategory = taxCategory;
    }

    public TaxExemptionReasonCode getExemptionReasonCode() {
        return exemptionReasonCode;
    }
    public void setExemptionReasonCode(TaxExemptionReasonCode exemptionReasonCode) {
        this.exemptionReasonCode = exemptionReasonCode;
    }

    public double getTaxAmount() {
        return taxAmount;
    }
    public void CalculateTaxAmount()
    {
        this.taxAmount=this.taxableAmount * (this.vatRate / 100.00);
    }
}
