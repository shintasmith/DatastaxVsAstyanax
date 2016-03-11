package datastax;

import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.TypeCodec;
import com.datastax.driver.core.querybuilder.Batch;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import utils.Constants;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Created by GauravBajaj on 8/10/15.
 */
public class DatastaxQueryBuilder{
    protected static Batch getFullMetricsBatch(){
        return QueryBuilder.batch();
    }
    private static int ttl = 172800; //2 days

    protected static Batch addDummyBatchOfMetrics(int size){
        Batch batch = getFullMetricsBatch();
        for(int i=0; i<size;i++) {

            String metricName = "datastax.int.abcdefg.hijklmnop.qrstuvw.xyz.ABCDEFG.HIJKLMNOP.QRSTUVW.XYZ.abcdefg.hijklmnop.qrstuvw.xyz.met." + i;
            Insert insertMetric = QueryBuilder
                    .insertInto("\"DATA\"", "metrics_full")
                    .value("key", metricName)
                    .value("column1", System.currentTimeMillis())
                    .value("value", TypeCodec.cdouble().serialize(new Random().nextDouble(), ProtocolVersion.V3));
            insertMetric.using(QueryBuilder.ttl(ttl));
            batch.add(insertMetric);
        }
        return batch;
    }

    protected static Insert addDummyMetric(){
        String metricName = "datastax.int.abcdefg.hijklmnop.qrstuvw.xyz.ABCDEFG.HIJKLMNOP.QRSTUVW.XYZ.abcdefg.hijklmnop.qrstuvw.xyz.met." + (new Random().nextInt(20 - 1) + 1);

        Insert insertMetric = QueryBuilder
                .insertInto("\"DATA\"", "metrics_full")
                .value("key", metricName)
                .value("column1", System.currentTimeMillis())
                .value("value",TypeCodec.cdouble().serialize(new Random().nextDouble(), ProtocolVersion.V3));

        return insertMetric;
    }

    protected static Select getDummyMetrics(int limit) {
        Select statement = QueryBuilder
                .select()
                .all()
                .from("\"DATA\"", "metrics_full")
                .where(QueryBuilder.eq("key", "datastax.int.abcdefg.hijklmnop.qrstuvw.xyz.ABCDEFG.HIJKLMNOP.QRSTUVW.XYZ.abcdefg.hijklmnop.qrstuvw.xyz.met." +
                        (new Random().nextInt((2 * Constants.WRITE_BATCH_SIZE - 1) - Constants.WRITE_BATCH_SIZE) + Constants.WRITE_BATCH_SIZE)))
                .and(QueryBuilder.gt("column1", System.currentTimeMillis() - ttl * 1000))
                .and(QueryBuilder.lt("column1", System.currentTimeMillis()))
                .limit(limit);
        return statement;
    }

    // gets the metrics written by AstyanaxWriter
    protected static Select getAstyanaxMetrics(int limit) {
        Select statement = QueryBuilder
                .select()
                .all()
                .from("\"DATA\"", "metrics_full")
                .where(QueryBuilder.eq("key", "astyanax.int.abcdefg.hijklmnop.qrstuvw.xyz.ABCDEFG.HIJKLMNOP.QRSTUVW.XYZ.abcdefg.hijklmnop.qrstuvw.xyz.met." +
                        (new Random().nextInt(Constants.WRITE_BATCH_SIZE))))
                .and(QueryBuilder.gt("column1", System.currentTimeMillis() - ttl * 1000))
                .and(QueryBuilder.lt("column1", System.currentTimeMillis()))
                .limit(limit);
        return statement;
    }
}
