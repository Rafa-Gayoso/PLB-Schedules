package utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {



        private Pattern pattern;
        private Matcher matcher;

        private final String TIME24HOURS_PATTERN =
                "([01]?[0-9]|2[0-3]):[0-5][0-9]";

        private final String AFFILIATION_PATTERN =
                "[0-9]{2}[/][0-9]{8}-[0-9]{2}";

        public Validator(){

        }

        /**
         * Validate time in 24 hours format with regular expression
         * @param time time address for validation
         * @return true valid time fromat, false invalid time format
         */
        public boolean validateTimePattern(final String time){
            pattern = Pattern.compile(TIME24HOURS_PATTERN);
            matcher = pattern.matcher(time);
            return matcher.matches();

        }

    public boolean validateAffiliationPattern(final String affiliation){
        pattern = Pattern.compile(AFFILIATION_PATTERN);
        matcher = pattern.matcher(affiliation);
        return matcher.matches();

    }


}
