class ExploreController extends BaseController
{
    /**
     * @type {Hoots}
     */
    hoots = new Hoots();

    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.explore.target;
    }

    execute()
    {
        let documentElement = document.getElementById('content-container');
        let lastPostId = null;

        if (this.hoots.hoots.length) {
            lastPostId = this.hoots.hoots.reduce((prev, cur) => prev.id < cur.id ? prev : cur).id;
        }

        Api.getHootTimelineGlobal(lastPostId, 50, '', hoots => {
            this.hoots = hoots;
            if (this.match(window.hootObjects.router.getCurrentRoute())) {
                HootsComponent.prependHootsToElement(this.hoots, documentElement);
            }
        }, e => console.log(e));
    }
}