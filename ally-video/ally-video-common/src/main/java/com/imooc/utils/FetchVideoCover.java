package com.imooc.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取视频封面
 * @author allycoding
 * @Date: 2020/8/6 14:56
 */
public class FetchVideoCover {

    private String ffmpegExe;

    public FetchVideoCover(String Path){
        super();
        this.ffmpegExe = Path;
    }

    //ffmpeg.exe -ss 00:00:01 -y -i 2.mp4 -vframes 1 new.jpg
    /**
     * 获取视频第一秒的界面
     * @param videoInputPath
     * @param coverOutputPath
     * @throws IOException
     */
    public void getCover(String videoInputPath, String coverOutputPath) throws IOException{
        List<String> command = new ArrayList<>();
        command.add(ffmpegExe);
        command.add("-ss");
        command.add("00:00:00");
        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);
        command.add("-vframes");
        command.add("1");
        command.add(coverOutputPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader reader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        while((line = bufferedReader.readLine()) != null){
        }
        if(bufferedReader != null){
            bufferedReader.close();
        }
        if(reader != null){
            reader.close();
        }
        if(bufferedReader != null){
            bufferedReader.close();
        }
    }

    public static void main(String[] args) throws IOException {
        String videoPath ="E:\\ffmpeg-4.3\\bin\\2.mp4";
        String jpgPath = "E:\\ffmpeg-4.3\\bin\\text.jpg";
        FetchVideoCover fetchVideoCover = new FetchVideoCover("E:\\ffmpeg-4.3\\bin\\ffmpeg.exe");
        fetchVideoCover.getCover(videoPath,jpgPath);
    }
}
