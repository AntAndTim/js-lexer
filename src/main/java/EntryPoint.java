import token.Token;
import token.type.Delimiter;

import java.io.EOFException;
import java.io.IOException;

public class EntryPoint {

    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(EntryPoint.class.getClassLoader().getResourceAsStream("test4.js"));
        while (true) {
            try {
                Token token = lexer.getToken();

                if (token.getType() != Delimiter.WHITESPACE) {
                    System.out.println(token);
                }
            } catch (EOFException e) {
                break;
            }
        }
    }
}