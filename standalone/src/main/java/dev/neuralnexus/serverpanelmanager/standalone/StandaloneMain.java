package dev.neuralnexus.serverpanelmanager.standalone;

import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.Utils;

import java.awt.GraphicsEnvironment;
import java.io.Console;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class StandaloneMain {
    public static void main (String [] args) {
        Console console = System.console();

        // Check if console is null and if the system is headless
        if (console == null && !GraphicsEnvironment.isHeadless()) {
            // Kill the application -- There be no GUI
            System.err.println("No console.");
            System.exit(1);

            // Normal execution
        } else if (console != null) {
            // Logger
            Logger logger = Logger.getLogger("serverpanelmanager");
            logger.setLevel(Level.FINE);

            // Log formatter
            logger.setUseParentHandlers(false);
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                            new Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getMessage()
                    );
                }
            });
            logger.addHandler(handler);

            // Start Server Panel Manager
            ServerPanelManager.start("./");

            // Start terminal application
            while (true) {
                String[] input = console.readLine().split(" ");

                if (input[0].equals("exit") || input[0].equals("quit") || input[0].equals("stop") || input[0].equals("end")) {
                    break;
                } else {
                    Utils.runTaskAsync(() -> {
                        String response = Utils.ansiiParser(ServerPanelManager.commandHandler.commandMessenger(input));
                        if (response.equals("")) {
                            response = "Use 'help' to see a list of commands.";
                        }

                        logger.info(response);
                    });
                }
            }
        }
    }
}