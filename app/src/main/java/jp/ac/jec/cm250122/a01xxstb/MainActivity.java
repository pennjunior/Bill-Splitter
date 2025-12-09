package jp.ac.jec.cm250122.a01xxstb;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.temporal.Temporal;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "android404";
    private EditText edtTotalPay;
    private Spinner spNumber;
    private RadioGroup rgRounding;
    private CheckBox chkKanjiMode;
    private CheckBox chkNijiMode;
    private TextView txtCollectMoney;
    private TextView txtMyPay;
    private TextView txtNijiMoney;
    private TextView lblNijiMoney;

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

        // Initialize views
        edtTotalPay = findViewById(R.id.editTotalPay);
        spNumber = findViewById(R.id.spNumber);
        rgRounding = findViewById(R.id.rgRounding);
        chkKanjiMode = findViewById(R.id.chkKanjiMode);
        chkNijiMode = findViewById(R.id.chkNijiMode);
        txtCollectMoney = findViewById(R.id.txtCollectPay);
        txtMyPay = findViewById(R.id.txtMyPay);
        txtNijiMoney = findViewById(R.id.txtNijiMoney);
        lblNijiMoney = findViewById(R.id.lblNijiMoney);

        // Initially hide Niji (second party) fields
        txtNijiMoney.setVisibility(View.GONE);
        lblNijiMoney.setVisibility(View.GONE);

        // Enable/disable calculate button based on input
        edtTotalPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                final boolean enable = editable.toString().isEmpty();
                final Button btnCalc = findViewById(R.id.btnCalc);
                btnCalc.setEnabled(!enable);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        // Show/hide Niji money fields
        chkNijiMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtNijiMoney.setVisibility(View.VISIBLE);
                    lblNijiMoney.setVisibility(View.VISIBLE);
                } else {
                    txtNijiMoney.setVisibility(View.GONE);
                    lblNijiMoney.setVisibility(View.GONE);
                }
            }
        });

        // Calculate button
        final Button btnCalc = findViewById(R.id.btnCalc);
        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate();
            }
        });
    }

    private void calculate() {
        // Get input values
        final String ttPay = edtTotalPay.getText().toString();
        if (ttPay.isEmpty()) return;

        final int totalPay = Integer.parseInt(ttPay);
        final String spNum = spNumber.getSelectedItem().toString();
        final int number = Integer.parseInt(spNum);

        // Get rounding unit (100, 500, or 1000)
        int roundingUnit = 100; // default
        int selectedRoundingId = rgRounding.getCheckedRadioButtonId();
        if (selectedRoundingId == R.id.rb500) {
            roundingUnit = 500;
        } else if (selectedRoundingId == R.id.rb1000) {
            roundingUnit = 1000;
        }

        // Get modes
        boolean isKanjiMode = chkKanjiMode.isChecked();
        boolean isNijiMode = chkNijiMode.isChecked();

        // Calculate
        int perPersonPay;
        int kanjiPay;
        int nijiMoney = 0;

        if (isNijiMode) {
            // 二次会モード: Everyone pays the same (rounded up)
            double perPersonRaw = (double) totalPay / number;
            perPersonPay = roundUp(perPersonRaw, roundingUnit);
            kanjiPay = perPersonPay;

            // Calculate leftover for second party
            int totalCollected = perPersonPay * number;
            nijiMoney = totalCollected - totalPay;

        } else if (isKanjiMode) {
            // 幹事得モード: Organizer benefits
            double perPersonRaw = (double) totalPay / number;
            perPersonPay = roundUp(perPersonRaw, roundingUnit);

            // Kanji pays less (gets the benefit)
            int totalCollected = perPersonPay * (number - 1); // others pay
            kanjiPay = totalPay - totalCollected;

        } else {
            // 通常モード: Organizer pays the difference
            double perPersonRaw = (double) totalPay / number;
            perPersonPay = roundDown(perPersonRaw, roundingUnit);

            // Kanji pays the remainder
            int totalCollected = perPersonPay * (number - 1); // others pay
            kanjiPay = totalPay - totalCollected;
        }

        Log.i(TAG, String.format("Total: %d, Number: %d, Rounding: %d", totalPay, number, roundingUnit));
        Log.i(TAG, String.format("Per Person: %d, Kanji: %d, Niji: %d", perPersonPay, kanjiPay, nijiMoney));

        // Update UI
        txtCollectMoney.setText(getString(R.string.format_yen, perPersonPay));
        txtMyPay.setText(getString(R.string.format_yen, kanjiPay));

        if (isNijiMode) {
            txtNijiMoney.setText(getString(R.string.format_yen, nijiMoney));
        }
    }

    // Round down to nearest unit
    private int roundDown(double value, int unit) {
        return (int) (Math.floor(value / unit) * unit);
    }

    // Round up to nearest unit
    private int roundUp(double value, int unit) {
        return (int) (Math.ceil(value / unit) * unit);
    }
}