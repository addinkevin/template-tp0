package ar.fiuba.tdd.template.tp0;

import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegExGeneratorTest {

    private int maxLength = 10;

    private boolean validate(String regEx, int numberOfResults) {
        RegExGenerator generator = new RegExGenerator(this.maxLength);
        List<String> results;
        try {
            results = generator.generate(regEx, numberOfResults);
        } catch (BadFormatException badFormatException ) {
            return false;
        }
        // force matching the beginning and the end of the strings
        Pattern pattern = Pattern.compile("^" + regEx + "$", Pattern.DOTALL);
        return results
                .stream()
                .reduce(true,
                    (acc, item) -> {
                        Matcher matcher = pattern.matcher(item);
                        return acc && matcher.find();
                    },
                    (item1, item2) -> item1 && item2);
    }


    @Test
    public void testAnyCharacter() {
        assertTrue(validate(".", 1));
    }

    @Test
    public void testMultipleCharacters() {
        assertTrue(validate("...", 1));
    }

    @Test
    public void testLiteral() {
        assertTrue(validate("\\@", 1));
    }

    @Test
    public void testLiteralDotCharacter() {
        assertTrue(validate("\\@..", 1));
    }

    @Test
    public void testZeroOrOneCharacter() {
        assertTrue(validate("\\@.h?", 1));
    }

    @Test
    public void testCharacterSet() {
        assertTrue(validate("[abc]", 1));
    }

    @Test
    public void testCharacterSetWithQuantifiers() {
        assertTrue(validate("[abc]+", 1));
    }

    @Test
    public void testGeneral() {
        assertTrue(validate(".*", 10000));
        assertTrue(validate(".+", 100));
        assertTrue(validate("\\.", 100));
        assertTrue(validate("\\..", 100));
        assertTrue(validate("\\.*", 100));
        assertTrue(validate("hola", 100));
        assertTrue(validate("ho.a", 100));
        assertTrue(validate("hol?a", 100));
        assertTrue(validate("hol*a", 100));
        assertTrue(validate("", 100));
        assertTrue(validate("a", 100));
        assertTrue(validate("[a\\[\\]]", 100));
        assertTrue(validate("a.d?h*[abc]+", 100));

        assertTrue(validate("[a\\?]", 100));

        assertFalse(validate("abc++", 100));
        assertFalse(validate("[aa[b]", 100));
        assertFalse(validate("[a.]", 100));
        assertFalse(validate("[a?]", 100));
    }

}
