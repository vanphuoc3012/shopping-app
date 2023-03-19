# e-commerce Webapp (Customer site)
## Architecture Overview:

![shop-system.png](shop-system.png)

- gateway-service: a module that use Spring Cloud Gateway for running. This module acts as a proxy/gateway and loadbalancer in our architecture.
- consul: a tool that provides Service Discover and Distributed Configuration (using the Consul Key/Value store)
- shop-service: main app, all logic appear here
- email-service: use for sending email
- rabbitmq: messge broker between shop-service and email-service, 
- redis: in-memory database to store session (to solve session sharing problem when adding more instance of shop-service)
- OracleDB: relational database
- logs: centralize logs from all services

### Containerization:
- Use Docker to build Docker image.

![img.png](img.png)

- This architecture design allows you easily scale up your system by adding more instances. Example: 
```
docker compose up --sacle shop-app=3 --scale shop-email=3
```
Running this docker compose command will create 3 instances of shop-service and 3 instances of email service.

## Feature
- Customer registration, verify customer email
- Sigin using Google, Facebook, or email
- Change customer information
- Adding more addresses
- Product catalog,  search for the product according to the specified criteria
- Shopping cart: add or delete product (for logged customer only)
- Order (support credit card)

## Result
Docker compose

![img_1.png](img_1.png)

Consul Service Discovery

![img_2.png](img_2.png)

Create an Order

![img_3.png](img_3.png)