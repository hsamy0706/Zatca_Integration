package CodeList;

public interface TaxExemptionReasonCode {

    String groupName();
    String code();
    String getReason();

    enum Z implements TaxExemptionReasonCode {

        _1("VATEX-SA-34-1","The international transport of Goods "),
        _2("VATEX-SA-34-2","international transport of passengers"),
        _3("VATEX-SA-34-3 ","services directly connected and\n" +
                "incidental to a Supply of international\n" +
                "passenger transport"),
        _4("VATEX-SA-34-4 ","Supply of a qualifying means of transport"),
        _5("VATEX-SA-34-5 ","Any services relating to Goods or\n" +
                "passenger transportation, as defined in\n" +
                "article twenty five of these Regulations"),
        _6("VATEX-SA-35","Medicines and medical equipment"),
        _7("VATEX-SA-36","Qualifying metals"),
        _8("VATEX-SA-EDU","Private education to citizen "),
        _9("VATEX-SA-HEA","Private healthcare to citizen"),
        _10("VATEX-SA-MLTRY","supply of qualified military goods"),
        _11("VATEX-SA-32","Export of goods"),
        _12("VATEX-SA-33","Export of services");

        private final String code;
        private final String reason;

        Z(final String code, final String reason) {
            this.code = code;
            this.reason = reason;
        }

        @Override
        public String groupName() {
            return "Z";
        }

        @Override
        public String code() {
            return this.code;
        }

        @Override
        public String getReason() {
            return this.reason;
        }
    }

    enum E implements TaxExemptionReasonCode {

        _1("VATEX-SA-29","Financial services mentioned in Article\n" +
                "29 of the VAT Regulations"),
        _2("VATEX-SA-29-7","Life insurance services mentioned in\n" +
                "Article 29 of the VAT Regulations"),
        _3("VATEX-SA-30 ","Real estate transactions mentioned in\n" +
                "Article 30 of the VAT Regulations\n");

        private final String code;
        private final String reason;

        E(final String code, final String reason) {
            this.code = code;
            this.reason = reason;
        }

        @Override
        public String groupName() {
            return "E";
        }

        @Override
        public String code() {
            return this.code;
        }

        @Override
        public String getReason() {
            return this.reason;
        }
    }
    enum O implements TaxExemptionReasonCode{

        _1("VATEX-SA-OOS","Free text");
        private final String code;
        private final String reason;

        O(final String code, final String reason) {
            this.code = code;
            this.reason = reason;
        }

        @Override
        public String groupName() {
            return "O";
        }

        @Override
        public String code() {
            return this.code;
        }

        @Override
        public String getReason() {
            return this.reason;
        }
    }
}



