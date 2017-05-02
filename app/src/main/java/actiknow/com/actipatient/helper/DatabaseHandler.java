package actiknow.com.actipatient.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "actipatient";


    public DatabaseHandler (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {

    }
/*
    // Table Names
    private static final String TABLE_SURVEY_TYPES = "survey_types";
    private static final String TABLE_ATMS = "atms";
    private static final String TABLE_REPORT = "report";
    private static final String TABLE_AUDITOR_LOCATION = "geo_location";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // QUESTIONS Table - column names
    private static final String KEY_QUESTION = "question";

    // ATMS Table - column names
    private static final String KEY_ATM_ID = "atm_id";
    private static final String KEY_ATM_UNIQUE_ID = "atm_unique_id";
    private static final String KEY_AGENCY_ID = "agency_id";
    private static final String KEY_LAST_AUDIT_DATE = "last_audit_date";
    private static final String KEY_BANK_NAME = "bank_name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CITY = "city";
    private static final String KEY_PINCODE = "pincode";

    // REPORT Table - column names
    private static final String KEY_AUDITOR_ID = "auditor_id";
    private static final String KEY_ISSUES_JSON = "issues_json";
    private static final String KEY_SIGN_IMAGE = "sign_image";
    private static final String KEY_RATING = "rating";
    private static final String KEY_GEO_IMAGE = "geo_image";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_OTHER_IMAGES_JSON = "other_images_json";


    // AUDITOR_LOCATION Table - column names
    private static final String KEY_TIME = "time";
    private static final String KEY_AUDITOR_LOCATION_ID = "auditor_location_id";

    // Question table Create Statements
    private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE "
            + TABLE_QUESTIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_QUESTION
            + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // ATM table create statement
    private static final String CREATE_TABLE_ATMS = "CREATE TABLE " + TABLE_ATMS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_AGENCY_ID + " INTEGER," + KEY_ATM_ID + " INTEGER," + KEY_ATM_UNIQUE_ID + " TEXT,"
            + KEY_LAST_AUDIT_DATE + " TEXT," + KEY_BANK_NAME + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_CITY + " TEXT,"
            + KEY_PINCODE + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // Report table create statement
    private static final String CREATE_TABLE_REPORT = "CREATE TABLE " + TABLE_REPORT
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ATM_ID + " INTEGER," + KEY_ATM_UNIQUE_ID + " TEXT," + KEY_AGENCY_ID + " INTEGER,"
            + KEY_AUDITOR_ID + " INTEGER," + KEY_ISSUES_JSON + " BLOB," +
            KEY_GEO_IMAGE + " BLOB," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," +
            KEY_SIGN_IMAGE + " BLOB," + KEY_OTHER_IMAGES_JSON + " BLOB," + KEY_TIME + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // Auditor location table create statement
    private static final String CREATE_TABLE_AUDITOR_LOCATION = "CREATE TABLE " + TABLE_AUDITOR_LOCATION
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_AUDITOR_ID + " INTEGER," +
            KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_TIME + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    public DatabaseHandler (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL (CREATE_TABLE_QUESTIONS);
        db.execSQL (CREATE_TABLE_ATMS);
        db.execSQL (CREATE_TABLE_REPORT);
        db.execSQL (CREATE_TABLE_AUDITOR_LOCATION);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_ATMS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_REPORT);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_AUDITOR_LOCATION);
        onCreate (db);
    }

    // ------------------------ "questions" table methods ----------------//

    public long createQuestion (Question question) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating Question", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_ID, question.getQuestion_id ());
        values.put (KEY_QUESTION, question.getQuestion ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long question_id = db.insert (TABLE_QUESTIONS, null, values);
        return question_id;
    }

    public Question getQuestion (long question_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_ID + " = " + question_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Question where ID = " + question_id, false);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Question question = new Question ();
        question.setQuestion_id (c.getInt (c.getColumnIndex (KEY_ID)));
        question.setQuestion ((c.getString (c.getColumnIndex (KEY_QUESTION))));
        return question;
    }

    public List<Question> getAllQuestions () {
        List<Question> questions = new ArrayList<Question> ();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all questions", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Question question = new Question ();
                question.setQuestion_id (c.getInt ((c.getColumnIndex (KEY_ID))));
                question.setQuestion ((c.getString (c.getColumnIndex (KEY_QUESTION))));
                questions.add (question);
            } while (c.moveToNext ());
        }
        return questions;
    }

    public int getQuestionCount () {
        String countQuery = "SELECT  * FROM " + TABLE_QUESTIONS;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total questions count : " + count, false);
        return count;
    }

    public int updateQuestion (Question question) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update questions", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_QUESTION, question.getQuestion ());
        // updating row
        return db.update (TABLE_QUESTIONS, values, KEY_ID + " = ?", new String[] {String.valueOf (question.getQuestion_id ())});
    }

    public void deleteQuestion (long question_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete question where ID = " + question_id, false);
        db.delete (TABLE_QUESTIONS, KEY_ID + " = ?",
                new String[] {String.valueOf (question_id)});
    }

    public void deleteAllQuestion () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all questions", false);
        db.execSQL ("delete from " + TABLE_QUESTIONS);
    }


    // ------------------------ "atms" table methods ----------------//

    public long createAtm (Atm atm) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating Atm", false);
        values.put (KEY_ID, atm.getAtm_id ());
        values.put (KEY_AGENCY_ID, atm.getAtm_agency_id ());
        values.put (KEY_ATM_ID, atm.getAtm_id ());
        values.put (KEY_ATM_UNIQUE_ID, atm.getAtm_unique_id ());
        values.put (KEY_LAST_AUDIT_DATE, atm.getAtm_last_audit_date ());
        values.put (KEY_BANK_NAME, atm.getAtm_bank_name ());
        values.put (KEY_ADDRESS, atm.getAtm_address ());
        values.put (KEY_CITY, atm.getAtm_city ());
        values.put (KEY_PINCODE, atm.getAtm_pincode ());
        values.put (KEY_CREATED_AT, getDateTime ());
        long atm_id = db.insert (TABLE_ATMS, null, values);
        return atm_id;
    }

    public Atm getAtm (long atm_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_ATMS + " WHERE " + KEY_ID + " = " + atm_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Atm where ID = " + atm_id, false);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        Atm atm = new Atm ();
        atm.setAtm_id (c.getInt (c.getColumnIndex (KEY_ID)));
        atm.setAtm_agency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
        atm.setAtm_id (c.getInt (c.getColumnIndex (KEY_ATM_ID)));
        atm.setAtm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_UNIQUE_ID)));
        atm.setAtm_last_audit_date (c.getString (c.getColumnIndex (KEY_LAST_AUDIT_DATE)));
        atm.setAtm_bank_name (c.getString (c.getColumnIndex (KEY_BANK_NAME)));
        atm.setAtm_address (c.getString (c.getColumnIndex (KEY_ADDRESS)));
        atm.setAtm_city (c.getString (c.getColumnIndex (KEY_CITY)));
        atm.setAtm_pincode (c.getString (c.getColumnIndex (KEY_PINCODE)));
        return atm;
    }

    public List<Atm> getAllAtms () {
        List<Atm> atms = new ArrayList<Atm> ();
        String selectQuery = "SELECT  * FROM " + TABLE_ATMS;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all atm", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                Atm atm = new Atm ();
                atm.setAtm_agency_id (c.getInt (c.getColumnIndex (KEY_AGENCY_ID)));
                atm.setAtm_id (c.getInt (c.getColumnIndex (KEY_ATM_ID)));
                atm.setAtm_unique_id (c.getString (c.getColumnIndex (KEY_ATM_UNIQUE_ID)));
                atm.setAtm_last_audit_date (c.getString (c.getColumnIndex (KEY_LAST_AUDIT_DATE)));
                atm.setAtm_bank_name (c.getString (c.getColumnIndex (KEY_BANK_NAME)));
                atm.setAtm_address (c.getString (c.getColumnIndex (KEY_ADDRESS)));
                atm.setAtm_city (c.getString (c.getColumnIndex (KEY_CITY)));
                atm.setAtm_pincode (c.getString (c.getColumnIndex (KEY_PINCODE)));
                atms.add (atm);
            } while (c.moveToNext ());
        }
        return atms;
    }

    public int getAtmCount () {
        String countQuery = "SELECT  * FROM " + TABLE_ATMS;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total atm count : " + count, false);
        return count;
    }

    public int updateAtm (Atm atm) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update atm", false);
        ContentValues values = new ContentValues ();
        values.put (KEY_ATM_ID, atm.getAtm_id ());
        values.put (KEY_ATM_UNIQUE_ID, atm.getAtm_unique_id ());
        values.put (KEY_AGENCY_ID, atm.getAtm_agency_id ());
        values.put (KEY_LAST_AUDIT_DATE, atm.getAtm_last_audit_date ());
        values.put (KEY_BANK_NAME, atm.getAtm_bank_name ());
        values.put (KEY_ADDRESS, atm.getAtm_address ());
        values.put (KEY_CITY, atm.getAtm_city ());
        values.put (KEY_PINCODE, atm.getAtm_pincode ());
        // updating row
        return db.update (TABLE_ATMS, values, KEY_ID + " = ?", new String[] {String.valueOf (atm.getAtm_id ())});
    }

    public void deleteAtm (long atm_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete atm where ID = " + atm_id, false);
        db.delete (TABLE_ATMS, KEY_ID + " = ?", new String[] {String.valueOf (atm_id)});
    }

    public void deleteAllAtms () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all atm", false);
        db.execSQL ("delete from " + TABLE_ATMS);
    }

    // ------------------------ "reports" table methods ----------------//



    public void closeDB () {
        SQLiteDatabase db = this.getReadableDatabase ();
        if (db != null && db.isOpen ())
            db.close ();
    }

    private String getDateTime () {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.getDefault ());
        Date date = new Date ();
        return dateFormat.format (date);
    }

    */
}