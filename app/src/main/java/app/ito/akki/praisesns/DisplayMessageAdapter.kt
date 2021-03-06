package app.ito.akki.praisesns

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.mail.view.*
import kotlin.properties.Delegates

class DisplayMessageAdapter
//コンストラクタを追加
//コンストラクタって何？？
//クラスを作った時にすぐ代入されるもの
    (var myDataset: ArrayList<Post>)
//DisplayMessageAdapterクラスにRecyclerView.Adapterを継承する。
    : RecyclerView.Adapter<DisplayMessageAdapter.ViewHolder>() {
    //イベント時に実行させたい関数を格納する変数をプロパティとして先に後悔しておく。
    var onThanksButtonClick: (String) -> Unit by Delegates.notNull()
    var onGoodButtonClick: (String) -> Unit by Delegates.notNull()
    var onWorkedHardButtonClick: (String) -> Unit by Delegates.notNull()

    //リスナを格納する変数を定義する(lateinitで初期化を遅らせている)
    lateinit var listener: OnItemClickListener

    //複数のViewを保持するクラスのこと
    //イベントを発生させるUI部品のインスタンスを取得し、プロパティとして公開しておく。
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sender: TextView = view.sender
        val message: TextView = view.message
        val thanksButton: Button = view.thanksButton
        val goodButton: Button = view.goodButton
        val workedHardButton: Button = view.workedHardButton
        val thanksText: TextView = view.thanksText
        val goodText: TextView = view.goodText
        val workedHardText = view.workedHardText
        val container: LinearLayout = view.container
    }

    // iOS: CellForRowAtのセルの作成部分（セルの作成）
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflateView = LayoutInflater.from(parent.context)
            .inflate(R.layout.mail, parent, false)
        return ViewHolder(inflateView)
    }

    //リストの要素数を返すメソッドを実装する。
    // iOS: numberOfRowsInSection
    override fun getItemCount() = myDataset.size

    // iOS: CellForRowAt（データを設定）
    //myDatasetのposition番目の要素をrecyclerViewのviewに表示するコードを書く
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.message.text = myDataset[position].message
        holder.sender.text = "from: " + myDataset[position].sender

        //クリックした時の処理を記述
        holder.container.setOnClickListener {
            listener.onItemClickListener(it, myDataset[position].id, myDataset[position].message, myDataset[position].sender)
        }
        holder.goodButton.setOnClickListener {
            onGoodButtonClick(myDataset[position].id)
        }
        holder.thanksButton.setOnClickListener {
            onThanksButtonClick(myDataset[position].id)
        }
        holder.workedHardButton.setOnClickListener {
            onWorkedHardButtonClick(myDataset[position].id)
        }


        val thanksCountToString: String = myDataset[position].thanksButtonCount.toString()
        val goodCountToString: String = myDataset[position].goodButtonCount.toString()
        val workedCountToString: String = myDataset[position].workedHardButtonCount.toString()
        //クリックされた後の表示の処理を行う
        holder.thanksText.text = thanksCountToString
        holder.goodText.text = goodCountToString
        holder.workedHardText.text = workedCountToString

    }

    //インタフェースを作成する
    interface OnItemClickListener {
        fun onItemClickListener(view: View, postId: String, message: String, sender: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}