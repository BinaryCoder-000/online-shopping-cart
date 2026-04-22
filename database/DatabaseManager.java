package database;

import java.io.*;
import java.util.*;
import model.Product;

public class DatabaseManager {

    private static final String FILE = "products.dat";

    @SuppressWarnings("unchecked")
    public static List<Product> loadProducts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            return (List<Product>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveProducts(List<Product> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}