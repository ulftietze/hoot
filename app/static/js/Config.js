class Config
{
    // TODO: This must be per instance.
    static BASE_URL = 'https://informatik.hs-bremerhaven.de';

    static CONTEXT_URL = '/docker-swe3-21-team-d-java/';

    static routeMapping = {
        home    : {target: 'home', controller: new HomeController()},
        explore : {target: 'explore', controller: new ExploreController()},
        search  : {target: 'search', controller: new SearchController()},
        profile : {target: 'profile', controller: new ProfileController()},
        monitor : {target: 'monitor', controller: new MonitorController()},
        swagger : {target: 'swagger', controller: new SwaggerController()},
        login   : {target: 'login', controller: new LoginController()},
        logout  : {target: 'logout', controller: new LogoutController()},
        register: {target: 'register', controller: new RegisterController()},
    };

    static getRemoteBaseUrl()
    {
        return Config.BASE_URL;
    }

    static getWebappUrl()
    {
        return this.getRemoteBaseUrl() + Config.CONTEXT_URL;
    }

    static getApiUrl()
    {
        return Config.getWebappUrl() + 'api/V1/';
    }

    static getRouteMapping()
    {
        return this.routeMapping;
    }

    static getDefaultProfileImage()
    {
        return 'static/image/Profile_avatar_placeholder_large.png';
    }
}