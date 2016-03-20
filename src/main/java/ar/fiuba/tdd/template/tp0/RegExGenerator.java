package ar.fiuba.tdd.template.tp0;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegExGenerator {
    private int maxLength;
    private int indexRegEx;
    private List<Integer> numbersToFilterForDotChar;

    public RegExGenerator(int maxLength) {
        this.maxLength = maxLength;
        this.loadNumbersToFilterForDotChar();
    }

    private void loadNumbersToFilterForDotChar() {
        this.numbersToFilterForDotChar = new ArrayList<>();
        // Control characters(new lines) added to the filter because a error from Pattern
        this.numbersToFilterForDotChar.add(10);
        this.numbersToFilterForDotChar.add(13);
        this.numbersToFilterForDotChar.add(133);
    }

    private boolean isQuantifier(char character) {
        if ( character == '*' || character == '?' || character == '+' ) {
            return true;
        }
        return false;
    }

    private boolean isReservedCharacter(char character) {
        String reservedCharacters = ".*+?[]";
        if (reservedCharacters.contains("" + character)) {
            return true;
        }
        return false;
    }

    private boolean isValidAsLiteral(char character) {
        if ( isReservedCharacter(character) && character != '.' ) {
            return false;
        }
        return true;
    }

    private String getSetTokenFromToken(String regEx) throws BadFormatException {
        int regexLength = regEx.length();
        char character = regEx.charAt(this.indexRegEx++); // '[';
        String token = "" + character;
        while ( this.indexRegEx < regexLength ) {
            char lastChar = character;
            character = regEx.charAt(this.indexRegEx++);
            token = token.concat("" + character);
            if ( lastChar != '\\' && character == ']') {
                // Finished
                return token;
            }
            if ( lastChar != '\\' && isReservedCharacter(character) ) {
                throw new BadFormatException();
            }
        }
        // No ']' detected.
        throw new BadFormatException();
    }

    private boolean checkIfThereIsQuantifier(String regEx) {
        int regexLength = regEx.length();
        if ( this.indexRegEx < regexLength ) {
            char character = regEx.charAt(this.indexRegEx);
            if ( isQuantifier(character) ) {
                return true;
            }
        }
        return false;
    }

    private String getEscapedLiteralFromToken(String regEx) throws BadFormatException {
        int regexLength = regEx.length();
        char character = regEx.charAt(this.indexRegEx++); // character = \
        if ( this.indexRegEx < regexLength ) {
            return "" + character + regEx.charAt(this.indexRegEx++);
        } else {
            // There is no escaped literal
            throw new BadFormatException();
        }
    }

    private String getLiteralFromToken(String regEx) throws BadFormatException {
        char character = regEx.charAt(this.indexRegEx++);
        if ( !isValidAsLiteral(character) ) {
            throw new BadFormatException();
        }
        return "" + character;
    }

    private String getTokenWithOutQuantifier(String regEx) throws BadFormatException {
        char character = regEx.charAt(this.indexRegEx);
        String token;
        if ( character == '[' ) {
            token = getSetTokenFromToken(regEx);
        } else if ( character == '\\' ) {
            token = getEscapedLiteralFromToken(regEx);
        } else {
            token = getLiteralFromToken(regEx);
        }
        return token;
    }

    private String getToken(String regEx) throws BadFormatException {
        String token = getTokenWithOutQuantifier(regEx);

        if ( checkIfThereIsQuantifier(regEx) ) {
            token = token.concat("" + regEx.charAt(this.indexRegEx++));
        }

        return token;
    }

    private List<String> getTokens(String regEx) throws BadFormatException {
        List<String> tokens = new ArrayList<>();
        if (regEx.isEmpty()) {
            return tokens;
        }

        int regexLength = regEx.length();
        this.indexRegEx = 0;
        String token;
        while ( this.indexRegEx < regexLength ) {
            token = getToken(regEx);
            tokens.add(token);
        }
        return tokens;
    }

    private int getRandomBetween(int infInclusive, int supInclusive) {
        Random random = new Random();
        return infInclusive + random.nextInt(supInclusive - infInclusive + 1);
    }

    private int getRandomBetweenWithOut(int infInclusive, int supInclusive, List<Integer> numbersToFilter) {
        int random = getRandomBetween(infInclusive, supInclusive);
        while ( numbersToFilter.contains(random) ) {
            random = getRandomBetween(infInclusive, supInclusive);
        }
        return random;
    }

    private int getRandomNumberFromOperation(char operation) {
        int randomNumber = 0;
        if ( operation == '+' ) {
            randomNumber = getRandomBetween(1, this.maxLength);
        } else if ( operation == '?' ) {
            randomNumber = getRandomBetween(0,1);
        } else if ( operation == '*' ) {
            // Operation *
            randomNumber = getRandomBetween(0, this.maxLength);
        }

        return randomNumber;
    }

    private int getStringLengthToGenerate(String token) {
        char operation = token.charAt(token.length() - 1);
        int stringLengthToGenerate;
        if ( isQuantifier(operation) ) {
            stringLengthToGenerate = getRandomNumberFromOperation(operation);
        } else {
            stringLengthToGenerate = 1;
        }
        return stringLengthToGenerate;
    }

    private String generateStringFromLiteralToken(String token) {
        String output = "";
        char character = token.charAt(0);
        char charToConcat;
        for (int i = 0 ; i < getStringLengthToGenerate(token) ; i ++ ) {
            if ( character == '\\' ) {
                charToConcat = token.charAt(1);
            } else if ( character == '.' ) {
                charToConcat = (char) getRandomBetweenWithOut(0, 255, this.numbersToFilterForDotChar);
            } else {
                charToConcat = character;
            }

            output = output.concat("" + charToConcat);
        }
        return output;
    }

    private List<Character> getCharsFromSet(String token) {
        List<Character> list = new ArrayList<>();
        int index = 1;
        while ( index < token.length() ) {
            char character = token.charAt(index++);
            if ( character == '\\' ) {
                character = token.charAt(index++);
            } else if ( character == ']' ) {
                return list;
            }

            list.add(character);
        }

        return list;
    }

    private String generateStringFromSetToken(String token) {
        List<Character> chars = getCharsFromSet(token);
        if ( chars.isEmpty()) {
            return "";
        }
        String output = "";
        for (int i = 0 ; i < getStringLengthToGenerate(token) ; i ++ ) {
            int randomIndex = getRandomBetween(0, chars.size() - 1);
            output = output.concat("" + chars.get(randomIndex));
        }
        return output;
    }

    private String generateStringFromToken(String token) {
        if ( token.isEmpty() ) {
            return "";
        }
        char initialChar = token.charAt(0);
        if ( initialChar == '[') {
            return generateStringFromSetToken(token);
        } else {
            return generateStringFromLiteralToken(token);
        }
    }

    public List<String> generate(String regEx, int numberOfResults) throws BadFormatException {
        List<String> list = new ArrayList<>();
        List<String> tokens = getTokens(regEx);
        String result = "";
        for (int i = 0 ; i < numberOfResults; i++) {
            for (String token : tokens) {
                result = result.concat(generateStringFromToken(token));
            }
            list.add(result);
            result = "";
        }
        return list;
    }

}