package com.imooc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allycoding
 * @Date: 2020/7/28 0:31
 */
public class FFMpegTest {

    //FFmpegExe程序所在路径
    private String ffmpegEXE;

    public FFMpegTest(String path){
        super();
        this.ffmpegEXE = path;
    }



    public void convertor(String videoInputPath, String videoOutputPath){
        //ffmpeg -i 1.mp4 2.avi
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add(videoOutputPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try{
            Process process = processBuilder.start();
            InputStream errorStream = process.getErrorStream();
            InputStreamReader reader = new InputStreamReader(errorStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = "";
            while((line = bufferedReader.readLine()) != null){

            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(reader != null){
                reader.close();
            }
            if(errorStream != null){
                errorStream.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        FFMpegTest ffmpeg = new FFMpegTest("E:\\ffmpeg-4.3\\bin\\ffmpeg.exe");
        ffmpeg.convertor("C:\\Users\\Ally\\Desktop\\1.mp4","C:\\Users\\Ally\\Desktop\\4.avi");

    }
}
