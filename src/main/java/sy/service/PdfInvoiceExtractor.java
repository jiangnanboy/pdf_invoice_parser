package sy.service;

import sy.utils.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/**
 * 专用于处理电子发票识别的类
 */

public class PdfInvoiceExtractor {

    public static Invoice extract(File file) throws IOException {

//        PDDocument doc = PDDocument.load(file);
        PDDocument doc = Loader.loadPDF(file);
        PDPage firstPage = doc.getPage(0);
        int pageWidth = Math.round(firstPage.getCropBox().getWidth());
        PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setSortByPosition(true);
        String fullText = textStripper.getText(doc);
        if (firstPage.getRotation() != 0) {
            pageWidth = Math.round(firstPage.getCropBox().getHeight());
        }
        String allText = StringUtils.replace(fullText).replaceAll("（", "(").replaceAll("）", ")").replaceAll("￥", "¥");
        allText = allText.trim();
        System.out.println("allText --> " + allText);
        if(allText.contains("电子发票") || allText.contains("电⼦发票")){
            System.out.println("全电票处理...");
            // 全票
          return PdfFullElectronicInvoiceService.getFullElectronicInvoice(fullText, allText, pageWidth, doc, firstPage);
        }else {
            System.out.println("常规发票处理...");
           return PdfRegularInvoiceService.getRegularInvoice(fullText, allText, pageWidth, doc, firstPage);
        }
    }


}