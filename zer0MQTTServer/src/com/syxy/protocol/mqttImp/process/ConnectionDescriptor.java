/*
 * Copyright (c) 2012-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package com.syxy.protocol.mqttImp.process;

import com.syxy.server.ClientSession;

/**
 *  此类是每个客户端的会话，客户端ID，cleanSession的保存
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-7
 */
public class ConnectionDescriptor {
    
    private String clientID;
    private ClientSession client;
    private boolean cleanSession;
    
    public ConnectionDescriptor(String clientID, ClientSession session, boolean cleanSession) {
        this.clientID = clientID;
        this.client = session;
        this.cleanSession = cleanSession;
    }

    public String getClientID() {
		return clientID;
	}
    
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public ClientSession getClient() {
		return client;
	}

	public void setClient(ClientSession client) {
		this.client = client;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	@Override
    public String toString() {
        return "ConnectionDescriptor{" + "m_clientID=" + clientID + ", m_cleanSession=" + cleanSession + '}';
    }
}
