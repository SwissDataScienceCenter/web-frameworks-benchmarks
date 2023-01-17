while ! redis-cli -h redis ping
do
    echo "Waiting for redis"
    sleep 10
done
echo "Redis is ready, doing init"
redis-cli -h redis set test "test"
redis-cli -h redis keys \*
