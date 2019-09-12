package davidtomic.projekti.diplomskiprojekt;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = Receipt.class, version = 1, exportSchema = false)
@TypeConverters({DataConverter.class})
public abstract class ReceiptDatabase extends RoomDatabase {

    private static ReceiptDatabase instance;

    public abstract ReceiptDao receiptDao();

    public static synchronized ReceiptDatabase getInstance(Context context) {
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), ReceiptDatabase.class, "receipt_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
