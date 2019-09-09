package com.example.chatapp.song

import android.util.Log
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
class MySongPresenter {
    private val mySongApiInterface: MySongAPI = MySongService().getRetrofit()!!
    private lateinit var newSongsView: AllSongsView
    val disposables: CompositeDisposable = CompositeDisposable()

    fun presentAllMySongs(allSongsView: AllSongsView){
        newSongsView = allSongsView
        mySongApiInterface.getAllTracks()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (myObserver)
    }

    private val myObserver = object: Observer<ArrayList<MySong>> {
        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(tracks: ArrayList<MySong>) {
            newSongsView.displayAllSongs(tracks)
        }

        override fun onError(e: Throwable) {
            e.message
        }

        override fun onComplete() {
            Log.d(TAG, "Completed Now")
        }
    }

    companion object{
        const val TAG = "MySongsActivity"
    }
}