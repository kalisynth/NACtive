package au.org.nac.nactive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import au.org.nac.nactive.R
import au.org.nac.nactive.model.Exercise
import kotlinx.android.synthetic.main.exerciseitem.view.*

/**
 * Exercise List Adapter
 */

class ExerciseAdapter : BaseAdapter(){

    private var dataset : List<Exercise> = ArrayList<Exercise>()

    private class ExerciseViewHolder(itemView : View){

        var name : TextView
        var level : TextView
        var id : TextView

        init {
            name = itemView.findViewById<TextView>(R.id.eiName)
            level = itemView.findViewById<TextView>(R.id.eiLevel)
            id = itemView.findViewById<TextView>(R.id.eiId)
        }
    }

    fun setExercises(exercises: List<Exercise>){
        dataset = exercises
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent : ViewGroup): View{
        var convertView = convertView
        val holder : ExerciseViewHolder
        if(convertView == null){
            convertView = LayoutInflater.from(parent.context).inflate(R.layout.exerciseitem, parent, false) as View
            holder = ExerciseViewHolder(convertView)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ExerciseViewHolder
        }

        val exercise = getItem(position)
        holder.name.text = exercise.name
        holder.level.text = exercise.difficultylevel.toString()
        holder.id.text = exercise.id.toString()

        return convertView
    }

    override fun getCount(): Int{
        return dataset.size
    }

    override fun getItem(position: Int): Exercise {
        return dataset[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}