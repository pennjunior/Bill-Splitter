package jp.ac.jec.cm250122.a01xxstb;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.temporal.Temporal;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "andriod404";
    private EditText edtTotalPay; //for entering the payment amount
    private Spinner spNumber; //for the number of people input
    private TextView txtCollectMoney; //一人当たりの支払い金額表示用(how much each person is paying)
    private TextView txtMyPay;
    private int collect;
    private int KanjiCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtTotalPay = findViewById(R.id.editTotalPay);
        spNumber = findViewById(R.id.spNumber);
        txtCollectMoney = findViewById(R.id.txtCollectPay);
        txtMyPay = findViewById(R.id.txtMyPay);

        edtTotalPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final boolean enable = editable.toString().isEmpty(); //Whether the payment amount has been entered
                final Button btnCalc = findViewById(R.id.btnCalc); //call the calculation button in XML from the Java world
                btnCalc.setEnabled(!enable);  //making sure the calc button is not pressed when the payment amount is not inputted
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        final Button btnCalc = findViewById(R.id.btnCalc);//計算ボタン
        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ttPay = edtTotalPay.getText().toString();
                final String spNum = spNumber.getSelectedItem().toString();
                final int totalPay = Integer.parseInt(ttPay);
                final int number = Integer.parseInt(spNum);
                doCalculate(totalPay, number);

                txtCollectMoney.setText(getString(R.string.format_yen, collect));
                txtMyPay.setText(getString(R.string.format_yen, KanjiCollect));

//                txtMyPay.setText(doCalculate(totalPay, number));

            }
        });
    }
    public void doCalculate(int totalPay, int number){
        Log.i(TAG, "totalpay"+ totalPay + "people to share" + number);

        final double result = totalPay/number;
        Log.i(TAG, "what was shared" + result);
        double floorResult = Math.floor(result);
        collect = (int) floorResult;
        KanjiCollect = (int) totalPay;
//        txtCollectMoney.setText(String.valueOf(collect));
//        txtMyPay.setText(String.valueOf(totalPay));

    }

}