FROM node:lts-alpine as node_cache
WORKDIR /cache
RUN apk add git

RUN git clone https://github.com/Mermade/widdershins /cache
RUN npm i

FROM slatedocs/slate:v2.13.1 as final

RUN apt update && \
    apt install -y nodejs npm curl

COPY --from=node_cache /cache /widdershins
COPY ./source /srv/slate/source
COPY ./build.sh ./build.sh
RUN chmod +x ./build.sh

WORKDIR /srv/slate

RUN mkdir ./pre-build && \
    rm ./source/index.html.md
EXPOSE 4567

ENTRYPOINT ["bash", "./build.sh"]
