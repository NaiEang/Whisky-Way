package src.tile;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;

import src.tile.FileUtils; // adjust the package name if needed

public class TMXParser {

    public static TMXMap parseTMX(String filePath) {
        TMXMap tmxMap = new TMXMap();
        try {
            File file = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            Element mapElement = doc.getDocumentElement();
            tmxMap.width = Integer.parseInt(mapElement.getAttribute("width"));
            tmxMap.height = Integer.parseInt(mapElement.getAttribute("height"));
            tmxMap.tileWidth = Integer.parseInt(mapElement.getAttribute("tilewidth"));
            tmxMap.tileHeight = Integer.parseInt(mapElement.getAttribute("tileheight"));

            // ✅ Parse Tileset Images Here
            NodeList tilesetList = doc.getElementsByTagName("tileset");
            for (int i = 0; i < tilesetList.getLength(); i++) {
                Element tilesetElement = (Element) tilesetList.item(i);
                NodeList imageList = tilesetElement.getElementsByTagName("image");

                if (imageList.getLength() > 0) {
                    Element imageElement = (Element) imageList.item(0);
                    String source = imageElement.getAttribute("source");

                    File imgFile = FileUtils.findFileRecursive(new File("res/"), new File(source).getName());
                    if (imgFile != null) {
                        System.out.println("Resolved image path: " + imgFile.getPath());
                        tmxMap.tilesetImage = ImageIO.read(imgFile);
                        // You can store this tilesetImage into TMXMap if needed
                    } else {
                        System.out.println("Image not found for source: " + source);
                    }
                }
            }

            // ✅ Parse Layer Data
            NodeList layerList = doc.getElementsByTagName("layer");
            if (layerList.getLength() > 0) {
                Element layer = (Element) layerList.item(0);
                Element data = (Element) layer.getElementsByTagName("data").item(0);
                String csvData = data.getTextContent().trim();
                String[] tileValues = csvData.split(",");

                tmxMap.tileData = new int[tmxMap.height][tmxMap.width];

                for (int row = 0; row < tmxMap.height; row++) {
                    for (int col = 0; col < tmxMap.width; col++) {
                        int index = row * tmxMap.width + col;
                        long gid = Long.parseLong(tileValues[index].trim());
                        gid = gid & 0x1FFFFFFF; // Mask out flipping flags
                        tmxMap.tileData[row][col] = (int) gid;
                    }
                }
            }

            System.out.println("TMX Map Loaded: " + filePath);
            return tmxMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
