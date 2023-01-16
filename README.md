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

Notes:

* The numbers next to the Flask and Fastapi results indicate the numbers of `gunicorn`
and `uvicorn` workers respectively.
* The results were run on a MacBook Pro with the Docker VM having 3 cores and 6GB of RAM.
* K6 tests were run locally outside of Docker.
* Best of 5 test runs based on average response time because rerunning the same tests
showed some variability in the results.
* Express.js was used in Node.

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
