package ubl_invoice;

import CodeList.AllowanceChargeReasonCode;
import CodeList.TaxCategoryCode;
import CodeList.TaxExemptionReasonCode;

import java.text.DecimalFormat;


public class InvoiceItem {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private String ProductName;

    private double ProductPrice;
    private long ProductQuantity,BaseQuantity=1;
    private static long ID=0;
    private long itemID=0; // to save in it id of each item
    private double VatPercentage=0.00,VatValue=0.00,totalPriceAfterVat=0.00;

    private AllowanceCharge itemCharge,itemAllowance;
    private double ProductNetPrice; // = product price - discount value
    private double totalPriceNetAmount; // = ((ProductNetPrice / BaseQuantity) * ProductQuantity)
                                        // - discountValue + chargeValue
    private TaxCategoryCode taxCategoryCode;
    private TaxExemptionReasonCode exemptionReasonCode;


    protected InvoiceItem() {
        ID++;
        itemID=ID;
        itemAllowance=new AllowanceCharge(false);
        itemCharge=new AllowanceCharge(true);
        //this.docCurrencyCode=currencyCode;
    }

    public long getItemID() {
        return itemID;
    }


    public String getProductName() {
        return ProductName;
    }
    public void setProductName(String productName) {
        ProductName = productName;
    }

    public double getProductPrice() {
        return ProductPrice;
    }
    public boolean setProductPrice(double productPrice) {
        if(productPrice < 0.0)
            return false;
        ProductPrice = productPrice;
        return true;
    }

    public long getProductQuantity() {
        return ProductQuantity;
    }
    public boolean setProductQuantity(long productQuantity) {
        if(productQuantity <= 0)
            return false;
        ProductQuantity = productQuantity;

        return true;
    }

    public long getBaseQuantity() {
        return BaseQuantity;
    }
    public boolean setBaseQuantity(long baseQuantity) {
        if(baseQuantity <= 0)
            return false;
        BaseQuantity = baseQuantity;

        return true;
    }

    public TaxCategoryCode getTaxCategoryCode() {
        return taxCategoryCode;
    }
    public TaxExemptionReasonCode getExemptionReasonCode() {
        return exemptionReasonCode;
    }
    public double getVatPercentage() {
        return VatPercentage;
    }

    public boolean setVatPercentage(double vatPercentage , TaxCategoryCode taxCategory) {
        if(vatPercentage > 100 || vatPercentage < 0)
            return false;
        if(taxCategory == TaxCategoryCode.E || taxCategory == TaxCategoryCode.O
           || taxCategory == TaxCategoryCode.Z)
            if(vatPercentage != 0)
                return false;
        if(taxCategory == TaxCategoryCode.S && vatPercentage == 0.00)
                 return false;
        VatPercentage = Double.parseDouble(df.format(vatPercentage));
        taxCategoryCode=taxCategory;

        return true;
    }
    public boolean setVatPercentage(double vatPercentage , TaxCategoryCode taxCategory, TaxExemptionReasonCode exemptionReasonCode){
        if (taxCategory == TaxCategoryCode.S) // Standard tax category doesn't 've exemption reason
            return false;
        // to ensure that exemptionReasonCode is from its tax category's code list
        if(!exemptionReasonCode.groupName().equals(taxCategory.toString()))
            return false;
        if(!this.setVatPercentage(vatPercentage,taxCategory))
            return false;
        this.exemptionReasonCode=exemptionReasonCode;
        return true;
    }



    public boolean setAllowanceCharge(boolean isCharge, double base, double multiplierFactor, AllowanceChargeReasonCode reasonCode)
    {
        if(isCharge)
        {
            if(!itemCharge.setReasonCode(reasonCode))
                return false;
        }
        else{
            if(!itemAllowance.setReasonCode(reasonCode))
                return false;
        }
        return this.setAllowanceCharge(isCharge, base, multiplierFactor);
    }
    public boolean setAllowanceCharge(boolean isCharge, double value, AllowanceChargeReasonCode reasonCode)
    {
        if (isCharge)
        {
            if(!itemCharge.setReasonCode(reasonCode))
                return false;
        }
        else{
            if(!itemAllowance.setReasonCode(reasonCode))
                return false;
        }
        return this.setAllowanceCharge(isCharge, value);
    }
    public boolean setAllowanceCharge(boolean isCharge,double base,double multiplierFactor)
    {
        if(isCharge)
        {
            itemCharge=new AllowanceCharge(isCharge);
            return itemCharge.setAmount(base, multiplierFactor);
        }
        else{
            itemAllowance=new AllowanceCharge(isCharge);
            return itemAllowance.setAmount(base, multiplierFactor);
        }
    }
    public boolean setAllowanceCharge(boolean isCharge,double value)
    {
        if(isCharge)
        {
            itemCharge=new AllowanceCharge(isCharge);
            return itemCharge.setAmount(value);
        }
        else{
            itemAllowance=new AllowanceCharge(isCharge);
            return itemAllowance.setAmount(value);
        }
    }

    public AllowanceCharge getItemCharge() {
        return itemCharge;
    }
    public AllowanceCharge getItemAllowance() {
        return itemAllowance;
    }

    private void calculateProductNetPrice()
    {
        ProductNetPrice = Double.parseDouble(df.format(ProductPrice - itemAllowance.getAmount()));
    }
    private void calculateTotalNetAmount()
    {
         totalPriceNetAmount = Double.parseDouble(df.format((ProductNetPrice / BaseQuantity) * ProductQuantity));
         totalPriceNetAmount += itemCharge.getAmount();
         totalPriceNetAmount -= itemAllowance.getAmount();
         totalPriceNetAmount= Double.parseDouble(df.format(totalPriceNetAmount));

    }
    private void calculateTotalPriceAfterVat()
    {
        if(VatPercentage > 0)
            VatValue = Double.parseDouble(df.format(totalPriceNetAmount * (VatPercentage /100.0)));
        totalPriceAfterVat=Double.parseDouble(df.format(totalPriceNetAmount + VatValue));

    }
    protected void calculate()
    {
        //calculate total values of invoice item
        calculateProductNetPrice();
        calculateTotalNetAmount();
        calculateTotalPriceAfterVat();
    }

    public double getVatValue() {
        return VatValue;
    }
    public double getTotalPriceAfterVat() {
        return totalPriceAfterVat;
    }
    public double getProductNetPrice() {
        return ProductNetPrice;
    }
    public double getTotalPriceNetAmount() {
        return totalPriceNetAmount;
    }


}
