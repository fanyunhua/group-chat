
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    static Logger logger=Logger.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) {
        logger.setLevel(Level.INFO);
        new Server().start();
    }
}
