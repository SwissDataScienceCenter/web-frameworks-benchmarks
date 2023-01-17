FROM python:3.10
WORKDIR /app
COPY . .
ENV PATH=$PATH:/root/.local/bin
RUN curl -sSL https://install.python-poetry.org | python3 - && \
    poetry install || echo "Some error"
ENTRYPOINT [ "poetry", "run" ]
CMD [ "hypercorn", "app:app", "--bind=0.0.0.0:3000", "--workers=4" ]
