FROM consul:latest
COPY ./consul-shop-app.json /usr/src/consul/
WORKDIR /usr/src/consul/
ENV CONSUL_HTTP_ADDR=consul-shop-app:8500
ENTRYPOINT until consul kv import @consul-shop-app.json; do echo "Waiting for Consul"; sleep 2; done