# Architecture
Below is the architecture of this project. It starts with an Angular frontend that connects with a Java backend through the API Gateway. Within the Java backend, there are three services, each with its own database.

The Post Service uses OpenFeign for synchronous communication with the Review Service and Comment Service. For example, when a GET request is made to fetch a post, the Post Service calls the Review Service and Comment Service via OpenFeign to retrieve the associated reviews and comments based on their IDs. It combines this data with the post details and sends the complete response back to the API Gateway.

The project also incorporates asynchronous communication using a RabbitMQ message bus. When a new comment or review is added to a post, a message is sent to the appropriate queue. The Post Service listens to these queues and updates the corresponding post in its database by linking the new comment or review ID, ensuring the changes are saved.

Additionally, the Java backend includes a Eureka Server for service discovery and a Config Server to manage configuration properties across the services. All services are connected to the message bus to facilitate asynchronous communication.

<img src="./architecture.png"/>

there is also a .drawio file to view the diagram in [draw.io](https://www.draw.io)
