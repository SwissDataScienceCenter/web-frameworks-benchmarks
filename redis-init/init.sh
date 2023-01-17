while ! redis-cli -h redis ping
do
    echo "Waiting for redis"
    sleep 10
done
echo "Redis is ready, doing init"
redis-cli -h redis set test1 "test1-value"
redis-cli -h redis set test2 "test2-value"
redis-cli -h redis set test3 "test3-value"
redis-cli -h redis keys \*
