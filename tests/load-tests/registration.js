import http from 'k6/http';
import {check} from 'k6';

export let options = {
    vus       : '1',
    //duration  : '120s',
    iterations: '1',
};
export default function() {
    const url = `https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/register`;
    let data  = {
        username: uuidv4(),
        password: 'test123',
        image   : 'String,String',
    };

    let res = http.post(url, JSON.stringify(data), {
        headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'},
    });

    //console.log(res.json());
    check(res, {'status is 201': (r) => r.status === 201});
}

export function uuidv4()
{
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        let r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}