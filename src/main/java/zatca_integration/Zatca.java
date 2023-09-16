package zatca_integration;


import CodeList.*;
import ubl_invoice.InvoiceItem;
import ubl_invoice.UBLInvoice;
import ubl_invoice.person;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;

public class Zatca {
    public static void createStandardInvoice(){
        UBLInvoice inv= new UBLInvoice(true);
        inv.setID("100xx12875");
        inv.setInvoiceTypeCode(InvoiceTypeCode.invoice);
        inv.setIssueDate(LocalDate.now());
        inv.setIssueTime(LocalTime.now());
        inv.setCurrencyCode(CurrencyCode.SAR);

        inv.supplierParty.setID("311111111111113", person.schemaID.CRN);
        inv.supplierParty.setRegistrationName("ABV ROCK GROUP KB");
        inv.supplierParty.setBuilding_num("2322");
        inv.supplierParty.setCity_name("MALAZ");
        inv.supplierParty.setDistrict("AL DHUBBAT");
        inv.supplierParty.setStreet_name("SALAH AL DIN AL AYOUBI RD");
        inv.supplierParty.setPostal_address("12627");
        inv.supplierParty.setCountry_code(CountryCode.SA);
        inv.supplierParty.setCompanyId("311111111101113");

        inv.customerParty.setID("311111111111113", person.schemaID.NAT);
        inv.customerParty.setRegistrationName("Acme Widget’s LTD 2");
        inv.customerParty.setStreet_name("EL Reyad");
        inv.customerParty.setBuilding_num("1111");
        inv.customerParty.setCity_name("Dammam");
        inv.customerParty.setDistrict("El Reyad");
        inv.customerParty.setPostal_address("12222");
        inv.customerParty.setCountry_code(CountryCode.SA);

        inv.setPaymentMeansCode(PaymentMeansCode._10);
        inv.setDeliveryDate(LocalDate.of(2023,9,18));

        inv.addAllowanceCharge(true,200.00, TaxCategoryCode.S,15.00,AllowanceChargeReasonCode.Charge.CG);
        inv.addAllowanceCharge(false,100.0,TaxCategoryCode.S,15.0,AllowanceChargeReasonCode.Allowance._95);

        // Invoice line with VAT 15%
        InvoiceItem item1=inv.createInvoiceItem();
        item1.setProductName("Item 1");
        item1.setProductPrice(500.0);
        item1.setProductQuantity(10);
        item1.setVatPercentage(15.0,TaxCategoryCode.S);
        inv.addInvoiceItem(item1);

        //Invoice line with VAT 15%
        InvoiceItem item2=inv.createInvoiceItem();
        item2.setProductName("Item 2");
        item2.setProductPrice(80.0);
        item2.setProductQuantity(10);
        item2.setVatPercentage(15.0,TaxCategoryCode.S);
        inv.addInvoiceItem(item2);

        //Invoice line with VAT 0%
        InvoiceItem item3=inv.createInvoiceItem();
        item3.setProductName("Item 3");
        item3.setProductPrice(300.0);
        item3.setProductQuantity(10);
        item3.setVatPercentage(0,TaxCategoryCode.E,TaxExemptionReasonCode.E._3);
        inv.addInvoiceItem(item3);

        inv.generateXMLInvoice();

    }
    public static void main(String[] args) {

       //demo due to test
       createStandardInvoice();



    }
}
