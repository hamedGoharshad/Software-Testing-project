import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Software Testing Final Project
 * by Dr. samad paydar
 *
 * @author hamed goharshad
 */
public class Main {

    static WebDriver driver;
    static List<WebElement> allElements;
    static String url = "";

    public static void main(String[] args) {
        int repeatCount = 0;

        try {
            getUrl();
            getAllElements();
            hasHrefImg();
            hasDeprecatedAttr();
            hasMetaRefresh();
            hasStyleAttr();
            hasSameTextLink();
            while (repeatCount < 2) {
                hasUiConflict(repeatCount);
                repeatCount++;
            }
        } finally {
            if (driver != null) driver.quit();
        }
    }

    static void getAllElements() {
        allElements = driver.findElements(By.xpath("//*"));
    }

    static void getUrl() {
        // get and validate URL
        while (true) {
            System.out.println("Please Enter a valid URL : ");
            url = new Scanner(System.in).nextLine();
            if (isValidURL(url)) break;
            else {
                System.out.println("invalid url , try again . . .");
            }
        }
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get(url);
        driver.manage().window().maximize();
    }

    static boolean hasHrefImg() {
        boolean result = false;
        List<WebElement> imageList = driver.findElements(By.tagName("img"));
        for (WebElement img :
                imageList) {
            String href = img.getAttribute("href");
            if (href != null) {
                boolean containsImg = href.contains("jpg") || href.contains("png") || href.contains("svg");
                if (containsImg) {
                    System.out.println("Href is a image at : " + img.toString());
                    result = true;
                }
            }
        }
        return result;
    }

    static boolean hasDeprecatedAttr() {
        boolean hasDeprecatedAttr = false;
        for (WebElement element :
                allElements) {
            for (Map.Entry<String, List<String>> entry : deprecatedList.entrySet()) {
                if (element.getTagName().equals(entry.getKey())) {
                    for (String attr :
                            entry.getValue()) {
                        if (element.getAttribute(attr) != null) {
                            hasDeprecatedAttr = true;
                            System.out.println("deprecated attribute in " + element.toString());
                        }
                    }
                }
            }
        }
        if (!hasDeprecatedAttr) System.out.println("There is no deprecated attribute");
        return hasDeprecatedAttr;
    }

    static boolean hasMetaRefresh() {
        boolean result = false;
        List<WebElement> metaTagWithRefresh = driver.findElements(By.xpath("//meta[@property='og:http-equiv' and @content='refresh']"));
        if (!metaTagWithRefresh.isEmpty()) {
            System.out.println("There is a meta tag with refresh usage at " + metaTagWithRefresh);
            result = true;
        }
        return result;
    }

    static boolean hasSameTextLink() {
        // same title same link
        boolean result = false;
        List<WebElement> allLinks = driver.findElements(By.tagName("a"));
        for (WebElement link1 : allLinks) {
            for (WebElement link2 :
                    allLinks) {
                String href1 = link1.getAttribute("href");
                String href2 = link2.getAttribute("href");

                if (link1.getText().equals(link2.getText()) && href1 != null && href2 != null && !href1.equals(href2)) {
                    System.out.println("There is two link with same text and defferent href at :" + link1 + link2);
                    result = true;
                }
            }
        }
        return result;
    }

    static boolean hasStyleAttr() {
        boolean result = false;
        List<WebElement> allElements = driver.findElements(By.xpath("//*"));
        for (WebElement element :
                allElements) {
            if (element.getAttribute("style") != null) {
                System.out.println("There is an element with style attribute at : " + element);
                result = true;
            }
        }
        return result;
    }

    static boolean hasUiConflict(int repeatCount) {
        boolean result = false;
        if (repeatCount == 0) {
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
            driver = new ChromeDriver();
        } else if (repeatCount == 1) {
            System.setProperty("webdriver.gecko.driver", "C:\\geckodriver.exe");
            driver = new FirefoxDriver();
        }
        driver.get(url);
        for (Map.Entry<Integer, Integer> entry : resolutions.entrySet()) {
            driver.manage().window().setSize(new Dimension(entry.getKey(), entry.getValue()));
            List<WebElement> allElements = driver.findElements(By.xpath("//input"));
            for (WebElement element1 :
                    allElements) {
                for (WebElement element2 :
                        allElements) {
                    if (areElementsOverlapping(element1, element2)) {
                        System.out.println("There is view overlapping at : " + element1 + "and" + element2);
                        result = true;
                    }
                }
            }
        }
        return result;
    }


    static public boolean areElementsOverlapping(WebElement element1, WebElement element2) {
        Rectangle r1 = element1.getRect();
        Point topRight1 = r1.getPoint().moveBy(r1.getWidth(), 0);
        Point bottomLeft1 = r1.getPoint().moveBy(0, r1.getHeight());

        Rectangle r2 = element2.getRect();
        Point topRight2 = r2.getPoint().moveBy(r2.getWidth(), 0);
        Point bottomLeft2 = r2.getPoint().moveBy(0, r2.getHeight());

        if (topRight1.getY() > bottomLeft2.getY()
                || bottomLeft1.getY() < topRight2.getY()) {
            return false;
        }
        if (topRight1.getX() < bottomLeft2.getX()
                || bottomLeft1.getX() > topRight2.getX()) {
            return false;
        }
        return true;
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
        return true;
    }

    static Map<Integer, Integer> resolutions = Map.ofEntries(
            Map.entry(800, 600),
            Map.entry(1024, 768),
            Map.entry(1448, 1072),
            Map.entry(1600, 1200),
            Map.entry(2048, 1536)
    );

    static Map<String, List<String>> deprecatedList = Map.ofEntries(
            Map.entry("accept", List.of("form")),
            Map.entry("align", List.of("caption", "col", "div", "embed", "h1", "hr", "iframe", "img", "input", "legend", "object", "p", "table", "tbody", "thead", "tfoot", "td", "th", "tr")),
            Map.entry("alink", List.of("body")),
            Map.entry("allowtransparency", List.of("iframe")),
            Map.entry("archive", List.of("object")),
            Map.entry("axis", List.of("td", "th")),
            Map.entry("background", List.of("body", "table", "thead", "tbody", "tfoot", "tr", "td", "th")),
            Map.entry("bgcolor", List.of("body", "table", "td", "th", "tr")),
            Map.entry("bordercolo", List.of("table")),
            Map.entry("cellpadding", List.of("table")),
            Map.entry("cellspacing", List.of("table")),
            Map.entry("char", List.of("col", "tbody", "thead", "tfoot", "td", "th", "tr")),
            Map.entry("charoff", List.of("col", "tbody", "thead", "tfoot", "td)", "th", "tr")),
            Map.entry("charset", List.of("a", "link")),
            Map.entry("classid", List.of("object")),
            Map.entry("clear", List.of("br")),
            Map.entry("code", List.of("object")),
            Map.entry("codebase", List.of("object")),
            Map.entry("codetype", List.of("object")),
            Map.entry("color", List.of("hr")),
            Map.entry("compact", List.of("dl", "ol", "ul")),
            Map.entry("coords", List.of("a")),
            Map.entry("datafld", List.of("a", "applet", "button", "div", "fiel)dset", "frame", "iframe", "img", "input", "label", "legend", "marquee", "object", "param", "select", "span", "textarea")),
            Map.entry("dataformatas", List.of("button", "div", "input", "label)", "legend", "marquee", "object", "option", "select", "span", "table")),
            Map.entry("datapagesize", List.of("table")),
            Map.entry("datasrc", List.of("a", "applet", "button", "div", "fram)e", "iframe", "img", "input", "label", "legend", "marquee", "object", "option", "select", "span", "table", "textarea")),
            Map.entry("declare", List.of("object")),
            Map.entry("event", List.of("script")),
            Map.entry("for", List.of("script")),
            Map.entry("frame", List.of("table")),
            Map.entry("frameborder", List.of("iframe")),
            Map.entry("height", List.of("td", "th")),
            Map.entry("hspace", List.of("embed", "iframe", "img", "input", "object")),
            Map.entry("ismap", List.of("input")),
            Map.entry("langauge", List.of("script")),
            Map.entry("link", List.of("body")),
            Map.entry("lowsrc", List.of("img")),
            Map.entry("marginbottom", List.of("body")),
            Map.entry("marginheight", List.of("body", "iframe")),
            Map.entry("marginleft", List.of("body")),
            Map.entry("marginright", List.of("body")),
            Map.entry("margintop", List.of("body")),
            Map.entry("marginwidth", List.of("body", "iframe")),
            Map.entry("methods", List.of("a", "link")),
            Map.entry("name", List.of("a", "embed", "img", "option")),
            Map.entry("nohref", List.of("area")),
            Map.entry("noshade", List.of("hr")),
            Map.entry("nowrap", List.of("td", "th")),
            Map.entry("profile", List.of("head")),
            Map.entry("rules", List.of("table")),
            Map.entry("scheme", List.of("meta")),
            Map.entry("scope", List.of("td")),
            Map.entry("scrolling", List.of("iframe")),
            Map.entry("shape", List.of("a")),
            Map.entry("size", List.of("hr")),
            Map.entry("standby", List.of("object")),
            Map.entry("summary", List.of("table")),
            Map.entry("target", List.of("link")),
            Map.entry("text", List.of("body")),
            Map.entry("type", List.of("li", "param", "ul")),
            Map.entry("urn", List.of("a", "link")),
            Map.entry("usemap", List.of("input")),
            Map.entry("valign", List.of("col", "tbody", "thead", "tfoot", "td", "th", "tr")),
            Map.entry("valuetype", List.of("param")),
            Map.entry("version", List.of("html")),
            Map.entry("vlink", List.of("body")),
            Map.entry("vspace", List.of("embed", "iframe", "img", "input", "object")),
            Map.entry("width", List.of("col", "hr", "pre", "table", "td", "th"))
    );
}
