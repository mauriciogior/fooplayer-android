package com.mauriciogiordano.fooplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mauriciogiordano.fooplayer.config.Constants;

import java.sql.SQLException;

/**
 * Created by mauricio on 10/27/14.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static ConnectionSource connectionSource;

    private static DatabaseHelper databaseHelper = null;

    private DatabaseHelper(Context context)
    {
        super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION, Constants.DATABASE_CONFIG);
    }

    public static DatabaseHelper getInstance(Context context)
    {
        if(databaseHelper == null)
        {
            databaseHelper = new DatabaseHelper(context);
        }

        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource)
    {
        try
        {
            TableUtils.createTable(connectionSource, Artist.class);
            TableUtils.createTable(connectionSource, Album.class);
            TableUtils.createTable(connectionSource, Track.class);
            DatabaseHelper.connectionSource = connectionSource;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
                          int newVersion)
    {
        try
        {
            TableUtils.dropTable(connectionSource, Artist.class, true);
            TableUtils.dropTable(connectionSource, Album.class, true);
            TableUtils.dropTable(connectionSource, Track.class, true);
            onCreate(database, connectionSource);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}