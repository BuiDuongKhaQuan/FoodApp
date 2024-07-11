package com.sriram_n.foodmart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sriram_n.foodmart.Common.Common;
import com.sriram_n.foodmart.Model.User;

import io.paperdb.Paper;

public class sign_in_tab extends Fragment {

    EditText editPhone, editPassword;
    Button btnLogIn;
    CheckBox ckbRemember;
    TextView editForgot;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_login_tab, container, false);
        Paper.init(getContext());
        editPhone = v.findViewById(R.id.edtPhone);
        editPassword = v.findViewById(R.id.edtPassword);
        btnLogIn = v.findViewById(R.id.btnSignIn);
        ckbRemember = v.findViewById(R.id.ckbRemember);
        editForgot = v.findViewById(R.id.editForgot);
        firebaseAuth = FirebaseAuth.getInstance();
        editForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ForgotPassword.class);
                startActivity(i);
            }
        });

        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd != null) {
            if (!user.isEmpty() && !pwd.isEmpty())
                login(user, pwd);
        }
        //init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPhone.getText().toString().trim().length() == 0 || editPassword.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!!!", Toast.LENGTH_SHORT).show();
                } else if (Common.isConnectedToInternet(getContext())) {
                    if (ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, editPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, editPassword.getText().toString());
                    }
                    final ProgressDialog mDialog = new ProgressDialog(getContext());
                    mDialog.setMessage("Vui lòng chờ...");
                    mDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(editPhone.getText().toString(), editPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    table_user.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            mDialog.dismiss();
                                            User user = dataSnapshot.child(authResult.getUser().getUid()).getValue(User.class);
                                            System.out.println(authResult.getUser().getUid());
                                            Intent i = new Intent(getContext(), HomeDeliveryLiquidSwipe.class);
                                            startActivity(i);
                                            Common.currentUser = user;
                                            Toast.makeText(getContext(), "Đã đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
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
        });
        return v;
    }

    private void login(final String phone, final String pwd) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectedToInternet(getContext())) {

            final ProgressDialog mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Vui lòng chờ...");
            mDialog.show();
            firebaseAuth.signInWithEmailAndPassword(phone,pwd)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            table_user.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    mDialog.dismiss();
                                    User user = dataSnapshot.child(authResult.getUser().getUid()).getValue(User.class);
//                                            user.setEmail(editPhone.getText().toString());
                                    System.out.println(authResult.getUser().getUid());
                                    Intent i = new Intent(getContext(), Home.class);
                                    startActivity(i);
                                    Common.currentUser = user;
                                    Common.userUid = authResult.getUser().getUid();
                                    Toast.makeText(getContext(), "Đã đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối của bạn!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}



