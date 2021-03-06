FROM python:3.8.10-buster

RUN apt-get update \
&& apt-get install -y --no-install-recommends git \
&& apt-get purge -y --auto-remove \
&& rm -rf /var/lib/apt/lists/*

WORKDIR /root/

ENV VIRTUAL_ENV=/root/venv
RUN python -m venv $VIRTUAL_ENV
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

COPY requirements.txt .
COPY src src
COPY data data

RUN mkdir output &&\
    python -m pip install --upgrade pip &&\
    pip install -r requirements.txt

RUN python -m src.training_classifier_mybag &&\
    python -m src.training_classifier_tfidf

EXPOSE 8080

ENTRYPOINT [ "python", "-m" ]
CMD ["src.serve_models"]