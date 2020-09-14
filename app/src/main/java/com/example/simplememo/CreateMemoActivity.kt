package com.example.simplememo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*

class CreateMemoActivity : AppCompatActivity() {

    // MemoOpenHelperクラスを定義
    internal var helper: MemoOpenHelper? = null

    // 新規フラグ
    internal var newFlag = false

    internal var id  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memo)

        // データベースから値を取得する
        if(helper == null){
            helper = MemoOpenHelper(this@CreateMemoActivity)
        }

        // ListActivityからインテントを取得
        val intent = this.intent

        // 値を取得
        id = intent.getStringExtra("id")!!


        // 画面に表示
                    if (id == "") {
                        // 新規作成の場合
                        newFlag = true

                    } else {
                        // 編集の場合 データベースから値を取得して表示
                        // データベースを取得する
                        val db = helper!!.writableDatabase
                        try{
                            // rawQueryというSELECT専用メソッドを使用してデータを取得する
                            val c = db.rawQuery("SELECT body FROM MEMO_TABLE WHERE uuid = '$id'", null)

                            // Cursorの先頭行があるかどうか確認
                            var next = c.moveToFirst()

                            // 取得した全ての行を取得
                            while(next){
                                // 取得したカラムの順番(0から始まる)と型を指定してデータを取得する
                                val disBody = c.getString(0)
                                val body = findViewById<View>(R.id.body) as EditText
                                body.setText(disBody, TextView.BufferType.NORMAL )
                                next = c.moveToNext()
                            }
                        }finally{
                            db.close()
                        }

                    }

        /**
         * 登録ボタン処理
         */
        // idがregisterのボタンを取得
        val registerButton = findViewById<View>(R.id.register) as Button

        registerButton.setOnClickListener{
            // 入力内容を取得する
            val body = findViewById<View>(R.id.body) as EditText
            val bodyStr = body.text.toString()

            // データベースに保存する
            val db = helper!!.writableDatabase
            try{
                if(newFlag){
                    // 新規作成の場合
                    // 新しくuuidを発行する
                    id = UUID.randomUUID().toString()

                    //INSERT
                    db.execSQL("INSERT INTO MEMO_TABLE(uuid, body) VALUES('$id', '$bodyStr')")
                }else{
                    db.execSQL("UPDATE MEMO_TABLE SET body = '$bodyStr' WHERE uuid = '$id'")
                }
            }finally{
                db.close()
            }
            // 保存後に一覧へ戻る
            val intent = Intent(this@CreateMemoActivity, ListActivity::class.java)
            startActivity(intent)
        }

        /**
         * 戻るボタン処理
         */
        // idがbackのボタンを取得
        val backButton = findViewById<View>(R.id.back) as Button
        backButton.setOnClickListener {
            // 保存せずに一覧へ戻る
            finish()
        }
    }
}