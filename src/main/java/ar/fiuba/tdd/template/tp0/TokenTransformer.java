package ar.fiuba.tdd.template.tp0;

import java.util.ArrayList;
import java.util.List;

class TokenTransformer {
    private int maxLength;
    private List<Integer> numbersToFilterForDotChar;

    TokenTransformer(int maxLength) {
        this.maxLength = maxLength;
        this.numbersToFilterForDotChar = new ArrayList<>();
    }

    void loadNumbersToFilterForDotChar(List<Integer> list) {
        this.numbersToFilterForDotChar = list;
    }

    private int getRandomNumberFromOperation(char operation) {
        int randomNumber = 0;
        if ( operation == '+' ) {
            randomNumber = Utility.getRandomBetween(1, this.maxLength);
        } else if ( operation == '?' ) {
            randomNumber = Utility.getRandomBetween(0,1);
        } else if ( operation == '*' ) {
            // Operation *
            randomNumber = Utility.getRandomBetween(0, this.maxLength);
        }

        return randomNumber;
    }

    private int getStringLengthToGenerate(String token) {
        char operation = token.charAt(token.length() - 1);
        int stringLengthToGenerate;
        if ( Utility.isQuantifier(operation) ) {
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
                charToConcat = (char) Utility.getRandomBetweenWithOut(0, 255, this.numbersToFilterForDotChar);
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
            int randomIndex = Utility.getRandomBetween(0, chars.size() - 1);
            output = output.concat("" + chars.get(randomIndex));
        }
        return output;
    }

    String generateStringFromToken(String token) {
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

}
