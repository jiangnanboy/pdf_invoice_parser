package sy.utils;


import org.ofdrw.converter.ConvertHelper;
import org.ofdrw.converter.GeneralConvertException;
import org.ofdrw.converter.ImageMaker;
import org.ofdrw.converter.SVGMaker;
import org.ofdrw.reader.OFDReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * PDF转换概述： 通过对OFD的文档进行解析，使用 Apache Pdfbox生成并转换OFD中的元素为PDF内的元素实现PDF的转换。
 * 图片转换概述： 通过对OFD的文档进行解析，采用java.awt绘制图片，支持转换为PNG、JPEG图片格式。
 * SVG矢量图形转换概述： 使用Apachebatik-transcoder提供的图形绘制实现java.awtAPI绘制，最终生成SVG矢量图形。
 * HTML转换概述： 使用上述SVG矢量图形转换作为显示效果层A，再将OFD文档中的文字（仅）解析为SVG作为文字复制层B，B置于A层之上，
 * 文字颜色transparent，无需关心字体，在移动端同样正常显示。
 *
 *
 */

public class OFDUtils {


    /**
     * 1.正常的ofd文件
     * 2.图片的ofd
     * 3.进过其他转换器转换而来的ofd
     */
    public static String ofdtoPdf(Path input, Path output) {
        /****************************************************
         * 转换成PDF    如图不是全屏图转成pdf后显示会乱
         */
        try {
            // 3. OFD转换PDF
            ConvertHelper.toPdf(input, output);
        } catch (GeneralConvertException e) {
            // GeneralConvertException 类型错误表明转换过程中发生异常
            e.printStackTrace();
        }
        return output.toAbsolutePath().toString();
    }


    /**
     * 按页数转
     * @param input
     * @param output
     */
    public static void ofdtoPic(Path input, Path output) {
        /***********************************************************
         * 转换成图片
         */
//        // 1. 文件输入路径
        Path src =input; // Paths.get("C:/Users/kong/Desktop/ofd/2.ofd");

//        output = Paths.get(src.toAbsolutePath()+"_1.jpeg");
        // 2. 加载指定目录字体(非必须)
        // FontLoader.getInstance().scanFontDir(new File("src/test/resources/fonts"));
        // 3. 创建转换转换对象，设置 每毫米像素数量(Pixels per millimeter)
        try{

            OFDReader reader = new OFDReader(src);
            ImageMaker imageMaker = new ImageMaker(reader, 15);
            for (int i = 0; i < imageMaker.pageSize(); i++) {
                // 4. 指定页码转换图片
                BufferedImage image = imageMaker.makePage(i);
                Path dist = Paths.get(src.getParent().toString(), src.getFileName()+""+i+"-1.jpeg");
                // 5. 存储为指定格式图片
                ImageIO.write(image, "JPEG", dist.toFile());
            }
            System.out.println("ofdtoPic-ok");
        }catch(Exception e){
            e.printStackTrace();
        }finally {

        }

    }

    public static void ofdtoSvg(Path input, Path output) {

        /********************************************************
         * 转换成svg
         */
        // 1. 文件输入路径
        Path src =input; // Paths.get("C:/Users/kong/Desktop/ofd/2.ofd");
//         output = Paths.get(src.toAbsolutePath()+"_1.svg");
        // 2. 加载指定目录字体(非必须)
        // FontLoader.getInstance().scanFontDir(new File("src/test/resources/fonts"));
        // 3. 创建转换转换对象，设置 每毫米像素数量(Pixels per millimeter)
        try{
            OFDReader reader = new OFDReader(src);
            SVGMaker svgMaker = new SVGMaker(reader, 20d);
            for (int i = 0; i < svgMaker.pageSize(); i++) {
                // 4. 指定页码转换SVG，得到SVG(XML)
                String svg = svgMaker.makePage(i);
                Path dist = Paths.get(src.getParent().toString(), src.getFileName()+""+i+"-1.svg");
                // 5. 存储到文件。
                Files.write(dist, svg.getBytes());
            }

            System.out.println("ofdtoSvg-ok");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void ofdtoHtml(Path input, Path output) {

        /***************************************************************
         * 转换成html
         */
        try {
            // 1. 提供文档
            Path ofdIn =input; // Paths.get("C:/Users/kong/Desktop/ofd/1.ofd");
            Path htmlOut = Paths.get(ofdIn.toAbsolutePath()+"_1.html");
            // 2. [可选]配置字体，别名，扫描目录等
            // FontLoader.getInstance().addAliasMapping(null, "小标宋体", "方正小标宋简体", "方正小标宋简体")
            // FontLoader.getInstance().scanFontDir(new File("src/test/resources/fonts"));
            // 3. 配置参数（HTML页面宽度(px)），转换并存储HTML到文件。
            ConvertHelper.toHtml(ofdIn, htmlOut, 1000);

            System.out.println("ofdtoHtml-ok");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
       /* Path ofdIn= Paths.get("C:/Users/kong/Desktop/ofd/OFD/1.ofd");

        OFDUtils.ofdtoPdf(ofdIn,Paths.get(""));
        OFDUtils.ofdtoHtml(ofdIn,Paths.get(""));
        OFDUtils.ofdtoPic(ofdIn,Paths.get(""));
        OFDUtils.ofdtoSvg(ofdIn,Paths.get(""));
*/
//        ofdU.vPageLayerTest();
//        ofdU.streamTestParagraphPageSplit();
//        ofdU.OFDRWCanvas();
//        ContentExtractorTest.getPageContent();
//        Path ofdIn= Paths.get("C:/Users/kong/Desktop/ofd/OFD/第三方ofd/60830858_578.ofd");

//        OFDUtils.ofdtoPic(ofdIn,Paths.get(""));
//        new WatermarkTest().addWatermark();//加水印

//        ContentExtractorTest.traverse();

        Path ofdIn= Paths.get("E:\\git_project\\invoiceocr-master\\ofd_example\\test1.ofd");

        OFDUtils.ofdtoPdf(ofdIn,Paths.get(""));
//        Path ofdIn= Paths.get("C:\\Users\\kong\\Desktop\\ofd\\2.ofd");
//        OFDUtils.ofdtoPic(ofdIn,Paths.get(""));

    }


}