import http from 'k6/http';
import {check} from 'k6';

export let options = {
    vus: '50',
    //duration: '120s',
    iterations: '30000',
    noCookiesReset: true
}
export default function()
{
    let urlLogin = 'https://informatik.hs-bremerhaven.de/docker-utietze-java/api/V1/login'
    let dataLogin = {
        username: "1ba67273-bfe3-41b0-bd1b-5823f8a5008a",
        password: "string"
    }
    let paramsLogin = {
        headers: { 'Accept': '*/*', 'Content-Type': 'application/json; charset=UTF-8' }
    }

    let urlPost = 'https://informatik.hs-bremerhaven.de/docker-utietze-java/api/V1/hoot/post'
    let dataPost = {
        content: "test123",
        onlyFollower: true,
    }
    let paramsPost = {
        headers: { 'Accept': '*/*', 'Content-Type': 'application/json; charset=UTF-8' }
    }


    let resLogin = http.post(urlLogin, JSON.stringify(dataLogin), paramsLogin);
    let resPost = http.post(urlPost, JSON.stringify(dataPost), paramsPost);

    //console.log("Request LOGIN: " + resLogin.json());
    //console.log("Request POST: " + resPost.json());
    check(resLogin, {
        'status is 200': (r) => r.status === 200
    });
    check(resPost, {
        'status is 200 Saving sucessfull': (r) => r.status === 200,
        'status is 401 login failed': (r) => r.status === 401,
        'status is 406 Hoot could not be saved': (r) => r.status === 406,
    });
}
