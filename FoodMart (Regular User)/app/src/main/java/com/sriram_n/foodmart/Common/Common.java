package com.sriram_n.foodmart.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sriram_n.foodmart.Model.User;

import java.text.NumberFormat;
import java.util.Locale;

public class Common {
    public static User currentUser;
    public static String userUid;
    public static final int PICK_IMAGE_REQUEST = 71;
    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return  "Đã đặt hàng.";
        else if(status.equals("1"))
            return "Đơn đặt hàng đang được chuẩn bị.";
        else
            return "Giao hàng!";
    }
    public static String formatPrice(Object price){
        Locale locale = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        return fmt.format(price);
    }
    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for(int i=0;i<info.length;i++)
                {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
    public static final String DETAIL = "Xem chi tiết";
    public static final String DELETE = "Xóa";
    public static final String USER_KEY = "Tài khoản";
    public static final String PWD_KEY = "Mật khẩu";

}
