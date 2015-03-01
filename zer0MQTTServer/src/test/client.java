package test;

import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class client extends Frame implements ActionListener{

Label label=new Label("交谈");
Panel panel=new Panel();
TextField tf=new TextField(10);
TextArea ta=new TextArea();
Socket client;
InputStream in;
OutputStream out;

public client() {
super("客户端");
setSize(250,250);
panel.add(label);
panel.add(tf);
tf.addActionListener(this);
add("North",panel);
add("Center",ta);
addWindowFocusListener(new WindowAdapter() {
public void windowClosing(WindowEvent e) {
// TODO Auto-generated method stub
System.exit(0);
}
});
setVisible(true);

try {
client=new Socket(InetAddress.getLocalHost(), 8088); //因为是在自己的机器上使用所以用InetAddress的静态方法getLocalHost方法得到主机
ta.append("已连接到服务器："+client.getInetAddress().getHostName()+"\n\n");
in=client.getInputStream();
out=client.getOutputStream();
} catch (Exception e) {
// TODO: handle exception
e.printStackTrace();
}

while(true) {
try {
byte[] buf=new byte[256];
in.read(buf);
String str=new String(buf);
ta.append("服务器说："+str);
ta.append("\n");
} catch (Exception e) {
// TODO: handle exception
e.printStackTrace();
}
}
}

@Override
public void actionPerformed(ActionEvent e) {
// TODO Auto-generated method stub
try {
String str=tf.getText();
byte[] buf=str.getBytes();
out.write(buf);
tf.setText("");
ta.append("我说："+str);
ta.append("\n");
} catch (Exception e2) {
// TODO: handle exception
e2.printStackTrace();
}
}

public static void main(String[] args) {
// TODO Auto-generated method stub
new client();
}
}