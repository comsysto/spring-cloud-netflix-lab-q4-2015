package com.comsysto.netflix.reporting;

import com.comsysto.netflix.common.model.Country;
import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.common.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Map;

@RestController
public class WebController {

    public static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy,  HH:mm:ss z";
    
    @Autowired
    private AggregationServiceClient aggregationServiceClient;

    @RequestMapping("report")
    public String report() {
        Report report = aggregationServiceClient.getReport();

        StringBuilder sb = new StringBuilder();

        long ageInMilliseconds = System.currentTimeMillis() - report.getReportGenerationTime().getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        sb.append("<html><body>");
        sb.append("<span>Report Date: ")
                .append(simpleDateFormat.format(report.getReportGenerationTime()))
                .append("</span><br/><br/>");
        sb.append(String.format("<div style=\"background: rgba(220,0,0,%s)\">Report Age in Milliseconds: ", ageInMilliseconds / 20000.0))
                .append("<span id=\"reportAge\">")
                .append(ageInMilliseconds)
                .append("</span>")
                .append("</div><br/><br/>");

        sb.append("<table border=\"1\">");
        for (Map.Entry<Country, Map<DataType, BigInteger>> line : report.getReportData().entrySet()) {
            sb.append("<tr>");
            sb.append("<td>").append(line.getKey().getName()).append("</td>");
            for (Map.Entry<DataType, BigInteger> dataCell : line.getValue().entrySet()) {
                sb.append("<td>").append(dataCell.getKey()).append(": ").append(dataCell.getValue()).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
