class UserDTO
{
    /**
     * @type {int}
     */
    id;

    /**
     * @type {String}
     */
    userName;

    /**
     * @type {String}
     */
    imageUrl;

    /**
     * @type {String}
     */
    imageFilename;

    /**
     * @type {String} // TODO: Declare as base64
     */
    image;

    /**
     * @type {FollowerDTO[]}
     */
    followers;

    /**
     * @type {FollowDTO[]}
     */
    follows;
}