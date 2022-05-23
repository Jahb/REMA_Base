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

RUN python -m pip install --upgrade pip &&\
    pip install -r requirements.txt

#TODO: Add more functionalities
EXPOSE 8080

CMD "bash"