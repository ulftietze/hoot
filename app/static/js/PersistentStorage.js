class PersistentStorage
{
    /**
     * @type {Storage}
     */
    storage;

    constructor()
    {
        this.storage = window.localStorage;
    }

    /**
     * Returns the name of the nth key, or null if n is greater than or equal to the number of key/value pairs.
     *
     * @param {int} index
     * @returns {any}
     */
    key(index)
    {
        return JSON.parse(this.storage.key(index));
    }

    /**
     * Check whether the storage contains the item or not.
     *
     * @param key
     * @returns {boolean}
     */
    hasItem(key)
    {
        return !!this.storage.getItem(key);
    }

    /**
     * Returns the current value associated with the given key, or null if the given key does not exist.
     *
     * @param {String} key
     * @returns {any}
     */
    getItem(key)
    {
        return JSON.parse(this.storage.getItem(key));
    }

    /**
     * Sets the value of the pair identified by key to value, creating a new key/value pair if none existed for key
     * previously.
     *
     * Throws a "QuotaExceededError" DOMException exception if the new value couldn't be set. (Setting could fail if,
     * e.g., the user has disabled storage for the site, or if the quota has been exceeded.)
     *
     * Dispatches a storage event on Window objects holding an equivalent Storage object.
     *
     * @param {String} key
     * @param {any} value
     */
    setItem(key, value)
    {
        this.storage.setItem(key, JSON.stringify(value));
    }

    /**
     * Returns the number of key/value pairs.
     *
     * @returns {number}
     */
    length()
    {
        return this.storage.length;
    }

    /**
     * Removes all key/value pairs, if there are any.
     *
     * Dispatches a storage event on Window objects holding an equivalent Storage object.
     */
    clear()
    {
        this.storage.clear();
    }

    /**
     * Removes the key/value pair with the given key, if a key/value pair with the given key exists.
     *
     * Dispatches a storage event on Window objects holding an equivalent Storage object.
     *
     * @param {String} key
     */
    removeItem(key)
    {
        this.storage.removeItem(key);
    }
}