import redis
from quart import Quart, jsonify

app = Quart(__name__)

r = redis.Redis(host='redis')

@app.route('/')
async def hello():
    return jsonify({"message": "Hello world"})


@app.route("/redis")
async def redis():
    output = [
        r.get("test1").decode("utf-8"),
        r.get("test2").decode("utf-8"),
        r.get("test3").decode("utf-8"),
    ]
    return jsonify({
        "values": output,
    })
