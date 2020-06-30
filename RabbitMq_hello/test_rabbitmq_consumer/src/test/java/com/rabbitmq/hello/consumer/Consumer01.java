package com.rabbitmq.hello.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author 王津
 * @Date 2020/6/30
 * @Version 1.0
 */
public class Consumer01 {

    private static final String QUEUE = "helloworld";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection =null;
        Channel channel = null;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.58.130");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");

            connection = factory.newConnection();
            channel = connection.createChannel();

            /**              
             *声明队列，如果Rabbit中没有此队列将自动创建          
             */
            channel.queueDeclare(QUEUE,true,false,false,null);

            //定义消费方法
            DefaultConsumer consumer = new DefaultConsumer(channel){
                /**              
               消费者接收消息调用此方法              
               * @param consumerTag 消费者的标签，在channel.basicConsume()去指定              
               * @param envelope消息包的内容,可从中获取消息id,消息routingkey,交换机,消息和重传标志 (收到消息失败后是否需要重新发送)
               * @param properties              
               * @param body              
               * @throws IOException
                */

                 @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                     //交换机
                     String exchange = envelope.getExchange();
                     //路由key
                     String routingKey = envelope.getRoutingKey();
                     //消息id
                     long deliveryTag = envelope.getDeliveryTag();
                     //消息内容
                     String msg = new String(body,"utf-8");

                     System.out.println("receive Message:"+msg);
                 }
            };
            /**          
            监听队列String queue, boolean autoAck,Consumer callback          
            * 参数明细          
            * 1、队列名称          
            * 2、是否自动回复，设置为true为表示消息接收到自动向mq回复接收到了，
             *  mq接收到回复会删除消息，设置 为false则需要手动回复          
            * 3、消费消息的方法，消费者接收到消息后调用此方法          
            */
            channel.basicConsume(QUEUE, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (channel!=null){
                channel.close();
            }
            if (connection!=null){
                connection.close();
            }
        }

    }
}
