package hoot.app.Service;

import hoot.model.entities.History;
import hoot.model.monitoring.Gnuplotter;
import hoot.model.repositories.HistoryRepository;
import hoot.model.search.HistorySearchCriteria;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Service.ServiceInterface;

import java.util.ArrayList;

public class MonitorGraphCreationService implements ServiceInterface
{
    private final HistoryRepository historyRepository;

    private final LoggerInterface logger;

    public MonitorGraphCreationService()
    {
        this.historyRepository = (HistoryRepository) ObjectManager.get(HistoryRepository.class);
        this.logger            = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void execute()
    {
        try {
            HistorySearchCriteria historySearchCriteria = this.createHistorySearchCriteria();
            historySearchCriteria.secondsToLoad = 3600; // 1 hour

            try {
                ArrayList<History> historyList = historyRepository.getList(historySearchCriteria);
                this.drawGraphs(historyList);
            } catch (EntityNotFoundException e) {
                this.logger.log("Could not get List of History Objects");
            }
        } catch (Throwable t) {
            this.logger.logException("Could not monitor History: " + t.getMessage(), t);
        }
    }

    private void drawGraphs(ArrayList<History> historyList)
    {
        for (Gnuplotter.GraphType graphType : Gnuplotter.GraphType.values()) {
            new Thread(() -> {
                try {
                    Gnuplotter.createGraph(graphType, historyList);
                } catch (Throwable t) {
                    this.logger.logException("Could not create Graph " + graphType.name(), t);
                }
            }).start();
        }
    }

    private HistorySearchCriteria createHistorySearchCriteria()
    {
        return (HistorySearchCriteria) ObjectManager.create(HistorySearchCriteria.class);
    }
}
