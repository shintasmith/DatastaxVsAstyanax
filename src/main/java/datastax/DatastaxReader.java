package datastax;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Select;
import utils.Metrics;
import java.util.List;

/**
 * Created by GauravBajaj on 8/15/15.
 */
public class DatastaxReader {
    private final static Timer batchReadDurationTimer = Metrics.timer(DatastaxReader.class, "Datastax Batch Read Duration");
    private final static Meter readMeter = Metrics.meter(DatastaxReader.class, "Datastax Read Operations");

    public static void readMetrics(int limit) {
        Select select = DatastaxQueryBuilder.getDummyMetrics(limit);
        readMetrics(limit, select);
    }

    public static void readAstyanaxMetrics(int limit) {
        Select select = DatastaxQueryBuilder.getAstyanaxMetrics(limit);
        readMetrics(limit, select);
    }

    private static void readMetrics(int limit, Select select){
        Session session = DatastaxIO.getSession();
        final Timer.Context actualReadCtx = batchReadDurationTimer.time();
        long datastaxStartTime = System.currentTimeMillis();
        ResultSetFuture future = session.executeAsync(select);
        boolean listen = true;
        while(listen) {
            if (future.isDone()){
                readMeter.mark();
                actualReadCtx.stop();
                long datastaxEndTime = System.currentTimeMillis();
                System.out.println("Datastax Batch Read Execution Time " + (datastaxEndTime - datastaxStartTime));
                listen = false;
            }
        }
        try {
            List<Row> results = future.get().all();
            System.out.println("Results size=" + results.size());
            for (Row row : results) {
                System.out.printf( "%s: %s - %s\n",
                        row.getString("key"),
                        row.get(1, TypeCodec.bigint()),
                        row.getBytes("value") );
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}