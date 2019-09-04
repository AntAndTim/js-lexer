import java.io.IOException;

public class EntryPoint {

    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(EntryPoint.class.getClassLoader().getResourceAsStream("testFile.js"));
        System.out.println(lexer.getToken());
        System.out.println(lexer.getToken());
        System.out.println(lexer.getToken());
    }
}