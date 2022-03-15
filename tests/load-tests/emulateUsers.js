import http from 'k6/http';
import exec from "k6/execution";
import { check } from "k6";
import {SharedArray} from "k6/data";

let size = 1000;
let vus  = 1;

const data = new SharedArray("my dataset", function () {
    let data = [];
    for (let i = 0; i < size; i++) {
        data[i] = {"username": "testUser" + i, "password": "test123"}
    }

    return data;
})
export const options = {
    scenarios: {
        "use-all-the-data": {
            executor: "shared-iterations", vus: vus, iterations: 3000, maxDuration: "3m"
        }
    }
}

export default function () {
    let item = data[Math.floor(Math.random() * data.length)];
    let random = Math.floor(Math.random() * data.length);

    let dataPost = {content: `RandomZahlenLOL #randomZahl #${random}`, onlyFollower: true}
    let dataImage = { content: "testImage", imageFilename: "image/testImage.png", image: "String,String", onlyFollower: true}
    let dataComment = { content: "I Like your Hoot", parentHootId: random}

    let url = `https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/`

    let res = [];

    res.push(http.post(`${url}login`, JSON.stringify(item), {headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'}}));
    res.push(http.get(`${url}user?id=${random}`));
    res.push(http.get(`${url}hoot/search?quantity=50&userId=${random}`));
    res.push(http.get(`${url}user/follower?userId=${random}&quantity=50`));
    res.push(http.get(`${url}user/follows?userId=${random}&quantity=50`));
    res.push(http.get(`${url}hoot/timeline/mine?quantity=50`));
    res.push(http.post(`${url}user/me/follow`, JSON.stringify(random), {headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'}}));
    res.push(http.get(`${url}hoot/timeline/mine?lastPostId=200&quantity=50`));
    res.push(http.get(`${url}hoot/timeline/global?lastPostId=150&quantity=50`));
    res.push(http.post(`${url}user/me/unfollow`, JSON.stringify(random), {headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'}}));
    res.push(http.get(`${url}hoot/timeline/mine?lastPostId=150&quantity=50`));
    res.push(http.post(`${url}hoot/post`, JSON.stringify(dataPost), {headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'}}));
    res.push(http.get(`${url}hoot/timeline/global?quantity=50`));
    res.push(http.get(`${url}hoot/timeline/global?lastPostId=50&quantity=50`));
    res.push(http.post(`${url}hoot/image`, JSON.stringify(dataImage), {headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'}}));
    res.push(http.get(`${url}hoot/timeline/mine?lastPostId=50&quantity=50`));
    res.push(http.get(`${url}hoot/timeline/global?lastPostId=100&quantity=50`));
    res.push(http.get(`${url}hoot/timeline/mine?lastPostId=100&quantity=50`));
    res.push(http.get(`${url}hoot/timeline/global?lastPostId=200&quantity=50`));

    res.forEach(r => check(r, {'is status 200': (r) => r.status === 200}) ? () => {} : console.log(r.url + " " + r.content));

}