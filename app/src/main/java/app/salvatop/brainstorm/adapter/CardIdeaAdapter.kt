package app.salvatop.brainstorm.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.adapter.CardIdeaAdapter.IdeaHolder
import app.salvatop.brainstorm.model.Idea
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class CardIdeaAdapter(private val context: Context, private val ideas: ArrayList<Idea>) : RecyclerView.Adapter<IdeaHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): IdeaHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.idea_card_layout, parent, false)
        return IdeaHolder(view)
    }

    override fun onBindViewHolder(holder: IdeaHolder, position: Int) {
        val idea = ideas[position]
        holder.setDetails(idea)
    }

    override fun getItemCount(): Int {
        return ideas.size
    }

    inner class IdeaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val author: TextView = itemView.findViewById(R.id.ideaAuthor)
        private val title: TextView = itemView.findViewById(R.id.textViewTitle)
        private val ideaContext: TextView = itemView.findViewById(R.id.ideaContext)
        private val content: TextView = itemView.findViewById(R.id.ideaContents)
        private val forks: TextView = itemView.findViewById(R.id.ideaForks)
        private val cover: ImageView = itemView.findViewById(R.id.ideaCover)
        private val forksButton: Button = itemView.findViewById(R.id.buttonFork)
        private val bookmarkButton: Button = itemView.findViewById(R.id.buttonBookmark)


        private val expandBtn: Button = itemView.findViewById(R.id.buttonShowMore)
        private val expandableLayout: ConstraintLayout = itemView.findViewById(R.id.expandable)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun setDetails(idea: Idea) {
            author.text = idea.author
            title.text = idea.title
            content.text = idea.content
            ideaContext.text = idea.ideaContext
            Glide.with(context.applicationContext)
                    .load(R.drawable.idea)
                    .into(cover)
            val nbOfforks = "forks[ " + (idea.forks.size - 1) + " ]"
            forks.text = nbOfforks

            forksButton.setOnClickListener {
                var newName = ""
                val titleText = idea.author + idea.title + newName
                val firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
                val database = FirebaseDatabase.getInstance()
                val currentUser = firebaseAuth!!.currentUser?.displayName.toString()
                val myRef = database.getReference("users").child(currentUser).child("ideas").child(titleText)

                myRef.setValue(idea)
                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        //TODO update the original idea forks field when implementing delete ideas if not forked
                        Log.d("FORK IDEA", "Value is: " + dataSnapshot.value)
                    } override fun onCancelled(error: DatabaseError) {
                        Log.w("ADD IDEA", "Failed to read value.", error.toException())
                    }
                })
            }
            bookmarkButton.setOnClickListener {
                val database = FirebaseDatabase.getInstance()
                val firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
                val currentUser = firebaseAuth!!.currentUser?.displayName.toString()
                val myRef = database.getReference("users").child(currentUser).child("bookmarks").child(idea.title)
                myRef.setValue(idea)
                Toast.makeText(context, "This idea was added to your bookmarks", Toast.LENGTH_SHORT).show()
            }
        }
    init {
            expandBtn.setOnClickListener {
                if (expandableLayout.visibility == View.GONE) {
                    cardView.let { it1 -> TransitionManager.beginDelayedTransition(it1, AutoTransition()) }
                    expandableLayout.visibility = View.VISIBLE
                    val collapse: String = it.resources.getString(R.string.collapse)
                    expandBtn.text = collapse
                } else {
                    cardView.let { it1 -> TransitionManager.beginDelayedTransition(it1, AutoTransition()) }
                    expandableLayout.visibility = View.GONE
                    val expand: String = it.resources.getString(R.string.expand)
                    expandBtn.text = expand
                }
            }

        }
    }
}