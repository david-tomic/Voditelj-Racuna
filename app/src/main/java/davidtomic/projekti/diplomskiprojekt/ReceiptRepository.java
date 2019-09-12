package davidtomic.projekti.diplomskiprojekt;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ReceiptRepository {

    private ReceiptDao receiptDao;
    private LiveData<List<Receipt>> receipts;

    public ReceiptRepository(Application application) {
        ReceiptDatabase database = ReceiptDatabase.getInstance(application);
        receiptDao = database.receiptDao();
        receipts = receiptDao.getAllReceipts();
    }

    public void insert(Receipt receipt) {
        new InsertReceiptAsyncTask(receiptDao).execute(receipt);
    }

    public void update(Receipt receipt) {
        new UpdateReceiptAsyncTask(receiptDao).execute(receipt);
    }

    public void delete(Receipt receipt) {
        new DeleteReceiptAsyncTask(receiptDao).execute(receipt);

    }

    public void deleteAll() {
        new DeleteAllNotesAsyncTask(receiptDao).execute();

    }

    public LiveData<List<Receipt>> getAllReceipts() {
        return receipts;
    }

    private static class InsertReceiptAsyncTask extends AsyncTask<Receipt, Void, Void> {
        private ReceiptDao receiptDao;

        private InsertReceiptAsyncTask(ReceiptDao receiptDao) {
            this.receiptDao = receiptDao;
        }

        @Override
        protected Void doInBackground(Receipt... receipts) {
            receiptDao.insert(receipts[0]);
            return null;
        }
    }

    private static class UpdateReceiptAsyncTask extends AsyncTask<Receipt, Void, Void> {
        private ReceiptDao receiptDao;

        private UpdateReceiptAsyncTask(ReceiptDao receiptDao) {
            this.receiptDao = receiptDao;
        }

        @Override
        protected Void doInBackground(Receipt... receipts) {
            receiptDao.update(receipts[0]);
            return null;
        }
    }

    private static class DeleteReceiptAsyncTask extends AsyncTask<Receipt, Void, Void> {
        private ReceiptDao receiptDao;

        private DeleteReceiptAsyncTask(ReceiptDao receiptDao) {
            this.receiptDao = receiptDao;
        }

        @Override
        protected Void doInBackground(Receipt... receipts) {
            receiptDao.delete(receipts[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
        private ReceiptDao receiptDao;

        private DeleteAllNotesAsyncTask(ReceiptDao receiptDao) {
            this.receiptDao = receiptDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            receiptDao.deleteAll();
            return null;
        }
    }
}
