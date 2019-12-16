package com.example.view

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.StringCodec
import io.flutter.view.FlutterMain
import io.flutter.view.FlutterRunArguments
import io.flutter.view.FlutterView

class MainActivity : AppCompatActivity() {
    var flutterView: FlutterView? = null
    var counter: Int = 0

    companion object {
        val CHANNEL = "increment"
        val EMPTY_MESSAGE = ""
        val PING = "ping"
    }


    private var messageChannel: BasicMessageChannel<String>? = null


    fun getArgsFromIntent(intent: Intent): Array<String>? {
        // Before adding more entries to this list, consider that arbitrary
        // Android applications can generate intents with extra data and that
        // there are many security-sensitive args in the binary.
        var args: MutableList<String> = mutableListOf()
        if (intent.getBooleanExtra("trace-startup", false)) {
            args.add("--trace-startup")
        }
        if (intent.getBooleanExtra("start-paused", false)) {
            args.add("--start-paused")
        }
        if (intent.getBooleanExtra("enable-dart-profiling", false)) {
            args.add("--enable-dart-profiling")
        }
        if (args.isNotEmpty()) {
            return args.toTypedArray()
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var args = getArgsFromIntent(getIntent())
        FlutterMain.ensureInitializationComplete(getApplicationContext(), args)
        setContentView(R.layout.flutter_view_layout)
        supportActionBar?.hide()

        val runArguments = FlutterRunArguments()
        runArguments.bundlePath = FlutterMain.findAppBundlePath(getApplicationContext())
        runArguments.entrypoint = "main"

        flutterView = findViewById(R.id.flutter_view)
        flutterView?.runFromBundle(runArguments)

        flutterView?.let { fv ->
            messageChannel = BasicMessageChannel(fv, CHANNEL, StringCodec.INSTANCE)

        }

        messageChannel?.setMessageHandler { _, reply ->
            onFlutterIncrement()
            reply.reply(EMPTY_MESSAGE)
        }

        val fab : FloatingActionButton= findViewById (R.id.button)

        fab.setOnClickListener {
            sendAndroidIncrement()
        }
    }

    fun sendAndroidIncrement() {
        messageChannel?.send(PING)
    }

    fun onFlutterIncrement() {
        counter++
        val textView: TextView = findViewById(R.id.button_tap)
        val value: String = "Flutter button tapped " + counter + if (counter == 1) " time" else " times"
        textView.text = value
    }

    override fun onDestroy() {
        flutterView?.destroy()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        flutterView?.onPause()
    }

    override fun onPostResume() {
        super.onPostResume();
        flutterView?.onPostResume()
    }
}
