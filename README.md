# Web frameworks benchmarking

## How to run

Requirements:
- Docker
- Docker compose
- k6 load testing (just an executable see https://k6.io/)

Steps:
1. `docker compose build`
2. `docker compose up`
3. `cd load-tests`
4. Run `k6` on any of the tests scenarios (i.e. `directAccess*` or `proxy*`). For example `k6 run directAccessGo.js`
will measure the latency of accessing a GET endpoint from a Go server.

## Direct access

Direct access without a proxy. A simple GET endpoint that returns json with content
`{"message": "Hello world"}`.
```
Go:         avg=21.36ms  min=5.86ms med=22.48ms max=38.27ms p(90)=31.95ms  p(95)=33.49ms
Flask 12:   avg=47.61ms  min=15.56ms med=48.92ms max=77.26ms p(90)=62.86ms p(95)=64.62ms
Fastapi 12: avg=89.93ms  min=6.93ms med=81.34ms max=226.95ms p(90)=170.18ms p(95)=222.57ms
Flask 4:    avg=40.28ms  min=13.2ms  med=42.11ms max=61.47ms p(90)=48.52ms  p(95)=56.22ms
Fastapi 4:  avg=81.38ms  min=15.63ms med=72.14ms max=189.26ms p(90)=151.35ms p(95)=166.01ms
Node:       avg=42.94ms  min=1.63ms med=35.41ms max=108.94ms p(90)=73.01ms  p(95)=75.97ms
```

Including Sanic, on Ralf's laptop:
```
Go:             avg=1.94ms  min=105.78µs med=1.7ms   max=28.56ms  p(90)=3.5ms    p(95)=4.03ms
Sanic(w4):      avg=2.31ms  min=190.96µs med=1.99ms  max=5.87ms   p(90)=4.75ms   p(95)=5.38ms
Sanic:          avg=2.42ms  min=120.16µs med=2ms     max=37.7ms  p(90)=4.29ms   p(95)=5.21ms
Flask:          avg=9.97ms  min=2.42ms   med=9.16ms   max=25.6ms  p(90)=16.47ms p(95)=18.31ms
FastAPI:        avg=36.68ms  min=1.75ms  med=42.32ms max=74.19ms p(90)=61.18ms  p(95)=66.81ms
Quart:          avg=37.36ms  min=1.99ms med=39.35ms max=72.7ms  p(90)=61.51ms  p(95)=65.08ms
```

`Sanic(w4)` = with `--workers=4`, `Sanic` = with `--fast`

Notes:

* The numbers next to the Flask and Fastapi results indicate the numbers of `gunicorn`
and `uvicorn` workers respectively.
* The results were run on a MacBook Pro with the Docker VM having 3 cores and 6GB of RAM.
* K6 tests were run locally outside of Docker.
* Best of 5 test runs based on average response time because rerunning the same tests
showed some variability in the results.
* Express.js was used in Node.
* Echo is the framework used in the Go tests

----

On Eike's MacBook (2.3Ghz QuadCore i7, 16G RAM) running Docker on MacOS (4CPU,
8G RAM). Load tests outside of docker on the same machine. `vu=100, iterations=200`

```
Go:       avg=10ms    min=890.26µs med=9.07ms max=50.62ms p(90)=16.79ms p(95)=20.19ms
Sanic:    avg=7.53ms  min=814.92µs med=6.83ms max=56.52ms p(90)=11.98ms p(95)=14.36ms
Http4s:   avg=9.82ms  min=1.04ms med=6.1ms  max=1.28s  p(90)=11.31ms p(95)=13.78ms
FastApi:  avg=59.33ms min=1.94ms med=56.92ms max=174.26ms p(90)=98.77ms p(95)=110.04ms
Flask:    avg=167.46ms min=1.69ms med=25.46ms max=13.13s   p(90)=44.12ms p(95)=53.98ms
Quart:    avg=50.14ms min=1.59ms med=47.31ms max=191ms    p(90)=90.07ms p(95)=103.08ms
```

Flask produced errors in 3 of 5 runs.

## Proxying (to Traefik or not to Traefik)

This aims to answer the question of what happens if we do not use Traefik for proxying
but rather depend on the reverse proxy functionality that Go and Node server frameworks
and/or packages can provide.

One of the reasons we use Traefik is for its [ForwardAuth middleware](https://doc.traefik.io/traefik/middlewares/http/forwardauth/).
This allows us to specify a HTTP endpoint that Traefik calls whenever it proxies a request.
If the endpoint returns a `2XX` code then the call is "authenticated" and proceeds normally.
If the endpoint returns any other status code then the request does not go through to the
requested internal service and Traefik simply returns the response that the authentication
endpoint sent. In addition this middleware allows the authentication endpoint to add any
headers into the request forwarded to the internal service (when the request is allowed).

We can simply eliminate the need for this authentication service and Traefik in general
by using the proxying features in Go or Node (i.e. Express.js).

The scenarios are as follows:
* Current state. Use traefik to route to the Python flask app by using `https://www.google.com`
as the authentication endpoint in the middleware. Google will always return a 200 and the response
will be allowed to continue.
* Use Node to send a request to `https://www.google.com` and then forward the request on to the
internal Python Flask service.
* Same as above but with Go.

```
Traefik     avg=247.46ms min=92.17ms med=253.13ms max=384.02ms p(90)=315.34ms p(95)=327.06ms
Go          avg=246.78ms min=98ms    med=236.61ms max=476.72ms p(90)=376.7ms  p(95)=414.38ms
Node        avg=741.14ms min=383.71ms med=709.15ms max=1.12s  p(90)=930.47ms p(95)=937.54ms
```

## Accessing Redis then Sending Response

This is similar to the "direct access" test. Except that instead of simply returning a string
the servers query Redis for 3 keys and then return the results. The queries all happen
sequentially.

Also just to make sure there are enough resources for the tests and for now running Redis
in Docker on top of all the other services I increased the resources for Docker to 4 cores
and 8GB of RAM.

```
Flask       avg=81.56ms  min=15.2ms  med=90.64ms max=119.48ms p(90)=105.9ms  p(95)=111.94ms
Quart       avg=73.7ms   min=27.17ms med=70.66ms  max=130.48ms p(90)=101.01ms p(95)=111.89ms
Go          avg=43.45ms min=11.51ms med=41.99ms max=94.57ms  p(90)=78.72ms  p(95)=93.15ms
```

Including Sanic, on Ralf's laptop:
```
Go:             avg=4.26ms  min=392.21µs med=4.02ms  max=10.04ms  p(90)=6.57ms   p(95)=8.15ms
Sanic:          avg=4.68ms  min=434.92µs med=3.77ms  max=17.69ms  p(90)=7.11ms   p(95)=8.57ms
Sanic(w4):      avg=5.56ms  min=448.52µs med=5.77ms  max=10.63ms  p(90)=8.26ms   p(95)=10.13ms
Flask:          avg=10.12ms  min=1.97ms  med=9.41ms   max=18.11ms p(90)=15.59ms  p(95)=16.57ms
Quart:          avg=40.11ms  min=2.11ms  med=48.86ms max=72.53ms p(90)=65.4ms   p(95)=66.61ms
```
`Sanic(w4)` = with `--workers=4`, `Sanic` = with `--fast`


On Eike's Macbook (see above, `vu=100, iterations=200`):

```
Go:      avg=20.94ms min=1.33ms med=19.77ms max=93.09ms p(90)=32.41ms p(95)=37.9ms
Sanic:   avg=27.74ms min=1.72ms med=26.26ms max=88.71ms p(90)=41.75ms p(95)=45.15ms
Http4s:  avg=16.13ms min=2.26ms med=12.6ms  max=1.18s  p(90)=21.47ms p(95)=24.98ms
Flask:   avg=173.17ms min=2.9ms  med=82.97ms max=13.17s p(90)=101.83ms p(95)=107.6ms
Quart:   avg=68.41ms min=2.38ms med=63.9ms  max=175.73ms p(90)=111.23ms p(95)=125.25ms
```
