package ie.wit.donationx.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import ie.wit.donationx.R
import ie.wit.donationx.databinding.CardDonationBinding
import ie.wit.donationx.models.DonationModel
import ie.wit.donationx.utils.customTransformation

interface DonationClickListener {
    fun onDonationClick(donation: DonationModel)
}

class DonationAdapter constructor(private var donations: ArrayList<DonationModel>,
                                  private val listener: DonationClickListener,
                                  private val readOnly: Boolean)
    : RecyclerView.Adapter<DonationAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardDonationBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding,readOnly)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val donation = donations[holder.adapterPosition]
        holder.bind(donation,listener)
    }

    fun removeAt(position: Int) {
        donations.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = donations.size

    inner class MainHolder(val binding : CardDonationBinding, private val readOnly : Boolean) :
            RecyclerView.ViewHolder(binding.root) {

        val readOnlyRow = readOnly

        fun bind(donation: DonationModel, listener: DonationClickListener) {
            binding.root.tag = donation
            binding.donation = donation

            Picasso.get().load(donation.profilepic.toUri())
                    .resize(200, 200)
                    .transform(customTransformation())
                    .centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onDonationClick(donation) }
            binding.executePendingBindings()
        }
    }
}