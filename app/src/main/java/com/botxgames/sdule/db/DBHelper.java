package com.botxgames.sdule.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * All queries to executed on the database should be lauched through this class only.
 * Any operation on database should be COMPULSORLY done through this class
 */
public class DBHelper extends SQLiteOpenHelper {


	public static final String DB_NAME = "sdule.db";
	private static final int DB_VERSION = 9;


	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {

		final String[] newTables = {
				TAct.q_CREATE_TABLE(),
		};

		for (String i : newTables) {
			db.execSQL(i);
		}

	}

	/**
	 * Use this method to change the database schemas i.e. add new table, drop tables
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


	}

	/**
	 * Provide Table and map of content values to be inserted.
	 * Return -1 if failed or ID of the newly inserted row
	 */
	public long insert(String tableName, ContentValues cv) {
		return getWritableDatabase().insert(tableName, null, cv);
	}

	/**
	 * Provide query string and clauses array to replace ?s in where clause.
	 * Cursor will be returned
	 * e.g.
	 * select("SELECT * FROM student WHERE ID = ?", arrayOf("1"))
	 */
	public Cursor select(String query, String[] clauses) {
		try {
			return getReadableDatabase().rawQuery(query, clauses);
		} catch (SQLiteException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Provide name of table to be updated, content values, where string and array of clauses
	 * e.g.
	 * <p>
	 * update("Student", {roll->'1'}, "name = ?", {"Pranay"})
	 */
	public void update(String tableName, ContentValues cv, String where, String[] clauses) {
		getWritableDatabase().update(tableName, cv, where, clauses);
	}

	/**
	 * Do not use this to INSERT, UPDATE, DELETE rows
	 * Use for ALTER statements
	 */
	public void execute(String query) {
		getWritableDatabase().execSQL(query);
	}

	public void delete(String table, String where, String[] clauses) {
		getWritableDatabase().delete(table, where, clauses);
	}

}
