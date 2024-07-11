package com.sriram_n.foodmart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sriram_n.foodmart.Common.Common;
import com.sriram_n.foodmart.Model.User;

public class sign_up_tab extends Fragment {

    EditText editPhone, editName, editPassword, editEmail;
    Button btnSignUp;
    TextView editForgot;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up_tab, container, false);

        editPhone = v.findViewById(R.id.edtPhone);
        editName = v.findViewById(R.id.edtName);
        editPassword = v.findViewById(R.id.edtPassword);
        editEmail = v.findViewById(R.id.edtEmail);
        btnSignUp = v.findViewById(R.id.btnSignUp);
        editForgot = v.findViewById(R.id.editForgot);

        editForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ForgotPassword.class);
                startActivity(i);
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editName.getText().toString().trim().length() == 0 || editPassword.getText().toString().trim().length() == 0
                        || editPhone.getText().toString().trim().length() == 0 || editEmail.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!!!", Toast.LENGTH_SHORT).show();
                } else {
                    if (Common.isConnectedToInternet(getContext())) {
                        final ProgressDialog mDialog = new ProgressDialog(getContext());
                        mDialog.setMessage("Vui lòng chờ...");
                        mDialog.show();
                        firebaseAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        mDialog.cancel();
                                        User user = new User(editName.getText().toString(), editEmail.getText().toString()
                                                ,editPhone.getText().toString() ,"");
                                        table_user.child(authResult.getUser().getUid()).setValue(user);
                                        Toast.makeText(getContext(), "Đăng ký thành công!!!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mDialog.cancel();
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối của bạn!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        return v;
    }
}

