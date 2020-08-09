import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String inputFileName = args[0];
        String outputFileName = args[1];
        List<Employee> list = parseCSV(columnMapping, inputFileName);
        String json = listToJson(list);
        stringToFile(outputFileName, json);
        printJSONFile(outputFileName);
    }

    private static void printJSONFile(String jsonFileName) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(jsonFileName));
            JSONArray jsonObjects = (JSONArray) obj;
            jsonObjects.forEach(System.out::println);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName));) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            List<Employee> list = csv.parse();
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void stringToFile(String fileName, String jsonString) {
        File file = new File(fileName);
        try (Writer writer = new FileWriter(file, false)) {
            writer.write(jsonString.toString());
        } catch (IOException ex) {
            System.out.printf("Ошибка сохранения файла %s:", ex.getMessage());
        }
    }
}

