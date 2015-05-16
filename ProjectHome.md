This project has been abandoned. A slightly more recent version can be found on [GitHub](http://github.com/tadglines/Socket.IO-Java) along with a number of forks that contain various alternative versions. Also, check out [Atmosphere](https://github.com/Atmosphere/atmosphere) for an alternative.

This is a J2EE Servlet based [Socket.IO](http://github.com/learnboost/socket.io) server implementation.

At the moment all transports have been implemented.
Tested (with chat example and GWT chat example) browsers/transports:
  * Firefox 3.6.10 (flashsocket, xhr-multipart, xhr-polling, jsonp-polling)
  * Chrome 6 (websocket)
  * IE7 (flashsocket, htmlfile)
  * iPad (xhr-polling)

A GWT Client module has been added.

There are three examples: chat, echo, and broadcast. To run the chat example server use the "run-chat" ant target then browse to `http://localhost:8080/chat.html`

The echo example will echo any message on the socket on which the message was sent.
The broadcast example will take any message send and broadcast it to all connected sockets except the one that sent the message.