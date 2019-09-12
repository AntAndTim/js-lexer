import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import token.Token;
import token.TokenType;
import token.type.Delimiter;
import token.type.Identifier;
import token.type.Keyword;
import token.type.NumberLiteral;
import token.type.Operator;
import token.type.Unknown;

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
    private String lastUnhandled = null;

    public Lexer(InputStream jsFileStream) {
        this.jsFileStream = jsFileStream;
    }

    Token getToken() throws IOException {
        String value;

        if (lastUnhandled != null) {
            value = lastUnhandled;
            lastUnhandled = null;
        } else {
            char symbol = readSymbol();

            if (symbol == EOF) {
                throw new EOFException("All the possible tokens already obtained");
            }

            value = String.valueOf(symbol);
        }

        if (!checkValue(value) && !value.matches("[a-z]|[A-Z]|\\$|_|[0-9]|[а-я]")) {
            return getUnknown(value);
        }

        boolean potentialIdentifier = value.matches("[a-z]|[A-Z]|\\$|_");
        boolean numberLiteral = value.matches("[0-9]");

        while (true) {
            char symbol = readSymbol();

            if (symbol == EOF) {
                if (potentialIdentifier) {
                    return getIdentifier(value);
                } else {
                    if (numberLiteral) {
                        return getNumberLiteral(value);
                    }
                    return getElement(value);
                }
            }

            String newSymbolValue = String.valueOf(symbol);

            if (potentialIdentifier) {
                if (!newSymbolValue.matches("[a-z]|[A-Z]|\\$|_|[0-9]|[а-я]")) {
                    lastUnhandled = newSymbolValue;
                    return checkValue(value) ? getElement(value) : getIdentifier(value);
                }
            } else if (checkValue(value) && !checkValue(value + newSymbolValue)) {
                lastUnhandled = newSymbolValue;
                return getElement(value);
            }

            if (numberLiteral && !newSymbolValue.matches("[0-9]")) {
                lastUnhandled = newSymbolValue;
                return getNumberLiteral(value);
            }

            if (!checkValue(value) && !newSymbolValue.matches("[a-z]|[A-Z]|\\$|_|[0-9]|[а-я]")) {
                lastUnhandled = newSymbolValue;
                return getUnknown(value);
            }

            value += newSymbolValue;
        }
    }

    private Token getNumberLiteral(String value) {
        return new Token(NumberLiteral.NUMBER_LITERAL, value, currentPosition - value.length() - 1, currentLine);
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

    private Token getUnknown(String value) {
        return new Token(Unknown.UNKNOWN,
                         value.equals("\n") ? "\\n" : value,
                         currentPosition - value.length(),
                         currentLine);
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
