FROM python:3.10
WORKDIR /app
COPY . .
ENV PATH=$PATH:/root/.local/bin
RUN curl -sSL https://install.python-poetry.org | python3 - && \
    poetry install
ENTRYPOINT [ "poetry", "run" ]
CMD [ "gunicorn", "--workers=4", "-b 0.0.0.0:3000", "app:app" ]
