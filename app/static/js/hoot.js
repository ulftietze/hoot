window.addEventListener('load', () => {
    window.hootObjects                   = {};
    window.hootObjects.persistentStorage = new PersistentStorage();
    window.hootObjects.controller        = [];
    window.hootObjects.router            = new Router();

    Menu.buildMenu();

    [].slice
        .call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        .forEach(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
});
