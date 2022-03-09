package hoot.model.monitoring;

import hoot.model.entities.History;
import hoot.system.Filesystem.FileHandler;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

interface DataWriter
{
    ArrayList<String> writeToLines(ArrayList<History> input);
}

public class Gnuplotter
{
    private static final String                     tmpFolder   = "tmp";
    private static final String                     tmpPath     = tmpFolder + File.separator;
    private static final String                     graphFolder = "graphs";
    private static final HashMap<GraphType, String> urlMap      = new HashMap<>();

    public static synchronized String getGraphUrl(GraphType graphtype)
    {
        return urlMap.get(graphtype);
    }

    public static String createGraph(GraphType graphType, ArrayList<History> input)
    {
        switch (graphType) {
            case Statistics:
                return Gnuplotter.createStatisticsGraph(input);
            case Thread:
                return Gnuplotter.createThreadGraph(input);
            case Memory:
                return Gnuplotter.createMemoryGraph(input);
            case CPULoad:
                return Gnuplotter.createCPULoadGraph(input);
            case SystemLoad:
                return Gnuplotter.createSystemLoadGraph(input);
            case Requests:
                return Gnuplotter.createRequestsGraph(input);
            case CurrentLoggedIn:
                return Gnuplotter.createCurrentLoggedInGraph(input);
        }
        return null;
    }

    private static synchronized String createStatisticsGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.loginsPerSixHours.toString();
                line += "\t";
                line += history.registrationsPerSixHours.toString();
                line += "\t";
                line += history.postsPerMinute.toString();

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"loginsPerSixHours\" w lines, ",
                "u 1:3 t \"registrationsPerSixHours\" w lines, ",
                "u 1:4 t \"postsPerMinute\" w lines"
        };

        return Gnuplotter.createGraphWithGnuplot(GraphType.Statistics, input, dataWriter, gnuplotDirectives);
    }

    private static String createThreadGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.threadCount.toString();
                line += "\t";
                line += history.threadCountTotal.toString();

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"threadCount\" w lines, ",
                "u 1:3 t \"threadCountTotal\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.Thread, input, dataWriter, gnuplotDirectives);
    }

    private static String createMemoryGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.memoryMax.toString();
                line += "\t";
                line += history.memoryTotal.toString();
                line += "\t";
                line += history.memoryUsed.toString();
                line += "\t";
                line += history.memoryFree.toString();

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"memoryMax\" w lines, ",
                "u 1:3 t \"memoryTotal\" w lines, ",
                "u 1:4 t \"memoryUsed\" w lines, ",
                "u 1:5 t \"memoryFree\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.Memory, input, dataWriter, gnuplotDirectives);
    }

    private static String createCPULoadGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.systemCPULoad.toString();
                line += "\t";
                line += history.processCPULoad.toString();

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"systemCPULoad\" w lines, ",
                "u 1:3 t \"processCPULoad\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.CPULoad, input, dataWriter, gnuplotDirectives);
    }

    private static String createSystemLoadGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.systemLoadAverage.toString();

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"systemLoadAverage\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.SystemLoad, input, dataWriter, gnuplotDirectives);
    }

    private static String createRequestsGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.requestsPerSecond.toString();
                line += "\t";
                line += history.requestsLoggedInPerSecond.toString();

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"requestsPerSecond\" w lines, ",
                "u 1:3 t \"requestsLoggedInPerSecond\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.Requests, input, dataWriter, gnuplotDirectives);
    }

    private static String createCurrentLoggedInGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.currentLoggedIn.toString();

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"currentLoggedIn\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.CurrentLoggedIn, input, dataWriter, gnuplotDirectives);
    }

    private synchronized static String createGraphWithGnuplot(
            final GraphType graphType, final ArrayList<History> input, DataWriter dataWriter, String[] gnuplotDirectives
    )
    {
        if (input == null) {
            return null;
        }

        FileHandler      fileHandler      = (FileHandler) ObjectManager.create(FileHandler.class);
        ProcessBuilder   plotProcess      = (ProcessBuilder) ObjectManager.create(ProcessBuilder.class);
        StringBuilder    plotCall         = (StringBuilder) ObjectManager.create(StringBuilder.class);
        MediaFileHandler mediaFileHandler = (MediaFileHandler) ObjectManager.create(MediaFileHandler.class);

        final String      tmpDataFile        = graphType.toString() + ".txt";
        ArrayList<String> data               = dataWriter.writeToLines(input);
        final String      dataFileSystemPath = fileHandler.save(data, tmpFolder, tmpDataFile);
        final String graphFileSystemPath = mediaFileHandler.getMediaFilePath(
                graphFolder + File.separator + graphType + ".png");

        plotCall
                .append("gnuplot -e '")
                .append("set terminal png size 1024,576; ")
                .append("set output \"")
                .append(graphFileSystemPath)
                .append("\"; ")
                .append("set xdata time; ")
                .append("set timefmt \"%Y-%m-%dT%H:%M:%S\"; ");

        for (int i = 0; i < gnuplotDirectives.length; ++i) {
            if (i == 0) {
                plotCall.append("plot ");
            }
            plotCall.append("\"").append(dataFileSystemPath).append("\" ");
            plotCall.append(gnuplotDirectives[i]);
        }
        plotCall.append("'");

        plotProcess.command("/bin/bash", "-c", plotCall.toString());

        mediaFileHandler.createDir(graphFolder);

        try {
            plotProcess.start().waitFor();

            String url = mediaFileHandler.getImageUrl(graphFolder + "/" + graphType + ".png");

            Gnuplotter.urlMap.put(graphType, url);

            return url;
        } catch (IOException e) {
            Gnuplotter.log("Gnuplot Process execution failed: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Gnuplotter.log("Gnuplot Process failed to terminate: " + e.getMessage());
            return null;
        } finally {
            fileHandler.delete(tmpPath, tmpDataFile);
        }
    }

    private static synchronized void log(String input)
    {
        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        logger.log(input);
    }

    public enum GraphType
    {
        Statistics, Thread, Memory, CPULoad, SystemLoad, Requests, CurrentLoggedIn
    }
}
