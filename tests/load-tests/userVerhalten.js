import http from 'k6/http';
import exec from "k6/execution";
import {SharedArray} from "k6/data";

const data = new SharedArray("my dataset", function () {
    return JSON.parse(open(`${__ENV.PATH}/tests/load-tests/userdata.json`));
})
export const options = {
    scenarios: {
        "use-all-the-data": {
            executor: "shared-iterations", vus: 2, iterations: 5, maxDuration: "1h"
        }
    }
}

export default function () {
    var item = data[exec.scenario.iterationInTest];
    let random = Math.floor(Math.random() * data.length);
    let dataPost = {content: `RandomZahlenLOL #randomZahl #${random}`, onlyFollower: true}
    let dataImage = { content: "testImage", imageFilename: "image/testImage.png", image: "String,String", onlyFollower: true}
    let dataComment = { content: "I Like your Hoot", parentHootId: random}

    http.post(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/login`, JSON.stringify(item), {
        headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'},
    });

    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/mine`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/mine?quantity=50`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/mine?quantity=50`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/mine?quantity=50`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/mine?quantity=50`);

    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/global`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/global?quantity=50`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/global?quantity=50`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/global?quantity=50`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/timeline/global?quantity=50`);

    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/user?id=${random}`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/search?quantity=50&userId=${random}`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/user/follower?userId=${random}&quantity=50`);
    http.get(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/user/follows?userId=${random}&quantity=50`);

    http.post(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/user/me/follow`, JSON.stringify(random), {
        headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'},
    });
    http.post(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/user/me/unfollow`, JSON.stringify(random), {
        headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'},
    });
    http.post(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/post`, JSON.stringify(dataPost), {
        headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'},
    });
    http.post(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/image`, JSON.stringify(dataImage), {
        headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'},
    });
    http.post(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/hoot/comment`, JSON.stringify(dataComment), {
        headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'},
    });
    http.post(`https://informatik.hs-bremerhaven.de/${__ENV.USER}-java/api/V1/logout`," ", {
        headers: {'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8'},
    });



}