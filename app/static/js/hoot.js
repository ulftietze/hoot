window.addEventListener('load', () => {
    window.hootObjects                   = {};
    window.hootObjects.persistentStorage = new PersistentStorage();
    window.hootObjects.controller        = [];
    window.hootObjects.router            = new Router();
    window.hootObjects.router.dispatch();

    Menu.buildMenu();

    [].slice
        .call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        .forEach(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
});
