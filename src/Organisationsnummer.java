    public static class Organisationsnummer {
        private final Pattern regexPattern =
                Pattern.compile("^(16)?(\\d{2})(\\d{2})(\\d{2})(-)?(\\d{3})(\\d)$");

        /**
         * Checks if the string representation of the organisationsnummer is valid.
         * 
         * @param orgnum ergo sum.
         * @throws Exception when a validity check fails.
         */

        public Organisationsnummer(String orgnum) throws Exception {
            if (orgnum == null) {
                throw new Exception("Orgnum is null");
            }

            Matcher m = regexPattern.matcher(orgnum);

            if (!m.find()) {
                throw new Exception("Invalid input. Doesn't match regular expression");
            }

            if (m.group(3) != null && !m.group(3).isEmpty()) {
                if (!(Integer.parseInt(m.group(3)) >= 20)) {
                    throw new Exception("Invalid orgnmr of shape xxNNxx-xxxx. NN must be >= 20.");
                }
            }

            String s = String.format("%s%s%s%s", m.group(2), m.group(3), m.group(4), m.group(6));
            if (luhn(s) != Integer.parseInt(m.group(7))) {
                throw new Exception("Invalid orgnmr. Control number is invalid.");
            }
        }

        public static boolean valid(String data, boolean verbose) {
            try {
                new Organisationsnummer(data);
                return true;
            } catch (Exception e) {
                System.out.println(data + " is invalid.");
                e.printStackTrace();
                System.out.println();
                return false;
            }
        }
    }
}
