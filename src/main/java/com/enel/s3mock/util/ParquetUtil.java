package com.enel.s3mock.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.IntegerValue;
import org.apache.parquet.example.data.simple.NanoTime;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.format.converter.ParquetMetadataConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;
import org.joda.time.DateTimeConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ParquetUtil {

    public static void printParquestFile(Path path, int rowToRead, StringBuffer buffer, JTable tableData) throws IllegalArgumentException {

        Configuration conf = new Configuration();

        try {
            ParquetMetadata readFooter = ParquetFileReader.readFooter(conf, path, ParquetMetadataConverter.NO_FILTER);
            MessageType schema = readFooter.getFileMetaData().getSchema();
            ParquetFileReader r = new ParquetFileReader(conf, path, readFooter);
            var rowRed = new AtomicInteger();
            PageReadStore pages = null;
            try {
                while (null != (pages = r.readNextRowGroup())) {
                    final long rows = pages.getRowCount();
                   log.info("#################");
                   log.info("Number of rows: " + rows);
                   log.info("#################");
                   //log.info("Read N. " + rowToRead);
                   //log.info("_________________________");

                    buffer.append("#################").append(System.getProperty("line.separator"));
                    buffer.append(rowToRead).append(" first rows of ").append(rows).append(System.getProperty("line.separator"));
                    buffer.append("#################").append(System.getProperty("line.separator"));
                    buffer.append(System.getProperty("line.separator"));
                    final MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
                    final RecordReader recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));
                    DefaultTableModel model = new DefaultTableModel();
                    var addColumn = new AtomicBoolean(Boolean.TRUE);
                    for (int i = 0; i < rows; i++) {
                        final Group g = (Group) recordReader.read();
                        //buffer.append("----------------------------------------").append(System.getProperty("line.separator"));
                        printTable((Group) recordReader.read(),tableData,i,model);
                        //printGroup(g, buffer);

                        if (rowRed.incrementAndGet() == rowToRead)
                            break;
                    }
                    tableData.setModel(model);
                    tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    tableData.setVisible(true);
                }
            } finally {
                r.close();
            }        } catch (IOException e) {
           log.info("Error reading parquet file.");
            e.printStackTrace();
        }
    }

    public static final long JULIAN_DAY_NUMBER_FOR_UNIX_EPOCH = 2440588;
    public static final long NANOS_PER_MILLISECOND = 1000000;
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'mm:ss");

    private static void printTable(Group g, JTable tableData,Integer iValue , DefaultTableModel model){
        int fieldCount = g.getType().getFieldCount();

        var elementRow = new ArrayList<>();

        for (int field = 0; field < fieldCount; field++) {
            Type fieldType = g.getType().getType(field);
            String fieldName = fieldType.getName();
            var valueCount = g.getFieldRepetitionCount(field);

            if (iValue == 0 )
                model.addColumn(fieldName);

            for (int index = 0; index < valueCount; index++) {
                if (fieldName.equals("begindatetime") || fieldName.equals("enddatetime") ||  fieldName.equals("extractiondate")) {
                    if (fieldType.isPrimitive()) {
                        var strInt96Value = g.getValueToString(field, index);
                        var byteArray = to_byte(strInt96Value.substring(strInt96Value.indexOf("[") + 1, strInt96Value.indexOf("]")).replace(" ", "").split(","));
                        NanoTime nt = NanoTime.fromBinary(Binary.fromConstantByteArray(byteArray));
                        int julianDay = nt.getJulianDay();
                        long nanosOfDay = nt.getTimeOfDayNanos();
                        long dateTime = (julianDay - JULIAN_DAY_NUMBER_FOR_UNIX_EPOCH) * DateTimeConstants.MILLIS_PER_DAY
                                + nanosOfDay / NANOS_PER_MILLISECOND;
                        elementRow.add(formatter.format(new Date(dateTime)));
                    }
                } else if (fieldType.isPrimitive()) {
                    elementRow.add(g.getValueToString(field, index));
                }
            }

        }

        model.addRow(elementRow.toArray());


          /* DefaultTableModel model = new DefaultTableModel();

                model.addColumn("Col1");
                model.addColumn("Col2");
                model.addRow(new Object[]{"v1", "v2"});
                model.addRow(new Object[]{"r1", "r2"});

                arJTable.get().setModel(model);
                arJTable.get().setVisible(true);*/
    }

    public static void printGroup(Group g , StringBuffer buffer ) {
        int fieldCount = g.getType().getFieldCount();
        for (int field = 0; field < fieldCount; field++) {
            int valueCount = g.getFieldRepetitionCount(field);

            Type fieldType = g.getType().getType(field);
            String fieldName = fieldType.getName();

            for (int index = 0; index < valueCount; index++) {
                if (fieldName.equals("begindatetime") || fieldName.equals("enddatetime") ||  fieldName.equals("extractiondate")) {
                    if (fieldType.isPrimitive()) {
                        var strInt96Value = g.getValueToString(field, index);
                        var byteArray = to_byte(strInt96Value.substring(strInt96Value.indexOf("[") + 1, strInt96Value.indexOf("]")).replace(" ", "").split(","));
                        NanoTime nt = NanoTime.fromBinary(Binary.fromConstantByteArray(byteArray));
                        int julianDay = nt.getJulianDay();
                        long nanosOfDay = nt.getTimeOfDayNanos();
                        long dateTime = (julianDay - JULIAN_DAY_NUMBER_FOR_UNIX_EPOCH) * DateTimeConstants.MILLIS_PER_DAY
                                + nanosOfDay / NANOS_PER_MILLISECOND;
                       log.info(fieldName + " " + formatter.format(new Date(dateTime)));
                       buffer.append(fieldName).append(" : ").append(formatter.format(new Date(dateTime))).append(System.getProperty("line.separator"));
                    }
                } else if (fieldType.isPrimitive()) {
                   log.info(fieldName + " " + g.getValueToString(field, index));
                   buffer.append(fieldName).append(" : ").append(g.getValueToString(field, index)).append(System.getProperty("line.separator"));
                    if(fieldName.equals("id"))
                        buffer.append("----------------------------------------").append(System.getProperty("line.separator"));
                }
            }
        }
       log.info("");
    }


    public static byte[] to_byte(String[] strs) {
        byte[] bytes = new byte[strs.length];
        for (int i = 0; i < strs.length; i++) {
            bytes[i] = Byte.parseByte(strs[i]);
        }
        return bytes;
    }

}
