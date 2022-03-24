class SwaggerController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.swagger.target;
    }

    execute()
    {
        SwaggerUIBundle({
            url   : 'static/api/V1.yml',
            dom_id: '#content-container',
        });

        document.getElementById('content-container').style.backgroundColor = 'white';
    }
}