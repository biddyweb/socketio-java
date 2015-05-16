# Introduction #

This is an attempt to describe the protocol between the Socket.IO client and server.
**NOTE: This is a draft and is not yet implemented.**

# Protocol #

The current Web Sockets interface spec for the browser allows simple text messages to be passed
between browser and server. Because Socket.IO has several control messages and needs to be able to
package several text messages together for transport a message encoding format is needed.

Here's the message encoding in [ABNF](http://tools.ietf.org/html/rfc5234) form:
```
message-block = message *message ; A block of one or more messages

message       = "~" opcode "~" size "~" data ; A single message

opcode        = control-code       ; The message opcode
              / "E" [message-type] ; Application text data frame
              / "F" [message-type] ; Application text data frame fragment

control-code  = "0" ; Close
              / "1" ; Session ID
              / "2" ; Timeout
              / "3" ; Ping
              / "4" ; Pong

message-type  = "0" ; Plain Text. This is the assumed value if not present.
              / "1" ; JSON
              / 1*2HEXDIG

size          = 1*HEXDIG ; The number of UTF-8 characters in the data.

data          = *( UTF8-char ); UTF-8 encoded string
```

## Protocol Semantics ##

### Connection Startup ###

When the client first connects to the server the first message the server sends is the Session ID.
It may follow this with a Timeout message. This message, if sent, contains the maximum number of
milliseconds the client can expect to wait before receiving a message from the server.

### Keep Alive Mechanism ###

For persistent connection transports something must sent periodically or the browser (or server)
will close the connection. The Ping and Pong messages are use for the persistent connection
transports so that the client and server can detect lost or prohibitively lagged connectivity.
If the sender of the Ping message does not received a Pong within the configured timeout then
the connection is considered closed.

When an endpoint receives a Ping message, it is expected to send a Pong reply immediately.
This may mean placing the Pong message at the head of the send buffer instead of at the tail.
The contents of the Ping message must be echoed in the Pong message.

### Closing the session ###

An an end point may wish to ensure that the last send message makes it to the other end before
terminating the connection. This is initiated by sending a Close message. Once sent, the local
end point waits for the remote end point to send a reply Close message before disconnecting.
In order ensure that the received Close is an acknowledgment and not a result of the remote
end point simultaneously initiating a close, the initial close message needs to contain a unique
value this then can be matched with the value of the received close message. The simplest approach
is for the client to use the value "client" and the server to use the value "server".

NOTE: The server especially needs to timeout the connection if, after initiating a close, the client
continues sending data and doesn't send the reply close fast enough. Otherwise the client could keep
the connection open indefinitely.

## Transports ##

There are 6 transports available to the Socket.IO client. The client selects one based on the
browser it's running in and other factors.

The transports are:
  * websocket - Web Sockets
  * flashsocket - Flash based Web Sockets
  * htmlfile - A persistent connection transport.
  * xhr-multipart - A persistent connection transport.
  * xhr-polling - A long polling transport.
  * jsonp-polling - A long polling transport.
