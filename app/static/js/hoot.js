window.addEventListener('popstate', (event) => {
    // Log the state data to the console
    console.log(event);
});

window.addEventListener('load', () => {
    Menu.buildMenu();

    [].slice
        .call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        .forEach(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
});