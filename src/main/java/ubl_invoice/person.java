package ubl_invoice;

import CodeList.CountryCode;


public class person {

    public enum schemaID {
        CRN,
        MOM,MLS,SAG,OTH,
        TIN,NAT,GCC,IQA;
    }

    private String RegistrationName,ID;
    private String street_name,district,city_name,building_num,postal_address,companyId;
    private CountryCode country_code;
    private schemaID schemaId;
    private boolean isSeller;





    protected person(boolean isSeller){
        this.isSeller=isSeller;
    }
    public String getRegistrationName() {
        return RegistrationName;
    }

    public void setRegistrationName(String RegistrationName) {
        this.RegistrationName = RegistrationName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID, schemaID schema) {
        this.ID = ID;
        this.schemaId=schema;

    }

    public schemaID getSchemaId() {
        return schemaId;
    }

    public CountryCode getCountry_code() {
        return country_code;
    }

    public void setCountry_code(CountryCode country_code) {
        this.country_code = country_code;

    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;

    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity_name() {
        return city_name;

    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;

    }

    public String getPostal_address() {
        return postal_address;
    }

    public boolean setPostal_address(String postal_address) {

        if(this.country_code == CountryCode.SA && postal_address.length() != 5)
            return false;
        this.postal_address = postal_address;

        return true;

    }

    public String getCompanyId() {
        return companyId;
    }

    public boolean setCompanyId(String companyId) {

        if(companyId.length() == 15 && companyId.charAt(0) == '3' && companyId.charAt(14)=='3')
        {
            this.companyId = companyId;
            return true;
        }
        else
            return false;

    }

    public String getBuilding_num() {
        return building_num;
    }

    public boolean setBuilding_num(String building_num) {

        if(building_num.length() != 4)
            return false;
        this.building_num = building_num;
        return true;
    }

}
