package com.sriram_n.foodmart;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.sriram_n.foodmart.Common.Common;
import com.sriram_n.foodmart.Model.User;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Profile extends AppCompatActivity {
    ImageView imageView;
    TextView nameUser;
    EditText editPhone, editEmail, editAdress;
    Button btnUpdate, btnAvater;
    FButton btnUploadImg, btnSelectImg;
    FirebaseDatabase database;
    DatabaseReference userRef;
    DatabaseReference requests;
    User user1;
    Uri saveUri;
    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Khởi tạo Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("User");
        userRef = database.getReference("User");

        nameUser = findViewById(R.id.user_name);
        editPhone = findViewById(R.id.editTextPhone);
        editEmail = findViewById(R.id.editTextTextEmailAddress);
        editAdress = findViewById(R.id.editTextTextPostalAddress);
        btnUpdate = findViewById(R.id.edit_profile_button);
        btnAvater = findViewById(R.id.selectAvatar);
        imageView = findViewById(R.id.avatar);
        storageReference = FirebaseStorage.getInstance().getReference();

        nameUser.setText(Common.currentUser.getName());
        editPhone.setText(Common.currentUser.getPhone());
        editEmail.setText(Common.currentUser.getEmail());
        editAdress.setText(Common.currentUser.getAddress());

        Picasso.with(getBaseContext()).load(Common.currentUser.getImage())
                .into(imageView);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentUser.setName(nameUser.getText().toString());
                Common.currentUser.setEmail(editEmail.getText().toString());
                Common.currentUser.setAddress(editAdress.getText().toString());
                requests.child(Common.userUid).setValue(Common.currentUser)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(v, "Thông tin người dùng đã được cập nhật", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(v, "Lỗi: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        btnAvater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
            }
        });
    }

    private void showDialog1() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Profile.this);
        alertDialog.setTitle("Cập nhật ảnh diện");

                LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_avatar, null);

        btnSelectImg = add_menu_layout.findViewById(R.id.btnSelect);
        btnUploadImg = add_menu_layout.findViewById(R.id.btnUpload);

        // Sự kiện cho nút chọn ảnh
        btnSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        // Sự kiện cho nút tải lên ảnh
        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);

        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                if (user1 != null) {
                    userRef.child(Common.userUid).child("image")
                            .setValue(user1.getImage())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(add_menu_layout, "Hình ảnh đại diện đã được cập nhật", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(add_menu_layout, "Lỗi: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            user1 = new User(saveUri.toString());
            Picasso.with(getBaseContext()).load(saveUri).into(imageView);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Đang tải lên...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("image/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Profile.this, "Tải lên thành công!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Lưu URL vào Firebase Realtime Database
                                    userRef.child(Common.userUid).child("image").setValue(uri.toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Thông báo cập nhật thành công
                                                    Toast.makeText(Profile.this, "Hình ảnh đã được cập nhật", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Xử lý khi lưu URL không thành công
                                                    Toast.makeText(Profile.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Profile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Đang tải " + progress + "%");
                        }
                    });
        }
    }
}