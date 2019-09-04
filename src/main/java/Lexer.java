import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import token.Token;
import token.TokenType;
import token.type.Keyword;
import token.type.Operator;

public class Lexer {

    private static final Map<String, TokenType> LANGUAGE_ELEMENTS = Stream
        .of(Keyword.values(), Operator.values())
        .flatMap(Arrays::stream)
        .collect(Collectors.toMap(anEnum -> anEnum.getValue(), anEnum -> anEnum));

    private final InputStream jsFileStream;
    private int currentPosition = 1;
    private int currentLine = 1;
    private boolean startFromNextLine;

    public Lexer(InputStream jsFileStream) {
        this.jsFileStream = jsFileStream;
    }

    Token getToken() throws IOException {
        char symbol = readSymbol();
        String value = getValue(symbol);
        while (!checkValue(value)) {
            symbol = readSymbol();
            value += getValue(symbol);
        }
        return new Token(LANGUAGE_ELEMENTS.get(value), value, currentPosition - value.length(), currentLine);
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
            return ' ';
        }
        return nextChar;
    }
}
