import http from 'k6/http';
import {check} from 'k6';
import {SharedArray} from 'k6/data';

const sut = __ENV.SUT ?? 'http://localhost:8080';

export const options = {
    // vus: 3,
    // duration: '10s',
    iterations: 3,
    thresholds: {
        'http_reqs{status:401}': [
            {
                threshold: 'count==0',
                abortOnFail: true // stop as soon as you get 401, no point in continuing
            }
        ]
    }
};

// load test tokens, make sure they're valid on the system under test
const tokens = new SharedArray('tokens', function () {
    return JSON.parse(open(__ENV.TOKENS_FILE ?? 'tokens.json'))
});

export default function () {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + tokens[Math.floor(Math.random() * tokens.length)]
    }

    // create todo
    let res = http.post(sut + "/api/v1/todos", JSON.stringify({
        title: "dummy todo",
        description: 'dummy description',
        dueTo: new Date().toISOString()
    }), {headers})
    check(res, {"todo created": (res) => res.status === 200})
    const todoId = JSON.parse(res.body).id

    // update it
    res = http.put(sut + "/api/v1/todos/" + todoId, JSON.stringify({
        title: "dummy todo updated",
        description: 'dummy description updated',
        dueTo: new Date().toISOString()
    }), {headers})
    check(res, {"todo updated": (res) => res.status === 200})

    // // list everything
    res = http.get(sut + "/api/v1/todos", {headers})
    check(res, {"all todos returned": res => res.status === 200})

    // delete it
    res = http.del(sut + "/api/v1/todos/" + todoId, null, {
        headers: {
            'Authorization': headers['Authorization']
        }
    })
    check(res, {"todo deleted": (res) => res.status === 200})
}
