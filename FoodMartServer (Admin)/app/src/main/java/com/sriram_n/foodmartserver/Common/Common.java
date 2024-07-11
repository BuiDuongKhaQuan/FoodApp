package com.sriram_n.foodmartserver.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sriram_n.foodmartserver.Model.User;

public class Common {
    public static User currentUser;

    public static final String UPDATE = "Cập nhật";
    public static final String DELETE = "Xóa";
    public static final String DETAIL = "Xem chi tiết";
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String USER_KEY = "Tài khoản";
    public static final String PWD_KEY = "Mật khẩu";
    public static String convertCodeToStatus(String code)
    {
        if(code.equals("0"))
            return "Đã đặt hàng";
        else if(code.equals("1"))
            return "Đơn hàng đang được chuẩn bị";
        else
            return "Đang vẫn chuyển!";
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
}
