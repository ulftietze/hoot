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
    protected LoggerInterface logger;

    public Gnuplotter()
    {
        this.logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    public String createPNGFromHistories(ArrayList<Historie> toPrint)
    {
        try {
            StringBuilder inputs = new StringBuilder();
            for (Historie historie : toPrint) {
                inputs.append(historie.timestamp.toString());
                inputs.append("\t");
                inputs.append(historie.currentlyRegisteredUsers);
                inputs.append(System.lineSeparator());
            }

            ProcessBuilder processBuilder = new ProcessBuilder();

            ArrayList<String> commands = new ArrayList<>();
            commands.add("/bin/bash");
            commands.add("-c");

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("echo \"");
            stringBuilder.append(inputs);
            stringBuilder.append("\"");
            stringBuilder.append("| gnuplot -e 'set terminal png size 1024,576; set xdata time; set timefmt \"%Y-%m-%dT%H:%M:%S\"; plot \"-\" u 1:2 t \"Registered Users\" w lines'");
            stringBuilder.append("| base64");

            commands.add(stringBuilder.toString());

            processBuilder.command(commands);

            Process process = processBuilder.start();

            BufferedReader br  = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder base64String = (StringBuilder) ObjectManager.create(StringBuilder.class);
            base64String.append("data:image/png;base64,");

            String tmp;
            while ((tmp = br.readLine()) != null) {
                base64String.append(tmp);
            }

            MediaFileHandler fileHandler = (MediaFileHandler) ObjectManager.create(MediaFileHandler.class);

            fileHandler.saveMedia("graph.png", "monitoring" + File.separator, base64String.toString());

            return fileHandler.getImageUrl("monitoring" + File.separator + "graph.png");
        } catch (IOException e) {
            this.logger.log("Could not create Historie-Graph: " + e.getMessage());
            return null;
        }
    }
}
