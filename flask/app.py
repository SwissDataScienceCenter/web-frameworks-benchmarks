import redis
from flask import Flask, jsonify

app = Flask(__name__)

r = redis.Redis(host='redis')

@app.route("/redis")
def redis():
    output = [
        r.get("test1").decode("utf-8"),
        r.get("test2").decode("utf-8"),
        r.get("test3").decode("utf-8"),
    ]
    return jsonify({
        "values": output,
    })

@app.route("/")
def hello_world():
    return jsonify({
        "message": "Hello world"
    })
