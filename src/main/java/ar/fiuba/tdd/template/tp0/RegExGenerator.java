package ar.fiuba.tdd.template.tp0;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

public class RegExGenerator {
    private int maxLength;
    private String acumString;

    public RegExGenerator(int maxLength) {
        this.maxLength = maxLength;
        this.acumString =  "";
    }

    private boolean isQuantifier(char character) {
        if ( character == '*' || character == '?' || character == '+' ) {
            return true;
        }
        return false;
    }

    private int getSetTokenFromToken(String regEx, int index) {
        char character = '[';
        int regexLength = regEx.length();
        while ( index < regexLength ) {
            char lastChar = character;
            character = regEx.charAt(index++);
            this.acumString = this.acumString.concat("" + character);
            if ( lastChar != '\\' && character == ']') {
                break;
            }
        }
        return index;
    }

    private boolean checkIfThereIsQuantifier(String regEx, int index) {
        int regexLength = regEx.length();
        if ( index < regexLength ) {
            char character = regEx.charAt(index);
            if ( isQuantifier(character) ) {
                return true;
            }
        }
        return false;
    }

    private int getTokenWithOutQuantifier(String regEx, int index) {
        char character = regEx.charAt(index++);
        int regexLength = regEx.length();

        if ( character == '[' ) {
            this.acumString = "" + character;
            index = getSetTokenFromToken(regEx, index);
        } else if ( character == '\\' ) {
            // escaped literal
            if ( index < regexLength ) {
                this.acumString = "" + character + regEx.charAt(index++);
            }
        } else {
            this.acumString = "" + character;
        }

        return index;
    }

    private int getToken(String regEx, int index) {

        index = getTokenWithOutQuantifier(regEx, index);

        if ( checkIfThereIsQuantifier(regEx,index) ) {
            this.acumString = this.acumString.concat("" + regEx.charAt(index++));
        }

        return index;
    }

    // Preconditions: regEx is a well formed regular expression
    private List<String> getTokens(String regEx) {
        List<String> tokens = new ArrayList<>();
        if (regEx.isEmpty()) {
            return tokens;
        }

        int regexLength = regEx.length();
        int index = 0;
        while ( index < regexLength ) {
            this.acumString = "";
            index = getToken(regEx, index);
            tokens.add(this.acumString);
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
        for (int i = 0 ; i < getStringLengthToGenerate(token) ; i ++ ) {
            if ( character == '\\' ) {
                output = output.concat("" + token.charAt(1));
            } else if ( character == '.' ) {
                output = output.concat("" + (char) rand.nextInt(256));
            } else {
                output = output.concat("" + character);
            }
        }
        return output;
    }

    private List<Character> getCharsFromSet(String token) {
        List<Character> list = new ArrayList<>();
        char character = token.charAt(0);
        int index = 1;
        while ( index < token.length() ) {
            char lastChar = character;
            character = token.charAt(index++);
            if ( lastChar != '\\' && character == ']' ) {
                return list;
            }

            if ( character == '\\' ) {
                character = token.charAt(index++);
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


    public List<String> generate(String regEx, int numberOfResults) {
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