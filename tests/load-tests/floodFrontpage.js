import http from 'k6/http';
import { check } from 'k6';

export let options = {
    vus: '50',
    duration: '180s',
    //iterations    : '300000',
    noCookiesReset: true,
};

export default function() {
    let res = http.get('https://informatik.hs-bremerhaven.de/docker-swe3-21-team-d-java/hello-world');
    check(res, {
        'is status 200': (r) => r.status === 200,
    });
}





