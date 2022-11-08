import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length != 1)
        {
            System.out.println("Hiba: Adj meg egy eleresi utat!");
            System.exit(1);
        }
        
        File f = new File(args[0]);

        if(!f.exists() || !f.isDirectory())
        {
            System.out.println("Hiba: Adj meg egy ervenyes mappat!");
            System.exit(2);
        }

        HtmlManip htmanip = new HtmlManip(f);

        htmanip.start();
    }
}