# Introduction #

```
interface MessageParser {
	// Decodes text into an application specific format (e.g. Java script object)
	Object decode(String message);
	
	// Encodes application specific value into message text
	String encode(Object message);
};

interface io.Socket {
	// Socket state
	const unsigned int CONNECTING = 0;
	const unsigned int CONNECTED = 1;
	const unsigned int CLOSING = 2;
	const unsigned int CLOSED = 3;

	// Disconnect Reason
	const unsigned int DR_CONNECT_FAILED = 1;
	const unsigned int DR_DISCONNECT = 2;
	const unsigned int DR_TIMEOUT = 3;
	const unsigned int DR_CLOSE_FAILED = 4;
	const unsigned int DR_ERROR = 5;
	const unsigned int DR_CLOSED_REMOTELY = 6;
	const unsigned int DR_CLOSED = 7;

	// Event Types
	const String CONNECT_EVENT = 'connect';
	const String DISCONNECT_EVENT = 'disconnect';
	const String MESSAGE_EVENT = 'message';

	// Message Types
	const unsigned int TEXT_MESSAGE = 0;
	const unsigned int JSON_MESSAGE = 1;

	// Return current socket state
	attribute int socketState;

	// Initiate connection.
	// If the connect fails, the DISCONNECT_EVENT will be fires with a disconnect reason of
	// CONNECT_FAILED.
	// Throws an exception if the socketState is not CLOSED.
	void connect();

	// Brutally disconnect, discarding any unsent messages.
	void disconnect();
	
	// Initiate an orderly close.
	// A successful close means that all sent messages where received by the server.
	// Throws an exception if the socketState is not CONNECTED.
	void close();

	// Add an event handler
	void on(String event_type, Function handler);

	// Remove an event handler
	void removeEvent(String event_type, Function handler);

	// Send a plain text message.
	// throws an exception if the socketState is not CONNECTED.
	void send(DOMString message);
	
	// Send a message. If the message is not a sting and there is no registered
	// parser for the message type, then an exception will be thrown.
	// If there is a parser and it fails, an exception will be thrown.
	// If the value of messagetype is not in the range 0-255, an exception will be thrown.
	// Throws an exception if the socketState is not CONNECTED.
	void send(int messagetype, Object message);

	// If the value of messagetype is not in the range 0-255, an exception will be thrown.
	void setMessageParser(int messagetype, MessageParser parser);
};
```

# Codes #

## Socket State ##

| **Name** | **Code** | **Description** |
|:---------|:---------|:----------------|
| CONNECTING | 0 | A connection attempt has been initiated |
| CONNECTED | 1 | The socket is connected |
| CLOSING | 2 | The socket is attempting an orderly close |
| CLOSED | 3 | The socket is closed/disconnected |

## Disconnect Reason ##

| **Name** | **Code** | **Description** |
|:---------|:---------|:----------------|
| CONNECT\_FAILED | 1 | A connection attempt failed. |
| DISCONNECT | 2 | The disconnect method was called, or the connection was lost. |
| TIMEOUT | 3 | The connection timed out prior to close initiation. |
| CLOSE\_FAILED | 4 | A close attempt failed prior to reaching CLOSE\_SIMPLE state. |
| ERROR | 5 | Some sort of error occurred that resulted in a disconnect. |
| CLOSED\_REMOTELY | 6 | Disconnected due to a server initiated a close. |
| CLOSED | 7 | An orderly close completed successfully. |

# Events #

## CONNECT\_EVENT ##

Indicates that the connection has been established.

## DISCONNECT\_EVENT ##

Indicates that the connection was disconnected or failed to connect.

| **Arg** | **Value** |
|:--------|:----------|
| First | Disconnect Reason |
| Second | If present is an error message indicating the reason for the disconnect |

## MESSAGE\_EVENT ##

| **Arg** | **Value** |
|:--------|:----------|
| First | The message type |
| Second | The message object |
| Third | If present is the exception thrown by the message parser |

The message event handler will be given 2 or three arguments. the first will be the message type.
If none was specified by the sender then the message type will be 0 (text). The second argument is
the message contents. If there is a message parser registered for the message type then the message
will be the result of the decode if the decode succeeds, or the original message contents if the
parse failed. If the third argument is provided, then it is the exception, that was thrown by the
parser.