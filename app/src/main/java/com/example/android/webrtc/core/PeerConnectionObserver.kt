package com.example.android.webrtc.core

import android.util.Log
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver

/**
 * PeerConnection 状态监听
 * */
class PeerConnectionObserver(
    private val onIceCandidate: (IceCandidate) -> Unit,
    private val onIceConnectionChange: (PeerConnection.IceConnectionState) -> Unit,
    private val onIceGatheringChange: (PeerConnection.IceGatheringState) -> Unit,
    private val onSignalingChange: (PeerConnection.SignalingState) -> Unit,
    private val onAddTrack: (RtpReceiver, MediaStream) -> Unit,
    private val onRenegotiationNeeded: () -> Unit
) : PeerConnection.Observer {

    private val kTag = "PeerConnectionObserver"

    override fun onIceCandidate(candidate: IceCandidate?) {
        candidate?.let {
            Log.d(kTag, "ICE candidate received: ${candidate.sdpMid}, ${candidate.sdpMLineIndex}")
            onIceCandidate(it)
        }
    }

    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {
        Log.d(kTag, "ICE candidates removed: ${candidates?.size}")
    }

    override fun onSignalingChange(newState: PeerConnection.SignalingState?) {
        newState?.let {
            Log.d(kTag, "Signaling state changed: $it")
            onSignalingChange(it)
        }
    }

    override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
        newState?.let {
            Log.d(kTag, "ICE connection state changed: $it")
            onIceConnectionChange(it)

            if (it == PeerConnection.IceConnectionState.DISCONNECTED ||
                it == PeerConnection.IceConnectionState.FAILED ||
                it == PeerConnection.IceConnectionState.CLOSED
            ) {
                Log.w(kTag, "ICE connection failed/disconnected/closed: $it")
            }
        }
    }

    override fun onIceConnectionReceivingChange(receiving: Boolean) {
        Log.d(kTag, "ICE connection receiving changed: $receiving")
    }

    override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {
        newState?.let {
            Log.d(kTag, "ICE gathering state changed: $it")
            onIceGatheringChange(it)
        }
    }

    override fun onAddStream(stream: MediaStream?) {
        stream?.let {
            Log.d(kTag, "Media stream added: ${it.id}")
        }
    }

    override fun onRemoveStream(stream: MediaStream?) {
        stream?.let {
            Log.d(kTag, "Media stream removed: ${it.id}")
        }
    }

    override fun onDataChannel(dc: DataChannel?) {
        dc?.let {
            Log.d(kTag, "Data channel opened: ${it.label()}")
        }
    }

    override fun onRenegotiationNeeded() {
        Log.d(kTag, "Renegotiation needed")
        onRenegotiationNeeded()
    }

    override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {
        receiver?.let {
            Log.d(kTag, "Track added: ${receiver.track()?.kind()}")
            if (streams != null && streams.isNotEmpty()) {
                onAddTrack(it, streams[0])
            }
        }
    }
}