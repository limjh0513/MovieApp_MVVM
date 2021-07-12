package com.example.moviemvvm.ui.popular_movie

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviemvvm.data.api.POSTER_BASE_URL
import com.example.moviemvvm.data.repository.NetworkState
import com.example.moviemvvm.data.vo.Movie
import com.example.moviemvvm.databinding.MovieListItemBinding
import com.example.moviemvvm.databinding.NetworkStateItemBinding
import com.example.moviemvvm.ui.single_movie_details.SingleMovie

class PopularMoviePagedListAdapter(public val context: Context) :
    PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {
    lateinit var binding1: MovieListItemBinding
    lateinit var binding2: NetworkStateItemBinding

    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            (holder as MovieItemViewHolder).bind(getItem(position), context)
        } else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MOVIE_VIEW_TYPE) {
            binding1 =
                MovieListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MovieItemViewHolder(binding1)
        } else {
            binding2 =
                NetworkStateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NetworkStateItemViewHolder(binding2)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            MOVIE_VIEW_TYPE
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    class MovieItemViewHolder(val view: MovieListItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(movie: Movie?, context: Context) {
            view.cvMovieTitle.text = movie?.title
            view.cvMovieRelaseDate.text = movie?.releaseDate

            val moviePosterURL = POSTER_BASE_URL + movie?.posterPath
            Glide.with(itemView.context)
                .load(moviePosterURL)
                .into(view.cvIvMoviePoster);

            itemView.setOnClickListener {
                val intent = Intent(context, SingleMovie::class.java)
                intent.putExtra("id", movie?.id)
                context.startActivity(intent)
            }
        }
    }

    class NetworkStateItemViewHolder(val view: NetworkStateItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                view.progressBarItem.visibility = View.VISIBLE
            } else {
                view.progressBarItem.visibility = View.GONE
            }
            if (networkState != null && networkState == NetworkState.ERROR) {
                view.errorMsgItem.visibility = View.VISIBLE
                view.errorMsgItem.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                view.errorMsgItem.visibility = View.VISIBLE
                view.errorMsgItem.text = networkState.msg
            } else {
                view.errorMsgItem.visibility = View.GONE
            }
        }
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState: NetworkState? = this.networkState
        val hadExtraRow = hasExtraRow()

        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}