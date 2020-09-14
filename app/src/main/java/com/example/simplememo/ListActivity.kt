package com.example.simplememo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class ListActivity : AppCompatActivity() {

    // MemoOpenHelperクラスを定義
    internal var helper: MemoOpenHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // データベースから値を取得する
        if (helper == null) {
            helper = MemoOpenHelper(this@ListActivity)
        }

        val memoList = ArrayList<HashMap<String, String>>()

        // データベースを取得する
        val db = helper!!.writableDatabase
        try {
            // rawQueryというSELECT専用メソッドを使用してデータを取得する
            val c = db.rawQuery("select uuid, body from MEMO_TABLE order by id", null)

            // Cursorの先頭行があるかどうか確認
            var next = c.moveToFirst()

            while (next) {
                val data = HashMap<String, String>()

                // 取得したカラムの順番(0から始まる)と型を指定してデータを取得する
                val uuid = c.getString(0)
                var body = c.getString(1)

                if (body.length > 10) {
                    // リストに表示するのは10文字まで
                    body = body.substring(0, 11) + "..."
                }

                // 引数には、(名前,実際の値)という組合せで指定します　名前はSimpleAdapterの引数で使用します
                data["body"] = body
                data["id"] = uuid

                memoList.add(data)

                // 次の行が存在するか確認
                next = c.moveToNext()
            }
        } finally {
            // finallyは、tryの中で例外が発生した時でも必ず実行される
            // dbを開いたら確実にclose
            db.close()
        }

        //データベースから値を取得
        //仮のデータを作成
//        val tmpList = ArrayList<HashMap<String, String>>()
////
////        for (i in 1..5) {
////            val data = HashMap<String, String>()
////
////            //引数は(名前, 実際の値)という組み合わせで指定
////            data["body"] = "サンプルデータ$i"
////            data["id"] = "sampleId$i"
////            tmpList.add(data)
////        }

        // Adapter生成
        // tmpListを正式なデータと入れ替える

        val simpleAdapter = SimpleAdapter(
            this,
            memoList,
            android.R.layout.simple_list_item_2, // 使用するレイアウト
            arrayOf("body", "id"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        //idがmemoListのListViewを取得
        val listView = findViewById<View>(R.id.memoList) as ListView
        listView.adapter = simpleAdapter

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                /**
                 * @param parent ListView
                 * @param view 選択した項目
                 * @param position 選択した項目の添え字
                 * @param id 選択した項目のID
                 */

                val intent = Intent(this, ListActivity::class.java)

                // 選択されたビューを取得 TwoLineListItemを取得した後、text2の値を取得する
                val two = view as TwoLineListItem
                val idTextView = two.text2 as TextView
                val isStr = idTextView.text as String
                // 値を引き渡す (識別名, 値)の順番で指定します
                intent.putExtra("id", isStr)
                // Activity起動
                startActivity(intent)
            }

        listView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, id ->
        /**
         * @param parent ListView
         * @param view 選択した項目
         * @param position 選択した項目の添え字
         * @param id 選択した項目のID
         */
        // 選択されたビューを取得 TwoLineListItemを取得した後、text2の値を取得する
        val two = view as TwoLineListItem
        val idTextView = two.text2 as TextView
        val idStr = idTextView.text as String

        val db = helper!!.writableDatabase
        try {
            db.execSQL("DELETE FROM MEMO_TABLE WHERE uuid = '$idStr'")
        } finally {
            db.close()
        }

        // 長押しした項目を画面から削除
        memoList.removeAt(position)
        simpleAdapter.notifyDataSetChanged()

        // trueにすることで通常のクリックイベントを発生させない
        true
        }



        /**
         * 新規作成するボタン処理
         */
        // idがnewButtonのボタンを取得
        val newButton = findViewById<View>(R.id.newButton) as Button
        newButton.setOnClickListener {
            val intent = Intent(this@ListActivity, CreateMemoActivity::class.java)

            intent.putExtra("id", "")
            startActivity(intent)
        }
    }
}