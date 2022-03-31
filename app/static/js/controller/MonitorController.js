class MonitorController extends BaseController
{
    /**
     * @type {GraphList}
     */
    graphList = new GraphList();

    constructor()
    {
        super();
        window.setInterval(() => {
            this.loadFromApi.bind(this)();
        }, 30000);
    }

    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.monitor.target;
    }

    execute()
    {
        console.log(this);
        this.loadFromApi();
    }

    loadFromApi()
    {
        Api.getSystemMonitorGraphs(graphList => {
            this.graphList = graphList;
            if (this.match(window.hootObjects.router.getCurrentRoute())) {
                this.#drawGraphs.bind(this)();
            }
        }, result => console.log(result));
    }

    #drawGraphs()
    {
        let contentElement = document.getElementById('content-container');
        contentElement.classList.add('container');

        Object.entries(this.graphList.graphList).forEach(graph => {
            let title = graph[0];
            let url   = graph[1];

            let monitorRow = document.querySelector('div[data-monitor-id="monitor-' + title+'"]')
            let monitor = MonitorComponent.drawMonitor(title, url);
            monitor.setAttribute('data-monitor-id', 'monitor-' + title);

            if (!monitorRow) {
                contentElement.appendChild(monitor);
                contentElement.appendChild(document.createElement('hr'));
            } else {
                monitorRow.replaceWith(monitor);
            }
        });
    }
}