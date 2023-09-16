package ubl_invoice;

import CodeList.*;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UBLXML {

    private InvoiceType invoiceType = new InvoiceType();
    private List<DocumentReferenceType> documentReferences = invoiceType.getAdditionalDocumentReference();
    private List<DeliveryType> deliveryTypes=invoiceType.getDelivery();
    private List<TaxTotalType> taxTotalTypes=invoiceType.getTaxTotal();
    private CurrencyCode currencyCode;
    private String xml_file_name="";

    protected UBLXML() {

    }
    protected boolean GenerateXMLInvoice(UBLInvoice invoice){

        this.createProfileID(UBLInvoice.getProfileId());
        this.createInvoiceID(invoice.getID());
        this.createUUID();
        this.createTaxCurrencyCode(UBLInvoice.getTaxCurrencyCode());
        this.createCurrencyCode(invoice.getCurrencyCode());
        this.createInvoiceTypeCode(invoice.getInvoiceTypeCode(),invoice.isStandard());
        this.createICV(UBLInvoice.getICV());
        this.createIssueDate(invoice.getIssueDate());
        this.createIssueTime(invoice.getIssueTime());
        this.createSupplierParty(invoice.supplierParty);
        this.createCustomerParty(invoice.customerParty);
        this.createPaymentMeans(invoice.getPaymentMeansCode());
        this.createActualDeliveryDate(invoice.getDeliveryDate());
        this.createDocAllowanceCharges(invoice.allowanceCharges);
        this.createVATBreakdowns(invoice.vatBreakdowns,invoice.getTaxTotal());
        this.createTaxTotalAmount(invoice.getTaxTotalinSAR());
        this.createLegalMonetaryTotal(invoice);
        this.createInvoiceLines(invoice.invoiceItems);

        this.generateXmlFileName(invoice.supplierParty.getID(),invoice.getID(),
                                 invoice.getIssueDate().toString(),invoice.getIssueTime().toString());
        this.generateXMLFile();


      return true;
    }
    private void createProfileID(String profileID) {
        ProfileIDType profileIDType=new ProfileIDType();
        profileIDType.setValue(profileID);
        invoiceType.setProfileID(profileIDType);
    }
    private void createInvoiceID(String ID) {
        IDType id=new IDType();
        id.setValue(ID);
        invoiceType.setID(id);
    }
    private void createUUID() {
        UUIDType uuid=new UUIDType();
        uuid.setValue(UUID.randomUUID().toString());
        invoiceType.setUUID(uuid);
    }
    private void createTaxCurrencyCode(String TaxCurrencyCode) {
        TaxCurrencyCodeType taxCurrencyCodeType=new TaxCurrencyCodeType();
        taxCurrencyCodeType.setValue(TaxCurrencyCode);
        invoiceType.setTaxCurrencyCode(taxCurrencyCodeType);
    }
    private void createCurrencyCode(CurrencyCode currencyCode) {
        DocumentCurrencyCodeType currencyCodeType=new DocumentCurrencyCodeType();
        currencyCodeType.setValue(currencyCode.code);
        invoiceType.setDocumentCurrencyCode(currencyCodeType);
        this.currencyCode=currencyCode;
    }
    private void createInvoiceTypeCode(InvoiceTypeCode invoiceTypeCode,boolean isStandard) {
        InvoiceTypeCodeType invcode=new InvoiceTypeCodeType();

        invcode.setValue(invoiceTypeCode.label);
        if(isStandard)
            invcode.setName("0100000");
        else
            invcode.setName("0200000");
        invoiceType.setInvoiceTypeCode(invcode);
    }
    private void createICV(long ICV) {
        String icv_val=Long.toString(ICV);
        DocumentReferenceType documentReference  = new DocumentReferenceType();
        IDType idICV=new IDType();
        idICV.setValue("ICV");
        documentReference.setID(idICV);
        UUIDType uuidICV=new UUIDType();
        uuidICV.setValue(icv_val);
        documentReference.setUUID(uuidICV);
        documentReferences.add(documentReference);
    }
    private void createIssueDate(XMLGregorianCalendar date) {
        IssueDateType issueDateType=new IssueDateType();
        issueDateType.setValue(date);
        invoiceType.setIssueDate(issueDateType);
    }
    private void createIssueTime(XMLGregorianCalendar Time) {
        IssueTimeType issueTimeType = new IssueTimeType();
        issueTimeType.setValue(Time);
        invoiceType.setIssueTime(issueTimeType);

    }


    private void createSupplierParty(person Supplier){

        SupplierPartyType supplierPartyType= new SupplierPartyType();
        supplierPartyType.setParty(this.createParty(Supplier));
        invoiceType.setAccountingSupplierParty(supplierPartyType);
    }
    private void createCustomerParty(person customer){
        CustomerPartyType customerPartyType= new CustomerPartyType();
        customerPartyType.setParty(this.createParty(customer));
        invoiceType.setAccountingCustomerParty(customerPartyType);
    }
    private PartyType createParty(person person){

        PartyType party= new PartyType();
        List<PartyIdentificationType> listOfPartyIDs=party.getPartyIdentification();
        List<PartyLegalEntityType> listOfLegalEntities=party.getPartyLegalEntity();
        List<PartyTaxSchemeType> listOfPartyTaxScheme =party.getPartyTaxScheme();
        AddressType postalAddress=new AddressType();
        //id
        if(person.getID() != null)
        {
            listOfPartyIDs.add(this.createPartyID(person.getID(),person.getSchemaId().toString()));
        }


        //Name
        if (person.getRegistrationName() != null)
            listOfLegalEntities.add(this.createPartyName(person.getRegistrationName()));


        //country code
        if (person.getCountry_code() != null)
            postalAddress.setCountry(this.createPartyCountryCode(person.getCountry_code()));



        //Street name
        if (person.getStreet_name() != null)
            postalAddress.setStreetName(this.createPartyStreetName(person.getStreet_name()));



        //city name
        if(person.getCity_name() != null)
            postalAddress.setCityName(this.createPartyCityName(person.getCity_name()));


        //district
        if (person.getDistrict() != null)
            postalAddress.setCitySubdivisionName(this.createPartyDistrict(person.getDistrict()));


        //building number
        if (person.getBuilding_num() != null)
            postalAddress.setBuildingNumber(this.createPartyBuildingNum(person.getBuilding_num()));


        //postal address
        if (person.getPostal_address() != null)
            postalAddress.setPostalZone(this.createPartyPostalAddress(person.getPostal_address()));

        //company id
        if (person.getCompanyId() != null)
            listOfPartyTaxScheme.add(this.createCompanyID(person.getCompanyId()));

        party.setPostalAddress(postalAddress);

        return party;
    }
    private PartyIdentificationType createPartyID(String ID,String schemaID){

        PartyIdentificationType identificationType= new PartyIdentificationType();
        IDType idType=new IDType();
        idType.setValue(ID);
        idType.setSchemeID(schemaID);
        identificationType.setID(idType);
        return identificationType;

    }
    private PartyLegalEntityType createPartyName(String name) {

        PartyLegalEntityType legalEntityType=new PartyLegalEntityType();
        RegistrationNameType registrationName= new RegistrationNameType();
        registrationName.setValue(name);
        legalEntityType.setRegistrationName(registrationName);
        return legalEntityType;

    }
    private CountryType createPartyCountryCode(CountryCode country_code){

        CountryType countryType=new CountryType();
        IdentificationCodeType identificationCodeType=new IdentificationCodeType();
        identificationCodeType.setValue(country_code.code);
        countryType.setIdentificationCode(identificationCodeType);
        return countryType;

    }
    private StreetNameType createPartyStreetName(String street_name) {

        StreetNameType streetNameType=new StreetNameType();
        streetNameType.setValue(street_name);
        return streetNameType;

    }
    private CityNameType createPartyCityName(String city_name) {

        CityNameType cityNameType=  new CityNameType();
        cityNameType.setValue(city_name);
        return cityNameType;

    }
    private CitySubdivisionNameType createPartyDistrict(String district){

        CitySubdivisionNameType citySubdivisionNameType= new CitySubdivisionNameType();
        citySubdivisionNameType.setValue(district);
        return citySubdivisionNameType;


    }
    private BuildingNumberType createPartyBuildingNum(String building_num) {

        BuildingNumberType buildingNumberType=new BuildingNumberType();
        buildingNumberType.setValue(building_num);
        return buildingNumberType;

    }
    private PostalZoneType createPartyPostalAddress(String postal_address) {

        PostalZoneType postalZoneType = new PostalZoneType();
        postalZoneType.setValue(postal_address);
        return postalZoneType;


    }
    private PartyTaxSchemeType createCompanyID(String companyID) {

        PartyTaxSchemeType taxSchemeType=new PartyTaxSchemeType();
        CompanyIDType companyIDType=new CompanyIDType();
        companyIDType.setValue(companyID);
        taxSchemeType.setCompanyID(companyIDType);
        TaxSchemeType schemeType=new TaxSchemeType();
        IDType taxId = new IDType();
        taxId.setValue("VAT");
        schemeType.setID(taxId);
        taxSchemeType.setTaxScheme(schemeType);
        return taxSchemeType;

    }

    private void createPaymentMeans(PaymentMeansCode paymentMeansCode) {
        if (paymentMeansCode == null)
            return;
        PaymentMeansType paymentMeansType=new PaymentMeansType();
        PaymentMeansCodeType paymentMeansCodeType=new PaymentMeansCodeType();
        paymentMeansCodeType.setValue(paymentMeansCode.ID);
        paymentMeansType.setPaymentMeansCode(paymentMeansCodeType);
        List<PaymentMeansType> paymentMeansTypes=invoiceType.getPaymentMeans();
        paymentMeansTypes.add(paymentMeansType);
    }
    private void createActualDeliveryDate(XMLGregorianCalendar date){
        if (date == null)
            return;
        DeliveryType deliveryType=new DeliveryType();
        ActualDeliveryDateType actualDeliveryDateType=new ActualDeliveryDateType();
        actualDeliveryDateType.setValue(date);
        deliveryType.setActualDeliveryDate(actualDeliveryDateType);

        deliveryTypes.add(deliveryType);
    }

    private void createDocAllowanceCharges(ArrayList<AllowanceCharge> allowanceCharges) {
        List<AllowanceChargeType> allowanceChargeTypes =invoiceType.getAllowanceCharge();
        for (AllowanceCharge allowanceCharge:allowanceCharges) {
              AllowanceChargeType allowanceChargeType = this.createAllowanceCharge(allowanceCharge);

              //tax category
              List<TaxCategoryType> taxCategoryTypes=allowanceChargeType.getTaxCategory();
              taxCategoryTypes.add(this.createTaxCategory(allowanceCharge.getVatRate(),
                                                          allowanceCharge.getTaxCode().toString()));
              allowanceChargeTypes.add(allowanceChargeType);

        }
    }
    private void createVATBreakdowns(ArrayList<VATBreakdown> vatBreakdowns, double taxTotal){
        TaxTotalType taxTotalType=new TaxTotalType();

        TaxAmountType taxtotalAmountType=new TaxAmountType();
        taxtotalAmountType.setValue(BigDecimal.valueOf(taxTotal));
        taxtotalAmountType.setCurrencyID(currencyCode.code);
        taxTotalType.setTaxAmount(taxtotalAmountType);

        List<TaxSubtotalType> taxSubtotalTypes=taxTotalType.getTaxSubtotal();

        for (VATBreakdown breakDown:vatBreakdowns) {
            TaxSubtotalType taxSubtotalType=new TaxSubtotalType();
            TaxAmountType taxAmountType=new TaxAmountType();
            taxAmountType.setValue(BigDecimal.valueOf(breakDown.getTaxAmount()));
            taxAmountType.setCurrencyID(currencyCode.code);
            taxSubtotalType.setTaxAmount(taxAmountType);

            TaxableAmountType taxableAmountType =new TaxableAmountType();
            taxableAmountType.setValue(BigDecimal.valueOf(breakDown.getTaxableAmount()));
            taxableAmountType.setCurrencyID(currencyCode.code);
            taxSubtotalType.setTaxableAmount(taxableAmountType);

            TaxCategoryType taxCategoryType= this.createTaxCategory(breakDown.getVatRate(),breakDown.getTaxCategory());
            if(!breakDown.getTaxCategory().equals(TaxCategoryCode.S.toString()))
            {
                //System.out.println(breakDown.getTaxCategory().toString());
                TaxExemptionReasonCodeType codeType=new TaxExemptionReasonCodeType();
                codeType.setValue(breakDown.getExemptionReasonCode().code());
                taxCategoryType.setTaxExemptionReasonCode(codeType);

                TaxExemptionReasonType reasonType=new TaxExemptionReasonType();
                reasonType.setValue(breakDown.getExemptionReasonCode().getReason());
               List<TaxExemptionReasonType> reasonTypes= taxCategoryType.getTaxExemptionReason();
               reasonTypes.add(reasonType);

            }
            taxSubtotalType.setTaxCategory(taxCategoryType);

            taxSubtotalTypes.add(taxSubtotalType);

        }

        taxTotalTypes.add(taxTotalType);

    }
    private TaxCategoryType createTaxCategory(double vatRate,String TaxCode) {
        TaxCategoryType taxCategoryType=new TaxCategoryType();
        IDType idType=new IDType();
        idType.setValue(TaxCode);
        taxCategoryType.setID(idType);

        PercentType percentType=new PercentType();
        percentType.setValue(BigDecimal.valueOf(vatRate));
        taxCategoryType.setPercent(percentType);

        TaxSchemeType taxSchemeType = new TaxSchemeType();
        IDType taxSchemeId= new IDType();
        taxSchemeId.setValue("VAT");
        taxSchemeType.setID(taxSchemeId);
        taxCategoryType.setTaxScheme(taxSchemeType);
        return taxCategoryType;
    }

    private void createTaxTotalAmount(double taxTotal){
        TaxTotalType taxTotalType=new TaxTotalType();

        TaxAmountType taxAmountType= new TaxAmountType();
        taxAmountType.setValue(BigDecimal.valueOf(taxTotal));
        taxAmountType.setCurrencyID(UBLInvoice.getTaxCurrencyCode());
        taxTotalType.setTaxAmount(taxAmountType);

        taxTotalTypes.add(taxTotalType);
    }

    private void createLegalMonetaryTotal(UBLInvoice invoice) {
        MonetaryTotalType monetaryTotalType=new MonetaryTotalType();

        //total line amount
        LineExtensionAmountType lineExtensionAmount=new LineExtensionAmountType();
        lineExtensionAmount.setValue(BigDecimal.valueOf(invoice.getTotalLineAmount()));
        lineExtensionAmount.setCurrencyID(currencyCode.code);
        monetaryTotalType.setLineExtensionAmount(lineExtensionAmount);

        //tax exclusive
        TaxExclusiveAmountType taxExclusiveAmount = new TaxExclusiveAmountType();
        taxExclusiveAmount.setValue(BigDecimal.valueOf(invoice.getTaxExclusive()));
        taxExclusiveAmount.setCurrencyID(currencyCode.code);
        monetaryTotalType.setTaxExclusiveAmount(taxExclusiveAmount);

        // tax inclusive
        TaxInclusiveAmountType taxInclusiveAmount=new TaxInclusiveAmountType();
        taxInclusiveAmount.setValue(BigDecimal.valueOf(invoice.getTaxInclusive()));
        taxInclusiveAmount.setCurrencyID(currencyCode.code);
        monetaryTotalType.setTaxInclusiveAmount(taxInclusiveAmount);

        //allowance total
        AllowanceTotalAmountType allowanceTotalAmount=new AllowanceTotalAmountType();
        allowanceTotalAmount.setValue(BigDecimal.valueOf(invoice.getAllowanceTotal()));
        allowanceTotalAmount.setCurrencyID(currencyCode.code);
        monetaryTotalType.setAllowanceTotalAmount(allowanceTotalAmount);

        // charge total
        ChargeTotalAmountType chargeTotalAmount=new ChargeTotalAmountType();
        chargeTotalAmount.setValue(BigDecimal.valueOf(invoice.getChargeTotal()));
        chargeTotalAmount.setCurrencyID(currencyCode.code);
        monetaryTotalType.setChargeTotalAmount(chargeTotalAmount);

        //prepaid amount
        PrepaidAmountType prepaidAmount=new PrepaidAmountType();
        prepaidAmount.setValue(BigDecimal.valueOf(invoice.getPrepaymentAmount()));
        prepaidAmount.setCurrencyID(currencyCode.code);
        monetaryTotalType.setPrepaidAmount(prepaidAmount);

        //payable amount
        PayableAmountType payableAmount=new PayableAmountType();
        payableAmount.setValue(BigDecimal.valueOf(invoice.getPayableAmount()));
        payableAmount.setCurrencyID(currencyCode.code);
        monetaryTotalType.setPayableAmount(payableAmount);

        invoiceType.setLegalMonetaryTotal(monetaryTotalType);

    }
    private void createInvoiceLines(ArrayList<InvoiceItem> invoiceItems) {
        List<InvoiceLineType> invoiceLineTypes=invoiceType.getInvoiceLine();
        for (InvoiceItem item:invoiceItems) {
            InvoiceLineType invoiceLine = new InvoiceLineType();

            //ID
            IDType id = new IDType();
            id.setValue(Long.toString(item.getItemID()));
            invoiceLine.setID(id);

            //quantity
            invoiceLine.setInvoicedQuantity(this.createItemQuantity(item.getProductQuantity()));

            //total net amount
            invoiceLine.setLineExtensionAmount(this.createItemTotalNet(item.getTotalPriceNetAmount()));

            //tax total
            List<TaxTotalType> taxTotalTypes=invoiceLine.getTaxTotal();
            taxTotalTypes.add(this.createItemTaxTotal(item.getVatValue(),item.getTotalPriceAfterVat()));

            //itemType
            invoiceLine.setItem(this.createItemType(item));

            //price type
            invoiceLine.setPrice(this.createPriceType(item));
            invoiceLineTypes.add(invoiceLine);
        }
    }
    private InvoicedQuantityType createItemQuantity(long quantity) {
        InvoicedQuantityType invoicedQuantity= new InvoicedQuantityType();
        invoicedQuantity.setValue(BigDecimal.valueOf(quantity));
        return invoicedQuantity;
    }
    private LineExtensionAmountType createItemTotalNet(double totalNetAmount) {
        LineExtensionAmountType lineExtensionAmount = new LineExtensionAmountType();
        lineExtensionAmount.setValue(BigDecimal.valueOf(totalNetAmount));
        lineExtensionAmount.setCurrencyID(currencyCode.code);
        return lineExtensionAmount;
    }
    private TaxTotalType createItemTaxTotal(double vatValue,double totalWithVAT){

        TaxTotalType taxTotal = new TaxTotalType();
        TaxAmountType taxAmount= new TaxAmountType();
        taxAmount.setValue(BigDecimal.valueOf(vatValue));
        taxAmount.setCurrencyID(currencyCode.code);
        taxTotal.setTaxAmount(taxAmount);

        RoundingAmountType roundingAmount = new RoundingAmountType();
        roundingAmount.setValue(BigDecimal.valueOf(totalWithVAT));
        roundingAmount.setCurrencyID(currencyCode.code);
        taxTotal.setRoundingAmount(roundingAmount);
        return taxTotal;
    }
    private ItemType createItemType(InvoiceItem invoiceItem) {
        ItemType itemType = new ItemType();
        //item name
        NameType name = new NameType();
        name.setValue(invoiceItem.getProductName());
        itemType.setName(name);
        List<TaxCategoryType> taxCategoryTypes =itemType.getClassifiedTaxCategory();
        taxCategoryTypes.add(this.createTaxCategory(invoiceItem.getVatPercentage(),invoiceItem.getTaxCategoryCode().toString()));
        return itemType;

    }
    private PriceType createPriceType(InvoiceItem invoiceItem) {
        PriceType priceType =new PriceType();
        //item net price
        PriceAmountType priceAmountType= new PriceAmountType();
        priceAmountType.setValue(BigDecimal.valueOf(invoiceItem.getProductNetPrice()));
        priceAmountType.setCurrencyID(currencyCode.code);
        priceType.setPriceAmount(priceAmountType);

        List<AllowanceChargeType> chargeTypes= priceType.getAllowanceCharge();
        if(invoiceItem.getBaseQuantity() > 1)
        {
            BaseQuantityType baseQuantityType=new BaseQuantityType();
            baseQuantityType.setValue(BigDecimal.valueOf(invoiceItem.getBaseQuantity()));
            priceType.setBaseQuantity(baseQuantityType);
        }
        if(invoiceItem.getItemAllowance().getAmount() > 0.00)
        {
            chargeTypes.add(this.createAllowanceCharge(invoiceItem.getItemAllowance()));
        }
        if(invoiceItem.getItemCharge().getAmount() > 0.00)
        {
            chargeTypes.add(this.createAllowanceCharge(invoiceItem.getItemCharge()));
        }
        return priceType;
    }
    private AllowanceChargeType createAllowanceCharge(AllowanceCharge allowanceCharge) {
        AllowanceChargeType allowanceChargeType = new AllowanceChargeType();
        //charge indicator
        ChargeIndicatorType chargeIndicatorType=new ChargeIndicatorType();
        chargeIndicatorType.setValue(allowanceCharge.isChargeIndicator());
        allowanceChargeType.setChargeIndicator(chargeIndicatorType);
        //reason & reason code
        if(allowanceCharge.getReasonCode() != null)
        {
            AllowanceChargeReasonType reasonType= new AllowanceChargeReasonType();
            reasonType.setValue(allowanceCharge.getReasonCode().getReason());
            List<AllowanceChargeReasonType> reasonTypes =allowanceChargeType.getAllowanceChargeReason();
            reasonTypes.add(reasonType);

            AllowanceChargeReasonCodeType codeType= new AllowanceChargeReasonCodeType();
            codeType.setValue(allowanceCharge.getReasonCode().code());
            allowanceChargeType.setAllowanceChargeReasonCode(codeType);
        }
        //amount
        AmountType amountType= new AmountType();
        amountType.setValue(BigDecimal.valueOf(allowanceCharge.getAmount()));
        amountType.setCurrencyID(currencyCode.code);
        allowanceChargeType.setAmount(amountType);
        return allowanceChargeType;
    }
    private void generateXmlFileName(String sellerId,String invoice_num,String date,String time){

      date=date.replaceAll("-","");
      time=time.replaceAll(":","");
      invoice_num=invoice_num.replaceAll("[^0-9]", "-");
      this.xml_file_name= sellerId+"_"+date+"T"+time+"_"+invoice_num+".xml";
    }
    private void generateXMLFile()
    {
        ObjectFactory objectFactory = new ObjectFactory();

        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance("oasis.names.specification.ubl.schema.xsd.invoice_2");
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            JAXBElement<InvoiceType> je =  objectFactory.createInvoice(invoiceType);

            marshaller.marshal(je,new File(xml_file_name));
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

}
