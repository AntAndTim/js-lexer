import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import token.Token;
import token.TokenType;
import token.type.Delimiter;
import token.type.Identifier;
import token.type.Keyword;
import token.type.Operator;

public class Lexer {

    private static final Map<String, TokenType> LANGUAGE_ELEMENTS = Stream
        .of(Keyword.values(), Operator.values(), Delimiter.values())
        .flatMap(Arrays::stream)
        .collect(Collectors.toMap(anEnum -> anEnum.getValue(), anEnum -> anEnum));
    private static final char EOF = (char) -1;

    private final InputStream jsFileStream;
    private int currentPosition = 1;
    private int currentLine = 1;
    private boolean startFromNextLine;
    private Optional<Token> nextToken = Optional.empty();

    public Lexer(InputStream jsFileStream) {
        this.jsFileStream = jsFileStream;
    }

    Token getToken() throws IOException {
        if (nextToken.isPresent()) {
            Token token = nextToken.get();
            nextToken = Optional.empty();
            return token;
        }
        char symbol = readSymbol();
        String value = getValue(symbol);
        while (!checkValue(value)) {
            symbol = readSymbol();
            String newSymbolValue = getValue(symbol);
            if (checkValue(newSymbolValue) && !value.equals("")) {
                nextToken = Optional.of(getElement(newSymbolValue));
                return getIdentifier(value);
            }
            value += newSymbolValue;
            if (symbol == EOF) {
                throw new EOFException("All the possible tokens already obtained");
            }
        }
        return getElement(value);
    }

    private Token getIdentifier(String value) {
        return new Token(Identifier.IDENTIFIER, value, currentPosition - value.length() - 1, currentLine);
    }

    private Token getElement(String value) {
        return new Token(LANGUAGE_ELEMENTS.get(value),
                         value.equals("\n") ? "\\n" : value,
                         currentPosition - value.length(),
                         currentLine);
    }

    private String getValue(char symbol) {
        return symbol == ' ' ? "" : String.valueOf(symbol);
    }

    private boolean checkValue(String value) {
        return LANGUAGE_ELEMENTS.keySet().contains(value);
    }

    private char readSymbol() throws IOException {
        if (startFromNextLine) {
            currentLine++;
            currentPosition = 1;
            startFromNextLine = false;
        }
        currentPosition++;

        char nextChar = (char) jsFileStream.read();
        if (nextChar == '\n') {
            startFromNextLine = true;
        }
        return nextChar;
    }
}
