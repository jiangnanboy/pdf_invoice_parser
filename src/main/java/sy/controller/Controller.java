package sy.controller;

import org.springframework.web.bind.annotation.PostMapping;
import sy.utils.OFDUtils;
import sy.service.Invoice;
import sy.service.PdfInvoiceExtractor;
import org.apache.commons.io.FileUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sy.utils.CollectionUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("pdf_invoice")
public class Controller {

    private static ThreadLocal<Map<String, DateFormat>> threadLocal = new ThreadLocal<>();
    private static final String FILE_NAME_FORMAT_STRING = "yyyy/MM-dd/HH-mm-ssSSSS";

    /**
     * @param pattern
     * @return date format
     */
    public static DateFormat getDateFormat(String pattern) {
        Map<String, DateFormat> map = threadLocal.get();
        DateFormat format = null;
        if (null == map) {
            map = CollectionUtil.newHashMap();
            format = new SimpleDateFormat(pattern);
            map.put(pattern, format);
            threadLocal.set(map);
        } else {
            format = map.computeIfAbsent(pattern, k -> new SimpleDateFormat(k));
        }
        return format;
    }

    @PostMapping("extract")
    public Invoice extrat(@RequestParam(value = "file", required = true) MultipartFile file) {
        String fileName = getDateFormat(FILE_NAME_FORMAT_STRING).format(new Date());
        File dest = null;
        boolean ofd = false;
        if (null != file && !file.isEmpty()) {
            if (file.getOriginalFilename().toLowerCase().endsWith(".ofd")) {
                ofd = true;
                dest = new File( fileName + ".ofd");
            } else {
                dest = new File( fileName + ".pdf");
            }
            dest.getParentFile().mkdirs();
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
            } catch (IOException e) {
            }
        }
        Invoice result = null;
        try {
            if (null != dest) {
                if (ofd) {//这里将ofd文件直接转为pdf做抽取
                    System.out.println("ofd处理...");
                    Path ofdPath = Paths.get(fileName + ".ofd");
                    Path pdfPath = Paths.get(fileName + ".pdf");
                    String pdfFilePath = OFDUtils.ofdtoPdf(ofdPath, pdfPath);
                    result = PdfInvoiceExtractor.extract(new File(pdfFilePath));
                    result.setMsgCode(200);
                    result.setMsg("返回成功！");
                } else {
                    result = PdfInvoiceExtractor.extract(dest);
                    result.setMsgCode(200);
                    result.setMsg("返回成功！");
                }
                //不保存上传的文件，删除
//                if (null != result.getAmount()) {
//                    dest.delete();
//                }
            } else {
                result = new Invoice();
                result.setMsgCode(500);
                result.setMsg("检测输入参数是否正确！");
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = new Invoice();
            result.setMsgCode(500);
            result.setMsg("检测输入参数是否正确！");
        }
        return result;
    }

}
