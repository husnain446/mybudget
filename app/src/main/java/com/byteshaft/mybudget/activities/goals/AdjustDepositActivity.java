package com.byteshaft.mybudget.activities.goals;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.database.DBHelper;

public class AdjustDepositActivity extends AppCompatActivity {

    private String depositName;
    private String depositDate;
    private String itemName;
    private String goalName;
    private int depositAmount;
    private int remaining;

    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_deposit);

        myDb = DBHelper.getInstance(this);

        Bundle b = getIntent().getExtras();
        depositName = b.getString("DEPOSIT_NAME");
        depositDate = b.getString("DEPOSIT_DATE");
        depositAmount = b.getInt("DEPOSIT_AMOUNT");
        itemName = b.getString("ITEM_NAME");
        remaining = b.getInt("ITEM_REMAINING");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(depositName);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onSubmitClick(View v) {

        EditText newAmountView = (EditText) findViewById(R.id.amount);

        String newAmountStr = newAmountView.getText().toString();

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        // validation
        if (newAmountStr.equals("")) {

            text = "A new amount must be specified, please try again";
            Toast.makeText(context, text, duration).show();

        } else {

            int newAmount = Integer.parseInt(newAmountStr);

            if (newAmount > (remaining + depositAmount)) {   // calculates what the remaining would be minus this item

                text = "Amount exceeds remaining budget for that item, please try again";
                Toast.makeText(context, text, duration).show();

            } else {

                String parts[] = depositName.split(": ");
                goalName = parts[1];

                myDb.updateExpense(itemName, depositName, depositName, depositDate, newAmount);
                myDb.adjustDeposit(goalName, itemName, depositDate, newAmount);

                text = "Deposit has been adjusted";
                Toast.makeText(context, text, duration).show();

                finish();
            }
        }
    }

    public void onDeleteDepositClick(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                AdjustDepositActivity.this);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Context context = getApplicationContext();
                CharSequence text = "Deposit deleted";
                int duration = Toast.LENGTH_SHORT;

                String parts[] = depositName.split(": ");
                goalName = parts[1];

                myDb.deleteDeposit(goalName, itemName, depositName);

                Toast.makeText(context, text, duration).show();
                finish();
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Are you sure you want to delete this deposit?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();
    }
}
