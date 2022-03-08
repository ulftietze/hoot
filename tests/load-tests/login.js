import http from 'k6/http';
import { check, sleep } from 'k6';

export default function() {
    const data = {username: 'test1', password: 'test'};
    let res    = http.post('https://informatik.hs-bremerhaven.de/docker-utietze-java/api/V1/login/', data);
    check(res, {'success login': (r) => r.status === 200});
    sleep(0.3);
}





