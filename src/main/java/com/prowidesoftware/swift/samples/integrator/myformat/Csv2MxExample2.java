package com.prowidesoftware.swift.samples.integrator.myformat;

import com.prowidesoftware.swift.model.mx.MxPacs00800102;
import com.prowidesoftware.swift.model.mx.MxType;
import com.prowidesoftware.swift.myformat.FileFormat;
import com.prowidesoftware.swift.myformat.MappingTable;
import com.prowidesoftware.swift.myformat.MyFormatEngine;
import com.prowidesoftware.swift.myformat.csv.CsvFieldsDef;
import com.prowidesoftware.swift.myformat.csv.CsvReader;
import com.prowidesoftware.swift.myformat.mx.MxWriter;

import java.util.List;

/**
 * This example shows how to convert a CSV into an MX using API from Prowide Integrator MyFormat module.
 *
 * <p>Basic example with just a few mapping rules, translating a single CSV row into an incomplete MX output, with a
 * field definition configuration to use labels in the mapping rules selectors. This example also uses a custom
 * split separator for the columns in the CSV row
 */
public class Csv2MxExample2 {

    public static void main(String[] args) {
        // Create a mapping table instance with source and target formats
        // There is no need to indicate the MX version because we will provide a specific writer to the translation call
        MappingTable table = new MappingTable(FileFormat.CSV, FileFormat.MX);

        // Create the field definitions configuration
        // This maps CSV indexes with custom labels, then the mapping rules can use the labels instead of the indexes
        CsvFieldsDef defs = new CsvFieldsDef()
            .addField("BIC", "0")
            .addField("IBAN", "1")
            .addField("ADDR1", "2")
            .addField("ADDR2", "3")
            .addField("ADDR3", "4");

        // Load mapping rules from Excel
        MappingTable.loadFromSpreadsheet(Xml2MtExample1.class.getResourceAsStream("/myformat/csv2mx.xls"), "example2", table);

        // Validate mapping rules syntax
        List<String> problems = table.validate();
        for (String s : problems) {
            System.out.println(s);
        }

        // Source message sample
        String input = "ABCDUSXXXXX|FAB2019051402400300005|0037 0039 RUE BOISSIERE|75116|PARIS";

        // We create a reader for the input, providing the fields definition, and also customizing the separator
        CsvReader reader = CsvReader.builder(input).separator('|').fieldsDef(defs).build();

        // Create a specific writer and call translation providing the reader and writer instances
        MxWriter writer = new MxWriter(MxType.pacs_008_001_02);
        MyFormatEngine.translate(reader, writer, table.getRules());

        // Get the result directly from the writer
        MxPacs00800102 mx = (MxPacs00800102) writer.mx();
        System.out.println(mx.message("message"));
    }

}
