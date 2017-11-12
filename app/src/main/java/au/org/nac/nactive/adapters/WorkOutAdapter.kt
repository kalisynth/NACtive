package au.org.nac.nactive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import au.org.nac.nactive.R
import au.org.nac.nactive.model.WorkOutSession
import kotlinx.android.synthetic.main.workoutitem.view.*
import org.jetbrains.anko.find

/**
 * WorkOutSessoion List Adapter
 */
class WorkOutAdapter : BaseAdapter() {

    private var dataset : List<WorkOutSession> = ArrayList<WorkOutSession>()

    private class WorkOutViewHolder(itemView : View){
        var name : TextView
        var AoF : TextView
        var id : TextView

        init {
            name = itemView.findViewById(R.id.wiName)
            AoF = itemView.findViewById(R.id.wiAoF)
            id = itemView.findViewById(R.id.wiId)
        }
    }

    fun setWorkOuts(workOutSessions: List<WorkOutSession>){
        dataset = workOutSessions
        notifyDataSetChanged()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup): View {
        var p1 = p1
        val holder : WorkOutViewHolder
        if(p1 == null){
            p1 = LayoutInflater.from(p2.context).inflate(R.layout.workoutitem, p2, false) as View
            holder = WorkOutViewHolder(p1)
            p1.tag = holder
        } else {
            holder = p1.tag as WorkOutViewHolder
        }

        val workOut = getItem(p0)
        holder.name.text = workOut.name
        holder.AoF.text = workOut.areaOfFocus
        holder.id.text = workOut.id.toString()

        return p1
    }

    override fun getCount(): Int {
        return dataset.size
    }

    override fun getItem(p0: Int): WorkOutSession {
        return dataset[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
}