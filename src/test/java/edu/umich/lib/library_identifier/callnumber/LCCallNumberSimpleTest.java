package edu.umich.lib.library_identifier.callnumber;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class LCCallNumberSimpleTest {

    @ParameterizedTest
    @CsvFileSource(files = "src/test/java/edu/umich/lib/library_identifier/callnumber/lc_collation_pairs.tsv", delimiterString = "->")
    void collation_key(String original, String collation) {
        LCCallNumberSimple lccs = new LCCallNumberSimple(original);
        String key = lccs.collation_key();
        if (key == null) key = "null";
        assert(key).equals(collation.toString());
    }
}