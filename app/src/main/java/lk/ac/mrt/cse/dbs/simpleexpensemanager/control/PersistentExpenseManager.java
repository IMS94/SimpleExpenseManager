package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 * Created by Imesha Sudasingha on 05/12/2015.
 */
public class PersistentExpenseManager extends ExpenseManager {

    private Context context;

    /**
     * Overloaded context to obtain context for further use
     * @param context
     */
    public PersistentExpenseManager(Context context){
        this.context=context;
        setup();
    }

    /**
     * Implementation of the setup method in parent class
     */
    public void setup(){

        try {
            TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(context);
            setTransactionsDAO(persistentTransactionDAO);

            AccountDAO persistentAccountDAO = new PersistentAccountDAO(context);
            setAccountsDAO(persistentAccountDAO);

            // dummy data
            Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
            Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
            getAccountsDAO().addAccount(dummyAcct1);
            getAccountsDAO().addAccount(dummyAcct2);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
