package com.example.androidgeekproject.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.androidgeekproject.R;

public class CompoundView extends RelativeLayout {
    EditText phoneInput, smsInput;
    Button callBtn, sendSmsBtn;

    public CompoundView(Context context) {
        super(context);
        initViews(context);
    }

    public CompoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public CompoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.card_compound_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
        setOnCallBtnClickBehaviour();
        setOnSendSmsBtnClickBehaviour();
        //set listeners...
        //all other code...
    }

    private void initViews() {
        phoneInput = this.findViewById(R.id.telephoneInput);
        smsInput = this.findViewById(R.id.smsInput);
        callBtn = this.findViewById(R.id.callBtn);
        sendSmsBtn = this.findViewById(R.id.sendSms);
    }

    private void setOnCallBtnClickBehaviour() {
        callBtn.setOnClickListener(v -> {
            String phoneNumber = phoneInput.getText().toString().replace("-", "");

            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        });
    }

    private void setOnSendSmsBtnClickBehaviour() {
        sendSmsBtn.setOnClickListener(v -> {
            String smsText = smsInput.getText().toString();
            String phoneNumber = phoneInput.getText().toString().replace("-", "");
            String toNumberSms="smsto:" + phoneNumber;
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(toNumberSms));
            intent.putExtra("sms_body", smsText);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);

            /*SmsManager.getDefault().sendTextMessage(phoneNumber, null, smsText,
                    null, null);*/
        });
    }
}
