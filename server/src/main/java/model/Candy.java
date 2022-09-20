package model;

public class Candy {
    private String name;
    private double stock;
    private double capacity;
    private double id;
    
    public Candy(String name, double stock, double capacity, double id) {
        this.name = name;
        this.stock = stock;
        this.capacity = capacity;
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public double getStock() {
        return stock;
    }
    
    public double getCapacity() {
        return capacity;
    }
    
    public double getId() {
        return id;
    }
}
