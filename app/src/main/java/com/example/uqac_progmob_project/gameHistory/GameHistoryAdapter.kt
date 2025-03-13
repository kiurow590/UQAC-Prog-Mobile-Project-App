package com.example.uqac_progmob_project.gameHistory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.uqac_progmob_project.R

class GameHistoryAdapter(
    private val context: Context,
    private val historyData: List<GameHistoryItem>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = historyData.size

    override fun getChildrenCount(groupPosition: Int): Int = 1

    override fun getGroup(groupPosition: Int): Any = historyData[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any = historyData[groupPosition].playerScores

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.group_item, parent, false)
        val groupNameTextView = view.findViewById<TextView>(R.id.groupNameTextView)
        val groupIndicator = view.findViewById<ImageView>(R.id.groupIndicator)

        val item = getGroup(groupPosition) as GameHistoryItem
        groupNameTextView.text = item.gameName

        // Set the indicator image based on the expanded state
        groupIndicator.setImageResource(if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)

        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.history_item_detail, parent, false)
        val podiumContainer = view.findViewById<ViewGroup>(R.id.podiumContainer)
        val playersContainer = view.findViewById<ViewGroup>(R.id.playersContainer)

        // Clear containers to avoid duplication
        podiumContainer.removeAllViews()
        playersContainer.removeAllViews()

        val playerScores = getChild(groupPosition, childPosition) as List<PlayerScore>

        // Populate podium
        for (i in 0..2) {
            val podiumView = LayoutInflater.from(context).inflate(R.layout.podium_item, podiumContainer, false)
            val playerNameTextView = podiumView.findViewById<TextView>(R.id.playerNameTextView)
            val playerScoreTextView = podiumView.findViewById<TextView>(R.id.playerScoreTextView)
            playerNameTextView.text = playerScores[i].playerName
            playerScoreTextView.text = playerScores[i].score.toString()
            podiumContainer.addView(podiumView)
        }

        // Populate remaining players
        for (i in 3 until playerScores.size) {
            val playerView = LayoutInflater.from(context).inflate(R.layout.player_item, playersContainer, false)
            val playerNameTextView = playerView.findViewById<TextView>(R.id.playerNameTextView)
            val playerScoreTextView = playerView.findViewById<TextView>(R.id.playerScoreTextView)
            playerNameTextView.text = playerScores[i].playerName
            playerScoreTextView.text = playerScores[i].score.toString()
            playersContainer.addView(playerView)
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}