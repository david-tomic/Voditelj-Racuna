package davidtomic.projekti.diplomskiprojekt;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ReceiptDao {

    @Insert
    void insert(Receipt receipt);

    @Update
    void update(Receipt receipt);

    @Delete
    void delete(Receipt receipt);

    @Query("DELETE FROM receipts")
    void deleteAll();

    @Query("SELECT * FROM receipts ORDER BY id DESC")
    LiveData<List<Receipt>> getAllReceipts();
}
