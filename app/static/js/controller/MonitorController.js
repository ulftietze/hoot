class MonitorController extends BaseController
{
    graphList = {};

    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.monitor.target;
    }

    execute()
    {
        Api.getSystemMonitorGraphs(response => {
            this.graphList = response;
            this.#drawGraphs();
        }, (result) => console.log(result));
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
        Object.entries(this.graphList).forEach(graph => {

            let title = graph[0];
            let url   = graph[1];

            let img = document.createElement('img');
            img.src = url + '?' + Math.random().toString(36).slice(2);
            img.alt = title;
            img.classList.add('col');
            row.appendChild(img);
        });

        contentElement.appendChild(row);
    }
}