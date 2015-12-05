package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DatabaseConnection;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Imesha Sudasingha on 05/12/2015.
 */
public class PersistentAccountDAO implements AccountDAO {
    private SQLiteDatabase db = null;
    private Context context;

    public PersistentAccountDAO(Context context) {
        this.context = context;
        DatabaseConnection.setContext(context);
    }

    /**
     * Get the account numbers from the database
     *
     * @return
     */
    @Override
    public List<String> getAccountNumbersList() {

        db = DatabaseConnection.getWritingDatabase();
        try {
            Cursor c = db.query(Constants.TABLE_ACCOUNT, new String[]{Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER},
                    null, null, null, null, null);
            List<String> accountNumbers = new ArrayList<>();
            while (c.moveToNext()) {
                accountNumbers.add(c.getString(c.getColumnIndexOrThrow(Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER)));
            }

            db.close();
            return accountNumbers;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Account> getAccountsList() {
        db = DatabaseConnection.getWritingDatabase();
        Cursor c = db.rawQuery("SELECT * FROM ?", new String[]{Constants.TABLE_ACCOUNT});

        List<Account> accounts = new ArrayList<>();
        while (c.moveToNext()) {
            String accountNumber = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER));
            String bankName = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_ACCOUNT_COLUMN_BANKNAME));
            String holderName = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_ACCOUNT_COLUMN_HOLDERNAME));
            Double balance = c.getDouble(c.getColumnIndexOrThrow(Constants.TABLE_ACCOUNT_COLUMN_BALANCE));

            accounts.add(new Account(accountNumber, bankName, holderName, balance));
        }

        db.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        db = DatabaseConnection.getWritingDatabase();
        Cursor c = db.query(Constants.TABLE_ACCOUNT, null, Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER + "=?",
                new String[]{accountNo}, null, null, null);
        Account account = null;
        if (c.moveToFirst()) {
            String accountNumber = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER));
            String bankName = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_ACCOUNT_COLUMN_BANKNAME));
            String holderName = c.getString(c.getColumnIndexOrThrow(Constants.TABLE_ACCOUNT_COLUMN_HOLDERNAME));
            Double balance = c.getDouble(c.getColumnIndexOrThrow(Constants.TABLE_ACCOUNT_COLUMN_BALANCE));

            db.close();
            return new Account(accountNumber, bankName, holderName, balance);
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        db = DatabaseConnection.getWritingDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER, account.getAccountNo());
        values.put(Constants.TABLE_ACCOUNT_COLUMN_BANKNAME, account.getBankName());
        values.put(Constants.TABLE_ACCOUNT_COLUMN_HOLDERNAME, account.getAccountHolderName());
        values.put(Constants.TABLE_ACCOUNT_COLUMN_BALANCE, account.getBalance());

        db.insert(Constants.TABLE_ACCOUNT, null, values);
        db.close();

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        db = DatabaseConnection.getWritingDatabase();
        int rowsAffected = db.delete(Constants.TABLE_ACCOUNT, Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER + "=?", new String[]{accountNo});
        db.close();
        if (rowsAffected == 0) {
            throw new InvalidAccountException(accountNo + " in not found!");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = getAccount(accountNo);
        double newBalance = 0;

        switch (expenseType) {
            case EXPENSE:
                newBalance = account.getBalance() - amount >= 0 ? account.getBalance() - amount : account.getBalance();
                break;
            case INCOME:
                newBalance = account.getBalance() + amount;
                break;
        }
        account.setBalance(newBalance);

        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_ACCOUNT_COLUMN_BALANCE, account.getBalance());

        db = DatabaseConnection.getWritingDatabase();
        try {
            db.update(Constants.TABLE_ACCOUNT, values, Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER + "=?", new String[]{accountNo});
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        db.close();
    }

}
