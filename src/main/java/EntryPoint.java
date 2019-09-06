import java.io.EOFException;
import java.io.IOException;

public class EntryPoint {

    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(EntryPoint.class.getClassLoader().getResourceAsStream("testFile.js"));
        while (true) {
            try {
                System.out.println(lexer.getToken());
            } catch (EOFException e) {
                break;
            }
        }
    }
}