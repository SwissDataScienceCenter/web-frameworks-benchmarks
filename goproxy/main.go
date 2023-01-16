package main

import (
	"net/http"
	"net/url"

	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
)

func authMiddleware(next echo.HandlerFunc) echo.HandlerFunc {
	return func(c echo.Context) error {
		res, err := http.Get("https://www.google.com/")
		if err != nil {
			return echo.ErrUnauthorized
		}
		if res.StatusCode < 200 ||  res.StatusCode >= 300 {
			return echo.ErrUnauthorized
		}
		return next(c)
	}
}

func main() {
	e := echo.New()
	e.Use(middleware.Recover())
	e.Use(middleware.Logger())

	// Setup proxy
	serviceURL, err := url.Parse("http://flask:3000")
	if err != nil {
		e.Logger.Fatal(err)
	}
	targets := []*middleware.ProxyTarget{
		{
			URL: serviceURL,
		},
	}
	e.Use(
		authMiddleware,
		middleware.Proxy(middleware.NewRoundRobinBalancer(targets)),
	)
	e.Logger.Fatal(e.Start(":3000"))
}
