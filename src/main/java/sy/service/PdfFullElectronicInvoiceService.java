package sy.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import sy.utils.CollectionUtil;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * PdfFullElectronicInvoiceService
 * 全电发票处理
 */
public class PdfFullElectronicInvoiceService {

    /**
     * 普通发票
     * @return
     */
    public static Invoice getFullElectronicInvoice(String fullText, String allText, int pageWidth, PDDocument doc, PDPage firstPage ) throws IOException {
        Invoice invoice = new Invoice();
        {
            String reg = "发票号码:(?<number>\\d{20})|:(?<date>\\d{4}年\\d{2}月\\d{2}日)|购名称:(?<buyerName>[\\u4e00-\\u9fa5]+公司)|销名称:(?<sellerAccount>[\\u4e00-\\u9fa5]+公司)";

            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(allText);
            while (matcher.find()) {
                if (matcher.group("number") != null) {
                    invoice.setNumber(matcher.group("number"));
                } else if (matcher.group("date") != null) {
                    invoice.setDate(matcher.group("date"));
                } else if (matcher.group("buyerName") != null) {
                    invoice.setBuyerName(matcher.group("buyerName"));
                }else if(matcher.group("sellerAccount") != null){
                    invoice.setSellerName(matcher.group("sellerAccount"));
                }
            }
        }
        {
            String reg = "纳税人识别号:([\\dA-Z]{18})";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(allText);
            // 多个匹配 会匹配两次.第一次为购买方的税号，第二次为销售的税号
            int i = 0;
            while (matcher.find()) {
                if(i==0){
                    invoice.setBuyerCode(matcher.group(1));
                    i++;
                }else {
                    invoice.setSellerCode(matcher.group(1));
                }
            }
        }

        {
            String reg = "合计¥?(?<amount>[^ \\f\\n\\r\\t\\v\\*]*)(?:¥?(?<taxAmount>\\S*)|\\*+)\\s";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(allText);
            if (matcher.find()) {
                try {
                    invoice.setAmount(new BigDecimal(matcher.group("amount")));
                } catch (Exception e) {
                }
                try {
                    invoice.setTaxAmount(new BigDecimal(matcher.group("taxAmount")));
                } catch (Exception e) {
                    invoice.setTaxAmount(new BigDecimal(0));
                }
            }
        }
        if (null == invoice.getAmount()) {
            String reg = "合\\u0020*计\\u0020*¥?(?<amount>[^ ]*)\\u0020+¥?(?:(?<taxAmount>\\S*)|\\*+)\\s";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(fullText);
            if (matcher.find()) {
                try {
                    invoice.setAmount(new BigDecimal(matcher.group("amount")));
                } catch (Exception e) {
                    invoice.setAmount(new BigDecimal(0));
                }
                try {
                    invoice.setTaxAmount(new BigDecimal(matcher.group("taxAmount")));
                } catch (Exception e) {
                    invoice.setTaxAmount(new BigDecimal(0));
                }
            }
        }
        {
            String reg = "价税合计\\u0028大写\\u0029(?<amountString>\\S*)\\u0028小写\\u0029¥?(?<amount>\\S*)\\s";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(allText);
            if (matcher.find()) {
                invoice.setTotalAmountString(matcher.group("amountString"));
                try {
                    invoice.setTotalAmount(new BigDecimal(matcher.group("amount")));
                } catch (Exception e) {
                    invoice.setTotalAmount(new BigDecimal(0));
                }
            }
        }
        {
            Pattern type00Pattern = Pattern.compile("\\((.*发票?)\\)");
            Matcher m00 = type00Pattern.matcher(allText);
            if (m00.find()) {
                invoice.setType(m00.group(1));
            }
        }
        PDFKeyWordPosition kwp = new PDFKeyWordPosition();
        Map<String, List<Position>> positionListMap = kwp
                .getCoordinate(Arrays.asList("机器编号", "税率", "单价", "价税合计", "合计", "开票日期", "规格型号", "车牌号", "开户行及账号", "密", "码", "区"), doc);

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        PDFTextStripperByArea detailStripper = new PDFTextStripperByArea();
        detailStripper.setSortByPosition(true);
        {
            Position machineNumber;
            if (positionListMap.get("机器编号").size() > 0) {
                machineNumber = positionListMap.get("机器编号").get(0);
            } else {
                machineNumber = positionListMap.get("开票日期").get(0);
                machineNumber.setY(machineNumber.getY() + 30);
            }
            Position taxRate = positionListMap.get("税率").get(0);

            Position totalAmount = positionListMap.get("价税合计").get(0);
            Position amount = positionListMap.get("合计").get(0);
            Position model = null;
            if (!positionListMap.get("规格型号").isEmpty()) {
                model = positionListMap.get("规格型号").get(0);
            } else if(!positionListMap.get("车牌号").isEmpty()){
                model = positionListMap.get("车牌号").get(0);
                model.setX(model.getX() - 15);
            } else if(!positionListMap.get("单价").isEmpty()) {
                model = positionListMap.get("单价").get(0);
                model.setX(model.getX() - 50);
            }

            if(Optional.ofNullable(model).isPresent()) {
                int x = Math.round(model.getX()) - 13;
                int y = Math.round(taxRate.getY()) + 5; // 用税率的y坐标作参考
                int h = Math.round(amount.getY()) - Math.round(taxRate.getY()) - 25; // 价税合计的y坐标减去税率的y坐标
                detailStripper.addRegion("detail", new Rectangle(0, y, pageWidth, h));
                stripper.addRegion("detailName", new Rectangle(0, y, x, h));
                stripper.addRegion("detailPrice", new Rectangle(x, y, pageWidth, h));
            } else {
                int x = 0;
                int y = Math.round(taxRate.getY()) + 5; // 用税率的y坐标作参考
                int h = Math.round(amount.getY()) - Math.round(taxRate.getY()) - 25; // 价税合计的y坐标减去税率的y坐标
                detailStripper.addRegion("detail", new Rectangle(0, y, pageWidth, h));
                stripper.addRegion("detailName", new Rectangle(0, y, x, h));
                stripper.addRegion("detailPrice", new Rectangle(x, y, pageWidth, h));

            }

        }
        stripper.extractRegions(firstPage);
        detailStripper.extractRegions(firstPage);
        doc.close();

        {
            List<String> skipList = CollectionUtil.newArrayList();
            List<Detail> detailList = CollectionUtil.newArrayList();
            String[] detailPriceStringArray = stripper.getTextForRegion("detailPrice").replaceAll("　", " ").replaceAll(" ", " ")
                    .replaceAll("\r", "").split("\\n");

            for (String detailString : detailPriceStringArray) {
                if(StringUtils.containsAny(detailString, "数 ：", "数：", "数:", "数: ", "数 :")) { //过滤发票旁边“下载次数：”信息
                    continue;
                }
                else if(StringUtils.contains(detailString, "等")) {
                    continue;
                }
                Detail detail = new Detail();
                detail.setName("");
                String[] itemArray = StringUtils.split(detailString, " ");
                if (2 == itemArray.length) {
                    if(detailString.contains("¥")) {
                        continue;
                    }
                    detail.setAmount(new BigDecimal(itemArray[0]));
                    detail.setTaxAmount(new BigDecimal(itemArray[1]));
                    detailList.add(detail);
                } else if (2 < itemArray.length) {
                    detail.setAmount(new BigDecimal(itemArray[itemArray.length - 3]));
                    String taxRate = itemArray[itemArray.length - 2];
                    if (taxRate.indexOf("免税") > 0 || taxRate.indexOf("不征税") > 0 || taxRate.indexOf("出口零税率") > 0
                            || taxRate.indexOf("普通零税率") > 0 || taxRate.indexOf("%") < 0) {
                        detail.setTaxRate(new BigDecimal(0));
                        detail.setTaxAmount(new BigDecimal(0));
                    } else {
                        BigDecimal rate = new BigDecimal(Integer.parseInt(taxRate.replaceAll("%", "")));
                        detail.setTaxRate(rate.divide(new BigDecimal(100)));
                        detail.setTaxAmount(new BigDecimal(itemArray[itemArray.length - 1]));
                    }
                    for (int j = 0; j < itemArray.length - 3; j++) {
                        if (itemArray[j].matches("^(-?\\d+)(\\.\\d+)?$")) {
                            if (null == detail.getCount()) {
                                detail.setCount(new BigDecimal(itemArray[j]));
                            } else {
                                detail.setPrice(new BigDecimal(itemArray[j]));
                            }
                        } else {
                            if (itemArray.length >= j + 1 && !itemArray[j + 1].matches("^(-?\\d+)(\\.\\d+)?$")) {
                                detail.setUnit(itemArray[j + 1]);
                                detail.setModel(itemArray[j]);
                                j++;
                            } else if (itemArray[j].length() > 2) {
                                detail.setModel(itemArray[j]);
                            } else {
                                detail.setUnit(itemArray[j]);
                            }
                        }
                    }
                    detailList.add(detail);
                } else {
                    skipList.add(detailString);
                }
            }

            String[] detailNameStringArray = stripper.getTextForRegion("detailName").replaceAll("　", " ").replaceAll(" ", " ")
                    .replaceAll("\r", "").split("\\n");
            String[] detailStringArray = sy.utils.StringUtils.replace(detailStripper.getTextForRegion("detail")).replaceAll("\r", "").split("\\n");
            int i = 0, j = 0, h = 0, m = 0;
            Detail lastDetail = null;
            for (String detailString : detailStringArray) {
                if (m < detailNameStringArray.length) {
                    if (detailString.matches("\\S+\\d*(%|免税|不征税|出口零税率|普通零税率)\\S*")
                            && !detailString.matches("^ *\\d*(%|免税|不征税|出口零税率|普通零税率)\\S*")
                            && detailString.matches("\\S+\\d+%[\\-\\d]+\\S*")
                            || detailStringArray.length > i + 1
                            && detailStringArray[i + 1].matches("^ *\\d*(%|免税|不征税|出口零税率|普通零税率)\\S*")) {
                        if (j < detailList.size()) {
                            lastDetail = detailList.get(j);
                            lastDetail.setName(detailNameStringArray[m]);
                        }
                        j++;
                    } else if (null != lastDetail && StringUtils.isNotBlank(detailNameStringArray[m])) {
                        if (skipList.size() > h) {
                            String skip = skipList.get(h);
                            if (detailString.endsWith(skip)) {
                                if (detailString.equals(skip)) {
                                    m--;
                                } else {
                                    lastDetail.setName(lastDetail.getName() + detailNameStringArray[m]);
                                }
                                lastDetail.setModel(lastDetail.getModel() + skip);
                                h++;
                            } else {
                                lastDetail.setName(lastDetail.getName() + detailNameStringArray[m]);
                            }
                        } else {
                            lastDetail.setName(lastDetail.getName() + detailNameStringArray[m]);
                        }
                    }
                }
                i++;
                m++;
            }
            invoice.setDetailList(detailList);
        }

        {
            if((Optional.ofNullable(invoice.getDetailList()).isPresent()) && (invoice.getDetailList().size() > 0)) {
                List<Detail> details = invoice.getDetailList();
                if(invoice.getAmount().equals(BigDecimal.valueOf(0))) {
                    BigDecimal amout = BigDecimal.valueOf(0);
                    for(Detail detail : details) {
                        if(!detail.getAmount().equals(BigDecimal.valueOf(0))) {
                            amout = amout.add(detail.getAmount());
                        }
                    }
                    if(!amout.equals(BigDecimal.valueOf(0))) {
                        invoice.setAmount(amout);
                    }

                }
                if(invoice.getTaxAmount().equals(BigDecimal.valueOf(0))) {
                    BigDecimal taxAmout = BigDecimal.valueOf(0);
                    for(Detail detail : details) {
                        if(!detail.getTaxAmount().equals(BigDecimal.valueOf(0))) {
                            taxAmout = taxAmout.add(detail.getTaxAmount());
                        }
                    }
                    if(!taxAmout.equals(BigDecimal.valueOf(0))) {
                        invoice.setTaxAmount(taxAmout);
                    }
                }
            }
        }

        return invoice;
    }

    public static boolean containChineseCharacter(String text) {
        String patter = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(patter);
        Matcher m = p.matcher(text);
        return m.find();
    }

    public static void main(String...args) {
        String text = "出⾏日期 出发地 到达地 等   级 交通⼯具类型";
        byte[] utf8Bytes = text.getBytes(StandardCharsets.UTF_8);
        String str = new String(utf8Bytes);
        System.out.println(str);
        System.out.println(isChinese("行"));
    }

    public static boolean isChinese(String text) {
        for(char c : text.toCharArray()) {
            if(c < 0x4e00 || c > 0x9fa5) {
                return false;
            }
        }
        return true;
    }
}

