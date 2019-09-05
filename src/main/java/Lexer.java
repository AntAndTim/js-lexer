import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import token.Token;
import token.TokenType;
import token.type.Identifier;
import token.type.Keyword;
import token.type.Operator;
import token.type.Delimiter;

import static token.type.Delimiter.NEWLINE;
import static token.type.Delimiter.WHITESPACE;

public class Lexer {

    private static final Map<String, TokenType> DELIMITER_ELEMENTS = Stream
            .of(Delimiter.values())
            .collect(Collectors.toMap(anEnum -> anEnum.getValue(), anEnum -> anEnum));

    private static final Map<String, TokenType> LANGUAGE_ELEMENTS = Stream
        .of(Keyword.values(), Operator.values())
        .flatMap(Arrays::stream)
        .collect(Collectors.toMap(anEnum -> anEnum.getValue(), anEnum -> anEnum));


    private final InputStream jsFileStream;
    private int currentPosition = 1;
    private int currentLine = 1;
    private boolean startFromNextLine;
    private boolean nextCharIsLast = false;

    public Lexer(InputStream jsFileStream) {
        this.jsFileStream = jsFileStream;
    }

    ArrayList<Token> getToken() throws IOException {
        char symbol = readSymbol();
        String value = symbol == '\n' ? "" : getValue(symbol);
        ArrayList<Token> result = new ArrayList<Token>();
        while (!checkValue(value)) {
            if (DELIMITER_ELEMENTS.get(String.valueOf(symbol)) != null && !value.equals("")) {
                if (DELIMITER_ELEMENTS.get(String.valueOf(symbol)) == WHITESPACE) {
                    result.add(new Token(Identifier.IDENTIFIER, value, currentPosition - value.length() - 1, currentLine));
                }
                else {
                    result.add(new Token(Identifier.IDENTIFIER, value.substring(0, value.length() - 1), currentPosition - value.length() - 1, currentLine));
                }
                result.add(new Token(DELIMITER_ELEMENTS.get(String.valueOf(symbol)), String.valueOf(symbol), currentPosition - value.length() - 1, currentLine));
                return result;
            }
            else if (value.equals("")) {
                result.add(new Token(symbol == '\n' ? NEWLINE : WHITESPACE, symbol == '\n'? "\\n":" ",currentPosition - value.length() - 1, currentLine));
                return result;
            }

            symbol = readSymbol();
            value += symbol == '\n' ? "" : getValue(symbol);
            if (symbol == Character.MIN_VALUE) {
                return null;
            }
        }
        result.add(new Token(LANGUAGE_ELEMENTS.get(value), value, currentPosition - value.length(), currentLine));
        return result;
    }

    private String getValue(char symbol) {
        return symbol == ' ' ? "" : String.valueOf(symbol);
    }

    private boolean checkValue(String value) {
        return LANGUAGE_ELEMENTS.keySet().contains(value);
    }

    private char readSymbol() throws IOException {
        if (nextCharIsLast) {
            return Character.MIN_VALUE;
        }
        if (startFromNextLine) {
            currentLine++;
            currentPosition = 1;
            startFromNextLine = false;
        }
        currentPosition++;

        char nextChar = (char) jsFileStream.read();
        if (jsFileStream.available() == 0) {
            nextCharIsLast = true;
        }
        if (nextChar == '\n') {
            startFromNextLine = true;
            return '\n';
        }
        return nextChar;
    }
}
