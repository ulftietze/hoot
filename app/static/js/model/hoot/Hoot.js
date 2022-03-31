class Hoot
{
    /**
     * @type {int}
     */
    id;

    /**
     * @type {String}
     */
    type; // TODO: ENUM

    /**
     * @type {User}
     */
    user;

    /**
     * @type {Date}
     */
    created;

    /**
     * @type {Date}
     */
    modified;

    /**
     * @type {Mentions[]}
     */
    mentions;

    /**
     * @type {HootTags[]}
     */
    hootTags;

    /**
     * @type {Object}
     */
    reactionCount;

    /**
     * @type {boolean}
     */
    onlyFollower;
}