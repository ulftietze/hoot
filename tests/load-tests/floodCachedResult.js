import http from 'k6/http';
import { check } from 'k6';

export let options = {
    vus: '50',
    duration: '180s',
    //iterations    : '300000',
    noCookiesReset: true,
};

export default function() {
    let url = `https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/`
    let res = http.get(`${url}hoot/timeline/global?lastPostId=100&quantity=50`);
    check(res, {
        'is status 200': (r) => r.status === 200,
    });
}





