package com.syxy.protocol.mqttImp;

import com.syxy.protocol.mqttImp.message.ConnAckMessage;
import com.syxy.protocol.mqttImp.message.ConnAckVariableHeader;
import com.syxy.protocol.mqttImp.message.ConnectMessage;
import com.syxy.protocol.mqttImp.message.ConnectPayload;
import com.syxy.protocol.mqttImp.message.ConnectVariableHeader;
import com.syxy.protocol.mqttImp.message.FixedHeader;
import com.syxy.protocol.mqttImp.message.Message;
import com.syxy.protocol.mqttImp.message.PackageIdVariableHeader;
import com.syxy.protocol.mqttImp.message.PublishMessage;
import com.syxy.protocol.mqttImp.message.PublishVariableHeader;
import com.syxy.protocol.mqttImp.message.SubAckMessage;
import com.syxy.protocol.mqttImp.message.SubAckPayload;
import com.syxy.protocol.mqttImp.message.SubscribeMessage;
import com.syxy.protocol.mqttImp.message.SubscribePayload;
import com.syxy.protocol.mqttImp.message.UnSubscribeMessage;
import com.syxy.protocol.mqttImp.message.UnSubscribePayload;

import io.netty.buffer.ByteBuf;

public final class MQTTMesageFactory {

    public static Message newMessage(FixedHeader fixedHeader, Object variableHeader, Object payload) {
        switch (fixedHeader.getMessageType()) {
            case CONNECT :
                return new ConnectMessage(fixedHeader, 
                		(ConnectVariableHeader)variableHeader, 
                		(ConnectPayload)payload);

            case CONNACK:
                return new ConnAckMessage(fixedHeader, (ConnAckVariableHeader) variableHeader);

            case SUBSCRIBE:
                return new SubscribeMessage(
                        fixedHeader,
                        (PackageIdVariableHeader) variableHeader,
                        (SubscribePayload) payload);

            case SUBACK:
                return new SubAckMessage(
                        fixedHeader,
                        (PackageIdVariableHeader) variableHeader,
                        (SubAckPayload) payload);

            case UNSUBSCRIBE:
                return new UnSubscribeMessage(
                        fixedHeader,
                        (PackageIdVariableHeader) variableHeader,
                        (UnSubscribePayload) payload);

            case PUBLISH:
                return new PublishMessage(
                        fixedHeader,
                        (PublishVariableHeader) variableHeader,
                        (ByteBuf) payload);

            case PUBACK:
            case UNSUBACK:
            case PUBREC:
            case PUBREL:
            case PUBCOMP:
                return new Message(fixedHeader, variableHeader);

            case PINGREQ:
            case PINGRESP:
            case DISCONNECT:
                return new Message(fixedHeader);

            default:
                throw new IllegalArgumentException("unknown message type: " + fixedHeader.getMessageType());
        }
    }

    private MQTTMesageFactory() { }
}