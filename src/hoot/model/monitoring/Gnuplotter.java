package hoot.model.monitoring;

import hoot.model.entities.History;
import hoot.model.monitoring.consumer.RequestDurationCollector;
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

    public static String getGraphUrl(GraphType graphtype)
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
            case HeapMemory:
                return Gnuplotter.createHeapMemoryGraph(input);
            case Memory:
                return Gnuplotter.createMemoryGraph(input);
            case CacheSize:
                return Gnuplotter.createCacheSizeGraph(input);
            case CPULoad:
                return Gnuplotter.createCPULoadGraph(input);
            case SystemLoad:
                return Gnuplotter.createSystemLoadGraph(input);
            case Requests:
                return Gnuplotter.createRequestsGraph(input);
            case RequestDuration:
                return Gnuplotter.createRequestDurationGraph(input);
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

    private static synchronized String createThreadGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.threadCount.toString();

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {"u 1:2 t \"threadCount\" w lines"};

        return Gnuplotter.createGraphWithGnuplot(GraphType.Thread, input, dataWriter, gnuplotDirectives);
    }

    private static synchronized String createHeapMemoryGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_HEAP_MAX);
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_HEAP_TOTAL);
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_HEAP_FREE);

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"" + SystemWorkloadCollector.MEMORY_HEAP_MAX + "\" w lines, ",
                "u 1:3 t \"" + SystemWorkloadCollector.MEMORY_HEAP_TOTAL + "\" w lines, ",
                "u 1:4 t \"" + SystemWorkloadCollector.MEMORY_HEAP_FREE + "\" w lines, ",
                "u 1:5 t \"" + SystemWorkloadCollector.MEMORY_HEAP_USAGE + "\" w lines, ",
                "u 1:6 t \"" + SystemWorkloadCollector.MEMORY_NON_HEAP_USAGE + "\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.HeapMemory, input, dataWriter, gnuplotDirectives);
    }

    private static synchronized String createMemoryGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();
            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_CLASS_COMMITTED);
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_CLASS_RESERVED);
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_THREAD_COMMITTED);
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_THREAD_RESERVED);
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_CODE_COMMITTED);
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_CODE_RESERVED);
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_GC_COMMITTED);
                line += "\t";
                line += history.workload.get(SystemWorkloadCollector.MEMORY_GC_RESERVED);

                result.add(line);
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"" + SystemWorkloadCollector.MEMORY_CLASS_COMMITTED + "\" w lines, ",
                "u 1:3 t \"" + SystemWorkloadCollector.MEMORY_CLASS_RESERVED + "\" w lines, ",
                "u 1:4 t \"" + SystemWorkloadCollector.MEMORY_THREAD_COMMITTED + "\" w lines, ",
                "u 1:5 t \"" + SystemWorkloadCollector.MEMORY_THREAD_RESERVED + "\" w lines, ",
                "u 1:6 t \"" + SystemWorkloadCollector.MEMORY_CODE_COMMITTED + "\" w lines, ",
                "u 1:7 t \"" + SystemWorkloadCollector.MEMORY_CODE_RESERVED + "\" w lines, ",
                "u 1:8 t \"" + SystemWorkloadCollector.MEMORY_GC_COMMITTED + "\" w lines, ",
                "u 1:9 t \"" + SystemWorkloadCollector.MEMORY_GC_RESERVED + "\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.Memory, input, dataWriter, gnuplotDirectives);
    }

    private static synchronized String createCPULoadGraph(ArrayList<History> input)
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

        String[] gnuplotDirectives = {"u 1:2 t \"systemCPULoad\" w lines, ", "u 1:3 t \"processCPULoad\" w lines"};

        return Gnuplotter.createGraphWithGnuplot(GraphType.CPULoad, input, dataWriter, gnuplotDirectives);
    }

    private static synchronized String createSystemLoadGraph(ArrayList<History> input)
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

        String[] gnuplotDirectives = {"u 1:2 t \"systemLoadAverage\" w lines"};

        return Gnuplotter.createGraphWithGnuplot(GraphType.SystemLoad, input, dataWriter, gnuplotDirectives);
    }

    private static synchronized String createRequestsGraph(ArrayList<History> input)
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
                "u 1:2 t \"requestsPerSecond\" w lines, ", "u 1:3 t \"requestsLoggedInPerSecond\" w lines"
        };

        return Gnuplotter.createGraphWithGnuplot(GraphType.Requests, input, dataWriter, gnuplotDirectives);
    }

    private static synchronized String createRequestDurationGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();

            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.requestDurations.get(RequestDurationCollector.ALL);
                line += "\t";
                line += history.requestDurations.get(RequestDurationCollector.GET);
                line += "\t";
                line += history.requestDurations.get(RequestDurationCollector.PUT);
                line += "\t";
                line += history.requestDurations.get(RequestDurationCollector.POST);
                line += "\t";
                line += history.requestDurations.get(RequestDurationCollector.DELETE);
                line += "\t";
                line += history.requestDurations.get(RequestDurationCollector.OPTION);

                result.add(line.toString());
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"" + RequestDurationCollector.ALL + "\" w lines, ",
                "u 1:3 t \"" + RequestDurationCollector.GET + "\" w lines, ",
                "u 1:4 t \"" + RequestDurationCollector.PUT + "\" w lines, ",
                "u 1:5 t \"" + RequestDurationCollector.POST + "\" w lines, ",
                "u 1:6 t \"" + RequestDurationCollector.DELETE + "\" w lines, ",
                "u 1:7 t \"" + RequestDurationCollector.OPTION + "\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.RequestDuration, input, dataWriter, gnuplotDirectives);
    }

    private static String createCacheSizeGraph(ArrayList<History> input)
    {
        DataWriter dataWriter = (histories) -> {
            ArrayList<String> result = new ArrayList<>();

            for (History history : histories) {
                String line = "";

                line += history.timestamp.toString();
                line += "\t";
                line += history.cacheSize.get(CacheSizeCollector.USER_CACHE);
                line += "\t";
                line += history.cacheSize.get(CacheSizeCollector.HOOT_CACHE);

                result.add(line.toString());
            }
            return result;
        };

        String[] gnuplotDirectives = {
                "u 1:2 t \"" + CacheSizeCollector.USER_CACHE + "\" w lines, ",
                "u 1:3 t \"" + CacheSizeCollector.HOOT_CACHE + "\" w lines",
                };

        return Gnuplotter.createGraphWithGnuplot(GraphType.CacheSize, input, dataWriter, gnuplotDirectives);
    }

    private static synchronized String createCurrentLoggedInGraph(ArrayList<History> input)
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
                "u 1:2 t \"currentLoggedIn\" w lines"
        };

        return Gnuplotter.createGraphWithGnuplot(GraphType.CurrentLoggedIn, input, dataWriter, gnuplotDirectives);
    }

    private static synchronized String createGraphWithGnuplot(
            final GraphType graphType, final ArrayList<History> input, DataWriter dataWriter, String[] gnuplotDirectives
    )
    {
        if (input == null) {
            return null;
        }

        FileHandler      fileHandler      = (FileHandler) ObjectManager.create(FileHandler.class);
        ProcessBuilder   plotProcess      = (ProcessBuilder) ObjectManager.create(ProcessBuilder.class);
        StringBuilder    plotCall         = (StringBuilder) ObjectManager.create(StringBuilder.class);
        MediaFileHandler mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);

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
                .append("set timefmt \"%Y-%m-%dT%H:%M:%S\"; ")
                .append("set format x \"%H:%M:%S\"; ");

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
        Statistics,
        Thread,
        Memory,
        HeapMemory,
        CPULoad,
        SystemLoad,
        Requests,
        RequestDuration,
        CacheSize,
        CurrentLoggedIn
    }
}
