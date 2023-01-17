package main

import (
	"context"
	"net/http"

	"github.com/go-redis/redis/v9"
	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
)

var rdb *redis.Client = redis.NewClient(&redis.Options{
	Addr:     "redis:6379",
	Password: "",
	DB:       0,
})

func queryRedis(ctx context.Context, key string) (string, error) {
	return rdb.Get(ctx, key).Result()
}

func main() {
	e := echo.New()
	e.Use(middleware.Logger())
	e.Use(middleware.Recover())
	e.GET("/", func(c echo.Context) error {
		return c.JSON(http.StatusOK, &map[string]string{"message": "Hello world"})
	})
	e.GET("/redis", func(c echo.Context) error {
		ctx := c.Request().Context()
		v1, err := queryRedis(ctx, "test1")
		if err != nil {
			return err
		}
		v2, err := queryRedis(ctx, "test2")
		if err != nil {
			return err
		}
		v3, err := queryRedis(ctx, "test3")
		if err != nil {
			return err
		}
		output := []string{
			v1,
			v2,
			v3,
		}
		return c.JSON(http.StatusOK, &map[string][]string{"value": output})
	})
	e.Logger.Fatal(e.Start(":3000"))
}
