FROM golang:1.19.5-alpine3.17 as builder
WORKDIR /app
COPY . .
RUN go build -o app

FROM alpine:3.17
COPY --from=builder /app/app /app
ENTRYPOINT [ "/app" ]
