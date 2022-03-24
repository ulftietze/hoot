window.addEventListener('load', () => {
    Menu.buildMenu();

    window.hootObjects = {};
    window.hootObjects.router = new Router();
    window.hootObjects.controller = [];

    [].slice
        .call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        .forEach(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
});
