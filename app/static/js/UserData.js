class UserData
{
    static STORAGE_USER_IDENTIFIER = 'current-user';

    static isLoggedIn()
    {
        return this.getUser() !== null;

    }

    static getProfileImage()
    {
        // TODO: Not only if logged in, also if user has an image configured.
        if (this.isLoggedIn()) {
            // TODO: Load correct image
            return 'static/image/Profile_avatar_placeholder_large.png';
        }

        return Config.getDefaultProfileImage();
    }

    /**
     *
     * @returns {User|null}
     */
    static getUser()
    {
        let storage = window.hootObjects.persistentStorage;

        if (storage.hasItem(this.STORAGE_USER_IDENTIFIER)) {
            return window.hootObjects.persistentStorage.getItem(this.STORAGE_USER_IDENTIFIER);
        }

        return null;
    }

    /**
     * @param {User|null} user
     */
    static setUser(user)
    {
        let storage = window.hootObjects.persistentStorage;

        if (!user) {
            storage.removeItem(this.STORAGE_USER_IDENTIFIER);
        } else {
            storage.setItem(this.STORAGE_USER_IDENTIFIER, user);
        }
    }
}