class ProfileController extends BaseController
{
    static USER_HOOTS_CONTAINER = 'user-hoots-container';

    static USER_ROUTE_IDENTIFIER = 'userId';

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
        return routeTarget === Config.routeMapping.profile.target;
    }

    infiniteScrollLoad()
    {
        if (this.loading) {
            return;
        }

        this.loading       = true;
        let lastPostId     = null;
        let hootsComponent = document.getElementById(ProfileController.USER_HOOTS_CONTAINER);
        let userId         = this.getUserId();

        if (this.hoots.hoots.length && this.match(window.hootObjects.router.getCurrentRoute())) {
            lastPostId = this.hoots.hoots.reduce((prev, cur) => prev.id < cur.id ? prev : cur).id;
        }

        Api.getHootSearch(lastPostId, 50, null, userId, hoots => {
            this.hoots.hoots = this.hoots.hoots.concat(hoots.hoots).sort((a, b) => a.id < b.id ? -1 : 1);
            HootsComponent.appendHootsToElement(this.hoots, hootsComponent);
            this.loading = false;
        }, e => console.log(e));
    }

    execute()
    {
        if (!UserData.isLoggedIn()) {
            UtilComponent.createToast('Dafür musst du eingeloggt sein.');
            window.hootObjects.router.setRoute(Config.routeMapping.login.target);
            return;
        }

        this.hoots                     = new Hoots();
        let documentElement            = document.getElementById('content-container');
        documentElement.style.maxWidth = '720px';
        documentElement.classList.add('container');

        if (!this.getUserId()) {
            let errorMsg       = document.createElement('p');
            errorMsg.innerText = 'No user found';
            documentElement.appendChild(errorMsg);
            return;
        }

        Api.getUser(this.getUserId(), user => {
            documentElement.appendChild(ProfileComponent.drawProfileHead(user));

            let hootsComponent = document.getElementById(ProfileController.USER_HOOTS_CONTAINER);

            if (!hootsComponent) {
                hootsComponent    = document.createElement('div');
                hootsComponent.id = ProfileController.USER_HOOTS_CONTAINER;
                hootsComponent.classList.add('container');
                documentElement.appendChild(hootsComponent);
            }

            while (hootsComponent.firstChild) {
                hootsComponent.removeChild(hootsComponent.lastChild);
            }

            Api.getHootSearch(null, 50, null, user.id, hoots => {
                this.hoots = hoots;
                HootsComponent.appendHootsToElement(this.hoots, hootsComponent);
            }, er => console.log(er));
        }, er => console.log(er))
    }

    getUserId()
    {
        let userId = window.hootObjects.router.getParameter(ProfileController.USER_ROUTE_IDENTIFIER);

        console.log(userId);

        if (userId === 'currentUser') {
            userId = UserData.getUser().id;
        }

        return userId;
    }
}