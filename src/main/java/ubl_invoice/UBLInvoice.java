package ubl_invoice;

import CodeList.*;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class UBLInvoice {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final File icv_file=new File("ICV.txt");
    public person customerParty,supplierParty;
    protected ArrayList<InvoiceItem> invoiceItems;
    protected ArrayList<AllowanceCharge> allowanceCharges;
    protected ArrayList<VATBreakdown> vatBreakdowns;
    private Map<Map.Entry<String,Double>,Double> taxableAmountPerCategory;
    private XMLGregorianCalendar issueDate,deliveryDate;
    private XMLGregorianCalendar issueTime;
    private double taxTotal=0.00,allowanceTotal=0.00,chargeTotal=0.00,taxExclusive=0.00,
            taxInclusive=0.00,payableAmount=0.00,totalLineAmount=0.00,prepaymentAmount=0.00,taxTotalinSAR=0.00;
    private String ID;
    private final static String ProfileId="reporting:1.0";
    private final static String TaxCurrencyCode="SAR";
    private boolean isStandard;

    private static long ICV=0; //invoice counter value
    private CurrencyCode currencyCode;
    private double currencyToSAR;
    private PaymentMeansCode paymentMeansCode;
    private InvoiceTypeCode invoiceTypeCode;
    private TaxExemptionReasonCode E_exemptionReason,Z_exemptionReason;
    private final TaxExemptionReasonCode O_exemptionReason = TaxExemptionReasonCode.O._1;



    public UBLInvoice(boolean isStandard)
    {
        this.isStandard=isStandard;

        customerParty=new person(false);
        supplierParty=new person(true);
        invoiceItems = new ArrayList<>();
        allowanceCharges=new ArrayList<>();
        taxableAmountPerCategory=new HashMap<>();
        vatBreakdowns =new ArrayList<>();

//        invoice counter value
        if(!icv_file.exists()) {//create new one if not exist
            try {
                icv_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ICV=readICV();
        updateICV();


    }
    private long readICV(){
        try (BufferedReader reader = new BufferedReader(new FileReader(icv_file))){
            String val=reader.readLine();
            if(val == null)//means the file is empty
                return 0;
            else
               return Long.parseLong(val);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private void updateICV(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ICV.txt"))) {
            ICV++;
            writer.write(Long.toString(ICV));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static long getICV() {
        return ICV;
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;

    }

    public boolean isStandard() {
        return isStandard;
    }

    public static String getProfileId() {
        return ProfileId;
    }
    public static String getTaxCurrencyCode() {
        return TaxCurrencyCode;
    }


    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }
    public void setCurrencyCode(CurrencyCode currencyCode) {
        this.currencyCode = currencyCode;
    }
    public boolean setCurrencyCode(CurrencyCode currencyCode,double currencyToSAR) {
        if (currencyToSAR <= 0.00)
            return false;
        this.currencyCode = currencyCode;
        this.currencyToSAR=currencyToSAR;
        return true;

    }


    public InvoiceTypeCode getInvoiceTypeCode() {
        return invoiceTypeCode;
    }
    public void setInvoiceTypeCode(InvoiceTypeCode invoiceTypeCode) {
        this.invoiceTypeCode = invoiceTypeCode;

    }

    public XMLGregorianCalendar getIssueDate() {
        return issueDate;
    }
    public boolean setIssueDate(LocalDate date)
    {
        if(date.isAfter(LocalDate.now()))
        {
            System.out.println("please, Enter date les than or equal today");
            return false;
        }
        try{
            XMLGregorianCalendar xts = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            xts.setYear(date.getYear());
            xts.setMonth(date.getMonthValue());
            xts.setDay(date.getDayOfMonth());
            this.issueDate=xts;
            return true;
        }
        catch(DatatypeConfigurationException e){
            e.printStackTrace();
            return false;
        }

    }

    public XMLGregorianCalendar getIssueTime() {
        return issueTime;
    }
    public boolean setIssueTime(LocalTime time) {
        try{
            XMLGregorianCalendar xts = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            xts.setHour(time.getHour());
            xts.setMinute(time.getMinute());
            xts.setSecond(time.getSecond());
            this.issueTime=xts;

            return true;
        }
        catch(DatatypeConfigurationException e){
            e.printStackTrace();
            return false;
        }
    }


    public XMLGregorianCalendar getDeliveryDate() {
        return deliveryDate;
    }
    public boolean setDeliveryDate(LocalDate deliveryDate) {

        try{
            XMLGregorianCalendar xts = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            xts.setYear(deliveryDate.getYear());
            xts.setMonth(deliveryDate.getMonthValue());
            xts.setDay(deliveryDate.getDayOfMonth());
            this.deliveryDate = xts;
            return true;
        }
        catch(DatatypeConfigurationException e){
            e.printStackTrace();
            return false;
        }
    }


    public PaymentMeansCode getPaymentMeansCode() {
        return paymentMeansCode;
    }
    public void setPaymentMeansCode(PaymentMeansCode paymentMeansCode) {
        this.paymentMeansCode = paymentMeansCode;
    }


    public boolean addAllowanceCharge(boolean isCharge, double amount, TaxCategoryCode taxCode, double vatRate
            , AllowanceChargeReasonCode reasonCode)
    {
        AllowanceCharge allowance=new AllowanceCharge(isCharge);

        if(!allowance.setAmount(amount))
            return false;

        if (!allowance.setVatPercentage(vatRate,taxCode))
            return false;
        if(!allowance.setReasonCode(reasonCode))
            return false;
        allowanceCharges.add(allowance);

        //update taxable value and total values
        this.calculateDocAllowanceVal(allowance);
        return true;
    }
    public boolean addAllowanceCharge(boolean isCharge, double base,double multiplierFactor, TaxCategoryCode taxCode, double vatRate
            , AllowanceChargeReasonCode reasonCode)
    {
        AllowanceCharge allowance=new AllowanceCharge(isCharge);

        if(!allowance.setAmount(base,multiplierFactor))
            return false;

        if (!allowance.setVatPercentage(vatRate,taxCode))
            return false;
        if(!allowance.setReasonCode(reasonCode))
            return false;
        allowanceCharges.add(allowance);

        //update taxable value and total values
        this.calculateDocAllowanceVal(allowance);
        return true;
    }
    public boolean addAllowanceCharge(boolean isCharge, double amount, TaxCategoryCode taxCode, double vatRate)
    {
        AllowanceCharge allowance=new AllowanceCharge(isCharge);

        if(!allowance.setAmount(amount))
            return false;

       if (!allowance.setVatPercentage(vatRate,taxCode))
           return false;
       allowanceCharges.add(allowance);

        //update taxable value and total values
        this.calculateDocAllowanceVal(allowance);
       return true;
    }
    public boolean addAllowanceCharge(boolean isCharge,double base,double multiplierFactor,TaxCategoryCode taxCode,double vatRate)
    {
        AllowanceCharge allowance=new AllowanceCharge(isCharge);

        if(!allowance.setAmount(base,multiplierFactor))
            return false;

        if (!allowance.setVatPercentage(vatRate,taxCode))
            return false;
        allowanceCharges.add(allowance);

        //update taxable value and total values
        this.calculateDocAllowanceVal(allowance);
        return true;
    }
    private void calculateDocAllowanceVal(AllowanceCharge allowanceCharge)
    {
        //update taxable value and total values that related to document allowance charge

         String key=allowanceCharge.getTaxCode().toString();
         double value=allowanceCharge.getVatRate();
        double oldVal=taxableAmountPerCategory.getOrDefault(Map.entry(key,value),0.00);
        if(allowanceCharge.isChargeIndicator())
        {
            chargeTotal+=allowanceCharge.getAmount();

            taxableAmountPerCategory.put(Map.entry(key,value),oldVal + allowanceCharge.getAmount());

        }
        else
        {
            allowanceTotal+=allowanceCharge.getAmount();

            taxableAmountPerCategory.put(Map.entry(key,value),oldVal - allowanceCharge.getAmount());
        }
    }



    public InvoiceItem createInvoiceItem() {

        return new InvoiceItem();
    }
    public void addInvoiceItem(InvoiceItem invoiceItem) {

        invoiceItem.calculate();
        totalLineAmount+=invoiceItem.getTotalPriceNetAmount();

        String key= invoiceItem.getTaxCategoryCode().toString();
        double value = invoiceItem.getVatPercentage();

        taxableAmountPerCategory.put(Map.entry(key,value),taxableAmountPerCategory.getOrDefault(Map.entry(key,value),0.00)
                                                                                 + invoiceItem.getTotalPriceNetAmount());
        TaxCategoryCode categoryCode=invoiceItem.getTaxCategoryCode();
        if(categoryCode != TaxCategoryCode.S)
        {
            // Save exemption reason to add it to vat break down
            TaxExemptionReasonCode exemptionReason=invoiceItem.getExemptionReasonCode();
            if(categoryCode == TaxCategoryCode.E)
                E_exemptionReason=exemptionReason;
            if (categoryCode == TaxCategoryCode.Z)
                Z_exemptionReason=exemptionReason;

        }
        invoiceItems.add(invoiceItem);
    }

    private boolean createVATBreakdowns()
    {
        //total vat = sum of vat category tax amount for all categories.
        for (Map.Entry<Map.Entry<String,Double>, Double> entry : taxableAmountPerCategory.entrySet())
        {
            String taxCat=entry.getKey().getKey();
            double vatRate=entry.getKey().getValue();

            VATBreakdown breakdown= new VATBreakdown();
            breakdown.setTaxableAmount(entry.getValue());
            breakdown.setTaxCategory(taxCat);
            breakdown.setVatRate(vatRate);
            breakdown.CalculateTaxAmount();
            this.taxTotal+=breakdown.getTaxAmount();
            if(taxCat.equals(TaxCategoryCode.E.toString()))
            {
                if(E_exemptionReason == null)
                {
                    System.out.println("Error!, Vat break down with E vat category doesn't have tax exemption reason.");
                    return false;
                }
                breakdown.setExemptionReasonCode(E_exemptionReason);
            }

            if(taxCat.equals(TaxCategoryCode.Z.toString()))
            {
                if(Z_exemptionReason == null)
                {
                    System.out.println("Error!, Vat break down with Z vat category doesn't have tax exemption reason.");
                    return false;
                }
                breakdown.setExemptionReasonCode(Z_exemptionReason);
            }
            if(taxCat.equals(TaxCategoryCode.O.toString()))
                breakdown.setExemptionReasonCode(O_exemptionReason);

            vatBreakdowns.add(breakdown);
        }
        return true;
    }
    private void calculate()
    {
        if(!currencyCode.code.equals(TaxCurrencyCode))
            taxTotalinSAR= Double.parseDouble(df.format(taxTotal * currencyToSAR));
        else
            taxTotalinSAR=taxTotal;

        this.taxExclusive=  Double.parseDouble(df.format(totalLineAmount - allowanceTotal + chargeTotal));
        this.taxInclusive= Double.parseDouble(df.format(taxExclusive+taxTotal));
        this.payableAmount=taxInclusive - prepaymentAmount;
    }

    public double getTaxTotal() {
        return taxTotal;
    }

    public double getTaxTotalinSAR() {
        return taxTotalinSAR;
    }

    public double getAllowanceTotal() {
        return allowanceTotal;
    }
    public double getChargeTotal() {
        return chargeTotal;
    }
    public double getTaxExclusive() {
        return taxExclusive;
    }
    public double getTaxInclusive() {
        return taxInclusive;
    }
    public double getPayableAmount() {
        return payableAmount;
    }
    public double getTotalLineAmount() {
        return totalLineAmount;
    }
    public double getPrepaymentAmount() {
        return prepaymentAmount;
    }


    public boolean generateXMLInvoice(){

        if(!createVATBreakdowns())
            return false;

        if(invoiceItems.isEmpty())
        {
            System.out.println("Invoice must have at least one invoice line");
            return false;
        }

        calculate();

        UBLXML ublxml=new UBLXML();
        ublxml.GenerateXMLInvoice(this);

        return true;
    }
}
