import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import model.Candy;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        Gson gson = new Gson();

        //This is required to allow GET and POST requests with the header 'content-type'
        options("/*",
                (request, response) -> {
                        response.header("Access-Control-Allow-Headers",
                                "content-type");

                        response.header("Access-Control-Allow-Methods",
                                "GET, POST");


                    return "OK";
                });

        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));

        //TODO: Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            FileInputStream inputStream = new FileInputStream("./resources/Inventory.xlsx");
            
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = firstSheet.iterator();
            Map<Double, Candy> candies = new HashMap<>();
            
            //Skip column headers row
            rowIterator.next();
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String name = row.getCell(0).getStringCellValue();
                double stock = row.getCell(1).getNumericCellValue();
                double capacity = row.getCell(2).getNumericCellValue();
                double id = row.getCell(3).getNumericCellValue();
                
                double stockLeftPercent = (stock / capacity) * 100;
                final int LOW_STOCK_THRESH = 25;
                
                if (stockLeftPercent < LOW_STOCK_THRESH) {
                    Candy candy = new Candy(name, stock, capacity, id);
                    candies.put(candy.getId(), candy); 
                }
            }
            
            return new ArrayList<>(candies.values());
        }, gson::toJson);

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
            Map<Integer, Integer> orders = gson.fromJson(request.body(), new TypeToken<HashMap<Integer, Integer>>(){}.getType());
            Map<Integer, Double> lowestCost = new HashMap<>();
            
            FileInputStream inputStream = new FileInputStream("./resources/Distributors.xlsx");
            
            Workbook workbook = new XSSFWorkbook(inputStream);
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                Iterator<Row> rowIterator = sheet.rowIterator();
                
                //Skip column headers row
                rowIterator.next();
            
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    //Some seemingly empty rows are being read, quick fix
                    if (row.getCell(0) == null) continue;
                    
                    int id = (int) row.getCell(1).getNumericCellValue();
                    double cost = row.getCell(2).getNumericCellValue();
                    
                    if (orders.containsKey(id)) {
                       lowestCost.computeIfPresent(id, (key, val) -> cost < val ? cost : val);
                       lowestCost.computeIfAbsent(id, (key) -> cost);
                    }
                }
            }
            
            double totalCost = 0;
            
            for (int id : orders.keySet()) {
                int amount = orders.get(id);
                double cost = lowestCost.get(id);
                
                totalCost += (amount * cost);
            }
           
            Map<String, Double> result = new HashMap<>();
            result.put("cost", totalCost);
            
            return result;
        }, gson::toJson);

    }
}
