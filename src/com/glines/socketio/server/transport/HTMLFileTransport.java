/**
 * The MIT License
 * Copyright (c) 2010 Tad Glines
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.glines.socketio.server.transport;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.ajax.JSON;

import com.glines.socketio.server.SocketIOFrame;
import com.glines.socketio.server.SocketIOSession;
import com.glines.socketio.server.transport.ConnectionTimeoutPreventor.IdleCheck;

public class HTMLFileTransport extends XHRTransport {
	public static final String TRANSPORT_NAME = "htmlfile";

	private class HTMLFileSessionHelper extends XHRSessionHelper {
		private final IdleCheck idleCheck;

		HTMLFileSessionHelper(SocketIOSession session, IdleCheck idleCheck) {
			super(session, true);
			this.idleCheck = idleCheck;
		}

		protected void startSend(HttpServletResponse response) throws IOException {
			response.setContentType("text/html");
			response.setHeader("Connection", "keep-alive");
			response.setHeader("Transfer-Encoding", "chunked");
			char[] spaces = new char[244];
			Arrays.fill(spaces, ' ');
			ServletOutputStream os = response.getOutputStream();
			os.print("<html><body>" + new String(spaces));
			response.flushBuffer();
		}
		
		protected void writeData(ServletResponse response, String data) throws IOException {
			idleCheck.activity();
			response.getOutputStream().print("<script>parent.s._("+ JSON.toString(data) +", document);</script>");
			response.flushBuffer();
		}

		protected void finishSend(ServletResponse response) throws IOException {};

		protected void customConnect(HttpServletRequest request,
				HttpServletResponse response) throws IOException {
			startSend(response);
			writeData(response, SocketIOFrame.encode(SocketIOFrame.FrameType.SESSION_ID, 0, session.getSessionId()));
			writeData(response, SocketIOFrame.encode(SocketIOFrame.FrameType.HEARTBEAT_INTERVAL, 0, "" + HEARTBEAT_DELAY));
		}
	}

	public HTMLFileTransport(int bufferSize, int maxIdleTime) {
		super(bufferSize, maxIdleTime);
	}

	@Override
	public String getName() {
		return TRANSPORT_NAME;
	}

	protected XHRSessionHelper createHelper(SocketIOSession session) {
		IdleCheck idleCheck = ConnectionTimeoutPreventor.newTimeoutPreventor();
		return new HTMLFileSessionHelper(session, idleCheck);
	}
}
