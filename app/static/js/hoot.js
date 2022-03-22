window.addEventListener('popstate', (event) => {
    // Log the state data to the console
    console.log(event);
});

window.addEventListener('load', () => {
    Menu.buildMenu();
});