import redis
from sanic import Sanic
from sanic.response import json
from orjson import dumps

app = Sanic("Benchmark")

app.ctx.db = redis.Redis(host="redis")


@app.get("/redis")
async def redis(request):
    output = [
        app.ctx.db.get("test1").decode("utf-8"),
        app.ctx.db.get("test2").decode("utf-8"),
        app.ctx.db.get("test3").decode("utf-8"),
    ]
    return json(
        {
            "values": output,
        },
        dumps=dumps,
    )


@app.get("/")
async def hello_world(request):
    return json({"message": "Hello world"}, dumps=dumps)
