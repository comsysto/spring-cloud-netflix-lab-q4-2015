package com.comsysto.netflix.reporting;

import com.comsysto.netflix.common.model.Country;
import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.common.model.Report;
import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WebController {

    public static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy,  HH:mm:ss z";

    @RequestMapping("report")
    public String report() {
        Report report = createDummyReport();

        StringBuilder sb = new StringBuilder();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        sb.append("<html><body>");
        sb.append("<span>Report Date: ")
                .append(simpleDateFormat.format(report.getReportGenerationTime()))
                .append("</span><br/><br/>");

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


    public Report createDummyReport() {
        Map<DataType, BigInteger> set1 = new HashMap<>();
        set1.put(DataType.TOTAL_SOLD_AMOUNT, BigInteger.ONE);

        Map<DataType, BigInteger> set2 = new HashMap<>();
        set2.put(DataType.TOTAL_SOLD_AMOUNT, BigInteger.ZERO);

        Map<Country, Map<DataType, BigInteger>> reportData = Maps.newHashMap();
        reportData.put(new Country("Germany"), set1);
        reportData.put(new Country("Austria"), set2);
        return new Report(new Date(System.currentTimeMillis()), reportData);
    }
}
