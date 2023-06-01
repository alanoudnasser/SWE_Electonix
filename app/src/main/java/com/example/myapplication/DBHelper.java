package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "Electronix.db";
    private static final int DATABASE_VERSION = 2;
    private Context context;
    public DBHelper(@Nullable Context context) {
        super(context, DBNAME, null, DATABASE_VERSION);
        this.context = context;
    }
    private static final String DEVICE_TABLE = "device";
    private static final String USER_TABLE = "user";
    private static final String RENTAL_TABLE = "rental";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASS = "password";
    private static final String COLUMN_COMPANY = "company";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_DEVICE_ID = "device_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_RETURN_DATE = "return_date";
    private static final String COLUMN_RENTAL_DATE = "rental_date";
    private static final String[] DEFAULT_USERS = {
            "John-Doe,john@example.com,password123",
            "Jane-Smith,jane@example.com,secret456",
            "Mark-Johnson,mark@example.com,pass789"
    };

    private static final String[] DEFAULT_DEVICES = {
            "iPhone,Apple,1000,1,image1",
            "Galaxy S21,Samsung,900,1,image2",
            "Pixel 5,Google,800,2,image3"
    };

    private void seedUsers(SQLiteDatabase db) {
        for (String user : DEFAULT_USERS) {
            String[] userParts = user.split(    ",");
            String username = userParts[0];
            String email = userParts[1];
            String password = userParts[2];

            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_PASS, password);

            db.insert(USER_TABLE, null, values);
        }
    }
    private void seedDevices(SQLiteDatabase db) {
        for (String device : DEFAULT_DEVICES) {
            String[] deviceParts = device.split(",");
            String name = deviceParts[0];
            String company = deviceParts[1];
            int price = Integer.parseInt(deviceParts[2]);
            int userId = Integer.parseInt(deviceParts[3]);
            String imageName = deviceParts[4];

            int imageResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            byte[] imageBytes = getDrawableBytes(imageResourceId);

            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_COMPANY, company);
            values.put(COLUMN_PRICE, price);
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_IMAGE, imageBytes);

            db.insert(DEVICE_TABLE, null, values);
        }
    }

    private byte[] getDrawableBytes( int resourceId) {
        Drawable drawable = ContextCompat.getDrawable(context, resourceId);
        Bitmap bitmap = drawableToBitmap(drawable);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USER_TABLE + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASS + " TEXT);");
        db.execSQL("CREATE TABLE " + DEVICE_TABLE + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_COMPANY + " TEXT,"
                + COLUMN_PRICE + " INTEGER,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_IMAGE + " BLOB, " +
                "FOREIGN KEY ("+COLUMN_USER_ID+") REFERENCES "+ USER_TABLE + "("+COLUMN_ID+")"
                + ");");

        db.execSQL("CREATE TABLE " + RENTAL_TABLE + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RENTAL_DATE + " TEXT,"
                + COLUMN_RETURN_DATE + " TEXT,"
                + COLUMN_DEVICE_ID + " INTEGER,"
                + COLUMN_USER_ID + " TEXT,"
                +"FOREIGN KEY ("+COLUMN_USER_ID+") REFERENCES "+ USER_TABLE + "("+COLUMN_ID+"),"
                +"FOREIGN KEY ("+COLUMN_DEVICE_ID+") REFERENCES "+ DEVICE_TABLE + "("+COLUMN_ID+")"
                + ");");

        seedUsers(db);
        seedDevices(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists "+ USER_TABLE);
        MyDB.execSQL("drop Table if exists "+ DEVICE_TABLE);
        MyDB.execSQL("drop Table if exists "+ RENTAL_TABLE);
    }

    public Boolean insertUser(String username, String password, String email){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASS, password);
        contentValues.put(COLUMN_EMAIL, email);
        return MyDB.insert(USER_TABLE, null, contentValues) != -1;
    }

    public Boolean checkEmailExisted(String email) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from " + USER_TABLE + " where email = ?", new String[]{email});
        return cursor.getCount() > 0;
    }
    public Boolean checkUsernameExisted(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from " + USER_TABLE + " where username = ?", new String[]{username});
        return cursor.getCount() > 0;
    }

    public Boolean checkUsernameAndPassword(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from " + USER_TABLE + " where username = ? and password = ?", new String[] {username,password});
        if(cursor.getCount()>0) {
            User._username = username;
            return true;
        }
        return false;
    }
    public int getUserIdFromUsername (String username) {
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT " + COLUMN_ID + " FROM " + USER_TABLE
                            +" WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            return id;
        }
        return -1;
    }

    public List<Device> getAllDevices(HomepageActivity.Page page) {
        List<Device> deviceList = new ArrayList<>();


        String extraColumn  = "" ;
        if (page == HomepageActivity.Page.RENTALS) {
            extraColumn =" ,r." + COLUMN_ID + " as rentalId ";
        }
        String selectQuery = "SELECT d.*, u." + COLUMN_USERNAME + extraColumn+
                " FROM " + DEVICE_TABLE + " d" +
                " INNER JOIN " + USER_TABLE + " u" +
                " ON d." + COLUMN_USER_ID + " = u." + COLUMN_ID;

        String[] selectionArgs = null;
        int userId = getUserIdFromUsername(User._username);
        if (page == HomepageActivity.Page.MY_DEVICES ) {
            selectQuery += " WHERE d." + COLUMN_USER_ID + " = ? ";
            selectionArgs = new String[] { Integer.toString(userId) };
        }
        else if (page == HomepageActivity.Page.RENTALS) {
            selectQuery += " JOIN (SELECT " + COLUMN_USER_ID + " , " + COLUMN_ID + ", " + COLUMN_DEVICE_ID + " , max(" + COLUMN_RENTAL_DATE + ")"
                    + " FROM " + RENTAL_TABLE + " WHERE " + COLUMN_RETURN_DATE + " is NULL) r"
                    + " ON r." + COLUMN_DEVICE_ID  + " = d." + COLUMN_ID
                    + " WHERE r." + COLUMN_USER_ID  + " = ? ";
            selectionArgs = new String[] { Integer.toString(userId) };
        }
        else {
            selectQuery += " WHERE d." + COLUMN_USER_ID + " != ? ";
            selectionArgs = new String[] { Integer.toString(userId) };
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") String company = cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY));
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
                @SuppressLint("Range") int price = cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE));
                @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                Device device = new Device(id, name, company, price, -1, image);
                if (page == HomepageActivity.Page.RENTALS){
                    @SuppressLint("Range") int rentalId = cursor.getInt(cursor.getColumnIndex("rentalId"));
                    device.setRentalId(rentalId);
                }
                device.setUsername(username);
                deviceList.add(device);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return deviceList;
    }

    public void insertDevice(Device device) {
        SQLiteDatabase db = this.getWritableDatabase();
        device.setUserId(getUserIdFromUsername(User._username));
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, device.getName());
        values.put(COLUMN_COMPANY, device.getCompany());
        values.put(COLUMN_PRICE, device.getPrice());
        values.put(COLUMN_USER_ID,device.getUserId());
        values.put(COLUMN_IMAGE, device.getImage());
        db.insert(DEVICE_TABLE, null, values);
        db.close();
    }
    public void deleteDevice(int deviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DEVICE_TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(deviceId)});
        db.close();
    }

    public void rentDevice(int deviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        values.put(COLUMN_USER_ID, this.getUserIdFromUsername(User._username));
        values.put(COLUMN_DEVICE_ID,deviceId);
        values.put(COLUMN_RENTAL_DATE, today);
        db.insert(RENTAL_TABLE,  null, values);
        db.close();
    }
    public void returnDevice(int rentalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        values.put(COLUMN_RETURN_DATE, today);
        String where = COLUMN_ID + " = ?";
        db.update(RENTAL_TABLE,values,  where, new String[]{Integer.toString(rentalId)});
        db.close();
    }
}
