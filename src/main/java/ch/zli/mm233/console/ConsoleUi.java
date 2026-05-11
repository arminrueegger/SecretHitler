package ch.zli.mm233.console;

import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleUi {

    private final Scanner in;
    private final PrintStream out;

    public ConsoleUi(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public void println(String line) {
        out.println(line);
    }

    public void blank() {
        out.println();
    }

    public String promptLine(String prompt) {
        out.print(prompt + ": ");
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    public int promptInt(String prompt, int min, int max) {
        while (true) {
            out.print(prompt + " [" + min + ".." + max + "]: ");
            String line = in.hasNextLine() ? in.nextLine().trim() : "";
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
            }
            out.println("  invalid, try again");
        }
    }

    public boolean promptYesNo(String prompt) {
        while (true) {
            out.print(prompt + " [y/n]: ");
            String line = in.hasNextLine() ? in.nextLine().trim().toLowerCase() : "";
            if (line.equals("y") || line.equals("yes") || line.equals("ja")) {
                return true;
            }
            if (line.equals("n") || line.equals("no") || line.equals("nein")) {
                return false;
            }
            out.println("  invalid, try again");
        }
    }
}
