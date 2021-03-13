package app.ito.akki.praisesns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.messageEdit
import kotlinx.android.synthetic.main.activity_main.send
import kotlinx.android.synthetic.main.activity_reply.*
import kotlinx.android.synthetic.main.activity_sent_messages.*
import java.text.SimpleDateFormat
import java.util.*


class ReplyActivity : AppCompatActivity() {

    private lateinit var myEmailAddress: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ReplyMessageAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        myEmailAddress = FirebaseAuth.getInstance().currentUser?.email.toString()

        val sentPostId = intent.getStringExtra("postKey")
        val sentGroupId = intent.getStringExtra("groupKey")
        send.setOnClickListener {
            replyMessage(
                messageEdit.text.toString(), sentGroupId!!, sentPostId!!
            )
        }

        myEmailAddress = FirebaseAuth.getInstance().currentUser?.email.toString()

        //受信ボックスのメッセージを取得してrecyclerViewに反映する
        db = FirebaseFirestore.getInstance()
        val allMessages = mutableListOf<Reply>()
        //名前を入力してコレクションを取得する
        db.collection("groups")
            //orderByを使用することでフィールドを指定し、データの並び替えができる
            //Query.Direction.DESCENDINGによって降順に並び替えることができる
            .document(sentGroupId!!)
            .collection("messages")
            .document(sentPostId!!)
            .collection("replies")
            //以下でFirestoreの更新時の操作を登録
            //.addSnapshotListenerの中に書いた処理がデータベース更新時に自動で処理される
            //データの取得
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e)
                    return@addSnapshotListener
                }


                allMessages.clear()
                for (doc in value!!) {
                    val reply = doc.toObject<Reply>()
                    allMessages.add(reply)
                }


                //RecyclerViewの更新をする
                //RecyclerViewに紐づいているallMessagesの更新を表示に反映するために
                //notifyDataSetChanged()を使用する
                // iOSでいうtableView.reloadData()
                viewAdapter.myDataset = allMessages
                viewAdapter.notifyDataSetChanged()
            }


        viewManager = LinearLayoutManager(this)
        viewAdapter = ReplyMessageAdapter(allMessages)
        recyclerView = replyMessageInbox.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    //メッセージをデータベースに格納する
    fun replyMessage(message: String, sentGroupId: String, sentPostId: String) {
        val db = FirebaseFirestore.getInstance()

        //現在時刻の取得
        val date = Date()

        //map...keyとvalueを一つのセットにしてデータを管理する
        //他言語ではハッシュや辞書(ディクショナリ)と呼ばれるもの
        //Firestoreでデータを登録する際、Hashを必ず使用する。型が指定されている。
//        val mail = hashMapOf(
//            "datetime" to format.format(date),
//            "sender" to myEmailAddress,
//            "message" to message
//        )

        val sentId = intent.getStringExtra("key")

        // whereでコレクションの中をフィルタできる
//        db.collection("messages").where()

        val mail2 = Reply(datetime = date, sender = myEmailAddress, message = message)


        //collectionにいれたものがコレクションに入る
        db.collection("groups") //usersとかmail
            .document(sentGroupId)
            .collection("messages")
            .document(sentPostId)
            .collection("replies")
            .add(mail2)
            .addOnSuccessListener {
                //データの保存が成功した際の処理
                messageEdit.text.clear()
            }
            .addOnFailureListener { e ->
                //データの保存が失敗した際の処理
                //致命的なエラーが発生したらログに出力されるようにする。
                Log.e("Firestore", "Error writing document", e)
            }
    }


}
