import http from 'k6/http';
import { check } from 'k6';

export let options = {
    vus: '50',
    duration: '120s',
    //iterations    : '300000',
    noCookiesReset: true,
};

export default function() {
    let url = `https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/hello-world`
    let res = http.get(url);
    check(res, {
        'is status 200': (r) => r.status === 200,
    });
}





