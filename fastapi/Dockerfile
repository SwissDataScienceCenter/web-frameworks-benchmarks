FROM python:3.11
WORKDIR /app
COPY . .
ENV PATH=$PATH:/root/.local/bin
RUN curl -sSL https://install.python-poetry.org | python3 - && \
    poetry install || echo "Some error"
ENTRYPOINT [ "poetry", "run" ]
CMD [ "uvicorn", "app:app", "--port=3000", "--host=0.0.0.0", "--workers=4" ]
