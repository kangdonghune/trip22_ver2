package com.sw.HeyBuddy2.FindBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nordan.dialog.Animation;
import com.nordan.dialog.DialogType;
import com.nordan.dialog.NordanAlertDialog;
import com.nordan.dialog.NordanAlertDialogListener;
import com.sw.HeyBuddy2.Main.QMainActivity;
import com.sw.HeyBuddy2.R;

import java.util.HashMap;
import java.util.Map;

import xyz.hasnat.sweettoast.SweetToast;

public class Report extends AppCompatActivity {

    TextView report_Name;
    EditText report_content;
    Button report_submit;
    String reportName, reportUid;
    private FirebaseFirestore db;
    private String currentUserId;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        setContentView(R.layout.activity_report);
        reportUid = getIntent().getExtras().get("reportId").toString();
//        Log.d("유저리포트", "대상: " + reportUid);
        report_submit=(Button)findViewById(R.id.report_submit);
        report_Name=(TextView)findViewById(R.id.reportName);
        report_content=(EditText)findViewById(R.id.report_contents);

        reportName=getIntent().getExtras().get("reportName").toString();

        report_Name.setText(reportName);

        report_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NordanAlertDialog.Builder alert=new NordanAlertDialog.Builder(Report.this);
                alert.setAnimation(Animation.SLIDE);
                alert.setDialogType(DialogType.INFORMATION);
                alert.setTitle("Report User");
                alert.setMessage("Are you sure you want to report this user?");
                alert.isCancellable(true);
                alert.setPositiveBtnText("Report");
                alert.setNegativeBtnText("Cancel");
                alert.onPositiveClicked(new NordanAlertDialogListener() {
                    @Override
                    public void onClick() {
                        alert.build().dismiss();
                        DocumentReference reportRef = db.collection("Report").document();
//                        String reportDocUid = reportRef.getId();
                        Map<String, Object> reportMap = new HashMap<>();
                        reportMap.put("ReportUser", currentUserId);
                        reportMap.put("ReportedUser", reportUid);
                        reportMap.put("description", report_content.getText().toString());
                        reportMap.put("timestamp", FieldValue.serverTimestamp());
                        reportRef.set(reportMap);
                        new NordanAlertDialog.Builder(Report.this).setDialogType(DialogType.SUCCESS)
                                .setTitle("Report Complete")
                                .setMessage("Your report has been sent to administrator.\n Sorry for your discomfort.")
                                .setPositiveBtnText("Fine")
                                .onPositiveClicked(new NordanAlertDialogListener() {
                                    @Override
                                    public void onClick() {
                                        Intent goFindBuddy=new Intent(getApplicationContext(), QMainActivity.class);
                                        startActivity(goFindBuddy);
                                        finish();
                                    }
                                })
                                .build().show();
                    }
                });
                alert.build().show();
            }
        });
    }
}