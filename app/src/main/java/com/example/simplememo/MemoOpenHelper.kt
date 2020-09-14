package com.example.simplememo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build

class MemoOpenHelper
// コンストラクタ　以下のように呼ぶこと
    (context: Context) : SQLiteOpenHelper(context, DBName, null, VERSION){

    override fun onCreate(db: SQLiteDatabase) {
        /**
         * テーブルを作成する
         * execSQLメソッドにCREATET TABLE命令を文字列として渡すことで実行される
         * 引数で指定されているものの意味は以下の通り
         * 引数1 ・・・ id：列名 , INTEGER：数値型 , PRIMATY KEY：テーブル内の行で重複無し , AUTOINCREMENT：1から順番に振っていく
         * 引数2 ・・・ uuid：列名 , TEXT：文字列型
         * 引数3 ・・・ body：列名 , TEXT：文字列型
         */

        db.execSQL(
            "CREATE TABLE MEMO_TABLE (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT, " +
                    "body TEXT)"
        )
    }

    // データベースをバージョンアップした時に実行される処理
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /**
         * テーブルを削除する
         */
        db.execSQL("DROP TABLE IF EXISTS MEMO_TABLE")

        // 新しくテーブルを作成する
        onCreate(db)
    }

    // データベースが開かれた時に実行される処理
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
    }

    companion object{
        // データベース名
        private val DBName = "MEMO_DB"
        // データベースのバージョン(2,3と挙げていくとonUpgradeメソッドが実行される)
        private val VERSION = 1
    }
}