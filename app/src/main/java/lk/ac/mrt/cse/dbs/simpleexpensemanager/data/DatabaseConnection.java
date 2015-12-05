package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;

/**
 * Created by Imesha Sudasingha on 05/12/2015.
 */
public class DatabaseConnection extends SQLiteOpenHelper {

    private static DatabaseConnection dbHelper = null;

    private static Context context;

    private static String CREATE_TABLE_ACCOUNT = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_ACCOUNT +
            "(" + Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER + " varchar(20) primary key, " + Constants.TABLE_ACCOUNT_COLUMN_BANKNAME + " varchar(100) not null," +
            " " + Constants.TABLE_ACCOUNT_COLUMN_HOLDERNAME + " varchar(100) not null, " + Constants.TABLE_ACCOUNT_COLUMN_BALANCE + " double default 0 check(balance>=0))";

    private static String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + Constants.TABLE_TRANSACTION +
            " (" + Constants.TABLE_TRANSACTION_COLUMN_DATE + " DATETIME default '0000-00-00 00:00:00'," +
            Constants.TABLE_TRANSACTION_COLUMN_ACCOUNTNUMBER + " varchar(20), " +
            "" + Constants.TABLE_TRANSACTION_COLUMN_EXPENSETYPE + " varchar(7) check(expenseType in ('EXPENSE','INCOME')), " +
            Constants.TABLE_TRANSACTION_COLUMN_AMOUNT + " double default 0 check(amount>0)," +
            "primary key (" + Constants.TABLE_TRANSACTION_COLUMN_DATE + "," + Constants.TABLE_TRANSACTION_COLUMN_ACCOUNTNUMBER + "," +
            Constants.TABLE_TRANSACTION_COLUMN_EXPENSETYPE + "," + Constants.TABLE_TRANSACTION_COLUMN_AMOUNT +
            "), foreign key (" + Constants.TABLE_TRANSACTION_COLUMN_ACCOUNTNUMBER + ") references " + Constants.TABLE_ACCOUNT + "(" + Constants.TABLE_ACCOUNT_COLUMN_ACCOUNTNUMBER +
            ") on delete cascade on update cascade)";

    /**
     * Private constructor for Database connection
     */
    private DatabaseConnection() {
        super(context, Constants.DATABASE_NAME, null, 1);
    }


    /**
     * Create database if not exist, create tables if not exist.
     * Create all the tables when creating
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ACCOUNT);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * Get the writable database
     *
     * @return
     */
    public static SQLiteDatabase getWritingDatabase() {
        if (dbHelper == null) {
            dbHelper = new DatabaseConnection();
        }

        return dbHelper.getWritableDatabase();

    }


    /**
     * Get the readable database
     *
     * @return
     */
    public static SQLiteDatabase getReadingDatabase() {
        if (dbHelper == null) {
            dbHelper = new DatabaseConnection();
        }

        return dbHelper.getReadableDatabase();
    }


    public static void setContext(Context context) {
        if (DatabaseConnection.context == null) {
            DatabaseConnection.context = context;
        }
    }


}
