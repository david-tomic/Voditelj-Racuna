package davidtomic.projekti.diplomskiprojekt;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class ReceiptViewModel extends AndroidViewModel {

    private ReceiptRepository repository;
    private LiveData<List<Receipt>> allReceipts;

    public ReceiptViewModel(@NonNull Application application) {
        super(application);

        repository = new ReceiptRepository(application);
        allReceipts = repository.getAllReceipts();
    }

    public void insert(Receipt receipt) {
        repository.insert(receipt);
    }

    public void update(Receipt receipt) {
        repository.update(receipt);
    }

    public void delete(Receipt receipt) {
        repository.delete(receipt);
    }

    public void deleteAll(Receipt receipt) {
        repository.deleteAll();
    }

    public LiveData<List<Receipt>> getAllReceipts() {
        return allReceipts;
    }
}
