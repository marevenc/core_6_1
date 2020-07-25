import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.*;
import com.opencsv.bean.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        String fileNameXML = "data.xml";

        List<Employee> listCSV = parseCSV(columnMapping, fileNameCSV);
        String jsonCSV = listToJson(listCSV);
        writeString(jsonCSV, "data.json");

        List<Employee> listXML = parseXML(fileNameXML);
        String jsonXML = listToJson(listXML);
        writeString(jsonXML, "data2.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))){
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csv.parse();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }

    public static List<Employee> parseXML(String fileName){
        List<Employee> list = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()){
                    Element element = (Element) node;
                    list.add(new Employee(Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())));
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }

    public static String listToJson (List<Employee> list){
        Type listType = new TypeToken<List<Employee>>(){}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String str, String fileName){
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(str);
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
