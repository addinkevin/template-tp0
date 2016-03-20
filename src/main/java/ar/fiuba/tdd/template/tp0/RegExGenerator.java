package ar.fiuba.tdd.template.tp0;

import java.util.ArrayList;
import java.util.List;

public class RegExGenerator {
    private int maxLength;

    public RegExGenerator(int maxLength) {
        this.maxLength = maxLength;
    }

    private TokenTransformer createTokenTransformer() {
        List<Integer> numbersToFilterForDotChar = new ArrayList<>();
        // Control characters(new lines) added to the filter because a error from Pattern
        numbersToFilterForDotChar.add(10);
        numbersToFilterForDotChar.add(13);
        numbersToFilterForDotChar.add(133);

        TokenTransformer tokenTransformer = new TokenTransformer(this.maxLength);
        tokenTransformer.loadNumbersToFilterForDotChar(numbersToFilterForDotChar);
        return tokenTransformer;
    }

    private List<String> getTokens(String regEx) throws BadFormatException {
        RegExParser tokenizer = new RegExParser();
        List<String> tokens = tokenizer.getTokens(regEx);
        return tokens;
    }

    private String generateStringFromTokens(List<String> tokens) {
        TokenTransformer tokenTransformer = createTokenTransformer();
        String result = "";
        for (String token : tokens) {
            result = result.concat(tokenTransformer.generateStringFromToken(token));
        }

        return result;
    }

    public List<String> generate(String regEx, int numberOfResults) throws BadFormatException {
        List<String> list = new ArrayList<>();
        List<String> tokens = getTokens(regEx);
        for (int i = 0 ; i < numberOfResults; i++) {
            list.add(generateStringFromTokens(tokens));
        }
        return list;
    }

}