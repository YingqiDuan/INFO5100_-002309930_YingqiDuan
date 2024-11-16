import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class BookShelfParser {

    public static void main(String[] args) {
        String xmlPath = "bookshelf.xml";
        String jsonPath = "bookshelf.json";

        try {
            // Parse and print XML
            System.out.println("Parsing XML:");
            parseAndPrintXML(xmlPath);

            // Add a new book to XML and reprint
            addBookToXML(xmlPath, createNewXMLBook());
            System.out.println("\nAfter adding a new book to XML:");
            parseAndPrintXML(xmlPath);

            // Parse and print JSON
            System.out.println("\nParsing JSON:");
            parseAndPrintJSON(jsonPath);

            // Add a new book to JSON and reprint
            addBookToJSON(jsonPath, createNewJSONBook());
            System.out.println("\nAfter adding a new book to JSON:");
            parseAndPrintJSON(jsonPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to create a new XML Book Element
    private static Element createNewXMLBook() throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element newBook = doc.createElement("Book");

        appendChildWithText(doc, newBook, "title", "Pride and Prejudice");
        appendChildWithText(doc, newBook, "publishedYear", "1813");
        appendChildWithText(doc, newBook, "numberOfPages", "279");

        Element authors = doc.createElement("authors");
        appendChildWithText(doc, authors, "author", "Jane Austen");
        newBook.appendChild(authors);

        return newBook;
    }

    // Helper method to append child with text content
    private static void appendChildWithText(Document doc, Element parent, String tag, String text) {
        Element elem = doc.createElement(tag);
        elem.appendChild(doc.createTextNode(text));
        parent.appendChild(elem);
    }

    // Helper method to create a new JSON Book Object
    private static JSONObject createNewJSONBook() {
        JSONObject newBook = new JSONObject();
        newBook.put("title", "Pride and Prejudice");
        newBook.put("publishedYear", 1813);
        newBook.put("numberOfPages", 279);
        newBook.put("authors", new JSONArray(List.of("Jane Austen")));
        return newBook;
    }

    // Method to parse and print XML
    private static void parseAndPrintXML(String filePath) throws ParserConfigurationException, SAXException, IOException {
        File xmlFile = new File(filePath);
        if (!xmlFile.exists()) {
            System.out.println("XML file not found: " + filePath);
            return;
        }

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList books = doc.getElementsByTagName("Book");
        for (int i = 0; i < books.getLength(); i++) {
            Element book = (Element) books.item(i);
            printBookDetails(book);
        }
    }

    // Method to add a new book to XML
    private static void addBookToXML(String filePath, Element newBook) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(filePath));
        doc.getDocumentElement().normalize();

        Node bookShelf = doc.getElementsByTagName("BookShelf").item(0);
        Node importedBook = doc.importNode(newBook, true);
        bookShelf.appendChild(importedBook);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
    }

    // Method to parse and print JSON
    private static void parseAndPrintJSON(String filePath) throws IOException {
        File jsonFile = new File(filePath);
        if (!jsonFile.exists()) {
            System.out.println("JSON file not found: " + filePath);
            return;
        }

        try (FileReader reader = new FileReader(jsonFile)) {
            JSONObject root = new JSONObject(new JSONTokener(reader));
            JSONArray books = root.getJSONObject("BookShelf").getJSONArray("Book");

            for (int i = 0; i < books.length(); i++) {
                JSONObject book = books.getJSONObject(i);
                printJSONBookDetails(book);
            }
        }
    }

    // Method to add a new book to JSON
    private static void addBookToJSON(String filePath, JSONObject newBook) throws IOException {
        File jsonFile = new File(filePath);
        if (!jsonFile.exists()) {
            System.out.println("JSON file not found: " + filePath);
            return;
        }

        JSONObject root;
        try (FileReader reader = new FileReader(jsonFile)) {
            root = new JSONObject(new JSONTokener(reader));
        }

        root.getJSONObject("BookShelf").getJSONArray("Book").put(newBook);

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(root.toString(4)); // Indent with 4 spaces
        }
    }

    // Helper method to print XML Book details
    private static void printBookDetails(Element book) {
        String title = getTagValue("title", book);
        String publishedYear = getTagValue("publishedYear", book);
        String numberOfPages = getTagValue("numberOfPages", book);

        NodeList authorsList = book.getElementsByTagName("author");
        StringBuilder authors = new StringBuilder();
        for (int j = 0; j < authorsList.getLength(); j++) {
            authors.append(authorsList.item(j).getTextContent());
            if (j < authorsList.getLength() - 1) authors.append(", ");
        }

        System.out.println("Title: " + title);
        System.out.println("Published Year: " + publishedYear);
        System.out.println("Number of Pages: " + numberOfPages);
        System.out.println("Authors: " + authors);
        System.out.println("---------------------------");
    }

    // Helper method to print JSON Book details
    private static void printJSONBookDetails(JSONObject book) {
        String title = book.getString("title");
        int publishedYear = book.getInt("publishedYear");
        int numberOfPages = book.getInt("numberOfPages");
        JSONArray authorsArray = book.getJSONArray("authors");

        String authors = String.join(", ", authorsArray.toList().stream().map(Object::toString).toArray(String[]::new));

        System.out.println("Title: " + title);
        System.out.println("Published Year: " + publishedYear);
        System.out.println("Number of Pages: " + numberOfPages);
        System.out.println("Authors: " + authors);
        System.out.println("---------------------------");
    }

    // Helper method to get text content of a tag
    private static String getTagValue(String tag, Element element) {
        Node node = element.getElementsByTagName(tag).item(0);
        return node != null ? node.getTextContent() : "";
    }
}
