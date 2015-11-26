package com.comsysto.netflix.loadtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@RestController
public class WebController {
    private TaskExecutor taskExecutor;
    private List<Integer> resultList;


    public WebController() {
    }

    @Autowired
    public WebController(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        resultList = new ArrayList<Integer>();
    }

    @RequestMapping("run")
    public String handleCall() throws IOException {


        for(int i = 0; i < 25; i++) {
            taskExecutor.execute(new TestingRunnable(resultList));
        }

        fetchSingleValue();

        return createStatistics(resultList);
    }

    private String fetchSingleValue() {



        try {
            TestingRunnable testingRunnable = new TestingRunnable(resultList);
            Thread thread = new Thread(testingRunnable);
            thread.start();
            thread.join();
            return resultList.get(0).toString();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "error";
    }


    private String createStatistics(List<Integer> results) {
        StringBuilder sb = new StringBuilder();

        OptionalDouble average = results.stream().mapToInt(w -> w.intValue()).average();
        OptionalInt min = results.stream().mapToInt(w -> w.intValue()).min();
        OptionalInt max = results.stream().mapToInt(w -> w.intValue()).max();

        sb.append("fired " + results.size() + " requests");
        sb.append("<br>");
        sb.append("average: " + average);
        sb.append("<br>");
        sb.append("max: " + max);
        sb.append("<br>");
        sb.append("min: " + min);
        sb.append("<br>");

        return sb.toString();
    }
}
