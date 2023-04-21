FROM amazoncorretto:20-alpine as builder

WORKDIR /work

COPY . .

RUN export PATH="/usr/local/sbt/bin:$PATH" && \
    apk update && apk add --no-cache --virtual .build-dependencies bash wget tar git && \
    mkdir -p "/usr/local/sbt" && \
    wget -qO - "https://github.com/sbt/sbt/releases/download/v1.8.2/sbt-1.8.2.tgz" | tar xz -C /usr/local/sbt --strip-components=1 && \
    sbt "make-stage" && \
    apk del .build-dependencies

FROM amazoncorretto:20-alpine

WORKDIR /opt/http4s-test-app

# Add artifacts from builder
COPY --from=builder /work/target/universal/stage .

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
ENV TZ UTC

RUN apk add --no-cache tzdata curl bash tini && \
    chown -R daemon:daemon .

RUN chmod 755 -R /opt/http4s-test-app/bin/http4s-test-app

ENTRYPOINT ["/sbin/tini", "--"]
CMD ["bin/http4s-test-app"]
