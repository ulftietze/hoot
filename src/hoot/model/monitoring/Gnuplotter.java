package hoot.model.monitoring;

import hoot.model.entities.Historie;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Gnuplotter
{
    public static synchronized String createPNGUrlFromHistories(ArrayList<Historie> input)
    {
        // Cannot use ObjectManager here: Instantiation will fail
        ProcessBuilder processBuilder = new ProcessBuilder();

        ArrayList<String> commands = new ArrayList<>();
        commands.add("/bin/bash");
        commands.add("-c");
        commands.add(Gnuplotter.getGnuplotCall(input));

        processBuilder.command(commands);

        String base64result;

        try {
            Process process = processBuilder.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder base64String = (StringBuilder) ObjectManager.create(StringBuilder.class);
            base64String.append("data:image/png;base64,");

            String tmp;
            while ((tmp = br.readLine()) != null) {
                base64String.append(tmp);
            }

            base64result = base64String.toString();
        } catch (IOException e) {
            Gnuplotter.log("Could not create Historie-Graph: " + e.getMessage());
            return null;
        }

        return Gnuplotter.saveBase64Image(base64result);
    }

    private static String getDataFromInput(ArrayList<Historie> input)
    {
        StringBuilder inputs = (StringBuilder) ObjectManager.create(StringBuilder.class);

        for (Historie historie : input) {
            inputs.append(historie.timestamp.toString());
            inputs.append("\t");
            inputs.append(historie.currentLoggedIn);
            inputs.append("\t");
            inputs.append(historie.currentlyRegisteredUsers);
            inputs.append("\t");
            inputs.append(historie.postsPerSecond);
            inputs.append("\t");
            inputs.append(historie.requestsPerSecond);
            inputs.append("\t");
            inputs.append(historie.loginsPerSecond);
            inputs.append(System.lineSeparator());
        }

        return inputs.toString();
    }

    private static String getGnuplotCall(ArrayList<Historie> input)
    {
        String data = Gnuplotter.getDataFromInput(input);

        StringBuilder gnuplotCall = (StringBuilder) ObjectManager.create(StringBuilder.class);

        gnuplotCall.append("echo \"");
        gnuplotCall.append(data);
        gnuplotCall.append("\" > tmp.txt;");
        gnuplotCall.append("gnuplot -e '");
        gnuplotCall.append("set terminal png size 1024,576; ");
        gnuplotCall.append("set xdata time; ");
        gnuplotCall.append("set timefmt \"%Y-%m-%dT%H:%M:%S\"; ");

        gnuplotCall.append("plot \"tmp.txt\" u 1:2 ");
        gnuplotCall.append("t \"currentLoggedIn\" ");
        gnuplotCall.append("w lines, ");

        gnuplotCall.append("\"tmp.txt\" u 1:3 ");
        gnuplotCall.append("t \"currentlyRegisteredUsers\" ");
        gnuplotCall.append("w lines, ");

        gnuplotCall.append("\"tmp.txt\" u 1:4 ");
        gnuplotCall.append("t \"postsPerSecond\" ");
        gnuplotCall.append("w lines, ");

        gnuplotCall.append("\"tmp.txt\" u 1:5 ");
        gnuplotCall.append("t \"requestsPerSecond\" ");
        gnuplotCall.append("w lines, ");

        gnuplotCall.append("\"tmp.txt\" u 1:6 ");
        gnuplotCall.append("t \"loginsPerSecond\" ");
        gnuplotCall.append("w lines");

        gnuplotCall.append("'");
        gnuplotCall.append("| base64;");
        gnuplotCall.append("rm tmp.txt");

        return gnuplotCall.toString();
    }

    private static String saveBase64Image(String base64result)
    {
        MediaFileHandler fileHandler = (MediaFileHandler) ObjectManager.create(MediaFileHandler.class);

        fileHandler.saveMedia("graph.png", "monitoring" + File.separator, base64result);

        return fileHandler.getImageUrl("monitoring" + File.separator + "graph.png");
    }

    protected static synchronized void log(String input)
    {
        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        logger.log(input);
    }
}
