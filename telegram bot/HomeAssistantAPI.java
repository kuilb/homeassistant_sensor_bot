import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class HomeAssistantAPI {
    public static final String ip="192.168.8.115:8123";     //你的homeassistant的ip和端口号
    public static final String accees_api_key="";           //从homeassistant处获得的密钥
    public static final String indoor_temp_sensor="sensor.temperature_humidity_sensor_bb5a_temperature";        //传感器名称
    public static final String indoor_humi_sensor="sensor.temperature_humidity_sensor_bb5a_humidity";
    public static final String outdoor_temp_sensor="sensor.temperature_humidity_sensor_60d5_temperature";
    public static final String outdoor_humi_sensor="sensor.temperature_humidity_sensor_60d5_humidity";

    public static String indoor_temp_time,indoor_humi_time,outdoor_temp_time,outdoor_humi_time;
    public static float indoor_temp_stats,indoor_humi_stats,outdoor_temp_stats,outdoor_humi_stats;
    
    public static String get_url(String type){
        if(type=="indoor_temp")
            return "http://"+ip+"/api/states/"+indoor_temp_sensor;
        if(type=="indoor_humi")
            return "http://"+ip+"/api/states/"+indoor_humi_sensor;
        if(type=="outdoor_temp")
            return "http://"+ip+"/api/states/"+outdoor_temp_sensor;
        if(type=="outdoor_humi")
            return "http://"+ip+"/api/states/"+outdoor_humi_sensor;
        else
            return "http://192.168.8.115:8123/api";
    }

    public static void get_stats(String type,String stats){
        if(type=="indoor_temp")
            indoor_temp_stats=Float.parseFloat(stats);
        if(type=="indoor_humi")
            indoor_humi_stats=Float.parseFloat(stats);
        if(type=="outdoor_temp")
            outdoor_temp_stats=Float.parseFloat(stats);
        if(type=="outdoor_humi")
            outdoor_humi_stats=Float.parseFloat(stats);
    }

    public static String time_trans(String time){
        int year,mon,day,hour,min,sec;
        String s_year,s_mon,s_day,s_hour,s_min,s_sec;
        int m31[]={1,3,5,7,8,10,12};

        s_year=time.substring(0, 4);
        s_mon=time.substring(5,7);
        s_day=time.substring(8,10);
        s_hour=time.substring(11,13);
        s_min=time.substring(14,16);
        s_sec=time.substring(17,19);

        year=Integer.parseInt(s_year);
        mon=Integer.parseInt(s_mon);
        day=Integer.parseInt(s_day);
        hour=Integer.parseInt(s_hour);
        min=Integer.parseInt(s_min);
        sec=Integer.parseInt(s_sec);

        //System.out.println("Source time:UTC"+year+"/"+mon+"/"+day+"  "+hour+":"+min+":"+sec);

        hour+=8;
        if(hour>=24){
            hour-=24;
            day+=1;
        }
        if(day==29){
            if(mon==2 && (year%4!=0 || year%100==0)){
                day=1;
                mon++;
                return year+String.format("-%02d-%02d %02d:%02d:%02d", mon,day,hour,min,sec);
            }
        }
        if(day==30){
            if(mon==2 && year%4==0 && year%100!=0){
                day=1;
                mon++;
                return year+String.format("-%02d-%02d %02d:%02d:%02d", mon,day,hour,min,sec);
            }
        }
        else if(day==31){
            for(int i=0;i<=6;i++){
                if(mon==m31[i]){
                    return year+String.format("-%02d-%02d %02d:%02d:%02d", mon,day,hour,min,sec);
                }
            }
            day=1;
            mon++;
            return year+String.format("-%02d-%02d %02d:%02d:%02d", mon,day,hour,min,sec);
        }
        else if(day==32){
            day=1;
            mon++;
            return year+String.format("-%02d-%02d %02d:%02d:%02d", mon,day,hour,min,sec);
        }
        return year+String.format("-%02d-%02d %02d:%02d:%02d", mon,day,hour,min,sec);
    }

    public static void get_times(String type,String source_time){
        String time=time_trans(source_time);
        System.out.println("Trans time:"+time);
        System.out.println("--------------------------------");

        if(type=="indoor_temp")
            indoor_temp_time=time;
        if(type=="indoor_humi")
            indoor_humi_time=time;
        if(type=="outdoor_temp")
            outdoor_temp_time=time;
        if(type=="outdoor_humi")
            outdoor_humi_time=time;
    }

    public static void log_write(){
        try{
            System.out.println("log Write");

            OutputStream outputStream = new FileOutputStream("sensor.log");
            OutputStreamWriter file_out = new OutputStreamWriter(outputStream, "UTF-8");  

            file_out.write("室外温度:"+outdoor_temp_stats+"°C 最后更新:"+outdoor_temp_time+"\n");
            file_out.write("室外湿度:"+outdoor_humi_stats+"%  最后更新:"+outdoor_humi_time+"\n");
            file_out.write("室内温度:"+indoor_temp_stats +"°C 最后更新:"+indoor_temp_time+ "\n");
            file_out.write("室内湿度:"+indoor_humi_stats +"%  最后更新:"+indoor_humi_time+ "\n");
            file_out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void get_state(String type){
        System.out.println(type+"\n");
        try {
            URL obj = new URL(get_url(type));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // add request header
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer "+accees_api_key);
            con.setRequestProperty("Content-Type", "application/json");

            // send post request
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            String result=response.toString();
            String result_utf8=new String(result.getBytes(),"UTF8");
            //System.out.println(result_utf8);

            int index=result_utf8.indexOf("\"state\":\"");
            String stats=result_utf8.substring(index+9, index+13);
            System.out.println("stats:"+stats);

            index=result_utf8.indexOf("\"last_updated\":\"");
            String last_updatated=result_utf8.substring(index+16, index+35);
            System.out.println("last_updated:"+last_updatated);

            get_times(type, last_updatated);
            get_stats(type, stats);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            while(true){
                System.out.println("-----------------------");
                get_state("indoor_temp");
                get_state("indoor_humi");
                get_state("outdoor_temp");
                get_state("outdoor_humi");

                log_write();

                TimeUnit.SECONDS.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}