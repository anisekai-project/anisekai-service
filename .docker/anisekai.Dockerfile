FROM gradle:8.10.1-jdk17-alpine AS anisekai
WORKDIR /source
ADD .. .
RUN gradle clean build --no-daemon && rm -rf /source/build/libs/*-plain.jar && mv /source/build/libs/*.jar /app.jar && rm -rf /source

FROM openjdk:17.0.2-slim AS service
LABEL authors="akio"
WORKDIR /app

# Installing mkvtoolnix
RUN apt-get update && apt-get install -y wget xz-utils

RUN wget -O /usr/share/keyrings/gpg-pub-moritzbunkus.gpg https://mkvtoolnix.download/gpg-pub-moritzbunkus.gpg

RUN echo "deb [signed-by=/usr/share/keyrings/gpg-pub-moritzbunkus.gpg] https://mkvtoolnix.download/debian/ bullseye main" >> /etc/apt/sources.list.d/mkvtoolnix.list
RUN echo "deb-src [signed-by=/usr/share/keyrings/gpg-pub-moritzbunkus.gpg] https://mkvtoolnix.download/debian/ bullseye main" >> /etc/apt/sources.list.d/mkvtoolnix.list

RUN apt-get install -y mkvtoolnix && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

COPY --from=alexpado/ffmpeg:7.0.1 /var/opt/ffmpeg/binaries/ffmpeg /usr/bin/ffmpeg
COPY --from=anisekai /app.jar .

# Cleanup
RUN rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
ENTRYPOINT ["java", "-Djava.security.egdfile:/dev/./urandom", "-jar", "app.jar"]