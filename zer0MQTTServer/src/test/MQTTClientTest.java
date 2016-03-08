package test;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.syxy.util.StringTool;

public class MQTTClientTest extends JFrame {  
    private static final long serialVersionUID = 1L;  
    private JPanel panel;  
    private JButton button;  
  
    private MqttClient client;  
    private String host = "tcp://10.0.1.8:8088";  
//  private String host = "tcp://iot.eclipse.org:1883";  
    private String userName = "test";  
    private String passWord = "test";  
    private MqttTopic topic;  
    private MqttMessage message;  
  
    private String myTopic = "test/topic";  
    String clientMac = StringTool.generalMacString();
  
    public MQTTClientTest() {  
  
        try { 
            client = new MqttClient(host, clientMac, new MemoryPersistence());  
            connect();
            client.subscribe(myTopic, 0);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
  
//        Container container = this.getContentPane();  
//        panel = new JPanel();  
//        button = new JButton(clientMac);  
//        button.addActionListener(new ActionListener() {  
//
//            public void actionPerformed(ActionEvent ae) {  
//                try {
//                    MqttDeliveryToken token = topic.publish(message);  
//                    token.waitForCompletion();  
//                    System.out.println(token.isComplete()+"========");  
//                } catch (Exception e) {  
//                    e.printStackTrace();  
//                }  
//            } 
//        });  
//        panel.add(button);  
//        container.add(panel, "North");  
    }  
  
    private void connect() {  
  
        MqttConnectOptions options = new MqttConnectOptions();  
        options.setCleanSession(false);  
        options.setUserName(userName);  
        options.setPassword(passWord.toCharArray());  
        options.setConnectionTimeout(10);    
        options.setKeepAliveInterval(5 * 60);  
        try {  
            client.setCallback(new MqttCallback() {  
  
                public void connectionLost(Throwable cause) {  
                    System.out.println("connectionLost-----------");  
                }  
  
                public void deliveryComplete(IMqttDeliveryToken token) { 
                    System.out.println("deliveryComplete---------"+token.isComplete());  
                }  
  
                public void messageArrived(String topic, MqttMessage arg1)  
                        throws Exception {  
                    System.out.println("messageArrived----------");  
                    System.out.println(topic+":"+arg1.toString());
  
                }  
            });  
            
            
            topic = client.getTopic(myTopic);  
            
            message = new MqttMessage();
            message.setQos(1);  
            message.setRetained(true);  
            System.out.println(message.isRetained()+"------ratained״̬");  
            message.setPayload("eeeeeaaaaaawwwwww---".getBytes());  
  
            client.connect(options); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    }  
  
    public static void main(String[] args) {
    	for (int i = 0; i < 1000; i++) {
    		MQTTClientTest s = new MQTTClientTest();
    		System.out.println(i);
		}
    		
//        s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
//        s.setSize(600, 370);  
//        s.setLocationRelativeTo(null);  
//        s.setVisible(true);  
    } 
}  