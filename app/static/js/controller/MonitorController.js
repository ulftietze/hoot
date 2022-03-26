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

        let row = document.createElement('div');
        row.classList.add('row');

        while (contentElement.firstChild) {
            contentElement.removeChild(contentElement.lastChild);
        }
        Object.entries(this.graphList.graphList).forEach(graph => {

            let title = graph[0];
            let url   = graph[1];

            let titleElement = document.createElement('h2');
            titleElement.textContent = title;
            titleElement.classList.add('text-primary');

            let img = document.createElement('img');
            img.src = Config.getRemoteBaseUrl() + url + '?' + Math.random().toString(36).slice(2);
            img.alt = title;
            img.classList.add('col');

            row.appendChild(titleElement);
            row.appendChild(img);
            row.appendChild(document.createElement('hr'));
        });

        contentElement.appendChild(row);
    }
}