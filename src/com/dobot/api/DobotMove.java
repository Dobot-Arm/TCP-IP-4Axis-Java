package com.dobot.api;


import com.alibaba.fastjson.JSONArray;
import com.dobot.api.entity.JointMovJEntity;
import com.dobot.api.entity.MovJEntity;
import com.dobot.api.entity.MovLEntity;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DobotMove {
    private Socket socketClient = new Socket();

    private final static String SEND_ERROR = ":send error";

    private String ip = "";
    public boolean connect(String ip,Integer port){
        boolean ok = false;
        try {
            socketClient = new Socket(ip,port);
            this.ip = ip;
            socketClient.setSoTimeout(5000);
            Logger.instance.log("DobotMove connect success");
            ok = true;
        } catch (Exception e) {
            Logger.instance.log("Connect failed:" + e.getMessage());
        }
        return ok;
    }

    public void disconnect(){
        if(!socketClient.isClosed()){
            try {
                socketClient.shutdownOutput();
                socketClient.shutdownInput();
                socketClient.close();
                Logger.instance.log("DobotMove closed");
            } catch (IOException e) {
                Logger.instance.log("DobotMove Close Socket Exception:" + e.getMessage());
            }
        }else {
            Logger.instance.log("this ip is not connected");
        }
    }


    /// <summary>
    /// 关节点动运动，不固定距离运动
    /// </summary>
    /// <param name="s">点动运动轴</param>
    /// <returns>返回执行结果的描述信息</returns>
    public String moveJog(String s)
    {
        if (socketClient.isClosed())
        {
            return "device does not connected!!!";
        }

        String str;
        if (s == null || s.isEmpty())
        {
            str = "MoveJog()";
        }
        else
        {
            String strPattern = "^(J[1-6][+-])|([XYZR][+-])$";
            if (s.matches(strPattern))
            {
                str = "MoveJog(" + s + ")";
            }
            else
            {
                return "send error:invalid parameter!!!";
            }
        }
        if (!sendData(str))
        {
            return str + SEND_ERROR;
        }

        return waitReply(5000);
    }
    /// <summary>
    /// 停止关节点动运动
    /// </summary>
    /// <returns>返回执行结果的描述信息</returns>
    public String stopMoveJog()
    {
        return moveJog(null);
    }

    public String MovJ(MovJEntity movJEntity){
        if (socketClient.isClosed())
        {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        StringBuilder str = new StringBuilder();
        str.append("MovJ("+movJEntity.X+","+movJEntity.Y+","+movJEntity.Z+","+movJEntity.R+")");
        if(movJEntity.User != null){
            str.append(",User=" + movJEntity.User);
        }
        if(movJEntity.Tool != null){
            str.append(",Tool=" + movJEntity.Tool);
        }
        if(movJEntity.SpeedJ != null){
            str.append(",SpeedJ=" + movJEntity.SpeedJ);
        }
        if(movJEntity.AccJ != null){
            str.append(",AccJ=" + movJEntity.AccJ);
        }
        if(!sendData(str.toString())){
            return str + SEND_ERROR;
        }
        return waitReply(5000);
    }

    public String MovL(MovLEntity movLEntity){
        if (socketClient.isClosed())
        {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        StringBuilder str = new StringBuilder();

        str.append("MovL("+movLEntity.X+","+movLEntity.Y+","+movLEntity.Z+","+movLEntity.R+")");
        if(movLEntity.User != null){
            str.append(",User=" + movLEntity.User);
        }
        if(movLEntity.Tool != null){
            str.append(",Tool=" + movLEntity.Tool);
        }
        if(movLEntity.SpeedL != null){
            str.append(",SpeedL=" + movLEntity.SpeedL);
        }
        if(movLEntity.AccL != null){
            str.append(",AccL=" + movLEntity.AccL);
        }
        if(!sendData(str.toString())){
            return str + SEND_ERROR;
        }
        return waitReply(5000);
    }


    public String JointMovJ(JointMovJEntity jointMovJEntity){
        if (socketClient.isClosed())
        {
            return "device does not connected!!!";
        }
        StringBuilder str = new StringBuilder();
        str.append("JointMovJ("+jointMovJEntity.J1+","+jointMovJEntity.J2+","+jointMovJEntity.J3+","+jointMovJEntity.J4+")");
        if(jointMovJEntity.User != null){
            str.append(",User=" + jointMovJEntity.User);
        }
        if(jointMovJEntity.Tool != null){
            str.append(",Tool=" + jointMovJEntity.Tool);
        }
        if(jointMovJEntity.SpeedJ != null){
            str.append(",SpeedJ=" + jointMovJEntity.SpeedJ);
        }
        if(jointMovJEntity.AccJ != null){
            str.append(",AccJ=" + jointMovJEntity.AccJ);
        }
        if(!sendData(str.toString())){
            return str + SEND_ERROR;
        }
        return waitReply(5000);
    }




    public String waitReply(int timeout){
        String reply = "";
        try {
            if(socketClient.getSoTimeout() != timeout){
                socketClient.setSoTimeout(timeout);
            }
            byte[] buffer = new byte[1024];				//缓冲
            int len = socketClient.getInputStream().read(buffer);//每次读取的长度（正常情况下是1024，最后一次可能不是1024，如果传输结束，返回-1）
            reply= new String(buffer,0,len,"UTF-8");
            ErrorInfoHelper.getInstance().parseResult(reply);
            Logger.instance.log("Receive from:"+this.ip+":"+socketClient.getPort()+":"+reply);
        } catch (IOException e) {
            Logger.instance.log("Exception"+e.getMessage());
            return reply;
        }
        return reply;
    }

    public boolean sendData(String str){
        try {
            Logger.instance.log("Send to:" +this.ip+":"+socketClient.getPort()+":"+str);
            socketClient.getOutputStream().write((str).getBytes());
        } catch (IOException e) {
            Logger.instance.log("Exception:" + e.getMessage());
            return false;
        }
        return true;
    }




}