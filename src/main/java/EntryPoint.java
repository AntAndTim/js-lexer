import token.Token;

import java.io.IOException;
import java.util.ArrayList;

public class EntryPoint {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(EntryPoint.class.getClassLoader().getResourceAsStream("testFile.js"));
        ArrayList<Token> result = lexer.getToken();
        while (result != null ) {
            for (Token aResult : result) {
                System.out.println(aResult);
            }
            result = lexer.getToken();
        }
    }
}