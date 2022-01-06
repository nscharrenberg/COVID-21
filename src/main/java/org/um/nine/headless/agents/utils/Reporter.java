package org.um.nine.headless.agents.utils;

import java.io.FileWriter;
import java.io.IOException;

import static org.um.nine.headless.game.Settings.DEFAULT_REPORTER;

public class Reporter extends Logger {

    public static void report() {
        DEFAULT_REPORTER.report(false);
    }

    public static void report(String filePath) {
        DEFAULT_REPORTER.report(filePath, false);
    }


    public void report(String filePath, boolean clear) {
        try {
            FileWriter fw = new FileWriter(Reporter.class.getClassLoader().getResource(filePath).getFile());
            for (String s : this.getLog()) fw.append(s).append("\n");
            fw.close();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        if (clear) this.clear();
    }

    public void report(boolean clear) {
        this.report("reports/report.txt", clear);

    }

}
