import sy.service.Invoice;
import sy.service.PdfInvoiceExtractor;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;


public class Test {

    public static void main(String[] args) {
//        try {
//            Invoice invoice = PdfInvoiceExtractor.extract(new File("E:/pycharm project/invoice_project/example/test-3.pdf"));
//            System.out.println("---------------------------");
//            System.out.println(invoice);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        BigDecimal bigDecimal = BigDecimal.valueOf(0.0);
        System.out.println(bigDecimal);
        bigDecimal = bigDecimal.add(BigDecimal.valueOf(10.0));
        System.out.println(bigDecimal);
        System.out.println(bigDecimal.equals(BigDecimal.valueOf(10.0)));
    }

}
