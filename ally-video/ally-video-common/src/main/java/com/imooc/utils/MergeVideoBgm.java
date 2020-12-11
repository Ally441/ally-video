package com.imooc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allycoding
 * @Date: 2020/7/30 14:55
 * 视频和音乐合并
 */
public class MergeVideoBgm {

    //FFmpegExe程序所在路径
    private String ffmpegExe;

    public MergeVideoBgm(String ffmpegExe){
        super();
        this.ffmpegExe = ffmpegExe;
    }

    //1.ffmpeg.exe -i 6.mp4 -c:v copy -an text.mp4 先除去源视频的音频轨
    /**
     * 除去源视频的音频轨
     * @param videoInputPath
     * @return
     */
    public String clearCommand(String videoInputPath) throws IOException {
        List<String> command = new ArrayList<>();
        int start = videoInputPath.lastIndexOf("/");
        int end = videoInputPath.lastIndexOf(".");
        String videoName = videoInputPath.substring(start+1,end);
        String temporaryPath = videoInputPath.replace(videoName,"text");
        command.add(ffmpegExe);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-c:v");
        command.add("copy");
        command.add("-an");
        command.add(temporaryPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        this.isClose(bufferedReader,inputStreamReader,errorStream);
        return temporaryPath;
    }

    //2.ffmpeg.exe -i 2.mp4 -i 1.mp3 -t 7 -y 合成新视频.mp4

    /**
     * 音频和视频合并
     * @param videoInputPath
     * @param bgmPath
     * @param seconds
     * @param videoOutputPath
     * @throws IOException
     */
    public void convertor(String videoInputPath, String bgmPath, double seconds, String videoOutputPath) throws IOException {

        //2.ffmpeg.exe -i 2.mp4 -i 1.mp3 -t 7 -y 合成新视频.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegExe);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-i");
        command.add(bgmPath);
        command.add("-t");
        command.add(String.valueOf(seconds));
        command.add("-y");
        command.add(videoOutputPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader reader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        this.isClose(bufferedReader,reader,errorStream);
    }

    /**
     * 关闭错误临时文件流
     * @param bufferedReader
     * @param reader
     * @param errorStream
     * @throws IOException
     */
    public void isClose(BufferedReader bufferedReader, InputStreamReader reader, InputStream errorStream) throws IOException {
        String line = "";
        while ((line = bufferedReader.readLine()) != null){

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
    }

    public static void main(String[] args) throws IOException {
        MergeVideoBgm mergeVideoBgm = new MergeVideoBgm("E:\\ffmpeg-4.3\\bin\\ffmpeg.exe");
        String path = mergeVideoBgm.clearCommand("E:\\ffmpeg-4.3\\bin\\1.mp4");
        mergeVideoBgm.convertor(path,"E:\\ffmpeg-4.3\\bin\\1.mp3",7,"E:\\ffmpeg-4.3\\bin\\合成视频.mp4");
    }

}
