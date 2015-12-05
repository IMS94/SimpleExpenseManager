package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DatabaseConnection;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Imesha Sudasingha on 05/12/2015.
 */
public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteDatabase db;
    private Context context;


    /**
     * Overloaded constructor to get the context
     *
     * @param context
     */
    public PersistentTransactionDAO(Context context) {
        this.context = context;
        DatabaseConnection.setContext(context);
    }


    /***
     * Log the transaction requested by the user.
     *
     * @param date        - date of the transaction
     * @param accountNo   - account number involved
     * @param expenseType - type of the expense
     * @param amount      - amount involved
     */
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        db = DatabaseConnection.getWritingDatabase();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_TRANSACTION_COLUMN_DATE, formatter.format(date));
        values.put(Constants.TABLE_TRANSACTION_COLUMN_ACCOUNTNUMBER, accountNo);
        values.put(Constants.TABLE_TRANSACTION_COLUMN_EXPENSETYPE, expenseType.toString());
        values.put(Constants.TABLE_TRANSACTION_COLUMN_AMOUNT, amount);

        db.insert(Constants.TABLE_TRANSACTION, null, values);
        db.close();
    }

    /***
     * Return all the transactions logged.
     *
     * @return - a list of all the transactions
     */
    @Override
    public List<Transaction> getAllTransactionLogs() {
        db = DatabaseConnection.getReadingDatabase();

        List<Transaction> transactions = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM " + Constants.TABLE_TRANSACTION, null);

        while (c.moveToNext()) {
            String accountNumber = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_TRANSACTION_COLUMN_ACCOUNTNUMBER));
            double amount = c.getDouble(c.getColumnIndexOrThrow(Constants.TABLE_TRANSACTION_COLUMN_AMOUNT));

            //Recognizing the expense type to Java's Enum specified
            String type = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_TRANSACTION_COLUMN_EXPENSETYPE));
            ExpenseType expenseType = null;

            switch (type) {
                case "INCOME":
                    expenseType = ExpenseType.INCOME;
                    break;
                case "EXPENSE":
                    expenseType = ExpenseType.EXPENSE;
                    break;
            }

            //parse the date and convert to java Date
            String dateString = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_TRANSACTION_COLUMN_DATE));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = formatter.parse(dateString);
            } catch (ParseException e) {

            }

            transactions.add(new Transaction(date, accountNumber, expenseType, amount));

        }

        db.close();
        return transactions;
    }

    /***
     * Return a limited amount of transactions logged.
     *
     * @param limit - number of transactions to be returned
     * @return - a list of requested number of transactions
     */
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        db = DatabaseConnection.getReadingDatabase();

        List<Transaction> transactions = new ArrayList<>();


        Cursor c = db.query(Constants.TABLE_TRANSACTION, null, null, null, null, null, null, Integer.toString(limit));

        while (c.moveToNext()) {
            String accountNumber = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_TRANSACTION_COLUMN_ACCOUNTNUMBER));
            double amount = c.getDouble(c.getColumnIndexOrThrow(Constants.TABLE_TRANSACTION_COLUMN_AMOUNT));

            //Recognizing the expense type to Java's Enum specified
            String type = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_TRANSACTION_COLUMN_EXPENSETYPE));
            ExpenseType expenseType = null;

            switch (type) {
                case "INCOME":
                    expenseType = ExpenseType.INCOME;
                    break;
                case "EXPENSE":
                    expenseType = ExpenseType.EXPENSE;
                    break;
            }

            //parse the date and convert to java Date
            String dateString = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_TRANSACTION_COLUMN_DATE));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = formatter.parse(dateString);
            } catch (ParseException e) {

            }

            transactions.add(new Transaction(date, accountNumber, expenseType, amount));

        }


        db.close();
        return transactions;
    }
}
