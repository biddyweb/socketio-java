
```
public abstract class SocketIOServlet extends GenericServlet {
    /**
     * Returns an instance of SocketIOInbound or null if the connection is to be denied.
     * The value of cookies and protocols may be null.
     */
	protected abstract SocketIOInbound doSocketIOConnect(Cookie[] cookies,
			String host, String origin, String[] protocols);
}

public interface SocketIOInbound {
	interface SocketIOOutbound {
		/**
		 * Terminate the connection. This method may return before the connection disconnect
		 * completes. The onDisconnect() method of the associated SocketInbound will be called
		 * when the disconnect is completed. The onDisconnect() method may be called during the
		 * invocation of this method.
		 */
		void disconnect();

		/**
		 * Initiate an orderly close of the connection. The state will be changed to CLOSING so no
		 * new messages can be sent, but messages may still arrive until the distant end has
		 * acknowledged the close.
		 * 
		 * @param closeType
		 */
		void close();
		
		ConnectionState getConnectionState();

		/**
		 * Send a message to the client. This method will block if the message will not fit in the
		 * outbound buffer.
		 * If the socket is closed, becomes closed, or times out, while trying to send the message,
		 * the SocketClosedException will be thrown.
		 *
		 * @param message The message to send
		 * @throws SocketIOException
		 */
		void sendMessage(String message) throws SocketIOException;
		
		/**
		 * Send a message.
		 * 
		 * @param message
		 * @throws IllegalStateException if the socket is not CONNECTED.
		 * @throws SocketIOMessageParserException if the message type parser encode() failed.
		 */
		void sendMessage(int messageType, String message) throws SocketIOException;
	}

	/**
	 * Return the name of the protocol this inbound is associated with.
	 * This is one of the values provided by
	 * {@link SocketIOServlet#doSocketIOConnect(HttpServletRequest, String[])}.
	 * @return
	 */
	String getProtocol();
	
	/**
	 * Called when the connection is established. This will only ever be called once.
	 * @param outbound The SocketOutbound associated with the connection
	 */
	void onConnect(SocketIOOutbound outbound);
	
	/**
	 * Called when the socket connection is closed. This will only ever be called once.
	 * This method may be called instead of onConnect() if the connection handshake isn't
	 * completed successfully.
	 * @param reason The reason for the disconnect.
	 * @param errorMessage Possibly non null error message associated with the reason for disconnect.
	 */
	void onDisconnect(DisconnectReason reason, String errorMessage);


	/**
	 * Called one per arriving message.
	 * @param messageType
	 * @param message
	 * @param parseError
	 */
	void onMessage(int messageType, String message);
}
```