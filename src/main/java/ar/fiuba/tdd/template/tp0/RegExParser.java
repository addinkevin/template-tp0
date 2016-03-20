package ar.fiuba.tdd.template.tp0;

import java.util.ArrayList;
import java.util.List;

class RegExParser {
    private int indexRegEx;

    private boolean isReservedCharacter(char character) {
        String reservedCharacters = ".*+?[]";
        return reservedCharacters.contains("" + character);
    }

    private boolean isValidAsLiteral(char character) {
        if ( character == '.' ) {
            return true;
        }
        return !isReservedCharacter(character);
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
            return Utility.isQuantifier(character);
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

    List<String> getTokens(String regEx) throws BadFormatException {
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
}
