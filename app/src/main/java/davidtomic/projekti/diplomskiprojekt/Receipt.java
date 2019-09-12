package davidtomic.projekti.diplomskiprojekt;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.HashMap;

@Entity(tableName = "receipts")
public class Receipt {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String category ;
    private String date;
    private int icon ;
    private float total;
    private String imagePath;
    private HashMap<String, String> items = new HashMap<>();

    public Receipt(String category, String date, int icon, float total, String imagePath, HashMap<String, String> items) {
        this.category = category;
        this.date = date;
        this.icon = icon;
        this.total = total;
        this.imagePath = imagePath;
        this.items = items;
    }


    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public int getIcon() {
        return icon;
    }

    public float getTotal() {
        return total;
    }

    public String getImagePath() {
        return imagePath;
    }

    public HashMap<String, String> getItems() {
        return items;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItems(HashMap<String, String> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", icon=" + icon +
                ", total=" + total +
                ", imagePath='" + imagePath + '\'' +
                ", items=" + items +
                '}';
    }
}
