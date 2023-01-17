from quart import Quart, jsonify

app = Quart(__name__)

@app.route('/')
async def hello():
    return jsonify({"message": "Hello world"})
