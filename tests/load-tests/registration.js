import http from 'k6/http';
import exec from "k6/execution";
import {check} from 'k6';

export let options = {
    scenarios: {
        "1000-registrations": {
            executor: "shared-iterations", vus: 20, iterations: 1000, maxDuration: "1h"
        }
    }
};

export default function() {
    const url = `https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/register`;
    let data  = {
        username: "testUser" + exec.scenario.iterationInTest,
        password: 'test123',
        image   : 'String,String',
    };

    let res = http.post(url, JSON.stringify(data), {
        headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'},
    });

    //console.log(res.json());
    check(res, {'status is 201': (r) => r.status === 201});
}
