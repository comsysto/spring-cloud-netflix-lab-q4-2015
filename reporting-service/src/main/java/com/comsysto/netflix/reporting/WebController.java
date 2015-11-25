package com.comsysto.netflix.reporting;

import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.common.model.Location;
import com.comsysto.netflix.common.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private DummyServiceClient dummyServiceClient;


    @RequestMapping("report")
    public String report() {
        Report report = createDummyReport();

        StringBuilder sb = new StringBuilder();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        sb.append("Report Date: ")
                .append(simpleDateFormat.format(report.getReportGenerationTime()))
                .append("\n");

        sb.append("more data to come...");
        return sb.toString();
    }


    public Report createDummyReport() {
        Map<DataType, BigInteger> set1 = new HashMap<DataType, BigInteger>();
        set1.put(DataType.TOTAL_SALES, BigInteger.ONE);

        Map<DataType, BigInteger> set2 = new HashMap<DataType, BigInteger>();
        set2.put(DataType.TOTAL_SALES, BigInteger.ZERO);

        Map<DataType, BigInteger> set3 = new HashMap<DataType, BigInteger>();
        set3.put(DataType.TOTAL_SALES, BigInteger.TEN);

        Map<Location, Map<DataType, BigInteger>> reportData = new HashMap();
        reportData.put(new Location("l1", "Munich", "Germany"), set1);
        reportData.put(new Location("l2", "Frankfurt", "Germany"), set2);
        reportData.put(new Location("l3", "Vienna", "Austria"), set3);
        return new Report(new Date(System.currentTimeMillis()), reportData);
    }
}
