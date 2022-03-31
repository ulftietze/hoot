class MonitorComponent
{
    static drawMonitor(title, url)
    {
        let row = document.createElement('div');
        row.classList.add('row');

        let titleElement         = document.createElement('h2');
        titleElement.textContent = title;
        titleElement.classList.add('text-primary');

        let img = document.createElement('img');
        img.src = Config.getRemoteBaseUrl() + url + '?' + Math.random().toString(36).slice(2);
        img.alt = title;
        img.classList.add('col');

        row.appendChild(titleElement);
        row.appendChild(img);

        return row;
    }
}