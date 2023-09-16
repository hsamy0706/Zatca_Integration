package ubl_invoice;

import CodeList.AllowanceChargeReasonCode;
import CodeList.TaxCategoryCode;

import java.text.DecimalFormat;

public class AllowanceCharge {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private double amount=0.00,baseAmount=0.00,multiplierFactor=0.00;
    private boolean chargeIndicator;
//    private String reasonCode,reason;
    private double vatRate;
    private TaxCategoryCode taxCode;
    private AllowanceChargeReasonCode reasonCode;



    public AllowanceCharge(boolean chargeIndicator) {
        this.chargeIndicator = chargeIndicator;
    }

    public boolean isChargeIndicator() {
        return chargeIndicator;
    }

    public AllowanceChargeReasonCode getReasonCode() {
        return reasonCode;
    }

    public boolean setReasonCode(AllowanceChargeReasonCode reasonCode) {
        if(chargeIndicator && !reasonCode.groupName().equals("Charge"))
            return false;
        if(!chargeIndicator && !reasonCode.groupName().equals("Allowance"))
            return false;
        this.reasonCode = reasonCode;

        return true;
    }



    public boolean setAmount(double amount)
    {
        if(amount < 0.00)
            return false;
        this.amount=amount;
        return  true;
    }
    public boolean setAmount(double baseAmount,double multiplierFactor)
    {
        if(baseAmount < 0.00)
            return false;
        if (multiplierFactor < 0.00 || multiplierFactor > 100.00)
            return false;
        this.baseAmount=baseAmount;
        this.multiplierFactor=multiplierFactor;
        this.amount= Double.parseDouble(df.format(baseAmount * (multiplierFactor / 100.00)));
        return true;
    }

    public double getAmount() {
        return amount;
    }
    public boolean setVatPercentage(double vatPercentage , TaxCategoryCode taxCategory) {

        if (taxCategory == TaxCategoryCode.O) //vat category O shall not contain document level allowance
            return false;
        if(vatPercentage > 100 || vatPercentage < 0)
            return false;
        if(taxCategory == TaxCategoryCode.E || taxCategory == TaxCategoryCode.Z)
            if(vatPercentage != 0)
                return false;
        if(taxCategory == TaxCategoryCode.S && vatPercentage == 0.00)
            return false;
        vatRate = Double.parseDouble(df.format(vatPercentage));
        taxCode=taxCategory;
        return true;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public double getMultiplierFactor() {
        return multiplierFactor;
    }

    public double getVatRate() {
        return vatRate;
    }

    public TaxCategoryCode getTaxCode() {
        return taxCode;
    }
}
