FROM python:3.11
WORKDIR /app
COPY . .
ENV PATH=$PATH:/root/.local/bin
RUN curl -sSL https://install.python-poetry.org | python3 - && \
    poetry install
ENTRYPOINT [ "poetry", "run" ]
CMD [ "sanic", "--host=0.0.0.0", "--port=3000", "--fast", "app:app" ]
