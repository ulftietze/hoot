class Router
{
    constructor()
    {
        window.addEventListener('popstate', () => this.dispatch());
        this.dispatch();
    }

    dispatch()
    {
        let routeConfig = new URLSearchParams(window.location.hash.substring(1));
        let route       = routeConfig.get('route') ?? Config.routeMapping.home.target;

        let controller = Object
            .entries(Config.getRouteMapping())
            .find(mapping => route === mapping[1].target);

        if (controller === undefined) {
            throw new ControllerException('No controller found');
        } else if (!controller[1].controller instanceof BaseController) {
            throw new ControllerException('Controller is not instance of BaseController found.');
        }

        let content             = document.getElementById('content');
        let oldContentContainer = document.getElementById('content-container');

        content.removeChild(oldContentContainer);

        let newContentContainer = document.createElement('div');
        newContentContainer.id  = 'content-container';
        content.appendChild(newContentContainer);

        controller[1].controller.execute();
    }
}