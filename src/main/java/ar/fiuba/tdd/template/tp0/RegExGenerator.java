package ar.fiuba.tdd.template.tp0;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegExGenerator {
    private int maxLength;
    private int indexRegEx;

    public RegExGenerator(int maxLength) {
        this.maxLength = maxLength;
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
        String token = "";
        char character = '[';
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

    private String getTokenWithOutQuantifier(String regEx) throws BadFormatException {
        char character = regEx.charAt(this.indexRegEx++);
        int regexLength = regEx.length();

        String token;
        if ( character == '[' ) {
            token = "" + character + getSetTokenFromToken(regEx);
        } else if ( character == '\\' ) {
            // escaped literal
            if ( this.indexRegEx < regexLength ) {
                token = "" + character + regEx.charAt(this.indexRegEx++);
            } else {
                // There is no escaped literal
                throw new BadFormatException();
            }
        } else {
            if ( !isValidAsLiteral(character) ) {
                throw new BadFormatException();
            }
            token = "" + character;
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

    private int getRandomNumberFromOperation(char operation) {
        Random rand = new Random();
        int randomNumber = 0;
        if ( operation == '+' ) {
            randomNumber = rand.nextInt(this.maxLength) + 1;
        } else if ( operation == '?' ) {
            randomNumber = rand.nextInt(2);
        } else if ( operation == '*' ) {
            // Operation *
            randomNumber = rand.nextInt(this.maxLength + 1);
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
        Random rand = new Random();
        char character = token.charAt(0);
        char charToConcat;
        for (int i = 0 ; i < getStringLengthToGenerate(token) ; i ++ ) {
            if ( character == '\\' ) {
                charToConcat = token.charAt(1);
            } else if ( character == '.' ) {
                charToConcat = (char) rand.nextInt(256);
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
        Random rand = new Random();
        String output = "";
        for (int i = 0 ; i < getStringLengthToGenerate(token) ; i ++ ) {
            int randomIndex = rand.nextInt(chars.size());
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