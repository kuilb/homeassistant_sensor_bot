//用于内网显示屏

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
public class Temp_1602 {
    public static String URLstream="http://192.168.8.125";

    public static void send_temp(String temp_url){
        try{
            // 创建URL对象
            URL url = new URL(temp_url);
                
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // 设置请求方法
            connection.setRequestMethod("GET");
            
            // 获取响应代码
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws InterruptedException {   
        String temp=" ";
        String semi=" ";
        String temp_time=" ";
        String semi_time=" ";

        String file_path="C:/Users/kulib/Desktop/tools/homeassistant_sensor_bot/telegram bot/sensor.log";
        while(true){
            try{
                InputStreamReader isr=new InputStreamReader(new FileInputStream(file_path),"UTF-8");
                BufferedReader br = new BufferedReader(isr);

                String line;

                // 逐行读取文件内容
                line = br.readLine();
                System.out.println(line);  // 输出每一行
                temp=line.substring(5, 9);
                temp_time=line.substring(28, 33);

                line = br.readLine();
                System.out.println(line);  // 输出每一行
                semi=line.substring(5, 10);
                semi_time=line.substring(28, 33);

            }catch (IOException e) {
                // 处理文件操作异常
                e.printStackTrace();
            }

            System.out.println(temp+" "+semi);
            System.out.println(temp_time+" "+semi_time);

            send_temp("http://192.168.8.125/?inputbox=temp:"+temp+"C "+temp_time+"semi:"+semi+" "+semi_time);
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
