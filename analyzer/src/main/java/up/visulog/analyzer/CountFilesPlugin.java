package up.visulog.analyzer;

import up.visulog.config.Configuration;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CountFilesPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountFilesPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }


    //including hidden files
    public static Result getAllFilesCountOfProject(Path gitPath) {
        var result = new Result();
        File Root=gitPath.toFile();
        result.NumberOfFiles=getAllFilesCount(Root);
        return result;
    }

    public static int getAllFilesCount(File file) {
        File[] files = file.listFiles();
        int count = 0;
        if(files==null) parseError();
        for (File f : files) {
            if (f.isDirectory())
                count += getAllFilesCount(f);
            else
                count++;
        }
        return count;
    }


    //Path gitPath = FileSystems.getDefault().getPath(".");
    public static Result getFilesCountOfProject(Path gitPath) {
        var result = new Result();
        File Root=gitPath.toFile();
        result.NumberOfFiles=getFilesCount(Root);
        return result;
    }
    public static int getFilesCount(File file) {
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isHidden();
            }
        });
        int count = 0;
        if(files==null) parseError();
        for (File f : files) {
            if (f.isDirectory())
                count += getFilesCount(f);
            else
                count++;
        }
        return count;
    }

    @Override
    public void run() {
        result =getFilesCountOfProject(configuration.getGitPath());
    }

    @Override
    public Result getResult() {
        //If the analysis hasn't already been run, it is run and only then is the result returned
        if (result == null) run();
        return result;
    }


    private static void parseError() {
        throw new RuntimeException("Can't read files");
    }

    static class Result implements AnalyzerPlugin.Result {
        private Integer NumberOfFiles=null;

        //Method that returns the hashmap that contains the number of commits associated with each author
        Integer getNumberOfFiles() {
            return NumberOfFiles;
        }

        @Override
        //Method that returns the commitsPerAuthor list in String form
        public String getResultAsString() {
            return NumberOfFiles.toString();
        }

        @Override
        //Method that returns a String which can then be used to display the commitsPerAuthor list as an html page
        public String getResultAsHtmlDiv() {
            return "<div class=\"module\" hidden>fileCounter</div><div id=\"data-file-number\" hidden><div data-file-number=" + NumberOfFiles + "></div></div>";
        }
    }
}


