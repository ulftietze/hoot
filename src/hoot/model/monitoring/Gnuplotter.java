package hoot.model.monitoring;

import hoot.model.entities.History;
import hoot.system.Filesystem.FileHandler;
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
    private static final String tmpFile     = "tmp.txt";
    private static       int    graphsSaved = 0;

    public static synchronized String createStatisticsGraph(ArrayList<History> input)
    {
        if (input == null) {
            return null;
        }

        final String tmpPath = "tmp";

        ArrayList<String> data        = Gnuplotter.parseInputToData(input);
        FileHandler       fileHandler = (FileHandler) ObjectManager.create(FileHandler.class);
        String            filepath    = fileHandler.save(data, tmpPath, tmpFile);

        ProcessBuilder getPlotResultCurrent = (ProcessBuilder) ObjectManager.create(ProcessBuilder.class);

        getPlotResultCurrent.command("/bin/bash", "-c", Gnuplotter.getGnuplotCallCurrent(filepath));

        try {
            String b64ResultCurrent = Gnuplotter.getOutputStringFromProcess(getPlotResultCurrent.start());

            return Gnuplotter.saveBase64Image(b64ResultCurrent);
        } catch (IOException e) {
            Gnuplotter.log("Could not create Historie-Graph: " + e.getMessage());
            return null;
        } finally {
            fileHandler.delete(tmpPath, tmpFile);
        }
    }

    private static String getOutputStringFromProcess(Process process) throws IOException
    {
        BufferedReader br  = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = (StringBuilder) ObjectManager.create(StringBuilder.class);
        output.append("data:image/png;base64,");

        String tmp;
        while ((tmp = br.readLine()) != null) {
            output.append(tmp);
        }

        return output.toString();
    }

    private static ArrayList<String> parseInputToData(ArrayList<History> input)
    {
        ArrayList<String> result = new ArrayList<>();

        for (History historie : input) {
            String line = "";

            line += (historie.timestamp.toString());
            line += ("\t");
            line += (historie.loginsPerSixHours.toString());
            line += ("\t");
            line += (historie.registrationsPerSixHours.toString());
            line += ("\t");
            line += (historie.postsPerMinute.toString());

            result.add(line);
        }

        return result;
    }

    private static String getGnuplotCallCurrent(String dataPath)
    {
        StringBuilder plotCallCurrent = (StringBuilder) ObjectManager.create(StringBuilder.class);

        plotCallCurrent.append("gnuplot -e '");
        plotCallCurrent.append("set terminal png size 1024,576; ");
        plotCallCurrent.append("set xdata time; ");
        plotCallCurrent.append("set timefmt \"%Y-%m-%dT%H:%M:%S\"; ");

        plotCallCurrent.append("plot \"");
        plotCallCurrent.append(dataPath);
        plotCallCurrent.append("\" u 1:2 ");
        plotCallCurrent.append("t \"loginsPerSixHours\" ");
        plotCallCurrent.append("w lines, ");

        plotCallCurrent.append("\"");
        plotCallCurrent.append(dataPath);
        plotCallCurrent.append("\" u 1:3 ");
        plotCallCurrent.append("t \"registrationsPerSixHours\" ");
        plotCallCurrent.append("w lines, ");

        plotCallCurrent.append("\"");
        plotCallCurrent.append(dataPath);
        plotCallCurrent.append("\" u 1:4 ");
        plotCallCurrent.append("t \"postsPerMinute\" ");
        plotCallCurrent.append("w lines");

        plotCallCurrent.append("'");
        plotCallCurrent.append(" | base64");

        return plotCallCurrent.toString();
    }

    private static String getGnuplotCallPerSec(String dataPath)
    {
        StringBuilder plotCallPerSec = (StringBuilder) ObjectManager.create(StringBuilder.class);

        plotCallPerSec.append("gnuplot -e '");
        plotCallPerSec.append("set terminal png size 1024,576; ");
        plotCallPerSec.append("set output " + MediaFileHandler.mediaPath + " ; ");
        plotCallPerSec.append("set xdata time; ");
        plotCallPerSec.append("set timefmt \"%Y-%m-%dT%H:%M:%S\"; ");

        plotCallPerSec.append("plot \"");
        plotCallPerSec.append(dataPath);
        plotCallPerSec.append("\" u 1:4 ");
        plotCallPerSec.append("t \"postsPerSecond\" ");
        plotCallPerSec.append("w lines, ");

        plotCallPerSec.append("\"");
        plotCallPerSec.append(dataPath);
        plotCallPerSec.append("\" u 1:5 ");
        plotCallPerSec.append("t \"requestsPerSecond\" ");
        plotCallPerSec.append("w lines, ");

        plotCallPerSec.append("\"");
        plotCallPerSec.append(dataPath);
        plotCallPerSec.append("\" u 1:6 ");
        plotCallPerSec.append("t \"loginsPerSecond\" ");
        plotCallPerSec.append("w lines");

        plotCallPerSec.append("'");

        return plotCallPerSec.toString();
    }

    private static String saveBase64Image(String base64result)
    {
        MediaFileHandler fileHandler = (MediaFileHandler) ObjectManager.create(MediaFileHandler.class);

        fileHandler.saveBase64Image(graphsSaved + ".png", "monitoring" + File.separator, base64result);

        return fileHandler.getImageUrl("monitoring" + File.separator + graphsSaved++ + ".png");
    }

    protected static synchronized void log(String input)
    {
        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        logger.log(input);
    }
}
