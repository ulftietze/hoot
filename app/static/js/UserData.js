class UserData
{
    static isLoggedIn()
    {
        // TODO: Load from section
        return true;
    }

    static getProfileImage()
    {
        // TODO: Not only if logged in, also if user has an image configured.
        if (this.isLoggedIn()) {
            // TODO: Load correct image
            return 'https://github.com/mdo.png';
        }

        return Config.getDefaultProfileImage();
    }
}