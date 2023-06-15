import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaPairRDD$;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataProcess {
    static String appName = "Assignment 3";
    static String master = "local[*]";
    static SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
    static JavaSparkContext sc = new JavaSparkContext(conf);
    static JavaRDD<String> distFile = sc.textFile("src/main/java/parking_data_sz.csv");

    static JavaRDD<String[]> lines;
    static JavaPairRDD<String, String> task1;
    static JavaPairRDD<String, String> task2;

    static JavaPairRDD<String, Long> task3;

    static JavaPairRDD<String, Long> task4;
    static JavaPairRDD<String, String> task5_berthage_section;
    static JavaRDD<String[]> task5_30min;

    static Map<String, Long> restask1;
    static List<Tuple2<String, String>> list = new ArrayList<>();

    public static void main(String[] args) {

        String header = distFile.first();
        distFile = distFile.filter(l -> !l.equals(header));

        lines = distFile.map(l -> l.split(","));
        lines = lines.filter(l -> {
            Timestamp outtime = Timestamp.valueOf(l[0].substring(1, l[0].length() - 1));
            Timestamp intime = Timestamp.valueOf(l[2].substring(1, l[0].length() - 1));
            return outtime.after(intime) && !l[3].equals("\"211271.0\"");
        });
        task1 = lines.mapToPair(l -> new Tuple2<>(l[4], l[3]));

        task2 = lines.mapToPair(l -> new Tuple2<>(l[3], l[4]));
        task3 = lines.mapToPair(l -> {
            Timestamp outtime = Timestamp.valueOf(l[0].substring(1, l[0].length() - 1));
            Timestamp intime = Timestamp.valueOf(l[2].substring(1, l[0].length() - 1));
            long out_timestamp = outtime.getTime();
            long in_timestamp = intime.getTime();
            long parking_length = (out_timestamp - in_timestamp) / 1000;
            return new Tuple2<>(l[4], parking_length);
        });
        task4 = lines.mapToPair(l -> {
            Timestamp outtime = Timestamp.valueOf(l[0].substring(1, l[0].length() - 1));
            Timestamp intime = Timestamp.valueOf(l[2].substring(1, l[0].length() - 1));
            long out_timestamp = outtime.getTime();
            long in_timestamp = intime.getTime();
            long parking_length = (out_timestamp - in_timestamp) / 1000;
            return new Tuple2<>(l[3], parking_length);
        });

        //JavaRDD<Integer> afterfilter = task5_30min.map(l -> 1);
        //System.out.println(afterfilter.reduce((a,b) -> a+b));


        task1();
        task2();
        task3();
        task4();
        task5_sql();
        
    }

    

    /**
     * Output the total number of parking lots in each section. The output file should have two columns,
     * with the headers being section and count.
     */
    public static void task1() {
        System.out.println("this is start of task1");
        task1 = task1.distinct();
        Map<String, Long> sectioncnt = task1.countByKey();
        restask1 = sectioncnt;

        File file = new File("src/main/java/r1.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/main/java/r1.csv"))) {
            bufferedWriter.write("section,count\n");
            for (Map.Entry<String, Long> m : sectioncnt.entrySet()) {
                bufferedWriter.write(m.getKey() + "," + m.getValue() + "\n");
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Output all unique ids (berthages), associated with their section. The output file
     * should have two columns, with the headers being berthage and section.
     */
    public static void task2() {
        task2 = task2.distinct().sortByKey(true);
        List<Tuple2<String, String>> res = task2.collect();
        File file = new File("src/main/java/r2.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/main/java/r2.csv"))) {
            bufferedWriter.write("berthage,section\n");
            for (Tuple2<String, String> m : res) {
                bufferedWriter.write(m._1 + "," + m._2 + "\n");
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Output for each section: the average parking time of a car in that section. The output
     * file should have two columns, with the headers being section and avg_parking_time.
     * The average parking time should be counted in seconds as an integer.
     */
    public static void task3() {
        JavaPairRDD<String, Tuple2<Long, Integer>> tmp = task3.mapValues(x -> new Tuple2<>(x, 1));
        JavaPairRDD<String, Double> tmp2 = tmp.reduceByKey((x, y) -> new Tuple2<>(x._1 + y._1, x._2 + y._2)).mapValues(t -> (double) t._1 / t._2);
        List<Tuple2<String, Double>> res = tmp2.collect();
        //res.forEach(l -> System.out.println(l._1+" "+l._2));

        File file = new File("src/main/java/r3.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/main/java/r3.csv"))) {
            bufferedWriter.write("section,avg_parking_time\n");
            for (Tuple2<String, Double> m : res) {
                bufferedWriter.write(m._1 + "," + Math.round(m._2) + "\n");
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Output the average parking time for each parking lot, sorted in descending order.
     * The output file should have two columns, with the headers being berthage and
     * avg_parking_time. The average parking time should be counted in seconds as an
     * integer.
     */
    public static void task4() {
        JavaPairRDD<String, Tuple2<Long, Integer>> tmp = task4.mapValues(x -> new Tuple2<>(x, 1));
        JavaPairRDD<String, Double> tmp2 = tmp.reduceByKey((x, y) -> new Tuple2<>(x._1 + y._1, x._2 + y._2)).mapValues(t -> (double) t._1 / t._2);
        JavaPairRDD<Double, String> tmp3 = tmp2.mapToPair(e -> new Tuple2<>(e._2, e._1)).sortByKey(false);
        List<Tuple2<Double, String>> res = tmp3.collect();
        //res.forEach(l -> System.out.println(l._1+" "+l._2));
        File file = new File("src/main/java/r4.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/main/java/r4.csv"))) {
            bufferedWriter.write("berthage,avg_parking_time\n");
            for (Tuple2<Double, String> m : res) {
                bufferedWriter.write(m._2 + "," + Math.round(m._1) + "\n");
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Output for each section: the total number of parking lots in use (“in use” means there
     * is at least one car in that parking lot) and the percentage out of the total number of
     * parking lots in that section, in a 30-minute interval (e.g. during 09:00:00-09:29:59).
     * The output file should have three columns, with the headers being section, count
     * and percentage. The percentage value should be rounded to two decimal places.
     */
    
    public static void task5_sql() {
        SparkSession spark = SparkSession.builder()
                .appName("Assignment 3")
                .master("local[*]")
                .config("spark.some.config.option", "some-value")
                .getOrCreate();
        JavaRDD<String> tmp = spark.read().textFile("src/main/java/parking_data_sz.csv").javaRDD();
        String head = tmp.first();
        tmp = tmp.filter(l -> {
            String[] s = l.split(",");
            return !s[3].equals("\"211271.0\"");
        });
        JavaRDD<Park> parkJavaRDD = tmp.filter(l -> (!l.equals(head))).map(line -> {
            String[] l = line.split(",");
            Park p = new Park();
            p.setOut_time(Timestamp.valueOf(l[0].substring(1, l[0].length() - 1)));
            p.setIn_time(Timestamp.valueOf(l[2].substring(1, l[2].length() - 1)));
            p.setBerthage(l[3]);
            p.setSection(l[4]);
            p.setAdmin_region(l[1]);
            return p;
        });

        Dataset<Row> task5df = spark.createDataFrame(parkJavaRDD, Park.class);
        task5df.createOrReplaceTempView("distribute");
        Dataset<Row> table1 = spark.sql("select distinct split_part(concat(out_time, ''), ' ', 1) out_date, split_part(concat(out_time, ''), ' ', 2) _out_time, split_part(concat(in_time, ''), ' ', 1) in_date, split_part(concat(in_time, ''), ' ', 2) _in_time, section, berthage from distribute where out_time > in_time");
        table1.createOrReplaceTempView("table1");
        //table1.show();

        Map<String, String> sectiontototal = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/java/r1.csv"))) {
            String line;
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] lin = line.split(",");
                sectiontototal.put(lin[0], lin[1]);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String [][] minute =new String[][] {{"00", "29", "30"},{"30", "59", "00"}};
        int [] hour = new int[]{0, 1};
        //System.out.println(sectiontototal.size());
        File file = new File("src/main/java/r5.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("src/main/java/r5.csv", true));
            writer.write("start_time,end_time,section,count,percentage\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 7; i < 24; i++) {
            for (int j = 0; j < 2; j++) {
                for (Map.Entry<String, String> entry: sectiontototal.entrySet()) {
                    Dataset<Row> res;
                    if (i<10) {

                        res = spark.sql("with q as (select section, count(distinct berthage) tot from distribute where out_time > distribute.in_time group by section) " +
                                "select concat(in_date, ' " + "0"+i + ":" + minute[j][0] + ":00') start_time,concat(in_date, ' " + "0"+i + ":" + minute[j][1] + ":59') end_time, section, count(case " +
                                "when  (split_part(concat(_in_time, ''), ':', 1) <= '" +"0"+i + "' and " +
                                "       split_part(concat(_in_time, ''), ':', 2) <= '" + minute[j][1] + "' and "+
                                "       split_part(concat(_out_time, ''), ':', 1) >= '" + "0"+i + "' and "+
                                "       split_part(concat(_out_time, ''), ':', 2) >= '" + minute[j][0] + "') then berthage " +
                                "end) as count , " +
                                "   concat(round(count(case " +
                                "when  (split_part(concat(_in_time, ''), ':', 1) <= '" +"0"+ i + "' and " +
                                "       split_part(concat(_in_time, ''), ':', 2) <= '" + minute[j][1] + "' and " +
                                "       split_part(concat(_out_time, ''), ':', 1) >= '" + "0"+i + "' and "+
                                "       split_part(concat(_out_time, ''), ':', 2) >= '" + minute[j][0] + "' ) then berthage " +
                                "end) * 1.0 " +
                                "                    / " + entry.getValue() + " * 100, 2), '%') " +
                                "    as percentage from table1 where section = '" + entry.getKey() + "' group by section, in_date order by start_time; ");
                        //res.show();
                    }
                    else {
                        res = spark.sql("with q as (select section, count(distinct berthage) tot from distribute where out_time > distribute.in_time group by section) " +
                                "select concat(in_date, ' " + i + ":" + minute[j][0] + ":00') start_time,concat(in_date, ' " + i + ":" + minute[j][1] + ":59') end_time, section, count(case " +
                                "when  (split_part(concat(_in_time, ''), ':', 1) <= '" + i + "' and " +
                                "       split_part(concat(_in_time, ''), ':', 2) <= '" + minute[j][1] + "' and "+
                                "       split_part(concat(_out_time, ''), ':', 1) >= '" + i + "' and "+
                                "       split_part(concat(_out_time, ''), ':', 2) >= '" + minute[j][0] + "') then berthage " +
                                "end) as count , " +
                                "   concat(round(count(case " +
                                "when  (split_part(concat(_in_time, ''), ':', 1) <= '" + i + "' and " +
                                "       split_part(concat(_in_time, ''), ':', 2) <= '" + minute[j][1] + "' and " +
                                "       split_part(concat(_out_time, ''), ':', 1) >= '" + i + "' and "+
                                "       split_part(concat(_out_time, ''), ':', 2) >= '" + minute[j][0] + "' ) then berthage " +
                                "end) * 1.0 " +
                                "                    / " + entry.getValue() + " * 100, 2), '%') " +
                                "    as percentage from table1 where section = '" + entry.getKey() + "' group by section, in_date order by start_time; ");
                        //res.show();
                    }
                    List<Row> reslist = res.collectAsList();
                    for (Row r: reslist){
                        String start_time = r.getString(0);
                        String end_time = r.getString(1);
                        String section = r.getString(2);
                        long count = r.getLong(3);
                        String percentage = r.getString(4);

                        try {
                            writer.write(start_time+","+end_time+","+section+","+count+","+percentage+"\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        writer.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }
}
