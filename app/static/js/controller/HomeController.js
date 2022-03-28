class HomeController extends BaseController
{
    /**
     * @type {Hoots}
     */
    hoots = new Hoots();

    /**
     * @type {boolean}
     */
    loading = false;

    constructor()
    {
        super();

        document.addEventListener('scroll', () => {
            if (!this.match(window.hootObjects.router.getCurrentRoute())) {
                return;
            }

            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 1000) {
                this.infiniteScrollLoad.bind(this)();
            }
        });
    }

    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.home.target;
    }

    infiniteScrollLoad()
    {
        if (this.loading) {
            return;
        }

        this.loading        = true;
        let lastPostId      = null;
        let documentElement = document.getElementById('content-container');

        if (this.hoots.hoots.length && this.match(window.hootObjects.router.getCurrentRoute())) {
            lastPostId = this.hoots.hoots.reduce((prev, cur) => prev.id < cur.id ? prev : cur).id;
        }

        Api.getHootTimelineMine(lastPostId, 50, '', hoots => {
            this.hoots.hoots = this.hoots.hoots.concat(hoots.hoots).sort((a, b) => a.id < b.id ? -1 : 1);
            HootsComponent.appendHootsToElement(this.hoots, documentElement);
            this.loading = false;
        }, e => console.log(e));
    }

    execute()
    {
        this.hoots          = new Hoots();
        let documentElement = document.getElementById('content-container');
        documentElement.classList.add('container');
        documentElement.style.maxWidth = '720px';

        while (documentElement.firstChild) {
            documentElement.removeChild(documentElement.lastChild);
        }

        if (UserData.isLoggedIn()) {
            documentElement.appendChild(HootComponent.drawCreateHoot());
        }

        Api.getHootTimelineMine(null, 50, '', hoots => {
            this.hoots = hoots;
            HootsComponent.appendHootsToElement(this.hoots, documentElement);
        }, e => console.log(e));
    }
}