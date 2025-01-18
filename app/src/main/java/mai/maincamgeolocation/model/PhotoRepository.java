package mai.maincamgeolocation.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PhotoRepository {

    private DatabaseHelper dbHelper;

    public PhotoRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void savePhoto(Photo photo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FILE_PATH, photo.getFilePath());
        values.put(DatabaseHelper.COLUMN_LATITUDE, photo.getLatitude());
        values.put(DatabaseHelper.COLUMN_LONGITUDE, photo.getLongitude());
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, photo.getTimestamp());

        db.insert(DatabaseHelper.TABLE_PHOTOS, null, values);
        db.close();
    }

    public List<Photo> getAllPhotos() {
        List<Photo> photos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_FILE_PATH,
                DatabaseHelper.COLUMN_LATITUDE,
                DatabaseHelper.COLUMN_LONGITUDE,
                DatabaseHelper.COLUMN_TIMESTAMP
        };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PHOTOS,   // The table to query
                projection,                   // The columns to return
                null,                          // The columns for the WHERE clause
                null,                          // The values for the WHERE clause
                null,                          // Don't group the rows
                null,                          // Don't filter by row groups
                null                           // The sort order
        );

        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FILE_PATH));
            double latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE));
            String timestamp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP));

            Photo photo = new Photo(filePath, latitude, longitude, timestamp);
            photos.add(photo);
        }
        cursor.close();
        db.close();

        return photos;
    }
    public void deletePhoto(String filePath) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_PHOTOS, DatabaseHelper.COLUMN_FILE_PATH + " = ?", new String[]{filePath});
        db.close();
    }

}
